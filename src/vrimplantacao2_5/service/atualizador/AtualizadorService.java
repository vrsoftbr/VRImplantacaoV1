package vrimplantacao2_5.service.atualizador;

/**
 *
 * @author Desenvolvimento
 */

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2_5.dao.atualizador.AtualizadorDAO;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;

public class AtualizadorService {

    private AtualizadorDAO atualizadorDAO;

    public AtualizadorService() {
        this.atualizadorDAO = new AtualizadorDAO();
    }

    public AtualizadorService(AtualizadorDAO atualizadorDAO) {
        this.atualizadorDAO = atualizadorDAO;
    }

    public boolean verificarBancoDados(EBancoDados eBancoDados) throws Exception {
        return this.atualizadorDAO.verificarBancoDados(eBancoDados);
    }

    public List<EBancoDados> getBancoDados() throws Exception {
        List<EBancoDados> result = new ArrayList<>();
        for (EBancoDados eBancoDados : EBancoDados.values()) {
            if (!verificarBancoDados(eBancoDados)) {
                result.add(eBancoDados);
            }
        }
        return result;
    }
    
    public boolean verificarSistema(ESistema eSistema) throws Exception {
        return this.atualizadorDAO.verificarSistema(eSistema);
    }
    
    public List<ESistema> getSistema() throws Exception {
        List<ESistema> result = new ArrayList<>();
        for (ESistema eSistema : ESistema.values()) {
            if (!verificarSistema(eSistema)) {
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
        List<EBancoDados> eBancoDados = getBancoDados();
        for (EBancoDados eBancoDado : eBancoDados) {
            this.atualizadorDAO.inserirTabelaBancoDados(eBancoDado);
        }        
    }
    
    public void inserirTabelaSistema() throws Exception {
        List<ESistema> eSistemas = getSistema();
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
        this.atualizadorDAO.inserirTabelaSistemaBancoDados();
    }
}
