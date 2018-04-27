package vrimplantacao2.dao.interfaces.hipcom;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
    
    private static final SimpleDateFormat TIMESTAMP_DATE = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    
    public static String makeId(String idLoja, Date data, String ecf, String idCaixa, String numeroCupom) {
        return idLoja + "-" + TIMESTAMP_DATE.format(data) + "-" + ecf + "-" + idCaixa + "-" + numeroCupom;
    }

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
    
    private String getSQL(String idLojaCliente, String tableName) {  

        return 
            "select\n" +
            "	v.loja id_loja,\n" +
            "	v.codigo_caixa id_caixa,\n" +
            "	v.numero_cupom_fiscal numerocupom,\n" +
            "	v.codigo_terminal ecf,\n" +
            "	v.data,\n" +
            "	min(v.hora) horainicio,\n" +
            "	max(v.hora) horatermino,	\n" +
            "	min(v.cupom_cancelado) cancelado,\n" +
            "	sum(v.valor_total) subtotalimpressora\n" +
            "from\n" +
            "	" + tableName + " v\n" +
            "where\n" +
            "	v.loja = " + idLojaCliente + " and\n" +
            "	v.data >= '{DATA_INICIO}' and\n" +
            "	v.data <= '{DATA_TERMINO}'\n" +
            "group by\n" +
            "	id_loja,\n" +
            "	id_caixa,\n" +
            "	numerocupom,\n" +
            "	ecf,\n" +
            "	data\n";
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
    
    private static class CustomNextBuilder implements NextBuilder<VendaIMP> {
        @Override
        public VendaIMP makeNext(ResultSet rst) throws Exception {
            VendaIMP next = new VendaIMP();
            String id = makeId(rst.getString("id_loja"), rst.getDate("data"), rst.getString("ecf"), rst.getString("id_caixa"), rst.getString("numerocupom"));
            next.setId(id);
            next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
            next.setEcf(Utils.stringToInt(rst.getString("ecf")));
            next.setData(rst.getDate("data"));
            String horaInicio = TIMESTAMP_DATE.format(rst.getDate("data")) + " " + rst.getString("horainicio");
            String horaTermino = TIMESTAMP_DATE.format(rst.getDate("data")) + " " + rst.getString("horatermino");
            next.setHoraInicio(TIMESTAMP.parse(horaInicio));
            next.setHoraTermino(TIMESTAMP.parse(horaTermino));
            next.setCancelado("S".equals(rst.getString("cancelado")));
            next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
            return next;
        }
    }
    
}
