package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

/**
 *
 * @author Importacao
 */
public enum SituacaoPagarOutrasDespesas {
    
    NAO_FINALIZADO(0), FINALIZADO(1);
    public int id;
    
    SituacaoPagarOutrasDespesas(int id){
        this.id = id;
    }
}
