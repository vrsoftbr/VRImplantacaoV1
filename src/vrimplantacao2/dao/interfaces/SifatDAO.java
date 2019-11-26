package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemAnteriorDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemDAO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class SifatDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(SifatDAO.class.getName());
    public String db = "";

    @Override
    public String getSistema() {
        return "Sifat";
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
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM_MINIMA,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MAPA_TRIBUTACAO,
            OpcaoProduto.RECEITA_BALANCA,}));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();

        try (Statement stmt = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select\n"
                    + "	icm.DEPTO_ICMS as id,\n"
                    + "	icm.CST_ICMS as cst,\n"
                    + "	icm.AL_ICMS as aliquota,\n"
                    + "	icm.RED_BC_ICMS as reducao,\n"
                    + "	icm.DESCRICAO as descricao\n"
                    + "from CE01T icm\n"
                    + "where operacao = 1\n"
                    + "order by icm.CST_ICMS"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
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
                    "select \n"
                    + "	ID_GRUPO as merc1, \n"
                    + "	NOME_GRUPO as merc1_desc,\n"
                    + "	ID_SUBGRUPO as merc2, \n"
                    + "	NOME_SUBGRUPO as merc2_desc\n"
                    + "from CE07\n"
                    + "order by ID_GRUPO, ID_SUBGRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + " ID_FAMILIA as id,\n"
                    + "	DESCRICAO as descricao\n"
                    + "from CE27"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
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
                    "select \n"
                    + "	pro.ID_PRODUTO as id,\n"
                    + "	pro.CODIGO as ean,\n"
                    + "	pro.PESADO as balanca,\n"
                    + "	pro.FRACIONA as fracionado,\n"
                    + "	pro.validade as validade,\n"
                    + "	pro.DESCRICAO as descricaocompleta,\n"
                    + "	pro.ABREVIACAO as descricaoreduzida,\n"
                    + " pro.REFERENCIA as compl_descricao,\n"
                    + "	pro.UNIDADE as tipoembalagem,\n"
                    + " pro.UN_COMPRA AS tipoembalagemcotacao,\n"
                    + " pro.QTDE_COMPRA AS qtdembalagemcotacao,\n"
                    + "	pro.PESO as peso,\n"
                    + "	pro.FAMILIA as idfamilia,\n"
                    + "	pro.GRUPO as merc1,\n"
                    + "	pro.SUBGRUPO as merc2,\n"
                    + "	pro.NCM as ncm,\n"
                    + "	pro.CEST as cest,\n"
                    + "	pro.DT_CADASTRO as datacadastro,\n"
                    + "	pre.ATIVO as situacaocadastro,\n"
                    + " pre.ML_MAXIMA AS margem,\n"
                    + " pre.ML_MINIMA as margemminima,\n"
                    + "	pre.PRECO_CUSTO as custo,\n"
                    + "	pre.PRECO_VENDA as precovenda,\n"
                    + "	pre.ESTOQUE as estoque,\n"
                    + "	pre.ESTOQUE_MIN as estoqueminimo,\n"
                    + "	pre.ESTOQUE_MAX as estoquemaximo,\n"
                    + "	pre.DEPTO_PIS,\n"
                    + "	pis.CST as cstpis,\n"
                    + "	pis.nat_operacao as naturezareceita,\n"
                    + "	pis.DESCRICAO as descricaopis,\n"
                    + "	pre.DEPTO_COFINS,\n"
                    + "	cof.CST as cstcofins,\n"
                    + "	cof.DESCRICAO as descricaopis,\n"
                    + "	pre.DEPTO_ICMS icmsid,\n"
                    + "	icm.CST_ICMS as csticms,\n"
                    + "	icm.AL_ICMS as aliqicms,\n"
                    + "	icm.RED_BC_ICMS as reduicms,\n"
                    + "	icm.DESCRICAO as descricaoicms\n"
                    + "from CE01 pro\n"
                    + "left join CE01E pre on pre.ID_PRODUTO = pro.ID_PRODUTO and pre.LOJA = " + getLojaOrigem() + "\n"
                    + "left join CE61 pis on pis.ID = pre.DEPTO_PIS\n"
                    + "left join CE61 cof on cof.ID = pre.DEPTO_COFINS\n"
                    + "left join CE01T icm on icm.DEPTO_ICMS = pre.DEPTO_ICMS and icm.OPERACAO = 1\n"
                    + "order by pro.ID_PRODUTO"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setValidade(rst.getInt("validade"));

                    String complDescricao = null;

                    if ((rst.getString("compl_descricao") != null)
                            && (!rst.getString("compl_descricao").trim().isEmpty())) {

                        complDescricao = " " + rst.getString("compl_descricao");
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta") + (complDescricao == null ? "" : complDescricao));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida") + (complDescricao == null ? "" : complDescricao));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagemcotacao"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("idfamilia"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargemMinima(rst.getDouble("margemminima"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setPiscofinsCstDebito(rst.getInt("cstpis"));
                    imp.setPiscofinsCstCredito(rst.getInt("cstcofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("naturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("icmsid"));
                    imp.setIcmsCreditoId(rst.getString("icmsid"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ID_PRODUTO, \n"
                    + "	CODIGO, \n"
                    + "	DESCRICAO, \n"
                    + "	COALESCE(RENDIMENTO_RECEITA, 1) as RENDIMENTO_RECEITA, \n"
                    + "	INGREDIENTES \n"
                    + "FROM CE01 \n"
                    + "WHERE INGREDIENTES IS NOT NULL\n"
                    + "AND INGREDIENTES <> ''"
            )) {
                while (rst.next()) {
                    ReceitaBalancaIMP imp = new ReceitaBalancaIMP();

                    imp.setId(rst.getString("CODIGO"));
                    imp.setDescricao(rst.getString("DESCRICAO"));
                    imp.setReceita(rst.getString("INGREDIENTES"));
                    imp.getProdutos().add(rst.getString("ID_PRODUTO"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.CODIGO as id,\n"
                    + "	f.ATIVO as situacaocadastro,\n"
                    + "	f.NOME as razao,\n"
                    + "	f.NOME_FANTASIA as fantasia,\n"
                    + "	f.INSC_FEDERAL as cnpj,\n"
                    + "	f.INSC_ESTADUAL as ie_rg,\n"
                    + "	f.ENDERECO as endereco,\n"
                    + "	f.END_NUMERO as numero,\n"
                    + "	f.END_COMPLEMENTO as complemento,\n"
                    + "	f.BAIRRO as bairro,\n"
                    + "	f.CIDADE as municipio,\n"
                    + "	f.UF as uf,\n"
                    + "	f.ID_MUNICIPIO as municipioibge,\n"
                    + "	f.CEP as cep,\n"
                    + "	f.TELEFONE as telefone,\n"
                    + "	f.FAX as fax,\n"
                    + "	f.CELULAR as celular,\n"
                    + "	SUBSTR(f.EMAIL, 1, 50) as email,\n"
                    + "	f.SITE as site,\n"
                    + "	f.DIA_VENCIMENTO as diavencimento,\n"
                    + "	f.OBSERVACAO as observacao,\n"
                    + " f.HISTORICO as historico,\n"
                    + "	f.PRAZO_ENTREGA as prazoentrega,\n"
                    + "	f.PRAZO_PGTO as prazopagto,\n"
                    + " f.DT_CADASTRO as datacadastro\n"
                    + "from CD02 f\n"
                    + "where f.E_FORNECEDOR = 1\n"
                    + "order by f.CODIGO"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setAtivo("1".equals(rst.getString("situacaocadastro")));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setPrazoPedido(rst.getInt("prazoentrega"));
                    imp.setCondicaoPagamento(rst.getInt("prazopagto"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("historico") != null)
                            && (!rst.getString("historico").trim().isEmpty())) {

                        imp.setObservacao(imp.getObservacao() + " - " + rst.getString("historico"));
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "CELULAR",
                                null,
                                Utils.formataNumero(rst.getString("celular")),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email")
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
                                rst.getString("site")
                        );
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.CODIGO as id,\n"
                    + "	c.ATIVO as situacaocadastro,\n"
                    + "	c.NOME as razao,\n"
                    + "	c.NOME_FANTASIA as fantasia,\n"
                    + "	c.INSC_FEDERAL as cnpj,\n"
                    + "	c.INSC_ESTADUAL as ie_rg,\n"
                    + "	c.ENDERECO as endereco,\n"
                    + "	c.END_NUMERO as numero,\n"
                    + "	c.END_COMPLEMENTO as complemento,\n"
                    + "	c.BAIRRO as bairro,\n"
                    + "	c.CIDADE as municipio,\n"
                    + "	c.UF as uf,\n"
                    + "	c.ID_MUNICIPIO as municipioibge,\n"
                    + "	c.CEP as cep,\n"
                    + "	c.TELEFONE as telefone,\n"
                    + "	c.FAX as fax,\n"
                    + "	c.CELULAR as celular,\n"
                    + "	SUBSTR(c.EMAIL, 1, 50) as email,\n"
                    + "	c.SITE as site,\n"
                    + "	c.DIA_VENCIMENTO as diavencimento,\n"
                    + "	c.OBSERVACAO as observacao,\n"
                    + "	c.PRAZO_ENTREGA as prazoentrega,\n"
                    + "	c.PRAZO_PGTO as prazopagto,\n"
                    + "	c.LIMITE_CREDITO as valorlimite,\n"
                    + "	c.STATUS_CREDITO as statuscredito,\n"
                    + "	c.LIMITE_CHEQUE as limitecheque,\n"
                    + "	c.DT_NASCIMENTO as datanascimento,\n"
                    + "	c.FILIACAO as filiacao,\n"
                    + "	c.SEXO as sexo,\n"
                    + "	c.ESTADO_CIVIL as estadocivil,\n"
                    + "	c.NOME_CONJUGE as nomeconjuge,\n"
                    + "	c.TRABALHO as empresa,\n"
                    + "	c.DT_ADMISSAO as dataadmissao,\n"
                    + "	c.RENDA_MENSAL as salario,\n"
                    + "	c.PROFISSAO as cargo,\n"
                    + "	c.CPF_CONJUGE as cpfconjuge,\n"
                    + " c.DT_CADASTRO as datacadastro,"
                    + "c.STATUS_CREDITO as status\n"
                    + "from CD02 c\n"
                    + "where c.E_CLIENTE  = 1\n"
                    + "order by c.CODIGO"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setAtivo(rst.getInt("situacaocadastro") == 1);
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setBloqueado(rst.getInt("status") == 2);

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "SITE",
                                null,
                                null,
                                Utils.formataNumero(rst.getString("site"))
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select "
                     + "id, "
                     + "cliente, "
                     + "loja, "
                     + "caixa,\n"
                     + "venda, "
                     + "data emissao, "
                     + "faturado, "
                     + "valor,\n"
                     + "ADDDATE(data, interval 30 day) vencimento\n"
                     + "from CF11\n"
                     + "where DC = 'D'\n"
                     + "and historico like '%VENDA%'\n"
                     + "and loja = " + getLojaOrigem()
                     + " order by data"*/
                    "select\n"
                    + "	r.ID_TITULO as id,\n"
                    + "	r.CAIXA as caixa,\n"
                    + "	r.CF as numerocupom,\n"
                    + "	r.EMISSAO as dataemissao,\n"
                    + "	r.VENCIMENTO as datavencimento,\n"
                    + "	r.VALOR as valor,\n"
                    + "	r.CLIENTE as idcliente,\n"
                    + "	r.HISTORICO as observacao\n"
                    + "from CF04 r \n"
                    + "where DATAPGTO is NULL\n"
                    + "and r.LOJA = " + getLojaOrigem() + "\n"
                    + "and IND_RP = 'R'\n"
                    + "order by r.EMISSAO"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setObservacao(rst.getString("observacao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public void importarPagamentoRotativo() throws Exception {
        Conexao.begin();
        try {
            Map<String, Double> pagamentos = new HashMap<>();

            ProgressBar.setStatus("Importando pagamentos...");
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select distinct a.cliente,\n"
                        + "(select sum(coalesce(valor, 0)) from CF11 where dc = 'C' and cliente = a.cliente) "
                        //+ "- "
                        //+ "(select sum(coalesce(valor, 0)) from cf11 where historico like '%ESTORNO%' and cliente = a.cliente)"
                        + " valor\n"
                        + "from CF11 a\n"
                        + "where loja = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        double valor = rst.getDouble("valor");

                        if (valor < 0) {
                            valor *= -1;
                        }

                        pagamentos.put(rst.getString("cliente"), MathUtils.trunc(valor, 2));
                    }
                }
            }

            for (String id : pagamentos.keySet()) {
                double valorPagoTotal = pagamentos.get(id);
                System.out.println("ID: " + id + "  VALOR: " + valorPagoTotal);
            }

            CreditoRotativoDAO rotDao = new CreditoRotativoDAO();
            CreditoRotativoItemDAO dao = new CreditoRotativoItemDAO();
            CreditoRotativoItemAnteriorDAO antDao = new CreditoRotativoItemAnteriorDAO();
            MultiMap<String, CreditoRotativoItemAnteriorVO> baixasAnteriores = antDao.getBaixasAnteriores(null, null);

            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	 ant.sistema,\n"
                        + "    ant.loja,\n"
                        + "    ant.id_cliente,\n"
                        + "    ant.id,\n"
                        + "    ant.codigoatual,\n"
                        + "    r.id_loja,\n"
                        + "    r.valor,\n"
                        + "    r.datavencimento\n"
                        + "from \n"
                        + "	implantacao.codant_recebercreditorotativo ant\n"
                        + "    join recebercreditorotativo r on\n"
                        + "    	ant.codigoatual = r.id\n"
                        + " where ant.loja = '" + getLojaOrigem() + "' "
                        + " and ant.sistema = '" + getSistema() + "'"
                        + " order by\n"
                        + "	ant.id_cliente, r.datavencimento"
                )) {
                    int cont1 = 0, cont2 = 0;
                    while (rst.next()) {
                        String sistema = rst.getString("sistema");
                        String loja = rst.getString("loja");
                        String idCliente = rst.getString("id_cliente");
                        String idRotativo = rst.getString("id");
                        int codigoAtual = rst.getInt("codigoatual");
                        int id_loja = rst.getInt("id_loja");
                        double valor = rst.getDouble("valor");
                        Date vencimento = rst.getDate("datavencimento");

                        if (!baixasAnteriores.containsKey(sistema, loja, idRotativo, idRotativo)) {
                            if (pagamentos.containsKey(idCliente)) {
                                double valorPagoTotal = pagamentos.get(idCliente);
                                if (valorPagoTotal > 0) {
                                    System.out.println("CLIENTE: " + idCliente + " VAL_PAGO: " + valorPagoTotal);
                                    double valorParc;
                                    if (valorPagoTotal >= valor) {
                                        valorPagoTotal -= valor;
                                        valorParc = valor;
                                    } else {
                                        valorParc = valorPagoTotal;
                                        valorPagoTotal = 0;
                                    }

                                    CreditoRotativoItemVO pag = new CreditoRotativoItemVO();
                                    pag.setId_receberCreditoRotativo(codigoAtual);
                                    pag.setValor(valorParc);
                                    pag.setValorTotal(valorParc);
                                    pag.setDatabaixa(vencimento);
                                    pag.setDataPagamento(vencimento);
                                    pag.setObservacao("IMPORTADO VR");
                                    pag.setId_loja(id_loja);

                                    dao.gravarRotativoItem(pag);

                                    CreditoRotativoItemAnteriorVO ant = new CreditoRotativoItemAnteriorVO();
                                    ant.setSistema(sistema);
                                    ant.setLoja(loja);
                                    ant.setIdCreditoRotativo(idRotativo);
                                    ant.setId(idRotativo);
                                    ant.setCodigoAtual(pag.getId());
                                    ant.setDataPagamento(vencimento);
                                    ant.setValor(pag.getValor());

                                    antDao.gravarRotativoItemAnterior(ant);

                                    rotDao.verificarBaixado(codigoAtual);

                                    pagamentos.put(idCliente, valorPagoTotal);
                                    baixasAnteriores.put(ant,
                                            ant.getSistema(),
                                            ant.getLoja(),
                                            ant.getIdCreditoRotativo(),
                                            ant.getId()
                                    );
                                }
                            }
                        }
                        cont1++;
                        cont2++;

                        if (cont1 == 1000) {
                            cont1 = 0;
                            ProgressBar.setStatus("Importando pagamentos..." + cont2);
                        }
                    }
                }
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pag.ID_TITULO as id,\n"
                    + "	pag.NUM_TITULO as numerodocumento,\n"
                    + "	pag.NF_NUMERO as numeroNF,\n"
                    + "	pag.NF_SERIE as serieNF,\n"
                    + "	pag.TOTAL_PARCELAS as totalparcelas,\n"
                    + "	pag.PARCELA as parcela,\n"
                    + "	pag.ENTIDADE as idfornecedor,\n"
                    + "	pag.EMISSAO as dataemissao,\n"
                    + "	pag.VENCIMENTO as datavencimento,\n"
                    + "	pag.VR_NF as valorNF,\n"
                    + "	pag.VR_TITULO as valorparcela,\n"
                    + "	pag.HISTORICO as observacao,\n"
                    + "	pag.ESPECIE as especie\n"
                    + "from CF22 pag\n"
                    + "where pag.ENTIDADE in (select codigo from CD02 where E_FORNECEDOR = 1)\n"
                    + "and pag.DT_PGTO is null\n"
                    + "and pag.LOJA = " + getLojaOrigem() + " \n"
                    + "order by pag.ENTIDADE, pag.NF_NUMERO, pag.NUM_TITULO"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    String numeroDocumento = "0";

                    if ((rst.getString("numeroNF") != null)
                            && (!rst.getString("numeroNF").trim().isEmpty())
                            && (!"0".equals(rst.getString("numeroNF").trim()))) {
                        numeroDocumento = rst.getString("numeroNF");
                    } else {
                        numeroDocumento = rst.getString("numerodocumento");
                    }

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setDataEntrada(rst.getDate("dataemissao"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroDocumento(numeroDocumento);
                    ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("datavencimento"), rst.getDouble("valorparcela"));
                    parc.setNumeroParcela(Utils.stringToInt(rst.getString("parcela"), 1));
                    parc.setObservacao("VALOR TOTAL " + rst.getString("valorNF")
                            + " NUMERO DE PARCELAS " + rst.getString("totalparcelas")
                            + " PARCELA DA CONTA " + rst.getString("parcela")
                            + "..." + rst.getString("observacao"));

                    /*try (Statement stm2 = ConexaoMySQL.getConexao().createStatement()) {
                     try (ResultSet rst2 = stm2.executeQuery(
                     "select\n"
                     + " pag.PARCELA as parcela,\n"
                     + " pag.VENCIMENTO as datavencimento,\n"
                     + " pag.VR_TITULO as valorparcela,\n"
                     + " pag.HISTORICO as observacao\n"
                     + "from cf22 pag\n"
                     + "where pag.ENTIDADE in (select codigo from cd02 where E_FORNECEDOR = 1)\n"
                     + "and pag.DT_PGTO is null\n"
                     + "and pag.LOJA = " + getLojaOrigem() + "\n"
                     + "and pag.NUM_TITULO = '" + numeroDocumento + "'\n"
                     + "and pag.EMISSAO = '" + rst.getString("dataemissao") + "'\n"
                     + "and pag.ENTIDADE = " + rst.getString("idfornecedor") + "\n"
                     + "and pag.TOTAL_PARCELAS = " + rst.getString("totalparcelas") + "\n"
                     + "union all \n"
                     + "select\n"
                     + " pag.PARCELA as parcela,\n"
                     + " pag.VENCIMENTO as datavencimento,\n"
                     + " pag.VR_TITULO as valorparcela,\n"                                        
                     + " pag.HISTORICO as observacao\n"
                     + "from cf22 pag\n"
                     + "where pag.ENTIDADE in (select codigo from cd02 where E_FORNECEDOR = 1)\n"
                     + "and pag.DT_PGTO is null\n"
                     + "and pag.LOJA = " + getLojaOrigem() + "\n"
                     + "and pag.NF_NUMERO = '" + numeroDocumento + "'\n"
                     + "and pag.EMISSAO = '" + rst.getString("dataemissao") + "'\n"
                     + "and pag.ENTIDADE = " + rst.getString("idfornecedor") + "\n"
                     + "and pag.TOTAL_PARCELAS = " + rst.getString("totalparcelas") + "\n"                                
                     )) {
                     while (rst2.next()) {
                     ContaPagarVencimentoIMP parc = imp.addVencimento(rst2.getDate("datavencimento"), rst2.getDouble("valorparcela"));
                     parc.setNumeroParcela(Utils.stringToInt(rst2.getString("parcela"), 1));
                     parc.setObservacao(rst2.getString("observacao"));                                
                     }
                     }
                     }*/
                    result.add(imp);
                }
            }
        }
        return result;
    }

    private java.util.Date dataInicioVenda;
    private java.util.Date dataTerminoVenda;

    public void setDataInicioVenda(java.util.Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(java.util.Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        public VendaIterator(String idLojaCliente, java.util.Date dataInicio, java.util.Date dataTermino) throws Exception {
            this.sql
                    = "select \n"
                    + "	ven.LOJA as loja,\n"
                    + "	ven.CAIXA as caixa,\n"
                    + "	ven.NUMERO as numero,\n"
                    + "	ven.ESPECIE as especie,\n"
                    + "	ven.SERIE as serieecf,\n"
                    + "	ven.ECF as ecf,\n"
                    + "	ven.CF as cupomfiscal,\n"
                    + "	ven.CCF,\n"
                    + "	ven.EMISSAO datavenda,\n"
                    + "	concat(substr(ven.HORA, 1, 2), ':', substr(ven.HORA, 3, 2), ':', substr(ven.HORA, 5, 2)) as horavenda,\n"
                    + "	ven.CLIENTE as idcliente,\n"
                    + "	ven.VRTOTAL as valorvenda,\n"
                    + "	ven.DESCONTO as desconto,\n"
                    + "	ven.VRLIQUIDO as valorliquido,\n"
                    + "	ven.CANCELADO as cancelado,\n"
                    + "	ven.NFE_CHAVE as chaveNFE,\n"
                    + "	ven.NOME_CONSUMIDOR as nomecliente\n"
                    + "from CF01 ven \n"
                    + "where ven.LOJA = " + idLojaCliente
                    + " and ven.EMISSAO >= '" + dataInicio + "' and ven.EMISSAO <= '" + dataTermino + "'\n"
                    + " AND coalesce(ven.VRTOTAL,0 ) > 0\n"
                    + " AND coalesce(ven.SERIE, '') <> 'RC'";

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

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("loja") + "-" + rst.getString("caixa") + "-" + rst.getString("numero");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);

                        if ((rst.getString("cancelado") != null)
                                && (!rst.getString("cancelado").trim().isEmpty())) {

                            if (rst.getInt("cancelado") == 1) {
                                next.setCancelado(true);
                            } else {
                                next.setCancelado(false);
                            }
                        } else {
                            next.setCancelado(false);
                        }
                        
                        String numeroCupom = "";
                        if ((rst.getString("cupomfiscal") != null)
                                && (!rst.getString("cupomfiscal").trim().isEmpty())
                                && (!"0".equals(rst.getString("cupomfiscal").trim()))) {

                            
                            if (next.isCancelado()) {
                                numeroCupom = rst.getString("numero");
                            } else {
                                numeroCupom = rst.getString("cupomfiscal");
                            }
                            
                            
                        } else {
                            numeroCupom = rst.getString("numero");
                        }
                        
                        String ecfCaixa = "0";
                        if ((rst.getString("ecf") != null) &&
                                (!rst.getString("ecf").trim().isEmpty()) &&
                                (!"0".equals(rst.getString("ecf").trim()))) {
                            
                            ecfCaixa = rst.getString("ecf");
                        } else {
                            ecfCaixa = rst.getString("caixa");
                        }

                        
                        next.setNumeroCupom(Utils.stringToInt(numeroCupom));
                        next.setEcf(Utils.stringToInt(ecfCaixa));
                        next.setData(rst.getDate("datavenda"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));

                        String horavenda = rst.getString("horavenda");
                        if ((horavenda).contains("::")) {
                            horavenda = "00:00:00";
                        }

                        String horaInicio = timestampDate.format(rst.getDate("datavenda")) + " " + horavenda;
                        String horaTermino = timestampDate.format(rst.getDate("datavenda")) + " " + horavenda;

                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valorvenda"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setChaveCfe(rst.getString("chaveNFE"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             0700   7.00    ALIQUOTA 07%
             1200   12.00   ALIQUOTA 12%
             1800   18.00   ALIQUOTA 18%
             2500   25.00   ALIQUOTA 25%
             1100   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "04":
                    cst = 0;
                    aliq = 7;
                    break;
                case "03":
                    cst = 0;
                    aliq = 12;
                    break;
                case "01":
                    cst = 0;
                    aliq = 18;
                    break;
                case "02":
                    cst = 0;
                    aliq = 25;
                    break;
                case "05":
                    cst = 0;
                    aliq = 11;
                    break;
                case "06":
                    cst = 0;
                    aliq = 4.5;
                case "FF":
                    cst = 60;
                    aliq = 0;
                    break;
                case "NN":
                    cst = 41;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, java.util.Date dataInicio, java.util.Date dataTermino) throws Exception {
            this.sql
                    = "select distinct\n"
                    + " ven.LOJA as loja,\n"
                    + "	ven.ECF as ecf,\n"
                    + "	ite.CAIXA as caixa,\n"
                    + "	ite.NUMERO as numero,\n"
                    + "	ite.ITEM as sequencia,\n"
                    + "	ite.ID_PRODUTO as idproduto,\n"
                    + "	ite.PRODUTO as codigobarras,\n"
                    + "	ite.UNITARIO as precovenda,\n"
                    + "	ite.QUANTIDADE as qtdproduto,\n"
                    + "	ite.VALOR as valortotal,\n"
                    + "	ite.ECF_ST as tribproduto,\n"
                    + "	pro.UNIDADE as tipoembalagem,\n"
                    + "	ite.CANCELADO as cancelado,\n"
                    + "	ven.EMISSAO datavenda,\n"
                    + " ite.VR_DESCONTO as valordesconto\n"
                    + "from CF02 ite\n"
                    + "inner join CE01 pro on pro.ID_PRODUTO = ite.ID_PRODUTO\n"
                    + "inner join CF01 ven on ven.NUMERO = ite.NUMERO \n"
                    + "	and ven.CAIXA = ite.CAIXA \n"
                    + "	and ven.LOJA = ite.LOJA\n"
                    + " and ven.LOJA = " + idLojaCliente + "\n"
                    + " and ite.LOJA = " + idLojaCliente + "\n"
                    + " and ven.EMISSAO >= '" + dataInicio + "' and ven.EMISSAO <= '" + dataTermino + "'";

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

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String idVenda = rst.getString("loja") + "-" + rst.getString("caixa") + "-" + rst.getString("numero");
                        String id = rst.getString("loja")
                                + "-" + rst.getString("caixa")
                                + "-" + rst.getString("numero")
                                + "-" + rst.getString("idproduto")
                                + "-" + rst.getString("codigobarras")
                                + "-" + rst.getString("sequencia");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto"));
                        next.setQuantidade(rst.getDouble("qtdproduto"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("valordesconto"));

                        boolean cancelado = false;

                        if ((rst.getString("cancelado") != null)
                                && (!rst.getString("cancelado").trim().isEmpty())) {
                            if (rst.getInt("cancelado") == 1) {
                                cancelado = true;
                            }
                        }

                        next.setCancelado(cancelado);
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("tipoembalagem"));

                        String strTrib = "";
                        if ((rst.getString("tribproduto") != null) &&
                                (!rst.getString("tribproduto").trim().isEmpty())) {
                            strTrib = rst.getString("tribproduto").trim();
                        }
                        
                        String trib = strTrib;
                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }
    }
}
