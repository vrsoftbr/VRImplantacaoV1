package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.enums.SituacaoCadastro;


public class FamiliaFornecedorIMP {

    private String importSistema;
    private String importLoja;
    
    private String importId;
    private String descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }
    
    public String[] getChave() {
        return new String[] {
            this.importSistema,
            this.importLoja,
            this.importId
        };
    }
    
}
