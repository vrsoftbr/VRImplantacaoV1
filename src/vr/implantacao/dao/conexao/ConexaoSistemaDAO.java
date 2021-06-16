package vr.implantacao.dao.conexao;

import java.sql.Statement;
import vr.implantacao.vo.cadastro.ConexaoVO;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author guilhermegomes
 */
public class ConexaoSistemaDAO {
    
    public void inserir(ConexaoVO conexaoVO) throws Exception {
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
    
    public void alterar(ConexaoVO conexaoVO) throws Exception {
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
