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
                    "select\n" +
                    "    cod_cf id,\n" +
                    "    descricao,\n" +
                    "    per_aliq aliquota,\n" +
                    "    substring(sit_trib_nf from 2 for 3) cst,\n" +
                    "    per_red reducao\n" +
                    "from\n" +
                    "    cat_fiscal\n" +
                    "where\n" +
                    "    cod_cf in (select cast(cod_cat_fiscal as integer) from produtos)\n" +
                    "order by\n" +
                    "    2"
            )) {
                while (rs.next()) {
                    // D - Aliquota de Débito
                    result.add(new MapaTributoIMP("D" + rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
            
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    cod_cf id,\n" +
                    "    descricao,\n" +
                    "    per_aliq aliquota,\n" +
                    "    substring(sit_trib_nf from 2 for 3) cst,\n" +
                    "    per_red reducao\n" +
                    "from\n" +
                    "    cat_fiscal\n" +
                    "where\n" +
                    "    cod_cf in (select cast(cod_cat_fis_entrada as integer) from produtos)\n" +
                    "order by\n" +
                    "    2")) {
                while (rs.next()) {
                    // C - Aliquota de Crédito
                    result.add(new MapaTributoIMP("C" + rs.getString("id"),
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
                      "select\n" +
                    "     p.cod_plu importid,\n" +
                    "     data_inc datacadastro,\n" +
                    "     p.cod_ean ean,\n" +
                    "     emb_cpra_und tipoembalagem,\n" +
                    "     p.emb_cpra_tipo tipoembalagemcompra,\n" +
                    "     p.emb_cpra_dim qtdembalagemcompra,\n" +
                    "     granel ebalanca,\n" +
                    "     granel_val validade,\n" +
                    "     cast(p.cod_cat_fiscal as integer) idicms_debito,\n" +
                    "     p.cod_cat_fis_entrada idicms_credito,\n" +
                    "     p.icms_s,\n" +
                    "     p.icms_e,\n" +
                    "     p.per_iva,\n" +
                    "     descricao descricaocompleta,\n" +
                    "     descr_reduzida descricaoreduzida,\n" +
                    "     descricao descricaogondola,\n" +
                    "     cod_cat_prod idfamiliaproduto,\n" +
                    "     e.estoque_max estoquemaximo,\n" +
                    "     e.estoque_min estoqueminimo,\n" +
                    "     e.estoque estoque,\n" +
                    "     p.mar_venda margem,\n" +
                    "     e.custo_final custosemimposto,\n" +
                    "     e.custo_final custocomimposto,\n" +
                    "     e.preco_venda precovenda,\n" +
                    "     case when upper(inativo) = 'S' then 0 else 1 end situacaocadastro,\n" +
                    "     codigo_ncm ncm,\n" +
                    "     codigo_cest cest,\n" +
                    "     p.piscofins,\n" +
                    "     p.piscofins_regime,\n" +
                    "     p.piscofins_mod\n" +
                    " from \n" +
                    "     produtos p\n" +
                    " left join \n" +
                    "     estoque e\n" +
                    "     on p.cod_plu = e.cod_plu\n" +
                    " where loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importid"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.setTipoEmbalagemCotacao(rs.getString("tipoembalagemcompra"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcompra"));
                    imp.seteBalanca("S".equals(rs.getString("ebalanca")));
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

                    imp.setPiscofinsCstCredito(rs.getString("piscofins_mod"));
                    //imp.setPiscofinsCstDebito(rs.getString("pis_saida"));
                    //imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    // Icms Debito
                    imp.setIcmsDebitoId("D" + rs.getString("idicms_debito"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    
                    // Icms Credito
                    imp.setIcmsCreditoId("C" + rs.getString("idicms_credito"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());

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
                    imp.setAtivo(true);
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
}
