package br.fe.felipe.rest.testsRefactSuite;

import br.fe.felipe.rest.core.BaseTest;
import br.fe.felipe.rest.testsRefact.AuthTest;
import br.fe.felipe.rest.testsRefact.ContasTest;
import br.fe.felipe.rest.testsRefact.MovimentacaoTest;
import br.fe.felipe.rest.testsRefact.SaldoTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class Suite extends BaseTest {

    @BeforeClass
    public static void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "felipeam10@hotmail.com");
        login.put("senha", "123456");

        String TOKEN = given()
                .body(login)
        .when()
                .post("/signin")
        .then()
                .statusCode(200)
                .extract().path("token")
                ;

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }
}
