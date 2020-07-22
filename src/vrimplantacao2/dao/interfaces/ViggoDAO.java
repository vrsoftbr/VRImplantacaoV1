package vrimplantacao2.dao.interfaces;

import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author leandro
 */
public class ViggoDAO extends InterfaceDAO {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "VIGGO" + (!complemento.equals("") ? " - " + complemento : "");
    }

    public Iterable<Estabelecimento> getLojas() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
