package vrimplantacao2.dao.cadastro.nutricional;

/**
 *
 * @author Leandro
 */
public class NutricionalRepositoryProvider {
    
    private String sistema;
    private String loja;
    private int lojaVR;

    public NutricionalRepositoryProvider(String sistema, String loja, int lojaVR) {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
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

    public int getLojaVR() {
        return lojaVR;
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
    }
    
    
    
}
