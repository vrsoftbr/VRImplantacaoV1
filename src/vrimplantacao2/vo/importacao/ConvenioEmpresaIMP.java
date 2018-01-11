package vrimplantacao2.vo.importacao;

import java.util.Date;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Leandro
 */
public class ConvenioEmpresaIMP {
    
    private String id;
    private String razao;
    private String cnpj;
    private String inscricaoEstadual;
    private String endereco;
    private String numero;
    private String complemento;    
    private String bairro;
    private String municipio;
    private String uf;
    private int ibgeMunicipio;
    private String cep;
    private String telefone;
    private Date dataInicio;
    private Date dataTermino;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private double desconto = 0;
    private boolean renovacaoAutomatica = false;
    private int diaPagamento = 1;
    private boolean bloqueado = false;
    private Date dataBloqueio;
    private int diaInicioRenovacao = 0;
    private int diaFimRenovacao = 0;    
    private String observacoes;

    public void setId(String id) {
        this.id = id;
    }

    public void setRazao(String razao) {
        this.razao = razao;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public void setIbgeMunicipio(int ibgeMunicipio) {
        this.ibgeMunicipio = ibgeMunicipio;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public void setRenovacaoAutomatica(boolean renovacaoAutomatica) {
        this.renovacaoAutomatica = renovacaoAutomatica;
    }

    public void setDiaPagamento(int diaPagamento) {
        this.diaPagamento = diaPagamento;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public void setDiaInicioRenovacao(int diaInicioRenovacao) {
        this.diaInicioRenovacao = diaInicioRenovacao;
    }

    public void setDiaFimRenovacao(int diaFimRenovacao) {
        this.diaFimRenovacao = diaFimRenovacao;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getId() {
        return id;
    }

    public String getRazao() {
        return razao;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getUf() {
        return uf;
    }

    public int getIbgeMunicipio() {
        return ibgeMunicipio;
    }

    public String getCep() {
        return cep;
    }

    public String getTelefone() {
        return telefone;
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

    public double getDesconto() {
        return desconto;
    }

    public boolean isRenovacaoAutomatica() {
        return renovacaoAutomatica;
    }

    public int getDiaPagamento() {
        return diaPagamento;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public int getDiaInicioRenovacao() {
        return diaInicioRenovacao;
    }

    public int getDiaFimRenovacao() {
        return diaFimRenovacao;
    }

    public String getObservacoes() {
        return observacoes;
    }
    
}
