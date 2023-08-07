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
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Wagner
 */
public class SaurusPDVDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "SaurusPDV";
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select DISTINCT\n"
                    + " concat(pro_icms_cst,'.', pro_icms_pIcms,'.',pro_icms_pRedBC) id,\n"
                    + " concat(pro_icms_cst,' aliquota: ', pro_icms_pIcms, ' RDZ: ', pro_icms_pRedBC) descricao,\n"
                    + " pro_icms_cst cst,\n"
                    + " pro_icms_pIcms aliquota,\n"
                    + " pro_icms_pRedBC reducao \n"
                    + "from tbProdutoImpostos "
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + " pro_descCategoria merc1,\n"
                    + " pro_descSubCategoria merc2\n"
                    + "from tbProdutoDados \n"
                    + "order by 1,2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2"));
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " p.pro_idProduto  id,\n"
                    + " concat(trib.pro_icms_cst,'.', trib.pro_icms_pIcms,'.',trib.pro_icms_pRedBC) idaliquota,\n"
                    + " p.pro_descProduto descricao,\n"
                    + " p.pro_descCategoria merc1,\n"
                    + " p.pro_descSubCategoria merc2,\n"
                    + " SUBSTRING(p.pro_descMedida,1,2) embalagem,\n"
                    + " p.pro_tpBalanca ebalanca,\n"
                    + " p.pro_dVal validade,\n"
                    + " case when p.pro_indStatus = 0 then 1 else 0 end situacao,\n"
                    + " ean.pro_codProduto ean,\n"
                    + " trib.pro_pis_cst pis,\n"
                    + " trib.pro_cofins_cst cofins,\n"
                    + " ncmcest.pro_codigo ncm,\n"
                    + " ncmcest.pro_cest cest,\n"
                    + " pv.pro_vPreco precovenda,\n"
                    + " p.pro_vCompra custo\n"
                    + "from tbProdutoDados p\n"
                    + "left join tbProdutoCodigos ean on ean.pro_idProduto = p.pro_idProduto\n"
                    + "left join tbProdutoImpostos trib on trib.pro_idImposto = p.pro_idImposto \n"
                    + "left join tbProdutoNcms ncmcest on ncmcest.pro_idNcm = p.pro_idNcm \n"
                    + "left join tbProdutoPrecos pv on pv.pro_idProduto = p.pro_idProduto"
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
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setCustoComImposto(imp.getCustoSemImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(0);

                    imp.setPiscofinsCstDebito(rst.getString("pis"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
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
                        imp.setTipoEmbalagem(rst.getString("embalagem"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setQtdEmbalagem(1);
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " cad_idCadastro id,\n"
                    + " cad_doc cpfcnpj,\n"
                    + " cad_ie ie,\n"
                    + " cad_xNome nome,\n"
                    + " cad_cep cep,\n"
                    + " cad_xLgr endereco,\n"
                    + " cad_nro numero,\n"
                    + " cad_email email,\n"
                    + " cad_infAdic obs,\n"
                    + " case when cad_indStatus = 0 then 1 else 0 end situacao\n"
                    + "from tbCadastroDados"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));

                    imp.setObservacao(rst.getString("obs"));
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
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
        return new SaurusPDVDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SaurusPDVDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
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

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
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
