package vr.implantacao.controller.atualizador;

import java.util.List;
import vr.implantacao.service.atualizador.AtualizadorService;
import vr.implantacao.vo.enums.EBancoDados;
import vr.implantacao.vo.enums.ESistema;

/**
 *
 * @author Desenvolvimento
 */
public class AtualizadorController {

    AtualizadorService atualizadorService = new AtualizadorService();
    
    public void criarSchema() throws Exception {
        this.atualizadorService.criarSchema();
    }

    public List<EBancoDados> verificarBancoDados() throws Exception {
        return this.atualizadorService.verificarBancoDados();
    }
    
    public List<ESistema> verificarSistema() throws Exception {
        return this.atualizadorService.verificarSistema();
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
    
    public void criarEstrutura2_5() throws Exception {
        this.atualizadorService.criarEstrutura2_5();
    }

}
