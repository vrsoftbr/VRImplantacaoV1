package vrimplantacao2.vo.importacao;

import java.util.Date;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;

/**
 * Classe utilizada para importar as ofertas.
 * @author Leandro
 */
public class OfertaIMP {
    
    private String idProduto;
    private Date dataInicio;
    private Date dataFim;
    private double precoOferta;
    private SituacaoOferta situacaoOferta = SituacaoOferta.ATIVO;
    private TipoOfertaVO tipoOferta = TipoOfertaVO.CAPA;

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public double getPrecoOferta() {
        return precoOferta;
    }

    public void setPrecoOferta(double precoOferta) {
        this.precoOferta = precoOferta;
    }

    public SituacaoOferta getSituacaoOferta() {
        return situacaoOferta;
    }

    public void setSituacaoOferta(SituacaoOferta situacaoOferta) {
        this.situacaoOferta = situacaoOferta;
    }

    public TipoOfertaVO getTipoOferta() {
        return tipoOferta;
    }

    public void setTipoOferta(TipoOfertaVO tipoOferta) {
        this.tipoOferta = tipoOferta;
    }
    
}
