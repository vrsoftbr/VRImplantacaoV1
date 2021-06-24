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

    /*@Override
     public List<ProdutoIMP> getEANs() throws Exception {
     List<ProdutoIMP> result = new ArrayList<>();
     try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
     try (ResultSet rs = stm.executeQuery(
     "select\n"
     + "	codprod id_produto,\n"
     + "	barra ean,\n"
     + "	1 qtdembalagem,\n"
     + "	preco_unit preco\n"
     + "from\n"
     + "	produtos prod\n"
     + "union\n"
     + "select\n"
     + "	codprod id_produto, \n"
     + "	rtrim(barra_emb) ean, \n"
     + "	qtd qtdembalagem,\n"
     + "	preco_unit preco\n"
     + "from \n"
     + "	embalagens \n"
     + "where\n"
     + "	barra_emb is not null \n"
     + "union\n"
     + "select\n"
     + "	codprod id_produto,\n"
     + "	barra ean,\n"
     + "	1 qtdembalagem,\n"
     + "	0 preco\n"
     + "from \n"
     + "	alternativo\n"
     + "where\n"
     + "	barra is not null"
     )) {
     while (rs.next()) {
     ProdutoIMP imp = new ProdutoIMP();

     imp.setImportLoja(getLojaOrigem());
     imp.setImportSistema(getSistema());
     imp.setImportId(rs.getString("id_produto"));
     imp.setEan(rs.getString("ean"));
     imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

     result.add(imp);
     }
     }
     }
     return result;
     }*/
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
                    // imp.addEmail("email", rst.getString("email"), TipoContato.NFE);
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

    /*@Override
     public List<MapaTributoIMP> getTributacao() throws Exception {
     List<MapaTributoIMP> result = new ArrayList();

     try (Statement stmt = ConexaoSqlServer.getConexao().createStatement()) {
     try (ResultSet rs = stmt.executeQuery(
     "select \n"
     + "	distinct\n"
     + "	ltrim(rtrim(replace(icms.codaliq,'\\','\\\\\\'))) id,\n"
     + "	coalesce(fcp.VALORTRIB, 0) fcp,\n"
     + "	icms.descricao\n"
     + "from \n"
     + "	aliquota_icms icms\n"
     + "	left join\n"
     + "		PRODUTOS p on icms.CODALIQ = p.CODALIQ\n"
     + "	left join PROD_TRIBFCP fcp on p.codprod = fcp.CODPROD\n"
     + "where\n"
     + "	icms.descricao is not null\n"
     + "order by\n"
     + "	id, fcp"
     )) {
     while (rs.next()) {
     result.add(new MapaTributoIMP(
     formatTributacaoId(rs.getString("id"), rs.getDouble("fcp")),
     String.format("%s + FCP %.2f %%", rs.getString("descricao"), rs.getDouble("fcp"))
     ));
     }
     }
     }
     return result;
     }*/
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
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
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
                        //next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        //next.setModeloImpressora(rst.getString("modelo"));

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
                    + "	NoCupom as numerocupom,\n"
                    + "	1 as ecf,\n"
                    + "	v.Dt_cad as data,\n"
                    + "	v.id_cli idclientepreferencial,\n"
                    + " c.Nome nomecliente,\n"
                    + "	CAST(CONVERT(NVARCHAR, v.Dt_cad, 8) as time) horainicio,\n"
                    + "	CAST(CONVERT(NVARCHAR, v.Dt_cad, 8) as time) horatermino,\n"
                    + "	case when Status = 'cancelado' then 1 else 0 end cancelado,\n"
                    + "	subtotal subtotalimpressora,\n"
                    + "	c.CNPJ cpf,\n"
                    + "	desconto,\n"
                    + "	SN numeroserie\n"
                    + "from\n"
                    + "	transacao v\n"
                    + "	left join Cliente c on c.Id_cli = v.Id_cli \n"
                    + "where\n"
                    + "	v.id_loja = " + idLojaCliente +  " \n"
                    + "	and operacao = 'VEND'\n"
                    + "	and CONVERT(NVARCHAR, v.Dt_cad, 23) BETWEEN '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "\n"
                    + " order by 1";
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
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");

                        next.setId(rst.getString("id"));
                        next.setVenda(id);
                        next.setProduto(rst.getString("produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));

                        String trib = rst.getString("codaliq_venda");
                        if (trib == null || "".equals(trib)) {
                            trib = rst.getString("codaliq_produto");
                        }
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
                    + "    cx.id,\n"
                    + "    cx.coo as numerocupom,\n"
                    + "    cx.codcaixa as ecf,\n"
                    + "    cx.data as data,\n"
                    + "    cx.codprod as produto,\n"
                    + "    pr.DESC_PDV as descricao,    \n"
                    + "    isnull(cx.qtd, 0) as quantidade,\n"
                    + "    isnull(cx.totitem, 0) as total,\n"
                    + "    case when cx.cancelado = 'N' then 0 else 1 end as cancelado,\n"
                    + "    isnull(cx.descitem, 0) as desconto,\n"
                    + "    isnull(cx.acrescitem, 0) as acrescimo,\n"
                    + "    case\n"
                    + "     when LEN(cx.barra) > 14 \n"
                    + "     then SUBSTRING(cx.BARRA, 4, LEN(cx.barra))\n"
                    + "    else cx.BARRA end as codigobarras,\n"
                    + "    pr.unidade,\n"
                    + "    cx.codaliq codaliq_venda,\n"
                    + "    pr.codaliq codaliq_produto,\n"
                    + "    ic.DESCRICAO trib_desc\n"
                    + "from\n"
                    + "    caixageral as cx\n"
                    + "    join PRODUTOS pr on cx.codprod = pr.codprod\n"
                    + "    left join creceita c on pr.codcreceita = c.codcreceita\n"
                    + "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n"
                    + "    left join ALIQUOTA_ICMS ic on pr.codaliq = ic.codaliq\n"
                    + "where\n"
                    + "    cx.tipolancto = '' and\n"
                    + "    (cx.data between convert(date, '" + VendaIterator.FORMAT.format(dataInicio) + "', 23) and convert(date, '" + VendaIterator.FORMAT.format(dataTermino) + "', 23)) and\n"
                    + "    cx.codloja = " + idLojaCliente + " and\n"
                    + "    cx.atualizado = 'S' and\n"
                    + "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')";
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
