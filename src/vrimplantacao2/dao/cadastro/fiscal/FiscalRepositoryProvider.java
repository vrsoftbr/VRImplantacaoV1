package vrimplantacao2.dao.cadastro.fiscal;

/**
 *
 * @author Leandro
 */
public class FiscalRepositoryProvider {
    private final String sistema;
    private final String loja;
    private final int lojaVR;

    public FiscalRepositoryProvider(String sistema, String loja, int lojaVR) {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;        
    }

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public int getLojaVR() {
        return lojaVR;
    }
    
    
    
}
