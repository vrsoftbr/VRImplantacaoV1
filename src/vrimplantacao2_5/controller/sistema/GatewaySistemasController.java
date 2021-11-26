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
    
    public GatewaySistemasController() {}
    
    public GatewaySistemasController(GatewaySistemasVO gatewaySistemasVO, 
            OpcoesMigracaoVO opcoesMigracaoVO) {
        
        dao = new GatewaySistemasDAO();
        dao.gatewaySistemasVO = gatewaySistemasVO;
        
        this.opcoesMigracaoVO = opcoesMigracaoVO;        
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_SOMENTE_PRODUTOS_ATIVOS,
                    opcoesMigracaoVO.isHabilitarMigracaoFamiliaProduto() ? OpcaoProduto.FAMILIA : null,
                    opcoesMigracaoVO.isHabilitarMigracaoFamiliaProduto() ? OpcaoProduto.FAMILIA_PRODUTO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoMercadologicos() ? OpcaoProduto.MERCADOLOGICO_PRODUTO : null,
                    opcoesMigracaoVO.isHabilitarMigracaoMercadologicos() ? OpcaoProduto.MERCADOLOGICO : null,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.IMPORTAR_SOMENTE_ATIVOS,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.NUMERO,
                OpcaoFornecedor.COMPLEMENTO,
                OpcaoFornecedor.BAIRRO,
                OpcaoFornecedor.MUNICIPIO,
                OpcaoFornecedor.UF,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.DATA_CADASTRO,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.CONTATOS,
                opcoesMigracaoVO.isHabilitarMigracaoProdutosFornecedores() ? OpcaoFornecedor.PRODUTO_FORNECEDOR : null
        ));
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.IMPORTAR_SOMENTE_ATIVO_EVENTUAL,
                OpcaoCliente.IMPORTAR_SOMENTE_ATIVO_PREFERENCIAL,
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.NOME_PAI,
                OpcaoCliente.NOME_MAE,
                OpcaoCliente.NOME_CONJUGE,
                OpcaoCliente.CARGO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.OBSERVACOES,
                opcoesMigracaoVO.isHabilitarMigracaoReceberCreditoRotativo() ? OpcaoCliente.RECEBER_CREDITOROTATIVO : null,
                opcoesMigracaoVO.isHabilitarMigracaoClientesEventuais() ? OpcaoCliente.CLIENTE_EVENTUAL : null
        ));        
    }
}
