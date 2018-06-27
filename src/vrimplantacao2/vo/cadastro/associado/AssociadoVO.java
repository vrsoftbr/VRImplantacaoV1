package vrimplantacao2.vo.cadastro.associado;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa o associado no banco de dados.
 * @author Leandro
 */
public class AssociadoVO {
    
    private int id;// integer NOT NULL DEFAULT nextval('associado_id_seq'::regclass),
    private int idProduto; //id_produto integer NOT NULL,
    private int qtdEmbalagem;// integer NOT NULL,
    private final Map<Integer, AssociadoItemVO> itens = new HashMap<>();

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

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem < 1 ? 1 : qtdEmbalagem;
    }

    public Map<Integer, AssociadoItemVO> getItens() {
        return itens;
    }
    
}
