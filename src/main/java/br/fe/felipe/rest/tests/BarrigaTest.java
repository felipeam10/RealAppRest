package br.fe.felipe.rest.tests;

import br.fe.felipe.rest.core.BaseTest;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {

    private String TOKEN;

    @Before
    public void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "felipeam10@hotmail.com");
        login.put("senha", "123456");

        TOKEN = given()
                .body(login)
        .when()
                .post("/signin")
        .then()
                .statusCode(200)
                .extract().path("token")
        ;
    }

    @Test
    public void naoDeveAcessarAPISemToken(){
        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;

    }

    @Test
    public void deveIncluirContaComSucesso(){
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \"conta qualquer3\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
        ;

    }

    @Test
    public void deveAlterarContaComSucesso(){
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \"conta alterada3\"}")
        .when()
                .put("/contas/1541255")
        .then()
                .statusCode(200)
                .body("nome", is("conta alterada3"))
        ;

    }

    @Test
    public void naoDeveIncluirContaComNomeRepetido(){
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \"conta qualquer3\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;

    }
}
