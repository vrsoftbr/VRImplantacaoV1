/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class AvistareDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Avistare";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.ProdID,\n"
                    + "	p.ProdCodInterno,\n"
                    + "	p.ProdCodBarras1,\n"
                    + "	p.ProdCodBarras2,\n"
                    + "	p.ProdCodBarras3,\n"
                    + "	p.ProdEan14,\n"
                    + "	un.UnSigla,\n"
                    + "	p.ProdDescricao,\n"
                    + "	p.ProdEstoqueAtual,\n"
                    + "	p.ProdEstoqueDisponivel,\n"
                    + "	p.ProdEstoqueMin,\n"
                    + "	p.ProdEstoqueMax,\n"
                    + "	p.ProdFabricanteID,\n"
                    + "	p.ProdFamiliaID,\n"
                    + "	p.ProdNcm,\n"
                    + "	p.ProdCest,\n"
                    + "	p.ProdPesoBruto,\n"
                    + "	p.ProdPesoLiquido,\n"
                    + "	p.ProdPrecoCompra,\n"
                    + "	p.ProdPrecoCusto,\n"
                    + "	p.ProdValorVenda1,\n"
                    + "	p.ProdMargem1,\n"
                    + "	p.ProdDtCadastro,\n"
                    + "	p.ProdEmbalagemQtde,\n"
                    + "	p.ProdClaFisID,\n"
                    + "	p.ProdClaFisID2,\n"
                    + "	pis.CstPisCofinsCodigo,\n"
                    + "	pis.CstPisCofinsDescricao,\n"
                    + "	cofins.CstPisCofinsCodigo,\n"
                    + "	cofins.CstPisCofinsDescricao,\n"
                    + "	nat.NatRecPisCofinsCodigo,\n"
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
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.FornID,\n"
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

                }
            }
        }
        return null;
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
                }
            }
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.CliID,\n"
                    + "	c.CliCodigoPessoal,\n"
                    + "	c.CliLimiteTotal,\n"
                    + "	c.CliLimiteSaldo,\n"
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

                }
            }
        }
        return null;
    }
}
