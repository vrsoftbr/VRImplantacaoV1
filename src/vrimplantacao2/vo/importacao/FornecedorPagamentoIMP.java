package vrimplantacao2.vo.importacao;

public class FornecedorPagamentoIMP {
    private String importSistema;
    private String importLoja;
    private String importFornecedorId;
    private String importId;
    private int vencimento;

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

    public String getImportFornecedorId() {
        return importFornecedorId;
    }

    public void setImportFornecedorId(String importFornecedorId) {
        this.importFornecedorId = importFornecedorId;
    }

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public int getVencimento() {
        return vencimento;
    }

    public void setVencimento(int vencimento) {
        this.vencimento = vencimento;
    }
}
