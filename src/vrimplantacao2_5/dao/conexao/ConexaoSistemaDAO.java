package vrimplantacao2_5.dao.conexao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBDVO;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author guilhermegomes
 */
public class ConexaoSistemaDAO {

    public void inserir(ConfiguracaoBDVO conexaoVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setTableName("conexao");
        sql.setSchema("implantacao2_5");

        sql.put("host", conexaoVO.getHost());
        sql.put("porta", conexaoVO.getPorta());
        sql.put("usuario", conexaoVO.getUsuario());
        sql.put("senha", conexaoVO.getSenha());
        sql.put("descricao", conexaoVO.getDescricao());
        sql.put("nomeschema", conexaoVO.getSchema());
        sql.put("id_sistema", conexaoVO.getIdSistema());
        sql.put("id_bancodados", conexaoVO.getIdBancoDados());

        sql.getReturning().add("id");

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                    if (rst.next()) {
                        conexaoVO.setId(rst.getInt("id"));
                    }
                }
            }
        }
    }

    public void alterar(ConfiguracaoBDVO conexaoVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setTableName("conexao");
        sql.setSchema("implantacao2_5");

        sql.put("host", conexaoVO.getHost());
        sql.put("porta", conexaoVO.getPorta());
        sql.put("usuario", conexaoVO.getUsuario());
        sql.put("senha", conexaoVO.getSenha());
        sql.put("descricao", conexaoVO.getDescricao());
        sql.put("nomeschema", conexaoVO.getSchema());
        sql.put("id_sistema", conexaoVO.getIdSistema());
        sql.put("id_bancodados", conexaoVO.getIdBancoDados());

        sql.setWhere("id = " + conexaoVO.getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }
    }
}
