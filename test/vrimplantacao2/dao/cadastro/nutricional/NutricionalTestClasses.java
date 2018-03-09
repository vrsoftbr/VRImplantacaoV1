package vrimplantacao2.dao.cadastro.nutricional;

import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import static org.junit.Assert.*;
import vrimplantacao2.vo.cadastro.nutricional.NutricionalAnteriorVO;

/**
 *
 * @author Leandro
 */
public final class NutricionalTestClasses {

    public static NutricionalFilizolaVO getFilizola1() {        
        NutricionalFilizolaVO vo = new NutricionalFilizolaVO();
        
        vo.setId(1);
        vo.setDescricao(null);
        vo.setId_situacaocadastro(0);
        vo.setCaloria(150);
        vo.setCarboidrato(12);
        vo.setCarboidratoinferior(true);
        vo.setProteina(34);
        vo.setProteinainferior(false);
        vo.setGordura(5);
        vo.setGordurasaturada(0);
        vo.setGorduratrans(0);
        vo.setColesterolinferior(true);
        vo.setFibra(3);
        vo.setFibrainferior(false);
        vo.setCalcio(10);
        vo.setFerro(6);
        vo.setSodio(9);
        vo.setPercentualcaloria(1);
        vo.setPercentualcarboidrato(10);
        vo.setPercentualproteina(12);
        vo.setPercentualgordura(24);
        vo.setPercentualgordurasaturada(0);
        vo.setPercentualfibra(4);
        vo.setPercentualcalcio(26);
        vo.setPercentualferro(16);
        vo.setPercentualsodio(22);
        vo.setPorcao("120g");
        vo.setMensagemAlergico("TESTE ALERGICO");
        vo.addProduto("12");
        vo.addProduto("29");
        vo.addProduto("18");
        
        return null;
    }

    public static NutricionalIMP getImp1() {
        
        NutricionalIMP imp = new NutricionalIMP();
        
        imp.setId("1");
        imp.setDescricao("BOLO");
        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
        imp.setCaloria(150);
        imp.setCarboidrato(12);
        imp.setCarboidratoInferior(true);
        imp.setProteina(34);
        imp.setProteinaInferior(false);
        imp.setGordura(5);
        imp.setGorduraSaturada(0);
        imp.setGorduraTrans(0);
        imp.setColesterolInferior(true);
        imp.setFibra(3);
        imp.setFibraInferior(false);
        imp.setCalcio(10);
        imp.setFerro(6);
        imp.setSodio(9);
        imp.setPercentualCaloria(1);
        imp.setPercentualCarboidrato(10);
        imp.setPercentualProteina(12);
        imp.setPercentualGordura(24);
        imp.setPercentualGorduraSaturada(0);
        imp.setPercentualFibra(4);
        imp.setPercentualCalcio(26);
        imp.setPercentualFerro(16);
        imp.setPercentualSodio(22);
        imp.setPorcao("120g");
        imp.addMensagemAlergico("Teste alérgico");
        imp.addProduto("12");
        imp.addProduto("29");
        imp.addProduto("18");
        
        return imp;
        
    }

    public static void compararFilizola(NutricionalFilizolaVO expected, NutricionalFilizolaVO actual) {
        
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescricao(), actual.getDescricao());
        assertEquals(expected.getId_situacaocadastro(), actual.getId_situacaocadastro());
        assertEquals(expected.getCaloria(), actual.getCaloria());
        assertEquals(expected.getCarboidrato(), actual.getCarboidrato());
        assertEquals(expected.isCarboidratoinferior(), actual.isCarboidratoinferior());
        assertEquals(expected.getProteina(), actual.getProteina());
        assertEquals(expected.isProteinainferior(), actual.isProteinainferior());
        assertEquals(expected.getGordura(), actual.getGordura());
        assertEquals(expected.getGordurasaturada(), actual.getGordurasaturada());
        assertEquals(expected.getGorduratrans(), actual.getGorduratrans());
        assertEquals(expected.isColesterolinferior(), actual.isColesterolinferior());
        assertEquals(expected.getFibra(), actual.getFibra());
        assertEquals(expected.isFibrainferior(), actual.isFibrainferior());
        assertEquals(expected.getCalcio(), actual.getCalcio());
        assertEquals(expected.getFerro(), actual.getFerro());
        assertEquals(expected.getSodio(), actual.getSodio());
        assertEquals(expected.getPercentualcaloria(), actual.getPercentualcaloria());
        assertEquals(expected.getPercentualcarboidrato(), actual.getPercentualcarboidrato());
        assertEquals(expected.getPercentualproteina(), actual.getPercentualproteina());
        assertEquals(expected.getPercentualgordura(), actual.getPercentualgordura());
        assertEquals(expected.getPercentualgordurasaturada(), actual.getPercentualgordurasaturada());
        assertEquals(expected.getPercentualfibra(), actual.getPercentualfibra());
        assertEquals(expected.getPercentualcalcio(), actual.getPercentualcalcio());
        assertEquals(expected.getPercentualferro(), actual.getPercentualferro());
        assertEquals(expected.getPercentualsodio(), actual.getPercentualsodio());
        assertEquals(expected.getPorcao(), actual.getPorcao());

    }

    public static NutricionalAnteriorVO getAnterior1() {
        NutricionalAnteriorVO ant = new NutricionalAnteriorVO();
        
        ant.setSistema("TESTE");
        ant.setLoja("LOJA");
        ant.setId("1");
        ant.setCodigoAtualFilizola(1);
        ant.setCodigoAtualToledo(1);
        
        return ant;        
    }

    static void compararAnterior(NutricionalAnteriorVO expected, NutricionalAnteriorVO actual) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private NutricionalTestClasses() {}
    
    
    
}
