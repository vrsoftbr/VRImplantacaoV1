package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Leandro
 */
public class OutraReceitaDAO {

    public void gravar(OutraReceitaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("receberoutrasreceitas");
            sql.put("id_loja", vo.getIdLoja());
            sql.put("id_clienteeventual", vo.getIdClienteEventual(), 0);
            sql.put("dataemissao", vo.getDataEmissao());
            sql.put("datavencimento", vo.getDataVencimento());
            sql.put("valor", vo.getValor());
            sql.put("observacao", vo.getObservacao());
            sql.put("id_situacaoreceberoutrasreceitas", vo.getSituacao().getId());
            sql.put("id_tipolocalcobranca", vo.getTipoLocalCobranca().getId());
            sql.put("id_fornecedor", vo.getIdFornecedor(), 0);
            sql.put("id_boleto", vo.getIdBoleto(), 0);
            sql.put("datahoraalteracao", vo.getDataHoraAlteracao());
            sql.put("exportado", vo.isExportado());
            sql.put("id_notaservico", vo.getIdNotaServico(), 0);
            sql.put("dataexportacao", vo.getDataExportacao());
            sql.getReturning().add("id");
            
            try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                if (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }
        }
    }
    
}
