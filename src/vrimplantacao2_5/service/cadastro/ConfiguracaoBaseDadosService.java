package vrimplantacao2_5.service.cadastro;

import java.util.List;
import org.openide.util.Exceptions;
import vrimplantacao2_5.dao.bancodados.BancoDadosDAO;
import vrimplantacao2_5.dao.sistema.SistemaDAO;
import vrimplantacao2_5.service.cadastro.panelconexaofactory.PanelConexaoFactory;
import vrimplantacao2_5.service.cadastro.panelobserver.PanelObserver;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;
import vrframework.classe.Util;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosService {
    
    private SistemaDAO sistemaDAO;
    private BancoDadosDAO bdDAO;

    public ConfiguracaoBaseDadosService() {
        sistemaDAO = new SistemaDAO();
        bdDAO = new BancoDadosDAO();
    }

    public ConfiguracaoBaseDadosService(SistemaDAO sistemaDAO,
                                        BancoDadosDAO bdDAO) {
        this.sistemaDAO = sistemaDAO;
        this.bdDAO = bdDAO;
    }
    
    public List getSistema() {
        List<SistemaVO> sistemas = null;
        
        try {
            
            sistemas = sistemaDAO.getSistema();
            
        } catch (Exception e) {
            try {
                Util.exibirMensagem(e.getMessage(), "Configuração de Base de Dados");
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return sistemas;
    }
    
    public List getBancoDadosPorSistema(int idSistema) {
        List<BancoDadosVO> bdPorSistema = null;
        
        try {
            
            bdPorSistema = bdDAO.getBancoDadosPorSistema(idSistema);
            
        } catch (Exception e) {
            try {
                Util.exibirMensagem(e.getMessage(), "Configuração de Base de Dados");
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return bdPorSistema;
    }
    
    public javax.swing.JPanel exibiPainelConexao(PanelObserver conexaoBD, int idSistema, int idBancoDados) {
        return PanelConexaoFactory.getPanelConexao(conexaoBD, idSistema, idBancoDados);
    }
}
