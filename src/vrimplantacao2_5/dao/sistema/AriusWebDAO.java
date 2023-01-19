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
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;

/**
 *
 * @author Alan
 */
public class AriusWebDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ARIUS_WEB";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.ATIVO,
            OpcaoProduto.CEST,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MARGEM,
            OpcaoProduto.QTD_EMBALAGEM_EAN,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.NCM,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.DESCONTINUADO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.OFERTA,
            OpcaoProduto.FABRICANTE,
            OpcaoProduto.VENDA_PDV,
            OpcaoProduto.PDV_VENDA
        }));
    }

    /*@Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }*/
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.RAZAO,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigo id,\n"
                    + "	descricao,\n"
                    + "	case\n"
                    + "	  when codigo = 'F' then '60'\n"
                    + "	  when codigo = 'I' then '40'\n"
                    + "	  when codigo = 'Z' then '00'\n"
                    + "	  when codigo = 'Y' then '00'\n"
                    + "	  when codigo = 'X' then '00'\n"
                    + "	  when codigo = 'D' then '51'\n"
                    + "   when codigo = 'N' then '41'\n"
                    + "	  else '00'\n"
                    + "	end cst,\n"
                    + "	percentual aliq,\n"
                    + "	0 red\n"
                    + "from\n"
                    + "	controle.tributacoes t\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("red")));
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigoean id_produto,\n"
                    + "	codigoean codigobarras,\n"
                    + "	unidade\n"
                    + "from\n"
                    + "	mercador m\n"
                    + "where\n"
                    + "	nroloja = " + getLojaOrigem() + "\n"
                    + "	and codigoint = 0"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(1);

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	m1.codigo merc1,\n"
                    + "	m1.descricao descmerc1,\n"
                    + "	COALESCE (m2.codigo, m1.codigo) merc2,\n"
                    + "	COALESCE (m2.descricao, m1.descricao) descmerc2\n"
                    + "from\n"
                    + "	controle.secoes m1\n"
                    + "	left join controle.grupos m2 on m1.codigo = m2.codsecao\n"
                    + "order by 1, 3")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc2"));
                    imp.setMerc3Descricao(rs.getString("descmerc2"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	case when codigoint = 0 then codigoean else codigoint end id,\n"
                    + " case when flg_eanvalido = 0 then 1 else 0 end pesavel,\n"
                    + "	codigoean ean,\n"
                    + "	descricao_completa,\n"
                    + "	descricao desc_reduzida,\n"
                    + "	custo precocusto,\n"
                    + "	valor precovenda,\n"
                    + "	unidade,\n"
                    + "	estoque_atual estoque,\n"
                    + "	depto merc1,\n"
                    + "	case when Grupo = 0 then depto end merc2,\n"
                    + "	case when Grupo = 0 then depto end merc3,\n"
                    + "	tributacao id_icms,\n"
                    + "	ncm,\n"
                    + "	cest,\n"
                    + "	cst_pis_cofins pis_cofins\n"
                    + "from\n"
                    + "	mercador p\n"
                    + "where\n"
                    + "	nroloja = " + getLojaOrigem() + "\n"
                    + "order by 1"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getBoolean("pesavel"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(imp.getImportId());
                    }

                    imp.setDescricaoCompleta(rs.getString("descricao_completa"));
                    imp.setDescricaoReduzida(rs.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());
                    imp.setTipoEmbalagem(rs.getString("unidade"));

                    imp.setCodMercadologico1("merc1");
                    imp.setCodMercadologico2("merc2");
                    imp.setCodMercadologico3("merc3");

                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rs.getDouble("estoque"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setIcmsDebitoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());

                    imp.setPiscofinsCstDebito(rs.getString("pis_cofins"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	Convenio id,\n"
                    + "	CodCgcCpf cpf_cnpj,\n"
                    + "	IE ie_rg,\n"
                    + "	Nome,\n"
                    + "	Endereco,\n"
                    + "	numero_predio numero,\n"
                    + "	complemento,\n"
                    + "	Bairro,\n"
                    + "	Cidade,\n"
                    + "	uf,\n"
                    + "	Cep,\n"
                    + "	Fone,\n"
                    + "	email,\n"
                    + "	observacao,\n"
                    + "	case when status_cli = 'A' then 1 else 0 end situacao\n"
                    + "from\n"
                    + "	clientes c")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie_rg"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setEmail(rs.getString("email"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setAtivo(rs.getBoolean("situacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CONCAT( pdv, nrocupom, codconvenio) id,\n"
                    + "	pdv,\n"
                    + " nrocupom cupom,\n"
                    + "	codconvenio id_cliente,\n"
                    + "	valor,\n"
                    + "	dataproc emissao,\n"
                    + "	dataVencimento vencimento\n"
                    + "FROM\n"
                    + "	convenio c\n"
                    + "WHERE\n"
                    + "	nroloja = " + getLojaOrigem() + ""
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setEcf(rs.getString("pdv"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));

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
        return new AriusWebDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new AriusWebDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");

                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }

                        next.setId(id);
                        next.setNumeroCupom(rst.getInt("cupom"));
                        next.setEcf(rst.getInt("ecf"));
                        next.setData(rst.getDate("datavenda"));
                        String horaInicio = timestampDate.format(rst.getDate("datavenda")) + " 00:00:00";
                        String horaTermino = timestampDate.format(rst.getDate("datavenda")) + " 00:00:00";
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	CONCAT(NroCupom,Pdv,NroItens,DataProc,numabertura) id_venda, \n"
                    + "	NroCupom cupom,\n"
                    + "	pdv ecf,\n"
                    + "	HoraFim datavenda,\n"
                    // + "	SUBSTRING(HoraFim, 12,5) hora, \n"
                    + "	total valor,\n"
                    + "	case when FlagFimCupom = 0 then 1 else 0 end cancelado\n"
                    + "from\n"
                    + "	cupom \n"
                    + "where\n"
                    + "	nroloja = " + idLojaCliente + "\n"
                    + "	and DataProc BETWEEN '" + dataInicio + "' and '" + dataTermino + "'\n"
                    + "	order by 2,3,4";
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

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id_item"));
                        next.setVenda(rst.getString("id_venda"));
                        next.setSequencia(rst.getInt("sequencial"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setDescricaoReduzida(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	CONCAT(v.NroCupom, v.Pdv, v.NroItens, v.DataProc, v.numabertura) id_venda,\n"
                    + "	CONCAT(vi.NroCupom,vi.pdv,vi.Item, vi.Codigo, vi.HoraMinSeg) id_item,\n"
                    + "	Item sequencial,\n"
                    + "	Codigo id_produto,\n"
                    + "	p.descricao produto,\n"
                    + "	p.unidade unidade,\n"
                    + "	vi.Codigo ean,\n"
                    + "	Quantidade, \n"
                    + "	ValorUnitario precovenda,\n"
                    + "	vi.valor total,\n"
                    + "	valordesc desconto,\n"
                    + "	Estornado cancelado\n"
                    + "from\n"
                    + "	itens vi\n"
                    + "	join cupom v on v.nroloja = vi.nroloja and v.NroCupom = vi.NroCupom \n"
                    + "	join mercador p on p.nroloja = vi.nroloja and p.codigoean = vi.Codigo \n"
                    + "where\n"
                    + "	v.nroloja = " + idLojaCliente + "\n"
                    + "	and v.DataProc BETWEEN '" + dataInicio + "' and '" + dataTermino + "'\n"
                    + "order by 1,2,3";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            System.out.println(sql);
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
