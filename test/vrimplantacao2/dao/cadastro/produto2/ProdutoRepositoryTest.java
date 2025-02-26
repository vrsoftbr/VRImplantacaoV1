package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.FamiliaProdutoVO;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoAliquotaVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.vo.enums.NormaReposicao;
import vrimplantacao2.vo.enums.PisCofinsVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class ProdutoRepositoryTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProdutoRepositoryProvider provider;
    
    @Before
    public void init() throws Exception {
        when(provider.tributo().getIcms(20, 18, 61.11)).thenReturn(new Icms(4, "18% RED 61.11", 20, 18, 61.11));
        when(provider.tributo().getIcms(eq(0), eq(18d), anyDouble())).thenReturn(new Icms(2, "18%", 0, 18, 0));
        when(provider.tributo().getIcms(eq(0), eq(7d), anyDouble())).thenReturn(new Icms(0, "07%", 0, 7, 0));
        when(provider.tributo().getIcms(eq(10), anyDouble(), anyDouble())).thenReturn(new Icms(2, "SUBSTITUIDO", 0, 0, 0));
        when(provider.tributo().getIcms(10, 0, 0)).thenReturn(new Icms(18, "SUBSTITUICAO", 10, 0, 0));
        when(provider.getFamiliaProduto(argThat(not(equalTo("MOCA 123"))))).thenReturn(null);
        when(provider.getFamiliaProduto("MOCA 123")).thenReturn(new FamiliaProdutoVO(123, "FAMILIA MOCA"));
        when(provider.getLojaVR()).thenReturn(2);
        when(provider.getLoja()).thenReturn("LOJA 02");
        when(provider.getSistema()).thenReturn("TESTE");
        when(provider.getNivelMaximoMercadologico()).thenReturn(3);
        EstadoVO uf = new EstadoVO();
        uf.setId(35);
        uf.setSigla("SP");
        uf.setDescricao("SAO PAULO");
        when(provider.tributo().getUf(2)).thenReturn(uf);
        
        NcmVO ncm = new NcmVO();
        ncm.setId(1);
        ncm.setNcm1(402);
        ncm.setNcm2(99);
        ncm.setNcm3(0);
        ncm.setDescricao("LEITE CONDENSADO(NCM)");
        ncm.setNivel(3);
        when(provider.tributo().getNcm("0402.99.00")).thenReturn(ncm);
        ncm = new NcmVO();
        ncm.setId(2);
        ncm.setNcm1(210);
        ncm.setNcm2(20);
        ncm.setNcm3(0);
        ncm.setDescricao("CORTE BOVINO(NCM)");
        ncm.setNivel(3);
        when(provider.tributo().getNcm("0210.20.00")).thenReturn(ncm);
        
        CestVO cest = new CestVO();
        cest.setId(1);
        cest.setCest1(17);
        cest.setCest2(20);
        
        cest.setCest3(0);
        cest.setDescricao("LEITE CONDENSADO");
        when(provider.tributo().getCest("17.020.00")).thenReturn(cest);
        cest = new CestVO();
        cest.setId(2);
        cest.setCest1(17);
        cest.setCest2(83);
        cest.setCest3(0);
        cest.setDescricao("CARNES BOVINAS");        
        when(provider.tributo().getCest("17.083.00")).thenReturn(cest);
        
        MercadologicoVO merc = new MercadologicoVO();
        merc.setId(1);
        merc.setMercadologico1(1);
        merc.setMercadologico2(2);
        merc.setMercadologico3(1);
        merc.setMercadologico4(0);
        merc.setMercadologico5(0);
        merc.setNivel(3);
        merc.setDescricao("LEITE CONDENSADO");
        when(provider.getMercadologico("MERCEARIA", "LEITE CONDENSADO", "", "", "")).thenReturn(merc);
        merc = new MercadologicoVO();
        merc.setId(2);
        merc.setMercadologico1(2);
        merc.setMercadologico2(4);
        merc.setMercadologico3(1);
        merc.setMercadologico4(0);
        merc.setMercadologico5(0);
        merc.setNivel(3);
        merc.setDescricao("BOVINA");
        when(provider.getMercadologico("ACOUGUE", "BOVINO", "", "", "")).thenReturn(merc);
        
        Map<String, Integer> pautas = new HashMap<>();
        pautas.put("0402.99.00-A", 1);
        pautas.put("0402.99.00-B", 2);
        pautas.put("0402.99.00-C", 3);
        pautas.put("0402.99.00-D", 4);
        when(provider.getPautaExcecao()).thenReturn(pautas);
        
        when(provider.tributo().getPisConfisDebito(eq(1))).thenReturn(new PisCofinsVO(0, "TRIBUTADO", 1, false));
        when(provider.tributo().getPisConfisCredito(eq(50))).thenReturn(new PisCofinsVO(12, "TRIBUTADO (E)", 50, true));
        when(provider.tributo().getNaturezaReceita(anyInt(), anyInt())).thenReturn(null);
        
        when(provider.tributo().getPisConfisDebito(eq(49))).thenReturn(new PisCofinsVO(9, "OUTRAS OPERACOES", 49, false));
        when(provider.tributo().getPisConfisCredito(eq(99))).thenReturn(new PisCofinsVO(21, "OUTRAS OPERACOES (E)", 99, true));
        
        when(provider.tributo().getPisConfisDebito(eq(7))).thenReturn(new PisCofinsVO(1, "ISENTO", 7, false));
        when(provider.tributo().getPisConfisCredito(eq(71))).thenReturn(new PisCofinsVO(13, "ISENTO (E)", 71, true));        
        
        when(provider.tributo().getPisConfisDebito(eq(4))).thenReturn(new PisCofinsVO(3, "MONOFASICO", 4, false));
        when(provider.tributo().getPisConfisCredito(eq(70))).thenReturn(new PisCofinsVO(15, "MONOFASICO (E)", 70, true));    
        
        when(provider.tributo().getPisConfisDebito(eq(6))).thenReturn(new PisCofinsVO(7, "TRIB - ALIQUOTA ZERO", 6, false));
        when(provider.tributo().getPisConfisCredito(eq(73))).thenReturn(new PisCofinsVO(19, "TRIB - ALIQUOTA ZERO(E)", 73, true));
        
        
        when(provider.tributo().getNaturezaReceita(eq(2), eq(403))).thenReturn(new NaturezaReceitaVO(22, 2, 403, "REFRIGERANTES"));
        when(provider.tributo().getNaturezaReceita(eq(3), eq(940))).thenReturn(new NaturezaReceitaVO(67, 3, 940, "AGUAS E REFRIGERANTES, EM LATA"));
        when(provider.tributo().getNaturezaReceita(eq(4), eq(403))).thenReturn(new NaturezaReceitaVO(98, 4, 403, "REFRIGERANTES"));
        when(provider.tributo().getNaturezaReceita(eq(5), eq(409))).thenReturn(new NaturezaReceitaVO(152, 5, 409, "ARTIGOS DE PERFUMARIA (VENDAS A ZFM)"));
        when(provider.tributo().getNaturezaReceita(eq(6), eq(121))).thenReturn(new NaturezaReceitaVO(1, 6, 121, "CARNES (NAT)"));
        when(provider.tributo().getNaturezaReceita(eq(6), eq(999))).thenReturn(new NaturezaReceitaVO(212, 6, 999, "OUTROS PRODUTOS E RECEITAS"));
        when(provider.tributo().getNaturezaReceita(eq(7), eq(999))).thenReturn(new NaturezaReceitaVO(196, 7, 999, "OUTRAS RECEITAS COM ISENCAO"));
        when(provider.tributo().getNaturezaReceita(eq(7), eq(101))).thenReturn(new NaturezaReceitaVO(213, 7, 101, "FORNECIMENTO DE MERCADORIAS OU SERVICOS PARA USO OU CONSUMO DE BORDO EM EMBARCACOES E AERONAVES EM TRAFEGO INTERNACIONAL, QUANDO O PAGAMENTO FOR EFETUADO EM MOEDA CONVERSIVEL (EXCETO QUEROSENE DE AVIACAO)"));
        when(provider.tributo().getNaturezaReceita(eq(8), eq(999))).thenReturn(new NaturezaReceitaVO(232, 8, 999, "OUTRAS RECEITAS SEM INCIDENCIA"));
        when(provider.tributo().getNaturezaReceita(eq(9), eq(999))).thenReturn(new NaturezaReceitaVO(269, 9, 999, "OUTRAS OPERACOES COM SUSPENSAO"));

    }
    
    public static ProdutoIMP getProdutoIMP_MOCA() throws Exception {
        ProdutoIMP imp = new ProdutoIMP();
        
        imp.setImportSistema("TESTE");
        imp.setImportLoja("LOJA 02");
        imp.setImportId("12345");

        imp.setDataCadastro(Utils.convertStringToDate("dd/MM/yyyy", "01/02/2017"));
        imp.setEan("7891000100103");
        imp.setQtdEmbalagem(1);
        imp.setTipoEmbalagem("UN");
        imp.seteBalanca(false);
        imp.setValidade(0);

        imp.setDescricaoCompleta("LEITE CONDENSADO CREMOSO MOCA LATA 395G");
        imp.setDescricaoReduzida("LEIT COND MOCA LT 395");
        imp.setDescricaoGondola("LEITE COND CREMOSO MOCA LT 395G");

        imp.setCodMercadologico1("MERCEARIA");
        imp.setCodMercadologico2("LEITE CONDENSADO");
        imp.setIdFamiliaProduto("MOCA 123");

        imp.setPesoBruto(100);
        imp.setPesoLiquido(100);
        imp.setEstoqueMaximo(1000);
        imp.setEstoqueMinimo(5);
        imp.setEstoque(568);

        imp.setMargem(70);
        imp.setCustoSemImposto(3.65);
        imp.setCustoComImposto(4.02);    
        imp.setPrecovenda(7.12);

        imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
        imp.setNcm("0402.99.00");
        imp.setCest("17.020.00");
        imp.setPautaFiscalId("0402.99.00-B");

        imp.setPiscofinsCstDebito(7);
        imp.setPiscofinsCstCredito(71);
        imp.setPiscofinsNaturezaReceita(101);

        imp.setIcmsCst(20);
        imp.setIcmsAliq(18);
        imp.setIcmsReducao(61.11);
        
        return imp;
    }
    
    public static ProdutoIMP getProdutoIMP_MOCA2() throws Exception {
        ProdutoIMP imp = new ProdutoIMP();
        
        imp.setImportSistema("TESTE");
        imp.setImportLoja("LOJA 02");
        imp.setImportId("12345");

        imp.setDataCadastro(Utils.convertStringToDate("dd/MM/yyyy", "01/02/2017"));
        imp.setEan("7891000100103");
        imp.setQtdEmbalagem(1);
        imp.setTipoEmbalagem("UN");
        imp.seteBalanca(false);
        imp.setValidade(0);

        imp.setDescricaoCompleta("LEITE CONDENSADO CREMOSO MOCA LATA 395G");
        imp.setDescricaoReduzida("LEIT COND MOCA LT 395");
        imp.setDescricaoGondola("LEITE COND CREMOSO MOCA LT 395G");

        imp.setCodMercadologico1("MERCEARIA");
        imp.setCodMercadologico2("LEITE CONDENSADO");
        imp.setIdFamiliaProduto("MOCA 123");

        imp.setPesoBruto(100);
        imp.setPesoLiquido(100);
        imp.setEstoqueMaximo(1000);
        imp.setEstoqueMinimo(5);
        imp.setEstoque(568);

        imp.setMargem(70);
        imp.setCustoSemImposto(3.65);
        imp.setCustoComImposto(4.02);    
        imp.setPrecovenda(7.12);    

        imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
        imp.setNcm("0402.99.00");
        imp.setCest("17.020.00");
        imp.setPautaFiscalId("A");

        imp.setPiscofinsCstDebito(7);
        imp.setPiscofinsCstCredito(71);
        imp.setPiscofinsNaturezaReceita(101);

        imp.setIcmsCstEntrada(10);
        imp.setIcmsAliqEntrada(0);
        imp.setIcmsReducaoEntrada(0);
        
        imp.setIcmsCstEntradaForaEstado(10);
        imp.setIcmsAliqEntradaForaEstado(0);
        imp.setIcmsReducaoEntradaForaEstado(0);
        
        imp.setIcmsCstSaida(20);
        imp.setIcmsAliqSaida(18);
        imp.setIcmsReducaoSaida(61.11);
        
        imp.setIcmsCstSaidaForaEstado(20);
        imp.setIcmsAliqSaidaForaEstado(18);
        imp.setIcmsReducaoSaidaForaEstado(61.11);
        
        imp.setIcmsCstSaidaForaEstadoNF(20);
        imp.setIcmsAliqSaidaForaEstadoNF(18);
        imp.setIcmsReducaoSaidaForaEstadoNF(61.11);
        
        imp.setIcmsCstConsumidor(20);
        imp.setIcmsAliqConsumidor(18);
        imp.setIcmsReducaoConsumidor(61.11);
        
        return imp;
    }
    
    public static ProdutoIMP getProdutoIMP_ACEM() throws Exception {
        ProdutoIMP imp = new ProdutoIMP();
        
        imp.setImportSistema("TESTE");
        imp.setImportLoja("LOJA 02");
        imp.setImportId("3214");

        imp.setDataCadastro(Utils.convertStringToDate("dd/MM/yyyy", "01/02/2017"));
        imp.setEan("18");
        imp.setQtdEmbalagem(1);
        imp.setTipoEmbalagem("KG");
        imp.seteBalanca(true);
        imp.setValidade(10);

        imp.setDescricaoCompleta("ACEM BOVINO KG");
        imp.setDescricaoReduzida("ACEM BOVI KG");
        imp.setDescricaoGondola("ACEM BOVINO KG");

        imp.setCodMercadologico1("ACOUGUE");
        imp.setCodMercadologico2("BOVINO");
        imp.setIdFamiliaProduto(null);

        imp.setPesoBruto(0);
        imp.setPesoLiquido(0);
        imp.setEstoqueMaximo(1000);
        imp.setEstoqueMinimo(5);
        imp.setEstoque(568);

        imp.setMargem(70);
        imp.setCustoSemImposto(13.65);
        imp.setCustoComImposto(14.02);    
        imp.setPrecovenda(17.12);    

        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
        imp.setNcm("0210.20.00");
        imp.setCest("17.083.00");

        imp.setPiscofinsCstDebito(6);
        imp.setPiscofinsCstCredito(73);
        imp.setPiscofinsNaturezaReceita(121);

        imp.setIcmsCst(0);
        imp.setIcmsAliq(18);
        imp.setIcmsReducao(0);
        
        return imp;
    }

    @Test
    public void testConverterEAN_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoAutomacaoVO actual = new ProdutoRepository(provider).converterEAN(imp, 7891000100103L, TipoEmbalagem.UN);
        assertEquals(7891000100103l, actual.getCodigoBarras());
        assertEquals(TipoEmbalagem.UN, actual.getTipoEmbalagem());
        assertEquals(1, actual.getQtdEmbalagem());
        assertFalse(actual.isDun14());
        assertEquals(-1, actual.getId()); //Não é para retornar nada
        assertNull(actual.getProduto());
    }
    
    @Test
    public void testConverterEAN_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoAutomacaoVO actual = new ProdutoRepository(provider).converterEAN(imp, 18, TipoEmbalagem.KG);
        assertEquals(18, actual.getCodigoBarras());
        assertEquals(TipoEmbalagem.KG, actual.getTipoEmbalagem());
        assertEquals(1, actual.getQtdEmbalagem());
        assertFalse(actual.isDun14());
        assertEquals(-1, actual.getId()); //Não é para retornar nada
        assertNull(actual.getProduto());
    }
    
    @Test
    public void testConverterComplemento_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoComplementoVO actual = new ProdutoRepository(provider).converterComplemento(imp);
        assertEquals(0, actual.getId());
        assertEquals(4.02, actual.getCustoComImposto(),0.0001);
        assertEquals(3.65, actual.getCustoSemImposto(),0.0001);
        assertEquals(568, actual.getEstoque(),0.0001);
        assertEquals(1000, actual.getEstoqueMaximo(),0.0001);
        assertEquals(5, actual.getEstoqueMinimo(),0.0001);
        assertEquals(2, actual.getIdLoja());
        assertEquals(7.12, actual.getPrecoVenda(),0.0001);
        assertEquals(7.12, actual.getPrecoDiaSeguinte(),0.0001);
        assertEquals(SituacaoCadastro.ATIVO, actual.getSituacaoCadastro());
        assertNull(actual.getProduto());
    }
    
    @Test
    public void testConverterComplemento_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoComplementoVO actual = new ProdutoRepository(provider).converterComplemento(imp);
        assertEquals(0, actual.getId());
        assertEquals(14.02, actual.getCustoComImposto(),0.0001);
        assertEquals(13.65, actual.getCustoSemImposto(),0.0001);
        assertEquals(568, actual.getEstoque(),0.0001);
        assertEquals(1000, actual.getEstoqueMaximo(),0.0001);
        assertEquals(5, actual.getEstoqueMinimo(),0.0001);
        assertEquals(2, actual.getIdLoja());
        assertEquals(17.12, actual.getPrecoVenda(),0.0001);
        assertEquals(17.12, actual.getPrecoDiaSeguinte(),0.0001);
        assertEquals(SituacaoCadastro.EXCLUIDO, actual.getSituacaoCadastro());
        assertNull(actual.getProduto());
    }
    
    @Test
    public void testConverterAliquota_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoAliquotaVO actual = new ProdutoRepository(provider).converterAliquota(imp);
        
        assertEquals(20, actual.getAliquotaDebito().getCst());
        assertEquals(4, actual.getAliquotaDebito().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaDebito().getDescricao());
        assertEquals(61.11, actual.getAliquotaDebito().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebito().getAliquota(), 0.01);
        
        assertEquals(20, actual.getAliquotaCredito().getCst());
        assertEquals(4, actual.getAliquotaCredito().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaCredito().getDescricao());
        assertEquals(61.11, actual.getAliquotaCredito().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaCredito().getAliquota(), 0.01);
        
        assertEquals(20, actual.getAliquotaCreditoForaEstado().getCst());
        assertEquals(4, actual.getAliquotaCreditoForaEstado().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaCreditoForaEstado().getDescricao());
        assertEquals(61.11, actual.getAliquotaCreditoForaEstado().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaCreditoForaEstado().getAliquota(), 0.01);
        
        assertEquals(20, actual.getAliquotaDebitoForaEstado().getCst());
        assertEquals(4, actual.getAliquotaDebitoForaEstado().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaDebitoForaEstado().getDescricao());
        assertEquals(61.11, actual.getAliquotaDebitoForaEstado().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebitoForaEstado().getAliquota(), 0.01);
        
        assertEquals(20, actual.getAliquotaDebitoForaEstadoNf().getCst());
        assertEquals(4, actual.getAliquotaDebitoForaEstadoNf().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaDebitoForaEstadoNf().getDescricao());
        assertEquals(61.11, actual.getAliquotaDebitoForaEstadoNf().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebitoForaEstadoNf().getAliquota(), 0.01);
        
        assertEquals(20, actual.getAliquotaConsumidor().getCst());
        assertEquals(4, actual.getAliquotaConsumidor().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaConsumidor().getDescricao());
        assertEquals(61.11, actual.getAliquotaConsumidor().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaConsumidor().getAliquota(), 0.01);    
    }
    
    @Test
    public void testConverterAliquota_MOCA2() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA2();
        ProdutoAliquotaVO actual = new ProdutoRepository(provider).converterAliquota(imp);
        
        assertEquals(20, actual.getAliquotaDebito().getCst());
        assertEquals(4, actual.getAliquotaDebito().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaDebito().getDescricao());
        assertEquals(61.11, actual.getAliquotaDebito().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebito().getAliquota(), 0.01);
        
        assertEquals(10, actual.getAliquotaCredito().getCst());
        assertEquals(18, actual.getAliquotaCredito().getId());
        assertEquals("SUBSTITUICAO", actual.getAliquotaCredito().getDescricao());
        assertEquals(0, actual.getAliquotaCredito().getReduzido(), 0.01);
        assertEquals(0, actual.getAliquotaCredito().getAliquota(), 0.01);
        
        assertEquals(10, actual.getAliquotaCreditoForaEstado().getCst());
        assertEquals(18, actual.getAliquotaCreditoForaEstado().getId());
        assertEquals("SUBSTITUICAO", actual.getAliquotaCreditoForaEstado().getDescricao());
        assertEquals(0, actual.getAliquotaCreditoForaEstado().getReduzido(), 0.01);
        assertEquals(0, actual.getAliquotaCreditoForaEstado().getAliquota(), 0.01);
        
        assertEquals(20, actual.getAliquotaDebitoForaEstado().getCst());
        assertEquals(4, actual.getAliquotaDebitoForaEstado().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaDebitoForaEstado().getDescricao());
        assertEquals(61.11, actual.getAliquotaDebitoForaEstado().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebitoForaEstado().getAliquota(), 0.01);
        
        assertEquals(20, actual.getAliquotaDebitoForaEstadoNf().getCst());
        assertEquals(4, actual.getAliquotaDebitoForaEstadoNf().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaDebitoForaEstadoNf().getDescricao());
        assertEquals(61.11, actual.getAliquotaDebitoForaEstadoNf().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebitoForaEstadoNf().getAliquota(), 0.01);
        
        assertEquals(20, actual.getAliquotaConsumidor().getCst());
        assertEquals(4, actual.getAliquotaConsumidor().getId());
        assertEquals("18% RED 61.11", actual.getAliquotaConsumidor().getDescricao());
        assertEquals(61.11, actual.getAliquotaConsumidor().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaConsumidor().getAliquota(), 0.01);    
    }
    
    @Test
    public void testConverterAliquota_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoAliquotaVO actual = new ProdutoRepository(provider).converterAliquota(imp);
        
        assertEquals(0, actual.getAliquotaDebito().getCst());
        assertEquals(2, actual.getAliquotaDebito().getId());
        assertEquals("18%", actual.getAliquotaDebito().getDescricao());
        assertEquals(0, actual.getAliquotaDebito().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebito().getAliquota(), 0.01);
        
        assertEquals(0, actual.getAliquotaCredito().getCst());
        assertEquals(2, actual.getAliquotaCredito().getId());
        assertEquals("18%", actual.getAliquotaCredito().getDescricao());
        assertEquals(0, actual.getAliquotaCredito().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaCredito().getAliquota(), 0.01);
        
        assertEquals(0, actual.getAliquotaCreditoForaEstado().getCst());
        assertEquals(2, actual.getAliquotaCreditoForaEstado().getId());
        assertEquals("18%", actual.getAliquotaCreditoForaEstado().getDescricao());
        assertEquals(0, actual.getAliquotaCreditoForaEstado().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaCreditoForaEstado().getAliquota(), 0.01);
        
        assertEquals(0, actual.getAliquotaDebitoForaEstado().getCst());
        assertEquals(2, actual.getAliquotaDebitoForaEstado().getId());
        assertEquals("18%", actual.getAliquotaDebitoForaEstado().getDescricao());
        assertEquals(0, actual.getAliquotaDebitoForaEstado().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebitoForaEstado().getAliquota(), 0.01);
        
        assertEquals(0, actual.getAliquotaDebitoForaEstadoNf().getCst());
        assertEquals(2, actual.getAliquotaDebitoForaEstadoNf().getId());
        assertEquals("18%", actual.getAliquotaDebitoForaEstadoNf().getDescricao());
        assertEquals(0, actual.getAliquotaDebitoForaEstadoNf().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaDebitoForaEstadoNf().getAliquota(), 0.01);
        
        assertEquals(0, actual.getAliquotaConsumidor().getCst());
        assertEquals(2, actual.getAliquotaConsumidor().getId());
        assertEquals("18%", actual.getAliquotaConsumidor().getDescricao());
        assertEquals(0, actual.getAliquotaConsumidor().getReduzido(), 0.01);
        assertEquals(18, actual.getAliquotaConsumidor().getAliquota(), 0.01);    
    }
    
    @Test
    public void testConverterAnterior_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoAnteriorVO actual = new ProdutoRepository(provider).converterImpEmAnterior(imp);
        
        assertEquals("17.020.00", actual.getCest());
        assertArrayEquals(new String[] { "TESTE", "LOJA 02", "12345" }, actual.getChave());
        assertNull(actual.getCodigoAtual());
        assertEquals(0, actual.getContadorImportacao());
        assertEquals(4.02d, actual.getCustocomimposto(), 0.01);
        assertEquals(3.65d, actual.getCustosemimposto(), 0.01);
        assertEquals("LEITE CONDENSADO CREMOSO MOCA LATA 395G", actual.getDescricao());
        assertEquals(568, actual.getEstoque(), 0.01);
        assertEquals(18d, actual.getIcmsAliq(), 0.1);
        assertEquals(20, actual.getIcmsCst());
        assertEquals(61.11d, actual.getIcmsReducao(), 0.01);
        assertEquals("12345", actual.getImportId());
        assertEquals("LOJA 02", actual.getImportLoja());
        assertEquals("TESTE", actual.getImportSistema());
        assertEquals(70, actual.getMargem(), 0.0);
        assertEquals("0402.99.00", actual.getNcm());
        assertEquals(71, actual.getPisCofinsCredito());
        assertEquals(7, actual.getPisCofinsDebito());
        assertEquals(101, actual.getPisCofinsNaturezaReceita());
        assertEquals(7.12d, actual.getPrecovenda(),0.01);
    }
    
    @Test
    public void testConverterAnterior_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoAnteriorVO actual = new ProdutoRepository(provider).converterImpEmAnterior(imp);
        
        assertEquals("17.083.00", actual.getCest());
        assertArrayEquals(new String[] { "TESTE", "LOJA 02", "3214" }, actual.getChave());
        assertNull(actual.getCodigoAtual());
        assertEquals(0, actual.getContadorImportacao());
        assertEquals(14.02d, actual.getCustocomimposto(), 0.01);
        assertEquals(13.65d, actual.getCustosemimposto(), 0.01);
        assertEquals("ACEM BOVINO KG", actual.getDescricao());
        assertEquals(568, actual.getEstoque(), 0.01);
        assertEquals(18d, actual.getIcmsAliq(), 0.1);
        assertEquals(0, actual.getIcmsCst());
        assertEquals(0, actual.getIcmsReducao(), 0.01);
        assertEquals("3214", actual.getImportId());
        assertEquals("LOJA 02", actual.getImportLoja());
        assertEquals("TESTE", actual.getImportSistema());
        assertEquals(70, actual.getMargem(), 0.0);
        assertEquals("0210.20.00", actual.getNcm());
        assertEquals(73, actual.getPisCofinsCredito());
        assertEquals(6, actual.getPisCofinsDebito());
        assertEquals(121, actual.getPisCofinsNaturezaReceita());
        assertEquals(17.12d, actual.getPrecovenda(),0.01);
    }
    
    @Test
    public void testConverterEANAnterior_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoAnteriorEanVO actual = new ProdutoRepository(provider).converterAnteriorEAN(imp);
        
        assertEquals("7891000100103", actual.getEan());
        assertEquals("12345", actual.getImportId());
        assertEquals("LOJA 02", actual.getImportLoja());
        assertEquals("TESTE", actual.getImportSistema());
        assertEquals(1, actual.getQtdEmbalagem());
        assertEquals("UN", actual.getTipoEmbalagem());
        assertEquals(0, actual.getValor(), 0.01);
    }
    
    @Test
    public void testConverterEANAnterior_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoAnteriorEanVO actual = new ProdutoRepository(provider).converterAnteriorEAN(imp);
        
        assertEquals("18", actual.getEan());
        assertEquals("3214", actual.getImportId());
        assertEquals("LOJA 02", actual.getImportLoja());
        assertEquals("TESTE", actual.getImportSistema());
        assertEquals(1, actual.getQtdEmbalagem());
        assertEquals("KG", actual.getTipoEmbalagem());
        assertEquals(0, actual.getValor(), 0.01);
    }
    
    @Test
    public void testConverterIMP_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoVO actual = new ProdutoRepository(provider).converterIMP(imp, 12345, TipoEmbalagem.UN, false);
        
        assertEquals(17, actual.getCest().getCest1());
        assertEquals(20, actual.getCest().getCest2());
        assertEquals(0, actual.getCest().getCest3());
        assertEquals("LEITE CONDENSADO", actual.getCest().getDescricao());
        assertEquals(Utils.convertStringToDate("dd/MM/yyyy", "01/02/2017"), actual.getDatacadastro());
        assertEquals("LEITE CONDENSADO CREMOSO MOCA LATA 395G", actual.getDescricaoCompleta());
        assertEquals("LEITE COND CREMOSO MOCA LT 395G", actual.getDescricaoGondola());
        assertEquals("LEIT COND MOCA LT 395", actual.getDescricaoReduzida());
        assertEquals(123, actual.getFamiliaProduto().getId());
        assertEquals("FAMILIA MOCA", actual.getFamiliaProduto().getDescricao());
        assertEquals(12345, actual.getId());
        assertEquals(70, actual.getMargem(), 0.01);
        assertEquals(1, actual.getMercadologico().getMercadologico1());
        assertEquals(2, actual.getMercadologico().getMercadologico2());
        assertEquals(1, actual.getMercadologico().getMercadologico3());
        assertEquals(0, actual.getMercadologico().getMercadologico4());
        assertEquals(0, actual.getMercadologico().getMercadologico5());
        assertEquals("LEITE CONDENSADO", actual.getMercadologico().getDescricao());
        assertEquals(402, actual.getNcm().getNcm1());
        assertEquals(99, actual.getNcm().getNcm2());
        assertEquals(0, actual.getNcm().getNcm3());
        assertEquals(2, actual.getExcecao());
        assertEquals("LEITE CONDENSADO(NCM)", actual.getNcm().getDescricao());
        assertEquals(NormaReposicao.CAIXA, actual.getNormaCompra());
        assertEquals(NormaReposicao.CAIXA, actual.getNormaReposicao());
        assertEquals(100, actual.getPesoBruto(), 0.001);
        assertEquals(100, actual.getPesoLiquido(), 0.001);
        assertEquals(7, actual.getPisCofinsDebito().getCst());
        assertEquals(71, actual.getPisCofinsCredito().getCst());
        assertEquals(101, actual.getPisCofinsNaturezaReceita().getCodigo());
        assertEquals(TipoEmbalagem.UN, actual.getTipoEmbalagem());
        assertEquals(0, actual.getValidade());
    }
    
    @Test
    public void testConvertPisCofins_VALIDO() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(1);
        imp.setPiscofinsCstCredito(50);
        imp.setPiscofinsNaturezaReceita(0);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(1, actual.getPisCofinsDebito().getCst());
        assertEquals(50, actual.getPisCofinsCredito().getCst());
        assertNull(actual.getPisCofinsNaturezaReceita());
        
    }
    
    @Test
    public void testConvertPisCofins_INVALIDO() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(-1);
        imp.setPiscofinsCstCredito(-1);
        imp.setPiscofinsNaturezaReceita(0);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(7, actual.getPisCofinsDebito().getCst());
        assertEquals(71, actual.getPisCofinsCredito().getCst());
        assertEquals(999, actual.getPisCofinsNaturezaReceita().getCodigo());
        
    }
    
    @Test
    public void testConvertPisCofins_INVALIDO_2() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(99);
        imp.setPiscofinsCstCredito(99);
        imp.setPiscofinsNaturezaReceita(0);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(49, actual.getPisCofinsDebito().getCst());
        assertEquals(99, actual.getPisCofinsCredito().getCst());
        
    }
    
    @Test
    public void testConvertPisCofins_INVALIDO_3() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(49);
        imp.setPiscofinsCstCredito(49);
        imp.setPiscofinsNaturezaReceita(0);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(49, actual.getPisCofinsDebito().getCst());
        assertEquals(99, actual.getPisCofinsCredito().getCst());
        
    }
    
    private void testNaturezaReceita(ProdutoRepository repository, int cstDebito, int naturezaReceita, int expected) throws Exception {
        
        NaturezaReceitaVO nat = repository.getNaturezaReceita(cstDebito, naturezaReceita);        
        assertEquals(expected, nat.getCodigo());
        
    }
    
    @Test
    public void testGetNaturezaReceita() throws Exception {
        ProdutoRepository repository = new ProdutoRepository(provider);
        
        assertNull(repository.getNaturezaReceita(1, 2));
        testNaturezaReceita(repository, 2, 403, 403);
        testNaturezaReceita(repository, 2, 15, 403);
        testNaturezaReceita(repository, 3, 940, 940);
        testNaturezaReceita(repository, 3, 0, 940);
        testNaturezaReceita(repository, 4, 403, 403);
        testNaturezaReceita(repository, 4, 48, 403);
        testNaturezaReceita(repository, 5, 409, 409);
        testNaturezaReceita(repository, 5, 789, 409);
        testNaturezaReceita(repository, 6, 999, 999);
        testNaturezaReceita(repository, 6, 121, 121);
        testNaturezaReceita(repository, 6, 10, 999);
        testNaturezaReceita(repository, 7, 999, 999);
        testNaturezaReceita(repository, 7, 156, 999);
        testNaturezaReceita(repository, 8, 999, 999);
        testNaturezaReceita(repository, 8, 741, 999);
        testNaturezaReceita(repository, 9, 999, 999);
        testNaturezaReceita(repository, 9, 359, 999);
        
    }
    
    @Test
    public void testConvertPisCofins_VALIDO_C_NATUREZA_RECEITA() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(4);
        imp.setPiscofinsCstCredito(70);
        imp.setPiscofinsNaturezaReceita(403);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(4, actual.getPisCofinsDebito().getCst());
        assertEquals(70, actual.getPisCofinsCredito().getCst());
        assertEquals(403, actual.getPisCofinsNaturezaReceita().getCodigo());
        
    }
    
    @Test
    public void testConverterIMP_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoVO actual = new ProdutoRepository(provider).converterIMP(imp, 18, TipoEmbalagem.KG, true);
        
        assertEquals(17, actual.getCest().getCest1());
        assertEquals(83, actual.getCest().getCest2());
        assertEquals(0, actual.getCest().getCest3());
        assertEquals("CARNES BOVINAS", actual.getCest().getDescricao());
        assertEquals(Utils.convertStringToDate("dd/MM/yyyy", "01/02/2017"), actual.getDatacadastro());
        assertEquals("ACEM BOVINO KG", actual.getDescricaoCompleta());
        assertEquals("ACEM BOVINO KG", actual.getDescricaoGondola());
        assertEquals("ACEM BOVI KG", actual.getDescricaoReduzida());
        assertNull(actual.getFamiliaProduto());
        assertEquals(18, actual.getId());
        assertEquals(70, actual.getMargem(), 0.01);
        assertEquals(2, actual.getMercadologico().getMercadologico1());
        assertEquals(4, actual.getMercadologico().getMercadologico2());
        assertEquals(1, actual.getMercadologico().getMercadologico3());
        assertEquals(0, actual.getMercadologico().getMercadologico4());
        assertEquals(0, actual.getMercadologico().getMercadologico5());
        assertEquals("BOVINA", actual.getMercadologico().getDescricao());
        assertEquals(210, actual.getNcm().getNcm1());
        assertEquals(20, actual.getNcm().getNcm2());
        assertEquals(0, actual.getNcm().getNcm3());
        assertEquals("CORTE BOVINO(NCM)", actual.getNcm().getDescricao());
        assertEquals(NormaReposicao.CAIXA, actual.getNormaCompra());
        assertEquals(NormaReposicao.CAIXA, actual.getNormaReposicao());
        assertEquals(0, actual.getPesoBruto(), 0.001);
        assertEquals(0, actual.getPesoLiquido(), 0.001);
        assertEquals(6, actual.getPisCofinsDebito().getCst());
        assertEquals(73, actual.getPisCofinsCredito().getCst());
        assertEquals(121, actual.getPisCofinsNaturezaReceita().getCodigo());
        assertEquals(TipoEmbalagem.KG, actual.getTipoEmbalagem());
        assertEquals(10, actual.getValidade());
    }
    
    @Test
    public void testSetUpVariaveis_MOCA() throws Exception {
        long ean;
        String strID;
        boolean eBalanca;
        TipoEmbalagem unidade;

        {
            ProdutoRepository.SetUpVariaveisTO to = new ProdutoRepository(provider).setUpVariaveis(getProdutoIMP_MOCA());
            ean = to.ean;
            strID = to.strID;
            eBalanca = to.eBalanca;
            unidade = to.unidade;
        }
        
        assertEquals(7891000100103L, ean);
        assertEquals("12345", strID);
        assertFalse(eBalanca);
        assertEquals(TipoEmbalagem.UN, unidade);
    }
    
    @Test
    public void testSetUpVariaveis_ACEM() throws Exception {
        long ean;
        String strID;
        boolean eBalanca;
        TipoEmbalagem unidade;
        
        {
            ProdutoRepository.SetUpVariaveisTO to = new ProdutoRepository(provider).setUpVariaveis(getProdutoIMP_ACEM());
            ean = to.ean;
            strID = to.strID;
            eBalanca = to.eBalanca;
            unidade = to.unidade;
        }
        
        assertEquals(18L, ean);
        assertEquals("3214", strID);
        assertTrue(eBalanca);
        assertEquals(TipoEmbalagem.KG, unidade);
    }
    
    @Test
    public void testSalvar() throws Exception {
        final ArrayList<String> evts = new ArrayList<>();
        ProdutoRepository rep = new ProdutoRepository(provider) {      

            @Override
            public void notificar() throws Exception {
                super.notificar();
            }
            
        };
        
        ArrayList<ProdutoIMP> lista = new ArrayList<>();
        ProdutoIMP imp = getProdutoIMP_MOCA();
        imp.setEan("0001517");
        imp.setManterEAN(true);
        lista.add(imp);
        lista.add(getProdutoIMP_MOCA2());
        lista.add(getProdutoIMP_ACEM());
        
        rep.salvar(lista);
    }
    
    @Test
    public void testUnificar2() throws Exception {
        
    }
    
    @Test
    public void testResetarIdsSomenteBalanca() throws Exception {
        ProdutoRepository rep = new ProdutoRepository(provider);
        HashSet<OpcaoProduto> hash = new HashSet<>();
        hash.add(OpcaoProduto.IMPORTAR_RESETAR_BALANCA);
        when(provider.getOpcoes()).thenReturn(hash);
        String[] ids = new String[]{"265", "123569", "789654", "2"};
        boolean[] bal = new boolean[]{true, true, false, false};
        
        assertEquals("-1",rep.resetarIds(ids[0], bal[0]));
        assertEquals("-1",rep.resetarIds(ids[1], bal[1]));
        assertEquals("789654",rep.resetarIds(ids[2], bal[2]));
        assertEquals("2",rep.resetarIds(ids[3], bal[3]));
    }
    
    @Test
    public void testResetarIdsSomenteNormais() throws Exception {
        ProdutoRepository rep = new ProdutoRepository(provider);
        HashSet<OpcaoProduto> hash = new HashSet<>();
        hash.add(OpcaoProduto.IMPORTAR_RESETAR_NORMAIS);
        when(provider.getOpcoes()).thenReturn(hash);
        String[] ids = new String[]{"265", "123569", "789654", "2"};
        boolean[] bal = new boolean[]{true, true, false, false};
        
        assertEquals("265",rep.resetarIds(ids[0], bal[0]));
        assertEquals("123569",rep.resetarIds(ids[1], bal[1]));
        assertEquals("-1",rep.resetarIds(ids[2], bal[2]));
        assertEquals("-1",rep.resetarIds(ids[3], bal[3]));
    }
  
    @Test
    public void testResetarIdsNormaisEBalanca() throws Exception {
        ProdutoRepository rep = new ProdutoRepository(provider);
        HashSet<OpcaoProduto> hash = new HashSet<>();
        hash.add(OpcaoProduto.IMPORTAR_RESETAR_NORMAIS);
        hash.add(OpcaoProduto.IMPORTAR_RESETAR_BALANCA);
        when(provider.getOpcoes()).thenReturn(hash);
        String[] ids = new String[]{"265", "123569", "789654", "2"};
        boolean[] bal = new boolean[]{true, true, false, false};
        
        assertEquals("-1",rep.resetarIds(ids[0], bal[0]));
        assertEquals("-1",rep.resetarIds(ids[1], bal[1]));
        assertEquals("-1",rep.resetarIds(ids[2], bal[2]));
        assertEquals("-1",rep.resetarIds(ids[3], bal[3]));
    }
    
    @Test
    public void testResetarIdsNenhum() throws Exception {
        ProdutoRepository rep = new ProdutoRepository(provider);
        String[] ids = new String[]{"265", "123569", "789654", "2"};
        boolean[] bal = new boolean[]{true, true, false, false};
        
        assertEquals("265",rep.resetarIds(ids[0], bal[0]));
        assertEquals("123569",rep.resetarIds(ids[1], bal[1]));
        assertEquals("789654",rep.resetarIds(ids[2], bal[2]));
        assertEquals("2",rep.resetarIds(ids[3], bal[3]));
    }
    
    @Test
    public void filtrarSomenteAtivos() throws Exception {
        ProdutoRepository rep = new ProdutoRepository(provider);
        
        List<ProdutoIMP> imps = new ArrayList<>();
        {
            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportId("1");
            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
            imps.add(imp);
        }
        {
            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportId("1");
            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
            imps.add(imp);
        }
        {
            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportId("3");
            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
            imps.add(imp);
        }
        {
            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportId("5");
            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
            imps.add(imp);
        }
        {
            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportId("2");
            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
            imps.add(imp);
        }
        {
            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportId("4");
            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
            imps.add(imp);
        }   
        {
            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportId("6");
            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
            imps.add(imp);
        }     
        
        imps = rep.filtrarProdutosInativos(imps);
        
        assertEquals(4, imps.size());
        assertEquals("1", imps.get(0).getImportId());
        assertEquals(SituacaoCadastro.ATIVO, imps.get(0).getSituacaoCadastro());
        assertEquals("3", imps.get(1).getImportId());
        assertEquals(SituacaoCadastro.ATIVO, imps.get(1).getSituacaoCadastro());
        assertEquals("2", imps.get(2).getImportId());
        assertEquals(SituacaoCadastro.ATIVO, imps.get(2).getSituacaoCadastro());
        assertEquals("4", imps.get(3).getImportId());
        assertEquals(SituacaoCadastro.ATIVO, imps.get(3).getSituacaoCadastro());
        
    }
    
}
