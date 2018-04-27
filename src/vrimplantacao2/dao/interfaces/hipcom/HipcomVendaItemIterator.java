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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Leandro
 */
public class HipcomVendaItemIterator extends MultiStatementIterator<VendaItemIMP> {
    
    private static final Logger LOG = Logger.getLogger(HipcomVendaItemIterator.class.getName());
    
    public HipcomVendaItemIterator(boolean vendaUtilizaDigito, String idLoja, Date dataInicial, Date dataTermino) throws Exception {
        super(
            new CustomNextBuilder(vendaUtilizaDigito),
            new StatementBuilder() {
                @Override
                public Statement makeStatement() throws Exception {
                    return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                }
            }
        );
        
        for (String statement : SQLUtils.quebrarSqlEmMeses(getFullSQL(idLoja), dataInicial, dataTermino, new SimpleDateFormat("yyyy-MM-dd"))) {
            this.addStatement(statement);
        }
        
    }
    
    private String getSQL(String idLojaCliente, String tableName) {  

        return 
                "select\n" +
                "	v.loja id_loja,\n" +
                "	v.data,\n" +
                "	v.codigo_terminal ecf,\n" +
                "	v.codigo_caixa id_caixa,\n" +
                "	v.numero_cupom_fiscal numerocupom,\n" +
                "	v.sequencia,\n" +
                "	v.codigo_plu_bar ean,\n" +
                "	sum(v.quantidade_itens) quantidade,\n" +
                "	sum(v.valor_total) total_bruto,\n" +
                "	min(v.item_cancelado) cancelado,\n" +
                "	min(v.legenda) icms,\n" +
                "	min(case substring(v.legenda,1,1)\n" +
                "	when 'T' then 0\n" +
                "	when '0' then 0\n" +
                "	when 'F' then 60\n" +
                "	when 'I' then 40\n" +
                "	else 40\n" +
                "	end) as cst,\n" +
                "	min(case substring(v.legenda,1,1)\n" +
                "	when 'T' then v.aliquota\n" +
                "	when '0' then v.aliquota\n" +
                "	when 'F' then 0\n" +
                "	when 'I' then 0\n" +
                "	else 0\n" +
                "	end) as aliquota\n" +
                "from\n" +
                "	" + tableName + " v\n" +
                "where\n" +
                "	v.loja = " + idLojaCliente + " and\n" +
                "	v.data >= '{DATA_INICIO}' and\n" +
                "	v.data <= '{DATA_TERMINO}'\n" +
                "group by\n" +
                "	v.loja,\n" +
                "	v.data,\n" +
                "	v.codigo_terminal,\n" +
                "	v.codigo_caixa,\n" +
                "	v.numero_cupom_fiscal,\n" +
                "	v.sequencia,\n" +
                "	v.codigo_plu_bar\n";        
    }

    private String getFullSQL(String idLojaCliente) throws Exception {

        StringBuilder str = new StringBuilder();

        str.append(getSQL(idLojaCliente, "hip_cupom_ultimos_meses2"));
        str.append("union\n");
        str.append(getSQL(idLojaCliente, "hip_cupom_item_semcript_2017"));
        str.append("union\n");
        str.append(getSQL(idLojaCliente, "hip_cupom_item_semcript_2016"));
        str.append("union\n");
        str.append(getSQL(idLojaCliente, "hip_cupom_item_semcript_2015"));

        return str.toString();

    }
    
    private static class CustomNextBuilder implements NextBuilder<VendaItemIMP> {

        private final Map<String, SmProduto> produtos = new HashMap<>(); 
        private boolean vendaUtilizaDigito;

        public CustomNextBuilder(boolean vendaUtilizaDigito) throws Exception {
            this.vendaUtilizaDigito = vendaUtilizaDigito;
            try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select distinct\n" +
                        "	coalesce(ean.barcodbar, p.procodplu) ean,\n" +
                        "	p.procodplu id,\n" +
                        "	coalesce(nullif(trim(p.prodescres),''),p.prodescr) descricao,\n" +
                        "	substring(p.proembu, 1,2) embalagem\n" +
                        "from\n" +
                        "	hippro p\n" +
                        "	left join hipbar ean on\n" +
                        "		ean.barcodplu = p.procodplu"
                )) {
                    while (rs.next()) {
                        SmProduto smProduto = new SmProduto(
                                rs.getString("id"),
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
        public VendaItemIMP makeNext(ResultSet rst) throws Exception {
            
            String vendaId = HipcomVendaIterator.makeId(rst.getString("id_loja"), rst.getDate("data"), rst.getString("ecf"), rst.getString("id_caixa"), rst.getString("numerocupom"));
            String idVendaItem = vendaId + "-" + rst.getString("sequencia") + "-" + rst.getString("ean");

            String ean = rst.getString("ean");

            if (ean == null) ean = "";

            if (ean.length() < 7 && ean.length() > 1) {
                String old = ean;
                if (vendaUtilizaDigito) {
                    ean = ean.substring(0, ean.length() - 1);
                } else {
                    ean = ean.substring(0, ean.length());
                }
                LOG.finest("EAN de balanca anterior: " + old + " atual: " + ean);
            }

            SmProduto prod = produtos.get(ean);

            VendaItemIMP next = new VendaItemIMP();

            next.setId(idVendaItem);                   

            next.setVenda(vendaId);
            next.setSequencia(rst.getInt("sequencia"));
            if (prod != null) {
                next.setProduto(prod.id);
                next.setDescricaoReduzida(prod.descricao);
                next.setUnidadeMedida(prod.embalagem);
                next.setCodigoBarras(prod.ean);
            } else {
                next.setProduto("");
                next.setDescricaoReduzida("SEM DESCRICAO");
                next.setUnidadeMedida("UN");
                next.setCodigoBarras(ean);
            }
            next.setQuantidade(rst.getDouble("quantidade"));
            next.setTotalBruto(rst.getDouble("total_bruto"));
            next.setValorDesconto(0);
            next.setValorAcrescimo(0);
            next.setCancelado("S".equals(rst.getString("cancelado")));
            next.setIcmsCst(Utils.stringToInt(rst.getString("cst")));
            next.setIcmsAliq(rst.getDouble("aliquota"));

            return next;
        }
    }
    
    private static class SmProduto {
        public String id;
        public String ean;
        public String descricao;
        public String embalagem;

        public SmProduto(String id, String ean, String descricao, String embalagem) {
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
            final SmProduto other = (SmProduto) obj;
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
