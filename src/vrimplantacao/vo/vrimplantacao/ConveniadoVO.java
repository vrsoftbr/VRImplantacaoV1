package vrimplantacao.vo.vrimplantacao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ConveniadoVO {
    public int id = 0;
    public String nome = "";
    public int id_empresa = 0;
    public boolean bloqueado = false;
    public int id_situacaocadastro = 1;
    public int senha = 0;
    public int id_loja = 1;
    public long cnpj = 0;
    public String observacao = "";
    public int id_tipoinscricao = 1;
    public int matricula = 0;
    public String datavalidadecartao = "";
    public String datadesbloqueio = null;
    public boolean visualizasaldo = true;
    public Date databloqueio = null;
    public List<ConveniadoServicoVO> vConveniadoServico = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getId_tipoinscricao() {
        return id_tipoinscricao;
    }

    public void setId_tipoinscricao(int id_tipoinscricao) {
        this.id_tipoinscricao = id_tipoinscricao;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getDatavalidadecartao() {
        return datavalidadecartao;
    }

    public void setDatavalidadecartao(String datavalidadecartao) {
        this.datavalidadecartao = datavalidadecartao;
    }

    public String getDatadesbloqueio() {
        return datadesbloqueio;
    }

    public void setDatadesbloqueio(String datadesbloqueio) {
        this.datadesbloqueio = datadesbloqueio;
    }

    public boolean isVisualizasaldo() {
        return visualizasaldo;
    }

    public void setVisualizasaldo(boolean visualizasaldo) {
        this.visualizasaldo = visualizasaldo;
    }

    public Date getDatabloqueio() {
        return databloqueio;
    }

    public void setDatabloqueio(Date databloqueio) {
        this.databloqueio = databloqueio;
    }

    public List<ConveniadoServicoVO> getvConveniadoServico() {
        return vConveniadoServico;
    }

    public void setvConveniadoServico(List<ConveniadoServicoVO> vConveniadoServico) {
        this.vConveniadoServico = vConveniadoServico;
    }
    
    
}