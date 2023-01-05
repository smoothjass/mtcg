package app;

import app.controllers.CardController;
import app.controllers.UserController;
import app.daos.*;
import app.repositories.CardRepository;
import app.repositories.UserProfileRepository;
import app.services.DatabaseService;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Request;
import server.Response;
import server.ServerApp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import static java.lang.Integer.parseInt;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class App implements ServerApp {
    private UserController userController;
    private CardController cardController;
    private Connection connection;
    private String sessionUserToken = "";

    // In our app we instantiate all of our DAOs, repositories, and controllers
    // we inject the DAOs to the repos
    // we inject the repos to the controllers
    public App() {
        try {
            setConnection(new DatabaseService().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // DAOs
        UserDao userDao = new UserDao(getConnection());
        RoleDao roleDao = new RoleDao(getConnection());
        CardDao cardDao = new CardDao(getConnection());
        CardtypeDao cardtypeDao = new CardtypeDao(getConnection());
        ElementDao elementDao = new ElementDao(getConnection());

        // Repos
        UserProfileRepository userProfileRepository = new UserProfileRepository(userDao, roleDao);
        CardRepository cardRepository = new CardRepository(cardDao, cardtypeDao, elementDao);

        // Controllers
        UserController userController = new UserController(userProfileRepository);
        CardController cardController = new CardController(cardRepository, userProfileRepository);

        setUserController(userController);
        setCardController(cardController);
    }

    // the handleRequest Method is used in the server
    // it returns the response to the client
    public Response handleRequest(Request request) {
        // check method
        switch (request.getMethod()) {
            case GET: {
                // check path and path variables
                // paths as specified in openapi on moodle
                if (request.getPathname().matches("/users/[a-zA-Z0-9]*")) {
                    String username = request.getPathname().split("/")[2];
                    // Retrieves the user data for the username provided in the route.
                    String authUser = getUserFromAuthToken(request.getAuthToken());
                    if (!Objects.equals(authUser, "admin")) {
                        if (authUser == null) {
                            return new Response(
                                HttpStatus.UNAUTHORIZED,
                                ContentType.JSON,
                                "{ \"data\": null, \"error\": Access token missing or invalid }"
                            );
                        }
                    }
                    return getUserController().getUserProfile(username);
                }
                if (request.getPathname().equals("/cards")) {
                    // Returns all cards that have been required by the provided user
                    String username = getUserFromAuthToken(request.getAuthToken());
                    if (username == null) {
                        return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"data\": null, \"error\": Access token missing or invalid }"
                        );
                    }
                    return getCardController().getCardsForUser(username, false, false);
                }
                if (request.getPathname().equals("/decks")) {
                    // Returns the cards that are owned by the uses and are put into the deck
                    String username = getUserFromAuthToken(request.getAuthToken());
                    if (username == null) {
                        return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"data\": null, \"error\": Access token missing or invalid }"
                        );
                    }
                    boolean plainFormat = false;
                    if (request.getParams() != null) {
                        if (Objects.equals(request.getParams(), "format=plain")) {
                            plainFormat = true;
                        }
                    }
                    return getCardController().getCardsForUser(username, true, plainFormat);
                }
                if (request.getPathname().equals("/stats")) {
                    // Retrieves the stats for the requesting user.
                    String username = getUserFromAuthToken(request.getAuthToken());
                    if (username == null) {
                        return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"data\": null, \"error\": Access token missing or invalid }"
                        );
                    }
                    return getUserController().getStats(username);
                }
                if (request.getPathname().equals("/scores")) {
                    // Retrieves the user scoreboard ordered by the user's ELO.
                    return getUserController().getScores();
                }
                if (request.getPathname().equals("/tradings")) {
                    // Retrieves the currently available trading deals.
                }
            }
            case POST: {
                // paths as specified in openapi on moodle
                if (request.getPathname().equals("/users")) {
                    // Register a new user
                    return getUserController().createUser(request.getBody());
                }
                if (request.getPathname().equals("/sessions")) {
                    // Login with existing user
                    return getUserController().login(request.getBody());
                }
                if (request.getPathname().equals("/packages")) {
                    // Create new card packages (requires admin)
                    String username = getUserFromAuthToken(request.getAuthToken());
                    if (username == null) {
                        return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"data\": null, \"error\": Access token missing or invalid }"
                        );
                    }
                    return getCardController().createPackage(request.getBody());
                }
                if (request.getPathname().equals("/transactions/packages")) {
                    // Acquire a card package for requesting user
                    String username = getUserFromAuthToken(request.getAuthToken());
                    if (username == null) {
                        return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"data\": null, \"error\": Access token missing or invalid }"
                        );
                    }
                    return getCardController().acquirePackage(username);
                }
                if (request.getPathname().equals("/battles")) {
                    // Enters the lobby to start a battle
                }
                if (request.getPathname().equals("/tradings")) {
                    // Creates a new trading deal.
                }
                if (request.getPathname().matches("/tradings/\\d+")) {
                    Integer tradingDealId = parseInt(request.getPathname().split("/")[2]);
                    // Carry out a trade for the deal with the provided card.
                }
            }
            case PUT: {
                if (request.getPathname().matches("/users/[a-zA-Z0-9]*")) {
                    // Updates the user data for the given username.
                    String username = request.getPathname().split("/")[2];
                    return getUserController().updateUser(username, request.getBody());
                }
                if (request.getPathname().equals("/decks")) {
                    // Configures the deck with four provided cards
                    String username = getUserFromAuthToken(request.getAuthToken());
                    if (username == null) {
                        return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"data\": null, \"error\": Access token missing or invalid }"
                        );
                    }
                    return getCardController().configureDeck(username, request.getBody());
                }
            }
            case DELETE: {
                if (request.getPathname().matches("/tradings/\\d+")) {
                    Integer tradingDealId = parseInt(request.getPathname().split("/")[2]);
                    // Deletes an existing trading deal.
                }
            }
        }

        // default response
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"data\": null, \"error\": \"Route Not Found\" }");
    }

    private String getUserFromAuthToken(String authToken) {
        if (!authToken.matches("[a-zA-Z0-9]*-mtcgToken")) {
            return null;
        }
        // TODO check if user is the right user, if not return null
        return authToken.substring(0, authToken.length()-"-mtcgToken".length());
    }
}
