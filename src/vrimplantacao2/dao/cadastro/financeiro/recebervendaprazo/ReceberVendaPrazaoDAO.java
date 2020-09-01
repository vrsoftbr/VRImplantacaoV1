/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.financeiro.recebervendaprazo;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.cadastro.financeiro.ReceberVendaPrazoVO;

/**
 *
 * @author Lucas
 */
public class ReceberVendaPrazaoDAO {

    public void salvar(List<ReceberVendaPrazoVO> vo, int idLoja) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(vo.size());
            ProgressBar.setStatus("Importando Receber Venda Prazo...");

            for (ReceberVendaPrazoVO i_vo : vo) {

                rst = stm.executeQuery(
                        "select id from recebervendaprazo "
                        + "where dataemissao = '" + i_vo.getDataemissao() + "' "
                        + "and numeronota = " + i_vo.getNumeronota() + " "
                        + "and valor = " + i_vo.getValor() + " "
                        + "and id_clienteeventual = " + i_vo.getId_clienteeventual()+ " "
                        + "and id_loja = " + idLoja
                );

                if (!rst.next()) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO recebervendaprazo(\n"
                            + " id_loja, "
                            + " id_clienteeventual, "
                            + " dataemissao, "
                            + " datavencimento, \n"
                            + " valor, "
                            + " valorliquido, "
                            + " observacao, "
                            + " id_situacaorecebervendaprazo, \n"
                            + " id_tipolocalcobranca, "
                            + " numeronota, "
                            + " impostorenda, "
                            + " pis, "
                            + " cofins, \n"
                            + " csll, "
                            + " id_tiposaida, "
                            + " lancamentomanual, "
                            + " numeroparcela) "
                            + "values ("
                            + idLoja + ", " //id_loja
                            + i_vo.getId_clienteeventual() + ", " //id_clienteeventual
                            + "'" + i_vo.getDataemissao() + "', " //dataemissao
                            + "'" + i_vo.getDatavencimento() + "', " //datavencimento
                            + i_vo.getValor() + ", " //valor
                            + i_vo.getValorliquido() + ", " //valorliquido
                            + "'" + i_vo.getObservacao() + "', " //observacao
                            + "0, " //id_situacaorecebervendaprazo
                            + "0, " //id_tipolocalcobranca
                            + i_vo.getNumeronota() + ", " //numeronota
                            + "0, " //impostorenda
                            + "0, " //pis
                            + "0, " //cofins
                            + "0, " //csll
                            + "0, " //id_tiposaida
                            + "false, " //lancamentomanual
                            + i_vo.getNumeroparcela() + ");" //numeroparcela
                    );
                    
                    System.out.println(sql.toString());
                    
                    stm.execute(sql.toString());                    
                }                
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
