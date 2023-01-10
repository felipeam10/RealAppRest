package br.fe.felipe.rest.utils;

import br.fe.felipe.rest.tests.Movimentacao;
import io.restassured.RestAssured;

public class BarrigaUtils {

    public static Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
    }

    public static Integer getIdMovPelaDescricao(String desc){
        return RestAssured.get("/transacoes?descricao=" + desc).then().extract().path("id[0]");
    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
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
