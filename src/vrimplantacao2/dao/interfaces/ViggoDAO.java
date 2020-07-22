package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author leandro
 */
public class ViggoDAO extends InterfaceDAO {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "VIGGO" + (!complemento.equals("") ? " - " + complemento : "");
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select codigo, nome, cnpj from empresa order by 1"
                )
        ) {
            while (rs.next()) {
                result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("nome") + " - " + rs.getString("cnpj")));
            }
        }
        
        return result;
    }
    
}
