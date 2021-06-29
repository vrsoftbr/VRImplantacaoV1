/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.service.atualizador;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.atualizador.AtualizadorDAO;
import vrimplantacao2_5.vo.enums.EBancoDados;

/**
 *
 * @author Desenvolvimento
 */
public class AtualizadorServiceTest {
    
    @Test
    public void testVerificarBancoDados() throws Exception {
        AtualizadorDAO atulizadorDAO = mock(AtualizadorDAO.class);        
        AtualizadorService atualizadorService = new AtualizadorService(atulizadorDAO);
        
        List<EBancoDados> bancoDados = new ArrayList<>();
        
        for (EBancoDados eBancoDados : EBancoDados.values()) {
            if (!atulizadorDAO.verificarBancoDados(eBancoDados)) {
                bancoDados.add(eBancoDados);
                when(!atulizadorDAO.verificarBancoDados(eBancoDados)).thenReturn(false);
            }
        }
        
        assertEquals(bancoDados.size(), atualizadorService.verificarBancoDados().size());        
    }
}
