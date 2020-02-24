package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.enums.TipoContato;

/**
 * Classe que auxilia na importação dos contatos do fornecedor.
 * @author Leandro
 */
public class FornecedorContatoIMP {
    
    private String importSistema;
    private String importLoja;
    private String importFornecedorId;
    private String importId;
    private String nome;
    private String telefone;
    private TipoContato tipoContato = TipoContato.COMERCIAL;
    private String email;
    private String celular;

    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public String getImportFornecedorId() {
        return importFornecedorId;
    }

    public String getImportId() {
        return importId;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public TipoContato getTipoContato() {
        return tipoContato;
    }

    public String getEmail() {
        return email;
    }

    public String getCelular() {
        return celular;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public void setImportFornecedorId(String importFornecedorId) {
        this.importFornecedorId = importFornecedorId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setTipoContato(TipoContato tipoContato) {
        this.tipoContato = tipoContato;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    @Override
    public String toString() {
        return "FornecedorContatoIMP{" + "importSistema=" + importSistema + ", importLoja=" + importLoja + ", importFornecedorId=" + importFornecedorId + ", importId=" + importId + ", nome=" + nome + ", telefone=" + telefone + ", tipoContato=" + tipoContato + ", email=" + email + ", celular=" + celular + '}';
    }
    
}
