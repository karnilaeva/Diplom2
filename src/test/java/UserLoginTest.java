import io.restassured.response.Response;
import model.User;
import model.UserLogin;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserLoginTest extends BaseTest {

    User user;
    String accessToken;

    @Before
    public void createUser(){
        this.user = Util.randomUser();
        Response response = Util.createUser(this.user);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        this.accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        Util.deleteUser(user, accessToken);
    }

    @Test
    public void successfulLogin() {
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());

        Response response = given()
                .header("Content-type", "application/json")
                .body(userLogin)
                .post("/api/auth/login");

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.name", equalTo(user.getName()))
                .body("user.email", equalTo(user.getEmail()));
    }

    @Test
    public void unsuccessfulLogin() {
        UserLogin userLogin = new UserLogin(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        Response response = given()
                .header("Content-type", "application/json")
                .body(userLogin)
                .post("/api/auth/login");

        response.then().assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }

}
