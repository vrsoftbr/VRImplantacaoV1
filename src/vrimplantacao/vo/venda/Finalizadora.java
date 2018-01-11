/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.venda;

/**
 *
 * @author lucas
 */
public enum Finalizadora {

    DINHEIRO(1), //vista
    CHEQUE(2), //vista
    TEF(3), //verificar
    TICKET(4), //vista
    CARTAO(5), //verificar
    TROCA_CUPOM(6), //vista
    CONVENIO(7), //prazo 
    CHEQUE_PRE(8), //prazo
    TICKET_REF(9), //vista
    CREDITO_ROTATIVO(10), //prazo
    MULTICHEQUE(11), //prazo
    NOTA_FISCAL(12), //prazo
    CONTRA_VALE(13); //vista
    private int id = 0;

    private Finalizadora(int i_id) {
        this.id = i_id;
    }

    public int getId() throws Exception {
        return id;
    }
}
