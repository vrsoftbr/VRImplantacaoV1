package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.enums.TipoDestinatario;
import java.util.ArrayList;
import java.util.Date;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNfe;
import vrimplantacao2.vo.cadastro.notafiscal.TipoFreteNotaFiscal;
import vrimplantacao2.vo.cadastro.notafiscal.TipoNota;

/**
 * Classe para fazer a importação de notas fiscais.
 * @author Leandro
 */
public class NotaFiscalIMP {
    
    private String id;
    private NotaOperacao operacao = NotaOperacao.ENTRADA;
    private TipoNota tipoNota = TipoNota.NORMAL;
    
    private TipoDestinatario tipoDestinatario = TipoDestinatario.FORNECEDOR;
    private String idDestinatario; //Pode ser Cliente Eventual ou Fornecedor
    
    private String modelo = "55";
    private String serie;
    private int numeroNota;
    private Date dataEmissao;
    private Date dataEntradaSaida;
    private double valorIpi = 0;
    private double valorFrete = 0;
    private double valorOutrasDespesas = 0;
    private double valorProduto = 0;
    private double valorTotal = 0;
    //private double valorBaseCalculo = 0;
    private double valorIcms = 0;
    //private double valorBaseSubstituicao = 0;
    private double valorIcmsSubstituicao = 0;
    //private double valorBaseIpi = 0;
    private double valorSeguro = 0;
    private double valorDesconto = 0;
    //private double valorGuiaSubstituicao = 0;
    private boolean impressao = true;
    private boolean finalizada = true;
    private boolean produtorRural = false;
    private Motorista motorista;
    private TipoFreteNotaFiscal tipoFreteNotaFiscal = TipoFreteNotaFiscal.PROPRIO_REMETENTE;
    private Transportador transportador;
    private String informacaoComplementar = "";
    private int volume = 1;
    private double pesoLiquido = 0;
    private double pesoBruto = 0;
    private SituacaoNfe situacaoNfe = SituacaoNfe.AUTORIZADA;
    private String chaveNfe = "";
    private Date dataHoraAlteracao;
    private String xml;
    private final ArrayList<NotaFiscalItemIMP> itens = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String... ids) {
        StringBuilder builder = new StringBuilder();
        for (String key: ids) {
            builder.append(key);
        }
        this.id = builder.toString();
    }

    public NotaOperacao getOperacao() {
        return operacao;
    }

    public void setOperacao(NotaOperacao operacao) {
        this.operacao = operacao;
    }

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota;
    }

    public TipoDestinatario getTipoDestinatario() {
        return tipoDestinatario;
    }

    public void setTipoDestinatario(TipoDestinatario tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario == null ? TipoDestinatario.FORNECEDOR : tipoDestinatario;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataEntradaSaida() {
        return dataEntradaSaida;
    }

    public void setDataEntradaSaida(Date dataEntradaSaida) {
        this.dataEntradaSaida = dataEntradaSaida;
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = valorIpi;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(double valorFrete) {
        this.valorFrete = valorFrete;
    }

    public double getValorOutrasDespesas() {
        return valorOutrasDespesas;
    }

    public void setValorOutrasDespesas(double valorOutrasDespesas) {
        this.valorOutrasDespesas = valorOutrasDespesas;
    }

    public double getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(double valorProduto) {
        this.valorProduto = valorProduto;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
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

    public double getValorSeguro() {
        return valorSeguro;
    }

    public void setValorSeguro(double valorSeguro) {
        this.valorSeguro = valorSeguro;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public boolean isImpressao() {
        return impressao;
    }

    public void setImpressao(boolean impressao) {
        this.impressao = impressao;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    public boolean isProdutorRural() {
        return produtorRural;
    }

    public void setProdutorRural(boolean produtorRural) {
        this.produtorRural = produtorRural;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public TipoFreteNotaFiscal getTipoFreteNotaFiscal() {
        return tipoFreteNotaFiscal;
    }

    public void setTipoFreteNotaFiscal(TipoFreteNotaFiscal tipoFreteNotaFiscal) {
        this.tipoFreteNotaFiscal = tipoFreteNotaFiscal;
    }

    public Transportador getTransportador() {
        return transportador;
    }

    public void setTransportador(Transportador transportador) {
        this.transportador = transportador;
    }

    public String getInformacaoComplementar() {
        return informacaoComplementar;
    }

    public void setInformacaoComplementar(String informacaoComplementar) {
        this.informacaoComplementar = informacaoComplementar;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public SituacaoNfe getSituacaoNfe() {
        return situacaoNfe;
    }

    public void setSituacaoNfe(SituacaoNfe situacaoNfe) {
        this.situacaoNfe = situacaoNfe;
    }

    public String getChaveNfe() {
        return chaveNfe;
    }

    public void setChaveNfe(String chaveNfe) {
        this.chaveNfe = chaveNfe;
    }

    public Date getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public void setDataHoraAlteracao(Date dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public ArrayList<NotaFiscalItemIMP> getItens() {
        return itens;
    }
    
    public NotaFiscalItemIMP addItem() {
        NotaFiscalItemIMP item = new NotaFiscalItemIMP(this);
        itens.add(item);
        return item;
    }
    
}
