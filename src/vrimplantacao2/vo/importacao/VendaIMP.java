package vrimplantacao2.vo.importacao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import vrimplantacao2.dao.cadastro.venda.DateTimePersister;
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoDesconto;

/**
 * Classe utilizada para importar vendas.
 * @author Leandro
 */
@DatabaseTable(tableName = "venda")
public class VendaIMP {

    public VendaIMP() {
    }
    
    @DatabaseField() private int VERSAO = 1;
    @DatabaseField(
            id = true, 
            canBeNull = false
    ) private String id;
    @DatabaseField(
            uniqueCombo = true
    ) private int numeroCupom;
    @DatabaseField(
            uniqueCombo = true
    ) private int ecf;
    @DatabaseField(
            uniqueCombo = true, 
            persisterClass = DateTimePersister.class
    ) private Date data;
    @DatabaseField() private String idClientePreferencial;    
    @DatabaseField(
            persisterClass = DateTimePersister.class
    ) private Date horaInicio;
    @DatabaseField(
            persisterClass = DateTimePersister.class
    ) private Date horaTermino;
    @DatabaseField() private boolean cancelado;
    @DatabaseField() private double subTotalImpressora = 0;
    @DatabaseField(
            persisterClass = TipoCancelamento.TipoCancelamentoPersister.class
    ) private TipoCancelamento tipoCancelamento;
    @DatabaseField() private String cpf;
    @DatabaseField() private double valorDesconto;
    @DatabaseField() private double valorAcrescimo;
    @DatabaseField() private boolean canceladoEmVenda = false;
    @DatabaseField() private String numeroSerie;
    @DatabaseField() private String modeloImpressora;
    @DatabaseField() private String nomeCliente;
    @DatabaseField() private String enderecoCliente;
    @DatabaseField() private String clienteEventual;
    @DatabaseField() private String chaveCfe;
    @DatabaseField() private String chaveNfCe;
    @DatabaseField() private String xml;
    @DatabaseField(
            persisterClass = TipoDesconto.TipoDescontoPersister.class
    ) private TipoDesconto tipoDesconto;
    @DatabaseField() private String chaveNfCeContingencia;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public int getNumeroCupom() {
        return numeroCupom;
    }

    public void setNumeroCupom(int numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public int getEcf() {
        return ecf;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getIdClientePreferencial() {
        return idClientePreferencial;
    }

    public void setIdClientePreferencial(String idClientePreferencial) {
        this.idClientePreferencial = idClientePreferencial;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(Date horaTermino) {
        this.horaTermino = horaTermino;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public double getSubTotalImpressora() {
        return subTotalImpressora;
    }

    public void setSubTotalImpressora(double subTotalImpressora) {
        this.subTotalImpressora = subTotalImpressora;
    }

    public TipoCancelamento getTipoCancelamento() {
        return tipoCancelamento;
    }

    public void setTipoCancelamento(TipoCancelamento tipoCancelamento) {
        this.tipoCancelamento = tipoCancelamento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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

    public boolean isCanceladoEmVenda() {
        return canceladoEmVenda;
    }

    public void setCanceladoEmVenda(boolean canceladoEmVenda) {
        this.canceladoEmVenda = canceladoEmVenda;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getModeloImpressora() {
        return modeloImpressora;
    }

    public void setModeloImpressora(String modeloImpressora) {
        this.modeloImpressora = modeloImpressora;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEnderecoCliente() {
        return enderecoCliente;
    }

    public void setEnderecoCliente(String enderecoCliente) {
        this.enderecoCliente = enderecoCliente;
    }

    public String getClienteEventual() {
        return clienteEventual;
    }

    public void setClienteEventual(String clienteEventual) {
        this.clienteEventual = clienteEventual;
    }

    public String getChaveCfe() {
        return chaveCfe;
    }

    public void setChaveCfe(String chaveCfe) {
        this.chaveCfe = chaveCfe;
    }

    public String getChaveNfCe() {
        return chaveNfCe;
    }

    public void setChaveNfCe(String chaveNfCe) {
        this.chaveNfCe = chaveNfCe;
    }

    public TipoDesconto getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public String getChaveNfCeContingencia() {
        return chaveNfCeContingencia;
    }

    public void setChaveNfCeContingencia(String chaveNfCeContingencia) {
        this.chaveNfCeContingencia = chaveNfCeContingencia;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
