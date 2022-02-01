/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Wagner
 */
public class SatFacilDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SatFacil";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT \n"
                    + "  ID_REGISTRO id,\n"
                    + "  NOME_LOJA descricao,\n"
                    + "  '1' cnpj"
                    + "  FROM PARAMETROS p"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cnpj"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    public List<String> getNomeLojaCliente() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "s SELECT \n"
                    + "  ID_REGISTRO id,\n"
                    + "  NOME_LOJA descricao\n"
                    + "  FROM PARAMETROS p"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("descricao"));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + " CASE WHEN replace(IMPOSTO_ECF,',','') = 'FF' THEN '60'\n"
                    + "      WHEN replace(IMPOSTO_ECF,',','') = 'II' THEN '40'\n"
                    + "      ELSE '00' END id,\n"
                    + " IMPOSTO_ECF descricao,\n"
                    + " REDUCAO_BASE_ICMS\n"
                    + "FROM PRODUTOS "
            )) {
                while (rst.next()) {

                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao") + '-' + rst.getString("id")
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
                    "SELECT \n"
                    + " CODIGO id,\n"
                    + " SECAO descricao\n"
                    + "FROM SECAO "
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("id"));
                    imp.setMerc1Descricao(rst.getString("descricao"));
                    imp.setMerc2ID(rst.getString("id"));
                    imp.setMerc2Descricao(rst.getString("descricao"));
                    imp.setMerc3ID(rst.getString("id"));
                    imp.setMerc3Descricao(rst.getString("descricao"));
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
                    + " p.CODIGO id,\n"
                    + " p.CODIGO_BARRAS ean,\n"
                    + " p.CODIGO_NCM ncm,\n"
                    + " p.DATA_CADASTRO dtcadastro,\n"
                    + " p.DESCRICAO descricao,\n"
                    + " p.CODIGO_SECAO merc1,\n"
                    + " p.CODIGO_SECAO merc2,\n"
                    + " p.CODIGO_SECAO merc3,\n"
                    + " p.CODIGO_UNIDADE unidade,\n"
                    + " p.VALOR_CUSTO valorcusto,\n"
                    + " p.VALOR_VENDA_1 precovenda,\n"
                    + " e.ESTOQUE_ATUAL estoque,\n"
                    + " p.ESTOQUE_MINIMO estoqueminimo,\n"
                    + " p.INCLUIR_BALANCA balanca,\n"
                    + " CASE WHEN p.ATIVO = 'A' THEN 1\n"
                    + " ELSE 0 END status,\n"
                    + " p.CEST,\n"
                    + " p.CST_PIS_COFINS,\n"
                    + " CASE WHEN replace(p.IMPOSTO_ECF,',','') = 'FF' THEN '60'\n"
                    + "      WHEN replace(p.IMPOSTO_ECF,',','') = 'II' THEN '40'\n"
                    + "      ELSE '00' END aliquota_id,\n"
                    + " p.REDUCAO_BASE_ICMS\n"
                    + "FROM PRODUTOS p \n"
                    + "LEFT JOIN PRODUTOS_ESTOQUE e ON e.CODIGO = p.CODIGO"
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
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setCustoSemImposto(rst.getDouble("valorcusto"));
                    imp.setCustoComImposto(rst.getDouble("valorcusto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("CST_PIS_COFINS"));
                    imp.setPiscofinsCstDebito(rst.getString("CST_PIS_COFINS"));

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

                    imp.setIcmsDebitoId(rst.getString("aliquota_id"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("aliquota_id"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("aliquota_id"));
                    imp.setIcmsCreditoId(rst.getString("aliquota_id"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("aliquota_id"));
                    imp.setIcmsConsumidorId(rst.getString("aliquota_id"));

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
                    "SELECT \n"
                    + " pb.CODIGO_PRODUTO produtoid,\n"
                    + " pb.CODIGO_BARRAS ean,\n"
                    + " p.CODIGO_UNIDADE unidade\n"
                    + "FROM PRODUTOS_BARRAS pb \n"
                    + "JOIN PRODUTOS p ON p.CODIGO = pb.CODIGO_PRODUTO"
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
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	f.CODIGO,\n"
                    + "	f.SEQ_GESTAO,\n"
                    + "	f.RAZAO_SOCIAL,\n"
                    + "	f.NOME_FANTASIA,\n"
                    + "	f.CNPJ_NUMEROS CNPJ,\n"
                    + "	f.ATIVO,\n"
                    + "	f.IE,\n"
                    + "	f.DATA_CADASTRO,\n"
                    + "	f.ENDERECO,\n"
                    + "	f.NUMERO,\n"
                    + "	f.COMPLEMENTO,\n"
                    + "	f.BAIRRO,\n"
                    + "	f.IBGE_CODIGO_CIDADE,\n"
                    + "	f.CIDADE,\n"
                    + "	f.ESTADO,\n"
                    + "	f.CEP,\n"
                    + "	f.FONE,\n"
                    + "	f.FAX,\n"
                    + "	f.SITE,\n"
                    + "	f.EMAIL,\n"
                    + "	f.CONTATO,\n"
                    + "	f.FONE_CONTATO,\n"
                    + "	f.NOME_VENDEDOR,\n"
                    + "	f.FONE_VENDEDOR\n"
                    + "FROM \n"
                    + "	FORNECEDORES f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("CODIGO"));
                    imp.setRazao(rst.getString("RAZAO_SOCIAL"));
                    imp.setFantasia(rst.getString("NOME_FANTASIA"));
                    imp.setCnpj_cpf(rst.getString("CNPJ"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setTel_principal(rst.getString("FONE"));
                    imp.setDatacadastro(rst.getDate("DATA_CADASTRO"));

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
                    "SELECT\n"
                    + "	CODIGO id,\n"
                    + "	NOME razao,\n"
                    + "	FANTASIA,\n"
                    + "	CPF_CNPJ,\n"
                    + "	RG,\n"
                    + "	ENDERECO,\n"
                    + "	NUMERO,\n"
                    + "	COMPLEMENTO,\n"
                    + "	BAIRRO,\n"
                    + "	CIDADE,\n"
                    + "	ESTADO,\n"
                    + "	CEP,\n"
                    + "	FONE,\n"
                    + "	CELULAR,\n"
                    + "	EMAIL,\n"
                    + "	CASE WHEN ATIVO = 'S' THEN 1 ELSE 0 END ativo, \n"
                    + "	DATA_CADASTRO,\n"
                    + "	NASCTO data_nasc,\n"
                    + "	MAE nomemae,\n"
                    + "	PAI nomepai,\n"
                    + "	CONJUGE nomeconjuge,\n"
                    + "	ESTADO_CIVIL,\n"
                    + "	LIMITE_CREDITO valorlimite,\n"
                    + "	DIA_DE_VENCIMENTO,\n"
                    + "	OBSERVACOES1 obs \n"
                    + "FROM\n"
                    + "	CLIENTES c\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setDataNascimento(rst.getDate("data_nasc"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setCnpj(rst.getString("CPF_CNPJ"));
                    imp.setInscricaoestadual(rst.getString("RG"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));

                    if (rst.getString("FONE") == null) {
                        imp.setTelefone(rst.getString("FONE"));
                    } else if (rst.getString("FONE").startsWith("0")) {
                        imp.setTelefone(rst.getString("FONE").substring(1));
                    } else {
                        imp.setTelefone(rst.getString("FONE"));
                    }

                    imp.setCelular(rst.getString("celular"));
                    imp.setDataCadastro(rst.getDate("DATA_CADASTRO"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setEstadoCivil(rst.getString("ESTADO_CIVIL"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setAtivo(rst.getBoolean("ativo"));

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
                    "SELECT \n"
                    + " cp.ID_DOCUMENTO,\n"
                    + " f.CODIGO idfornecedor,\n"
                    + " cp.NUM_DOCUMENTO,\n"
                    + " cp.DATA_EMISSAO,\n"
                    + " cp.VENCIMENTO,\n"
                    + " cp.VALOR,\n"
                    + " cp.PARCELA,\n"
                    + " cp.OBSERVACAO\n"
                    + "FROM CONTAS_PAGAR cp\n"
                    + "JOIN FORNECEDORES f ON f.CODIGO = cp.CODIGO_FORNECEDOR\n"
                    + "WHERE \n"
                    + " cp.SITUACAO <> 'PG'\n"
                    + " AND \n"
                    + " cp.DATA_PAGTO IS NULL "
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("ID_DOCUMENTO"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setNumeroDocumento(rst.getString("NUM_DOCUMENTO"));
                    imp.setDataEmissao(rst.getDate("DATA_EMISSAO"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setVencimento(rst.getDate("VENCIMENTO"));
                    imp.setObservacao(rst.getString("OBSERVACAO"));

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
                    "SELECT\n"
                    + "	cr.CODIGO id,\n"
                    + "	DATA_EMISSAO,\n"
                    + "	NUMERO_CUPOM,\n"
                    + "	NUMERO_ECF ecf,\n"
                    + "	VALOR_TOTAL valor,\n"
                    + "	CODIGO_CLIENTE idcliente,\n"
                    + "	c.CPF_CNPJ,\n"
                    + "	DATA_VENCIMENTO vencimento,\n"
                    + "	VALOR_JUROS juros,\n"
                    + "	MULTA_BOLETO_P multa,\n"
                    + "	OBS1\n"
                    + "FROM\n"
                    + "	CONTAS_RECEBER cr\n"
                    + "	JOIN CLIENTES c ON c.CODIGO = cr.CODIGO_CLIENTE\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("DATA_EMISSAO"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setNumeroCupom(rst.getString("NUMERO_CUPOM"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));

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
        return new SatFacilDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SatFacilDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        //next.setCancelado(rst.getBoolean("cancelado"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setNomeCliente(rst.getString("nome_cliente"));
                        next.setCpf(rst.getString("cpf_cnpj"));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        String horaInicio = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
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
                    = "";
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
                        next.setSequencia(rst.getInt("nro_item"));
                        next.setProduto(rst.getString("produto"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
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
                    = "";
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
