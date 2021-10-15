package vrimplantacao2_5.service.loja;

import vrframework.classe.Util;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.provider.ConexaoProvider;

/**
 *
 * @author Desenvolvimento
 */
public class LojaService {

    private final ConexaoProvider provider;
    private final LojaDAO lojaDAO;
    
    public LojaService() {
        this.lojaDAO = new LojaDAO();
        this.provider = new ConexaoProvider();
    }
    
    public LojaService(LojaDAO lojaDAO,
                       ConexaoProvider provider) {
        
        this.lojaDAO = lojaDAO;
        this.provider = provider;
    }
    
    public void salvaNovo(LojaVO vo) throws Exception {
        
        try {
            provider.begin();
            
            lojaDAO.salvarNovo(vo);
            
            provider.commit();
            
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();
        }
    }
    
    private String getTitle() {
        return "Cadastro Loja";
    }
}
