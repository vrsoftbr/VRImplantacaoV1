package vrimplantacao2.vo.enums;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 *
 * @author Leandro
 */
public class TipoReceita {

    public static final TipoReceita CR_OUTRAS_UNIDADES = new TipoReceita(1, "CR-OUTRAS UNIDADES");

    static {
        Map<Integer, TipoReceita> map = new HashMap<>();

        map.put(CR_OUTRAS_UNIDADES.getId(), CR_OUTRAS_UNIDADES);
        
        MAPA_POR_ID = Collections.unmodifiableMap(map);
    }

    public static TipoReceita getById(int id) {
        TipoReceita tipo = MAPA_POR_ID.get(id);

        return tipo != null ? tipo : CR_OUTRAS_UNIDADES;
    }

    private static final Map<Integer, TipoReceita> MAPA_POR_ID;

    private int id;
    private String descricao;

    public TipoReceita() {
    }

    public TipoReceita(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

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
        this.descricao = descricao;
    }

}