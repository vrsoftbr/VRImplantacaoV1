package vrimplantacao.vo.cadastro;

import java.util.List;
import java.util.ArrayList;

public class ContratoFornecedorVO {

    public long id = 0;
    public int idFornecedor = 0;
    public String fornecedor = "";
    public int idFornecedorCobranca = 0;
    public String fornecedorCobranca = "";
    public List<ContratoFornecedorExcecaoVO> vExcecao = new ArrayList();
}