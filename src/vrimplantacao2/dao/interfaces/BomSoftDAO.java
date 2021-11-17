package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class BomSoftDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "BomSoft";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	CASE\n"
                    + "	 TRIBUTACAO_PROD\n"
                    + "	 WHEN 'T' THEN 0\n"
                    + "	 WHEN 'R' THEN 20\n"
                    + "	 WHEN 'I' THEN 40\n"
                    + "	 WHEN 'F' THEN 60\n"
                    + "	 ELSE 40\n"
                    + "	END icms_cst,\n"
                    + "	ICMS_PROD icms_aliq,\n"
                    + "	COALESCE (REDUCAO_PROD,0) icms_red\n"
                    + "FROM PRODUTOS p"
            )) {
                while (rs.next()) {
                    String id = rs.getString("icms_cst") + "-" + rs.getString("icms_aliq") + "-" + rs.getString("icms_red");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("icms_cst"),
                            rs.getDouble("icms_aliq"),
                            rs.getDouble("icms_red")));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ID_CFG codigo,\n"
                    + "	FANTASIA_CFG fantasia,\n"
                    + "	CNPJ_CFG cnpj\n"
                    + "FROM\n"
                    + "	CONFIG c\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"),
                                    rst.getString("fantasia") + "-"
                                    + rst.getString("cnpj")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_GRPROD merc1,\n"
                    + "	DESCRICAO_GRPROD desc_merc1,\n"
                    + "	CODIGO_GRPROD merc2,\n"
                    + "	DESCRICAO_GRPROD desc_merc2,\n"
                    + "	CODIGO_GRPROD merc3,\n"
                    + "	DESCRICAO_GRPROD desc_merc3\n"
                    + "FROM\n"
                    + "	GRUPO_PROD\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_PROD id,\n"
                    + "	REFERENCIA_PROD ean,\n"
                    + "	DESCRICAO_PROD descricao,\n"
                    + "	GRUPO_PROD merc1,\n"
                    + "	GRUPO_PROD merc2,\n"
                    + "	GRUPO_PROD merc3,\n"
                    + "	CADASTRO_PROD data_cad,\n"
                    + "	ULTIMA_ALTERACAO_PROD data_alteracao,\n"
                    + "	UNIDADE_PROD unidade,\n"
                    + "	PRCUSTO_PROD custosemimposto,\n"
                    + "	PRCUSTO_PROD custocomimposto,\n"
                    + "	PRVISTA_PROD precovenda,\n"
                    + "	MARGEM_PROD margem,\n"
                    + "	CASE\n"
                    + "	 TRIBUTACAO_PROD\n"
                    + "	 WHEN 'T' THEN 0\n"
                    + "	 WHEN 'R' THEN 20\n"
                    + "	 WHEN 'I' THEN 40\n"
                    + "	 WHEN 'F' THEN 60\n"
                    + "	 ELSE 40\n"
                    + "	END icms_cst,\n"
                    + "	ICMS_PROD icms_aliq,\n"
                    + "	REDUCAO_PROD icms_red,\n"
                    + "	CST_PISCOFINS piscofins,\n"
                    + "	NCM_PROD ncm,\n"
                    + "	CEST_PROD cest,\n"
                    + "	ESTATUAL_PROD estoque,\n"
                    + "	ESTMINIMO_PROD est_minimo,\n"
                    + "	ESTMAXIMO_PROD est_maximo,\n"
                    + "	PESOLIQ_PROD pesoliquido,\n"
                    + "	PESOBRUT_PROD pesobruto,\n"
                    + "	CASE\n"
                    + "	  WHEN BALANCA_PROD = 'S' THEN 1\n"
                    + "	  ELSE 0\n"
                    + "	END e_balanca,\n"
                    + " VALIDADE_PROD validade\n"
                    + "FROM\n"
                    + "	PRODUTOS\n"
                    + "ORDER BY 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setDataCadastro(rst.getDate("data_cad"));
                    imp.setDataAlteracao(rst.getDate("data_alteracao"));

                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("est_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("est_maximo"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    //imp.seteBalanca(rst.getBoolean("e_balanca"));
                    //imp.setValidade(rst.getInt("validade"));

                    imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    String idIcmsDebito/*, IdIcmsCredito*/;
                    idIcmsDebito = rst.getString("icms_cst") + "-" + rst.getString("icms_aliq") + "-" + rst.getString("icms_red");
                    //IdIcmsCredito = rst.getString("cst_entrada") + "-" + rst.getString("aliquota_entrada") + "-" + rst.getString("reducao_entrada");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);
                    //imp.setIcmsCreditoId(IdIcmsCredito);
                    //imp.setIcmsCreditoForaEstadoId(IdIcmsCredito);
                    imp.setIcmsConsumidorId(idIcmsDebito);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO_PROD idproduto,\n"
                    + "	REFERENCIA_PROD ean,\n"
                    + "	UNIDADE_PROD tipoembalagem,\n"
                    + "	1 qtdembalagem\n"
                    + "FROM\n"
                    + "	PRODUTOS p\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	 CODIGO_FOR id,\n"
                    + "	 RAZAO_FOR razao,\n"
                    + "	 FANTASIA_FOR fantasia,\n"
                    + "	 CGC_FOR cnpj_cpf,\n"
                    + "	 INSCRICAO_FOR ie_rg,\n"
                    + "	 TIPOENDERECO||' '||ENDERECO_FOR as endereco,\n"
                    + "	 NUMERO_FOR numero,\n"
                    + "	 COMPLEMENTO_FOR complemento,\n"
                    + "	 BAIRRO_FOR bairro,\n"
                    + "	 nome_cid cidade,\n"
                    + "	 uf_cid uf,\n"
                    + "	 CEP_FOR cep,\n"
                    + "	 CADASTRO_FOR data_cad,\n"
                    + "	 CASE WHEN ATIVO_FOR = 'S' THEN 1 ELSE 0 END situacaocadastro,\n"
                    + "	 OBS1_FOR ||' '||OBS2_FOR observacao\n"
                    + "FROM\n"
                    + "	 FORNEC F \n"
                    + "	 LEFT JOIN CIDADES m ON m.CODIGO_CID = f.cidade_for\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(rst.getDate("data_cad"));
                    imp.setAtivo(rst.getBoolean("situacaocadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGO id,\n"
                    + "	RAZAO_SOCIAL razao,\n"
                    + "	FANTASIA,\n"
                    + "	CGC cpf_cnpj,\n"
                    + "	INSCRICAO ie_rg,\n"
                    + "	INSCRICAO_MUNICIPAL im,\n"
                    + "	TIPOENDERECO||' '||ENDERECO as endereco,\n"
                    + "	NUMERO,\n"
                    + "	COMPLEMENTO,\n"
                    + "	BAIRRO,\n"
                    + "	CEP,\n"
                    + "	nome_cid cidade,\n"
                    + "	uf_cid uf,\n"
                    + "	DATA_NASC data_nasc,\n"
                    + "	CADASTRO data_cad,\n"
                    + "	CASE WHEN ATIVO = 'S' THEN 1 ELSE 0 END situacaocadastro,\n"
                    + "	LIMITE_CREDITO limite,\n"
                    + "	PROFISSAO cargo,\n"
                    + "	LOCAL_TRABALHO empresa,\n"
                    + "	FONE_TRABALHO fone_empresa,\n"
                    + "	EMAIL_CLI email,\n"
                    + "	MAE nomemae,\n"
                    + "	PAI nomepai,\n"
                    + "	CONJUGUE conjuge,\n"
                    + "	CASE WHEN RESTRICAO = 'S' THEN 1 ELSE 0 END bloqueado, \n"
                    + "	OBS1||' '||OBS2 observacao\n"
                    + "FROM\n"
                    + "	CLIENTES c\n"
                    + "	LEFT JOIN CIDADES m ON m.CODIGO_CID = c.CIDADE \n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie_rg"));
                    imp.setInscricaoMunicipal(rs.getString("im"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDataCadastro(rs.getDate("data_cad"));
                    imp.setDataNascimento(rs.getDate("data_nasc"));
                    imp.setAtivo(rs.getBoolean("situacaocadastro"));

                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("fone_empresa"));
                    imp.setEmail(rs.getString("email"));

                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));

                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	id,\n"
                    + "	CLIENTE id_cliente,\n"
                    + "	c.CGC doc_cliente,\n"
                    + "	EMISSAO,\n"
                    + "	DATA_VCTO vencimento,\n"
                    + "	TITULO numerocupom,\n"
                    + "	VALOR,\n"
                    + "	OBS observacao\n"
                    + "FROM\n"
                    + "	RECEBER r\n"
                    + "	LEFT JOIN CLIENTES c ON c.CODIGO = r.CLIENTE \n"
                    + "WHERE\n"
                    + "	DATA_PGTO IS NULL"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setCnpjCliente(rs.getString("doc_cliente"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
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
        return new BomSoftDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new BomSoftDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
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
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	ID_VEN id_venda,\n"
                    + "	DATA_VEN data,\n"
                    + "	CLIENTE_VEN id_cliente,\n"
                    + "	SUBSTRING(OBS_VEN FROM 11 FOR 20) numerocupom, \n"
                    + "	CAIXA_VEN ecf,\n"
                    + " ACRESCIMOS_VEN acrescimo,\n"
                    + "	DESCONTOS_VEN desconto,"
                    + "	SUBTOTAL_VEN subtotalimpressora\n"
                    + "FROM\n"
                    + "	VENDAS v\n"
                    + "WHERE\n"
                    + " VALORPAGO_VEN > 0"
                    + "	AND DATA_VEN BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "ORDER BY 1";
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

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("id_item");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setVenda(rst.getString("id_venda"));
                        next.setId(id);
                        next.setProduto(rst.getString("produto"));
                        next.setCodigoBarras(rst.getString("codigobarra"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	NUMERO_VIT id_venda,\n"
                    + "	ID_VIT id_item,\n"
                    + "	PRODUTO_VIT produto,\n"
                    + "	p.REFERENCIA_PROD codigobarra,\n"
                    + "	p.DESCRICAO_PROD descricao,\n"
                    + "	UND_VIT unidade,\n"
                    + "	QTDE_VIT quantidade,\n"
                    + "	PRUNIT_VIT precovenda,\n"
                    + "	SUBTOTAL_VIT total\n"
                    + "FROM\n"
                    + "	VENITENS vi\n"
                    + "	JOIN VENDAS v ON v.ID_VEN = vi.NUMERO_VIT\n"
                    + "	JOIN PRODUTOS p ON p.CODIGO_PROD = vi.PRODUTO_VIT \n"
                    + "WHERE\n"
                    + " v.VALORPAGO_VEN > 0"
                    + "	AND v.DATA_VEN BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "ORDER BY 1,2";
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
