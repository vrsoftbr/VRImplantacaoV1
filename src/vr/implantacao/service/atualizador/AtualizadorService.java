package vr.implantacao.service.atualizador;

/**
 *
 * @author Desenvolvimento
 */

import java.util.ArrayList;
import java.util.List;
import vr.implantacao.dao.atualizador.AtualizadorDAO;
import vr.implantacao.vo.enums.EBancoDados;
import vr.implantacao.vo.enums.ESistema;

public class AtualizadorService {

    private AtualizadorDAO atualizadorDAO;

    public AtualizadorService() {
        this.atualizadorDAO = new AtualizadorDAO();
    }

    public AtualizadorService(AtualizadorDAO atualizadorDAO) {
        this.atualizadorDAO = atualizadorDAO;
    }

    public List<EBancoDados> verificarBancoDados() throws Exception {
        List<EBancoDados> result = new ArrayList<>();
        for (EBancoDados eBancoDados : EBancoDados.values()) {
            if (!this.atualizadorDAO.verificarBancoDados(eBancoDados)) {
                result.add(eBancoDados);
            }
        }
        return result;
    }
    
    public List<ESistema> verificarSistema() throws Exception {
        List<ESistema> result = new ArrayList<>();
        for (ESistema eSistema : ESistema.values()) {
            if (!this.atualizadorDAO.verificarSistema(eSistema)) {
                result.add(eSistema);
            }
        }
        return result;
    }
    
    public void criarSchema() throws Exception {
        this.atualizadorDAO.criarSchema();
    }

    public void criarTabelas() throws Exception {
        this.atualizadorDAO.criarTabelas();
    }
    
    public void inserirTabelaBancoDados() throws Exception {
        List<EBancoDados> eBancoDados = verificarBancoDados();
        for (EBancoDados eBancoDado : eBancoDados) {
            this.atualizadorDAO.inserirTabelaBancoDados(eBancoDado);
        }        
    }
    
    public void inserirTabelaSistema() throws Exception {
        List<ESistema> eSistemas = verificarSistema();
        for (ESistema eSistema : eSistemas) {
            this.atualizadorDAO.inserirTabelaSistema(eSistema);
        }        
    }
    
    public void inserirTabelaSistemaBancoDados() throws Exception {
        this.atualizadorDAO.inserirTabelaSistemaBancoDados();
    }
    
    public void criarEstrutura2_5() throws Exception {
        this.atualizadorDAO.criarSchema();
        this.atualizadorDAO.criarTabelas();
        this.inserirTabelaBancoDados();
        this.inserirTabelaSistema();
    }
}
