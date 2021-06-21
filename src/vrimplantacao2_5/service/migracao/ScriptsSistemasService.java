package vrimplantacao2_5.service.migracao;

import vrimplantacao2_5.dao.migracao.ScriptsSistemasDAO;

/**
 *
 * @author Desenvolvimento
 */
public class ScriptsSistemasService {

    private ScriptsSistemasDAO scriptSistemasDAO;
    
    public String getLojas(int idSistema, int idBancodados) throws Exception {
        return scriptSistemasDAO.getLojas(idSistema, idBancodados);
    }
    
}
