package vrimplantacao2.vo.cadastro.reformatributaria;

/**
 *
 * @author wesley
 */
public class ClassificacaoTributariaVO {
    
    private int id = -1;
    private Double reducao;
    private Double diferimento;
    private int idCstIbsCbs;
    private String cclasstrib;
    private String descricao;
    private String fundamentacaolegal;
    private boolean aliquotazero;
    private final int id_situacaocadastro = 1;

    public ClassificacaoTributariaVO(Double reducao, Double diferimento, int idCstIbsCbs, String cclasstrib, String descricao, String fundamentacaolegal, boolean aliquotazero) {
        this.reducao = reducao;
        this.diferimento = diferimento;
        this.idCstIbsCbs = idCstIbsCbs;
        this.cclasstrib = cclasstrib;
        this.descricao = descricao;
        this.fundamentacaolegal = fundamentacaolegal;
        this.aliquotazero = aliquotazero;
    }

    public Double getReducao() {
        return reducao;
    }

    public Double getDiferimento() {
        return diferimento;
    }

    public int getIdCstIbsCbs() {
        return idCstIbsCbs;
    }

    public String getCclasstrib() {
        return cclasstrib;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getFundamentacaolegal() {
        return fundamentacaolegal;
    }

    public boolean isAliquotazero() {
        return aliquotazero;
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }
    
    
}
