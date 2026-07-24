package vrimplantacao2.vo.enums;

/**
 * Classe que representa o CST (Reforma Tributaria).
 * @author Wesley
 */
public class ClassificacaoTributaria {
       
    private final int id;
    private final Double reducao;
    private final Double diferimento;
    private final int idCstIbsCbs;
    private final String cclasstrib;
    private final String descricao;
    private final String fundamentacaolegal;
    private final boolean aliquotazero;
    private final int idSituacaoCadastro = 1;

    public ClassificacaoTributaria(int id, Double reducao, Double diferimento, int idCstIbsCbs, String cclasstrib, String descricao, String fundamentacaolegal, boolean aliquotazero) {
        this.id = id;
        this.reducao = reducao;
        this.diferimento = diferimento;
        this.idCstIbsCbs = idCstIbsCbs;
        this.cclasstrib = cclasstrib;
        this.descricao = descricao;
        this.fundamentacaolegal = fundamentacaolegal;
        this.aliquotazero = aliquotazero;
    }

    public int getId() {
        return id;
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

    public int getIdSituacaoCadastro() {
        return idSituacaoCadastro;
    }
}
