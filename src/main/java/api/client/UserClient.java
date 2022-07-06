package api.client;

import io.restassured.response.Response;
import model.User;
import model.UserLogin;
import org.apache.http.HttpStatus;

import java.util.Random;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String AUTH_API = "/api/auth/user";
    private static final String LOGIN_API = "/api/auth/login";
    private static final String REGISTER_API = "/api/auth/register";

    public static User randomUser() {
        Random random = new Random();
        String email = "user" + random.nextInt(10000000) + "@yandex.ru";
        return new User(email, "1234567", "John");
    }

    public Response createUser(User user) {
        System.out.println("Trying to create a user. Email: " + user.getEmail() + ", Password: " + user.getPassword() + ", Name: " + user.getName());
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(REGISTER_API);
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            System.out.println("User successfully created");
        } else {
            System.out.println("Response code after creating user is " + response.getStatusCode());
        }
        return response;
    }

    public void deleteUser(User user, String authToken) {
        Response response = given()
                .header("Authorization", authToken)
                .delete(AUTH_API);
        if (response.getStatusCode() == HttpStatus.SC_ACCEPTED) {
            System.out.println("User " + user.getEmail() + " deleted");
        }

    }

    public Response updateUser(String accessToken, String newEmail, String password, String newName) {
        User newUser = new User(newEmail, password, newName);

        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(newUser)
                .patch(AUTH_API);
    }

    public Response updateUser(String newEmail, String password, String newName) {
        User newUser = new User(newEmail, password, newName);

        return given()
                .header("Content-type", "application/json")
                .body(newUser)
                .patch(AUTH_API);
    }

    public Response loginUser(String email, String password) {
        UserLogin userLogin = new UserLogin(email, password);

        return given()
                .header("Content-type", "application/json")
                .body(userLogin)
                .post(LOGIN_API);
    }
}
