/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author Leandro
 */
public class ProdutoAutomacaoLojaVO {
    public int id_produto = 0;
    public int id = 0;
    public long codigobarras = 0;
    public double precovenda = 0d;
    public int id_loja = 1;
    public int qtdEmbalagem;
    public boolean gravarAutomacao = false;
    public int idTipoEmbalagem = -1;

    @Override
    public String toString() {
        return "ProdutoAutomacaoLojaVO{" + "id_produto=" + id_produto + ", id=" + id + ", codigobarras=" + codigobarras + ", precovenda=" + precovenda + ", id_loja=" + id_loja + ", qtdEmbalagem=" + qtdEmbalagem + '}';
    }
    
    
}
