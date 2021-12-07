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
import vrimplantacao2_5.controller.interfaces.InterfaceController;
import vrimplantacao2_5.dao.sistema.GatewaySistemasDAO;
import vrimplantacao2_5.vo.checks.migracao.OpcoesMigracaoVO;
import vrimplantacao2_5.vo.sistema.GatewaySistemasVO;

/**
 *
 * @author Desenvolvimento
 */
public class GatewaySistemasController extends InterfaceController {

    public OpcoesMigracaoVO opcoesMigracaoVO = null;
    public GatewaySistemasDAO dao = null;
    private String complementoSistema = "";
    private final String SISTEMA = "Gateway Sistemas";
    
    public GatewaySistemasController() {}
    
    public GatewaySistemasController(OpcoesMigracaoVO opcoesMigracaoVO, GatewaySistemasDAO dao) {        
        this.opcoesMigracaoVO = opcoesMigracaoVO;
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
    
    public void setGatewaySistemas(GatewaySistemasVO gatewaySistemasVO) {
        dao.gatewaySistemasVO = gatewaySistemasVO;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.FORCAR_UNIFICACAO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.IMPORTAR_MANTER_BALANCA : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.IMPORTAR_SOMENTE_PRODUTOS_ATIVOS : null,
                    opcoesMigracaoVO.isHabilitarMigracaoMercadologicos() ? OpcaoProduto.MERCADOLOGICO_PRODUTO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoMercadologicos() ? OpcaoProduto.MERCADOLOGICO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.PRODUTOS : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.EAN : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.EAN_EM_BRANCO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.TIPO_EMBALAGEM_EAN : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.TIPO_EMBALAGEM_PRODUTO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.PESAVEL : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.VALIDADE : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.DESC_COMPLETA : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.DESC_GONDOLA : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.DESC_REDUZIDA : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ESTOQUE_MAXIMO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ESTOQUE_MINIMO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.PRECO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.CUSTO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ESTOQUE : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ATIVO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.NCM : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.CEST : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.PIS_COFINS : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ICMS : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ICMS_SAIDA : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ICMS_SAIDA_FORA_ESTADO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ICMS_SAIDA_NF : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ICMS_ENTRADA : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ICMS_CONSUMIDOR : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.MARGEM : null,
                    opcoesMigracaoVO.isHabilitarMigracaoProdutos() ? OpcaoProduto.MAPA_TRIBUTACAO : null
                }
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.IMPORTAR_SOMENTE_ATIVOS : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.DADOS : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.RAZAO_SOCIAL : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.NOME_FANTASIA : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.CNPJ_CPF : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.INSCRICAO_ESTADUAL : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.ENDERECO : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.NUMERO : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.COMPLEMENTO : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.BAIRRO : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.MUNICIPIO : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.UF : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.CEP : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.SITUACAO_CADASTRO : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.DATA_CADASTRO : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.TELEFONE : null,
                opcoesMigracaoVO.isHabilitarMigracaoFornecedores() ? OpcaoFornecedor.CONTATOS : null,
                opcoesMigracaoVO.isHabilitarMigracaoProdutosFornecedores() ? OpcaoFornecedor.PRODUTO_FORNECEDOR : null
        ));
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                opcoesMigracaoVO.isHabilitarMigracaoClientesEventuais() ? OpcaoCliente.IMPORTAR_SOMENTE_ATIVO_EVENTUAL : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.IMPORTAR_SOMENTE_ATIVO_PREFERENCIAL : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.DADOS : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.CNPJ : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.INSCRICAO_ESTADUAL : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.ENDERECO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.NUMERO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.COMPLEMENTO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.BAIRRO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.MUNICIPIO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.UF : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.CEP : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.DATA_CADASTRO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.SITUACAO_CADASTRO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.BLOQUEADO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.NOME_PAI : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.NOME_MAE : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.NOME_CONJUGE : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.CARGO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.VALOR_LIMITE : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.DATA_NASCIMENTO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.TELEFONE : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.CELULAR : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.EMAIL : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesPreferenciais() ? OpcaoCliente.OBSERVACOES : null,
                opcoesMigracaoVO.isHabilitarMigracaoReceberCreditoRotativo() ? OpcaoCliente.RECEBER_CREDITOROTATIVO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesEventuais() ? OpcaoCliente.CLIENTE_EVENTUAL : null
        ));
    }
}