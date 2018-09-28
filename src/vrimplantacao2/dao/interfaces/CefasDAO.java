package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Importacao
 */
public class CefasDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "CEFAS";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "    codfilial id,\n"
                    + "    nomefantasia,\n"
                    + "    cpfcnpj\n"
                    + "from \n"
                    + "    filial")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codfilial"), rs.getString("nomefantasia")));
                }
            }
            return result;
        }
    }

}
