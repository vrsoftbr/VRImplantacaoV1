package vrimplantacao2.vo.enums;

public enum TipoInscricao {
    VAZIO(-1, "VAZIO"),
    JURIDICA (0, "JURIDICA"),
    FISICA (1, "FISICA");
    
    private final int id;
    private final String descricao;

    private TipoInscricao(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public static TipoInscricao analisarCnpjCpf(long cnpjCpf) {
        if (String.valueOf(cnpjCpf).length() <= 11) {
            return FISICA;
        } else {
            return JURIDICA;
        }
    } 
}
