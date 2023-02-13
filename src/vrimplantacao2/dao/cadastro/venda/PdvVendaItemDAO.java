package vrimplantacao2.dao.cadastro.venda;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.venda.PdvVendaItemVO;

/**
 *
 * @author Leandro
 */
public class PdvVendaItemDAO {

    private static final Logger LOG = Logger.getLogger(PdvVendaItemDAO.class.getName());
    private final String sistema;
    private final String loja;
    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

    public PdvVendaItemDAO(String sistema, String loja) {
        this.sistema = sistema;
        this.loja = loja;
    }    
    
    public void gravar(PdvVendaItemVO item) throws Exception {
        try (Statement stm = Conexao.createStatement()) {         
            
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("pdv");
            sql.setTableName("vendaitem");
            sql.put("id_venda", item.getVenda().getId());
            sql.put("id_produto", item.getId_produto());
            sql.put("quantidade", item.getQuantidade());
            sql.put("precoVenda", item.getPrecoVenda());
            sql.put("valortotal", item.getValorTotal());
            sql.put("id_aliquota", item.getId_aliquota());
            sql.put("cancelado", item.isCancelado());
            sql.put("valorCancelado", item.getValorCancelado());
            if ( item.getTipoCancelamento() != null ) {
                sql.put("id_tipocancelamento", item.getTipoCancelamento().getId());
            }
            sql.put("matriculaCancelamento", item.getMatriculaCancelamento(), 0);
            sql.put("contadorDoc", item.getContadorDoc());
            sql.put("valorDesconto", item.getValorDesconto());
            sql.put("valorAcrescimo", item.getValorAcrescimo());
            sql.put("valorDescontoCupom", item.getValorDescontoCupom());
            sql.put("valorAcrescimoCupom", item.getValorAcrescimoCupom());
            sql.put("regraCalculo", item.getRegraCalculo());
            sql.put("codigoBarras", item.getCodigoBarras());
            sql.put("unidadeMedida", item.getUnidadeMedida());
            sql.put("totalizadorParcial", item.getTotalizadorParcial());
            sql.put("sequencia", item.getSequencia());
            sql.put("valorAcrescimoFixo", item.getValorAcrescimoFixo());
            sql.put("valorDescontoPromocao", item.getValorDescontoPromocao());
            sql.put("oferta", item.isOferta());
            if (versao.igualOuMenorQue(3, 18, 3)) {
                if (item.getTipoDesconto() != null) {
                    sql.put("tipoDesconto", item.getTipoDesconto().getId());
                }
            }
            
            sql.put("custoComImposto", item.getCustoComImposto());
            sql.put("custoSemImposto", item.getCustoSemImposto());
            sql.put("custoMedioComimposto", item.getCustoMedioComImposto(), 0);
            sql.put("custoMedioSemImposto", item.getCustoMedioSemImposto(), 0);
            sql.put("aplicaDescontoPromocao", item.isAplicaDescontoPromocao());
            sql.put("id_tipoOferta", item.getId_tipoOferta(), -1);
            sql.put("atacado", item.isAtacado());
            
            if(versao.igualOuMaiorQue(4,1,0)){
                sql.put("iskitpreconormal", false);
            }
            
            if (versao.igualOuMaiorQue(3, 21, 10)) {
                if (item.getData() != null) {
                    sql.put("data", item.getData());
                }
            }
            
            sql.getReturning().add("id");
            
            String strSQL = sql.getInsert();
            
            LOG.finer(
                String.format(
                        "Venda: %d Item: (%d) %d - %.2f X %.2f",
                        item.getVenda().getId(),
                        item.getId_produto(),
                        item.getCodigoBarras(),
                        item.getQuantidade(),
                        item.getPrecoVenda()
                )
            );
            LOG.finest(strSQL);
            
            try (ResultSet rst = stm.executeQuery(
                    strSQL
            )) {
                if (rst.next()) {
                    item.setId(rst.getLong("id"));
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, 
                        String.format(
                                "Erro ao processar o produto\n"
                                + "Venda: %d Item: (%d) %d - %.2f X %.2f\n"
                                + "SQL: " + strSQL,
                                item.getVenda().getId(),
                                item.getId_produto(),
                                item.getCodigoBarras(),
                                item.getQuantidade(),
                                item.getPrecoVenda()
                        )
                , e);
                throw e;
            }
        }
    }

