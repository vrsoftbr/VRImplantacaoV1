package vrimplantacao2_5.controller.migracao;

import vrimplantacao2_5.service.migracao.ScriptsSistemasService;

/**
 *
 * @author Desenvolvimento
 */
public class ScriptsSistemasController {

    private ScriptsSistemasService scriptSistemasService;
    
    public String getLojas(int id_sistema, int id_bancodados) throws Exception {
        return scriptSistemasService.getLojas(id_sistema, id_bancodados);
    }
}
