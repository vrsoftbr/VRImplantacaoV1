package vrimplantacao2_5.controller.atualizador;

import java.util.List;
import vrimplantacao2_5.service.atualizador.AtualizadorService;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author Desenvolvimento
 */
public class AtualizadorController {

    AtualizadorService atualizadorService = new AtualizadorService();
    
    public void criarSchema() throws Exception {
        this.atualizadorService.criarSchema();
    }

    public List<EBancoDados> getBancoDados() throws Exception {
        return this.atualizadorService.getBancoDados();
    }
    
    public List<ESistema> getSistema() throws Exception {
        return this.atualizadorService.getSistema();
    }
    
    public void criarTabelas() throws Exception {
        this.atualizadorService.criarTabelas();
    }
    
    public void salvarBancoDados() throws Exception {
        this.atualizadorService.salvarBancoDados();
    }
    
    public void salvarSistema() throws Exception {
        this.atualizadorService.salvarSistema();
    }
    
    public void salvarSistemaBancoDados() throws Exception {
        this.atualizadorService.salvarSistemaBancoDados();
    }
    
    public void criarEstrutura2_5() throws Exception {
        this.atualizadorService.criarEstrutura2_5();
    }

}
