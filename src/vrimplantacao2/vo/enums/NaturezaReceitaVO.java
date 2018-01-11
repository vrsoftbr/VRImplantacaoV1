package vrimplantacao2.vo.enums;

public class NaturezaReceitaVO {
    private int id;
    private int cst;
    private int codigo;
    private String descricao;

    public NaturezaReceitaVO(int id, int cst, int codigo, String descricao) {
        this.id = id;
        this.cst = cst;
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public int getCst() {
        return cst;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
    
    
}
