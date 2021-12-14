/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.controller.sistema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.AvistareDAO;
import vrimplantacao2_5.controller.interfaces.InterfaceController;
import vrimplantacao2_5.vo.sistema.AvistareVO;

/**
 *
 * @author Desenvolvimento
 */
public class AvistareController extends InterfaceController {

    public AvistareDAO dao = null;
    private final String SISTEMA = "Avistare";
    private String complementoSistema = "";
    
    public AvistareController() {
        this.dao = new AvistareDAO();
    }
    
    public AvistareController(AvistareDAO dao) {
        this.dao = dao;
    }
    
    @Override
    public String getSistema() {
        return (!"".equals(complementoSistema) ? this.complementoSistema + "-" : "") + SISTEMA;
    }
    
    public String getComplementoSistema() {
        return this.complementoSistema;
    }
    
    public void setComplementoSistema(String complementoSistema) {
        this.complementoSistema = complementoSistema == null ? "" : complementoSistema.trim();
    }
    
    public void setAvistare(AvistareVO avistareVO) {
        dao.avistareVO = avistareVO;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FORCAR_ATUALIZACAO,
                    OpcaoProduto.FORCAR_UNIFICACAO,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DESC_COMPLETA,                    
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.PESO_BRUTO,
                    OpcaoProduto.PESO_LIQUIDO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.PDV_VENDA
                }
        ));
    }
    
    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {        
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.FORCAR_UNIFICACAO,
                OpcaoFornecedor.IMPORTAR_SOMENTE_ATIVOS,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.NUMERO,
                OpcaoFornecedor.COMPLEMENTO,
                OpcaoFornecedor.BAIRRO,
                OpcaoFornecedor.MUNICIPIO,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.UF,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.DATA_CADASTRO,
                OpcaoFornecedor.OBSERVACAO,
                OpcaoFornecedor.EMAIL,
                OpcaoFornecedor.CELULAR,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.FORCAR_UNIFICACAO,
                OpcaoCliente.IMPORTAR_SOMENTE_ATIVO,
                OpcaoCliente.DADOS,
                OpcaoCliente.RAZAO,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }
}