    private Map<String, Integer> produtosPorCodigoAnterior;
    public Integer getProdutoPorCodigoAnterior(String produto) throws Exception {
        if (produtosPorCodigoAnterior == null) {
            LOG.info("Carregando produtos pelo codigo anterior");
            produtosPorCodigoAnterior = new HashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                String sql = "select\n" +
                        "	ant.impid,\n" +
                        "	ant.codigoatual\n" +
                        "from \n" +
                        "	implantacao.codant_produto ant\n" +
                        "	join produto p on ant.codigoatual = p.id\n" +
                        "where\n" +
                        "	ant.impsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                        "	ant.imploja = " + SQLUtils.stringSQL(loja);
                LOG.info(sql);
                try (ResultSet rst = stm.executeQuery(sql)) {
                    while (rst.next()) {
                        produtosPorCodigoAnterior.put(rst.getString("impid"), rst.getInt("codigoatual"));
                    }
                }
            }
            LOG.info("Produtos pelo código anterior carregados");
        }
        return produtosPorCodigoAnterior.get(produto);
    }

    private Map<String, Integer> produtosPorCodigoAnteriorSemUltimoDigito;
    public Integer getProdutoPorCodigoAnteriorSemUltimoDigito(String produto) throws Exception {
        if (produtosPorCodigoAnteriorSemUltimoDigito == null) {
            LOG.info("Carregando produtos pelo codigo anterior");
            produtosPorCodigoAnteriorSemUltimoDigito = new HashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	substring(ant.impid, 1, char_length(ant.impid) -1) as impid,\n" +
                        "	ant.codigoatual\n" +
                        "from \n" +
                        "	implantacao.codant_produto ant\n" +
                        "	join produto p on ant.codigoatual = p.id\n" +
                        "where\n" +
                        "	ant.impsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                        "	ant.imploja = " + SQLUtils.stringSQL(loja)
                )) {
                    while (rst.next()) {
                        produtosPorCodigoAnteriorSemUltimoDigito.put(rst.getString("impid"), rst.getInt("codigoatual"));
                    }
                }
            }
            LOG.info("Produtos pelo código anterior carregados");
        }
        return produtosPorCodigoAnteriorSemUltimoDigito.get(produto);
    }
    
    private Map<String, Integer> produtosPorEanAnterior;
    public Integer getProdutoPorEANAnterior(String ean) throws Exception {
        if (produtosPorEanAnterior == null) {
            LOG.info("Carregando produtos por EAN anterior");
            produtosPorEanAnterior = new HashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n" +
                        "	antean.ean,\n" +
                        "	ant.codigoatual\n" +
                        "from\n" +
                        "	implantacao.codant_ean antean\n" +
                        "	join implantacao.codant_produto ant on\n" +
                        "		antean.importsistema = ant.impsistema and\n" +
                        "		antean.importloja = ant.imploja and\n" +
                        "		antean.importid = ant.impid\n" +
                        "	join produto p on\n" +
                        "		ant.codigoatual = p.id\n" +
                        "where\n" +
                        "	antean.importsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                        "	antean.importloja = " + SQLUtils.stringSQL(loja)
                )) {
                    while (rst.next()) {
                        produtosPorEanAnterior.put(rst.getString("ean"), rst.getInt("codigoatual"));
                    }
                }
            }
            LOG.info("Produtos por EAN anterior carregados");
        }
        return produtosPorEanAnterior.get(ean);
    }
    
    private Map<Long, Integer> produtoPorEANAtual;
    public Integer getProdutoPorEANAtual(long ean) throws Exception {
        if (produtoPorEANAtual == null) {
            LOG.info("Carregando produtos por EAN");
            produtoPorEANAtual = new HashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	ean.id_produto,\n" +
                        "	ean.codigobarras\n" +
                        "from\n" +
                        "	produtoautomacao ean"
                )) {
                    while (rst.next()) {
                        produtoPorEANAtual.put(rst.getLong("codigobarras"), rst.getInt("id_produto"));
                    }
                }
            }
            LOG.info("Produtos por EAN carregados");
        }
        return produtoPorEANAtual.get(ean);
    }

    public void atualizar(int idVenda, PdvVendaItemVO item) throws Exception {
        try (Statement st = Conexao.createStatement()) {
            
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("pdv");
            sql.setTableName("vendaitem");
            sql.setWhere(String.format(
                    "id_venda = %d and sequencia = %d and id_produto = %d",
                    idVenda,
                    item.getSequencia(),
                    item.getId_produto()
            ));
            sql.put("custoComImposto", item.getCustoComImposto(), 0);
            sql.put("custoSemImposto", item.getCustoSemImposto(), 0);
            sql.put("custoMedioComimposto", item.getCustoMedioComImposto(), 0);
            sql.put("custoMedioSemImposto", item.getCustoMedioSemImposto(), 0);
            String update = sql.getUpdate();
            LOG.finer(update);
            int alterados = st.executeUpdate(update);
            
            String message = "id_venda = {0} and sequencia = {1} and id_produto = {2}";
            Object[] params = new Object[]{idVenda, item.getSequencia(), item.getId_produto()};            
            
            if (isMaisQueUmItemAlterado(alterados)) {                
                throw new MultiplosItensDeVendaAlteradosException(
                        idVenda,
                        item.getSequencia(), 
                        item.getId_produto(), 
                        alterados
                );
            } else if (nenhumItemFoiLocalizado(alterados)) {
                LOG.log(Level.FINEST, message, params);
            }
        }
    }
    
    private static class MultiplosItensDeVendaAlteradosException extends RuntimeException {
        private final int idVenda;
        private final int sequencia;
        private final int idProduto;
        private final int qtdAlterada;

        public MultiplosItensDeVendaAlteradosException(int idVenda, int sequencia, int idProduto, int qtdAlterada) {
            this.idVenda = idVenda;
            this.sequencia = sequencia;
            this.idProduto = idProduto;
            this.qtdAlterada = qtdAlterada;
            LOG.log(
                    Level.SEVERE,
                    MESSAGE,
                    new Object[]{
                        idVenda, 
                        sequencia,
                        idProduto,
                        qtdAlterada
                    }
            );
        }
        
        @Override
        public String getMessage() {
            return MessageFormat.format(
                    MESSAGE, 
                    qtdAlterada,
                    idVenda,
                    sequencia,
                    idProduto
            );
        }        
        private static final String MESSAGE = "Quantidade alterada {0} - id_venda = {1} and sequencia = {2} and id_produto = {3}";
    }

    private static boolean nenhumItemFoiLocalizado(int alterados) {
        return alterados == 0;
    }

    private boolean isMaisQueUmItemAlterado(int alterados) {
        return alterados > 1;
    }
    
    public void gravarItemPontuacao(PdvVendaItemVO item) throws Exception {
        try (Statement stm = Conexao.createStatement()) {         
            
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("pdv");
            sql.setTableName("vendaitem");
            sql.put("id_venda", item.getVenda().getId());
            sql.put("id_produto", item.getId_produto());
            sql.put("quantidade", item.getQuantidade());
            sql.put("precoVenda", item.getPrecoVenda());
            sql.put("valortotal", item.getValorTotal());
            sql.put("id_aliquota", item.getId_aliquota());
            sql.put("cancelado", item.isCancelado());
            sql.put("valorCancelado", item.getValorCancelado());
            if ( item.getTipoCancelamento() != null ) {
                sql.put("id_tipocancelamento", item.getTipoCancelamento().getId());
            }
            
            sql.put("contadorDoc", item.getContadorDoc());
            sql.put("valorDesconto", item.getValorDesconto());
            sql.put("valorAcrescimo", item.getValorAcrescimo());
            sql.put("valorDescontoCupom", item.getValorDescontoCupom());
            sql.put("valorAcrescimoCupom", item.getValorAcrescimoCupom());
            sql.put("regraCalculo", item.getRegraCalculo());
            sql.put("codigoBarras", item.getCodigoBarras());
            sql.put("unidadeMedida", item.getUnidadeMedida());
            sql.put("totalizadorParcial", item.getTotalizadorParcial());
            sql.put("sequencia", item.getSequencia());
            
            if (versao.igualOuMaiorQue(3, 21, 10)) {
                if (item.getData() != null) {
                    sql.put("data", item.getData());
                }
            }
            
            sql.getReturning().add("id");
            
            String strSQL = sql.getInsert();
            
            LOG.finer(
                String.format(
                        "Venda: %d Item: (%d) %d - %.2f X %.2f",
                        item.getVenda().getId(),
                        item.getId_produto(),
                        item.getCodigoBarras(),
                        item.getQuantidade(),
                        item.getPrecoVenda()
                )
            );
            LOG.finest(strSQL);
            
            try (ResultSet rst = stm.executeQuery(
                    strSQL
            )) {
                if (rst.next()) {
                    item.setId(rst.getLong("id"));
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, 
                        String.format(
                                "Erro ao processar o produto\n"
                                + "Venda: %d Item: (%d) %d - %.2f X %.2f\n"
                                + "SQL: " + strSQL,
                                item.getVenda().getId(),
                                item.getId_produto(),
                                item.getCodigoBarras(),
                                item.getQuantidade(),
                                item.getPrecoVenda()
                        )
                , e);
                throw e;
            }
        }
    }

}
