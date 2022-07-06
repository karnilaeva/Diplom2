import io.qameta.allure.junit4.DisplayName;
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

public class OrderCreatingTest extends BaseTest {

    private final String apiPath = "/api/orders";
    private final User user = Util.randomUser();
    private String accessToken;
    private List<String> ingredients;

    @Before
    public void getIngredients() {
        Response response = given().get("/api/ingredients");
        ingredients = response.jsonPath().getList("data._id", String.class);
    }

    @Before
    public void createUser() {
        Response response = Util.createUser(user);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        this.accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        Util.deleteUser(user, accessToken);
    }

    @Test
    @DisplayName("Успешный заказ авторизованным пользователем")
    public void successfulOrderAuthorized() {
        Order order = new Order(ingredients);

        Response response = given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(order)
                .post(apiPath);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Успешный заказ неавторизованным пользователем")
    public void successfulOrderjUnauthorized() {
        Order order = new Order(ingredients);

        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post(apiPath);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка при создании заказа без ингредиентов")
    public void orderWithoutIngredients() {
        Order order = new Order(List.of());

        Response response = given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(order)
                .post(apiPath);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Ошибка при создании заказа с некорректными ингредиентами")
    public void wrongOrderIngredients() {
        Order order = new Order(List.of("123"));

        Response response = given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(order)
                .post(apiPath);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
