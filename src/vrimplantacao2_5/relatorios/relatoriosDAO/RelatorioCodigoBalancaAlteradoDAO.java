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
import vrimplantacao2_5.relatorios.vo.CodBalAlteradoVO;

/**
 *
 * @author Michael
 */
public class RelatorioCodigoBalancaAlteradoDAO {

    public List<CodBalAlteradoVO> getCodBalFaltando() throws Exception {
        List<CodBalAlteradoVO> dadosCodBal = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select 	\n"
                    + "	ce.importid,\n"
                    + "	cp.descricao,\n"
                    + "	ce.ean,\n"
                    + "	cp.codigoatual\n"
                    + "from \n"
                    + "	implantacao.codant_ean ce \n"
                    + "join implantacao.codant_produto cp on ce.importsistema = cp.impsistema and \n"
                    + "	ce.importloja = cp.imploja and \n"
                    + "	ce.importid = cp.impid\n"
                    + "where \n"
                    + "	ce.ean similar to '[0-9]*' and \n"
                    + "	ce.ean != '' and \n"
                    + "	ce.ean is not null and \n"
                    + "	length(cast(ce.ean::numeric as varchar)) < 7 and \n"
                    + "	cp.codigoatual != ce.ean::integer\n"
                    + "order by cp.descricao"
            )) {
                while (rst.next()) {
                    CodBalAlteradoVO vo = new CodBalAlteradoVO();
                    vo.setId(rst.getString("importid"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setEan(rst.getString("ean"));
                    vo.setCodigoAtual(rst.getString("codigoatual"));
                    dadosCodBal.add(vo);
                }
                return dadosCodBal;
            }
        }
    }

}
