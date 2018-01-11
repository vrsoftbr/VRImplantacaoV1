package vrimplantacao.vo.vrimplantacao;

import java.sql.Date;
import vrimplantacao.utils.Utils;

public class OfertaVO {
    public int id_loja = 0;
    public int id_produto = 0;
    private String idProduto = "";
    public double id_produtoDouble = 0;
    public String datainicio = "";
    public String datatermino = "";
    public double precooferta = 0;
    public double preconormal = 0;
    public int id_situacaooferta = 1;
    public int id_tipooferta = 1;
    public boolean precoimediato = true;
    public boolean ofertafamilia = true;
    public boolean ofertaassociado = true;
    public int controle = 0;

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public int getId_produto() {
        return id_produto;
    }
    
    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public double getId_produtoDouble() {
        return id_produtoDouble;
    }

    public void setId_produtoDouble(double id_produtoDouble) {
        this.id_produtoDouble = id_produtoDouble;
    }
    
    public String getDatainicio() {
        return datainicio;
    }

    public void setDatainicio(String datainicio) {
        this.datainicio = datainicio;
    }
    
    public void setDatainicio(Date datainicio) {
        this.datainicio = Utils.formatDate(datainicio);
    }

    public String getDatatermino() {
        return datatermino;
    }

    public void setDatatermino(String datatermino) {
        this.datatermino = datatermino;
    }
    
    public void setDatatermino(Date datatermino) {
        this.datatermino = Utils.formatDate(datatermino);
    }


    public double getPrecooferta() {
        return precooferta;
    }

    public void setPrecooferta(double precooferta) {
        this.precooferta = precooferta;
    }

    public double getPreconormal() {
        return preconormal;
    }

    public void setPreconormal(double preconormal) {
        this.preconormal = preconormal;
    }

    public int getId_situacaooferta() {
        return id_situacaooferta;
    }

    public void setId_situacaooferta(int id_situacaooferta) {
        this.id_situacaooferta = id_situacaooferta;
    }

    public int getId_tipooferta() {
        return id_tipooferta;
    }

    public void setId_tipooferta(int id_tipooferta) {
        this.id_tipooferta = id_tipooferta;
    }

    public boolean isPrecoimediato() {
        return precoimediato;
    }

    public void setPrecoimediato(boolean precoimediato) {
        this.precoimediato = precoimediato;
    }

    public boolean isOfertafamilia() {
        return ofertafamilia;
    }

    public void setOfertafamilia(boolean ofertafamilia) {
        this.ofertafamilia = ofertafamilia;
    }

    public boolean isOfertaassociado() {
        return ofertaassociado;
    }

    public void setOfertaassociado(boolean ofertaassociado) {
        this.ofertaassociado = ofertaassociado;
    }

    public int getControle() {
        return controle;
    }

    public void setControle(int controle) {
        this.controle = controle;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }
}