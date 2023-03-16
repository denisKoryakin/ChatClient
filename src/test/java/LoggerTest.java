import Logger.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

import java.io.*;
import java.util.stream.Stream;

public class LoggerTest {

    @BeforeAll
    public static void beforeAllTests() {
        System.out.println("All LoggerTests started");
    }

    @AfterAll
    public static void afterAllTests() {
        System.out.println("All LoggerTests completed");
    }

    @ParameterizedTest
    @MethodSource("logTestParametersDefinition")
    public void logTest(String message) throws IOException, InterruptedException {
//        arrange
        Logger logger = new LoggerImpl();
        Date date = new Date();
        BufferedReader input = new BufferedReader(new FileReader("logs.txt"));
        String log = String.format("%td %tB %tY года %tH:%tM:%tS - ", date, date, date, date, date, date) +
                message;
//        act
        logger.log(message);
        String last = null;
        String line;
        while (null != (line = input.readLine())) {
            last = line; /*  чтение последней строки из файла */
        }
//        assert
        Assertions.assertEquals(log, last);
    }

    private static Stream<Arguments> logTestParametersDefinition() throws IOException {
        return Stream.of(
                Arguments.of("Привет!"),
                Arguments.of("Как дела?")
        );
    }
}
