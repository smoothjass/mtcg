package app.controllers;

import app.dtos.UserProfileDTO;
import app.models.User;
import app.repositories.UserProfileRepository;
import app.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;

import lombok.Setter;
import server.Response;

import java.util.ArrayList;

// Our User Controller is using the database with repositories, DAOs, Models, (DTOs)
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class UserController extends Controller {
    UserProfileRepository userProfileRepository;
    private UserService userService;
    // we inject our repository, so we can mock the repository when testing the controller
    public UserController(UserProfileRepository userProfileRepository, UserService userService) {
        // always use the getter and setter
        // otherwise if we change the setter or getter we would
        // need to manually update all occurances of
        // this.userProfileRepository = ... and update them as well
        // wich is error prone so always remember we also use getter and setter
        // in the class itself
        setUserProfileRepository(userProfileRepository);
        setUserService(userService);
    }

    // GET /user-profiles
    public Response getUserProfiles() {
        try {
            // The repository getAll is returning an arraylist
            ArrayList<UserProfileDTO> userProfiles = getUserProfileRepository().getAll();
            // We need to map that to a JSON String
            String userProfileJSON = getObjectMapper().writeValueAsString(userProfiles);

            // remember to send the correct status code
            // if we do not have any userProfiles
            // we send an 404 - Not Found
            // can you refactor this code so that is more readable?
            // e.g. get rid of another nesting?
            if (userProfiles.isEmpty()) {
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": \"No users found\" }"
                );
            }

            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + userProfileJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
            HttpStatus.BAD_REQUEST,
            ContentType.JSON,
            "{ \"data\": null, \"error\": \"Error\" }"
        );
    }

    // GET /user-profiles/:ID
    // We get the ID from the path (e.g. a path variable (:ID))
    public Response getUserProfile(Integer ID) {
        try {
            // our userRepository returns a single UserProfile getById
            UserProfileDTO userProfile = getUserProfileRepository().getById(ID);
            // parse to JSON string
            String userProfileJSON = getObjectMapper().writeValueAsString(userProfile);

            if (userProfile == null) {
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": \"User does not exist\" }"
                );
            }

            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + userProfileJSON + ", \"error\": null }"
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
                        "{\"description\": user successfully created, \"data\": " + body + ", \"error\": null }"
                );
            }
            else{
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "{\"description\": User with same username already registered, \"data\": " + body + ", \"error\": null }"
                );
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
