package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * Dao do sistema AutoSystem.
 * @author Leandro
 */
public class AutoSystemDAO extends InterfaceDAO implements MapaTributoProvider {

    private int idDeposito = 0;

    public void setIdDeposito(int idDeposito) {
        this.idDeposito = idDeposito;
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select grid, grid || ' - ' || nome_reduzido descricao from empresa order by 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("grid"), rst.getString("descricao"))
                    );
                }
            }
        }
        
        return result;
    }
    
    public List<Estabelecimento> getDepositos() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            String sql = "select\n" +
                    "	grid,\n" +
                    "	nome\n" +
                    "from\n" +
                    "	deposito\n" +
                    "where\n" +
                    "	empresa = " + getLojaOrigem() + " \n" +
                    "order by codigo";
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("grid"), rst.getString("nome")));
                }
            }
        }
        
        return result;
    }    

    @Override
    public String getSistema() {
        return "AutoSystem";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	g.codigo merc1,\n" +
                    "	g.nome merc1_desc,\n" +
                    "	sg.codigo merc2,\n" +
                    "	sg.nome merc2_desc\n" +
                    "from\n" +
                    "	grupo_produto g\n" +
                    "	left join subgrupo_produto sg on\n" +
                    "		sg.grupo = g.grid\n" +
                    "order by\n" +
                    "	g.codigo,\n" +
                    "	sg.codigo"
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

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	coalesce(cst,'') || '-' || coalesce(tributacao,0) || '%-' || coalesce(reducao_base,0) || '%-' || descricao  descricao\n" +
                    "from\n" +
                    "	tributacao\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    
    
    
}
