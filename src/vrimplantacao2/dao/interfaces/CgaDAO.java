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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class CgaDAO extends InterfaceDAO implements MapaTributoProvider {

    public String id_loja;

    @Override
    public String getSistema() {
        if ((id_loja != null) && (!id_loja.trim().isEmpty())) {
            return "Cga" + id_loja;
        } else {
            return "Cga";
        }
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret011.\"SUBCod\", ret011.\"SUBDesc\" "
                    + "from ret011"
            )) {
                while (rst.next()) {
                    if ((rst.getString("SUBDesc") != null)
                            && (!rst.getString("SUBDesc").trim().isEmpty())) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("SUBCod"));
                        imp.setDescricao(rst.getString("SUBDesc"));
                        vResult.add(imp);
                    }
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret018.\"SECCod\", ret018.\"SECDesc\", ret019.\"GRUCod\",\n"
                    + "ret019.\"GRUDesc\", ret020.\"SUBGCod\",ret020.\"SUBGDesc\"\n"
                    + "from ret018\n"
                    + "INNER JOIN RET019 ON RET018.\"SECCod\"  = RET019.\"SECCod\"\n"
                    + "INNER JOIN ret020 ON RET020.\"GRUCod\" = RET019.\"GRUCod\"\n"
                    + "order by RET018.\"SECCod\", RET020.\"GRUCod\""
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("GRUCod"));
                    imp.setMerc1Descricao(rst.getString("GRUDesc"));
                    imp.setMerc2ID(rst.getString("SUBGCod"));
                    imp.setMerc2Descricao(rst.getString("SUBGDesc"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("SUBGDesc"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret051.\"PRODCod\", ret051.\"PRODNome\",\n"
                    + "ret051.\"PRODNomeRed\", ret051.\"PRODEtq\", ret051.\"PRODCadast\", ret051.\"PRODCusto\",\n"
                    + "ret051.\"PRODMargem\", ret051.\"PRODVenda\", ret051.\"GRUCod\",\n"
                    + "ret051.\"SUBGCod\", ret051.prodai, ret051.\"SECCod\",\n"
                    + "ret051.\"PRODBARCod\" ean, ret051.clasfisccod, ret051.natreccod,\n"
                    + "ret051.prodstcofinsent, ret051.prodstcofins, ret051.\"SUBCod\",\n"
                    + "ret051.prodsdo, prodqtemb, ret051.\"ALIQCod\", ret051.\"TABBCod\" cstSaida,\n"
                    + "al1.\"ALIQNFPerc\" aliqDebito, al1.\"ALIQRedNF\" redDebito, ret051.aliqcred,\n"
                    + "ret051.tabbcred cstEntrada, al2.\"ALIQNFPerc\" aliqCredito, al2.\"ALIQRedNF\" redCredito,\n"
                    + "ret041.clasfisccod ncm, ret041.clasfisccest CODCEST, ret051.\"PRODUnid\", \n"
                    + "ret051.prodcustofinal, ret051.prodcustofinalvenda\n "
                    + "from RET051\n"
                    + "left join ret041 on ret041.clasfisccod = ret051.clasfisccod\n"
                    + "left join RET053 on RET053.\"PRODCod\" = ret051.\"PRODCod\"\n"
                    + "left join ret016 al1 on al1.\"ALIQCod\" = ret051.\"ALIQCod\"\n"
                    + "left join ret016 al2 on al2.\"ALIQCod\" = ret051.aliqcred\n"
                    + "order by ret051.\"PRODCod\""
            /*+ "union all\n"
             + "select ret051.\"PRODCod\", ret051.\"PRODNome\",\n"
             + "ret051.\"PRODNomeRed\", ret051.\"PRODEtq\", ret051.\"PRODCadast\", ret051.\"PRODCusto\",\n"
             + "ret051.\"PRODMargem\", ret051.\"PRODVenda\", ret051.\"GRUCod\",\n"
             + "ret051.\"SUBGCod\", ret051.prodai, ret051.\"SECCod\",\n"
             + "ean.\"BARCod\" ean, ret051.clasfisccod, ret051.ncm,\n"
             + "ret051.prodstcofinsent, ret051.prodstcofins, ret051.\"SUBCod\",\n"
             + "ret051.prodsdo, prodqtemb, ret051.\"ALIQCod\", ret051.\"TABBCod\" cstSaida,\n"
             + "al1.\"ALIQNFPerc\" aliqDebito, al1.\"ALIQRedNF\" redDebito, ret051.aliqcred,\n"
             + "ret051.tabbcred cstEntrada, al2.\"ALIQNFPerc\" aliqCredito, al2.\"ALIQRedNF\" redCredito,\n"
             + "ret041.clasfisccod ncm, ret041.clasfisccest CODCEST, ret051.\"PRODUnid\"\n"
             + "from RET051\n"
             + "left join ret041 on ret041.clasfisccod = ret051.clasfisccod\n"
             + "left join RET053 on RET053.\"PRODCod\" = ret051.\"PRODCod\"\n"
             + "left join ret016 al1 on al1.\"ALIQCod\" = ret051.\"ALIQCod\"\n"
             + "left join ret016 al2 on al2.\"ALIQCod\" = ret051.aliqcred\n"
             + "left join ret052 ean on ean.\"PRODCod\" = ret051.\"PRODCod\"\n"
             + "where cast(ret051.\"PRODCod\" as numeric(14,0)) > 0\n"
             + "and cast(ean.\"BARCod\" as numeric(14,0)) > 999999"*/
            )) {
                int contador = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportId(rst.getString("PRODCod"));

                    long codigoProduto;
                    codigoProduto = Long.parseLong(imp.getImportId());
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("PRODNome"));
                    imp.setDescricaoReduzida(rst.getString("PRODNomeRed"));
                    imp.setDescricaoGondola(rst.getString("PRODEtq"));
                    imp.setDataCadastro(rst.getDate("PRODCadast"));
                    imp.setMargem(rst.getDouble("PRODMargem"));
                    imp.setCustoComImposto(rst.getDouble("prodcustofinal"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("PRODVenda"));
                    imp.setEstoque(rst.getDouble("prodsdo"));
                    imp.setCodMercadologico1(rst.getString("GRUCod"));
                    imp.setCodMercadologico2(rst.getString("SUBGCod"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("SUBCod"));
                    imp.setNcm(rst.getString("clasfisccod"));
                    imp.setCest(rst.getString("CODCEST"));
                    imp.setQtdEmbalagem(rst.getInt("prodqtemb") == 0 ? 1 : rst.getInt("prodqtemb"));
                    imp.setTipoEmbalagem(rst.getString("PRODUnid"));

                    if ((rst.getString("prodai") != null)
                            && (!rst.getString("prodai").trim().isEmpty())) {
                        imp.setSituacaoCadastro(rst.getString("prodai").contains("A") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    }

                    imp.setPiscofinsCstDebito(Integer.parseInt(Utils.formataNumero(rst.getString("prodstcofins"))));
                    imp.setPiscofinsCstCredito(Integer.parseInt(Utils.formataNumero(rst.getString("prodstcofinsent"))));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreccod"));
                    imp.setIcmsDebitoId(rst.getString("ALIQCod"));
                    imp.setIcmsCreditoId(rst.getString("ALIQCRED"));

                    vResult.add(imp);
                    contador++;
                    ProgressBar.setStatus("Carregando dados..." + contador);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "ret052.\"BARCod\",\n"
                    + "ret052.\"PRODCod\",\n"
                    + "ret052.barunbxa,\n"
                    + "ret051.\"PRODUnid\"\n"
                    + "from RET052\n"
                    + "inner join ret051 on ret051.\"PRODCod\" = ret052.\"PRODCod\""
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("PRODCod"));
                    imp.setEan(rst.getString("BARCod"));
                    imp.setQtdEmbalagem(rst.getInt("barunbxa"));
                    imp.setTipoEmbalagem(rst.getString("PRODUnid"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    ret007.\"FORCod\",\n"
                    + "    ret007.\"FORRazao\",\n"
                    + "    ret007.\"FORFant\",\n"
                    + "    ret007.\"FOREnd\",\n"
                    + "    ret007.\"FORCep\",\n"
                    + "    ret501.cidibge,\n"
                    + "    ret501.\"CIDNome\",\n"
                    + "    ret501.ciduf,\n"
                    + "    ret007.\"FORBairro\",\n"
                    + "    ret007.fornumero,\n"
                    + "    ret007.forcomplemento,\n"
                    + "    coalesce(nullif(coalesce(trim(ret007.forcnpj),''),''),\n"
                    + "    nullif(coalesce(trim(ret007.forcpf),''),'')) forcnpjcpf,\n"
                    + "    ret007.forie,\n"
                    + "    ret007.forativo,\n"
                    + "    ret007.\"FORFone1\",\n"
                    + "    ret007.\"FORFone2\",\n"
                    + "    ret007.\"FORFax\",\n"
                    + "    ret007.\"FORContato\",\n"
                    + "    ret007.\"FORBco\",\n"
                    + "    ret007.\"FORAg\",\n"
                    + "    ret007.\"FORCta\",\n"
                    + "    ret007.\"FOREmail\",\n"
                    + "    ret007.forobs,\n"
                    + "    ret007.forobsmemo,\n"
                    + "    ret007.forinclusao,\n"
                    + "    ret007.\"FORRep\",\n"
                    + "    ret007.\"FORRepF1\",\n"
                    + "    ret007.\"FORRepF2\",\n"
                    + "    ret007.forrepemail\n"
                    + "from\n"
                    + "    ret007\n"
                    + "    left join ret501 on ret501.\"CIDCod\" = ret007.\"CIDCod\""
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("FORCod"));
                    imp.setRazao(rst.getString("FORRazao"));
                    imp.setFantasia(rst.getString("FORFant"));
                    imp.setEndereco(rst.getString("FOREnd"));
                    imp.setBairro(rst.getString("FORBairro"));
                    imp.setCep(rst.getString("FORCep"));
                    imp.setMunicipio(rst.getString("CIDNome"));
                    imp.setIbge_municipio(rst.getInt("cidibge"));
                    imp.setUf(rst.getString("ciduf"));
                    imp.setNumero(rst.getString("fornumero"));
                    imp.setComplemento(rst.getString("forcomplemento"));
                    imp.setCnpj_cpf(rst.getString("forcnpjcpf"));
                    imp.setIe_rg(rst.getString("forie"));
                    imp.setAtivo(true);
                    imp.setTel_principal(rst.getString("FORFone1"));
                    imp.setDatacadastro(rst.getDate("forinclusao"));
                    imp.setObservacao(rst.getString("forobsmemo"));
                    if ((rst.getString("FORFone2") != null)
                            && (!rst.getString("FORFone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("FORFone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("FORFax") != null)
                            && (!rst.getString("FORFax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("FORFax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("FOREmail") != null)
                            && (!rst.getString("FOREmail").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("FOREmail")
                        );
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
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret154.forcod, ret154.prodcod,\n"
                    + "ret154.prodbarcod, ret154.codfabricante\n"
                    + "from RET154"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("forcod"));
                    imp.setIdProduto(rst.getString("prodcod"));
                    imp.setCodigoExterno(rst.getString("codfabricante"));
                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret028.\"CLICod\", ret028.\"CLINome\", ret028.\"CLIFantasia\", ret028.\"CLIContato\",\n"
                    + "ret028.\"CLIEnd\", ret028.\"CLIBairro\", ret028.\"CLICep\", ret501.cidibge, ret501.\"CIDNome\",\n"
                    + "ret501.ciduf, ret028.\"CLIFone1\", ret028.\"CLIFone2\", ret028.\"CLIFax\", ret028.clicpf,\n"
                    + "ret028.clirg, ret028.clicnpj, ret028.cliie, ret028.\"CLIInclusao\", ret028.\"CLICadastro\",\n"
                    + "ret028.\"CLIEmail\", ret028.\"CLINasc\", ret028.clinumero, ret028.clicomplemento, ret028.\"CLICred\", \n"
                    + "ret028.\"CLIEstCIV\", ret028.clisexo, ret028.\"CLIPai\", ret028.\"CLIMae\", ret028.clicj,\n"
                    + "ret028.clicjcpf, ret028.clicjrg, ret028.\"CLICJNasc\", ret028.\"CLIObs\", ret028.\"CLIBco1\",\n"
                    + "ret028.\"CLIAg1\", ret028.\"CLICta1\", ret028.\"CLILIMCred\", ret028.\"CLICPTrab\", ret028.\"CLITrab\",\n"
                    + "ret028.\"CLICPRenda\", ret028.\"CLITrabFone\", ret028.cliativo, ret028.clilimcc\n"
                    + "from ret028\n"
                    + "left join RET501 on RET501.\"CIDCod\"  = ret028.\"CIDCod\" "
            )) {
                int contador = 1;
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CLICod"));
                    imp.setRazao(rst.getString("CLINome"));
                    imp.setFantasia(rst.getString("CLIFantasia"));
                    imp.setEndereco(rst.getString("CLIEnd"));
                    imp.setBairro(rst.getString("CLIBairro"));
                    imp.setCep(rst.getString("CLICep"));
                    imp.setMunicipioIBGE(rst.getInt("cidibge"));
                    imp.setMunicipio(rst.getString("CIDNome"));
                    imp.setUf(rst.getString("ciduf"));
                    imp.setNumero(rst.getString("clinumero"));
                    imp.setComplemento(rst.getString("clicomplemento"));
                    imp.setValorLimite(rst.getDouble("CLILIMCred") > 0 ? rst.getDouble("CLILIMCred") : rst.getDouble("clilimcc"));
                    imp.setDataCadastro(rst.getDate("CLICadastro"));
                    imp.setDataNascimento(rst.getDate("CLINasc"));
                    if ((rst.getString("clicpf") != null)
                            && (!rst.getString("clicpf").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("clicpf"));
                    } else if ((rst.getString("clicnpj") != null)
                            && (!rst.getString("clicnpj").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("clicnpj"));
                    } else {
                        imp.setCnpj("");
                    }
                    if ((rst.getString("clirg") != null)
                            && (!rst.getString("clirg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("clirg"));
                    } else if ((rst.getString("cliie") != null)
                            && (!rst.getString("cliie").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("cliie"));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    if ((rst.getString("CLIEstCIV") != null)
                            && (!rst.getString("CLIEstCIV").trim().isEmpty())) {
                        if (null != rst.getString("CLIEstCIV").trim()) {
                            switch (rst.getString("CLIEstCIV").trim()) {
                                case "O":
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                                case "C":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "S":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "V":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                                case "D":
                                    imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }
                    if ((rst.getString("clisexo") != null)
                            && (!rst.getString("clisexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("clisexo").trim())) {
                            imp.setSexo(TipoSexo.FEMININO);
                        } else {
                            imp.setSexo(TipoSexo.MASCULINO);
                        }
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    if ((rst.getString("cliativo") != null)
                            && (!rst.getString("cliativo").trim().isEmpty())) {
                        imp.setAtivo("S".equals(rst.getString("cliativo")) ? true : false);
                    } else {
                        imp.setAtivo(true);
                    }

                    if ((rst.getString("CLICred") != null)
                            && (!rst.getString("CLICred").trim().isEmpty())) {
                        if ("S".equals(rst.getString("CLICred").trim())) {
                            imp.setBloqueado(false);
                            imp.setPermiteCreditoRotativo(true);
                            imp.setPermiteCheque(true);
                        } else {
                            imp.setBloqueado(true);
                            imp.setPermiteCreditoRotativo(false);
                            imp.setPermiteCheque(false);
                        }
                    } else {
                        imp.setBloqueado(true);
                        imp.setPermiteCreditoRotativo(false);
                        imp.setPermiteCheque(false);
                    }
                    imp.setNomePai(rst.getString("CLIPai"));
                    imp.setNomeMae(rst.getString("CLIMae"));
                    imp.setNomeConjuge(rst.getString("clicj"));
                    imp.setObservacao(rst.getString("CLIObs"));
                    imp.setEmpresa(rst.getString("CLITrab"));
                    imp.setEmpresaTelefone(rst.getString("CLITrabFone"));
                    imp.setSalario(rst.getDouble("CLICPRenda"));
                    imp.setEmail(rst.getString("CLIEmail"));
                    if ((rst.getString("CLIFone2") != null)
                            && (!rst.getString("CLIFone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("CLIFone2"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("CLIFax") != null)
                            && (!rst.getString("CLIFax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                rst.getString("CLIFax"),
                                null,
                                null
                        );
                    }
                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados..." + contador);
                    contador++;
                }
                return vResult;
            }
        }
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ret010.\"CLICod\", ret010.\"CCTCupom\", ret010.cctecf, ret010.\"CCTData\",\n"
                    + "ret010.cctvcto, ret010.\"CCTDebito\", ret010.cctobs, ret010.\"CCTPgto\", ret010.\"CCTCod\"\n"
                    + "from ret010\n"
                    + "where ret010.\"CCTPG\" = 'N'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("CCTCod"));
                    imp.setIdCliente(rst.getString("CLICod"));
                    imp.setNumeroCupom(rst.getString("CCTCupom"));
                    imp.setEcf(rst.getString("cctecf"));
                    imp.setDataEmissao(rst.getDate("CCTData"));
                    imp.setDataVencimento(rst.getDate("cctvcto"));
                    imp.setValor(rst.getDouble("CCTDebito"));
                    imp.setObservacao(rst.getString("cctobs"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ch.\"CHQCod\", ch.\"CHQBco\", ch.\"CHQConta\", ch.\"CHQAge\", ch.\"CHQNum\",\n"
                    + "ch.\"CHQVcto\", ch.\"CHQLcto\", ch.\"CHQValor\", ch.\"CLICod\", ch.\"CHQTitular\",\n"
                    + "ch.\"CHQDoc\", ch.\"CHQObs\", cl.clirg, cl.\"CLIFone1\"\n"
                    + "from ret033 ch\n"
                    + "left join ret028 cl on cl.\"CLICod\" = ch.\"CLICod\"\n"
                    + "where ch.\"CHQBaixa\" is null"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("CHQCod"));
                    imp.setAgencia(rst.getString("CHQAge"));
                    imp.setConta(rst.getString("CHQConta"));
                    imp.setNumeroCheque(rst.getString("CHQNum"));
                    imp.setNumeroCupom("0");
                    imp.setValor(rst.getDouble("CHQValor"));
                    imp.setNome(rst.getString("CHQTitular"));
                    imp.setCpf(rst.getString("CHQDoc"));
                    imp.setRg(rst.getString("clirg"));
                    imp.setTelefone(rst.getString("CLIFone1"));
                    imp.setObservacao(rst.getString("CHQObs"));
                    imp.setDate(rst.getDate("CHQLcto"));
                    imp.setDataDeposito(rst.getDate("CHQVcto"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public void importarOfertas(int idLojaVR, int idLojaCliente, String impLoja) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertas(idLojaVR, idLojaCliente);
        new OfertaDAO().salvar(ofertas, idLojaVR, impLoja);
    }

    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception {
        List<OfertaVO> ofertas = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    ret051.\"PRODCod\", ret051.\"PRODVendaPR\",\n"
                    + "    ret051.\"PRODPromoIN\", ret051.\"PRODPromoFM\"\n"
                    + "from\n"
                    + "    ret051\n"
                    + "where ret051.\"PRODPromoFM\" >= current_date"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produto(rst.getInt("PRODCod"));
                    vo.setDatainicio(rst.getDate("PRODPromoIN"));
                    vo.setDatatermino(rst.getDate("PRODPromoFM"));
                    vo.setPrecooferta(rst.getDouble("PRODVendaPR"));
                    ofertas.add(vo);
                }
            }
        }
        return ofertas;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> vResult = new ArrayList<>();
        String observacao;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    p.\"FORCod\"||' - '||p.\"PAGDoc\"||' - '||p.\"PAGParc\" id,\n"
                    + "    p.\"FORCod\", f.\"FORRazao\", f.forcnpj, p.\"PAGDoc\", p.\"PAGParc\", p.\"PAGPgto\",\n"
                    + "    p.\"PAGDup\", p.pagcmp, p.\"PAGVcto\", p.pagvlr, p.\"PAGJuros\", p.\"PAGDesc\", p.pagobs\n"
                    + "FROM\n"
                    + "    RET091 p\n"
                    + "inner join\n"
                    + "    ret007 f on f.\"FORCod\" = p.\"FORCod\"\n"
                    + "where\n"
                    + "    p.\"PAGPgto\" is null\n"
                    + "order by\n"
                    + "    p.\"FORCod\"||' - '||p.\"PAGDoc\"||' - '||p.\"PAGParc\""
            )) {
                while (rst.next()) {
                    observacao = "";

                    if ((rst.getString("PAGDoc") != null)
                            && (!rst.getString("PAGDoc").trim().isEmpty())) {
                        observacao = "DOC. " + rst.getString("PAGDoc").trim() + " ";
                    }
                    if ((rst.getString("PAGDup") != null)
                            && (!rst.getString("PAGDup").trim().isEmpty())) {
                        observacao = observacao + "DUP. " + rst.getString("PAGDup") + " ";
                    }
                    if ((rst.getString("PAGParc") != null)
                            && (!rst.getString("PAGParc").trim().isEmpty())) {
                        observacao = observacao + "PARCELA. " + rst.getString("PAGParc") + " ";
                    }
                    if ((rst.getString("pagobs") != null)
                            && (!rst.getString("pagobs").trim().isEmpty())) {
                        observacao = observacao + "OBS." + rst.getString("pagobs") + " ";
                    }

                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("FORCod"));
                    imp.setDataEmissao(rst.getDate("pagcmp"));
                    imp.setDataEntrada(rst.getDate("pagcmp"));
                    imp.setNumeroDocumento(rst.getString("PAGDoc"));
                    imp.setFinalizada(false);
                    imp.setValor(rst.getDouble("pagvlr"));
                    imp.setObservacao(observacao);
                    imp.setDataHoraAlteracao(rst.getTimestamp("pagcmp"));
                    imp.addVencimento(rst.getDate("PAGVcto"), imp.getValor());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "ret016.\"ALIQCod\",\n"
                    + "ret016.\"ALIQDesc\",\n"
                    + "ret016.\"ALIQNFPerc\",\n"
                    + "ret016.\"ALIQRedNF\",\n"
                    + "ret016.\"ALIQPerc\"\n"
                    + "from ret016\n"
                    + "order by ret016.\"ALIQCod\" asc"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("ALIQCod"), rs.getString("ALIQDesc")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "ret000.\"Codigo\",\n"
                    + "ret000.\"Fantasia\",\n"
                    + "ret000.\"CNPJ\"\n"
                    + "from ret000\n"
                    + "order by ret000.\"Codigo\""
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("Codigo").trim(), rs.getString("Fantasia").trim()));
                }
            }
        }
        return lojas;
    }
}
