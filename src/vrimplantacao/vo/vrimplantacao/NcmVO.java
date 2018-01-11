package vrimplantacao.vo.vrimplantacao;

import java.util.List;
import java.util.ArrayList;
import vrimplantacao.utils.Utils;

public class NcmVO {

    public long id = 0;
    public String descricao = "SEM DESCRICAO DE NCM";
    public int ncm1 = -1;
    public int ncm2 = -1;
    public int ncm3 = -1;
    public int nivel = 0;
    public String strNcm1 = "";
    public String strNmc2 = "";
    public String strNcm3 = "";
    public String strNcm = "";
    public List<NcmVO> vNcm = new ArrayList();

    public NcmVO() {
    }
    
    public NcmVO(int ncm1, int ncm2, int ncm3) {
        this.ncm1 = ncm1;
        this.ncm2 = ncm2;
        this.ncm3 = ncm3;
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
        this.descricao = Utils.acertarTexto(descricao, 150, "SEM DESCRICAO DE NCM");
    }

    public int getNcm1() {
        return ncm1;
    }

    public void setNcm1(int ncm1) {
        this.ncm1 = ncm1;
    }

    public int getNcm2() {
        return ncm2;
    }

    public void setNcm2(int ncm2) {
        this.ncm2 = ncm2;
    }

    public int getNcm3() {
        return ncm3;
    }

    public void setNcm3(int ncm3) {
        this.ncm3 = ncm3;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public List<NcmVO> getvNcm() {
        return vNcm;
    }

    public void setvNcm(List<NcmVO> vNcm) {
        this.vNcm = vNcm;
    }
    
    public String getChave() {
        return ncm1 + "-" + ncm2 + "-" + ncm3;
    }

    @Override
    public String toString() {
        return "NcmVO{" + "descricao=" + descricao + ", ncm1=" + ncm1 + ", ncm2=" + ncm2 + ", ncm3=" + ncm3 + ", nivel=" + nivel + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NcmVO other = (NcmVO) obj;
        if (this.ncm1 != other.ncm1) {
            return false;
        }
        if (this.ncm2 != other.ncm2) {
            return false;
        }
        if (this.ncm3 != other.ncm3) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.ncm1;
        hash = 79 * hash + this.ncm2;
        hash = 79 * hash + this.ncm3;
        return hash;
    }
    
    
    
}