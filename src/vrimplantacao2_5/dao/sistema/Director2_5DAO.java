package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import vrframework.remote.ItemComboVO;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Bruno
 */
public class Director2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    public int codigoDocumentoRotativo;

    @Override
    public String getSistema() {
        return "Director";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TROCA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV, // Libera produto para Venda no PDV
                OpcaoProduto.VOLUME_QTD
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE
        ));
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws SQLException {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	distinct\n"
                    + "	 cst.DFcod_tributacao_cst cst,\n"
                    + "	 ae.DFaliquota_icms icms,\n"
                    + "	 ae.DFpercentual_reducao icms_reducao\n"
                    + "from \n"
                    + "	TBitem_estoque p\n"
                    + "left join \n"
                    + "	TBitem_estoque_empresa pe on p.DFcod_item_estoque = pe.DFcod_item_estoque\n"
                    + "inner join\n"
                    + "	TBempresa em on pe.DFcod_empresa = em.DFcod_empresa\n"
                    + "left join\n"
                    + "	TBitem_cst_aliquota_icms_estado ai on p.DFcod_item_estoque = ai.DFcod_item_estoque\n"
                    + "left join\n"
                    + "	TBcst_aliquota_icms_estado ae on ai.DFid_cst_aliquota_icms_estado = ae.DFid_cst_aliquota_icms_estado\n"
                    + "left join\n"
                    + "	TBaliquota_icms_estado ac on ae.DFid_aliquota_icms_estado = ac.DFid_aliquota_icms_estado\n"
                    + "inner join\n"
                    + "	TBcst cst WITH (NOLOCK) on ae.DFid_cst = cst.DFid_cst \n"
                    + "inner join\n"
                    + "	TBtributacao_cst tc WITH (NOLOCK) on tc.DFcod_tributacao_cst = cst.DFcod_tributacao_cst\n"
                    + "where\n"
                    + "	em.DFcod_empresa = 1 and\n"
                    + "	ai.DFpessoa_fisica_juridica = 'F' and\n"
                    + "	ac.DFcod_uf = em.DFuf_base and\n"
                    + "	ac.DFcod_uf_destino = em.DFuf_base and\n"
                    + "	ae.DFcod_grupo_tributacao = em.DFcod_grupo_tributacao\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    while (rst.next()) {
                        String id = getAliquotaKey(
                                rst.getString("cst"),
                                rst.getDouble("icms"),
                                rst.getDouble("icms_reducao")
                        );

                        result.add(new MapaTributoIMP(
                                id,
                                id,
                                Utils.stringToInt(rst.getString("cst")),
                                rst.getDouble("icms"),
                                rst.getDouble("icms_reducao"))
                        );
                    }
                }
            }
            return result;
        }
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	d.DFid_departamento_item merc1,\n"
                    + "	d.DFdescricao descmerc1,\n"
                    + "	s.DFid_departamento_item merc2,\n"
                    + "	s.DFdescricao descmerc2,\n"
                    + "	g.DFid_departamento_item merc3,\n"
                    + "	g.DFdescricao descmerc3\n"
                    + "from \n"
                    + "	TBdepartamento_item d\n"
                    + "join\n"
                    + "	TBdepartamento_item s on d.DFid_departamento_item = s.DFid_departamento_item_pai\n"
                    + "join\n"
                    + "	TBdepartamento_item g on s.DFid_departamento_item = g.DFid_departamento_item_pai\n"
                    + "order by\n"
                    + "	2, 4, 6"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	distinct(f.DFcod_produto_origem) codigo, \n"
                    + "	p.DFdescricao descricao\n"
                    + "from \n"
                    + "	TBproduto_similar f \n"
                    + "inner join TBitem_estoque p on p.DFcod_item_estoque = f.DFcod_produto_origem \n"
                    + "order by\n"
                    + "	2")) {
                while (rs.next()) {
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
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	 p.DFcod_item_estoque id,\n"
                    + "	 cb.DFcodigo_barra ean,\n"
                    + "	 un.DFdescricao embalagem,\n"
                    + "	 pu.DFfator_conversao qtdembalagem\n"
                    + "from TBitem_estoque p\n"
                    + "left join\n"
                    + "     TBunidade_item_estoque pu on p.DFcod_item_estoque = pu.DFcod_item_estoque\n"
                    + "left join \n"
                    + "     TBunidade un on pu.DFcod_unidade = un.DFcod_unidade\n"
                    + "left join \n"
                    + "     TBcodigo_barra cb on pu.DFid_unidade_item_estoque = cb.DFid_unidade_item_estoque"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	 p.DFcod_item_estoque id,\n"
                    + "	 p.DFdescricao descricaocompleta,\n"
                    + "	 p.DFdescricao_resumida descricaoreduzida,\n"
                    + "	 p.DFdescricao_analitica descricaogondola,\n"
                    + "	 cb.DFcodigo_barra ean,\n"
                    + "	 un.DFdescricao embalagem,\n"
                    + "	 pu.DFfator_conversao qtdembalagem,\n"
                    + "	 p.DFativo_inativo situacao,\n"
                    + "	 p.DFdata_cadastro datacadastro,\n"
                    + "	 merc.merc1,\n"
                    + "	 merc.merc2,\n"
                    + "	 merc.merc3,\n"
                    + "	 fam.DFcod_produto_origem familia,\n"
                    + "	 p.DFpeso_liquido pesoliquido,\n"
                    + "	 p.DFpeso_variavel pesavel,\n"
                    + "	 pa.DFcodigo_setor_balanca setor_balanca,\n"
                    + "	 pa.DFvalidade_pesaveis validade,\n"
                    + "	 pe.DFestoque_minimo estoqueminimo,\n"
                    + "	 pe.DFestoque_maximo estoquemaximo,\n"
                    + "	 es.DFquantidade_Atual estoque,\n"
                    + "	 pe.DFmargem_lucro margem,\n"
                    + "	 pr.DFpreco_venda precovenda,\n"
                    + "	 pr.DFcusto_real custoreal,\n"
                    + "	 pr.DFcusto_contabil custocontabil,\n"
                    + "  pr.DFcusto_real_ce,\n"
                    + "	 pa.DFcod_classificacao_fiscal ncm,\n"
                    + "	 pa.DFcod_cst_pis pis_saida,\n"
                    + "	 pa.DFcod_cst_cofins cofins_saida,\n"
                    + "	 pa.DFcod_cst_pis_entrada pis_entrada,\n"
                    + "	 pa.DFcod_cst_cofins_entrada cofins_entrada,\n"
                    + "	 pa.dfcod_cest cest,\n"
                    + "	 cst.DFcod_tributacao_cst cst,\n"
                    + "	 tc.DFtipo_tributacao tipocst,\n"
                    + "	 ae.DFaliquota_icms icms,\n"
                    + "	 ae.DFpercentual_reducao icms_reducao,\n"
                    + "	 ae.DFaliquota_icms_subst_tributaria icms_sub_tributaria,\n"
                    + "	 ae.DFaliquota_icms_desonerado icms_desonerado,\n"
                    + "	 nr.DFcod_natureza_receita_pis_cofins naturezareceita\n"
                    + "from \n"
                    + "	TBitem_estoque p\n"
                    + "left join \n"
                    + "	TBitem_estoque_empresa pe on p.DFcod_item_estoque = pe.DFcod_item_estoque\n"
                    + "left join\n"
                    + "	TBempresa em on pe.DFcod_empresa = em.DFcod_empresa and em.DFcod_empresa = 1\n"
                    + "left join \n"
                    + "	TBunidade_item_estoque pu on p.DFcod_item_estoque = pu.DFcod_item_estoque and pu.DFfator_conversao = 1\n"
                    + "left join \n"
                    + "	TBitem_estoque_atacado_varejo pa on p.DFcod_item_estoque = pa.DFcod_item_estoque_atacado_varejo\n"
                    + "left join \n"
                    + "	TBunidade un on pu.DFcod_unidade = un.DFcod_unidade\n"
                    + "left join \n"
                    + "	TBtipo_unidade_item_estoque tu WITH (NOLOCK) on tu.DFid_unidade_item_estoque = pu.DFid_unidade_item_estoque and\n"
                    + "	 tu.DFid_tipo_unidade = (SELECT DFvalor FROM TBopcoes WITH (NOLOCK) WHERE DFcodigo = 420)\n"
                    + "left join \n"
                    + "	tbresumo_estoque es WITH (NOLOCK) on es.DFid_unidade_item_estoque = p.DFunidade_controle and \n"
                    + "	es.DFcod_empresa = pe.DFcod_empresa and \n"
                    + "	es.DFid_tipo_estoque = (select DFvalor from TBopcoes WITH (NOLOCK) where DFcodigo = 553)\n"
                    + "left join \n"
                    + "	TBunidade_item_estoque_preco pr on pr.DFid_unidade_item_estoque = pu.DFid_unidade_item_estoque and\n"
                    + "	pr.DFcod_empresa = pe.DFcod_empresa and pr.DFcod_empresa = 1\n"
                    + "left join \n"
                    + "	TBcodigo_barra cb on pu.DFid_unidade_item_estoque = cb.DFid_unidade_item_estoque\n"
                    + "left join\n"
                    + "	TBitem_cst_aliquota_icms_estado ai on p.DFcod_item_estoque = ai.DFcod_item_estoque and\n"
                    + "	ai.DFpessoa_fisica_juridica = 'F'\n"
                    + "left join\n"
                    + "	TBcst_aliquota_icms_estado ae on ai.DFid_cst_aliquota_icms_estado = ae.DFid_cst_aliquota_icms_estado and\n"
                    + "	ae.DFcod_grupo_tributacao = em.DFcod_grupo_tributacao and\n"
                    + "	ae.DFid_tipo_estabelecimento = em.DFid_tipo_estabelecimento	\n"
                    + "left join\n"
                    + "	TBaliquota_icms_estado ac on ae.DFid_aliquota_icms_estado = ac.DFid_aliquota_icms_estado and\n"
                    + "	ac.DFcod_uf = em.DFuf_base and\n"
                    + "	ac.DFcod_uf_destino = em.DFuf_base	\n"
                    + "left join\n"
                    + "	TBcst cst WITH (NOLOCK) on ae.DFid_cst = cst.DFid_cst \n"
                    + "left join \n"
                    + "	TBramo_atividade_tributacao_cst ra WITH (NOLOCK) on ra.DFcod_tributacao_cst = cst.DFcod_tributacao_cst and \n"
                    + "	em.DFid_ramo_atividade = ra.DFid_ramo_atividade 	\n"
                    + "left join\n"
                    + "	TBtributacao_cst tc WITH (NOLOCK) on tc.DFcod_tributacao_cst = cst.DFcod_tributacao_cst\n"
                    + "left join\n"
                    + "	TBnatureza_receita_pis_cofins nr on pa.DFid_natureza_receita_pis_cofins = nr.DFid_natureza_receita_pis_cofins\n"
                    + "left join\n"
                    + "	TBproduto_similar fam on  p.DFcod_item_estoque = fam.DFcod_produto_similar\n"
                    + "left join\n"
                    + "	(select \n"
                    + "		d.DFid_departamento_item merc1,\n"
                    + "		d.DFdescricao descmerc1,\n"
                    + "		s.DFid_departamento_item merc2,\n"
                    + "		s.DFdescricao descmerc2,\n"
                    + "		g.DFid_departamento_item merc3,\n"
                    + "		g.DFdescricao descmerc3\n"
                    + "	from \n"
                    + "		TBdepartamento_item d\n"
                    + "	join\n"
                    + "		TBdepartamento_item s on d.DFid_departamento_item = s.DFid_departamento_item_pai\n"
                    + "	join\n"
                    + "		TBdepartamento_item g on s.DFid_departamento_item = g.DFid_departamento_item_pai) merc on p.DFid_departamento_item = merc.merc3\n"
                    + "where em.DFcod_empresa = 1  And cst.DFcod_tributacao_cst is not null and pa.DFcod_cst_pis  is not null\n"
                    + "order by\n"
                    + "	1"
            /*"select \n"
                    + "	 p.DFcod_item_estoque id,\n"
                    + "	 p.DFdescricao descricaocompleta,\n"
                    + "	 p.DFdescricao_resumida descricaoreduzida,\n"
                    + "	 p.DFdescricao_analitica descricaogondola,\n"
                    + "	 cb.DFcodigo_barra ean,\n"
                    + "	 un.DFdescricao embalagem,\n"
                    + "	 pu.DFfator_conversao qtdembalagem,\n"
                    + "	 p.DFativo_inativo situacao,\n"
                    + "	 p.DFdata_cadastro datacadastro,\n"
                    + "	 merc.merc1,\n"
                    + "	 merc.merc2,\n"
                    + "	 merc.merc3,\n"
                    + "	 fam.DFcod_produto_origem familia,\n"
                    + "	 p.DFpeso_liquido pesoliquido,\n"
                    + "	 p.DFpeso_variavel pesavel,\n"
                    + "	 pa.DFcodigo_setor_balanca setor_balanca,\n"
                    + "	 pa.DFvalidade_pesaveis validade,\n"
                    + "	 pe.DFestoque_minimo estoqueminimo,\n"
                    + "	 pe.DFestoque_maximo estoquemaximo,\n"
                    + "	 es.DFquantidade_Atual estoque,\n"
                    + "	 pe.DFmargem_lucro margem,\n"
                    + "	 pr.DFpreco_venda precovenda,\n"
                    + "	 pr.DFcusto_real custoreal,\n"
                    + "	 pr.DFcusto_contabil custocontabil,\n"
                    + "  pr.DFcusto_real_ce,\n"
                    + "	 pa.DFcod_classificacao_fiscal ncm,\n"
                    + "	 pa.DFcod_cst_pis pis_saida,\n"
                    + "	 pa.DFcod_cst_cofins cofins_saida,\n"
                    + "	 pa.DFcod_cst_pis_entrada pis_entrada,\n"
                    + "	 pa.DFcod_cst_cofins_entrada cofins_entrada,\n"
                    + "	 pa.dfcod_cest cest,\n"
                    + "	 cst.DFcod_tributacao_cst cst,\n"
                    + "	 tc.DFtipo_tributacao tipocst,\n"
                    + "	 ae.DFaliquota_icms icms,\n"
                    + "	 ae.DFpercentual_reducao icms_reducao,\n"
                    + "	 ae.DFaliquota_icms_subst_tributaria icms_sub_tributaria,\n"
                    + "	 ae.DFaliquota_icms_desonerado icms_desonerado,\n"
                    + "	 nr.DFcod_natureza_receita_pis_cofins naturezareceita\n"
                    + "from \n"
                    + "	TBitem_estoque p\n"
                    + "left join \n"
                    + "	TBitem_estoque_empresa pe on p.DFcod_item_estoque = pe.DFcod_item_estoque\n"
                    + "inner join\n"
                    + "	TBempresa em on pe.DFcod_empresa = em.DFcod_empresa\n"
                    + "left join \n"
                    + "	TBunidade_item_estoque pu on p.DFcod_item_estoque = pu.DFcod_item_estoque\n"
                    + "left join \n"
                    + "	TBitem_estoque_atacado_varejo pa on p.DFcod_item_estoque = pa.DFcod_item_estoque_atacado_varejo\n"
                    + "left join \n"
                    + "	TBunidade un on pu.DFcod_unidade = un.DFcod_unidade\n"
                    + "inner join \n"
                    + "	TBtipo_unidade_item_estoque tu WITH (NOLOCK) on tu.DFid_unidade_item_estoque = pu.DFid_unidade_item_estoque and\n"
                    + "	 tu.DFid_tipo_unidade = (SELECT DFvalor FROM TBopcoes WITH (NOLOCK) WHERE DFcodigo = 420)  \n"
                    + "left join \n"
                    + "	tbresumo_estoque es WITH (NOLOCK) on es.DFid_unidade_item_estoque = p.DFunidade_controle and \n"
                    + "	es.DFcod_empresa = pe.DFcod_empresa and \n"
                    + "	es.DFid_tipo_estoque = (select DFvalor from TBopcoes WITH (NOLOCK) where DFcodigo = 553)\n"
                    + "left join \n"
                    + "	TBunidade_item_estoque_preco pr on pr.DFid_unidade_item_estoque = pu.DFid_unidade_item_estoque and \n"
                    + "	pr.DFcod_empresa = pe.DFcod_empresa\n"
                    + "left join \n"
                    + "	TBcodigo_barra cb on pu.DFid_unidade_item_estoque = cb.DFid_unidade_item_estoque\n"
                    + "left join\n"
                    + "	TBitem_cst_aliquota_icms_estado ai on p.DFcod_item_estoque = ai.DFcod_item_estoque\n"
                    + "left join\n"
                    + "	TBcst_aliquota_icms_estado ae on ai.DFid_cst_aliquota_icms_estado = ae.DFid_cst_aliquota_icms_estado\n"
                    + "left join\n"
                    + "	TBaliquota_icms_estado ac on ae.DFid_aliquota_icms_estado = ac.DFid_aliquota_icms_estado\n"
                    + "inner join\n"
                    + "	TBcst cst WITH (NOLOCK) on ae.DFid_cst = cst.DFid_cst \n"
                    + "inner join \n"
                    + "	TBramo_atividade_tributacao_cst ra WITH (NOLOCK) on ra.DFcod_tributacao_cst = cst.DFcod_tributacao_cst \n"
                    + "inner join\n"
                    + "	TBtributacao_cst tc WITH (NOLOCK) on tc.DFcod_tributacao_cst = cst.DFcod_tributacao_cst\n"
                    + "left join\n"
                    + "	TBnatureza_receita_pis_cofins nr on pa.DFid_natureza_receita_pis_cofins = nr.DFid_natureza_receita_pis_cofins\n"
                    + "left join\n"
                    + "	TBproduto_similar fam on  p.DFcod_item_estoque = fam.DFcod_produto_similar\n"
                    + "left join\n"
                    + "	(select \n"
                    + "		d.DFid_departamento_item merc1,\n"
                    + "		d.DFdescricao descmerc1,\n"
                    + "		s.DFid_departamento_item merc2,\n"
                    + "		s.DFdescricao descmerc2,\n"
                    + "		g.DFid_departamento_item merc3,\n"
                    + "		g.DFdescricao descmerc3\n"
                    + "	from \n"
                    + "		TBdepartamento_item d\n"
                    + "	join\n"
                    + "		TBdepartamento_item s on d.DFid_departamento_item = s.DFid_departamento_item_pai\n"
                    + "	join\n"
                    + "		TBdepartamento_item g on s.DFid_departamento_item = g.DFid_departamento_item_pai) merc on p.DFid_departamento_item = merc.merc3\n"
                    + "where\n"
                    + "	em.DFcod_empresa = " + getLojaOrigem() + " and\n"
                    + "	ai.DFpessoa_fisica_juridica = 'F' and\n"
                    + "	ra.DFid_ramo_atividade = em.DFid_ramo_atividade and\n"
                    + "	ac.DFcod_uf = em.DFuf_base and\n"
                    + "	ac.DFcod_uf_destino = em.DFuf_base and\n"
                    + "	ae.DFcod_grupo_tributacao = em.DFcod_grupo_tributacao and\n"
                    + "	ae.DFid_tipo_estabelecimento = em.DFid_tipo_estabelecimento\n"
                    + "order by\n"
                    + "	1"*/
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setCustoMedioSemImposto(rs.getDouble("custocontabil"));
                    imp.setCustoMedioComImposto(rs.getDouble("custoreal"));
                    imp.setCustoSemImposto(rs.getDouble("custocontabil"));
                    imp.setCustoComImposto(rs.getDouble("DFcusto_real_ce"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    //imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.seteBalanca(rs.getInt("pesavel") == 1);

                    if (imp.isBalanca()) {
                        imp.setEan(imp.getImportId());
                    }

                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsCstDebito(rs.getString("cofins_saida"));
                    imp.setPiscofinsCstCredito(rs.getString("cofins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setCest(rs.getString("cest"));

                    String icms = getAliquotaKey(
                            rs.getString("cst"),
                            rs.getDouble("icms"),
                            rs.getDouble("icms_reducao")
                    );

                    imp.setIcmsDebitoId(icms);
                    imp.setIcmsDebitoForaEstadoId(icms);
                    imp.setIcmsDebitoForaEstadoNfId(icms);
                    imp.setIcmsCreditoId(icms);
                    imp.setIcmsCreditoForaEstadoId(icms);
                    imp.setIcmsConsumidorId(icms);


                    /*imp.setIcmsCstSaida(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms_debito"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_reducao_debito"));
                    
                    imp.setIcmsCstSaidaForaEstado(rs.getInt("cst"));
                    imp.setIcmsAliqSaidaForaEstado(rs.getDouble("icms_debito"));
                    imp.setIcmsReducaoSaidaForaEstado(rs.getDouble("icms_reducao_debito"));
                    
                    imp.setIcmsCstSaidaForaEstadoNF(rs.getInt("cst"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rs.getDouble("icms_debito"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rs.getDouble("icms_reducao_debito"));                    

                    imp.setIcmsCstEntrada(rs.getInt("cst"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_debito"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_reducao_debito"));
                    
                    imp.setIcmsCstEntradaForaEstado(rs.getInt("cst"));
                    imp.setIcmsAliqEntradaForaEstado(rs.getDouble("icms_debito"));
                    imp.setIcmsReducaoEntradaForaEstado(rs.getDouble("icms_reducao_debito"));
                    
                    imp.setIcmsCstConsumidor(rs.getInt("cst"));
                    imp.setIcmsAliqConsumidor(rs.getDouble("icms_debito"));
                    imp.setIcmsReducaoConsumidor(rs.getDouble("icms_reducao_debito"));*/
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	p.DFid_promocao id,\n"
                    + "	p.DFdata_inicial datainicial,\n"
                    + "	p.DFdata_final datafinal,\n"
                    + "	p.DFdescricao descricao,\n"
                    + "	pi.DFid_unidade_item_estoque idproduto,\n"
                    + "	pi.dfpreco precopromocao,\n"
                    + "	ie.DFpreco_venda precovenda\n"
                    + "from\n"
                    + "	tbpromocao p\n"
                    + "join\n"
                    + "	TBpromocao_empresa pe on p.DFid_promocao = pe.DFid_promocao\n"
                    + "join\n"
                    + "	TBpromocao_unidade_item_estoque pi on p.DFid_promocao = pi.DFid_promocao \n"
                    + "join\n"
                    + "	TBunidade_item_estoque_preco ie on pi.DFid_unidade_item_estoque = ie.DFid_unidade_item_estoque and\n"
                    + "	ie.DFcod_empresa = pe.DFcod_empresa\n"
                    + "where\n"
                    + "	p.DFdata_final >= GETDATE() and\n"
                    + "	pe.DFcod_empresa = \n"
                    + "order by\n"
                    + "	p.DFdata_final"
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicial"));
                    imp.setDataFim(rs.getDate("datafinal"));
                    imp.setPrecoOferta(rs.getDouble("precopromocao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	f.DFcod_fornecedor id,\n"
                    + "	f.DFnome razao,\n"
                    + "	f.DFnome_fantasia fantasia,\n"
                    + "	f.DFcgc cnpj,\n"
                    + "	f.DFinscr_estadual ie,\n"
                    + "	f.DFprazo_entrega prazo_entrega,\n"
                    + "	f.DFobservacao obs,\n"
                    + "	f.DFdata_cadastro data_cadastro,\n"
                    + "	ft.DFid_tipo_estabelecimento idtipo_empresa,\n"
                    + "	ft.DFdescricao tipo_empresa,\n"
                    + "	fp.DFdescricao forma_pagamento,\n"
                    + "	f.DFinscricao_municipal inscricao_municipal,\n"
                    + "	fr.DFid_ramo_atividade ramo_atividade,\n"
                    + "	fr.DFdescricao descricao_ramo_atividade,\n"
                    + "	cl.DFcod_cep cep,\n"
                    + "	tl.DFdescricao + ' ' + lo.DFdescricao endereco,\n"
                    + "	f.DFcomplemento_endereco numero,\n"
                    + "	lo.DFcomplemento complemento,\n"
                    + "	ba.DFdescricao bairro,\n"
                    + "	lc.DFdescricao municipio,\n"
                    + "	lc.DFcod_uf uf,\n"
                    + "	ct.DFe_mail email,\n"
                    + "	ct.DFfax fax,\n"
                    + "	ct.DFtelefone telefone,\n"
                    + "	ct.DFtelefone_celular celular,\n"
                    + "	ct.DFcontato contato,\n"
                    + "	ct.DFcargo_contato cargo_contato,\n"
                    + "	sc.DFdescricao setor,\n"
                    + " f.DFindicador_IE as indicadoIE,\n"
                    + " f.DFdata_inativacao\n"
                    + "from\n"
                    + "	TBfornecedor f\n"
                    + "left join \n"
                    + "	TBtipo_estabelecimento ft on f.DFid_tipo_estabelecimento = ft.DFid_tipo_estabelecimento\n"
                    + "left join\n"
                    + "	TBplano_pagamento fp on f.DFcod_plano_pagamento = fp.DFcod_plano_pagamento\n"
                    + "left join\n"
                    + "	TBramo_atividade fr on f.DFid_ramo_atividade = fr.DFid_ramo_atividade\n"
                    + "left join\n"
                    + "	TBcep_logradouro cl on f.DFid_cep_logradouro = cl.DFid_cep_logradouro\n"
                    + "left join\n"
                    + "	TBlogradouro lo on cl.DFid_logradouro = lo.DFid_logradouro\n"
                    + "left join\n"
                    + "	TBtipo_logradouro tl on lo.DFcod_tipo_logradouro = tl.DFcod_tipo_logradouro\n"
                    + "left join\n"
                    + "	TBbairro ba on lo.DFid_bairro = ba.DFid_bairro\n"
                    + "left join\n"
                    + "	TBlocalidade lc on ba.DFcod_localidade = lc.DFcod_localidade\n"
                    + "left join\n"
                    + "	TBcontato_fornecedor ct on f.DFcod_fornecedor = ct.DFcod_fornecedor\n"
                    + "left join\n"
                    + "	TBsetor_contato sc on ct.DFid_setor_contato = sc.DFid_setor_contato\n"
                    + "order by\n"
                    + "	f.DFcod_fornecedor"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));
                    imp.setObservacao(rs.getString("obs") + " Forma Pag.: " + rs.getString("forma_pagamento"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setInsc_municipal(rs.getString("inscricao_municipal"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setAtivo((rs.getDate("DFdata_inativacao") == null));

                    switch (rs.getInt("indicadoIE")) {
                        case 1:
                            imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ICMS);
                            break;
                        case 2:
                            imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ISENTO);
                            break;
                        default:
                            imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);
                            break;
                    }

                    if (rs.getString("telefone") != null
                            && !rs.getString("telefone").trim().isEmpty()) {
                        imp.setTel_principal(rs.getString("telefone"));
                    } else {
                        imp.setTel_principal(rs.getString("celular"));
                    }

                    int idTipoEmpresa = rs.getInt("idtipo_empresa");
                    if (idTipoEmpresa == 57) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                    } else if (idTipoEmpresa == 98) {
                        imp.setTipoEmpresa(TipoEmpresa.PESSOA_FISICA);
                    } else if (idTipoEmpresa == 99) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    } else if (idTipoEmpresa == 101) {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    } else if (idTipoEmpresa == 102) {
                        imp.setProdutorRural();
                    } else if (idTipoEmpresa == 103) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                    } else {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    }

                    int idTipoFornecedor = rs.getInt("ramo_atividade");
                    if (idTipoFornecedor == 2) {
                        imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                    } else if (idTipoFornecedor == 3) {
                        imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                    } else if (idTipoFornecedor == 7) {
                        imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                    } else if (idTipoFornecedor == 57) {
                        imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                    } else if (idTipoFornecedor == 45) {
                        imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                    } else {
                        imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                    }

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select \n"
                                + "	DFcod_fornecedor, \n"
                                + "	DFtelefone, \n"
                                + "	DFfax, \n"
                                + "	DFcontato,\n"
                                + "	DFe_mail,\n"
                                + "	DFtelefone_celular \n"
                                + "from TBcontato_fornecedor\n"
                                + "where DFcod_fornecedor = " + imp.getImportId() + "\n"
                                + "order by DFcod_fornecedor "
                        )) {
                            while (rst2.next()) {
                                imp.addContato(
                                        rst2.getString("DFcontato"),
                                        rst2.getString("DFtelefone"),
                                        rst2.getString("DFtelefone_celular"),
                                        TipoContato.COMERCIAL,
                                        rst2.getString("DFe_mail") == null ? null : rst2.getString("DFe_mail").toLowerCase()
                                );
                            }
                        }
                    }

                    try (Statement stm3 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst3 = stm3.executeQuery(
                                "select \n"
                                + "	f.DFcod_fornecedor,\n"
                                + "	f.DFcod_plano_pagamento,\n"
                                + "	fp.DFdescricao forma_pagamento\n"
                                + " from TBfornecedor f\n"
                                + "inner join TBplano_pagamento fp \n"
                                + "   on f.DFcod_plano_pagamento = fp.DFcod_plano_pagamento\n"
                                + "where f.DFcod_fornecedor = " + imp.getImportId()
                        )) {
                            while (rst3.next()) {

                                if (rst3.getString("forma_pagamento").contains("/")) {

                                    String condPagamento = rst3.getString("forma_pagamento");
                                    String[] cond = condPagamento.split("/");

                                    for (int i = 0; i < cond.length; i++) {
                                        imp.addCondicaoPagamento(Integer.parseInt(Utils.formataNumero(cond[i])));
                                    }
                                } else if (rst3.getString("forma_pagamento").contains(",")) {

                                    String condPagamento = rst3.getString("forma_pagamento");
                                    String[] cond = condPagamento.split(",");

                                    for (int i = 0; i < cond.length; i++) {
                                        imp.addCondicaoPagamento(Integer.parseInt(Utils.formataNumero(cond[i])));
                                    }
                                } else {
                                    imp.addCondicaoPagamento(Integer.parseInt(Utils.formataNumero(rst3.getString("forma_pagamento"))));
                                }
                            }
                        }
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	pf.DFcod_fornecedor idfornecedor,\n"
                    + "	pf.DFcod_item_estoque idproduto,\n"
                    + "	pf.DFpart_number codigoexterno,\n"
                    + "	max(pu.DFfator_conversao) as qtdembalagem\n"
                    + "from\n"
                    + "	TBfornecedor_item pf\n"
                    + "join TBitem_estoque p on p.DFcod_item_estoque = pf.DFcod_item_estoque\n"
                    + "join TBunidade_item_estoque pu on p.DFcod_item_estoque = pu.DFcod_item_estoque\n"
                    + "group by pf.DFcod_fornecedor, pf.DFcod_item_estoque, pf.DFpart_number "
            /*"select \n"
                    + "	DFcod_fornecedor idfornecedor,\n"
                    + "	DFcod_item_estoque idproduto,\n"
                    + "	DFpart_number codigoexterno\n"
                    + "from\n"
                    + "	TBfornecedor_item\n"
                    + "order by\n"
                    + "	1, 2"*/
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	c.DFcod_cliente id,\n"
                    + "	c.DFnome razao,\n"
                    + "	c.DFnome_fantasia fantasia,\n"
                    + "	c.DFcnpj_cpf cnpj,\n"
                    + "	c.DFfisico_juridico tipo,\n"
                    + "	c.DFinscr_estadual ie,\n"
                    + "	c.DFcarteira_identidade rg,\n"
                    + "	c.DFdata_cadastro data_cadastro,\n"
                    + "	c.DFobservacao obs,\n"
                    + "	c.DFlimite_credito valor_limite,\n"
                    + "	c.DFdata_inativacao inativacao,\n"
                    + "	ce.DFcomplemento_endereco numero,\n"
                    + "	ce.DFponto_referencia referencia,\n"
                    + "	cc.DFcod_cep cep,\n"
                    + "	tl.DFdescricao + ' ' + cl.DFdescricao endereco,\n"
                    + "	cl.DFcomplemento complemento,\n"
                    + " bai.DFdescricao bairro,\n"
                    + "	lo.DFdescricao municipio,\n"
                    + "	lo.DFcod_uf uf,\n"
                    + "	cn.DFe_mail email,\n"
                    + "	cn.DFdata_aniversario data_aniversario,\n"
                    + "	cn.DFcontato contato,\n"
                    + "	sc.DFdescricao setor,\n"
                    + "	cn.DFfax fax,\n"
                    + "	cn.DFtelefone telefone,\n"
                    + "	cn.DFtelefone_celular celular,\n"
                    + " c.DFid_tipo_cliente tipocliente\n"
                    + "from\n"
                    + "	TBcliente c\n"
                    + "left join\n"
                    + "	TBendereco_cliente ce on c.DFcod_cliente = ce.DFcod_cliente\n"
                    + "left join	\n"
                    + "	TBcep_logradouro cc on ce.DFid_cep_logradouro = cc.DFid_cep_logradouro\n"
                    + "left join\n"
                    + "	TBlogradouro cl on cc.DFid_logradouro = cl.DFid_logradouro\n"
                    + "left join\n"
                    + "	TBtipo_logradouro tl on cl.DFcod_tipo_logradouro = tl.DFcod_tipo_logradouro\n"
                    + "left join\n"
                    + "	TBlocalidade lo on cl.DFcod_localidade = lo.DFcod_localidade\n"
                    + "left join\n"
                    + "	TBcontato_cliente cn on c.DFcod_cliente = cn.DFcod_cliente\n"
                    + "left join\n"
                    + "	TBsetor_contato sc on cn.DFid_setor_contato = sc.DFid_setor_contato\n"
                    + "left join\n"
                    + "   TBbairro bai on bai.DFid_bairro = cl.DFid_bairro\n"
                    + "where\n"
                    + "	ce.DFtipo_endereco = 'C'\n"
                    + "order by\n"
                    + "	c.DFcod_cliente"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));

                    if ("F".equals(rs.getString("tipo"))) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rs.getString("ie"));
                    }

                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setValorLimite(rs.getDouble("valor_limite"));

                    if (rs.getDate("inativacao") != null) {
                        imp.setAtivo(false);
                    } else {
                        imp.setAtivo(true);
                    }

                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro").replace("'", ""));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataNascimento(rs.getDate("data_aniversario"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));

                    switch (rs.getInt("tipocliente")) {
                        case 9: // CHEQUE
                            imp.setPermiteCreditoRotativo(false);
                            imp.setPermiteCheque(true);
                            break;
                        case 26: // CONVENIO
                            imp.setPermiteCreditoRotativo(false);
                            imp.setPermiteCheque(false);
                            break;
                        case 31: // FUNCIONARIO
                            imp.setPermiteCreditoRotativo(true);
                            imp.setPermiteCheque(false);
                            break;
                        default:
                            imp.setPermiteCreditoRotativo(true);
                            imp.setPermiteCheque(true);
                            break;
                    }

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n"
                                + "	tbc.DFcod_cliente,\n"
                                + "	tbc.DFcod_bloqueio_cliente,\n"
                                + "	tc.DFdescricao,\n"
                                + "	tc.DFlibera_pedido_venda,\n"
                                + "	tc.DFlibera_venda_boleta,\n"
                                + "	tc.DFlibera_venda_prazo \n"
                                + "from TBcliente_bloqueio_cliente tbc \n"
                                + "inner join TBbloqueio_cliente tc \n"
                                + "  on tc.DFcod_bloqueio_cliente = tbc.DFcod_bloqueio_cliente\n"
                                + "where tbc.DFcod_cliente = " + imp.getId()
                        )) {
                            while (rst2.next()) {
                                if ((rst2.getInt("DFlibera_pedido_venda") > 0)
                                        || (rst2.getInt("DFlibera_venda_boleta") > 0)
                                        || (rst2.getInt("DFlibera_venda_prazo") > 0)) {

                                    imp.setBloqueado(false);
                                    imp.setObservacao2(rst2.getString("DFdescricao"));
                                } else {
                                    imp.setBloqueado(true);
                                    imp.setObservacao2(rst2.getString("DFdescricao"));
                                }
                            }
                        }
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<ItemComboVO> getDocumentoRotativo() throws SQLException {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + " DFcod_tipo_documento id,\n"
                    + " DFdescricao descricao\n"
                    + "from \n"
                    + "	TBtipo_documento")) {
                while (rs.next()) {
                    result.add(new ItemComboVO(rs.getInt("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	tr.DFid_titulo_receber id,\n"
                    + "	tr.DFcod_cliente idcliente,\n"
                    + "	tr.DFcod_tipo_documento tipo_doc,\n"
                    + "	tr.DFnumero_titulo titulo,\n"
                    + "	tr.DFdata_emissao emissao,\n"
                    + "	tr.DFdata_vencimento vencimento,\n"
                    + "	tr.DFvalor valor,\n"
                    + "	tr.DFobservacao obs\n"
                    + "from\n"
                    + "	TBtitulo_receber tr\n"
                    + "where\n"
                    + "	tr.DFcod_empresa = " + getLojaOrigem() + " and\n"
                    + "	tr.DFcod_tipo_documento = " + codigoDocumentoRotativo + " and\n"
                    + "	tr.DFid_titulo_receber not in (select DFid_titulo_receber from TBtitulo_baixado_receber) and\n"
                    + " tr.DFid_titulo_receber not in (select DFid_titulo_cancelado_receber from TBtitulo_cancelado_receber) and\n"
                    + " tr.DFcliente_fornecedor_empresa = 'C'\n"
                    + "order by\n"
                    + "	tr.DFdata_vencimento"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setNumeroCupom(rs.getString("titulo"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ch.DFid_movimento_bancario as id,\n"
                    + "	ch.DFdata_emissao as dataemissao,\n"
                    + "	ch.DFdata_vencimento as datavencimento,\n"
                    + "	ch.DFvalor as valor,\n"
                    + "	tpmb.DFdescricao as descricao_historico,\n"
                    + "	ch.DFnumero_documento as DFnumero_documento,\n"
                    + "	ch.DFnominal as nome,\n"
                    + "	ch.DFobservacao as observacao,\n"
                    + "	ch.DFid_conta,\n"
                    + "	chc.DFdata_devolucao,\n"
                    + "	chc.DFdata_compensacao,\n"
                    + "	chc.DFcod_alinea_cheque as alinea,\n"
                    + "	icmbc.DFbanda_magnetica,\n"
                    + "	cli.DFcod_cliente,\n"
                    + "	cli.DFnome,\n"
                    + "	age.DFnumero as numero_agencia,\n"
                    + "	age.DFdigito_verificador as digito_numero_agencia,\n"
                    + "	ban.DFcod_banco as idbanco,\n"
                    + "	ech.DFtelefone as telefone,\n"
                    + "	ech.DFcnpj_cpf as cpf_cnpj,\n"
                    + "	ech.DFnome_emitente,\n"
                    + "	ech.DFnumero_conta_banda_magnetica,\n"
                    + "	ech.DFnumero_conta\n"
                    + "from TBmovimento_bancario ch\n"
                    + "left join TBcheque_compensado chc on ch.DFid_movimento_bancario = chc.DFid_movimento_bancario \n"
                    + "	and chc.DFdata_compensacao in (select max(DFdata_compensacao) from TBcheque_compensado where DFid_movimento_bancario = ch.DFid_movimento_bancario)\n"
                    + "left join TBinformacao_comp_movto_bancario_credito icmbc on ch.DFid_movimento_bancario = icmbc.DFid_informacao_comp_movto_bancario_credito \n"
                    + "left join TBcliente cli on icmbc.DFcod_cliente = cli.DFcod_cliente \n"
                    + "left join TBemitente_cheque ech on icmbc.DFid_emitente_cheque = ech.DFid_emitente_cheque \n"
                    + "left join TBagencia age on ech.DFid_agencia = age.DFid_agencia\n"
                    + "left join TBbanco ban on age.DFcod_banco = ban.DFcod_banco \n"
                    + "left join TBhistorico_padrao_movto_bancario tpmb on ch.DFcod_historico_movto_bancario = tpmb.DFcod_historico_movto_bancario\n"
                    + "where ch.DFcod_tipo_documento in (2)\n"
                    + "and tpmb.DFnatureza  = 'C'\n"
                    + "and tpmb.DFcod_historico_movto_bancario in (1, 7, 37, 44, 45)\n"
                    + "and ch.DFid_movimento_bancario not in (select DFid_movimento_bancario from TBbaixado_receber_movto_bancario)\n"
                    + "and ch.DFid_movimento_bancario not in (select DFid_movimento_bancario from TBbaixado_pagar_movto_bancario)\n"
                    + "and ch.DFid_movimento_bancario not in (select DFid_movimento_bancario_cancelado from TBmovimento_bancario_cancelado)\n"
                    + "and ch.DFcod_empresa = " + getLojaOrigem() + "\n"
                    + "and ch.DFdata_emissao in (select max(DFdata_emissao) \n"
                    + "                               from TBmovimento_bancario\n"
                    + "                              where DFvalor = ch.DFvalor\n"
                    + "                                and DFnumero_documento = ch.DFnumero_documento\n"
                    + "                                and DFdata_vencimento = ch.DFdata_vencimento)\n"
                    + "and ch.DFid_conta in (12, 13)"
            /*"select\n"
                    + "	tr.DFid_titulo_receber id,\n"
                    + "	tr.DFcod_cliente idcliente,\n"
                    + "	tr.DFcod_tipo_documento tipo_doc,\n"
                    + "	tr.DFnumero_titulo titulo,\n"
                    + "	tr.DFdata_emissao emissao,\n"
                    + "	tr.DFdata_vencimento vencimento,\n"
                    + "	tr.DFvalor valor,\n"
                    + "	tr.DFobservacao obs\n"
                    + "from\n"
                    + "	TBtitulo_receber tr\n"
                    + "where\n"
                    + "	tr.DFcod_empresa = " + getLojaOrigem() + " and\n"
                    + "	tr.DFcod_tipo_documento = " + codigoDocumentoCheque + " and\n"
                    + "	tr.DFid_titulo_receber not in (select DFid_titulo_receber from TBtitulo_baixado_receber) and\n"
                    + "   tr.DFcliente_fornecedor_empresa = 'C'\n"
                    + "order by\n"
                    + "	tr.DFdata_vencimento"*/
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("Codigo"));
                    imp.setNome(rst.getString("Nome_Cliente"));
                    imp.setCpf(rst.getString("CPF"));
                    imp.setValor(rst.getDouble("Valor_Cheque"));
                    imp.setNumeroCheque(rst.getString("Cheque"));
                    imp.setBanco(rst.getInt("Banco"));
                    imp.setAgencia(rst.getString("Agencia"));
                    imp.setConta(rst.getString("Conta"));
                    imp.setNumeroCupom(rst.getString("Venda"));
                    imp.setObservacao(rst.getString("Observacao"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("Data_Cadastro"));
                    imp.setAlinea(0);

                    result.add(imp);
                }
            }
        }
        return result;
    }

}
