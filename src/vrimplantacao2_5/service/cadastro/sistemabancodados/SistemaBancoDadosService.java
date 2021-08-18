package vrimplantacao2_5.service.cadastro.sistemabancodados;

import java.util.List;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.cadastro.sistemabancodados.SistemaBancoDadosDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.SistemaBancoDadosVO;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaBancoDadosService {

    private final ConexaoProvider provider;
    private final SistemaBancoDadosDAO sistemaBancoDadosDAO;
    
    public SistemaBancoDadosService() {
        this.sistemaBancoDadosDAO = new SistemaBancoDadosDAO();
        this.provider = new ConexaoProvider();
    }
    
    public SistemaBancoDadosService(SistemaBancoDadosDAO sistemaBancoDadosDAO, ConexaoProvider provider) {
        this.sistemaBancoDadosDAO = sistemaBancoDadosDAO;
        this.provider = provider;
    }
    
    public void inserir(SistemaBancoDadosVO vo) throws Exception {
        
        try {
            provider.begin();
            
            sistemaBancoDadosDAO.inserir(vo);
            
            provider.commit();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();
        }
    }
    
    public void alterar(SistemaBancoDadosVO vo) throws Exception {
        
        try {
            provider.begin();
            
            sistemaBancoDadosDAO.alterar(vo);
            
            provider.commit();
        } catch(Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();
        }
    }
    
    public List<SistemaBancoDadosVO> consultar(SistemaBancoDadosVO vo) throws Exception {
        List<SistemaBancoDadosVO> result = null;
        
        try {
            result = sistemaBancoDadosDAO.consultar(vo);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Sistema x Banco de Dados");
        }
        
        return result;
    }
    
    private String getTitle() {
        return "Cadastro Sistema x Banco de Dados";
    } }
