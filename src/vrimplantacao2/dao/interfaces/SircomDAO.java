package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
//import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class SircomDAO extends InterfaceDAO implements MapaTributoProvider {

    //private Date vendasDataInicio = null;
    //private Date vendasDataTermino = null;
    private static final Logger LOG = Logger.getLogger(SircomDAO.class.getName());

    @Override
    public String getSistema() {
        return "SIRCOM";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
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
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    cod_cf id,\n"
                    + "    descricao,\n"
                    + "    per_aliq aliquota,\n"
                    + "    sit_trib_nf cst,\n"
                    + "    per_red reducao\n"
                    + "from cat_fiscal"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "     cod_empr id,\n"
                    + "     razao_social razao\n"
                    + "from empresas"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return result;
    }

    /*@Override
     public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                      "select \n"
                    + "     codigo merc1,\n"
                    + "     descricao descmerc1\n"
                    + "from\n"
                    + "     cat_prod\n"
                    + "order by 1"
            )) {
                    while (rs.next()) {
                
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    
                    result.add(imp);
                }
            }
        }
            return result;
    }*/

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "     codigo id,\n"
                    + "     descricao\n"
                    + "from\n"
                    + "     cat_prod\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.cod_plu id,\n"
                    + "    c.cod_ean ean\n"
                    + "from\n"
                    + "    cod_ean_aux c\n"
                    + "join produtos p on c.cod_plu = p.cod_plu\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));

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
                      " select\n"
                    + "     p.cod_plu importid,\n"
                    + "     data_inc datacadastro,\n"
                    + "     p.cod_ean ean,\n"
                    + "     emb_cpra_und tipoembalagem,\n"
                    + "     granel ebalanca,\n"
                    + "     granel_val validade,\n"
                    + "     descricao descricaocompleta,\n"
                    + "     descr_reduzida descricaoreduzida,\n"
                    + "     descricao descricaogondola,\n"
                    + "     cod_cat_prod idfamiliaproduto,\n"
                    + "     e.estoque_max estoquemaximo,\n"
                    + "     e.estoque_min estoqueminimo,\n"
                    + "     e.estoque estoque,\n"
                    + "     p.mar_venda margem,\n"
                    + "     e.custo_final custosemimposto,\n"
                    + "     e.custo_final custocomimposto,\n"
                    + "     e.preco_venda precovenda,\n"
                    + "     case when inativo = 's' then 0 else 1 end situacaocadastro,\n"
                    + "     codigo_ncm ncm,\n"
                    + "     codigo_cest	cest\n"
                    + " from \n"
                    + "     produtos p\n"
                    + " left join \n"
                    + "     estoque e\n"
                    + "     on p.cod_plu = e.cod_plu\n"
                    + " where loja = " + getLojaOrigem()
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importid"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setIdFamiliaProduto(rs.getString("idfamiliaproduto"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    
                    
                    
                    
                    
                    //imp.setCodMercadologico1(rs.getString("merc1"));
                    //imp.setCodMercadologico2(rs.getString("merc2"));
                    //imp.setCodMercadologico3(rs.getString("merc3"));
                                       
                    
                    //imp.setPiscofinsCstCredito(rs.getString("pis_entrada"));
                    //imp.setPiscofinsCstDebito(rs.getString("pis_saida"));
                    //imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    // Icms debito
                    /*imp.setIcmsAliqSaida(rs.getDouble("icms_debito"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_red_debito"));

                    if (rs.getString("tipoaliquota") != null && !"".equals(rs.getString("tipoaliquota"))) {
                        switch (rs.getString("tipoaliquota").trim()) {
                            case "F":
                                imp.setIcmsCstSaida(60);
                                break;
                            case "I":
                                imp.setIcmsCstSaida(40);
                                break;
                            case "N":
                                imp.setIcmsCstSaida(41);
                                break;
                            case "T":
                                imp.setIcmsCstSaida(0);
                                break;
                            case "R":
                                imp.setIcmsCstSaida(20);
                                if (rs.getDouble("icms_red_debito") == 0) {
                                    imp.setIcmsCstSaida(0);
                                }
                                break;
                            default:
                                imp.setIcmsCstSaida(40);
                                break;
                        }
                    }
                    imp.setIcmsAliqSaidaForaEstado(imp.getIcmsAliqSaida());
                    imp.setIcmsCstSaidaForaEstado(imp.getIcmsCstSaida());
                    imp.setIcmsReducaoSaidaForaEstado(imp.getIcmsReducaoSaida());

                    //Icms Credito
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_credito"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_red_credito"));
                    imp.setIcmsCstEntrada(rs.getInt("cst_credito"));

                    imp.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntrada());
                    imp.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntrada());
                    imp.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntrada());
                    */
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
                    "select\n"
                    + "    cod_forn importId,\n"
                    + "    nome_razao razao,\n"
                    + "    fantasia,\n"
                    + "    cnpj_cpf,\n"
                    + "    ie_rg,\n"
                    + "    home_page,\n"
                    + "    bloqueado,\n"
                    + "    endereco,\n"
                    + "    end_numero numero,\n"
                    + "    bairro,\n"
                    + "    cod_munic_ibge ibge_municipio,\n"
                    + "    cidade municipio,\n"
                    + "    estado uf,\n"
                    + "    cep,\n"
                    + "    fone1 tel_principal,\n"
                    + "    vr_min_pedido valor_minimo_pedido,\n"
                    + "    data_inc datacadastro,\n"
                    + "    observ observacao,\n"
                    + "    prazo_entrega prazoEntrega,\n"
                    + "    contato_ger,\n"
                    + "    fone_gerencia,\n"
                    + "    nome_vendedor,\n"
                    + "    fone_vendedor,\n"
                    + "    email_nfe\n"
                    + "from fornecedores"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importId"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setAtivo(rs.getBoolean(1));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setValor_minimo_pedido(rs.getDouble("valor_minimo_pedido"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    if (rs.getString("observacao") != null && !"".equals(rs.getString("observacao"))) {
                        imp.setObservacao(rs.getString("observacao"));
                    }
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));

                    if (rs.getString("tel_principal") != null
                            && !"0".equals(rs.getString("tel_principal"))
                            && !"".equals(rs.getString("tel_principal"))) {
                        imp.setTel_principal(rs.getString("tel_principal"));
                    }
                    if (rs.getString("fone_gerencia") != null
                            && !"0".equals(rs.getString("fone_gerencia"))
                            && !"".equals(rs.getString("fone_gerencia"))) {
                        imp.addContato("1",
                                (rs.getString("contato_ger")),
                                (rs.getString("fone_gerencia")),
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }

                    if (rs.getString("home_page") != null
                            && !"".equals(rs.getString("home_page"))) {
                        imp.addContato("2",
                                (rs.getString("home_page")),
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if (rs.getString("email_nfe") != null
                            && !"".equals(rs.getString("email_nfe"))) {
                        imp.addContato("3",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rs.getString("email_nfe"));
                    }
                    if (rs.getString("nome_vendedor") != null
                            && !"".equals(rs.getString("nome_vendedor"))) {
                        imp.addContato("4",
                                (rs.getString("nome_vendedor")),
                                (rs.getString("fone_vendedor")),
                                null,
                                TipoContato.FINANCEIRO,
                                null);
                    }

                    /*if (imp.getCnpj_cpf() == null) {
                     imp.setCnpj_cpf(rs.getString("cpf"));
                     }*/
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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    f.cod_plu id_produto,\n"
                    + "    f.cod_forn id_fornecedor,\n"
                    + "    f.cod_prod_forn codexterno,\n"
                    + "    p.emb_cpra_dim qtdEmbalagem,\n"
                    + "    f.data_alt dataalteracao\n"
                    + "from cod_ref_for f\n"
                    + "    left join produtos p\n"
                    + "    on p.cod_plu = f.cod_plu\n"
                    + "order by 1,2"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdEmbalagem"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

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
                    "select\n"
                    + "    id,\n"
                    + "    numero,\n"
                    + "    ativo,\n"
                    + "    nome,\n"
                    + "    endereco,\n"
                    + "    complemento,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    uf,\n"
                    + "    cep,\n"
                    + "    tipo_pessoa,\n"
                    + "    cpf,\n"
                    + "    cnpj,\n"
                    + "    insc_estadual,\n"
                    + "    rg,\n"
                    + "    ddd,\n"
                    + "    telefone,\n"
                    + "    fax, \n"
                    + "    celular,\n"
                    + "    site,\n"
                    + "    data_cadastro,\n"
                    + "    data_aniversario,\n"
                    + "    limite_credito, \n"
                    + "    email,\n"
                    + "    s_codigo_municipio,\n"
                    + "    end_num,\n"
                    + "    comentarios\n"
                    + "from \n"
                    + "    clientes\n"
                    + "order by\n"
                    + "    id "
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("end_num"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCnpj(rs.getString("cnpj"));
                    if (imp.getCnpj() == null && "".equals(imp.getCnpj())) {
                        imp.setCnpj(rs.getString("cpf"));
                    }
                    imp.setInscricaoestadual(rs.getString("insc_estadual"));
                    if (imp.getInscricaoestadual() == null && "".equals(imp.getInscricaoestadual())) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    }
                    imp.setTelefone(rs.getString("ddd") + rs.getString("telefone"));
                    imp.setCelular(rs.getString("ddd") + rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setDataNascimento(rs.getDate("data_aniversario"));
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    imp.setEmail(rs.getString("email"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*public List<Estabelecimento> getLojaClienteSQLite() throws Exception {
     List<Estabelecimento> result = new ArrayList<>();
     ConexaoSQLite conSQLite = new ConexaoSQLite();
     try(Statement stm = conSQLite.get().createStatement()) {
     try(ResultSet rs = stm.executeQuery(
     "select\n" +
     "    1 id,\n" +
     "    'LOJA - SQL LITE' razaosocial"
     )) {
     while(rs.next()) {
     result.add(new Estabelecimento(rs.getString("id"), rs.getString("razaosocial")));
     }
     }
     }
     return result;
     }*/
    /*    public Date getVendasDataInicio() {
     return vendasDataInicio;
     }

     public void setVendasDataInicio(Date vendasDataInicio) {
     this.vendasDataInicio = vendasDataInicio;
     }

     public void setVendasDataTermino(Date vendasDataTermino) {
     this.vendasDataTermino = vendasDataTermino;
     }

     public Date getVendasDataTermino() {
     return vendasDataTermino;
     }
    
     @Override
     public Iterator<VendaIMP> getVendaIterator() throws Exception {
     return new VendaIterator(getLojaOrigem(), getVendasDataInicio(), getVendasDataTermino());
     }

     @Override
     public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
     return new VendaItemIterator(getLojaOrigem(), getVendasDataInicio(), getVendasDataTermino());
     }
    
     private static class VendaIterator implements Iterator<VendaIMP> {

     private Statement stm;
     private ResultSet rst;
     private VendaIMP next;

     public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) {
     try {
     ConexaoSQLite sqLite = new ConexaoSQLite();
     this.stm = sqLite.get().createStatement();
     this.rst = stm.executeQuery(
     "select	\n" +
     "	v.id,\n" +
     "	v.sCCF_CVC_CBP ccf,\n" +
     "	ecf.sECF ecf,\n" +
     "	v.sCOO coo,\n" +
     "	v.sCPF_CNPJ_ADQUIRENTE cnpj,\n" +
     "	v.sNOME_ADQUIRENTE razao,\n" +
     "	v.sDataINICIO_EMISSAO data,\n" +
     "	v.sHORA horaemissao,\n" +
     "	v.rSUB_TOTAL_DOCUMENTO subtotalimpressora\n" +
     "from	\n" +
     "	r04 v\n" +
     "join r01 ecf on v.id_r01 = ecf.id\n" +
     "where	\n" +
     "	substr(sDataINICIO_EMISSAO,7)||substr(sDataINICIO_EMISSAO,4,2)||substr(sDataINICIO_EMISSAO,1,2) between '" + 
     DATE_FORMAT.format(dataInicio) + "' and '" + DATE_FORMAT.format(dataTermino) + "'"
     );
     } catch (Exception ex) {
     LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
     throw new RuntimeException(ex);
     }
     }

     @Override
     public boolean hasNext() {
     processarNext();
     return next != null;
     }

     @Override
     public VendaIMP next() {
     processarNext();
     VendaIMP result = next;
     next = null;
     return result;
     }

     @Override
     public void remove() {
     throw new UnsupportedOperationException("Not supported.");
     }

     private void processarNext() {
     try {
     if (next == null) {
     if (rst.next()) {
     next = new VendaIMP();

     next.setId(rst.getString("id"));
     next.setNumeroCupom(rst.getInt("coo"));
     next.setEcf(Integer.valueOf(Utils.formataNumero(rst.getString("ecf"))));
     next.setData(new SimpleDateFormat("dd/MM/yy").parse(rst.getString("data")));
     //next.setIdClientePreferencial(rst.getString("id_cliente"));
     next.setHoraInicio(new SimpleDateFormat("hh:mm:ss").parse(rst.getString("horaemissao")));
     next.setHoraTermino(new SimpleDateFormat("hh:mm:ss").parse(rst.getString("horaemissao")));
     next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
     }
     }
     } catch (Exception ex) {
     LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
     throw new RuntimeException(ex);
     }
     }

     }
     public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyMMdd");

     private static class VendaItemIterator implements Iterator<VendaItemIMP> {

     private Statement stm;
     private ResultSet rst;
     private VendaItemIMP next;
     private String loja = "";

     public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) {
     try {
     loja = idLojaCliente;
     ConexaoSQLite sqLite = new ConexaoSQLite();
     stm = sqLite.get().createStatement();
     rst = stm.executeQuery(
     "select	\n" +
     "	i.id,\n" +
     "       v.id id_venda,\n" +
     "	i.scoo coo,\n" +
     "	i.sccf_cvc_cbp ccf,\n" +
     "	i.snum_item sequencia,\n" +
     "	i.scodigo_produto id_produto,\n" +
     "	i.sdescricao descricao,\n" +
     "	i.sunidade unidade,\n" +
     "	i.rquantidade qtd,\n" +
     "	i.rvalor_unitario valorvenda,\n" +
     "	i.rvalor_total_liquido subtotalimpressora,\n" +
     "	i.sst cst,\n" +
     "	i.rtaxa_aliquota icms,\n" +
     "	i.sindicador_cancelamento cancelado\n" +
     "from	\n" +
     "	r05 i\n" +
     "join r04 v on i.scoo = v.scoo\n" +
     "where	\n" +
     "	substr(sDataINICIO_EMISSAO,7)||substr(sDataINICIO_EMISSAO,4,2)||substr(sDataINICIO_EMISSAO,1,2) between '" + 
     DATE_FORMAT.format(dataInicio) + "' and '" + DATE_FORMAT.format(dataTermino) + "'"
     );

     } catch (Exception ex) {
     LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
     throw new RuntimeException(ex);
     }
     }

     @Override
     public boolean hasNext() {
     processarNext();
     return next != null;
     }

     @Override
     public VendaItemIMP next() {
     processarNext();
     VendaItemIMP result = next;
     next = null;
     return result;
     }

     @Override
     public void remove() {
     throw new UnsupportedOperationException("Not supported.");
     }

     private void processarNext() {
     try {
     if (next == null) {
     if (rst.next()) {
     ProdutoAnteriorDAO antDAO = new ProdutoAnteriorDAO();
     next = new VendaItemIMP();

     next.setId(rst.getString("id"));
     next.setVenda(rst.getString("id_venda"));
                        
     int idProduto = antDAO.getCodigoAtualEANant("SOPHYX", loja, rst.getString("id_produto"));
                        
     next.setProduto(String.valueOf(idProduto));
     next.setDescricaoReduzida(rst.getString("descricao"));
     next.setQuantidade(rst.getDouble("qtd"));
     next.setTotalBruto(rst.getDouble("subtotalimpressora"));
     next.setCancelado("S".equals(rst.getString("cancelado").trim()));
     next.setCodigoBarras(rst.getString("id_produto"));
     next.setUnidadeMedida(rst.getString("unidade"));
                        
     if(rst.getString("cst") != null && !"".equals(rst.getString("cst"))) {
     switch(rst.getString("cst").trim()) {
     case "T": next.setIcmsAliq(rst.getDouble("icms"));
     next.setIcmsCst(0);
     next.setIcmsReduzido(0);
     break;
     case "F": next.setIcmsAliq(rst.getDouble("icms"));
     next.setIcmsCst(60);
     next.setIcmsReduzido(0);
     break;
     default: 
     next.setIcmsAliq(rst.getDouble("icms"));
     next.setIcmsCst(40);
     next.setIcmsReduzido(0);
     break;
     }
     }
     }
     }
     } catch (Exception ex) {
     LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
     throw new RuntimeException(ex);
     }
     }
     }*/
}
