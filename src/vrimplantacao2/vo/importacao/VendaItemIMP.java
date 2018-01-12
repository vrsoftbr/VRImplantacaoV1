package vrimplantacao2.vo.importacao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoDesconto;

/**
 * Classe para importar itens da venda.
 * @author Leandro
 */
@DatabaseTable(tableName = "vendaitem")
public class VendaItemIMP {
    
    @DatabaseField(id = true, canBeNull = false)
    private String id;
    @DatabaseField(canBeNull = false)
    private int sequencia;
    @DatabaseField(foreign = true, canBeNull = false, index = true)
    private VendaIMP venda;
    @DatabaseField()
    private String produto;
    @DatabaseField()
    private String descricaoReduzida;
    @DatabaseField()
    private double quantidade;
    @DatabaseField()
    private double precoVenda;
    @DatabaseField()
    private double totalBruto;
    @DatabaseField()
    private boolean cancelado;
    @DatabaseField(persisterClass = TipoCancelamento.TipoCancelamentoPersister.class)
    private TipoCancelamento tipoCancelamento;
    @DatabaseField()
    private double valorDesconto;
    @DatabaseField()
    private double valorAcrescimo;
    @DatabaseField()
    private String codigoBarras;
    @DatabaseField()
    private String unidadeMedida;
    @DatabaseField(persisterClass = TipoDesconto.TipoDescontoPersister.class)
    private TipoDesconto tipoDesconto;
    @DatabaseField()
    private int icmsCst = 0;
    @DatabaseField()
    private double icmsAliq = 0;
    @DatabaseField()
    private double icmsReduzido = 0;
    @DatabaseField()
    private int contadorDoc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public VendaIMP getVenda() {
        return venda;
    }

    public void setVenda(VendaIMP venda) {
        this.venda = venda;
    }
    
    public void setVenda(String id) {
        if (this.venda == null) {
            this.venda = new VendaIMP();
        }
        this.venda.setId(id);
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public double getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(double totalBruto) {
        this.totalBruto = totalBruto;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public TipoCancelamento getTipoCancelamento() {
        return tipoCancelamento;
    }

    public void setTipoCancelamento(TipoCancelamento tipoCancelamento) {
        this.tipoCancelamento = tipoCancelamento;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorAcrescimo() {
        return valorAcrescimo;
    }

    public void setValorAcrescimo(double valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public TipoDesconto getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public int getIcmsCst() {
        return icmsCst;
    }

    public void setIcmsCst(int icmsCst) {
        this.icmsCst = icmsCst;
    }

    public double getIcmsAliq() {
        return icmsAliq;
    }

    public void setIcmsAliq(double icmsAliq) {
        this.icmsAliq = icmsAliq;
    }

    public int getContadorDoc() {
        return contadorDoc;
    }

    public void setContadorDoc(int contadorDoc) {
        this.contadorDoc = contadorDoc;
    }

    public double getIcmsReduzido() {
        return icmsReduzido;
    }

    public void setIcmsReduzido(double icmsReduzido) {
        this.icmsReduzido = icmsReduzido;
    }
   
}