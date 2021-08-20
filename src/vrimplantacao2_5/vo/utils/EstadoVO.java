package vrimplantacao2_5.vo.utils;

/**
 *
 * @author Desenvolvimento
 */
public class EstadoVO {

    private int id;
    private String sigla;
    private String descricao;
    private int codigoGia;
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the sigla
     */
    public String getSigla() {
        return sigla;
    }

    /**
     * @param sigla the sigla to set
     */
    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the codigoGia
     */
    public int getCodigoGia() {
        return codigoGia;
    }

    /**
     * @param codigoGia the codigoGia to set
     */
    public void setCodigoGia(int codigoGia) {
        this.codigoGia = codigoGia;
    }
}
