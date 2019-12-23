package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class DirectorDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Director";
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select DFcod_empresa id, DFnome_fantasia fantasia from TBempresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	d.DFid_departamento_item merc1,\n" +
                    "	d.DFdescricao descmerc1,\n" +
                    "	s.DFid_departamento_item merc2,\n" +
                    "	s.DFdescricao descmerc2,\n" +
                    "	g.DFid_departamento_item merc3,\n" +
                    "	g.DFdescricao descmerc3\n" +
                    "from \n" +
                    "	TBdepartamento_item d\n" +
                    "join\n" +
                    "	TBdepartamento_item s on d.DFid_departamento_item = s.DFid_departamento_item_pai\n" +
                    "join\n" +
                    "	TBdepartamento_item g on s.DFid_departamento_item = g.DFid_departamento_item_pai\n" +
                    "order by\n" +
                    "	2, 4, 6")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct(f.DFcod_produto_origem) codigo, \n" +
                    "	p.DFdescricao descricao\n" +
                    "from \n" +
                    "	TBproduto_similar f \n" +
                    "inner join TBitem_estoque p on p.DFcod_item_estoque = f.DFcod_produto_origem \n" +
                    "order by\n" +
                    "	2")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	 p.DFcod_item_estoque id,\n" +
                    "	 p.DFdescricao descricaocompleta,\n" +
                    "	 p.DFdescricao_resumida descricaoreduzida,\n" +
                    "	 p.DFdescricao_analitica descricaogondola,\n" +
                    "	 cb.DFcodigo_barra ean,\n" +
                    "	 un.DFdescricao embalagem,\n" +
                    "	 pu.DFfator_conversao qtdembalagem,\n" +
                    "	 p.DFativo_inativo situacao,\n" +
                    "	 p.DFdata_cadastro datacadastro,\n" +
                    "	 merc.merc1,\n" +
                    "	 merc.merc2,\n" +
                    "	 merc.merc3,\n" +
                    "	 fam.DFcod_produto_origem familia,\n" +
                    "	 p.DFpeso_liquido pesoliquido,\n" +
                    "	 p.DFpeso_variavel pesavel,\n" +
                    "	 pa.DFcodigo_setor_balanca setor_balanca,\n" +
                    "	 pa.DFvalidade_pesaveis validade,\n" +
                    "	 pe.DFestoque_minimo estoqueminimo,\n" +
                    "	 pe.DFestoque_maximo estoquemaximo,\n" +
                    "	 es.DFquantidade_Atual estoque,\n" +
                    "	 pe.DFmargem_lucro margem,\n" +
                    "	 pr.DFpreco_venda precovenda,\n" +
                    "	 pr.DFcusto_real custoreal,\n" +
                    "	 pr.DFcusto_contabil custocontabil,\n" +
                    "	 pa.DFcod_classificacao_fiscal ncm,\n" +
                    "	 pa.DFcod_cst_pis pis_saida,\n" +
                    "	 pa.DFcod_cst_cofins cofins_saida,\n" +
                    "	 pa.DFcod_cst_pis_entrada pis_entrada,\n" +
                    "	 pa.DFcod_cst_cofins_entrada cofins_entrada,\n" +
                    "	 pa.dfcod_cest cest,\n" +
                    "	 cst.DFcod_tributacao_cst cst,\n" +
                    "	 tc.DFtipo_tributacao tipocst,\n" +
                    "	 ae.DFaliquota_icms icms_debito,\n" +
                    "	 ae.DFpercentual_reducao icms_reducao_debito,\n" +
                    "	 ae.DFaliquota_icms_subst_tributaria icms_sub_tributaria,\n" +
                    "	 ae.DFaliquota_icms_desonerado icms_desonerado,\n" +
                    "	 nr.DFcod_natureza_receita_pis_cofins naturezareceita\n" +
                    "from \n" +
                    "	TBitem_estoque p\n" +
                    "left join \n" +
                    "	TBitem_estoque_empresa pe on p.DFcod_item_estoque = pe.DFcod_item_estoque\n" +
                    "inner join\n" +
                    "	TBempresa em on pe.DFcod_empresa = em.DFcod_empresa\n" +
                    "left join \n" +
                    "	TBunidade_item_estoque pu on p.DFcod_item_estoque = pu.DFcod_item_estoque\n" +
                    "left join \n" +
                    "	TBitem_estoque_atacado_varejo pa on p.DFcod_item_estoque = pa.DFcod_item_estoque_atacado_varejo\n" +
                    "left join \n" +
                    "	TBunidade un on pu.DFcod_unidade = un.DFcod_unidade\n" +
                    "inner join \n" +
                    "	TBtipo_unidade_item_estoque tu WITH (NOLOCK) on tu.DFid_unidade_item_estoque = pu.DFid_unidade_item_estoque and\n" +
                    "	 tu.DFid_tipo_unidade = (SELECT DFvalor FROM TBopcoes WITH (NOLOCK) WHERE DFcodigo = 420)  \n" +
                    "left join \n" +
                    "	tbresumo_estoque es WITH (NOLOCK) on es.DFid_unidade_item_estoque = p.DFunidade_controle and \n" +
                    "	es.DFcod_empresa = pe.DFcod_empresa and \n" +
                    "	es.DFid_tipo_estoque = (select DFvalor from TBopcoes WITH (NOLOCK) where DFcodigo = 553)\n" +
                    "left join \n" +
                    "	TBunidade_item_estoque_preco pr on pr.DFid_unidade_item_estoque = pu.DFid_unidade_item_estoque and \n" +
                    "	pr.DFcod_empresa = pe.DFcod_empresa\n" +
                    "left join \n" +
                    "	TBcodigo_barra cb on pu.DFid_unidade_item_estoque = cb.DFid_unidade_item_estoque\n" +
                    "left join\n" +
                    "	TBitem_cst_aliquota_icms_estado ai on p.DFcod_item_estoque = ai.DFcod_item_estoque\n" +
                    "left join\n" +
                    "	TBcst_aliquota_icms_estado ae on ai.DFid_cst_aliquota_icms_estado = ae.DFid_cst_aliquota_icms_estado\n" +
                    "left join\n" +
                    "	TBaliquota_icms_estado ac on ae.DFid_aliquota_icms_estado = ac.DFid_aliquota_icms_estado\n" +
                    "inner join\n" +
                    "	TBcst cst WITH (NOLOCK) on ae.DFid_cst = cst.DFid_cst \n" +
                    "inner join \n" +
                    "	TBramo_atividade_tributacao_cst ra WITH (NOLOCK) on ra.DFcod_tributacao_cst = cst.DFcod_tributacao_cst \n" +
                    "inner join\n" +
                    "	TBtributacao_cst tc WITH (NOLOCK) on tc.DFcod_tributacao_cst = cst.DFcod_tributacao_cst\n" +
                    "left join\n" +
                    "	TBnatureza_receita_pis_cofins nr on pa.DFid_natureza_receita_pis_cofins = nr.DFid_natureza_receita_pis_cofins\n" +
                    "left join\n" +
                    "	TBproduto_similar fam on  p.DFcod_item_estoque = fam.DFcod_produto_similar\n" +
                    "left join\n" +
                    "	(select \n" +
                    "		d.DFid_departamento_item merc1,\n" +
                    "		d.DFdescricao descmerc1,\n" +
                    "		s.DFid_departamento_item merc2,\n" +
                    "		s.DFdescricao descmerc2,\n" +
                    "		g.DFid_departamento_item merc3,\n" +
                    "		g.DFdescricao descmerc3\n" +
                    "	from \n" +
                    "		TBdepartamento_item d\n" +
                    "	join\n" +
                    "		TBdepartamento_item s on d.DFid_departamento_item = s.DFid_departamento_item_pai\n" +
                    "	join\n" +
                    "		TBdepartamento_item g on s.DFid_departamento_item = g.DFid_departamento_item_pai) merc on p.DFid_departamento_item = merc.merc3\n" +
                    "where\n" +
                    "	em.DFcod_empresa = " + getLojaOrigem() + " and\n" +
                    "	ai.DFpessoa_fisica_juridica = 'F' and\n" +
                    "	ra.DFid_ramo_atividade = em.DFid_ramo_atividade and\n" +
                    "	ac.DFcod_uf = em.DFuf_base and\n" +
                    "	ac.DFcod_uf_destino = em.DFuf_base and\n" +
                    "	ae.DFcod_grupo_tributacao = em.DFcod_grupo_tributacao and\n" +
                    "	ae.DFid_tipo_estabelecimento = em.DFid_tipo_estabelecimento\n" +
                    "order by\n" +
                    "	1")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("familia"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.seteBalanca(rs.getInt("pesavel") == 1);
                    
                    if(imp.isBalanca()) {
                        imp.setEan(imp.getImportId());
                    }
                    
                    imp.setValidade(rs.getInt("validade"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoSemImposto(rs.getDouble("custoreal"));
                    imp.setCustoComImposto(rs.getDouble("custocontabil"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsCstDebito(rs.getString("cofins_saida"));
                    imp.setPiscofinsCstCredito(rs.getString("cofins_entrada"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsCst(rs.getString("cst"));
                    imp.setIcmsAliq(rs.getDouble("icms_debito"));
                    imp.setIcmsReducao(rs.getDouble("icms_reducao_debito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
