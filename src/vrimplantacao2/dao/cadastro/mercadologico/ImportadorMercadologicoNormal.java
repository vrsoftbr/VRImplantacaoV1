package vrimplantacao2.dao.cadastro.mercadologico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

class ImportadorMercadologicoNormal {

    private final MercadologicoRepository repository;

    ImportadorMercadologicoNormal(MercadologicoRepository repository) {
        this.repository = repository;
    }

    void salvar(List<MercadologicoIMP> mercadologicos, Set<OpcaoProduto> opt) throws Exception {
        Map<MercadologicoKey, MercadologicoIMP> mercs = filtrarRepetidos(mercadologicos);
        mercadologicos = null;
        
        List<MercadologicoNivelIMP> resultados = new ArrayList<>();
        
        Map<MercadologicoKey, MercadologicoNivelIMP> nivel1 = filtrarPorNivel(mercs, 1);
        Map<MercadologicoKey, MercadologicoNivelIMP> nivel2 = filtrarPorNivel(mercs, 2);
        Map<MercadologicoKey, MercadologicoNivelIMP> nivel3 = filtrarPorNivel(mercs, 3);
        Map<MercadologicoKey, MercadologicoNivelIMP> nivel4 = filtrarPorNivel(mercs, 4);
        Map<MercadologicoKey, MercadologicoNivelIMP> nivel5 = filtrarPorNivel(mercs, 5);
        
        relacionarMercadologicosFilhosAosPais(nivel5, nivel4, 4);
        relacionarMercadologicosFilhosAosPais(nivel4, nivel3, 3);
        relacionarMercadologicosFilhosAosPais(nivel3, nivel2, 2);
        relacionarMercadologicosFilhosAosPais(nivel2, nivel1, 1); 
        
        resultados.addAll(new ArrayList<>(nivel1.values()));
        
        repository.salvar(resultados, opt);
    }
    
    void relacionarMercadologicosFilhosAosPais(Map<MercadologicoKey, MercadologicoNivelIMP> mercadologicosFilho, Map<MercadologicoKey, MercadologicoNivelIMP> mercadologicosPai, int nivelPai) {
        for (Map.Entry<MercadologicoKey, MercadologicoNivelIMP> entry: mercadologicosFilho.entrySet()) {
            String[] keyPai = Arrays.copyOf(entry.getKey().getKey(), nivelPai);
            MercadologicoNivelIMP pai = mercadologicosPai.get(MercadologicoKey.filterAndBuildKey(keyPai));
            if (pai != null) {
                pai.addFilho(entry.getValue());
            }
        }
    }

    Map<MercadologicoKey, MercadologicoIMP> filtrarRepetidos(List<MercadologicoIMP> mercadologicos) {
        Map<MercadologicoKey, MercadologicoIMP> result = new LinkedHashMap<>();
        for (MercadologicoIMP imp: mercadologicos) {
            MercadologicoKey key = MercadologicoKey.filterAndBuildKey(
                    imp.getMerc1ID(),
                    imp.getMerc2ID(),
                    imp.getMerc3ID(),
                    imp.getMerc4ID(),
                    imp.getMerc5ID()
            );
            result.put(key, imp);
        }
        return result;
    }

    Map<MercadologicoKey, MercadologicoNivelIMP> filtrarPorNivel(Map<MercadologicoKey, MercadologicoIMP> mercadologicos, int nivelDeCorte) {
        if (nivelDeCorte > 5)
            nivelDeCorte = 5;
        if (nivelDeCorte < 1)
            nivelDeCorte = 1;
        Map<MercadologicoKey, MercadologicoNivelIMP> result = new TreeMap<>();
        
        nivel1:
        for (Map.Entry<MercadologicoKey, MercadologicoIMP> entry: mercadologicos.entrySet()) {
            
            final boolean nivelDaChaveMenorQueNecessario = entry.getKey().length() < nivelDeCorte;
            
            if (nivelDaChaveMenorQueNecessario)
                continue;
            
            String[] oldKey = entry.getKey().getKey();
            String[] newKey = Arrays.copyOf(oldKey, nivelDeCorte);
            
            MercadologicoIMP original = entry.getValue();
            MercadologicoNivelIMP convertido = this.converterMercadologico(original, nivelDeCorte);
            
            result.put(MercadologicoKey.filterAndBuildKey(newKey), convertido);
        }
        
        return result;
    }
    
    MercadologicoNivelIMP converterMercadologico(MercadologicoIMP value, int nivel) {
        if (nivel < 1)
            nivel = 1;
        if (nivel > 5)
            nivel = 5;
        MercadologicoNivelIMP convertido = new MercadologicoNivelIMP();
        switch(nivel) {
            case 1:
                convertido.setId(value.getMerc1ID());
                convertido.setDescricao(value.getMerc1Descricao());
                break;
            case 2:
                convertido.setId(value.getMerc2ID());
                convertido.setDescricao(value.getMerc2Descricao());
                break;
            case 3:
                convertido.setId(value.getMerc3ID());
                convertido.setDescricao(value.getMerc3Descricao());
                break;
            case 4:
                convertido.setId(value.getMerc4ID());
                convertido.setDescricao(value.getMerc4Descricao());
                break;
            case 5:
                convertido.setId(value.getMerc5ID());
                convertido.setDescricao(value.getMerc5Descricao());
                break;
        }
        return convertido;
    }
    
}

class MercadologicoKey implements Comparable<MercadologicoKey>{
    private final String[] key;

    public String[] getKey() {
        return key;
    }
    
    public static MercadologicoKey filterAndBuildKey(String... key) {
        String[] arr = new String[5];
        int cont = 0;
        for (String k: key) {
            if (k == null || "".equals(k.trim()))
                break;
            arr[cont] = k;
            cont++;
        }
        return new MercadologicoKey(Arrays.copyOf(arr, cont));
    }

    private MercadologicoKey(String... key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Arrays.deepHashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MercadologicoKey other = (MercadologicoKey) obj;
        return Arrays.deepEquals(this.key, other.key);
    }

    @Override
    public int compareTo(MercadologicoKey o) {
        int size = Math.min(this.key.length, o.key.length);
        for (int i = 0; i < size; i++) {
            String left = this.key[i];
            String right = o.key[i];
            if (left == null && right == null)
                return 0;
            if (left == null && right != null)
                return -1;
            int comparacao = left.compareTo(right);
            if (comparacao != 0)
                return comparacao;
        }
        return 0;
    }

    public int length() {
        return this.key.length;
    }

}