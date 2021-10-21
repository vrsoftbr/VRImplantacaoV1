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
            
            if (isLojaExiste(vo)) {
                lojaDAO.atualizarLoja(vo);
            } else {
                lojaDAO.salvarLoja(vo);

                if (vo.isCopiaTecladoLayout()) {
                    lojaDAO.copiarPdvTecladoLayout(vo);
                    lojaDAO.copiarPdvTecladoLayoutFuncao(vo);
                }
            }
            
            provider.commit();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();
        }
    }
    
    private boolean isLojaExiste(LojaVO vo) throws Exception {
        return lojaDAO.isLojaExiste(vo);
    }
    
    private String getTitle() {
        return "Cadastro Loja";
    }
}
