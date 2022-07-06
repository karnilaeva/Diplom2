package api.client;

import io.restassured.response.Response;
import model.Order;
import org.apache.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrdersClient {

    private static final String API_PATH = "/api/orders";

    public Response postOrder(String accessToken) {
        Order order = new Order(getIngredients());

        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(order)
                .post(API_PATH);
    }

    public Response postOrder() {
        Order order = new Order(getIngredients());

        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(API_PATH);
    }

    public Response postOrderWithoutIngredients(String accessToken) {
        Order order = new Order(List.of());

        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(order)
                .post(API_PATH);
    }

    public Response postOrderWithWrongIngredients(String accessToken) {
        Order order = new Order(List.of("123"));

        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(order)
                .post(API_PATH);
    }

    public Response getOrders(String accessToken) {
        createOrder(accessToken);

        return given()
                .header("Authorization", accessToken)
                .get(API_PATH);
    }

    public Response getOrders() {
        return given().get(API_PATH);
    }

    private void createOrder(String accessToken) {
        Response response = given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(new Order(getIngredients()))
                .post("/api/orders");

        response.then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    private List<String> getIngredients() {
        Response response = given().get("/api/ingredients");
        return response.jsonPath().getList("data._id", String.class);
    }
}
