package vrimplantacao2.dao.cadastro.produtosimilar;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarItemVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarItemAnteriorVO;

public class ProdutoSimilarItemAnteriorDAO {

    private MultiMap<String, ProdutoSimilarItemAnteriorVO> anteriores;

    public MultiMap<String, ProdutoSimilarItemAnteriorVO> getAnteriores() throws Exception {
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
                    + "	cp.importidprodutosimilar,\n"
                    + "	cp.codigoatualprodutosimilar,\n"
                    + "	cp.importidproduto,\n"
                    + "	cp.codigoatualproduto,\n"
                    + "	cp.observacaoimportacao \n"
                    + "FROM\n"
                    + "	implantacao.codant_produtosimilaritem cp\n"
                    + "ORDER BY\n"
                    + "	cp.impsistema,\n"
                    + "	cp.imploja,\n"
                    + "	cp.impid"
            )) {
                while (rst.next()) {
                    ProdutoSimilarItemAnteriorVO vo = new ProdutoSimilarItemAnteriorVO();
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setObservacaoImportacao(rst.getString("observacaoimportacao"));

                    int codigoAtual = rst.getInt("codigoatual");
                    ProdutoSimilarItemVO ps = new ProdutoSimilarItemVO();
                    ps.setId(codigoAtual);
                    vo.setCodigoAtual(ps);
                    
                    vo.setImportIdProdutoSimilar(rst.getString("importidprodutosimilar"));
                    
                    int codigoAtualProdutoSimilar = rst.getInt("codigoatualprodutosimilar");
                    ProdutoSimilarVO psp = new ProdutoSimilarVO();
                    psp.setId(codigoAtualProdutoSimilar);
                    vo.setCodigoAtualProdutoSimilar(psp);
                    
                    vo.setImportIdProduto(rst.getString("importidproduto"));
                    
                    int codigoatualproduto = rst.getInt("codigoatualproduto");
                    ProdutoVO pro = new ProdutoVO();
                    pro.setId(codigoatualproduto);
                    vo.setCodigoAtualProduto(pro);

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

//    public void gravarAssociadoAnterior(AssociadoAnteriorVO vo) throws Exception {
//        try (Statement stm = Conexao.createStatement()) {
//            SQLBuilder sql = new SQLBuilder();
//            sql.setSchema("implantacao");
//            sql.setTableName("codant_associado");
//            sql.put("impsistema", vo.getImportSistema());
//            sql.put("imploja", vo.getImportLoja());
//            sql.put("impid", vo.getImportId());
//            sql.put("importIdProduto", vo.getImportIdProduto());
//            if (vo.getCodigoAtual() != null) {
//                sql.put("codigoatual", vo.getCodigoAtual().getId());
//            } else {
//                sql.putNull("codigoatual");
//            }
//            if (vo.getCodigoAtualProdutoAssociado() != null) {
//                sql.put("codigoAtualProdutoAssociado", vo.getCodigoAtualProdutoAssociado().getId());
//            } else {
//                sql.putNull("codigoAtualProdutoAssociado");
//            }
//
//            sql.put("qtdEmbalagem", vo.getQtdEmbalagem());
//            sql.put("observacaoImportacao", vo.getObservacaoImportacao());
//            stm.execute(sql.getInsert());
//        }
//    }
//
//    public void gravarAssociadoItemAnterior(AssociadoItemAnteriorVO vo) throws Exception {
//        try (Statement stm = Conexao.createStatement()) {
//            SQLBuilder sql = new SQLBuilder();
//            sql.setSchema("implantacao");
//            sql.setTableName("codant_associadoitem");
//            sql.put("impsistema", vo.getImportSistema());
//            sql.put("imploja", vo.getImportLoja());
//            sql.put("impid", vo.getImportId());
//            if (vo.getCodigoAtual() != null) {
//                sql.put("codigoatual", vo.getCodigoAtual().getId());
//            } else {
//                sql.putNull("codigoatual");
//            }
//            sql.put("importidassociado", vo.getImportIdAssociado());
//            if (vo.getCodigoAtualAssociado() != null) {
//                sql.put("codigoAtualAssociado", vo.getCodigoAtualAssociado().getId());
//            } else {
//                sql.putNull("codigoAtualAssociado");
//            }
//            sql.put("importIdProdutoAssociadoItem", vo.getImportIdProduto());
//            if (vo.getCodigoAtualProdutoAssociadoItem() != null) {
//                sql.put("codigoAtualProdutoAssociadoItem", vo.getCodigoAtualProdutoAssociadoItem().getId());
//            } else {
//                sql.putNull("codigoAtualProdutoAssociadoItem");
//            }
//            sql.put("qtdEmbalagemItem", vo.getQtdEmbalagemItem());
//
//            sql.put("percentualPreco", vo.getPercentualPreco());
//            sql.put("aplicaPreco", vo.isAplicaPreco());
//            sql.put("aplicaCusto", vo.isAplicaCusto());
//            sql.put("aplicaEstoque", vo.isAplicaEstoque());
//            sql.put("percentualcustoestoque", vo.getPercentualcustoestoque());
//            sql.put("observacaoImportacao", vo.getObservacaoImportacao());
//            stm.execute(sql.getInsert());
//        }
//    }
}
