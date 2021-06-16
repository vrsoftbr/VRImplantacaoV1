package vrimplantacao2_5.gui.componente;

import vrimplantacao2_5.gui.componente.conexao.ConexaoEvent;

/**
 *
 * @author guilhermegomes
 */
public interface ConfiguracaoPanel {

    void setOnConectar(ConexaoEvent evento);
    String getHost();
    String getPorta();
    String getSchema();
    String getUsuario();
    String getSenha();
}
