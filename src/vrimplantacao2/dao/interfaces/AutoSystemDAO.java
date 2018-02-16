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

    public int getIdDeposito() {
        return idDeposito;
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT grid, grid || ' - ' || nome_reduzido descricao FROM pessoa WHERE tipo ~~ '%%E%%'::bpchar::text order by 1"
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
                    "	g.nome::varchar merc1_desc,\n" +
                    "	sg.codigo merc2,\n" +
                    "	sg.nome::varchar merc2_desc\n" +
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
                    "select\n" +
                    "	p.grid id,\n" +
                    "	p.codigo,\n" +
                    "	p.data_cad datacadastro,\n" +
                    "	ean.codigo_barra ean,\n" +
                    "	1 qtdEmbalagem,\n" +
                    "	p.qtde_unid_entrada qtdEmbalagemCotacao,\n" +
                    "	p.unid_med unidade,\n" +
                    "	case p.unid_med\n" +
                    "	when 'KG' then 1\n" +
                    "	else 0\n" +
                    "	end ebalanca,\n" +
                    "	p.dias_validade validade,\n" +
                    "	p.nome descricaocompleta,\n" +
                    "	p.grupo merc1,\n" +
                    "	p.subgrupo merc2,\n" +
                    "	est.estoque,\n" +
                    "	p.margem_lucro margem,\n" +
                    "	est.ult_custo custosemimposto,\n" +
                    "	est.ult_custo_fiscal custocomimposto,\n" +
                    "	p.preco_unit precovenda,\n" +
                    "	case when p.permite_venda then 1 else 0 end id_situacaocadastro,\n" +
                    "	p.codigo_ncm ncm,\n" +
                    "	p.cest,\n" +
                    "	p.cst_pis piscofins_saida,\n" +
                    "	p.cst_pis_entrada pisconfins_entrada,\n" +
                    "	p.natureza_receita piscofins_natureza_receita,\n" +
                    "	p.tributacao id_icms,\n" +
                    "	p.fornecedor\n" +
                    "from\n" +
                    "	produto p\n" +
                    "	left join (\n" +
                    "		select\n" +
                    "			grid,\n" +
                    "			codigo_barra\n" +
                    "		from\n" +
                    "			produto\n" +
                    "		where\n" +
                    "			not codigo_barra is null and\n" +
                    "			trim(codigo_barra) != ''\n" +
                    "		union	\n" +
                    "		select\n" +
                    "			grid,\n" +
                    "			codigo_barra\n" +
                    "		from\n" +
                    "			produto_codigo_barra\n" +
                    "		where\n" +
                    "			not codigo_barra is null and\n" +
                    "			trim(codigo_barra) != ''\n" +
                    "	) ean on\n" +
                    "		ean.grid = p.grid\n" +
                    "	left join  estoque_produto est on\n" +
                    "		est.empresa = " + getLojaOrigem() + "\n" +
                    "		and est.deposito = " + getIdDeposito() + "\n" +
                    "		and est.produto = p.grid\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdEmbalagemCotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("pisconfins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setFornecedorFabricante(rst.getString("fornecedor"));
                    imp.setCodigoSped(rst.getString("id"));
                    
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
