/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.utils.Utils;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class VCashDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivo;

    @Override
    public String getSistema() {
        return "VCash";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_trib,\n"
                    + "descricao\n"
                    + "from tributac\n"
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
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_tip,\n"
                    + "nome\n"
                    + "from tip_prod\n"
                    + "where sub_tip = 0\n"
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
                    + "where sub_tip > 0"
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
                    + "'1' as merc3"
                    + "nome\n"
                    + "from tip_prod "
                    + "where sub_tip > 0"
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
        ConexaoDBF.abrirConexao(i_arquivo);
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
                    + "from produtos p"
                    + "left join tip_prod t on t.cod_trib = p.cod_tip and t.sub_tip = p.sub_tip\n"
                    + "left join estoques e on e.cod_pro = p.cod_pro "
                    + "order by p.cod_pro"
            )) {
                while (rst.next()) {
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
        ConexaoDBF.abrirConexao(i_arquivo);
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
        ConexaoDBF.abrirConexao(i_arquivo);
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
                    + "data_incl"
                    + "from forneced "
            )) {
                while (rst.next()) {
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
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.setDatacadastro(rst.getDate("data_incl"));
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
        ConexaoDBF.abrirConexao(i_arquivo);
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
                    + "conjuge "
                    + "from CLIENTES "
                    + "order by cod_cli"
            )) {
                while (rst.next()) {
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
                    imp.setNomeConjuge(rst.getString("conuge"));
                    imp.setEmpresa(rst.getString("trabalho"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setObservacao(rst.getString("obs"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
