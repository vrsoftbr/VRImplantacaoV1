package vrimplantacao2.vo.importacao;

/**
 *
 * @author wesley
 */
public class MapaReformaTributariaClassificacaoNcmIMP {
    
    private String origId;
    private String origClassificacao;
    private String origIdNcm;
    private int origNcm1;
    private int origNcm2;
    private int origNcm3;

    public MapaReformaTributariaClassificacaoNcmIMP(String origId, String origClassificacao, String origIdNcm, int origNcm1, int origNcm2, int origNcm3) {
        this.origId = origId;
        this.origClassificacao = origClassificacao;
        this.origIdNcm = origIdNcm;
        this.origNcm1 = origNcm1;
        this.origNcm2 = origNcm2;
        this.origNcm3 = origNcm3;
    }

    public String getOrigId() {
        return origId;
    }

    public String getOrigClassificacao() {
        return origClassificacao;
    }

    public String getOrigIdNcm() {
        return origIdNcm;
    }

    public int getOrigNcm1() {
        return origNcm1;
    }

    public int getOrigNcm2() {
        return origNcm2;
    }

    public int getOrigNcm3() {
        return origNcm3;
    }
}