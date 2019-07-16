package vrimplantacao.dao;

import vrframework.classe.Properties;
import vrframework.classe.Proxy;
import vrframework.classe.Util;
import vrframework.remote.Arquivo;
import vrimplantacao.gui.ConfiguracaoGUI;

public class PropertiesDAO {

    public void verficarConfiguracao() throws Exception {
        if (!Arquivo.exists(Util.getRoot() + "vr/implantacao/vrimplantacao.properties")) {
            ConfiguracaoGUI form = new ConfiguracaoGUI();
            form.setVisible(true);

            if (form.isCancel()) {
                Util.exibirMensagem("Arquivo properties não configurado!", "Atenção");
                System.exit(0);
            }
        }
    }

    public void carregarConfiguracao(Properties i_properties) throws Exception {
        //configura proxy
        Proxy.ip = i_properties.getString("proxy.ip");
        Proxy.porta = i_properties.getString("proxy.porta");
        Proxy.usuario = i_properties.getString("proxy.usuario");
        Proxy.senha = i_properties.getString("proxy.senha");

        if (!Proxy.ip.isEmpty()) {
            Proxy.setProxy();
        }
    }
}
