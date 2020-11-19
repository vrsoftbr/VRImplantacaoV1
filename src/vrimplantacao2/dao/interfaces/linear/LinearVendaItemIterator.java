package vrimplantacao2.dao.interfaces.linear;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author leandro
 */
public class LinearVendaItemIterator extends MultiStatementIterator<VendaItemIMP> {

    public LinearVendaItemIterator(String idLoja, Date dataInicial, Date dataTermino) {
        super(
                new LinearVendaItemNextBuild(),
                new LinearVendaItemStatementBuilder()
        );
        if (dataInicial == null) throw new NullPointerException("Informe a data inicial");
        if (dataTermino == null) throw new NullPointerException("Informe a data final");
        //log001venda0820        
        for (SQLUtils.Intervalo intervalo: SQLUtils.intervalosMensais(dataInicial, dataTermino)) {
            this.addStatement(getFullSQL(Utils.stringToInt(idLoja), intervalo));
        }
    }
    
    private static final SimpleDateFormat TABLE_NAME_DATE = new SimpleDateFormat("MMyy");
    private String getNomeTabela(int idLoja, Date dataInicial) {        
        return String.format("log%03dvenda%s", idLoja, TABLE_NAME_DATE.format(dataInicial));
    }
    private String getFullSQL(int idLoja, SQLUtils.Intervalo intervalo) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return
            "select\n" +
            "	v.cupom,\n" +
            "	v.caixa,\n" +
            "	v.data,\n" +
            "	v.item sequencia,\n" +
            "	v.codprod id_produto,\n" +
            "	p.es1_compl descricaoreduzida,\n" +
            "	v.quant qtd,\n" +
            "	v.total total,\n" +
            "	case\n" +
            "		when v.datahoracancelamentoitem is null and v.reproccanc = 0 then 0\n" +
            "		else 1\n" +
            "	end cancelado,\n" +
            "	v.desconto valorDesconto,\n" +
            "	v.acrescimo valorAcrescimo,\n" +
            "	v.codbusca codigobarras,\n" +
            "	pc.Es1_UM unidade,\n" +
            "	pc.ES1_TRIBUTACAO id_tributo\n" +
            "from\n" +
            "	" + getNomeTabela(idLoja, intervalo.dataInicial) + " v\n" +
            "	join es1p p on\n" +
            "		v.codprod = p.es1_cod\n" +
            "	JOIN es1 pc on\n" +
            "		p.es1_cod = pc.ES1_COD\n" +
            "where\n" +
            "	v.data between '" + format.format(intervalo.dataInicial) + "' and '" + format.format(intervalo.dataFinal) + "'\n" +
            "	and pc.es1_empresa = " + idLoja + "\n" +
            "order by\n" +
            "	v.cupom,\n" +
            "	v.caixa,\n" +
            "	v.data,\n" +
            "	v.item";
    }
    
    public static String formatID(int cupom, int caixa, Date data, int numeroProduto) {
        return String.format("%s-%d", LinearVendaIterator.formatID(cupom, caixa, data), numeroProduto);
    }
    
    private static class LinearVendaItemNextBuild implements NextBuilder<VendaItemIMP> {
        
        @Override
        public VendaItemIMP makeNext(ResultSet rs) throws Exception {
            VendaItemIMP v = new VendaItemIMP();
            
            v.setId(formatID(rs.getInt("cupom"), rs.getInt("caixa"), rs.getDate("data"), rs.getInt("sequencia")));
            v.setSequencia(rs.getInt("sequencia"));
            v.setVenda(LinearVendaIterator.formatID(rs.getInt("cupom"), rs.getInt("caixa"), rs.getDate("data")));
            v.setProduto(rs.getString("id_produto"));
            v.setDescricaoReduzida(rs.getString("descricaoreduzida"));
            v.setQuantidade(rs.getDouble("qtd"));
            v.setTotalBruto(rs.getDouble("total"));
            v.setCancelado(rs.getBoolean("cancelado"));
            v.setValorDesconto(rs.getDouble("valorDesconto"));
            v.setValorAcrescimo(rs.getDouble("valorAcrescimo"));
            v.setCodigoBarras(rs.getString("codigobarras"));
            v.setUnidadeMedida(rs.getString("unidade"));
            v.setIcmsAliquotaId(rs.getString("id_tributo"));
            
            return v;
        }        
    }
    
    private static class LinearVendaItemStatementBuilder implements StatementBuilder {
        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }        
    }
    
}
