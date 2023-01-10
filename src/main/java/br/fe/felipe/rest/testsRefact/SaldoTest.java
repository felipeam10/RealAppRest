package br.fe.felipe.rest.testsRefact;

import br.fe.felipe.rest.core.BaseTest;
import br.fe.felipe.rest.tests.Movimentacao;
import br.fe.felipe.rest.utils.DateUtils;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SaldoTest extends BaseTest {

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

    @Test
    public void deveCalcularSaldoContas(){
        Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");
        System.out.println(CONTA_ID);
        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;

    }

    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome" + nome).then().extract().path("id[0]");
    }


}
