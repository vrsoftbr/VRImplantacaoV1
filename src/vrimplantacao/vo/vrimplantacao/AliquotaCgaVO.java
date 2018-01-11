package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author lucasrafael
 */
public class AliquotaCgaVO {
    private int codigo;
    private String aliquotadescricao;
    private double aliquotaNFperc;
    private double aliquotaNFred;
    private double aliquotaperc;
    private String codigoaliquota;

    /**
     * @return the codigo
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the aliquotadescricao
     */
    public String getAliquotadescricao() {
        return aliquotadescricao;
    }

    /**
     * @param aliquotadescricao the aliquotadescricao to set
     */
    public void setAliquotadescricao(String aliquotadescricao) {
        this.aliquotadescricao = aliquotadescricao;
    }

    /**
     * @return the aliquotaNFperc
     */
    public double getAliquotaNFperc() {
        return aliquotaNFperc;
    }

    /**
     * @param aliquotaNFperc the aliquotaNFperc to set
     */
    public void setAliquotaNFperc(double aliquotaNFperc) {
        this.aliquotaNFperc = aliquotaNFperc;
    }

    /**
     * @return the aliquotaNFred
     */
    public double getAliquotaNFred() {
        return aliquotaNFred;
    }

    /**
     * @param aliquotaNFred the aliquotaNFred to set
     */
    public void setAliquotaNFred(double aliquotaNFred) {
        this.aliquotaNFred = aliquotaNFred;
    }

    /**
     * @return the aliquotaperc
     */
    public double getAliquotaperc() {
        return aliquotaperc;
    }

    /**
     * @param aliquotaperc the aliquotaperc to set
     */
    public void setAliquotaperc(double aliquotaperc) {
        this.aliquotaperc = aliquotaperc;
    }

    /**
     * @return the codigoaliquota
     */
    public String getCodigoaliquota() {
        return codigoaliquota;
    }

    /**
     * @param codigoaliquota the codigoaliquota to set
     */
    public void setCodigoaliquota(String codigoaliquota) {
        this.codigoaliquota = codigoaliquota;
    }    
}
