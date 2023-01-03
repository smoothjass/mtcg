package app.controllers;

import app.dtos.UserProfileDTO;
import app.models.User;
import app.repositories.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.hash.Hashing;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;

import lombok.Setter;
import server.Response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

// Our User Controller is using the database with repositories, DAOs, Models, (DTOs)
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class UserController extends Controller {
    UserProfileRepository userProfileRepository;
    // we inject our repository, so we can mock the repository when testing the controller
    public UserController(UserProfileRepository userProfileRepository) {
        // always use the getter and setter
        // otherwise if we change the setter or getter we would
        // need to manually update all occurances of
        // this.userProfileRepository = ... and update them as well
        // wich is error prone so always remember we also use getter and setter
        // in the class itself
        setUserProfileRepository(userProfileRepository);
    }

    // GET /user-profiles/:ID
    // We get the ID from the path (e.g. a path variable (:ID))
    public Response getUserProfile(String username) {
        try {
            // our userRepository returns a single UserProfile getByUsername
            UserProfileDTO userProfile = getUserProfileRepository().getByUsername(username);
            // parse to JSON string
            String userProfileJSON = getObjectMapper().writeValueAsString(userProfile);

            if (userProfile == null) {
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": \"User not found\" }"
                );
            }

            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"description\": Data successfully retrieved, " +
                        "\"data\": {" +
                        "\"name\": " + userProfile.getName() + ", " +
                        "\"bio\": " + userProfile.getBio() + ", " +
                        "\"image\": " + userProfile.getImage() + ", " +
                        "}" +
                        "\"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
            HttpStatus.BAD_REQUEST,
            ContentType.JSON,
            "{ \"data\": null, \"error\": \"Request was malformed\" }"
        );
    }

    public Response createUser(String body) {
        try {
            User userData = getObjectMapper().readValue(body, User.class);
            UserProfileDTO userProfile = getUserProfileRepository().postUser(userData);
            if (userProfile != null) {
                return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{\"description\": user successfully created, \"data\": null, \"error\": null }"
                );
            }
            else{
                return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{\"data\": null, \"error\": User with same username already registered }"
                );
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Response updateUser(String username, String body) {
        try {
            User data = getObjectMapper().readValue(body, User.class);
            UserProfileDTO userProfile = getUserProfileRepository().updateUser(username, data);

            if (userProfile == null) {
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": \"User not found\" }"
                );
            }

            // parse to JSON string
            String userProfileJSON = getObjectMapper().writeValueAsString(userProfile);
            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"description\": User successfully updated, \"data\": null, \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Response login(String body) {
        try {
            User data = getObjectMapper().readValue(body, User.class);
            UserProfileDTO userProfile = getUserProfileRepository().getByUsername(data.getUsername());
            if(userProfile != null) {
                // user with given username found -> compare passwords
                String sha256hex = Hashing.sha256()
                        .hashString(data.getPassword(), StandardCharsets.UTF_8)
                        .toString();
                if (sha256hex.equals(userProfile.getPassword())) {
                    // parse to JSON string
                    String userProfileJSON = getObjectMapper().writeValueAsString(userProfile);
                    return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"description\": User login successful, \"data\": " + userProfile.getUsername() + "-mtcgToken" + ", \"error\": null }"
                    );
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Response(
            HttpStatus.UNAUTHORIZED,
            ContentType.JSON,
            "{\"data\": null, \"error\": Invalid username/password provided}"
        );
    }

    public Response getStats(String username) {
        UserProfileDTO userProfile = getUserProfileRepository().getByUsername(username);
        if (userProfile != null) {
            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"description\": User stats could be retrieved successfully, " +
                        "\"data\": {" +
                        "\"name\": " + userProfile.getName() + ", " +
                        "\"elo\": " + userProfile.getElo() + ", " +
                        "\"wins\": " + userProfile.getGames_won() + ", " +
                        "\"losses\": " + (userProfile.getGames_played()-userProfile.getGames_won()) + ", " +
                        "}, " +
                        "\"error\": null }"
            );
        }
        return null;
    }

    public Response getScores() {
        ArrayList<UserProfileDTO> users = getUserProfileRepository().getAll("elo");
        ArrayList<String> scores = new ArrayList<String>();
        for(UserProfileDTO userProfile: users) {
            String temp =
                    "{" +
                    "\"name\": " + userProfile.getName() + ", " +
                    "\"elo\": " + userProfile.getElo() + ", " +
                    "\"wins\": " + userProfile.getGames_won() + ", " +
                    "\"losses\": " + (userProfile.getGames_played()-userProfile.getGames_won()) + ", " +
                    "}, ";
            scores.add(temp);
        }
        return new Response(
            HttpStatus.OK,
            ContentType.JSON,
            "{ \"description\": The scoreboard could be retrieved successfully, " +
                    "\"data\": {" +
                    scores +
                    "\"error\": null }"
        );
    }
}
