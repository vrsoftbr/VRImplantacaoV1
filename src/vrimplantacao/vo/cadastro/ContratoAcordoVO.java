package vrimplantacao.vo.cadastro;

import java.util.ArrayList;
import java.util.List;

public class ContratoAcordoVO {

    public long id = 0;
    public int idTipoAcordo = 0;
    public String tipoAcordo = "";
    public double percentual = 0;
    public double valorBaseCalculo = 0;
    public List<ContratoAcordoExcecaoLojaVO> vExcecao = new ArrayList();
}