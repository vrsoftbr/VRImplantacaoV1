package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author Importacao
 */
public class KcmsDAO extends InterfaceDAO implements MapaTributoProvider {

    public String id_loja;
    
    @Override
    public String getSistema() {
        return "KCMS" + id_loja;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        return result;
    }
}
