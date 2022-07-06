import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreatingTest extends BaseTest{

    @Test
    @DisplayName("Успешное создание пользователя")
    public void successfulCreating() {
        User user = Util.randomUser();

        Response response = Util.createUser(user);

        response.then().assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));

        String accessToken = response.jsonPath().getString("accessToken");

        Util.deleteUser(user, accessToken);
    }

    @Test
    @DisplayName("Попытка создания существующего пользователя")
    public void creatingExistingUser() {
        User user = Util.randomUser();

        Response firstResponse = Util.createUser(user);

        firstResponse.then().assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));

        Response secondResponse = Util.createUser(user);

        secondResponse.then().assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(HttpStatus.SC_FORBIDDEN);

        String accessToken = firstResponse.jsonPath().getString("accessToken");

        Util.deleteUser(user, accessToken);
    }

    @Test
    @DisplayName("Создание пользователя без e-mail'а")
    public void CreateUserWithoutEmail() {
        User user = new User(null, "1234567", "John");

        Response firstResponse = Util.createUser(user);

        firstResponse.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }
}
