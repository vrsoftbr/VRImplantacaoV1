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
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class CervantesDAO extends InterfaceDAO implements MapaTributoProvider {

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	id_dados_empresa id,\n" +
                    "	nome_fantasia fantasia \n" +
                    "from \n" +
                    "	dados_empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("id"), 
                            rst.getString("fantasia")));
                }
            }
        }

        return result;
    }

    @Override
    public String getSistema() {
        return "Cervantes";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.OFERTA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.CEST,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MARGEM,
            OpcaoProduto.QTD_EMBALAGEM_EAN,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.NCM,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.DESCONTINUADO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.RECEITA,
            OpcaoProduto.SECAO,
            OpcaoProduto.PRATELEIRA,
            OpcaoProduto.OFERTA,
            OpcaoProduto.FABRICANTE
        }));
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {

                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("prun_prod_codigo"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("prun_emb"));
                    imp.setQtdEmbalagem(rst.getInt("prun_fatorpr3"));

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
                    ""
            )) {
                while (rst.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rst.getString("id_tributacao"),
                                    rst.getString("descricao"),
                                    rst.getInt("cst"),
                                    rst.getDouble("aliquota"),
                                    rst.getDouble("reducao")
                            )
                    );
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
                    "select \n" +
                    "	p.id_produto,\n" +
                    "	p.produto_codigo,\n" +
                    "	p.produto_balanca,\n" +
                    "	p.descricao,\n" +
                    "	p.descricao_detalhada,\n" +
                    "	p.descricao_resumida,\n" +
                    "	ean.codigo_barra,\n" +
                    "	p.cean ean,\n" +
                    "	p.cean_trib eantrib,\n" +
                    "	ps.id_prod_grupo merc1,\n" +
                    "	p.id_prod_subgrupo merc2,\n" +
                    "	un.descricao unidade,\n" +
                    "	p.inativo,\n" +
                    "	p.qtd_estoque,\n" +
                    "	p.qtd_estoque_minimo,\n" +
                    "	p.cod_ncm ncm,\n" +
                    "	p.cest,\n" +
                    "	p.preco_custo,\n" +
                    "	p.preco_venda,\n" +
                    "	p.sit_trib_pis pisdebito,\n" +
                    "	p.sit_trib_pis_entrada piscredito,\n" +
                    "	p.sit_trib_cofins cofinsdebito,\n" +
                    "	p.sit_trib_cofins_entrada cofinscredito,\n" +
                    "	p.aliquota_icms,\n" +
                    "	p.aliquota_icms_entrada,\n" +
                    "	p.sit_trib_icms,\n" +
                    "	p.sit_trib_icms_entrada,\n" +
                    "	p.sit_trib_icms_simples,\n" +
                    "	p.sit_trib_icms_simples_entrada \n" +
                    "from \n" +
                    "	produto p\n" +
                    "left join prod_unidade un on p.id_prod_unidade = un.id_prod_unidade\n" +
                    "left join prod_cod_barras ean on p.id_produto = ean.id_produto\n" +
                    "left join prod_subgrupo ps on p.id_prod_subgrupo = ps.id_prod_subgrupo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));

                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("embalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaoreduzida"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));

                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getBoolean("situacaocadastro") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_s"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natrec"));

                    imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	f.id_fornecedor,\n" +
                    "	f.fornecedor_codigo,\n" +
                    "	f.cpf_cnpj,\n" +
                    "	f.insc_estadual,\n" +
                    "	f.inativo,\n" +
                    "	f.razao_social,\n" +
                    "	f.nome_fantasia,\n" +
                    "	f.logradouro,\n" +
                    "	f.bairro,\n" +
                    "	c.descricao cidade,\n" +
                    "	c.sigla_estado uf,\n" +
                    "	f.cep,\n" +
                    "	f.numero,\n" +
                    "	f.complemento,\n" +
                    "	f.obs,\n" +
                    "	f.email,\n" +
                    "	f.fone,\n" +
                    "	f.contato,\n" +
                    "	f.suframa\n" +
                    "from \n" +
                    "	fornecedor f\n" +
                    "left join cidade c using (id_cidade)"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("fornecedor_codigo"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpjcpf"));
                    imp.setIe_rg(rst.getString("ierg"));
                    imp.setInsc_municipal(rst.getString("inscmun"));
                    imp.setAtivo(!"S".equals(rst.getString("forn_status")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("municipioIBGE"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("forn_fone"));
                    imp.addTelefone("TEL INDUSTRIA", rst.getString("forn_foneindustria"));
                    imp.addTelefone("FAX", rst.getString("forn_fax"));
                    imp.addTelefone("FAX INDUSTRIA", rst.getString("forn_faxindustria"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("forn_obspedidos") + " " + rst.getString("forn_obstrocas"));
                   
                    switch (rst.getString("tipofornecedor").trim()) {
                        case "A":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "D":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "E":
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "P":
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "R":
                            imp.setProdutorRural();
                            break;
                        case "S":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                            break;
                        case "F":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
                        case "O":
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            imp.setTipoEmpresa(TipoEmpresa.SOCIEDADE_CIVIL);
                            break;
                        default:
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                            break;
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	p.produto_codigo idproduto,\n" +
                    "	f.fornecedor_codigo idfornecedor,\n" +
                    "	pf.codigo codigoexterno,\n" +
                    "	pf.unidade,\n" +
                    "	pf.qtd_produto qtd\n" +
                    "from \n" +
                    "	produto_fornecedor pf \n" +
                    "join produto p on pf.id_produto = p.id_produto\n" +
                    "join fornecedor f on pf.id_fornecedor = f.id_fornecedor\n" +
                    "order by \n" +
                    "	2, 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qemb"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
