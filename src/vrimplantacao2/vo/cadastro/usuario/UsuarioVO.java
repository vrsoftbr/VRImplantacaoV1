package vrimplantacao2.vo.cadastro.usuario;

import java.time.LocalDate;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class UsuarioVO {

    private int id;
    private String login;
    private String nome = "SEM NOME";
    private String senha = "**********";
    private int idTipoSetor = 1;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private int idLoja = 1;
    private LocalDate dataHoraUltimoAcesso = null;
    private boolean verificaAtualizacao = true;
    private int idTema = 1;
    private boolean exibePopupsNovidades = true;
    private boolean exibepopupestoquecongelado = false;
    private int tempoVerificacaoPopup = 180;
    private boolean exibePopupOfertaContingencia = false;

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

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public LocalDate getDataHoraUltimoAcesso() {
        return dataHoraUltimoAcesso;
    }

    public void setDataHoraUltimoAcesso(LocalDate dataHoraUltimoAcesso) {
        this.dataHoraUltimoAcesso = dataHoraUltimoAcesso;
    }

    public boolean isVerificaAtualizacao() {
        return verificaAtualizacao;
    }

    public void setVerificaAtualizacao(boolean verificaAtualizacao) {
        this.verificaAtualizacao = verificaAtualizacao;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

    public boolean getExibePopupsNovidades() {
        return exibePopupsNovidades;
    }

    public void setExibePopupsNovidades(boolean exibePopupsNovidades) {
        this.exibePopupsNovidades = exibePopupsNovidades;
    }

    public boolean getExibepopupestoquecongelado() {
        return exibepopupestoquecongelado;
    }

    public void setExibepopupestoquecongelado(boolean exibepopupestoquecongelado) {
        this.exibepopupestoquecongelado = exibepopupestoquecongelado;
    }

    public int getTempoVerificacaoPopup() {
        return tempoVerificacaoPopup;
    }

    public void setTempoVerificacaoPopup(int tempoVerificacaoPopup) {
        this.tempoVerificacaoPopup = tempoVerificacaoPopup;
    }

    public boolean getExibePopupOfertaContingencia() {
        return exibePopupOfertaContingencia;
    }

    public void setExibePopupOfertaContingencia(boolean exibePopupOfertaContingencia) {
        this.exibePopupOfertaContingencia = exibePopupOfertaContingencia;
    }

}
