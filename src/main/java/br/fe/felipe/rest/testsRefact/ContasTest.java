package br.fe.felipe.rest.testsRefact;

import br.fe.felipe.rest.core.BaseTest;
import br.fe.felipe.rest.utils.BarrigaUtils;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso(){
        given()
                .body("{\"nome\": \"Conta inserida\" }")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
        ;

    }

    @Test
    public void deveAlterarContaComSucesso(){
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar");

        given()
                .body("{\"nome\": \"Conta alterada independente\"}")
                .pathParam("id", CONTA_ID)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is("Conta alterada independente"))
        ;

    }

    @Test
    public void naoDeveIncluirContaComNomeRepetido(){
        given()
                .body("{\"nome\": \"Conta mesmo nome\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;

    }
}
