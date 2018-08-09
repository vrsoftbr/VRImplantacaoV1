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
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class MarketDAO extends InterfaceDAO implements MapaTributoProvider{

    public boolean v_usar_arquivoBalanca;
    public String lojaMesmoID;
    
    @Override
    public String getSistema() {
        return "Market";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	cd_loja,\n" +
                    "	nm_loja\n" +
                    "from\n" +
                    "	cadastro.tb_loja")){
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("cd_loja"), rs.getString("nm_loja")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<MercadologicoIMP> getMercadologico() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	dep.cd_depto as merc1,\n" +
                    "	dep.nm_depto as descmerc1,\n" +
                    "	sec.cd_depto_secao as merc2,\n" +
                    "	sec.nm_depto_secao as descmerc2,\n" +
                    "	coalesce(gru.cd_depto_grupo, 1) as merc3,\n" +
                    "	coalesce(gru.nm_depto_grupo, sec.nm_depto_secao) as descmerc3,\n" +
                    "from\n" +
                    "	produto.tb_depto dep\n" +
                    "left join\n" +
                    "	produto.tb_depto_secao sec on sec.cd_depto = dep.cd_depto\n" +
                    "left join\n" +
                    "	produto.tb_depto_grupo gru on gru.cd_depto_secao = sec.cd_depto_secao\n" +
                    "order by\n" +
                    "	dep.cd_depto, sec.cd_depto_secao")) {
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
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.cd_produto,\n" +
                    "	f.cd_base_fornecedor,\n" +
                    "	d.cd_divisao,\n" +
                    "	d.nm_divisao,\n" +
                    "	pd.nr_produto_fornecedor\n" +
                    "from \n" +
                    "	produto.tb_produto_loja_forn f\n" +
                    "left join\n" +
                    "	produto.tb_divisao d on d.cd_base_fornecedor = f.cd_base_fornecedor\n" +
                    "left join\n" +
                    "	produto.tb_produto_divisao pd on pd.cd_produto = f.cd_produto and\n" +
                    "	d.cd_divisao = pd.cd_divisao\n" +
                    //"where\n" +
                    //"	f.cd_produto = 11036\n" +
                    "order by\n" +
                    "	f.cd_base_fornecedor, f.cd_produto")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("cd_produto"));
                    imp.setIdFornecedor(rs.getString("cd_base_fornecedor"));
                    imp.setCodigoExterno(rs.getString("nr_produto_fornecedor"));
                                  
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	pro.cd_produto as id,\n" +
                    "	pro.nm_produto as nomecompleto,\n" +
                    "	pro.nm_reduzido as nomereduzido,\n" +
                    "	barra.cd_codbarra as codigobarras,\n" +
                    "	preco.vl_custo_faturado as custosemimposto,\n" +
                    "	preco.vl_custo as custocomimposto,\n" +
                    "	preco.vl_venda,\n" +
                    "	est.qt_estoque,\n" +
                    "	pro.cd_depto as codmerc1,\n" +
                    "	pro.cd_depto_secao as codmerc2,\n" +
                    "	pro.cd_depto_grupo as codmerc3,\n" +
                    "	pro.cd_depto_subgrupo as codmerc4,\n" +
                    "	pro.tp_venda as balanca,\n" +
                    "	pro.tp_embalagem as embalagem,\n" +
                    "	pro.qt_embalagem as qtdembalagem,\n" +
                    "	case when pro.is_excluido = 'N' then 1 else 0 end as ativo,\n" +
                    "	pro.dt_inc as dtcadastro,\n" +
                    "	pro.vl_peso_liquido as pesoliquido,\n" +
                    "	(select \n" +
                    "		f.nr_ncm\n" +
                    "	from\n" +
                    "		produto.tb_ncm_figura_vigencia_federal f\n" +
                    "	where \n" +
                    "		f.cd_ncm_figura_mva = pro.cd_ncm_figura_mva limit 1) as ncm,\n" +
                    "	(select \n" +
                    "		f.nr_cest\n" +
                    "	from\n" +
                    "		produto.tb_ncm_figura_vigencia_federal f\n" +
                    "	where \n" +
                    "		f.cd_ncm_figura_mva = pro.cd_ncm_figura_mva limit 1) as cest,\n" +
                    "	preco.pr_icms as icmsdebito,\n" +
                    "	preco.ds_icms,\n" +
                    "	preco.pr_pis,\n" +
                    "	preco.pr_cofins\n" +
                    "  from \n" +
                    "	produto.tb_produto pro\n" +
                    "left join\n" +
                    "	produto.tb_produto_codbarra barra on barra.cd_produto = pro.cd_produto\n" +
                    "left join\n" +
                    "	produto.tb_produto_loja preco on preco.cd_produto = pro.cd_produto\n" +
                    "left join\n" +
                    "	paf.tb_mercadoria_estoque est on est.cd_mercadoria = pro.cd_produto\n" +
                    "join\n" +
                    "	cadastro.tb_loja l on l.cd_loja = preco.cd_loja\n" +
                    "where\n" +
                    "	barra.is_padrao = 'S' and\n" +
                    "	l.cd_loja = " + getLojaOrigem() +"\n" +
                    "order by\n" +
                    "	pro.cd_produto")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("nomecompleto"));
                    imp.setDescricaoReduzida(rs.getString("nomereduzido"));
                    imp.setDescricaoGondola(rs.getString("nomecompleto"));
                    
                }
            }
        }
        return result;
    }
}
