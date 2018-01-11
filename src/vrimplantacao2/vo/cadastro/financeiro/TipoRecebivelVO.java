package vrimplantacao2.vo.cadastro.financeiro;

import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;

/**
 * Classe que representa um registro da tabela tiporecebivel.
 * @author Leandro
 */
public class TipoRecebivelVO {
    private int id;
    private String descricao;
    private double percentual;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = Utils.acertarTexto(descricao, 30, "SEM DESCRICAO");
    }

    public double getPercentual() {
        return percentual;
    }

    public void setPercentual(double percentual) {
        this.percentual = MathUtils.round(percentual, 2, 99999999);
    }
    
    
}
