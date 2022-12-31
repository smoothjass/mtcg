package app;

import app.controllers.CityController;
import app.controllers.UserController;
import app.daos.CityDao;
import app.daos.RoleDao;
import app.daos.UserDao;
import app.repositories.UserProfileRepository;
import app.services.CityService;
import app.services.DatabaseService;
import app.services.UserService;
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

import static java.lang.Integer.parseInt;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class App implements ServerApp {
    private CityController cityController;
    private UserController userController;
    private Connection connection;

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

        // Repos
        UserProfileRepository userProfileRepository = new UserProfileRepository(userDao, roleDao);

        // Controllers
        CityController cityController = new CityController(new CityService());
        UserController userController = new UserController(userProfileRepository, new UserService());

        setCityController(cityController);
        setUserController(userController);
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
                    // TODO Only the admin or the matching user can successfully retrieve the data. 401 Response. access token in body
                    return getUserController().getUserProfile(username);
                }
                if (request.getPathname().equals("/cards")) {
                    // Returns all cards that have been required by the provided user
                }
                if (request.getPathname().equals("/decks")) {
                    // Returns the cards that are owned by the uses and are put into the deck
                }
                if (request.getPathname().equals("/stats")) {
                    // Retrieves the stats for the requesting user.
                }
                if (request.getPathname().equals("/scores")) {
                    // Retrieves the user scoreboard ordered by the user's ELO.
                }
                if (request.getPathname().equals("/tradings")) {
                    // Retrieves the currently available trading deals.
                }
            }
            case POST: {
                // paths from in-class coding
                if (request.getPathname().equals("/cities")) {
                    return getCityController().createCity(request.getBody());
                }
                // paths as specified in openapi on moodle
                if (request.getPathname().equals("/users")) {
                    // Register a new user
                    return getUserController().createUser(request.getBody());
                }
                if (request.getPathname().equals("/sessions")) {
                    // Login with existing user
                }
                if (request.getPathname().equals("/packages")) {
                    // Create new card packages (requires admin)
                }
                if (request.getPathname().equals("/transactions/packages")) {
                    // Acquire a card package
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
}
