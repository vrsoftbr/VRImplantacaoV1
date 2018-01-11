package vrimplantacao.gui.assistente.mapamercadologico;

import java.util.List;
import vrimplantacao.vo.vrimplantacao.MercadologicoMapaVO;

/**
 *
 * @author Leandro
 */
public interface MercadologicoMapaAdapter {

    public List<MercadologicoMapaVO> obterListagem() throws Exception;
    
}
