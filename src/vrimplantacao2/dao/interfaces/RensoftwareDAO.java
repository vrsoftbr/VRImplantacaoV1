package vrimplantacao2.dao.interfaces;

import java.util.List;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author leandro
 */
public class RensoftwareDAO extends InterfaceDAO {

    private String complemento = "";
    
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Rensoftware";
        } else {
            return "Rensoftware - " + complemento;
        }
    }

    public List<Estabelecimento> getLojaCliente() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
