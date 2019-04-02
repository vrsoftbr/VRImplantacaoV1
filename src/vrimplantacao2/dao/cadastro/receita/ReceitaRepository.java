/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.receita;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrimplantacao2.vo.cadastro.receita.ReceitaAnteriorVO;
import vrimplantacao2.vo.importacao.ReceitaIMP;

/**
 *
 * @author lucasrafael
 */
public class ReceitaRepository {
    
    private static final Logger LOG = Logger.getLogger(ReceitaBalancaRepository.class.getName());
    private final ReceitaRepositoryProvider provider;
    
    public ReceitaRepository(ReceitaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public void importar(List<ReceitaIMP> receita)  throws Exception {
        provider.setMessage("Receita...Carregando dados...");
        
        Map<String, Integer> produtos = provider.getProdutos();
        Map<String, ReceitaAnteriorVO> anteriores = provider.getAnteriores();
    }
}
