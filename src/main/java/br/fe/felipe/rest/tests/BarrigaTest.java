package br.fe.felipe.rest.tests;

import br.fe.felipe.rest.core.BaseTest;
import static org.hamcrest.Matchers.*;

import br.fe.felipe.rest.utils.DateUtils;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

    private static String contaName = "Conta " + System.nanoTime();

    private static Integer CONTA_ID;
    private static Integer MOV_ID;

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
    }

    @Test
    public void t02_deveIncluirContaComSucesso(){
        CONTA_ID = given()
                .body("{\"nome\": \"" + contaName + "\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;

    }

    @Test
    public void t03_deveAlterarContaComSucesso(){
        given()
                .body("{\"nome\": \"" + contaName + " alterada\"}")
                .pathParam("id", CONTA_ID)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is(contaName + " alterada"))
        ;

    }

    @Test
    public void t04_naoDeveIncluirContaComNomeRepetido(){
        given()

                .body("{\"nome\": \"" + contaName + " alterada\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("J?? existe uma conta com esse nome!"))
        ;

    }

    @Test
    public void t05_deveIncluirMovimentacaoComSucesso(){
        Movimentacao mov = getMovimentacaoValida();

        MOV_ID = given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;

    }

    @Test
    public void t06_deveValidarCamposObrigatoriosNaMovimentacao(){
        given()
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimenta????o ?? obrigat??rio",
                        "Data do pagamento ?? obrigat??rio",
                        "Descri????o ?? obrigat??rio",
                        "Interessado ?? obrigat??rio",
                        "Valor ?? obrigat??rio",
                        "Valor deve ser um n??mero",
                        "Conta ?? obrigat??rio",
                        "Situa????o ?? obrigat??rio"
                ))
        ;

    }

    @Test
    public void t07_naoDeveIncluirMovimentacaoComDataFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DateUtils.getDiferencaDias(2));

        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimenta????o deve ser menor ou igual ?? data atual"))
        ;

    }

    @Test
    public void t08_naoDeveRemoverContaComMovimentacao(){
        given()
                .pathParam("id", CONTA_ID)
        .when()
                .delete("/contas/{id}")
        .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
        ;

    }

    @Test
    public void t09_deveCalcularSaldoContas(){
        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
        ;

    }

    @Test
    public void t10_deveRemoverMovimentacao(){
        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204)
        ;

    }

    @Test
    public void t11_naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;

    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(CONTA_ID);
//        mov.setId_usuario(id_usuario);
        mov.setDescricao("Movimentacao teste");
        mov.setEnvolvido("Interessado");
        mov.setTipo("REC");
        mov.setData_transacao(DateUtils.getDiferencaDias(-1));
        mov.setData_pagamento(DateUtils.getDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }
}
