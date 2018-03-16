package vrimplantacao2.utils.collection;

import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * Pilha utilizada para ordenar e trabalhar com IDs vagos, fornecendo sempre
 * o menor id disponível.
 * @author Leandro
 */
public class IDStack {
    
    private static final Logger LOG = Logger.getLogger(IDStack.class.getName());
    
    private final long intervalo = 10000;
    private final SortedMap<Long, SortedSet<Long>> stacks = new TreeMap<>();
    private int size = 0;

    public IDStack() {}
    public IDStack(long... ids) {
        for (long id: ids) {
            add(id);
        }
    }
    

    /**
     * Inclui, já ordenando, um ID vago na pilha.
     * @param id ID a ser incluso.
     */
    public final void add(long id) {
        SortedSet<Long> stack = getStack(id);        
        if (stack == null) {
            Long ultimo = !stacks.isEmpty() ? stacks.lastKey() : 0;
            Long proximo = ultimo;
            do {
                proximo += intervalo;
                stacks.put(proximo, new TreeSet<Long>(Collections.reverseOrder()));
            } while (id > proximo);
            stack = stacks.get(proximo);
        }
        if (stack.add(id)) {
            size++;
        }
    }
    
    /**
     * Se o ID informado estiver disponível para uso na pilha, retorna esse id
     * e o remove da pilha de disponíveis, senão retorna o menor ID disponível.
     * @param strId ID para verificar disponíbilidade.
     * @return 
     */
    public long pop(String strId) {
        StringBuilder string = new StringBuilder("------------------------\n");
        try {
            string.append("IDSTR: ").append(strId).append("\n");
            long id = Long.parseLong(strId);
            if (id <= 999999) {
                string.append("999999 <= TRUE\n");
                if (getStack(id).contains(id)) {
                    string.append("ID Disponível\n");
                    this.remove(id);
                    return id;
                }
            }
            return this.pop();
        } catch (NumberFormatException e) {
            return this.pop();
        } finally {
            string.append("------------------------");
            LOG.finest(string.toString());
        }
    }
    
    /**
     * Extrai da pilha o menor ID disponível.
     * @return Menor ID disponível.
     * @exception RuntimeException Caso a pilha esteja vazia retorna este erro.
     */
    public long pop() {
        if (stacks.isEmpty()) {
            throw new RuntimeException("Pilha de IDs vazia.");
        }
        SortedSet<Long> stack = null;
        for (Long key: stacks.keySet()) {
            SortedSet<Long> aux = stacks.get(key);
            if (!aux.isEmpty()) {
                stack = aux;
                break;
            }
        }
        if (stack != null) {
            long id = stack.last();
            if (stack.remove(id)) {
                size--;
            }
            return id;
        }
        throw new RuntimeException("Pilha de IDs vazia.");
    }
    
    /**
     * Remove o valor da pilha se existir.
     * @param id ID a ser removido.
     */
    public void remove(long id) {
        SortedSet<Long> stack = getStack(id);
        if (stack != null) {
            if (stack.remove((Long) id)) {
                size--;
            }
        }
    }

    /**
     * Retorna a pilha correspondente ao valor informado.<br>
     * Exemplo.<br>
     * Se id == 8693 então será retornada a pilha até 10000.
     * @param id ID utilizado para localizar a pilha.
     * @return Retorna a pilha correspondente ao valor informado.
     */
    private SortedSet<Long> getStack(long id) {
        for (Long stackTop: stacks.keySet()) {
            if (id <= stackTop) {
                return stacks.get(stackTop);
            }
        }
        return null;
    }
    
    /**
     * Limpa todos os registros da pilha
     */
    public void clear() {
        for (SortedSet<Long> stack: stacks.values()) {
            stack.clear();
        }
        stacks.clear();
        size = 0;
    }

    /**
     * Retorna a quantidade de IDs armazenada no {@link IDStack}
     * @return 
     */
    public int size() {
        return this.size;
    }
    
    /**
     * Verifica se a pilha está vazia.
     * @return True se estiver vazia.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }
}
