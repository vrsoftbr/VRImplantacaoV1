package vrimplantacao2_5.controller.migracao;

import vrimplantacao2_5.service.migracao.ScriptsSistemasService;

/**
 *
 * @author Desenvolvimento
 */
public class ScriptsSistemasController {

    private ScriptsSistemasService scriptSistemasService;
    
    public String getLojas(int idSistema, int idBancodados) throws Exception {
        return scriptSistemasService.getLojas(idSistema, idBancodados);
    }
}
