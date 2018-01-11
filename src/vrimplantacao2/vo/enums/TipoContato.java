package vrimplantacao2.vo.enums;

public enum TipoContato {
    
    COMERCIAL (0,"COMERCIAL"),
    FINANCEIRO (1,"FINANCEIRO"),
    FISCAL (2,"FISCAL"),
    NFE (3,"NFE");

    private final int id;
    private final String descricao;

    private TipoContato(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }    
    
    public static TipoContato getByDescricao(String value) {
        if (value == null) {
            value = "";
        }
        for (TipoContato cont: values()) {
            if (value.equals(cont.getDescricao())) {
                return cont;
            }
        }    
        return COMERCIAL;
    }
    
}
