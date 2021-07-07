package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class PoligonDAO extends InterfaceDAO /*implements MapaTributoProvider */ {

    private static final Logger LOG = Logger.getLogger(PoligonDAO.class.getName());

    public int v_tipoDocumento;
    public int v_tipoDocumentoCheque;
    public boolean v_usar_arquivoBalanca;
    public boolean v_usar_arquivoBalancaUnificacao;
    public boolean usarMargemBruta = false;

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        return "Poligon" + (!"".equals(complemento) ? " - " + complemento : "");
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	Id_loja id,\n"
                    + "	RazaoSocial descricao\n"
                    + "FROM \n"
                    + "	PADARIA.dbo.Loja\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    /*@Override
     public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
     List<FamiliaProdutoIMP> vResult = new ArrayList<>();
     try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
     try (ResultSet rst = stm.executeQuery(
     "select "
     + "CODFAMILIA, "
     + "descricao "
     + "from "
     + "FAMILIA"
     )) {
     while (rst.next()) {
     FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
     imp.setImportLoja(getLojaOrigem());
     imp.setImportSistema(getSistema());
     imp.setImportId(rst.getString("CODFAMILIA"));
     imp.setDescricao(rst.getString("descricao"));
     vResult.add(imp);
     }
     }
     }
     return vResult;
     }*/
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	Id_grupo m1,\n"
                    + "	Descricao m1desc,\n"
                    + "	Id_grupo m2,\n"
                    + "	Descricao m2desc,\n"
                    + "	Id_grupo m3,\n"
                    + "	Descricao m3desc\n"
                    + "FROM \n"
                    + "	PADARIA.dbo.Grupo g \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1"));
                    imp.setMerc2ID(rst.getString("m2"));
                    imp.setMerc3ID(rst.getString("m3"));
                    imp.setMerc1Descricao(rst.getString("m1desc"));
                    imp.setMerc2Descricao(rst.getString("m2desc"));
                    imp.setMerc3Descricao(rst.getString("m3desc"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.codigo importid,\n"
                    + "	p.Descricao descricaocompleta,\n"
                    + "	ref_prd cod_balanca,\n"
                    + " cartela codigobarras,\n"
                    + "	Id_grupo mercadologico1,\n"
                    + "	Id_grupo mercadologico2,\n"
                    + "	Id_grupo mercadologico3,\n"
                    + "	u.Unidade unidade,\n"
                    + " case when u.Unidade = 'KG' then 'S' else 'N' end pesavel,\n"
                    + "	PrecoCusto custosemimposto,\n"
                    + "	PrecoVenda,\n"
                    + "	Markup margem,\n"
                    + "	PesoLiq pesoliquido,\n"
                    + "	Peso pesobruto,\n"
                    + "	cf.Codigo ncm,\n"
                    + "	cest,\n"
                    + "	p.ICMS aliq_saida,\n"
                    + "	i.SitTrib cst_saida,\n"
                    + "	predbcicms red_saida,\n"
                    + "	Qtde_min estmin,\n"
                    + "	Qtde_max estmax,\n"
                    + "	Ativo,\n"
                    + "	p.Dt_Alt dataalteracao,\n"
                    + "	p.Dt_Cad datacadastro,\n"
                    + "	p.Obs1 observacao\n"
                    + "from\n"
                    + "	Produto p\n"
                    + "	left join unidade u on u.Id_unidade = p.Id_Unidade \n"
                    + "	left join SitTribut i on i.Id_Sittrib = p.Id_Sittrib \n"
                    + "	left join ClasFiscal cf on cf.Id_clasfisc = p.Id_Clasfisc\n"
                    + "order by 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("importid"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());

                    if ("S".equals(rst.getString("pesavel"))) {
                        imp.setEan(rst.getString("cod_balanca"));
                    } else {
                        imp.setEan(rst.getString("codigobarras"));
                    }

                    if ("S".equals(rst.getString("pesavel"))) {

                        String pesavel = Utils.acertarTexto(rst.getString("pesavel"));
                        if (produtosBalanca.isEmpty()) {
                            if (pesavel != null && "S".equals(pesavel.trim())) {
                                imp.seteBalanca(true);
                            }
                        } else {
                            long codigoProduto;
                            if (imp.getEan() != null && !imp.getEan().trim().isEmpty() && imp.getEan().trim().length() <= 6) {

                                codigoProduto = Long.parseLong(imp.getEan());
                                if (codigoProduto <= Integer.MAX_VALUE) {
                                    produtoBalanca = produtosBalanca.get((int) codigoProduto);
                                } else {
                                    produtoBalanca = null;
                                }

                                if (produtoBalanca != null) {
                                    imp.seteBalanca(true);
                                    imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                                } else {
                                    imp.setValidade(0);
                                    imp.seteBalanca(false);
                                }
                            } else {
                                imp.seteBalanca(false);
                                imp.setValidade(0);
                            }

                        }
                    }

                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setTipoEmbalagem(Utils.acertarTexto(rst.getString("unidade")));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro(rst.getInt("ativo") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);

                    imp.setIcmsCst(rst.getInt("cst_saida"));
                    imp.setIcmsAliq(rst.getDouble("aliq_saida"));
                    imp.setIcmsReducao(rst.getDouble("red_saida"));

                    //imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));

                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	id_forne importId,\n"
                    + "	razaosocial razao,\n"
                    + "	nome fantasia,\n"
                    + "	cnpj cnpj_cpf,\n"
                    + "	ie ie_rg,\n"
                    + "	ativo,\n"
                    + "	endereco,\n"
                    + "	numend numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	cidade municipio,\n"
                    + "	uf,\n"
                    + "	cep,\n"
                    + "	CONCAT(ddd1,Fone1) tel_principal,\n"
                    + "	fax,\n"
                    + "	e_mail email ,\n"
                    + "	dt_cad datacadastro,\n"
                    + "	prazoEntrega,\n"
                    + "	obs1 observacao\n"
                    + "FROM\n"
                    + "	PADARIA.dbo.Fornecedor f\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("importid"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTel_principal(Utils.stringLong(rst.getString("tel_principal")));
                    imp.addTelefone("FAX", rst.getString("FAX"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "email",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }

                    if ((rst.getString("prazoentrega") != null) && (!rst.getString("prazoentrega").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao()
                                + " - Prazo entrega: " + rst.getInt("prazoentrega"));
                    }

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.CODFORNEC id_fornecedor,\n"
                    + "	pf.CODPROD id_produto,\n"
                    + "	pf.CODREF codigoexterno,\n"
                    + "	coalesce(pf.QTD_EMB, 1) qtdembalagem,\n"
                    + "	pf.DATAREF dataalteracao,\n"
                    + "	p.QTD_EMB qtd_cotacao\n"
                    + "from\n"
                    + "	PRODREF pf\n"
                    + "	join produtos p on\n"
                    + "		pf.CODPROD = p.CODPROD"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	   id_cli id,\n"
                    + "    cnpj,\n"
                    + "    Documento ie,\n"
                    + "    razaosocial razao,\n"
                    + "    nome fantasia,\n"
                    + "    ativo,\n"
                    + "    endereco,\n"
                    + "    numend numero,\n"
                    + "    complemento,\n"
                    + "    bairro,\n"
                    + "    cidade municipio,\n"
                    + "    uf,\n"
                    + "    cep,\n"
                    + "    dt_nasc dataNascimento,\n"
                    + "    dt_cad dataCadastro,\n"
                    + "    empresa,\n"
                    + "    profissao cargo,\n"
                    + "    limite_crd valorLimite,\n"
                    + "    nomePai,\n"
                    + "    nomeMae,\n"
                    + "    obs1 observacao,\n"
                    + "    obs2 observacao2, \n"
                    + "    CONCAT(ddd1,fone1 )telefone,\n"
                    + "    CONCAT(ddd2,fone2) celular,\n"
                    + "    e_mail email,\n"
                    + "    fax\n"
                    + "FROM\n"
                    + "	PADARIA.dbo.Cliente c\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setObservacao2(rst.getString("observacao2"));
                    imp.setTelefone(Utils.stringLong(rst.getString("telefone")));
                    imp.setCelular(Utils.stringLong(rst.getString("celular")));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CONCAT(Promiss,parte) id,\n"
                    + "	cr.dt_cad dataemissao,\n"
                    + "	Promiss numerocupom,\n"
                    + "	Valor,\n"
                    + "	cr.Id_cli idcliente,\n"
                    + "	cnpj cnpjcliente,\n"
                    + "	Dt_vcto datavencimento,\n"
                    + "	parte parcela,\n"
                    + "	vl_multa multa,\n"
                    + "	cr.obs1 observacao\n"
                    + "from\n"
                    + "	Promiss cr\n"
                    + "	join Cliente c on c.Id_cli = cr.Id_cli \n"
                    + "where\n"
                    + "	cr.Id_loja = " + getLojaOrigem() + " \n"
                    + "	and Dt_rcto is NULL and Status != 'cancelado'\n"
                    + "order by 1,2,3"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setCnpjCliente(rst.getString("cnpjcliente"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setMulta(rst.getDouble("multa"));
                    imp.setObservacao(rst.getString("observacao"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
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
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
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
                        String id = rst.getString("Id_loja") + rst.getString("data") 
                                  + rst.getString("Id_turno") + rst.getString("SN") + rst.getString("Referencia");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idclientepreferencial"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setValorDesconto(rst.getDouble("desconto"));

                        if (rst.getString("nomecliente") != null
                                && !rst.getString("nomecliente").trim().isEmpty()
                                && rst.getString("nomecliente").trim().length() > 45) {

                            next.setNomeCliente(rst.getString("nomecliente").substring(0, 45));
                        } else {
                            next.setNomeCliente(rst.getString("nomecliente"));
                        }
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
                    + " v.Id_loja,\n"
                    + " v.Data,\n"
                    + " v.Id_turno,\n"
                    + " v.SN,\n"
                    + " v.Referencia,"
                    + "	CASE when v.NoCupom is null then SUBSTRING(v.Referencia,3,10) else NoCupom end numerocupom,\n"
                    + " CONCAT(v.Id_loja, CONVERT(NVARCHAR, v.data, 23), v.Id_turno, v.SN, v.Referencia) id_venda,\n"
                    + "	1 as ecf,\n"
                    + "	v.id_cli idclientepreferencial,\n"
                    + " c.Nome nomecliente,\n"
                    + "	CAST(CONVERT(NVARCHAR, v.Dt_cad, 8) as time) horainicio,\n"
                    + "	CAST(CONVERT(NVARCHAR, v.Dt_cad, 8) as time) horatermino,\n"
                    + "	case when Status = 'cancelado' then 1 else 0 end cancelado,\n"
                    + "	subtotal subtotalimpressora,\n"
                    + "	c.CNPJ cpf,\n"
                    + "	desconto\n"
                    + "from\n"
                    + "	transacao v\n"
                    + "	left join Cliente c on c.Id_cli = v.Id_cli \n"
                    + "where\n"
                    + "	v.id_loja = " + idLojaCliente + " \n"
                    + "	and operacao = 'VEND'\n"
                    + "	and CONVERT(NVARCHAR, v.data, 23) BETWEEN '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "'\n"
                    + " and v.Referencia not in ('V-0000226200','V-0000336107') order by 1";
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
                        String id = rst.getString("Id_loja")
                                    + rst.getString("data")
                                    + rst.getString("Id_turno")
                                    + rst.getString("SN")
                                    + rst.getString("Referencia")
                                    + rst.getString("NumItem");
                        String idvenda = rst.getString("Id_loja")
                                    + rst.getString("data") 
                                    + rst.getString("Id_turno")
                                    + rst.getString("SN")
                                    + rst.getString("Referencia");
                        System.out.println(id);
                        next.setId(id);
                        next.setVenda(idvenda);
                        next.setProduto(rst.getString("produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + " CONCAT(v.Id_loja, v.data, v.Id_turno, v.SN, v.Referencia) id_venda,\n"
                    + "	CONCAT(i.Id_loja, i.Data, i.Id_turno, i.SN, i.Referencia, i.NumItem) id_item,\n"
                    + " i.Id_loja,\n"
                    + " i.data,\n"
                    + " i.Id_turno,\n"
                    + " i.SN,\n"
                    + " i.referencia,\n"
                    + " i.NumItem,\n"
                    + " 1 as ecf,\n"
                    + "	p.codigo produto,\n"
                    + "	p.descricao,\n"
                    + "	Qtde * -1 quantidade,\n"
                    + "	(Qtde * Vl_Unit)* -1 total,\n"
                    + "	case when i.Status = 'cancelado' then 1 else 0 end cancelado,\n"
                    + "	SUBSTRING(p.Cartela,0,14) codigobarras,\n"
                    + "	u.Unidade unidade\n"
                    + "from EstMovto i\n"
                    + "left join Transacao v on\n"
                    + "		v.Id_loja = i.Id_loja and\n"
                    + "		v.[Data] = i.[Data] and\n"
                    + "		v.Id_turno = i.Id_turno and\n"
                    + "		v.SN = i.SN and\n"
                    + "		v.Referencia = i.Referencia\n"
                    + "left join Produto p on p.Id_prd = i.Id_prd\n"
                    + "left join unidade u on u.Id_unidade = p.Id_Unidade\n"
                    + "where\n"
                    + "	i.id_loja = " + idLojaCliente + " \n"
                    + "	and v.operacao = 'VEND'\n"
                    + "	and CONVERT(NVARCHAR, v.data, 23) BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + " and v.Referencia not in ('V-0000226200','V-0000336107') order by 1,2";
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
