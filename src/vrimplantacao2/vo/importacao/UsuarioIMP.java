package vrimplantacao2.vo.importacao;

import org.joda.time.LocalDate;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class UsuarioIMP {

    private String importSistema;
    private String importLoja;
    private String importId;
    private String login;
    private String nome;
    private String senha = "**********";
    private int idTipoSetor;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private LocalDate dataHoraUltimoAcesso = null;
    private boolean verificaAtualizacao = true;
    private int idTema = 1;
    private boolean exibePopupsNovidades = true;
    private boolean exibepopupestoquecongelado = false;
    private int tempoVerificacaoPopup = 180;
    private boolean exibePopupOfertaContingencia = false;

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

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
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

    public boolean isExibePopupsNovidades() {
        return exibePopupsNovidades;
    }

    public void setExibePopupsNovidades(boolean exibePopupsNovidades) {
        this.exibePopupsNovidades = exibePopupsNovidades;
    }

    public boolean isExibepopupestoquecongelado() {
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

    public boolean isExibePopupOfertaContingencia() {
        return exibePopupOfertaContingencia;
    }

    public void setExibePopupOfertaContingencia(boolean exibePopupOfertaContingencia) {
        this.exibePopupOfertaContingencia = exibePopupOfertaContingencia;
    }
}
