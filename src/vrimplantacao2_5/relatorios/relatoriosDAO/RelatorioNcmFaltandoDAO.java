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
import vrimplantacao2_5.relatorios.vo.NCMFaltandoVO;

/**
 *
 * @author Michael
 */
public class RelatorioNcmFaltandoDAO {

    public List<NCMFaltandoVO> getNcmFaltando() throws Exception {
        List<NCMFaltandoVO> dadosNcm = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	distinct cp2.ncm,\n"
                    + "	count(*) qtd\n"
                    + "from \n"
                    + "	implantacao.codant_produto cp2 \n"
                    + "where \n"
                    + "	cp2.ncm not in \n"
                    + "(with ncm_ant as (select \n"
                    + "						distinct\n"
                    + "						substring(cp.ncm, 1, 4) ncm1,\n"
                    + "						substring(cp.ncm, 5, 2) ncm2,\n"
                    + "						substring(cp.ncm, 7, 2) ncm3,\n"
                    + "						cp.ncm\n"
                    + "					from \n"
                    + "						implantacao.codant_produto cp\n"
                    + "					where \n"
                    + "						cp.ncm is not null and \n"
                    + "						cp.ncm != '' and \n"
                    + "						cp.ncm != '00000000')\n"
                    + "select \n"
                    + "	a.ncm\n"
                    + "from  \n"
                    + "	ncm_ant a\n"
                    + "join ncm n on a.ncm1 = lpad(n.ncm1::varchar,4,'0') and \n"
                    + "	a.ncm2 = lpad(n.ncm2::varchar,2,'0') and \n"
                    + "	a.ncm3 = lpad(n.ncm3::varchar,2,'0'))\n"
                    + "group by cp2.ncm"
            )) {
                while (rst.next()) {
                    NCMFaltandoVO vo = new NCMFaltandoVO();
                    vo.setNcm(rst.getString("ncm"));
                    vo.setQtd(rst.getString("qtd"));
                    dadosNcm.add(vo);
                }
                return dadosNcm;
            }
        }
    }

}
