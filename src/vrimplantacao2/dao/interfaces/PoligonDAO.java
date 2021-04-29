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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
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
public class PoligonDAO extends InterfaceDAO /*implements MapaTributoProvider */{

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
                    + "	Id_prd importid,\n"
                    + "	p.Descricao descricaocompleta,\n"
                    + "	ref_prd ean,\n"
                    + "	Id_grupo mercadologico1,\n"
                    + "	Id_grupo mercadologico2,\n"
                    + "	Id_grupo mercadologico3,\n"
                    + "	u.Unidade unidade,\n"
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
                    + "	join unidade u on u.Id_unidade = p.Id_Unidade \n"
                    + "	join SitTribut i on i.Id_Sittrib = p.Id_Sittrib \n"
                    + "	join ClasFiscal cf on cf.Id_clasfisc = p.Id_Clasfisc\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("importid"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setEan(rst.getString("ean"));
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
                    imp.setSituacaoCadastro(rst.getInt("Ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    
                    imp.setIcmsCst(rst.getInt("cst_saida"));
                    imp.setIcmsAliq(rst.getDouble("aliq_saida"));
                    imp.setIcmsReducao(rst.getDouble("red_saida"));
                    
                    //imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));
                    
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    
                    //imp.seteBalanca("S".equals(rst.getString("bala")));
                   
                    //imp.setPiscofinsCstDebito(rst.getInt("tppis"));
                    //imp.setPiscofinsCstCredito(rst.getInt("tppise"));
                    //imp.setPiscofinsNaturezaReceita(rst.getInt("natrec"));

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
                    + "	and Dt_rcto is NULL\n"
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
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));

                        if (rst.getString("nomecliente") != null
                                && !rst.getString("nomecliente").trim().isEmpty()
                                && rst.getString("nomecliente").trim().length() > 45) {

                            next.setNomeCliente(rst.getString("nomecliente").substring(0, 45));
                        } else {
                            next.setNomeCliente(rst.getString("nomecliente"));
                        }

                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
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
                    + "    cx.coo as numerocupom,\n"
                    + "    cx.codcaixa as ecf,\n"
                    + "    cx.data as data,\n"
                    + "    coalesce(cx.cliente, '') as idclientepreferencial,\n"
                    + "    min(cx.hora) as horainicio,\n"
                    + "    max(cx.hora) as horatermino,\n"
                    + "    min(case when cx.cancelado = 'N' then 0 else 1 end) as cancelado,\n"
                    + "    sum(cx.totitem) as subtotalimpressora,\n"
                    + "    cl.cnpj_cpf cpf,\n"
                    + "    sum(isnull(cx.descitem,0)) desconto,\n"
                    + "    sum(isnull(cx.acrescitem, 0)) acrescimo,\n"
                    + "    pdv.NUM_SERIE numeroserie,\n"
                    + "    pdv.IMP_MODELO modelo,\n"
                    + "    pdv.IMP_MARCA marca,\n"
                    + "    cl.razao nomecliente,\n"
                    + "    cl.endereco,\n"
                    + "    cl.numero,\n"
                    + "    cl.complemento,\n"
                    + "    cl.bairro,\n"
                    + "    cl.cidade,\n"
                    + "    cl.estado,\n"
                    + "    cl.cep\n"
                    + "from\n"
                    + "    caixageral as cx\n"
                    + "    join PRODUTOS pr on cx.codprod = pr.codprod\n"
                    + "    left join creceita c on pr.codcreceita = c.codcreceita\n"
                    + "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n"
                    + "    left join parampdv pdv on cx.codloja = pdv.CODLOJA and cx.codcaixa = pdv.CODCAIXA\n"
                    + "where\n"
                    + "    cx.tipolancto = '' and\n"
                    + "    (cx.data between convert(date, '" + FORMAT.format(dataInicio) + "', 23) and convert(date, '" + FORMAT.format(dataTermino) + "', 23)) and\n"
                    + "    cx.codloja = " + idLojaCliente + " and\n"
                    + "    cx.atualizado = 'S' and\n"
                    + "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')\n"
                    + "group by\n"
                    + "	   cx.coo,\n"
                    + "    cx.codcaixa,\n"
                    + "    cx.data,\n"
                    + "    coalesce(cx.cliente, ''),\n"
                    + "	   cl.cnpj_cpf,\n"
                    + "    pdv.NUM_SERIE,\n"
                    + "    pdv.IMP_MODELO,\n"
                    + "    pdv.IMP_MARCA,\n"
                    + "    cl.razao,\n"
                    + "    cl.endereco,\n"
                    + "    cl.numero,\n"
                    + "    cl.complemento,\n"
                    + "    cl.bairro,\n"
                    + "    cl.cidade,\n"
                    + "    cl.estado,\n"
                    + "    cl.cep\n"
                    + "order by\n"
                    + "    data, numerocupom";
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

                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             TA	7.00	ALIQUOTA 07%
             TB	12.00	ALIQUOTA 12%
             TC	18.00	ALIQUOTA 18%
             TD	25.00	ALIQUOTA 25%
             TE	11.00	ALIQUOTA 11%
             I	0.00	ISENTO
             F	0.00	SUBST TRIBUTARIA
             N	0.00	NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "TA":
                    cst = 0;
                    aliq = 7;
                    break;
                case "TB":
                    cst = 0;
                    aliq = 12;
                    break;
                case "TC":
                    cst = 0;
                    aliq = 18;
                    break;
                case "TD":
                    cst = 0;
                    aliq = 25;
                    break;
                case "TE":
                    cst = 0;
                    aliq = 11;
                    break;
                case "TF":
                    cst = 0;
                    aliq = 11;
                    break;
                case "TG":
                    cst = 0;
                    aliq = 4.5;
                    break;
                case "TH":
                    cst = 0;
                    aliq = 8;
                    break;
                case "TI":
                    cst = 0;
                    aliq = 4;
                    break;
                case "TJ":
                    cst = 0;
                    aliq = 9.14;
                    break;
                case "TL":
                    cst = 0;
                    aliq = 13.3;
                    break;
                case "TM":
                    cst = 0;
                    aliq = 4.14;
                    break;
                case "TN":
                    cst = 0;
                    aliq = 4.7;
                    break;
                case "TO":
                    cst = 0;
                    aliq = 11.2;
                    break;
                case "TP":
                    cst = 0;
                    aliq = 8.40;
                    break;
                case "TQ":
                    cst = 0;
                    aliq = 8.83;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
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
