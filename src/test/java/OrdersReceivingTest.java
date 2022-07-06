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
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrdersReceivingTest extends BaseTest {

    private final OrdersClient ordersClient = new OrdersClient();
    private final UserClient userClient = new UserClient();

    private final User user = UserClient.randomUser();
    private String accessToken;

    @Before
    public void createUserAndOrder() {
        Response response = userClient.createUser(user);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        this.accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(user, accessToken);
    }

    @Test
    @DisplayName("Получение списка заказов")
    public void receivingOrders() {
        Response response = ordersClient.getOrders(accessToken);

        response.then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованным пользователем")
    public void unauthorized() {
        Response response = ordersClient.getOrders();

        response.then().assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

}
