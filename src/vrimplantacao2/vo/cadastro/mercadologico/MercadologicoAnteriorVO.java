package vrimplantacao2.vo.cadastro.mercadologico;

/**
 * Código anterior do mercadológico
 * @author Leandro
 */
public class MercadologicoAnteriorVO {
    
    private String sistema = "";
    private String loja = "";
    private String antMerc1 = "";
    private String antMerc2 = "";
    private String antMerc3 = "";
    private String antMerc4 = "";
    private String antMerc5 = "";
    private int nivel = 1;
    private String descricao = "SEM DESCRICAO";
    private int merc1 = 0;
    private int merc2 = 0;
    private int merc3 = 0;
    private int merc4 = 0;
    private int merc5 = 0;

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public String getAntMerc1() {
        return antMerc1;
    }

    public String getAntMerc2() {
        return antMerc2;
    }

    public String getAntMerc3() {
        return antMerc3;
    }

    public String getAntMerc4() {
        return antMerc4;
    }

    public String getAntMerc5() {
        return antMerc5;
    }

    public int getNivel() {
        return nivel;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getMerc1() {
        return merc1;
    }

    public int getMerc2() {
        return merc2;
    }

    public int getMerc3() {
        return merc3;
    }

    public int getMerc4() {
        return merc4;
    }

    public int getMerc5() {
        return merc5;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setAntMerc1(String antMerc1) {
        if (antMerc1 == null) {
            antMerc1 = "";
        }
        this.antMerc1 = antMerc1;
    }

    public void setAntMerc2(String antMerc2) {
        if (antMerc2 == null) {
            antMerc2 = "";
        }
        this.antMerc2 = antMerc2;
    }

    public void setAntMerc3(String antMerc3) {
        if (antMerc3 == null) {
            antMerc3 = "";
        }
        this.antMerc3 = antMerc3;
    }

    public void setAntMerc4(String antMerc4) {
        if (antMerc4 == null) {
            antMerc4 = "";
        }
        this.antMerc4 = antMerc4;
    }

    public void setAntMerc5(String antMerc5) {
        if (antMerc5 == null) {
            antMerc5 = "";
        }
        this.antMerc5 = antMerc5;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setMerc1(int merc1) {
        this.merc1 = merc1;
    }

    public void setMerc2(int merc) {
        this.merc2 = merc;
    }

    public void setMerc3(int merc3) {
        this.merc3 = merc3;
    }

    public void setMerc4(int merc4) {
        this.merc4 = merc4;
    }

    public void setMerc5(int merc5) {
        this.merc5 = merc5;
    }
    
}
