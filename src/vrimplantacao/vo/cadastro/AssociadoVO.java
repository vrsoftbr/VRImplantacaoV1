package vrimplantacao.vo.cadastro;

import java.util.List;
import java.util.ArrayList;

public class AssociadoVO {

    public long id = 0;
    public int idProduto = 0;
    public String produto = "";
    public int qtdEmbalagem = 0;
    public List<AssociadoItemVO> vProduto = new ArrayList();
}