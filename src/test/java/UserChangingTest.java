import io.restassured.response.Response;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class UserChangingTest extends BaseTest {

    private final static User user = Util.randomUser();
    private String accessToken;
    private final String newName;
    private final String newEmail;

    public UserChangingTest(String newName, String newEmail) {
        this.newName = newName;
        this.newEmail = newEmail;
    }

    @Parameters
    public static Object[][] getParameters() {
        Random random = new Random();
        String randomEmail = "user" + random.nextInt(10000000) + "@yandex.ru";

        return new Object[][]{
                {user.getName(), user.getEmail()},
                {"mark", user.getEmail()},
                {user.getName(), randomEmail}
        };
    }

    @Before
    public void createUser(){
        Response response = Util.createUser(user);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        this.accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        Util.deleteUser(user, accessToken);
    }

    @Test
    public void successfulUpdate() {
        User newUser = new User(newEmail, user.getPassword(), newName);

        Response response = given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(newUser)
                .patch("/api/auth/user");

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(newName));
    }

    @Test
    public void unsuccessfulUpdateWithoutAuthorization() {
        User newUser = new User(newEmail, user.getPassword(), newName);

        Response response = given()
                .header("Content-type", "application/json")
                .body(newUser)
                .patch("/api/auth/user");

        response.then().assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
