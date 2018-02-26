package vrimplantacao2.dao.cadastro.cliente;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import vrimplantacao2.utils.collection.IDStack;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class ClientePreferencialIDStackTest {

    public ClientePreferencialIDStackTest() {
    }

    @Test
    public void testObterID() throws Exception {
        ClientePreferencialIDStack stack = new ClientePreferencialIDStack(1) {
            @Override
            public Set<Integer> obterIdsExistentes() throws Exception {
                return new LinkedHashSet<>(Arrays.asList(1, 4, 6, 8, 9));
            }
            @Override
            public IDStack obterIdsLivres() throws Exception {
                IDStack idStack = new IDStack(2, 3, 5, 7, 10);
                for (int i = 11; i <= 999999; i++) {
                    idStack.add(i);
                }
                return idStack;
            }
        };
        
        assertEquals(10, stack.obterID("10"));
        assertEquals(3, stack.obterID("3"));
        assertEquals(14, stack.obterID("14"));
        assertEquals(2, stack.obterID("123456789"));
        assertEquals(5, stack.obterID("4"));
        assertEquals(7, stack.obterID("7"));
        
    }

}
