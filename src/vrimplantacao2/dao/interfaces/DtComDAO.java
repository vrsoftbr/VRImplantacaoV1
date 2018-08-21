package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoPagamento;
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
 * @author Importacao
 */
public class DtComDAO extends InterfaceDAO implements MapaTributoProvider {

    public static boolean vBalanca;
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyMMdd");
    private static final Logger LOG = Logger.getLogger(DtComDAO.class.getName());

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
                    + "picms, \n"
                    + "tipvenda \n"
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
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricao")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descpdv")));
                    imp.setDescricaoGondola(Utils.acertarTexto(rs.getString("descricao")));
                    imp.setTipoEmbalagem(rs.getString("unidade").toUpperCase());
                    imp.setValidade(rs.getInt("validade"));
                    imp.setCodMercadologico1(rs.getString("secao"));
                    imp.setCodMercadologico2(rs.getString("subsec"));
                    imp.setCodMercadologico3("1");
                    imp.setEan(rs.getString("ean1"));
                    imp.setPrecovenda(rs.getDouble("venda"));
                    imp.setCustoComImposto(rs.getDouble("cust_fisca"));
                    imp.setCustoSemImposto(rs.getDouble("custunit"));
                    imp.setMargem(rs.getDouble("perfixo"));
                    imp.setEstoque(rs.getDouble("qtest"));
                    imp.setEstoqueMinimo(rs.getDouble("minimo"));
                    imp.setDataCadastro(rs.getDate("dat_cad"));
                    imp.setPesoLiquido(rs.getDouble("peso"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setIcmsCstSaida(rs.getInt("cstvenda"));
                    imp.setIcmsCstEntrada(rs.getInt("cstvenda"));
                    imp.setIcmsAliqSaida(rs.getDouble("picms"));
                    imp.setIcmsAliqEntrada(rs.getDouble("picms"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("picms"));
                    imp.setIcmsAliq(rs.getDouble("picms"));
                    imp.setSituacaoCadastro((rs.getDate("dat_can")) == null ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    if (vBalanca) {
                        if ((rs.getString("ean1") != null)
                                && ("F".equals(rs.getString("tipvenda").trim()))) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            String ean = rs.getString("ean1").trim().substring(6, 12);
                            imp.setEan(ean);
                            codigoProduto = Long.parseLong(imp.getEan().trim());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        }
                    } else {
                        imp.seteBalanca(true);
                        imp.setValidade(rs.getInt("validade"));
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
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigo,\n"
                    + "	cgc,\n"
                    + "	codfor\n"
                  + "from\n"
                    + "	prodfor\n"
                  + "order by\n"
                    + "	codigo")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("codigo"));
                    imp.setIdFornecedor(rs.getString("cgc"));
                    imp.setCodigoExterno(rs.getString("codfor"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	qtdcaixa,\n" +
                    "	ean2\n" +
                    "from\n" +
                    "	produtos\n" +
                    "order by\n" +
                    "	codigo")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setQtdEmbalagem(rs.getInt("qtdcaixa"));
                    
                    if ((rs.getString("ean2") != null) && 
                            (!"0000000000000".equals(rs.getString("ean2")))) {
                        imp.setEan(rs.getString("ean2"));
                    }
                    
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
                    + " observa1,\n"
                    + " observa2,\n"
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
                    imp.setCnpj_cpf(rs.getString("cgc"));
                    imp.setInsc_municipal(rs.getString("inscricao"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome1")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
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
                    if ((rs.getString("observa1") != null) && (!"".equals(rs.getString("observa1")))) {
                        obs1 = " Obs2: " + rs.getString("observa1");
                    } else if ((rs.getString("observa2") != null) && !"".equals(rs.getString("observa2"))) {
                        obs2 = " Obs3: " + rs.getString("observa2");
                    } else {
                        obs1 = "";
                        obs2 = "";
                    }
                    imp.setObservacao(rs.getString("observacao") + obs1 + obs2);

                    if (!"".equals(String.valueOf(rs.getInt("prazo1")))) {
                        imp.addPagamento("1", rs.getInt("prazo1"));
                    }
                    if (!"".equals(String.valueOf(rs.getInt("prazo2")))) {
                        imp.addPagamento("2", rs.getInt("prazo2"));
                    }
                    if (!"".equals(String.valueOf(rs.getInt("prazo3")))) {
                        imp.addPagamento("3", rs.getInt("prazo3"));
                    }
                    imp.setTipoPagamento(new TipoPagamento(1, rs.getString("condpg")));

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
                    + " inscricao,\n"
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
                    + " coment2,\n"
                    + " coment3\n"
                  + "from\n"
                    + "	clientes\n"
                  + "order by\n"
                    + "	codigo")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("codigo"));
                    imp.setCnpj(rs.getString("cpf"));
                    if ((rs.getString("identidade") != null) && (!"".equals(rs.getString("identidade")))) {
                        imp.setInscricaoestadual(rs.getString("identidade"));
                    } else if ((rs.getString("inscricao") != null) && (!"".equals(rs.getString("inscricao")))) {
                        imp.setInscricaoestadual(rs.getString("inscricao"));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome1")));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("cod_mun"));
                    imp.setUf(rs.getString("estado"));
                    imp.setUfIBGE(rs.getInt("cod_uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));

                    if ((rs.getString("fonetrab") != null) && !"".equals(rs.getString("fonetrab"))) {
                        int qtdString = rs.getString("fonetrab").length();
                        if (qtdString > 14) {
                            imp.addContato("1", "Tel. Trabalho", rs.getString("fonetrab").trim().substring(0, 13), null, null);
                        } else {
                            imp.addContato("1", "Tel. Trabalho", rs.getString("fonetrab").trim(), null, null);
                        }
                    }

                    imp.setValorLimite(rs.getDouble("limite"));
                    if ((rs.getInt("situacao")) == 1) {
                        imp.setAtivo(true);
                    } else {
                        imp.setAtivo(false);
                    }
                    String obs1;
                    String obs2;
                    String obs3;
                    if ((rs.getString("coment1") != null) && !"".equals(rs.getString("coment1"))) {
                        obs2 = " Obs2: " + rs.getString("coment1");
                    } else {
                        obs2 = "";
                    }
                    if ((rs.getString("coment2") != null) && !"".equals(rs.getString("coment2"))) {
                        obs3 = " Obs3: " + rs.getString("coment2");
                    } else {
                        obs3 = "";
                    }
                    if (!"".equals(rs.getString("coment3"))) {
                        imp.setObservacao2(rs.getString("coment3"));
                    }
                    if ((rs.getString("observacao") != null) && (!"".equals(rs.getString("observacao")))) {
                        obs1 = rs.getString("observacao");
                    } else {
                        obs1 = "";
                    }
                    imp.setObservacao(obs1 + obs2 + obs3);
                    imp.setDataCadastro(rs.getDate("data_cad"));
                    imp.setDataNascimento(rs.getDate("nascimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	r.cgc as idcliente,\n"
                    + "	r.cheque as idconta,\n"
                    + " c.codigo,\n"
                    + "	r.valor,\n"
                    + "	r.vencto,\n"
                    + "	r.movto,\n"
                    + "	r.parcela\n"
                  + "from\n"
                    + "	receb r\n"
                  + "join\n"
                    + "	clientes c\n"
                    + " on c.cpf = r.cgc\n"
                  + "where\n"
                    + "	tipo = 2 and\n"
                    + "	dtpg is null\n")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                                        
                    imp.setCnpjCliente(rs.getString("idcliente"));
                    imp.setIdCliente(rs.getString("codigo"));

                    //Data emissão
                    if ((rs.getString("movto") != null)) {
                       imp.setDataEmissao(FORMAT.parse(rs.getString("movto")));
                    }

                    //Data vencimento
                    if((rs.getString("vencto") != null)) {
                        imp.setDataVencimento(FORMAT.parse(rs.getString("vencto")));
                     }
                    
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setId(rs.getString("idconta"));
                    imp.setNumeroCupom(rs.getString("idconta"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	r.cgc as idcliente,\n"
                    + "	c.identidade,\n"
                    + "	c.inscricao,\n"
                    + "	c.nome1,\n"
                    + "	r.banco,\n"
                    + "	r.agencia,\n"
                    + "	r.conta,\n"
                    + "	r.cheque,\n"
                    + "	r.valor,\n"
                    + "	r.vencto,\n"
                    + "	r.movto,\n"
                    + "	r.parcela,\n"
                    + " r.comen\n"        
                 + "from\n"
                    + "	receb r\n"
                 + "join\n"
                    + "	clientes c\n"
                    + "       on c.cpf = r.cgc\n"
                 + "where\n"
                    + "	r.tipo = 1 and\n"
                    + "	r.dtpg is null")) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setConta(rs.getString("conta"));
                    imp.setCpf(rs.getString("idcliente"));
                    imp.setNumeroCheque(rs.getString("cheque"));
                    imp.setObservacao(rs.getString("comen"));
                    if ((rs.getString("identidade") != null) && (!"000000000000".equals(rs.getString("identidade").trim()))) {
                        imp.setRg(rs.getString("identidade"));
                    } else {
                        imp.setRg("inscricao");
                    }
                    imp.setNome(Utils.acertarTexto(rs.getString("nome1")));

                    imp.setDataDeposito(FORMAT.parse(rs.getString("vencto")));
                    imp.setDate(FORMAT.parse(rs.getString("movto")));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setAlinea(0);

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
