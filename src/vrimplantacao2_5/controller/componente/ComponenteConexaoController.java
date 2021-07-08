package vrimplantacao2_5.controller.componente;

import vrframework.classe.VRException;
import vrimplantacao2_5.gui.componente.conexao.DriverConexao;
import vrimplantacao2_5.service.componente.ComponenteConexaoService;
import vrimplantacao2_5.vo.enums.EBancoDados;

/**
 *
 * @author guilhermegomes
 */
public class ComponenteConexaoController {
    
    private final ComponenteConexaoService componenteService;

    public ComponenteConexaoController() {
        this.componenteService = new ComponenteConexaoService();
    }
    
    public DriverConexao getConexao(EBancoDados eBD) throws VRException {
        return componenteService.getConexao(eBD);
    }
    
}
