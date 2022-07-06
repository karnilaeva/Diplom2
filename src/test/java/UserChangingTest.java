import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
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

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class UserChangingTest extends BaseTest {

    private final UserClient userClient = new UserClient();

    private final static User user = UserClient.randomUser();
    private String accessToken;
    private final String newName;
    private final String newEmail;

    public UserChangingTest(String newName, String newEmail) {
        this.newName = newName;
        this.newEmail = newEmail;
    }

    @Parameters(name = "name={0}; email={1}")
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
        Response response = userClient.createUser(user);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        this.accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(user, accessToken);
    }

    @Test
    @DisplayName("Успешное обновление данных пользователя")
    public void successfulUpdate() {
        Response response = userClient.updateUser(accessToken, newEmail, user.getPassword(), newName);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Неуспешное обновление данных пользователя неавторизованным пользователем")
    public void unsuccessfulUpdateWithoutAuthorization() {
        Response response = userClient.updateUser(newEmail, user.getPassword(), newName);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
