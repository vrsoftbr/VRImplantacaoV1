package vrimplantacao.vo.vrimplantacao;

public class ConveniadoTransacaoVO {
    public int id_conveniado = 0;
    public int ecf = 0;
    public int numerocupom = 0;
    public String datahora = "";
    public int id_loja = 1;
    public double valor = 0;
    public int id_situacaotransacaoconveniado = 0;
    public boolean lancamentomanual = true;
    public int matricula = 500001;
    public String datamovimento = "";
    public boolean finalizado = true;
    public int id_tiposervicoconvenio = 1;
    public String observacao = "";

    public int getId_conveniado() {
        return id_conveniado;
    }

    public void setId_conveniado(int id_conveniado) {
        this.id_conveniado = id_conveniado;
    }

    public int getEcf() {
        return ecf;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public int getNumerocupom() {
        return numerocupom;
    }

    public void setNumerocupom(int numerocupom) {
        this.numerocupom = numerocupom;
    }

    public String getDatahora() {
        return datahora;
    }

    public void setDatahora(String datahora) {
        this.datahora = datahora;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getId_situacaotransacaoconveniado() {
        return id_situacaotransacaoconveniado;
    }

    public void setId_situacaotransacaoconveniado(int id_situacaotransacaoconveniado) {
        this.id_situacaotransacaoconveniado = id_situacaotransacaoconveniado;
    }

    public boolean isLancamentomanual() {
        return lancamentomanual;
    }

    public void setLancamentomanual(boolean lancamentomanual) {
        this.lancamentomanual = lancamentomanual;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getDatamovimento() {
        return datamovimento;
    }

    public void setDatamovimento(String datamovimento) {
        this.datamovimento = datamovimento;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public int getId_tiposervicoconvenio() {
        return id_tiposervicoconvenio;
    }

    public void setId_tiposervicoconvenio(int id_tiposervicoconvenio) {
        this.id_tiposervicoconvenio = id_tiposervicoconvenio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
