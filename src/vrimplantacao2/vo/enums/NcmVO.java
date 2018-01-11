package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public class NcmVO {
    
    public long id = 0;
    public String descricao = "SEM DESCRICAO DE NCM";
    public int ncm1 = 402;
    public int ncm2 = 99;
    public int ncm3 = 0;
    public int nivel = 3;

    public long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getNcm1() {
        return ncm1;
    }

    public int getNcm2() {
        return ncm2;
    }

    public int getNcm3() {
        return ncm3;
    }

    public int getNivel() {
        return nivel;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setNcm1(int ncm1) {
        this.ncm1 = ncm1;
    }

    public void setNcm2(int ncm2) {
        this.ncm2 = ncm2;
    }

    public void setNcm3(int ncm3) {
        this.ncm3 = ncm3;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
}
