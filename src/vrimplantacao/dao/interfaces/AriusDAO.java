package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.ProgressBar;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

public class AriusDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(AriusDAO.class.getName());

    private List<PlanoConta> planosSelecionados;
    private boolean importarDeClientes = true;
    private boolean importarDeEmpresas = false;
    private boolean importarDeFornecedores = false;
    private boolean importarDeTransportadoras = false;
    private boolean importarDeAdminCartao = false;
    private int diasVenda = 360;
    private Date vendaDataInicio;
    private Date vendaDataTermino;
    private int tipoVenda = 1;

    public List<PlanoConta> getPlanosSelecionados() {
        return planosSelecionados;
    }

    public void setPlanosSelecionados(List<PlanoConta> planosSelecionados) {
        this.planosSelecionados = planosSelecionados;
    }

    public boolean isImportarDeClientes() {
        return importarDeClientes;
    }

    public void setImportarDeClientes(boolean importarDeClientes) {
        this.importarDeClientes = importarDeClientes;
    }

    public boolean isImportarDeEmpresas() {
        return importarDeEmpresas;
    }

    public void setImportarDeEmpresas(boolean importarDeEmpresas) {
        this.importarDeEmpresas = importarDeEmpresas;
    }

    public boolean isImportarDeFornecedores() {
        return importarDeFornecedores;
    }

    public void setImportarDeFornecedores(boolean importarDeFornecedores) {
        this.importarDeFornecedores = importarDeFornecedores;
    }

    public boolean isImportarDeTransportadoras() {
        return importarDeTransportadoras;
    }

    public void setImportarDeTransportadoras(boolean importarDeTransportadoras) {
        this.importarDeTransportadoras = importarDeTransportadoras;
    }

    public boolean isImportarDeAdminCartao() {
        return importarDeAdminCartao;
    }

    public void setImportarDeAdminCartao(boolean importarDeAdminCartao) {
        this.importarDeAdminCartao = importarDeAdminCartao;
    }

    @Override
    public String getSistema() {
        return "ARIUS";
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {

        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select depto, secao, grupo, subgrupo, descritivo from deptos where secao = 0 and grupo = 0 and subgrupo = 0 order by 1,2,3,4"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP(rst.getString("depto"), rst.getString("descritivo"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select depto, secao, grupo, subgrupo, descritivo from deptos where secao != 0 and grupo = 0 and subgrupo = 0 order by 1,2,3,4"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP pai = merc.get(rst.getString("depto"));
                    pai.addFilho(rst.getString("secao"), rst.getString("descritivo"));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select depto, secao, grupo, subgrupo, descritivo from deptos where secao != 0 and grupo != 0 and subgrupo = 0 order by 1,2,3,4"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP pai = merc.get(rst.getString("depto"));
                    pai = pai.getNiveis().get(rst.getString("secao"));
                    pai.addFilho(rst.getString("grupo"), rst.getString("descritivo"));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select depto, secao, grupo, subgrupo, descritivo from deptos where secao != 0 and grupo != 0 and subgrupo != 0 order by 1,2,3,4"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP pai = merc.get(rst.getString("depto"));
                    pai = pai.getNiveis().get(rst.getString("secao"));
                    pai = pai.getNiveis().get(rst.getString("grupo"));
                    pai.addFilho(rst.getString("subgrupo"), rst.getString("descritivo"));
                }
            }
        }

        return new ArrayList<>(merc.values());
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    m1.depto merc1,\n"
                    + "    m1.descritivo merc1_desc,\n"
                    + "    m2.secao merc2,\n"
                    + "    m2.descritivo merc2_desc,\n"
                    + "    m3.grupo merc3,\n"
                    + "    m3.descritivo merc3_desc,\n"
                    + "    m4.subgrupo merc4,\n"
                    + "    m4.descritivo merc4_desc\n"
                    + "from\n"
                    + "    (select * from deptos where secao = 0 and grupo = 0 and subgrupo = 0) m1\n"
                    + "    left join (select * from deptos where secao != 0 and grupo = 0 and subgrupo = 0) m2 on m1.depto = m2.depto\n"
                    + "    left join (select * from deptos where secao != 0 and grupo != 0 and subgrupo = 0) m3 on m2.depto = m3.depto and m2.secao = m3.secao\n"
                    + "    left join (select * from deptos where secao != 0 and grupo != 0 and subgrupo != 0) m4 on m3.depto = m4.depto and m3.secao = m4.secao and m3.grupo = m4.grupo\n"
                    + "order by\n"
                    + "    merc1,\n"
                    + "    merc2,\n"
                    + "    merc3,\n"
                    + "    merc4"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
                    imp.setMerc4ID(rst.getString("merc4"));
                    imp.setMerc4Descricao(rst.getString("merc4_desc"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    id,\n"
                    + "    descritivo\n"
                    + "from \n"
                    + "    familias\n"
                    + "order by \n"
                    + "    id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String sql
                = "SELECT\n"
                + "    a.id,\n"
                + "    coalesce(ean.ean, cast(a.id as varchar(13))) codigobarras,\n"
                + "    coalesce(ean.qtdee, 1) qtdembalagem,\n"
                + "    a.unidade_venda unidade,\n"
                + "    a.qtde_embalageme qtdembalagem_compra,\n"
                + "    a.unidade_compra,\n"
                + "    a.ipv,\n"
                + "    case when not bal.id is null then 'S' else 'N' end balanca,\n"
                + "    a.descritivo descricaocompleta,\n"
                + "    a.descritivo_pdv descricaoreduzida,\n"
                + "    a.descritivo descricaogondola, \n"
                + "    a.depto cod_mercadologico1,\n"
                + "    nullif(a.secao,0) cod_mercadologico2,\n"
                + "    nullif(a.grupo,0) cod_mercadologico3,\n"
                + "    nullif(a.subgrupo,0) cod_mercadologico4,\n"
                + "    a.familia id_familiaproduto,\n"
                + "    fam.descritivo familiaproduto,\n"
                + "    a.pesob pesobruto,\n"
                + "    a.pesol pesoliquido,\n"
                + "    a.datahora_cadastro datacadastro,\n"
                + "    a.validade,\n"
                + "    loja.margem_lucro margem, \n"
                + "    0 as estoquemaximo,\n"
                + "    estoq.estoque_minimo estoqueminimo,\n"
                + "    estoq.estoque_atual estoque,\n"
                + "    loja.custo custocomimposto, \n"
                + "    loja.custo_liquido custosemimposto,  \n"
                + "    preco.venda precovenda,\n"
                + "    case a.status when 0 then 'S' else 'N' end as ativo,\n"
                + "    a.classificacao_fiscal ncm,\n"
                + "    case when a.cest > 0 then a.cest else null end as cest,\n"
                + "    case a.monofasico\n"
                + "        when 'T' then 1\n"
                + "        when 'I' then 7\n"
                + "        when 'N' then 8\n"
                + "        when 'S' then 9\n"
                + "        when 'M' then 4\n"
                + "        when 'B' then 5\n"
                + "        when 'O' then 6\n"
                + "    else 9 end as piscofins_cst_debito,\n"
                + "    case a.monofasico\n"
                + "        when 'T' then 50\n"
                + "        when 'I' then 71\n"
                + "        when 'N' then 74\n"
                + "        when 'S' then 72\n"
                + "        when 'M' then 70\n"
                + "        when 'B' then 75\n"
                + "        when 'O' then 73\n"
                + "    else 21 end as piscofins_cst_credito,\n"
                + "    0 as piscofins_natureza_receita,\n"
                + "    case pe.tributacao_venda\n"
                + "        when 'T' then 0\n"
                + "        when 'R' then 20\n"
                + "        when 'D' then 51\n"
                + "        when 'S' then 50\n"
                + "        when 'F' then 60\n"
                + "        when 'I' then 40\n"
                + "        when 'N' then 41\n"
                + "    else 90 end as icms_cst,\n"
                + "    pe.icms_venda icms_aliquota, \n"
                + "    pe.reducao_venda icms_reduzido  \n"
                + "FROM\n"
                + "    produtos a\n"
                + "    join empresas emp on emp.id = " + getLojaOrigem() + "\n"
                + "    join produtos_estado pe on a.id = pe.id and pe.estado = emp.estado    \n"
                + "    join politicas_empresa poli on poli.empresa = emp.id\n"
                + "    join produtos_precos preco on a.id = preco.produto and poli.politica = preco.politica and preco.id = " + tipoVenda + "\n"
                + "    join produtos_loja loja on a.id = loja.id and poli.politica = loja.politica\n"
                + "    join estoques e on e.empresa = emp.id and e.troca != 'T'\n"
                + "    join produtos_estoques estoq on estoq.produto = a.id and estoq.estoque = e.id\n"
                + "    left join produtos_ean ean on ean.produto = a.id\n"
                + "    left join (select distinct id from vw_produtos_balancas order by id) bal on bal.id = a.id\n"
                + "    left join familias fam on a.familia = fam.id\n"
                + "order by\n"
                + "    a.id";

        ProgressBar.setStatus("Executando a query....");
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql)) {
                ProgressBar.setStatus("Convertendo em IMP....");
                int cont = 1;
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem_compra"));

                    switch (rst.getInt("ipv")) {
                        case 0: {
                            imp.setTipoEmbalagem("KG");
                            imp.seteBalanca(true);
                        }
                        ;
                        break;
                        case 2: {
                            imp.setTipoEmbalagem("UN");
                            imp.seteBalanca(true);
                        }
                        ;
                        break;
                        default: {
                            imp.setTipoEmbalagem("UN");
                            imp.seteBalanca(false);
                        }
                        ;
                        break;
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("cod_mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("cod_mercadologico3"));
                    imp.setCodMercadologico4(rst.getString("cod_mercadologico4"));
                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    if ("N".equals(rst.getString("ativo"))) {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    }
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_cst_debito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins_cst_credito"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natureza_receita"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));

                    //imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem_compra"));
                    ProgressBar.setStatus("Convertendo em IMP.... " + cont);
                    cont++;

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /**
     * Retorna as lojas do cliente
     *
     * @return
     * @throws java.lang.Exception
     */
    public List<ItemComboVO> getLojasCliente() throws Exception {
        List<ItemComboVO> lojas = new ArrayList<>();

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    id, \n"
                    + "    id || ' - ' || descritivo || '-' || cnpj_cpf descricao\n"
                    + "from \n"
                    + "    empresas\n"
                    + "order by\n"
                    + "    id"
            )) {
                while (rst.next()) {
                    lojas.add(new ItemComboVO(rst.getInt("id"), rst.getString("descricao")));
                }
            }
        }

        return lojas;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.id,\n"
                    + "    f.descritivo razao,\n"
                    + "    f.fantasia,\n"
                    + "    f.cnpj_cpf,\n"
                    + "    f.inscricao_rg,\n"
                    + "    f.inscricao_municipal,\n"
                    + "    f.suframa,\n"
                    + "    case when f.situacao != 0 then 0 else 1 end ativo,\n"
                    + "    f.endereco,\n"
                    + "    f.numero,\n"
                    + "    f.complemento,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade,\n"
                    + "    f.estado,\n"
                    + "    f.cod_ibge id_cidade,\n"
                    + "    f.cep,    \n"
                    + "    f.telefone1,\n"
                    + "    f.telefone2,\n"
                    + "    f.fax,\n"
                    + "    f.pedminimo,\n"
                    + "    f.datahora_cadastro,\n"
                    + "    f.observacao,\n"
                    + "    f.dias_vencto,\n"
                    + "    f.frequencia prazovisita,\n"
                    + "    f.entrega prazoentrega,\n"
                    + "    f.email,\n"
                    + "    f.condpagto\n"
                    + "from\n"
                    + "    fornecedores f\n"
                    + "order by\n"
                    + "    f.id"
            )) {

                while (rst.next()) {

                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("inscricao_rg"));
                    imp.setInsc_municipal(rst.getString("inscricao_municipal"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setIbge_municipio(rst.getInt("id_cidade"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone1"));
                    imp.setValor_minimo_pedido(rst.getDouble("pedminimo"));
                    imp.setDatacadastro(rst.getDate("datahora_cadastro"));
                    imp.setObservacao(rst.getString("observacao") + " Cond. pag: " + rst.getString("condpagto"));
                    imp.setCondicaoPagamento(Utils.stringToInt(rst.getString("dias_vencto")));
                    imp.setPrazoVisita(rst.getInt("prazovisita"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    String email = Utils.acertarTexto(rst.getString("email")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("1", "Email", "", "", TipoContato.COMERCIAL, email);
                    }
                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "Telefone 2",
                                null,
                                rst.getString("telefone2"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "Fax",
                                null,
                                rst.getString("fax"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opc) throws Exception {
        if (opc == OpcaoProduto.ICMS_FORNECEDOR) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                
                ProgressBar.setStatus("Executando a query...");
                
                int cont = 1;
                try (ResultSet rs = stm.executeQuery(
                        "SELECT\n" +
                        "    distinct\n" +
                        "    tf.produto, \n" +
                        "    tf.fornecedor, \n" +
                        "    tf.estado, \n" +
                        "    case \n" +
                        "     when tf.tributacao_compra = 'T' and tf.icms_compra = 18 and tf.reducao_compra = 0 then 2\n" +
                        "     when tf.tributacao_compra = 'R' and tf.icms_compra = 18 and tf.reducao_compra = 33.33 then 9\n" +
                        "     when tf.tributacao_compra = 'I' and tf.icms_compra = 0 and tf.reducao_compra = 0 then 6\n" +
                        "     when tf.tributacao_compra = 'T' and tf.icms_compra = 2.58 and tf.reducao_compra = 0 then 20\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 0 and tf.reducao_compra = 0 then 7\n" +
                        "     when tf.tributacao_compra = 'N' and tf.icms_compra = 0 and tf.reducao_compra = 0 then  17\n" +
                        "     when tf.tributacao_compra = 'T' and tf.icms_compra = 2.58 and tf.reducao_compra = 0 then 20 \n" +
                        "     when tf.tributacao_compra = 'O' and tf.icms_compra = 2.58 and tf.reducao_compra = 0 then 8\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 0 then 1\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 33.33 then 21\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 41.66 then 22\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 61.11 then 23\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 43.23 then 24\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 4 and tf.reducao_compra = 0 then 25\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 4 and tf.reducao_compra = 0 then 26\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 18 and tf.reducao_compra = 43.23 then 27\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 18 and tf.reducao_compra = 54.81 then 28\n" +
                        "     when tf.tributacao_compra = 'R' and tf.icms_compra = 18 and tf.reducao_compra = 61.11 then 4\n" +
                        "     when tf.tributacao_compra = 'T' and tf.icms_compra = 25 and tf.reducao_compra = 0 then 3\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 18 and tf.reducao_compra = 85.98 then 29\n" +
                        "     when tf.tributacao_compra = 'F' and tf.icms_compra = 18 and tf.reducao_compra = 57 then 30\n" +
                        "    else \n" +
                        "        8\n" +
                        "    end as icms_credito, \n" +
                        "    tf.icms_compra, \n" +
                        "    tf.reducao_compra,\n" +
                        "    tf.st_compra,\n" +
                        "    tf.pauta\n" +
                        "FROM\n" +
                        "    produtos a\n" +
                        "    join empresas emp on emp.id = " + getLojaOrigem() + "\n" +
                        "    join produtos_estado pe on a.id = pe.id and pe.estado = emp.estado \n" +
                        "    join politicas_empresa poli on poli.empresa = emp.id\n" +
                        "    join produtos_precos preco on a.id = preco.produto and poli.politica = preco.politica and preco.id = " + tipoVenda + "\n" +
                        "    join produtos_loja loja on a.id = loja.id and poli.politica = loja.politica\n" +
                        "    join estoques e on e.empresa = emp.id and e.troca != 'T'\n" +
                        "    join produtos_estoques estoq on estoq.produto = a.id and estoq.estoque = e.id\n" +
                        "    left join produtos_ean ean on ean.produto = a.id\n" +
                        "    left join (select distinct id from vw_produtos_balancas order by id) bal on bal.id = a.id\n" +
                        "    left join familias fam on a.familia = fam.id\n" +
                        "    join tabela_fornecedor_uf tf on a.id = tf.produto and\n" +
                        "    tf.datahora_alteracao in (select \n" +
                        "                                max(t.datahora_alteracao) \n" +
                        "                              from \n" +
                        "                                tabela_fornecedor_uf t \n" +
                        "                              where \n" +
                        "                                t.produto = tf.produto)\n" +
                        "order by\n" +
                        "    tf.produto")) {
                    while (rs.next()) {
                        
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("produto"));
                        imp.setIcmsAliqEntrada(rs.getDouble("icms_compra"));
                        imp.setIcmsCstEntrada(rs.getInt("st_compra"));
                        imp.setIcmsReducaoEntrada(rs.getDouble("reducao_compra"));
                        //imp.setIcmsCreditoId(rs.getString("icms_credito"));
                        ProgressBar.setStatus("Convertendo ICMS Fornecedor em IMP....");
                        cont++;
                        
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	pf.codigo_exterior,\n"
                    + "	p.qtde_embalageme qtdembalagem,\n"
                    + "	p.unidade_compra\n"
                    + "from \n"
                    + "	(select\n"
                    + "		produto id_produto,\n"
                    + "		fornecedor id_fornecedor,\n"
                    + "		trim(coalesce(referencia,'')) codigo_exterior\n"
                    + "	from\n"
                    + "		produtos_fornecedor\n"
                    + "	union\n"
                    + "	select\n"
                    + "		produto id_produto,\n"
                    + "		fornecedor id_fornecedor,\n"
                    + "		trim(coalesce(referencia,'')) codigo_exterior \n"
                    + "	from\n"
                    + "	    produtos_fornecedor_refs) pf\n"
                    + "	join produtos p on pf.id_produto = p.id\n"
                    + "order by\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigo_exterior"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        LOG.config("Carregar clientes de : ["
                + (importarDeClientes ? " Clientes " : "")
                + (importarDeEmpresas ? " Empresas " : "")
                + (importarDeFornecedores ? " Fornecedores " : "")
                + (importarDeTransportadoras ? " Transportadoras " : "")
                + (importarDeAdminCartao ? " Adminsitradora de Cartões " : "")
                + "]"
        );

        try (Statement stm = ConexaoOracle.createStatement()) {
            if (importarDeClientes) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    c.id,\n"
                        + "    c.cnpj_cpf,\n"
                        + "    c.inscricao_rg,\n"
                        + "    c.orgao_publico,\n"
                        + "    c.datahora_cadastro datacadastro,\n"
                        + "    c.descritivo razao,\n"
                        + "    c.fantasia,\n"
                        + "    c.situacao as bloqueado,\n"
                        + "    c.endereco,\n"
                        + "    c.numero,\n"
                        + "    c.complemento,\n"
                        + "    c.bairro,\n"
                        + "    c.cidade,\n"
                        + "    c.estado,\n"
                        + "    c.cod_ibge municipio_ibge,\n"
                        + "    c.cep,\n"
                        + "    c.estado_civil,\n"
                        + "    c.data_nascimento,\n"
                        + "    case c.sexo when 1 then 0 else 1 end sexo,\n"
                        + "    c.empresacad,\n"
                        + "    c.telefoneemp,\n"
                        + "    c.data_admissao,\n"
                        + "    c.cargo,\n"
                        + "    c.salario,\n"
                        + "    c.limite,\n"
                        + "    c.conjugue,\n"
                        + "    c.pai,\n"
                        + "    c.mae,\n"
                        + "    c.observacao,\n"
                        + "    c.dias_vencto,\n"
                        + "    c.telefone1,\n"
                        + "    c.telefone2,\n"
                        + "    c.email,\n"
                        + "    c.fax,\n"
                        + "    c.telefone_cobranca,\n"
                        + "    c.endereco_c,\n"
                        + "    c.numero_c,\n"
                        + "    c.complemento_c,\n"
                        + "    c.bairro_c,\n"
                        + "    c.cidade_c,\n"
                        + "    c.estado_c,\n"
                        + "    c.cep_c,\n"
                        + "    c.inscricao_municipal,\n"
                        + "    decode(c.empresa_convenio, '', 3, c.empresa_convenio) as empresa_convenio\n"
                        + "from\n"
                        + "    clientes c\n"
                        + "where\n"
                        + "    upper(c.descritivo) != 'CADASTRO AUTOMATICO'\n"
                        + "order by\n"
                        + "    id"
                )) {
                    while (rst.next()) {

                        ClienteIMP imp = new ClienteIMP();

                        imp.setId(rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj_cpf"));
                        imp.setInscricaoestadual(rst.getString("inscricao_rg"));
                        imp.setDataCadastro(rst.getTimestamp("datacadastro"));
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setBloqueado(rst.getBoolean("bloqueado"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setUf(rst.getString("estado"));
                        imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                        imp.setCep(rst.getString("cep"));
                        imp.setDataNascimento(rst.getDate("data_nascimento"));
                        imp.setSexo(rst.getInt("sexo") == 0 ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                        imp.setEmpresa(rst.getString("empresacad"));
                        imp.setEmpresaTelefone(rst.getString("telefoneemp"));
                        imp.setDataAdmissao(rst.getDate("data_admissao"));
                        imp.setCargo(rst.getString("cargo"));
                        imp.setSalario(rst.getDouble("salario"));
                        imp.setValorLimite(rst.getDouble("limite"));
                        imp.setNomeConjuge(rst.getString("conjugue"));
                        imp.setNomePai(rst.getString("pai"));
                        imp.setNomeMae(rst.getString("mae"));
                        imp.setObservacao("CLIENTE");
                        imp.setObservacao2(rst.getString("observacao"));
                        imp.setDiaVencimento(Utils.stringToInt(rst.getString("dias_vencto")));
                        imp.setTelefone(rst.getString("telefone1"));
                        imp.setCelular(rst.getString("telefone2"));
                        imp.setEmail(rst.getString("email"));
                        imp.setFax(rst.getString("fax"));
                        imp.setCobrancaTelefone(rst.getString("telefone_cobranca"));
                        imp.setCobrancaEndereco(rst.getString("endereco_c"));
                        imp.setCobrancaNumero(rst.getString("numero_c"));
                        imp.setCobrancaComplemento(rst.getString("complemento_c"));
                        imp.setCobrancaBairro(rst.getString("bairro_c"));
                        imp.setCobrancaMunicipio(rst.getString("cidade_c"));
                        imp.setCobrancaUf(rst.getString("estado_c"));
                        imp.setCobrancaCep(rst.getString("cep_c"));
                        imp.setInscricaoMunicipal(rst.getString("inscricao_municipal"));
                        imp.setGrupo(rst.getInt("empresa_convenio"));

                        result.add(imp);

                    }
                }
            }
            if (importarDeEmpresas) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    e.id,\n"
                        + "    e.cnpj_cpf,\n"
                        + "    e.inscricao_rg,\n"
                        + "    e.descritivo,\n"
                        + "    e.fantasia,\n"
                        + "    case when e.excluido = 'T' then 0 else 1 end ativo,\n"
                        + "    e.endereco,\n"
                        + "    e.numero,\n"
                        + "    e.complemento,\n"
                        + "    e.bairro,\n"
                        + "    e.cidade,\n"
                        + "    e.cod_ibge,\n"
                        + "    e.estado,\n"
                        + "    e.cep,\n"
                        + "    e.telefone1,\n"
                        + "    e.telefone2,\n"
                        + "    e.datahora_cadastro\n"
                        + "from\n"
                        + "    empresas e\n"
                        + "order by\n"
                        + "    e.id"
                )) {
                    while (rst.next()) {

                        ClienteIMP imp = new ClienteIMP();

                        imp.setId("1-" + rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj_cpf"));
                        imp.setInscricaoestadual(rst.getString("inscricao_rg"));
                        imp.setRazao(rst.getString("descritivo"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setAtivo(rst.getBoolean("ativo"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setMunicipioIBGE(rst.getInt("cod_ibge"));
                        imp.setUf(rst.getString("estado"));
                        imp.setCep(rst.getString("cep"));
                        imp.setTelefone(rst.getString("telefone1"));
                        imp.setCelular(rst.getString("telefone2"));
                        imp.setDataCadastro(rst.getDate("datahora_cadastro"));
                        imp.setObservacao("EMPRESA");

                        result.add(imp);

                    }
                }
            }
            if (importarDeFornecedores) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    f.id,\n"
                        + "    f.cnpj_cpf,\n"
                        + "    f.inscricao_rg,\n"
                        + "    f.descritivo,\n"
                        + "    f.fantasia,\n"
                        + "    case when f.situacao != 0 then 0 else 1 end as ativo,\n"
                        + "    f.endereco,\n"
                        + "    f.numero,\n"
                        + "    f.complemento,\n"
                        + "    f.bairro,\n"
                        + "    f.cidade,\n"
                        + "    f.estado,\n"
                        + "    f.cep,\n"
                        + "    f.telefone1,\n"
                        + "    f.observacao,\n"
                        + "    f.email,\n"
                        + "    f.datahora_cadastro\n"
                        + "from\n"
                        + "    fornecedores f\n"
                        + "order by\n"
                        + "    f.id"
                )) {
                    while (rst.next()) {

                        ClienteIMP imp = new ClienteIMP();

                        imp.setId("2-" + rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj_cpf"));
                        imp.setInscricaoestadual(rst.getString("inscricao_rg"));
                        imp.setRazao(rst.getString("descritivo"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setAtivo(rst.getBoolean("ativo"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setUf(rst.getString("estado"));
                        imp.setCep(rst.getString("cep"));
                        imp.setTelefone(rst.getString("telefone1"));
                        imp.setObservacao("FORNECEDOR");
                        imp.setObservacao2(rst.getString("observacao"));
                        imp.setEmail(rst.getString("email"));
                        imp.setDataCadastro(rst.getDate("datahora_cadastro"));

                        result.add(imp);

                    }
                }
            }
            if (importarDeTransportadoras) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    t.id,\n"
                        + "    t.cnpj_cpf,\n"
                        + "    t.inscricao_rg,\n"
                        + "    t.descritivo,\n"
                        + "    t.fantasia,\n"
                        + "    t.endereco,\n"
                        + "    t.numero,\n"
                        + "    t.complemento,\n"
                        + "    t.bairro,\n"
                        + "    t.cidade,\n"
                        + "    t.estado,\n"
                        + "    t.cep,\n"
                        + "    t.datahora_cadastro,\n"
                        + "    t.observacao,\n"
                        + "    t.telefone1,\n"
                        + "    t.email,\n"
                        + "    t.site\n"
                        + "from\n"
                        + "    transportadoras t\n"
                        + "order by\n"
                        + "    t.id"
                )) {
                    while (rst.next()) {

                        ClienteIMP imp = new ClienteIMP();

                        imp.setId("3-" + rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj_cpf"));
                        imp.setInscricaoestadual(rst.getString("inscricao_rg"));
                        imp.setRazao(rst.getString("descritivo"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setUf(rst.getString("estado"));
                        imp.setCep(rst.getString("cep"));
                        imp.setDataCadastro(rst.getDate("datahora_cadastro"));
                        imp.setObservacao("TRANSPORTADORA");
                        imp.setObservacao2(rst.getString("observacao"));
                        imp.setTelefone(rst.getString("telefone1"));
                        imp.setEmail(rst.getString("email"));

                        result.add(imp);

                    }
                }
            }
            if (importarDeAdminCartao) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    a.id,\n"
                        + "    a.cnpj,\n"
                        + "    a.inscricao,\n"
                        + "    a.razao_social,\n"
                        + "    a.descritivo,\n"
                        + "    a.endereco,\n"
                        + "    a.numero,\n"
                        + "    a.complemento,\n"
                        + "    a.bairro,\n"
                        + "    a.cidade,\n"
                        + "    a.estado,\n"
                        + "    a.datahora_cadastro\n"
                        + "from\n"
                        + "    adm_cartoes a\n"
                        + "order by\n"
                        + "    a.id"
                )) {
                    while (rst.next()) {

                        ClienteIMP imp = new ClienteIMP();

                        imp.setId("4-" + rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj"));
                        imp.setInscricaoestadual(rst.getString("inscricao"));
                        imp.setRazao(rst.getString("razao_social"));
                        imp.setFantasia(rst.getString("descritivo"));
                        imp.setFantasia(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setUf(rst.getString("estado"));
                        imp.setDataCadastro(rst.getDate("datahora_cadastro"));
                        imp.setObservacao("ADMINISTRADORA DE CARTAO");

                        result.add(imp);

                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.id,\n"
                    + "    c.cnpj_cpf,\n"
                    + "    c.inscricao_rg,\n"
                    + "    c.descritivo,\n"
                    + "    c.fantasia,\n"
                    + "    case when c.bloqueado != 'F' then 1 else 0 end ativo,\n"
                    + "    c.endereco,\n"
                    + "    c.numero,\n"
                    + "    c.complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade,\n"
                    + "    c.estado,\n"
                    + "    c.cep,\n"
                    + "    c.datahora_cadastro,\n"
                    + "    c.observacao,\n"
                    + "    c.telefone1,\n"
                    + "    c.vencimento1\n"
                    + "from\n"
                    + "    conveniadas c\n"
                    + "order by\n"
                    + "    c.id"
            )) {
                while (rst.next()) {

                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj_cpf"));
                    imp.setInscricaoEstadual(rst.getString("inscricao_rg"));
                    imp.setRazao(rst.getString("descritivo"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setObservacoes(rst.getString("observacao"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setDiaPagamento(rst.getInt("vencimento1"));

                    result.add(imp);

                }
            }
        }

        return result;
    }

    public void setPlanoContaEntrada(List<PlanoConta> planosSelecionados) {
        this.planosSelecionados = planosSelecionados;
    }

    private String getPlanosContaStr() {

        StringBuilder planos = new StringBuilder();
        String strPlanos = "";

        for (Iterator<PlanoConta> iterator = planosSelecionados.iterator(); iterator.hasNext();) {
            PlanoConta plano = iterator.next();
            planos.append(plano.getId());

            strPlanos += plano.getId() + " - " + plano.getDescricao();

            if (iterator.hasNext()) {
                planos.append(",");
                strPlanos += ",";
            }
        }
        LOG.log(
                Level.CONFIG,
                "Planos a serem carregados: {0}",
                strPlanos
        );

        return planos.toString();

    }

    public void setDiasVenda(int diasVenda) {
        this.diasVenda = diasVenda;
    }

    private int getDiasVenda() {
        return diasVenda;
    }

    public void setVendaDataInicio(Date vendaDataInicio) {
        this.vendaDataInicio = vendaDataInicio;
    }

    public void setVendaDataTermino(Date vendaDataTermino) {
        this.vendaDataTermino = vendaDataTermino;
    }

    public List<ItemComboVO> getTipoVenda() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descritivo from tipo_venda order by id"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("id") + " - " + rst.getString("descritivo")));
                }
            }
        }

        return result;
    }

    public void setTipoVenda(int tipoVenda) {
        this.tipoVenda = tipoVenda;
    }

    /**
     * Classe que representa um plano de contas no Arius.
     */
    public static class PlanoConta {

        private String id;
        private String descricao;

        public PlanoConta(String id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

    }

    public List<PlanoConta> getPlanoContasEntrada() throws Exception {
        List<PlanoConta> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    id,\n"
                    + "    descritivo\n"
                    + "from\n"
                    + "    plano_contas\n"
                    + "where\n"
                    + "    plano_tipo = 0\n"
                    + "order by\n"
                    + "    id"
            )) {
                while (rst.next()) {
                    result.add(new PlanoConta(rst.getString("id"), rst.getString("descritivo")));
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {

        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            String sql = "select\n"
                    + "	c.id,\n"
                    + "	to_char(c.emissao,'dd/MM/yyyy') emissao,\n"
                    + "	c.nf,\n"
                    + "	c.pdv,\n"
                    + "	c.valor,\n"
                    + "	c.desconto,\n"
                    + "	c.liquido,\n"
                    + "	c.descritivo,\n"
                    + "	c.observacao,\n"
                    + "	c.desc_plano_conta,\n"
                    + "	c.desc_forma_pagto,\n"
                    + "	c.tipo_cadastro,\n"
                    + "	c.id_cadastro,\n"
                    + "	to_char(c.vencimento,'dd/MM/yyyy') vencimento,\n"
                    + "	c.parcela,\n"
                    + "	c.juros,\n"
                    + "	c.cpf_cnpj\n"
                    + "from\n"
                    + "	vw_contas c\n"
                    + "where\n"
                    + "	empresa = " + getLojaOrigem() + "\n"
                    + "	and parcela <> 0\n"
                    + "	and tipo_conta = 1\n"
                    + "	and pagamento is null\n"
                    + "	and not tipo_cadastro is null\n"
                    + "	and plano_conta in (" + getPlanosContaStr() + ")\n"
                    + "order by id";
            LOG.fine("SQL a ser executado:\n" + sql);
            try (ResultSet rst = stm.executeQuery(sql)) {
                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
                while (rst.next()) {

                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(formater.parse(rst.getString("emissao")));
                    imp.setNumeroCupom(rst.getString("nf"));
                    imp.setEcf(rst.getString("pdv"));
                    imp.setValor(rst.getDouble("liquido"));
                    StringBuilder obs = new StringBuilder();
                    if (rst.getString("descritivo") != null && !"".equals(rst.getString("descritivo").trim())) {
                        obs.append("descricao: ").append(rst.getString("descritivo")).append(" ");
                    }
                    if (rst.getString("desc_plano_conta") != null && !"".equals(rst.getString("desc_plano_conta").trim())) {
                        obs.append("planoconta: ").append(rst.getString("desc_plano_conta")).append(" ");
                    }
                    if (rst.getString("desc_forma_pagto") != null && !"".equals(rst.getString("desc_forma_pagto").trim())) {
                        obs.append("forma pag: ").append(rst.getString("desc_forma_pagto")).append(" ");
                    }
                    if (rst.getString("observacao") != null && !"".equals(rst.getString("observacao").trim())) {
                        obs.append("observacao: ").append(rst.getString("observacao")).append(" ");
                    }
                    imp.setIdCliente(rst.getString("id_cadastro"));
                    imp.setDataVencimento(formater.parse(rst.getString("vencimento")));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setCnpjCliente(rst.getString("cpf_cnpj"));

                    result.add(imp);

                }
            }
        }

        return result;

    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            String sql = "select  \n"
                    + "        c.id,  \n"
                    + "        c.id_cadastro,  \n"
                    + "        c.descritivo,  \n"
                    + "        trunc(c.emissao) emissao,  \n"
                    + "        c.nf,  \n"
                    + "        c.pdv,  \n"
                    + "        c.valor,  \n"
                    + "        c.desconto,  \n"
                    + "        c.liquido,  \n"
                    + "        c.descritivo, \n"
                    + "        c.id_cadastro, \n"
                    + "        trim(translate(c.observacao, '->', ' ')) observacao,\n"
                    + "        c.desc_plano_conta,  \n"
                    + "        c.desc_forma_pagto,  \n"
                    + "        c.tipo_cadastro,  \n"
                    + "        c.id_cadastro,  \n"
                    + "        to_char(c.vencimento, 'yyyy-MM-dd') vencimento,  \n"
                    + "        c.parcela,  \n"
                    + "        c.juros,  \n"
                    + "        c.cpf_cnpj, \n"
                    + "        c.cheque, \n"
                    + "        c.banco_cheque, \n"
                    + "        c.agencia,  \n"
                    + "        c.conta, \n"
                    + "        cl.inscricao_rg, \n"
                    + "        cl.telefone1 \n"
                    + "        from  \n"
                    + "        vw_contas c \n"
                    + "        join clientes cl on (c.id_cadastro = cl.id) \n"
                    + "        and empresa = 1 \n"
                    + "        and parcela <> 0  \n"
                    + "        and tipo_conta = " + getLojaOrigem() + "\n"
                    + "        and pagamento is null  \n"
                    + "        and not tipo_cadastro is null  \n"
                    + "        and plano_conta in (" + getPlanosContaStr() + ") \n"
                    + "        order by emissao desc";

            LOG.fine("SQL a ser executado:\n" + sql);

            try (ResultSet rst = stm.executeQuery(sql)) {
                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

                while (rst.next()) {

                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id_cadastro"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("nf"));
                    imp.setEcf(rst.getString("pdv"));
                    imp.setValor(rst.getDouble("liquido"));
                    imp.setNumeroCheque(rst.getString("cheque"));
                    imp.setBanco(rst.getInt("banco_cheque"));
                    imp.setRg(rst.getString("inscricao_rg"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setObservacao(rst.getString("observacao"));
                    StringBuilder obs = new StringBuilder();
                    if (rst.getString("descritivo") != null && !"".equals(rst.getString("descritivo").trim())) {
                        obs.append("descricao: ").append(rst.getString("descritivo")).append(" ");
                    }
                    if (rst.getString("desc_plano_conta") != null && !"".equals(rst.getString("desc_plano_conta").trim())) {
                        obs.append("planoconta: ").append(rst.getString("desc_plano_conta")).append(" ");
                    }
                    if (rst.getString("desc_forma_pagto") != null && !"".equals(rst.getString("desc_forma_pagto").trim())) {
                        obs.append("forma pag: ").append(rst.getString("desc_forma_pagto")).append(" ");
                    }
                    if (rst.getString("observacao") != null && !"".equals(rst.getString("observacao").trim())) {
                        obs.append("observacao: ").append(rst.getString("observacao")).append(" ");
                    }
                    imp.setId(rst.getString("tipo_cadastro") + "-" + rst.getString("id"));
                    imp.setNome(rst.getString("descritivo"));
                    imp.setCpf(rst.getString("cpf_cnpj"));
                    imp.setValorJuros(rst.getFloat("juros"));
                    imp.setDataDeposito(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        try {
            MultiStatementIterator<VendaIMP> iterator = new MultiStatementIterator<>(
                    new MultiStatementIterator.NextBuilder<VendaIMP>() {
                        @Override
                        public VendaIMP makeNext(ResultSet rst) throws Exception {
                            VendaIMP imp = new VendaIMP();

                            imp.setId(rst.getString("id"));
                            imp.setNumeroCupom(rst.getInt("numerocupom"));
                            imp.setEcf(rst.getInt("ecf"));
                            imp.setData(rst.getDate("data"));
                            imp.setIdClientePreferencial(rst.getString("idclientepreferencial"));
                            imp.setHoraInicio(rst.getDate("data"));
                            imp.setHoraTermino(rst.getDate("data"));
                            imp.setCancelado(rst.getBoolean("cancelado"));
                            imp.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                            imp.setCpf(rst.getString("cpf"));
                            imp.setNumeroSerie(rst.getString("numeroserie"));
                            imp.setModeloImpressora(rst.getString("modeloimpressora"));
                            imp.setNomeCliente(rst.getString("nomecliente"));
                            imp.setEnderecoCliente(rst.getString("enderecocliente"));
                            imp.setChaveNfCe(rst.getString("chavenfce"));

                            return imp;
                        }
                    },
                    new MultiStatementIterator.StatementBuilder() {
                        @Override
                        public Statement makeStatement() throws Exception {
                            return ConexaoOracle.getConexao().createStatement();
                        }
                    }
            );

            String sql = "select\n"
                    + "    v.id,\n"
                    + "    v.nf numerocupom,\n"
                    + "    v.pdv ecf,\n"
                    + "    v.data_hora data,\n"
                    + "    v.id_cliente idclientepreferencial,\n"
                    + "    case when v.cancelado != 'F' then 1 else 0 end as cancelado,\n"
                    + "    v.valor subtotalimpressora,\n"
                    + "    v.cnpj_cpf cpf,\n"
                    + "    pdv.serie numeroserie,\n"
                    + "    pdv.ecf_mod modeloimpressora,\n"
                    + "    c.descritivo nomecliente,\n"
                    + "    c.endereco || ',' || c.numero || ',' || c.complemento || ',' || c.bairro || ',' || c.cidade || '' || c.estado enderecocliente,\n"
                    + "    v.chave chavenfce\n"
                    + "from\n"
                    + "    vendas v\n"
                    + "    left join pdvs pdv on v.pdv = pdv.id and pdv.empresa = v.empresa\n"
                    + "    left join clientes c on v.id_cliente = c.id\n"
                    + "where\n"
                    + "    v.empresa = " + getLojaOrigem() + "\n"
                    + "    and cast(to_char(v.data_hora, 'yyyymmdd') as integer) >= cast('{DATA_INICIO}' as integer)\n"
                    + "    and cast(to_char(v.data_hora, 'yyyymmdd') as integer) <= cast('{DATA_TERMINO}' as integer)\n"
                    + "order by v.id";

            for (String statement : SQLUtils.quebrarSqlEmMeses(sql, vendaDataInicio, vendaDataTermino, new SimpleDateFormat("yyyyMMdd"))) {
                iterator.addStatement(statement);
            }

            return iterator;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro\n", ex);
            throw ex;
        }

    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private Statement stm;
        private ResultSet rst;
        private final String sql;

        public VendaIterator(String origem, Date vendaDataInicio, Date vendaDataTermino) throws Exception {
            this.stm = ConexaoOracle.createStatement();
            this.sql
                    = "select\n"
                    + "    v.id,\n"
                    + "    v.nf numerocupom,\n"
                    + "    v.pdv ecf,\n"
                    + "    v.data_hora data,\n"
                    + "    v.id_cliente idclientepreferencial,\n"
                    + "    case when v.cancelado != 'F' then 1 else 0 end as cancelado,\n"
                    + "    v.valor subtotalimpressora,\n"
                    + "    v.cnpj_cpf cpf,\n"
                    + "    pdv.serie numeroserie,\n"
                    + "    pdv.ecf_mod modeloimpressora,\n"
                    + "    c.descritivo nomecliente,\n"
                    + "    c.endereco || ',' || c.numero || ',' || c.complemento || ',' || c.bairro || ',' || c.cidade || '' || c.estado enderecocliente,\n"
                    + "    v.chave chavenfce\n"
                    + "from\n"
                    + "    vendas v\n"
                    + "    left join pdvs pdv on v.pdv = pdv.id and pdv.empresa = v.empresa\n"
                    + "    left join clientes c on v.id_cliente = c.id\n"
                    + "where\n"
                    + "    v.empresa = " + origem + "\n"
                    + "    and v.data_hora >= '" + DATE_FORMAT.format(vendaDataInicio) + "'\n"
                    + "    and v.data_hora <= '" + DATE_FORMAT.format(vendaDataTermino) + "'\n"
                    + "order by v.id";
            this.stm.setFetchSize(10000);
            this.rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            try {
                return !rst.isClosed() && !rst.isLast();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no hasNext()\n" + sql, ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public VendaIMP next() {
            try {
                if (rst.next()) {
                    VendaIMP imp = new VendaIMP();
                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(rst.getInt("numerocupom"));
                    imp.setEcf(rst.getInt("ecf"));
                    imp.setData(rst.getDate("data"));
                    imp.setIdClientePreferencial(rst.getString("idclientepreferencial"));
                    imp.setHoraInicio(rst.getDate("data"));
                    imp.setHoraTermino(rst.getDate("data"));
                    imp.setCancelado(rst.getBoolean("cancelado"));
                    imp.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroSerie(rst.getString("numeroserie"));
                    imp.setModeloImpressora(rst.getString("modeloimpressora"));
                    imp.setNomeCliente(rst.getString("nomecliente"));
                    imp.setEnderecoCliente(rst.getString("enderecocliente"));
                    imp.setChaveNfCe(rst.getString("chavenfce"));

                    return imp;
                } else {
                    rst.close();
                    stm.close();
                    return null;
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no next()", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Não suportado.");
        }
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {

        try {
            MultiStatementIterator<VendaItemIMP> iterator = new MultiStatementIterator<>(
                    new MultiStatementIterator.NextBuilder<VendaItemIMP>() {
                        @Override
                        public VendaItemIMP makeNext(ResultSet rst) throws Exception {
                            VendaItemIMP imp = new VendaItemIMP();

                            imp.setId(rst.getString("id"));
                            imp.setSequencia(rst.getInt("sequencia"));
                            imp.setVenda(rst.getString("venda"));
                            imp.setProduto(rst.getString("produto"));
                            imp.setDescricaoReduzida(rst.getString("descritivo_pdv"));
                            imp.setQuantidade(rst.getDouble("qtde"));
                            imp.setPrecoVenda(rst.getDouble("valor"));
                            imp.setValorDesconto(rst.getDouble("desconto"));
                            imp.setValorAcrescimo(rst.getDouble("acrescimo"));
                            imp.setCodigoBarras(rst.getString("ean"));
                            imp.setUnidadeMedida(rst.getString("unidade"));
                            imp.setIcmsCst(rst.getInt("cst"));
                            imp.setIcmsAliq(rst.getDouble("aliquota"));

                            return imp;
                        }
                    },
                    new MultiStatementIterator.StatementBuilder() {
                        @Override
                        public Statement makeStatement() throws Exception {
                            return ConexaoOracle.getConexao().createStatement();
                        }
                    }
            );

            String sql = "select\n"
                    + "    vi.id,\n"
                    + "    vi.id sequencia,\n"
                    + "    vi.venda,\n"
                    + "    vi.produto,\n"
                    + "    p.descritivo_pdv,\n"
                    + "    vi.qtde,\n"
                    + "    (vi.valor / vi.qtde) valor,\n"
                    + "    vi.desconto,\n"
                    + "    vi.acrescimo,\n"
                    + "    coalesce((select ean from produtos_ean where produto = p.id and rownum = 1), cast(p.id as varchar(10))) ean,\n"
                    + "    p.unidade_venda unidade,\n"
                    + "    vi.cst,\n"
                    + "    vi.aliquota\n"
                    + "from\n"
                    + "    itens_venda vi\n"
                    + "    join vendas v on vi.venda = v.id\n"
                    + "    join produtos p on vi.produto = p.id\n"
                    + "where\n"
                    + "    v.empresa = " + getLojaOrigem() + "\n"
                    + "    and cast(to_char(v.data_hora, 'yyyymmdd') as integer) >= cast('{DATA_INICIO}' as integer)\n"
                    + "    and cast(to_char(v.data_hora, 'yyyymmdd') as integer) <= cast('{DATA_TERMINO}' as integer)\n"
                    + "order by vi.id";

            for (String statement : SQLUtils.quebrarSqlEmMeses(sql, vendaDataInicio, vendaDataTermino, new SimpleDateFormat("yyyyMMdd"))) {
                iterator.addStatement(statement);
            }

            return iterator;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro\n", ex);
            throw ex;
        }

        /*
         try {
         return new VendaItemIterator(getLojaOrigem(), vendaDataInicio, vendaDataTermino);
         } catch (Exception ex) {        
         LOG.log(Level.SEVERE, "Erro\n", ex);
         throw ex;
         }
         */
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm;
        private ResultSet rst;
        private String sql;

        public VendaItemIterator(String origem, Date vendaDataInicio, Date vendaDataTermino) throws Exception {
            this.stm = ConexaoOracle.createStatement();
            this.sql
                    = "select\n"
                    + "    vi.id,\n"
                    + "    vi.id sequencia,\n"
                    + "    vi.venda,\n"
                    + "    vi.produto,\n"
                    + "    p.descritivo_pdv,\n"
                    + "    vi.qtde,\n"
                    + "    (vi.valor / vi.qtde) valor,\n"
                    + "    vi.desconto,\n"
                    + "    vi.acrescimo,\n"
                    + "    coalesce((select ean from produtos_ean where produto = p.id and rownum = 1), cast(p.id as varchar(10))) ean,\n"
                    + "    p.unidade_venda unidade,\n"
                    + "    vi.cst,\n"
                    + "    vi.aliquota\n"
                    + "from\n"
                    + "    itens_venda vi\n"
                    + "    join vendas v on vi.venda = v.id\n"
                    + "    join produtos p on vi.produto = p.id\n"
                    + "where\n"
                    + "    v.empresa = " + origem + "\n"
                    + "    and v.data_hora >= '" + DATE_FORMAT.format(vendaDataInicio) + "'\n"
                    + "    and v.data_hora <= '" + DATE_FORMAT.format(vendaDataTermino) + "'\n"
                    + "order by vi.id";
            this.stm.setFetchSize(10000);
            this.rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            try {
                return !rst.isClosed() && !rst.isLast();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no hasNext()\n" + sql, ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public VendaItemIMP next() {
            try {
                if (rst.next()) {
                    VendaItemIMP imp = new VendaItemIMP();

                    imp.setId(rst.getString("id"));
                    imp.setSequencia(rst.getInt("sequencia"));
                    imp.setVenda(rst.getString("venda"));
                    imp.setProduto(rst.getString("produto"));
                    imp.setDescricaoReduzida(rst.getString("descritivo_pdv"));
                    imp.setQuantidade(rst.getDouble("qtde"));
                    imp.setPrecoVenda(rst.getDouble("valor"));
                    imp.setValorDesconto(rst.getDouble("desconto"));
                    imp.setValorAcrescimo(rst.getDouble("acrescimo"));
                    imp.setCodigoBarras(rst.getString("ean"));
                    imp.setUnidadeMedida(rst.getString("unidade"));
                    imp.setIcmsCst(rst.getInt("cst"));
                    imp.setIcmsAliq(rst.getDouble("aliquota"));

                    return imp;
                } else {
                    rst.close();
                    stm.close();
                    return null;
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no next()", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Não suportado.");
        }

    }

}
