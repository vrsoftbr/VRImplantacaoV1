package vrimplantacao2.vo.importacao;

public class TipoSetorIMP {

    private String importSistema;
    private String importLoja;
    
    private String importId;
    private String descricao;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String[] getChave() {
        return new String[] {
            this.importSistema,
            this.importLoja,
            this.importId
        };
    }
    
}
