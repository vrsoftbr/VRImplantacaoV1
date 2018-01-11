/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.venda;

import vrimplantacao2.dao.cadastro.venda.VendaRepositoryProvider;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class VendaRepositoryTest {
    
    @Mock
    public VendaRepositoryProvider provider;
    
    @Before
    public void init() throws Exception {
        when(provider.getSistema()).thenReturn("TESTE");
        when(provider.getLoja()).thenReturn("1");
        when(provider.getLojaVR()).thenReturn(1);
    }

    @Test
    public void testImportar() throws Exception {
        fail();
    }
    
}
