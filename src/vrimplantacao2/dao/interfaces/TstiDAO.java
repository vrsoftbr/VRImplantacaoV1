/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class TstiDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Tsti";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "empcod, empnom, empcnpj "
                    + "from tsl.tsc008a"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("empcod") + rst.getString("empcnpj"), "LOJA " + rst.getString("empcod")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo, descricao from tsl.tslc033\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("descricao"));
                    imp.setMerc2ID(rst.getString("codigo"));
                    imp.setMerc2Descricao(rst.getString("descricao"));
                    imp.setMerc3ID(rst.getString("codigo"));
                    imp.setMerc3Descricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.codigo,\n"
                    + "	p.codbar,\n"
                    + "	upper(p.descpro) descricaocompleta,\n"
                    + "	upper(p.descpdv) descricaoreduzida,\n"
                    + "	p.estoque,\n"
                    + "	p.preco1,\n"
                    + "	p.preco2,\n"
                    + "	p.grupo,\n"
                    + "	p.lucro,\n"
                    + "	p.codst,\n"
                    + "	p.min,\n"
                    + "	p.bala,\n"
                    + "	p.dias,\n"
                    + "	p.icms,\n"
                    + "	p.base,\n"
                    + "	p.lucrob,\n"
                    + "	p.preco3,\n"
                    + "	p.icmsst,\n"
                    + "	p.lucroc,\n"
                    + "	p.ncm,\n"
                    + "	p.custo,\n"
                    + "	p.custofis,\n"
                    + "	p.custocom,\n"
                    + "	p.inativo,\n"
                    + "	p.custom,\n"
                    + "	p.uni,\n"
                    + "	p.tppis,\n"
                    + "	p.tppise,\n"
                    + "	p.tpcofinss,\n"
                    + "	p.tpcofinse,\n"
                    + "	p.pesado,\n"
                    + "	p.qtditemd,\n"
                    + "	p.aliicms,\n"
                    + "	p.cest,\n"
                    + "	reducao,\n"
                    + "	p.natrec,\n"
                    + "	p.estqmax,\n"
                    + "	case cst\n"
                    + "	  when 0 then '40'\n"
                    + "	  when 1 then '00'\n"
                    + "	  when 4 then '40'\n"
                    + "	  when 5 then '60'\n"
                    + "	end cst,\n"
                    + " case codst\n"
                    + "	  when 1  then 18\n"
                    + "	  when 2  then 7\n"
                    + "	  when 3  then 12\n"
                    + "	  when 4  then 25\n"
                    + "	  when 5  then 0\n"
                    + "	  when 6  then 0\n"
                    + "	  when 23 then 11\n"
                    + "	  when 24 then 4.5\n"
                    + "	  when 25 then 4\n"
                    + "	  when 26 then 4.7\n"
                    + "	  when 27 then 13.3\n"
                    + "	  when 28 then 4.32\n"
                    + "	  when 29 then 1.14\n"
                    + "	end aliquota,"
                    //+ "	aliicms aliquota,\n"
                    + "	reducao\n"
                    + "from\n"
                    + "	tsl.tslc003 p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("codbar"));
                    imp.seteBalanca("S".equals(rst.getString("bala")));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setSituacaoCadastro(rst.getInt("inativo") == 0 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setTipoEmbalagem(Utils.acertarTexto(rst.getString("uni")));
                    imp.setValidade(rst.getInt("dias"));
                    imp.setCodMercadologico1(rst.getString("grupo"));
                    imp.setCodMercadologico2(rst.getString("grupo"));
                    imp.setCodMercadologico3(rst.getString("grupo"));
                    imp.setMargem(rst.getDouble("lucro"));
                    imp.setPrecovenda(rst.getDouble("preco1"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoSemImposto());
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("min"));
                    imp.setEstoqueMaximo(rst.getDouble("estqmax"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("tppis"));
                    imp.setPiscofinsCstCredito(rst.getInt("tppise"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("natrec"));

                    imp.setIcmsCst(rst.getInt("cst"));
                    imp.setIcmsAliq(rst.getDouble("aliquota"));
                    imp.setIcmsReducao(rst.getDouble("reducao"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "forcod, fornom, forend, forbai, forcid, forest, forcep,\n"
                    + "forcgc, forins, fortel, forfax, forcon, agencia, banco,\n"
                    + "conta, obs, forcpf, forrg, email, pessoa, fant, cel, site,\n"
                    + "tel2, fax2, cel2, cod_mun\n"
                    + "from tsl.tslc002"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("forcod"));
                    imp.setRazao(rst.getString("fornom"));
                    imp.setFantasia(rst.getString("fant"));
                    imp.setEndereco(rst.getString("forend"));
                    imp.setBairro(rst.getString("forbai"));
                    imp.setMunicipio(rst.getString("forcid"));
                    imp.setUf(rst.getString("forest"));
                    imp.setCep(rst.getString("forcep"));
                    if ((rst.getString("forcgc") != null)
                            && (!rst.getString("forcgc").trim().isEmpty())) {
                        imp.setCnpj_cpf(rst.getString("forcgc"));
                    } else {
                        imp.setCnpj_cpf(rst.getString("forcpf"));
                    }
                    if ((rst.getString("forins") != null)
                            && (!rst.getString("forins").trim().isEmpty())) {
                        imp.setIe_rg(rst.getString("forins"));
                    } else if ((rst.getString("forrg") != null)
                            && (!rst.getString("forrg").trim().isEmpty())) {
                        imp.setIe_rg(rst.getString("forrg"));
                    } else {
                        imp.setIe_rg("ISENTO");
                    }

                    imp.setTel_principal(rst.getString("fortel"));
                    imp.setObservacao(rst.getString("obs"));

                    if ((rst.getString("forfax") != null)
                            && (!rst.getString("forfax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("forfax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("cel") != null)
                            && (!rst.getString("cel").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "CELULAR",
                                null,
                                rst.getString("cel"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "SITE",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("site").toLowerCase()
                        );
                    }
                    if ((rst.getString("tel2") != null)
                            && (!rst.getString("tel2").trim().isEmpty())) {
                        imp.addContato(
                                "5",
                                "TELEFONE 2",
                                rst.getString("tel2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fax2") != null)
                            && (!rst.getString("fax2").trim().isEmpty())) {
                        imp.addContato(
                                "6",
                                "FAX 2",
                                rst.getString("fax2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("cel2") != null)
                            && (!rst.getString("cel2").trim().isEmpty())) {
                        imp.addContato(
                                "7",
                                "CELULAR 2",
                                null,
                                rst.getString("cel2"),
                                TipoContato.NFE,
                                null
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "seq003 as produto,\n"
                    + "seq002 as fornecedor\n"
                    + "from tsl.tslc003_fo"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "clicod, clinom, cliend, clibai, clicid, cliest, clicep,\n"
                    + "pessoa, clicgc, cliins, clicpf, clirg, clitel, clicon,\n"
                    + "agencia, banco, cidbco, obs, endcob, baicob, cidcob, estcob,\n"
                    + "cepcob, clitel2, celular, bloqueio, email, cc, limite,\n"
                    + "dtnasc, motiblo, clifan, empresa, conjuje, cpfconj,\n"
                    + "rgconj, clitel3, clicel2, numero, pai, mae, renda,\n"
                    + "cargo, obs2, clifax\n"
                    + "from tsl.tslc001"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("clicod"));
                    imp.setRazao(Utils.acertarTexto(rst.getString("clinom")));
                    imp.setFantasia(Utils.acertarTexto(rst.getString("clifan")));
                    imp.setEndereco(Utils.acertarTexto(rst.getString("cliend")));
                    imp.setBairro(Utils.acertarTexto(rst.getString("clibai")));
                    imp.setMunicipio(Utils.acertarTexto(rst.getString("clicid")));
                    imp.setUf(Utils.acertarTexto(rst.getString("cliest")));
                    imp.setCep(rst.getString("clicep"));
                    if ((rst.getString("clicgc") != null)
                            && (!rst.getString("clicgc").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("clicgc"));
                    } else if ((rst.getString("clicpf") != null)
                            && (!rst.getString("clicpf").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("clicpf"));
                    }
                    if ((rst.getString("cliins") != null)
                            && (!rst.getString("cliins").trim().isEmpty())) {
                        imp.setInscricaoestadual(Utils.acertarTexto(rst.getString("cliins")));
                    } else if ((rst.getString("clirg") != null)
                            && (!rst.getString("clirg").trim().isEmpty())) {
                        imp.setInscricaoestadual(Utils.acertarTexto(rst.getString("clirg")));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }

                    imp.setTelefone(Utils.formataNumero(rst.getString("clitel")));
                    imp.setCelular(Utils.formataNumero(rst.getString("celular")));
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.setEmail(Utils.acertarTexto(rst.getString("email")).toLowerCase());
                    } else {
                        imp.setEmail("");
                    }

                    imp.setObservacao(Utils.acertarTexto(rst.getString("obs")));
                    imp.setObservacao2(Utils.acertarTexto(rst.getString("obs2")));
                    imp.setValorLimite(rst.getDouble("limite"));
                    if ((rst.getString("bloqueio") != null)
                            && (!rst.getString("bloqueio").trim().isEmpty())) {
                        if ("S".equals(rst.getString("bloqueio").trim())) {
                            imp.setBloqueado(true);
                            imp.setPermiteCreditoRotativo(false);
                            imp.setPermiteCheque(false);
                        } else {
                            imp.setBloqueado(false);
                            imp.setPermiteCreditoRotativo(true);
                            imp.setPermiteCheque(true);
                        }
                    } else {
                        imp.setBloqueado(false);
                        imp.setPermiteCreditoRotativo(true);
                        imp.setPermiteCheque(true);
                    }

                    imp.setCobrancaEndereco(Utils.acertarTexto(rst.getString("endcob")));
                    imp.setCobrancaBairro(Utils.acertarTexto(rst.getString("baicob")));
                    imp.setCobrancaMunicipio(Utils.acertarTexto(rst.getString("cidcob")));
                    imp.setCobrancaUf(Utils.acertarTexto(rst.getString("estcob")));
                    imp.setCobrancaCep(Utils.acertarTexto(rst.getString("cepcob")));
                    imp.setEmpresa(Utils.acertarTexto(rst.getString("empresa")));
                    imp.setCargo(Utils.acertarTexto(rst.getString("cargo")));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setNomePai(Utils.acertarTexto(rst.getString("pai")));
                    imp.setNomeMae(Utils.acertarTexto(rst.getString("mae")));
                    imp.setNomeConjuge(Utils.acertarTexto(rst.getString("conjuje")));

                    if ((rst.getString("clitel2") != null)
                            && (!rst.getString("clitel2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                (rst.getString("clitel2").length() > 14 ? rst.getString("clitel2").substring(0, 14) : rst.getString("clitel2").replace("'", "")),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("clitel3") != null)
                            && (!rst.getString("clitel3").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 3",
                                (rst.getString("clitel3").length() > 14 ? rst.getString("clitel3").substring(0, 14) : rst.getString("clitel3").replace("'", "")),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("clicel2") != null)
                            && (!rst.getString("clicel2").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "CELULAR 2",
                                null,
                                (rst.getString("clicel2").length() > 14 ? rst.getString("clicel2").substring(0, 14) : rst.getString("clicel2").replace("'", "")),
                                null
                        );
                    }
                    if ((rst.getString("clifax") != null)
                            && (!rst.getString("clifax").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "FAX",
                                (rst.getString("clifax").length() > 14 ? rst.getString("clifax").substring(0, 14) : rst.getString("clifax").replace("'", "")),
                                null,
                                null
                        );
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
        java.sql.Date dtEmissao, dtVencimento;
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "recseq, reccod, rectitulo, reccli,\n"
                    + "recemiss, recvenci, recobs, recvalor,\n"
                    + "recvalpag, recparc\n"
                    + "from tsl.tsm003\n"
                    + "where recbaixa <> 'S'\n"
                    + "and recvenci <> '000-00-00'\n"
                    + "order by recvenci desc"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("recseq") + "-"
                            + rst.getString("reccli") + "-"
                            + getLojaOrigem());
                    imp.setIdCliente(rst.getString("reccli"));

                    if ((rst.getString("rectitulo") != null)
                            && (!rst.getString("rectitulo").trim().isEmpty())) {
                        if (!Utils.encontrouLetraCampoNumerico(rst.getString("rectitulo"))) {
                            imp.setNumeroCupom(rst.getString("rectitulo").trim());
                        }
                    }

                    if ("0000-00-00".equals(rst.getString("recemiss"))) {
                        dtEmissao = new Date(new java.util.Date().getTime());
                        imp.setDataEmissao(dtEmissao);
                    } else {
                        imp.setDataEmissao(rst.getDate("recemiss"));
                    }
                    if ("0000-00-00".equals(rst.getString("recvenci"))) {
                        dtVencimento = new Date(new java.util.Date().getTime());
                        imp.setDataVencimento(dtVencimento);
                    } else {
                        imp.setDataVencimento(rst.getDate("recvenci"));
                    }

                    imp.setValor(rst.getDouble("recvalor"));
                    imp.setParcela(rst.getInt("recparc"));
                    imp.setObservacao(rst.getString("recobs") + " TITULO "
                            + rst.getString("rectitulo") + " RECCOD " + rst.getString("reccod"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 Cod_Classe codigo,\n"
                    + "	 Descricao_Classe descricao,\n"
                    + "	 Cst_Classe cst,\n"
                    + "	 Ecf_Aliquota_Classe aliquota,\n"
                    + "	 Nota_Reducao_Classe reducao\n"
                    + "from tbl_classe"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"),
                            rs.getString("descricao"),
                            Utils.stringToInt(rs.getString("cst")),
                            Utils.stringToDouble(rs.getString("aliquota")),
                            Utils.stringToDouble(rs.getString("reducao"))));
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "seq, ag, cc, dcc, numero, valor,\n"
                    + "nome, banco, documento, pre, fone\n"
                    + "from tsl.tsm004 "
                    + "where pre <> '0000-00-00'"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("seq"));
                    imp.setAgencia(rst.getString("ag"));
                    imp.setConta(rst.getString("cc") + rst.getString("dcc"));
                    imp.setNumeroCheque(rst.getString("numero"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNome(rst.getString("nome"));
                    imp.setBanco(Integer.parseInt(Utils.formataNumero(rst.getString("banco"))));
                    imp.setCpf(rst.getString("documento"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setDate(rst.getDate("pre"));
                    imp.setDataDeposito(rst.getDate("pre"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
