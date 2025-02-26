package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class MercaLiteDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(MercaLiteDAO.class.getName());
    private String idLojaComplemento = "";

    public String getIdLojaComplemento() {
        return this.idLojaComplemento;
    }

    public void setIdLojaComplemento(String idLojaComplemento) {
        this.idLojaComplemento = idLojaComplemento;
    }

    @Override
    public String getSistema() {
        if (!getIdLojaComplemento().trim().isEmpty()) {
            return "MercaLite - " + getIdLojaComplemento();
        } else {
            return "MercaLite";
        }
    }

    private String Encoding = "WIN1252";

    public void setEncoding(String Encoding) {
        this.Encoding = Encoding == null ? "WIN1252" : Encoding;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.CODIGO_BENEFICIO
                }
        ));
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	 id,\n"
                    + "	 n_fant descricao\n"
                    + "FROM\n"
                    + "	 empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "	 gru m1,\n"
                    + "	 m1.descr m1desc,\n"
                    + "	 sub_g m2,\n"
                    + "	 m2.descr m2desc,\n"
                    + "	 setr m3,\n"
                    + "	 m3.descr m3desc\n"
                    + "FROM\n"
                    + "	 CADPROD p\n"
                    + "LEFT JOIN TAB_GR m1 ON p.gru = m1.cod\n"
                    + "LEFT JOIN TAB_SG m2 ON p.sub_g = m2.cod_sg AND m2.cod_gr = m1.cod\n"
                    + "LEFT JOIN TAB_ST m3 ON p.setr = m3.cod_st AND m3.cod_sg = m2.cod_sg AND m3.cod_gr = m1.cod\n"
                    + "  ORDER BY 1,3,5")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("m1"));
                    imp.setMerc1Descricao(rs.getString("m1desc"));
                    imp.setMerc2ID(rs.getString("m2"));
                    imp.setMerc2Descricao(rs.getString("m2desc"));
                    imp.setMerc3ID(rs.getString("m3"));
                    imp.setMerc3Descricao(rs.getString("m3desc"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	cod importid,\n"
                    + "	descr descricaocompleta,\n"
                    + "	und tipoembalagem,\n"
                    + " ncm,\n"
                    + "	custo precocusto,\n"
                    + "	pr_unit precovenda,\n"
                    + "	saldo estoque,\n"
                    + "	gru m1,\n"
                    + "	sub_g m2,\n"
                    + "	setr m3,\n"
                    + "	CASE trib\n"
                    + "	  WHEN 'T' THEN '00'\n"
                    + "	  WHEN 'I' THEN '40'\n"
                    + "	  WHEN 'N' THEN '41'\n"
                    + "	  WHEN 'F' THEN '60'\n"
                    + "	END icms_cst,\n"
                    + "	taxa icms_aliq\n"
                    + "FROM CADPROD p\n"
                    + "ORDER BY descr"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importid"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCustoSemImposto(rs.getDouble("precocusto"));
                    imp.setCustoComImposto(imp.getCustoSemImposto());
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setEstoque(rs.getDouble("estoque"));

                    imp.setCodMercadologico1(rs.getString("m1"));
                    imp.setCodMercadologico2(rs.getString("m2"));
                    imp.setCodMercadologico3(rs.getString("m3"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));
                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                    } else {
                        if (imp.getImportId() != null
                                && !imp.getImportId().trim().trim().isEmpty()
                                && imp.getImportId().trim().length() == 7
                                && imp.getImportId().startsWith("20")) {

                            String ean;
                            ean = imp.getImportId().trim().substring(1, imp.getImportId().trim().length() - 1);
                            imp.seteBalanca(true);
                            imp.setEan(ean);
                        } else {
                            imp.seteBalanca(false);
                            imp.setEan(imp.getImportId());
                        }
                    }

                    String idIcms = getAliquotaKey(
                            rs.getString("icms_cst"),
                            rs.getDouble("icms_aliq"),
                            0
                    );

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);

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
                    "SELECT COD, UND FROM CADPROD c "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("COD"));
                    imp.setEan(imp.getImportId());
                    imp.setTipoEmbalagem(rst.getString("UND"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	codprod idproduto,\n"
                    + "	per1 datainicio,\n"
                    + "	per2 datafim,\n"
                    + "	pr_unit preconormal,\n"
                    + "	pr_per precooferta\n"
                    + "FROM\n"
                    + "	ATAKREJO\n"
                    + "WHERE \n"
                    + "	pr_per > 0\n"
                    + "	AND pr_unit > 0\n"
                    + "	AND CAST(CASE WHEN per2 = '  .  .       :  ' THEN CURRENT_date \n"
                    + "       WHEN per2 = '' THEN current_date ELSE per2 end AS timestamp) >= current_date")) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(Utils.convertStringToDate("dd.MM.yyyy HH:mm", rs.getString("datainicio")));
                    imp.setDataFim(Utils.convertStringToDate("dd.MM.yyyy HH:mm", rs.getString("datafim")));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	cod id,\n"
                    + "	nome razao,\n"
                    + "	nome fantasia,\n"
                    + "	cgc_cpf cnpj,\n"
                    + "	insc inscricaoestadual,\n"
                    + "	cid municipio,\n"
                    + "	c_contabil municipioibge,\n"
                    + "	cod_mun uf,\n"
                    + "	fone telefone,\n"
                    + "	contato celular,\n"
                    + "	obs observacao,\n"
                    + "	email\n"
                    + "FROM\n"
                    + "	CADFORNEC f"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setCnpj_cpf(Utils.formataNumero(rs.getString("cnpj")));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(Utils.stringToInt(rs.getString("municipioibge")));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setObservacao(rs.getString("observacao"));
                    if (rs.getString("celular") != null && !"".equals(rs.getString("celular"))) {
                        imp.addContato("1", "CELULAR", null, rs.getString("celular"), TipoContato.NFE, null);
                    }
                    if (rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("2", "EMAIL", null, null, TipoContato.NFE, rs.getString("email"));
                    }

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	cod id,\n"
                    + "	cpf cnpj,\n"
                    + "	rg inscricaoestadual,\n"
                    + "	nome razao,\n"
                    + "	nome fantasia,\n"
                    + "	ender endereco,\n"
                    + "	dt_nasc datanascimento,\n"
                    + "	lim_cred valorlimite,\n"
                    + "	fone telefone\n"
                    + "FROM\n"
                    + "	CADCLIE c\n"
                    + "ORDER BY COD"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(Utils.formataNumero(rs.getString("cnpj")));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setEndereco(rs.getString("endereco"));
                    //imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setTelefone(rs.getString("telefone"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	 num_pr id,\n"
                    + "	 dt_emis dataemissao,\n"
                    + "	 num_pr numerocupom,\n"
                    + "	 vlr_deb valor,\n"
                    + "	 venct vencimento,\n"
                    + "	 hist observacao,\n"
                    + "	 cod_clie idcliente,\n"
                    + "	 cpf cnpjcliente\n"
                    + "FROM\n"
                    + "	 CADPROMIS cr\n"
                    + "	 LEFT JOIN CADCLIE c ON cr.cod_clie = c.cod\n"
                    + "WHERE\n"
                    + "	 dt_movt IS null"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(Utils.formataNumero(rs.getString("cnpjcliente")));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws SQLException {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "CASE trib \n"
                    + "	WHEN 'T' THEN '00'\n"
                    + "	WHEN 'I' THEN '40'\n"
                    + "	WHEN 'N' THEN '41'\n"
                    + "	WHEN 'F' THEN '60'\n"
                    + "END cst, \n"
                    + "TAXA AS icms, \n"
                    + "0 AS reducao FROM CADPROD"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cst"),
                            rst.getDouble("icms"),
                            0
                    );
                    result.add(
                            new MapaTributoIMP(
                                    id,
                                    id,
                                    rst.getInt("cst"),
                                    rst.getDouble("icms"),
                                    0
                            )
                    );
                }
            }
        }
        return result;
    }

    /*
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

     public final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
     public final static SimpleDateFormat TIMESTAMP = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

     private Statement stm = ConexaoFirebird.getConexao().createStatement();
     private ResultSet rst;
     private String sql;
     private VendaIMP next;
     private Set<String> uk = new HashSet<>();

     private void obterNext() {
     try {

     if (next == null) {
     if (rst.next()) {
     next = new VendaIMP();
     String id = rst.getString("id");
     if (!uk.add(id)) {
     LOG.warning("Venda " + id + " já existe na listagem");
     }
     next.setId(id);
     next.setNumeroCupom(Utils.stringToInt(rst.getString("cupom")));
     next.setEcf(Utils.stringToInt(rst.getString("ecf")));
     next.setData(rst.getDate("data"));
     next.setIdClientePreferencial(rst.getString("id_cliente"));
     String horaInicio = FORMAT.format(rst.getDate("data")) + " " + rst.getString("hora");
     String horaTermino = FORMAT.format(rst.getDate("data")) + " " + rst.getString("hora");
     next.setHoraInicio(TIMESTAMP.parse(horaInicio));
     next.setHoraTermino(TIMESTAMP.parse(horaTermino));
     next.setCancelado(!"V".equals(rst.getString("situacao")));
     next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
     next.setCpf(rst.getString("cnpj"));
     next.setValorDesconto(rst.getDouble("desconto"));
     next.setValorAcrescimo(rst.getDouble("acrescimo"));
     next.setNumeroSerie(rst.getString("numeroserie"));
     next.setModeloImpressora(rst.getString("modelo"));
     //next.setNomeCliente(rst.getString("nomecliente"));
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
     + "    (c.id_ecf || '' || c.id_data || '' || c.id_cupom || '' || c.id_final) as id,\n"
     + "    c.id_data data,\n"
     + "    c.fin_hora hora,\n"
     + "    c.fin_pdv ecf,\n"
     + "    e.num_serie serie,\n"
     + "    e.num_mfd mfd,\n"
     + "    e.fab_serie numeroserie,\n"
     + "    e.fab_modelo modelo,\n"
     + "    e.fab_marca marca,\n"
     + "    c.id_cupom cupom,\n"
     + "    c.fin_hora hora,\n"
     + "    c.fin_obs obs,\n"
     + "    c.fin_valor subtotalimpressora,\n"
     + "    c.fin_desconto desconto,\n"
     + "    c.fin_acresc acrescimo,\n"
     + "    c.fin_situacao situacao,\n"
     + "    c.fin_cpf_cnpj cnpj,\n"
     + "    c.fin_clie id_cliente\n"
     + "from\n"
     + "    pdv_mapaf c\n"
     + "join pdv_ecfs e on c.id_ecf = e.id_ecf\n"
     + "where\n"
     + "    c.id_data between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' and\n"
     + "    c.id_final = 995 and\n"
     + "    c.fin_loja = " + idLojaCliente + "\n"
     + "order by\n"
     + "    c.id_data, c.id_ecf";
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

     next.setId(rst.getString("id"));
     next.setVenda(rst.getString("id_venda"));
     next.setProduto(rst.getString("id_produto"));
     next.setDescricaoReduzida(rst.getString("descricao"));
     next.setQuantidade(rst.getDouble("quantidade"));
     next.setTotalBruto(rst.getDouble("total"));
     next.setValorDesconto(rst.getDouble("desconto"));
     next.setValorAcrescimo(rst.getDouble("acrescimo"));
     next.setCancelado(rst.getBoolean("cancelado"));
     next.setCodigoBarras(rst.getString("ean"));
     next.setUnidadeMedida(rst.getString("embalagem"));

     if (rst.getString("icm") != null && !"".equals(rst.getString("icm"))) {
     switch (rst.getString("icm")) {
     case "FF":
     next.setIcmsAliq(0);
     next.setIcmsCst(60);
     next.setIcmsReduzido(0);
     break;

     case "II":
     next.setIcmsAliq(0);
     next.setIcmsCst(40);
     next.setIcmsReduzido(0);
     break;

     case "0700":
     next.setIcmsAliq(7);
     next.setIcmsCst(0);
     next.setIcmsReduzido(0);
     break;

     case "1700":
     next.setIcmsAliq(17);
     next.setIcmsCst(0);
     next.setIcmsReduzido(0);
     break;

     case "1200":
     next.setIcmsAliq(12);
     next.setIcmsCst(0);
     next.setIcmsReduzido(0);
     break;

     case "2500":
     next.setIcmsAliq(25);
     next.setIcmsCst(0);
     next.setIcmsReduzido(0);
     break;

     case "NN":
     next.setIcmsAliq(0);
     next.setIcmsCst(41);
     next.setIcmsReduzido(0);
     break;

     case "0720":
     next.setIcmsAliq(7.20);
     next.setIcmsCst(0);
     next.setIcmsReduzido(0);
     break;

     case "1800":
     next.setIcmsAliq(18);
     next.setIcmsCst(0);
     next.setIcmsReduzido(0);
     break;

     case "2700":
     next.setIcmsAliq(27);
     next.setIcmsCst(0);
     next.setIcmsReduzido(0);
     break;

     default:
     next.setIcmsAliq(0);
     next.setIcmsCst(40);
     next.setIcmsReduzido(0);
     break;
     }
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
     + "    (b.id_ecf || '' ||\n"
     + "    b.id_data || '' ||\n"
     + "    b.id_cupom || '' ||\n"
     + "    b.id_final) as id_venda,\n"
     + "    (b.id_ecf || '' || \n"
     + "    b.id_data || '' || \n"
     + "    b.id_cupom || '' || \n"
     + "    icm || '' ||\n"
     + "    id_seq || '' ||\n"
     + "    c.total || '' ||\n"
     + "    id_seq ) as id,\n"
     + "    c.id_seq sequencia,\n"
     + "    c.cod_prod id_produto,\n"
     + "    c.cod_prod ean,\n"
     + "    p.nome_produto descricao,\n"
     + "    p.unm_emb embalagem,\n"
     + "    c.qtd quantidade,\n"
     + "    c.custo,\n"
     + "    c.preco,\n"
     + "    c.desconto,\n"
     + "    c.acresc acrescimo,\n"
     + "    c.total,\n"
     + "    c.icm,\n"
     + "    c.cst,\n"
     + "    c.situacao cancelado,\n"
     + "    i.tabicm_aliq aliquota,\n"
     + "    i.tabicm_pbc reducao\n"
     + "from\n"
     + "    pdv_ecfs a,\n"
     + "    pdv_mapaf b,\n"
     + "    pdv_mapai c,\n"
     + "    est_produtos p,\n"
     + "    tab_icm i\n"
     + "where\n"
     + "    a.id_ecf = b.id_ecf and\n"
     + "    a.id_ecf = c.id_ecf and\n"
     + "    b.id_data = c.id_data and\n"
     + "    b.id_cupom = c.id_cupom and\n"
     + "    c.cod_prod = p.id_produto and\n"
     + "    c.icm = i.tabicm_ecf and\n"
     + "    b.id_final = 995 and\n"
     + "    b.fin_loja = " + idLojaCliente + " and\n"
     + "    b.id_data between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
     + "order by\n"
     + "    c.id_data,\n"
     + "    c.id_cupom,\n"
     + "    c.id_seq";
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
     }*/
}
