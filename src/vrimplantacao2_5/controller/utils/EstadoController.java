package vrimplantacao2_5.controller.utils;

import java.util.List;
import vrimplantacao2_5.service.utils.EstadoService;
import vrimplantacao2_5.vo.utils.EstadoVO;

/**
 *
 * @author Desenvolvimento
 */
public class EstadoController {
    
    private final EstadoService estadoService = new EstadoService();
    private List<EstadoVO> estadoVO = null;
    
    public List<EstadoVO> getEstados() throws Exception {
        return this.estadoVO = estadoService.getEstado();
    }
    
}
