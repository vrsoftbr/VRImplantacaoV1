package vrimplantacao2.utils.multimap;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import vrimplantacao2.utils.Factory;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;

/**
 * Mapa multi níveis.
 * @author LeandroCaires
 * @param <K> Classe das key utilizadas para localizar o objeto.
 * @param <T> Classe dos objetos armazenados.
 */
public class MultiMap<K extends Comparable, T> {
    
    private Factory<T> factory;
    private final int limite;    
    private final Map<KeyList<K>, T> root = new LinkedHashMap<>();   
    
    /**
     * Retorna true se o map estiver vazio.
     * @return true se o map estiver vazio ou false caso contrário.
     */
    public boolean isEmpty() {
        return root.isEmpty();
    }

    /**
     * Retorna true ou false se o map contêm a chave informada.
     * @param keys Chaves utilizadas para localizar o arquivo.
     * @return true ou false se o map contêm a chave informada.
     */
    public boolean containsKey(K... keys) {
        return containsKey(new KeyList<>(keys));
    }

    /**
     * Retorna true ou false se o map contêm a chave informada.
     * @param keys Chaves utilizadas para localizar o arquivo.
     * @return true ou false se o map contêm a chave informada.
     */
    public boolean containsKey(KeyList<K> keys) {
        return root.containsKey(keys);
    }

    /**
     * Verifica se o objeto informado existe no map e retorna true caso sim e
     * false caso não exista.
     * @param value Objeto a ser localizado.
     * @return true caso sim e false caso não exista.
     */
    public boolean containsValue(T value) {
        return root.containsValue(value);
    }

    /**
     * Retorna o objeto relacionados com as chaves informadas ou null caso não
     * encontre nenhum.
     * @param key Chave para localizar o objeto.
     * @return O objeto armazenado na chave ou null caso não encontre nenhum.
     */
    public T get(K... key) {
        return root.get(new KeyList<>(key));
    }
    /**
     * Retorna o objeto relacionados com as chaves informadas ou null caso não
     * encontre nenhum.
     * @param keys Chave para localizar o objeto.
     * @return O objeto armazenado na chave ou null caso não encontre nenhum.
     */
    public T get(KeyList<K> keys) {
        return root.get(keys);
    }

    /**
     * Excluí a chave informada.
     * @param key Chave a ser excluída.
     * @return Objeto excluído ou null se não for excluído nada.
     */
    public T remove(K... key) {
        return root.remove(new KeyList<>(key));
    }

    /**
     * Elimina todas as chaves da listagem.
     */
    public void clear() {
        root.clear();
    }

    /**
     * Retorna um {@link Set} com todas as chaves do map.
     * @return {@link Set} com as chaves armazenadas.
     */
    public Set<KeyList<K>> keySet() {
        return root.keySet();
    }

    /**
     * Retorna uma {@link Collection} com todos os objetos armazenados no map.
     * @return Collection com todos os objetos.
     */
    public Collection<T> values() {
        return root.values();
    }
    
    public MultiMap() {
        this(0);
    }
    
    /**
     * Construtor da classe que permite determinar quantas chaves cada registro
     * do map deve ter para ser aceito ao ser incluso.
     * @param limite Quantidade de chaves.
     */
    public MultiMap(int limite) {
        if (limite < 0) {
            limite = 1;
        }
        this.limite = limite;
    }
    
    /**
     * Instancia uma nova lista e informa uma {@link Factory} para criar novos
     * objetos.
     * @param factory {@link Factory} responsável por criar novas instancias dos objetos.
     */
    public MultiMap(Factory<T> factory) {
        this();
        this.factory = factory;
    }
    
    /**
     * Instancia uma nova lista e informa uma {@link Factory} para criar novos
     * objetos.
     * @param factory {@link Factory} responsável por criar novas instancias dos objetos.
     * @param limite Quantidade de chaves.
     */
    public MultiMap(Factory<T> factory, int limite) {
        this(limite);
        this.factory = factory;
    }
    
    /**
     * <b>Inclui um objeto no map e o posiciona conforme as chaves informadas.</b> Se
     * ao instanciar a classe for informado um valor de limite maior que zero,
     * então a quantidade de chaves informadas deve ser igual ao valor limite
     * passado.
     * @param object Objeto a ser incluso no map.
     * @param keys Chaves que definirão como o objeto será posicionado.
     * @throws MultiMapException "Quantidade de chaves menor ou maior que o limite informado!"
     */
    public void put(T object, K... keys) {
        if (limite == 0 || keys.length == limite) {
            root.put(new KeyList<>(keys), object);
        } else {
            throw new MultiMapException("Quantidade de chaves menor ou maior que o limite informado!");
        }
    } 
    /**
     * <b>Inclui um objeto no map e o posiciona conforme as chaves informadas.</b> Se
     * ao instanciar a classe for informado um valor de limite maior que zero,
     * então a quantidade de chaves informadas deve ser igual ao valor limite
     * passado.
     * @param object Objeto a ser incluso no map.
     * @param keys Chaves que definirão como o objeto será posicionado.
     * @throws MultiMapException "Quantidade de chaves menor ou maior que o limite informado!"
     */
    public void put(T object, KeyList<K> keys) {
        if (limite == 0 || keys.size() == limite) {
            root.put(keys, object);
        } else {
            throw new MultiMapException("Quantidade de chaves menor ou maior que o limite informado!");
        }
    } 
    
    /**
     * Retorna a quantidade de registros no map.
     * @return quantidade de registros.
     */
    public int size() {
        return root.size();
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
     * @param keys Chaves que definirão como o objeto será posicionado.
     * @return Objeto fabricado pela {@link Factory}.
     */
    public T make(K... keys) {
        if (factory != null) {
            T a = factory.make();
            put(a, keys);
            return a;
        } else {
            throw new RuntimeException("Não foi definida nenhuma factory para o MultiMap");
        }
    }
    
    /**
     * Instancia um novo objeto, inclui na lista e o retorna.
     * @param keys Chaves que definirão como o objeto será posicionado.
     * @return Objeto fabricado pela {@link Factory}.
     */
    public T make(KeyList<K> keys) {
        if (factory != null) {
            T a = factory.make();
            put(a, keys);
            return a;
        } else {
            throw new RuntimeException("Não foi definida nenhuma factory para o MultiMap");
        }
    }

    public void put(FornecedorVO vo, String cnpj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public FornecedorVO get(String cnpj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static class MultiMapEntry<K extends Comparable,T> {
        KeyList<K> key;
        T value;

        public MultiMapEntry(KeyList<K> key, T value) {
            this.key = key;
            this.value = value;
        }
        
    }
    
    public MultiMap<K, T> getSortedMap() {
        TreeMap<KeyList<K>, MultiMapEntry<K, T>> ordernado = new TreeMap<>();

        for (KeyList<K> keys: root.keySet()) {
            T value = root.get(keys);
            ordernado.put(keys, new MultiMapEntry<>(keys, value));
        }        
        
        MultiMap<K, T> result = new MultiMap<>(limite);
        for (MultiMapEntry<K, T> entry: ordernado.values()) {
            T value = entry.value;
            KeyList<K> key = entry.key;
            result.put(value, key);
        }
        
        return result;
    }
    
    
    
    
}
