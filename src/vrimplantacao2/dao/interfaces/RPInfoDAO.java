package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Leandro
 */
public class RPInfoDAO extends InterfaceDAO {
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select empr_codigo, empr_nome from empresas order by empr_codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("empr_codigo"), rst.getString("empr_nome")));
                }
            }
        }
        
        return result;
    }

    @Override
    public String getSistema() {
        return "RPInfo";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO_PRODUTO
        }));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	d.dpto_codigo merc1,\n" +
                    "	d.dpto_descricao merc1_desc,\n" +
                    "	g.grup_codigo merc2,\n" +
                    "	g.grup_descricao merc2_desc\n" +
                    "from\n" +
                    "	produtos p\n" +
                    "	join departamentos d on p.prod_dpto_codigo = d.dpto_codigo\n" +
                    "	join grupos g on p.prod_grup_codigo = g.grup_codigo\n" +
                    "order by\n" +
                    "	1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
