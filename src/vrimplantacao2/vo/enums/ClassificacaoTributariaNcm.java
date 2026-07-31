package vrimplantacao2.vo.enums;

/**
 * Classe que representa o CST (Reforma Tributaria).
 * @author Wesley
 */
public class ClassificacaoTributariaNcm {
       
    private final int id;
    private final int idClassificacao;
    private final int idNcm;
    public final int ncm1;
    public final int ncm2;
    public final int ncm3;

    public ClassificacaoTributariaNcm(int id, int idClassificacao, int idNcm, int ncm1, int ncm2, int ncm3) {
        this.id = id;
        this.idClassificacao = idClassificacao;
        this.idNcm = idNcm;
        this.ncm1 = ncm1;
        this.ncm2 = ncm2;
        this.ncm3 = ncm3;
    }

    public int getId() {
        return id;
    }

    public int getIdClassificacao() {
        return idClassificacao;
    }

    public int getIdNcm() {
        return idNcm;
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
    
    
}
