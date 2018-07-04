package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import vrimplantacao2.vo.enums.NcmVO;

/**
 *
 * @author Leandro
 */
public class ProdutoPautaVO {
    
    private NcmVO ncm;
    private int id_aliquotaCredito;// integer NOT NULL,
    private int id_aliquotaDebito;// integer NOT NULL,
    private int id_aliquotaDebitoForaEstado;// integer NOT NULL DEFAULT 0,
    private int id_aliquotaCreditoForaEstado;// integer,

    public NcmVO getNcm() {
        return ncm;
    }

    public void setNcm(NcmVO ncm) {
        this.ncm = ncm;
    }

    public int getId_aliquotaCredito() {
        return id_aliquotaCredito;
    }

    public void setId_aliquotaCredito(int id_aliquotaCredito) {
        this.id_aliquotaCredito = id_aliquotaCredito;
    }

    public int getId_aliquotaDebito() {
        return id_aliquotaDebito;
    }

    public void setId_aliquotaDebito(int id_aliquotaDebito) {
        this.id_aliquotaDebito = id_aliquotaDebito;
    }

    public int getId_aliquotaDebitoForaEstado() {
        return id_aliquotaDebitoForaEstado;
    }

    public void setId_aliquotaDebitoForaEstado(int id_aliquotaDebitoForaEstado) {
        this.id_aliquotaDebitoForaEstado = id_aliquotaDebitoForaEstado;
    }

    public int getId_aliquotaCreditoForaEstado() {
        return id_aliquotaCreditoForaEstado;
    }

    public void setId_aliquotaCreditoForaEstado(int id_aliquotaCreditoForaEstado) {
        this.id_aliquotaCreditoForaEstado = id_aliquotaCreditoForaEstado;
    }
    
    
}
