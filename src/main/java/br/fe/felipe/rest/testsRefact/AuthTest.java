package br.fe.felipe.rest.testsRefact;

import br.fe.felipe.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class AuthTest extends BaseTest {

    @Test
    public void naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;

    }
}
