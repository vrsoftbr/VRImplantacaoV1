package vrimplantacao2_5.service.cadastro.configuracao;

import vrframework.classe.Conexao;

/**
 *
 * @author guilhermegomes
 */
class ConfiguracaoBaseDadosServiceProvider {
    
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
