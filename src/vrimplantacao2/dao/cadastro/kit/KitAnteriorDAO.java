package vrimplantacao2.dao.cadastro.kit;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.kit.KitVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.kit.KitAnteriorVO;

public class KitAnteriorDAO {

    private MultiMap<String, KitAnteriorVO> anteriores;

    public MultiMap<String, KitAnteriorVO> getAnteriores() throws Exception {
        if (anteriores == null) {
            atualizarAnteriores();
        }
        return anteriores;
    }

    public void atualizarAnteriores() throws Exception {
        anteriores = new MultiMap<>(3);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ck.impsistema,\n"
                    + "	ck.imploja,\n"
                    + "	ck.impid,\n"
                    + "	ck.codigoatual,\n"
                    + "	ck.importidproduto,\n"
                    + "	ck.codigoatualprodutokit,\n"
                    + "	ck.observacaoimportacao \n"
                    + "FROM implantacao.codant_kit ck \n"
                    + "ORDER BY\n"
                    + "	ck.impsistema,\n"
                    + "	ck.imploja,\n"
                    + "	ck.impid"
            )) {
                while (rst.next()) {
                    KitAnteriorVO vo = new KitAnteriorVO();
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setImportIdProduto(rst.getString("importidproduto"));
                    vo.setCodigoAtualProduto(rst.getInt("codigoatualprodutokit"));
                    vo.setObservacaoImportacao(rst.getString("observacaoimportacao"));

                    int codigoAtual = rst.getInt("codigoatual");
                    KitVO kit = new KitVO();
                    kit.setId(codigoAtual);

                    vo.setCodigoAtual(kit);
                   
                    anteriores.put(
                            vo,
                            vo.getImportSistema(),
                            vo.getImportLoja(),
                            vo.getImportId()
                    );
                }
            }
        }
    }

    public void gravarKitAnterior(KitAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_kit");
            sql.put("impsistema", vo.getImportSistema());
            sql.put("imploja", vo.getImportLoja());
            sql.put("impid", vo.getImportId());
            if (vo.getCodigoAtual() != null) {
                sql.put("codigoatual", vo.getCodigoAtual().getId());
            } else {
                sql.putNull("codigoatual");
            }
            sql.put("importidproduto", vo.getImportIdProduto());
            sql.put("codigoatualprodutokit", vo.getCodigoAtualProduto());
            sql.put("observacaoImportacao", vo.getObservacaoImportacao());
            stm.execute(sql.getInsert());
        }
    }
}
