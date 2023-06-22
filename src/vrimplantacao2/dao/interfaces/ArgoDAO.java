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
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class ArgoDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Argo";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.PDV_VENDA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.SEXO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.VALOR_LIMITE));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	DISTINCT \n"
                    + "	cst_icms + '-' + cast(aliq_icms as varchar(4)) + '-' + cast(base_red_icms as varchar(2)) as id,\n"
                    + "	'CST '+cst_icms + ' ALIQ '+cast(aliq_icms as varchar(4)) + ' RED ' + cast(base_red_icms as varchar(2)) as descricao,\n"
                    + "	cst_icms cst_icms,\n"
                    + "	aliq_icms,\n"
                    + "	base_red_icms red_icms\n"
                    + "from\n"
                    + "	PROD_NAT_OP\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_icms"),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codgru merc1,\n"
                    + "	desgru descmerc1,\n"
                    + "	codgru merc2,\n"
                    + "	desgru descmerc2,\n"
                    + "	codgru merc3,\n"
                    + "	desgru descmerc3\n"
                    + "from\n"
                    + "	GRUPRO\n"
                    + "order by 1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	CODPRO id_produto,\n"
                    + "	CODIGOEAN ean,\n"
                    + "	QTDUNIBASE qtde_emb,\n"
                    + "	UNISAIDA tipo_emb\n"
                    + "from\n"
                    + "	PRODUTOS p\n"
                    + "WHERE\n"
                    + "	codemp = '" + getLojaOrigem() + "'"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtde_emb"));
                    imp.setTipoEmbalagem(rs.getString("tipo_emb"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.CODPRO id,\n"
                    + "	p.CODIGOEAN ean,\n"
                    + "	p.NOMPRO desc_completa,\n"
                    + "	DESPRO desc_reduzida,\n"
                    + "	cast (DATA_CREATE as date) data_cad,\n"
                    + "	cast (DATA_CHANGE as date) data_alt,\n"
                    + "	UNISAIDA tipo_emb,\n"
                    + "	QTDUNIBASE qtde_emb,\n"
                    + "	case when STATUS = 'A' then 1 else 0 end ativo,\n"
                    + "	vvenda precovenda,\n"
                    + "	vprecocusto custo,\n"
                    + "	p2.codcla ncm,\n"
                    + "	estmin,\n"
                    + "	estmax,\n"
                    + "	p.codgru merc1,\n"
                    + "	p.codgru merc2,\n"
                    + "	p.codgru merc3,\n"
                    + "	cst_icms + '-' + cast(aliq_icms as varchar(4)) + '-' + cast(base_red_icms as varchar(2)) as id_icms,\n"
                    + "	cst_pis pis_debito,\n"
                    + "	cst_pis_e pis_credito,\n"
                    + "	cod_nat_rec nat_rec\n"
                    + "from\n"
                    + "	PRODUTOS p\n"
                    + "join PRODCPL1 p2 on p.codpro = p2.codpro and p.codemp = p2.codemp\n"
                    + "join PRODFISCAL pf on p.codpro = pf.codpro and p.codemp = pf.codemp\n"
                    + "join GETPROTOVENDA pr on p.codpro = pr.codpro and p.codemp = pr.codemp\n"
                    + "left join PROD_NAT_OP tr on p.codpro = tr.codpro and p.codemp = tr.codemp\n"
                    + "WHERE\n"
                    + "	p.codemp = '" + getLojaOrigem() + "'"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString("desc_completa"));
                    imp.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("data_cad"));
                    imp.setDataAlteracao(rst.getDate("data_alt"));
                    imp.setTipoEmbalagem(rst.getString("tipo_emb"));
                    imp.setQtdEmbalagem(rst.getInt("qtde_emb"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    //imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));

                    //imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));

                    imp.setNcm(rst.getString("ncm"));
                    //imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_debito"));
                    imp.setPiscofinsCstCredito(rst.getString("pis_credito"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("nat_rec"));

                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codcli id,\n"
                    + "	razcli razao,\n"
                    + "	fancli fantasia,\n"
                    + "	cgccli cpf_cnpj,\n"
                    + "	inscli rg_ie,\n"
                    + "	endcli endereco,\n"
                    + "	nroende numero,\n"
                    + "	endecpl complemento,\n"
                    + "	baicli bairro,\n"
                    + "	cidcli cidade,\n"
                    + "	estcli uf,\n"
                    + "	cepcli cep,\n"
                    + "	foncli fone,\n"
                    + "	mailcli email\n"
                    + "from\n"
                    + "	FORNE"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpf_cnpj"));
                    imp.setIe_rg(rst.getString("rg_ie"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTel_principal(rst.getString("fone"));

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "E-mail",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email")
                        );
                    }

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codcli id,\n"
                    + "	razcli razao,\n"
                    + "	fancli fantasia,\n"
                    + "	cgccli cpf_cnpj,\n"
                    + "	inscli rg_ie,\n"
                    + "	cast(data_create as date) data_cad,\n"
                    + "	endcli endereco,\n"
                    + "	nroende numero,\n"
                    + "	baicli bairro,\n"
                    + "	cidcli cidade,\n"
                    + " estcli uf,\n"
                    + "	cepcli cep,\n"
                    + "	foncli fone,\n"
                    + "	faxcli fax\n"
                    + "from\n"
                    + "	clientes"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("rg_ie"));
                    imp.setDataCadastro(rst.getDate("data_cad"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setCep(rst.getString("cep"));
                    imp.setUf(rst.getString("uf"));

                    imp.setTelefone(rst.getString("fone"));
                    imp.setFax(rst.getString("fax"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new ArgoDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new ArgoDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
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
                        next.setSubTotalImpressora(rst.getDouble("total"));
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
                    = "SELECT \n"
                    + "	ORDLAN id_venda,\n"
                    + "	NROCPO numerocupom,\n"
                    + "	CODCXA ecf,\n"
                    + "	CAST(DATLAN as date) data, \n"
                    + "	HSR_CR hora,\n"
                    + "	VLRTOT total,\n"
                    + "	CASE WHEN STALAN = 'X' THEN 0 ELSE 1 END cancelado\n"
                    + "FROM\n"
                    + "	HNFCE h\n"
                    + "WHERE\n"
                    + "	CODEMP = '" + idLojaCliente + "'\n"
                    + "	and CAST(DATLAN as date) BETWEEN '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + "ORDER BY 1,4";
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
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));
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
                    + "	v.ORDLAN id_venda,\n"
                    + "	CAST(vi.ORDLAN as varchar) +'-'+ CAST(vi.NROCPO as varchar) +'-'+ CAST(vi.NROORD as varchar) id_item,\n"
                    + "	vi.NROORD nroitem,\n"
                    + "	vi.codpro produto,\n"
                    + "	unimov unidade,\n"
                    + "	p.CODIGOEAN codigobarras,\n"
                    + "	p.NOMPRO descricao,\n"
                    + "	vi.QTDA quantidade,\n"
                    + "	vi.VLRUNI precovenda,\n"
                    + "	vi.VLRTOT total,\n"
                    + "	CASE WHEN vi.STALAN = 'X' THEN 0 ELSE 1 END cancelado\n"
                    + "FROM\n"
                    + "	DNFCE vi\n"
                    + "	join HNFCE v on v.ORDLAN = vi.ORDLAN and v.CODEMP = vi.CODEMP and v.NROCPO = vi.NROCPO \n"
                    + "	join PRODUTOS p on p.CODPRO = vi.codpro\n"
                    + "WHERE\n"
                    + "	v.CODEMP = '" + idLojaCliente + "'\n"
                    + "	and CAST(v.DATLAN as date) BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "'\n"
                    + " and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "ORDER BY 1, 3";
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
