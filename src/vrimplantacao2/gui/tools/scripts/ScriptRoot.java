package vrimplantacao2.gui.tools.scripts;

import vrimplantacao2.gui.tools.scripts.sped.SpedNode;

/**
 * Nó inicial.
 * @author Leandro
 */
class ScriptRoot extends ScriptNode {

    public ScriptRoot() {
        add(new SpedNode());
    }

    @Override
    public String getNome() {
        return "Scripts";
    }

    @Override
    public String getInstrucoes() {
        return "<- Selecione um script no ao lado.";
    }
}
