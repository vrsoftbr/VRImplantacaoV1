package vrimplantacao2.utils.multimap;

import java.util.Arrays;

/**
 * Lista que armazena as chaves de cada registro.
 * @param <E> Tipo da chave
 */
public class KeyList<E extends Comparable> implements Comparable {
    
    private E[] itens;

    public KeyList(E... array) {
        this.itens = array;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            throw new RuntimeException("Objeto [o] não pode ser null");
        } else if (o instanceof KeyList) {
            KeyList other = (KeyList) o;
            int comparacoes;
            if (this.size() < other.size()) {
                comparacoes = this.size();
            } else {
                comparacoes = other.size();
            }
            int result = 0;
            for (int i = 0; i < comparacoes; i++) {
                String strItem1 = this.get(i).toString();
                String strItem2 = other.get(i).toString();

                if (!"".equals(strItem1) && !"".equals(strItem2) && strItem1.matches("[0-9]{1,3}(((,)[0-9]{3})|[0-9])*((\\.)[0-9]+)?") && strItem2.matches("[0-9]{1,3}(((,)[0-9]{3})|[0-9])*((\\.)[0-9]+)?")) {
                    double dblItem1 = Double.parseDouble(strItem1.replace(",", ""));
                    double dblItem2 = Double.parseDouble(strItem2.replace(",", ""));
                    if (dblItem1 == dblItem2) {
                        result = strItem1.compareTo(strItem2);
                    } else {
                        return Double.compare(dblItem1, dblItem2);
                    }
                } else {
                    result = strItem1.compareTo(strItem2);
                }
                if (result != 0) {
                    return result;
                }
            }
            return this.size() - other.size();
        } else {
            throw new RuntimeException("Objeto [" + o.toString() + "] não é um KeyList");
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + java.util.Arrays.deepHashCode(this.itens);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KeyList<?> other = (KeyList<?>) obj;
        return java.util.Arrays.deepEquals(this.itens, other.itens);
    }

    public E get(int index) {
        return this.itens[index];
    }
    
    public int size() {
        return this.itens.length;
    }
    
    public E[] toArray() {
        return this.itens;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.itens);
    }
    
}
