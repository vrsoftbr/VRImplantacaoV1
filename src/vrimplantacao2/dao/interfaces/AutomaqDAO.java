package vrimplantacao2.dao.interfaces;

import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Leandro
 */
public class AutomaqDAO extends InterfaceDAO {

    private String complemento = "";
    
    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Automaq";
        } else {
            return "Automaq(" + complemento + ")";
        }
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public Iterable<Estabelecimento> getLojas() {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
}
