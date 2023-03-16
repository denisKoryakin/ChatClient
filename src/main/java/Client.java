import java.io.*;
import java.net.*;
import java.util.Scanner;

import Logger.*;

public class Client {
    private BufferedReader br;
    private BufferedWriter bw;
    private final Socket socket;
    public final String name;
    private final Logger logger;

    public Client(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.name = name;
        this.logger = new LoggerImpl();
        logger.log("клиентское приложение запущено");
    }

    public BufferedReader getBr() {
        return this.br;
    }

    public BufferedWriter getBw() {
        return this.bw;
    }

    public void readMessage() {
        Thread readMessage = new Thread(() -> {
            String message;
            while (true) {
                try {
                    message = br.readLine();
                    logger.log(message);
                    System.out.println(message);
                } catch (IOException e) {
                    System.out.println("Ошибка чтения сообщения:" + e.getMessage());
                    closeResurces();
                    break;
                }
            }
        });
        readMessage.start();
    }

    public void sendMessage() {
        try {
//            первоначальная отправка имени
            bw.write(name);
            bw.newLine();
            bw.flush();

            Scanner sc = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = sc.nextLine();
                bw.write(name + ": " + messageToSend);
                bw.newLine();
                bw.flush();
                if (messageToSend.equals("exit")) {
                    closeResurces();
                    break; /* для удобства теста */
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка отправки сообщения:" + e.toString());
        }
    }

    public void closeResurces() {
        try {
            if (this.br != null) {
                this.br.close();
            }
            if (this.bw != null) {
                this.bw.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ваш ник ");
        String name = scanner.nextLine();
        String host = null;
        int port = 0;

//        считываем номер порта из файла
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("settings.txt"))
        ) {
            String setting = bufferedReader.readLine();
            String[] settings = setting.split(", ");
            host = settings[1];
            port = Integer.parseInt(settings[0]);
        } catch (IOException ex) {
            String message = "Не удалось прочитать файл настроек клиента";
            System.out.println(message);
            ex.printStackTrace();
        }

        Socket socket = new Socket(host, port);

        try {
            Client client = new Client(socket, name);
            client.logger.log("Произведено подключение к серверу: " + socket.getInetAddress() + " " + socket.getPort());
            client.readMessage();
            client.sendMessage();
        } catch (IOException ex) {
            System.out.println("Сервер недоступен");
            ex.printStackTrace();
        }
    }
}