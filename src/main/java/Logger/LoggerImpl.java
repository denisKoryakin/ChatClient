package Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;


public class LoggerImpl implements Logger {

    Date date = new Date();

    @Override
    public void log(String text) {

        try (FileOutputStream fos = new FileOutputStream("logs.txt", true)) {
//        переводим текст в массив байт
            String log = String.format("%td %tB %tY года %tH:%tM:%tS - ", date, date, date, date, date, date) +
                    text +
                    "\n";
            byte[] bytes = log.getBytes(StandardCharsets.UTF_8);
//        запись байт в файл
            fos.write(bytes, 0, bytes.length);
            fos.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
