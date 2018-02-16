package vrimplantacao2.dao.cadastro.fornecedor;

import java.util.List;
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
    RAZAO_SOCIAL,
    NOME_FANTASIA,
    ENDERECO,
    BAIRRO,
    UNIFICACAO, 
    CONDICAO_PAGAMENTO,
    CONDICAO_PAGAMENTO2,
    PRAZO_FORNECEDOR,
    TELEFONE,
    TIPO_INSCRICAO,
    SITUACAO_CADASTRO,
    TIPO_EMPRESA,
    CNPJ_CPF,
    INSCRICAO_ESTADUAL;
    
    private List<FornecedorIMP> listaEspecial;

    public List<FornecedorIMP> getListaEspecial() {
        return listaEspecial;
    }

    public void setListaEspecial(List<FornecedorIMP> listaEspecial) {
        this.listaEspecial = listaEspecial;
    }
}
