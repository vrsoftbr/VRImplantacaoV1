package vrimplantacao.vo.cadastro;

import java.util.List;
import java.util.ArrayList;

public class ContratoFornecedorExcecaoVO {

    public long id = 0;
    public int idProduto = 0;
    public String produto = "";
    public List<ContratoFornecedorExcecaoAcordoVO> vAcordo = new ArrayList();
}