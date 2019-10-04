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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SabTechDAO extends InterfaceDAO implements MapaTributoProvider {

    public String database = "AutoSystem2005";
    public String user_banco = "Todos";
    public String pass_banco = "123";

    @Override
    public String getSistema() {
        return "SabTech";
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
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MAPA_TRIBUTACAO
        }));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "     icm.Codigo as codigo,\n"
                    + "     icm.ST as cst,\n"
                    + "     icm.SubsTrib as tributacao\n"
                    + "     from dbo.CPro_TabICMS icm\n"
                    + "     where icm.Codigo in (select ICMSTabela from dbo.CPro_Produto)\n"
                    + "     order by icm.Codigo"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"),
                            rs.getString("cst")
                            + " - " + rs.getString("tributacao"))
                    );
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
                    + "	distinct \n"
                    + "	m1.Depto as codMercadologico1,\n"
                    + " m1.Descricao as mercadologico1,\n"
                    + "	m2.Classe as codMercadologico2,\n"
                    + " m2.Descricao as mercadologico2\n"
                    + "from dbo.CPro_Produto p\n"
                    + "inner join dbo.CPro_Depto m1 on m1.Depto = p.Depto\n"
                    + "inner join dbo.CPro_Classe m2 on m2.Classe = p.Classe\n"
                    + "order by m1.Depto, m2.Classe"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codMercadologico1"));
                    imp.setMerc1Descricao(rst.getString("mercadologico1"));
                    imp.setMerc2ID(rst.getString("codMercadologico2"));
                    imp.setMerc2Descricao(rst.getString("mercadologico2"));
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
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.Produto as id,\n"
                    + "	p.CodBarras as ean,\n"
                    + "	p.Balanca,\n"
                    + "	p.Validade,\n"
                    + "	p.Unidade as tipoembalagem,\n"
                    + "	p.Descricao as descricaocompleta,\n"
                    + "	p.DescricaoCurta as descricaoreduzida,\n"
                    + "	p.Depto as codMercadologico1,\n"
                    + "	p.Classe as codMercadologico2,\n"
                    + " p.PesoBruto pesobruto,\n"
                    + "	p.EstMin as estoqueminimo,\n"
                    + "	p.EstMax as estoquemaximo,\n"
                    + "	p.EstAtual as estoque,\n"
                    + "	p.ValorCusto as custo,\n"
                    + "	p.VlVenda as precovenda,\n"
                    + "	p.Lucro as margem,\n"
                    + "	p.ICMSTabela as idicms,\n"
                    + "	p.Inativo as situacaocadastro,\n"
                    + "	p.ClaFiscal as ncm,\n"
                    + "	REPLACE(ces.CEST_Codigo, '.', '') as cest,\n"
                    + "	pis.ST as cst_pis,\n"
                    + "	cof.ST as cst_cofins\n"
                    + "from dbo.CPro_Produto p\n"
                    + "left join dbo.CPro_TabCEST ces on ces.CEST = p.CESTTabela\n"
                    + "left join dbo.CPro_TabPIS pis on pis.PISTabela = p.PISTabela\n"
                    + "left join dbo.CPro_TabCOFINS cof on cof.COFINSTabela = p.COFINSTabela"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("Balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("codMercadologico1"));
                    imp.setCodMercadologico2(rst.getString("codMercadologico2"));
                    imp.setCodMercadologico3("1");
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesobruto"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins"));
                    imp.setIcmsDebitoId(rst.getString("idicms"));
                    imp.setIcmsCreditoId(rst.getString("idicms"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	ean.Produto as idproduto, \n"
                    + "	ean.CodBarras as ean, \n"
                    + "	ean.Unidade as tipoembalagem \n"
                    + "from dbo.CPro_CodBarras ean"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT dbo.CRH_Humano.Cadastro, dbo.CRH_Humano.Codigo, dbo.CRH_Humano.Sexo, dbo.CRH_Humano.Tipo, dbo.CRH_Humano.Desde, dbo.CRH_Humano.Nome, dbo.CRH_Humano.Nascimento, \n"
                    + "       dbo.CRH_Humano.RSocial, dbo.CRH_Humano.CPFCNPJ, dbo.CRH_Humano.RGIE, dbo.CRH_Humano.Endereco, dbo.CRH_Humano.Numero, ISNULL(dbo.CRH_Humano.Endereco, '') \n"
                    + "       + ', ' + ISNULL(dbo.CRH_Humano.Numero, '') AS Ender, dbo.CRH_Humano.Complemento, dbo.CRH_Humano.Bairro, dbo.CRH_Humano.Cidade, dbo.CRH_Humano.UF, ISNULL(dbo.CRH_Humano.Cidade, '') \n"
                    + "       + ' - ' + ISNULL(dbo.CRH_Humano.UF, '') AS Cid, dbo.CRH_Humano.CEP, dbo.CRH_Humano.Fone1, dbo.CRH_Humano.Fone2, dbo.CRH_Humano.Celular, dbo.CRH_Humano.Fax, dbo.CRH_Humano.Fone0800, \n"
                    + "       dbo.CRH_Humano.EMail, dbo.CRH_Humano.HomePage, dbo.CRH_Humano.CadCla, dbo.CRH_Humano.Classe, dbo.CRH_Classe.Descricao AS ClasseDesc, dbo.CRH_Humano.ContNome, \n"
                    + "       dbo.CRH_Humano.ContFone, dbo.CRH_Humano.ContRamal, dbo.CRH_Humano.ContFax, dbo.CRH_Humano.ContFaxRamal, dbo.CRH_Humano.ContCelular, dbo.CRH_Humano.ContEMail, \n"
                    + "       dbo.CRH_Humano.RamoAtiv, dbo.CRH_Humano.Inativo\n"
                    + "  FROM dbo.CRH_Humano LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Classe ON dbo.CRH_Humano.CadCla = dbo.CRH_Classe.Cadastro AND dbo.CRH_Humano.Classe = dbo.CRH_Classe.Classe\n"
                    + " WHERE (dbo.CRH_Humano.Cadastro = 'For')\n"
                    + " ORDER BY dbo.CRH_Humano.Cadastro"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("RSocial"));
                    imp.setFantasia(rst.getString("Nome"));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("CPFCNPJ")));
                    if ((rst.getString("RGIE") != null)
                            && (!rst.getString("RGIE").trim().isEmpty())) {
                        imp.setIe_rg(Utils.formataNumero(rst.getString("RGIE")));
                    } else {
                        imp.setIe_rg("ISENTO");
                    }
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCep(Utils.formataNumero(rst.getString("CEP")));
                    imp.setAtivo((rst.getInt("Inativo") == 0 ? true : false));
                    imp.setTel_principal(Utils.formataNumero(rst.getString("Fone1")));

                    if ((rst.getString("Fone2") != null)
                            && (!rst.getString("Fone2").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("1");
                        contato.setNome("TELEFONE 2");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fone2").trim()));
                        imp.getContatos().put(contato, "1");
                    }

                    if ((rst.getString("Celular") != null)
                            && (!rst.getString("Celular").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("2");
                        contato.setNome("CELULAR");
                        contato.setCelular(Utils.formataNumero(rst.getString("Celular").trim()));
                        imp.getContatos().put(contato, "2");
                    }

                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("3");
                        contato.setNome("FAX");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fax").trim()));
                        imp.getContatos().put(contato, "3");
                    }

                    if ((rst.getString("Fone0800") != null)
                            && (!rst.getString("Fone0800").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("4");
                        contato.setNome("FONE 0800");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fone0800")));
                        imp.getContatos().put(contato, "4");
                    }

                    if ((rst.getString("EMail") != null)
                            && (!rst.getString("EMAil").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("5");
                        contato.setNome("EMAIL");
                        contato.setEmail(rst.getString("EMail").trim());
                        imp.getContatos().put(contato, "5");
                    }

                    if ((rst.getString("HomePage") != null)
                            && (!rst.getString("HomePage").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("6");
                        contato.setNome("HOME PAGE");
                        contato.setEmail(rst.getString("HomePage"));
                        imp.getContatos().put(contato, "6");
                    }

                    if ((rst.getString("ContNome") != null)
                            && (!rst.getString("ContNome").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("7");
                        contato.setNome(rst.getString("ContNome"));
                        if ((rst.getString("ContFone") != null)
                                && (!rst.getString("ContFone").trim().isEmpty())) {
                            contato.setTelefone(Utils.formataNumero(rst.getString("ContFone")));
                        }
                        imp.getContatos().put(contato, "7");
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Produto, Fornec, Docto, Qtde \n"
                    + "from dbo.VwCPro_ProdutoCompra" /*"SELECT dbo.CPro_Produto.Produto, dbo.CPro_Produto.Fabricante, dbo.CPro_Produto.Depto, dbo.CPro_Produto.Classe, dbo.MAC_Compra.CadFor, dbo.MAC_Compra.Fornec\n"
             + "  FROM dbo.MAC_Compra \n"
             + " INNER JOIN dbo.MAC_Recto ON dbo.MAC_Compra.Compra = dbo.MAC_Recto.Compra \n"
             + " INNER JOIN dbo.CPro_Produto \n"
             + " INNER JOIN dbo.MAC_RectoPro ON dbo.CPro_Produto.Produto = dbo.MAC_RectoPro.Produto ON dbo.MAC_Recto.Recto = dbo.MAC_RectoPro.Recto"*/
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("Fornec"));
                    imp.setIdProduto(rst.getString("Produto"));
                    imp.setCodigoExterno(rst.getString("Docto"));
                    imp.setQtdEmbalagem(rst.getInt("Qtde"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT dbo.CRH_Humano.Cadastro, dbo.CRH_Humano.Codigo, dbo.CRH_Humano.Tipo, dbo.CRH_Humano.Limite, dbo.CRH_Humano.Sexo, dbo.CRH_Humano.Desde, dbo.CRH_Humano.Nascimento, \n"
                    + "       dbo.CRH_Humano.RSocial, dbo.CRH_Humano.Nome, ISNULL(dbo.CRH_Humano.RSocial, '') AS NomeComp, dbo.CRH_Humano.CPFCNPJ, dbo.CRH_Humano.RGIE, \n"
                    + "       dbo.CRH_Humano.Endereco, dbo.CRH_Humano.Numero, dbo.CRH_Humano.Complemento, dbo.CRH_Humano.Bairro, dbo.CRH_Humano.Cidade, dbo.CRH_Humano.UF, dbo.CRH_Humano.CEP, \n"
                    + "       dbo.CRH_Humano.Fone1, dbo.CRH_Humano.Fone2, dbo.CRH_Humano.Celular, dbo.CRH_Humano.Fax, dbo.CRH_Humano.Fone0800, dbo.CRH_Humano.EMail, dbo.CRH_Humano.CadRep, dbo.CRH_Humano.Rep,\n"
                    + "       CRH_Rep.Nome AS RepNome, dbo.CRH_Humano.Cartao, dbo.CRH_Humano.CartaoGold, dbo.CRH_Humano.CartaoData, dbo.CRH_Humano.ConexaoImp, dbo.CRH_Humano.Imp, dbo.CRH_Humano.Inativo, \n"
                    + "       dbo.CRH_Humano.Conexao, dbo.CRH_Humano.CadCla, dbo.CRH_Humano.Classe, dbo.CRH_Classe.Descricao AS ClaDesc, dbo.CRH_Humano.CodBarras, dbo.CRH_Humano.OT1, dbo.CRH_Humano.OT2, \n"
                    + "       dbo.CRH_Humano.OT3, dbo.CRH_Humano.OF1_1, dbo.CRH_Humano.OF1_2, dbo.CRH_Humano.OF1_3, dbo.CRH_Humano.OF1_4, dbo.CRH_Humano.OF1_5, dbo.CRH_Humano.OF1_6, dbo.CRH_Humano.OF2_1, \n"
                    + "       dbo.CRH_Humano.OF2_2, dbo.CRH_Humano.OF2_3, dbo.CRH_Humano.OBS, dbo.CRH_Humano.ClassificacaoContribuinte, dbo.MFis_ClassificacaoContribuinte.Descricao AS ClasContDesc, \n"
                    + "       dbo.CRH_Humano.RegimeTributario, dbo.MFis_RegimeTributario.Descricao AS RegTribDesc, dbo.CRH_Humano.TabelaPreco\n"
                    + "  FROM dbo.CRH_Humano LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Classe ON dbo.CRH_Humano.CadCla = dbo.CRH_Classe.Cadastro AND dbo.CRH_Humano.Classe = dbo.CRH_Classe.Classe LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Humano AS CRH_Rep ON dbo.CRH_Humano.CadRep = CRH_Rep.Cadastro AND dbo.CRH_Humano.Rep = CRH_Rep.Codigo LEFT OUTER JOIN\n"
                    + "       dbo.MFis_ClassificacaoContribuinte ON dbo.CRH_Humano.ClassificacaoContribuinte = dbo.MFis_ClassificacaoContribuinte.Codigo LEFT OUTER JOIN\n"
                    + "       dbo.MFis_RegimeTributario ON dbo.CRH_Humano.RegimeTributario = dbo.MFis_RegimeTributario.Codigo\n"
                    + " WHERE (dbo.CRH_Humano.Cadastro = 'Cli')"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setCnpj(Utils.formataNumero(rst.getString("CPFCNPJ")));
                    if ((rst.getString("RGIE") != null)
                            && (!rst.getString("RGIE").trim().isEmpty())) {
                        imp.setInscricaoestadual(Utils.formataNumero(rst.getString("RGIE")));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCep(Utils.formataNumero(rst.getString("Cep")));
                    imp.setAtivo((rst.getInt("Inativo") == 0 ? true : false));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setValorLimite(rst.getDouble("Limite"));
                    imp.setDataCadastro(rst.getDate("Desde"));
                    imp.setDataNascimento(rst.getDate("Nascimento"));
                    imp.setSexo("Masculino".equals(rst.getString("Sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setTelefone(Utils.formataNumero(rst.getString("Fone1")));

                    if ((rst.getString("Fone2") != null)
                            && (!rst.getString("Fone2").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("1");
                        contato.setNome("TELEFONE 2");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fone2").trim()));
                        imp.getContatos().add(contato);
                    }

                    if ((rst.getString("Celular") != null)
                            && (!rst.getString("Celular").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("2");
                        contato.setNome("CELULAR");
                        contato.setCelular(Utils.formataNumero(rst.getString("Celular").trim()));
                        imp.getContatos().add(contato);
                    }

                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("3");
                        contato.setNome("FAX");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fax").trim()));
                        imp.getContatos().add(contato);
                    }

                    if ((rst.getString("Fone0800") != null)
                            && (!rst.getString("Fone0800").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("4");
                        contato.setNome("FONE 0800");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fone0800")));
                        imp.getContatos().add(contato);
                    }

                    if ((rst.getString("EMail") != null)
                            && (!rst.getString("EMail").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("5");
                        contato.setNome("EMAIL");
                        contato.setEmail(rst.getString("EMail").trim());
                        imp.getContatos().add(contato);
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        String observacao;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT dbo.MCR_Conta.Conta, dbo.MCR_Conta.CaixaVenda, dbo.MCR_Conta.Acerto, dbo.MCR_Conta.SitRec, dbo.MCR_Conta.SitVencto, dbo.MCR_Conta.Data, dbo.MCR_Conta.Status, dbo.MCR_Conta.DtStatus, \n"
                    + "       dbo.MCR_Conta.Situacao, dbo.MCR_Conta.Origem, dbo.MCR_Conta.DocOrigem, dbo.MCR_Conta.Descricao, dbo.MCR_Conta.Barras, dbo.MCR_Conta.PContas, dbo.CPC_Contas.Descricao AS PCDesc, \n"
                    + "       dbo.CPC_Contas.Tipo AS PCTipo, dbo.CPC_Contas.SubTipo AS PCSubTipo, dbo.MCR_Conta.Parcela, dbo.MCR_Conta.Docto, dbo.MCR_Conta.DoctoNF, dbo.MCR_Conta.DtPortador, dbo.MCR_Conta.Portador, \n"
                    + "       dbo.MCR_Conta.DtRestricao, dbo.MCR_Conta.Restricao, dbo.MCR_Conta.CadRepr, dbo.MCR_Conta.Repr, CRH_Repr.Nome AS ReprNome, dbo.MCR_Conta.CadCli, dbo.MCR_Conta.Cliente, \n"
                    + "       CRH_Cli.Tipo AS CliTipo, CRH_Cli.Nascimento AS CliNasc, CRH_Cli.Nome AS CliNome, CRH_Cli.RSocial AS CliRSocial, CRH_Cli.CPFCNPJ AS CliCPFCNPJ, CRH_Cli.RGIE AS CliRGIE, CRH_Cli.Endereco AS CliEnd, \n"
                    + "       CRH_Cli.Numero AS CliNum, CRH_Cli.Endereco + ', ' + CRH_Cli.Numero AS CliEndC, CRH_Cli.Complemento AS CliComp, CRH_Cli.Bairro AS CliBai, CRH_Cli.Cidade AS CliCid, CRH_Cli.UF AS CliUF, \n"
                    + "       CRH_Cli.Cidade + ' - ' + CRH_Cli.UF AS CliCidC, CRH_Cli.CEP AS CliCEP, CRH_Cli.Fone1 AS CliFone1, CRH_Cli.Fone2 AS CliFone2, CRH_Cli.Celular AS CliCel, CRH_Cli.Limite AS CliLimite, dbo.MCR_Conta.Vencto, \n"
                    + "       dbo.MCR_Conta.Valor, dbo.MCR_Conta.MultaP, dbo.MCR_Conta.MultaVl, dbo.MCR_Conta.MultaAplic, dbo.MCR_Conta.JurosPMes, dbo.MCR_Conta.JurosPDia, dbo.MCR_Conta.JurosVlDia, \n"
                    + "       dbo.MCR_Conta.JurosAplic, dbo.MCR_Conta.Total, dbo.MCR_Conta.Anotacoes, dbo.MCR_Conta.DtRecto, CASE WHEN dbo.MCR_Conta.DtRecto IS NULL THEN CASE WHEN CONVERT(INT, GETDATE() \n"
                    + "       - dbo.MCR_Conta.Vencto) < 0 THEN 0 ELSE CONVERT(INT, GETDATE() - dbo.MCR_Conta.Vencto) END WHEN (dbo.MCR_Conta.DtRecto - dbo.MCR_Conta.Vencto) \n"
                    + "       < 0 THEN 0 WHEN (dbo.MCR_Conta.DtRecto - dbo.MCR_Conta.Vencto) > 0 THEN CONVERT(INT, dbo.MCR_Conta.DtRecto - dbo.MCR_Conta.Vencto) ELSE 0 END AS DiasAtraso, dbo.MCR_Conta.Desconto, \n"
                    + "       dbo.MCR_Conta.VlRec, dbo.MCR_Conta.VlDev, dbo.MCR_Conta.RecValor, dbo.MCR_Conta.RecDesconto, dbo.MCR_Conta.RecBaixa, dbo.MCR_Conta.RecDevido, dbo.MCR_Conta.Retorno, dbo.MCR_Conta.Calc01,\n"
                    + "       dbo.MCR_Conta.Calc02, dbo.MCR_Conta.Calc03, dbo.MCR_Conta.Sel, dbo.MCR_Conta.SelAcerto, dbo.MCR_Conta.SelDev, dbo.MCR_Conta.SelME, dbo.MCR_Conta.SelCC, dbo.MCR_Conta.SelImp, \n"
                    + "       dbo.MCR_Conta.SelCob, dbo.MCR_Conta.Conexao, dbo.MCR_Conta.ConexaoAcerto, dbo.MCR_Conta.ConexaoDev, dbo.MCR_Conta.ConexaoME, dbo.MCR_Conta.ConexaoCC, dbo.MCR_Conta.ConexaoImp, \n"
                    + "       dbo.MCR_Conta.ConexaoCob, dbo.MCR_Conta.Contador, dbo.MCR_Conta.Financeira, dbo.MCR_Conta.CobConta, dbo.MCR_Conta.CobEmissao, dbo.MCR_Conta.CobArquivoDig, \n"
                    + "       dbo.MCR_Conta.CobLinhaDigitavel, dbo.MCR_Conta.CobNossoNumero\n"
                    + "  FROM dbo.MCR_Conta LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Humano AS CRH_Repr ON dbo.MCR_Conta.CadRepr = CRH_Repr.Cadastro AND dbo.MCR_Conta.Repr = CRH_Repr.Codigo LEFT OUTER JOIN\n"
                    + "       dbo.CPC_Contas ON dbo.MCR_Conta.PContas = dbo.CPC_Contas.PContas LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Humano AS CRH_Cli ON dbo.MCR_Conta.CadCli = CRH_Cli.Cadastro AND dbo.MCR_Conta.Cliente = CRH_Cli.Codigo\n"
                    + " WHERE dbo.MCR_Conta.Status like '%ANDAMENTO%'\n"
                    + "   AND dbo.MCR_Conta.CadCli = 'cli'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("Conta"));
                    imp.setDataEmissao(rst.getDate("Data"));
                    imp.setDataVencimento(rst.getDate("Vencto"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("DocOrigem")));
                    imp.setValor(rst.getDouble("VlDev"));
                    imp.setIdCliente(Utils.formataNumero(rst.getString("Cliente")));
                    imp.setCnpjCliente(Utils.formataNumero(rst.getString("CliCPFCNPJ")));
                    observacao = (rst.getString("Docto") == null ? "" : "DOCTO - " + rst.getString("Docto") + " PARCELA " + rst.getString("Parcela"));
                    imp.setObservacao(observacao + (rst.getString("Anotacoes") == null ? "" : rst.getString("Anotacoes")));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
