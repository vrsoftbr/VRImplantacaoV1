/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Lucas
 */
public class DSoftDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(DSoftDAO.class.getName());
    private Date dataInicioVenda;
    private Date dataTerminoVenda;
    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }
    
    
    @Override
    public String getSistema() {
        return "DSoft" + ("".equals(complemento) ? "" : " - " + complemento);
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws Exception {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    nome,\n"
                    + "    cgc as cnpj\n"
                    + "from empresa\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("codigo"), rst.getString("nome") + " - " + rst.getString("cnpj")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    icm.codigo as id,\n"
                    + "    icm.nome as descricao,\n"
                    + "    icm.cst,\n"
                    + "    icm.aliquota,\n"
                    + "    0 as reducao\n"
                    + "from ecfaliquota icm\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                        )
                    );
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    cst,\n"
                    + "    coalesce(icm, 0) as aliquota,\n"
                    + "    coalesce(perc_reducao_icms, 0) as reducao\n"
                    + "from estoque\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    );

                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            Utils.stringToInt(rst.getString("cst")),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                        )
                    );
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    cst_entrada as cst_entrada,\n"
                    + "    coalesce(icms_entrada, 0) as aliquota_entrada,\n"
                    + "    coalesce(red_bc_entrada, 0) as reducao_entrada\n"
                    + "from estoque\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cst_entrada"),
                            rst.getDouble("aliquota_entrada"),
                            rst.getDouble("reducao_entrada")
                    );

                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            Utils.stringToInt(rst.getString("cst_entrada")),
                            rst.getDouble("aliquota_entrada"),
                            rst.getDouble("reducao_entrada")
                        )
                    );
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.TIPO_PRODUTO,
                    OpcaoProduto.ATACADO
                }
        ));
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    m1.codigo as merc1,\n"
                    + "    m1.nome as desc_merc1,\n"
                    + "    m2.codigo as merc2,\n"
                    + "    m2.nome as desc_merc2,\n"
                    + "    m3.id as merc3,\n"
                    + "    m3.descricao as desc_merc3\n"
                    + "from grupo m1\n"
                    + "join subgrupo m2 on m2.codgrupo = m1.codigo\n"
                    + "join subcategorias m3 on m3.codgrupo = m1.codigo\n"
                    + "    and m3.codsubgrupo = m2.codigo\n"
                    + "order by 1, 3, 5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    e.codigo as id,\n"
                    + "    e.codbarra as ean,\n"
                    + "    case e.balanca when 'S' then 1 else 0 end balanca,\n"
                    + "    e.validade,\n"
                    + "    e.descricao as descricaocompleta,\n"
                    + "    e.descrifiscal as descricaoreduzida,\n"
                    + "    e.med as tipoembalagem,\n"
                    + "    e.codgrupo as merc1,\n"
                    + "    grp.nome as desc_merc1,\n"
                    + "    e.codsubgrupo as merc2,\n"
                    + "    sgr.nome as desc_merc2,\n"
                    + "    e.codsubcategoria as merc3,\n"
                    + "    sub.codgrupo as grupo_subcategoria,\n"
                    + "    sub.codsubgrupo as subgrupo_subcategoria,\n"
                    + "    sub.descricao as descricao_subcategoria,\n"
                    + "    coalesce(e.precocusto, 0) as custo,\n"
                    + "    coalesce(e.precovenda, 0) as precovenda,\n"
                    + "    coalesce(e.qtde, 0) as qtdestoque,\n"
                    + "    coalesce(e.qtdeminima, 0) as estoqueminimo,\n"
                    + "    e.datacadastro,\n"
                    + "    case e.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    e.cod_ncm as ncm,\n"
                    + "    e.cod_cest as cest,\n"
                    + "    e.cst,\n"
                    + "    e.icm,\n"
                    + "    e.perc_reducao_icms as reducao,\n"
                    + "    e.cst_entrada,\n"
                    + "    e.icms_entrada,\n"
                    + "    e.red_bc_entrada as reducao_entrada,\n"
                    + "    e.codaliquota as icms_ecf,\n"
                    + "    ecf.nome as descricao_aliq_ecf,\n"
                    + "    ecf.cst as cst_ecf,\n"
                    + "    ecf.aliquota as aliquota_ecf,\n"
                    + "    e.cst_pis,\n"
                    + "    e.cst_cofins,\n"
                    + "    e.natureza_pis_cofins as naturezareceita\n"
                    + "from estoque e\n"
                    + "left join ecfaliquota ecf on ecf.codigo = e.codaliquota\n"
                    + "left join subcategorias sub on sub.id = e.codsubcategoria\n"
                    + "left join grupo grp on grp.codigo = e.codgrupo\n"
                    + "left join subgrupo sgr on sgr.codigo = e.codsubgrupo\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    
                    if ((rst.getString("validade") != null)
                            && (!rst.getString("validade").trim().isEmpty())) {
                        imp.setValidade(rst.getInt("validade"));
                    }
                                        
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    
                    if (rst.getString("descricaoreduzida") != null
                            && !rst.getString("descricaoreduzida").trim().isEmpty()) {
                        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    } else {
                        imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    }
                                       
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("qtdestoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    String icmsDebitoId = getAliquotaKey(rst.getString("cst"), rst.getDouble("aliquota"), rst.getDouble("reducao"));
                    String icmsCreditoId = getAliquotaKey(rst.getString("cst_entrada"), rst.getDouble("icms_entrada"), rst.getDouble("reducao_entrada"));

                    imp.setIcmsDebitoId(icmsDebitoId);
                    imp.setIcmsDebitoForaEstadoId(icmsDebitoId);
                    imp.setIcmsDebitoForaEstadoNfId(icmsDebitoId);
                    imp.setIcmsCreditoId(icmsCreditoId);
                    imp.setIcmsCreditoForaEstadoId(icmsCreditoId);
                    imp.setIcmsConsumidorId(rst.getString("icms_ecf"));

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
                    "select\n"
                    + "    b.cod_produto as idproduto,\n"
                    + "    b.cod_barras as ean\n"
                    + "from estoque_codbarras b\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    a.cod_produto_pai as idproduto_pai,\n"
                    + "    p1.descricao as desc_produtopai,\n"
                    + "    a.cod_produto_filho as idproduto_filho,\n"
                    + "    p2.descricao as desc_produto_filho,\n"
                    + "    a.qtde_indice as qtd\n"
                    + "from estoque_indices a\n"
                    + "join estoque p1 on p1.codigo = a.cod_produto_pai\n"
                    + "join estoque p2 on p2.codigo = a.cod_produto_filho\n"
                    + "order by 1, 3"
                    /*+  TESTAR AMBOS SQL PARA MIGRAÇÃO DO ASSOCIADO  "select\n"
                    + "    ren.cod_prod_mestre,\n"
                    + "    p1.descricao as idproduto_pai,\n"
                    + "    ren.cod_prod_filho,\n"
                    + "    p2.descricao as idproduto_filho,\n"
                    + "    ren.descricao, \n"
                    + "    1 as qtd\n"
                    + "from estoque_rentabilidade ren\n"
                    + "join estoque p1 on p1.codigo = ren.cod_prod_mestre\n"
                    + "join estoque p2 on p2.codigo = ren.cod_prod_filho\n"
                    + "order by 1, 3"*/
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();
                    imp.setImpIdProduto(rst.getString("idproduto_pai"));
                    imp.setQtdEmbalagem(1);
                    imp.setImpIdProdutoItem(rst.getString("idproduto_filho"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtd"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    distinct\n"
                    + "    e.cod_ncm as ncm,\n"
                    + "    e.cst as cst_debito,\n"
                    + "    coalesce(e.icm, 0) as aliquota_debito,\n"
                    + "    coalesce(e.perc_reducao_icms, 0) as reducao_debito,\n"
                    + "    e.cst_entrada as cst_credito,\n"
                    + "    coalesce(e.icms_entrada, 0) as aliquota_credito,\n"
                    + "    coalesce(e.red_bc_entrada, 0) as reducao_credito,\n"
                    + "    e.PERC_MVA_COMPRA as mva\n"
                    + "from estoque e\n"
                    + "where coalesce(e.PERC_MVA_COMPRA, 0) > 0\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    imp.setId(rst.getString("ncm")
                            + rst.getString("cst_debito")
                            + rst.getString("aliquota_debito")
                            + rst.getString("reducao_debito")
                            + rst.getString("cst_credito")
                            + rst.getString("aliquota_credito")
                            + rst.getString("reducao_credito")
                            + rst.getString("mva")
                    );
                    imp.setTipoIva(TipoIva.PERCENTUAL);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setIva(rst.getDouble("mva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setUf(Parametros.get().getUfPadraoV2().getSigla());

                    int cstSaida = rst.getInt("cst_debito");
                    double aliquotaSaida = rst.getDouble("aliquota_debito");
                    double reduzidoSaida = rst.getDouble("reducao_debito");
                    int cstEntrada = rst.getInt("cst_credito");
                    double aliquotaEntrada = rst.getDouble("aliquota_credito");
                    double reduzidoEntrada = rst.getDouble("reducao_credito");

                    if (aliquotaSaida > 0 && reduzidoSaida == 0) {
                        cstSaida = 0;
                    }
                    if (aliquotaEntrada > 0 && reduzidoEntrada == 0) {
                        cstEntrada = 0;
                    }

                    if (aliquotaSaida > 0 && reduzidoSaida > 0) {
                        cstSaida = 20;
                    }
                    if (aliquotaEntrada > 0 && reduzidoEntrada > 0) {
                        cstEntrada = 20;
                    }

                    imp.setAliquotaDebito(cstSaida, aliquotaSaida, reduzidoSaida);
                    imp.setAliquotaDebitoForaEstado(cstSaida, aliquotaSaida, reduzidoSaida);
                    imp.setAliquotaCredito(cstEntrada, aliquotaEntrada, reduzidoEntrada);
                    imp.setAliquotaCreditoForaEstado(cstEntrada, aliquotaEntrada, reduzidoEntrada);

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.EXCECAO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    e.codigo as id,\n"
                        + "    e.cod_ncm as ncm,\n"
                        + "    e.cst as cst_debito,\n"
                        + "    coalesce(e.icm, 0) as aliquota_debito,\n"
                        + "    coalesce(e.perc_reducao_icms, 0) as reducao_debito,\n"
                        + "    e.cst_entrada as cst_credito,\n"
                        + "    coalesce(e.icms_entrada, 0) as aliquota_credito,\n"
                        + "    coalesce(e.red_bc_entrada, 0) as reducao_credito,\n"
                        + "    e.PERC_MVA_COMPRA as mva\n"
                        + "from estoque e\n"
                        + "where coalesce(e.PERC_MVA_COMPRA, 0) > 0\n"
                        + "order by 1"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));
                        
                        String id_pautafiscal = rst.getString("ncm")
                                + rst.getString("cst_debito")
                                + rst.getString("aliquota_debito")
                                + rst.getString("reducao_debito")
                                + rst.getString("cst_credito")
                                + rst.getString("aliquota_credito")
                                + rst.getString("reducao_credito")
                                + rst.getString("mva");

                        imp.setPautaFiscalId(id_pautafiscal);

                        result.add(imp);                        
                    }
                }
                return result;
            }
        }
        return null;
    }

    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.codigo as id,\n"
                    + "    f.razao,\n"
                    + "    f.nome as fantasia,\n"
                    + "    f.cgc as cnpj,\n"
                    + "    f.ie, \n"
                    + "    f.cpf,\n"
                    + "    f.rg, \n"
                    + "    f.contato,\n"
                    + "    f.endereco,\n"
                    + "    f.num_endereco as numero,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade as municipio,\n"
                    + "    f.uf,\n"
                    + "    f.cep,\n"
                    + "    f.telefone,\n"
                    + "    f.fax, \n"
                    + "    f.celular,\n"
                    + "    f.email,\n"
                    + "    f.email_secundario,\n"
                    + "    f.email_cotacao_web,\n"
                    + "    f.homepage,\n"
                    + "    case f.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    f.prazo_entrega,\n"
                    + "    f.obs as observacao,\n"
                    + "    f.vendedor,\n"
                    + "    f.fone_vend,\n"
                    + "    f.representante,\n"
                    + "    f.fone_rep\n"
                    + "from fornecedor f\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj_cpf(rst.getString("cpf"));
                    }

                    if ((rst.getString("ie") != null)
                            && (!rst.getString("ie").trim().isEmpty())) {
                        imp.setIe_rg(rst.getString("ie"));
                    } else {
                        imp.setIe_rg(rst.getString("rg"));
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTel_principal(rst.getString("telefone"));

                    imp.setAtivo(rst.getInt("situacaocadastro") == 1);
                    imp.setPrazoEntrega(rst.getInt("prazo_entrega"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }

                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addCelular("CELULAR", rst.getString("celular"));
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }

                    if ((rst.getString("email_secundario") != null)
                            && (!rst.getString("email_secundario").trim().isEmpty())) {
                        imp.addEmail("EMAIL 2", rst.getString("email_secundario").toLowerCase(), TipoContato.COMERCIAL);
                    }

                    if ((rst.getString("email_cotacao_web") != null)
                            && (!rst.getString("email_cotacao_web").trim().isEmpty())) {
                        imp.addEmail("EMAIL COTACAO WEB", rst.getString("email_cotacao_web").toLowerCase(), TipoContato.COMERCIAL);
                    }

                    imp.addTelefone(rst.getString("vendedor"), rst.getString("fone_vend"));
                    imp.addTelefone(rst.getString("representante"), rst.getString("fone_rep"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo_fornecedor as idfornecedor,\n"
                    + "    codigo_interno_sistema as idproduto,\n"
                    + "    codigo_fabricante as codigoexterno\n"
                    + "from estoque_codigo_fabric\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.codigo as id,\n"
                    + "    c.nome as razao,\n"
                    + "    c.cpf,\n"
                    + "    c.rg,\n"
                    + "    c.cgc as cnpj,\n"
                    + "    c.ie as inscricaoestadual,\n"
                    + "    c.endereco,\n"
                    + "    c.num_endereco,\n"
                    + "    c.complemento,\n"
                    + "    c.complemento_endereco,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade as municipio,\n"
                    + "    c.uf,\n"
                    + "    c.cep,\n"
                    + "    c.endereco_cobranca,\n"
                    + "    c.complemento_cobranca,\n"
                    + "    c.bairro_cobranca,\n"
                    + "    c.cidade_cobranca,\n"
                    + "    c.uf_cobranca,\n"
                    + "    c.cep_cobranca,\n"
                    + "    c.endereco_entrega,\n"
                    + "    c.num_end_entrega,\n"
                    + "    c.bairro_entrega,\n"
                    + "    c.cidade_entrega,\n"
                    + "    c.uf_entrega,\n"
                    + "    c.cep_entrega,\n"
                    + "    c.telefone,\n"
                    + "    c.telefone_2,\n"
                    + "    c.telefone_conjuge,\n"
                    + "    c.telefone_entrega,\n"
                    + "    c.celular,\n"
                    + "    c.fax,\n"
                    + "    c.email,\n"
                    + "    c.email_secundario,\n"
                    + "    c.email_cobranca,\n"
                    + "    c.email_financeiro,\n"
                    + "    c.limitecredito as valorlimite,\n"
                    + "    c.datanascto as datanascimento,\n"
                    + "    c.datacadastro,\n"
                    + "    c.ondetrabalha as empresa,\n"
                    + "    c.fonetrabalho as telefoneempresa,\n"
                    + "    c.endtrabalho as enderecotrabalho,\n"
                    + "    c.funcao as cargo,\n"
                    + "    c.salario,\n"
                    + "    c.estadocivil,\n"
                    + "    c.conjugue,\n"
                    + "    c.conjugue_cpf,\n"
                    + "    c.conjugue_rg,\n"
                    + "    c.datanasctoconjugue,\n"
                    + "    c.nomepai,\n"
                    + "    c.nomemae,\n"
                    + "    case c.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    c.contato\n"
                    + "from cliente c\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(imp.getRazao());

                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj(rst.getString("cpf"));
                    }

                    if ((rst.getString("inscricaoestadual") != null)
                            && (!rst.getString("inscricaoestadual").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    }

                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setAtivo(rst.getInt("situacaocadastro") == 1);
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("num_endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaComplemento(rst.getString("complemento_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaMunicipio(rst.getString("cidade_cobranca"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("email"));
                    imp.setValorLimite(rst.getDouble("valorlimite") > 100000000 ? 0 : rst.getDouble("valorlimite"));

                    if (imp.getValorLimite() > 0) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    } else {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                    }

                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("enderecotrabalho"));
                    imp.setEmpresaTelefone(rst.getString("telefoneempresa"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));

                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomeConjuge(rst.getString("conjugue"));
                    imp.setCpfConjuge(rst.getString("conjugue_cpf"));

                    if ((rst.getString("estadocivil") != null)
                            && (!rst.getString("estadocivil").trim().isEmpty())) {
                        if ("CASADO".equals(rst.getString("estadocivil"))
                                || "CASADO(A)".equals(rst.getString("estadocivil"))) {
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                        } else if ("DIVORCIADO".equals(rst.getString("estadocivil"))) {
                            imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                        } else if ("SOLTEIRO".equals(rst.getString("estadocivil"))) {
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                        } else {
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.codigo as id,\n"
                    + "    r.codcliente as idcliente,\n"
                    + "    r.documento,\n"
                    + "    r.numparcela as parcela,\n"
                    + "    r.dataemissao,\n"
                    + "    r.datavencimento,\n"
                    + "    r.historico,\n"
                    + "    r.valor,\n"
                    + "    COALESCE(VALORRECEBIDO, 0) AS valorrecebido\n"
                    + "from receber r\n"
                    + "WHERE COALESCE(VALORRECEBIDO, 0) < VALOR\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));                    
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("historico") + " PARCELA - " + rst.getString("parcela"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pg.codigo as id,\n"
                    + "    pg.codfornecedor as idfornecedor,\n"
                    + "    pg.documento as numerodocumento,\n"
                    + "    pg.dataemissao,\n"
                    + "    pg.datavencimento,\n"
                    + "    pg.valor,\n"
                    + "    pg.parcela,\n"
                    + "    pg.historico as observacao\n"
                    + "from pagar pg\n"
                    + "where pg.datapagamento is null\n"
                    + "and pg.codfornecedor is not null"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataemissao"));
                    imp.setObservacao(rst.getString("observacao"));
                    
                    ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("datavencimento"), rst.getDouble("valor"));
                    
                    String numParcela = rst.getString("parcela");
                    
                    if ((numParcela != null)
                            && (!numParcela.trim().isEmpty())) {
                        if (numParcela.contains("/")) {
                            parc.setNumeroParcela(Utils.stringToInt(numParcela.substring(numParcela.indexOf("/"))));
                        } else {
                            parc.setNumeroParcela(Utils.stringToInt(numParcela));
                        }
                        
                    } else {
                        parc.setNumeroParcela(1);
                    }
                    
                    parc.setObservacao(numParcela);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
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

                        String id = rst.getString("id");

                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }

                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numero_cupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("pdv")));
                        next.setData(rst.getDate("datavenda"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));

                        String horaInicio = timestampDate.format(rst.getDate("datavenda")) + " " + rst.getString("horavenda");
                        String horaTermino = timestampDate.format(rst.getDate("datavenda")) + " " + rst.getString("horavenda");

                        next.setCancelado(rst.getInt("cancelado") == 1);
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotal"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        //next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        //next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo_ecf"));
                        next.setChaveNfCe(rst.getString("nfce_chave"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    v.codvenda as id,\n"
                    + "    v.codcliente as idcliente,\n"
                    + "    v.dataemissao as datavenda,\n"
                    + "    v.saida as horavenda,\n"
                    + "    v.desconto,\n"
                    + "    v.subtotal,\n"
                    + "    v.total,\n"
                    + "    v.totalcusto,\n"
                    + "    v.observacao,\n"
                    + "    v.troco,\n"
                    + "    v.numero_cupom,\n"
                    + "    v.pdv,\n"
                    + "    case v.cancelado when 'S' then 1 else 0 end cancelado,\n"
                    + "    (v.modelo_nf||v.serie_nf) as modelo_ecf,\n"
                    + "    v.nome_cliente,\n"
                    + "    v.cpf_cnpj,\n"
                    + "    v.tipo_lancamento,\n"
                    + "    v.tipo_ecf,\n"
                    + "    v.nfce_chave\n"
                    + "from venda v\n"
                    + "where v.dataemissao between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' \n"
                    + "AND v.TIPO != 'D'";

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
        
        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        String id = rst.getString("idproduto")
                                + "-"
                                + rst.getString("item")
                                + "-"
                                + rst.getString("codvenda")
                                + "-"
                                + rst.getString("datavenda");

                        String idVenda = rst.getString("idvenda");


                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto"));
                        next.setQuantidade(rst.getDouble("qtde"));
                        next.setPrecoVenda(rst.getDouble("valor_unit_bruto"));
                        next.setTotalBruto(rst.getDouble("valor_total_bruto"));
                        next.setValorDesconto(rst.getDouble("valor_desconto"));
                        next.setValorAcrescimo(rst.getDouble("valor_acrescimo"));
                        next.setCancelado(rst.getInt("cancelado") == 1);
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setSequencia(rst.getInt("item"));

                        String trib = Utils.acertarTexto(rst.getString("st"));
                        if (trib == null || "".equals(trib)) {
                            trib = "I";
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
             0700   7.00    ALIQUOTA 07%
             1200   12.00   ALIQUOTA 12%
             1800   18.00   ALIQUOTA 18%
             2500   25.00   ALIQUOTA 25%
             1100   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "NN":
                    cst = 41;
                    aliq = 0;
                    break;
                case "II":
                    cst = 40;
                    aliq = 0;
                    break;
                case "FF":
                    cst = 60;
                    aliq = 0;
                    break;
                case "0300":
                    cst = 0;
                    aliq = 3;
                    break;
                case "0500":
                    cst = 0;
                    aliq = 5;
                    break;
                case "0700":
                    cst = 0;
                    aliq = 7;
                    break;
                case "1000":
                    cst = 0;
                    aliq = 10;
                    break;
                case "1200":
                    cst = 0;
                    aliq = 12;
                    break;
                case "1300":
                    cst = 0;
                    aliq = 13;
                    break;
                case "1700":
                    cst = 0;
                    aliq = 17;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    i.codvenda as idvenda,\n"
                    + "    i.controle,\n"
                    + "    i.item,\n"
                    + "    p.codbarra as codigobarras,\n"
                    + "    i.codproduto as idproduto,\n"
                    + "    i.descricao as descricaoproduto,\n"
                    + "    i.dataemissao as datavenda,\n"
                    + "    i.qtde,\n"
                    + "    i.vendido as precovenda,\n"
                    + "    i.totaliten,\n"
                    + "    i.valor_total_bruto,\n"
                    + "    i.valor_unit_bruto, \n"
                    + "    i.custofinal,\n"
                    + "    i.totalcusto,\n"
                    + "    i.st,\n"
                    + "    case i.cancelado when 'S' then 1 else 0 end cancelado,\n"
                    + "    i.cst_pis,\n"
                    + "    i.cst_cofins,\n"
                    + "    i.aliq_pis,\n"
                    + "    i.aliq_cofins,\n"
                    + "    i.tipo_lancamento,\n"
                    + "    i.valor_desconto, \n"
                    + "    i.valor_acrescimo\n"
                    + "from itemvenda i\n"
                    + "join estoque p on p.codigo = i.codproduto\n"
                    + "join venda v on v.codvenda = i.codvenda and \n"
                    + " v.TIPO != 'D' \n"
                    + "where i.dataemissao between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "'";

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
