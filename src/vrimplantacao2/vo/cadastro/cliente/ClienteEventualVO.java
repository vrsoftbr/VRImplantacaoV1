package vrimplantacao2.vo.cadastro.cliente;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClienteEventualContatoVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoOrgaoPublico;

/**
 * Classe que representa o Cliente Eventual.
 * @author Leandro
 */
public class ClienteEventualVO {
    
    private int id;// integer NOT NULL,
    private String nome = "SEM NOME";// character varying(60) NOT NULL,
    private String endereco = "SEM ENDERECO";// character varying(50) NOT NULL,
    private String bairro = "SEM BAIRRO";// character varying(30) NOT NULL,
    private int id_estado = 0;// integer NOT NULL,
    private String telefone = "0000000000";// character varying(14) NOT NULL,
    private TipoInscricao tipoInscricao = TipoInscricao.JURIDICA; //id_tipoinscricao integer NOT NULL,
    private String inscricaoEstadual = "ISENTO"; //character varying(20) NOT NULL,
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO; //id_situacaocadastro integer NOT NULL,
    private String fax = "(00)0000-0000";// character varying(14) NOT NULL,
    private String enderecoCobranca = "SEM ENDERECO";// character varying(45) NOT NULL,
    private String bairroCobranca = "SEM BAIRRO";// character varying(30) NOT NULL,
    private int id_estadoCobranca;// integer,
    private String telefoneCobranca;// character varying(14) NOT NULL,
    private int prazoPagamento = 0;// integer NOT NULL,
    private TipoOrgaoPublico tipoOrgaoPublico = TipoOrgaoPublico.NENHUM;// tipoOrgaoPublico integer NOT NULL,
    private Date dataCadastro;// timestamp without time zone NOT NULL,
    private double limiteCompra;// numeric(16,2) NOT NULL,
    private boolean cobraTaxaNotaFiscal = false;// boolean NOT NULL,
    private int id_municipio;// integer NOT NULL,
    private int id_municipioCobranca;// integer,
    private int cep = 0;// numeric(8,0) NOT NULL,
    private long cnpj = -2;// numeric(14,0) NOT NULL,
    private int cepCobranca;// numeric(8,0) NOT NULL,
    private int id_tiporecebimento = 0;// integer,
    private boolean bloqueado = false;//boolean NOT NULL,
    private String numero = "0";// character varying(6) NOT NULL,
    private String observacao;// character varying(2500) NOT NULL DEFAULT ''::character varying,
    private int id_pais = 1085;// integer NOT NULL,
    private String inscricaoMunicipal;// character varying(20) DEFAULT ''::character varying,
    private long id_contaContabilFiscalPassivo;// bigint,
    private String numeroCobranca = "0";// character varying(6) NOT NULL DEFAULT '0'::character varying,
    private String complemento = "SEM COMPLEMENTO";// character varying(30) NOT NULL DEFAULT ''::character varying,
    private String complementoCobranca = "SEM COMPLEMENTO";// character varying(30) NOT NULL DEFAULT ''::character varying,
    private long id_contaContabilFiscalAtivo;// bigint,
    private TipoIndicadorIE tipoIndicadorIe = TipoIndicadorIE.CONTRIBUINTE_ISENTO;//id_tipoindicadorie integer,
    private int id_classeRisco = 3;// integer NOT NULL DEFAULT 3
    private List<ClienteEventualContatoVO> contatos = new ArrayList<>();

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 60, "SEM NOME");
    }

    public void setEndereco(String endereco) {
        this.endereco = Utils.acertarTexto(endereco, 50, "SEM ENDERECO");
    }

    public void setBairro(String bairro) {
        this.bairro = Utils.acertarTexto(bairro, 30, "SEM BAIRRO");
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.acertarTexto(telefone, 14, "(00)0000-0000");
    }

    public void setTipoInscricao(TipoInscricao tipoInscricao) {
        this.tipoInscricao = tipoInscricao;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = Utils.acertarTexto(inscricaoEstadual, 20, "ISENTO");
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public void setFax(String fax) {
        this.fax = Utils.acertarTexto(fax, 14, "(00)0000-0000");
    }

    public void setEnderecoCobranca(String enderecoCobranca) {
        this.enderecoCobranca = Utils.acertarTexto(enderecoCobranca, 45, "SEM ENDERECO");
    }

    public void setBairroCobranca(String bairroCobranca) {
        this.bairroCobranca = Utils.acertarTexto(bairroCobranca, 30, "SEM BAIRRO");
    }

    public void setId_estadoCobranca(int id_estadoCobranca) {
        this.id_estadoCobranca = id_estadoCobranca;
    }

    public void setTelefoneCobranca(String telefoneCobranca) {
        this.telefoneCobranca = Utils.acertarTexto(telefoneCobranca, 14, "(00)0000-0000");
    }

    public void setPrazoPagamento(int prazoPagamento) {
        this.prazoPagamento = prazoPagamento < 0 ? 0 : prazoPagamento;
    }

    public void setTipoOrgaoPublico(TipoOrgaoPublico tipoOrgaoPublico) {
        this.tipoOrgaoPublico = tipoOrgaoPublico;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public void setLimiteCompra(double limiteCompra) {
        this.limiteCompra = limiteCompra;
    }

    public void setCobraTaxaNotaFiscal(boolean cobraTaxaNotaFiscal) {
        this.cobraTaxaNotaFiscal = cobraTaxaNotaFiscal;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public void setId_municipioCobranca(int id_municipioCobranca) {
        this.id_municipioCobranca = id_municipioCobranca;
    }

    public void setCep(int cep) {
        this.cep = cep < 0 || cep > 99999999 ? 0 : cep;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj <= 99999999999999L ? cnpj : this.id;
    }

    public void setCepCobranca(int cepCobranca) {
        this.cepCobranca = cepCobranca < 0 || cepCobranca > 99999999 ? 0 : cepCobranca;
    }

    public void setId_tiporecebimento(int id_tiporecebimento) {
        this.id_tiporecebimento = id_tiporecebimento;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setNumero(String numero) {
        this.numero = Utils.acertarTexto(numero, 6, "0");
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarObservacao(observacao, 2500);
    }

    public void setId_pais(int id_pais) {
        this.id_pais = id_pais;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = Utils.acertarTexto(inscricaoMunicipal, 20, "");
    }

    public void setId_contaContabilFiscalPassivo(long id_contaContabilFiscalPassivo) {
        this.id_contaContabilFiscalPassivo = id_contaContabilFiscalPassivo;
    }

    public void setNumeroCobranca(String numeroCobranca) {
        this.numeroCobranca = Utils.acertarTexto(numeroCobranca, 6, "0");
    }

    public void setComplemento(String complemento) {
        this.complemento = Utils.acertarTexto(complemento, 30, "SEM COMPLEMENTO");
    }

    public void setComplementoCobranca(String complementoCobranca) {
        this.complementoCobranca = Utils.acertarTexto(complementoCobranca, 30, "SEM COMPLEMENTO");
    }

    public void setId_contaContabilFiscalAtivo(long id_contaContabilFiscalAtivo) {
        this.id_contaContabilFiscalAtivo = id_contaContabilFiscalAtivo;
    }

    public void setTipoIndicadorIe(TipoIndicadorIE tipoIndicadorIe) {
        this.tipoIndicadorIe = tipoIndicadorIe;
    }

    public void setId_classeRisco(int id_classeRisco) {
        this.id_classeRisco = id_classeRisco;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public int getId_estado() {
        return id_estado;
    }

    public String getTelefone() {
        return telefone;
    }

    public TipoInscricao getTipoInscricao() {
        return tipoInscricao;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public String getFax() {
        return fax;
    }

    public String getEnderecoCobranca() {
        return enderecoCobranca;
    }

    public String getBairroCobranca() {
        return bairroCobranca;
    }

    public int getId_estadoCobranca() {
        return id_estadoCobranca;
    }

    public String getTelefoneCobranca() {
        return telefoneCobranca;
    }

    public int getPrazoPagamento() {
        return prazoPagamento;
    }

    public TipoOrgaoPublico getTipoOrgaoPublico() {
        return tipoOrgaoPublico;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public double getLimiteCompra() {
        return limiteCompra;
    }

    public boolean isCobraTaxaNotaFiscal() {
        return cobraTaxaNotaFiscal;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public int getId_municipioCobranca() {
        return id_municipioCobranca;
    }

    public int getCep() {
        return cep;
    }

    public long getCnpj() {
        return cnpj;
    }

    public int getCepCobranca() {
        return cepCobranca;
    }

    public int getId_tiporecebimento() {
        return id_tiporecebimento;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public String getNumero() {
        return numero;
    }

    public String getObservacao() {
        return observacao;
    }

    public int getId_pais() {
        return id_pais;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public long getId_contaContabilFiscalPassivo() {
        return id_contaContabilFiscalPassivo;
    }

    public String getNumeroCobranca() {
        return numeroCobranca;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getComplementoCobranca() {
        return complementoCobranca;
    }

    public long getId_contaContabilFiscalAtivo() {
        return id_contaContabilFiscalAtivo;
    }

    public TipoIndicadorIE getTipoIndicadorIe() {
        return tipoIndicadorIe;
    }

    public int getId_classeRisco() {
        return id_classeRisco;
    }

    public List<ClienteEventualContatoVO> getContatos() {
        return contatos;
    }
    
}
