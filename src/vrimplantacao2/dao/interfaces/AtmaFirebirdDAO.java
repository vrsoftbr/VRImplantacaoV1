package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class AtmaFirebirdDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "Atma(Firebird)" + ("".equals(complemento) ? "" : " - " + complemento);
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT distinct\n" +
                        "	p.CST icms_cst,\n" +
                        "	p.ALIQUOTA icms_aliquota,\n" +
                        "	COALESCE(p.REDUCAO, 0) icms_reducao\n" +
                        "FROM\n" +
                        "	C000025 p\n" +
                        "ORDER BY\n" +
                        "	icms_cst, icms_aliquota, icms_reducao"
                )
                ) {
            while (rs.next()) {
                final String idIcms = formataIdTributacao(
                        rs.getString("icms_cst"),
                        rs.getDouble("icms_aliquota"),
                        rs.getDouble("icms_reducao")
                );
                result.add(new MapaTributoIMP(
                        idIcms,
                        idIcms,
                        Utils.stringToInt(rs.getString("icms_cst")),
                        rs.getDouble("icms_aliquota"),
                        rs.getDouble("icms_reducao")
                ));
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT\n" +
                        "	c.CODIGO id,\n" +
                        "	c.FILIAL razao,\n" +
                        "	c.CNPJ\n" +
                        "FROM\n" +
                        "	C000004 c\n" +
                        "ORDER BY\n" +
                        "	id"
                )
        ) {
            while (rs.next()) {
                result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao") + " - " + rs.getString("cnpj")));
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT\n" +
                        "	m1.CODIGO merc1,\n" +
                        "	m1.GRUPO merc1_desc,\n" +
                        "	m2.CODIGO merc2,\n" +
                        "	m2.SUBGRUPO merc2_desc\n" +
                        "FROM\n" +
                        "	C000017 m1\n" +
                        "	LEFT JOIN C000018 m2 ON\n" +
                        "		m1.CODIGO = m2.CODGRUPO \n" +
                        "ORDER BY\n" +
                        "	merc1, merc2"
                )
        ) {
            while (rs.next()) {
                MercadologicoIMP imp = new MercadologicoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setMerc1ID(rs.getString("merc1"));
                imp.setMerc1Descricao(rs.getString("merc1_desc"));
                imp.setMerc2ID(rs.getString("merc2"));
                imp.setMerc2Descricao(rs.getString("merc2_desc"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT\n" +
                        "	p.CODIGO id,\n" +
                        "	p.DATA_CADASTRO datacadastro,\n" +
                        "	p.CODBARRA ean,\n" +
                        "	p.QTDE_EMBALAGEM qtdembalagem,\n" +
                        "	p.UNIDADE unidade,\n" +
                        "	CASE p.USA_BALANCA\n" +
                        "		WHEN 1 THEN 1\n" +
                        "		ELSE 0\n" +
                        "	END pesavel,\n" +
                        "	p.VALIDADE,\n" +
                        "	p.PRODUTO descricaocompleta,\n" +
                        "	p.CODGRUPO merc1,\n" +
                        "	p.CODSUBGRUPO merc2,\n" +
                        "	p.PESO pesobruto,\n" +
                        "	p.PESO_LIQUIDO pesoliquido,\n" +
                        "	p.ESTOQUEMINIMO,\n" +
                        "	p.ESTOQUE,\n" +
                        "	p.FLAG_EST,\n" +
                        "	p.PMARGEM1 margem,\n" +
                        "	p.PRECOCUSTO custocomimposto,\n" +
                        "	p.CUSTOMEDIO,\n" +
                        "	p.PRECOCUSTO_ANTERIOR CUSTOCOMIMPOSTOanterior,\n" +
                        "	p.PRECOVENDA precovenda,\n" +
                        "	p.SITUACAO,\n" +
                        "	p.CLASSIFICACAO_FISCAL ncm,\n" +
                        "	p.\"CEST\" cest,\n" +
                        "	pc.PIS pisconfins_s,\n" +
                        "	pc.PIS_ENTRADA piscofins_e,\n" +
                        "	--natureza da receita,\n" +
                        "	p.CODALIQUOTA,\n" +
                        "	p.CST icms_cst,\n" +
                        "	p.ALIQUOTA icms_aliquota,\n" +
                        "	COALESCE(p.REDUCAO, 0) icms_reducao,\n" +
                        "	p.PRECOATACADO1 atacado,\n" +
                        "	p.QTDE_EMBALAGEMATACADO QTDEMBALAGEMatacado,\n" +
                        "	p.CODFORNECEDOR fabricante,\n" +
                        "	CODIGO_ANP\n" +
                        "FROM\n" +
                        "	C000025 p\n" +
                        "	LEFT JOIN C000026 pc ON\n" +
                        "		pc.CODPRODUTO = p.CODIGO\n" +
                        "ORDER BY\n" +
                        "	p.CODIGO"
                )
        ) {
            while (rs.next()) {
                ProdutoIMP imp = new ProdutoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setDataCadastro(rs.getDate("datacadastro"));
                imp.setEan(rs.getString("ean"));
                imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                imp.setTipoEmbalagem(rs.getString("unidade"));
                imp.seteBalanca(rs.getBoolean("pesavel"));
                imp.setValidade(rs.getInt("VALIDADE"));
                imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                imp.setCodMercadologico1(rs.getString("merc1"));
                imp.setCodMercadologico2(rs.getString("merc2"));
                imp.setPesoBruto(rs.getDouble("pesobruto"));
                imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                imp.setEstoqueMinimo(rs.getDouble("ESTOQUEMINIMO"));
                imp.setEstoque(rs.getDouble("ESTOQUE"));
                imp.setMargem(rs.getDouble("margem"));
                imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                imp.setCustoSemImposto(rs.getDouble("custocomimposto"));
                imp.setCustoMedio(rs.getDouble("CUSTOMEDIO"));
                imp.setCustoAnteriorComImposto(rs.getDouble("CUSTOCOMIMPOSTOanterior"));
                imp.setCustoAnteriorSemImposto(rs.getDouble("CUSTOCOMIMPOSTOanterior"));
                imp.setPrecovenda(rs.getDouble("precovenda"));
                imp.setNcm(rs.getString("ncm"));
                imp.setCest(rs.getString("cest"));
                imp.setPiscofinsCstDebito(rs.getString("pisconfins_s"));
                imp.setPiscofinsCstCredito(rs.getString("piscofins_e"));
                
                String icmsId = formataIdTributacao(
                        rs.getString("icms_cst"),
                        rs.getDouble("icms_aliquota"),
                        rs.getDouble("icms_reducao")
                );
                
                imp.setIcmsDebitoId(icmsId);
                imp.setIcmsDebitoForaEstadoId(icmsId);
                imp.setIcmsDebitoForaEstadoNfId(icmsId);
                imp.setIcmsCreditoId(icmsId);
                imp.setIcmsCreditoForaEstadoId(icmsId);
                
                imp.setFornecedorFabricante(rs.getString("fabricante"));
                imp.setCodigoAnp(rs.getString("CODIGO_ANP"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.ICMS,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.CUSTO_ANTERIOR,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.CODIGO_ANP
        ));
    }

    private String formataIdTributacao(String cst, double aliquota, double reducao) {
        return String.format("%s-%.2f-%.2f", cst, aliquota, reducao);
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	f.codigo id,\n" +
                        "	f.nome razao,\n" +
                        "	f.fantasia,\n" +
                        "	f.cnpj,\n" +
                        "	f.ie,\n" +
                        "	f.im,\n" +
                        "	f.endereco,\n" +
                        "	f.numero,\n" +
                        "	f.complemento,\n" +
                        "	f.bairro,\n" +
                        "	f.cod_municipio_ibge,\n" +
                        "	f.cidade,\n" +
                        "	f.uf,\n" +
                        "	f.cep,\n" +
                        "	f.contato1,\n" +
                        "	f.telefone1,\n" +
                        "	f.celular1,\n" +
                        "	f.contato2,\n" +
                        "	f.telefone2,\n" +
                        "	f.celular2,\n" +
                        "	f.fax,\n" +
                        "	f.rep_nome,\n" +
                        "	f.data,\n" +
                        "	f.obs1,\n" +
                        "	f.obs2,\n" +
                        "	f.obs3,\n" +
                        "	f.banco,\n" +
                        "	f.agencia,\n" +
                        "	f.conta,\n" +
                        "	f.email\n" +
                        "from\n" +
                        "	c000009 f\n" +
                        "order by\n" +
                        "	f.codigo"
                )
                ) {
            while (rs.next()) {
                FornecedorIMP imp = new FornecedorIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setCnpj_cpf(rs.getString("cnpj"));
                imp.setIe_rg(rs.getString("ie"));
                imp.setInsc_municipal(rs.getString("im"));
                imp.setEndereco(rs.getString("endereco"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setIbge_municipio(rs.getInt("cod_municipio_ibge"));
                imp.setMunicipio(rs.getString("cidade"));
                imp.setUf(rs.getString("uf"));
                imp.setCep(rs.getString("cep"));
                imp.setTel_principal(rs.getString("telefone1"));
                imp.addContato(
                        rs.getString("contato1"),
                        rs.getString("telefone1"),
                        rs.getString("celular1"),
                        TipoContato.COMERCIAL,
                        rs.getString("email")
                );
                imp.addContato(
                        rs.getString("contato2"),
                        rs.getString("telefone2"),
                        rs.getString("celular2"),
                        TipoContato.COMERCIAL,
                        ""
                );
                imp.addTelefone("FAX", rs.getString("fax"));
                
                imp.setDatacadastro(rs.getDate("data"));
                imp.setObservacao(
                        String.format(
                                "(OBS1) %s (OBS2) %s (OBS3) %s",
                                rs.getString("obs1"),
                                rs.getString("obs2"),
                                rs.getString("obs3")
                        )
                );
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
                
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	p.codigo id_produto,\n" +
                        "	p.codfornecedor id_fornecedor,\n" +
                        "	p.referencia_fornecedor codigoexterno,\n" +
                        "	p.precocusto custotabela\n" +
                        "from\n" +
                        "	c000025 p\n" +
                        "where\n" +
                        "	not p.codfornecedor is null and\n" +
                        "	not p.referencia_fornecedor is null\n" +
                        "union\n" +
                        "select\n" +
                        "	fc.codproduto id_produto,\n" +
                        "	fc.codfornecedor id_fornecedor,\n" +
                        "	fc.codigo codigoexterno,\n" +
                        "	fc.preco custotabela\n" +
                        "from\n" +
                        "	fornecedor_codigo fc"
                )
                ) {
            while (rs.next()) {
                ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setIdFornecedor(rs.getString("id_fornecedor"));
                imp.setIdProduto(rs.getString("id_produto"));
                imp.setCodigoExterno(rs.getString("codigoexterno"));
                imp.setCustoTabela(rs.getDouble("custotabela"));
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
}
