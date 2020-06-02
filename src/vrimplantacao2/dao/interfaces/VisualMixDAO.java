/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;

/**
 *
 * @author Lucas
 */
public class VisualMixDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "VisualMix";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
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
                    OpcaoProduto.FABRICANTE,
                    OpcaoProduto.ASSOCIADO,
                    OpcaoProduto.COMPRADOR,
                    OpcaoProduto.COMPRADOR_PRODUTO,
                    OpcaoProduto.RECEITA,
                    OpcaoProduto.RECEITA_BALANCA
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo, \n"
                    + "	descricao\n"
                    + "from dbo.Empresas_CAP\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	al.CODIGO as id, \n"
                    + "	al.DESCRICAO as descricao, "
                    + "	al.SITUACAOTRIBUTARIA as cst,\n"
                    + " al.PERCENTUAL as aliquota, \n"
                    + "	al.REDUCAO as reducao \n"
                    + "from dbo.Aliquotas_NF al\n"
                    + "where codigo in (select Aliquota_NF from dbo.Produtos)\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "f.Codigo, "
                    + "f.Descricao "
                    + "from dbo.Grupo_Precos f "
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setDescricao(rst.getString("Descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "Mercadologico1 as merc1, "
                    + "Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 1\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("descricao"));

                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Mercadologico1 as merc1, \n"
                    + "Mercadologico2 as merc2, \n"
                    + "Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 2\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc2 = merc.get(rst.getString("merc1"));
                    if (merc2 != null) {
                        merc2.addFilho(
                                rst.getString("merc2"),
                                rst.getString("descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	Mercadologico1 as merc1,\n"
                    + "	Mercadologico2 as merc2, \n"
                    + "	Mercadologico3 as merc3, \n"
                    + "	Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 3\n"
                    + "order by 1, 2, 3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("descricao")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cast(p.Produto_Id as bigint) as id,\n"
                    + "	cast(ean.Codigo_Automacao as bigint) as Codigo_Automacao,\n"
                    + "	cast(ean.Digito_Automacao as bigint) as Digito_Automacao,\n"
                    + " p.Peso_Variavel,\n"
                    + " p.Pre_Pesado,\n"
                    + " p.Qtd_Decimal,\n"
                    + " p.ProdutoPai,\n"
                    + "	p.Descricao_Completa as descricaocompleta, \n"
                    + " p.Descricao_Reduzida as descricaoreduzida, \n"
                    + " p.Descricao_Balanca,\n"
                    + "	est.Custo_Ultima_Entrada_Com_Icms as custocomimposto,\n"
                    + " est.Custo_Ultima_Entrada_Sem_Icms as custosemimposto,\n"
                    + "	pre.preco_venda as precovenda,\n"
                    + " p.Margem_Atacado, \n"
                    + " p.Margem_Teorica, \n"
                    + " p.MargemFixa, \n"
                    + " p.Aliquota, \n"
                    + " p.Aliquota_FCP, \n"
                    + " p.Aliquota_Interna, \n"
                    + " p.Aliquota_NF,\n"
                    + " f.Codigo as idfamiliaproduto,\n"
                    + "	p.Mercadologico1, \n"
                    + " p.Mercadologico2, \n"
                    + " p.Mercadologico3, \n"
                    + " p.Mercadologico4, \n"
                    + " p.Mercadologico5, \n"
                    + " p.Situacao as situacaocadastro,\n"
                    + "	p.SituacaoTributaria as csticms, \n"
                    + " est.EstoqueInicial as estoque, \n"
                    + " p.Estoque_Minimo, \n"
                    + " p.Estoque_Maximo, \n"
                    + " p.EspecUnitariaTipo as tipoembalagem, \n"
                    + " p.EspecUnitariaQtde as qtdembalagem,\n"
                    + "	p.TipoProduto, \n"
                    + " p.Codigo_NCM as ncm, \n"
                    + " p.CEST as cest, \n"
                    + " p.TipoCodMercad as tipomercadoria,\n"
                    + "	p.CstPisCofinsEntrada, \n"
                    + " p.CstPisCofinsSaida, \n"
                    + " p.NaturezaReceita,\n"
                    + " cast(p.Fabricante as bigint) as idfabricante,\n"
                    + " p.Comprador as idcomprador\n"
                    + "from dbo.Produtos p\n"
                    + "left join dbo.Precos_Loja pre on pre.produto_id = p.Produto_Id\n"
                    + "	and pre.loja = " + getLojaOrigem() + " and pre.sequencia = 1\n"
                    + "left join dbo.Produtos_Estoque est on est.Produto_Id = p.Produto_Id\n"
                    + "	and est.Loja = " + getLojaOrigem() + "\n"
                    + "left join dbo.Automacao ean on ean.Produto_Id = p.Produto_Id\n"
                    + "left join dbo.Grupo_Precos_Produtos f on f.Produto_Id = p.Produto_Id\n"
                    + "order by p.Produto_Id"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));

                    String ean = rst.getString("Codigo_Automacao") + rst.getString("Digito_Automacao");

                    if ((rst.getString("Codigo_Automacao") != null)
                            && (!rst.getString("Codigo_Automacao").trim().isEmpty())
                            && (rst.getString("Digito_Automacao") != null)
                            && (!rst.getString("Digito_Automacao").trim().isEmpty())) {

                        long codigoProduto;
                        codigoProduto = Long.parseLong(ean.substring(0, ean.length() - 1));
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
                        if ((rst.getInt("Pre_Pesado") == 1)
                                && (ean.length() <= 6)) {
                            imp.setEan(ean.substring(0, ean.length() - 1));
                        } else {
                            imp.setEan(ean);
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setEan(ean);
                    }

                    //imp.seteBalanca(rst.getInt("balanca") > 0);
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(rst.getString("idfamiliaproduto"));
                    imp.setCodMercadologico1(rst.getString("Mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("Mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("Mercadologico3"));
                    imp.setFornecedorFabricante(rst.getString("idfabricante"));
                    imp.setIdComprador(rst.getString("idcomprador"));
                    imp.setMargem(rst.getDouble("Margem_Teorica"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("Estoque_Minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("Estoque_Maximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("CstPisCofinsSaida"));
                    imp.setPiscofinsCstCredito(rst.getString("CstPisCofinsEntrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NaturezaReceita"));
                    imp.setIcmsDebitoId(rst.getString("Aliquota_NF"));
                    imp.setIcmsCreditoId(rst.getString("Aliquota_NF"));

                    imp.setPautaFiscalId(imp.getImportId());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(p1.Produto_Id as bigint) as produto_pai,\n"
                    + "	p1.EspecUnitariaQtde as qtembalagem_pai,\n"
                    + "	cast(p2.Produto_Id as bigint) as produto_filho,\n"
                    + "	p2.EspecUnitariaQtde as qtdembalagem_filho\n"
                    + "from dbo.Produtos p1 \n"
                    + "join dbo.Produtos p2 on p2.ProdutoPai = p1.Produto_Id"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();
                    imp.setId(rst.getString("produto_pai"));
                    imp.setQtdEmbalagem(rst.getInt("qtembalagem_pai") == 0 ? 1 : rst.getInt("qtembalagem_pai"));
                    imp.setProdutoAssociadoId(rst.getString("produto_filho"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtdembalagem_filho") == 0 ? 1 : rst.getInt("qtdembalagem_filho"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	mx.mxf_icms_tipo_iva,\n"
                    + "	cast(mx.codigo_produto as bigint) as codigo_produto,\n"
                    + "	mx.ncm,\n"
                    + "	mx.cod_natureza_receita,\n"
                    + "	mx.cest,\n"
                    + "	mx.mxf_piscofins_cst_s,\n"
                    + "	(cast(coalesce(mx.mxf_pis_alq_s, 0) as numeric) / 1000) mxf_pis_alq_s,\n"
                    + "	(cast(coalesce(mx.mxf_pis_alq_s, 0) as numeric) / 1000) mxf_pis_alq_s,\n"
                    + "	mx.mxf_piscofins_cst_e,\n"
                    + "	(cast(coalesce(mx.mxf_pis_alq_e, 0) as numeric) / 1000) mxf_pis_alq_e,\n"
                    + "	(cast(coalesce(mx.mxf_cofins_alq_e, 0)  as numeric) / 1000) mxf_cofins_alq_e,\n"
                    + "	mx.mxf_icms_cst_s,\n"
                    + "	(cast(coalesce(mx.mxf_icms_alq_s, '0') as numeric) / 1000) mxf_icms_alq_s,\n"
                    + "	(cast(coalesce(mx.mxf_icms_rbc_s, '0') as numeric) / 1000) mxf_icms_rbc_s,\n"
                    + "	mx.mxf_icms_cst_e,\n"
                    + "	(cast(coalesce(mx.mxf_icms_alq_e, '0') as numeric) / 1000) mxf_icms_alq_e,\n"
                    + "	(cast(coalesce(mx.mxf_icms_rbc_e, '0') as numeric) / 1000) mxf_icms_rbc_e, \n"
                    + "	(cast(coalesce(mx.mxf_icms_iva_valor, '0') as numeric) / 1000) mxf_icms_iva_valor\n"
                    + "from dbo.produtosMixFiscal mx\n"
                    + "order by mx.codigo_produto"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    imp.setTipoIva(TipoIva.VALOR);
                    imp.setId(rst.getString("codigo_produto"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setIva(rst.getDouble("mxf_icms_iva_valor"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setUf(Parametros.get().getUfPadraoV2().getSigla());

                    switch (rst.getInt("mxf_icms_cst_s")) {
                        case 0:
                            imp.setAliquotaDebito(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                            imp.setAliquotaDebitoForaEstado(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                            break;
                        case 20:
                            if (rst.getDouble("mxf_icms_rbc_s") == 0) {
                                imp.setAliquotaDebito(0, rst.getDouble("mxf_icms_alq_s"), 0);
                                imp.setAliquotaDebitoForaEstado(0, rst.getDouble("mxf_icms_alq_s"), 0);
                            } else {
                                imp.setAliquotaDebito(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                                imp.setAliquotaDebitoForaEstado(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                            }
                            break;
                        case 40:
                            if (rst.getDouble("mxf_icms_alq_s") > 0) {
                                imp.setAliquotaDebito(0, rst.getDouble("mxf_icms_alq_s"), 0);
                                imp.setAliquotaDebitoForaEstado(0, rst.getDouble("mxf_icms_alq_s"), 0);
                            } else {
                                imp.setAliquotaDebito(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                                imp.setAliquotaDebitoForaEstado(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                            }
                            break;
                        case 41:
                            if (rst.getDouble("mxf_icms_alq_s") > 0) {
                                imp.setAliquotaDebito(0, rst.getDouble("mxf_icms_alq_s"), 0);
                                imp.setAliquotaDebitoForaEstado(0, rst.getDouble("mxf_icms_alq_s"), 0);
                            } else {
                                imp.setAliquotaDebito(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                                imp.setAliquotaDebitoForaEstado(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                            }
                            break;
                        case 60:
                            if (rst.getDouble("mxf_icms_alq_s") > 0) {
                                imp.setAliquotaDebito(0, rst.getDouble("mxf_icms_alq_s"), 0);
                                imp.setAliquotaDebitoForaEstado(0, rst.getDouble("mxf_icms_alq_s"), 0);
                            } else {
                                imp.setAliquotaDebito(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                                imp.setAliquotaDebitoForaEstado(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                            }
                            break;
                        default:
                            imp.setAliquotaDebito(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                            imp.setAliquotaDebitoForaEstado(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                            break;
                    }

                    switch (rst.getInt("mxf_icms_cst_e")) {
                        case 0:
                            imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            break;
                        case 20:
                            if (rst.getDouble("mxf_icms_rbc_e") == 0) {
                                imp.setAliquotaCredito(0, rst.getDouble("mxf_icms_alq_e"), 0);
                                imp.setAliquotaCreditoForaEstado(0, rst.getDouble("mxf_icms_alq_e"), 0);
                            } else {
                                imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                                imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            }
                            break;
                        case 40:
                            if (rst.getDouble("mxf_icms_alq_e") > 0) {
                                imp.setAliquotaCredito(0, rst.getDouble("mxf_icms_alq_e"), 0);
                                imp.setAliquotaCreditoForaEstado(0, rst.getDouble("mxf_icms_alq_e"), 0);
                            } else {
                                imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                                imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            }
                            break;
                        case 41:
                            if (rst.getDouble("mxf_icms_alq_e") > 0) {
                                imp.setAliquotaCredito(0, rst.getDouble("mxf_icms_alq_e"), 0);
                                imp.setAliquotaCreditoForaEstado(0, rst.getDouble("mxf_icms_alq_e"), 0);
                            } else {
                                imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                                imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            }
                            break;
                        case 51:
                            if (rst.getDouble("mxf_icms_alq_e") > 0) {
                                imp.setAliquotaCredito(0, rst.getDouble("mxf_icms_alq_e"), 0);
                                imp.setAliquotaCreditoForaEstado(0, rst.getDouble("mxf_icms_alq_e"), 0);
                            } else {
                                imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                                imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            }
                            break;
                        case 60:
                            if (rst.getDouble("mxf_icms_alq_e") > 0) {
                                imp.setAliquotaCredito(0, rst.getDouble("mxf_icms_alq_e"), 0);
                                imp.setAliquotaCreditoForaEstado(0, rst.getDouble("mxf_icms_alq_e"), 0);
                            } else {
                                imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                                imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            }
                            break;
                        case 70:
                            if (rst.getDouble("mxf_icms_alq_e") > 0) {
                                imp.setAliquotaCredito(0, rst.getDouble("mxf_icms_alq_e"), 0);
                                imp.setAliquotaCreditoForaEstado(0, rst.getDouble("mxf_icms_alq_e"), 0);
                            } else {
                                imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                                imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            }
                            break;
                        default:
                            imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                            break;
                    }

                    //imp.setAliquotaDebito(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                    //imp.setAliquotaDebitoForaEstado(rst.getInt("mxf_icms_cst_s"), rst.getDouble("mxf_icms_alq_s"), rst.getDouble("mxf_icms_rbc_s"));
                    //imp.setAliquotaCredito(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                    //imp.setAliquotaCreditoForaEstado(rst.getInt("mxf_icms_cst_e"), rst.getDouble("mxf_icms_alq_e"), rst.getDouble("mxf_icms_rbc_e"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(f.Codigo as bigint) as id,\n"
                    + " f.Tipo as idtipofornecedor,\n"
                    + " tf.Descricao as tipofornecedor,\n"
                    + " f.RazaoSocial as razao,\n"
                    + " f.NomeFantasia as fantasia,\n"
                    + "	f.TipoLogradouro as logradouro,\n"
                    + " f.Endereco,\n"
                    + " CAST(f.NumeroEnd as bigint) as numero,\n"
                    + " f.Complemento,\n"
                    + " f.Bairro,\n"
                    + " f.Cidade as municipio,\n"
                    + " f.Estado as uf,\n"
                    + "	f.Cep,\n"
                    + " f.CxPostal as caixapostal,\n"
                    + " f.Telefone,\n"
                    + " f.Fax,\n"
                    + " f.Telex,\n"
                    + " f.TeleContato,\n"
                    + " f.Contato,\n"
                    + "	cast(f.CGC as bigint) as cnpj,\n"
                    + " f.InscricaoEstadual as ie,\n"
                    + " f.InscrMunicipal as im,\n"
                    + " f.PrazoEntrega,\n"
                    + " f.FrequenciaVisita as prazoVisita,\n"
                    + " f.DataCadastro,\n"
                    + "	f.CondicaoPagto,\n"
                    + " cp.Descricao as condicaopagamento,\n"
                    + " cp.Qtd_Parcelas,\n"
                    + " f.Observacao,\n"
                    + "	f.Supervisor,\n"
                    + " f.CelSupervisor,\n"
                    + " f.EmailSupervisor,\n"
                    + " f.TelSupervisor,\n"
                    + " f.Email,\n"
                    + " f.Vendedor,\n"
                    + " f.TelVendedor,\n"
                    + " f.CelVendedor,\n"
                    + "	f.EmailVendedor,\n"
                    + " f.Gerente,\n"
                    + " f.TelGerente,\n"
                    + " f.CelGerente,\n"
                    + " f.EmailGerente,\n"
                    + "	f.Situacao,\n"
                    + " f.Status\n"
                    + "from dbo.Fornecedores f\n"
                    + "left join dbo.Condicoes_Pagto cp on cp.Codigo = f.CondicaoPagto\n"
                    + "left join dbo.TipoFornecedor tf on tf.Tipo = f.Tipo\n"
                    + "order by f.Codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("im"));

                    if ((rst.getString("Endereco") != null)
                            && (!rst.getString("Endereco").trim().isEmpty())) {
                        imp.setEndereco(rst.getString("logradouro") + " " + rst.getString("Endereco"));
                    }

                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setDatacadastro(rst.getDate("DataCadastro"));

                    if ((rst.getString("Telefone") != null)
                            && (!rst.getString("Telefone").trim().isEmpty())) {

                        if (rst.getString("Telefone").startsWith("0")) {
                            imp.setTel_principal(rst.getString("Telefone").substring(1));
                        }
                    }

                    imp.setPrazoEntrega(rst.getInt("PrazoEntrega"));
                    imp.setPrazoVisita(rst.getInt("prazoVisita"));
                    imp.setPrazoSeguranca(2);
                    imp.setPrazoPedido(rst.getInt("PrazoEntrega"));

                    imp.addDivisao(
                            imp.getImportId(),
                            imp.getPrazoVisita(),
                            imp.getPrazoEntrega(),
                            imp.getPrazoSeguranca()
                    );

                    imp.setCondicaoPagamento(rst.getInt("CondicaoPagto"));
                    imp.setObservacao(rst.getString("Observacao"));

                    switch (rst.getInt("idtipofornecedor")) {
                        case 1:
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            break;
                        case 2:
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            break;
                        case 3:
                            imp.setTipoFornecedor(TipoFornecedor.PRODUTORRURAL);
                            break;
                        case 6:
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                        default:
                            break;
                    }

                    if ((rst.getString("Email") != null)
                            && (!rst.getString("Email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("Email").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }
                    if ((rst.getString("Telex") != null)
                            && (!rst.getString("Telex").trim().isEmpty())) {
                        imp.addTelefone("TELEX", rst.getString("Telex"));
                    }
                    if ((rst.getString("TeleContato") != null)
                            && (!rst.getString("TeleContato").trim().isEmpty())) {
                        imp.addTelefone(rst.getString("Contato") == null ? "CONTATO" : rst.getString("Contato"), rst.getString("TeleContato"));
                    }

                    // Dados do Supervisor
                    imp.addContato(
                            rst.getString("Supervisor") == null ? "" : rst.getString("Supervisor"),
                            rst.getString("TelSupervisor") == null ? "" : rst.getString("TelSupervisor"),
                            rst.getString("CelSupervisor") == null ? "" : rst.getString("CelSupervisor"),
                            TipoContato.COMERCIAL,
                            rst.getString("EmailSupervisor") == null ? "" : rst.getString("EmailSupervisor").toLowerCase()
                    );

                    // Dados do Vendedor
                    imp.addContato(
                            rst.getString("Vendedor") == null ? "" : rst.getString("Vendedor"),
                            rst.getString("TelVendedor") == null ? "" : rst.getString("TelVendedor"),
                            rst.getString("CelVendedor") == null ? "" : rst.getString("CelVendedor"),
                            TipoContato.COMERCIAL,
                            rst.getString("EmailVendedor") == null ? "" : rst.getString("EmailVendedor").toLowerCase()
                    );

                    // Dados do Gerente
                    imp.addContato(
                            rst.getString("Gerente") == null ? "" : rst.getString("Gerente"),
                            rst.getString("TelGerente") == null ? "" : rst.getString("TelGerente"),
                            rst.getString("CelGerente") == null ? "" : rst.getString("CelGerente"),
                            TipoContato.COMERCIAL,
                            rst.getString("EmailGerente") == null ? "" : rst.getString("EmailGerente").toLowerCase()
                    );

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(pf.Fornecedor as bigint) as idfornecedor,\n"
                    + "	cast(pf.Produto_Id as bigint) as idproduto,\n"
                    + "	pf.Referencia as codigoexterno,\n"
                    + "	pf.Qtde_Emb as qtdembalagem,\n"
                    + "	pf.Preco_Tabela as custo\n"
                    + "from dbo.Produtos_Fornecedor pf\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setCustoTabela(rst.getDouble("custo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.Codigo as id, \n"
                    + "	c.Nome as razao, \n"
                    + "	c.Apelido as fantasia,\n"
                    + "	c.RG, \n"
                    + "	cast(c.CPF as bigint) as CPF, \n"
                    + "	c.IDSexo as sexo,\n"
                    + "	c.DataNascimento,\n"
                    + "	c.EstadoCivil,\n"
                    + "	c.NomeConjuge,\n"
                    + "	c.DataNascimentoConjuge,\n"
                    + "	c.Endereco,\n"
                    + "	c.Numero,\n"
                    + "	c.Complemento,\n"
                    + "	c.Bairro,\n"
                    + "	c.CEP,\n"
                    + "	c.Cidade as municipio,\n"
                    + "	c.Estado as uf,\n"
                    + "	c.Referencia,\n"
                    + "	c.TipoEndereco,\n"
                    + "	c.eMail,\n"
                    + "	c.Empresa,\n"
                    + "	c.DataAdmissao,\n"
                    + "	c.CodigoProfissao,\n"
                    + "	c.TelefoneEmpresa,\n"
                    + "	e.Descricao as nomeempresa,\n"
                    + "	e.EnderecoEmpresa,\n"
                    + "	e.NumeroEmpresa,\n"
                    + "	e.ComplementoEmpresa,\n"
                    + "	e.BairroEmpresa,\n"
                    + "	e.CidadeEmpresa,\n"
                    + "	e.EstadoEmpresa,\n"
                    + "	e.CEPEmpresa,"
                    + "	c.RamalEmpresa,\n"
                    + "	c.DataInclusao as datacadastro,\n"
                    + "	c.Telefone,\n"
                    + "	c.InscEstadual as ie_rg,\n"
                    + "	c.Status,\n"
                    + "	c.LimiteCredito as valorlimite,\n"
                    + "	c.LimiteCheques,\n"
                    + "	c.DescProfissao as cargo,\n"
                    + " p.Descricao as profissao\n,"        
                    + "	c.Renda as salario,\n"
                    + "	c.EnderecoEntrega,\n"
                    + "	c.NumeroEntrega,\n"
                    + "	c.ComplEntrega,\n"
                    + "	c.BairroEntrega,\n"
                    + "	c.CidadeEntrega as municipioentrega,\n"
                    + "	c.UFEntrega as ufentrega,\n"
                    + "	c.CEPEntrega as cepentrega,\n"
                    + "	c.FoneEntrega as telefoneentrega,\n"
                    + " c.DataNascimentoConjuge as datanascimentoconjuge\n"
                    + "from dbo.Clientes c\n"
                    + "left join [DiggerMatriz].[dbo].Empresa e on e.Codigo = c.Empresa\n" 
                    + "left join [DiggerMatriz].[dbo].Profissao p on p.Codigo = c.CodigoProfissao\n"
                    + "where c.IDLoja = " + getLojaOrigem() + "\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("CPF"));
                    imp.setInscricaoestadual(rst.getString("RG"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTelefone(rst.getString("Telefone"));
                    imp.setEmail(rst.getString("eMail") == null ? "" : rst.getString("eMail").toLowerCase());
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    imp.setDataNascimento(rst.getDate("DataNascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setNomeConjuge(rst.getString("NomeConjuge"));
                    imp.setDataNascimentoConjuge(rst.getDate("datanascimentoconjuge"));
                                        
                    imp.setEmpresa(rst.getString("nomeempresa"));
                    imp.setEmpresaEndereco(rst.getString("EnderecoEmpresa"));
                    imp.setEmpresaNumero(rst.getString("NumeroEmpresa"));
                    imp.setEmpresaComplemento(rst.getString("ComplementoEmpresa"));
                    imp.setEmpresaBairro(rst.getString("BairroEmpresa"));
                    imp.setEmpresaMunicipio(rst.getString("CidadeEmpresa"));
                    imp.setEmpresaUf(rst.getString("EstadoEmpresa"));
                    imp.setEmpresaCep(rst.getString("CEPEmpresa"));
                    imp.setEmpresaTelefone(rst.getString("TelefoneEmpresa"));
                    imp.setDataAdmissao(rst.getDate("DataAdmissao"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setSalario(rst.getDouble("salario"));

                    if (rst.getInt("EstadoCivil") == 2) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }

                    switch (rst.getInt("sexo")) {
                        case 1:
                            imp.setSexo(TipoSexo.MASCULINO);
                            break;
                        default:
                            imp.setSexo(TipoSexo.FEMININO);
                            break;
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CompradorIMP> getCompradores() throws Exception {
        List<CompradorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	Codigo,\n"
                    + "	Nome\n"
                    + "from dbo.Compradores"
            )) {
                while (rst.next()) {
                    CompradorIMP imp = new CompradorIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setDescricao(rst.getString("Nome"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(r.Codigo_Produto as bigint) as codigo_receita,\n"
                    + "	cast(p.Produto_Id as bigint) as id_produto,\n"
                    + "	p.Descricao_Completa as descricao_receita,\n"
                    + "	r.Quant_Produto as qtd,\n"
                    + "	r.Fator as fator,\n"
                    + "	cast(ri.Codigo_Produto as bigint) as codigo_item,\n"
                    + "	p2.Descricao_Completa as descrocao_item,\n"
                    + "	(ri.Quant_Produto * 1000) as qtd_item,\n"
                    + "	ri.Fator as fator_item\n"
                    + "from dbo.Receita r\n"
                    + "join dbo.Produtos p on p.Produto_Id = r.Codigo_Produto\n"
                    + "join dbo.Receita_Itens ri on ri.Codigo_Produto_Receita = r.Codigo_Produto\n"
                    + "join dbo.Produtos p2 on p2.Produto_Id = ri.Codigo_Produto \n"
                    + "order by r.Codigo_Produto"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportsistema(getSistema());
                    imp.setImportid(rst.getString("codigo_receita"));
                    imp.setIdproduto(rst.getString("id_produto"));
                    imp.setDescricao(rst.getString("descricao_receita"));
                    imp.setRendimento(rst.getDouble("qtd"));
                    imp.setQtdembalagemproduto(1000);
                    imp.setQtdembalagemreceita(rst.getInt("qtd_item") == 0 ? 1 * 1000 : rst.getInt("qtd_item"));
                    imp.setFator(rst.getDouble("fator_item") == 0 ? 1 : rst.getDouble("fator_item"));
                    imp.setFichatecnica("");

                    imp.getProdutos().add(rst.getString("codigo_item"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(rb.Codigo as bigint) as id,\n"
                    + "	rb.Descricao as descricao,\n"
                    + "	CONCAT(\n"
                    + "	rb.Linha01, ' ', rb.Linha02, ' ', rb.Linha03, ' ',\n"
                    + "	rb.Linha04, ' ', rb.Linha05, ' ', rb.Linha06, ' ',\n"
                    + "	rb.Linha07, ' ', rb.Linha08, ' ', rb.Linha09, ' ',\n"
                    + "	rb.Linha10, ' ' , rb.Linha11, ' ', rb.Linha12) as receita,\n"
                    + "	cast(rbp.Produto_Id as bigint) as id_produto,\n"
                    + "	p.Descricao_Completa as desricaoproduto\n"
                    + "from dbo.Ingrediente_Novo rb\n"
                    + "join dbo.Embalagem rbp on rbp.Ingredientes = rb.Codigo\n"
                    + "join dbo.Produtos p on p.Produto_Id = rbp.Produto_Id\n"
                    + "order by rb.Descricao"
            )) {
                Map<String, ReceitaBalancaIMP> receitas = new HashMap<>();
                while (rst.next()) {

                    ReceitaBalancaIMP imp = receitas.get(rst.getString("id"));

                    if (imp == null) {
                        imp = new ReceitaBalancaIMP();
                        imp.setId(rst.getString("id"));
                        imp.setDescricao(rst.getString("descricao"));
                        imp.setReceita(rst.getString("receita"));
                        receitas.put(imp.getId(), imp);
                    }

                    imp.getProdutos().add(rst.getString("id_produto"));
                }

                return new ArrayList<>(receitas.values());
            }
        }
    }
}
