package vrimplantacao2.dao.interfaces.gestora;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaIMP;

/**
 *
 * @author leandro
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

    private static final SimpleDateFormat TABLE_NAME_DATE = new SimpleDateFormat("MM-yyyy");

    private String getNomeTabela(Date dataInicial) {
        return String.format("CP_%s", TABLE_NAME_DATE.format(dataInicial));
    }

    private String getFullSQL(SQLUtils.Intervalo intervalo) {
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT\n"
                    + "	COM_REGISTRO ID,\n"
                    + "	COM_NCUPOM NUMEROCUPOM,\n"
                    + " CASE maq_nome\n"
                    + "	  WHEN 'CAIXA-01' THEN 1\n"
                    + "	  WHEN 'CAIXA-02' THEN 2\n"
                    + "	  WHEN 'CAIXA-03' THEN 3\n"
                    + "	  WHEN 'ADM'      THEN 4\n"
                    + "	  ELSE 5\n"
                    + "END ECF,\n"
                    + "	COM_DATA DATA,\n"
                    + "	COM_HORA HORAINICIO,\n"
                    + "	COM_HORA HORATERMINO,\n"
                    + "	CLI_CODIGO IDCLIENTEPREFERENCIAL,\n"
                    + "	CLI_CPFCGC CPF,\n"
                    + "	CLI_NOME NOMECLIENTE,\n"
                    + "	CLI_ENDERECO+' '+CLI_ENDNRO+' '+CLI_BAIRRO+' '+CLI_CIDADE+' '+CLI_ESTADO as ENDERECO,\n"
                    + "	COM_TOTAL SUBTOTALIMPRESSORA,\n"
                    + "	COM_DESCONTO DESCONTO,\n"
                    + "	COM_NSERIE NUMEROSERIE,\n"
                    + "	COM_CHAVE CHAVE,\n"
                    + "	ECF_FAB MODELO,\n"
                    + "	CASE WHEN DATA_PROCESSO_CANCEL IS NULL THEN 0 ELSE 1 END CANCELADO\n"
                    + "FROM\n"
                    + "	" + getNomeTabela(intervalo.dataInicial) + " AS CP"
            )) {
            }
        } catch (Exception ex) {
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return null;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatID(int numerocupom, int numeroserie, Date data) {
        return String.format("%d-%d-%s", numerocupom, numeroserie, DATE_FORMAT.format(data));
    }

    private static class GestoraNextBuilder implements NextBuilder<VendaIMP> {

        @Override
        public VendaIMP makeNext(ResultSet rs) throws Exception {
            VendaIMP v = new VendaIMP();

            v.setId(formatID(
                    rs.getInt("id"),
                    rs.getInt("numeroserie"),
                    rs.getDate("data")
            ));
            v.setNumeroCupom(rs.getInt("numerocupom"));
            v.setEcf(rs.getInt("ecf"));
            v.setData(rs.getDate("data"));
            v.setHoraInicio(rs.getDate("horainicio"));
            v.setHoraTermino(rs.getDate("horatermino"));
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
            return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
    }
}
