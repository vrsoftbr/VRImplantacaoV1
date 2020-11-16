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
        
        for (String statement : SQLUtils.quebrarSqlEmMeses(getFullSQL(Utils.stringToInt(idLoja)), dataInicial, dataTermino, new SimpleDateFormat("MMyy"))) {
            this.addStatement(statement);
        }
        
    }
    
    private String getNomeTabela(int idLoja) {
        return String.format("log%03dvenda{DATA_INICIO}", idLoja);
    }
    
    private String getFullSQL(int idLoja) {
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
            "			when reproccanc is null then 0\n" +
            "			else 1\n" +
            "		end\n" +
            "	) cancelado,\n" +
            "	sum(coalesce(total,0)) subtotalimpressora,\n" +
            "	sum(coalesce(desconto,0)) valorDesconto,\n" +
            "	sum(coalesce(acrescimo,0)) valorAcrescimo,\n" +
            "	min(cx.serie) serie,\n" +
            "	min(nf.chv_cfe) chave\n" +
            "from\n" +
            "	" + getNomeTabela(idLoja) + " v\n" +
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
            "	left join " + getNomeTabela(idLoja) + "nf nf on\n" +
            "		v.cupom = nf.cupom and \n" +
            "		v.caixa = nf.caixa and\n" +
            "		v.`data` = nf.`data` and\n" +
            "		cast(cx.serie as SIGNED) = cast(nf.serie as SIGNED)\n" +
            "where\n" +
            "	v.data between '{DATA_INICIO}' and '{DATA_TERMINO}'\n" +
            "group by\n" +
            "	v.cupom,\n" +
            "	v.caixa,\n" +
            "	v.data";
    }
    
    private static class LinearNextBuilder implements NextBuilder<VendaIMP> {

        @Override
        public VendaIMP makeNext(ResultSet rst) throws Exception {
            VendaIMP v = new VendaIMP();
            
            
            
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
