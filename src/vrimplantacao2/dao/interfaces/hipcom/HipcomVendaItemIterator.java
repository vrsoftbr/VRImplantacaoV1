package vrimplantacao2.dao.interfaces.hipcom;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Leandro
 */
public class HipcomVendaItemIterator extends MultiStatementIterator<VendaItemIMP> {
    
    private static final Logger LOG = Logger.getLogger(HipcomVendaItemIterator.class.getName());
    
    public HipcomVendaItemIterator(boolean vendaUtilizaDigito, String idLoja, Date dataInicial, Date dataTermino, Integer versaoVenda) throws Exception {
        super(
            new CustomNextBuilder(vendaUtilizaDigito, versaoVenda),
            new StatementBuilder() {
                @Override
                public Statement makeStatement() throws Exception {
                    return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                }
            }
        );
        
        for (String statement : SQLUtils.quebrarSqlEmMeses(getFullSQL(idLoja, versaoVenda), dataInicial, dataTermino, new SimpleDateFormat("yyyy-MM-dd"))) {
            this.addStatement(statement);
        }
        
    }

    private String getFullSQL(String idLojaCliente, Integer versaoVenda) throws Exception {

        if (versaoVenda == 1) {
            return "select\n"
                    + " i.loja, \n"
                    + " i.numero_cupom_fiscal, \n"
                    + " i.`data`,\n"
                    + "	i.sequencia,\n"
                    + "	i.codigo_plu_bar ean,\n"
                    + "	i.quantidade_itens as quantidade,\n"
                    + "	i.valor_unitario,\n"
                    + "	i.valor_total_item as valor_total,\n"
                    + "	i.valor_desconto_item,\n"
                    + "	i.item_cancelado \n"
                    + "from\n"
                    + "	vendasantigas i	\n"
                    + "where\n"
                    + "	i.`data` >= '{DATA_INICIO}' and\n"
                    + "	i.`data` <= '{DATA_TERMINO}' and\n"
                    + "	i.loja = " + idLojaCliente + " and\n"
                    + "       i.quantidade_itens > 0";

        } else {
            return "select\n" +
                "	i.id_cupom,\n" +
                "	i.sequencia,\n" +
                "	i.codigo_plu_bar ean,\n" +
                "	i.quantidade,\n" +
                "	i.valor_unitario,\n" +
                "	i.valor_total,\n" +
                "	i.valor_desconto_item,\n" +
                "	i.item_cancelado, \n" +
                "	i.promocao, \n" +
                "       case substring(i.legenda,1,1) \n" +
                "		when 'T' then 0 \n" + 
                "		when 'F' then 60 \n" +
                "		when 'I' then 40 \n" +
                "		when 'N' then 41 \n" +
                "	ELSE 40 END cst, \n" +    
                "       i.aliquota_icms as aliquota \n" +
                "from\n" +
                "	hipcom_cupom_tr i	\n" +
                "where\n" +
                "	i.`data` >= '{DATA_INICIO}' and\n" +
                "	i.`data` <= '{DATA_TERMINO}' and\n" +
                "	i.loja = " + idLojaCliente;
        }
    }
    
    private static class CustomNextBuilder implements NextBuilder<VendaItemIMP> {
        
        private Map<String, HipProduto> produtos;
        private boolean vendaUtilizaDigito;
        private Integer versaoVenda;

