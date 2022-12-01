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
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Wagner
 */
public class WLSDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "WLS";
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
                OpcaoProduto.PDV_VENDA
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
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.MUNICIPIO
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
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT DISTINCT\n"
                    + "  N07||'.'||REPLACE(N08,',','.')||'.'||REPLACE(REDUCAOBC_ICMS,',','.') id,\n"
                    + "  N07 cst,\n"
                    + "  REPLACE(N08,',','.') aliquota,\n"
                    + "  REPLACE(REDUCAOBC_ICMS,',','.') reducao\n"
                    + " FROM ESTOQUE_RETROATIVO_ITENS\n"
                    + "  WHERE N07 IS NOT NULL AND N08 IS NOT NULL \n"
                    + "  AND REDUCAOBC_ICMS IS NOT NULL"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("id"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
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
                    "SELECT DISTINCT\n"
                    + " CLASSIFICACAO,\n"
                    + " SUBCLASSIFICACAO,\n"
                    + " CATEGORIA,\n"
                    + " SUBCATEGORIA \n"
                    + "FROM PRODUTOS\n"
                    + "WHERE \n"
                    + "CLASSIFICACAO IS NOT NULL \n"
                    + "AND \n"
                    + "SUBCLASSIFICACAO IS NOT NULL \n"
                    + "AND \n"
                    + "SUBCLASSIFICACAO <> ''"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("CLASSIFICACAO"));
                    imp.setMerc1Descricao(rst.getString("CLASSIFICACAO"));
                    imp.setMerc2ID(rst.getString("SUBCLASSIFICACAO"));
                    imp.setMerc2Descricao(rst.getString("SUBCLASSIFICACAO"));
                    imp.setMerc3ID(rst.getString("CATEGORIA"));
                    imp.setMerc3Descricao(rst.getString("CATEGORIA"));
                    imp.setMerc4ID(rst.getString("SUBCATEGORIA"));
                    imp.setMerc4Descricao(rst.getString("SUBCATEGORIA"));

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
                    + " p.COD_PRODUTO id,\n"
                    + " p.COD_BARRAS ean,\n"
                    + " p.DESCRICAO,\n"
                    + " p.DESCRICAO_RESUMIDA,\n"
                    + " substring(p.UNIDADE_MEDIDA FROM 1 FOR 2) unidade,\n"
                    + " CASE WHEN p.CLASSIFICACAO IN ('HORTIFRUTI','ACOUGUE')  THEN 'P'\n"
                    + "      ELSE 'N' END pesavel,\n"
                    + " p.CLASSIFICACAO mercid1,\n"
                    + " p.SUBCLASSIFICACAO mercid2,\n"
                    + " p.CATEGORIA mercid3,\n"
                    + " p.SUBCATEGORIA mercid4,\n"
                    + " pe.VENDA precovenda,\n"
                    + " pe.CUSTO_MEDIO,\n"
                    + " pe.CUSTO,\n"
                    + " pe.CUSTO_FINAL,\n"
                    + " pe.QTD estoque,\n"
                    + " pe.QTD_MAX,\n"
                    + " pe.QTD_MINIMA,\n"
                    + " pe.STATUS situacao,\n"
                    + " pe.LUCRO,\n"
                    + " pe.CEST,\n"
                    + " pe.I05 ncm,\n"
                    + " pe.N07||'.'||CAST(REPLACE(pe.N08,',','.') AS NUMERIC(10,2))||'.'||REPLACE(e.REDUCAOBC_ICMS,',','.') tributacaoid,\n"
                    + " pe.Q07 piscofins\n"
                    + "FROM PRODUTOS p\n"
                    + "JOIN PRODUTOS_ESTOQUE pe ON p.COD_PRODUTO = pe.COD_PRODUTO \n"
                    + "LEFT JOIN ESTOQUE_RETROATIVO_ITENS e ON e.COD_PRODUTO = p.COD_PRODUTO\n"
                    + "WHERE \n"
                    + "p.DESCRICAO IS NOT NULL\n"
                    + "order by p.COD_PRODUTO;"
            )) {
                //Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(rst.getString("DESCRICAO_RESUMIDA"));
                    imp.setDescricaoGondola(rst.getString("DESCRICAO_RESUMIDA"));
                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));
                    imp.setCodMercadologico4(rst.getString("mercid4"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setTipoEmbalagemCotacao(imp.getTipoEmbalagem());
                    imp.setCustoSemImposto(rst.getDouble("CUSTO"));
                    imp.setCustoComImposto(rst.getDouble("CUSTO"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("LUCRO"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins"));
                    imp.setEan(rst.getString("ean"));

                    if ("P".equals(rst.getString("pesavel"))) {
                        imp.seteBalanca(true);
                    } else {
                        imp.seteBalanca(false);
                    }

                    imp.setIcmsDebitoId(rst.getString("tributacaoid"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

                    /*int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
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
                    }*/
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
                    + " COD_FORNECEDOR id,\n"
                    + " NOME_FANTASIA fantasia,\n"
                    + " RAZAO_SOCIAL razao,\n"
                    + " IE,\n"
                    + " CNPJ,\n"
                    + " CEP_EMPRESA cep,\n"
                    + " ENDERECO_EMPRESA endereco,\n"
                    + " BAIRRO_EMPRESA bairro,\n"
                    + " CIDADE_EMPRESA cidade,\n"
                    + " UF_EMPRESA uf,\n"
                    + " TELEFONE_EMPRESA telefone,\n"
                    + " DATA_CADASTRO,\n"
                    + " CASE WHEN STATUS = 'ATIVO' THEN 1\n"
                    + "     ELSE 0 END situacao,\n"
                    + " NUMERO_ENDERECO_EMPRESA numero,\n"
                    + " COD_MUNICIPIO_ENDERECO_EMPRESA ibge\n"
                    + "FROM FORNECEDOR "
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("CNPJ"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("DATA_CADASTRO"));

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
                    + "  COD_PRODUTO produtoid,\n"
                    + "  COD_PRODUTO_FORNECEDOR referencia,\n"
                    + "  COD_FORNECEDOR fornecedorid\n"
                    + "FROM \n"
                    + " PRODUTOS_FORNECEDOR "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setIdProduto(rst.getString("produtoid"));
                    imp.setCodigoExterno(rst.getString("referencia"));

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
                    "SELECT DISTINCT\n"
                    + " b.COD_BARRAS ean,\n"
                    + " b.COD_PRODUTO produtoid,\n"
                    + " substring(p.UNIDADE_MEDIDA FROM 1 FOR 2) unidade\n"
                    + "FROM BARCODE b\n"
                    + "JOIN PRODUTOS p ON p.COD_PRODUTO = b.COD_PRODUTO "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produtoid"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setTipoEmbalagemCotacao(rst.getString("unidade"));

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
                    + " COD_CLIENTE id,\n"
                    + " NOME,\n"
                    + " CEP,\n"
                    + " ENDERECO,\n"
                    + " NUMERO,\n"
                    + " TELEFONE,\n"
                    + " CELULAR,\n"
                    + " BAIRRO,\n"
                    + " CIDADE,\n"
                    + " UF,\n"
                    + " CPF,\n"
                    + " RG,\n"
                    + " DATA_NASCIMENTO,\n"
                    + " ESTADO_CIVIL,\n"
                    + " CONJUGE,\n"
                    + " PAI,\n"
                    + " MAE,\n"
                    + " SEXO,\n"
                    + " RENDA,\n"
                    + " DATA_CADASTRO,\n"
                    + " CASE WHEN STATUS = 'ATIVO' THEN 1\n"
                    + "     ELSE 0 END situacao,\n"
                    + " PESSOA,\n"
                    + " CNPJ,\n"
                    + " IE,\n"
                    + " LIMITE,\n"
                    + " AUTORIZADO\n"
                    + "FROM CLIENTES"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setDataNascimento(rst.getDate("DATA_NASCIMENTO"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));

                    if ("PESSOA FISICA".equals(rst.getString("pessoa"))) {
                        imp.setCnpj(rst.getString("cpf"));
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else {
                        imp.setCnpj(rst.getString("cnpj"));
                        imp.setInscricaoestadual(rst.getString("ie"));
                    }

                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));

                    imp.setDataCadastro(rst.getDate("DATA_CADASTRO"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setAtivo(rst.getBoolean("situacao"));

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
                    + " NORDEM id,\n"
                    + " COD_CLIENTE clienteid,\n"
                    + " N_PEDIDO numerodocumento,\n"
                    + " N_PARCELA parcela,\n"
                    + " DATA,\n"
                    + " VENCIMENTO,\n"
                    + " VENDA valor,\n"
                    + " DESCONTO,\n"
                    + " CAIXA ecf,\n"
                    + " N_RECIBO,\n"
                    + " COD_LOJA,\n"
                    + " FORMA\n"
                    + "FROM FICHA\n"
                    + "WHERE \n"
                    + "FORMA = 'FICHA'\n"
                    + "AND DATA_PAGO IS NULL \n"
                    + "AND STATUS = 'FINALIZADO';"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("clienteid"));
                    imp.setDataEmissao(rst.getDate("data"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setNumeroDocumento(rst.getString("numeroDocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setVencimento(rst.getDate("dtvencimento"));
                    imp.setObservacao(rst.getString("obs"));

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
        return new WLSDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new WLSDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                    = " SELECT\n"
                    + "  N_PEDIDO||'-'||CAIXA id_venda,\n"
                    + "  CAIXA ecf,\n"
                    + "  DATA,\n"
                    + "  HORA,\n"
                    + "  STATUS,\n"
                    + "  TOTAL valor,\n"
                    + "  coalesce(CUPOM_FISCAL,N_PEDIDO) numerocupom\n"
                    + "FROM VENDAS\n"
                    + "WHERE\n"
                    + " DATA >= '" + strDataInicio + "'\n"
                    + " AND \n"
                    + " DATA <= '" + strDataTermino + "'\n"
                    + " AND  N_PEDIDO||'-'||CAIXA IS NOT NULL ";//AND STATUS <> 'CANCELADO'"; PdvVendaDAO
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
                        next.setProduto(rst.getString("produtoid"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setQuantidade(rst.getDouble("QTD"));
                        next.setPrecoVenda(rst.getDouble("valor"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT \n"
                    + "  v.NORDEM id_item,\n"
                    + "  v.N_PEDIDO||'-'||v.CAIXA id_venda,\n"
                    + "  v.CAIXA ecf,\n"
                    + "  v.DATA,\n"
                    + "  v.HORA,\n"
                    + "  v.COD_PRODUTO produtoid,\n"
                    + "  v.COD_BARRAS codigobarras,\n"
                    + "  v.QTD,\n"
                    + "  v.VENDA valor,\n"
                    + "  substring(p.UNIDADE_MEDIDA FROM 1 FOR 2) unidade\n"
                    + "  FROM VENDAS_DETALHES v\n"
                    + "  JOIN PRODUTOS p ON p.COD_PRODUTO = v.COD_PRODUTO\n"
                    + " WHERE \n"
                    + " v.DATA >= '"+VendaIterator.FORMAT.format(dataInicio)+"'\n"
                    + " AND \n"
                    + " v.DATA <= '"+VendaIterator.FORMAT.format(dataTermino)+"'\n"
                    + " AND \n"
                    + " STATUS = 'FINALIZADO' OR STATUS IS NULL"; //PdvVendaItemDAO
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
