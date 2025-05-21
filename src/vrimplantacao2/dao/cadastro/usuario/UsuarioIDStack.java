package vrimplantacao2.dao.cadastro.usuario;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.collection.IDStack;

/**
 *
 * @author Leandro
 */
public class UsuarioIDStack {

    private IDStack stack;
    private Set<Integer> idsExistentes;
    
    public Set<Integer> obterIdsExistentes() throws Exception {
        Set<Integer> result = new HashSet<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from usuario"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        
        return result;
    }
    
    public IDStack obterIdsLivres() throws Exception {
        IDStack result = new IDStack();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from\n" +
                    "(SELECT id FROM generate_series(1, 9999)\n" +
                    "AS s(id) EXCEPT SELECT id FROM usuario) AS codigointerno"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        
        return result;
    }

    public int obterID(String strId) throws Exception {
        
        if (stack == null) {
            stack = obterIdsLivres();
        }
        
        if (idsExistentes == null) {
            idsExistentes = obterIdsExistentes();
        }
        
        boolean gerarID = false;
        long id = -1;
        try {
            id = Long.parseLong(strId);
            if (id > 9999 || id <= 0) {
                gerarID = true;
            } else if (idsExistentes.contains((int) id)) {
                gerarID = true;
            }
        } catch (NumberFormatException e) {
            gerarID = true;
        }
        
        if (gerarID) {
            id = stack.pop();
        } else {
            stack.remove(id);
        }
        
        idsExistentes.add((int) id);
        
        return (int) id;
        
    }
    
}
