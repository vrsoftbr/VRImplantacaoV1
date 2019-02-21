package vrimplantacao2.dao.interfaces;

import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Leandro
 */
public class OpenDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Open";
    }

    public Iterable<Estabelecimento> getLojasCliente() {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
}
