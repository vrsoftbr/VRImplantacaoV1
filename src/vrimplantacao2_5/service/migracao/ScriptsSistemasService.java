package vrimplantacao2_5.service.migracao;

import vrimplantacao2_5.dao.migracao.ScriptsSistemasDAO;

/**
 *
 * @author Desenvolvimento
 */
public class ScriptsSistemasService {

    private ScriptsSistemasDAO scriptSistemasDAO;

    public ScriptsSistemasService() {
        this.scriptSistemasDAO = new ScriptsSistemasDAO();
    }
    
    public ScriptsSistemasService(ScriptsSistemasDAO scriptSistemasDAO) {
        this.scriptSistemasDAO = scriptSistemasDAO;
    }
    
    public String getLojas(int idSistema, int idBancodados) throws Exception {
        return scriptSistemasDAO.getLojas(idSistema, idBancodados);
    }
    
}
