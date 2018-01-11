package vrimplantacao.vo.notafiscal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class NotaEntradaItemVO implements Serializable {

    public long id = 0;
    public int idProduto = 0;
    public String produto = "";
    public double quantidade = 0;
    public int qtdEmbalagem = 0;
    public double valor = 0;
    public double valorEmbalagem = 0;
    public double valorTotal = 0;
    public double valorIpi = 0;
    public int idAliquota = 0;
    public String aliquota = "";
    public double custoComImposto = 0;
    public double custoSemImposto = 0;
    public double valorTotalFinal = 0;
    public int idTipoEmbalagem = 0;
    public String tipoEmbalagem = "";
    public double custoComImpostoAnterior = 0;
    public double valorIcms = 0;
    public double valorIcmsSubstituicao = 0;
    public double valorIcmsSubstituicaoXml = 0;
    public int ncm1 = 0;
    public int ncm2 = 0;
    public int ncm3 = 0;
    public int excecao = 0;
    public String codigoExterno = "";
    public boolean verificaCustoTabela = false;
    public double valorBaseCalculo = 0;
    public List<NotaEntradaItemDesmembramentoVO> vDesmembramento = new ArrayList();
    public double valorBonificacao = 0;
    public double valorVerba = 0;
    public double percentualPerda = 0;
    public double quantidadeDevolvida = 0;
    public double valorPisCofins = 0;
    public int idTipoPisCofins = 0;
    public boolean contabilizaValor = false;
    public double valorBaseSubstituicao = 0;
    public int idFamiliaProduto = 0;
    public double custoSemPerdaSemImposto = 0;
    public long codigoBarras = 0;
    public long codigoCaixa = 0;
    public String cfop = "";
    public double valorIsento = 0;
    public double valorOutras = 0;
    public int situacaoTributaria = 0;
    public double valorOutrasDespesas = 0;
    public double valorFrete = 0;
    public double valorDesconto = 0;
    public double custoNotaLog = 0;
    public double valorAcrescimoCusto = 0;
    public double valorAcrescimoImposto = 0;
    public double valorDescontoCusto = 0;
    public double valorDescontoImposto = 0;
    public int idAliquotaCreditoForaEstado = 0;
    public double valorBaseCalculoCreditoForaEstado = 0;
    public double valorIsentoCreditoForaEstado = 0;
    public double valorIcmsCreditoForaEstado = 0;
    public String cfopNota = "";
    public int idTipoEntrada = 0;
    
    public NotaEntradaItemVO getCopia() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(this);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));

        return (NotaEntradaItemVO) in.readObject();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = valorIpi;
    }

    public int getIdAliquota() {
        return idAliquota;
    }

    public void setIdAliquota(int idAliquota) {
        this.idAliquota = idAliquota;
    }

    public String getAliquota() {
        return aliquota;
    }

    public void setAliquota(String aliquota) {
        this.aliquota = aliquota;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = custoComImposto;
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = custoSemImposto;
    }

    public double getValorTotalFinal() {
        return valorTotalFinal;
    }

    public void setValorTotalFinal(double valorTotalFinal) {
        this.valorTotalFinal = valorTotalFinal;
    }

    public int getIdTipoEmbalagem() {
        return idTipoEmbalagem;
    }

    public void setIdTipoEmbalagem(int idTipoEmbalagem) {
        this.idTipoEmbalagem = idTipoEmbalagem;
    }

    public double getCustoComImpostoAnterior() {
        return custoComImpostoAnterior;
    }

    public void setCustoComImpostoAnterior(double custoComImpostoAnterior) {
        this.custoComImpostoAnterior = custoComImpostoAnterior;
    }

    public double getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(double valorIcms) {
        this.valorIcms = valorIcms;
    }

    public double getValorIcmsSubstituicao() {
        return valorIcmsSubstituicao;
    }

    public void setValorIcmsSubstituicao(double valorIcmsSubstituicao) {
        this.valorIcmsSubstituicao = valorIcmsSubstituicao;
    }

    public int getNcm1() {
        return ncm1;
    }

    public void setNcm1(int ncm1) {
        this.ncm1 = ncm1;
    }

    public int getNcm2() {
        return ncm2;
    }

    public void setNcm2(int ncm2) {
        this.ncm2 = ncm2;
    }

    public int getNcm3() {
        return ncm3;
    }

    public void setNcm3(int ncm3) {
        this.ncm3 = ncm3;
    }

    public int getExcecao() {
        return excecao;
    }

    public void setExcecao(int excecao) {
        this.excecao = excecao;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = codigoExterno;
    }

    public boolean isVerificaCustoTabela() {
        return verificaCustoTabela;
    }

    public void setVerificaCustoTabela(boolean verificaCustoTabela) {
        this.verificaCustoTabela = verificaCustoTabela;
    }

    public double getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(double valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public List<NotaEntradaItemDesmembramentoVO> getvDesmembramento() {
        return vDesmembramento;
    }

    public void setvDesmembramento(List<NotaEntradaItemDesmembramentoVO> vDesmembramento) {
        this.vDesmembramento = vDesmembramento;
    }

    public double getValorBonificacao() {
        return valorBonificacao;
    }

    public void setValorBonificacao(double valorBonificacao) {
        this.valorBonificacao = valorBonificacao;
    }

    public double getValorVerba() {
        return valorVerba;
    }

    public void setValorVerba(double valorVerba) {
        this.valorVerba = valorVerba;
    }

    public double getPercentualPerda() {
        return percentualPerda;
    }

    public void setPercentualPerda(double percentualPerda) {
        this.percentualPerda = percentualPerda;
    }

    public double getQuantidadeDevolvida() {
        return quantidadeDevolvida;
    }

    public void setQuantidadeDevolvida(double quantidadeDevolvida) {
        this.quantidadeDevolvida = quantidadeDevolvida;
    }

    public double getValorPisCofins() {
        return valorPisCofins;
    }

    public void setValorPisCofins(double valorPisCofins) {
        this.valorPisCofins = valorPisCofins;
    }

    public int getIdTipoPisCofins() {
        return idTipoPisCofins;
    }

    public void setIdTipoPisCofins(int idTipoPisCofins) {
        this.idTipoPisCofins = idTipoPisCofins;
    }

    public boolean isContabilizaValor() {
        return contabilizaValor;
    }

    public void setContabilizaValor(boolean contabilizaValor) {
        this.contabilizaValor = contabilizaValor;
    }

    public double getValorBaseSubstituicao() {
        return valorBaseSubstituicao;
    }

    public void setValorBaseSubstituicao(double valorBaseSubstituicao) {
        this.valorBaseSubstituicao = valorBaseSubstituicao;
    }

    public int getIdFamiliaProduto() {
        return idFamiliaProduto;
    }

    public void setIdFamiliaProduto(int idFamiliaProduto) {
        this.idFamiliaProduto = idFamiliaProduto;
    }

    public double getCustoSemPerdaSemImposto() {
        return custoSemPerdaSemImposto;
    }

    public void setCustoSemPerdaSemImposto(double custoSemPerdaSemImposto) {
        this.custoSemPerdaSemImposto = custoSemPerdaSemImposto;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public long getCodigoCaixa() {
        return codigoCaixa;
    }

    public void setCodigoCaixa(long codigoCaixa) {
        this.codigoCaixa = codigoCaixa;
    }

    public String getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(String tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = cfop;
    }
}
