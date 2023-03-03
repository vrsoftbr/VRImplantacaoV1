/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;

/**
 *
 * @author Wagner
 */
public class SoftLogDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "SoftLog";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
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
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.RECEITA
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.TELEFONE
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT DISTINCT\n"
                    + "  cast(ALIQUOTA AS varchar(10)) id,\n"
                    + "  ALIQUOTA,\n"
                    + "  REDUCAO\n"
                    + " FROM C000025"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("aliquota"),
                            0,
                            0,
                            rst.getDouble("reducao")
                    ));
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
                    "SELECT DISTINCT \n"
                    + " p.CODGRUPO mercid1,\n"
                    + " n1.GRUPO desc1,\n"
                    + " p.CODSUBGRUPO mercid2,\n"
                    + " n2.SUBGRUPO desc2\n"
                    + "FROM C000025 p\n"
                    + "JOIN C000017 n1 ON n1.CODIGO = p.CODGRUPO\n"
                    + "LEFT JOIN C000018 n2 ON n2.CODIGO = p.CODSUBGRUPO\n"
                    + "ORDER BY 1,3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(imp.getMerc2ID());
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "SELECT \n"
                    + " CODIGO id,\n"
                    + " CODBARRA ean,\n"
                    + " PRODUTO descricao,\n"
                    + " UNIDADE,\n"
                    + " CODGRUPO mercid1,\n"
                    + " CODSUBGRUPO mercid2,\n"
                    + " CODFORNECEDOR fornecedorid,\n"
                    + " CODMARCA,\n"
                    + " PRECOCUSTO,\n"
                    + " PRECOVENDA,\n"
                    + " E.ESTOQUE_ATUAL ESTOQUE,\n"
                    + " CASE WHEN USA_BALANCA = 2 THEN 0 ELSE 1 END ebalanca,\n"
                    + " CST,\n"
                    + " replace(CLASSIFICACAO_FISCAL,'.','') ncm,\n"
                    + " cast(ALIQUOTA AS varchar(10)) id_aliquota,\n"
                    + " REDUCAO,\n"
                    + " QTDE_EMBALAGEM,\n"
                    + " SITUACAO,\n"
                    + " PISCOFINS,\n"
                    + " SITUACAO_TRIBUTARIA,\n"
                    + " CSOSN,\n"
                    + " CODFAMILIA,\n"
                    + " CODALIQUOTA,\n"
                    + " replace(FISCO_CEST,'.','') cest,\n"
                    + " FISCO_CST_PIS pis,\n"
                    + " FISCO_CST_COFINS cofins,\n"
                    + " CASE WHEN replace(COALESCE(VALIDADE,'0'),'','0') <> '' THEN replace(COALESCE(VALIDADE,'0'),'','0')\n"
                    + "   ELSE '0' END validade \n"
                    + " FROM C000025 P \n"
                    + " LEFT JOIN C000100 E ON P.CODIGO = E.CODPRODUTO "
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setCustoSemImposto(rst.getDouble("precocusto"));
                    imp.setCustoComImposto(imp.getCustoSemImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("pis"));
                    imp.setPiscofinsCstDebito(rst.getString("pis"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        //imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(rst.getBoolean("ebalanca"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setQtdEmbalagem(rst.getInt("qtde_embalagem"));
                        imp.setValidade(rst.getInt("validade"));
                    }
                    imp.setValidade(rst.getInt("validade"));
                    imp.setIcmsDebitoId(rst.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

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
                    "SELECT \n"
                    + " CODIGO id,\n"
                    + " NOME razao,\n"
                    + " FANTASIA,\n"
                    + " ENDERECO,\n"
                    + " BAIRRO,\n"
                    + " CIDADE,\n"
                    + " UF,\n"
                    + " CIDADE,\n"
                    + " COMPLEMENTO,\n"
                    + " TELEFONE1,\n"
                    + " CNPJ,\n"
                    + " IE,\n"
                    + " NUMERO,\n"
                    + " COD_MUNICIPIO_IBGE,\n"
                    + " SITUACAO\n"
                    + "FROM C000009"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone1"));

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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " CODIGO id,\n"
                    + " NOME,\n"
                    + " ENDERECO,\n"
                    + " BAIRRO,\n"
                    + " CIDADE,\n"
                    + " UF,\n"
                    + " CEP,\n"
                    + " TIPO,\n"
                    + " SITUACAO,\n"
                    + " TELEFONE1,\n"
                    + " TELEFONE2,\n"
                    + " CELULAR,\n"
                    + " EMAIL,\n"
                    + " RG,\n"
                    + " CPF,\n"
                    + " LIMITE,\n"
                    + " NUMERO,\n"
                    + " COD_MUNICIPIO_IBGE,\n"
                    + " SITUACAO\n"
                    + "FROM C000007"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setEmail(rst.getString("email"));

                    result.add(imp);

                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "  CODFORNECEDOR fornecedorid,\n"
                    + "  CODPRODUTO produtoid,\n"
                    + "  CODIGO externo \n"
                    + "FROM \n"
                    + "FORNECEDOR_CODIGO"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setIdProduto(rst.getString("produtoid"));
                    imp.setCodigoExterno(rst.getString("externo"));

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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "  CODIGO id,\n"
                    + "  CODCLIENTE idcliente,\n"
                    + "  NUMERO_CUPOM cupom,\n"
                    + "  DATA_EMISSAO dataemissao,\n"
                    + "  DATA_VENCIMENTO datavencimento,\n"
                    + "  DOCUMENTO numerodocumento,\n"
                    + "  VALOR_ORIGINAL valor\n"
                    + "FROM C000049\n"
                    + "WHERE SITUACAO = 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT  \n"
                    +   "CODIGO id,\n"
                    +   "CODFORNECEDOR idforecedor,\n"
                    +   "NOTAFISCAL numerodocumento,\n"
                    +   "DATA_EMISSAO dataemissao,\n"
                    +   "VALOR valor,\n"
                    +   "DATA_VENCIMENTO dtvencimento\n"
                    + "FROM C000046\n"
                    + "WHERE SITUACAO = 1"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setNumeroDocumento(rst.getString("numeroDocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setVencimento(rst.getDate("dtvencimento"));
                    //imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }*/
    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "  SELECT \n"
                    + "  CODIGO ID_PRODUTOPAI,\n"
                    + "  PRODUTO DESCRICAO,\n"
                    + "  QUANTIDADE QUANTIDADE,\n"
                    + "  CODPRODUTO ID_PRODUTOFILHO\n"
                    + "  FROM C000079"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rst.getString("id_produtopai"));
                    imp.setIdproduto(rst.getString("id_produtopai"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setQtdembalagemreceita(rst.getInt("quantidade"));
                    imp.setQtdembalagemproduto(1);
                    imp.setFator(1);
                    imp.getProdutos().add(rst.getString("id_produtofilho"));

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
        return new SoftLogDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SoftLogDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "";/*"SELECT\n"
                    + "	c.seq id_venda,\n"
                    + "	CASE WHEN c.SEQ IS NULL THEN c.SEQ||'-'||c.NUM_OPER ELSE c.SEQ END numerocupom,\n"
                    + "	c.COD_CAIXA ecf,\n"
                    + "	c.DATA data,\n"
                    + "	c.HORA hora,\n"
                    + "	c.VALORTOTAL valor,\n"
                    + "	c.DESCONTO desconto,\n"
                    + "	c.COD_CLI id_cliente,\n"
                    + "	cc.CGC cpf,\n"
                    + "	cc.NOME nomecliente,\n"
                    + "	CASE WHEN c.SITUACAO = 'A' THEN 0 ELSE 1 END cancelado\n"
                    + "FROM\n"
                    + "	CAIXA c\n"
                    + "	LEFT JOIN CADCLI cc ON c.COD_CLI = cc.CODIGO \n"
                    + "WHERE\n"
                    + "	c.DATA BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "	AND c.NATUREZA = 500";*/
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

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nritem"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "";/*"WITH teste AS (SELECT NUM_OPER, max(NUMEROITEM) n FROM venda GROUP BY 1)\n"
                    + "SELECT  \n"
                    + " v.NUMEROITEM nritem,\n"
                    + "	c.SEQ id_venda,\n"
                    + "	v.SEQ id_item,\n"
                    + "	v.COD_PROD id_produto,\n"
                    + "	v.NOME_PROD descricao,\n"
                    + "	v.QUANTIDADE quantidade,\n"
                    + "	v.PRECO_VEND valor,\n"
                    + "	v.UNIDADE unidade,\n"
                    + "	(c.DESCONTO/t.n) desconto\n"
                    + "FROM\n"
                    + " VENDA v \n"
                    + " JOIN CAIXA c ON v.NUM_OPER = c.NUM_OPER \n"
                    + " JOIN teste t ON t.num_oper = v.NUM_OPER \n"
                    + "WHERE v.DATA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "	AND NATUREZA = 500\n"
                    + "GROUP BY 1, 2, 3, 4, 5, 6, 7 ,8 , 9";*/
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
