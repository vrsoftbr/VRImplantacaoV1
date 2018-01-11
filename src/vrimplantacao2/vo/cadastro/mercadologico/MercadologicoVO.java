package vrimplantacao2.vo.cadastro.mercadologico;

import vrimplantacao.utils.Utils;

public class MercadologicoVO {
    private int id = 0;
    private String descricao = SEM_DESCRICAO;
    public static final String SEM_DESCRICAO = "SEM DESCRICAO";
    private int mercadologico1 = 0;
    private int mercadologico2 = 0;
    private int mercadologico3 = 0;
    private int mercadologico4 = 0;
    private int mercadologico5 = 0;
    private int nivel = 0;

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getMercadologico1() {
        return mercadologico1;
    }

    public int getMercadologico2() {
        return mercadologico2;
    }

    public int getMercadologico3() {
        return mercadologico3;
    }

    public int getMercadologico4() {
        return mercadologico4;
    }

    public int getMercadologico5() {
        return mercadologico5;
    }

    public int getNivel() {
        return nivel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = Utils.acertarTexto(descricao, 35, SEM_DESCRICAO);
    }

    public void setMercadologico1(int mercadologico1) {
        this.mercadologico1 = mercadologico1;
    }

    public void setMercadologico2(int mercadologico2) {
        this.mercadologico2 = mercadologico2;
    }

    public void setMercadologico3(int mercadologico3) {
        this.mercadologico3 = mercadologico3;
    }

    public void setMercadologico4(int mercadologico4) {
        this.mercadologico4 = mercadologico4;
    }

    public void setMercadologico5(int mercadologico5) {
        this.mercadologico5 = mercadologico5;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel; 
    }

}
