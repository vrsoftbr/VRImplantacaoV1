package vr.implantacao;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vr.core.collection.Properties;
import vrframework.classe.Conexao;
import vrframework.classe.SplashScreen;
import vrframework.classe.Util;
import vrimplantacao.classe.Global;
import vrimplantacao.gui.ConfiguracaoGUI;
import vrimplantacao.gui.LoginGUI;
import vrimplantacao2.parametro.Parametros;

/**
 * Classe principal da aplicação
 * @author leandro
 */
public class App {
    
    private static Logger LOG = LoggerFactory.getLogger(App.class);
    

    private static void tratarParametros(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (String arg: args) {
            String[] st = arg.split("=");
            if (st.length == 1) {
                params.put(st[0], "");
            } else if (st.length > 1) {
                params.put(st[0], st[1]);
            }
        }        
        if (params.containsKey("-lite")) {
            Parametros.lite = params.get("-lite");
        }
    }
    
    private static void inicializarView() {
        try {
            String OSName = System.getProperty("os.name");

            System.out.println("os.name " + OSName);

            if (OSName.toUpperCase().contains("LINUX")) {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Metal".equals(info.getName())) {
                        UIManager.getDefaults().put("TitledBorder.font", new Font("Tahoma", Font.PLAIN, 10));
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } else {
                Util.setLookAndFeel();
            }            
        } catch (Exception e) {
            LOG.error("Erro ao inicializar o LookAndFeel", e);
        }
    }
    
    private static void inicializarSistema() {
        try {

            SplashScreen.show();
            SplashScreen.setSobre("VR Implantação", Global.VERSAO, Global.DATA);
            SplashScreen.setStatus("Inicializando sistema...");

            Properties oProperties = Properties.getVrImplantacaoProperties();
            if (oProperties == null) {
                ConfiguracaoGUI gui = new ConfiguracaoGUI();
                gui.setModal(true);
                gui.setVisible(true);
                oProperties = Properties.getVrImplantacaoProperties();
                if (oProperties == null) {
                    System.exit(0);
                }
            }
            oProperties.carregar();
            
            Global.idLoja = oProperties.getInt("system.numeroloja");
            
            SplashScreen.setStatus("Abrindo conexão...");
            abrirConexao(oProperties);

            SplashScreen.setStatus("Carregando interface...");
            SplashScreen.dispose();            
            
            callLogin(oProperties);

        } catch (Exception ex) {
            SplashScreen.dispose();
            Util.exibirMensagemErro(ex, "Atenção");
        }
    }

    private static void abrirConexao(Properties oProperties) throws Exception {
        String ipBanco = oProperties.get("database.ip");
        String ipSecBanco = oProperties.get("database.ipsec");
        int portaBanco = oProperties.getInt("database.porta");
        String nomeBanco = oProperties.get("database.nome");
        String usuarioBanco = oProperties.get("database.usuario") == null ? "postgres" : oProperties.get("database.usuario");
        String senhaBanco = oProperties.get("database.senha") == null ? "postgres" : oProperties.get("database.senha");
        
        Conexao.abrirConexao(ipBanco, ipSecBanco, portaBanco, nomeBanco, usuarioBanco, senhaBanco);
    }

    private static void callLogin(Properties oProperties) throws Exception {
        LoginGUI form = new LoginGUI();
        
        if (!oProperties.get("system.usuario","").isEmpty()) {
            form.setUsuario(oProperties.get("system.usuario").toUpperCase());
        }
        
        if (!oProperties.get("system.senha","").isEmpty()) {
            form.setSenha(oProperties.get("system.senha").toUpperCase());
        }        
        
        form.setVisible(true);
    }
    
    public static void main(String[] args) {        
        tratarParametros(args);        
        inicializarView();        
        inicializarSistema();        
    }
    
}
