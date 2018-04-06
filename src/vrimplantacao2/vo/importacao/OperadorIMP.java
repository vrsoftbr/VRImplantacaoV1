/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.importacao;

/**
 *
 * @author lucasrafael
 */
public class OperadorIMP {

    private String id;
    private String importSistema;
    private String importLoja;
    private String matricula;
    private String nome;
    private String senha;
    private String codigo;
    private String id_tiponiveloperador;
    private String id_situacadastro;

    public String getImportSistema() {
        return importSistema;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setImportarMatricula(String matricula) {
        this.matricula = matricula;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getId_tiponiveloperador() {
        return id_tiponiveloperador;
    }

    public void setId_tiponiveloperador(String id_tiponiveloperador) {
        this.id_tiponiveloperador = id_tiponiveloperador;
    }

    public String getId_situacadastro() {
        return id_situacadastro;
    }

    public void setId_situacadastro(String id_situacadastro) {
        this.id_situacadastro = id_situacadastro;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
