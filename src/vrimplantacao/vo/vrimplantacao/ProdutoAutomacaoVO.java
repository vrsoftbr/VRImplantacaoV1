/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author handerson
 */
public class ProdutoAutomacaoVO {

    public long codigoBarras = 0;
    public int idTipoEmbalagem = -1;
    public String tipoEmbalagem = "";
    public int qtdEmbalagem = 1;
    public double precoVenda = 0;
    public List<ProdutoAutomacaoDescontoVO> vDesconto = new ArrayList();

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public int getIdTipoEmbalagem() {
        return idTipoEmbalagem;
    }

    public void setIdTipoEmbalagem(int idTipoEmbalagem) {
        this.idTipoEmbalagem = idTipoEmbalagem;
    }

    public String getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(String tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public List<ProdutoAutomacaoDescontoVO> getvDesconto() {
        return vDesconto;
    }

    public void setvDesconto(List<ProdutoAutomacaoDescontoVO> vDesconto) {
        this.vDesconto = vDesconto;
    }
    
    
}
