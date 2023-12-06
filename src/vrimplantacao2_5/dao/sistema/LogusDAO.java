package vrimplantacao2_5.dao.sistema;

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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
//import vrimplantacao.classe.ConexaoInformix;
import vrimplantacao2_5.dao.conexao.ConexaoInformix;

/**
 *
 * @author alan
 */
public class LogusDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Logus";
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
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.PRODUTOS_BALANCA,
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
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS
                }
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_INDICADOR_IE,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PRODUTO_FORNECEDOR));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.EMAIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.VALOR_LIMITE));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	distinct\n"
                    + "	icms.cdg_icms id,\n"
                    + "	icms.dcr_icms descricao,\n"
                    + "	cst.cdg_situacaotributariaicms cst,\n"
                    + "	icms.pct_aliq_icms aliquota,\n"
                    + "	icms.pct_reducao_bc reducao\n"
                    + "from \n"
                    + "	informix.cadicms icms  \n"
                    + "join informix.cadsituacoestributariasicms cst \n"
                    + "	on icms.idcadsituacaotributariaicms = cst.idcadsituacaotributariaicms\n"
                    + "where \n"
                    + " 	icms.cdg_icms in \n"
                    + " 		(select cdg_icms from cadassoc)\n"
                    + "order by\n"
                    + "	1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.cdg_interno id_produto,\n"
                    + "	ncm.cdg_ncm ncm,\n"
                    + "	pf.dat_alteracao data_inicio,\n"
                    + "	val_pvv valor,\n"
                    + "	pf.pct_aliquota aliquota_credito,\n"
                    + "	cdg_situacaotributariaicms cst_credito,\n"
                    + "	0 reducao_credito,\n"
                    + "	icm.pct_aliq_icms aliquota_debito,\n"
                    + "	cdg_situacaotributariaicms cst_debito,\n"
                    + "	icm.pct_reducao_bc reducao_debito,\n"
                    + "	'IVA' as tipo\n"
                    + "from\n"
                    + "	cadpvvpr pf\n"
                    + "join cadprod p on p.cdg_interno = pf.cdg_interno\n"
                    + "join cadassoc pa on p.cdg_interno = pa.cdg_interno and pa.cdg_estoque = p.cdg_produto\n"
                    + "join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and ncm.dat_ini_vigencia =\n"
                    + "	(select \n"
                    + "		max(x.dat_ini_vigencia)\n"
                    + "	from \n"
                    + "		cadncmproduto x \n"
                    + "	where \n"
                    + "		x.cdg_interno = ncm.cdg_interno and x.dat_ini_vigencia <= current year to fraction(3))\n"
                    + "join cadicms icm on pa.cdg_icms = icm.cdg_icms\n"
                    + "join cadsituacoestributariasicms  sticm on icm.idcadsituacaotributariaicms = sticm.idcadsituacaotributariaicms\n"
                    + "where\n"
                    + "	pf.dat_alteracao =\n"
                    + "		(select\n"
                    + "			max(dat_alteracao)\n"
                    + "		from\n"
                    + "			cadpvvpr pf2\n"
                    + "		where\n"
                    + "			cdg_interno = pf.cdg_interno)\n"
                    + "and val_pvv > 0\n"
                    + "union all\n"
                    + "select\n"
                    + "	pf.cdg_interno id_produto,\n"
                    + "	ncm.cdg_ncm ncm,\n"
                    + "	pf.dat_alteracao data_inicio,\n"
                    + "	pct_mva_atacado valor,\n"
                    + "	pf.pct_aliquota aliquota_credito,\n"
                    + "	cdg_situacaotributariaicms cst_credito,\n"
                    + "	0 reducao_credito,\n"
                    + "	icm.pct_aliq_icms aliquota_debito,\n"
                    + "	cdg_situacaotributariaicms cst_debito,\n"
                    + "	icm.pct_reducao_bc reducao_debito,\n"
                    + "	'MVA' as tipo\n"
                    + "from\n"
                    + "	cadmvaprestado pf\n"
                    + "join cadprod p on p.cdg_interno = pf.cdg_interno\n"
                    + "join cadassoc pa on p.cdg_interno = pa.cdg_interno and pa.cdg_estoque = p.cdg_produto\n"
                    + "join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and ncm.dat_ini_vigencia =\n"
                    + "	(select \n"
                    + "		max(x.dat_ini_vigencia)\n"
                    + "	from \n"
                    + "		cadncmproduto x \n"
                    + "	where \n"
                    + "		x.cdg_interno = ncm.cdg_interno and x.dat_ini_vigencia <= current year to fraction(3))\n"
                    + "join cadicms icm on pa.cdg_icms = icm.cdg_icms\n"
                    + "join cadsituacoestributariasicms  sticm on icm.idcadsituacaotributariaicms = sticm.idcadsituacaotributariaicms\n"
                    + "where\n"
                    + "	pf.dat_alteracao =\n"
                    + "		(select\n"
                    + "			max(dat_alteracao)\n"
                    + "		from\n"
                    + "			cadmvaprestado pf2\n"
                    + "		where\n"
                    + "			cdg_interno = pf.cdg_interno)\n"
                    + "and pct_mva_atacado > 0"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rst.getString("id_produto"));
                    imp.setTipoIva("IVA".equals(rst.getString("tipo")) ? TipoIva.VALOR : TipoIva.PERCENTUAL);
                    imp.setIva(rst.getDouble("valor"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));

                    // DÉBITO
                    if ((rst.getDouble("aliquota_debito") > 0) && (rst.getDouble("reducao_debito") == 0)) {

                        imp.setAliquotaDebito(0, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(0, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));

                    } else if ((rst.getDouble("aliquota_debito") > 0) && (rst.getDouble("reducao_debito") > 0)) {

                        imp.setAliquotaDebito(20, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(20, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));

                    } else {

                        imp.setAliquotaDebito(rst.getInt("cst_debito"), rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(rst.getInt("cst_debito"), rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                    }

                    // CRÉDITO
                    if ((rst.getDouble("aliquota_credito") > 0) && (rst.getDouble("reducao_credito") == 0)) {

                        imp.setAliquotaCredito(0, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(0, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));

                    } else if ((rst.getDouble("aliquota_credito") > 0) && (rst.getDouble("reducao_credito") > 0)) {

                        imp.setAliquotaCredito(20, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(20, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));

                    } else {

                        imp.setAliquotaCredito(rst.getInt("cst_credito"), rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(rst.getInt("cst_credito"), rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws SQLException {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	distinct\n"
                    + "	x5.cdg_depto merc1,\n"
                    + "	x5.dcr_depto descmerc1,\n"
                    + "	x4.cdg_secao merc2,\n"
                    + "	x4.dcr_secao descmerc2,\n"
                    + "	x3.cdg_grupo merc3,\n"
                    + "	x3.dcr_grupo descmerc3,\n"
                    + "	x6.cdg_subgrupo merc4,\n"
                    + "	x6.dcr_subgrupo descmerc4\n"
                    + "from\n"
                    + "	cadassoc x0,\n"
                    + "   cadprod x1,\n"
                    + "	cadgrupo x3,\n"
                    + "	cadsecao x4,\n"
                    + "	caddepto x5,\n"
                    + "	cadsubgr x6\n"
                    + "where\n"
                    + "	x0.cdg_interno = x1.cdg_interno and \n"
                    + "	x0.cdg_estoque = x1.cdg_produto and \n"
                    + "	x0.cdg_grupo = x3.cdg_grupo and \n"
                    + "	x3.cdg_secao = x4.cdg_secao and \n"
                    + "	x4.cdg_depto = x5.cdg_depto and \n"
                    + "	x0.cdg_subgrupo = x6.cdg_subgrupo\n"
                    + "order by\n"
                    + "	1, 2, 3, 4"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    imp.setMerc4ID(rs.getString("merc4"));
                    imp.setMerc4Descricao(rs.getString("descmerc4"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cdg_eqv_preco idfamilia,\n"
                    + "	dcr_equivalencia_preco descricao\n"
                    + "from cadequivalenciasprecos "
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idfamilia"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.cdg_interno id_interno,\n"
                    + "	p.cdg_barra ean,\n"
                    + "	un.sgl_unidade_medida unidade,\n"
                    + "	p.qtd_por_emb qtdembalagem\n"
                    + "from \n"
                    + "	informix.cadprod p\n"
                    + "left join informix.cadunidadesmedida un on p.idcadunidademedida = un.idcadunidademedida\n"
                    + "left join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                    + "where est.cdg_filial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_interno"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutosBalanca() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	p.cdg_produto id,\n"
                    + "	p.cdg_interno id_interno,\n"
                    + "	p.dcr_etiq_gondola descricaogondola,\n"
                    + "	p.dcr_reduzida descricaoreduzida,\n"
                    + "	pa.dcr_produto || ' ' || pa.dcr_variedade descricaologus,\n"
                    + "	p.cdg_barra ean,\n"
                    + "	nullif (pa.flb_tipo_peso, 'F') pesavel,\n"
                    + " pa.flb_balanca unitarioPesavel,\n"
                    + "	pa.flb_habilita_checagem_peso_pdv pesopdv,\n"
                    + "	est.val_custo custosemimposto,\n"
                    + "	est.val_custo_tot custocomimposto,\n"
                    + "	pa.pct_mg_lucro margem,\n"
                    + "	est.val_preco precovenda,\n"
                    + "	est.qtd_estoque estoque,\n"
                    + "	p.dat_cadastro cadastro,\n"
                    + "	p.dat_desativacao desativacao,\n"
                    + "	un.sgl_unidade_medida unidade,\n"
                    + "	p.qtd_por_emb qtdembalagem,\n"
                    + "	ncm.cdg_ncm ncm,\n"
                    + "	st.cdg_especificador_st cest,\n"
                    + "	se.cdg_depto merc1,\n"
                    + "	se.cdg_secao merc2,\n"
                    + "	gr.cdg_grupo merc3,\n"
                    + "	pa.cdg_subgrupo merc4,\n"
                    + "	pa.cdg_icms idicms,\n"
                    + "	est.pct_icms_ent icms_credito,\n"
                    + "	pis.flg_cst_piscofinse pis_credito,\n"
                    + "	pis.flg_cst_piscofinss pis_debito,\n"
                    + "	pis.cdg_natrecpiscof naturezareceita\n"
                    + "from \n"
                    + "	informix.cadprod p\n"
                    + "left join informix.cadunidadesmedida un on p.idcadunidademedida = un.idcadunidademedida\n"
                    + "left join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno and \n"
                    + "	pa.cdg_estoque = p.cdg_produto\n"
                    + "left join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                    + "left join informix.cadgrupo gr on pa.cdg_grupo = gr.cdg_grupo\n"
                    + "left join informix.cadsecao se on gr.cdg_secao = se.cdg_secao\n"
                    + "left join informix.caddepto dp on se.cdg_depto = dp.cdg_depto\n"
                    + "left join cadassocpiscofins pis on pa.cdg_interno = pis.cdg_interno\n"
                    + "	and pis.dat_ini_vigencia = (select\n"
                    + "                                   max(x.dat_ini_vigencia)\n"
                    + "                               from\n"
                    + "                                   cadassocpiscofins x\n"
                    + "                               where\n"
                    + "                                   x.cdg_interno = pis.cdg_interno and \n"
                    + "                                   x.dat_ini_vigencia <= current year to fraction(3))\n"
                    + "left join cadcodigosespecificstproduto cest on pa.cdg_interno = cest.cdg_interno and \n"
                    + "	cest.dat_inicio_vigencia = (select \n"
                    + "					min(x.dat_inicio_vigencia)\n"
                    + "                               from\n"
                    + "					cadcodigosespecificstproduto x\n"
                    + "                               where \n"
                    + "					x.cdg_interno = cest.cdg_interno)\n"
                    + "left join cadcodigosespecificadoresst st on cest.idcadcodigoespecificadorst = st.idcadcodigoespecificadorst\n"
                    + "left join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and \n"
                    + "	ncm.dat_ini_vigencia = (select \n"
                    + "					max(x.dat_ini_vigencia)\n"
                    + "				from \n"
                    + "					cadncmproduto x \n"
                    + "				where \n"
                    + "					x.cdg_interno = ncm.cdg_interno and \n"
                    + "					x.dat_ini_vigencia <= current year to fraction(3))\n"
                    + "where \n"
                    + "	est.cdg_filial = " + getLojaOrigem() + "\n"
                    + " and p.cdg_barra <= 999999 \n"
                    + " and nullif (pa.flb_tipo_peso, 'F') = 'V' \n"
                    + " and p.qtd_por_emb = 1"
            )) {
                Map<Integer, vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO> produtosBalanca = new vrimplantacao.dao.cadastro.ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO produtoBalanca;
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_interno"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaogondola") == null ? rs.getString("descricaoreduzida")
                            : rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());

                    long codigoProduto;
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

                    if (rs.getString("desativacao") != null) {
                        imp.setSituacaoCadastro(0);
                    }

                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("idicms"));
                    imp.setIcmsCstEntrada(00);
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_credito"));
                    imp.setIcmsReducaoEntrada(0.0);
                    imp.setIcmsCstEntradaForaEstado(00);
                    imp.setIcmsAliqEntradaForaEstado(rs.getDouble("icms_credito"));
                    imp.setIcmsReducaoEntradaForaEstado(0.0);
                    imp.setIcmsConsumidorId(rs.getString("idicms"));
                    imp.setPiscofinsCstCredito(rs.getString("pis_credito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	p.cdg_produto id,\n"
                    + "	p.cdg_interno id_interno,\n"
                    + "	p.dcr_etiq_gondola descricaogondola,\n"
                    + "	p.dcr_reduzida descricaoreduzida,\n"
                    + "	pa.dcr_produto || ' ' || pa.dcr_variedade descricaologus,\n"
                    + "	p.cdg_barra ean,\n"
                    + "	pa.flb_tipo_peso pesavel,\n"
                    + " pa.flb_balanca unitarioPesavel,\n"
                    + "	pa.flb_habilita_checagem_peso_pdv pesopdv,\n"
                    + "	est.val_custo custosemimposto,\n"
                    + "	est.val_custo_tot custocomimposto,\n"
                    + "	pa.pct_mg_lucro margem,\n"
                    + " mar.pct_margem, \n"
                    + "	est.val_preco precovenda,\n"
                    + "	est.qtd_estoque estoque,\n"
                    + "	p.dat_cadastro cadastro,\n"
                    + " pa.cdg_eqv_preco idfamilia,\n"
                    + "	p.dat_desativacao desativacao,\n"
                    //+ " un.sgl_unidade_medida unidade,\n"
                    + "	p.dcr_embalagem unidade,\n"
                    + "	p.qtd_por_emb qtdembalagem,\n"
                    + " p.qtd_da_embalagem as volume,\n"
                    + "	ncm.cdg_ncm ncm,\n"
                    + "	st.cdg_especificador_st cest,\n"
                    + "	se.cdg_depto merc1,\n"
                    + "	se.cdg_secao merc2,\n"
                    + "	gr.cdg_grupo merc3,\n"
                    + "	pa.cdg_subgrupo merc4,\n"
                    + "	pa.cdg_icms idicms,\n"
                    + "	est.pct_icms_ent icms_credito,\n"
                    + "	pis.flg_cst_piscofinse pis_credito,\n"
                    + "	pis.flg_cst_piscofinss pis_debito,\n"
                    + "	pis.cdg_natrecpiscof naturezareceita\n"
                    + "from \n"
                    + "	informix.cadprod p\n"
                    + "left join informix.cadunidadesmedida un on p.idcadunidademedida = un.idcadunidademedida\n"
                    + "left join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno and \n"
                    + "	pa.cdg_estoque = p.cdg_produto\n"
                    + "left join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                    + "left join informix.cadgrupo gr on pa.cdg_grupo = gr.cdg_grupo\n"
                    + "left join informix.cadsubgr sg on pa.cdg_subgrupo = sg.cdg_subgrupo \n"
                    + "left join informix.cadmargs mar on sg.cdg_subgrupo = mar.cdg_subgrupo \n"
                    + "left join informix.cadsecao se on gr.cdg_secao = se.cdg_secao\n"
                    + "left join informix.caddepto dp on se.cdg_depto = dp.cdg_depto\n"
                    + "left join cadassocpiscofins pis on pa.cdg_interno = pis.cdg_interno\n"
                    + "	and pis.dat_ini_vigencia = (select\n"
                    + "                                   max(x.dat_ini_vigencia)\n"
                    + "                               from\n"
                    + "                                   cadassocpiscofins x\n"
                    + "                               where\n"
                    + "                                   x.cdg_interno = pis.cdg_interno and \n"
                    + "                                   x.dat_ini_vigencia <= current year to fraction(3))\n"
                    + "left join cadcodigosespecificstproduto cest on pa.cdg_interno = cest.cdg_interno and \n"
                    + "	cest.dat_inicio_vigencia = (select \n"
                    + "					min(x.dat_inicio_vigencia)\n"
                    + "                               from\n"
                    + "					cadcodigosespecificstproduto x\n"
                    + "                               where \n"
                    + "					x.cdg_interno = cest.cdg_interno)\n"
                    + "left join cadcodigosespecificadoresst st on cest.idcadcodigoespecificadorst = st.idcadcodigoespecificadorst\n"
                    + "left join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and \n"
                    + "	ncm.dat_ini_vigencia = (select \n"
                    + "					max(x.dat_ini_vigencia)\n"
                    + "				from \n"
                    + "					cadncmproduto x \n"
                    + "				where \n"
                    + "					x.cdg_interno = ncm.cdg_interno and \n"
                    + "					x.dat_ini_vigencia <= current year to fraction(3))\n"
                    + "where \n"
                    + "	est.cdg_filial = " + getLojaOrigem() + "\n"
                    + "   and p.qtd_por_emb = 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_interno"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaogondola") == null ? rs.getString("descricaoreduzida")
                            : rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());

                    if ((rs.getString("pesavel") != null
                            && "V".equals(rs.getString("pesavel").trim().toUpperCase()))
                            || rs.getInt("unitarioPesavel") == 1) {
                        imp.seteBalanca(true);
                    }

                    if (rs.getString("desativacao") != null) {
                        imp.setSituacaoCadastro(0);
                    }

                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    //imp.setMargem(rs.getDouble("margem"));
                    imp.setMargem(rs.getDouble("pct_margem"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setTipoEmbalagemCotacao(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setVolume(rs.getDouble("volume"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstDebito(rs.getString("pis_debito"));
                    imp.setPiscofinsCstCredito(rs.getString("pis_credito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("idicms"));
                    imp.setIcmsCreditoId(rs.getString("idicms"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("idicms"));
                    imp.setIcmsConsumidorId(rs.getString("idicms"));

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
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	pf.cdg_interno id_produto,\n"
                        + "	ncm.cdg_ncm ncm,\n"
                        + "	pf.dat_alteracao data_inicio,\n"
                        + "	val_pvv valor,\n"
                        + "	pf.pct_aliquota aliquota_credito,\n"
                        + "	cdg_situacaotributariaicms cst_credito,\n"
                        + "	0 reducao_credito,\n"
                        + "	icm.pct_aliq_icms aliquota_debito,\n"
                        + "	cdg_situacaotributariaicms cst_debito,\n"
                        + "	icm.pct_reducao_bc reducao_debito,\n"
                        + "	'IVA' as tipo\n"
                        + "from\n"
                        + "	cadpvvpr pf\n"
                        + "join cadprod p on p.cdg_interno = pf.cdg_interno\n"
                        + "join cadassoc pa on p.cdg_interno = pa.cdg_interno and pa.cdg_estoque = p.cdg_produto\n"
                        + "join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and ncm.dat_ini_vigencia =\n"
                        + "	(select \n"
                        + "		max(x.dat_ini_vigencia)\n"
                        + "	from \n"
                        + "		cadncmproduto x \n"
                        + "	where \n"
                        + "		x.cdg_interno = ncm.cdg_interno and x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + "join cadicms icm on pa.cdg_icms = icm.cdg_icms\n"
                        + "join cadsituacoestributariasicms  sticm on icm.idcadsituacaotributariaicms = sticm.idcadsituacaotributariaicms\n"
                        + "where\n"
                        + "	pf.dat_alteracao =\n"
                        + "		(select\n"
                        + "			max(dat_alteracao)\n"
                        + "		from\n"
                        + "			cadpvvpr pf2\n"
                        + "		where\n"
                        + "			cdg_interno = pf.cdg_interno)\n"
                        + "and val_pvv > 0\n"
                        + "union all\n"
                        + "select\n"
                        + "	pf.cdg_interno id_produto,\n"
                        + "	ncm.cdg_ncm ncm,\n"
                        + "	pf.dat_alteracao data_inicio,\n"
                        + "	pct_mva_atacado valor,\n"
                        + "	pf.pct_aliquota aliquota_credito,\n"
                        + "	cdg_situacaotributariaicms cst_credito,\n"
                        + "	0 reducao_credito,\n"
                        + "	icm.pct_aliq_icms aliquota_debito,\n"
                        + "	cdg_situacaotributariaicms cst_debito,\n"
                        + "	icm.pct_reducao_bc reducao_debito,\n"
                        + "	'MVA' as tipo\n"
                        + "from\n"
                        + "	cadmvaprestado pf\n"
                        + "join cadprod p on p.cdg_interno = pf.cdg_interno\n"
                        + "join cadassoc pa on p.cdg_interno = pa.cdg_interno and pa.cdg_estoque = p.cdg_produto\n"
                        + "join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and ncm.dat_ini_vigencia =\n"
                        + "	(select \n"
                        + "		max(x.dat_ini_vigencia)\n"
                        + "	from \n"
                        + "		cadncmproduto x \n"
                        + "	where \n"
                        + "		x.cdg_interno = ncm.cdg_interno and x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + "join cadicms icm on pa.cdg_icms = icm.cdg_icms\n"
                        + "join cadsituacoestributariasicms  sticm on icm.idcadsituacaotributariaicms = sticm.idcadsituacaotributariaicms\n"
                        + "where\n"
                        + "	pf.dat_alteracao =\n"
                        + "		(select\n"
                        + "			max(dat_alteracao)\n"
                        + "		from\n"
                        + "			cadmvaprestado pf2\n"
                        + "		where\n"
                        + "			cdg_interno = pf.cdg_interno)\n"
                        + "and pct_mva_atacado > 0"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_produto"));
                        imp.setPautaFiscalId(imp.getImportId());

                        result.add(imp);
                    }
                }

                return result;
            }
        }

        if (opt == OpcaoProduto.DESC_COMPLETA) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	p.cdg_interno id_interno,\n"
                        + "	pa.dcr_produto nome, \n"
                        + "	mar.dcr_marca marca,\n"
                        + "	pa.dcr_variedade variedade\n"
                        + "from \n"
                        + "	informix.cadprod p\n"
                        + "left join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno \n"
                        + "left join informix.cadmarcasproduto mar on mar.idcadmarcaproduto = pa.idcadmarcaproduto	\n"
                        + "left join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                        + "where est.cdg_filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_interno"));
                        imp.setDescricaoCompleta(
                                (rst.getString("nome") != null ? rst.getString("nome").trim() : "")
                                + " "
                                + (rst.getString("variedade") != null ? rst.getString("variedade").trim() : "")
                        );

                        result.add(imp);
                    }
                }

                return result;
            }
        }

        if (opt == OpcaoProduto.PIS_COFINS) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	p.cdg_interno id_interno,\n"
                        + "	ncm.cdg_ncm ncm,\n"
                        + "	st.cdg_especificador_st cest,\n"
                        + "	pa.cdg_icms idicms,\n"
                        + "	est.pct_icms_ent icms_credito,\n"
                        + "	pis.flg_cst_piscofinse pis_credito,\n"
                        + "	pis.flg_cst_piscofinss pis_debito,\n"
                        + "	pis.cdg_natrecpiscof naturezareceita\n"
                        + "from\n"
                        + "	informix.cadprod p\n"
                        + "join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno and pa.cdg_estoque = p.cdg_produto\n"
                        + "join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                        + "join cadassocpiscofins pis on pa.cdg_interno = pis.cdg_interno and pis.dat_ultima_alt = (\n"
                        + "	select\n"
                        + "		max(x.dat_ultima_alt)\n"
                        + "	from\n"
                        + "		cadassocpiscofins x\n"
                        + "	where\n"
                        + "		x.cdg_interno = pis.cdg_interno and x.dat_ultima_alt <= current year to fraction(3))\n"
                        + "left join cadcodigosespecificstproduto cest on pa.cdg_interno = cest.cdg_interno and cest.dat_inicio_vigencia = (\n"
                        + "	select\n"
                        + "		min(x.dat_inicio_vigencia)\n"
                        + "	from\n"
                        + "		cadcodigosespecificstproduto x\n"
                        + "	where\n"
                        + "		x.cdg_interno = cest.cdg_interno)\n"
                        + "left join cadcodigosespecificadoresst st on cest.idcadcodigoespecificadorst = st.idcadcodigoespecificadorst\n"
                        + "join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and ncm.dat_ini_vigencia = (\n"
                        + "	select\n"
                        + "		max(x.dat_ini_vigencia)\n"
                        + "	from\n"
                        + "		cadncmproduto x\n"
                        + "	where\n"
                        + "		x.cdg_interno = ncm.cdg_interno\n"
                        + "		and x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + "where\n"
                        + "	est.cdg_filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_interno"));
                        imp.setPiscofinsCstDebito(rst.getString("pis_debito"));
                        imp.setPiscofinsCstCredito(rst.getString("pis_credito"));

                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.NATUREZA_RECEITA) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	p.cdg_interno id_interno,\n"
                        + "	ncm.cdg_ncm ncm,\n"
                        + "	st.cdg_especificador_st cest,\n"
                        + "	pa.cdg_icms idicms,\n"
                        + "	est.pct_icms_ent icms_credito,\n"
                        + "	pis.flg_cst_piscofinse pis_credito,\n"
                        + "	pis.flg_cst_piscofinss pis_debito,\n"
                        + "	pis.cdg_natrecpiscof naturezareceita\n"
                        + "from \n"
                        + "	informix.cadprod p\n"
                        + "join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno and \n"
                        + "	pa.cdg_estoque = p.cdg_produto\n"
                        + "join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                        + "join cadassocpiscofins pis on pa.cdg_interno = pis.cdg_interno\n"
                        + "	and pis.dat_ini_vigencia = (select\n"
                        + "                                   max(x.dat_ini_vigencia)\n"
                        + "                               from\n"
                        + "                                   cadassocpiscofins x\n"
                        + "                               where\n"
                        + "                                   x.cdg_interno = pis.cdg_interno and \n"
                        + "                                   x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + " left join cadcodigosespecificstproduto cest on pa.cdg_interno = cest.cdg_interno and \n"
                        + "	cest.dat_inicio_vigencia = (select \n"
                        + "					min(x.dat_inicio_vigencia)\n"
                        + "                               from\n"
                        + "					cadcodigosespecificstproduto x\n"
                        + "                               where \n"
                        + "					x.cdg_interno = cest.cdg_interno)\n"
                        + " left join cadcodigosespecificadoresst st on cest.idcadcodigoespecificadorst = st.idcadcodigoespecificadorst\n"
                        + "join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and \n"
                        + "	ncm.dat_ini_vigencia = (select \n"
                        + "					max(x.dat_ini_vigencia)\n"
                        + "				from \n"
                        + "					cadncmproduto x \n"
                        + "				where \n"
                        + "					x.cdg_interno = ncm.cdg_interno and \n"
                        + "					x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + "where est.cdg_filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_interno"));
                        imp.setPiscofinsCstDebito(rst.getString("pis_debito"));
                        imp.setPiscofinsCstCredito(rst.getString("pis_credito"));
                        imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                        result.add(imp);
                    }
                }
            }

            return result;
        }

        if (opt == OpcaoProduto.NCM) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	p.cdg_interno id_interno,\n"
                        + "	ncm.cdg_ncm ncm,\n"
                        + "	st.cdg_especificador_st cest,\n"
                        + "	pa.cdg_icms idicms,\n"
                        + "	est.pct_icms_ent icms_credito,\n"
                        + "	pis.flg_cst_piscofinse pis_credito,\n"
                        + "	pis.flg_cst_piscofinss pis_debito,\n"
                        + "	pis.cdg_natrecpiscof naturezareceita\n"
                        + "from \n"
                        + "	informix.cadprod p\n"
                        + "join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno and \n"
                        + "	pa.cdg_estoque = p.cdg_produto\n"
                        + "join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                        + "join cadassocpiscofins pis on pa.cdg_interno = pis.cdg_interno\n"
                        + "	and pis.dat_ini_vigencia = (select\n"
                        + "                                   max(x.dat_ini_vigencia)\n"
                        + "                               from\n"
                        + "                                   cadassocpiscofins x\n"
                        + "                               where\n"
                        + "                                   x.cdg_interno = pis.cdg_interno and \n"
                        + "                                   x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + "join cadcodigosespecificstproduto cest on pa.cdg_interno = cest.cdg_interno and \n"
                        + "	cest.dat_inicio_vigencia = (select \n"
                        + "					min(x.dat_inicio_vigencia)\n"
                        + "                               from\n"
                        + "					cadcodigosespecificstproduto x\n"
                        + "                               where \n"
                        + "					x.cdg_interno = cest.cdg_interno)\n"
                        + "join cadcodigosespecificadoresst st on cest.idcadcodigoespecificadorst = st.idcadcodigoespecificadorst\n"
                        + "join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and \n"
                        + "	ncm.dat_ini_vigencia = (select \n"
                        + "					max(x.dat_ini_vigencia)\n"
                        + "				from \n"
                        + "					cadncmproduto x \n"
                        + "				where \n"
                        + "					x.cdg_interno = ncm.cdg_interno and \n"
                        + "					x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + "where est.cdg_filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_interno"));
                        imp.setNcm(rst.getString("ncm"));

                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.CEST) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	p.cdg_interno id_interno,\n"
                        + "	ncm.cdg_ncm ncm,\n"
                        + "	st.cdg_especificador_st cest,\n"
                        + "	pa.cdg_icms idicms,\n"
                        + "	est.pct_icms_ent icms_credito,\n"
                        + "	pis.flg_cst_piscofinse pis_credito,\n"
                        + "	pis.flg_cst_piscofinss pis_debito,\n"
                        + "	pis.cdg_natrecpiscof naturezareceita\n"
                        + "from \n"
                        + "	informix.cadprod p\n"
                        + "join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno and \n"
                        + "	pa.cdg_estoque = p.cdg_produto\n"
                        + "join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                        + "join cadassocpiscofins pis on pa.cdg_interno = pis.cdg_interno\n"
                        + "	and pis.dat_ini_vigencia = (select\n"
                        + "                                   max(x.dat_ini_vigencia)\n"
                        + "                               from\n"
                        + "                                   cadassocpiscofins x\n"
                        + "                               where\n"
                        + "                                   x.cdg_interno = pis.cdg_interno and \n"
                        + "                                   x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + "join cadcodigosespecificstproduto cest on pa.cdg_interno = cest.cdg_interno and \n"
                        + "	cest.dat_inicio_vigencia = (select \n"
                        + "					min(x.dat_inicio_vigencia)\n"
                        + "                               from\n"
                        + "					cadcodigosespecificstproduto x\n"
                        + "                               where \n"
                        + "					x.cdg_interno = cest.cdg_interno)\n"
                        + "join cadcodigosespecificadoresst st on cest.idcadcodigoespecificadorst = st.idcadcodigoespecificadorst\n"
                        + "join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and \n"
                        + "	ncm.dat_ini_vigencia = (select \n"
                        + "					max(x.dat_ini_vigencia)\n"
                        + "				from \n"
                        + "					cadncmproduto x \n"
                        + "				where \n"
                        + "					x.cdg_interno = ncm.cdg_interno and \n"
                        + "					x.dat_ini_vigencia <= current year to fraction(3))\n"
                        + "where est.cdg_filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_interno"));
                        imp.setCest(rst.getString("cest"));

                        result.add(imp);
                    }
                }
            }

            return result;
        }

        if (opt == OpcaoProduto.ICMS) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	p.cdg_interno id_interno,\n"
                        + "	pa.cdg_icms idicms\n"
                        + "from\n"
                        + "	informix.cadprod p\n"
                        + "join informix.cadassoc pa on\n"
                        + "	p.cdg_interno = pa.cdg_interno\n"
                        + "	and pa.cdg_estoque = p.cdg_produto\n"
                        + "join informix.estprfil est on\n"
                        + "	p.cdg_produto = est.cdg_produto\n"
                        + "where\n"
                        + "	est.cdg_filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_interno"));
                        imp.setIcmsDebitoId(rst.getString("idicms"));
                        imp.setIcmsDebitoForaEstadoId(rst.getString("idicms"));
                        imp.setIcmsDebitoForaEstadoNfId(rst.getString("idicms"));
                        imp.setIcmsCreditoId(rst.getString("idicms"));
                        imp.setIcmsCreditoForaEstadoId(rst.getString("idicms"));
                        imp.setIcmsConsumidorId(rst.getString("idicms"));

                        result.add(imp);
                    }
                }
            }

            return result;
        }

        if (opt == OpcaoProduto.VOLUME_TIPO_EMBALAGEM) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	p.cdg_interno id_interno,\n"
                        + "	un.sgl_unidade_medida unidade\n"
                        + "from \n"
                        + "	informix.cadprod p\n"
                        + "join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno\n"
                        + "join informix.cadunidadesmedida un on un.idcadunidademedida = pa.idcadunidademedida\n"
                        + "join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                        + "where est.cdg_filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_interno"));
                        imp.setTipoEmbalagemVolume(rst.getString("unidade"));

                        result.add(imp);
                    }
                }
            }

            return result;
        }

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	p.cdg_interno id,\n"
                        + "	val_preco precovenda,\n"
                        + "	qtd_por_emb,\n"
                        + "	val_preco/qtd_por_emb precoatacado\n"
                        + "from estprfil a \n"
                        + "inner join cadprod p on p.cdg_barra = a.cdg_produto \n"
                        + "and qtd_por_emb > 1"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("id"));

                        if (codigoAtual > 0) {

                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());

                            imp.setImportId(rst.getString("id"));
                            imp.setQtdEmbalagem(rst.getInt("qtd_por_emb"));
                            imp.setPrecovenda(rst.getDouble("precovenda"));
                            imp.setAtacadoPreco(rst.getDouble("precoatacado"));

                            result.add(imp);
                        }
                    }
                }
            }

            return result;
        }

        if (opt == OpcaoProduto.MARGEM) {
            try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + " p.cdg_interno id_interno,\n"
                        + " mar.pct_margem \n"
                        + "from \n"
                        + "	informix.cadprod p\n"
                        + "join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno and \n"
                        + "	pa.cdg_estoque = p.cdg_produto\n"
                        + "join informix.estprfil est on p.cdg_produto = est.cdg_produto\n"
                        + "join informix.cadgrupo gr on pa.cdg_grupo = gr.cdg_grupo\n"
                        + "join informix.cadsubgr sg on pa.cdg_subgrupo = sg.cdg_subgrupo \n"
                        + "join informix.cadmargs mar on sg.cdg_subgrupo = mar.cdg_subgrupo \n"
                        + "join informix.cadsecao se on gr.cdg_secao = se.cdg_secao\n"
                        + "join informix.caddepto dp on se.cdg_depto = dp.cdg_depto\n"
                        + "where est.cdg_filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rst.getString("id_interno"));
                        imp.setMargem(rst.getDouble("pct_margem"));

                        result.add(imp);
                    }
                }
            }

            return result;
        }

        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	f.cdg_fornecedor id,\n"
                    + "	f.dcr_fornecedor razao,\n"
                    + "	f.dcr_fantasia fantasia,\n"
                    + "	f.nmr_ie ie,\n"
                    + "	f.nmr_im inscricaomunicipal,\n"
                    + " f.flb_simples_nac simples_nacional,\n"
                    + "	f.flb_contrib_icms indicador_ie,\n"
                    + "	f.dat_cadastro cadastro,\n"
                    + "	f.cdg_municipio ibgemunicipio,\n"
                    + "	f.dcr_cidade_old cidadeold,\n"
                    + "	f.dcr_endereco endereco,\n"
                    + "	f.nmr_endereco numero,\n"
                    + "	f.dcr_bairro bairro,\n"
                    + "	f.sgl_estado uf,\n"
                    + "	f.nmr_cep cep, \n"
                    + "	f.nmr_fone telefone,\n"
                    + "	f.nmr_fax fax,\n"
                    + "	f.dcr_vendedor vendedor,\n"
                    + "	f.nmr_fone_vend fonevendedor,\n"
                    + "	f.nmr_fax_vend faxvendedor,\n"
                    + "	f.cdg_cpag condicao,\n"
                    + "	f.dat_desativacao desativado,\n"
                    + "	f.dcr_email_vend emailvendedor,\n"
                    + "	f.dcr_email_pedido emailpedido,\n"
                    + "	f.dcr_assunto assunto\n"
                    + "from \n"
                    + "	cadforn f")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setCnpj_cpf(imp.getImportId());
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    if (imp.getInsc_municipal() == null) {
                        imp.setInsc_municipal("null");
                    }
                    
                    imp.setDatacadastro(rs.getDate("cadastro"));
                    imp.setIbge_municipio(rs.getInt("ibgemunicipio"));
                    imp.setMunicipio(rs.getString("cidadeold"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));

                    if (rs.getString("fax") != null && !"".equals(rs.getString("fax"))) {
                        imp.addContato("1", "FAX", rs.getString("fax"), null, TipoContato.NFE, null);
                    }

                    if (rs.getString("vendedor") != null && !"".equals(rs.getString("vendedor"))) {
                        imp.addContato("2", rs.getString("vendedor"), rs.getString("fonevendedor"), null, TipoContato.COMERCIAL, rs.getString("emailvendedor"));
                    }

                    if (rs.getString("faxvendedor") != null && !"".equals(rs.getString("faxvendedor"))) {
                        imp.addContato("3", "FAX VEND", rs.getString("faxvendedor"), null, TipoContato.COMERCIAL, null);
                    }

                    imp.setCondicaoPagamento(Integer.valueOf(Utils.formataNumero(rs.getString("condicao"))));

                    if (rs.getString("desativado") != null) {
                        imp.setAtivo(false);
                    }

                    if (rs.getString("emailpedido") != null && !"".equals(rs.getString("emailpedido"))) {
                        imp.addContato("4", "PEDIDO", null, null, TipoContato.COMERCIAL, rs.getString("emailpedido"));
                    }

                    if ("1".equals(rs.getString("simples_nacional"))) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                    } else {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    }

                    if ("1".equals(rs.getString("indicador_ie"))) {
                        imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ICMS);
                    } else {
                        imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	pf.cdg_fornecedor idfornecedor,\n"
                    + "	p.cdg_interno idproduto,\n"
                    + "	pf.cdg_prod_forn codigoexterno,\n"
                    + "	p.qtd_por_emb as qtdembalagem\n"
                    + "from \n"
                    + "	cadcodfor pf \n"
                    + "inner join cadforn f on pf.cdg_fornecedor = f.cdg_fornecedor \n"
                    + "inner join cadprod p on pf.cdg_produto = p.cdg_produto"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	c.nmr_cliente id,\n"
                    + "	c.cdg_cliente codigo,\n"
                    + "	c.dcr_cliente razao,\n"
                    + "	c.nmr_rg_ie ie,\n"
                    + "	c.nmr_rg rg,\n"
                    + "	c.dcr_endereco endereco,\n"
                    + "	c.dcr_compl_end complemento,\n"
                    + "	c.nmr_endereco numero,\n"
                    + "	c.cdg_municipio ibgemunicipio,\n"
                    + "	c.dcr_cidade_old municipio,\n"
                    + "	c.cdg_cidade idcidade,\n"
                    + "	c.dcr_bairro bairro,\n"
                    + "	c.sgl_estado uf,\n"
                    + "	c.nmr_fone fone,\n"
                    + "	c.nmr_fone2 fone2,\n"
                    + "	c.nmr_fone3 fone3,\n"
                    + "	c.nmr_celular celular,\n"
                    + "	c.dcr_funcao funcao,\n"
                    + "	c.dat_bloqueio bloqueio,\n"
                    + "	c.cdg_status situacao,\n"
                    + "	c.dat_cadastro cadastro,\n"
                    + "	c.dat_nascto nascimento,\n"
                    + "	c.nmr_fone_emp fonempresa,\n"
                    + "	c.dcr_email email,\n"
                    + "	c.dcr_email_xml emailxml,\n"
                    + "	c.flb_sexo sexo,\n"
                    + "	c.flb_estado_civil estadocivil,\n"
                    + "	c.val_limite_total_convenio limite\n"
                    + "from \n"
                    + "	cadcli c"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj(rs.getString("codigo"));
                    if (rs.getString("ie") != null && "ISENTO".equals(rs.getString("ie"))) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rs.getString("ie"));
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipioIBGE(rs.getString("ibgemunicipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("fone"));

                    if (rs.getString("fone2") != null && !"".equals(rs.getString("fone2"))) {
                        imp.addContato("1",
                                rs.getString("funcao") == null ? "" : rs.getString("funcao"),
                                rs.getString("fone2"), "", "");
                    }

                    imp.setAtivo(rs.getInt("situacao") == 0 ? true : false);

                    imp.setCelular(rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setEmail(rs.getString("email"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    if (rs.getString("sexo") != null) {
                        imp.setSexo("M".equals(rs.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
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

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	r.nmr_lancto id,\n"
                    + "	r.dat_venda emissao,\n"
                    + "	r.nmr_cliente idcliente,\n"
                    + "	r.val_docto_orig,\n"
                    + "	(r.val_docto - (case when r.val_recebido is null then 0 else r.val_recebido end)) valor,\n"
                    + "	r.dat_vecto vencimento,\n"
                    + "	r.nmr_docto documento,\n"
                    + "	r.nmr_docto_vnd docvenda\n"
                    + "from \n"
                    + "	recconta r\n"
                    + "where \n"
                    + "	r.nmr_cliente is not null and \n"
                    + "	r.val_recebido < r.val_docto or r.dat_recebto is null and \n"
                    + "	r.cdg_filial = " + getLojaOrigem() + "\n"
                    + "order by \n"
                    + "	r.dat_vecto"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setNumeroCupom(rs.getString("documento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.nmr_nosso_numero id,\n"
                    + "	p.cdg_fornecedor id_fornecedor,\n"
                    + "	p.nmr_docto numerodocumento,\n"
                    + "	p.nmr_parcela parcela,\n"
                    + "	p.dat_emissao dataemissao,\n"
                    + "	p.dat_entrada dataentrada,\n"
                    + "	p.dat_vecto vencimento,\n"
                    + "	p.val_pagar valor,\n"
                    + "	p.dcr_referente observacao\n"
                    + "from\n"
                    + "	pagconta p\n"
                    + "where\n"
                    + "	p.dat_pagto is null and p.cdg_filial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataentrada"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setVencimento(rst.getDate("vencimento"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getInt("parcela"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	 o.nmr_lancto id,\n"
                    + "	 p.cdg_interno idproduto,\n"
                    + "	 o.dat_preco_de datainicio,\n"
                    + "	 o.dat_preco_ate datatermino,\n"
                    + "	 o.val_preco_de preconormal,\n"
                    + "	 o.val_preco_ate precooferta\n"
                    + "from \n"
                    + "	bdoprpre o\n"
                    + "join cadprod p on o.cdg_produto = p.cdg_produto \n"
                    + "where \n"
                    + "	o.dat_preco_de is not null and \n"
                    + "	o.dat_preco_ate is not null and\n"
                    + "	o.dat_preco_ate > current\n"
                    + "order by \n"
                    + "	o.dat_proc_ate")) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datatermino"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        System.out.println(dataInicioVenda);
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        System.out.println(dataTerminoVenda);
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new LogusDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new LogusDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoInformix.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();
        private int contador = 0;

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Integer.parseInt(id));
                        next.setEcf(Integer.parseInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        //next.setIdClientePreferencial(rst.getString("id_cliente"));
                        //next.setCpf(rst.getString("cpf"));
                        //next.setNomeCliente(rst.getString("nomecliente"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	c.idvndpdv id_venda,\n"
                    + "	case when sat.nmr_extrato is null then c.idvndpdv else sat.nmr_extrato end numerocupom,\n"
                    + "	c.nmr_ecf ecf,\n"
                    + "	CAST(c.dat_emissao AS DATE) data,\n"
                    + "	CAST(c.dat_emissao AS DATETIME HOUR TO SECOND) hora,\n"
                    + "	c.val_venda valor,\n"
                    + "	c.val_desconto_itens desconto,\n"
                    + "	c.flb_cancelado cancelado\n"
                    + "FROM\n"
                    + "	vndpdv c\n"
                    + "	left join informix.vndpdvsat sat on c.idvndpdv = sat.idvndpdv\n"
                    + "WHERE\n"
                    + "	cast(c.dat_emissao as date) BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            System.out.println(sql);
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

        private Statement stm = ConexaoInformix.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nritem"));
                        next.setProduto(rst.getString("id_produto"));
                        //next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valorUnitario"));
                        next.setValorDesconto(rst.getDouble("desconto"));

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
                    + "	c.nmr_sequencia nritem,\n"
                    + "	c.idvndpdv id_venda,\n"
                    + "	c.idvnditempdv id_item,\n"
                    + "	cc.cdg_interno id_produto,\n"
                    + "	cc.dcr_etiq_gondola descricao,\n"
                    + "	c.qtd_produto quantidade,\n"
                    + "	c.val_unitario valorUnitario,\n"
                    + "	c.val_total valor,\n"
                    + "	c.val_desconto_item desconto\n"
                    + "FROM\n"
                    + "	vnditenspdv c\n"
                    + "	LEFT JOIN informix.cadprod cc ON c.cdg_produto = cc.cdg_produto \n"
                    + "WHERE\n"
                    + "	cast(c.dat_registro_item as date) BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'";
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
