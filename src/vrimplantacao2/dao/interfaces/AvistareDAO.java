/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class AvistareDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Avistare";
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
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
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
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA
                }
        ));
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.ProdID as id,\n"
                    + "	p.ProdCodInterno,\n"
                    + "	p.ProdCodBarras1 as ean,\n"
                    + "	un.UnSigla as unidade,\n"
                    + "	p.ProdDescricao as descricao,\n"
                    + "	p.ProdEstoqueAtual as estoque,\n"
                    + "	p.ProdEstoqueDisponivel,\n"
                    + "	p.ProdEstoqueMin as estoqueminimo,\n"
                    + "	p.ProdEstoqueMax as estoquemaximo,\n"
                    + "	p.ProdFabricanteID,\n"
                    + "	p.ProdFamiliaID,\n"
                    + "	p.ProdNcm as ncm,\n"
                    + "	p.ProdCest as cest,\n"
                    + "	p.ProdPesoBruto as pesobruto,\n"
                    + "	p.ProdPesoLiquido as pesoliquido,\n"
                    + "	p.ProdPrecoCompra,\n"
                    + "	p.ProdPrecoCusto as custo,\n"
                    + "	p.ProdValorVenda1 as precovenda,\n"
                    + "	p.ProdMargem1 as margem,\n"
                    + "	p.ProdDtCadastro as datacadastro,\n"
                    + "	p.ProdEmbalagemQtde as qtdembalagem,\n"
                    + "	p.ProdClaFisID as tribicms,\n"
                    + "	pis.CstPisCofinsCodigo as cstpissaida,\n"
                    + "	pis.CstPisCofinsDescricao,\n"
                    + "	cofins.CstPisCofinsCodigo as cstpisentrada,\n"
                    + "	cofins.CstPisCofinsDescricao,\n"
                    + "	nat.NatRecPisCofinsCodigo as naturezareceita,\n"
                    + "	nat.NatRecPisCofinsDescricao\n"
                    + "from dbo.TB_PRODUTO p\n"
                    + "left join dbo.TB_UNIDADE_MEDIDA un on un.UnID = p.ProdUnidadeMedidaID\n"
                    + "left join dbo.TB_CST_PIS_COFINS pis on pis.CstPisCofinsID = p.ProdCstPisID\n"
                    + "	and pis.CstPisCofinsOperacaoID = 129\n"
                    + "left join dbo.TB_CST_PIS_COFINS cofins on cofins.CstPisCofinsID = p.ProdCstCofinsCompraID\n"
                    + "	and cofins.CstPisCofinsOperacaoID = 128\n"
                    + "left join dbo.TB_NATUREZA_RECEITA_PISCOFINS nat on nat.NatRecPisCofinsID = p.ProdNaturezaReceitaPisCofinsID\n"
                    + "order by p.ProdCodInterno"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO produtoBalanca;
                    long codigoProduto;
                    codigoProduto = Long.parseLong(imp.getImportId());

                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 1);
                    } else {
                        imp.seteBalanca(false);
                        
                        if ((imp.getEan() != null)
                                && (!imp.getEan().trim().isEmpty())) {

                            if (Long.parseLong(Utils.formataNumero(imp.getEan())) <= 999999) {
                                imp.setManterEAN(true);
                            } else {
                                imp.setManterEAN(false);
                            }
                        }
                    }
                    
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
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
                    + "	p.ProdID as id,\n"
                    + "	p.ProdCodBarras2 as ean,\n"
                    + "	un.UnSigla as unidade\n"
                    + "from dbo.TB_PRODUTO p\n"
                    + "left join dbo.TB_UNIDADE_MEDIDA un on un.UnID = p.ProdUnidadeMedidaID\n"
                    + "union all\n"
                    + "select \n"
                    + "	p.ProdID as id,\n"
                    + "	p.ProdCodBarras3 as ean,\n"
                    + "	un.UnSigla as unidade\n"
                    + "from dbo.TB_PRODUTO p\n"
                    + "left join dbo.TB_UNIDADE_MEDIDA un on un.UnID = p.ProdUnidadeMedidaID\n"
                    + "union all\n"
                    + "select \n"
                    + "	p.ProdID as id,\n"
                    + "	p.ProdEan14 as ean,\n"
                    + "	un.UnSigla as unidade\n"
                    + "from dbo.TB_PRODUTO p\n"
                    + "left join dbo.TB_UNIDADE_MEDIDA un on un.UnID = p.ProdUnidadeMedidaID\n"
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
                    + "from dbo.TB_FORNECEDOR f\n"
                    + "join dbo.TB_PESSOA_PFPJ pes on pes.PessoaID = f.FornID\n"
                    + "left join dbo.TB_PESSOA_ENDERECOS pend on pend.PessoaID = pes.PessoaID\n"
                    + "left join dbo.TB_ENDERECO ende on ende.EndID = pend.PessoaEndID\n"
                    + "left join dbo.TB_CIDADE cid on cid.CidID = ende.EndCidadeID\n"
                    + "left join dbo.TB_UF uf on uf.UfID = cid.CidUfID\n"
                    + "order by f.FornID"
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
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
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
                    + "	c.CliCodigoPessoal,\n"
                    + "	c.CliLimiteTotal as valortotal,\n"
                    + "	c.CliLimiteSaldo as valorsaldo,\n"
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
                    + "from dbo.TB_CLIENTE c\n"
                    + "join dbo.TB_PESSOA_PFPJ pes on pes.PessoaID = c.CliID\n"
                    + "left join dbo.TB_PESSOA_ENDERECOS pend on pend.PessoaID = pes.PessoaID\n"
                    + "left join dbo.TB_ENDERECO ende on ende.EndID = pend.PessoaEndID\n"
                    + "left join dbo.TB_CIDADE cid on cid.CidID = ende.EndCidadeID\n"
                    + "left join dbo.TB_UF uf on uf.UfID = cid.CidUfID\n"
                    + "order by c.CliID"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
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
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate(null));
                    imp.setNumeroCupom(rst.getString(null));
                    imp.setEcf(rst.getString(null));
                    imp.setValor(rst.getDouble(null));
                    imp.setObservacao(rst.getString(null));
                    imp.setIdCliente(rst.getString(null));
                    imp.setDataVencimento(rst.getDate(null));
                    imp.setJuros(rst.getDouble(null));
                    imp.setCnpjCliente(rst.getString(null));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
