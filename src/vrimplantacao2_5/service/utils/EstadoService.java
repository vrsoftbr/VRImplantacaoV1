package vrimplantacao2_5.service.utils;

import java.util.List;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.utils.EstadoDAO;
import vrimplantacao2_5.vo.utils.EstadoVO;

/**
 *
 * @author Desenvolvimento
 */
public class EstadoService {

    private final EstadoDAO estadoDAO;
    
    public EstadoService() {
        this.estadoDAO = new EstadoDAO();
    }
    
    public EstadoService(EstadoDAO estadoDAO) {        
        this.estadoDAO = estadoDAO;
    }
    
    public List<EstadoVO> getEstado() throws Exception {
        List<EstadoVO> result = null;
        
        try {
            result = estadoDAO.getEstados();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Estados");
        }

        return result;
    }
}