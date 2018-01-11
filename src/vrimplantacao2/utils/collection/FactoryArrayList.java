package vrimplantacao2.utils.collection;

import java.util.ArrayList;
import java.util.Collection;
import vrimplantacao2.utils.Factory;

/**
 * Listagem de objetos que extende de {@link ArrayList} e possui a capacidade
 * de fabricar objetos. Sua função é facilitar o manuseio da listagem.
 * @author Leandro
 * @param <T> Tipo do objeto.
 */
public class FactoryArrayList<T> extends ArrayList<T>{

    private Factory<T> factory;

    //<editor-fold defaultstate="collapsed" desc="Construtores ocultos para evitar instanciação incorreta">
    private FactoryArrayList() {
        super();
    }
    
    private FactoryArrayList(Collection<? extends T> c) {
        super(c);
    }
    
    private FactoryArrayList(int initialCapacity) {
        super(initialCapacity);
    }
    
    private FactoryArrayList(Factory<T> factory, Collection<? extends T> c) {
        super(c);
        this.factory = factory;
    }
    
    private FactoryArrayList(Factory<T> factory, int initialCapacity) {
        super(initialCapacity);
        this.factory = factory;
    }
    //</editor-fold>
    
    /**
     * Instancia uma nova lista e informa uma {@link Factory} para criar novos
     * objetos.
     * @param factory {@link Factory} responsável por criar novas instancias dos objetos.
     */
    public FactoryArrayList(Factory<T> factory) {
        super();
        this.factory = factory;
    }

    /**
     * Seta a {@link Factory} da lista.
     * @param factory {@link Factory} a ser inclusa.
     */
    public void setFactory(Factory<T> factory) {
        this.factory = factory;
    }

    /**
     * Instancia um novo objeto, inclui na lista e o retorna.
     * @return Objeto fabricado pela {@link Factory}.
     */
    public T add() {
        T a = factory.make();
        add(a);
        return a;
    }
    
    
    
}
