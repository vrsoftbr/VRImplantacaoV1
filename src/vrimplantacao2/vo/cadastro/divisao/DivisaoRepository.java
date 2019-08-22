package vrimplantacao2.vo.cadastro.divisao;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao2.vo.importacao.DivisaoIMP;
import static java.util.Map.Entry;
import vrimplantacao2.utils.collection.IDStack;

/**
 *
 * @author Leandro
 */
public class DivisaoRepository {
    
    private final DivisaoRepositoryProvider provider;    
    private Map<String, Entry<String, Integer>> anteriores;
    private IDStack idsVagos;

    public DivisaoRepository(DivisaoRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importar(List<DivisaoIMP> divisoes) throws Exception {
        this.provider.begin();
        try {
            this.provider.notificar("Carregando as informações sobre as divisões");

            //Remove os duplicados.
            filtrar(divisoes);

            //Obtendo os anteriores
            anteriores = provider.getAnteriores();
            idsVagos = provider.getIdsVagos();

            this.provider.notificar("Gravando as divisões", divisoes.size());
            //Processa as divisões
            for (DivisaoIMP imp: divisoes) {

                Entry<String, Integer> anterior = anteriores.get(imp.getId());
                if (anterior == null) {
                    incluirDivisao(imp);
                }

                this.provider.notificar();
            }
            this.provider.commit();
        } catch (Exception ex) {
            this.provider.rollback();
            throw ex;
        }
    }

    private void incluirDivisao(DivisaoIMP imp) throws Exception {
        //Incluir um item novo
        //Converte o novo item
        DivisaoFornecedorVO vo = new DivisaoFornecedorVO();
        vo.setId((int) idsVagos.pop());
        vo.setDescricao(imp.getDescricao());
        
        provider.salvar(vo); //Grava no banco e obtem o ID;
        
        //Grava o anterior
        Entry<String, Integer> anterior = new AbstractMap.SimpleEntry<>(imp.getId(), vo.getId());
        provider.salvar(anterior);
        anteriores.put(anterior.getKey(), anterior);
    }

    private void filtrar(List<DivisaoIMP> divisoes) {
        Map<String, DivisaoIMP> filtro = new LinkedHashMap<>();
        
        //Eliminando duplicados
        for (DivisaoIMP imp: divisoes) {
            filtro.put(imp.getId(), imp);
        }
        
        divisoes.clear();
        divisoes.addAll(filtro.values());
        
        //Limpa a memória
        filtro.clear();
        System.gc();
    }
    
    
}
