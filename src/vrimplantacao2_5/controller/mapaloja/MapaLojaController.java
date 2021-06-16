package vrimplantacao2_5.controller.mapaloja;

import java.util.List;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.service.mapaloja.MapaLojaService;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaController {
    
    private final MapaLojaService mapaLojaService;

    public MapaLojaController() {
        this.mapaLojaService = new MapaLojaService();
    }
    
    public List<LojaVO> getLojaVR() {
        return mapaLojaService.getLojaVR();
    }
}
