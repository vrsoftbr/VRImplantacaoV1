package vrimplantacao2.dao.interfaces.gestora;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class GestoraVendaItemIterator extends MultiStatementIterator<VendaItemIMP> {

    public GestoraVendaItemIterator(Date dataInicial, Date dataTermino) {
        super(
                new GestoraVendaItemNextBuild(),
                new GestoraVendaItemStatementBuilder()
        );
        if (dataInicial == null) {
            throw new NullPointerException("Informe a data inicial");
        }
        if (dataTermino == null) {
            throw new NullPointerException("Informe a data final");
        }
        //SP_12_2020     
        for (SQLUtils.Intervalo intervalo : SQLUtils.intervalosMensais(dataInicial, dataTermino)) {
            this.addStatement(getFullSQL(intervalo));
        }
    }

    private static final SimpleDateFormat TABLE_NAME_DATE = new SimpleDateFormat("MM_yyyy");

    private String getNomeTabela(Date dataInicial) {
        return String.format("SP_%s", TABLE_NAME_DATE.format(dataInicial));
    }

    private String getNomeTabelaV(Date dataInicial) {
        return String.format("CP_%s", TABLE_NAME_DATE.format(dataInicial));
    }

    private String getFullSQL(SQLUtils.Intervalo intervalo) {
        return "select\n"
                + "	cp.com_registro id,\n"
                + "	sai_registro nritem,\n"
                + "	cp.COM_NCUPOM numerocupom,\n"
                + "	cli_codigo idclientepreferencial,\n"
                + "	cp.com_status status,\n"
                + "case maq_nome\n"
                + "	when 'caixa-01' then 1\n"
                + "	when 'caixa-02' then 2\n"
                + "	when 'caixa-03' then 3\n"
                + "	when 'adm'      then 4\n"
                + "	else 5\n"
                + "end ecf,\n"
                + "     CAST (CONVERT(NVARCHAR,CP.COM_DATA,23) AS DATE) EMISSAO,\n"
                + "	CAST (CONVERT(NVARCHAR,SP.DATA_PROCESSO,23) AS DATE) DATA,\n"
                + "	sp.pro_codigo idproduto,\n"
                + "	sp.pro_descricao descricao,\n"
                + "	sp.pro_unidade unidade,\n"
                + "	sai_qtde quantidade,\n"
                + "	sp.pro_venda valor,\n"
                + "	round(sai_total, 2) total,\n"
                + "	sp.pro_desconto desconto,\n"
                + "	p.pro_barra codigobarras,\n"
                + "	case when sp.data_processo_cancel is null then 0 else 1 end cancelado,\n"
                + "     concat(sp.tributacao, '-', sp.tri_aliquota) as tributacaoicms\n"
                + "from\n"
                + "     " + getNomeTabela(intervalo.dataInicial) + " as SP\n"
                + "	left join " + getNomeTabelaV(intervalo.dataInicial) + " cp on cp.com_registro = sp.com_registro\n"
                + "     left join produtos p on p.pro_codigo = sp.pro_codigo\n"
                + "where\n"
                + "     CAST (CONVERT(NVARCHAR,SP.DATA_PROCESSO,23) AS DATE) between '" + DATE_FORMAT.format(intervalo.dataInicial) + "' and '" + DATE_FORMAT.format(intervalo.dataFinal) + "'\n";
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatID(int id, int numerocupom, int nritem, Date data) {
        return String.format("%d-%d-%d-%s", id, numerocupom, nritem, DATE_FORMAT.format(data));
    }

    private static class GestoraVendaItemNextBuild implements NextBuilder<VendaItemIMP> {

        @Override
        public VendaItemIMP makeNext(ResultSet rs) throws Exception {
            VendaItemIMP v = new VendaItemIMP();

            v.setVenda(rs.getString("id"));
            v.setId(formatID(
                    rs.getInt("id"),
                    rs.getInt("numerocupom"),
                    rs.getInt("nritem"),
                    rs.getDate("data")
            ));
            v.setSequencia(rs.getInt("nritem"));
            v.setProduto(rs.getString("idproduto"));
            v.setDescricaoReduzida(rs.getString("descricao"));
            v.setQuantidade(rs.getDouble("quantidade"));
            v.setTotalBruto(rs.getDouble("total"));
            v.setCancelado(rs.getBoolean("cancelado"));
            v.setValorDesconto(rs.getDouble("Desconto"));
            v.setCodigoBarras(rs.getString("codigobarras"));
            v.setUnidadeMedida(rs.getString("unidade"));
            
            String trib = Utils.acertarTexto(rs.getString("tributacaoicms"));
            if (trib == null || "".equals(trib)) {
                trib = Utils.acertarTexto(rs.getString("tributacaoicms"));
            }

            obterAliquota(v, trib);
            

            return v;
        }
        
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             0700   7.00    ALIQUOTA 07%
             1200   12.00   ALIQUOTA 12%
             1800   18.00   ALIQUOTA 18%
             2500   25.00   ALIQUOTA 25%
             1100   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "R26-18.00":
                    cst = 0;
                    aliq = 18;
                    break;
                case "T01-18.00":
                    cst = 0;
                    aliq = 18;
                    break;
                case "III-0.00":
                    cst = 40;
                    aliq = 0;
                    break;
                case "NNN-0.00":
                    cst = 41;
                    aliq = 0;
                    break;
                case "T02-18.00":
                    cst = 0;
                    aliq = 18;
                    break;
                case "T06-11.00":
                    cst = 0;
                    aliq = 11;
                    break;
                case "FFF-0.00":
                    cst = 60;
                    aliq = 0;
                    break;
                case "T04-25.00":
                    cst = 0;
                    aliq = 25;
                    break;
                case "T03-18.00":
                    cst = 0;
                    aliq = 18;
                    break;
                case "T01-7.00":
                    cst = 0;
                    aliq = 7;
                    break;
                case "T02-12.00":
                    cst = 0;
                    aliq = 12;
                    break;
                case "T47-4.70":
                    cst = 0;
                    aliq = 4.7;
                    break;
                case "T07-4.50":
                    cst = 0;
                    aliq = 4.5;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }
        
    }

    
    private static class GestoraVendaItemStatementBuilder implements StatementBuilder {

        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoSqlServer.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
    }

}
