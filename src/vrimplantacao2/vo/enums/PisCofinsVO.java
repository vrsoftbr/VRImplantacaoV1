package vrimplantacao2.vo.enums;

/**
 * Classe que representa o PIS/COFINS. Utilize os métodos getPisConfisDebito() e
 * getPisConfisCredito().
 * @author Leandro
 */
public final class PisCofinsVO {   

    private final int id;
    private final String sigla;
    private final int cst;
    private final boolean credito;

    public PisCofinsVO(int id, String sigla, int cst, boolean credito) {
        this.id = id;
        this.sigla = sigla;
        this.cst = cst;
        this.credito = credito;
    }

    public int getId() {
        return id;
    }

    public String getSigla() {
        return sigla;
    }

    public int getCst() {
        return cst;
    }

    public boolean isCredito() {
        return credito;
    }
    
}
