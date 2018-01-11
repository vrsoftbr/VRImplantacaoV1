package vrimplantacao.vo.administrativo;

import java.util.ArrayList;
import java.util.List;

public class KitVO {

    public long id = 0;
    public int idProduto = 0;
    public String produto = "";
    public boolean preconormal = false;
    public List<KitItemVO> vProduto = new ArrayList();
    public double valorTotal = 0;
    public double custoComImposto = 0;
    public double margemLiquida = 0;
    public double margemBruta = 0;
    public double margemSbCusto;
    public double margemSbVenda;
    public double custoSemImposto = 0;
}
