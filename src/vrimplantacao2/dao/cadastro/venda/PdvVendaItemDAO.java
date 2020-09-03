package vrimplantacao2.dao.cadastro.venda;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.parametro.Versao;
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
            if (Versao.menorQue(3, 18, 3)) {
                if (item.getTipoDesconto() != null) {
                    sql.put("tipoDesconto", item.getTipoDesconto().getId());
                }
            }
            
            System.out.println("Custo Com Imposto Gravar " + item.getCustoComImposto());
            System.out.println("Custo Sem Imposto Gravar " + item.getCustoSemImposto());
            
            sql.put("custoComImposto", item.getCustoComImposto());
            sql.put("custoSemImposto", item.getCustoSemImposto());
            sql.put("custoMedioComimposto", item.getCustoMedioComimposto(), 0);
            sql.put("custoMedioSemImposto", item.getCustoMedioSemImposto(), 0);
            sql.put("aplicaDescontoPromocao", item.isAplicaDescontoPromocao());
            sql.put("id_tipoOferta", item.getId_tipoOferta(), -1);
            sql.put("atacado", item.isAtacado());
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
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	ant.impid,\n" +
                        "	ant.codigoatual\n" +
                        "from \n" +
                        "	implantacao.codant_produto ant\n" +
                        "	join produto p on ant.codigoatual = p.id\n" +
                        "where\n" +
                        "	ant.impsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                        "	ant.imploja = " + SQLUtils.stringSQL(loja)
                )) {
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

}
