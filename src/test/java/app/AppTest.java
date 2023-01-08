package app;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppTest {
    @Test
    @DisplayName("Test: get username from authToken")
    public void getUsernameFromAuthToken() {
        // arrange
        String invalidToken = "kalöksdalkj";
        String validToken = "kienboec-mtcgToken";

        App app = new App();

        String expected2 = "kienboec";

        // act
        String actual1 = app.getUserFromAuthToken(invalidToken);
        String actual2 = app.getUserFromAuthToken(validToken);

        // assert
        assertNull(actual1);
        assertEquals(expected2, actual2);
    }
}