        public CustomNextBuilder(boolean vendaUtilizaDigito, Integer versaoVenda) throws Exception {
            this.vendaUtilizaDigito = vendaUtilizaDigito;
            this.versaoVenda = versaoVenda;
            try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	barcodbar ean,\n" +
                        "	barcodplu id_produto,\n" +
                        "	coalesce(nullif(trim(p.prodescres),''),p.prodescr) descricao,\n" +
                        "	substring(p.proembu, 1,2) embalagem\n" +
                        "from\n" +
                        "	hipbar ean\n" +
                        "	join hippro p on\n" +
                        "		ean.barcodplu = p.procodplu\n" +
                        "union\n" +
                        "select\n" +
                        "	procodplu ean,\n" +
                        "	procodplu id_produto,\n" +
                        "	coalesce(nullif(trim(p.prodescres),''),p.prodescr) descricao,\n" +
                        "	substring(p.proembu, 1,2) embalagem\n" +
                        "from\n" +
                        "	hippro p"
                )) {
                    produtos = new HashMap<>();
                    while (rs.next()) {
                        HipProduto smProduto = new HipProduto(
                                rs.getString("id_produto"),
                                rs.getString("ean"),
                                rs.getString("descricao"),
                                rs.getString("embalagem")
                        );
                        produtos.put(smProduto.ean, smProduto);
                    }
                }
            }
        }
        
        @Override
        public VendaItemIMP makeNext(ResultSet rs) throws Exception {

            VendaItemIMP next = new VendaItemIMP();
            
            String id, idVenda;

            if (this.versaoVenda == 1) {
                id = rs.getString("loja") + "-" + rs.getString("numero_cupom_fiscal") + rs.getString("data") + "-" + rs.getString("ean") + "-" + rs.getString("sequencia");
                idVenda = rs.getString("loja") + "-" + rs.getString("numero_cupom_fiscal") + rs.getString("data");
            } else {
                id = rs.getString("id_cupom") + "-" + rs.getString("sequencia");
                idVenda = rs.getString("id_cupom");
            }
            
            next.setId(id);
            next.setVenda(idVenda);
            
            String ean = rs.getString("ean");
            if (ean == null) ean = "";

            if (ean.length() <= 7 && ean.length() > 1) {
                String old = ean;
                if (vendaUtilizaDigito) {
                    ean = ean.substring(0, ean.length() - 1);
                } else {
                    ean = ean.substring(0, ean.length());
                }
                LOG.finest("EAN de balanca anterior: " + old + " atual: " + ean);
            }
            
            HipProduto prod = produtos.get(ean);
            if (prod == null) {
                ean = rs.getString("ean");
                if (ean == null) ean = ""; 
                prod = produtos.get(ean);
            }
            
            if (prod != null) {
                next.setProduto(prod.id);
                next.setDescricaoReduzida(prod.descricao);
                next.setUnidadeMedida(prod.embalagem);
                next.setCodigoBarras(prod.ean);
            } else {
                next.setProduto(ean);
                next.setDescricaoReduzida("SEM DESCRICAO");
                next.setUnidadeMedida("UN");
                next.setCodigoBarras(ean);
            }            
            
            if (this.versaoVenda == 1) {
                next.setSequencia(rs.getInt("sequencia"));
                next.setQuantidade(rs.getDouble("quantidade"));
                next.setTotalBruto(rs.getDouble("valor_total"));
                next.setCancelado("S".equals(rs.getString("item_cancelado")));
            } else {
                next.setSequencia(rs.getInt("sequencia"));
                next.setQuantidade(rs.getDouble("quantidade"));
                next.setTotalBruto(rs.getDouble("valor_total"));
                next.setCancelado("S".equals(rs.getString("item_cancelado")));
                next.setValorDesconto(rs.getDouble("valor_desconto_item"));
                next.setOferta("S".equals(rs.getString("promocao")));
                next.setIcmsCst(rs.getInt("cst"));
                next.setIcmsAliq(rs.getDouble("aliquota"));
            }

            return next;
        }
    }
    
    private static class HipProduto {
        public String id;
        public String ean;
        public String descricao;
        public String embalagem;

        public HipProduto(String id, String ean, String descricao, String embalagem) {
            this.id = id;
            this.ean = ean;
            this.descricao = descricao;
            this.embalagem = embalagem;
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + Objects.hashCode(this.id);
            hash = 31 * hash + Objects.hashCode(this.ean);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final HipProduto other = (HipProduto) obj;
            if (!Objects.equals(this.id, other.id)) {
                return false;
            }
            return Objects.equals(this.ean, other.ean);
        }

        @Override
        public String toString() {
            return "SmProduto{" + "id=" + id + ", ean=" + ean + ", descricao=" + descricao + '}';
        }
    }
}
