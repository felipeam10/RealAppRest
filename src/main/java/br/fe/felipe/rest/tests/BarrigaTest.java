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
}
