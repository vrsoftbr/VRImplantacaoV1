package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import static org.junit.Assert.*;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalAnteriorVO;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalVO;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;

/**
 *
 * @author Leandro
 */
public class PautaFiscalRepositoryClasses {

    public static PautaFiscalAnteriorVO getAnt1(boolean comAtual) {
        PautaFiscalAnteriorVO ant = new PautaFiscalAnteriorVO();
        ant.setSistema("TESTE");
        ant.setLoja("1");
        ant.setId("asd1234");
        if (comAtual) {
            ant.setCodigoAtual(PautaFiscalRepositoryClasses.getPauta1());
        }
        return ant;
    }
    
    public static PautaFiscalAnteriorVO getAnt2(boolean comAtual) {
        PautaFiscalAnteriorVO ant = new PautaFiscalAnteriorVO();
        ant.setSistema("TESTE");
        ant.setLoja("1");
        ant.setId("789");
        if (comAtual) {
            ant.setCodigoAtual(PautaFiscalRepositoryClasses.getPauta2());
        }
        return ant;
    }
    
    public static PautaFiscalAnteriorVO getAnt3(boolean comAtual) {
        PautaFiscalAnteriorVO ant = new PautaFiscalAnteriorVO();
        ant.setSistema("TESTE");
        ant.setLoja("1");
        ant.setId("123");
        if (comAtual) {
            ant.setCodigoAtual(PautaFiscalRepositoryClasses.getPauta3());
        }
        return ant;
    }

    public static PautaFiscalVO getPauta1() {
        PautaFiscalVO vo = new PautaFiscalVO();
        
        vo.setId(1);
        vo.setNcm1(201);
        vo.setNcm2(20);
        vo.setNcm3(10);
        vo.setId_estado(35);
        vo.setTipoIva(TipoIva.PERCENTUAL);
        vo.setIva(10);
        vo.setIvaAjustado(10);
        vo.setExcecao(1);
        vo.setId_aliquotaCredito(1);
        vo.setId_aliquotaDebito(1);
        vo.setId_aliquotaDebitoForaEstado(3);
        vo.setIcmsRecolhidoAntecipadamente(false);
        
        return vo;
    }
    
    public static PautaFiscalVO getPauta2() {
        PautaFiscalVO vo = new PautaFiscalVO();
        
        vo.setId(2);
        vo.setNcm1(202);
        vo.setNcm2(30);
        vo.setNcm3(0);
        vo.setId_estado(35);
        vo.setTipoIva(TipoIva.VALOR);
        vo.setIva(10.25);
        vo.setIvaAjustado(0);
        vo.setExcecao(1);
        vo.setId_aliquotaCredito(0);
        vo.setId_aliquotaDebito(1);
        vo.setId_aliquotaDebitoForaEstado(0);
        vo.setIcmsRecolhidoAntecipadamente(true);
        
        return vo;
    }
    
    public static PautaFiscalVO getPauta3() {
        PautaFiscalVO vo = new PautaFiscalVO();
        
        vo.setId(3);
        vo.setNcm1(201);
        vo.setNcm2(20);
        vo.setNcm3(10);
        vo.setId_estado(35);
        vo.setTipoIva(TipoIva.PERCENTUAL);
        vo.setIva(25.8);
        vo.setIvaAjustado(0);
        vo.setExcecao(2);
        vo.setId_aliquotaCredito(1);
        vo.setId_aliquotaDebito(1);
        vo.setId_aliquotaDebitoForaEstado(3);
        vo.setIcmsRecolhidoAntecipadamente(true);
        
        return vo;
    }

    public static PautaFiscalIMP getImp1() {
        PautaFiscalIMP imp = new PautaFiscalIMP();
        
        imp.setId("asd1234");
        imp.setUf("SP");
        imp.setIva(10);
        imp.setIvaAjustado(10);
        imp.setNcm("0201.20.10");
        imp.setIcmsRecolhidoAntecipadamente(true);
        imp.setTipoIva(TipoIva.PERCENTUAL);
        imp.setAliquotaCredito(0, 12, 0);
        imp.setAliquotaDebito(0, 12, 0);
        imp.setAliquotaDebitoForaEstado(0, 25, 0);
        
        return imp;
    }

