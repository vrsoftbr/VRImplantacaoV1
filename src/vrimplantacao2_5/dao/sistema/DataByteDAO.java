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
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Wagner
 */
public class DataByteDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "DataByte";
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT distinct\n"
                    + " COD_ICMS id,\n"
                    + " ALIQ_ICMS aliquota,\n"
                    + " SITTRIBUTARIA descricao\n"
                    + "FROM ICMS"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao")
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
                    "SELECT\n"
                    + "	DISTINCT \n"
                    + "	COD_MARCA mercid1,\n"
                    + "	m.DESCRICAO descri1,\n"
                    + "	COALESCE(COD_SUBGRUPO,	COD_MARCA) mercid2,\n"
                    + "	COALESCE(s.DESCRICAO,	m.DESCRICAO) descri2,\n"
                    + "	COALESCE(COD_SUBGRUPO,	COD_MARCA) mercid3,\n"
                    + "	COALESCE(s.DESCRICAO,	m.DESCRICAO) descri3\n"
                    + "FROM\n"
                    + "	PRODUTOS p\n"
                    + "LEFT JOIN MARCAS m ON m.CODIGO = p.COD_MARCA\n"
                    + "LEFT JOIN SUBGRUPOS s ON s.CODIGO = p.COD_SUBGRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("descri1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("descri2"));
                    imp.setMerc3ID(rst.getString("mercid3"));
                    imp.setMerc3Descricao(rst.getString("descri3"));
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
                    + " p.COD_PROD id,\n"
                    + " p.COD_BARRA ean,\n"
                    + " p.NOME_PROD descricao,\n"
                    + " p.COD_MARCA mercid1,\n"
                    + " CASE WHEN p.COD_SUBGRUPO IS NULL THEN p.COD_MARCA\n"
                    + " ELSE p.COD_SUBGRUPO END mercid2,\n"
                    + " CASE WHEN p.COD_SUBGRUPO IS NULL THEN p.COD_MARCA\n"
                    + " ELSE p.COD_SUBGRUPO END mercid3,\n"
                    + " p.UNIDADE unidade,\n"
                    + " p.PRECO_VEND precovenda,\n"
                    + " p.PRECOCOMPRA custosemimposto,\n"
                    + " p.PRECO_CUST custocomimposto,\n"
                    + " p.C_MEDIO customedio,\n"
                    + " p.MARGEM margem,\n"
                    + " p.COD_FORNEC,\n"
                    + " p.PESO,\n"
                    + " CASE WHEN p.BALANCA = 'S' THEN 1\n"
                    + " ELSE 0 END AS BALANCA,\n"
                    + " CASE WHEN p.STATUS = 'A' THEN 1\n"
                    + " ELSE 0 END AS situacao,\n"
                    + " p.CLASFISCAL ncm,\n"
                    + " ESTQ.QUANTIDADE estoque,\n"
                    + " p.PIS_CST piscofins,\n"
                    + " p.COFINS_CST piscofins2,\n"
                    + " p.ICMS idaliquota,\n"
                    + " p.DTCADASTRO dtcadastro,\n"
                    + " p.SUBSTITUICAO,\n"
                    + " p.CEST cest\n"
                    + " FROM PRODUTOS p\n"
                    + " JOIN ESTOQUEFILIAL estq ON estq.COD_PROD = p.COD_PROD"//AND ESTQ.COD_FILIAL = "+ getLojaOrigem() +" "
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));
                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
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

                    imp.setIcmsDebitoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsConsumidorId(rst.getString("idaliquota"));

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
                    + "	CODIGO id,\n"
                    + "	NOME razao,\n"
                    + "	ENDERECO endereco,\n"
                    + "	BAIRRO bairro,\n"
                    + "	CEP cep,\n"
                    + "	CIDADE municipio,\n"
                    + "	ESTADO uf,\n"
                    + "	NUMERO numero,\n"
                    + "	FONE tel_principal,\n"
                    + "	FAX,\n"
                    + "	CGC cpfcnpj,\n"
                    + "	IE inscestadual,\n"
                    + "	FANTASIA fantasia,\n"
                    + "	EMAIL,\n"
                    + "	DTCADASTRO dtcadastro,\n"
                    + "	STATUS\n"
                    + "FROM\n"
                    + "	FORNEC"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("inscestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));

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
                    "SELECT\n"
                    + "	COD_PROD produtoid,\n"
                    + "	COD_FORNEC fornecedorid,\n"
                    + "	REFERENCIA\n"
                    + "FROM\n"
                    + "	PRODUTOS_FORNECEDOR;"
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
                    "SELECT\n"
                    + "	ID as id,\n"
                    + "	COD_PROD produtoid,\n"
                    + "	COD_BARRA as ean\n"
                    + "FROM\n"
                    + "	PRODUTOS_BARRAS\n"
                    + "UNION\n"
                    + "SELECT\n"
                    + "	ID || '.' || ID as id,\n"
                    + "	COD_PROD produtoid,\n"
                    + "	COD_BARRA_GRADE as ean\n"
                    + "FROM\n"
                    + "	PRODUTOS_BARRAS\n"
                    + "WHERE\n"
                    + "	COD_BARRA_GRADE IS NOT NULL"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produtoid"));
                    imp.setEan(rst.getString("ean"));

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
                    + " NOME nome,\n"
                    + " ENDERECO endereco,\n"
                    + " NUMERO numero,\n"
                    + " BAIRRO bairro,\n"
                    + " CEP cep,\n"
                    + " CIDADE municipio,\n"
                    + " UF uf,\n"
                    + " TIPO,\n"
                    + " CGC cpfcnpj,\n"
                    + " IE inscestrg,\n"
                    + " DDD||FONE telefone,\n"
                    + " FONE1 celular,\n"
                    + " OBS||' '||LEMBRETE obs,\n"
                    + " DT_INC dtcadastro,\n"
                    + " NASC dtnasc,\n"
                    + " CIVIL,\n"
                    + " CONJUGE nomeconjuge,\n"
                    + " NAS_CO,\n"
                    + " CPF_CO,\n"
                    + " PAI nomepai,\n"
                    + " MAE nomemae,\n"
                    + " LIMITE limite,\n"
                    + " DTATUALIZACAO\n"
                    + "FROM CADCLI "
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
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("inscestrg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));

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
                    + " SEQ id,\n"
                    + " COD_CLI idcliente,\n"
                    + " COD_FILIAL,\n"
                    + " NUM_OPER numerodocumento,\n"
                    + " DT_VENDA dataemissao,\n"
                    + " DTATUALIZACAO,\n"
                    + " VALOR valor,\n"
                    + " N_FISCAL,\n"
                    + " PARCELA,\n"
                    + " VENCIMENTO datavencimento,\n"
                    + " STATUS\n"
                    + "FROM RECEBER \n"
                    + "WHERE \n"
                    + " BAIXADO <> 'S'"
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

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT  \n"
                    + " SEQ id,\n"
                    + " VENCIMENTO dtvencimento,\n"
                    + " COD_FORNEC idfornecedor,\n"
                    + " COD_FILIAL,\n"
                    + " EMISSAO dataemissao,\n"
                    + " VALOR valor,\n"
                    + " PRESTACAO obs,\n"
                    + " TIPO_DOC,\n"
                    + " NR_DOCUMENTO numeroDocumento,\n"
                    + " DT_PGTO,\n"
                    + " BAIXADO,\n"
                    + " NUM_OPER\n"
                    + "FROM PAGAR\n"
                    + "WHERE BAIXADO <> 'S'"
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
        return new DataByteDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new DataByteDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        next.setNumeroSerie(rst.getString("serie"));
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
                    = "SELECT\n"
                    + "	s.id id_venda,\n"
                    + "	CASE WHEN s.NUM_CUPOM IS NULL THEN s.id||'-'||s.NUM_OPER ELSE s.NUM_CUPOM END numerocupom,\n"
                    + "	s.NUM_CAIXA ecf,\n"
                    + "	s.DTEMISSAO data,\n"
                    + "	s.HORAEMISSAO hora,\n"
                    + "	s.SERIE serie,\n"
                    + "	s.VRTOTALPRODUTOS valor,\n"
                    + "	s.COD_CLI id_cliente,\n"
                    + "	c.CGC cpf,\n"
                    + "	s.NOME nomecliente,\n"
                    + "	CASE WHEN STATUS = 'C' THEN 1 ELSE 0 END cancelado\n"
                    + "FROM\n"
                    + "	SAT_MOVIMENTO s\n"
                    + "	LEFT JOIN CADCLI c ON s.COD_CLI = c.CODIGO \n"
                    + "WHERE\n"
                    + "	s.COD_FILIAL = " + idLojaCliente + "\n"
                    + "	AND s.DTEMISSAO BETWEEN '" + strDataInicio + "' and '" + strDataTermino + "'\n";
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
                    + "	si.NUMEROITEM nritem,\n"
                    + "	si.ID_SAT_MVTO id_venda,\n"
                    + "	si.id id_item,\n"
                    + "	si.COD_PROD id_produto,\n"
                    + "	si.NOME_PROD descricao,\n"
                    + "	si.QUANTIDADE quantidade,\n"
                    + "	si.PRECO_VEND valor,\n"
                    + "	si.VRDESCONTO desconto,\n"
                    + "	p.UNIDADE unidade\n"
                    + "FROM\n"
                    + "	SAT_ITENS si\n"
                    + "	LEFT JOIN SAT_MOVIMENTO sm ON si.ID_SAT_MVTO = sm.ID \n"
                    + "	JOIN PRODUTOS p ON si.COD_PROD = p.COD_PROD "
                    + "WHERE\n"
                    + "	sm.DTEMISSAO BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n";
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