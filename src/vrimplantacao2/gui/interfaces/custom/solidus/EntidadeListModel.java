package vrimplantacao2.gui.interfaces.custom.solidus;

import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Leandro
 */
public class EntidadeListModel extends AbstractListModel<Entidade> {

    private final List<Entidade> entidades;

    public EntidadeListModel(List<Entidade> entidades) {
        this.entidades = entidades;
    }
    
    @Override
    public int getSize() {
        return entidades.size();
    }

    @Override
    public Entidade getElementAt(int index) {
        return entidades.get(index);
    }
    
}
