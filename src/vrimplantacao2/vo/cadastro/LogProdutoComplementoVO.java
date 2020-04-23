package vrimplantacao2.vo.cadastro;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.NormaCompra;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoProduto;

/**
 *
 * @author Importacao
 */
public class LogProdutoComplementoVO {
    private int id = 0;
    private ProdutoVO produto;
    private int idLoja = 1;
    private Date dataMovimento;
    private Timestamp dataHora;
    private double custoSemImposto = 0;
    private double custoComImposto = 0;
    private double custoAnteriorSemImposto = 0;
    private double custoAnteriorComImposto = 0;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private String observacao = "";

    public void setId(int id) {
        this.id = id;
    }

    public void setProduto(ProdutoVO produto) {
        this.produto = produto;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = MathUtils.round(custoSemImposto, 4, 9999999D);
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = MathUtils.round(custoComImposto, 4, 9999999D);
    }

    public void setCustoAnteriorSemImposto(double custoAnteriorSemImposto) {
        this.custoAnteriorSemImposto = MathUtils.round(custoAnteriorSemImposto, 4, 9999999D);
    }
    
    public void setCustoAnteriorComImposto(double custoAnteriorComImposto) {
        this.custoAnteriorComImposto = MathUtils.round(custoAnteriorComImposto, 4, 9999999D);
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }

    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }

    public int getId() {
        return id;
    }

    public ProdutoVO getProduto() {
        return produto;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }
    
    public double getCustoAnteriorSemImposto() {
        return custoAnteriorSemImposto;
    }
    
    public double getCustoAnteriorComImposto() {
        return custoAnteriorComImposto;
    }   

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
