/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.cadastro.pdv;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Desenvolvimento
 */
public class PdvBalancaLayoutDAO {

    public List<SQLBuilder> carregarPdvBalancaEtiquetaLayout(LojaVO i_loja) throws Exception {
        String sql = "SELECT * FROM pdv.balancaetiquetalayout WHERE id_loja = " + i_loja.getIdCopiarLoja();
        SQLBuilder sqlInsert = null;
        List<SQLBuilder> listaDeInserts = new ArrayList<>();

        int proximoId = captaUltimoIdPdvBalancaEtiquetaLayout() + 1;

        if (proximoId == -1) {
            System.out.println("Erro em PdvBalancaLayoutDAO, provávelmente não há dados na tabela pdv.balancaetiquetalayout.");
            throw new Exception("Erro ao Copiar pdv.balancaetiquetalayout, provávelmente não há dados na tabela");
        }

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    sqlInsert = new SQLBuilder();

                    sqlInsert.setSchema("pdv");
                    sqlInsert.setTableName("balancaetiquetalayout");

                    sqlInsert.put("id", proximoId++);
                    sqlInsert.put("id_loja", i_loja.getId());
                    sqlInsert.put("id_tipobalancaetiqueta", rst.getInt("id_tipobalancaetiqueta"));
                    sqlInsert.put("id_tipobalancoetiquetacampo", rst.getInt("id_tipobalancoetiquetacampo"));
                    sqlInsert.put("iniciopeso", rst.getInt("iniciopeso"));
                    sqlInsert.put("tamanhopeso", rst.getInt("tamanhopeso"));
                    sqlInsert.put("iniciopreco", rst.getInt("iniciopreco"));
                    sqlInsert.put("tamanhopreco", rst.getInt("tamanhopreco"));
                    listaDeInserts.add(sqlInsert);
                }
            }
        }
        return listaDeInserts;
    }

    private int captaUltimoIdPdvBalancaEtiquetaLayout() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT max(id) ultimo_id FROM pdv.balancaetiquetalayout"
            )) {
                while (rst.next()) {
                    return rst.getInt("ultimo_id");
                }
            }
        }
        return -2;
    }
    
}
