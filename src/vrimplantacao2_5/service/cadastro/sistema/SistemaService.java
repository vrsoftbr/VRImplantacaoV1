package vrimplantacao2_5.service.cadastro.sistema;

import java.util.List;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2_5.dao.cadastro.sistema.SistemaDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaService {

    private final ConexaoProvider provider;
    private final SistemaDAO sistemaDAO;
    
    public SistemaService() {
        this.sistemaDAO = new SistemaDAO();
        this.provider = new ConexaoProvider();
    }
    
    public SistemaService(SistemaDAO sistemaDAO, 
                          ConexaoProvider provider) {
        this.sistemaDAO = sistemaDAO;
        this.provider = provider;
    }
    
    public void existeBancoDados(String nome) throws Exception {
        if (sistemaDAO.existeSistema(nome)) {
            throw new VRException("Sistema já cadastrado");
        }
    }
    
    public void inserir(SistemaVO vo) throws Exception {
        
        try {
            provider.begin();
            
            existeBancoDados(vo.getNome().trim());
            sistemaDAO.inserir(vo);
            
            provider.commit();            
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();
        }
    }
    
    public void alterar(SistemaVO vo) throws Exception {
        try {
            provider.begin();
            
            sistemaDAO.alterar(vo);
            
            provider.commit();
        } catch(Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();            
        }
    }
    
    public List<SistemaVO> consultar(String nome) {
        List<SistemaVO> result = null;
        
        try {
            result = sistemaDAO.consultar(nome);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Sistemas");
        }
        
        return result;
    }
    
    public List getSistema() {
        List<SistemaVO> sistemas = null;

        try {

            sistemas = sistemaDAO.getSistema();

        } catch (Exception e) {
            try {
                Util.exibirMensagem(e.getMessage(), getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }

        return sistemas;
    }
    
    private String getTitle() {
        return "Cadastro Sistema";
    }    
}
