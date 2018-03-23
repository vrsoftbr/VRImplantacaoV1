/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.pdv.operador;

/**
 *
 * @author lucasrafael
 */
public class OperadorAnteriorVO {

    private String sistema;
    private String loja;
    private String matricula;
    private OperadorVO matriculaatual;
    private String nome;
    private String senha;
    private String id_tiponiveloperador;
    private String id_situacaocadastro;
    private boolean forcargravacao = false;

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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public OperadorVO getMatriculaatual() {
        return matriculaatual;
    }

    public void setMatriculaatual(OperadorVO matriculaatual) {
        this.matriculaatual = matriculaatual;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getId_tiponiveloperador() {
        return id_tiponiveloperador;
    }

    public void setId_tiponiveloperador(String id_tiponiveloperador) {
        this.id_tiponiveloperador = id_tiponiveloperador;
    }

    public String getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public void setId_situacaocadastro(String id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public boolean isForcargravacao() {
        return forcargravacao;
    }

    public void setForcargravacao(boolean forcargravacao) {
        this.forcargravacao = forcargravacao;
    }
}
