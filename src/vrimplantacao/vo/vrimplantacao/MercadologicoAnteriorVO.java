package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author Leandro
 */
public class MercadologicoAnteriorVO {
    
    public int mercAnterior1 = 0;
    public int mercAnterior2 = 0;
    public int mercAnterior3 = 0;
    public int mercAnterior4 = 0;
    public int mercAnterior5 = 0;    
    public String descricao = "AJUSTAR";
    public int nivel = 1;    
    public int mercAtual1 = 0;
    public int mercAtual2 = 0;
    public int mercAtual3 = 0;
    public int mercAtual4 = 0;
    public int mercAtual5 = 0;

    public MercadologicoAnteriorVO(int merc1, int merc2, int merc3, int merc4, int merc5, String descricao, int nivel) {
        this.mercAnterior1 = merc1;
        this.mercAnterior2 = merc2;
        this.mercAnterior3 = merc3;
        this.mercAnterior4 = merc4;
        this.mercAnterior5 = merc5;
        
        this.mercAtual1 = merc1;
        this.mercAtual2 = merc2;
        this.mercAtual3 = merc3;
        this.mercAtual4 = merc4;
        this.mercAtual5 = merc5;
        
        this.descricao = descricao;
        this.nivel = nivel;
    }

    @Override
    public String toString() {
        return mercAnterior1 + "-" + mercAnterior2 + "-" + mercAnterior3 + "-" + mercAnterior4 + "-" + mercAnterior5;
    }

    
    
    
    
}
