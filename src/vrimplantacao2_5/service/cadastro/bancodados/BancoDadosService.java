/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.service.cadastro.bancodados;

import java.util.List;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2_5.dao.cadastro.bancodados.BancoDadosDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;

/**
 *
 * @author Desenvolvimento
 */
public class BancoDadosService {
    
    private final ConexaoProvider provider;
    private final BancoDadosDAO bancoDadosDAO;
    
    public BancoDadosService() {
        this.bancoDadosDAO = new BancoDadosDAO();
        this.provider = new ConexaoProvider();
    }
    
    public BancoDadosService(BancoDadosDAO bancoDadosDAO, 
                             ConexaoProvider conexaoProvider) {
        this.bancoDadosDAO = bancoDadosDAO;
        this.provider = conexaoProvider;
    }
    
    public void existeBancoDados(String nome) throws Exception {
        if (bancoDadosDAO.existeBancoDados(nome)) {
            throw new VRException("Banco de dados já cadastrado");
        }
    }
    
    public void inserir(BancoDadosVO vo) throws Exception {
        
        try {
            provider.begin();
            
            existeBancoDados(vo.getNome().trim());
            bancoDadosDAO.inserir(vo);
            
            provider.commit();            
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();
        }
    }
    
    public void alterar(BancoDadosVO vo) throws Exception {
        try {
            provider.begin();
            
            bancoDadosDAO.alterar(vo);
            
            provider.commit();
        } catch(Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();            
        }
    }
    
    public List<BancoDadosVO> consultar(String nome) {
        List<BancoDadosVO> result = null;
        
        try {
            result = bancoDadosDAO.consultar(nome);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Banco de Dados");
        }
        
        return result;
    }

    public List getBancoDados() throws Exception {
        List<BancoDadosVO> result = null;
        
        try {
            result = bancoDadosDAO.getBancoDados();
        } catch (Exception e) {
            try {
                Util.exibirMensagem(e.getMessage(), getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }
        
        return result;
    }
    
    private String getTitle() {
        return "Cadastro Banco de Dados";
    }    
}
