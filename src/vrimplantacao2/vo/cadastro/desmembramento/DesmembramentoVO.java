package vrimplantacao2.vo.cadastro.desmembramento;

import java.util.HashMap;
import java.util.Map;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class DesmembramentoVO {

    private int id;
    private int idProduto;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private final Map<Integer, DesmembramentoItemVO> itens = new HashMap<>();

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

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public Map<Integer, DesmembramentoItemVO> getItens() {
        return itens;
    }
}
