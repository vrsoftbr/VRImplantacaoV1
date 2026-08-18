package vrimplantacao2.dao.cadastro.kit;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.kit.KitVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.kit.KitItemVO;
import vrimplantacao2.vo.cadastro.kit.KitItemAnteriorVO;

public class KitItemAnteriorDAO {

    private MultiMap<String, KitItemAnteriorVO> anteriores;

    public MultiMap<String, KitItemAnteriorVO> getAnteriores() throws Exception {
        if (anteriores == null) {
            atualizarAnteriores();
        }
        return anteriores;
    }

    public void atualizarAnteriores() throws Exception {
        anteriores = new MultiMap<>(4);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ck.impsistema,\n"
                    + "	ck.imploja,\n"
                    + "	ck.impid,\n"
                    + "	ck.codigoatual,\n"
                    + "	ck.importidkit ,\n"
                    + "	ck.codigoatualkit,\n"
                    + "	ck.importidproduto,\n"
                    + "	ck.codigoatualproduto,\n"
                    + "	ck.observacaoimportacao \n"
                    + "FROM implantacao.codant_kititem ck \n"
                    + "ORDER BY \n"
                    + "	ck.impsistema,\n"
                    + "	ck.imploja,\n"
                    + "	ck.impid"
            )) {
                while (rst.next()) {
                    KitItemAnteriorVO vo = new KitItemAnteriorVO();
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setImportIdKit(rst.getString("importidkit"));
                    vo.setImportIdProduto(rst.getString("importidproduto"));
                    vo.setCodigoAtualProduto(rst.getInt("codigoatualproduto"));
                    vo.setObservacaoImportacao(rst.getString("observacaoimportacao"));

                    int codigoAtual = rst.getInt("codigoatual");
                    KitItemVO kitItem = new KitItemVO();
                    kitItem.setId(codigoAtual);

                    int codigoAtualKit = rst.getInt("codigoatualkit");
                    KitVO kit = new KitVO();
                    kit.setId(codigoAtualKit);

                    vo.setCodigoAtual(kitItem);
                    vo.setCodigoAtualKit(kit);

                    anteriores.put(
                            vo,
                            vo.getImportSistema(),
                            vo.getImportLoja(),
                            vo.getImportId(),
                            vo.getImportIdKit()
                    );
                }
            }
        }
    }

    public void gravarKitItemAnterior(KitItemAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_kititem");
            sql.put("impsistema", vo.getImportSistema());
            sql.put("imploja", vo.getImportLoja());
            sql.put("impid", vo.getImportId());
            if (vo.getCodigoAtual() != null) {
                sql.put("codigoatual", vo.getCodigoAtual().getId());
            } else {
                sql.putNull("codigoatual");
            }
            sql.put("importidkit", vo.getImportIdKit());
            if (vo.getCodigoAtualKit()!= null) {
                sql.put("codigoatualkit", vo.getCodigoAtualKit().getId());
            } else {
                sql.putNull("codigoatualkit");
            }
            sql.put("importidproduto", vo.getImportIdProduto());
            if (vo.getCodigoAtualProduto() > 0) {
                sql.put("codigoatualproduto", vo.getCodigoAtualProduto());
            } else {
                sql.putNull("codigoatualproduto");
            }
            sql.put("observacaoimportacao", vo.getObservacaoImportacao());
            stm.execute(sql.getInsert());
        }
    }
}
