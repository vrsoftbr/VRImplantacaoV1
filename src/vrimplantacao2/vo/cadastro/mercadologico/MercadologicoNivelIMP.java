package vrimplantacao2.vo.cadastro.mercadologico;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Leandro
 */
public class MercadologicoNivelIMP {
    
    private String id;
    private String descricao;
    private MercadologicoNivelIMP mercadologicoPai;
    private Map<String, MercadologicoNivelIMP> filhos = new LinkedHashMap<>();

    public MercadologicoNivelIMP() {
    }

    public MercadologicoNivelIMP(String id, String descricao, MercadologicoNivelIMP mercadologicoPai, MercadologicoNivelIMP... filhos) {
        this.id = id;
        this.descricao = descricao;
        this.mercadologicoPai = mercadologicoPai;
        for (MercadologicoNivelIMP filho: filhos) {
            this.filhos.put(filho.getId(), filho);
        }
    }
    
    public MercadologicoNivelIMP(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public MercadologicoNivelIMP getMercadologicoPai() {
        return mercadologicoPai;
    }

    public Map<String, MercadologicoNivelIMP> getNiveis() {
        return filhos;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setMercadologicoPai(MercadologicoNivelIMP mercadologicoPai) {
        this.mercadologicoPai = mercadologicoPai;
    }

    public MercadologicoNivelIMP addFilho(String id, String descricao) {
        MercadologicoNivelIMP merc = new MercadologicoNivelIMP();
        merc.setId(id);
        merc.setDescricao(descricao);
        merc.setMercadologicoPai(this);
        this.getNiveis().put(id, merc);
        return merc;
    }

    @Override
    public String toString() {
        return "MercadologicoNivelIMP{" + "id=" + id + ", descricao=" + descricao + ", mercadologicoPai=" + mercadologicoPai + "}";
    }
    
    
    
}
