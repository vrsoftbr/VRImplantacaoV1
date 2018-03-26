package vrimplantacao2.vo.enums;

/**
 * Representa o tipo local de cobrança no banco.
 * @author Leandro
 */
public class TipoLocalCobranca {
    
    public static final TipoLocalCobranca CARTEIRA = new TipoLocalCobranca(0, "CARTEIRA");
    public static final TipoLocalCobranca COBRANCA_INTERNA = new TipoLocalCobranca(1, "COBRANCA INTERNA");
    public static final TipoLocalCobranca COBRANCA_ADVOGADO = new TipoLocalCobranca(2, "COBRANCA ADVOGADO");
    public static final TipoLocalCobranca OUVE_DEV_CHEQUE = new TipoLocalCobranca(3, "OUVE DEV. CHEQUE");
    public static final TipoLocalCobranca BOLETO = new TipoLocalCobranca(4, "BOLETO");
    public static final TipoLocalCobranca DEPOSITO = new TipoLocalCobranca(5, "DEPOSITO");
    
    private int id;
    private String descricao;

    public TipoLocalCobranca() {}

    public TipoLocalCobranca(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
