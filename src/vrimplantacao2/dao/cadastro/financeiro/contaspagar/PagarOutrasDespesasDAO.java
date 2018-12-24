package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVO;

/**
 *
 * @author Leandro
 */
public class PagarOutrasDespesasDAO {

    public void gravar(PagarOutrasDespesasVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setTableName("pagaroutrasdespesas");
        sql.put("id_fornecedor", vo.getIdFornecedor());
        sql.put("numerodocumento", vo.getNumeroDocumento());
        sql.put("id_tipoentrada", vo.getTipoEntrada().getId());        
        sql.put("dataemissao", vo.getDataEmissao());
        sql.put("dataentrada", vo.getDataEntrada());
        sql.put("valor", vo.getValor());
        sql.put("id_situacaopagaroutrasdespesas", vo.getSituacaoPagarOutrasDespesas().getId());
        sql.put("id_loja", vo.getId_loja());
        sql.put("observacao", vo.getObservacao());
        sql.put("id_tipopiscofins", vo.getId_tipopiscofins(), -1);
        sql.put("datahoraalteracao", vo.getDataHoraAlteracao());
        sql.getReturning().add("id");
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                rst.next();
                vo.setId(rst.getInt("id"));
            }
        }
    }

    public void atualizar(PagarOutrasDespesasVO vo, Set<OpcaoContaPagar> opt) throws Exception {
        /*
        SQLBuilder sql = new SQLBuilder();
        sql.setTableName("pagaroutrasdespesas");
        sql.setWhere("id = " + vo.getId());        
        sql.put("id_fornecedor", vo.getIdFornecedor());
        sql.put("numerodocumento", vo.getNumeroDocumento());
        sql.put("id_tipoentrada", vo.getTipoEntrada().getId());        
        sql.put("dataemissao", vo.getDataEmissao());
        sql.put("dataentrada", vo.getDataEntrada());
        sql.put("valor", vo.getValor());
        sql.put("id_situacaopagaroutrasdespesas", vo.getSituacaoPagarOutrasDespesas().getId());
        sql.put("id_loja", vo.getId_loja());
        sql.put("observacao", vo.getObservacao());
        sql.put("id_tipopiscofins", vo.getId_tipopiscofins());
        sql.put("datahoraalteracao", vo.getDataHoraAlteracao());
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getUpdate());
        }
        */
    }

    public int getFornecedorLoja(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_fornecedor from loja where id = " + lojaVR
            )) {
                if (rst.next()) {
                    return rst.getInt("id_fornecedor");
                }
            }
        }
        return 1;
    }
    
}
