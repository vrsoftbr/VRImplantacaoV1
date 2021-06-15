package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class EcoCentauroDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Eco Centauro";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    codigo,\n"
                    + "    nomefantasia,\n"
                    + "    cpfcnpj\n"
                    + "FROM TGEREMPRESA\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"),
                                    rst.getString("nomefantasia") + "-" + rst.getString("cpfcnpj")
                            )
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    m1.codigo AS merc1,\n"
                    + "    m1.descricao AS desc_merc1,\n"
                    + "    m2.subgrupo AS merc2,\n"
                    + "    m2.descricao AS desc_merc2\n"
                    + "FROM TESTGRUPO m1\n"
                    + "LEFT JOIN TESTSUBGRUPO m2 ON m2.grupo = m1.codigo\n"
                    + "WHERE m1.empresa = '" + getLojaOrigem() + "'\n"
                    + "AND m2.empresa = '" + getLojaOrigem() + "'\n"
                    + "order by 1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
