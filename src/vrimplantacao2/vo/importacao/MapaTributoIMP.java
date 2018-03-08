package vrimplantacao2.vo.importacao;

/**
 *
 * @author Leandro
 */
public class MapaTributoIMP {
    private String id;
    private String descricao;
    private int cst;
    private double aliquota;
    private double reduzido;

    public MapaTributoIMP(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public MapaTributoIMP(String id, String descricao, int cst, double aliquota, double reduzido) {
        this.id = id;
        this.descricao = descricao;
        this.cst = cst;
        this.aliquota = aliquota;
        this.reduzido = reduzido;
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
    
}
