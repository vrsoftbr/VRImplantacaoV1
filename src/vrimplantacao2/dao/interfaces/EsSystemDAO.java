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
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class EsSystemDAO extends InterfaceDAO {

    private ConexaoDBF connDBF = new ConexaoDBF();
    public String pathBancoDBF;

    @Override
    public String getSistema() {
        return "EsSystem";
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        connDBF.abrirConexao(pathBancoDBF);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT COD_PRO, NOME "
                    + "FROM PRODUTOS "
                    + "WHERE COD_PRO IN (SELECT DISTINCT COD_PAI "
                    + "FROM PRODUTOS WHERE COD_PAI IS NOT NULL)"
            )) {
                int contador = 1;
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("COD_PRO"));
                    imp.setDescricao(rst.getString("NOME"));
                    vResult.add(imp);                    
                    ProgressBar.setStatus("Carregando dados..."+contador);
                    contador++;
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        connDBF.abrirConexao(pathBancoDBF);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT COD_TIP, NOME\n"
                    + "FROM TIP_PROD\n"
                    + "ORDER BY COD_TIP"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("COD_TIP"));
                    imp.setMerc1Descricao(rst.getString("NOME"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        boolean eBalanca;
        connDBF.abrirConexao(pathBancoDBF);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT P.COD_PRO, P.CODBAR, P.NOME, P.UNIDADE, P.ST, P.COD_PAI, P.COD_TRIB, \n"
                    + "P.QTE_CX, P.NBM, P.NAT_REC, P.PESOLIQ, P.PESOBRUTO, P.COFINS, P.PIS, P.STCOFINS, \n"
                    + "P.CEST, P.DATA_INCL, P.DATA_ALT, E.P_CUSTO, E.P_VENDA, E.QTE, E.QTE_MIN,\n"
                    + "E.PMCMARKUP, E.VALIDADE, P.COD_TIP\n"
                    + "FROM PRODUTOS P\n"
                    + "INNER JOIN ESTOQUES E ON E.COD_PRO = P.COD_PRO"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    long codigoProduto;
                    codigoProduto = Long.parseLong(Utils.formataNumero(rst.getString("COD_PRO")));
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("VALIDADE"));
                    } else {
                        imp.seteBalanca(false);
                    }
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("COD_PRO"));
                    if (imp.isBalanca()) {
                        imp.setEan(rst.getString("COD_PRO"));
                    } else {
                        imp.setEan(rst.getString("CODBAR"));
                    }
                    imp.setDescricaoCompleta(rst.getString("NOME"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("COD_TIP"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("COD_PAI"));
                    imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                    imp.setNcm(rst.getString("NBM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setValidade(rst.getInt("VALIDADE"));
                    imp.setPesoLiquido(rst.getDouble("PESOLIQ"));
                    imp.setPesoBruto(rst.getDouble("PESOBRUTO"));
                    imp.setMargem(rst.getDouble("PMCMARKUP"));
                    imp.setPrecovenda(rst.getDouble("P_VENDA"));
                    imp.setCustoComImposto(rst.getDouble("P_CUSTO"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMinimo(rst.getDouble("QTE_MIN"));
                    imp.setEstoque(rst.getDouble("QTE"));
                    imp.setPiscofinsCstDebito(7);
                    imp.setPiscofinsCstCredito(71);
                    imp.setPiscofinsNaturezaReceita(999);
                    imp.setIcmsCst(40);
                    imp.setIcmsAliq(0);
                    imp.setIcmsReducao(0);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        String obs1, obs2, obs3, nomeVendedor, foneVendedor;
        connDBF.abrirConexao(pathBancoDBF);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT COD_FOR, NOME, FANTASIA, ENDERECO, BAIRRO, CIDADE, CEP,\n"
                    + "ESTADO, CGC_CPF, INSC_RG, FONE, FAX, HOMEPAGE, E_MAIL, NOMEV, \n"
                    + "FONEV, OBS1, OBS2, OBS3\n"
                    + "FROM FORNECED\n"
                    + "ORDER BY COD_FOR"
            )) {
                while (rst.next()) {
                    obs1 = rst.getString("OBS1");
                    obs2 = rst.getString("OBS2");
                    obs3 = rst.getString("OBS3");
                    nomeVendedor = "";
                    foneVendedor = "";
                    if ((rst.getString("NOMEV") != null)
                            && (!rst.getString("NOMEV").trim().isEmpty())) {
                        nomeVendedor = "NOME VENDEDOR - " + rst.getString("NOMEV").trim();
                    }
                    if ((rst.getString("FONEV") != null)
                            && (!rst.getString("FONEV").trim().isEmpty())) {
                        foneVendedor = "FONE VENDEDOR - " + rst.getString("FONEV").trim();
                    }
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("COD_FOR"));
                    imp.setRazao(rst.getString("NOME"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("CGC_CPF")));
                    imp.setIe_rg(Utils.formataNumero(rst.getString("INSC_RG")));
                    imp.setTel_principal(Utils.formataNumero(rst.getString("FONE")));
                    imp.setObservacao(nomeVendedor + " " + foneVendedor);
                    if (Utils.stringToLong(rst.getString("FAX")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("FAX");
                        cont.setTelefone(rst.getString("FAX"));
                        imp.getContatos().put(cont, "1");
                    }
                    if ((rst.getString("HOMEPAGE") != null)
                            && (!rst.getString("HOMEPAGE").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("HOMEPAGE");
                        cont.setEmail(rst.getString("HOMEPAGE"));
                    }
                    if ((rst.getString("E_MAIL") != null)
                            && (!rst.getString("E_MAIL").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("E_MAIL");
                        cont.setEmail(rst.getString("E_MAIL"));
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        connDBF.abrirConexao(pathBancoDBF);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT COD_CLI, NOME, FANTASIA, DATANASC, ENDERECO, XCPL, BAIRRO,\n"
                    + "BAIRRO, CIDADE, ESTADO, CEP, CGC_CPF, INSC_RG, LIM_CRED, FONE,\n"
                    + "FAX, E_MAIL, HOMEPAGE, MAE, PAI, TRABALHO, OBS, PROFISSAO, CONJUGE,\n"
                    + "CONJCPF, CONJRG, CONJNASC, CONJPROF\n"
                    + "FROM CLIENTES"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("COD_CLI"));
                    imp.setRazao(rst.getString("NOME"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setComplemento(rst.getString("XCPL"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setCnpj(Utils.formataNumero(rst.getString("CGC_CPF")));
                    imp.setInscricaoestadual(Utils.formataNumero(rst.getString("INSC_RG")));
                    imp.setValorLimite(rst.getDouble("LIM_CRED"));
                    imp.setTelefone(rst.getString("FONE"));
                    imp.setNomePai(rst.getString("PAI"));
                    imp.setNomeMae(rst.getString("MAE"));
                    imp.setEmpresa(rst.getString("TRABALHO"));
                    imp.setCargo(rst.getString("PROFISSAO"));
                    imp.setNomeConjuge(rst.getString("CONJUGE"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setFax(Utils.formataNumero(rst.getString("FAX")));
                    imp.setEmail(rst.getString("E_MAIL"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
