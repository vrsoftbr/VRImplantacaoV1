/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

import vrimplantacao.classe.Global;

/**
 *
 * @author lucasrafael
 */
public class EmpresaVO {
    private int id = 0;   
    private String razaosocial = "";
    private String endereco = "";
    private String bairro = "";
    private int id_municipio = Global.idMunicipio;
    private String telefone = "";
    private long cep = 0;
    private String inscricaoestadual = "";
    private long cnpj = -1;
    private String datainicio = "";
    private String datatermino = "";
    private int id_situacaocadastro = 1;
    private int id_tipoinscricao = 0;
    private boolean renovacaoautomatica = false;
    private double percentualdesconto = 0;
    private int diapagamento = 20;
    private boolean bloqueado = false;
    private String datadesbloqueio = "";
    private int id_estado = Global.idEstado;
    private int diainiciorenovacao = 1;
    private int diaterminorenovacao = 20;
    private int tipoterminorenovacao = 1;
    private String databloqueio = "";
    private String observacao = "";
    private String numero = "0";
    private String complemento = "";
    private int id_contacontabilfiscalpassivo = -1;
    private int id_contacontabilfiscalativo = -1;

    public int getId() {
        return id;
    }

    public String getRazaosocial() {
        return razaosocial;
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

    public Long getCep() {
        return cep;
    }

    public String getInscricaoestadual() {
        return inscricaoestadual;
    }

    public Long getCnpj() {
        return cnpj;
    }

    public String getDatainicio() {
        return datainicio;
    }

    public String getDatatermino() {
        return datatermino;
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public int getId_tipoinscricao() {
        return id_tipoinscricao;
    }

    public boolean isRenovacaoautomatica() {
        return renovacaoautomatica;
    }

    public double getPercentualdesconto() {
        return percentualdesconto;
    }

    public int getDiapagamento() {
        return diapagamento;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public String getDatadesbloqueio() {
        return datadesbloqueio;
    }

    public int getId_estado() {
        return id_estado;
    }

    public int getDiainiciorenovacao() {
        return diainiciorenovacao;
    }

    public int getDiaterminorenovacao() {
        return diaterminorenovacao;
    }

    public int getTipoterminorenovacao() {
        return tipoterminorenovacao;
    }

    public String getDatabloqueio() {
        return databloqueio;
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

    public int getId_contacontabilfiscalpassivo() {
        return id_contacontabilfiscalpassivo;
    }

    public int getId_contacontabilfiscalativo() {
        return id_contacontabilfiscalativo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRazaosocial(String razaosocial) {
        this.razaosocial = razaosocial;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setCep(Long cep) {
        this.cep = cep;
    }

    public void setInscricaoestadual(String inscricaoestadual) {
        this.inscricaoestadual = inscricaoestadual;
    }

    public void setCnpj(Long cnpj) {
        this.cnpj = cnpj;
    }

    public void setDatainicio(String datainicio) {
        this.datainicio = datainicio;
    }

    public void setDatatermino(String datatermino) {
        this.datatermino = datatermino;
    }

    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public void setId_tipoinscricao(int id_tipoinscricao) {
        this.id_tipoinscricao = id_tipoinscricao;
    }

    public void setRenovacaoautomatica(boolean renovacaoautomatica) {
        this.renovacaoautomatica = renovacaoautomatica;
    }

    public void setPercentualdesconto(double percentualdesconto) {
        this.percentualdesconto = percentualdesconto;
    }

    public void setDiapagamento(int diapagamento) {
        this.diapagamento = diapagamento;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setDatadesbloqueio(String datadesbloqueio) {
        this.datadesbloqueio = datadesbloqueio;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public void setDiainiciorenovacao(int diainiciorenovacao) {
        this.diainiciorenovacao = diainiciorenovacao;
    }

    public void setDiaterminorenovacao(int diaterminorenovacao) {
        this.diaterminorenovacao = diaterminorenovacao;
    }

    public void setTipoterminorenovacao(int tipoterminorenovacao) {
        this.tipoterminorenovacao = tipoterminorenovacao;
    }

    public void setDatabloqueio(String databloqueio) {
        this.databloqueio = databloqueio;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public void setId_contacontabilfiscalpassivo(int id_contacontabilfiscalpassivo) {
        this.id_contacontabilfiscalpassivo = id_contacontabilfiscalpassivo;
    }

    public void setId_contacontabilfiscalativo(int id_contacontabilfiscalativo) {
        this.id_contacontabilfiscalativo = id_contacontabilfiscalativo;
    }
    
}
