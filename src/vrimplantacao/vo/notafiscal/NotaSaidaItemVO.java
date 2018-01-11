package vrimplantacao.vo.notafiscal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotaSaidaItemVO implements Serializable {

    public long id = 0;
    public int idProduto = 0;
    public String produto = "";
    public double quantidade = 0;
    public int qtdEmbalagem = 0;
    public double valor = 0;
    public double valorTotal = 0;
    public double valorIpi = 0;
    public double valorTotalIpi = 0;
    public int idAliquota = 0;
    public String aliquota = "";
    public double valorBaseCalculo = 0;
    public double valorIcms = 0;
    public double valorBaseSubstituicao = 0;
    public double valorIcmsSubstituicao = 0;
    public double valorPisCofins = 0;
    public double valorBaseIpi = 0;
    public int idTipoEmbalagem = 0;
    public String tipoEmbalagem = "";
    public int idTipoPisCofinsDebito = 0;
    public int idTipoPisCofinsCredito = 0;
    public long codigoBarras = 0;
    public String cfop = "";
    public double valorDesconto = 0;
    public int ncm1 = 0;
    public int ncm2 = 0;
    public int ncm3 = 0;
    public int tipoIva = 0;
    public int idAliquotaPautaFiscal = -1;
    public double pesoLiquido = 0;
    public double valorIsento = 0;
    public double valorOutras = 0;
    public int situacaoTributaria = 0;
    public double valorUnitario = 0;
    public int idAliquotaDispensado = -1;
    public double valorIcmsDispensado = 0;
    public int tipoNaturezaReceita = -1;
    public long idNotaEntrada = 0;
    public int numeroNota = 0;
    public String dataEntrada = "";
    public int idTipoOrigemMercadoria = 0;
    public String localDesembaraco = "";
    public int numeroAdicao = 0;
    public int idEstadoDesembaraco = -1;
    public String dataDesembaraco = "";
    public double impostoMedio = 0;
    public int cstPisCofins = 0;
    public int idTipoSaida = 0;
    
    public List<NotaSaidaItemDesmembramentoVO> vDesmembramento = new ArrayList();

    public List<NotaSaidaItemDesmembramentoVO> getvDesmembramento() {
        return vDesmembramento;
    }

    public void setvDesmembramento(List<NotaSaidaItemDesmembramentoVO> vDesmembramento) {
        this.vDesmembramento = vDesmembramento;
    }

    public int getIdAliquotaPautaFiscal() {
        return idAliquotaPautaFiscal;
    }

    public void setIdAliquotaPautaFiscal(int idAliquotaPautaFiscal) {
        this.idAliquotaPautaFiscal = idAliquotaPautaFiscal;
    }

    public int getIdTipoPisCofinsCredito() {
        return idTipoPisCofinsCredito;
    }

    public void setIdTipoPisCofinsCredito(int idTipoPisCofinsCredito) {
        this.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
    }

    public double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public int getSituacaoTributaria() {
        return situacaoTributaria;
    }

    public void setSituacaoTributaria(int situacaoTributaria) {
        this.situacaoTributaria = situacaoTributaria;
    }

    public int getTipoIva() {
        return tipoIva;
    }

    public void setTipoIva(int tipoIva) {
        this.tipoIva = tipoIva;
    }

    public double getValorIsento() {
        return valorIsento;
    }

    public void setValorIsento(double valorIsento) {
        this.valorIsento = valorIsento;
    }

    public double getValorOutras() {
        return valorOutras;
    }

    public void setValorOutras(double valorOutras) {
        this.valorOutras = valorOutras;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public NotaSaidaItemVO getCopia() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(this);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));

        return (NotaSaidaItemVO) in.readObject();
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

    public double getValorTotalIpi() {
        return valorTotalIpi;
    }

    public void setValorTotalIpi(double valorTotalIpi) {
        this.valorTotalIpi = valorTotalIpi;
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

    public double getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(double valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public double getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(double valorIcms) {
        this.valorIcms = valorIcms;
    }

    public double getValorBaseSubstituicao() {
        return valorBaseSubstituicao;
    }

    public void setValorBaseSubstituicao(double valorBaseSubstituicao) {
        this.valorBaseSubstituicao = valorBaseSubstituicao;
    }

    public double getValorIcmsSubstituicao() {
        return valorIcmsSubstituicao;
    }

    public void setValorIcmsSubstituicao(double valorIcmsSubstituicao) {
        this.valorIcmsSubstituicao = valorIcmsSubstituicao;
    }

    public double getValorPisCofins() {
        return valorPisCofins;
    }

    public void setValorPisCofins(double valorPisCofins) {
        this.valorPisCofins = valorPisCofins;
    }

    public double getValorBaseIpi() {
        return valorBaseIpi;
    }

    public void setValorBaseIpi(double valorBaseIpi) {
        this.valorBaseIpi = valorBaseIpi;
    }

    public int getIdTipoEmbalagem() {
        return idTipoEmbalagem;
    }

    public void setIdTipoEmbalagem(int idTipoEmbalagem) {
        this.idTipoEmbalagem = idTipoEmbalagem;
    }

    public String getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(String tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public int getIdTipoPisCofinsDebito() {
        return idTipoPisCofinsDebito;
    }

    public void setIdTipoPisCofinsDebito(int idTipoPisCofinsDebito) {
        this.idTipoPisCofinsDebito = idTipoPisCofinsDebito;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
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

    public int getNumeroAdicao() {
        return numeroAdicao;
    }

    public void setNumeroAdicao(int numeroAdicao) {
        this.numeroAdicao = numeroAdicao;
    }

    public int getIdEstadoDesembaraco() {
        return idEstadoDesembaraco;
    }

    public void setIdEstadoDesembaraco(int idEstadoDesembaraco) {
        this.idEstadoDesembaraco = idEstadoDesembaraco;
    }

    public String getLocalDesembaraco() {
        return localDesembaraco;
    }

    public void setLocalDesembaraco(String localDesembaraco) {
        this.localDesembaraco = localDesembaraco;
    }

    public String getDataDesembaraco() {
        return dataDesembaraco;
    }

    public void setDataDesembaraco(String dataDesembaraco) {
        this.dataDesembaraco = dataDesembaraco;
    }
}
