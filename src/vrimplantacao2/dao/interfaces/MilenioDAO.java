package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 * DAO de importação do Milênio.
 * @author leandro
 */
public class MilenioDAO extends InterfaceDAO {
    
    private String complemento = "";
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        if ("".equals(this.complemento)) {
            return "Milenio";
        } else {
            return "Milenio - " + this.complemento;
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(
                            rs.getString(""),
                            rs.getString("")
                    ));
                }
            }
        }
        
        return result;
    }
    
}
