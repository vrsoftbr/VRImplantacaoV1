package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Importacao
 */
public class OryonDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Oryon";
    }
    
    public List<Estabelecimento> getLojaCliente() {
        return new ArrayList<>(Arrays.asList(new Estabelecimento("1", "SUPERMERCADO ANDREA")));
    }
}
