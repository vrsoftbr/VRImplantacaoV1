package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.PagarFornecedorParcelaVO;

/**
 *
 * @author Leandro
 */
public class PagarFornecedorParcelaDAO {
    
    private static final SimpleDateFormat FORMATER = new SimpleDateFormat("yyyy-MM-dd");
    
    public void gravarPagarFornecedorParcela(PagarFornecedorParcelaVO venc) throws Exception {
        
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("public");
        sql.setTableName("pagarfornecedorparcela");
        sql.put("id_pagarfornecedor", venc.getId_pagarfornecedor());
        sql.put("numeroparcela", venc.getNumeroparcela());
        sql.put("datavencimento", venc.getDatavencimento());
        sql.put("datapagamento", venc.getDatapagamento());
        sql.put("valor", venc.getValor());
        sql.put("observacao", venc.getObservacao());
        sql.put("id_situacaopagarfornecedorparcela", venc.getSituacaopagarfornecedorparcela().getId());
        sql.put("id_tipopagamento", venc.getId_tipopagamento());
        sql.put("datapagamentocontabil", venc.getDatapagamentocontabil());
        sql.put("id_banco", venc.getId_banco(), -1);
        sql.put("agencia", venc.getAgencia());
        sql.put("conta", venc.getConta());
        sql.put("numerocheque", venc.getNumerocheque());
        sql.put("conferido", venc.isConferido());
        sql.put("valoracrescimo", venc.getValoracrescimo());
        sql.put("id_contacontabilfinanceiro", venc.getId_contacontabilfinanceiro(), -1);
        sql.put("id_conciliacaobancarialancamento", venc.getId_conciliacaobancarialancamento(), -1);
        sql.put("exportado", venc.isExportado());
        sql.put("datahoraalteracao", venc.getDatahoraalteracao());
        sql.getReturning().add("id");
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet qr = stm.executeQuery(sql.getInsert())) {
                qr.next();
                venc.setId(qr.getInt("id"));
            }
        }
        
    }

    public MultiMap<String, Void> getPagamentos() throws Exception {
        MultiMap<String, Void> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id_pagarfornecedor,\n" +
                    "	datavencimento,\n" +
                    "	valor\n" +
                    "from\n" +
                    "	pagarfornecedorparcela\n" +
                    "order by\n" +
                    "	id_pagarfornecedor,\n" +
                    "	datavencimento,\n" +
                    "	valor"
            )) {
                while (rst.next()) {
                    result.put(
                            null, 
                            rst.getString("id_pagarfornecedor"),
                            FORMATER.format(rst.getDate("datavencimento")),
                            String.format("%.2f", rst.getDouble("valor"))
                    );
                }
            }
        }
        return result;
    }
    
}
