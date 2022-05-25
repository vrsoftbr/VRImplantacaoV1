package vrimplantacao2.vo.importacao;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class DesmembramentoIMP {
    
    private String impLoja;
    private String impSistema;
    private String id;
    private String produtoPai;
    private List<DesmembramentoItemIMP> itens = new ArrayList<>();
    private SituacaoCadastro status = SituacaoCadastro.ATIVO;
    
    public String getImpLoja() {
        return impLoja;
    }
    
    public void setImpLoja(String impLoja) {
        this.impLoja = impLoja;
    }
    
     public String getImpSistema() {
        return impSistema;
    }
    
    public void setImpSistema(String impSistema) {
        this.impSistema = impSistema;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProdutoPai() {
        return produtoPai;
    }
    
    public void setProdutoPai(String produtoPai) {
        this.produtoPai = produtoPai;
    }
    
    public List<DesmembramentoItemIMP> getProdutoFilho() {
        return itens;
}
    
    public SituacaoCadastro getStatus() {
        return status;
    }
    
    public void setStatus(SituacaoCadastro status) {
        this.status = status == null ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO;
    }
}
