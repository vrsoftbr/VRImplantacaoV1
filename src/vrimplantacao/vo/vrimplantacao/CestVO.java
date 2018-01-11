package vrimplantacao.vo.vrimplantacao;

import java.util.LinkedHashSet;
import java.util.Set;
import vrimplantacao.utils.Utils;

public class CestVO {

    public long id = 0;
    public String descricao = "";
    public int cest1 = -1;
    public int cest2 = -1;
    public int cest3 = -1;
    private final Set<NcmVO> ncms = new LinkedHashSet<>();
    
    public CestVO() {
    }
    
    public CestVO(int cest1, int cest2, int cest3) {
        this.cest1 = cest1;
        this.cest2 = cest2;
        this.cest3 = cest3;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = Utils.acertarTexto(descricao, 600, "SEM DESCRICAO DE CEST");
    }

    public int getCest1() {
        return cest1;
    }

    public void setCest1(int cest1) {
        this.cest1 = cest1;
    }

    public int getCest2() {
        return cest2;
    }

    public void setCest2(int cest2) {
        this.cest2 = cest2;
    }

    public int getCest3() {
        return cest3;
    }

    public void setCest3(int cest3) {
        this.cest3 = cest3;
    }
    
    public String getChave() {
        return cest1 + "-" + cest2 + "-" + cest3;
    }

    public Set<NcmVO> getNcms() {
        return ncms;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.cest1;
        hash = 37 * hash + this.cest2;
        hash = 37 * hash + this.cest3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CestVO other = (CestVO) obj;
        if (this.cest1 != other.cest1) {
            return false;
        }
        if (this.cest2 != other.cest2) {
            return false;
        }
        if (this.cest3 != other.cest3) {
            return false;
        }
        return true;
    }
    
    
}