package vrimplantacao2_5.service.migracao;

import vrframework.classe.VRException;
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
        String sql = scriptSistemasDAO.getLojas(idSistema, idBancodados);
        
        if (sql.isEmpty()) {
            throw new VRException("Script de consulta loja origem não configurado!\n"
                    + "Verifique com o administrador do sistema!");
        }
        
        return sql;
    }
    
}
