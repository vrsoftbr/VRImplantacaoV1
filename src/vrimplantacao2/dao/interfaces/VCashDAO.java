/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class VCashDAO extends InterfaceDAO implements MapaTributoProvider {

    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat fmt2 = new SimpleDateFormat("dd/MM/yy");
    private static final Logger LOG = Logger.getLogger(DtComDAO.class.getName());

    @Override
    public String getSistema() {
        return "VCash";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "cod_est,\n"
                    + "nome\n"
                    + "from\n"
                    + "EMPR_USU")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("cod_est"), rs.getString("nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_trib,\n"
                    + "descricao, "
                    + "valor\n"
                    + "from TRIBUTAC\n"
                    + "order by cod_trib"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("cod_trib"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_tip,\n"
                    + "nome\n"
                    + "from tip_prod\n"
                    + "where sub_tip = '000'\n"
                    + "order by cod_tip"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("cod_tip"));
                    imp.setDescricao(rst.getString("nome"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_tip,\n"
                    + "sub_tip,\n"
                    + "nome\n"
                    + "from tip_prod "
                    + "where sub_tip > '000'\n"
                    + "order by cod_tip, sub_tip"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("cod_tip"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("sub_tip"),
                                rst.getString("nome")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_tip,\n"
                    + "sub_tip,\n"
                    + "'1' as merc3, "
                    + "nome\n"
                    + "from tip_prod "
                    + "where sub_tip > '000'\n"
                    + "order by cod_tip, sub_tip"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("cod_tip"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("sub_tip"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("nome")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String dataCadastro = null;
        java.sql.Date dataCad;
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "p.cod_pro, "
                    + "p.codbar, "
                    + "p.nome,"
                    + "p.unidade,"
                    + "p.st as cst,"
                    + "p.cod_tip,"
                    + "p.sub_tip,"
                    + "p.cod_trib,"
                    + "p.nbm,"
                    + "p.cest,"
                    + "p.nat_rec,"
                    + "p.pesoliq,"
                    + "p.pesobruto,"
                    + "t.minmarkup as margem,"
                    + "p.cod_trib,"
                    + "p.data_incl,"
                    + "e.p_custo,"
                    + "e.p_venda,"
                    + "e.qte as estoque,"
                    + "e.qte_min "
                    + "from produtos p\n"
                    + "left join tip_prod t on t.cod_tip = p.cod_tip and t.sub_tip = p.sub_tip\n"
                    + "left join estoques e on e.cod_pro = p.cod_pro and e.cod_est = '" + getLojaOrigem() + "'\n"
                    + "order by p.cod_pro"
            )) {
                while (rst.next()) {

                    dataCadastro = rst.getString("data_incl").substring(0, 4);
                    dataCadastro = dataCadastro + "-" + rst.getString("data_incl").substring(4, 6);
                    dataCadastro = dataCadastro + "-" + rst.getString("data_incl").substring(6, 8);
                    dataCad = new java.sql.Date(fmt.parse(dataCadastro).getTime());

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("cod_pro"));
                    imp.setEan(rst.getString("codbar"));

                    if (imp.getEan().trim().isEmpty()) {
                        imp.seteBalanca(true);
                    }

                    imp.setDescricaoCompleta(rst.getString("nome"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setCodMercadologico1(rst.getString("cod_tip"));
                    imp.setCodMercadologico2(rst.getString("sub_tip"));
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(dataCad);
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("p_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("p_venda"));
                    imp.setEstoqueMinimo(rst.getDouble("qte_min"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliq"));
                    imp.setNcm(rst.getString("nbm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsDebitoId(rst.getString("cod_trib"));
                    imp.setIcmsCreditoId(rst.getString("cod_trib"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "cod_pro,"
                    + "codbar "
                    + "from prod_bar"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("cod_pro"));
                    imp.setEan(rst.getString("codbar"));
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
        String dataCadastro = null;
        java.sql.Date dataCad;
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "cod_for, "
                    + "nome as razao, "
                    + "fantasia, "
                    + "endereco, "
                    + "bairro, "
                    + "cidade, "
                    + "cep, "
                    + "estado, "
                    + "cgc_cpf, "
                    + "insc_rg, "
                    + "ins_mun, "
                    + "fone, "
                    + "fax, "
                    + "e_mail, "
                    + "nomev, "
                    + "fonev, "
                    + "obs1, "
                    + "obs2, "
                    + "data_incl\n"
                    + "from FORNECED "
            )) {
                while (rst.next()) {

                    if (!rst.getString("data_incl").contains("/")) {
                        dataCadastro = rst.getString("data_incl").substring(0, 4);
                        dataCadastro = dataCadastro + "-" + rst.getString("data_incl").substring(4, 6);
                        dataCadastro = dataCadastro + "-" + rst.getString("data_incl").substring(6, 8);
                        dataCad = new java.sql.Date(fmt.parse(dataCadastro).getTime());
                    } else {
                        dataCad = new java.sql.Date(fmt2.parse(rst.getString("data_incl")).getTime());
                    }

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("cod_for"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cgc_cpf"));
                    imp.setIe_rg(rst.getString("insc_rg"));
                    imp.setInsc_municipal(rst.getString("ins_mun"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    
                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())) {
                        imp.setMunicipio(rst.getString("cidade").replace(" SP", ""));
                    }
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.setDatacadastro(dataCad);
                    imp.setObservacao(rst.getString("obs1"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("e_mail") != null)
                            && (!rst.getString("e_mail").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("e_mail").toLowerCase()
                        );
                    }

                    if ((rst.getString("nomev") != null)
                            && (!rst.getString("nomev").trim().isEmpty())
                            && (rst.getString("fonev") != null)
                            && (!rst.getString("fonev").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("nomev"),
                                Utils.formataNumero(rst.getString("fonev")),
                                null,
                                TipoContato.COMERCIAL,
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
        String dataCadastro = null;
        java.sql.Date dataCad;
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "cod_cli, "
                    + "nome, "
                    + "fantasia, "
                    + "endereco, "
                    + "bairro, "
                    + "cidade, "
                    + "estado, "
                    + "cep, "
                    + "cgc_cpf, "
                    + "insc_rg, "
                    + "ins_mun, "
                    + "lim_cred, "
                    + "fone, "
                    + "fax, "
                    + "e_mail, "
                    + "mae, "
                    + "pai, "
                    + "trabalho, "
                    + "endcom, "
                    + "fonecom, "
                    + "obs, "
                    + "profissao, "
                    + "data_incl, "
                    + "conjuge "
                    + "from CLIENTES "
                    + "order by cod_cli"
            )) {
                while (rst.next()) {

                    dataCadastro = rst.getString("data_incl").substring(0, 4);
                    dataCadastro = dataCadastro + "-" + rst.getString("data_incl").substring(4, 6);
                    dataCadastro = dataCadastro + "-" + rst.getString("data_incl").substring(6, 8);
                    dataCad = new java.sql.Date(fmt.parse(dataCadastro).getTime());

                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("cod_cli"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cgc_cpf"));
                    imp.setInscricaoestadual(rst.getString("insc_rg"));
                    imp.setInscricaoMunicipal(rst.getString("ins_mun"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("e_mail") != null ? rst.getString("e_mail").toLowerCase() : null);
                    imp.setValorLimite(rst.getDouble("lim_cred"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setEmpresa(rst.getString("trabalho"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setDataCadastro(dataCad);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        String dataEmissao = null, dataVencimento = null;
        java.sql.Date dataEmi, dataVenc;
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "n_docto, "
                    + "cod_cli, "
                    + "parcela, "
                    + "data_vend as emissao, "
                    + "data_venc as vencimento, "
                    + "vr_liquid, "
                    + "obs "
                    + "from CT_RC001 "
                    + "where data_rec = ''"
            )) {
                while (rst.next()) {

                    dataEmissao = rst.getString("emissao").substring(0, 4);
                    dataEmissao = dataEmissao + "-" + rst.getString("emissao").substring(4, 6);
                    dataEmissao = dataEmissao + "-" + rst.getString("emissao").substring(6, 8);
                    dataEmi = new java.sql.Date(fmt.parse(dataEmissao).getTime());

                    if ((rst.getString("vencimento") != null)
                            && (!rst.getString("vencimento").trim().isEmpty())) {
                        dataVencimento = rst.getString("vencimento").substring(0, 4);
                        dataVencimento = dataVencimento + "-" + rst.getString("vencimento").substring(4, 6);
                        dataVencimento = dataVencimento + "-" + rst.getString("vencimento").substring(6, 8);
                        dataVenc = new java.sql.Date(fmt.parse(dataVencimento).getTime());
                    } else {
                        dataVenc = dataEmi;
                    }

                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("n_docto") + rst.getString("cod_cli"));
                    imp.setIdCliente(rst.getString("cod_cli"));
                    imp.setDataEmissao(dataEmi);
                    imp.setDataVencimento(dataVenc);
                    imp.setNumeroCupom(rst.getString("n_docto"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setValor(rst.getDouble("vr_liquid"));
                    imp.setObservacao(rst.getString("obs"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
