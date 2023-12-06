package vr.implantacao.main;

import vr.view.helpers.ConexaoPropertiesEditorGUI;
import java.awt.Font;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vr.core.collection.Properties;
import vr.view.dialogs.Alerts;
import vrframework.classe.Conexao;
import vr.view.dialogs.splashsscreen.SplashScreen;
import vrframework.classe.Util;
import vrimplantacao.classe.Global;

import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.gui.login.LoginGUI;

/**
 * Classe principal da aplicação
 *
 * @author leandro
 */
public class App {

    private static Logger LOG = LoggerFactory.getLogger(App.class);

    private static App instance;

    private static void startApp(String... args) {
        if (instance != null) {
            instance.encerrar();
            instance = null;
        }
        instance = new App();
        instance.inicializar();
    }

    public static void main(String[] args) {
        App.startApp(args);
    }

    private Properties properties;

    private App() {
    }

    public static Properties properties() {
        return instance.properties;
    }

    /**
     * Rotina que encerra todas as atividades da aplicação. Seria como o
     * desligamento do sistema, porém sem encerrar a aplicação.
     */
    private void encerrar() {
        this.properties = null;
    }

    private void inicializar(String... args) {
        inicializarProperties();
        tratarParametros(args);
        inicializarView();
        inicializarSistema();
    }

    private void inicializarProperties() {
        try {
            //Tenta obter a localização do arquivo properties.
            properties = Properties.getVrImplantacaoProperties();
            if (properties == null || !properties.exists()) {
                properties = ConexaoPropertiesEditorGUI.create();
            }
            if (properties == null || !properties.exists()) {
                Alerts.aviso("Nenhum arquivo properties foi configurado, encerrando", LOG);
                System.exit(1);
            }
            properties.carregar();
        } catch (IOException ex) {
            Alerts.erro("Erro ao carregar o properties", ex);
            System.exit(1);
        }
    }

    private void tratarParametros(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (String arg : args) {
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

    private void inicializarView() {
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

    private void inicializarSistema() {
        try {

            SplashScreen.show();
            SplashScreen.setStatus("Inicializando sistema...");

            Global.idLoja = properties.getInt("system.numeroloja");

            SplashScreen.setStatus("Abrindo conexão...");
            abrirConexao(properties);

            SplashScreen.setStatus("Carregando interface...");
            SplashScreen.dispose();

            callLogin(properties);

        } catch (Exception ex) {
            ex.printStackTrace();
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
        String unidade = oProperties.get("system.unidade") == null ? null : oProperties.get("system.unidade");
        
        LoginGUI form = new LoginGUI();

        if (!oProperties.get("system.usuario", "").isEmpty()) {
            form.setUsuario(oProperties.get("system.usuario").toUpperCase());
        }

        if (!oProperties.get("system.senha", "").isEmpty()) {
            form.setSenha(oProperties.get("system.senha").toUpperCase());
        }

        if (!("".equals(unidade) || unidade == null)) {
            form.setUnidade(unidade);
        }

        form.setVisible(true);
    }

}
