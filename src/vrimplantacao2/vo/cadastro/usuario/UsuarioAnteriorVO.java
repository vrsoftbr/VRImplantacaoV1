package vrimplantacao2.vo.cadastro.usuario;

import vrimplantacao2.vo.cadastro.fornecedor.*;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class UsuarioAnteriorVO {

    private String importSistema;
    private String importLoja;
    private String importId;
    private UsuarioVO codigoAtual;
    private String login;
    private String nome;
    private int idTipoSetor;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;

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

    public UsuarioVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(UsuarioVO codigoAtual) {
        this.codigoAtual = codigoAtual;
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

//    public String getImportSistema() {
//        return importSistema;
//    }
//
//    public String getImportLoja() {
//        return importLoja;
//    }
//
//    public String getImportId() {
//        return importId;
//    }
//
//    public FornecedorVO getCodigoAtual() {
//        return codigoAtual;
//    }
//
//    public String getCnpjCpf() {
//        return cnpjCpf;
//    }
//
//    public String getRazao() {
//        return razao;
//    }
//
//    public String getFantasia() {
//        return fantasia;
//    }
//
//    public void setImportSistema(String importSistema) {
//        this.importSistema = importSistema;
//    }
//
//    public void setImportLoja(String importLoja) {
//        this.importLoja = importLoja;
//    }
//
//    public void setImportId(String importId) {
//        this.importId = importId;
//    }
//
//    public void setCodigoAtual(FornecedorVO codigoAtual) {
//        this.codigoAtual = codigoAtual;
//    }
//
//    public void setCnpjCpf(String cnpjCpf) {
//        this.cnpjCpf = cnpjCpf;
//    }
//
//    public void setRazao(String razao) {
//        this.razao = razao;
//    }
//
//    public void setFantasia(String fantasia) {
//        this.fantasia = fantasia;
//    }
//    
//    public String[] getChave() {
//        return new String[] {
//            getImportSistema(),
//            getImportLoja(),
//            getImportId()
//        };
//    }
//    
//    public int getIdConexao() {
//        return this.idConexao;
//    }
//    
//    public void setIdConexao(int idConexao) {
//        this.idConexao = idConexao;
//    }
}
