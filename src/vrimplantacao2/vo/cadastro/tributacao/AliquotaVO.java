package vrimplantacao2.vo.cadastro.tributacao;

import vr.core.utils.MathUtils;
import vr.core.utils.StringUtils;

/**
 *
 * @author Leandro
 */
public class AliquotaVO {
    
    public static final AliquotaVO OUTRAS = new AliquotaVO(8, "OUTRAS", 90, 0, 0);
    
    private int id = -1;
    private String descricao;
    private int cst;
    private double aliquota;
    private double reduzido;
    private double fcp;
    private boolean desonerado;
    private double porcentagemDesonerado;
    private int idAliquotaPdv;

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
        this.descricao = StringUtils.acertarTexto(descricao, 15);
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
    
    public double getAliquotaFinal() {
        return MathUtils.round(aliquota * (100 - reduzido) / 100, 2);
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

    public int getIdAliquotaPdv() {
        return idAliquotaPdv;
    }

    public void setIdAliquotaPdv(int idAliquotaPdv) {
        this.idAliquotaPdv = idAliquotaPdv;
    }

    @Override
    public String toString() {
        return "AliquotaVO{" + "cst=" + cst + ", aliquota=" + aliquota + ", reduzido=" + reduzido + '}';
    }
    
}
