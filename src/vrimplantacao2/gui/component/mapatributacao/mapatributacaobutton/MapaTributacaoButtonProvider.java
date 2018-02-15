package vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton;

import java.awt.Frame;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;

/**
 *
 * @author Leandro
 */
public interface MapaTributacaoButtonProvider {
    
    public MapaTributoProvider getProvider();
    public String getSistema();
    public String getLoja();
    public Frame getFrame();
    
}
