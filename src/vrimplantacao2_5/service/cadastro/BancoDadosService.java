/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.service.cadastro;

import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2_5.dao.bancodados.BancoDadosDAO;
import vrimplantacao2_5.provider.ConexaoProvider;

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
    
    public void salvar(String nome) throws Exception {
        
        try {
            provider.begin();
            
            bancoDadosDAO.salvar(nome);
            
            provider.commit();
            
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();
        }
    }
    
    private String getTitle() {
        return "Cadastro Banco de Dados";
    }    
}
