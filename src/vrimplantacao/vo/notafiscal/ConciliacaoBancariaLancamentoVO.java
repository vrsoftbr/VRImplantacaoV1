package vrimplantacao.vo.notafiscal;

public class ConciliacaoBancariaLancamentoVO {

    public long id = 0;
    public int idContaContabilFinanceiro = 0;
    public String contaContabilFinanceiro = "";
    public int idTipoInscricao = 0;
    public long cnpj = 0;
    public double valorCredito = 0;
    public double valorDebito = 0;
    public String observacao = "";
    public int idContaContabilFiscalCredito = 0;
    public int idContaContabilFiscalDebito = 0;
    public int contaReduzidaCredito = 0;
    public int contaReduzidaDebito = 0;
    public boolean transferencia = false;
    public int idBanco = 0;
    public String agencia = "";
    public String conta = "";
    public int idPagarFornecedorParcela = -1;
    public int idReceberCaixaItem = -1;
    public int idReceberChequeItem = -1;
    public long idReceberContratoItem = -1;
    public int idReceberConveniadoItem = -1;
    public int idReceberCreditoRotativoItem = -1;
    public long idReceberDevolucaoItem = -1;
    public long idReceberVendaPrazoItem = -1;
    public long idReceberVerbaItem = -1;
    public int idReceberOutrasReceitasItem = -1;

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getContaContabilFinanceiro() {
        return contaContabilFinanceiro;
    }

    public void setContaContabilFinanceiro(String contaContabilFinanceiro) {
        this.contaContabilFinanceiro = contaContabilFinanceiro;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public int getIdContaContabilFinanceiro() {
        return idContaContabilFinanceiro;
    }

    public void setIdContaContabilFinanceiro(int idContaContabilFinanceiro) {
        this.idContaContabilFinanceiro = idContaContabilFinanceiro;
    }

    public int getIdContaContabilFiscalCredito() {
        return idContaContabilFiscalCredito;
    }

    public void setIdContaContabilFiscalCredito(int idContaContabilFiscalCredito) {
        this.idContaContabilFiscalCredito = idContaContabilFiscalCredito;
    }

    public int getIdContaContabilFiscalDebito() {
        return idContaContabilFiscalDebito;
    }

    public void setIdContaContabilFiscalDebito(int idContaContabilFiscalDebito) {
        this.idContaContabilFiscalDebito = idContaContabilFiscalDebito;
    }

    public int getContaReduzidaCredito() {
        return contaReduzidaCredito;
    }

    public void setContaReduzidaCredito(int contaReduzidaCredito) {
        this.contaReduzidaCredito = contaReduzidaCredito;
    }

    public int getContaReduzidaDebito() {
        return contaReduzidaDebito;
    }

    public void setContaReduzidaDebito(int contaReduzidaDebito) {
        this.contaReduzidaDebito = contaReduzidaDebito;
    }

    public int getIdTipoInscricao() {
        return idTipoInscricao;
    }

    public void setIdTipoInscricao(int idTipoInscricao) {
        this.idTipoInscricao = idTipoInscricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isTransferencia() {
        return transferencia;
    }

    public void setTransferencia(boolean transferencia) {
        this.transferencia = transferencia;
    }

    public double getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(double valorCredito) {
        this.valorCredito = valorCredito;
    }

    public double getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(double valorDebito) {
        this.valorDebito = valorDebito;
    }
}
