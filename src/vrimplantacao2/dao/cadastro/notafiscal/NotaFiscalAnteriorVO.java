package vrimplantacao2.dao.cadastro.notafiscal;

import java.util.Date;
import vrimplantacao2.vo.cadastro.notafiscal.TipoNota;
import vrimplantacao2.vo.importacao.Destinatario;
import vrimplantacao2.vo.importacao.NotaOperacao;

/**
 *
 * @author Leandro
 */
public class NotaFiscalAnteriorVO {
    
    private String sistema;
    private String loja;
    private NotaOperacao operacao;
    private String id;
    
    private Integer idNotaSaida;
    private Integer idNotaEntrada;
    
    private TipoNota tipoNota = TipoNota.NORMAL;
    private String modelo = "55";
    private String serie;
    private int numeroNota;
    private Date dataEmissao;
    private double valorProduto = 0;
    private double valorTotal = 0;
    
    private Destinatario.TipoDestinatario tipoDestinatario = Destinatario.TipoDestinatario.FORNECEDOR;
    private String idDestinatario;
    private String razaoSocial;
    private String cnpjCpf;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public NotaOperacao getOperacao() {
        return operacao;
    }

    public void setOperacao(NotaOperacao operacao) {
        this.operacao = operacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIdNotaSaida() {
        return idNotaSaida;
    }

    public void setIdNotaSaida(Integer idNotaSaida) {
        this.idNotaSaida = idNotaSaida;
    }

    public Integer getIdNotaEntrada() {
        return idNotaEntrada;
    }

    public void setIdNotaEntrada(Integer idNotaEntrada) {
        this.idNotaEntrada = idNotaEntrada;
    }

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota;
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

    public Destinatario.TipoDestinatario getTipoDestinatario() {
        return tipoDestinatario;
    }

    public void setTipoDestinatario(Destinatario.TipoDestinatario tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }
    
    
}
