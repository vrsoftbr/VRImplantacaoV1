package vrimplantacao.vo.loja;

import java.util.List;
import java.util.ArrayList;

public class UsuarioVO {

    public int id = 0;
    public String login = "";
    public String nome = "";
    public String senha = "";
    public int idTipoSetor = 0;
    public String tipoSetor = "";
    public int idSituacaoCadastro = 0;
    public String situacaoCadastro = "";
    public int idLoja = 0;
    public String loja = "";
    public String dataHoraUltimoAcesso = "";
    public boolean verificaAtualizacao = false;
    public List<PermissaoVO> vPermissao = new ArrayList();
    public List<UsuarioSecaoVO> vSecao = new ArrayList();
    public List<UsuarioLojaVO> vLoja = new ArrayList();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public int getIdTipoSetor() {
        return idTipoSetor;
    }

    public void setIdTipoSetor(int idTipoSetor) {
        this.idTipoSetor = idTipoSetor;
    }

    public String getTipoSetor() {
        return tipoSetor;
    }

    public void setTipoSetor(String tipoSetor) {
        this.tipoSetor = tipoSetor;
    }

    public int getIdSituacaoCadastro() {
        return idSituacaoCadastro;
    }

    public void setIdSituacaoCadastro(int idSituacaoCadastro) {
        this.idSituacaoCadastro = idSituacaoCadastro;
    }

    public String getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(String situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getDataHoraUltimoAcesso() {
        return dataHoraUltimoAcesso;
    }

    public void setDataHoraUltimoAcesso(String dataHoraUltimoAcesso) {
        this.dataHoraUltimoAcesso = dataHoraUltimoAcesso;
    }

    public List<PermissaoVO> getvPermissao() {
        return vPermissao;
    }

    public void setvPermissao(List<PermissaoVO> vPermissao) {
        this.vPermissao = vPermissao;
    }

    public List<UsuarioSecaoVO> getvSecao() {
        return vSecao;
    }

    public void setvSecao(List<UsuarioSecaoVO> vSecao) {
        this.vSecao = vSecao;
    }
}
