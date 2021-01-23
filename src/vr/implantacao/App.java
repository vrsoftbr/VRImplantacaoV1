package vr.implantacao;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vrframework.classe.Conexao;
import vrframework.classe.Properties;
import vrframework.classe.SplashScreen;
import vrframework.classe.Util;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.PropertiesDAO;
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
            new PropertiesDAO().verficarConfiguracao();

            SplashScreen.show();
            SplashScreen.setSobre("VR Implantação", Global.VERSAO, Global.DATA);
            SplashScreen.setStatus("Inicializando sistema...");


            Properties oProperties = new Properties(Util.getRoot() + "vr/implantacao/vrimplantacao.properties");
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
        String ipBanco = oProperties.getString("database.ip");
        String ipSecBanco = oProperties.getString("database.ipsec");
        int portaBanco = oProperties.getInt("database.porta");
        String nomeBanco = oProperties.getString("database.nome");
        String usuarioBanco = oProperties.getString("database.usuario").isEmpty() ? "postgres" : oProperties.getString("database.usuario");
        String senhaBanco = oProperties.getString("database.senha").isEmpty() ? "postgres" : oProperties.getString("database.senha");
        
        Conexao.abrirConexao(ipBanco, ipSecBanco, portaBanco, nomeBanco, usuarioBanco, senhaBanco);
    }

    private static void callLogin(Properties oProperties) throws Exception {
        LoginGUI form = new LoginGUI();
        
        if (!oProperties.getString("system.usuario").isEmpty()) {
            form.setUsuario(oProperties.getString("system.usuario").toUpperCase());
        }
        
        if (!oProperties.getString("system.senha").isEmpty()) {
            form.setSenha(oProperties.getString("system.senha").toUpperCase());
        }
        
        
        form.setVisible(true);
    }
    
    public static void main(String[] args) {        
        tratarParametros(args);        
        inicializarView();        
        inicializarSistema();        
    }
    
}
