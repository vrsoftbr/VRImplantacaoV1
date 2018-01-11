package vrimplantacao.vo.administrativo;

import java.util.ArrayList;
import java.util.List;

public class ReceitaVO {

    public int id = 0;
    public String descricao = "";
    public int idSituacaoCadastro = 0;
    public String situacaoCadastro = "";
    public List<ReceitaProdutoVO> vProduto = new ArrayList();
    public List<ReceitaItemVO> vItem = new ArrayList();
}
