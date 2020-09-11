package vrimplantacao2.vo.cadastro.venda;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoDesconto;

/**
 *
 * @author Leandro
 */
public class PdvVendaVO {
    
    private long id;// serial NOT NULL,
    private int id_loja;// integer NOT NULL,
    private int numeroCupom;// integer NOT NULL,
    private int ecf;// integer NOT NULL,
    private Date data = new Date();// date NOT NULL,
    private int id_clientePreferencial;// integer,
    private int matricula;// integer,
    private Time horaInicio = new Time(new Date().getTime());// time without time zone NOT NULL,
    private Time horaTermino = new Time(new Date().getTime());// time without time zone NOT NULL,
    private boolean cancelado = false;// boolean NOT NULL,
    private double subTotalImpressora = 0;// numeric(11,2) NOT NULL,
    private int matriculaCancelamento;// integer,
    private TipoCancelamento tipoCancelamento = null;// id_tipocancelamento integer,
    private long cpf = 0;// numeric(14,0) NOT NULL,
    private int contadorDoc = 0;// integer NOT NULL,
    private double valorDesconto = 0;// numeric(11,2) NOT NULL,
    private double valorAcrescimo = 0;// numeric(11,2) NOT NULL,
    private boolean canceladoEmVenda = false;// boolean NOT NULL,
    private String numeroSerie = "";// character varying(20) NOT NULL,
    private int mfAdicional = 0;// integer NOT NULL,
    private String modeloImpressora = "";// character varying(20) NOT NULL,
    private int numeroUsuario = 0;// integer NOT NULL,
    private String nomeCliente = "";// character varying(45) NOT NULL,
    private String enderecoCliente = "";// character varying(50) NOT NULL,
    private int id_clienteEventual;// integer,
    private String chaveCfe = "";// character varying(50) DEFAULT ''::character varying,
    private long cpfCrm = 0;// numeric(14,0),
    private long cpfCnpjEntidade = 0;// numeric(14,0),
    private String razaoSocialEntidade = "";// character varying(40),
    private String chaveNfce = "";// character varying(44),
    private TipoDesconto tipoDesconto = null;// id_tipoDesconto integer,
    private String chaveNfceContingencia = "";// character varying(44)
    private String xml;//text
    private List<PdvVendaItemVO> itens = new ArrayList<>();

    public void setId(long id) {
        this.id = id;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public void setNumeroCupom(int numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public void setData(Date data) {
        this.data = data != null ? data : new Date();
    }

    public void setId_clientePreferencial(int id_clientePreferencial) {
        this.id_clientePreferencial = id_clientePreferencial;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio != null ? horaInicio : new Time(new Date().getTime());
    }

    public void setHoraTermino(Time horaTermino) {
        this.horaTermino = horaTermino != null ? horaTermino : new Time(new Date().getTime());
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public void setSubTotalImpressora(double subTotalImpressora) {
        this.subTotalImpressora = subTotalImpressora;
    }

    public void setMatriculaCancelamento(int matriculaCancelamento) {
        this.matriculaCancelamento = matriculaCancelamento;
    }

    public void setTipoCancelamento(TipoCancelamento tipoCancelamento) {
        this.tipoCancelamento = tipoCancelamento;
    }

    public void setCpf(long cpf) {
        this.cpf = cpf;
    }

    public void setContadorDoc(int contadorDoc) {
        this.contadorDoc = contadorDoc;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public void setValorAcrescimo(double valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public void setCanceladoEmVenda(boolean canceladoEmVenda) {
        this.canceladoEmVenda = canceladoEmVenda;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = Utils.acertarTexto(numeroSerie, 20);
    }

    public void setMfAdicional(int mfAdicional) {
        this.mfAdicional = mfAdicional;
    }

    public void setModeloImpressora(String modeloImpressora) {
        this.modeloImpressora = Utils.acertarTexto(modeloImpressora, 20);
    }

    public void setNumeroUsuario(int numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = Utils.acertarTexto(chaveCfe, 50);
    }

    public void setEnderecoCliente(String enderecoCliente) {
        this.enderecoCliente = Utils.acertarTexto(enderecoCliente, 50, "");
    }

    public void setId_clienteEventual(int id_clienteEventual) {
        this.id_clienteEventual = id_clienteEventual;
    }

    public void setChaveCfe(String chaveCfe) {
        this.chaveCfe = Utils.acertarTexto(chaveCfe, 44);
    }

    public void setCpfCrm(long cpfCrm) {
        this.cpfCrm = cpfCrm;
    }

    public void setCpfCnpjEntidade(long cpfCnpjEntidade) {
        this.cpfCnpjEntidade = cpfCnpjEntidade;
    }

    public void setRazaoSocialEntidade(String razaoSocialEntidade) {
        this.razaoSocialEntidade = Utils.acertarTexto(razaoSocialEntidade, 40);
    }

    public void setChaveNfce(String chaveNfce) {
        this.chaveNfce = Utils.acertarTexto(chaveNfce, 44);
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public void setChaveNfceContingencia(String chaveNfceContingencia) {
        this.chaveNfceContingencia = Utils.acertarTexto(chaveNfceContingencia, 44);
    }

    public long getId() {
        return id;
    }

    public int getId_loja() {
        return id_loja;
    }

    public int getNumeroCupom() {
        return numeroCupom;
    }

    public int getEcf() {
        return ecf;
    }

    public Date getData() {
        return data;
    }

    public int getId_clientePreferencial() {
        return id_clientePreferencial;
    }

    public int getMatricula() {
        return matricula;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public Time getHoraTermino() {
        return horaTermino;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public double getSubTotalImpressora() {
        return subTotalImpressora;
    }

    public int getMatriculaCancelamento() {
        return matriculaCancelamento;
    }

    public TipoCancelamento getTipoCancelamento() {
        return tipoCancelamento;
    }

    public long getCpf() {
        return cpf;
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

    public boolean isCanceladoEmVenda() {
        return canceladoEmVenda;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public int getMfAdicional() {
        return mfAdicional;
    }

    public String getModeloImpressora() {
        return modeloImpressora;
    }

    public int getNumeroUsuario() {
        return numeroUsuario;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getEnderecoCliente() {
        return enderecoCliente;
    }

    public int getId_clienteEventual() {
        return id_clienteEventual;
    }

    public String getChaveCfe() {
        return chaveCfe;
    }

    public long getCpfCrm() {
        return cpfCrm;
    }

    public long getCpfCnpjEntidade() {
        return cpfCnpjEntidade;
    }

    public String getRazaoSocialEntidade() {
        return razaoSocialEntidade;
    }

    public String getChaveNfce() {
        return chaveNfce;
    }

    public TipoDesconto getTipoDesconto() {
        return tipoDesconto;
    }

    public String getChaveNfceContingencia() {
        return chaveNfceContingencia;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public List<PdvVendaItemVO> getItens() {
        return itens;
    }

}
