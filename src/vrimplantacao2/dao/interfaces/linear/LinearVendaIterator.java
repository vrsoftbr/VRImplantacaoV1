package vrimplantacao2.dao.interfaces.linear;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaIMP;

/**
 *
 * @author leandro
 */
public class LinearVendaIterator extends MultiStatementIterator<VendaIMP> {

    public LinearVendaIterator(String idLoja, Date dataInicial, Date dataTermino) {        
        super(
                new LinearNextBuilder(),
                new LinearStatementBuilder()
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
        boolean nfExiste = false;
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select * from information_schema.TABLES t where TABLE_NAME = '" + getNomeTabela(idLoja, intervalo.dataInicial) + "nf'"
            )) {
                nfExiste = rs.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (nfExiste) {
            return
                "select\n" +
                "	v.cupom,\n" +
                "	v.caixa,\n" +
                "	v.data,\n" +
                "	max(codcli) id_clientepreferencial,\n" +
                "	min(hora) horainicio,\n" +
                "	max(hora) horatermino,\n" +
                "	min(\n" +
                "		case\n" +
                "			when v.datahoracancelamentoitem is null then 0\n" +
                "			else 1\n" +
                "		end\n" +
                "	) cancelado,\n" +
                "	sum(coalesce(total,0)) subtotalimpressora,\n" +
                "	sum(coalesce(desconto,0)) valorDesconto,\n" +
                "	sum(coalesce(acrescimo,0)) valorAcrescimo,\n" +
                "	min(cx.serie) serie,\n" +
                "	min(nf.chv_cfe) chave\n" +
                "from\n" +
                "	" + getNomeTabela(idLoja, intervalo.dataInicial) + " v\n" +
                "	left join (\n" +
                "		select\n" +
                "			distinct\n" +
                "			caixa,\n" +
                "			serie\n" +
                "		from\n" +
                "			cadecf_faixa cf\n" +
                "		where\n" +
                "			cf.filial = " + idLoja + "\n" +
                "	) cx on\n" +
                "		v.caixa = cx.caixa\n" +
                "	left join " + getNomeTabela(idLoja, intervalo.dataInicial) + "nf nf on\n" +
                "		v.cupom = nf.cupom and \n" +
                "		v.caixa = nf.caixa and\n" +
                "		v.`data` = nf.`data` and\n" +
                "		cast(cx.serie as SIGNED) = cast(nf.serie as SIGNED)\n" +
                "where\n" +
                "	v.data between '" + format.format(intervalo.dataInicial) + "' and '" + format.format(intervalo.dataFinal) + "'\n" +
                "group by\n" +
                "	v.cupom,\n" +
                "	v.caixa,\n" +
                "	v.data";
        } else {
            return
                "select\n" +
                "	v.cupom,\n" +
                "	v.caixa,\n" +
                "	v.data,\n" +
                "	max(codcli) id_clientepreferencial,\n" +
                "	min(hora) horainicio,\n" +
                "	max(hora) horatermino,\n" +
                "	min(\n" +
                "		case\n" +
                "			when v.datahoracancelamentoitem is null then 0\n" +
                "			else 1\n" +
                "		end\n" +
                "	) cancelado,\n" +
                "	sum(coalesce(total,0)) subtotalimpressora,\n" +
                "	sum(coalesce(desconto,0)) valorDesconto,\n" +
                "	sum(coalesce(acrescimo,0)) valorAcrescimo\n" +
                "from\n" +
                "	" + getNomeTabela(idLoja, intervalo.dataInicial) + " v\n" +
                "where\n" +
                "	v.data between '" + format.format(intervalo.dataInicial) + "' and '" + format.format(intervalo.dataFinal) + "'\n" +
                "group by\n" +
                "	v.cupom,\n" +
                "	v.caixa,\n" +
                "	v.data";
        }
    }
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static String formatID(int cupom, int caixa, Date data) {
        return String.format("%d-%d-%s", cupom, caixa, DATE_FORMAT.format(data));
    }
    
    private static class LinearNextBuilder implements NextBuilder<VendaIMP> {

        @Override
        public VendaIMP makeNext(ResultSet rs) throws Exception {
            VendaIMP v = new VendaIMP();
            
            v.setId(formatID(
                    rs.getInt("cupom"),
                    rs.getInt("caixa"),
                    rs.getDate("data")
            ));
            v.setNumeroCupom(rs.getInt("cupom"));
            v.setEcf(rs.getInt("caixa"));
            v.setData(rs.getDate("data"));
            v.setIdClientePreferencial(rs.getString("id_clientepreferencial"));
            v.setHoraInicio(rs.getDate("horainicio"));
            v.setHoraTermino(rs.getDate("horatermino"));
            v.setCancelado(rs.getBoolean("cancelado"));
            v.setSubTotalImpressora(rs.getDouble("subtotalimpressora"));
            v.setValorDesconto(rs.getDouble("valorDesconto"));
            v.setValorAcrescimo(rs.getDouble("valorAcrescimo"));
            v.setNumeroSerie(rs.getString("serie"));
            v.setChaveCfe(rs.getString("chave"));          
            
            return v;
        }
        
    }

    private static class LinearStatementBuilder implements StatementBuilder {
        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
    }
    
}
