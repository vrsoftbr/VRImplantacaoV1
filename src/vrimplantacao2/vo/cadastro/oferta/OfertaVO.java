package vrimplantacao2.vo.cadastro.oferta;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.cadastro.ProdutoVO;

public class OfertaVO {
    
    private int id;
    private int idLoja;
    private ProdutoVO produto;
    private Date dataInicio = new Date();
    private Date dataTermino = new Date();
    private double precoOferta = 0;
    private double precoNormal = 0;
    private SituacaoOferta situacaoOferta = SituacaoOferta.ATIVO;
    private TipoOfertaVO tipoOferta = TipoOfertaVO.CAPA;
    private boolean precoImediato = true;
    private boolean ofertaFamilia = false;
    private boolean ofertaAssociado = false;
    private int controle = 0;
    private boolean aplicaPercentualPrecoAssociado = false;
    private boolean encerraOferta = false;
    private int encerraOfertaItem = 0;
    private boolean bloquearVenda = false;
    private double bloquearVendaItens = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public ProdutoVO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoVO produto) {
        this.produto = produto;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio != null ? dataInicio : new Date();
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino != null ? dataTermino : getDataInicio();
    }

    public double getPrecoOferta() {
        return precoOferta;
    }

    public void setPrecoOferta(double precoOferta) {
        this.precoOferta = precoOferta;
    }

    public double getPrecoNormal() {
        return precoNormal;
    }

    public void setPrecoNormal(double precoNormal) {
        this.precoNormal = precoNormal;
    }

    public SituacaoOferta getSituacaoOferta() {
        return situacaoOferta;
    }

    public void setSituacaoOferta(SituacaoOferta situacaoOferta) {
        this.situacaoOferta = situacaoOferta != null ? situacaoOferta : SituacaoOferta.ATIVO;
    }

    public TipoOfertaVO getTipoOferta() {
        return tipoOferta;
    }

    public void setTipoOferta(TipoOfertaVO tipoOferta) {
        this.tipoOferta = tipoOferta != null ? tipoOferta : TipoOfertaVO.CAPA;
    }

    public boolean isPrecoImediato() {
        return precoImediato;
    }

    public void setPrecoImediato(boolean precoImediato) {
        this.precoImediato = precoImediato;
    }

    public boolean isOfertaFamilia() {
        return ofertaFamilia;
    }

    public void setOfertaFamilia(boolean ofertaFamilia) {
        this.ofertaFamilia = ofertaFamilia;
    }

    public boolean isOfertaAssociado() {
        return ofertaAssociado;
    }

    public void setOfertaAssociado(boolean ofertaAssociado) {
        this.ofertaAssociado = ofertaAssociado;
    }

    public int getControle() {
        return controle;
    }

    public void setControle(int controle) {
        this.controle = controle;
    }

    public boolean isAplicaPercentualPrecoAssociado() {
        return aplicaPercentualPrecoAssociado;
    }

    public void setAplicaPercentualPrecoAssociado(boolean aplicaPercentualPrecoAssociado) {
        this.aplicaPercentualPrecoAssociado = aplicaPercentualPrecoAssociado;
    }

    public boolean isEncerraOferta() {
        return encerraOferta;
    }

    public void setEncerraOferta(boolean encerraOferta) {
        this.encerraOferta = encerraOferta;
    }

    public int getEncerraOfertaItem() {
        return encerraOfertaItem;
    }

    public void setEncerraOfertaItem(int encerraOfertaItem) {
        this.encerraOfertaItem = encerraOfertaItem;
    }

    public boolean isBloquearVenda() {
        return bloquearVenda;
    }

    public void setBloquearVenda(boolean bloquearVenda) {
        this.bloquearVenda = bloquearVenda;
    }

    public double getBloquearVendaItens() {
        return bloquearVendaItens;
    }

    public void setBloquearVendaItens(double bloquearVendaItens) {
        this.bloquearVendaItens = MathUtils.trunc(bloquearVendaItens, 2, 9999999);
    }
    
    
}
