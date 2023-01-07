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

    @Test
    public void deveIncluirMovimentacaoComSucesso(){
        Movimentacao mov = getMovimentacaoValida();

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
        ;

    }

    @Test
    public void deveValidarCamposObrigatoriosNaMovimentacao(){
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;

    }

    @Test
    public void naoDeveIncluirMovimentacaoComDataFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao("07/01/2023");

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;

    }

    @Test
    public void naoDeveRemoverContaComMovimentacao(){
        given()
                .header("Authorization", "JWT " + TOKEN)
        .when()
                .delete("/contas/1541375")
        .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
        ;

    }

    @Test
    public void deveCalcularSaldoContas(){
        given()
                .header("Authorization", "JWT " + TOKEN)
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == 1546201}.saldo", is("-1500.00"))
        ;

    }

    @Test
    public void deveRemoverMovimentacao(){
        given()
                .header("Authorization", "JWT " + TOKEN)
        .when()
                .delete("/transacoes/1445688")
        .then()
                .statusCode(204)
        ;

    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(1541376);
//        mov.setId_usuario(id_usuario);
        mov.setDescricao("Movimentacao teste");
        mov.setEnvolvido("Interessado");
        mov.setTipo("REC");
        mov.setData_transacao("01/01/2020");
        mov.setData_pagamento("10/05/2010");
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }
}
