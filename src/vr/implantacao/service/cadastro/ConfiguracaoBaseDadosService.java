package vr.implantacao.service.cadastro;

import java.util.List;
import org.openide.util.Exceptions;
import vr.implantacao.dao.bancodados.BancoDadosDAO;
import vr.implantacao.dao.sistema.SistemaDAO;
import vr.implantacao.service.panelconexaofactory.PanelConexaoFactory;
import vr.implantacao.vo.cadastro.BancoDadosVO;
import vr.implantacao.vo.cadastro.SistemaVO;
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
    
    public javax.swing.JPanel exibiPainelConexao(int idSistema, int idBancoDados) {
        return PanelConexaoFactory.getPanelConexao(idSistema, idBancoDados);
    }
}
