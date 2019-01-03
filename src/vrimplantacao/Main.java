package vrimplantacao;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.Properties;
import vrframework.classe.SplashScreen;
import vrframework.classe.Util;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.PropertiesDAO;
import vrimplantacao.gui.LoginGUI;
import vrimplantacao2.parametro.Parametros;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        
        Map<String, String> params = new HashMap<>();
        for (String arg: args) {
            String[] st = arg.split("=");
            if (st.length == 1) {
                params.put(st[0], "");
            } else if (st.length > 1) {
                params.put(st[0], st[1]);
            }
        }

        try {
            Util.setLookAndFeel();
            new PropertiesDAO().verficarConfiguracao();

            SplashScreen.show();
            SplashScreen.setSobre("VR Implantação", Global.VERSAO, Global.DATA);
            SplashScreen.setStatus("Inicializando sistema...");

            Properties oProperties = new Properties(Util.getRoot() + "vr/Implantacao/vrImplantacao.properties");
            //Properties oProperties = new Properties(Util.getRoot() + "vr/vrImplantacao.properties");            

            //conecta banco
            String ipBanco = oProperties.getString("database.ip");
            String ipSecBanco = oProperties.getString("database.ipsec");
            int portaBanco = oProperties.getInt("database.porta");
            String nomeBanco = oProperties.getString("database.nome");
            String usuarioBanco = oProperties.getString("database.usuario").isEmpty() ? "postgres" : oProperties.getString("database.usuario");
            String senhaBanco = oProperties.getString("database.senha").isEmpty() ? "postgres" : oProperties.getString("database.senha");

            SplashScreen.setStatus("Abrindo conexão...");

            Conexao.abrirConexao(ipBanco, ipSecBanco, portaBanco, nomeBanco, usuarioBanco, senhaBanco);

            Global.idLoja = oProperties.getInt("system.numeroloja");

            SplashScreen.setStatus("Carregando interface...");

            //login
            LoginGUI form = new LoginGUI();

            if (!oProperties.getString("system.usuario").isEmpty()) {
                form.setUsuario(oProperties.getString("system.usuario").toUpperCase());
            }

            if (!oProperties.getString("system.senha").isEmpty()) {
                form.setSenha(oProperties.getString("system.senha").toUpperCase());
            }

            SplashScreen.dispose();
            
            if (params.containsKey("-lite")) {
                Parametros.lite = params.get("-lite");
            }

            form.setVisible(true);

        } catch (Exception ex) {
            SplashScreen.dispose();
            Util.exibirMensagemErro(ex, "Atenção");
        }
    }    
}
