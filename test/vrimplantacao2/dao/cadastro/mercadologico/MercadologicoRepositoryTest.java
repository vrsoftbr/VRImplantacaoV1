/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.mercadologico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoAnteriorVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class MercadologicoRepositoryTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MercadologicoRepositoryProvider provider;
    
    public MercadologicoRepositoryTest() {
    }

    public static MercadologicoNivelIMP getMerc1() {        
        MercadologicoNivelIMP m = new MercadologicoNivelIMP(
                "1",
                "M1"
        );
        
        MercadologicoNivelIMP m1 = m.addFilho("1", "M1M1");
        m1.addFilho("1", "M1M1M1");
        m1.addFilho("2", "M1M1M2");
        m1.addFilho("3", "M1M1M3");        
        
        return m;
    }
    
    public static MercadologicoNivelIMP getMerc2() {        
        MercadologicoNivelIMP m = new MercadologicoNivelIMP(
                "M2",
                "M2"
        );        
        
        m.addFilho("M2M1", "M2M1").addFilho("M2M1M1", "M2M1M1").addFilho("M2M1M1M1", "M2M1M1M1");
        
        return m;
    }
    
    @Before
    public void settingUp() throws Exception {
        when(provider.getSistema()).thenReturn("TESTE");
        when(provider.getLojaOrigem()).thenReturn("LJ1");
        when(provider.getNextMercadologico1()).thenReturn(1, 2, 3);
        
        
        when(provider.getNextMercadologico2(1)).thenReturn(1);
        when(provider.getNextMercadologico3(1, 1)).thenReturn(1, 2, 3);
        when(provider.getNextMercadologico4(1, 1, 1)).thenReturn(1);
        when(provider.getNextMercadologico4(1, 1, 2)).thenReturn(1);
        when(provider.getNextMercadologico4(1, 1, 3)).thenReturn(1);
        
        when(provider.getNextMercadologico2(2)).thenReturn(1);     
        when(provider.getNextMercadologico3(2, 1)).thenReturn(1);
        when(provider.getNextMercadologico4(2, 1, 1)).thenReturn(1);
        
        when(provider.getNextMercadologico2(3)).thenReturn(1);
        when(provider.getNextMercadologico3(3, 1)).thenReturn(1); 
        when(provider.getNextMercadologico4(3, 1, 1)).thenReturn(1);
    }
    
    @Test
    public void testSalvar() throws Exception {
        
        final List<MercadologicoVO> mercsGravados = new ArrayList<>();
        final List<MercadologicoAnteriorVO> mercsAntGravados = new ArrayList<>();
        
        List<MercadologicoNivelIMP> mercs = Arrays.asList(
                getMerc1(),
                getMerc2(), 
                new MercadologicoNivelIMP(
                        "M3",
                        "M3"
                )
        );
        
        MercadologicoRepository repository = new MercadologicoRepository(provider) {
            
            @Override
            public void gravarMercadologico(MercadologicoVO vo) throws Exception {
                mercsGravados.add(vo);
            }
            
            @Override
            public void gravarMercadologico(MercadologicoAnteriorVO vo) throws Exception {
                mercsAntGravados.add(vo);
            }
            
        };
        repository.salvar(mercs, new HashSet<OpcaoProduto>());
        
        assertEquals(16, mercsGravados.size());
        MercadologicoVO m = mercsGravados.get(0);
        assertEquals(1, m.getMercadologico1());
        assertEquals(0, m.getMercadologico2());
        assertEquals(0, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(1, m.getNivel());
        assertEquals("M1", m.getDescricao());
        
        m = mercsGravados.get(1);        
        assertEquals(1, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(0, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(2, m.getNivel());
        assertEquals("M1M1", m.getDescricao());
        
        m = mercsGravados.get(2);        
        assertEquals(1, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(1, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(3, m.getNivel());
        assertEquals("M1M1M1", m.getDescricao());        
        m = mercsGravados.get(3);        
        assertEquals(1, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(1, m.getMercadologico3());
        assertEquals(1, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(4, m.getNivel());
        assertEquals("M1M1M1", m.getDescricao());
        
        m = mercsGravados.get(4);        
        assertEquals(1, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(2, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(3, m.getNivel());
        assertEquals("M1M1M2", m.getDescricao());
        m = mercsGravados.get(5);        
        assertEquals(1, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(2, m.getMercadologico3());
        assertEquals(1, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(4, m.getNivel());
        assertEquals("M1M1M2", m.getDescricao());
        
        m = mercsGravados.get(6);        
        assertEquals(1, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(3, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(3, m.getNivel());
        assertEquals("M1M1M3", m.getDescricao());
        m = mercsGravados.get(7);        
        assertEquals(1, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(3, m.getMercadologico3());
        assertEquals(1, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(4, m.getNivel());
        assertEquals("M1M1M3", m.getDescricao());
        
        
        
        
        
        
        
        
        m = mercsGravados.get(8);                
        assertEquals(2, m.getMercadologico1());
        assertEquals(0, m.getMercadologico2());
        assertEquals(0, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(1, m.getNivel());
        assertEquals("M2", m.getDescricao());
        
        m = mercsGravados.get(9);        
        assertEquals(2, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(0, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(2, m.getNivel());
        assertEquals("M2M1", m.getDescricao());
        
        m = mercsGravados.get(10);        
        assertEquals(2, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(1, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(3, m.getNivel());
        assertEquals("M2M1M1", m.getDescricao());
        
        m = mercsGravados.get(11);        
        assertEquals(2, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(1, m.getMercadologico3());
        assertEquals(1, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(4, m.getNivel());
        assertEquals("M2M1M1M1", m.getDescricao());
        
        m = mercsGravados.get(12);        
        assertEquals(3, m.getMercadologico1());
        assertEquals(0, m.getMercadologico2());
        assertEquals(0, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(1, m.getNivel());
        assertEquals("M3", m.getDescricao());
        
        m = mercsGravados.get(13);        
        assertEquals(3, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(0, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(2, m.getNivel());
        assertEquals("M3", m.getDescricao());
        
        m = mercsGravados.get(14);        
        assertEquals(3, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(1, m.getMercadologico3());
        assertEquals(0, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(3, m.getNivel());
        assertEquals("M3", m.getDescricao());
        
        m = mercsGravados.get(15);        
        assertEquals(3, m.getMercadologico1());
        assertEquals(1, m.getMercadologico2());
        assertEquals(1, m.getMercadologico3());
        assertEquals(1, m.getMercadologico4());
        assertEquals(0, m.getMercadologico5());
        assertEquals(4, m.getNivel());
        assertEquals("M3", m.getDescricao());
        
        assertEquals(10, mercsAntGravados.size());
        MercadologicoAnteriorVO mercAnt;
        
        mercAnt = mercsAntGravados.get(0);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("1", mercAnt.getAntMerc1());
        assertEquals("", mercAnt.getAntMerc2());
        assertEquals("", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(1, mercAnt.getMerc1());
        assertEquals(0, mercAnt.getMerc2());
        assertEquals(0, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(1, mercAnt.getNivel());
        assertEquals("M1", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(1);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("1", mercAnt.getAntMerc1());
        assertEquals("1", mercAnt.getAntMerc2());
        assertEquals("", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(1, mercAnt.getMerc1());
        assertEquals(1, mercAnt.getMerc2());
        assertEquals(0, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(2, mercAnt.getNivel());
        assertEquals("M1M1", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(2);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("1", mercAnt.getAntMerc1());
        assertEquals("1", mercAnt.getAntMerc2());
        assertEquals("1", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(1, mercAnt.getMerc1());
        assertEquals(1, mercAnt.getMerc2());
        assertEquals(1, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(3, mercAnt.getNivel());
        assertEquals("M1M1M1", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(3);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("1", mercAnt.getAntMerc1());
        assertEquals("1", mercAnt.getAntMerc2());
        assertEquals("2", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(1, mercAnt.getMerc1());
        assertEquals(1, mercAnt.getMerc2());
        assertEquals(2, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(3, mercAnt.getNivel());
        assertEquals("M1M1M2", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(4);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("1", mercAnt.getAntMerc1());
        assertEquals("1", mercAnt.getAntMerc2());
        assertEquals("3", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(1, mercAnt.getMerc1());
        assertEquals(1, mercAnt.getMerc2());
        assertEquals(3, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(3, mercAnt.getNivel());
        assertEquals("M1M1M3", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(5);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("M2", mercAnt.getAntMerc1());
        assertEquals("", mercAnt.getAntMerc2());
        assertEquals("", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(2, mercAnt.getMerc1());
        assertEquals(0, mercAnt.getMerc2());
        assertEquals(0, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(1, mercAnt.getNivel());
        assertEquals("M2", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(6);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("M2", mercAnt.getAntMerc1());
        assertEquals("M2M1", mercAnt.getAntMerc2());
        assertEquals("", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(2, mercAnt.getMerc1());
        assertEquals(1, mercAnt.getMerc2());
        assertEquals(0, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(2, mercAnt.getNivel());
        assertEquals("M2M1", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(7);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("M2", mercAnt.getAntMerc1());
        assertEquals("M2M1", mercAnt.getAntMerc2());
        assertEquals("M2M1M1", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(2, mercAnt.getMerc1());
        assertEquals(1, mercAnt.getMerc2());
        assertEquals(1, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(3, mercAnt.getNivel());
        assertEquals("M2M1M1", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(8);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("M2", mercAnt.getAntMerc1());
        assertEquals("M2M1", mercAnt.getAntMerc2());
        assertEquals("M2M1M1", mercAnt.getAntMerc3());
        assertEquals("M2M1M1M1", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(2, mercAnt.getMerc1());
        assertEquals(1, mercAnt.getMerc2());
        assertEquals(1, mercAnt.getMerc3());
        assertEquals(1, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(4, mercAnt.getNivel());
        assertEquals("M2M1M1M1", mercAnt.getDescricao());
        
        mercAnt = mercsAntGravados.get(9);
        assertEquals("TESTE", mercAnt.getSistema());
        assertEquals("LJ1", mercAnt.getLoja());
        assertEquals("M3", mercAnt.getAntMerc1());
        assertEquals("", mercAnt.getAntMerc2());
        assertEquals("", mercAnt.getAntMerc3());
        assertEquals("", mercAnt.getAntMerc4());
        assertEquals("", mercAnt.getAntMerc5());
        assertEquals(3, mercAnt.getMerc1());
        assertEquals(0, mercAnt.getMerc2());
        assertEquals(0, mercAnt.getMerc3());
        assertEquals(0, mercAnt.getMerc4());
        assertEquals(0, mercAnt.getMerc5());
        assertEquals(1, mercAnt.getNivel());
        assertEquals("M3", mercAnt.getDescricao());
        
        
    }
    
    @Test
    public void testGetNivelMaximo() throws Exception {
        assertEquals(4, new MercadologicoRepository(provider).getNivelMaximo(Arrays.asList(
                getMerc1(),
                getMerc2(), 
                new MercadologicoNivelIMP(
                        "M3",
                        "M3"
                )                
        )));
    }
    
}
