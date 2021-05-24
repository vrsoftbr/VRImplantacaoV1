package vrimplantacao2.dao.interfaces.winthor;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.utils.sql.SQLUtils.Intervalo;
import vrimplantacao2.vo.importacao.VendaItemIMP;

class WinthorVendaItemIterator extends MultiStatementIterator<VendaItemIMP> {
    
    final SimpleDateFormat ORACLE_DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
    
    private static final Logger LOG = LoggerFactory.getLogger(WinthorVendaIterator.class); 
    
    public WinthorVendaItemIterator(String idLoja, Date dataInicial, Date dataTermino) {
        super(
                new WinthorVendaItemNextBuilder(),
                new WinthorVendaIterator.WinthorStatementBuilder()
        );
        
        for (SQLUtils.Intervalo intervalo: SQLUtils.intervalosMensais(dataInicial, dataTermino)) {
            this.addStatement(this.buildSQL(idLoja, intervalo));
        }
    }
    
    private String buildSQL(String idLoja, Intervalo intervalo) {
        StringBuilder sql = new StringBuilder()
                .append("select\n")
                .append("	vi.NUMTRANSITEM id,\n")
                .append("	vi.NUMSEQ sequencia,\n")
                .append("	vi.NUMTRANSVENDA id_venda,\n")
                .append("	vi.CODPROD id_produto,\n")
                .append("	vi.DESCRICAO descricaoreduzida,\n")
                .append("	vi.QT quantidade,\n")
                .append("	vi.PUNIT precounitario,\n")
                .append("	case when vi.DTCANCEL is null then 0 else 1 end cancelado, \n")
                .append("	vi.VLDESCONTO valordesconto,\n")
                .append("	vi.CODAUXILIAR codigobarras,\n")
                .append("	vi.UNIDADE,\n")
                .append("	vi.SITTRIBUT icms_cst,\n")
                .append("	vi.PERICM icms_aliq,\n")
                .append("	vi.PERCICMRED icms_red,\n")
                .append("	vi.CUSTOREAL custocomimposto,\n")
                .append("	coalesce(vi.CUSTOREALSEMST, vi.CUSTOREAL) custosemimposto\n")
                .append("from\n")
                .append("	PCMOV vi\n")
                .append("where\n")
                .append("	vi.DTMOV between '")
                    .append(ORACLE_DATE_FORMATTER.format(intervalo.dataInicial))
                .append("' and '")
                    .append(ORACLE_DATE_FORMATTER.format(intervalo.dataFinal))
                .append("' and\n")
                .append("	vi.CODFILIAL = '").append(idLoja).append("' and\n")
                .append("	vi.NUMTRANSVENDA > 0 and\n")
                .append("	not vi.NUMTRANSITEM is null and\n")
                .append("	not vi.NUMNOTA is null\n")
                .append("order by\n")
                .append("	vi.NUMTRANSITEM");
        LOG.info("Script criado para a criação das vendas", sql);
        return sql.toString();
    }
    
    private static class WinthorVendaItemNextBuilder implements NextBuilder<VendaItemIMP> {
        @Override
        public VendaItemIMP makeNext(ResultSet rs) throws Exception {
            VendaItemIMP imp = new VendaItemIMP();
            
            imp.setId(rs.getString("id"));
            imp.setSequencia(rs.getInt("sequencia"));
            imp.setVenda(rs.getString("id_venda"));
            imp.setProduto(rs.getString("id_produto"));
            imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
            imp.setQuantidade(rs.getDouble("quantidade"));
            imp.setPrecoVenda(rs.getDouble("precounitario"));
            imp.setCancelado(rs.getBoolean("cancelado"));
            imp.setValorDesconto(rs.getDouble("valordesconto"));
            imp.setCodigoBarras(rs.getString("codigobarras"));
            imp.setUnidadeMedida(rs.getString("UNIDADE"));
            imp.setIcmsCst(rs.getInt("icms_cst"));
            imp.setIcmsAliq(rs.getDouble("icms_aliq"));
            imp.setIcmsReduzido(rs.getDouble("icms_red"));
            imp.setCustoComImposto(rs.getDouble("custocomimposto"));
            imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
            imp.setCustoMedioComImposto(rs.getDouble("custocomimposto"));
            imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
            
            return imp;
        }        
    }
    
}
