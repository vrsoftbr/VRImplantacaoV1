package vrimplantacao2.vo.enums;

import java.util.Objects;

/**
 * Classe que representa o tipo de pagamento no VR.
 * @author Leandro
 */
public class TipoPagamento {
    
    public static final TipoPagamento CARTAO_DEBITO = new TipoPagamento(0, "CARTAO DEBITO");
    public static final TipoPagamento DOC_TED = new TipoPagamento(1, "DOC/TED");
    public static final TipoPagamento CARTAO_CREDITO = new TipoPagamento(2, "CARTAO CREDITO");
    public static final TipoPagamento CHEQUE = new TipoPagamento(3, "CHEQUE");
    public static final TipoPagamento BOLETO_BANCARIO = new TipoPagamento(4, "BOLETO BANCARIO");
    public static final TipoPagamento DINHEIRO = new TipoPagamento(5, "DINHEIRO");
    public static final TipoPagamento DEPOSITO = new TipoPagamento(6, "DEPOSITO");
    public static final TipoPagamento CARTEIRA = new TipoPagamento(7, "CARTEIRA");
    public static final TipoPagamento S_BOLETO = new TipoPagamento(8, "S/BOLETO");
    
    
    private int id;
    private String descricao;

    public TipoPagamento() {
    }

    public TipoPagamento(int id, String descricao) {
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

    @Override
    public String toString() {
        return this.descricao;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.id;
        hash = 61 * hash + Objects.hashCode(this.descricao);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TipoPagamento other = (TipoPagamento) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.descricao, other.descricao);
    }
    
    
}
