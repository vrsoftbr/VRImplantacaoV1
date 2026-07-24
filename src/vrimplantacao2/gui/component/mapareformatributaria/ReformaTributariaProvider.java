package vrimplantacao2.gui.component.mapareformatributaria;

import java.awt.Frame;

/**
 *
 * @author wesley
 */
public interface ReformaTributariaProvider {

    public MapaReformaTributariaProvider getProvider();

    public String getSistema();

    public String getLoja();

    public Frame getFrame();

}