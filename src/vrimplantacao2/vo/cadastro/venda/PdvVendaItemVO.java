package vrimplantacao2.vo.cadastro.venda;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoDesconto;

/**
 *
 * @author Leandro
 */
public class PdvVendaItemVO {
    
    private long id;// serial NOT NULL,
    private PdvVendaVO venda;// id_venda bigint NOT NULL,
    private int id_produto;// integer NOT NULL,
    private double quantidade = 0;// numeric(12,3) NOT NULL,
    private double precoVenda = 0;// numeric(11,2) NOT NULL,
    //private double valorTotal = 0;//valortotal numeric(11,2) NOT NULL,
    private int id_aliquota = 8;// integer NOT NULL,
    private boolean cancelado = false;// boolean NOT NULL,
    private double valorCancelado = 0;// numeric(11,2) NOT NULL,
    private TipoCancelamento tipoCancelamento;// id_tipocancelamento integer,
    private int matriculaCancelamento;// integer,
    private int contadorDoc = 0;// integer NOT NULL,
    private double valorDesconto = 0;// numeric(11,2) NOT NULL,
    private double valorAcrescimo = 0;// numeric(11,2) NOT NULL,
    private double valorDescontoCupom = 0;// numeric(11,2) NOT NULL,
    private double valorAcrescimoCupom = 0;// numeric(11,2) NOT NULL,
    private String regraCalculo = "A";// character varying(1) NOT NULL,
    private long codigoBarras = 0;// numeric(14,0) NOT NULL,
    private String unidadeMedida = "UN";// character varying(3) NOT NULL,
    private String totalizadorParcial = "";// character varying(7) NOT NULL,
    private int sequencia = 1;// integer NOT NULL,
    private double valorAcrescimoFixo = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private double valorDescontoPromocao = 0;// numeric(11,2),
    private boolean oferta = false;// boolean,
    private TipoDesconto tipoDesconto; //id_tipodesconto integer,
    private double custoComImposto = 0;// numeric(13,4),
    private double custoSemImposto = 0;// numeric(13,4),
    private double custoMedioComimposto = 0;// numeric(13,4),
    private double custoMedioSemImposto = 0;// numeric(13,4),
    private boolean aplicaDescontoPromocao = false;// boolean,
    private int id_tipoOferta = -1;// integer,
    private boolean atacado = false;// boolean,
    private Date data;

    public void setId(long id) {
        this.id = id;
    }

    public void setVenda(PdvVendaVO venda) {
        this.venda = venda;
    }

    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = MathUtils.round(precoVenda,2);
    }

    public void setId_aliquota(int id_aliquota) {
        this.id_aliquota = id_aliquota;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public void setValorCancelado(double valorCancelado) {
        this.valorCancelado = valorCancelado;
    }

    public void setTipoCancelamento(TipoCancelamento tipoCancelamento) {
        this.tipoCancelamento = tipoCancelamento;
    }

    public void setMatriculaCancelamento(int matriculaCancelamento) {
        this.matriculaCancelamento = matriculaCancelamento;
    }

    public void setContadorDoc(int contadorDoc) {
        this.contadorDoc = contadorDoc;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = MathUtils.round(valorDesconto, 2);
    }

    public void setValorAcrescimo(double valorAcrescimo) {
        this.valorAcrescimo = MathUtils.round(valorAcrescimo, 2);
    }

    public void setValorDescontoCupom(double valorDescontoCupom) {
        this.valorDescontoCupom = MathUtils.round(valorDescontoCupom, 2);
    }

    public void setValorAcrescimoCupom(double valorAcrescimoCupom) {
        this.valorAcrescimoCupom = MathUtils.round(valorAcrescimoCupom, 2);
    }

    public void setRegraCalculo(String regraCalculo) {
        this.regraCalculo = regraCalculo == null ? "A" : regraCalculo;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        unidadeMedida = Utils.acertarTexto(unidadeMedida, 3, "UN");
        this.unidadeMedida = unidadeMedida == null ? "UN" : unidadeMedida;
    }

    public void setTotalizadorParcial(String totalizadorParcial) {
        this.totalizadorParcial = totalizadorParcial != null ? totalizadorParcial : "";
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public void setValorAcrescimoFixo(double valorAcrescimoFixo) {
        this.valorAcrescimoFixo = MathUtils.round(valorAcrescimoFixo, 2);
    }

    public void setValorDescontoPromocao(double valorDescontoPromocao) {
        this.valorDescontoPromocao = MathUtils.round(valorDescontoPromocao, 2);
    }

    public void setOferta(boolean oferta) {
        this.oferta = oferta;
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = MathUtils.round(custoComImposto, 4);
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = MathUtils.round(custoSemImposto, 4);
    }

    public void setCustoMedioComImposto(double custoMedioComImposto) {
        this.custoMedioComimposto = MathUtils.round(custoMedioComImposto, 4);
    }

    public void setCustoMedioSemImposto(double custoMedioSemImposto) {
        this.custoMedioSemImposto = MathUtils.round(custoMedioSemImposto, 4);
    }

    public void setAplicaDescontoPromocao(boolean aplicaDescontoPromocao) {
        this.aplicaDescontoPromocao = aplicaDescontoPromocao;
    }

    public void setId_tipoOferta(int id_tipoOferta) {
        this.id_tipoOferta = id_tipoOferta;
    }

    public void setAtacado(boolean atacado) {
        this.atacado = atacado;
    }

    public long getId() {
        return id;
    }

    public PdvVendaVO getVenda() {
        return venda;
    }

    public int getId_produto() {
        return id_produto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public double getValorTotal() {
        return MathUtils.round(getQuantidade() * getPrecoVenda(), 2);
    }

    public int getId_aliquota() {
        return id_aliquota;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public double getValorCancelado() {
        return valorCancelado;
    }

    public TipoCancelamento getTipoCancelamento() {
        return tipoCancelamento;
    }

    public int getMatriculaCancelamento() {
        return matriculaCancelamento;
    }

    public int getContadorDoc() {
        return contadorDoc;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public double getValorAcrescimo() {
        return valorAcrescimo;
    }

    public double getValorDescontoCupom() {
        return valorDescontoCupom;
    }

    public double getValorAcrescimoCupom() {
        return valorAcrescimoCupom;
    }

    public String getRegraCalculo() {
        return regraCalculo;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public String getTotalizadorParcial() {
        return totalizadorParcial;
    }

    public int getSequencia() {
        return sequencia;
    }

    public double getValorAcrescimoFixo() {
        return valorAcrescimoFixo;
    }

    public double getValorDescontoPromocao() {
        return valorDescontoPromocao;
    }

    public boolean isOferta() {
        return oferta;
    }

    public TipoDesconto getTipoDesconto() {
        return tipoDesconto;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public double getCustoMedioComImposto() {
        return custoMedioComimposto;
    }

    public double getCustoMedioSemImposto() {
        return custoMedioSemImposto;
    }

    public boolean isAplicaDescontoPromocao() {
        return aplicaDescontoPromocao;
    }

    public int getId_tipoOferta() {
        return id_tipoOferta;
    }

    public boolean isAtacado() {
        return atacado;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
    
}
