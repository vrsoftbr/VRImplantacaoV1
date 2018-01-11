package vrimplantacao2.dao.cadastro.financeiro.recebercaixa;

import java.text.SimpleDateFormat;
import vrimplantacao2.vo.enums.TipoRecebimento;
import vrimplantacao2.vo.importacao.RecebimentoCaixaIMP;
import static org.junit.Assert.*;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaVO;
import vrimplantacao2.vo.enums.SituacaoReceberCaixa;

/**
 *
 * @author Leandro
 */
public class ReceberCaixaRepositoryClasses {
    
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    
    public static RecebimentoCaixaIMP getImp1() throws Exception {
        RecebimentoCaixaIMP imp = new RecebimentoCaixaIMP();
        imp.setId("1234");
        imp.setIdTipoRecebivel("10");
        imp.setDataEmissao(format.parse("13/09/2017"));
        imp.setDataVencimento(format.parse("20/09/2017"));
        imp.setObservacao("TESTE1");
        imp.setTipoRecebimento(TipoRecebimento.CHEQUE);
        imp.setValor(2.25);
        return imp;
    }
    
    public static RecebimentoCaixaIMP getImp2() throws Exception {
        RecebimentoCaixaIMP imp = new RecebimentoCaixaIMP();
        imp.setId("34sddf43");
        imp.setIdTipoRecebivel("dfg0");
        imp.setDataEmissao(format.parse("01/01/2016"));
        imp.setDataVencimento(format.parse("10/02/2016"));
        imp.setObservacao("TESTE2");
        imp.setTipoRecebimento(TipoRecebimento.CARTAO_DEBITO);
        imp.setValor(5.69);
        return imp;
    }
    
    public static RecebimentoCaixaIMP getImp3() throws Exception {
        RecebimentoCaixaIMP imp = new RecebimentoCaixaIMP();
        imp.setId("8974");
        imp.setIdTipoRecebivel("134");
        imp.setDataEmissao(format.parse("30/01/2017"));
        imp.setDataVencimento(format.parse("20/02/2017"));
        imp.setObservacao("TESTE3");
        imp.setTipoRecebimento(TipoRecebimento.CARTEIRA);
        imp.setValor(10.69);
        return imp;
    }
    
    public static RecebimentoCaixaIMP getImp4() throws Exception {
        RecebimentoCaixaIMP imp = new RecebimentoCaixaIMP();
        imp.setId("ASD123");
        imp.setIdTipoRecebivel("789");
        imp.setDataEmissao(format.parse("30/01/2017"));
        imp.setDataVencimento(format.parse("20/02/2017"));
        imp.setObservacao("TESTE4");
        imp.setTipoRecebimento(TipoRecebimento.CARTEIRA);
        imp.setValor(56.69);
        return imp;
    }
    
