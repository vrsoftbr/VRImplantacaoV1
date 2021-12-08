package vrimplantacao2.vo.cadastro.fornecedor;

public class FornecedorAnteriorVO {
    private String importSistema;
    private String importLoja;
    private String importId;
    private FornecedorVO codigoAtual;
    private String cnpjCpf;
    private String razao;
    private String fantasia;
    private int idConexao;

    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public String getImportId() {
        return importId;
    }

    public FornecedorVO getCodigoAtual() {
        return codigoAtual;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public String getRazao() {
        return razao;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public void setCodigoAtual(FornecedorVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public void setRazao(String razao) {
        this.razao = razao;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }
    
    public String[] getChave() {
        return new String[] {
            getImportSistema(),
            getImportLoja(),
            getImportId()
        };
    }
    
    public int getIdConexao() {
        return this.idConexao;
    }
    
    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }
}