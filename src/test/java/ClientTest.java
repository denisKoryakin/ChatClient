import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;


import java.io.*;
import java.net.Socket;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {

    public ClientTest() {
    }

    @BeforeAll
    public static void beforeAllTests() {
        System.out.println("All ClientTests started");
    }

    @AfterAll
    public static void afterAllTests() {
        System.out.println("All ClientTests completed");
    }

    Socket socket = Mockito.mock(Socket.class);

    @ParameterizedTest
    @MethodSource("sendMessageParametersDefinition")
    public void sendMessageTest(String message) throws IOException {
//    arrange
        System system = Mockito.mock(System.class);
        InputStream input = new ByteArrayInputStream((message).getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Mockito.when(socket.getInputStream()).thenReturn(input);
        Mockito.when(socket.getOutputStream()).thenReturn(output);
        Mockito.when(socket.isConnected()).thenReturn(true);
        Client client = new Client(socket, "Denis");
        System.setIn(input);
//    act
        client.sendMessage();
        String[] messages = message.split("\n");
        StringBuilder sb = new StringBuilder();
        sb.append(client.name);
        sb.append(System.lineSeparator());
        sb.append(client.name);
        sb.append(": ");
        sb.append(messages[0]);
        sb.append(System.lineSeparator());
        sb.append(client.name);
        sb.append(": ");
        sb.append(messages[1]);
        sb.append(System.lineSeparator());
//    assert
        assertEquals(sb.toString(), output.toString());
    }

    private static Stream<Arguments> sendMessageParametersDefinition() throws IOException {
        return Stream.of(
                Arguments.of("Привет!" + "\n" + "exit" + "\n"),
                Arguments.of("Как дела?" + "\n" + "exit" + "\n")
        );
    }

    @Test
    public void closeResurcesTest() throws IOException {
//    arrange
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayInputStream input = new ByteArrayInputStream("".getBytes());
        Mockito.when(socket.getInputStream()).thenReturn(input);
        Mockito.when(socket.getOutputStream()).thenReturn(output);
        Client client = new Client(socket, "Denis");
//    act
        client.closeResurces();
//    assert
        assertThat(socket.isConnected(), is(false));
        assertThrows(IOException.class, () -> client.getBr().read(), "Stream closed");
        assertThrows(IOException.class, () -> client.getBw().write(""), "Stream closed");
    }
}
