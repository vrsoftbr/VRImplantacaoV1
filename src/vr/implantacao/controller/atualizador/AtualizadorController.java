package vr.implantacao.controller.atualizador;

import vr.implantacao.service.atualizador.AtualizadorService;

/**
 *
 * @author Desenvolvimento
 */
public class AtualizadorController {

    AtualizadorService atualizadorService = new AtualizadorService();
    
    public void criarSchema() throws Exception {
        this.atualizadorService.criarSchema();
    }

    public void criarTabelas() throws Exception {
        this.atualizadorService.criarTabelas();
    }
    
    public void inserirTabelaBancoDados() throws Exception {
        this.atualizadorService.inserirTabelaBancoDados();
    }
    
    public void inserirTabelaSistema() throws Exception {
        this.atualizadorService.inserirTabelaSistema();
    }
    
    public void inserirTabelaSistemaBancoDados() throws Exception {
        this.atualizadorService.inserirTabelaSistemaBancoDados();
    }
    
    public void criarEstrutura() throws Exception {
        this.atualizadorService.criarEstrutura();
    }

}
