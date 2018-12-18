package vrimplantacao2.vo.enums;

import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public enum TipoProduto {
    
    MERCADORIA_REVENDA (0),
    MATERIA_PRIMA (1),
    EMBALAGEM (2),
    PRODUTO_EM_PROCESSO (3),
    PRODUTO_ACABADO (4),
    SUBPRODUTO (5),
    PRODUTO_INTERMEDIARIO (6),
    MATERIAL_USO_E_CONSUMO (7),
    ATIVO_IMOBILIZADO (8),
    SERVICOS (9),
    OUTROS_INSUMOS (10),
    OUTROS (99);
    
    private final int id;

    private TipoProduto(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public static TipoProduto getById(int id) {
        for (TipoProduto v: values()) {
            if (id == v.id) {
                return v;
            }
        }
        return null;
    }
    
    public static TipoProduto getById(String id) {
        return getById(Utils.stringToInt(id));
    }

}
