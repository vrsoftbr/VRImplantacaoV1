package vrimplantacao2.vo.enums;

public enum TipoEmbalagem {
    UN (0,"UN","UNIDADE"),
    CX (1,"CX","CAIXA"),
    LA (2,"LA","LATA"),
    PT (3,"PT","PACOTE"),
    KG (4,"KG","QUILO"),
    FD (5,"FD","FARDO"),
    MT (6,"MT","METRO"),
    GF (7,"GF","GARRAFA"),
    PC (8,"PC","PECA"),
    LT (9,"LT","LITRO"),
    FC (10,"FC","010FC"),
    VD (11,"VD","011VD"),
    SC (12,"SC","012SC"),
    CP (13,"CP","013CP"),
    BD (14,"BD","BANDEJA"),
    TP (15,"TP","015TP");

    private final int id;
    private final String sigla;
    private final String descricao;

    private TipoEmbalagem(int id, String sigla, String descricao) {
        this.id = id;
        this.sigla = sigla;
        this.descricao = descricao;        
    }

    public int getId() {
        return id;
    }

    public String getSigla() {
        return sigla;
    }

    public String getDescricao() {
        return descricao;
    }    

    /**
     * Retorna o tipo da embalagem correspondente baseando-se na sigla e na 
     * descricao.
     * @param siglaDescricao Sigla ou Descrição da unidade de medida.
     * @return TipoEmbalagem encontrado ou UN ca
     */
    public static TipoEmbalagem getTipoEmbalagem(String siglaDescricao) {
        if (siglaDescricao == null) {
            siglaDescricao = "";
        } else {
            siglaDescricao = siglaDescricao.trim().toUpperCase();
        }
        
        TipoEmbalagem[] aa = values();
        for (TipoEmbalagem a: aa) {
            if (a.getSigla().equals(siglaDescricao)) {
                return a;
            }
        }
        for (TipoEmbalagem a: aa) {
            if (a.getDescricao().equals(siglaDescricao)) {
                return a;
            }
        }
        return UN;
    }
    
    /**
     * Retorna o tipo da embalagem correspondente baseando-se na sigla e na 
     * descricao.
     * @param id ID da unidade de medida.
     * @return TipoEmbalagem encontrado ou UN ca
     */
    public static TipoEmbalagem getTipoEmbalagem(int id) {
        TipoEmbalagem[] aa = values();
        for (TipoEmbalagem a: aa) {
            if (a.getId() == id) {
                return a;
            }
        }
        return UN;
    }
}
