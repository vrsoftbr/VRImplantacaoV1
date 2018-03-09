package vrimplantacao2.dao.cadastro.nutricional;

import java.util.List;
import java.util.Set;
import vrimplantacao2.vo.importacao.NutricionalIMP;

/**
 *
 * @author Leandro
 */
public class NutricionalRepository {
    
    private NutricionalRepositoryProvider repository;

    public NutricionalRepository(NutricionalRepositoryProvider repository) {
        this.repository = repository;
    }

    public void importar(List<NutricionalIMP> nutricionais, Set<OpcaoNutricional> opt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
