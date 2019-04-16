package vrimplantacao2.vo.cadastro.notafiscal;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public class NotaSaida implements Nota {

    @Override
    public int getIndicadorNota() {
        return Nota.NOTA_SAIDA;
    }

    private int id;// integer NOT NULL DEFAULT nextval('notasaida_id_seq'::regclass),
    private int idLoja;//id_loja integer NOT NULL,
    private int numeroNota;// integer NOT NULL,
    private TipoNota tipoNota = TipoNota.NORMAL;// id_tiponota integer NOT NULL,
    private int idFornecedor = -1;//id_fornecedordestinatario integer,
    private int idClienteEventual = -1;//id_clienteeventualdestinatario integer,
    private int idTipoSaida = -1;// id_tiposaida integer NOT NULL,
    private Timestamp dataHoraEmissao;// timestamp without time zone NOT NULL,
    private Date dataSaida;// date NOT NULL,
    private double valorIpi = 0;// numeric(11,2) NOT NULL,
    private double valorFrete = 0;// numeric(11,2) NOT NULL,
    private double valorOutrasDespesas = 0;// numeric(11,2) NOT NULL,
    private double valorProduto = 0;// numeric(11,2) NOT NULL,
    private double valorTotal = 0;// numeric(11,2) NOT NULL,
    private double valorBaseCalculo = 0;// numeric(11,2) NOT NULL,
    private double valorIcms = 0;// numeric(11,2) NOT NULL,
    private double valorBaseSubstituicao = 0;// numeric(11,2) NOT NULL,
    private double valorIcmsSubstituicao = 0;// numeric(11,2) NOT NULL,
    private double valorSeguro = 0;// numeric(11,2) NOT NULL,
    private double valorDesconto = 0;// numeric(11,2) NOT NULL,
    private boolean impressao = true;//boolean NOT NULL,
    private SituacaoNotaSaida situacaoNotaSaida = SituacaoNotaSaida.FINALIZADO;// id_situacaonotasaida integer NOT NULL,
    private int idMotoristaTransportador = -1;// id_motoristatransportador integer,
    private int idFornecedorTransportador = -1;//id_fornecedortransportador integer,
    private int idClienteEventualTransportador = -1;//id_clienteeventualtransportador integer,
    private String placa = "";// character varying(7) NOT NULL,
    private int idTipoDevolucao = -1;//id_tipodevolucao integer,
    private String informacaoComplementar = "";// character varying(1000) NOT NULL,
    private String senha = "";// character varying(8) NOT NULL,
    private int tipoLocalBaixa = -1;// integer NOT NULL,
    private double valorBaseIpi = 0;// numeric(11,2) NOT NULL,
    private int volume = 1;// integer NOT NULL,
    private double pesoLiquido = 0;// numeric(12,3) NOT NULL,
    private SituacaoNfe situacaoNfe = SituacaoNfe.NAO_TRANSMITIDA;//id_situacaonfe integer NOT NULL DEFAULT 0,
    private String chaveNfe = "";// character varying(44) NOT NULL DEFAULT ''::character varying,
    private String reciboNfe;// character varying(15) DEFAULT ''::character varying,
    private String motivoRejeicaoNfe;// character varying(200) DEFAULT ''::character varying,
    private String protocoloRecebimentoNfe;// character varying(15) DEFAULT ''::character varying,
    private Timestamp dataHoraRecebimentoNfe;// timestamp without time zone,
    private String justificativaCancelamentoNfe;// character varying(200) DEFAULT ''::character varying,
    private String protocoloCancelamentoNfe;// character varying(15) DEFAULT ''::character varying,
    private Timestamp dataHoraCancelamentoNfe;// timestamp without time zone,
    private TipoFreteNotaFiscal tipoFreteNotaFiscal = TipoFreteNotaFiscal.PROPRIO_REMETENTE;//id_tipoFreteNotaFiscal;// integer NOT NULL,
    private long idNotaSaidaComplemento = -1;// bigint,
    private boolean emailNfe = false;// boolean NOT NULL,
    private boolean contingenciaNfe = false;// boolean NOT NULL DEFAULT false,
    private long idNotaEntrada = -1;// bigint,
    private boolean aplicaIcmsDesconto = true;// boolean NOT NULL DEFAULT false,
    private boolean aplicaIcmsEncargo = true;// boolean NOT NULL DEFAULT false,
    private double pesoBruto = 0;// numeric(12,3) NOT NULL DEFAULT (0)::numeric,
    private Timestamp dataHoraAlteracao;// timestamp without time zone NOT NULL DEFAULT now(),
    private int idLocalEntrega = 0;// integer,
    private double valorIcmsUsoConsumo = 0;// numeric(11,2),
    private boolean aplicaIcmsIpi = false;// boolean NOT NULL DEFAULT false,
    private int idTipoViaTransporteInteracional = -1;// integer,
    private double valorafrmm = 0;// numeric(13,2),
    private int idTipoFormaImportacao = -1;// integer,
    private boolean aplicaIcmsStIpi = true;// boolean DEFAULT true,
    private String especie;// character varying(60),
    private String marca;// character varying(60),
    private String numeracao;// character varying(60),
    private boolean aplicaPisCofinsDesconto = false;// boolean,
    private boolean aplicaPisCofinsEncargo = false;// boolean,
    private String serie;// character varying(4),
    private int idEscritaSaldo = -1;// integer,
    private double valorFcp = 0;// numeric(11,2),
    private double valorFcpSt = 0;// numeric(11,2),
    private double valorIcmsDesonerado = 0;// numeric(11,2),

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota != null ? tipoNota : TipoNota.NORMAL;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public int getIdClienteEventual() {
        return idClienteEventual;
    }

    public void setIdClienteEventual(int idClienteEventual) {
        this.idClienteEventual = idClienteEventual;
    }

    public int getIdTipoSaida() {
        return idTipoSaida;
    }

    public void setIdTipoSaida(int idTipoSaida) {
        this.idTipoSaida = idTipoSaida;
    }

    public Timestamp getDataHoraEmissao() {
        return dataHoraEmissao;
    }

    public void setDataHoraEmissao(Timestamp dataHoraEmissao) {
        this.dataHoraEmissao = dataHoraEmissao;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
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

    public SituacaoNotaSaida getSituacaoNotaSaida() {
        return situacaoNotaSaida;
    }

    public void setSituacaoNotaSaida(SituacaoNotaSaida situacaoNotaSaida) {
        this.situacaoNotaSaida = situacaoNotaSaida != null ? situacaoNotaSaida : SituacaoNotaSaida.FINALIZADO;
    }

    public int getIdMotoristaTransportador() {
        return idMotoristaTransportador;
    }

    public void setIdMotoristaTransportador(int idMotoristaTransportador) {
        this.idMotoristaTransportador = idMotoristaTransportador;
    }

    public int getIdFornecedorTransportador() {
        return idFornecedorTransportador;
    }

    public void setIdFornecedorTransportador(int idFornecedorTransportador) {
        this.idFornecedorTransportador = idFornecedorTransportador;
    }

    public int getIdClienteEventualTransportador() {
        return idClienteEventualTransportador;
    }

    public void setIdClienteEventualTransportador(int idClienteEventualTransportador) {
        this.idClienteEventualTransportador = idClienteEventualTransportador;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getIdTipoDevolucao() {
        return idTipoDevolucao;
    }

    public void setIdTipoDevolucao(int idTipoDevolucao) {
        this.idTipoDevolucao = idTipoDevolucao;
    }

    public String getInformacaoComplementar() {
        return informacaoComplementar;
    }

    public void setInformacaoComplementar(String informacaoComplementar) {
        this.informacaoComplementar = Utils.acertarTexto(informacaoComplementar, 1000, "");
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getTipoLocalBaixa() {
        return tipoLocalBaixa;
    }

    public void setTipoLocalBaixa(int tipoLocalBaixa) {
        this.tipoLocalBaixa = tipoLocalBaixa;
    }

    public double getValorBaseIpi() {
        return valorBaseIpi;
    }

    public void setValorBaseIpi(double valorBaseIpi) {
        this.valorBaseIpi = valorBaseIpi;
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

    public SituacaoNfe getSituacaoNfe() {
        return situacaoNfe;
    }

    public void setSituacaoNfe(SituacaoNfe situacaoNfe) {
        this.situacaoNfe = situacaoNfe != null ? situacaoNfe : SituacaoNfe.NAO_TRANSMITIDA;
    }

    public String getChaveNfe() {
        return chaveNfe;
    }

    public void setChaveNfe(String chaveNfe) {
        this.chaveNfe = chaveNfe;
    }

    public String getReciboNfe() {
        return reciboNfe;
    }

    public void setReciboNfe(String reciboNfe) {
        this.reciboNfe = reciboNfe;
    }

    public String getMotivoRejeicaoNfe() {
        return motivoRejeicaoNfe;
    }

    public void setMotivoRejeicaoNfe(String motivoRejeicaoNfe) {
        this.motivoRejeicaoNfe = motivoRejeicaoNfe;
    }

    public String getProtocoloRecebimentoNfe() {
        return protocoloRecebimentoNfe;
    }

    public void setProtocoloRecebimentoNfe(String protocoloRecebimentoNfe) {
        this.protocoloRecebimentoNfe = protocoloRecebimentoNfe;
    }

    public Timestamp getDataHoraRecebimentoNfe() {
        return dataHoraRecebimentoNfe;
    }

    public void setDataHoraRecebimentoNfe(Timestamp dataHoraRecebimentoNfe) {
        this.dataHoraRecebimentoNfe = dataHoraRecebimentoNfe;
    }

    public String getJustificativaCancelamentoNfe() {
        return justificativaCancelamentoNfe;
    }

    public void setJustificativaCancelamentoNfe(String justificativaCancelamentoNfe) {
        this.justificativaCancelamentoNfe = justificativaCancelamentoNfe;
    }

    public String getProtocoloCancelamentoNfe() {
        return protocoloCancelamentoNfe;
    }

    public void setProtocoloCancelamentoNfe(String protocoloCancelamentoNfe) {
        this.protocoloCancelamentoNfe = protocoloCancelamentoNfe;
    }

    public Timestamp getDataHoraCancelamentoNfe() {
        return dataHoraCancelamentoNfe;
    }

    public void setDataHoraCancelamentoNfe(Timestamp dataHoraCancelamentoNfe) {
        this.dataHoraCancelamentoNfe = dataHoraCancelamentoNfe;
    }

    public TipoFreteNotaFiscal getTipoFreteNotaFiscal() {
        return tipoFreteNotaFiscal;
    }

    public void setTipoFreteNotaFiscal(TipoFreteNotaFiscal tipoFreteNotaFiscal) {
        this.tipoFreteNotaFiscal = tipoFreteNotaFiscal != null ? tipoFreteNotaFiscal : TipoFreteNotaFiscal.PROPRIO_REMETENTE;
    }

    public long getIdNotaSaidaComplemento() {
        return idNotaSaidaComplemento;
    }

    public void setIdNotaSaidaComplemento(long idNotaSaidaComplemento) {
        this.idNotaSaidaComplemento = idNotaSaidaComplemento;
    }

    public boolean isEmailNfe() {
        return emailNfe;
    }

    public void setEmailNfe(boolean emailNfe) {
        this.emailNfe = emailNfe;
    }

    public boolean isContingenciaNfe() {
        return contingenciaNfe;
    }

    public void setContingenciaNfe(boolean contingenciaNfe) {
        this.contingenciaNfe = contingenciaNfe;
    }

    public long getIdNotaEntrada() {
        return idNotaEntrada;
    }

    public void setIdNotaEntrada(long idNotaEntrada) {
        this.idNotaEntrada = idNotaEntrada;
    }

    public boolean isAplicaIcmsDesconto() {
        return aplicaIcmsDesconto;
    }

    public void setAplicaIcmsDesconto(boolean aplicaIcmsDesconto) {
        this.aplicaIcmsDesconto = aplicaIcmsDesconto;
    }

    public boolean isAplicaIcmsEncargo() {
        return aplicaIcmsEncargo;
    }

    public void setAplicaIcmsEncargo(boolean aplicaIcmsEncargo) {
        this.aplicaIcmsEncargo = aplicaIcmsEncargo;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public Timestamp getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    public int getIdLocalEntrega() {
        return idLocalEntrega;
    }

    public void setIdLocalEntrega(int idLocalEntrega) {
        this.idLocalEntrega = idLocalEntrega;
    }

    public double getValorIcmsUsoConsumo() {
        return valorIcmsUsoConsumo;
    }

    public void setValorIcmsUsoConsumo(double valorIcmsUsoConsumo) {
        this.valorIcmsUsoConsumo = valorIcmsUsoConsumo;
    }

    public boolean isAplicaIcmsIpi() {
        return aplicaIcmsIpi;
    }

    public void setAplicaIcmsIpi(boolean aplicaIcmsIpi) {
        this.aplicaIcmsIpi = aplicaIcmsIpi;
    }

    public int getIdTipoViaTransporteInteracional() {
        return idTipoViaTransporteInteracional;
    }

    public void setIdTipoViaTransporteInteracional(int idTipoViaTransporteInteracional) {
        this.idTipoViaTransporteInteracional = idTipoViaTransporteInteracional;
    }

    public double getValorafrmm() {
        return valorafrmm;
    }

    public void setValorafrmm(double valorafrmm) {
        this.valorafrmm = valorafrmm;
    }

    public int getIdTipoFormaImportacao() {
        return idTipoFormaImportacao;
    }

    public void setIdTipoFormaImportacao(int idTipoFormaImportacao) {
        this.idTipoFormaImportacao = idTipoFormaImportacao;
    }

    public boolean isAplicaIcmsStIpi() {
        return aplicaIcmsStIpi;
    }

    public void setAplicaIcmsStIpi(boolean aplicaIcmsStIpi) {
        this.aplicaIcmsStIpi = aplicaIcmsStIpi;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNumeracao() {
        return numeracao;
    }

    public void setNumeracao(String numeracao) {
        this.numeracao = numeracao;
    }

    public boolean isAplicaPisCofinsDesconto() {
        return aplicaPisCofinsDesconto;
    }

    public void setAplicaPisCofinsDesconto(boolean aplicaPisCofinsDesconto) {
        this.aplicaPisCofinsDesconto = aplicaPisCofinsDesconto;
    }

    public boolean isAplicaPisCofinsEncargo() {
        return aplicaPisCofinsEncargo;
    }

    public void setAplicaPisCofinsEncargo(boolean aplicaPisCofinsEncargo) {
        this.aplicaPisCofinsEncargo = aplicaPisCofinsEncargo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = String.valueOf(Utils.stringToInt(serie));
    }

    public int getIdEscritaSaldo() {
        return idEscritaSaldo;
    }

    public void setIdEscritaSaldo(int idEscritaSaldo) {
        this.idEscritaSaldo = idEscritaSaldo;
    }

    public double getValorFcp() {
        return valorFcp;
    }

    public void setValorFcp(double valorFcp) {
        this.valorFcp = valorFcp;
    }

    public double getValorFcpSt() {
        return valorFcpSt;
    }

    public void setValorFcpSt(double valorFcpSt) {
        this.valorFcpSt = valorFcpSt;
    }

    public double getValorIcmsDesonerado() {
        return valorIcmsDesonerado;
    }

    public void setValorIcmsDesonerado(double valorIcmsDesonerado) {
        this.valorIcmsDesonerado = valorIcmsDesonerado;
    }

    public void addInformacaoComplementar(String informacaoComplementar) {
        this.informacaoComplementar += informacaoComplementar;
    }
    
    
    
}
