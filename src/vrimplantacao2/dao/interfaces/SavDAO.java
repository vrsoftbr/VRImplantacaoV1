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
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SavDAO extends InterfaceDAO {

    private int prefixoAtacado = 999999;

    public void setPrefixoAtacado(int prefixoAtacado) {
        this.prefixoAtacado = prefixoAtacado;
    }

    @Override
    public String getSistema() {
        return "Sav";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE
        }));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "empCodigo, "
                    + "empNome "
                    + "from dbo.tbEmpresa\n"
                    + "order by empCodigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("empCodigo"),
                            rst.getString("empNome")
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
                    "select\n"
                    + "p.proCodigo as id,\n"
                    + "p.proNome as descricao,\n"
                    + "p.proUnidade as tipoembalagem,\n"
                    + "p.proQtdTributaria as qtdembalagem,\n"
                    + "p.proCodigoBarras as ean,\n"
                    + "p.proStatusBalanca as balanca,\n"
                    + "p.proNCM as ncm,\n"
                    + "p.proCEST as cest,\n"
                    + "pis.pisCST as cst_pis,\n"
                    + "cof.cofinsCST as cst_cofins,\n"
                    + "icm.icmsCST as cst_icms,\n"
                    + "icm.icmsAliquota as aliq_icms,\n"
                    + "icm.icmsReducaoBC as redu_icms,\n"
                    + "p.proValorCusto as custo,\n"
                    + "p.proPorcLucro as margem,\n"
                    + "p.proValorVenda as precovenda,\n"
                    + "p.proStatus as situacaocadastro,\n"
                    + "est.estQtdProduto as estoque\n"
                    + "from dbo.tbProduto p\n"
                    + "left join dbo.tbProdutoPIS pis on pis.proCodigo = p.proCodigo\n"
                    + "left join dbo.tbProdutoCOFINS cof on cof.proCodigo = p.proCodigo \n"
                    + "left join dbo.tbProdutoICMS icm on icm.proCodigo = p.proCodigo\n"
                    + "left join dbo.tbEstoque est on est.proCodigo = p.proCodigo \n"
                    + "	and est.empCodigo = " + getLojaOrigem() + "\n"
                    + "order by p.proCodigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca("S".equals(rst.getString("baianca")));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setSituacaoCadastro("A".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins"));
                    imp.setIcmsCst(rst.getInt("cst_icms"));
                    imp.setIcmsAliq(rst.getDouble("aliq_icms"));
                    imp.setIcmsReducao(rst.getDouble("redu_icms"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "ata.proCodigo as id_produto, \n"
                        + "ata.pprQtdProduto as qtdembalagem, \n"
                        + "ata.pprValorVenda as precoatacado,\n"
                        + "pro.proValorVenda as precovenda\n"
                        + "from dbo.tbProdutoPreco ata\n"
                        + "inner join dbo.tbProduto pro on pro.proCodigo = ata.proCodigo"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("codigo"));

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setEan(prefixoAtacado + String.valueOf(codigoAtual));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        result.add(imp);
                    }
                }
                return result;
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
                    + "f.forCodigo as id,\n"
                    + "f.forRazao as razao,\n"
                    + "f.forNome as fantasia,\n"
                    + "f.forCNPJCPF as cnpj_cpf,\n"
                    + "f.forRg as rg,\n"
                    + "f.forInscEstadual as i_e,\n"
                    + "f.forInscMunicipal as i_m,\n"
                    + "f.forEndereco as endereco,\n"
                    + "f.forEndeNumero as numero,\n"
                    + "bai.baiDescricao as bairro,\n"
                    + "f.cidCodigo as municipio_ibge,\n"
                    + "cid.cidDescricao as municipio,\n"
                    + "f.forUF as uf,\n"
                    + "f.forCEP as cep,\n"
                    + "(coalesce(f.forDDD, '') + coalesce(f.forFone, '')) as telefone,\n"
                    + "f.forFax as fax,\n"
                    + "f.forCelular as celular,\n"
                    + "f.forContato as contato,\n"
                    + "f.forEmail as email,\n"
                    + "f.forDataCadastro as datacadastro,\n"
                    + "f.forStatus as situacoacadastro\n"
                    + "from dbo.tbFornecedor f\n"
                    + "left join dbo.tbBairro bai on bai.baiCodigo = f.baiCodigo\n"
                    + "left join dbo.tbCidade cid on cid.cidCodigo = f.cidCodigo\n"
                    + "order by f.forCodigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("i_e"));
                    imp.setInsc_municipal(rst.getString("i_m"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setAtivo("A".equals(rst.getString("situacoacadastro")));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {

                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {

                        imp.addContato(
                                "CELULAR",
                                null,
                                rst.getString("celular"),
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {

                        imp.addContato(
                                "CONTATO",
                                null,
                                rst.getString("contato"),
                                TipoContato.NFE,
                                null
                        );
                    }
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
                    + "c.cliCodigo as id,\n"
                    + "c.cliNome as nome,\n"
                    + "c.cliCNPJCPF as cnpj_cpf,\n"
                    + "c.cliRG as rg,\n"
                    + "c.cliInscEstadual as i_e,\n"
                    + "c.cliInscMunicipal as i_m,\n"
                    + "c.cliEndereco as endereco,\n"
                    + "c.cliEndeNumero as numero,\n"
                    + "c.cliEndeComplemento as complemento,\n"
                    + "bai.baiDescricao as bairro,\n"
                    + "c.cidCodigo as municipio_ibge,\n"
                    + "cid.cidDescricao as municipio,\n"
                    + "c.cliUF as uf,\n"
                    + "c.cliCEP as cep,\n"
                    + "(coalesce(c.cliDDD, '') + coalesce(c.cliFone, '')) as telefone,\n"
                    + "c.cliFax as fax,\n"
                    + "c.cliCelular as celular,\n"
                    + "c.cliEmail as email,\n"
                    + "c.cliContato as contato,\n"
                    + "c.cliDataCadastro as datacadastro,\n"
                    + "c.cliDataNascimento as nascimento,\n"
                    + "c.cliCredito as valorlimite,\n"
                    + "c.cliStatus as situacaocadastro\n"
                    + "from dbo.tbCliente c\n"
                    + "left join dbo.tbBairro bai on bai.baiCodigo = c.baiCodigo\n"
                    + "left join dbo.tbCidade cid on cid.cidCodigo = c.cidCodigo\n"
                    + "order by c.cliCodigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rst.getString("i_e"));
                    imp.setInscricaoMunicipal(rst.getString("i_m"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setAtivo("A".equals(rst.getString("situacaocadastro")));

                    if (imp.getValorLimite() > 0) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    } else {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                    }

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.addTelefone(
                                "CONTATO",
                                rst.getString("contato")
                        );
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
                    "select  \n"
                    + "crbNumero as id,\n"
                    + "cliCodigo as id_cliente,\n"
                    + "crbDocumento as numerocupom,\n"
                    + "crbHistorico as observacao,\n"
                    + "coalesce(crbParcela, 1) as numeroparcela,\n"
                    + "coalesce(crbDataCompra, crbDataVencimento) as dataemissao,\n"
                    + "crbDataVencimento as datavencimento,\n"
                    + "crbValor as valor,\n"
                    + "coalesce(crbValorDesconto, 0) as desconto,\n"
                    + "coalesce(crbValorJuros, 0) as juros,\n"
                    + "coalesce(crbValorMulta, 0) as multa\n"
                    + "from dbo.tbContaReceber\n"
                    + "where crbDataCancelamento is null\n"
                    + "and crbDataPagamento is null"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setParcela(rst.getInt("numeroparcela"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
