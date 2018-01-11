package vrimplantacao.vo.fiscal;

import java.util.ArrayList;
import java.util.List;

public class MapaResumoVO {

    public long id = 0;
    public int idLoja = 0;
    public String loja = "";
    public int ecf = 0;
    public String data = "";
    public int reducao = 0;
    public int contadorInicial = 0;
    public int contadorFinal = 0;
    public double gtInicial = 0;
    public double gtFinal = 0;
    public double cancelamento = 0;
    public double desconto = 0;
    public boolean lancamentoManual = false;
    public int contadorReinicio = 0;
    public String dataHoraEmissaoRz = "";
    public int contadorGerencial = 0;
    public int contadorCDC = 0;
    public double totalNaoFiscal = 0;
    public double acrescimo = 0;
    public List<MapaResumoItemVO> vItem = new ArrayList();
    public double valorContabil = 0;
    public int numeroControle = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public int getEcf() {
        return ecf;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getReducao() {
        return reducao;
    }

    public void setReducao(int reducao) {
        this.reducao = reducao;
    }

    public int getContadorInicial() {
        return contadorInicial;
    }

    public void setContadorInicial(int contadorInicial) {
        this.contadorInicial = contadorInicial;
    }

    public int getContadorFinal() {
        return contadorFinal;
    }

    public void setContadorFinal(int contadorFinal) {
        this.contadorFinal = contadorFinal;
    }

    public double getGtInicial() {
        return gtInicial;
    }

    public void setGtInicial(double gtInicial) {
        this.gtInicial = gtInicial;
    }

    public double getGtFinal() {
        return gtFinal;
    }

    public void setGtFinal(double gtFinal) {
        this.gtFinal = gtFinal;
    }

    public double getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(double cancelamento) {
        this.cancelamento = cancelamento;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public boolean isLancamentoManual() {
        return lancamentoManual;
    }

    public void setLancamentoManual(boolean lancamentoManual) {
        this.lancamentoManual = lancamentoManual;
    }

    public int getContadorReinicio() {
        return contadorReinicio;
    }

    public void setContadorReinicio(int contadorReinicio) {
        this.contadorReinicio = contadorReinicio;
    }

    public String getDataHoraEmissaoRz() {
        return dataHoraEmissaoRz;
    }

    public void setDataHoraEmissaoRz(String dataHoraEmissaoRz) {
        this.dataHoraEmissaoRz = dataHoraEmissaoRz;
    }

    public int getContadorGerencial() {
        return contadorGerencial;
    }

    public void setContadorGerencial(int contadorGerencial) {
        this.contadorGerencial = contadorGerencial;
    }

    public int getContadorCDC() {
        return contadorCDC;
    }

    public void setContadorCDC(int contadorCDC) {
        this.contadorCDC = contadorCDC;
    }

    public double getTotalNaoFiscal() {
        return totalNaoFiscal;
    }

    public void setTotalNaoFiscal(double totalNaoFiscal) {
        this.totalNaoFiscal = totalNaoFiscal;
    }

    public double getAcrescimo() {
        return acrescimo;
    }

    public void setAcrescimo(double acrescimo) {
        this.acrescimo = acrescimo;
    }

    public List<MapaResumoItemVO> getvItem() {
        return vItem;
    }

    public void setvItem(List<MapaResumoItemVO> vItem) {
        this.vItem = vItem;
    }

    public double getValorContabil() {
        return valorContabil;
    }

    public void setValorContabil(double valorContabil) {
        this.valorContabil = valorContabil;
    }

    public int getNumeroControle() {
        return numeroControle;
    }

    public void setNumeroControle(int numeroControle) {
        this.numeroControle = numeroControle;
    }

    public void calcularValorContabil() {
        this.valorContabil = (gtFinal - gtInicial) - cancelamento - desconto;
    }
}
