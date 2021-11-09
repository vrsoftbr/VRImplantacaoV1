package vrimplantacao2_5.service.atualizador;

/**
 *
 * @author Desenvolvimento
 */

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2_5.dao.atualizador.AtualizadorDAO;
import vrimplantacao2_5.dao.cadastro.bancodados.BancoDadosDAO;
import vrimplantacao2_5.dao.cadastro.sistema.SistemaDAO;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;
import vrimplantacao2_5.vo.enums.ESistemaBancoDados;

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
    
    public void salvarBancoDados() throws Exception {
        List<EBancoDados> eBancoDados = getBancoDados();
        for (EBancoDados eBancoDado : eBancoDados) {
            this.atualizadorDAO.salvarBancoDados(eBancoDado);
        }        
    }
    
    public void salvarSistema() throws Exception {
        List<ESistema> eSistemas = getSistema();
        for (ESistema eSistema : eSistemas) {
            this.atualizadorDAO.salvarSistema(eSistema);
        }        
    }
    
    public void deletarSistemaBancoDados() throws Exception {
        this.atualizadorDAO.deletarSistemaBancoDados();
    }
    
    public void salvarSistemaBancoDados() throws Exception {
        boolean existeSistema, existeBancoDados;

        for (ESistemaBancoDados eSistemaBancoDados : ESistemaBancoDados.values()) {
            existeSistema = new SistemaDAO().existeSistema(eSistemaBancoDados.getNomeSistema());
            existeBancoDados = new BancoDadosDAO().existeBancoDados(eSistemaBancoDados.getNomeBancoDados());

            if (existeSistema && existeBancoDados) {
                this.atualizadorDAO.salvarSistemaBancoDados(eSistemaBancoDados);
            }
        }
    }
    
    public void criarConstraint() throws Exception {
        this.atualizadorDAO.criarConstraint();
    }
    
    public void criarEstrutura2_5() throws Exception {
        this.criarSchema();
        this.criarTabelas();
        this.criarConstraint();
        this.salvarBancoDados();
        this.salvarSistema();
        this.deletarSistemaBancoDados();
        this.salvarSistemaBancoDados();
    }
}
