/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
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
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

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
                    + "	c.codigo cst,\n"
                    + "	i.aliquota,\n"
                    + "	reducao\n"
                    + "from\n"
                    + "	tsl.tslc003 p\n"
                    + "	join tslc035 i on i.SEQUENCIAL = p.CODST\n"
                    + " join tslc036 c on i.SEQ036 = c.SEQ"
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
                    + "  recseq, reccod, rectitulo, reccli,\n"
                    + "  recemiss, recvenci, recobs, recvalor,\n"
                    + "  recvalpag, recparc\n"
                    + "from tsl.tsm003\n"
                    + "where recbaixa <> 'S'\n"
                    + "  and horaest = ''"
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
                    + "	  i.sequencial codigo,\n"
                    + "	  i.descricao,\n"
                    + "	  c.codigo cst,\n"
                    + "	  i.aliquota,\n"
                    + "	  0 reducao\n"
                    + " from\n"
                    + "   tslc035 i\n"
                    + " join tslc036 c on i.SEQ036 = c.SEQ"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"),
                            rs.getString("descricao"),
                            Utils.stringToInt(rs.getString("cst")),
                            rs.getInt("aliquota"),
                            rs.getInt("reducao")));
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

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new TstiDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new TstiDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
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
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));

                        if (rst.getString("nomecliente") != null
                                && !rst.getString("nomecliente").trim().isEmpty()
                                && rst.getString("nomecliente").trim().length() > 45) {

                            next.setNomeCliente(rst.getString("nomecliente").substring(0, 45));
                        } else {
                            next.setNomeCliente(rst.getString("nomecliente"));
                        }

                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado"));
                        next.setEnderecoCliente(endereco);
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	seq id,\n"
                    + "	cupom numerocupom,\n"
                    + "	codcli idcliente,\n"
                    + "	case micro\n"
                    + "		when 'PDV01' then 1\n"
                    + "		when 'PDV02' then 2\n"
                    + "		when 'PDV03' then 3\n"
                    + "		when 'PDV04' then 4\n"
                    + "	end ecf,\n"
                    + "	data,\n"
                    + "	horaini horainicio,\n"
                    + "	horafim horatermino,\n"
                    + "	bruto subtotalimpressora,\n"
                    + "	cpf,\n"
                    + "	desconto,\n"
                    + "	acrescim,\n"
                    + "	nome nomecliente,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	comple,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	uf estado\n"
                    + "from\n"
                    + "	tslv010 v\n"
                    + "where\n"
                    + "	empresa = " + idLojaCliente + "\n"
                    + "	and exclui != 'S'\n"
                    + "	and data between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by seq,data,horaini";
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

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("id_venda");
                        String idItem = rst.getString("id_item");

                        next.setVenda(id);
                        next.setId(idItem);
                        next.setProduto(rst.getString("produto"));
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
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
                    + "	i.cupom id_venda,\n"
                    + "	i.seq id_item,\n"
                    + "	i.cupom numerocupom,\n"
                    + "	codprod produto,\n"
                    + "	codbarras codigobarras,\n"
                    + "	unidade,\n"
                    + "	descricao,\n"
                    + "	quant quantidade,\n"
                    + "	unit precovenda,\n"
                    + "	i.total,\n"
                    + "	i.desconto,\n"
                    + "	case when i.exclui = 'S' then 1 else 0 end cancelado,\n"
                    + "	case micro\n"
                    + "		when 'PDV01' then 1\n"
                    + "		when 'PDV02' then 2\n"
                    + "		when 'PDV03' then 3\n"
                    + "		when 'PDV04' then 4\n"
                    + "	end ecf,\n"
                    + "	v.data data\n"
                    + "from\n"
                    + "	tslv011 i\n"
                    + "	join tslv010 v on v.seq = i.cupom\n"
                    + "where\n"
                    + "	empresa = " + idLojaCliente + "\n"
                    + "	and data between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by i.cupom,i.seq";
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
