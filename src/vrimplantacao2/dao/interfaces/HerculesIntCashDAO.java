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
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class HerculesIntCashDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "HerculesIntCash";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
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
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE_MINIMO,
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
                    "select \n"
                    + "f.Fil_CodEmp,\n"
                    + "f.Fil_CodFil,\n"
                    + "f.Fil_NomFan\n"
                    + "from dbo.IntFil f\n"
                    + "inner join dbo.IntEmp e on e.Emp_CodEmp = f.Fil_CodEmp\n"
                    + "order by f.Fil_CodEmp, f.Fil_CodFil"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("Fil_CodEmp"),
                            rst.getString("Fil_CodFil") + " - " + rst.getString("Fil_NomFan")
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
                    + "g.Grp_CodGrp, g.Grp_DesGrp,\n"
                    + "coalesce(sg.Sgp_CodSub, 1) as Sgp_CodSub,\n"
                    + "sg.Sgp_DesSub\n"
                    + "from dbo.IntGrp g\n"
                    + "left join dbo.IntSgp sg on sg.Sgp_CodGrp = g.Grp_CodGrp\n"
                    + "order by g.Grp_CodGrp, sg.Sgp_CodSub"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("Grp_CodGrp"));
                    imp.setMerc1Descricao(rst.getString("Grp_DesGrp"));
                    imp.setMerc2ID(rst.getString("Sgp_CodSub"));

                    if ((rst.getString("Sgp_DesSub") != null)
                            && (!rst.getString("Sgp_DesSub").trim().isEmpty())) {
                        imp.setMerc2Descricao(rst.getString("Sgp_DesSub"));
                    } else {
                        imp.setMerc2Descricao(rst.getString("Grp_DesGrp"));
                    }

                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "select\n"
                    + "p.Prd_CodPrd as id,\n"
                    + "p.Prd_CodBar as barras,\n"
                    + "p.Prd_TipVen as balanca,\n"
                    + "p.Prd_DesPrd as descricao,\n"
                    + "p.Prd_CodUnd as unidade,\n"
                    + "p.Prd_PesLiq as pesoliquido,\n"
                    + "p.Prd_PesBru as pesobruto,\n"
                    + "p.Prd_SitPrd as situacaocadastro,\n"
                    + "p.Prd_DatAtu as datacadastro,\n"
                    + "p.Prd_PrdNcm as ncm,\n"
                    + "p.Prd_CodCes as cest,\n"
                    + "trib.Afs_SitPis as pis,\n"
                    + "trib.Afs_SitCof as cofins,\n"
                    + "trib.Afs_NatPis as naturezareceita,\n"
                    + "trib.Afs_SitTri as cstIcms,\n"
                    + "trib.Afs_AlqIcm as aliqIcms,\n"
                    + "trib.Afs_FatRed as reduIcms,\n"
                    + "p.Prd_CodGrp as merc1,\n"
                    + "p.Prd_CodSub as merc2,\n"
                    + "pr.Pvp_PreVen as precovenda\n"
                    + "from dbo.IntPrd p\n"
                    + "left join dbo.IntPvp pr on pr.Pvp_CodPrd = p.Prd_CodPrd\n"
                    + "	and p.Prd_CodEmp = '" + getLojaOrigem() + "'"
                    + "	and pr.Pvp_CodEmp = '" + getLojaOrigem() + "'\n"
                    + "left join dbo.IntAfs trib on trib.Afs_PrdNcm = p.Prd_PrdNcm \n"
                    + "and trib.Afs_CodTcl = 1\n"
                    + "and trib.Afs_CodTme in ('PDV')\n"
                    + "and trib.Afs_CodCfo = 5102 \n"
                    + "and trib.Afs_CodEmp = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("barras"));
                    imp.seteBalanca("B".equals(rst.getString("balanca")));
                    imp.setTipoEmbalagem(rst.getString("unidade").trim());
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro("A".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(String.valueOf(rst.getInt("merc2")));
                    imp.setCodMercadologico3("1");
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsCst(rst.getInt("cstIcms"));
                    imp.setIcmsAliq(rst.getDouble("aliqIcms"));
                    imp.setIcmsReducao(rst.getDouble("reduIcms"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "est.Fmp_CodPrd as idproduto, \n"
                        + "est.Fmp_DatMov,\n"
                        + "est.Fmp_TipEst,\n"
                        + "est.Fmp_QtdMov,\n"
                        + "est.Fmp_QtdEst as estoque\n"
                        + "from dbo.IntFmp est\n"
                        + "where est.Fmp_DatMov in (select MAX(Fmp_DatMov) from dbo.IntFmp where Fmp_CodPrd = est.Fmp_CodPrd)\n"
                        + "and est.Fmp_CodEmp = '" + getLojaOrigem() + "'\n"
                        + "order by Fmp_CodPrd"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        if (opt == OpcaoProduto.CUSTO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "  b.Pnf_CodPrd as id_produto,\n"
                        + "  b.Pnf_ValUni as custo\n"
                        + "from dbo.IntNfe a\n"
                        + "inner join dbo.IntPnf b on b.Pnf_NumNot = a.Nfe_NumNot\n"
                        + "where b.Pnf_CodTme = 'ENT'\n"
                        + "  and b.Pnf_CodEmp = '" + getLojaOrigem() + "'\n"
                        + "  and a.Nfe_DatEnt in (select \n"
                        + "	max(nf.Nfe_DatEnt) as ult_data\n"
                        + "  from dbo.IntPnf nfp\n"
                        + "  inner join dbo.IntNfe nf on nf.Nfe_NumNot = nfp.Pnf_NumNot\n"
                        + "  where nfp.Pnf_CodTme = 'ENT'\n"
                        + "  and nfp.Pnf_CodPrd = b.Pnf_CodPrd\n"
                        + "  and nfp.Pnf_CodEmp = '" + getLojaOrigem() + "')\n"
                        + "order by b.Pnf_CodPrd"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setCustoComImposto(rst.getDouble("custo"));
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.Prd_CodPrd as id,\n"
                    + "p.Prd_CodPrd as barras,\n"
                    + "p.Prd_CodUnd as unidade\n"
                    + "from dbo.IntPrd p\n"
                    + "where p.Prd_CodEmp = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("barras"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(1);
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
                    + "     f.For_CicFor as id,\n"
                    + "     f.For_CicFor as cnpj,\n"
                    + "     f.For_IntEst as inscricaoestadual,\n"
                    + "     f.For_NomFor as razao,\n"
                    + "     f.For_NomFan as fantasia,\n"
                    + "     f.For_EndFor as endereco,\n"
                    + "     f.For_EndNum as numero,\n"
                    + "     f.For_BaiFor as bairro,\n"
                    + "     f.For_CidFor as municipio,\n"
                    + "     f.For_CodMun as municipio_ibge,\n"
                    + "     f.For_EstFor as uf,\n"
                    + "     f.For_CepFor as cep,\n"
                    + "     f.For_FonFor as telefone,\n"
                    + "     f.For_FaxFor as fax,\n"
                    + "     f.For_CelFor as celular,\n"
                    + "     f.For_EmaFor as email\n"
                    + "from dbo.IntFor f\n"
                    + "where f.For_CodEmp = '" + getLojaOrigem() + "' \n"
                    + "order by f.For_CicFor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));

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
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {

                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
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
                    + "     Fpr_CodPrd as idproduto,\n"
                    + "     Fpr_CicFor as idfornecedor,\n"
                    + "     Fpr_CodFor as codigoexterno,\n"
                    + "     Fpr_UltEnt as dataalteracao,\n"
                    + "     Fpr_UltCus as custotabela\n"
                    + "from dbo.IntFpr"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setCustoTabela(rst.getDouble("custotabela"));
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
                    + "     c.Cli_CodCli as id,\n"
                    + "     c.Cli_CicCli as cnpj,\n"
                    + "     c.Cli_NomCli as razao,\n"
                    + "     c.Cli_NomFan as fantasia,\n"
                    + "     c.Cli_EndCli as endereco,\n"
                    + "     c.Cli_EndNum as numero,\n"
                    + "     c.Cli_BaiCli as bairro,\n"
                    + "     c.Cli_CidCli as municipio,\n"
                    + "     c.Cli_CodMun as municipio_ibge,\n"
                    + "     c.Cli_EstCli as uf,\n"
                    + "     c.Cli_CepCli as cep,\n"
                    + "     c.Cli_FonCli as telefone,\n"
                    + "     c.Cli_FaxCli as fax,\n"
                    + "     c.Cli_CelCli as celular,\n"
                    + "     c.Cli_EmaCli as email,\n"
                    + "     c.Cli_DatCad as datacadastro,\n"
                    + "     c.Cli_LimCre as valorlimite,\n"
                    + "     c.Cli_StaCli as situacaocadastro\n"
                    + "from dbo.IntCli c\n"
                    + "order by c.Cli_CicCli"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setAtivo("A".equals(rst.getString("situacaocadastro")));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email") != null ? rst.getString("email").toLowerCase() : "");
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
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
                    + "(rec.Cat_CodEmp + '-' + rec.Cat_CicCli + '-' + rec.Cat_NumTit) as id,\n"
                    + "cli.Cli_CodCli as id_cliente,\n"
                    + "rec.Cat_CicCli as cnpj_cliente,\n"
                    + "rec.Cat_NumTit as numero_cupom,\n"
                    + "rec.Cat_DatEmi as data_emissao,\n"
                    + "rec.Cat_DatVen as data_vencimento,\n"
                    + "rec.Cat_SldTit as valor\n"
                    + "from dbo.IntCat rec \n"
                    + "inner join dbo.IntCli cli on cli.Cli_CicCli = rec.Cat_CicCli\n"
                    + "where rec.Cat_SldTit > 0\n"
                    + "and rec.Cat_CodEmp = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataEmissao(rst.getDate("data_emissao"));
                    imp.setDataVencimento(rst.getDate("data_vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("numero_cupom"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
