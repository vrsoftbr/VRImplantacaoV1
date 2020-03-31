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
            new CustomNextBuilder(),
            new StatementBuilder() {
                @Override
                public Statement makeStatement() throws Exception {
                    return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                }
            }
        );
        
        for (String statement : SQLUtils.quebrarSqlEmMeses(getFullSQL(idLoja, vendaUtilizaDigito), dataInicial, dataTermino, new SimpleDateFormat("yyyy-MM-dd"))) {
            this.addStatement(statement);
        }
        
    }

    private String getFullSQL(String idLojaCliente, boolean vendaUtilizaDigito) throws Exception {

        return
                "SELECT\n" +
                "	i.id,\n" +
                "	i.id_cupom,\n" +
                "	i.sequencia,\n" +
                "	prod.id_produto,\n" +
                "	p.prodescres descricao,\n" +
                "	i.quantidade,\n" +
                "	i.valor_total,\n" +
                "	case when i.cancelado = 'S' then 1 else 0 end cancelado,\n" +
                "	i.valor_desconto desconto,\n" +
                "	0 as acrescimo,\n" +
                "	i.eanfinal codigobarras,\n" +
                "	substring(p.proembu, 1,2) unidade,\n" +
                "	case substring(i.legenda,1,1)\n" +
                "		when 'T' then 0\n" +
                "		when '0' then 0\n" +
                "		when 'F' then 60\n" +
                "		when 'I' then 40\n" +
                "		else 40\n" +
                "	end as cst,\n" +
                "	case substring(i.legenda,1,1)\n" +
                "		when 'T' then i.aliquota_icms\n" +
                "		when '0' then i.aliquota_icms\n" +
                "		when 'F' then 0\n" +
                "		when 'I' then 0\n" +
                "		else 0\n" +
                "	end as aliquota  \n" +
                "FROM\n" +
                "	(\n" +
                "		select i.*, \n" +
                (
                    vendaUtilizaDigito ?
                "               case\n" +
                "			when i.codigo_plu_barras <= 999999 then left(i.codigo_plu_barras, LENGTH (i.codigo_plu_barras) - 1)\n" +
                "			else i.codigo_plu_barras\n" +
                "		end eanfinal" :
                "               i.codigo_plu_barras eanfinal"
                ) +
                "		from\n" +
                "			hip_cupom_item i\n" +
                "	) i\n" +
                "	join (\n" +
                "		select\n" +
                "			barcodplu id_produto,\n" +
                "			barcodbar ean\n" +
                "		from\n" +
                "			hipbar ean\n" +
                "		union\n" +
                "		select\n" +
                "			procodplu id_produto,\n" +
                "			procodplu ean\n" +
                "		from\n" +
                "			hippro\n" +
                "	) prod on\n" +
                "		i.eanfinal = prod.ean\n" +
                "	left join hippro p on\n" +
                "		prod.id_produto = p.procodplu\n" +
                "	join hip_cupom v on\n" +
                "		v.id = i.id_cupom \n" +
                "where\n" +
                "	v.loja = " + idLojaCliente + " and\n" +
                "	cast(v.data_cupom as date) >= '{DATA_INICIO}' and\n" +
                "	cast(v.data_cupom as date) <= '{DATA_TERMINO}'\n" +
                "order BY\n" +
                "	i.id_cupom,\n" +
                "	i.sequencia";

    }
    
    private static class CustomNextBuilder implements NextBuilder<VendaItemIMP> {
        
        @Override
        public VendaItemIMP makeNext(ResultSet rs) throws Exception {

            VendaItemIMP next = new VendaItemIMP();

            next.setId(rs.getString("id"));
            next.setVenda(rs.getString("id_cupom"));
            next.setSequencia(rs.getInt("sequencia"));
            next.setProduto(rs.getString("id_produto"));
            next.setDescricaoReduzida(rs.getString("descricao"));
            next.setQuantidade(rs.getDouble("quantidade"));
            next.setTotalBruto(rs.getDouble("valor_total"));
            next.setCancelado(rs.getBoolean("cancelado"));
            next.setValorDesconto(rs.getDouble("desconto"));
            next.setValorAcrescimo(rs.getDouble("acrescimo"));
            next.setCodigoBarras(rs.getString("codigobarras"));
            next.setUnidadeMedida(rs.getString("unidade"));
            next.setIcmsCst(Utils.stringToInt(rs.getString("cst")));
            next.setIcmsAliq(rs.getDouble("aliquota"));

            return next;
        }
    }
    
}
