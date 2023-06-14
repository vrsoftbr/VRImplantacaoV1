/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class CPlus2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "CPlus";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "e.codempresa,\n"
                    + "e.nomeempresa,\n"
                    + "e.nomefantasia\n"
                    + "from empresa e"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codempresa"), rst.getString("nomeempresa")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "t.codtributacaoecf codigo,\n"
                    + "t.nometributacaoecf||' - '||t.tipotributacao||' - '||\n"
                    + "'ALIQ. '||t.aliqtributacao||' - RDZ. '||coalesce(t.aliqreducaobaseicms, 0) descri\n"
                    + "from tributacaoecf t\n"
                    + "order by t.codtributacaoecf"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descri")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "s.codsec,\n"
                    + "s.nomesecao "
                    + "from secao s\n"
                    + "order by s.codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codsec"));
                    imp.setMerc1Descricao(rst.getString("nomesecao"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(rst.getString("nomesecao"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("nomesecao"));
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
                    "select\n"
                    + "p.codprod,\n"
                    + "p.codigo ean,\n"
                    + "p.codsec,\n"
                    + "p.nomeprod,\n"
                    + "p.nomeprodcurto,\n"
                    + "p.flaginativo,\n"
                    + "p.unidade, \n"
                    + "pr.preco,\n"
                    + "pr.margem,\n"
                    + "p.precusto,\n"
                    + "p.custoreal,\n"
                    + "p.pesobruto,\n"
                    + "p.pesoliquido,\n"
                    + "p.validade,\n"
                    + "p.datcad,\n"
                    + "p.qtdeembalagem,\n"
                    + "p.cstcofinssaida, p.cstpissaida,\n"
                    + "p.codnaturezareceita, cf.CODCESTICMS,\n"
                    + "cf.CODIGOCLASSIFICACAOFISCAL ncm,\n"
                    + "p.codtributacaoecf tributacProd,\n"
                    + "t.codtributacaoecf tributacao,\n"
                    + "t.nometributacaoecf,\n"
                    + "t.tipotributacao,\n"
                    + "t.aliqtributacao,\n"
                    + "t.codsituacaotributaria,\n"
                    + "t.aliqreducaobaseicms\n"
                    + "from produto p\n"
                    + "left join classificacaofiscal cf ON cf.CODCLASSIFICACAOFISCAL = p.CODCLASSIFICACAOFISCAL\n"
                    + "left join produtopreco pr on pr.codprod = p.codprod and pr.codpreco = 1\n"
                    + "left join tributacaoecf t on t.codtributacaoecf = p.codtributacaoecf "
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {

                    ProdutoBalancaVO produtoBalanca;
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportId(rst.getString("codprod"));
                    imp.setEan(rst.getString("ean"));

                    if (!Utils.encontrouLetraCampoNumerico(imp.getEan())) {
                        if ((rst.getString("codsec") != null)
                                && (!rst.getString("codsec").trim().isEmpty())) {
                            if (("000000062".equals(rst.getString("codsec").trim())) // PADARIA
                                    || ("34".equals(rst.getString("codsec").trim())) // RACAO
                                    || ("35".equals(rst.getString("codsec").trim())) // LATICINIO
                                    || ("36".equals(rst.getString("codsec").trim())) // LEGUMES
                                    || ("5".equals(rst.getString("codsec").trim())) // AÇOUGUE
                                    || ("00000041".equals(rst.getString("codsec").trim()))) { //FRUTAS

                                if (imp.getEan().length() <= 6) {
                                    long codigoProduto;
                                    codigoProduto = Long.parseLong(imp.getEan());
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
                                } else {
                                    imp.seteBalanca(false);
                                }
                            } else {
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca(false);
                    }

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setDescricaoCompleta(rst.getString("nomeprod"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("codsec"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rst.getDate("datcad"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem((rst.getInt("qtdeembalagem") == 0 ? 1 : rst.getInt("qtdeembalagem")));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("precusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPiscofinsCstDebito(rst.getInt("cstpissaida"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("CODCESTICMS"));
                    imp.setPiscofinsCstCredito(rst.getInt("cstcofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getInt("codnaturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("tributacao"));
                    imp.setIcmsCreditoId(rst.getString("tributacao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "codprod,\n"
                        + "estatu,\n"
                        + "qtdemin\n"
                        + "from produtoestoque"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codprod"));
                        imp.setEstoqueMinimo(rst.getDouble("qtdemin"));
                        imp.setEstoque(rst.getDouble("estatu"));
                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.codforn,\n"
                    + "f.razaosocial,\n"
                    + "f.nomeforn,\n"
                    + "f.endereco,\n"
                    + "f.bairro,\n"
                    + "f.cidade,\n"
                    + "f.estado,\n"
                    + "f.cep,\n"
                    + "f.telefone,\n"
                    + "f.fax,\n"
                    + "f.inscr,\n"
                    + "f.cnpj,\n"
                    + "f.contato,\n"
                    + "f.datcad,\n"
                    + "f.obs,\n"
                    + "f.flagfisica,\n"
                    + "f.numerologradouro,\n"
                    + "f.bloqueado,\n"
                    + "f.email,\n"
                    + "f.emaildanfe,\n"
                    + "f.emailccdanfe\n"
                    + "from fornecedor f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codforn"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomeforn"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numerologradouro"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscr"));
                    imp.setDatacadastro(rst.getDate("datcad"));
                    imp.setObservacao(rst.getString("obs"));
                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("contato"));
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("fax"),
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
                    if ((rst.getString("emaildanfe") != null)
                            && (!rst.getString("emaildanfe").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("emaildanfe").toLowerCase()
                        );
                    }
                    if ((rst.getString("emailccdanfe") != null)
                            && (!rst.getString("emailccdanfe").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("emailccdanfe").toLowerCase()
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
                    "select\n"
                    + "pf.codforn,\n"
                    + "pf.codprod,\n"
                    + "pf.custoreal,\n"
                    + "pf.datatu,\n"
                    + "pf.codigoproduto\n"
                    + "from fornproduto pf"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("codforn"));
                    imp.setIdProduto(rst.getString("codprod"));
                    imp.setCodigoExterno(rst.getString("codigoproduto"));
                    imp.setCustoTabela(rst.getDouble("custoreal"));
                    imp.setDataAlteracao(rst.getDate("datatu"));
                    vResult.add(imp);
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
                    "select\n"
                    + "c.codcli,\n"
                    + "c.nomecli,\n"
                    + "c.endereco,\n"
                    + "c.bairro,\n"
                    + "c.cidade,\n"
                    + "c.estado,\n"
                    + "c.cep,\n"
                    + "c.telefone,\n"
                    + "c.fax,\n"
                    + "c.cnpj,\n"
                    + "c.cpf,\n"
                    + "c.limitecred,\n"
                    + "c.contato,\n"
                    + "c.inscr,\n"
                    + "c.flagfisica,\n"
                    + "c.datnasc,\n"
                    + "c.profissao,\n"
                    + "c.email,\n"
                    + "c.emaildanfe,\n"
                    + "c.emailccdanfe,\n"
                    + "c.obs,\n"
                    + "c.datemissao,\n"
                    + "c.datcad,\n"
                    + "c.estadocivil,\n"
                    + "c.sexo,\n"
                    + "c.bloqueado,\n"
                    + "c.motivobloqueio,\n"
                    + "c.numerologradouro,\n"
                    + "c.complementologradouro\n"
                    + "from cliente c"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codcli"));
                    imp.setRazao(rst.getString("nomecli"));
                    imp.setFantasia(rst.getString("nomecli"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numerologradouro"));
                    imp.setComplemento(rst.getString("complementologradouro"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email"));

                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cnpj"));
                    } else if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cpf"));
                    } else {
                        imp.setCnpj("-2");
                    }

                    imp.setInscricaoestadual(rst.getString("inscr"));
                    imp.setDataNascimento(rst.getDate("datnasc"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setDataAdmissao(rst.getDate("datemissao"));
                    imp.setObservacao(rst.getString("obs"));
                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("contato"));
                    }

                    if ((rst.getString("bloqueado") != null)
                            && (!rst.getString("bloqueado").trim().isEmpty())) {
                        if ("S".equals(rst.getString("bloqueado").trim())) {
                            imp.setBloqueado(true);
                            imp.setPermiteCheque(false);
                            imp.setPermiteCreditoRotativo(false);
                            imp.setObservacao2("CLIENTE BLOQUEADO MOTIVO BLOQUEIO " + rst.getString("motivobloqueio"));
                        } else {
                            imp.setBloqueado(false);
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                        }
                    } else {
                        imp.setBloqueado(false);
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("fax"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("emaildanfe") != null)
                            && (!rst.getString("emaildanfe").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                rst.getString("emaildanfe").toLowerCase()
                        );
                    }
                    if ((rst.getString("emailccdanfe") != null)
                            && (!rst.getString("emailccdanfe").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                null,
                                rst.getString("emailccdanfe").toLowerCase()
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
