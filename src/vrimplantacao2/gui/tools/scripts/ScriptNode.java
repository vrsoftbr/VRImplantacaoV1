package vrimplantacao2.gui.tools.scripts;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Classe abstrata utilizada para implementar Scripts no ScriptGUI.
 * @author Leandro
 */
public abstract class ScriptNode extends DefaultMutableTreeNode {
    
    /**
     * Nome do nó que deve aparecer no TreeView.
     * @return Nome do nó.
     */
    public abstract String getNome();
    /**
     * Instruções do Script que devem aparecer no txtScript para orientar o usuário.
     * @return 
     */
    public abstract String getInstrucoes();

    @Override
    public final String toString() {
        return getNome();
    }
}
