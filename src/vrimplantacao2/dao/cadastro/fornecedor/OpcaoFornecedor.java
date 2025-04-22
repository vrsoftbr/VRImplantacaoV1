package vrimplantacao2.dao.cadastro.fornecedor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.vo.importacao.FornecedorIMP;

public enum OpcaoFornecedor {
    /**
     * Dados básicos do fornecedor.
     */
    DADOS, 
    /**
     * Informações dos contatos.
     */
    CONTATOS,
    /**
     * Quando for unificar, marque esta opção.
     */
    FAMILIA,
    RAZAO_SOCIAL,
    NOME_FANTASIA,
    ENDERECO,
    ENDERECO_COMPLETO,
    ENDERECO_COMPLETO_COBRANCA,
    BAIRRO,
    UNIFICACAO, 
    CONDICAO_PAGAMENTO,
    CONDICAO_PAGAMENTO2,
    PRAZO_FORNECEDOR,
    PRAZO_PEDIDO_FORNECEDOR,
    TELEFONE,
    TIPO_INSCRICAO,
    SITUACAO_CADASTRO,
    TIPO_EMPRESA,
    CNPJ_CPF,
    INSCRICAO_ESTADUAL,
    INSCRICAO_MUNICIPAL,
    MUNICIPIO,
    UF,
    TIPO_PAGAMENTO, 
    NUMERO, 
    TIPO_FORNECEDOR,
    OBSERVACAO,
    BANCO_PADRAO,
    COMPLEMENTO,
    CEP, 
    EMAIL, 
    CONTATO_NOME, 
    TIPO_CONTATO, 
    CELULAR, 
    BLOQUEADO, 
    EMITE_NFE, 
    PERMITE_NF_SEM_PEDIDO,
    TIPO_INDICADOR_IE,
    PRODUTO_FORNECEDOR,
    IMPORTAR_SOMENTE_ATIVOS,
    DATA_CADASTRO,
    PAGAR_FORNECEDOR,
    OUTRAS_RECEITAS,
    FORCAR_UNIFICACAO,
    UTILIZAIVA;
    //TODO: SUFRAMA
    //TODO: DATACADASTRO
    
    private List<FornecedorIMP> listaEspecial;

    public List<FornecedorIMP> getListaEspecial() {
        return listaEspecial;
    }

    public void setListaEspecial(List<FornecedorIMP> listaEspecial) {
        this.listaEspecial = listaEspecial;
    }

    public static Set<OpcaoFornecedor> getDados() {
        return new HashSet<>(Arrays.asList(
                DADOS,
                RAZAO_SOCIAL,
                NOME_FANTASIA,
                CNPJ_CPF,
                INSCRICAO_ESTADUAL,
                INSCRICAO_MUNICIPAL
        ));
    }
    
    public static Set<OpcaoFornecedor> getEndereco() {
        return new HashSet<>(Arrays.asList(
                ENDERECO,
                NUMERO,
                COMPLEMENTO,
                BAIRRO,
                MUNICIPIO,
                UF,
                CEP
        ));
    }
    
    public static Set<OpcaoFornecedor> getContato() {
        return new HashSet<>(Arrays.asList(
                TELEFONE,
                CONTATOS
        ));
    }
    
    public static Set<OpcaoFornecedor> getDadosComplementares() {
        return new HashSet<>(Arrays.asList(
                DATA_CADASTRO,
                SITUACAO_CADASTRO,
                PRAZO_FORNECEDOR,
                CONDICAO_PAGAMENTO,
                TIPO_INDICADOR_IE,
                OBSERVACAO,
                TIPO_EMPRESA
        ));
    }
    
    public static Set<OpcaoFornecedor> getPadrao() {
        Set<OpcaoFornecedor> result = new HashSet<>();
        result.addAll(getDados());
        result.addAll(getEndereco());
        result.addAll(getContato());
        result.addAll(getDadosComplementares());
        result.add(PRODUTO_FORNECEDOR);
        
        return result;
    }
}
