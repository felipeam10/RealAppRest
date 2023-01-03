import br.fe.felipe.rest.core.Constantes;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;

public class BaseTest implements Constantes {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = APP_BASE_URL;
        RestAssured.port = APP_PORT;
        RestAssured.basePath = APP_BASE_PATH;

        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.setContentType(APP_CONTENT_TYPE);
        RestAssured.requestSpecification = reqBuilder.build();

        ResponseSpecBuilder respBuilder = new ResponseSpecBuilder();
        respBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT));
        RestAssured.responseSpecification = respBuilder.build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
