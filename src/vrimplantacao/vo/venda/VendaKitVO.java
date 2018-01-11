package vrimplantacao.vo.venda;

import java.util.ArrayList;
import java.util.List;

public class VendaKitVO {

    public long id = 0;
    public int idProduto = 0;
    public double quantidade = 0;
    public double precoVenda = 0;
    public List<VendaKitItemVO> vItem = new ArrayList();
}