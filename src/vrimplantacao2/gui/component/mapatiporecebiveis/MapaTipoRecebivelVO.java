package vrimplantacao2.gui.component.mapatiporecebiveis;

import vrimplantacao2.vo.cadastro.financeiro.TipoRecebivelVO;

/**
 * Classe que representa um registro na tabela implantacao.mapatiporecebivel.
 * @author Leandro
 */
public class MapaTipoRecebivelVO {
    private String sistema;
    private String agrupador;
    private String id;
    private String descricao;
    private TipoRecebivelVO codigoatual;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoRecebivelVO getCodigoatual() {
        return codigoatual;
    }

    public void setCodigoatual(TipoRecebivelVO codigoatual) {
        this.codigoatual = codigoatual;
    }
    
}
