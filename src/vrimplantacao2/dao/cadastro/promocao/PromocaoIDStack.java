package vrimplantacao2.dao.cadastro.promocao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.collection.IDStack;

/**
 * Classe criada para gerenciar os IDs.
 * @author Leandro
 */
public class PromocaoIDStack {
    
    private IDStack stack;
    private Set<Integer> idsExistentes;
    private final int iniciarEm;
    
    public PromocaoIDStack(int iniciarEm) {
        this.iniciarEm = iniciarEm;
    }    
    
    public Set<Integer> obterIdsExistentes() throws Exception {
        Set<Integer> result = new HashSet<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from promocao"
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
                    "(SELECT id FROM generate_series(" + iniciarEm + ", 999999)\n" +
                    "AS s(id) EXCEPT SELECT id FROM promocao) AS codigointerno"
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
            if ((id > 999999) || (id < 1)) {
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