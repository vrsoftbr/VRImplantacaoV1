package vrimplantacao.gui.interfaces.classes;

/**
 * Esta classe representa a loja de um cliente.
 * @author Leandro
 */
public class LojaClienteVO {
    
    /**
     * Código da loja no sistema do cliente, foi colocado como <i>String</i> por questão
     * de compatibilidade.
     */
    private int codigo = 1;
    private String descricao;

    public LojaClienteVO(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public LojaClienteVO() {
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }    

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }    
    
}
