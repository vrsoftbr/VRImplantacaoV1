package vrimplantacao2_5.controller.cadastro;

import vrimplantacao2_5.service.cadastro.BancoDadosService;

/**
 *
 * @author Desenvolvimento
 */
public class BancoDadosController {
    
    private final BancoDadosService bancoDadosService;
    
    public BancoDadosController() {
        this.bancoDadosService = new BancoDadosService();
    }
    
    public BancoDadosController(BancoDadosService bancoDadosService) {
        this.bancoDadosService = bancoDadosService;
    }
    
    public void salvar(String nome) throws Exception {
        bancoDadosService.salvar(nome);
    }
}
