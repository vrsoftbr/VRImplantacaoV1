package vrimplantacao2_5.service.cadastro.unidade;

import java.util.List;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2_5.dao.cadastro.unidade.UnidadeDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;

/**
 *
 * @author Desenvolvimento
 */
public class UnidadeService {

    private final UnidadeDAO unidadeDAO;
    private final ConexaoProvider provider;
    
    public UnidadeService() {
        this.unidadeDAO = new UnidadeDAO();
        this.provider = new ConexaoProvider();
    }
    
    public UnidadeService(UnidadeDAO unidadeDAO, 
                          ConexaoProvider provider) {
        this.unidadeDAO = unidadeDAO;
        this.provider = provider;
    }
    
    public void existeUnidade(UnidadeVO vo) throws Exception {
        if (unidadeDAO.existeUnidade(vo)) {
            throw new VRException("Unidade já cadastrada");
        }
    }
    
    public void inserir(UnidadeVO vo) throws Exception {
        
        try {
        
            provider.begin();
            
            existeUnidade(vo);
            
            unidadeDAO.inserir(vo);
            
            provider.commit();
            
        } catch (Exception e) {
            try {
                Util.exibirMensagem(e.getMessage(), getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }
    }
    
    public void alterar(UnidadeVO vo) throws Exception {
        
        try {
            provider.begin();
            
            unidadeDAO.alterar(vo);
            
            provider.commit();
            
        } catch (Exception e) {
            try {
                Util.exibirMensagem(e.getMessage(), getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }            
        }
    }
    
    public List<UnidadeVO> consultar(UnidadeVO vo) throws Exception {
        List<UnidadeVO> result = null;
        
        try {            
            result = unidadeDAO.consultar(vo);            
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Unidades");
        }
        
        return result;
    }
    
    public List<UnidadeVO> getUnidades() throws Exception {
        List<UnidadeVO> result = null;
        
        try {
            result = unidadeDAO.getUnidades();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Unidades");
        }
        
        return result;
    }
    
    private String getTitle() {
        return "Cadastro Unidade";
    }        
}
