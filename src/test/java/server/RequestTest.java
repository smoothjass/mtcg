package server;
import http.Method;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest {
    @Test
    @DisplayName("Test: build request")
    public void buildRequestFromInputStream() {
        // arrange
        Reader stringReader = new StringReader("" +
                "GET /users/kienboec HTTP/1.1\n" +
                "Authorization: Bearer kienboec-mtcgToken\n" +
                "\n");
        BufferedReader reader = new BufferedReader(stringReader);
        Request request = new Request(reader);

        Method expectedMethod = Method.valueOf("GET");
        String expectedPathname = "/users/kienboec";
        String expectedParams = "";
        String expectedAuthToken = "kienboec-mtcgToken";
        String expectedBody = "";

        // act
        request.buildRequest(reader);

        // assert
        assertEquals(expectedMethod, request.getMethod());
        assertEquals(expectedPathname, request.getPathname());
        assertEquals(expectedParams, request.getParams());
        assertEquals(expectedAuthToken, request.getAuthToken());
        assertEquals(expectedBody, request.getBody());
    }
}
