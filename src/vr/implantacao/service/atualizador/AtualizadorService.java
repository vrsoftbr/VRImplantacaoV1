package vr.implantacao.service.atualizador;

/**
 *
 * @author Desenvolvimento
 */

import vr.implantacao.dao.atualizador.AtualizadorDAO;

public class AtualizadorService {

    private AtualizadorDAO atualizadorDAO;

    public AtualizadorService() {
        this.atualizadorDAO = new AtualizadorDAO();
    }

    public AtualizadorService(AtualizadorDAO atualizadorDAO) {
        this.atualizadorDAO = atualizadorDAO;
    }

    public void criarSchema() throws Exception {
        this.atualizadorDAO.criarSchema();
    }

    public void criarTabelas() throws Exception {
        this.atualizadorDAO.criarTabelas();
    }
    
    public void inserirTabelaBancoDados() throws Exception {
        this.atualizadorDAO.inserirTabelaBancoDados();
    }
    
    public void inserirTabelaSistema() throws Exception {
        this.atualizadorDAO.inserirTabelaSistema();
    }
    
    public void inserirTabelaSistemaBancoDados() throws Exception {
        this.atualizadorDAO.inserirTabelaSistemaBancoDados();
    }
    
    public void criarEstrutura() throws Exception {
        this.atualizadorDAO.criarEstrutura();
    }
}