    public static void assertImp(RecebimentoCaixaIMP expected, RecebimentoCaixaIMP actual) throws Exception {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDataEmissao(), actual.getDataEmissao());
        assertEquals(expected.getDataVencimento(), actual.getDataVencimento());
        assertEquals(expected.getIdTipoRecebivel(), actual.getIdTipoRecebivel());
        assertEquals(expected.getObservacao(), actual.getObservacao());
        assertEquals(expected.getTipoRecebimento(), actual.getTipoRecebimento());
        assertEquals(expected.getValor(), actual.getValor(), 0.01f);
    }
    
    public static RecebimentoCaixaVO getRecebimento1() throws Exception {
        RecebimentoCaixaVO vo = new RecebimentoCaixaVO();
        RecebimentoCaixaIMP imp = getImp1();

        vo.setId(1);
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setIdLoja(1);
        vo.setIdTipoLocalCobranca(0);
        vo.setIdTipoRecebimento(0);
        vo.setIdTipoRecebivel(125);
        vo.setObservacao("IMPORTADO VR " + imp.getObservacao());
        vo.setSituacaoReceberCaixa(SituacaoReceberCaixa.ABERTO);
        vo.setValor(imp.getValor());
        
        return vo;
    }
    
    public static RecebimentoCaixaVO getRecebimento2() throws Exception {
        RecebimentoCaixaVO vo = new RecebimentoCaixaVO();
        RecebimentoCaixaIMP imp = getImp2();

        vo.setId(2);
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setIdLoja(1);
        vo.setIdTipoLocalCobranca(0);
        vo.setIdTipoRecebimento(0);
        vo.setIdTipoRecebivel(125);
        vo.setObservacao("IMPORTADO VR " + imp.getObservacao());
        vo.setSituacaoReceberCaixa(SituacaoReceberCaixa.ABERTO);
        vo.setValor(imp.getValor());
        
        return vo;
    }
    
    public static RecebimentoCaixaVO getRecebimento3() throws Exception {
        RecebimentoCaixaVO vo = new RecebimentoCaixaVO();
        RecebimentoCaixaIMP imp = getImp3();

        vo.setId(3);
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setIdLoja(1);
        vo.setIdTipoLocalCobranca(0);
        vo.setIdTipoRecebimento(0);
        vo.setIdTipoRecebivel(125);
        vo.setObservacao("IMPORTADO VR " + imp.getObservacao());
        vo.setSituacaoReceberCaixa(SituacaoReceberCaixa.ABERTO);
        vo.setValor(imp.getValor());
        
        return vo;
    }
    
    public static RecebimentoCaixaVO getRecebimento4() throws Exception {
        RecebimentoCaixaVO vo = new RecebimentoCaixaVO();
        RecebimentoCaixaIMP imp = getImp4();

        vo.setId(4);
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setIdLoja(1);
        vo.setIdTipoLocalCobranca(0);
        vo.setIdTipoRecebimento(0);
        vo.setIdTipoRecebivel(369);
        vo.setObservacao("IMPORTADO VR " + imp.getObservacao());
        vo.setSituacaoReceberCaixa(SituacaoReceberCaixa.ABERTO);
        vo.setValor(imp.getValor());
        
        return vo;
    }
    
    public static void assertRecebimento(RecebimentoCaixaVO expected, RecebimentoCaixaVO actual) {
        if (expected == null && actual != null) {
            fail("Diferente");
        } else if (expected != null && actual == null) {
            fail("Diferente");
        } else if (expected != null && actual != null) {
            assertEquals(expected.getDataEmissao(), actual.getDataEmissao());
            assertEquals(expected.getDataVencimento(), actual.getDataVencimento());
            assertEquals(expected.getIdLoja(), actual.getIdLoja());
            assertEquals(expected.getIdTipoLocalCobranca(), actual.getIdTipoLocalCobranca());
            assertEquals(expected.getIdTipoRecebimento(), actual.getIdTipoRecebimento());
            assertEquals(expected.getObservacao(), actual.getObservacao());
            assertEquals(expected.getSituacaoReceberCaixa(), actual.getSituacaoReceberCaixa());
            assertEquals(expected.getValor(), actual.getValor(), 0.01f);
        }
    }
    
    public static void assertRecebimentoAnterior(RecebimentoCaixaAnteriorVO expected, RecebimentoCaixaAnteriorVO actual) {
        assertEquals(expected.getAgrupador(), actual.getAgrupador());
        assertRecebimento(expected.getCodigoAtual(), actual.getCodigoAtual());
        assertEquals(expected.getDataEmissao(), actual.getDataEmissao());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getIdTipoRecebivel(), actual.getIdTipoRecebivel());
        assertEquals(expected.getSistema(), actual.getSistema());
        assertEquals(expected.getValor(), actual.getValor(), 0.01f);
        assertEquals(expected.getVencimento(), actual.getVencimento());
    }
    
    public static RecebimentoCaixaAnteriorVO getRecebimentoAnterior1() throws Exception {
        RecebimentoCaixaIMP imp = getImp1();
        
        RecebimentoCaixaAnteriorVO ant = new RecebimentoCaixaAnteriorVO();
        ant.setSistema("TESTE");
        ant.setAgrupador("1");
        ant.setId(imp.getId());
        ant.setDataEmissao(imp.getDataEmissao());
        ant.setVencimento(imp.getDataVencimento());
        ant.setIdTipoRecebivel(imp.getIdTipoRecebivel());
        ant.setValor(imp.getValor());
        
        return ant;
    }
    
    public static RecebimentoCaixaAnteriorVO getRecebimentoAnterior2() throws Exception {
        RecebimentoCaixaIMP imp = getImp2();
        
        RecebimentoCaixaAnteriorVO ant = new RecebimentoCaixaAnteriorVO();
        ant.setSistema("TESTE");
        ant.setAgrupador("1");
        ant.setId(imp.getId());
        ant.setDataEmissao(imp.getDataEmissao());
        ant.setVencimento(imp.getDataVencimento());
        ant.setIdTipoRecebivel(imp.getIdTipoRecebivel());
        ant.setValor(imp.getValor());
        
        return ant;
    }
    
    public static RecebimentoCaixaAnteriorVO getRecebimentoAnterior3() throws Exception {
        RecebimentoCaixaIMP imp = getImp3();
        
        RecebimentoCaixaAnteriorVO ant = new RecebimentoCaixaAnteriorVO();
        ant.setSistema("TESTE");
        ant.setAgrupador("1");
        ant.setId(imp.getId());
        ant.setDataEmissao(imp.getDataEmissao());
        ant.setVencimento(imp.getDataVencimento());
        ant.setIdTipoRecebivel(imp.getIdTipoRecebivel());
        ant.setValor(imp.getValor());
        
        return ant;
    }
    
    public static RecebimentoCaixaAnteriorVO getRecebimentoAnterior4() throws Exception {
        RecebimentoCaixaIMP imp = getImp4();
        
        RecebimentoCaixaAnteriorVO ant = new RecebimentoCaixaAnteriorVO();
        ant.setSistema("TESTE");
        ant.setAgrupador("1");
        ant.setId(imp.getId());
        ant.setDataEmissao(imp.getDataEmissao());
        ant.setVencimento(imp.getDataVencimento());
        ant.setIdTipoRecebivel(imp.getIdTipoRecebivel());
        ant.setValor(imp.getValor());
        
        return ant;
    }
}
