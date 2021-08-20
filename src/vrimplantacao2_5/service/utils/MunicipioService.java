package vrimplantacao2_5.service.utils;

import java.util.List;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.utils.MunicipioDAO;
import vrimplantacao2_5.vo.utils.MunicipioVO;

/**
 *
 * @author Desenvolvimento
 */
public class MunicipioService {

    private MunicipioDAO municipioDAO;
    
    public MunicipioService() {
        this.municipioDAO = new MunicipioDAO();
    }
    
    public MunicipioService(MunicipioDAO municipioDAO) {
        this.municipioDAO = municipioDAO;
    }
    
    public List<MunicipioVO> getMunicipios(int idEstado) throws Exception {
        List<MunicipioVO> result = null;
        
        try {
            result = municipioDAO.getMunicipios(idEstado);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Município");
        }
        
        return result;
    }
}
