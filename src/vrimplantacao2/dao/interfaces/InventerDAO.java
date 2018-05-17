package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class InventerDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Inventer";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ChvEmp, NomeRazao, Cnpj from tbEmp order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("ChvEmp"), rst.getString("NomeRazao") + " - " + rst.getString("Cnpj")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	g1.ChvGrp1 merc1,\n" +
                    "	g1.Descricao merc1_desc,\n" +
                    "	g2.ChvGrp2 merc2,\n" +
                    "	g2.Descricao merc2_desc,\n" +
                    "	g3.ChvGrp3 merc3,\n" +
                    "	g3.Descricao merc3_desc,\n" +
                    "	g4.ChvGrp4 merc4,\n" +
                    "	g4.Descricao merc4_desc\n" +
                    "from\n" +
                    "	tbTab_Grp1 g1\n" +
                    "	left join tbTab_Grp2 g2 on\n" +
                    "		g1.ChvGrp1 = g2.ChvGrp1\n" +
                    "	left join tbTab_Grp3 g3 on\n" +
                    "		g2.ChvGrp2 = g3.ChvGrp2\n" +
                    "	left join tbTab_Grp4 g4 on\n" +
                    "		g3.ChvGrp3 = g4.ChvGrp3\n" +
                    "where\n" +
                    "	g1.ChvGrp1 != 1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
                    imp.setMerc4ID(rst.getString("merc4"));
                    imp.setMerc4Descricao(rst.getString("merc4_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    
}
