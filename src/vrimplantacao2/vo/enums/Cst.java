package vrimplantacao2.vo.enums;

/**
 * Classe que representa o CST (Reforma Tributaria).
 * @author Wesley
 */
public class Cst {
       
    private final int id;
    private final int cst;
    private final String descricao;
    private final boolean grupoibscbs;
    private final boolean gruporeducao;
    private final boolean grupodiferimento;
    private final boolean grupotribregular;
    private final boolean grupoibscbsmono;

    public Cst(int id, int cst, String descricao, boolean grupoibscbs, boolean gruporeducao, boolean grupodiferimento, boolean grupotribregular, boolean grupoibscbsmono) {
        this.id = id;
        this.cst = cst;
        this.descricao = descricao;
        this.grupoibscbs = grupoibscbs;
        this.gruporeducao = gruporeducao;
        this.grupodiferimento = grupodiferimento;
        this.grupotribregular = grupotribregular;
        this.grupoibscbsmono = grupoibscbsmono;
    }

    public int getId() {
        return id;
    }

    public int getCst() {
        return cst;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isGrupoibscbs() {
        return grupoibscbs;
    }

    public boolean isGruporeducao() {
        return gruporeducao;
    }

    public boolean isGrupodiferimento() {
        return grupodiferimento;
    }

    public boolean isGrupotribregular() {
        return grupotribregular;
    }

    public boolean isGrupoibscbsmono() {
        return grupoibscbsmono;
    }    
}
