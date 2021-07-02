package vrimplantacao2_5.service.cadastro.configuracao;

import vrimplantacao2_5.gui.componente.conexao.ConexaoEvent;

/**
 *
 * @author guilhermegomes
 */
public interface ConfiguracaoPanel {

    void setOnConectar(ConexaoEvent evento);
    void setDadosConexao(String host, String schema, int porta, String usuario, String senha);
    String getHost();
    String getPorta();
    String getSchema();
    String getUsuario();
    String getSenha();
}
