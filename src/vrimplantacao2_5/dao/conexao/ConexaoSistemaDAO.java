package vrimplantacao2_5.dao.conexao;

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
        sql.put("id_sistemabancodados", conexaoVO.getIdSistemaBancoDados());
        
        try(Statement stm = Conexao.createStatement()) {
            
            stm.execute(sql.getInsert());
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
        sql.put("id_sistemabancodados", conexaoVO.getIdSistemaBancoDados());

        sql.setWhere("id = " + conexaoVO.getId());
        
        try(Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getUpdate());
        }
    }
}
