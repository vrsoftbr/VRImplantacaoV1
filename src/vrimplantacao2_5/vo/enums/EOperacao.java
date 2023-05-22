package vrimplantacao2_5.vo.enums;

/**
 *
 * @author guilhermegomes
 */
public enum EOperacao {

    SALVAR_PRODUTO(1, "SALVAR PRODUTO"),
    SALVAR_MERCADOLOGICO(2, "SALVAR MERCADOLOGICO"),
    SALVAR_FAMILIA_PRODUTO(3, "SALVAR FAMILIA PRODUTO"),
    SALVAR_EAN(4, "SALVAR EAN"),
    SALVAR_FORNECEDOR(5, "SALVAR FORNECEDOR"),
    SALVAR_PRODUTO_FORNECEDOR(6, "SALVAR PRODUTO FORNECEDOR"),
    SALVAR_PAGAR_FORNECEDOR(7, "SALVAR PAGAR FORNECEDOR"),
    SALVAR_CLIENTE_PREFERENCIAL(8, "SALVAR CLIENTE PREFERENCIAL"),
    SALVAR_CREDITO_ROTATIVO(9, "SALVAR CREDITO ROTATIVO"),
    SALVAR_CHEQUE(10, "SALVAR CHEQUE"),
    SALVAR_CONVENIO_EMPRESA(11, "SALVAR CONVENIO EMPRESA"),
    SALVAR_CONVENIO_CONVENIADO(12, "SALVAR CONVENIADO"),
    SALVAR_CONVENIO_TRANSACAO(13, "SALVAR CONVENIO TRANSACAO"),
    SALVAR_VENDA(14, "SALVAR VENDA"),
    ATUALIZAR_PRECO(15, "ATUALIZAR PRECO"),
    ATUALIZAR_CUSTO(16, "ATUALIZAR CUSTO"),
    ATUALIZAR_ESTOQUE(17, "ATUALIZAR ESTOQUE"),
    ATUALIZAR_ICMS(18, "ATUALIZAR ICMS"),
    ATUALIZAR_SITUACAO_CADASTRO(19, "ATUALIZAR SITUACAO CADASTRO"),
    UNIFICAR_PRODUTO(20, "UNIFICAR PRODUTO"),
    UNIFICAR_FORNECEDOR(21, "UNIFICAR FORNECEDOR"),
    UNIFICAR_CLIENTE_PREFERENCIAL(22, "UNIFICAR CLIENTE PREFERENCIAL"),
    SALVAR_PROMOCAO(23, "SALVAR PROMOCAO");

    private int id;
    private String nome;

    private EOperacao(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static EOperacao getById(int id) {
        for (EOperacao bd : values()) {
            if (bd.getId() == id) {
                return bd;
            }
        }
        return null;
    }
}
