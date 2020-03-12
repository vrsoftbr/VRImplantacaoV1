package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author leandro
 */
public class SnSistemaDAO extends InterfaceDAO {

    
    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {        
        return "SN Sistema" + (
                "".equals(complemento) ?
                "" :
                " - " + complemento
        );
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	fantasia\n" +
                    "from\n" +
                    "	empresa \n" +
                    "order by\n" +
                    "	codigo"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(
                            rs.getString("codigo"),
                            rs.getString("fantasia")
                    ));
                }
            }
        }
        
        return result;
    }
    
}
