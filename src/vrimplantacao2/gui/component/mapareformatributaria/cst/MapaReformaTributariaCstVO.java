package vrimplantacao2.gui.component.mapareformatributaria.cst;

import vrimplantacao2.vo.cadastro.reformatributaria.CstVO;
import vrimplantacao2.vo.enums.Cst;

/**
 *
 * @author wesley
 */
public class MapaReformaTributariaCstVO {
    
    private String sistema;
    private String loja;
    private String origId;
    private int origCst;
    private String origDescricao;
    private boolean grupoIbsCbs;
    private boolean grupoReducao;
    private boolean grupoDiferimento;
    private boolean grupoTribRegular;
    private boolean grupoIbsCbsMono;
    private Cst cst;

    public MapaReformaTributariaCstVO(String sistema, String loja, String origId, int origCst, String origDescricao, boolean grupoIbsCbs, boolean grupoReducao, boolean grupoDiferimento, boolean grupoTribRegular, boolean grupoIbsCbsMono) {
        this.sistema = sistema;
        this.loja = loja;
        this.origId = origId;
        this.origCst = origCst;
        this.origDescricao = origDescricao;
        this.grupoIbsCbs = grupoIbsCbs;
        this.grupoReducao = grupoReducao;
        this.grupoDiferimento = grupoDiferimento;
        this.grupoTribRegular = grupoTribRegular;
        this.grupoIbsCbsMono = grupoIbsCbsMono;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getOrigId() {
        return origId;
    }

    public void setOrigId(String origId) {
        this.origId = origId;
    }

    public int getOrigCst() {
        return origCst;
    }

    public void setOrigCst(int origCst) {
        this.origCst = origCst;
    }

    public String getOrigDescricao() {
        return origDescricao;
    }

    public void setOrigDescricao(String origDescricao) {
        this.origDescricao = origDescricao;
    }

    public boolean isGrupoIbsCbs() {
        return grupoIbsCbs;
    }

    public void setGrupoIbsCbs(boolean grupoIbsCbs) {
        this.grupoIbsCbs = grupoIbsCbs;
    }

    public boolean isGrupoReducao() {
        return grupoReducao;
    }

    public void setGrupoReducao(boolean grupoReducao) {
        this.grupoReducao = grupoReducao;
    }

    public boolean isGrupoDiferimento() {
        return grupoDiferimento;
    }

    public void setGrupoDiferimento(boolean grupoDiferimento) {
        this.grupoDiferimento = grupoDiferimento;
    }

    public boolean isGrupoTribRegular() {
        return grupoTribRegular;
    }

    public void setGrupoTribRegular(boolean grupoTribRegular) {
        this.grupoTribRegular = grupoTribRegular;
    }

    public boolean isGrupoIbsCbsMono() {
        return grupoIbsCbsMono;
    }

    public void setGrupoIbsCbsMono(boolean grupoIbsCbsMono) {
        this.grupoIbsCbsMono = grupoIbsCbsMono;
    }
    
    public Cst getCst() {
        return cst;
    }

    public void setCst(Cst cst) {
        this.cst = cst;
    }
    
    public CstVO converterEmVo() {
        CstVO vo = new CstVO(
                String.format("%03d", this.origCst),
                this.origDescricao,
                this.grupoIbsCbs,
                this.grupoReducao,
                this.grupoDiferimento,
                this.grupoTribRegular,
                this.grupoIbsCbsMono
        );
                
        return vo;
    }
}