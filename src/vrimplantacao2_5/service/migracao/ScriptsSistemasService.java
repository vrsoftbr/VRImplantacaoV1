package vrimplantacao2_5.service.migracao;

import vrimplantacao2_5.dao.migracao.ScriptsSistemasDAO;

/**
 *
 * @author Desenvolvimento
 */
public class ScriptsSistemasService {

    private ScriptsSistemasDAO scriptSistemasDAO;
    
    public String getLojas(int id_sistema, int id_bancodados) throws Exception {
        return scriptSistemasDAO.getLojas(id_sistema, id_bancodados);
    }
    
}
