package vrimplantacao2.gui.component.mapatributacao;

import java.util.List;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author Leandro
 */
public interface MapaTributoProvider {
    public List<MapaTributoIMP> getTributacao() throws Exception;
}
