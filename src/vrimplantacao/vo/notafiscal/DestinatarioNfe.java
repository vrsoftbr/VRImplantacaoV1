package vrimplantacao.vo.notafiscal;

import vrimplantacao2.vo.enums.TipoIndicadorIE;

/**
 *
 * @author Leandro
 */
public class DestinatarioNfe {
    
    private long cnpj;
    private String nome;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String municipio;
    private int codigoIbgeMunicipio = 9999999;
    private String uf;
    private String cep;
    private TipoIndicadorIE indicadorId = TipoIndicadorIE.CONTRIBUINTE_ICMS;
    private long inscricaoEstadual;
    private long suframa;
    private long inscricaoMunicipal;
    private String telefone;
    private String email;

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public int getCodigoIbgeMunicipio() {
        return codigoIbgeMunicipio;
    }

    public void setCodigoIbgeMunicipio(int codigoIbgeMunicipio) {
        this.codigoIbgeMunicipio = codigoIbgeMunicipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public TipoIndicadorIE getIndicadorId() {
        return indicadorId;
    }

    public void setIndicadorId(TipoIndicadorIE indicadorId) {
        this.indicadorId = indicadorId == null ? TipoIndicadorIE.CONTRIBUINTE_ISENTO : indicadorId;
    }

    public long getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(long inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public long getSuframa() {
        return suframa;
    }

    public void setSuframa(long suframa) {
        this.suframa = suframa;
    }

    public long getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(long inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
