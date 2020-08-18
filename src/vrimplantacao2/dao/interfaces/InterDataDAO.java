package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Importacao
 */
public class InterDataDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "InterData";
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    f.i_contador id,\n" +
                    "    f.a_fantasia fantasia\n" +
                    "from\n" +
                    "    filial f"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    i.i_contador id,\n"
                    + "    i.n_icms descricao\n"
                    + "from\n"
                    + "    ICMS I"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    distinct\n" +
                    "    lo.i_contador merc1,\n" +
                    "    LO.SLOCALIZA descmerc1,\n" +
                    "    L.i_contador merc2,\n" +
                    "    L.A_LINHAPRO descmerc2,\n" +
                    "    s.i_contador merc3,\n" +
                    "    S.A_SUBLINHA descmerc3\n" +
                    "from\n" +
                    "    PRODUTO P\n" +
                    "left join LINHA L on (L.I_CONTADOR = P.I_LINHAPRO)\n" +
                    "left join SUBLINHA S on (S.I_CONTADOR = P.I_SUBLINHA)\n" +
                    "left join LOCALIZA LO on (LO.I_CONTADOR = P.ID_LOCALIZA)\n" +
                    "order by\n" +
                    "    1, 3"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());                 
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

}
