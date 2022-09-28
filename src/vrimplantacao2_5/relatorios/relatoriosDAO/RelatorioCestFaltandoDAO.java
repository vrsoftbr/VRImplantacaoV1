/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.relatorios.relatoriosDAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2_5.relatorios.vo.CestFaltandoVO;

/**
 *
 * @author Michael
 */
public class RelatorioCestFaltandoDAO {

    public List<CestFaltandoVO> getCestFaltando() throws Exception {
        List<CestFaltandoVO> dadosCest = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	distinct\n"
                    + "	ca.cest,\n"
                    + "	count(*) qtd\n"
                    + "from \n"
                    + "	implantacao.codant_produto ca\n"
                    + "	join produto p on ca.codigoatual = p.id\n"
                    + "where \n"
                    + "	not ca.cest is null and\n"
                    + "	ca.cest != '' and \n"
                    + "	id_cest is null\n"
                    + "group by ca.cest"
            )) {
                while (rst.next()) {
                    CestFaltandoVO vo = new CestFaltandoVO();
                    vo.setCest(rst.getString("cest"));
                    vo.setQtd(rst.getString("qtd"));
                    dadosCest.add(vo);
                }
                return dadosCest;
            }
        }
    }

}
