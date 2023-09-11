package vrimplantacao2_5.mercadologicopadrao;

import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import vr.core.collection.Properties;
import vr.view.dialogs.Alerts;
import vr.view.helpers.ConexaoPropertiesEditorGUI;
import vrframework.classe.Conexao;
import vrimplantacao2_5.mercadologicopadrao.dao.MercadologicoPadraoDAO;

/**
 *
 * @author Michael
 */
public class MercadologicoPadraoDAOTest {

    private MercadologicoPadraoDAO dao;
    Properties prop;
    private String ip;
    private String nomeBanco;
    private int porta;
    private String usuario;
    private String senha;
    
    @Before
    public void carregarProperties(){
        try {
            inicializarProperties();
            ip = prop.get("database.ip");
            nomeBanco = prop.get("database.nome");
            porta = Integer.parseInt(prop.get("database.porta"));
            usuario = prop.get("database.usuario");
            senha = prop.get("database.senha");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void inicializarProperties() {
        try {
            //Tenta obter a localização do arquivo properties.
            prop = Properties.getVrImplantacaoProperties();
            if (prop == null || !prop.exists()) {
                prop = ConexaoPropertiesEditorGUI.create();
            }
            if (prop == null || !prop.exists()) {
                System.out.println("null");
            }
            prop.carregar();
        } catch (IOException ex) {
            Alerts.erro("Erro ao carregar o properties", ex);
            System.exit(1);
        }
    }

    @Test
    public void testMercadologicoPadraoPasso1() throws Exception {
        dao = mock(MercadologicoPadraoDAO.class);
        Mockito.verify(dao, Mockito.times(0)).insereMercadologicoTemporario();
    }

    @Test
    public void testMercadologicoPadraoPasso2() throws Exception {
        dao = mock(MercadologicoPadraoDAO.class);
        Mockito.verify(dao, Mockito.times(0)).mercadologicoPadraoPasso2();
    }

    @Test
    public void testInsereMercadologicoTemporario() throws Exception {
        Conexao.abrirConexao(ip, porta, nomeBanco, usuario, senha);
        dao = mock(MercadologicoPadraoDAO.class);
        dao.insereMercadologicoTemporario();
        
        Mockito.verify(dao, Mockito.times(1)).insereMercadologicoTemporario();  
    }

}
