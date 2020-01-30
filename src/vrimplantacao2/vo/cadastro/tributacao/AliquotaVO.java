package vrimplantacao2.vo.cadastro.tributacao;

/**
 *
 * @author Leandro
 */
public class AliquotaVO {
    
    public static final AliquotaVO OUTRAS = new AliquotaVO(8, "OUTRAS", 90, 0, 0);
    
    private int id;
    private String descricao;
    private int cst;
    private double aliquota;
    private double reduzido;

    public AliquotaVO() {
    }

    public AliquotaVO(int id, String descricao, int cst, double aliquota, double reduzido) {
        this.id = id;
        this.descricao = descricao;
        this.cst = cst;
        this.aliquota = aliquota;
        this.reduzido = reduzido;
    }
    
    public AliquotaVO(int cst, double aliquota, double reduzido) {
        this(-1, "", cst, aliquota, reduzido);
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return "AliquotaVO{" + "cst=" + cst + ", aliquota=" + aliquota + ", reduzido=" + reduzido + '}';
    }
    
}
