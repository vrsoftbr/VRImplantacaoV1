/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Wagner
 */
public class MRC6DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "MRC6";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigo,\n"
                    + "	nome as razao,\n"
                    + "	cnpj\n"
                    + "from\n"
                    + "	entidades;"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cnpj"), rst.getString("razao")));
                }
            }
        }

        return result;
    }

    public List<String> getNomeLojaCliente() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigo,\n"
                    + "	nomefantasia as razao,\n"
                    + "	cnpj\n"
                    + "from\n"
                    + "	entidades;"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("razao"));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	ClaFisID as id,\n"
                    + "	ClaFisDescricao as descricao,\n"
                    + "	ClaFisIcmsAliquota as aliquota,\n"
                    + "	ClaFisIcmsReducao as reducao\n"
                    + "from dbo.TB_CLASSIFICACAO_FISCAL\n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            0,
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n"
                    + " prod.Familiaid as codmerc1,\n"
                    + " merc1.descricao as descmerc1,\n"
                    + " prod.Grupoid as codmerc2,\n"
                    + " merc2.descricao as descmerc2,\n"
                    + " prod.Subgrupoid as codmerc3,\n"
                    + " merc3.descricao as descmerc3\n"
                    + "from produtos prod\n"
                    + "join produtosgrupos merc1 on merc1.codigo = prod.Familiaid\n"
                    + "join produtosgrupos merc2 on merc2.codigo = prod.Grupoid\n"
                    + "join produtosgrupos merc3 on merc3.codigo = prod.Subgrupoid\n"
                    + "order by 1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("codmerc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("codmerc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("codmerc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));
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
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ProdCodInterno"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpissaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpisentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("tribicms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("tribicms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("tribicms"));
                    imp.setIcmsCreditoId(rst.getString("tribicms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("tribicms"));
                    imp.setIcmsConsumidorId(rst.getString("tribicms"));
                    imp.setVolume(rst.getDouble("volume"));

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
                    "select * from (select\n"
                    + "	p.prodid id,\n"
                    + "	p.prodcodbarras1 ean,\n"
                    + "	un.UnSigla as unidade\n"
                    + "from\n"
                    + "	tb_produto p\n"
                    + "left join dbo.TB_UNIDADE_MEDIDA un on\n"
                    + "	un.UnID = p.ProdUnidadeMedidaID\n"
                    + "where\n"
                    + "	ltrim(rtrim(coalesce(p.prodcodbarras1, ''))) != ''\n"
                    + "union all \n"
                    + "select\n"
                    + "	p.ProdID as id,\n"
                    + "	p.ProdCodBarras2 as ean,\n"
                    + "	un.UnSigla as unidade\n"
                    + "from\n"
                    + "	dbo.TB_PRODUTO p\n"
                    + "left join dbo.TB_UNIDADE_MEDIDA un on\n"
                    + "	un.UnID = p.ProdUnidadeMedidaID\n"
                    + "union all \n"
                    + "select\n"
                    + "	p.ProdID as id,\n"
                    + "	p.ProdCodBarras3 as ean,\n"
                    + "	un.UnSigla as unidade\n"
                    + "from\n"
                    + "	dbo.TB_PRODUTO p\n"
                    + "left join dbo.TB_UNIDADE_MEDIDA un on\n"
                    + "	un.UnID = p.ProdUnidadeMedidaID\n"
                    + "union all \n"
                    + "select\n"
                    + "	p.ProdID as id,\n"
                    + "	p.ProdEan14 as ean,\n"
                    + "	un.UnSigla as unidade\n"
                    + "from\n"
                    + "	dbo.TB_PRODUTO p\n"
                    + "left join dbo.TB_UNIDADE_MEDIDA un on\n"
                    + "	un.UnID = p.ProdUnidadeMedidaID) ea \n"
                    + "where \n"
                    + "	ea.ean is not null"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.codigo as id,\n"
                    + "	f.nome as razao,\n"
                    + "	f.nomefantasia as fantasia,\n"
                    + "	f.cnpj,\n"
                    + "	f.inscrest as ie_rg,\n"
                    + "	f.inscrmunicipal,\n"
                    + "	f.endereco,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	c.cidade,\n"
                    + "	f.desativado as status,\n"
                    + "	c.codigoibge,\n"
                    + "	f.cep,\n"
                    + "	c.estado,\n"
                    + "	f.telefone1,\n"
                    + "	f.telefone2,\n"
                    + "	f.dtcadastro,\n"
                    + "	f.email\n"
                    + "from\n"
                    + "	fornecedores f\n"
                    + "join cidades c on c.codigo = f.cidadeID\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setAtivo(rst.getBoolean("status"));
                    imp.setIbge_municipio(rst.getInt("codigoibge"));
                    imp.setCep(rst.getString("cep"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal(rst.getString("telefone1"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addTelefone("TELEFONE 2", rst.getString("telefone2"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.fornecedorID as idfornecedor,\n"
                    + "	pf.produtoID as idproduto,\n"
                    + "	p.referencia as codigoexterno\n"
                    + " from\n"
                    + "	produtosfornecedores pf\n"
                    + " join produtos p on p.codigo = pf.produtoID\n"
                    + " join fornecedores f on f.codigo = pf.fornecedorID"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	a.codigo as id,\n"
                    + "	b.descricao,\n"
                    + "	a.cnpj,\n"
                    + "	a.inscrest as ie_rg,\n"
                    + "	a.nome as razao,\n"
                    + "	a.nomefantasia as fantasia,\n"
                    + "	case\n"
                    + "		when a.ativoinativo = 'A' then 1\n"
                    + "		else 0\n"
                    + "	end as status,\n"
                    + "	a.dtativoinativo,\n"
                    + "	a.endereco,\n"
                    + "	a.complemento,\n"
                    + "	a.bairro,\n"
                    + "	c.codigoibge,\n"
                    + "	c.estado as uf,\n"
                    + "	c.cidade,\n"
                    + "	a.cep,\n"
                    + "	a.estadocivil,\n"
                    + "	a.dtcadastro,\n"
                    + "	a.sexo,\n"
                    + "	a.limitecredito,\n"
                    + "	a.naoliberarcredito,\n"
                    + "	a.telefone1,\n"
                    + "	a.telefone2,\n"
                    + "	a.celular,\n"
                    + "	a.email,\n"
                    + "	a.fax,\n"
                    + "	a.enderecoc,\n"
                    + "	a.complementoc,\n"
                    + "	a.bairroc,\n"
                    + "	d.codigoibge,\n"
                    + "	d.estado as c_uf,\n"
                    + "	d.cidade as c_cidade,\n"
                    + "	a.cepc\n"
                    + "from\n"
                    + "	clientes a\n"
                    + "join clientesgrupos b on b.codigo = a.grupoID\n"
                    + "left join cidades c on c.codigo = a.cidadeid\n"
                    + "left join cidades d on d.codigo = a.cidadecID;"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setMunicipioIBGE(rst.getInt("codigoibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setAtivo(rst.getBoolean("status"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("email"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setValorLimite(rst.getDouble("limitecredito"));
                    imp.setPermiteCreditoRotativo(rst.getBoolean("naoliberarcredito"));

                    imp.setCobrancaBairro(rst.getString("bairroc"));
                    imp.setCobrancaCep(rst.getString("cepc"));
                    imp.setEndereco(rst.getString("enderecoc"));
                    imp.setCobrancaMunicipio(rst.getString("c_cidade"));
                    imp.setCobrancaUf(rst.getString("c_uf"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("codigoibge"));
                    imp.setCobrancaComplemento(rst.getString("complementoc"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addTelefone("TELEFONE 2", rst.getString("telefone2"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  dupliad.duplicatainfoadicionalID as id,\n"
                    + "  dup.duplicata as numerodocumento,\n"
                    + "  dupliad.valor as valor,\n"
                    + "  dup.clientefornecedorID as fornecedorid,\n"
                    + "  dupliad.dt\n"
                    + "  dup.dtcadastro as emissao,\n"
                    + "  dup.dtvencimento as vencimento,\n"
                    + "  dup.descricao as obs,\n"
                    + "  dup.numerodocumento\n"
                    + " from duplicatasinfoadicionais dupliad\n"
                    + " join duplicatas dup on dup.codigo = dupliad.codigoID \n"
                    + " where \n"
                    + "  dup.flagclientefornecedor = 'F'\n"
                    + " and\n"
                    + "  dup.dtbaixa is null \n"
                    + " and\n"
                    + "  dupliad.dtefetivopagamento is null"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setVencimento(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  dupliad.duplicatainfoadicionalID as id,\n"
                    + "  dup.duplicata as numerocupom,\n"
                    + "  dup.computername as ecf,\n"
                    + "  dup.parcela as parcela,\n"
                    + "  dupliad.valor as valor,\n"
                    + "  dup.clientefornecedorID as clienteid,\n"
                    + "  dup.dtcadastro as emissao,\n"
                    + "  dup.dtvencimento as vencimento,\n"
                    + "  dup.descricao as obs,\n"
                    + "  dup.numerodocumento\n"
                    + " from duplicatasinfoadicionais dupliad\n"
                    + " join duplicatas dup on dup.codigo = dupliad.codigoID \n"
                    + " where \n"
                    + " dup.flagclientefornecedor = 'C'\n"
                    + " and\n"
                    + " dup.dtbaixa is null \n"
                    + " and\n"
                    + " dupliad.dtefetivopagamento is null"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new MRC6DAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new MRC6DAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        //next.setCancelado(rst.getBoolean("cancelado"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setNomeCliente(rst.getString("nome_cliente"));
                        next.setCpf(rst.getString("cpf_cnpj"));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        String horaInicio = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	v.VndNumeroVenda id_venda,\n"
                    + "	d.VndDocNumero numerocupom,\n"
                    + "	VndClienteID id_cliente,\n"
                    + "	c.PessoaNome nome_cliente,\n"
                    + "	v.VndNfpCpfCnpj cpf_cnpj,\n"
                    + "	SUBSTRING(e.EstacaoDescricao, 4, 2) ecf,\n"
                    + "	v.VndDtEmissao emissao,\n"
                    + "	CAST (VndDtAbertura as time) horainicio,\n"
                    + "	CAST (VndDtFechamento as time) horatermino,\n"
                    + "	CASE\n"
                    + "	  when v.VndClienteValor = 0\n"
                    + "   then v.VndConvenioValor\n"
                    + "	  ELSE v.VndClienteValor\n"
                    + "	END subtotalimpressora\n"
                    + "FROM\n"
                    + "	TB_VENDA v\n"
                    + "LEFT JOIN TB_VENDA_DOCUMENTO d on d.VndDocID = v.VndID\n"
                    + "LEFT JOIN TB_ESTACAO e on e.EstacaoID = v.VndEstacaoID\n"
                    + "LEFT JOIN TB_PESSOA_PFPJ c on c.PessoaID = v.VndClienteID\n"
                    + "WHERE\n"
                    + " d.VndDocNumero is not NULL \n"
                    + "	and v.VndDtEmissao between '" + strDataInicio + "' and '" + strDataTermino + "'";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nro_item"));
                        next.setProduto(rst.getString("produto"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	v.VndNumeroVenda id_venda,\n"
                    + "	vi.DocBaseItemID id_item,\n"
                    + "	vi.DocBaseItemSequencia nro_item,\n"
                    + "	vi.DocBaseItemProdID produto,\n"
                    + "	un.UnSigla unidade,\n"
                    + "	case\n"
                    + "	   when p.ProdCodBarras1 is null then p.ProdCodInterno\n"
                    + "	   else p.ProdCodBarras1\n"
                    + "	end as codigobarras,\n"
                    + "	p.ProdDescricao descricao,\n"
                    + "	vi.DocBaseItemQuantidade quantidade,\n"
                    + "	vi.DocBaseItemValorUnitario precovenda,\n"
                    + "	vi.DocBaseItemValorTotal total\n"
                    + "from\n"
                    + "	TB_DOCUMENTO_BASE_ITENS vi\n"
                    + "left join TB_VENDA v on v.VndDocBaseID = vi.DocBaseItemDocBaseID \n"
                    + "left join TB_PRODUTO p on p.ProdID = vi.DocBaseItemProdID \n"
                    + "LEFT JOIN TB_VENDA_DOCUMENTO d on d.VndDocID = v.VndID \n"
                    + "left join TB_UNIDADE_MEDIDA un on un.UnID = vi.DocBaseItemUnidadeID \n"
                    + "WHERE\n"
                    + " d.VndDocNumero is not NULL \n"
                    + "	and v.VndDtEmissao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by 2,1";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
