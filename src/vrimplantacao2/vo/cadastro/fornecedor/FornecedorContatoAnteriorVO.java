package vrimplantacao2.vo.cadastro.fornecedor;

public class FornecedorContatoAnteriorVO {
    
    private String importSistema;
    private String importLoja;
    private String importFornecedorId;
    private String importId;
    private FornecedorContatoVO codigoAtual;

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

    public FornecedorContatoVO getCodigoAtual() {
        return codigoAtual;
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

    public void setCodigoAtual(FornecedorContatoVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }
    
}
