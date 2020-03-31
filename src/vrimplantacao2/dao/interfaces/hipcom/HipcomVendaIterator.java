package vrimplantacao2.dao.interfaces.hipcom;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaIMP;

/**
 *
 * @author Leandro
 */
public class HipcomVendaIterator extends MultiStatementIterator<VendaIMP> {
    
    private static final Logger LOG = Logger.getLogger(HipcomVendaIterator.class.getName());

     public HipcomVendaIterator(String idLojas, Date dataInicial, Date dataTermino) throws Exception {
        super(
            new CustomNextBuilder(),
            new StatementBuilder() {
                @Override
                public Statement makeStatement() throws Exception {
                    return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                }
            }
        );
        
        for (String statement : SQLUtils.quebrarSqlEmMeses(getFullSQL(idLojas), dataInicial, dataTermino, new SimpleDateFormat("yyyy-MM-dd"))) {
            this.addStatement(statement);
        }
    }

    private String getFullSQL(String idLojaCliente) throws Exception {

        return 
            "select\n" +
            "	v.id,\n" +
            "	v.numero,\n" +
            "	v.caixa,\n" +
            "	v.data_cupom,\n" +
            "	concat(c.cliloja,'-',c.clicod) id_cliente,\n" +
            "	cast(v.data_cupom as time) horainicio,\n" +
            "	cast(v.data_cupom as time) horafim,\n" +
            "	case when v.cancelado = 'S' then 1 else 0 end cancelado,\n" +
            "	v.cpf_cnpj,\n" +
            "	v.serie numeroserie,\n" +
            "	v.modelo_documento_fiscal,\n" +
            "	c.clinome nomecliente\n" +
            "from\n" +
            "	hip_cupom v\n" +
            "	left join clicli c on\n" +
            "		c.clicpfcnpj = nullif(v.cpf_cnpj,1) \n" +
            "where	\n" +
            "	v.loja = " + idLojaCliente + " and\n" +
            "	cast(v.data_cupom as date) >= '{DATA_INICIO}' and\n" +
            "	cast(v.data_cupom as date) <= '{DATA_TERMINO}'\n" +
            "order by\n" +
            "	v.id";

    }
    
    private static class CustomNextBuilder implements NextBuilder<VendaIMP> {
        @Override
        public VendaIMP makeNext(ResultSet rs) throws Exception {
            VendaIMP next = new VendaIMP();
            
            next.setId(rs.getString("id"));
            next.setNumeroCupom(Utils.stringToInt(rs.getString("numero")));
            next.setEcf(Utils.stringToInt(rs.getString("caixa")));
            next.setData(rs.getDate("data_cupom"));
            next.setIdClientePreferencial(rs.getString("id_cliente"));
            next.setHoraInicio(rs.getTime("horainicio"));
            next.setHoraTermino(rs.getTime("horafim"));
            next.setCancelado(rs.getBoolean("cancelado"));
            next.setCpf(rs.getString("cpf_cnpj"));
            next.setNumeroSerie(rs.getString("numeroserie"));
            next.setNomeCliente(rs.getString("nomecliente"));
            
            return next;
        }
    }
    
}
