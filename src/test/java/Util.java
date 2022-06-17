import io.restassured.response.Response;
import model.User;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class Util {

    public static Response createUser(User user) {
        System.out.println("Trying to create a user. Email: " + user.getEmail() + ", Password: " + user.getPassword() + ", Name: " + user.getName());
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("/api/auth/register");
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            System.out.println("User successfully created");
        } else {
            System.out.println("Response code after creating user is " + response.getStatusCode());
        }
        return response;
    }

    public static void deleteUser(User user, String authToken) {
        Response response = given()
                .header("Authorization", authToken)
                .delete("/api/auth/user");
        if (response.getStatusCode() == HttpStatus.SC_ACCEPTED) {
            System.out.println("User " + user.getEmail() + " deleted");
        }

    }
}
