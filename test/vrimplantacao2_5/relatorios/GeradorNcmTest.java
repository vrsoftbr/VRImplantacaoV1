/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.relatorios;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import vrimplantacao2_5.relatorios.gerador.GeradorNcm;
import vrimplantacao2_5.relatorios.relatoriosDAO.RelatorioNcmFaltandoDAO;
import vrimplantacao2_5.relatorios.vo.NCMFaltandoVO;

/**
 *
 * @author Desenvolvimento
 */
public class GeradorNcmTest {

    private GeradorNcm geraNcm = new GeradorNcm();
    
    @Mock
    RelatorioNcmFaltandoDAO mock = new RelatorioNcmFaltandoDAO();

    @Test
    public void deveriaCarregarDadosNcm() throws Exception {
        RelatorioNcmFaltandoDAO mock = Mockito.mock(RelatorioNcmFaltandoDAO.class);
        List<NCMFaltandoVO> ncmMockado = mock.getNcmFaltando();
        Assert.assertTrue(ncmMockado.isEmpty());
    }

    @Test
    public void deveriaGerarNcmTxtTest() throws Exception {
        List<NCMFaltandoVO> listaTeste = getNcmFaltandoListaTest();
        GeradorNcm geraNcmMockado = Mockito.mock(GeradorNcm.class);
        MockitoAnnotations.openMocks(this);
        Mockito.when(mock.getNcmFaltando()).thenReturn(listaTeste);
        Mockito.when(geraNcmMockado.carregarDadosNcm()).thenReturn(listaTeste);
        geraNcm.gerarNcmTxt();
        NCMFaltandoVO vo = listaTeste.get(0);
        String espacos = "----------------";
        Assert.assertEquals("147258          ", vo.getNcm().trim() + espacos.substring(vo.getNcm().trim().length()).replace("-", " "));
    }

    public List<NCMFaltandoVO> getNcmFaltandoListaTest() throws Exception {
        List<NCMFaltandoVO> dadosNcm = new ArrayList<>();
        NCMFaltandoVO vo = new NCMFaltandoVO();
        vo.setNcm("147258 ");
        vo.setQtd("2");
        dadosNcm.add(vo);
        return dadosNcm;
    }
}
