package vrimplantacao2_5.controller.utils;

import java.util.List;
import vrimplantacao2_5.service.utils.MunicipioService;
import vrimplantacao2_5.vo.utils.MunicipioVO;

/**
 *
 * @author Desenvolvimento
 */
public class MunicipioController {

    private MunicipioService municipioService = new MunicipioService();
    private List<MunicipioVO> municipioVO = null;
    
    public void getMunicipios(int idEstado) throws Exception {
        municipioVO = municipioService.getMunicipios(idEstado);
    }
    
    public List<MunicipioVO> getMunicipio() {
        return this.municipioVO;
    }
}
