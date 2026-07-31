package vrimplantacao2.vo.cadastro.reformatributaria;

/**
 *
 * @author wesley
 */
public class ClassificacaoTributariaNcmVO {
    
    private int id = -1;
    private int idClassificacao;
    private int idNcm;
    private int ncm1;
    private int ncm2;
    private int ncm3;
    private int idLoja;

    public ClassificacaoTributariaNcmVO(int idClassificacao, int idNcm, int ncm1, int ncm2, int ncm3, int idLoja) {
        this.idClassificacao = idClassificacao;
        this.idNcm = idNcm;
        this.ncm1 = ncm1;
        this.ncm2 = ncm2;
        this.ncm3 = ncm3;
        this.idLoja = idLoja;
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

    public int getIdLoja() {
        return idLoja;
    }
}