    public static PautaFiscalIMP getImp2() {
        PautaFiscalIMP imp = new PautaFiscalIMP();
        
        imp.setId("789");
        imp.setUf("SP");
        imp.setIva(10.25);
        imp.setIvaAjustado(0);
        imp.setNcm("0202.30.00");
        imp.setIcmsRecolhidoAntecipadamente(false);
        imp.setTipoIva(TipoIva.VALOR);
        imp.setAliquotaCredito(0, 7, 0);
        imp.setAliquotaDebito(0, 12, 0);
        imp.setAliquotaDebitoForaEstado(0, 7, 0);
        
        return imp;
    }
    
    public static PautaFiscalIMP getImp3() {
        PautaFiscalIMP imp = new PautaFiscalIMP();
        
        imp.setId("123");
        imp.setUf("SP");
        imp.setIva(25.8);
        imp.setIvaAjustado(0);
        imp.setNcm("0201.20.10");
        imp.setIcmsRecolhidoAntecipadamente(false);
        imp.setTipoIva(TipoIva.PERCENTUAL);
        imp.setAliquotaCredito(0, 12, 0);
        imp.setAliquotaDebito(0, 12, 0);
        imp.setAliquotaDebitoForaEstado(0, 25, 0);
        
        return imp;
    }
    
    public static void assertImp(PautaFiscalIMP expected, PautaFiscalIMP actual) {
        if (expected != null && actual != null) {
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getUf(), actual.getUf());
            assertEquals(expected.getIva(), actual.getIva(), 0.0001);
            assertEquals(expected.getIvaAjustado(), actual.getIvaAjustado(), 0.0001);
            assertEquals(expected.getNcm(), actual.getNcm());
            assertEquals(expected.getTipoIva(), actual.getTipoIva());
            assertEquals(expected.getAliquotaCredito().getCst(), actual.getAliquotaCredito().getCst());
            assertEquals(expected.getAliquotaCredito().getAliquota(), actual.getAliquotaCredito().getAliquota(), 0.01);
            assertEquals(expected.getAliquotaCredito().getReduzido(), actual.getAliquotaCredito().getReduzido(), 0.01);
            assertEquals(expected.getAliquotaDebito().getCst(), actual.getAliquotaDebito().getCst());
            assertEquals(expected.getAliquotaDebito().getAliquota(), actual.getAliquotaDebito().getAliquota(), 0.01);
            assertEquals(expected.getAliquotaDebito().getReduzido(), actual.getAliquotaDebito().getReduzido(), 0.01);
            assertEquals(expected.getAliquotaDebitoForaEstado().getCst(), actual.getAliquotaDebitoForaEstado().getCst());
            assertEquals(expected.getAliquotaDebitoForaEstado().getAliquota(), actual.getAliquotaDebitoForaEstado().getAliquota(), 0.01);
            assertEquals(expected.getAliquotaDebitoForaEstado().getReduzido(), actual.getAliquotaDebitoForaEstado().getReduzido(), 0.01);
        } else if (!(expected == null && actual == null)) {
            fail();
        }
    }
    
    public static void assertVO(PautaFiscalVO expected, PautaFiscalVO actual) {
        if (expected != null && actual != null) {
            
            assertEquals(expected.getId_aliquotaCredito(), actual.getId_aliquotaCredito());
            assertEquals(expected.getId_aliquotaCreditoForaEstado(), actual.getId_aliquotaCreditoForaEstado());
            assertEquals(expected.getId_aliquotaDebito(), actual.getId_aliquotaDebito());
            assertEquals(expected.getId_aliquotaDebitoForaEstado(), actual.getId_aliquotaDebitoForaEstado());
            assertEquals(expected.getId_estado(), actual.getId_estado());
            assertEquals(expected.getIva(), actual.getIva(), 0.0001);
            assertEquals(expected.getIvaAjustado(), actual.getIvaAjustado(), 0.0001);
            assertEquals(expected.getNcm1(), actual.getNcm1());
            assertEquals(expected.getNcm2(), actual.getNcm2());
            assertEquals(expected.getNcm3(), actual.getNcm3());
            assertEquals(expected.getTipoIva(), actual.getTipoIva());
            
        } else if (!(expected == null && actual == null)) {
            fail();
        }
    }
    
    public static void assertAnterior(PautaFiscalAnteriorVO expected, PautaFiscalAnteriorVO actual) {
        if (expected != null && actual != null) {
            
            assertEquals(expected.getSistema(), actual.getSistema());
            assertEquals(expected.getLoja(), actual.getLoja());
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getCodigoAtual() != null, actual.getCodigoAtual() != null);
            assertVO(expected.getCodigoAtual(), actual.getCodigoAtual());
            
        } else if (!(expected == null && actual == null)) {
            fail();
        }
    }

    
}
