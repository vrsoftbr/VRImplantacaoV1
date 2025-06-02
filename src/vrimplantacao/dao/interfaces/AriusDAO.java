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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.devolucao.receber.ReceberDevolucaoDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoProdutoFornecedor;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNfe;
import vrimplantacao2.vo.cadastro.notafiscal.TipoFreteNotaFiscal;
import vrimplantacao2.vo.cadastro.notafiscal.TipoNota;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoDestinatario;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.DivisaoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;
import vrimplantacao2.vo.importacao.NotaFiscalItemIMP;
import vrimplantacao2.vo.importacao.NotaOperacao;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

public class AriusDAO extends InterfaceDAO implements MapaTributoProvider {

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
    private int idEstoque = 1;
    private Date notasDataInicio = null;
    private Date notasDataTermino = null;
    public boolean i_notaEntrada = false;
    public boolean i_notaSaida = false;
    public boolean naoUtilizaPlanoConta = false;

    public void setNotasDataInicio(Date notasDataInicio) {
        this.notasDataInicio = notasDataInicio;
    }

    public void setNotasDataTermino(Date notasDataTermino) {
        this.notasDataTermino = notasDataTermino;
    }

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
        return "Arius";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "        id,\n"
                    + "        descritivo,\n"
                    + "        icms_venda,\n"
                    + "        reducao_venda,\n"
                    + "        tributacao_venda \n"
                    + "from \n"
                    + "        tributacao_pdv \n"
                    + "order by descritivo")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("descritivo")
                            + "(ALI: " + rs.getString("icms_venda")
                            + " RED: " + rs.getString("reducao_venda") + ")"));
                }
            }
        }
        return result;
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
                + "    a.nutricional,\n"
                + "    coalesce(ean.ean, cast(a.id as varchar(13))) codigobarras,\n"
                + "    coalesce(ean.qtdee, 1) qtdembalagem,\n"
                + "    a.unidade_venda unidade,\n"
                + "    a.qtde_embalageme qtdembalagem_compra,\n"
                + "    a.unidade_compra,\n"
                + "    a.ipv,\n"
                + "    a.UNIDADE_VENDA,\n"
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
                + "    loja.custo_liquido custosemimposto,\n"
                + "    preco.venda precovenda,\n"
                //+ "    case a.status when 0 then 'S' else 'N' end as ativo,\n"
                + "    a.status,\n"
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
                + "    pe.icms_venda icms_aliquota,\n"
                + "    pe.reducao_venda icms_reduzido,\n"
                + "    pe.iva,\n"
                + "    pe.estado,\n"
                + "    pe.tipo_iva,\n"
                + "    pe.st_venda,\n"
                + "    01 as p_iva\n"
                + "FROM\n"
                + "     produtos a\n"
                + "     join empresas emp on emp.id = " + getLojaOrigem() + "\n"
                + "     join config on config.id = emp.id\n"
                + "	join produtos_estado pe on a.id = pe.id and pe.estado = emp.estado\n"
                + "	join politicas_empresa poli on poli.empresa = emp.id\n"
                + "	join produtos_precos preco on a.id = preco.produto and poli.politica = preco.politica and preco.id = 1\n"
                + "	join produtos_loja loja on a.id = loja.id and poli.politica = loja.politica\n"
                + "	join estoques e on e.empresa = emp.id and e.id = config.estoque_saida\n"
                + "	join produtos_estoques estoq on estoq.produto = a.id and estoq.estoque = e.id\n"
                + "	left join produtos_ean ean on ean.produto = a.id\n"
                + "	left join (select distinct id from vw_produtos_balancas order by id) bal on bal.id = a.id\n"
                + "	left join familias fam on a.familia = fam.id\n"
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
                    if ("S".equals(rst.getString("balanca").trim())) {
                        if (rst.getString("codigobarras").length() > 6 && rst.getString("codigobarras").length() < 9) {
                            imp.setEan(rst.getString("codigobarras").substring(1, rst.getString("codigobarras").length()));
                        } else {
                            imp.setEan(rst.getString("codigobarras"));
                        }
                    } else {
                        imp.setEan(rst.getString("codigobarras"));
                    }
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem_compra"));

                    String tipoembalagem = rst.getString("UNIDADE_VENDA");

                    if (tipoembalagem.contains("KG")) {

                        if (rst.getInt("ipv") == 1) {

                            imp.setTipoEmbalagem("UN");
                            imp.setTipoEmbalagemCotacao("UN");
                        } else {

                            imp.setTipoEmbalagem("KG");
                            imp.setTipoEmbalagemCotacao("KG");
                        }

                    } else {

                        switch (rst.getInt("ipv")) {
                            case 0: {
                                imp.setTipoEmbalagem("KG");
                                imp.setTipoEmbalagemCotacao("KG");
                                imp.seteBalanca(true);
                            }
                            ;
                            break;
                            case 2: {
                                imp.setTipoEmbalagem("UN");
                                imp.setTipoEmbalagemCotacao("UN");
                                imp.seteBalanca(true);
                            }
                            ;
                            break;
                            default: {
                                imp.setTipoEmbalagem("UN");
                                imp.setTipoEmbalagemCotacao("UN");
                                imp.seteBalanca(false);
                            }
                            ;
                            break;
                        }
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

                    /*if ("N".equals(rst.getString("ativo"))) {
                     imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                     } else {
                     imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                     }*/
                    imp.setDescontinuado((rst.getInt("status") == 1));
                    imp.setSituacaoCadastro(rst.getInt("status") == 3 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setVendaPdv((rst.getInt("status") != 2));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_cst_debito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins_cst_credito"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natureza_receita"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));
                    imp.setPautaFiscalId(formatPautaFiscalId(
                            rst.getString("estado"),
                            rst.getString("ncm"),
                            rst.getDouble("p_iva"),
                            rst.getDouble("iva"),
                            rst.getInt("st_venda"),
                            rst.getInt("st_venda")
                    ));

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
                    + "    f.observacao_pedido,\n"
                    + "    f.dias_vencto,\n"
                    + "    f.frequencia prazovisita,\n"
                    + "    f.entrega prazoentrega,\n"
                    + "    f.email,\n"
                    + "    f.condpagto,\n"
                    + "    f.produtor,\n"
                    + "    f.simples_nacional,\n"
                    + "    fl.id as iddivisao,\n"
                    + "    fl.descritivo as descdivisao,\n"
                    + "    fl.condpagto as condpgtodivisao,\n"
                    + "    fl.dias_vencto as diasvencdivisao,\n"
                    + "    fl.prazo_entrega as prazoentregadivisao\n"
                    + "from\n"
                    + "    fornecedores f\n"
                    + "left join fornecedores_linhas fl on fl.fornecedor = f.id    \n"
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
                    imp.setNumero(rst.getString("numero") != null ? (rst.getString("numero").replace("/", "").replace("-", "")) : "");
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setIbge_municipio(rst.getInt("id_cidade"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone1"));
                    imp.setValor_minimo_pedido(rst.getDouble("pedminimo"));
                    imp.setDatacadastro(rst.getDate("datahora_cadastro"));
                    imp.setObservacao(rst.getString("observacao") + " - " + rst.getString("observacao_pedido"));
                    imp.setCondicaoPagamento(Utils.stringToInt(rst.getString("condpagto")));
                    imp.setPrazoVisita(rst.getInt("prazovisita"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    //imp.setIdDivisao(rst.getString("iddivisao") + "-" + rst.getString("id"));

                    if ("T".equals(rst.getString("produtor"))) {
                        if (Utils.stringToLong(imp.getCnpj_cpf()) <= 99999999999L) {
                            imp.setTipoEmpresa(TipoEmpresa.PRODUTOR_RURAL_FISICA);
                        } else {
                            imp.setTipoEmpresa(TipoEmpresa.PRODUTOR_RURAL_JURIDICO);
                        }
                    } else if ("T".equals(rst.getString("simples_nacional"))) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                    }

                    String email = Utils.acertarTexto(rst.getString("email")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("1", "Email", "", "", TipoContato.COMERCIAL, (email.length() > 50 ? email.substring(0, 50) : email));
                    }
                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "Telefone 2",
                                rst.getString("telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "Fax",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    try (Statement stm2 = ConexaoOracle.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select \n"
                                + "  fc.id as id_contato,\n"
                                + "  fc.descritivo as contato,\n"
                                + "  fc.depto,\n"
                                + "  fc.fone as cont_telefone,\n"
                                + "  fc.email as cont_email,\n"
                                + "  fc.fone2 as cont_telefone2,\n"
                                + "  fc.celular as cont_celular\n"
                                + "from fornecedores_contatos fc\n"
                                + "where fc.fornecedor = " + imp.getImportId()
                        )) {
                            while (rst2.next()) {

                                String emailCont = rst2.getString("cont_email");

                                if ((emailCont != null)
                                        && (!emailCont.trim().isEmpty())) {

                                    emailCont = (emailCont.length() > 50 ? emailCont.substring(0, 50) : emailCont);
                                }

                                imp.addContato(
                                        rst2.getString("id_contato"),
                                        rst2.getString("contato"),
                                        rst2.getString("cont_telefone"),
                                        rst2.getString("cont_celular"),
                                        TipoContato.COMERCIAL,
                                        emailCont
                                );

                                if ((rst2.getString("cont_telefone2") != null)
                                        && (!rst2.getString("cont_telefone2").trim().isEmpty())) {
                                    imp.addTelefone(
                                            rst2.getString("contato"),
                                            rst2.getString("cont_telefone2")
                                    );
                                }
                            }
                        }
                    }

                    try (Statement stm3 = ConexaoOracle.getConexao().createStatement()) {
                        try (ResultSet rst3 = stm3.executeQuery(
                                "select "
                                + " f.id,\n"
                                + " f.frequencia prazovisita,\n"
                                + " f.entrega prazoentrega,\n"
                                + " fl.id as iddivisao,\n"
                                + " fl.descritivo as descdivisao,\n"
                                + " fl.condpagto as condpgtodivisao,\n"
                                + " fl.dias_vencto as diasvencdivisao,\n"
                                + " fl.prazo_entrega as prazoentregadivisao\n"
                                + "from\n"
                                + "    fornecedores f\n"
                                + "left join fornecedores_linhas fl on fl.fornecedor = f.id\n "
                                + "where f.id = " + imp.getImportId()
                        )) {
                            while (rst3.next()) {
                                imp.addDivisao(
                                        rst3.getString("iddivisao") + "-" + rst.getString("id"),
                                        rst3.getInt("prazovisita"),
                                        rst3.getInt("prazoentrega"),
                                        0
                                );
                            }
                        }
                    }

                    result.add(imp);
                }
            }
            if (importarDeTransportadoras) {
                System.out.println("Importando transportadores");
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "  n.id,\n"
                        + "  n.descritivo razao,\n"
                        + "  n.fantasia,\n"
                        + "  n.cnpj_cpf,\n"
                        + "  n.inscricao_rg,\n"
                        + "  n.logradouro,\n"
                        + "  n.endereco,\n"
                        + "  n.numero,\n"
                        + "  n.complemento,\n"
                        + "  n.bairro,\n"
                        + "  n.cidade,\n"
                        + "  n.cod_ibge cidadeibge,\n"
                        + "  n.estado,\n"
                        + "  n.cep,\n"
                        + "  n.telefone1,\n"
                        + "  n.telefone2,\n"
                        + "  n.datahora_cadastro,\n"
                        + "  n.observacao,\n"
                        + "  n.email,\n"
                        + "  n.site,\n"
                        + "  n.fax\n"
                        + "from\n"
                        + "  transportadoras n\n"
                        + "order by\n"
                        + "  n.id"
                )) {
                    while (rst.next()) {
                        FornecedorIMP imp = new FornecedorIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId("TRANSP - " + rst.getString("id"));
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                        imp.setIe_rg(rst.getString("inscricao_rg"));
                        imp.setEndereco((rst.getString("logradouro") + " " + rst.getString("endereco")).trim());
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setIbge_municipio(rst.getInt("cidadeibge"));
                        imp.setUf(rst.getString("estado"));
                        imp.setCep(rst.getString("cep"));
                        imp.setTel_principal(rst.getString("telefone1"));
                        imp.addTelefone("FONE2", rst.getString("telefone2"));
                        imp.setDatacadastro(rst.getDate("datahora_cadastro"));
                        imp.setObservacao(rst.getString("observacao"));
                        imp.addEmail("EMAIL", rst.getString("email"), TipoContato.COMERCIAL);
                        imp.addTelefone("FAX", rst.getString("fax"));

                        result.add(imp);
                    }
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

                ProgressBar.setStatus("Executando a query (Icms Fornecedor)...");

                int cont = 1;
                try (ResultSet rs = stm.executeQuery(
                        "SELECT \n"
                        + "    distinct \n"
                        + "    tf.produto,  \n"
                        + "    tf.fornecedor,  \n"
                        + "    forn.estado,  \n"
                        + "    case  \n"
                        + "     when tf.tributacao_compra = 'T' and tf.icms_compra = 18 and tf.reducao_compra = 0 then 2 \n"
                        + "     when tf.tributacao_compra = 'R' and tf.icms_compra = 18 and tf.reducao_compra = 33.33 then 9 \n"
                        + "     when tf.tributacao_compra = 'I' and tf.icms_compra = 0 and tf.reducao_compra = 0 then 6 \n"
                        + "     when tf.tributacao_compra = 'T' and tf.icms_compra = 2.58 and tf.reducao_compra = 0 then 20 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 0 and tf.reducao_compra = 0 then 7 \n"
                        + "     when tf.tributacao_compra = 'N' and tf.icms_compra = 0 and tf.reducao_compra = 0 then  17 \n"
                        + "     when tf.tributacao_compra = 'T' and tf.icms_compra = 2.58 and tf.reducao_compra = 0 then 20  \n"
                        + "     when tf.tributacao_compra = 'O' and tf.icms_compra = 2.58 and tf.reducao_compra = 0 then 8 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 0 then 1 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 33.33 then 21 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 41.66 then 22 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 61.11 then 23 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 12 and tf.reducao_compra = 43.23 then 24 \n"
                        + "     when tf.tributacao_compra = 'T' and tf.icms_compra = 12 and tf.reducao_compra = 0 then 1\n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 4 and tf.reducao_compra = 0 then 25 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 4 and tf.reducao_compra = 0 then 26\n"
                        + "     when tf.tributacao_compra = 'T' and tf.icms_compra = 4 and tf.reducao_compra = 0 then 25 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 18 and tf.reducao_compra = 43.23 then 27 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 18 and tf.reducao_compra = 54.81 then 28 \n"
                        + "     when tf.tributacao_compra = 'R' and tf.icms_compra = 18 and tf.reducao_compra = 61.11 then 4 \n"
                        + "     when tf.tributacao_compra = 'T' and tf.icms_compra = 25 and tf.reducao_compra = 0 then 3 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 18 and tf.reducao_compra = 85.98 then 29 \n"
                        + "     when tf.tributacao_compra = 'F' and tf.icms_compra = 18 and tf.reducao_compra = 57 then 30 \n"
                        + "    else  \n"
                        + "        8 \n"
                        + "    end as icms_credito,  \n"
                        + "    tf.icms_compra,  \n"
                        + "    tf.reducao_compra, \n"
                        + "    tf.st_compra, \n"
                        + "    tf.pauta \n"
                        + "FROM \n"
                        + "    produtos a \n"
                        + "    join empresas emp on emp.id = 1 \n"
                        + "    join produtos_estado pe on a.id = pe.id and pe.estado = emp.estado  \n"
                        + "    join politicas_empresa poli on poli.empresa = emp.id \n"
                        + "    join produtos_precos preco on a.id = preco.produto and poli.politica = preco.politica and preco.id = 1  \n"
                        + "    join produtos_loja loja on a.id = loja.id and poli.politica = loja.politica \n"
                        + "    join estoques e on e.empresa = emp.id and e.troca != 'T' \n"
                        + "    join produtos_estoques estoq on estoq.produto = a.id and estoq.estoque = e.id \n"
                        + "    left join produtos_ean ean on ean.produto = a.id \n"
                        + "    left join (select distinct id from vw_produtos_balancas order by id) bal on bal.id = a.id \n"
                        + "    left join familias fam on a.familia = fam.id \n"
                        + "    join tabela_fornecedor_uf tf on a.id = tf.produto \n"
                        + "    join fornecedores forn on tf.fornecedor = forn.id and \n"
                        + "    tf.datahora_alteracao in (select  \n"
                        + "                                max(t.datahora_alteracao)  \n"
                        + "                              from  \n"
                        + "                                tabela_fornecedor_uf t  \n"
                        + "                              where  \n"
                        + "                                t.produto = tf.produto) and\n"
                        + "forn.estado != 'SP'\n"
                        + "order by \n"
                        + "    tf.produto")) {
                    while (rs.next()) {

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("produto"));
                        imp.setIcmsCreditoId(rs.getString("icms_credito"));
                        imp.setIcmsCstEntrada(rs.getInt("st_compra"));
                        imp.setIcmsReducaoEntrada(rs.getDouble("reducao_compra"));
                        imp.setIcmsAliqEntrada(rs.getDouble("icms_compra"));
                        imp.setUf(rs.getString("estado"));

                        ProgressBar.setStatus("Convertendo ICMS Fornecedor em IMP....");
                        cont++;

                        result.add(imp);
                    }
                }
                return result;
            }
        } else if (opc == OpcaoProduto.TIPO_EMBALAGEM_PRODUTO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "SELECT\n"
                        + "    a.id,\n"
                        + "    coalesce(ean.ean, cast(a.id as varchar(13))) codigobarras,\n"
                        + "    coalesce(ean.qtdee, 1) qtdembalagem,\n"
                        + "    a.unidade_venda unidade,\n"
                        + "    a.qtde_embalageme qtdembalagem_compra,\n"
                        + "    a.unidade_compra\n"
                        + "FROM\n"
                        + "    produtos a\n"
                        + "    join empresas emp on emp.id = " + getLojaOrigem() + "\n"
                        + "    join produtos_estado pe on a.id = pe.id and pe.estado = emp.estado\n"
                        + "    join politicas_empresa poli on poli.empresa = emp.id\n"
                        + "    join produtos_precos preco on a.id = preco.produto and poli.politica = preco.politica and preco.id = " + tipoVenda + "\n"
                        + "    join produtos_loja loja on a.id = loja.id and poli.politica = loja.politica\n"
                        + "    join estoques e on e.empresa = emp.id and e.troca != 'T'\n"
                        + "    join produtos_estoques estoq on estoq.produto = a.id and estoq.estoque = e.id\n"
                        + "    left join produtos_ean ean on ean.produto = a.id\n"
                        + "    left join (select distinct id from vw_produtos_balancas order by id) bal on bal.id = a.id\n"
                        + "    left join familias fam on a.familia = fam.id\n"
                        + "order by\n"
                        + "    a.id")) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("id"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));

                        result.add(imp);
                    }
                }

                return result;
            }
        } else if (opc == OpcaoProduto.TROCA) {
            ProgressBar.setStatus("Carregando produtos (TROCA)...");
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "SELECT\n"
                        + "    a.id,\n"
                        + "    a.unidade_venda unidade,\n"
                        + "    a.descritivo descricaocompleta,\n"
                        + "    estoq.estoque_atual troca\n"
                        + "FROM \n"
                        + "    produtos a\n"
                        + "    join empresas emp on emp.id = " + getLojaOrigem() + "\n"
                        + "    join produtos_estado pe on a.id = pe.id and pe.estado = emp.estado\n"
                        + "    join politicas_empresa poli on poli.empresa = emp.id\n"
                        + "    join produtos_loja loja on a.id = loja.id and poli.politica = loja.politica\n"
                        + "    join estoques e on e.empresa = emp.id and e.troca != 'T'\n"
                        + "    join produtos_estoques estoq on estoq.produto = a.id and estoq.estoque = e.id and e.id = " + idEstoque + "\n"
                        + "order by\n"
                        + "    a.id")) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("id"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setTroca(rs.getDouble("troca"));

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
                    + "	tf.fornecedor,\n"
                    + "	tf.produto,\n"
                    + "	pf.referencia,\n"
                    + "	tf.datahora_alteracao,\n"
                    + "	tf.qtde_embalageme qtdembalagem\n"
                    + "from\n"
                    + "	tabela_fornecedor tf \n"
                    + "	join produtos_fornecedor pf on tf.produto = pf.produto and tf.fornecedor = pf.fornecedor\n"
                    + "where\n"
                    + "    not pf.referencia is null\n"
                    + "order by\n"
                    + "	tf.fornecedor,\n"
                    + "	tf.produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    imp.setDataAlteracao(rst.getDate("datahora_alteracao"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores(OpcaoProdutoFornecedor opc) throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        if (opc == OpcaoProdutoFornecedor.IPI) {
            try (Statement stm = ConexaoOracle.createStatement()) {
                ProgressBar.setStatus("Convertendo Produto Fornecedor em IMP...(IPI Fornecedor)");
                try (ResultSet rs = stm.executeQuery(
                        "SELECT \n"
                        + "    distinct \n"
                        + "    tf.produto,  \n"
                        + "    tf.fornecedor,  \n"
                        + "    tf.ipi,\n"
                        + "    case \n"
                        + "     when tf.ipi_tipo = 'P' then 0\n"
                        + "    else 1\n"
                        + "    end as ipi_tipo,\n"
                        + "    tf.qtde_embalageme,\n"
                        + "    tf.unidade_compra\n"
                        + "FROM \n"
                        + "    produtos a \n"
                        + "    join empresas emp on emp.id = " + getLojaOrigem() + "\n"
                        + "    join produtos_estado pe on a.id = pe.id and pe.estado = emp.estado  \n"
                        + "    join politicas_empresa poli on poli.empresa = emp.id \n"
                        + "    join produtos_precos preco on a.id = preco.produto and poli.politica = preco.politica and preco.id = 1\n"
                        + "    join produtos_loja loja on a.id = loja.id and poli.politica = loja.politica \n"
                        + "    join tabela_fornecedor tf on a.id = tf.produto and \n"
                        + "    tf.datahora_alteracao in (select  \n"
                        + "                                max(t.datahora_alteracao)  \n"
                        + "                              from  \n"
                        + "                                tabela_fornecedor t  \n"
                        + "                              where  \n"
                        + "                                t.produto = tf.produto) and\n"
                        + "    tf.ipi != 0\n"
                        + "order by \n"
                        + "    tf.produto")) {
                    while (rs.next()) {
                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setIdProduto(rs.getString("produto"));
                        imp.setIdFornecedor(rs.getString("fornecedor"));
                        imp.setIpi(rs.getDouble("ipi"));
                        imp.setTipoIpi(rs.getInt("ipi_tipo"));

                        result.add(imp);
                    }
                }
            }
            return result;
        } else if (opc == OpcaoProdutoFornecedor.QTDEMBALAGEM) {
            try (Statement stm = ConexaoOracle.createStatement()) {
                ProgressBar.setStatus("Convertendo Produto Fornecedor em IMP...(Qtd. Embalagem)");
                try (ResultSet rs = stm.executeQuery(
                        "SELECT \n"
                        + "    distinct \n"
                        + "    tf.produto,  \n"
                        + "    tf.fornecedor, \n"
                        + "    tf.qtde_embalageme,\n"
                        + "    tf.unidade_compra\n"
                        + "FROM \n"
                        + "    produtos a \n"
                        + "    join tabela_fornecedor tf on a.id = tf.produto and \n"
                        + "    tf.datahora_alteracao in (select  \n"
                        + "                                max(t.datahora_alteracao)  \n"
                        + "                              from  \n"
                        + "                                tabela_fornecedor t  \n"
                        + "                              where  \n"
                        + "                                t.produto = tf.produto)\n"
                        + "order by \n"
                        + "    tf.produto")) {
                    while (rs.next()) {
                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setIdProduto(rs.getString("produto"));
                        imp.setQtdEmbalagem(rs.getDouble("qtde_embalageme"));
                        imp.setIdFornecedor(rs.getString("fornecedor"));

                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        LOG.config("Carregar clientes de : ["
                + (importarDeClientes ? " Clientes " : "")
                + (importarDeEmpresas ? " Empresas " : "")
                + (importarDeFornecedores ? " Fornecedores " : "")
                + (importarDeTransportadoras ? " Transportadoras " : "")
                + (importarDeAdminCartao ? " Administradora de Cartões " : "")
                + "]"
        );

        try (Statement stm = ConexaoOracle.createStatement()) {
            if (importarDeClientes) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH LIM AS\n" +
                        "(SELECT\n" +
                        "	cliente,\n" +
                        "	sum(limite) somalimite\n" +
                        "FROM clientes_limites\n" +
                        "GROUP BY cliente ORDER BY 1)\n" +
                        "SELECT\n" +
                        "	c.id,\n" +
                        "	c.cnpj_cpf,\n" +
                        "	c.inscricao_rg,\n" +
                        "	c.orgao_publico,\n" +
                        "	c.datahora_cadastro datacadastro,\n" +
                        "	c.descritivo razao,\n" +
                        "	c.fantasia,\n" +
                        "	c.situacao AS bloqueado,\n" +
                        "	c.endereco,\n" +
                        "	c.numero,\n" +
                        "	c.complemento,\n" +
                        "	c.bairro,\n" +
                        "	c.cidade,\n" +
                        "	c.estado,\n" +
                        "	c.cod_ibge municipio_ibge,\n" +
                        "	c.cep,\n" +
                        "	c.estado_civil,\n" +
                        "	c.data_nascimento,\n" +
                        "	CASE c.sexo WHEN 1 THEN 0 ELSE 1 END sexo,\n" +
                        "	c.empresacad,\n" +
                        "	c.telefoneemp,\n" +
                        "	c.data_admissao,\n" +
                        "	c.cargo,\n" +
                        "	c.salario,\n" +
                        "	COALESCE (somalimite,0) limite,\n" +
                        "	c.conjugue,\n" +
                        "	c.pai,\n" +
                        "	c.mae,\n" +
                        "	c.observacao,\n" +
                        "	c.dias_vencto,\n" +
                        "	c.telefone1,\n" +
                        "	c.telefone2,\n" +
                        "	c.email,\n" +
                        "	c.fax,\n" +
                        "	c.telefone_cobranca,\n" +
                        "	c.endereco_c,\n" +
                        "	c.numero_c,\n" +
                        "	c.complemento_c,\n" +
                        "	c.bairro_c,\n" +
                        "	c.cidade_c,\n" +
                        "	c.estado_c,\n" +
                        "	c.cep_c,\n" +
                        "	c.inscricao_municipal,\n" +
                        "	decode(c.empresa_convenio, '', 3, c.empresa_convenio) AS empresa_convenio,\n" +
                        "CASE\n" +
                        "		c.estado_civil WHEN 0 THEN 'SOLTEIRO'\n" +
                        "		WHEN 1 THEN 'CASADO'\n" +
                        "		WHEN 2 THEN 'DIVORCIADO'\n" +
                        "		WHEN 3 THEN 'VIUVO'\n" +
                        "		WHEN 4 THEN 'AMASIADO'\n" +
                        "	END estadocivil\n" +
                        "FROM\n" +
                        "	clientes c\n" +
                        "	LEFT JOIN LIM ON LIM.CLIENTE = C.ID\n" +
                        "WHERE\n" +
                        "	upper(c.descritivo) != 'CADASTRO AUTOMATICO'\n" +
                        "ORDER BY\n" +
                        "	ID"
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
                        imp.setEstadoCivil(rst.getString("estadocivil"));

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
                    imp.setDataInicio(rst.getDate("datahora_cadastro"));
                    imp.setDataTermino(Utils.getDataAtual());

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<NotaFiscalIMP> getNotasFiscais() throws Exception {
        List<NotaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    nfe.id_c100 id,\n"
                    + "    nfe.ind_oper notaoperacao,\n"
                    + "    nfe.nf_complementar_a e_complementar, --tiponota\n"
                    + "    coalesce(nfe.cod_part,'') participante,\n"
                    + "    nfe.cod_mod modelo,\n"
                    + "    nfe.ser serie,\n"
                    + "    nfe.num_doc numeronota,\n"
                    + "    nfe.dt_doc dataemissao,\n"
                    + "    nfe.dt_e_s dataentrasasaida,\n"
                    + "    nfe.vl_ipi,\n"
                    + "    nfe.vl_frt vl_frete,\n"
                    + "    nfe.vl_out_da vl_outrasdespesas,\n"
                    + "    nfe.vl_merc vl_produto,\n"
                    + "    nfe.vl_doc vl_total,\n"
                    + "    nfe.vl_bc_icms,\n"
                    + "    nfe.vl_icms,\n"
                    + "    nfe.vl_bc_icms_st,\n"
                    + "    nfe.vl_icms_st,\n"
                    + "    nfe.vl_ipi,\n"
                    + "    nfe.vl_seg vl_seguro,\n"
                    + "    nfe.vl_desc vl_desconto,\n"
                    + "    nfe.ind_frt ind_tipofrete,\n"
                    + "    nfe.observacao_a info_complementar,\n"
                    + "    nfe.nota_impressa_a impressa,\n"
                    + "    nfe.cod_sit situacaonfe,\n"
                    + "    nfe.chv_nfe\n"
                    + "from\n"
                    + "    arius.fis_t_c100 nfe\n"
                    + "where\n"
                    + "    nfe.id_empresa_a = " + getLojaOrigem() + " and\n"
                    + "    nfe.cod_part like 'F%' and\n"
                    + "    not nfe.cod_part is null and\n"
                    + "    nfe.dt_doc between '"
                    + DATE_FORMAT.format(notasDataInicio)
                    + "' and '"
                    + DATE_FORMAT.format(notasDataTermino) + "'\n"
                    + "order by\n"
                    + "    nfe.id_c100"
            )) {
                while (rs.next()) {

                    NotaFiscalIMP imp = new NotaFiscalIMP();
                    imp.setId(rs.getString("id"));
                    imp.setOperacao(NotaOperacao.get(rs.getInt("notaoperacao")));
                    if ("T".equals(rs.getString("e_complementar"))) {
                        imp.setTipoNota(TipoNota.COMPLEMENTO);
                    } else {
                        imp.setTipoNota(TipoNota.NORMAL);
                    }
                    {
                        String str = rs.getString("participante");
                        char tipoParticipante = str.charAt(0);
                        String idParticipante = str.substring(1, str.length());

                        if (tipoParticipante == 'F') {
                            imp.setTipoDestinatario(TipoDestinatario.FORNECEDOR);
                        } else {
                            imp.setTipoDestinatario(TipoDestinatario.CLIENTE_EVENTUAL);
                        }
                        if (idParticipante.equals("34695")
                                || idParticipante.equals("34695")) {
                            System.out.println(tipoParticipante + " - " + idParticipante);
                        }
                        imp.setIdDestinatario(idParticipante);
                    }
                    imp.setModelo(rs.getString("modelo"));
                    imp.setSerie(rs.getString("serie"));
                    imp.setNumeroNota(rs.getInt("numeronota"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntradaSaida(rs.getDate("dataentrasasaida"));
                    imp.setValorIpi(rs.getDouble("vl_ipi"));
                    imp.setValorFrete(rs.getDouble("vl_frete"));
                    //imp.setValorOutrasDespesas(rs.getDouble("vl_outrasdespesas"));
                    imp.setValorProduto(rs.getDouble("vl_produto"));
                    imp.setValorTotal(rs.getDouble("vl_total"));
                    imp.setValorIcms(rs.getDouble("vl_icms"));
                    imp.setValorIcmsSubstituicao(rs.getDouble("vl_icms_st"));
                    imp.setValorSeguro(rs.getDouble("vl_seguro"));
                    imp.setValorDesconto(rs.getDouble("vl_desconto"));
                    imp.setTipoFreteNotaFiscal(TipoFreteNotaFiscal.get(rs.getInt("ind_tipofrete")));
                    imp.setInformacaoComplementar(rs.getString("info_complementar"));
                    imp.setImpressao("T".equals(rs.getString("impressa")));
                    imp.setSituacaoNfe(SituacaoNfe.getByCodigo(rs.getInt("situacaonfe")));
                    imp.setDataHoraAlteracao(rs.getDate("dataentrasasaida"));
                    imp.setChaveNfe(rs.getString("chv_nfe"));

                    getNotasItem(imp);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private void getNotasItem(NotaFiscalIMP imp) throws Exception {

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    i.id_c170 id,\n"
                    + "    i.id_c100 id_notafiscal,\n"
                    + "    i.num_item numeroitem,\n"
                    + "    i.cod_item id_produto,\n"
                    + "    p.classificacao_fiscal ncm,\n"
                    + "    i.cest_a cest,\n"
                    + "    i.cfop,\n"
                    + "    i.descr_compl descricao,\n"
                    + "    i.unid unidade,\n"
                    + "    1 as qtd_embalagem,\n"
                    + "    i.qtd quantidade,\n"
                    + "    i.vl_item,\n"
                    + "    i.vl_desc,\n"
                    + "    i.vl_isentas_a vl_isento,\n"
                    + "    i.vl_outras_a vl_outras,\n"
                    + "    \n"
                    + "    i.cst_icms,\n"
                    + "    i.aliq_icms,\n"
                    + "    i.vl_red_bc_a,\n"
                    + "    i.vl_bc_icms,\n"
                    + "    i.vl_icms,\n"
                    + "    i.vl_bc_icms_st,\n"
                    + "    i.vl_icms_st,\n"
                    + "    \n"
                    + "    i.vl_bc_ipi,\n"
                    + "    i.vl_ipi_a,\n"
                    + "    \n"
                    + "    i.cst_pis,\n"
                    + "    i.vl_pis,\n"
                    + "    i.nat_rec,\n"
                    + "    \n"
                    + "    i.iva_valor_a,\n"
                    + "    i.cst_icms_a,\n"
                    + "    i.aliq_icms_a\n"
                    + "from\n"
                    + "    arius.fis_t_c170 i\n"
                    + "    join produtos p on\n"
                    + "        i.cod_item = p.id\n"
                    + "where\n"
                    + "    i.id_c100 = " + imp.getId() + "\n"
                    + "order by\n"
                    + "    i.id_c170"
            )) {
                while (rs.next()) {
                    NotaFiscalItemIMP item = imp.addItem();

                    item.setId(rs.getString("id"));
                    item.setNumeroItem(rs.getInt("numeroitem"));
                    item.setIdProduto(rs.getString("id_produto"));
                    item.setNcm(rs.getString("ncm"));
                    item.setCest(rs.getString("cest"));
                    item.setCfop(rs.getString("cfop"));
                    item.setDescricao(rs.getString("descricao"));
                    item.setUnidade(rs.getString("unidade"));
                    //item.setEan(rs.getString("ean"));
                    //item.setQuantidadeEmbalagem(rs.getInt("quantidadeembalagem"));
                    item.setQuantidade(rs.getDouble("quantidade"));
                    item.setValorTotalProduto(rs.getDouble("vl_item"));
                    item.setValorDesconto(rs.getDouble("vl_desc"));
                    item.setValorIsento(rs.getDouble("vl_isento"));
                    //item.setValorOutras(rs.getDouble("vl_outras"));
                    item.setIcmsCst(rs.getInt("cst_icms"));
                    item.setIcmsAliquota(rs.getDouble("aliq_icms"));
                    item.setIcmsReduzido(rs.getDouble("vl_red_bc_a"));
                    item.setIcmsBaseCalculo(rs.getDouble("vl_bc_icms"));
                    item.setIcmsValor(rs.getDouble("vl_icms"));
                    item.setIcmsBaseCalculoST(rs.getDouble("vl_bc_icms_st"));
                    item.setIcmsValorST(rs.getDouble("vl_icms_st"));
                    item.setIpiValorBase(rs.getDouble("vl_bc_ipi"));
                    item.setIpiValor(rs.getDouble("vl_ipi_a"));
                    item.setPisCofinsCst(rs.getInt("cst_pis"));
                    item.setTipoNaturezaReceita(rs.getInt("nat_rec"));
                }
            }
        }
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.id id_cliente,\n"
                    + "    c.descritivo nome,\n"
                    + "    c.datahora_cadastro,\n"
                    + "    c.logradouro,\n"
                    + "    c.endereco,\n"
                    + "    c.numero,\n"
                    + "    c.complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade,\n"
                    + "    c.estado,\n"
                    + "    c.cep,\n"
                    + "    c.telefone1,\n"
                    + "    c.telefone2,\n"
                    + "    c.observacao,\n"
                    + "    c.cnpj_cpf,\n"
                    + "    c.inscricao_rg,\n"
                    + "    cc.id id_empresa,\n"
                    + "    cc.descritivo nome_empresa,\n"
                    + "    cc.bloqueado,\n"
                    + "    c.limite,\n"
                    + "    c.situacao\n"
                    + "from\n"
                    + "    clientes c,\n"
                    + "    conveniadas cc\n"
                    + "where\n"
                    + "    c.empresa_convenio = cc.id\n"
                    + "order by\n"
                    + "    c.id"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rs.getString("id_cliente"));
                    imp.setNome(rs.getString("nome"));
                    imp.setIdEmpresa(rs.getString("id_empresa"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rs.getInt("situacao") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);

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

    public void setEstoque(int idEstoque) {
        this.idEstoque = idEstoque;
    }

    public List<ItemComboVO> getEstoques() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descritivo from estoques order by id"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("descritivo")));
                }
            }
        }

        return result;
    }

    private Date dataVencimentoContaPagar = new Date();

    public void setDataVencimentoContaPagar(Date dataVencimentoContaPagar) {
        this.dataVencimentoContaPagar = dataVencimentoContaPagar;
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
                    + " cl.id id_cliente,\n"
                    + "	to_char(c.vencimento,'dd/MM/yyyy') vencimento,\n"
                    + "	c.parcela,\n"
                    + "	c.juros,\n"
                    + "	c.cpf_cnpj\n"
                    + "from\n"
                    + "	vw_contas c\n"
                    + "left join\n"
                    + " clientes cl on cast(c.cpf_cnpj as numeric) = cast(cl.cnpj_cpf as numeric)\n"
                    + "where\n"
                    + "	empresa = " + getLojaOrigem() + "\n"
                    + "	and parcela <> 0\n"
                    + "	and tipo_conta = 1\n"
                    + "	and pagamento is null\n"
                    + "	and not tipo_cadastro is null\n"
                    + " and cl.id is not null\n"
                    + (naoUtilizaPlanoConta == true ? "\n" : " and plano_conta in (" + getPlanosContaStr() + ")\n")
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
                    imp.setIdCliente(rst.getString("id_cliente"));
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
                    + "from  \n"
                    + "        vw_contas c \n"
                    + "join clientes cl on (c.id_cadastro = cl.id) \n"
                    + "        and empresa = " + getLojaOrigem() + " \n"
                    + "        and parcela <> 0  \n"
                    + "        and tipo_conta = 1 \n"
                    + "        and pagamento is null  \n"
                    + "        and not tipo_cadastro is null  \n"
                    + "        and plano_conta in (" + getPlanosContaStr() + ") \n"
                    + "order by emissao desc";

            LOG.fine("SQL a ser executado:\n" + sql);

            try (ResultSet rst = stm.executeQuery(sql)) {

                while (rst.next()) {

                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id_cadastro"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("nf"));
                    imp.setEcf(rst.getString("pdv"));
                    imp.setValor(rst.getDouble("liquido"));
                    imp.setNumeroCheque(rst.getString("cheque"));
                    imp.setBanco(rst.getInt("banco_cheque"));
                    imp.setAgencia(rst.getString("agencia"));
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
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    id,\n"
                    + "    id_cadastro id_fornecedor,\n"
                    + "    nf numerodocumento,\n"
                    + "    emissao dataemissao,\n"
                    + "    datahora_cadastro dataentrada,\n"
                    + "    datahora_alteracao dataalteracao,\n"
                    + "    liquido valor,\n"
                    + "    observacao,\n"
                    + "    vencimento   \n"
                    + "from\n"
                    + "    vw_contas\n"
                    + "where\n"
                    + "    empresa = " + getLojaOrigem() + " and\n"
                    + "    tipo_conta = 0 and \n"
                    + "    parcela <> 0 and\n"
                    + "    not tipo_cadastro is null and\n"
                    + "    pagamento is null and\n"
                    //+ "    trunc(vencimento) <= '" + new SimpleDateFormat("dd/MM/yyyy").format(dataVencimentoContaPagar) + "' and\n"
                    + "    id_cadastro is not null\n"
                    + "order by\n"
                    + "    vencimento"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataentrada"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataalteracao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setVencimento(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*@Override
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
     + "    case \n"
     + "     when c.descritivo = 'CADASTRO AUTOMATICO' then null\n"
     + "     else v.id_cliente end as idclientepreferencial, \n"
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

     }*/
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), vendaDataInicio, vendaDataTermino);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), vendaDataInicio, vendaDataTermino);
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

    /*@Override
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

        
     //try {
     //return new VendaItemIterator(getLojaOrigem(), vendaDataInicio, vendaDataTermino);
     //} catch (Exception ex) {        
     // LOG.log(Level.SEVERE, "Erro\n", ex);
     // throw ex;
     //}
         
     }*/
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

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "        distinct\n"
                    + "        p.classificacao_fiscal,\n"
                    + "        pe.estado,\n"
                    + "        pe.tributacao_venda,\n"
                    + "        pe.icms_venda,\n"
                    + "        pe.reducao_venda,\n"
                    + "        pe.st_venda,\n"
                    + "        pe.iva,\n"
                    + "        pe.tipo_iva,\n"
                    + "        01 as p_iva\n"
                    + "from \n"
                    + "        produtos_estado pe\n"
                    + "join    produtos p on pe.id = p.id\n"
                    + "join    empresas em on pe.estado = em.estado\n"
                    + "join    politicas_empresa po on em.id = po.empresa\n"
                    + "left join tabela_fornecedor_uf tf on tf.produto = pe.id\n"
                    + "join    produtos_loja loja on p.id = loja.id and\n"
                    + "        po.politica = loja.politica and\n"
                    + "        loja.id = pe.id and\n"
                    + "        po.politica = 1 and\n"
                    + "        pe.iva != 0 and\n"
                    + "        em.id = " + getLojaOrigem() + " and\n"
                    + "        p.status = 0\n"
                    + "order by classificacao_fiscal")) {
                while (rs.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    imp.setId(formatPautaFiscalId(
                            rs.getString("estado"),
                            rs.getString("classificacao_fiscal"),
                            rs.getDouble("p_iva"),
                            rs.getDouble("iva"),
                            rs.getInt("st_venda"),
                            rs.getInt("st_venda")
                    ));
                    imp.setNcm(rs.getString("classificacao_fiscal"));
                    imp.setUf(rs.getString("estado"));
                    if ("P".equals(rs.getString("tipo_iva"))) {
                        imp.setTipoIva(TipoIva.PERCENTUAL);
                        imp.setIva(rs.getDouble("iva"));
                    } else {
                        imp.setTipoIva(TipoIva.VALOR);
                        imp.setIva(rs.getDouble("iva"));
                    }
                    if (rs.getInt("st_venda") == 60) {
                        imp.setAliquotaCredito(0, rs.getDouble("icms_venda"), 0);
                    } else {
                        imp.setAliquotaCredito(rs.getInt("st_venda"), rs.getDouble("icms_venda"), rs.getDouble("reducao_venda"));
                    }
                    if (rs.getInt("st_venda") == 60) {
                        imp.setAliquotaDebito(0, rs.getDouble("icms_venda"), 0);
                    } else {
                        imp.setAliquotaDebito(rs.getInt("st_venda"), rs.getDouble("icms_venda"), rs.getDouble("reducao_venda"));
                    }
                    if (rs.getInt("st_venda") == 60) {
                        imp.setAliquotaCreditoForaEstado(0, rs.getDouble("icms_venda"), 0);
                    } else {
                        imp.setAliquotaCreditoForaEstado(rs.getInt("st_venda"), rs.getDouble("icms_venda"), rs.getDouble("reducao_venda"));
                    }
                    if (rs.getInt("st_venda") == 60) {
                        imp.setAliquotaDebitoForaEstado(0, rs.getDouble("icms_venda"), 0);
                    } else {
                        imp.setAliquotaDebitoForaEstado(rs.getInt("st_venda"), rs.getDouble("icms_venda"), rs.getDouble("reducao_venda"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private String formatPautaFiscalId(String uf, String ncm, double p_iva, double v_iva, int idIcmsSaida, int idIcmsEntrada) {
        return String.format("%s-%s-%.2f-%.2f-%d-%d", uf, ncm, p_iva, v_iva, idIcmsSaida, idIcmsEntrada);
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  n.id,\n"
                    + "  n.descritivo,\n"
                    + "  1 as id_situacaocadastro,\n"
                    + "  n.valor_calorico caloria,\n"
                    + "  n.carboidratos,\n"
                    + "  case when n.med_carboidrato = 'T' then 1 else 0 end as carboidrato_inferior,\n"
                    + "  n.proteina,\n"
                    + "  case when n.med_proteina = 'T' then 1 else 0 end as proteina_inferior,\n"
                    + "  n.gorduras,\n"
                    + "  n.gorduras_saturada,\n"
                    + "  n.colesterol,\n"
                    + "  n.fibra_alimentar,\n"
                    + "  case when med_fibra = 'T' then 1 else 0 end as fibra_inferior,\n"
                    + "  n.calcio,\n"
                    + "  n.ferro,\n"
                    + "  n.sodio,\n"
                    + "  n.quantidade porcao,\n"
                    + "  n.obs mensagemalergico\n,"
                    + "  p.receita as mensagemalergico2\n"
                    + "from\n"
                    + "  nutricional n\n"
                    + "left join produtos p on p.id = n.id\n"
                    + "order by\n"
                    + "  n.id"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    imp.setCaloria(rst.getInt("caloria"));
                    imp.setCarboidrato(rst.getDouble("carboidratos"));
                    imp.setCarboidratoInferior(rst.getBoolean("carboidrato_inferior"));
                    imp.setProteina(rst.getDouble("proteina"));
                    imp.setProteinaInferior(rst.getBoolean("proteina_inferior"));
                    imp.setGordura(rst.getDouble("gorduras"));
                    imp.setGorduraSaturada(rst.getDouble("gorduras_saturada"));
                    imp.setFibra(rst.getDouble("fibra_alimentar"));
                    imp.setFibraInferior(rst.getBoolean("fibra_inferior"));
                    imp.setCalcio(rst.getDouble("calcio"));
                    imp.setFerro(rst.getDouble("ferro"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPorcao(rst.getString("porcao"));
                    imp.getMensagemAlergico().add(rst.getString("mensagemalergico2"));

                    imp.addProduto(rst.getString("id"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private static class FamiliaFornecedorIMP {

        private String id;
        private String descricao;
        private int codigoAtual;

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
            this.descricao = Utils.acertarTexto(descricao, 40);
        }

        public int getCodigoAtual() {
            return codigoAtual;
        }

        public void setCodigoAtual(int codigoAtual) {
            this.codigoAtual = codigoAtual;
        }

    }

    public void importarFamiliaFornecedor() throws Exception {
        Map<String, FamiliaFornecedorIMP> result = new LinkedHashMap<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descritivo from fornecedores where id in\n"
                    + "(select distinct associado from fornecedores f where not associado is null and id != associado)\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    FamiliaFornecedorIMP imp = new FamiliaFornecedorIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    result.put(imp.getId(), imp);
                }
            }
        }

        ProgressBar.setStatus("Importando família de fornecedores....");
        ProgressBar.setMaximum(result.size());

        Conexao.begin();
        try {
            MultiMap<String, FornecedorAnteriorVO> anteriores = new FornecedorAnteriorDAO().getAnteriores();
            for (FamiliaFornecedorIMP imp : result.values()) {
                FornecedorAnteriorVO anterior = anteriores.get(
                        getSistema(),
                        getLojaOrigem(),
                        imp.getId()
                );
                if (anterior != null && anterior.getCodigoAtual() != null) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setSchema("public");
                    sql.setTableName("familiafornecedor");
                    sql.put("id", anterior.getCodigoAtual().getId());
                    sql.put("descricao", imp.getDescricao());
                    sql.put("id_situacaocadastro", 1);
                    Conexao.createStatement().execute(sql.getInsert());
                } else {
                    System.out.println("Família não importada: " + imp.getId() + " - " + imp.getDescricao());
                }
                ProgressBar.next();
            }
            Conexao.createStatement().execute(
                    "select setval('familiafornecedor_id_seq'::regclass, coalesce(max(id),1)) from familiafornecedor"
            );
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void importarFamiliaFornecedorXProduto() throws Exception {
        Map<String, String> result = new LinkedHashMap<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, associado from fornecedores f where not associado is null and id != associado\n"
                    + "union                                                                                               \n"
                    + "select distinct associado id, associado from fornecedores f where not associado is null and id != associado\n"
                    + "order by associado, id"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), rst.getString("associado"));
                }
            }
        }

        ProgressBar.setStatus("Importando Família X Fornecedores....");
        ProgressBar.setMaximum(result.size());

        Conexao.begin();
        try {
            MultiMap<String, FornecedorAnteriorVO> anteriores = new FornecedorAnteriorDAO().getAnteriores();
            for (Map.Entry<String, String> et : result.entrySet()) {
                FornecedorAnteriorVO fornecedor = anteriores.get(getSistema(), getLojaOrigem(), et.getKey());
                FornecedorAnteriorVO familia = anteriores.get(getSistema(), getLojaOrigem(), et.getValue());
                if (fornecedor != null && familia != null && fornecedor.getCodigoAtual() != null && familia.getCodigoAtual() != null) {
                    Conexao.createStatement().execute("update fornecedor set id_familiafornecedor = " + familia.getCodigoAtual().getId() + " where id = " + fornecedor.getCodigoAtual().getId());
                }
                ProgressBar.next();
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " pc.produto_base as produto_pai,\n"
                    + " c.rendimento as qtdproduto_pai,\n"
                    + " pc.produto as produto_filho,\n"
                    + " pc.qtde,\n"
                    + " pc.qtdeemb\n"
                    + "from produtos_composicao pc\n"
                    + "inner join composicao c on c.produto_base = pc.produto_base\n"
                    + "where pc.produto_base in (select id from produtos where composto = 3)\n"
                    + "order by pc.produto_base"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();
                    imp.setImpIdProduto(rst.getString("produto_pai"));
                    imp.setQtdEmbalagem(rst.getInt("qtdproduto_pai"));
                    imp.setImpIdProdutoItem(rst.getString("produto_filho"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtdeemb"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " p.id, \n"
                    + " p.descritivo,\n"
                    + " p.receita\n"
                    + "from produtos p\n"
                    + "where p.composto = 2\n"
                    + "order by p.id"
            )) {
                while (rst.next()) {
                    ReceitaBalancaIMP imp = new ReceitaBalancaIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    imp.setReceita(rst.getString("receita"));
                    imp.getProdutos().add(rst.getString("id"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.id, \n"
                    + "p.descritivo,\n"
                    + "p.receita,\n"
                    + "c.rendimento,\n"
                    + "pc.produto,\n"
                    + "pc.qtde,\n"
                    + "pc.qtdeemb\n"
                    + "from produtos p\n"
                    + "inner join composicao c on c.produto_base = p.id\n"
                    + "inner join produtos_composicao pc on pc.produto_base = p.id\n"
                    + "where p.composto = 2\n"
                    + "order by p.id"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();

                    double qtdEmbUtilizado = 0;
                    qtdEmbUtilizado = rst.getDouble("qtde");

                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rst.getString("id"));
                    imp.setIdproduto(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    imp.setRendimento(rst.getDouble("rendimento"));
                    imp.setQtdembalagemreceita((int) qtdEmbUtilizado);
                    imp.setQtdembalagemproduto(rst.getInt("qtdeemb"));
                    imp.setFator(1);
                    imp.getProdutos().add(rst.getString("produto"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<DivisaoIMP> getDivisoes() throws Exception {
        List<DivisaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  ID, FORNECEDOR, DESCRITIVO \n"
                    + "from fornecedores_linhas order by  fornecedor "
            )) {
                while (rst.next()) {
                    DivisaoIMP imp = new DivisaoIMP();
                    imp.setId(rst.getString("ID") + "-" + rst.getString("FORNECEDOR"));
                    imp.setDescricao(rst.getString("DESCRITIVO"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    public void importarReceberDevolucao(int idLojaVR) throws Exception {
        List<ReceberDevolucaoVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados ReceberDevolucao...");
            vResult = getReceberDevolucao();
            if (!vResult.isEmpty()) {
                new ReceberDevolucaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberDevolucaoVO> getReceberDevolucao() throws Exception {
        List<ReceberDevolucaoVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "ID_TITULO AS ID,\n"
                    + "CPF_CNPJ AS CNPJ,\n"
                    + "NF,\n"
                    + "NOTA,\n"
                    + "EMISSAO,\n"
                    + "VENCIMENTO,\n"
                    + "VALOR,\n"
                    + "PARCELA,\n"
                    + "OBSERVACAO\n"
                    + "from arius.FIN_VI_TITULOS \n"
                    + "where plano_conta = 70110\n"
                    + "and tipo_conta = 1\n"
                    + "and tipo_participante = 'F'\n"
                    + "and id_empresa = " + getLojaOrigem() + "\n"
                    + "and pagamento is null\n"
                    + "order by EMISSAO"
            )) {
                while (rst.next()) {
                    String obs = "";
                    if ((rst.getString("CNPJ") != null)
                            && (!rst.getString("CNPJ").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ"))));
                        if (idFornecedor != -1) {
                            ReceberDevolucaoVO imp = new ReceberDevolucaoVO();
                            imp.setIdFornecedor(idFornecedor);
                            if ((rst.getString("NOTA") != null)
                                    && (!rst.getString("NOTA").trim().isEmpty())) {
                                if (rst.getString("NOTA").trim().length() > 9) {
                                    obs = "NOTA " + rst.getString("NOTA");
                                } else {
                                    imp.setNumeroNota(Integer.parseInt(Utils.formataNumero(rst.getString("NOTA"))));
                                }
                            } else {
                                imp.setNumeroNota(0);
                            }

                            if ((rst.getString("NF") != null)
                                    && (!rst.getString("NF").trim().isEmpty())) {
                                obs = obs + " NF " + rst.getString("NF");
                            }

                            imp.setDataemissao(rst.getDate("EMISSAO"));
                            imp.setDatavencimento(rst.getDate("VENCIMENTO"));
                            imp.setValor(rst.getDouble("VALOR"));
                            imp.setNumeroParcela(rst.getInt("PARCELA"));
                            imp.setObservacao("IMPORTADO VR " + (rst.getString("OBSERVACAO") + obs));
                            vResult.add(imp);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DATA_INICIAL dataInicio,\n"
                    + "	DATA_FINAL dataFim,\n"
                    + "	item.PRODUTO idProduto,\n"
                    + "	PRECO.VENDA precoNormal,\n"
                    + "	ITEM.PRECO precoOferta\n"
                    + "FROM\n"
                    + "	PROG_MIDIA ofer\n"
                    + "JOIN PROG_MIDIA_EMPRESAS emp ON emp.PROG_MIDIA = ofer.ID\n"
                    + "JOIN PROG_MIDIA_ITENS item ON ofer.ID = item.PROG_MIDIA\n"
                    + "JOIN PRODUTOS_PRECOS preco ON item.PRODUTO = preco.PRODUTO\n"
                    + "WHERE\n"
                    + "	emp.CONCLUIDO = 'F'\n"
                    + "     AND emp.EMPRESA = " + getLojaOrigem() + "\n"
                    + "     AND DATA_FINAL >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "' "
                    + "	ORDER BY 1,2"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("idProduto"));
                    imp.setDataInicio(rst.getDate("dataInicio"));
                    imp.setDataFim(rst.getDate("dataFim"));
                    imp.setPrecoNormal(rst.getDouble("precoNormal"));
                    imp.setPrecoOferta(rst.getDouble("precoOferta"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
