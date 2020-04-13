package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class NCADAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "NCA";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codtribut id,\n"
                    + "	obstribut,\n"
                    + "	aliqicms,\n"
                    + "	cst,\n"
                    + "	tipo\n"
                    + "from \n"
                    + "	nca_tributo_pdv")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("obstribut"),
                            rs.getInt("cst"),
                            rs.getDouble("aliqicms"),
                            0));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cod_empresa id,\n"
                    + "	fantasia \n"
                    + "from \n"
                    + "	nca_filial\n"
                    + "order by \n"
                    + "	1")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cod_grupo_preco id,\n"
                    + "	desc_grupo_preco descricao\n"
                    + "from\n"
                    + "	nca_grupo_preco\n"
                    + "order by \n"
                    + "	2")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	p.cod_produto id,\n"
                    + "	p.desc_produto descricaocompleta,\n"
                    + "	ean.cod_barras codigobarras,\n"
                    + "	un.sigla_unidade unidade,\n"
                    + "	e.custo_real custocomimposto,\n"
                    + "	e.preco_venda,\n"
                    + "	e.qt_estoque,\n"
                    + "	e.qt_estoque_fisico,\n"
                    + "	p.qt_unit_embalagem qtdembalagem,\n"
                    + "	p.cod_categoria merc1,\n"
                    + "	p.cod_secao merc2,\n"
                    + "	p.cod_departamento merc3,\n"
                    + "	p.cod_sub_categoria merc4,\n"
                    + "	p.cod_ncm ncm,\n"
                    + "	p.cest,\n"
                    + "	p.dt_cadastro cadastro,\n"
                    + "	p.peso_bruto,\n"
                    + "	p.peso_liquido,\n"
                    + "	p.cod_tribut_pdv idaliquota,\n"
                    + "	p.situacao,\n"
                    + "	p.enviar_para_balanca pesavel,\n"
                    + "	p.confere_peso_balanca,\n"
                    + "	p.cod_grupo_preco idfamilia,\n"
                    + "	pis_cre.cst_pis pis_credito,\n"
                    + "	pis_deb.cst_pis pis_debito,\n"
                    + "	cofins_cre.cst_cofins cofins_credito,\n"
                    + "	cofins_deb.cst_cofins cofins_debito\n"
                    + "from \n"
                    + "	nca_produto p\n"
                    + "join nca_estoque e on p.cod_produto = e.cod_produto \n"
                    + "left join nca_unidade un on p.cod_unidade = un.cod_unidade\n"
                    + "left join nca_cst_pis pis_cre on p.cod_cst_pis_ent = pis_cre.cod_cst_pis\n"
                    + "left join nca_cst_cofins cofins_cre on p.cod_cst_cofins_ent = cofins_cre.cod_cst_cofins\n"
                    + "left join nca_cst_pis pis_deb on p.cod_cst_pis_sai = pis_deb.cod_cst_pis\n"
                    + "left join nca_cst_cofins cofins_deb on p.cod_cst_cofins_sai = cofins_deb.cod_cst_cofins\n"
                    + "left join nca_codigo_barras ean on p.cod_produto = ean.cod_produto\n"
                    + "where 		\n"
                    + "	e.cod_empresa = " + getLojaOrigem() + "\n"
                    + "order by \n"
                    + "	p.cod_produto")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoGondola());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rs.getDouble("preco_venda"));
                    imp.setEstoque(rs.getDouble("qt_estoque_fisico"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setSituacaoCadastro("A".equals(rs.getString("situacao")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    if ("S".equals(rs.getString("balanca"))) {
                        imp.seteBalanca(true);
                    }

                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));
                    imp.setPiscofinsCstCredito(rs.getString("pis_credito"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_debito"));

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
                    "select \n"
                    + "	cod_produto idproduto,\n"
                    + "	cod_parceiro idfornecedor,\n"
                    + "	codigo_nf codigoexterno,\n"
                    + "	fator_conversao qtdembalagem\n"
                    + "from \n"
                    + "	nca_produto_fornecedor ")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	np.cod_parceiro id,\n"
                    + "	np.razao_social razao,\n"
                    + "	np.fantasia,\n"
                    + "	np.cpf_cnpj,\n"
                    + "	np.insc_estadual ie,\n"
                    + "	np.endereco,\n"
                    + "	np.numero,\n"
                    + "	np.logradouro,\n"
                    + "	np.bairro,\n"
                    + "	np.cep,\n"
                    + "	np.complemento,\n"
                    + "	np.sigla estado,\n"
                    + "	ci.cod_ibge ibge_cidade,\n"
                    + "	ci.cidade,\n"
                    + "	np.ponto_ref_ender referencia,\n"
                    + "	np.dt_cadastro,\n"
                    + "	np.dt_nascimento,\n"
                    + "	np.contato,\n"
                    + "	np.telefone,\n"
                    + "	np.celular,\n"
                    + "	np.fax,\n"
                    + "	np.email,\n"
                    + "	np.email_xml,\n"
                    + "	np.observacao,\n"
                    + "	np.sexo,\n"
                    + "	np.limite,\n"
                    + "	np.logradouro_cob,\n"
                    + "	np.bairro_cob,\n"
                    + "	np.numero_cob,\n"
                    + "	np.complemento_cob,\n"
                    + "	np.sigla_cob estado_cob,\n"
                    + "	ci_cob.cidade cidade_cob,\n"
                    + "	np.cep_cob,\n"
                    + "	np.telefone_cob,\n"
                    + "	np.inf_pes_nome1 inforpessoal,\n"
                    + "	np.inf_pes_grau1 inforgrau,\n"
                    + "	np.inf_pes_fone1 inforfone\n"
                    + "from \n"
                    + " 	nca_parceiro np\n"
                    + " left join nca_cidade ci on np.cod_cidade = ci.cod_cidade\n"
                    + " left join nca_cidade ci_cob on np.cod_cidade_cob = ci_cob.cod_cidade \n"
                    + "where \n"
                    + "	np.tipo_parceiro = 1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
