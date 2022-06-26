import org.junit.Before;

import static io.restassured.RestAssured.baseURI;

public class BaseTest {

    @Before
    public void setUp() {
        baseURI = "https://stellarburgers.nomoreparties.site/";
    }

}
