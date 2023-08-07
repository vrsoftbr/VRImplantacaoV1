/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.controller.interfaces;

import java.util.Set;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;

/**
 *
 * @author Desenvolvimento
 */
public abstract class InterfaceController {

    private String lojaOrigem = "";
        
    /**
     * Retorna o código da loja de origem selecionada.
     * @return 
     */
    public String getLojaOrigem() {
        return lojaOrigem;
    }

    /**
     * Seta o código da loja origem selecionada.
     * @param LojaOrigem Código da loja de origem.
     */
    public void setLojaOrigem(String LojaOrigem) {
        this.lojaOrigem = LojaOrigem;
    }
    
    /**
     * Retorna uma lista com todos os campos que este importa para os produtos.
     * @return Lista com os parâmetros.
     */
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return OpcaoProduto.getPadrao();
    }

    /**
     * Retorna uma lista com todos os campos que este importa para os fornecedores.
     * @return Lista com os parâmetros.
     */
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return OpcaoFornecedor.getPadrao();
    }

    /**
     * Retorna uma lista com todos os campos que este importa para os clientes.
     * @return Lista com os parâmetros.
     */
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return OpcaoCliente.getPadrao();
    }
    
}
