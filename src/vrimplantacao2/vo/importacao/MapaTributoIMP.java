package vrimplantacao2.vo.importacao;

/**
 *
 * @author Leandro
 */
public class MapaTributoIMP {
    
    private String id;
    private String descricao;
    private int cst;
    private double aliquota = 0;
    private double reduzido = 0;
    private double fcp = 0;
    private boolean desonerado = false;
    private double porcentagemDesonerado = 0;
    
    public static MapaTributoIMP make(String id, String descricao) {
        return new MapaTributoIMP(id, descricao);
    }
    public static MapaTributoIMP make(String id, String descricao, int cst, double aliquota, double reduzido) {
        return new MapaTributoIMP(id, descricao, cst, aliquota, reduzido);
    }
    public static MapaTributoIMP make(String id, String descricao, int cst, double aliquota, double reduzido, double fcp, boolean desonerado, double porcentagemDesonerado) {
        return new MapaTributoIMP(id, descricao, cst, aliquota, reduzido, fcp, desonerado, porcentagemDesonerado);
    }

    public MapaTributoIMP(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
    
    

    public MapaTributoIMP(String id, String descricao, int cst, double aliquota, double reduzido) {
        this(id, descricao);
        this.cst = cst;
        this.aliquota = aliquota;
        this.reduzido = reduzido;
    }
    
    public MapaTributoIMP(String id, String descricao, int cst, double aliquota, double reduzido, double fcp, boolean desonerado, double porcentagemDesonerado) {
        this(id, descricao, cst, aliquota, reduzido);
        this.fcp = fcp;
        this.desonerado = desonerado;
        this.porcentagemDesonerado = porcentagemDesonerado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCst() {
        return cst;
    }

    public void setCst(int cst) {
        this.cst = cst;
    }

    public double getAliquota() {
        return aliquota;
    }

    public void setAliquota(double aliquota) {
        this.aliquota = aliquota;
    }

    public double getReduzido() {
        return reduzido;
    }

    public void setReduzido(double reduzido) {
        this.reduzido = reduzido;
    }

    public double getFcp() {
        return fcp;
    }

    public void setFcp(double fcp) {
        this.fcp = fcp;
    }

    public boolean isDesonerado() {
        return desonerado;
    }

    public void setDesonerado(boolean desonerado) {
        this.desonerado = desonerado;
    }

    public double getPorcentagemDesonerado() {
        return porcentagemDesonerado;
    }

    public void setPorcentagemDesonerado(double porcentagemDesonerado) {
        this.porcentagemDesonerado = porcentagemDesonerado;
    }
    
}
