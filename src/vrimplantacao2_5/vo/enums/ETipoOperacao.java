/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.enums;

/**
 *
 * @author Desenvolvimento
 */
public enum ETipoOperacao {

    SALVAR_PRODUTO(1, "PRODUTO", 1),
    SALVAR_MERCADOLOGICO(2, "MERCADOLOGICO", 1),
    SALVAR_FAMILIA_PRODUTO(3, "FAMILIA PRODUTO", 1),
    SALVAR_EAN(4, "EAN", 1),
    SALVAR_FORNECEDOR(5, "FORNECEDOR", 1),
    SALVAR_PRODUTO_FORNECEDOR(6, "PRODUTO FORNECEDOR", 1),
    SALVAR_PAGAR_FORNECEDOR(7, "PAGAR FORNECEDOR", 1),
    SALVAR_CLIENTE_PREFERENCIAL(8, "CLIENTE PREFERENCIAL", 1),
    SALVAR_CREDITO_ROTATIVO(9, "CREDITO ROTATIVO", 1),
    SALVAR_CHEQUE(10, "CHEQUE", 1),
    SALVAR_CONVENIO_EMPRESA(11, "CONVENIO EMPRESA", 1),
    SALVAR_CONVENIO_CONVENIADO(12, "CONVENIO CONVENIADO", 1),
    SALVAR_CONVENIO_TRANSACAO(13, "CONVENIO TRANSACAO", 1),
    SALVAR_VENDA(14, "VENDA", 1),
    ATUALIZAR_PRECO(15, "PRECO", 3),
    ATUALIZAR_CUSTO(16, "CUSTO", 3),
    ATUALIZAR_ESTOQUE(17, "ESTOQUE", 3),
    ATUALIZAR_ICMS(18, "ICMS", 3),
    ATUALIZAR_SITUACAO_CADASTRO(19, "SITUACAO CADASTRO", 3),
    UNIFICAR_PRODUTO(20, "PRODUTO", 2),
    UNIFICAR_FORNECEDOR(21, "FORNECEDOR", 2),
    UNIFICAR_CLIENTE_PREFERENCIAL(22, "CLIENTE PREFERENCIAL", 2),
    SALVAR_PROMOCAO(23, "SALVAR PROMOCAO", 1);
    
    private int id;
    private String descricao;
    private int idMetodo;

    ETipoOperacao(int id, String descricao, int idMetodo) {
        this.id = id;
        this.descricao = descricao;
        this.idMetodo = idMetodo;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getDescricao() {
        return this.descricao;
    }
    
    public int getIdMetodo() {
        return this.idMetodo;
    }
    
    public static ETipoOperacao getById(int id) {
        for (ETipoOperacao tp : values()) {
            if (tp.getId() == id) {
                return tp;
            }
        }
        
        return null;
    }
}
