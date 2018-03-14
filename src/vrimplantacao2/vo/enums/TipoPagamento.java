package vrimplantacao2.vo.enums;

import java.util.Objects;

/**
 * Classe que representa o tipo de pagamento no VR.
 * @author Leandro
 */
public class TipoPagamento {
    
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
