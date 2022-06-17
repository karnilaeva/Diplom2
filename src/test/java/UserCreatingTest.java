import io.restassured.response.Response;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.*;

public class UserCreatingTest extends BaseTest{

    @Test
    public void successfulCreating() {
        User user = randomUser();

        Response response = Util.createUser(user);

        response.then().assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));

        String accessToken = response.jsonPath().getString("accessToken");

        Util.deleteUser(user, accessToken);
    }

    @Test
    public void creatingExistingUser() {
        User user = randomUser();

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
    public void CreateUserWithoutEmail() {
        User user = new User(null, "1234567", "John");

        Response firstResponse = Util.createUser(user);

        firstResponse.then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    private User randomUser() {
        Random random = new Random();
        String email = "user" + random.nextInt(10000000) + "@yandex.ru";
        return new User(email, "1234567", "John");
    }

}
