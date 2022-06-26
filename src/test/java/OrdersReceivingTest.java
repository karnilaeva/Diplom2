import io.restassured.response.Response;
import model.Order;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrdersReceivingTest extends BaseTest {

    private final String apiPath = "/api/orders";
    private final User user = Util.randomUser();
    private String accessToken;

    @Before
    public void createUserAndOrder() {
        Response response = Util.createUser(user);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        this.accessToken = response.jsonPath().getString("accessToken");

        response = given().get("/api/ingredients");
        List <String> ingredients = response.jsonPath().getList("data._id", String.class);

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(new Order(ingredients))
                .post("/api/orders")
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @After
    public void deleteUser() {
        Util.deleteUser(user, accessToken);
    }

    @Test
    public void receivingOrders() {
        Response response = given()
                .header("Authorization", accessToken)
                .get(apiPath);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    public void unauthorized() {
        Response response = given().get(apiPath);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

}
