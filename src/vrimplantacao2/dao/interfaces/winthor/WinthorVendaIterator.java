package vrimplantacao2.dao.interfaces.winthor;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vr.core.utils.StringUtils;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaIMP;

class WinthorVendaIterator extends MultiStatementIterator<VendaIMP> {

    final SimpleDateFormat ORACLE_DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
    
    private static final Logger LOG = LoggerFactory.getLogger(WinthorVendaIterator.class);        
    
    public WinthorVendaIterator(String idLoja, Date dataInicial, Date dataTermino) {
        super(
                new WinthorVendaNextBuilder(),
                new WinthorStatementBuilder()
        );
        
        for (SQLUtils.Intervalo intervalo: SQLUtils.intervalosMensais(dataInicial, dataTermino)) {
            this.addStatement(this.buildSQL(idLoja, intervalo));
        }
    }
    
    private String buildSQL(String idLoja, SQLUtils.Intervalo intervalo) {
        StringBuilder sql = new StringBuilder()
                .append("select\n")
                .append("	v.NUMTRANSVENDA id,\n")
                .append("	v.NUMNOTA numerocupom,\n")
                .append("	v.caixa ecf,\n")
                .append("	v.DTSAIDA data,\n")
                .append("	v.HORAEMISSAO horainicio,\n")
                .append("	v.HORAEMISSAO horafim,\n")
                .append("	case\n")
                .append("		when not v.DTCANCEL is null then 1\n")
                .append("		else 0\n")
                .append("	end cancelado,\n")
                .append("	case v.CODCLI\n")
                .append("		when 1 then null\n")
                .append("		else v.CODCLI\n")
                .append("	end id_cliente,\n")
                .append("	v.CGC cpf,\n")
                .append("	v.VLSUBTOTAL subtotalimpressora,\n")
                .append("	v.VLDESCONTO valordesconto,\n")
                .append("	v.DTCANCEL datacancelamento,\n")
                .append("	v.SERIE numeroserie,\n")
                .append("	v.CLIENTE nomecliente,\n")
                .append("	v.ENDERECO,\n")
                .append("	v.CHAVESAT,\n")
                .append("	v.CHAVENFE,\n")
                .append("	v.CHAVECTE\n")
                .append("from\n")
                .append("	PCNFSAID v\n")
                .append("where\n")
                .append("	v.DTSAIDA between '")
                    .append(ORACLE_DATE_FORMATTER.format(intervalo.dataInicial))
                    .append("' and '")
                    .append(ORACLE_DATE_FORMATTER.format(intervalo.dataFinal))
                .append("' and\n")
                .append("	v.CODFILIAL = '").append(idLoja).append("' and\n")
                .append("	not v.NUMNOTA is null and\n")
                .append("	not v.NUMCAIXAFISCAL is null\n")
                .append("order by\n")
                .append("	v.NUMTRANSVENDA");        
        LOG.info("Script criado para a criação das vendas", sql);
        return sql.toString();             
    }
    
    private static class WinthorVendaNextBuilder implements NextBuilder<VendaIMP> {
        @Override
        public VendaIMP makeNext(ResultSet rs) throws Exception {
            VendaIMP imp = new VendaIMP();
            
            Date dataCadastro = null;
            Date dataCancelamento = null;
            if (rs.getString("data") != null) {
                dataCadastro = rs.getDate("data");
            }
            if (rs.getString("datacancelamento") != null) {
                dataCancelamento = rs.getDate("datacancelamento");
            }
            String idCliente = rs.getString("id_cliente");
            
            imp.setId(rs.getString("id"));
            imp.setNumeroCupom(StringUtils.toInt(rs.getString("numerocupom")));
            imp.setEcf(StringUtils.toInt(rs.getString("ecf")));
            imp.setData(dataCadastro);
            imp.setHoraInicio(rs.getDate("horainicio"));
            imp.setHoraTermino(rs.getDate("horafim"));
            imp.setCancelado(rs.getBoolean("cancelado"));
            imp.setSubTotalImpressora(rs.getDouble("subtotalimpressora"));
            imp.setValorDesconto(rs.getDouble("valordesconto"));
            if (dataCancelamento != null) {
                imp.setCancelado(true);
                if (dataCadastro != null)
                    imp.setCanceladoEmVenda(dataCancelamento.equals(dataCadastro));
            } else {
                imp.setCancelado(false);
                imp.setCanceladoEmVenda(false);
            }
            imp.setCpf(rs.getString("cpf"));
            if (idCliente != null) {                
                imp.setIdClientePreferencial(idCliente);
                imp.setEnderecoCliente(rs.getString("ENDERECO"));
            }
            imp.setNumeroSerie(rs.getString("numeroserie"));
            imp.setChaveNfCe(rs.getString("CHAVENFE"));
            
            return imp;
        }    
    }
    
    static class WinthorStatementBuilder implements StatementBuilder {
        @Override
        public Statement makeStatement() throws Exception {
            return ConexaoOracle.createStatement();
        }
    }
    
}
