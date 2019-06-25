package vrimplantacao2.dao.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Leandro
 */
public class AutomaqDAO extends InterfaceDAO {

    private String complemento = "";
    private Connection conexaoProduto;

    public void setConexaoProduto(Connection conexaoProduto) {
        this.conexaoProduto = conexaoProduto;
    }
    
    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Automaq";
        } else {
            return "Automaq(" + complemento + ")";
        }
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public List<Estabelecimento> getLojas() {
        return Arrays.asList(new Estabelecimento("1", "AUTOMAQ LJ01"));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = conexaoProduto.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    d.codinterno cod_merc1,\n" +
                    "    d.nomedepartamento merc1,\n" +
                    "    g.codinterno cod_marc2,\n" +
                    "    g.nomegrupo merc2,\n" +
                    "    sg.codinterno cod_merc3,\n" +
                    "    sg.nomesubgrupo merc3\n" +
                    "from\n" +
                    "    tbldepartamento d\n" +
                    "    left join tblgrupo g on\n" +
                    "        d.codinterno = g.coddepartamento\n" +
                    "    left join tblsubgrupo sg on\n" +
                    "        g.coddepartamento = sg.coddepartamento and\n" +
                    "        g.codinterno = sg.codgrupo\n" +
                    "order by\n" +
                    "    d.codinterno,\n" +
                    "    g.codinterno,\n" +
                    "    sg.codinterno"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("cod_merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1"));
                    imp.setMerc2ID(rst.getString("cod_marc2"));
                    imp.setMerc2Descricao(rst.getString("merc2"));
                    imp.setMerc3ID(rst.getString("cod_merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = conexaoProduto.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.codinterno id,\n" +
                    "    f.nomeagrupamento descricao\n" +
                    "from\n" +
                    "    tblagrupamento f\n" +
                    "order by\n" +
                    "    codinterno"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
    
    
}
