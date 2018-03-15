package vrimplantacao2.dao.cadastro.financeiro.diversos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.enums.TipoPagamento;

/**
 * Controla a tabela tipopagamento.
 * @author Leandro
 */
public class TipoPagamentoDAO {
    
    public List<TipoPagamento> all() throws Exception {
        List<TipoPagamento> result = new ArrayList<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descricao from tipopagamento order by 2"
            )) {
                while (rst.next()) {
                    result.add(new TipoPagamento(rst.getInt("id"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    public TipoPagamento getById(int id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descricao from tipopagamento where id = " + id
            )) {
                if (rst.next()) {
                    return new TipoPagamento(rst.getInt("id"), rst.getString("descricao"));
                }
            }
        }
        return null;
    }
    
}
