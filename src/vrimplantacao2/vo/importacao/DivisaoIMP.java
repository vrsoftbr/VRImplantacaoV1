package vrimplantacao2.vo.importacao;

/**
 * Classe utilizada para importar as divisões.
 * @author Leandro
 */
public class DivisaoIMP {

    private String id;
    private String descricao = "SEM DESCRICAO";

    public DivisaoIMP(){}

    public DivisaoIMP(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
