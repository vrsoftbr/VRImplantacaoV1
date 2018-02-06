package vrimplantacao2.dao.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SysPdvDAO extends InterfaceDAO {
    
    private TipoConexao tipoConexao;

    public void setTipoConexao(TipoConexao tipoConexao) {
        this.tipoConexao = tipoConexao;
    }

    @Override
    public String getSistema() {
        return "SysPDV";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m1.seccod, \n" +
                    "	m1.secdes, \n" +
                    "	m2.grpcod, \n" +
                    "	m2.grpdes, \n" +
                    "	m3.sgrcod, \n" +
                    "	m3.sgrdes \n" +
                    "from \n" +
                    "	secao as m1 \n" +
                    "	left join grupo as m2 on \n" +
                    "		m2.seccod = m1.seccod \n" +
                    "	left join subgrupo as m3 on \n" +
                    "		m3.seccod = m1.seccod and\n" +
                    "		m3.grpcod = m2.grpcod \n" +
                    "order by \n" +
                    "	m1.seccod,\n" +
                    "	m2.grpcod,\n" +
                    "	m3.sgrcod"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("seccod"));
                    imp.setMerc1Descricao(rst.getString("secdes"));
                    imp.setMerc2ID(rst.getString("grpcod"));
                    imp.setMerc2Descricao(rst.getString("grpdes"));
                    imp.setMerc3ID(rst.getString("sgrcod"));
                    imp.setMerc3Descricao(rst.getString("sgrdes"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
         List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	procodsim,\n" +
                    "	similaresdes\n" +
                    "from\n" +
                    "	similares"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("procodsim"));
                    imp.setDescricao(rst.getString("similaresdes"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = tipoConexao.getConnection().createStatement()) {        
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	replace(p.procod,',','') procod,\n" +
                    "	p.procodint,\n" +
                    "	p.prodes,\n" +
                    "	p.prodesrdz,\n" +
                    "	p.seccod,\n" +
                    "	p.trbid,\n" +
                    "	p.prounid,\n" +
                    "	p.proestmin,\n" +
                    "	p.proestmax,\n" +
                    "	p.grpcod,\n" +
                    "	p.sgrcod,\n" +
                    "	p.proncm,\n" +
                    "	p.proforlin,\n" +
                    "	p.natcodigo,\n" +
                    "	p.proprc1,\n" +
                    "	p.proprccst,\n" +
                    "	p.prodatcadinc,\n" +
                    "	p.proiteemb,\n" +
                    "	p.promrg1,\n" +
                    "	proprcvdavar,\n" +
                    "	items.procodsim,\n" +
                    "	p.procest,\n" +
                    "	p.propesbrt,\n" +
                    "	p.propesliq,\n" +
                    "	case p.propesvar\n" +
                    "	when 'S' then 4\n" +
                    "	when 'P' then 4\n" +
                    "	else 1 end as id_tipoembalagem,\n" +
                    "	case when p.proenvbal = 'S' then 1 else 0 end as e_balanca,\n" +
                    "	p.provld validade,\n" +
                    "	i.trbtabb, \n" +
                    "	i.trbalq, \n" +
                    "	i.trbred, \n" +
                    "	p.procest, \n" +
                    "	vw.pis_cst_e, \n" +
                    "	vw.pis_cst_s,\n" +
                    "	vw.cod_natureza_receita, \n" +
                    "	p.PROPESVAR\n" +
                    "FROM \n" +
                    "	produto p\n" +
                    "	LEFT JOIN item_similares items ON \n" +
                    "		items.procod = p.procod\n" +
                    "	left join tributacao i on \n" +
                    "		i.trbid = p.trbid\n" +
                    "	left join mxf_vw_pis_cofins vw on \n" +
                    "		vw.codigo_produto = p.procod\n" +
                    "where \n" +
                    "	p.procod similar to '[0-9]+'\n" +
                    "ORDER BY \n" +
                    "	cast(replace(p.procod,',','') as bigint)"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString(""));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT prpcod, prpcab1 FROM PROPRIO"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("prpcod"), rst.getString("prpcab1")));
                }
            }
        }
        
        return result;
    }
        
    public static enum TipoConexao {
        
        FIREBIRD {
            @Override
            public Connection getConnection() {
                return ConexaoFirebird.getConexao();
            }
        },
        SQL_SERVER {
            @Override
            public Connection getConnection() {
                return ConexaoSqlServer.getConexao();
            }
        };
        
        public abstract Connection getConnection();
        public String getLojasClienteSQL() {
            return "";
        }
        
    }
    
}
