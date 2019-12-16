package vrimplantacao2.vo.cadastro.notafiscal;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public class NotaEntrada implements Nota {

    @Override
    public int getIndicadorNota() {
        return Nota.NOTA_ENTRADA;
    }

    private int id;// integer NOT NULL DEFAULT nextval('notaentrada_id_seq'::regclass),
    private int idLoja;//id_loja;// integer NOT NULL,
    private int numeroNota;// integer NOT NULL,
    private int idFornecedor;//id_fornecedor integer NOT NULL,
    private Date dataEntrada;// date NOT NULL,
    private int idTipoEntrada;//id_tipoentrada integer NOT NULL,
    private Date dataEmissao;// date NOT NULL,
    private Timestamp dataHoraLancamento;// timestamp without time zone NOT NULL,
    private double valorIpi = 0;// numeric(11,2) NOT NULL,
    private double valorFrete = 0;// numeric(11,2) NOT NULL,
    private double valorDesconto = 0;// numeric(11,2) NOT NULL,
    private double valorOutraDespesa = 0;// numeric(11,2) NOT NULL,
    private double valorDespesaAdicional = 0;// numeric(11,2) NOT NULL,
    private double valorMercadoria = 0;// numeric(11,2) NOT NULL,
    private double valorTotal = 0;// numeric(11,2) NOT NULL,
    private double valorIcms = 0;// numeric(11,2) NOT NULL,
    private double valorIcmsSubstituicao = 0;// numeric(11,2) NOT NULL,
    private int idUsuario = 0;//id_usuario integer NOT NULL,
    private boolean impressao = false;// boolean NOT NULL,
    private boolean produtorRural = false;// boolean NOT NULL,
    private boolean aplicaCustoDesconto = true;// boolean NOT NULL,
    private boolean aplicaIcmsDesconto = true;// boolean NOT NULL,
    private boolean aplicaCustoEncargo = true;// boolean NOT NULL,
    private boolean aplicaIcmsEncargo = true;// boolean NOT NULL,
    private boolean aplicaDespesaAdicional = true;// boolean NOT NULL,
    private SituacaoNotaEntrada situacaoNotaEntrada = SituacaoNotaEntrada.FINALIZADO;//id_situacaonotaentrada; integer NOT NULL,
    private String serie;// character varying(4) NOT NULL,
    private double valorGuiaSubstituicao = 0;// numeric(11,2),
    private double valorBaseCalculo = 0;// numeric(11,2),
    private int aplicaAliquota = -1;// integer NOT NULL,
    private double valorBaseSubstituicao = 0;// numeric(11,2) NOT NULL,
    private double valorFunrural = 0;// numeric(11,2) NOT NULL,
    private double valorDescontoBoleto = 0;// numeric(11,2) NOT NULL,
    private String chaveNfe;// character varying(44) NOT NULL,
    private boolean conferido = false;// boolean NOT NULL,
    private TipoFreteNotaFiscal tipoFreteNotaFiscal = TipoFreteNotaFiscal.CONTRATADO_DESTINATARIO;//id_tipofretenotafiscal integer NOT NULL,
    private String observacao = "";// text NOT NULL DEFAULT ''::character varying,
    private long idNotaSaida = -1;//id_notasaida bigint,
    private TipoNota tipoNota = TipoNota.NORMAL;//id_tiponota integer NOT NULL DEFAULT 0,
    private String modelo;// character varying(2) NOT NULL DEFAULT ''::character varying,
    private boolean liberadoPedido = false;// boolean NOT NULL DEFAULT false,
    private Timestamp dataHoraFinalizacao;// timestamp without time zone DEFAULT '1900-01-01'::date,
    private boolean importadoXml = false;// boolean NOT NULL DEFAULT false,
    private boolean aplicaIcmsIpi = false;// boolean NOT NULL DEFAULT false,
    private int liberadoBonificacao = 1;// integer NOT NULL DEFAULT '-1'::integer,
    private String informacaoComplementar = "";// character varying(1000),
    private double valorIcmsSN = 0;// numeric(13,4),
    private Timestamp dataHoraAlteracao;// timestamp without time zone NOT NULL DEFAULT now(),
    private int liberadoVencimento = -1;// integer NOT NULL DEFAULT '-1'::integer,
    private String justificativaDivergencia;// character varying(50),
    private boolean consistido = false;// boolean DEFAULT false,
    private int quantidadePaletes = 0;// integer NOT NULL DEFAULT 0,
    private long idNotaDespesa = -1;//id_notadespesa bigint,
    private double valorDespesaFrete = 0;// numeric(11,2),
    private boolean liberadoValidadeProduto = false;// boolean NOT NULL DEFAULT false,
    private double valorFcp = 0;// numeric(11,2),
    private double valorFcpST = 0;// numeric(11,2),
    private double valorIcmsDesonerado = 0;// numeric(11,2),
    private boolean liberadoDivergenciaColetor = false;// boolean DEFAULT false,
    private double valorSuframa = -1;// numeric(11,2),

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

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public int getIdTipoEntrada() {
        return idTipoEntrada;
    }

    public void setIdTipoEntrada(int idTipoEntrada) {
        this.idTipoEntrada = idTipoEntrada;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Timestamp getDataHoraLancamento() {
        return dataHoraLancamento;
    }

    public void setDataHoraLancamento(Timestamp dataHoraLancamento) {
        this.dataHoraLancamento = dataHoraLancamento;
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

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorOutraDespesa() {
        return valorOutraDespesa;
    }

    public void setValorOutraDespesa(double valorOutraDespesa) {
        this.valorOutraDespesa = valorOutraDespesa;
    }

    public double getValorDespesaAdicional() {
        return valorDespesaAdicional;
    }

    public void setValorDespesaAdicional(double valorDespesaAdicional) {
        this.valorDespesaAdicional = valorDespesaAdicional;
    }

    public double getValorMercadoria() {
        return valorMercadoria;
    }

    public void setValorMercadoria(double valorMercadoria) {
        this.valorMercadoria = valorMercadoria;
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

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isImpressao() {
        return impressao;
    }

    public void setImpressao(boolean impressao) {
        this.impressao = impressao;
    }

    public boolean isProdutorRural() {
        return produtorRural;
    }

    public void setProdutorRural(boolean produtorRural) {
        this.produtorRural = produtorRural;
    }

    public boolean isAplicaCustoDesconto() {
        return aplicaCustoDesconto;
    }

    public void setAplicaCustoDesconto(boolean aplicaCustoDesconto) {
        this.aplicaCustoDesconto = aplicaCustoDesconto;
    }

    public boolean isAplicaIcmsDesconto() {
        return aplicaIcmsDesconto;
    }

    public void setAplicaIcmsDesconto(boolean aplicaIcmsDesconto) {
        this.aplicaIcmsDesconto = aplicaIcmsDesconto;
    }

    public boolean isAplicaCustoEncargo() {
        return aplicaCustoEncargo;
    }

    public void setAplicaCustoEncargo(boolean aplicaCustoEncargo) {
        this.aplicaCustoEncargo = aplicaCustoEncargo;
    }

    public boolean isAplicaIcmsEncargo() {
        return aplicaIcmsEncargo;
    }

    public void setAplicaIcmsEncargo(boolean aplicaIcmsEncargo) {
        this.aplicaIcmsEncargo = aplicaIcmsEncargo;
    }

    public boolean isAplicaDespesaAdicional() {
        return aplicaDespesaAdicional;
    }

    public void setAplicaDespesaAdicional(boolean aplicaDespesaAdicional) {
        this.aplicaDespesaAdicional = aplicaDespesaAdicional;
    }

    public SituacaoNotaEntrada getSituacaoNotaEntrada() {
        return situacaoNotaEntrada;
    }

    public void setSituacaoNotaEntrada(SituacaoNotaEntrada situacaoNotaEntrada) {
        this.situacaoNotaEntrada = situacaoNotaEntrada != null ? situacaoNotaEntrada : SituacaoNotaEntrada.NAO_FINALIZADO;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = String.valueOf(Utils.stringToInt(serie));
    }

    public double getValorGuiaSubstituicao() {
        return valorGuiaSubstituicao;
    }

    public void setValorGuiaSubstituicao(double valorGuiaSubstituicao) {
        this.valorGuiaSubstituicao = valorGuiaSubstituicao;
    }

    public double getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(double valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public int getAplicaAliquota() {
        return aplicaAliquota;
    }

    public void setAplicaAliquota(int aplicaAliquota) {
        this.aplicaAliquota = aplicaAliquota;
    }

    public double getValorBaseSubstituicao() {
        return valorBaseSubstituicao;
    }

    public void setValorBaseSubstituicao(double valorBaseSubstituicao) {
        this.valorBaseSubstituicao = valorBaseSubstituicao;
    }

    public double getValorFunrural() {
        return valorFunrural;
    }

    public void setValorFunrural(double valorFunrural) {
        this.valorFunrural = valorFunrural;
    }

    public double getValorDescontoBoleto() {
        return valorDescontoBoleto;
    }

    public void setValorDescontoBoleto(double valorDescontoBoleto) {
        this.valorDescontoBoleto = valorDescontoBoleto;
    }

    public String getChaveNfe() {
        return chaveNfe;
    }

    public void setChaveNfe(String chaveNfe) {
        this.chaveNfe = chaveNfe;
    }

    public boolean isConferido() {
        return conferido;
    }

    public void setConferido(boolean conferido) {
        this.conferido = conferido;
    }

    public TipoFreteNotaFiscal getTipoFreteNotaFiscal() {
        return tipoFreteNotaFiscal;
    }

    public void setTipoFreteNotaFiscal(TipoFreteNotaFiscal tipoFreteNotaFiscal) {
        this.tipoFreteNotaFiscal = tipoFreteNotaFiscal != null ? tipoFreteNotaFiscal : TipoFreteNotaFiscal.CONTRATADO_DESTINATARIO;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao != null ? observacao : "";
    }

    public long getIdNotaSaida() {
        return idNotaSaida;
    }

    public void setIdNotaSaida(long idNotaSaida) {
        this.idNotaSaida = idNotaSaida;
    }

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota != null ? tipoNota : TipoNota.NORMAL;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo == null || modelo.length() > 2 ? "55" : modelo;
    }

    public boolean isLiberadoPedido() {
        return liberadoPedido;
    }

    public void setLiberadoPedido(boolean liberadoPedido) {
        this.liberadoPedido = liberadoPedido;
    }

    public Timestamp getDataHoraFinalizacao() {
        return dataHoraFinalizacao;
    }

    public void setDataHoraFinalizacao(Timestamp dataHoraFinalizacao) {
        this.dataHoraFinalizacao = dataHoraFinalizacao;
    }

    public boolean isImportadoXml() {
        return importadoXml;
    }

    public void setImportadoXml(boolean importadoXml) {
        this.importadoXml = importadoXml;
    }

    public boolean isAplicaIcmsIpi() {
        return aplicaIcmsIpi;
    }

    public void setAplicaIcmsIpi(boolean aplicaIcmsIpi) {
        this.aplicaIcmsIpi = aplicaIcmsIpi;
    }

    public int getLiberadoBonificacao() {
        return liberadoBonificacao;
    }

    public void setLiberadoBonificacao(int liberadoBonificacao) {
        this.liberadoBonificacao = liberadoBonificacao;
    }

    public String getInformacaoComplementar() {
        return informacaoComplementar;
    }

    public void setInformacaoComplementar(String informacaoComplementar) {
        this.informacaoComplementar = Utils.acertarTexto(informacaoComplementar, 1000, "");
    }

    public double getValorIcmsSN() {
        return valorIcmsSN;
    }

    public void setValorIcmsSN(double valorIcmsSN) {
        this.valorIcmsSN = valorIcmsSN;
    }

    public Timestamp getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    public int getLiberadoVencimento() {
        return liberadoVencimento;
    }

    public void setLiberadoVencimento(int liberadoVencimento) {
        this.liberadoVencimento = liberadoVencimento;
    }

    public String getJustificativaDivergencia() {
        return justificativaDivergencia;
    }

    public void setJustificativaDivergencia(String justificativaDivergencia) {
        this.justificativaDivergencia = justificativaDivergencia;
    }

    public boolean isConsistido() {
        return consistido;
    }

    public void setConsistido(boolean consistido) {
        this.consistido = consistido;
    }

    public int getQuantidadePaletes() {
        return quantidadePaletes;
    }

    public void setQuantidadePaletes(int quantidadePaletes) {
        this.quantidadePaletes = quantidadePaletes;
    }

    public long getIdNotaDespesa() {
        return idNotaDespesa;
    }

    public void setIdNotaDespesa(long idNotaDespesa) {
        this.idNotaDespesa = idNotaDespesa;
    }

    public double getValorDespesaFrete() {
        return valorDespesaFrete;
    }

    public void setValorDespesaFrete(double valorDespesaFrete) {
        this.valorDespesaFrete = valorDespesaFrete;
    }

    public boolean isLiberadoValidadeProduto() {
        return liberadoValidadeProduto;
    }

    public void setLiberadoValidadeProduto(boolean liberadoValidadeProduto) {
        this.liberadoValidadeProduto = liberadoValidadeProduto;
    }

    public double getValorFcp() {
        return valorFcp;
    }

    public void setValorFcp(double valorFcp) {
        this.valorFcp = valorFcp;
    }

    public double getValorFcpST() {
        return valorFcpST;
    }

    public void setValorFcpST(double valorFcpST) {
        this.valorFcpST = valorFcpST;
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

    public boolean isLiberadoDivergenciaColetor() {
        return liberadoDivergenciaColetor;
    }

    public void setLiberadoDivergenciaColetor(boolean liberadoDivergenciaColetor) {
        this.liberadoDivergenciaColetor = liberadoDivergenciaColetor;
    }

    public double getValorSuframa() {
        return valorSuframa;
    }

    public void setValorSuframa(double valorSuframa) {
        this.valorSuframa = valorSuframa;
    }
    
}
