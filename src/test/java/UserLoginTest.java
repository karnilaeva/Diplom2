import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserLoginTest extends BaseTest {

    private final UserClient userClient = new UserClient();

    User user;
    String accessToken;

    @Before
    public void createUser(){
        this.user = UserClient.randomUser();
        Response response = userClient.createUser(this.user);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        this.accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(user, accessToken);
    }

    @Test
    @DisplayName("Успешный вход")
    public void successfulLogin() {
        Response response = userClient.loginUser(user.getEmail(), user.getPassword());

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.name", equalTo(user.getName()))
                .body("user.email", equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Неуспешный вход")
    public void unsuccessfulLogin() {
        Response response = userClient.loginUser(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        response.then().assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }

}
