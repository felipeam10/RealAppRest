package br.fe.felipe.rest.tests;

import br.fe.felipe.rest.core.BaseTest;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {

    @Test
    public void naoDeveAcessarAPISemToken(){
        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;

    }
}
