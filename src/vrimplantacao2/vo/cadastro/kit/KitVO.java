package vrimplantacao2.vo.cadastro.kit;

import java.util.Map;
import java.util.HashMap;
import vrimplantacao.vo.administrativo.KitItemVO;

/**
 * Classe que representa um kit no banco de dados.
 * @author Wesley
 */
public class KitVO {
    
    private int id;
    private int idProduto;
    private boolean precoNormal;
    private final Map<Integer, KitItemVO> itens = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public boolean isPrecoNormal() {
        return precoNormal;
    }

    public void setPrecoNormal(boolean precoNormal) {
        this.precoNormal = precoNormal;
    }
}
