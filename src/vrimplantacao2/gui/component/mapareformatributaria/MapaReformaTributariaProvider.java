package vrimplantacao2.gui.component.mapareformatributaria;

import java.util.List;
import vrimplantacao2.vo.importacao.MapaReformaTributariaClassificacaoIMP;
import vrimplantacao2.vo.importacao.MapaReformaTributariaCstIMP;

/**
 *
 * @author wesley
 */
public interface MapaReformaTributariaProvider {
    
    public List<MapaReformaTributariaCstIMP> getMapaReformaTributariaCst() throws Exception;
    
    public List<MapaReformaTributariaClassificacaoIMP> getMapaReformaTributariaClassificacao() throws Exception;
}
