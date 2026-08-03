package vrimplantacao2.dao.cadastro.produtosimilar;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarAnteriorVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarItemAnteriorVO;

public class ProdutoSimilarAnteriorDAO {

    private MultiMap<String, ProdutoSimilarAnteriorVO> anteriores;

    public MultiMap<String, ProdutoSimilarAnteriorVO> getAnteriores() throws Exception {
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
                    + "	cp.impsistema,\n"
                    + "	cp.imploja,\n"
                    + "	cp.impid,\n"
                    + "	cp.codigoatual,\n"
                    + " cp.descricao, \n"
                    + "	cp.observacaoimportacao \n"
                    + "FROM\n"
                    + "	implantacao.codant_produtosimilar cp\n"
                    + "ORDER BY\n"
                    + "	cp.impsistema,\n"
                    + "	cp.imploja,\n"
                    + "	cp.impid"
            )) {
                while (rst.next()) {
                    ProdutoSimilarAnteriorVO vo = new ProdutoSimilarAnteriorVO();
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setObservacaoImportacao(rst.getString("observacaoimportacao"));

                    int codigoAtual = rst.getInt("codigoatual");
                    ProdutoSimilarVO ps = new ProdutoSimilarVO();
                    ps.setId(codigoAtual);

                    vo.setCodigoAtual(ps);

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

    public void gravarProdutoSimilarAnterior(ProdutoSimilarAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_produtosimilar");
            sql.put("impsistema", vo.getImportSistema());
            sql.put("imploja", vo.getImportLoja());
            sql.put("impid", vo.getImportId());
            if (vo.getCodigoAtual() != null) {
                sql.put("codigoatual", vo.getCodigoAtual().getId());
            } else {
                sql.putNull("codigoatual");
            }
            sql.put("descricao", vo.getDescricao());
            sql.put("observacaoImportacao", vo.getObservacaoImportacao());
            stm.execute(sql.getInsert());
        }
    }

    public void gravarProdutoSimilarItemAnterior(ProdutoSimilarItemAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_produtosimilaritem");
            sql.put("impsistema", vo.getImportSistema());
            sql.put("imploja", vo.getImportLoja());
            sql.put("impid", vo.getImportId());
            if (vo.getCodigoAtual() != null) {
                sql.put("codigoatual", vo.getCodigoAtual().getId());
            } else {
                sql.putNull("codigoatual");
            }
            sql.put("importidprodutosimilar", vo.getImportIdProdutoSimilar());
            if (vo.getCodigoAtualProdutoSimilar()!= null) {
                sql.put("codigoatualprodutosimilar", vo.getCodigoAtualProdutoSimilar().getId());
            } else {
                sql.putNull("codigoatualprodutosimilar");
            }
            sql.put("importidproduto", vo.getImportIdProduto());
            if (vo.getCodigoAtualProduto()!= null) {
                sql.put("codigoatualproduto", vo.getCodigoAtualProduto().getId());
            } else {
                sql.putNull("codigoatualproduto");
            }
            sql.put("observacaoimportacao", vo.getObservacaoImportacao());
            stm.execute(sql.getInsert());
        }
    }
}
