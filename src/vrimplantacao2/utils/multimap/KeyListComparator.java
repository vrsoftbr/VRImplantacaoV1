package vrimplantacao2.utils.multimap;

import java.util.Comparator;

/**
 *
 * @author Leandro
 * @param <K>
 */
public class KeyListComparator<K extends Comparable> implements Comparator<KeyList<K>> {
    
    @Override
    public int compare(KeyList<K> o1, KeyList<K> o2) {
        if (o1 == null && o2 != null) {
            return -1;
        } else if (o1 != null && o2 == null) {
            return 1;
        } else if (o1 != null && o2 != null) {
            int comparacoes;
            if (o1.size() < o2.size()) {
                comparacoes = o1.size();
            } else {
                comparacoes = o2.size();
            }
            int result = 0;
            for (int i = 0; i < comparacoes; i++) {
                K item1 = o1.get(i);
                K item2 = o2.get(i);
                String strItem1 = item1.toString();
                String strItem2 = item2.toString();
                if (!"".equals(strItem1) && !"".equals(strItem2) && strItem1.matches("[\\d.]*") && strItem2.matches("[\\d.]*")) {
                    double dblItem1 = Double.parseDouble(strItem1);
                    double dblItem2 = Double.parseDouble(strItem2);
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
            return o1.size() - o2.size();
        } else {
            return 0;
        }
    }
    
}
