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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.vo.sistema.AvistareVO;

/**
 *
 * @author Lucas
 */
public class AvistareDAO extends InterfaceDAO implements MapaTributoProvider {

    public AvistareVO avistareVO = null;
    private final String SISTEMA = "Avistare";

    @Override
    public String getSistema() {
        return SISTEMA;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	(select CfgValue from dbo.TB_CONFIG where CfgChave = 'CNPJ') as cnpj,\n"
                    + "	(select CfgValue from dbo.TB_CONFIG where CfgChave = 'EmpresaRegistro') as razao\n"
                    + "from dbo.TB_CONFIG"
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
                    "select distinct\n"
                    + "	(select CfgValue from dbo.TB_CONFIG where CfgChave = 'EmpresaRegistro') as razao\n"
                    + "from dbo.TB_CONFIG"
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
                    "select\n"
                    + "t.ClaFisID as id,\n"
                    + "t.ClaFisDescricao as descricao,\n"
                    + "tcc.CstCodigo as cst,\n"
                    + "t.ClaFisIcmsAliquota as aliquota,\n"
                    + "t.ClaFisIcmsReducao as reducao\n"
                    + "from dbo.TB_CLASSIFICACAO_FISCAL t\n"
                    + "left join TB_CST_CSOSN tcc on tcc.CstID = t.ClaFisCstID\n"
                    + "order by 1\n"
            )) {
                    while (rst.next()) {

                        result.add(new MapaTributoIMP(
                                rst.getString("id"),
                                rst.getString("descricao"),
                                rst.getInt("cst"),
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
                        "select \n"
                        + "	ProdFamID as merc1,\n"
                        + "	ProdFamDescricao as desc_merc1\n"
                        + "from TB_FAMILIA_PRODUTOS\n"
                        + "order by 1, 2"
                )) {
                    while (rst.next()) {
                        MercadologicoIMP imp = new MercadologicoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setMerc1ID(rst.getString("merc1"));
                        imp.setMerc1Descricao(rst.getString("desc_merc1"));
                        imp.setMerc2ID("1");
                        imp.setMerc2Descricao(imp.getMerc1Descricao());
                        imp.setMerc3ID("1");
                        imp.setMerc3Descricao(imp.getMerc1Descricao());
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
                        "with ean as (\n"
                        + "	select\n"
                        + "		p.ProdID id_produto,\n"
                        + "		p.ProdCodBarras1 ean\n"
                        + "	from\n"
                        + "		TB_PRODUTO p\n"
                        + "	where\n"
                        + "		ltrim(rtrim(coalesce(p.ProdCodBarras1,''))) != ''\n"
                        + "	union\n"
                        + "	select\n"
                        + "		p.ProdID id_produto,\n"
                        + "		p.ProdCodBarras2 ean\n"
                        + "	from\n"
                        + "		TB_PRODUTO p\n"
                        + "	where\n"
                        + "		ltrim(rtrim(coalesce(p.ProdCodBarras2,''))) != ''\n"
                        + "	union\n"
                        + "	select\n"
                        + "		p.ProdID id_produto,\n"
                        + "		p.ProdCodBarras3 ean\n"
                        + "	from\n"
                        + "		TB_PRODUTO p\n"
                        + "	where\n"
                        + "		ltrim(rtrim(coalesce(p.ProdCodBarras3,''))) != ''\n"
                        + ")\n"
                        + "select \n"
                        + "	p.ProdID as id,\n"
                        + "	p.ProdCodInterno,\n"
                        + "	ean.ean,\n"
                        + "	un.UnSigla as unidade,\n"
                        + "	p.ProdDescricao as descricao,\n"
                        + "	case p.ProdStatusID\n"
                        + "		when 1053 then 0\n"
                        + "		else 1\n"
                        + "	end ativo,\n"
                        + "	p.ProdFabricanteID,\n"
                        + "	p.ProdFamiliaID,\n"
                        + "	p.ProdNcm as ncm,\n"
                        + "	p.ProdCest as cest,\n"
                        + "	p.ProdPesoBruto as pesobruto,\n"
                        + "	p.ProdPesoLiquido as pesoliquido,\n"
                        + "	p.ProdPrecoCompra,\n"
                        + "	p.ProdPrecoCusto as custo,\n"
                        + "	p.ProdValorVenda1 as precovenda,\n"
                        + "	p.ProdEstoqueMin as estoqueminimo,\n"
                        + "	p.ProdEstoqueMax as estoquemaximo,\n"
                        + "	est.EstConsAtual as estoque,\n"
                        + "	p.ProdMargem1 as margem,\n"
                        + "	p.ProdDtCadastro as datacadastro,\n"
                        + "	p.ProdEmbalagemQtde as qtdembalagem,\n"
                        + "	p.ProdClaFisID as tribicms,\n"
                        + "	pis.CstPisCofinsCodigo as cstpissaida,\n"
                        + "	pis.CstPisCofinsDescricao,\n"
                        + "	cofins.CstPisCofinsCodigo as cstpisentrada,\n"
                        + "	cofins.CstPisCofinsDescricao,\n"
                        + "	nat.NatRecPisCofinsCodigo as naturezareceita,\n"
                        + "	nat.NatRecPisCofinsDescricao,\n"
                        + "	p.ProdCaixaQtde volume,\n"
                        + " p.ProdFamiliaId as mercadologico1 \n"
                        + "from\n"
                        + "	dbo.TB_PRODUTO p\n"
                        + "	left join dbo.TB_ESTOQUE_CONSOLIDADO est on\n"
                        + "		p.ProdID = est.EstConsProdID\n"
                        + "	left join ean on\n"
                        + "		p.ProdID = ean.id_produto\n"
                        + "	left join dbo.TB_UNIDADE_MEDIDA un on\n"
                        + "		un.UnID = p.ProdUnidadeMedidaID\n"
                        + "	left join dbo.TB_CST_PIS_COFINS pis on\n"
                        + "		pis.CstPisCofinsID = p.ProdCstPisID and\n"
                        + "		pis.CstPisCofinsOperacaoID = 129\n"
                        + "	left join dbo.TB_CST_PIS_COFINS cofins on\n"
                        + "		cofins.CstPisCofinsID = p.ProdCstCofinsCompraID and\n"
                        + "		cofins.CstPisCofinsOperacaoID = 128\n"
                        + "	left join dbo.TB_NATUREZA_RECEITA_PISCOFINS nat on\n"
                        + "		nat.NatRecPisCofinsID = p.ProdNaturezaReceitaPisCofinsID\n"
                        + "order by\n"
                        + "	p.ProdCodInterno"
                )) {
                    Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));

                        if (avistareVO.isTemArquivoBalanca()) {
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
                        } else {
                            imp.setEan(rst.getString("ean"));
                            imp.setTipoEmbalagem(rst.getString("unidade"));
                            imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                            imp.setValidade(0);
                            imp.seteBalanca(false);
                        }

                        imp.setDescricaoCompleta(rst.getString("descricao"));
                        imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                        imp.setPesoBruto(rst.getDouble("pesobruto"));
                        imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setCodMercadologico1(rst.getString("mercadologico1"));
                        imp.setCodMercadologico2("1");
                        imp.setCodMercadologico3("1");
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
                        imp.setQtdEmbalagemCotacao(rst.getInt("volume"));

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
                        "select \n"
                        + "	f.FornID as id,\n"
                        + "	f.FornCodInterno,\n"
                        + "	f.FornDiasEntrega,\n"
                        + "	pes.PessoaNome as razao,\n"
                        + "	pes.PessoaFantasia as fantasia,\n"
                        + "	pes.PessoaCpfCnpj as cnpj,\n"
                        + "	pes.PessoaIERG as ie_rg,\n"
                        + "	pes.PessoaIM as inscricaomunicipal,\n"
                        + "	ende.EndLogradouro as endereco,\n"
                        + "	ende.EndNumero as numero,\n"
                        + "	ende.EndComplemento as complemento,\n"
                        + "	ende.EndBairro as bairro,\n"
                        + "	ende.EndCEP as cep,\n"
                        + "	cid.CidNome as municipio,\n"
                        + "	cid.CidCodigoIBGE as municipio_ibge,\n"
                        + "	case pes.PessoaSituacaoID\n"
                        + "		when 51 then 0\n"
                        + "		else 1\n"
                        + "	end ativo,\n"
                        + "	uf.UfSigla as uf,\n"
                        + "	uf.UfCodigoIBGE as uf_ibge,\n"
                        + "	pes.PessoaEmail as email,\n"
                        + "	pes.PessoaSite as site,\n"
                        + "	pes.PessoaFonePrincipal as telefone,\n"
                        + "	pes.PessoaFoneCelular as celular,\n"
                        + "	pes.PessoaFoneFAX as fax,\n"
                        + "	pes.PessoaFoneOutro as telefone2,\n"
                        + "	pes.PessoaFonePABX as pabx,\n"
                        + "	pes.PessoaObservacoes as observacao,\n"
                        + "	pes.PessoaDtCadastro as datacadastro\n"
                        + "from\n"
                        + "	dbo.TB_FORNECEDOR f\n"
                        + "	join dbo.TB_PESSOA_PFPJ pes on\n"
                        + "		pes.PessoaID = f.FornID\n"
                        + "	left join dbo.TB_PESSOA_ENDERECOS pend on\n"
                        + "		pend.PessoaID = pes.PessoaID\n"
                        + "	left join dbo.TB_ENDERECO ende on\n"
                        + "		ende.EndID = pend.PessoaEndID\n"
                        + "	left join dbo.TB_CIDADE cid on\n"
                        + "		cid.CidID = ende.EndCidadeID\n"
                        + "	left join dbo.TB_UF uf on\n"
                        + "		uf.UfID = cid.CidUfID\n"
                        + "order by\n"
                        + "	f.FornID"
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
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("municipio"));
                        imp.setAtivo(rst.getBoolean("ativo"));
                        imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                        imp.setCep(rst.getString("cep"));
                        imp.setUf(rst.getString("uf"));
                        imp.setIbge_uf(rst.getInt("uf_ibge"));
                        imp.setTel_principal(rst.getString("telefone"));
                        imp.setDatacadastro(rst.getDate("datacadastro"));
                        imp.setObservacao(rst.getString("observacao"));

                        if ((rst.getString("email") != null)
                                && (!rst.getString("email").trim().isEmpty())) {
                            imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                        }
                        if ((rst.getString("celular") != null)
                                && (!rst.getString("celular").trim().isEmpty())) {
                            imp.addCelular("CELULAR", rst.getString("celular"));
                        }
                        if ((rst.getString("telefone2") != null)
                                && (!rst.getString("telefone2").trim().isEmpty())) {
                            imp.addTelefone("TELEFONE 2", rst.getString("telefone2"));
                        }
                        if ((rst.getString("fax") != null)
                                && (!rst.getString("fax").trim().isEmpty())) {
                            imp.addTelefone("FAX", rst.getString("fax"));
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
                        "select \n"
                        + "	ProdFornFornID as idfornecedor,\n"
                        + "	ProdFornProdID as idproduto,\n"
                        + "	ProdFornCodigo as codigoexterno\n"
                        + "from dbo.TB_PRODUTO_FORNECEDOR\n"
                        + "order by 1"
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
                        "select \n"
                        + "	c.CliID as id,\n"
                        + "	coalesce(c.CliCodigoPessoal, c.cliId) as CliCodigoPessoal,\n"
                        + "	c.CliLimiteTotal as valortotal,\n"
                        + "	c.CliLimiteSaldo as valorsaldo,\n"
                        + "	case pes.PessoaSituacaoID\n"
                        + "		when 51 then 0\n"
                        + "		else 1\n"
                        + "	end ativo,\n"
                        + "	pes.PessoaNome as razao,\n"
                        + "	pes.PessoaFantasia as fantasia,\n"
                        + "	pes.PessoaCpfCnpj as cnpj,\n"
                        + "	pes.PessoaIERG as ie_rg,\n"
                        + "	pes.PessoaIM as inscricaomunicipal,\n"
                        + "	ende.EndLogradouro as endereco,\n"
                        + "	ende.EndNumero as numero,\n"
                        + "	ende.EndComplemento as complemento,\n"
                        + "	ende.EndBairro as bairro,\n"
                        + "	ende.EndCEP as cep,\n"
                        + "	cid.CidNome as municipio,\n"
                        + "	cid.CidCodigoIBGE as municipio_ibge,\n"
                        + "	uf.UfSigla as uf,\n"
                        + "	uf.UfCodigoIBGE as uf_ibge,\n"
                        + "	pes.PessoaEmail as email,\n"
                        + "	pes.PessoaSite as site,\n"
                        + "	pes.PessoaFonePrincipal as telefone,\n"
                        + "	pes.PessoaFoneCelular as celular,\n"
                        + "	pes.PessoaFoneFAX as fax,\n"
                        + "	pes.PessoaFoneOutro as telefone2,\n"
                        + "	pes.PessoaFonePABX as pabx,\n"
                        + "	pes.PessoaObservacoes as observacao,\n"
                        + "	pes.PessoaDtCadastro as datacadastro\n"
                        + "from\n"
                        + "	dbo.TB_CLIENTE c\n"
                        + "	join dbo.TB_PESSOA_PFPJ pes on pes.PessoaID = c.CliID\n"
                        + "	left join dbo.TB_PESSOA_ENDERECOS pend on pend.PessoaID = pes.PessoaID\n"
                        + "	left join dbo.TB_ENDERECO ende on ende.EndID = pend.PessoaEndID\n"
                        + "	left join dbo.TB_CIDADE cid on cid.CidID = ende.EndCidadeID\n"
                        + "	left join dbo.TB_UF uf on uf.UfID = cid.CidUfID\n"
                        + "order by\n"
                        + "	c.CliID"
                )) {
                    while (rst.next()) {
                        ClienteIMP imp = new ClienteIMP();
                        //imp.setId(rst.getString("id"));                        ID DO CLIENTE, 
                        imp.setId(rst.getString("CliCodigoPessoal"));       // CODIGO INTERNO CLIENTE
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setCnpj(rst.getString("cnpj"));
                        imp.setInscricaoestadual(rst.getString("ie_rg"));
                        imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("municipio"));
                        imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                        imp.setUf(rst.getString("uf"));
                        imp.setUfIBGE(rst.getInt("uf_ibge"));
                        imp.setCep(rst.getString("cep"));
                        imp.setAtivo(rst.getBoolean("ativo"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setTelefone(rst.getString("telefone"));
                        imp.setCelular(rst.getString("celular"));
                        imp.setFax(rst.getString("fax"));
                        imp.setEmail(rst.getString("email"));
                        imp.setObservacao(rst.getString("observacao"));
                        imp.setValorLimite(rst.getDouble("valortotal"));

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
        public List<CreditoRotativoPagamentoAgrupadoIMP> getCreditoRotativoPagamentoAgrupado() throws Exception {
            List<CreditoRotativoPagamentoAgrupadoIMP> result = new ArrayList<>();

            try (
                    Statement stm = ConexaoSqlServer.getConexao().createStatement();
                    ResultSet rst = stm.executeQuery(
                            "select\n"
                            //+ "	t.CliSaldoMovCliID as id_cliente,\n"
                            + "     tc.CliCodigoPessoal as id_cliente,\n"
                            + "	sum(t.CliSaldoMovValor) as valor\n"
                            + "from\n"
                            + "	TB_CLIENTE_SALDO_MOVIMENTO t\n"
                            + "     join TB_CLIENTE tc on tc.CliID =  t.CliSaldoMovCliID\n"
                            + "where\n"
                            + "	t.CliSaldoMovNaturezaID = 100\n"
                            + "group by\n"
                            + "	tc.CliCodigoPessoal"
                    )) {
                while (rst.next()) {
                    result.add(new CreditoRotativoPagamentoAgrupadoIMP(
                            rst.getString("id_cliente"),
                            rst.getDouble("valor")
                    ));
                }
            }

            return result;
        }

        @Override
        public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
            List<CreditoRotativoIMP> result = new ArrayList<>();

            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	t.CliSaldoMovID as id,\n"
                        + "	v.VndDtEmissao as emissao,\n"
                        + "	v.VndNumeroVenda as numerocupom,\n"
                        + "	v.VndEstacaoID as ecf,\n"
                        + "	t.CliSaldoMovValor as valor,\n"
                        + "	t.CliSaldoMovObservacao as observacao,\n"
                        + "	coalesce(tc.CliCodigoPessoal, tc.cliId) as id_cliente\n"
                        //+ "	t.CliSaldoMovCliID as id_cliente\n"             // ROTATIVO POR ID_CLIENTE
                        + "from\n"
                        + "	TB_CLIENTE_SALDO_MOVIMENTO t\n"
                        + "	join TB_VENDA v on t.CliSaldoMovOrigemID = v.VndID\n"
                        + "	join TB_CLIENTE tc on tc.CliID =  t.CliSaldoMovCliID\n"
                        + "where\n"
                        + "	t.CliSaldoMovNaturezaID = 99 and\n"
                        + "	v.VndDtCancelamento is null\n"
                        + "order by\n"
                        + "	t.CliSaldoMovID "
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
                        GregorianCalendar vencimento = new GregorianCalendar();
                        vencimento.setTime(rst.getDate("emissao"));
                        vencimento.add(GregorianCalendar.DAY_OF_MONTH, 10);
                        imp.setDataVencimento(vencimento.getTime());

                        result.add(imp);
                    }
                }
            }
            return result;
        }

        @Override
        public Iterator<VendaIMP> getVendaIterator() throws Exception {
            return new AvistareDAO.VendaIterator(getLojaOrigem(), this.avistareVO.getDataInicioVenda(), this.avistareVO.getDataTerminoVenda());
        }

        @Override
        public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
            return new AvistareDAO.VendaItemIterator(getLojaOrigem(), this.avistareVO.getDataInicioVenda(), this.avistareVO.getDataTerminoVenda());
        }    
    
    

    public static class VendaIterator implements Iterator<VendaIMP> {

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

                        String numeroCupom = rst.getString("numerocupom");

                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));

                        if (numeroCupom != null && numeroCupom.length() > 10) {
                            numeroCupom = numeroCupom.substring(5, numeroCupom.length());
                            next.setNumeroCupom(Utils.stringToInt(numeroCupom));
                        }

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
                    /*+ "	CASE\n"
                    + "     when d.VndDocNumero is null\n"
                    + "     then v.VndNumeroVenda\n"
                    + "     else d.VndDocNumero\n"
                    + " END numerocupom,\n"*/
                    + " v.vndnumerovenda numerocupom,\n"
                    + "	c2.CliCodigoPessoal id_cliente,\n"
                    + "	c.PessoaNome nome_cliente,\n"
                    + "	v.VndNfpCpfCnpj cpf_cnpj,\n"
                    + "	SUBSTRING(e.EstacaoDescricao, 4, 2) ecf,\n"
                    + "	v.VndDtEmissao emissao,\n"
                    + "	CAST (VndDtAbertura as time) horainicio,\n"
                    + "	CAST (VndDtFechamento as time) horatermino,\n"
                    + "	CASE\n"
                    + "     when v.VndClienteValor = 0\n"
                    + "     then v.VndConvenioValor\n"
                    + "     ELSE v.VndClienteValor\n"
                    + "	END subtotalimpressora\n"
                    + "FROM\n"
                    + "	TB_VENDA v\n"
                    + "LEFT JOIN TB_VENDA_DOCUMENTO d on d.VndDocID = v.VndID\n"
                    + "LEFT JOIN TB_ESTACAO e on e.EstacaoID = v.VndEstacaoID\n"
                    + "LEFT JOIN TB_PESSOA_PFPJ c on c.PessoaID = v.VndClienteID\n"
                    + "LEFT JOIN TB_CLIENTE c2 on c2.CliID = v.VndClienteID\n"
                    + "WHERE\n"
                    + " v.VndDtCancelamento is NULL \n"
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

    public static class VendaItemIterator implements Iterator<VendaItemIMP> {

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
                    //+ "LEFT JOIN TB_VENDA_DOCUMENTO d on d.VndDocID = v.VndID \n"
                    + "left join TB_UNIDADE_MEDIDA un on un.UnID = vi.DocBaseItemUnidadeID \n"
                    + "WHERE\n"
                    //+ " d.VndDocNumero is not NULL \n"
                    + "v.VndDtEmissao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
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
