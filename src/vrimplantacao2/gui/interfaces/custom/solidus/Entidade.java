package vrimplantacao2.gui.interfaces.custom.solidus;

/**
 * Forma de pagamento no sistema Solidus é chamado de Entidade.
 * @author Leandro
 */
public class Entidade {
    
    private int id;
    private String descricao;

    public Entidade(int id, String descricao) {
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
        return this.id + " - " + this.descricao;
    }
    
}
