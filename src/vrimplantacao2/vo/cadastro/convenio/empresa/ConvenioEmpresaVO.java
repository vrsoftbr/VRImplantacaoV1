package vrimplantacao2.vo.cadastro.convenio.empresa;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoInscricao;

/**
 *
 * @author Leandro
 */
public class ConvenioEmpresaVO {
    
    private int id;// integer NOT NULL,
    private String razaoSocial;// character varying(40) NOT NULL,
    private String endereco = "SEM ENDERECO";// character varying(40) NOT NULL,
    private String bairro = "SEM BAIRRO";// character varying(30) NOT NULL,
    private int id_municipio = 0;// integer NOT NULL,
    private String telefone = "0000000000";// character varying(14) NOT NULL,
    private int cep;// numeric(8,0) NOT NULL,
    private String inscricaoEstadual;// character varying(20) NOT NULL,
    private long cnpj;// numeric(14,0) NOT NULL,
    private Date dataInicio;// date NOT NULL,
    private Date dataTermino;// date NOT NULL,
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;// id_situacaocadastro integer NOT NULL,
    private TipoInscricao tipoInscricao = TipoInscricao.JURIDICA;//id_tipoinscricao integer NOT NULL,
    private boolean renovacaoAutomatica = false;// boolean NOT NULL,
    private double percentualDesconto;// numeric(11,2) NOT NULL,
    private int diaPagamento;// integer NOT NULL,
    private boolean bloqueado = false;// boolean NOT NULL,
    private Date dataDesbloqueio;// date,
    private int id_estado;// integer NOT NULL,
    private int diaInicioRenovacao = 1;// integer NOT NULL,
    private int diaTerminoRenovacao = 31;// integer NOT NULL,
    private TipoTerminoRenovacao tipoTerminoRenovacao = TipoTerminoRenovacao.NENHUM; //integer NOT NULL,
    private Date dataBloqueio;// date,
    private String observacao;// character varying(2500) NOT NULL DEFAULT ''::character varying,
    private String numero = "";// character varying(6) NOT NULL DEFAULT '0'::character varying,
    private String complemento = "SEM COMPLEMENTO";// character varying(30) NOT NULL DEFAULT ''::character varying,
    private long id_contaContabilFiscalPassivo;// bigint,
    private long id_contaContabilFiscalAtivo;// bigint,

    public void setId(int id) {
        this.id = id;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = Utils.acertarTexto(razaoSocial, 40, "SEM NOME");
    }

    public void setEndereco(String endereco) {
        this.endereco = Utils.acertarTexto(endereco, 40, "SEM ENDERECO");
    }

    public void setBairro(String bairro) {
        this.bairro = Utils.acertarTexto(bairro, 30, "SEM BAIRRO");
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.formataNumero(telefone, 14, "0000000000");
    }

    public void setCep(int cep) {
        this.cep = cep;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = Utils.formataNumero(inscricaoEstadual, 20, "ISENTO");
    }

    public void setCnpj(long cnpj) {
        if (cnpj > 99999999999999L) {
            cnpj = id;
        }
        this.cnpj = cnpj;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }

    public void setRenovacaoAutomatica(boolean renovacaoAutomatica) {
        this.renovacaoAutomatica = renovacaoAutomatica;
    }

    public void setPercentualDesconto(double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public void setDiaPagamento(int diaPagamento) {
        diaPagamento = diaPagamento < 1 ? 1 : diaPagamento;
        this.diaPagamento = diaPagamento > 31 ? 31 : diaPagamento;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setDataDesbloqueio(Date dataDesbloqueio) {
        this.dataDesbloqueio = dataDesbloqueio;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public void setDiaInicioRenovacao(int diaInicioRenovacao) {
        diaInicioRenovacao = diaInicioRenovacao < 1 ? 1 : diaInicioRenovacao;
        this.diaInicioRenovacao = diaInicioRenovacao > 31 ? 31 : diaInicioRenovacao;
    }

    public void setDiaTerminoRenovacao(int diaTerminoRenovacao) {
        diaTerminoRenovacao = diaTerminoRenovacao > 31 ? 31 : diaTerminoRenovacao;
        this.diaTerminoRenovacao = diaTerminoRenovacao < 1 ? 1 : diaTerminoRenovacao;
    }

    public void setTipoTerminoRenovacao(TipoTerminoRenovacao tipoTerminoRenovacao) {
        this.tipoTerminoRenovacao = tipoTerminoRenovacao != null ? tipoTerminoRenovacao : TipoTerminoRenovacao.NENHUM;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setNumero(String numero) {
        this.numero = Utils.acertarTexto(numero, 6, "0");
    }

    public void setComplemento(String complemento) {
        this.complemento = Utils.acertarTexto(complemento, 30, "SEM COMPLEMENTO");
    }

    public void setId_contaContabilFiscalPassivo(long id_contaContabilFiscalPassivo) {
        this.id_contaContabilFiscalPassivo = id_contaContabilFiscalPassivo;
    }

    public void setId_contaContabilFiscalAtivo(long id_contaContabilFiscalAtivo) {
        this.id_contaContabilFiscalAtivo = id_contaContabilFiscalAtivo;
    }

    public TipoInscricao getTipoInscricao() {
        return cnpj > 99999999999L ? TipoInscricao.JURIDICA : TipoInscricao.FISICA;
    }

    public int getId() {
        return id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public String getTelefone() {
        return telefone;
    }

    public int getCep() {
        return cep;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public long getCnpj() {
        return cnpj;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public boolean isRenovacaoAutomatica() {
        return renovacaoAutomatica;
    }

    public double getPercentualDesconto() {
        return percentualDesconto;
    }

    public int getDiaPagamento() {
        return diaPagamento;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public Date getDataDesbloqueio() {
        return dataDesbloqueio;
    }

    public int getId_estado() {
        return id_estado;
    }

    public int getDiaInicioRenovacao() {
        return diaInicioRenovacao;
    }

    public int getDiaTerminoRenovacao() {
        return diaTerminoRenovacao;
    }

    public TipoTerminoRenovacao getTipoTerminoRenovacao() {
        return tipoTerminoRenovacao;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public String getObservacao() {
        return observacao;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public long getId_contaContabilFiscalPassivo() {
        return id_contaContabilFiscalPassivo;
    }

    public long getId_contaContabilFiscalAtivo() {
        return id_contaContabilFiscalAtivo;
    }

    
    
    
}
