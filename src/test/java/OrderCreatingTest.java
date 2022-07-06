import api.client.OrdersClient;
import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class OrderCreatingTest extends BaseTest {

    private final OrdersClient ordersClient = new OrdersClient();
    private final UserClient userClient = new UserClient();

    private final User user = UserClient.randomUser();
    private String accessToken;

    @Before
    public void createUser() {
        Response response = userClient.createUser(user);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        this.accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(user, accessToken);
    }

    @Test
    @DisplayName("Успешный заказ авторизованным пользователем")
    public void successfulOrderAuthorized() {

        Response response = ordersClient.postOrder(accessToken);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Успешный заказ неавторизованным пользователем")
    public void successfulOrderjUnauthorized() {
        Response response = ordersClient.postOrder();

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка при создании заказа без ингредиентов")
    public void orderWithoutIngredients() {
        Response response = ordersClient.postOrderWithoutIngredients(accessToken);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Ошибка при создании заказа с некорректными ингредиентами")
    public void wrongOrderIngredients() {
        Response response = ordersClient.postOrderWithWrongIngredients(accessToken);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
