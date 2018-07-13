package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class DtComDAO extends InterfaceDAO implements MapaTributoProvider {

    public static boolean vBalanca;

    @Override
    public String getSistema() {
        return "DTCOM";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "codloja,\n"
                    + "nomeloja\n"
                    + "from\n"
                    + "lojas")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codloja"), rs.getString("nomeloja")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	tribu,\n"
                    + "	desctribu\n"
                    + "from\n"
                    + "	aliquo\n"
                    + "order by\n"
                    + "	desctribu")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("tribu"), rs.getString("desctribu")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	secao merc1, nomesec descmerc1,\n"
                    + "	coalesce(subsec, secao) merc2, coalesce(nomesub, nomesec) descmerc2,\n"
                    + "	'1' merc3, nomesub descmerc3\n"
                    + "from\n"
                    + "	secoes\n"
                    + "order by\n"
                    + "	nomesec, nomesub")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "distinct \n"
                    + "codigo, \n"
                    + "descricao, \n"
                    + "descpdv, \n"
                    + "unidade, \n"
                    + "qtdcaixa, \n"
                    + "validade,\n"
                    + "secoes.secao,\n"
                    + "secoes.subsec,\n"
                    + "secao, \n"
                    + "ean1, \n"
                    + "ean2, \n"
                    + "venda, \n"
                    + "custunit, \n"
                    + "cust_fisca, \n"
                    + "perfixo, \n"
                    + "custmed, \n"
                    + "qtest, \n"
                    + "minimo, \n"
                    + "dat_cad, \n"
                    + "dat_can, \n"
                    + "peso, \n"
                    + "vl_pis, \n"
                    + "vl_cofins, \n"
                    + "ncm, \n"
                    + "cstvenda, \n"
                    + "cstpis, \n"
                    + "cstcofins, \n"
                    + "ppis, \n"
                    + "pcofins, \n"
                    + "picms \n"
                  + "from \n"
                    + "produtos \n"
                  + "left join\n"
                    + "secoes on secoes.sec_sub = produtos.secao\n"
                  + "order by \n"
                    + "codigo")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descpdv"));
                    imp.setDescricaoGondola(rs.getString("descricao"));
                    imp.setTipoEmbalagem(rs.getString("unidade").toUpperCase());
                    imp.setValidade(rs.getInt("validade"));
                    imp.setCodMercadologico1(rs.getString("secao"));
                    imp.setCodMercadologico2(rs.getString("subsec"));
                    imp.setCodMercadologico3("1");
                    imp.setEan(rs.getString("ean1"));
                    imp.setPrecovenda(rs.getDouble("venda"));
                    imp.setCustoComImposto(rs.getDouble("custunit"));
                    imp.setCustoSemImposto(rs.getDouble("cust_fisca"));
                    imp.setMargem(rs.getDouble("perfixo"));
                    imp.setEstoque(rs.getDouble("qtest"));
                    imp.setEstoqueMinimo(rs.getDouble("minimo"));
                    imp.setDataCadastro(rs.getDate("dat_cad"));
                    imp.setPesoLiquido(rs.getDouble("peso"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setIcmsCstSaida(rs.getInt("cstvenda"));
                    imp.setIcmsAliqSaida(rs.getDouble("picms"));
                    imp.setIcmsAliqEntrada(rs.getDouble("picms"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("picms"));
                    imp.setSituacaoCadastro((rs.getDate("dat_can")) == null ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    if (vBalanca) {
                        if ((rs.getString("ean1") != null)
                                && ("KG".equals(rs.getString("unidade").toUpperCase()))
                                && (rs.getString("ean1").trim().substring(9, 13).length() <= 5)) {
                            imp.setImportId(rs.getString("ean1"));
                            imp.seteBalanca(true);
                        }
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
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo,\n"
                    + "	cpf,\n"
                    + "   inscricao,\n"
                    + "	identidade,\n"
                    + "	nome1,\n"
                    + "	endereco,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	estado,\n"
                    + "	cep,\n"
                    + "	telefone,\n"
                    + "	fonetrab,\n"
                    + "	limite,\n"
                    + "	situacao,\n"
                    + "	observacao,\n"
                    + "	data_cad,\n"
                    + "	motivo,\n"
                    + "	filiacao,\n"
                    + "	nascimento,\n"
                    + "	cod_mun,\n"
                    + "	cod_uf,\n"
                    + "	coment1,\n"
                    + "   coment2 \n"
                    + "from\n"
                    + "	clientes\n"
                    + "order by\n"
                    + "	codigo")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("codigo"));
                    imp.setCnpj(rs.getString("cpf").substring(2, rs.getString("cpf").length()));
                    if ((!"".equals(rs.getString("identidade"))) && !"000000000000".equals(rs.getString("identidade"))) {
                        imp.setInscricaoestadual("identidade");
                    } else if ((!"".equals(rs.getString("inscricao"))) && (!"00000000000000".equals(rs.getString("inscricao")))) {
                        imp.setInscricaoestadual("inscricao");
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    imp.setInscricaoestadual(rs.getString("inscricao"));
                    imp.setRazao(rs.getString("nome1"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("cod_mun"));
                    imp.setUf(rs.getString("estado"));
                    imp.setUfIBGE(rs.getInt("cod_uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    if (!"".equals(rs.getString("fonetrab"))) {
                        imp.addContato("1", "Tel. Trabalho", rs.getString("fonetrab"), null, null);
                    }
                    imp.setValorLimite(rs.getDouble("limite"));
                    if ((rs.getInt("situacao")) == 1) {
                        imp.setAtivo(true);
                    } else {
                        imp.setAtivo(false);
                    }
                    String obs2;
                    String obs3;
                    if (!"".equals(rs.getString("coment1"))) {
                        obs2 = "Obs2: " + rs.getString("coment1");
                    } else {
                        obs2 = "";
                    }
                    if (!"".equals(rs.getString("coment2"))) {
                        obs3 = "Obs3: " + rs.getString("coment2");
                    } else {
                        obs3 = "";
                    }
                    if (!"".equals(rs.getString("coment3"))) {
                        imp.setObservacao2(rs.getString("coment3"));
                    }
                    imp.setObservacao(rs.getString("observacao") + obs2 + obs3);
                    imp.setDataCadastro(rs.getDate("data_cad"));
                    imp.setDataNascimento(rs.getDate("nascimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cgc,\n"
                    + "	inscricao,\n"
                    + "	nome1,\n"
                    + "	fantasia,\n"
                    + "	contato,\n"
                    + "	endereco,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	estado,\n"
                    + "	cep,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	observacao,\n"
                    + "   observa1,\n"
                    + "   observa2,\n"
                    + "	prazo1,\n"
                    + "	prazo2,\n"
                    + "	prazo3,\n"
                    + "	condpg,\n"
                    + "	cod_uf,\n"
                    + "	cod_mun\n"
                    + "from\n"
                    + "	fornec\n"
                    + "order by\n"
                    + "	nome1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("cgc"));
                    imp.setInsc_municipal(rs.getString("inscricao"));
                    imp.setRazao(rs.getString("nome1"));
                    imp.setFantasia(rs.getString("fantasia"));
                    if (!"".equals(rs.getString("contato"))) {
                        imp.addContato("1", rs.getString("contato"), null, null, TipoContato.COMERCIAL, null);
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setIbge_municipio(rs.getInt("cod_mun"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_uf(rs.getInt("cod_uf"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if (!"".equals(rs.getString("fax"))) {
                        imp.addContato("2", rs.getString("fax"), null, null, TipoContato.COMERCIAL, null);
                    }
                    String obs1 = "", obs2 = "";
                    if (!"".equals(rs.getString("observa1"))) {
                        obs1 = "Obs2: " + rs.getString("observa1");
                    } else if (!"".equals(rs.getString("observa2"))) {
                        obs2 = "Obs3: " + rs.getString("observa2");
                    } else {
                        obs1 = "";
                        obs2 = "";
                    }
                    imp.setObservacao(rs.getString("observacao") + obs1 + obs2);
                }
            }
        }

        return result;
    }

}
