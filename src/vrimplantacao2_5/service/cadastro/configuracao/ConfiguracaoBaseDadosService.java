package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.List;
import org.openide.util.Exceptions;
import vrframework.classe.Conexao;
import vrimplantacao2_5.dao.bancodados.BancoDadosDAO;
import vrimplantacao2_5.dao.sistema.SistemaDAO;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoSistemaDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosService {
    
    private final SistemaDAO sistemaDAO;
    private final BancoDadosDAO bdDAO;
    private final ConfiguracaoSistemaDAO conexaoDAO;

    public ConfiguracaoBaseDadosService() {
        sistemaDAO = new SistemaDAO();
        bdDAO = new BancoDadosDAO();
        conexaoDAO = new ConfiguracaoSistemaDAO();
    }

    public ConfiguracaoBaseDadosService(SistemaDAO sistemaDAO,
                                        BancoDadosDAO bdDAO,
                                        ConfiguracaoSistemaDAO conexaoDAO) {
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
                Util.exibirMensagem(e.getMessage(), getTitle());
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
                Util.exibirMensagem(e.getMessage(), getTitle());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return bdPorSistema;
    }
    
    public ConfiguracaoPanel exibiPainelConexao(int idSistema, int idBancoDados) {
        return PanelConexaoFactory.getPanelConexao(idSistema, idBancoDados);
    }
    
    public void salvar(ConfiguracaoBancoVO conexaoVO) {
        
        try {
            Conexao.begin();
            
            if(conexaoVO.getId() == 0) {
                if (existeConexao(conexaoVO)) {
                    Util.exibirMensagem("Já existe uma conexão cadastrada!", getTitle());
                    return;
                }
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
    
    public boolean existeConexao(ConfiguracaoBancoVO configuracaoVO) {
        boolean retorno = false;
        
        try {
            retorno = conexaoDAO.existeConexao(configuracaoVO);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return retorno;
    }
    
    private String getTitle() {
        return "Configuração de Base de Dados";
    }
}
