package vrimplantacao2_5.service.mapaloja;

import java.util.List;
import org.openide.util.Exceptions;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaService {
    
    private LojaDAO lojaDAO;

    public MapaLojaService() {
        this.lojaDAO = new LojaDAO();
    }
    
    public MapaLojaService(LojaDAO lojaDAO) {
        this.lojaDAO = lojaDAO;
    }
    
    public List<LojaVO> getLojaVR() {
        List<LojaVO> lojas = null;
        
        try {
            lojas = lojaDAO.carregar();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return lojas;
    }
}
