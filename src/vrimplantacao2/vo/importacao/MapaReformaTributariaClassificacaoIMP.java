package vrimplantacao2.vo.importacao;

/**
 *
 * @author wesley
 */
public class MapaReformaTributariaClassificacaoIMP {
    
    private String origId;
    private Double origReducao;
    private Double origDiferimento;
    private String id_cstIbsCbs;
    private String cclasstrib;
    private String descricao;
    private String fundamentacaoLegal;
    private boolean aliquotaZero;

    public MapaReformaTributariaClassificacaoIMP(String origId, Double origReducao, Double origDiferimento, String id_cstIbsCbs, String cclasstrib, String descricao, String fundamentacaoLegal, boolean aliquotaZero) {
        this.origId = origId;
        this.origReducao = origReducao;
        this.origDiferimento = origDiferimento;
        this.id_cstIbsCbs = id_cstIbsCbs;
        this.cclasstrib = cclasstrib;
        this.descricao = descricao;
        this.fundamentacaoLegal = fundamentacaoLegal;
        this.aliquotaZero = aliquotaZero;
    }

    public String getOrigId() {
        return origId;
    }

    public Double getOrigReducao() {
        return origReducao;
    }

    public Double getOrigDiferimento() {
        return origDiferimento;
    }

    public String getId_cstIbsCbs() {
        return id_cstIbsCbs;
    }

    public String getCclasstrib() {
        return cclasstrib;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getFundamentacaoLegal() {
        return fundamentacaoLegal;
    }

    public boolean isAliquotaZero() {
        return aliquotaZero;
    }
}