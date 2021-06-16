package vrimplantacao2_5.service.cadastro;

import java.util.List;
import org.openide.util.Exceptions;
import vrframework.classe.Conexao;
import vrimplantacao2_5.dao.bancodados.BancoDadosDAO;
import vrimplantacao2_5.dao.sistema.SistemaDAO;
import vrimplantacao2_5.service.cadastro.panelconexaofactory.PanelConexaoFactory;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.conexao.ConexaoSistemaDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBDVO;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosService {
    
    private final SistemaDAO sistemaDAO;
    private final BancoDadosDAO bdDAO;
    private final ConexaoSistemaDAO conexaoDAO;

    public ConfiguracaoBaseDadosService() {
        sistemaDAO = new SistemaDAO();
        bdDAO = new BancoDadosDAO();
        conexaoDAO = new ConexaoSistemaDAO();
    }

    public ConfiguracaoBaseDadosService(SistemaDAO sistemaDAO,
                                        BancoDadosDAO bdDAO,
                                        ConexaoSistemaDAO conexaoDAO) {
        this.sistemaDAO = sistemaDAO;
        this.bdDAO = bdDAO;
        this.conexaoDAO = conexaoDAO;
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
    
    public ConfiguracaoPanel exibiPainelConexao(int idSistema, int idBancoDados) {
        return PanelConexaoFactory.getPanelConexao(idSistema, idBancoDados);
    }
    
    public void salvar(ConfiguracaoBDVO conexaoVO) {

        try {
            Conexao.begin();
            
            if(conexaoVO.getId() == 0) {
                conexaoDAO.inserir(conexaoVO);
            } else {
                conexaoDAO.alterar(conexaoVO);
            }
            
            Conexao.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            
            try {
                Conexao.rollback();
            } catch (Exception ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
    }
}
