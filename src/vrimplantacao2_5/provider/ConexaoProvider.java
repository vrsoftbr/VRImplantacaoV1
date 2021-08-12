package vrimplantacao2_5.provider;

import vrframework.classe.Conexao;

/**
 *
 * @author Desenvolvimento
 */
public class ConexaoProvider {
    public void begin() throws Exception {
        Conexao.begin();
    }
    
    public void commit() throws Exception {
        Conexao.commit();
    }
    
    public void rollback() throws Exception {
        Conexao.rollback();
    }
    
    public void close() throws Exception {
        Conexao.close();
    }    
}
