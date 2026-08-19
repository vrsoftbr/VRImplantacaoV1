package vrimplantacao2.vo.cadastro.validacao;

/**
 *
 * @author wesley
 */
public class ProdutoBalancaValidacaoVO {

    private Long codigoAntigo;
    private Long codigoAtual;
    private String descricao;
    private Boolean pesavel;
    private Integer tipoEmbalagem;
    private Integer tipoEmbalagemAutomacao;
    private Long codigoBarras;

    public Long getCodigoAntigo() {
        return codigoAntigo;
    }

    public void setCodigoAntigo(Long codigoAntigo) {
        this.codigoAntigo = codigoAntigo;
    }

    public Long getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(Long codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getPesavel() {
        return pesavel;
    }

    public void setPesavel(Boolean pesavel) {
        this.pesavel = pesavel;
    }

    public Integer getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(Integer tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public Integer getTipoEmbalagemAutomacao() {
        return tipoEmbalagemAutomacao;
    }

    public void setTipoEmbalagemAutomacao(Integer tipoEmbalagemAutomacao) {
        this.tipoEmbalagemAutomacao = tipoEmbalagemAutomacao;
    }

    public Long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(Long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
}
