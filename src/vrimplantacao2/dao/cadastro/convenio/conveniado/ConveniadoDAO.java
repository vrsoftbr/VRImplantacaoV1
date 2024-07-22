package vrimplantacao2.dao.cadastro.convenio.conveniado;

import java.sql.ResultSet;
import java.sql.Statement;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoVO;

/**
 *
 * @author Leandro
 */
public class ConveniadoDAO {
     private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

    public MultiMap<Long, Integer> getCnpjCadastrado() throws Exception {
        MultiMap<Long, Integer> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, id_empresa, cnpj from conveniado"
            )) {
                while (rst.next()) {
                    result.put(rst.getInt("id"), rst.getLong("id_empresa"), rst.getLong("cnpj"));
                }
            }
        }
        
        return result;
    }

    public void gravarConveniado(ConveniadoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("conveniado");
            sql.put("id", vo.getId());
            sql.put("nome", vo.getNome());
            sql.put("id_empresa", vo.getId_empresa());
            sql.put("bloqueado", vo.isBloqueado());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
            sql.put("senha", vo.getSenha());
            sql.put("id_loja", vo.getId_loja());
            sql.put("cnpj", vo.getCnpj());
            sql.put("observacao", vo.getObservacao());
            sql.put("id_tipoinscricao", vo.getTipoInscricao().getId());
            sql.put("matricula", vo.getMatricula());
            sql.put("datavalidadecartao", vo.getDataValidadeCartao());
            sql.put("datadesbloqueio", vo.getDataDesbloqueio());
            sql.put("visualizasaldo", vo.isVisualizaSaldo());
            sql.put("databloqueio", vo.getDataBloqueio());
            if(versao.maiorQue(4,2,0)){
                sql.put("identificacaocartao", vo.getIdentificacaoCartao());
            }
            stm.execute(sql.getInsert());
        }
    }
    
}
