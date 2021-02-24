package vrimplantacao2.dao.interfaces.gestora;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaIMP;

/**
 *
 * @author Alan
 */
public class GestoraVendaIterator extends MultiStatementIterator<VendaIMP> {

    public GestoraVendaIterator(Date dataInicial, Date dataTermino) {
        super(
                new GestoraNextBuilder(),
                new GestoraStatementBuilder()
        );
        if (dataInicial == null) {
            throw new NullPointerException("Informe a data inicial");
        }
        if (dataTermino == null) {
            throw new NullPointerException("Informe a data final");
        }
        //CP_12_2020
        for (SQLUtils.Intervalo intervalo : SQLUtils.intervalosMensais(dataInicial, dataTermino)) {
            this.addStatement(getFullSQL(intervalo));
        }
    }

    private static final SimpleDateFormat TABLE_NAME_DATE = new SimpleDateFormat("MM_yyyy");
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat FORMATA_HORA = new SimpleDateFormat("HH:mm:ss");

    private String getNomeTabela(Date dataInicial) {
        return String.format("CP_%s", TABLE_NAME_DATE.format(dataInicial));
    }

    private String getFullSQL(SQLUtils.Intervalo intervalo) {
        return "select\n"
                + "	com_registro id,\n"
                + "	lote_codigo numerocupom,\n"
                + "	case maq_nome\n"
                + "		when 'caixa-01' then 1\n"
                + "		when 'caixa-02' then 2\n"
                + "		when 'caixa-03' then 3\n"
                + "		when 'adm' then 4\n"
                + "		else 5\n"
                + "	end ecf,\n"
                + "	cast (convert(nvarchar, cp.com_data, 23) as date) as datavenda,\n"
                + "	cast (com_hora as time) horainicio,\n"
                + "	cast (com_hora as time) horatermino,\n"
                + "	cli_codigo idclientepreferencial,\n"
                + "	cli_cpfcgc cpf,\n"
                + "	cli_nome nomecliente,\n"
                + "	rtrim(cli_endereco)+' '+rtrim(cli_endnro)+' '+rtrim(cli_bairro)+' '+rtrim(cli_cidade)+' '+rtrim(cli_estado) as endereco,\n"
                + "	com_total subtotalimpressora,\n"
                + "	com_desconto desconto,\n"
                + "	com_nserie numeroserie,\n"
                + "	com_chave chave,\n"
                + "	ecf_fab modelo,\n"
                + "	case\n"
                + "		when cast(convert(nvarchar, data_processo_cancel, 23) as date) is null then 0\n"
                + "		else 1\n"
                + "	end cancelado\n"
                + "from\n"
                + "	" + getNomeTabela(intervalo.dataInicial) + " as cp \n"
                + "where com_total > 0 and com_ncupom != 0"; /* and com_tipo_emissao like 'N'";*/

    }

    public static String formatID(int numerocupom, int id, int ecf,  Date data) {
        return String.format("%d-%d-%d-%s", numerocupom, id, ecf, format.format(data));
    }

    private static class GestoraNextBuilder implements NextBuilder<VendaIMP> {

        @Override
        public VendaIMP makeNext(ResultSet rs) throws Exception {
            VendaIMP v = new VendaIMP();

            v.setId(formatID(
                    rs.getInt("numerocupom"),
                    rs.getInt("id"),
                    rs.getInt("ecf"),
                    rs.getDate("datavenda")
            ));
            v.setNumeroCupom(rs.getInt("numerocupom"));
            v.setEcf(rs.getInt("ecf"));
            v.setData(rs.getDate("datavenda"));
            v.setHoraInicio(FORMATA_HORA.parse(rs.getString("horainicio")));
            v.setHoraTermino(FORMATA_HORA.parse(rs.getString("horatermino")));
            v.setIdClientePreferencial(rs.getString("idclientepreferencial"));
            v.setCpf(rs.getString("cpf"));
            v.setNomeCliente(rs.getString("nomecliente"));
            v.setEnderecoCliente(rs.getString("endereco"));
            v.setSubTotalImpressora(rs.getDouble("subtotalimpressora"));
            v.setValorDesconto(rs.getDouble("desconto"));
            v.setNumeroSerie(rs.getString("numeroserie"));
            v.setChaveCfe(rs.getString("chave"));
            v.setModeloImpressora(rs.getString("modelo"));
            v.setCancelado(rs.getBoolean("cancelado"));

            return v;
        }
    }

    private static class GestoraStatementBuilder implements StatementBuilder {

        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoSqlServer.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
    }
}
