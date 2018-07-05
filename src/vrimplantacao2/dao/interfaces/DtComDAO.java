package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Importacao
 */
public class DtComDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "DTCOM";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        return result;
    }  
}
