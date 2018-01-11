package vrimplantacao2.vo.cadastro.fornecedor;

/**
 *
 * @author lucasrafael
 */
public class FornecedorPagamentoAnteriorVO {
    private String importSistema;
    private String importLoja;
    private String importFornecedorId;
    private String importId;
    private FornecedorPagamentoVO codigoAtual;

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

    public FornecedorPagamentoVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(FornecedorPagamentoVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }
    
}
