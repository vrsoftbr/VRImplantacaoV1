package vrimplantacao2.dao.cadastro.financeiro.creditorotativo;

import java.text.SimpleDateFormat;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoVO;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoRepositoryClasses {
    
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        
    public static CreditoRotativoVO getVO1() throws Exception {
        CreditoRotativoVO r = new CreditoRotativoVO();
        
        r.setId(1);
        r.setId_clientePreferencial(10);
        r.setId_loja(1);
        r.setDataEmissao(format.parse("10/09/2017"));
        r.setDataVencimento(format.parse("20/09/2017"));
        r.setValor(6.56);
        r.setValorJuros(2);
        r.setNumeroCupom(1892);
        
        return r;
    }
    
    public static CreditoRotativoAnteriorVO getAnterior1(boolean comCodigoAtual) throws Exception {
        CreditoRotativoAnteriorVO r = new CreditoRotativoAnteriorVO();
        
        CreditoRotativoVO vo = getVO1();
        r.setSistema("TESTE");
        r.setLoja("1");
        r.setId("ASF2");
        r.setIdCliente("10");
        if (comCodigoAtual) {
            r.setCodigoAtual(vo);
        }
        r.setPago(false);
        r.setValor(vo.getValor());
        r.setVencimento(vo.getDataVencimento());
        
        return r;
    }
    
    public static CreditoRotativoVO getVO2() throws Exception {
        CreditoRotativoVO r = new CreditoRotativoVO();
        
        r.setId(2);        
        r.setId_clientePreferencial(10);
        r.setId_loja(1);
        r.setDataEmissao(format.parse("10/09/2017"));
        r.setDataVencimento(format.parse("20/09/2017"));
        r.setValor(6.56);
        r.setValorJuros(2);
        r.setNumeroCupom(1892);
        
        return r;
    }
    
    public static CreditoRotativoAnteriorVO getAnterior2(boolean comCodigoAtual) throws Exception {
        CreditoRotativoAnteriorVO r = new CreditoRotativoAnteriorVO();
        
        CreditoRotativoVO vo = getVO2();
        r.setSistema("TESTE");
        r.setLoja("2");
        r.setId("IOPI3");
        r.setIdCliente("11");
        if (comCodigoAtual) {
            r.setCodigoAtual(vo);
        }
        r.setPago(false);
        r.setValor(vo.getValor());
        r.setVencimento(vo.getDataVencimento());
        
        return r;
    }
    
    public static CreditoRotativoItemVO getItem1() throws Exception {
        CreditoRotativoItemVO item = new CreditoRotativoItemVO();
        CreditoRotativoVO vo = getVO1();
        
        item.setId(1);
        item.setDataPagamento(format.parse("15/09/2017"));
        item.setId_loja(1);
        item.setId_receberCreditoRotativo(vo.getId());
        item.setValor(3.02);
        item.setValor(1.0);
        item.setObservacao("OBS ITEM 1");
        
        return item;
    }
    
    public static CreditoRotativoItemAnteriorVO getAnteriorItem1(boolean comCodigoAtual) throws Exception {
        CreditoRotativoItemAnteriorVO item = new CreditoRotativoItemAnteriorVO();
        CreditoRotativoItemVO vo = getItem1();
        
        item.setSistema("TESTE");
        item.setLoja("1");
        item.setIdCreditoRotativo(getAnterior1(false).getId());
        item.setId("ASDF1");
        if (comCodigoAtual) {
            item.setCodigoAtual(vo.getId());
        }
        item.setDataPagamento(vo.getDataPagamento());
        item.setValor(vo.getValor());
        item.setValorDesconto(vo.getValorDesconto());
        item.setValorMulta(vo.getValorMulta());
        
        return item;
    }
}
