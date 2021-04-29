
package vrimplantacao2.vo.importacao;

import java.util.Objects;
import vrimplantacao.utils.Utils;

public class MercadologicoIMP {
    
    private String importSistema;
    private String importLoja;
    
    private String Merc1ID = "";
    private String Merc1Descricao;
    private String Merc2ID = "";
    private String Merc2Descricao;
    private String Merc3ID = "";
    private String Merc3Descricao;
    private String Merc4ID = "";
    private String Merc4Descricao;
    private String Merc5ID = "";
    private String Merc5Descricao;

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public void setMerc1ID(String Merc1ID) {
        this.Merc1ID = Utils.isNull(Merc1ID) ;
    }
    
    public void setMerc2ID(String Merc2ID) {
        this.Merc2ID = Utils.isNull(Merc2ID) ;
    }
    
    public void setMerc3ID(String Merc3ID) {
        this.Merc3ID = Utils.isNull(Merc3ID) ;
    }
    
    public void setMerc4ID(String Merc4ID) {
        this.Merc4ID = Utils.isNull(Merc4ID) ;
    }
    
    public void setMerc5ID(String Merc5ID) {
        this.Merc5ID = Utils.isNull(Merc5ID) ;
    }

    public void setMerc1Descricao(String Merc1Descricao) {
        this.Merc1Descricao = Utils.acertarTexto(Merc1Descricao);
    }

    public void setMerc2Descricao(String Merc2Descricao) {
        this.Merc2Descricao = Utils.acertarTexto(Merc2Descricao);
    }

    public void setMerc3Descricao(String Merc3Descricao) {
        this.Merc3Descricao = Utils.acertarTexto(Merc3Descricao);
    }

    public void setMerc4Descricao(String Merc4Descricao) {
        this.Merc4Descricao = Utils.acertarTexto(Merc4Descricao);
    }

    public void setMerc5Descricao(String Merc5Descricao) {
        this.Merc5Descricao = Utils.acertarTexto(Merc5Descricao);
    }

    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public String getMerc1ID() {
        return Merc1ID;
    }

    public String getMerc2ID() {
        return Merc2ID;
    }

    public String getMerc3ID() {
        return Merc3ID;
    }

    public String getMerc4ID() {
        return Merc4ID;
    }

    public String getMerc5ID() {
        return Merc5ID;
    }

    public String getMerc1Descricao() {
        return Merc1Descricao;
    }

    public String getMerc2Descricao() {
        return Merc2Descricao;
    }

    public String getMerc3Descricao() {
        return Merc3Descricao;
    }

    public String getMerc4Descricao() {
        return Merc4Descricao;
    }

    public String getMerc5Descricao() {
        return Merc5Descricao;
    }  

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.importSistema);
        hash = 83 * hash + Objects.hashCode(this.importLoja);
        hash = 83 * hash + Objects.hashCode(this.Merc1ID);
        hash = 83 * hash + Objects.hashCode(this.Merc2ID);
        hash = 83 * hash + Objects.hashCode(this.Merc3ID);
        hash = 83 * hash + Objects.hashCode(this.Merc4ID);
        hash = 83 * hash + Objects.hashCode(this.Merc5ID);
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
        final MercadologicoIMP other = (MercadologicoIMP) obj;
        if (!Objects.equals(this.importSistema, other.importSistema)) {
            return false;
        }
        if (!Objects.equals(this.importLoja, other.importLoja)) {
            return false;
        }
        if (!Objects.equals(this.Merc1ID, other.Merc1ID)) {
            return false;
        }
        if (!Objects.equals(this.Merc2ID, other.Merc2ID)) {
            return false;
        }
        if (!Objects.equals(this.Merc3ID, other.Merc3ID)) {
            return false;
        }
        if (!Objects.equals(this.Merc4ID, other.Merc4ID)) {
            return false;
        }
        return Objects.equals(this.Merc5ID, other.Merc5ID);
    }

    @Override
    public String toString() {
        return "MercadologicoIMP{" + "importSistema=" + importSistema + ", importLoja=" + importLoja + ", Merc1ID=" + Merc1ID + ", Merc1Descricao=" + Merc1Descricao + ", Merc2ID=" + Merc2ID + ", Merc2Descricao=" + Merc2Descricao + ", Merc3ID=" + Merc3ID + ", Merc3Descricao=" + Merc3Descricao + ", Merc4ID=" + Merc4ID + ", Merc4Descricao=" + Merc4Descricao + ", Merc5ID=" + Merc5ID + ", Merc5Descricao=" + Merc5Descricao + '}';
    }
    
}
