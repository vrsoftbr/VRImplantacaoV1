package vrimplantacao2.vo.cadastro.venda;

import java.util.Date;

/**
 *
 * @author guilhermegomes
 */
public class PdvVendaPromocaoPontuacaoVO {
    
    private long id;
    private long idVenda;
    private int idPromocao;
    private double ponto;
    private long cnpj;
    private int idSituacaoPromocaoPontuacao;
    private boolean lancamentoManual;
    private int idLoja;
    private Date dataCompra;
    private Date dataExpiracao;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(long idVenda) {
        this.idVenda = idVenda;
    }

    public int getIdPromocao() {
        return idPromocao;
    }

    public void setIdPromocao(int idPromocao) {
        this.idPromocao = idPromocao;
    }

    public double getPonto() {
        return ponto;
    }

    public void setPonto(double ponto) {
        this.ponto = ponto;
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public int getIdSituacaoPromocaoPontuacao() {
        return idSituacaoPromocaoPontuacao;
    }

    public void setIdSituacaoPromocaoPontuacao(int idSituacaoPromocaoPontuacao) {
        this.idSituacaoPromocaoPontuacao = idSituacaoPromocaoPontuacao;
    }

    public boolean isLancamentoManual() {
        return lancamentoManual;
    }

    public void setLancamentoManual(boolean lancamentoManual) {
        this.lancamentoManual = lancamentoManual;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public Date getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(Date dataCompra) {
        this.dataCompra = dataCompra;
    }

    public Date getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }
}
