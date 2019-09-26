package vrimplantacao2.dao.interfaces;

import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author leandro
 */
public class OrionTechDAO extends InterfaceDAO {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null || complemento.trim().equals("") ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        if (!"".equals(this.complemento)) {
            return "OrionTech - " + complemento;
        } else {
            return "OrionTech";
        }
    }

    public Iterable<Estabelecimento> getLojasCliente() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
