package vrimplantacao2.gui.tools.scripts.sped;

import vrimplantacao2.gui.tools.scripts.ScriptNode;

/**
 * Nó para organizar scripts do SPED.
 * @author Leandro
 */
public class SpedNode extends ScriptNode {

    public SpedNode() {
        add(new ProdutoAlteracao());
        add(new FornecedorAlteracao());
    }

    @Override
    public String getNome() {
        return "SPED";
    }

    @Override
    public String getInstrucoes() {
        return "Coleção de Scripts relacionados ao Sped.";
    }
    
}
