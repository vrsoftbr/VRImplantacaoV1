package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.PagarOutrasDespesasDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVencimentoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemAnteriorDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemDAO;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class RMSDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "RMS";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select \n" +
                "    loj_codigo id,\n" +
                "    loj_digito dig\n" +
                "from\n" +
                "    AA2CLOJA\n" +
                "order by\n" +
                "    loj_codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id") + rst.getString("dig"), "LOJA " + rst.getString("id")));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT \n" +
                    "    F.FAM_PAI AS CODIGO,\n" +
                    "    P.GIT_DESCRICAO AS DESCRICAO\n" +
                    "FROM \n" +
                    "    AA1FITEM  F\n" +
                    "    JOIN AA3CITEM  P ON P.GIT_COD_ITEM = F.FAM_PAI\n" +
                    "where\n" +
                    "    (select count(*) from AA1FITEM where FAM_PAI = F.FAM_PAI) > 1\n" +
                    "ORDER BY \n" +
                    "    F.FAM_PAI"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
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
                    "select distinct \n" +
                    "    nvl(m.ncc_secao,0) AS MERC1,\n" +
                    "    nvl(m.ncc_descricao,'MERCADOLOGICO VR') AS merc1_desc     \n" +
                    "from \n" +
                    "    aa3cnvcc m\n" +
                    "where\n" +
                    "    m.ncc_secao != 0 and\n" +
                    "    m.ncc_grupo = 0 and\n" +
                    "    m.ncc_subgrupo = 0 and\n" +
                    "    m.ncc_categoria = 0" 
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_desc"));
                    
                    merc.put(imp.getId(), imp);
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n" +
                    "    nvl(m.ncc_secao, 0) AS MERC1,\n" +
                    "    nvl(m.ncc_grupo, 0) as merc2,\n" +
                    "    nvl(m.ncc_descricao,'MERCADOLOGICO VR') AS merc2_desc     \n" +
                    "from \n" +
                    "    aa3cnvcc m\n" +
                    "where    \n" +
                    "    m.ncc_secao != 0 and\n" +
                    "    m.ncc_grupo != 0 and\n" +
                    "    m.ncc_subgrupo = 0 and\n" +
                    "    m.ncc_categoria = 0" 
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        merc1.addFilho(
                            rst.getString("merc2"),
                            rst.getString("merc2_desc")
                        );
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n" +
                    "    nvl(m.ncc_secao, 0) AS MERC1,\n" +
                    "    nvl(m.ncc_grupo, 0) as merc2,\n" +
                    "    nvl(m.ncc_subgrupo, 0) as merc3,\n" +
                    "    nvl(m.ncc_descricao,'MERCADOLOGICO VR') AS merc3_desc\n" +
                    "from \n" +
                    "    aa3cnvcc m\n" +
                    "where\n" +
                    "    m.ncc_secao != 0 and\n" +
                    "    m.ncc_grupo != 0 and\n" +
                    "    m.ncc_subgrupo != 0 and\n" +
                    "    m.ncc_categoria = 0" 
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                rst.getString("merc3"),
                                rst.getString("merc3_desc")
                            );
                        }
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n" +
                    "    nvl(m.ncc_secao, 0) AS merc1,\n" +
                    "    nvl(m.ncc_grupo, 0) as merc2,\n" +
                    "    nvl(m.ncc_subgrupo, 0) as merc3,\n" +
                    "    nvl(m.ncc_categoria, 0) as merc4,\n" +
                    "    nvl(m.ncc_descricao,'MERCADOLOGICO VR') AS merc4_desc " +
                    "from \n" +
                    "    aa3cnvcc m\n" +
                    "where\n" +
                    "    m.ncc_secao != 0 and\n" +
                    "    m.ncc_grupo != 0 and\n" +
                    "    m.ncc_subgrupo != 0 and\n" +
                    "    m.ncc_categoria != 0" 
            )) {
                while (rst.next()) { 
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            MercadologicoNivelIMP merc3 = merc2.getNiveis().get(rst.getString("merc3"));
                            if (merc3 != null) {
                                merc3.addFilho(
                                    rst.getString("merc4"),
                                    rst.getString("merc4_desc")
                                );
                            }
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
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.git_cod_item id,\n" +
                    "    p.git_cod_item||p.git_digito codigosped,\n" +
                    "    p.GIT_DAT_ENT_LIN datacadastro,\n" +
                    "    p.GIT_EMB_FOR qtdembalagemcotacao,\n" +
                    "    ean.EAN_COD_EAN ean,\n" +
                    "    ean.EAN_EMB_VENDA qtdEmbalagem,\n" +
                    "    ean.EAN_TPO_EMB_VENDA tipoEmbalagem,\n" +
                    "    case when bal.balcol_codigo is null then 0 else 1 end e_balanca,\n" +
                    "    coalesce(bal.BALCOL_VALIDADE, 0) validade,\n" +
                    "    p.GIT_DESCRICAO descricaocompleta,\n" +
                    "    p.GIT_DESC_REDUZ descricaoreduzida,\n" +
                    "    p.GIT_DESCRICAO descricaogondola,\n" +
                    "    p.GIT_SECAO merc1,\n" +
                    "    p.GIT_GRUPO merc2,\n" +
                    "    p.GIT_SUBGRUPO merc3,\n" +
                    "    p.GIT_CATEGORIA merc4,\n" +
                    "    coalesce((select fam_pai from AA1FITEM where fam_filho = p.git_cod_item and rownum = 1), (select fam_pai from AA1FITEM where fam_pai = p.git_cod_item and rownum = 1)) id_familia,\n" +
                    "    coalesce(preco.id_situacaocadastral, 1) id_situacaocadastral,\n" +
                    "    det.DET_PESO_VND pesoliquido,\n" +
                    "    det.DET_PESO_TRF pesobruto,\n" +
                    "    0 estoqueminimo,\n" +
                    "    0 estoquemaximo,    \n" +
                    "    est.GET_ESTOQUE estoque,\n" +
                    "    p.GIT_MRG_LUCRO_1 margem,\n" +
                    "    case when coalesce(preco.preco, 0) != 0 \n" +
                    "    then preco.preco\n" +
                    "    else coalesce(est.get_preco_venda,0) end precovenda,\n" +
                    "    est.GET_CUS_ULT_ENT custocomimposto,\n" +
                    "    est.GET_CUS_ULT_ENT custosemimposto,\n" +
                    "    det.DET_CLASS_FIS ncm,\n" +
                    "    det.DET_NCM_EXCECAO excecao,\n" +
                    "    det.DET_CEST cest,\n" +
                    "    trib.piscofins_debito,\n" +
                    "    det.DET_NAT_REC nat_rec,\n" +
                    "    trib.icms_cst,\n" +
                    "    trib.icms_aliq,\n" +
                    "    trib.icms_red,\n" +
                    "    coalesce(\n" +
                    "        atac.precoatac,\n" +
                    "        (case when coalesce(preco.preco, 0) != 0 \n" +
                    "            then preco.preco\n" +
                    "            else coalesce(est.get_preco_venda,0) end)\n" +
                    "    ) precoatac\n" +
                    "from\n" +
                    "    AA3CCEAN ean\n" +
                    "    join AA3CITEM p on\n" +
                    "        ean.EAN_COD_PRO_ALT = p.GIT_COD_ITEM || p.git_digito\n" +
                    "    left join AA2CLOJA loja on\n" +
                    "        loja.loj_codigo || loja.loj_digito =   " + SQLUtils.stringSQL(getLojaOrigem()) + "\n" +
                    "    left join AG1PBACO bal on\n" +
                    "        bal.BALCOL_CODIGO = p.GIT_COD_ITEM and\n" +
                    "        bal.BALCOL_DIGITO = p.GIT_DIGITO and\n" +
                    "        bal.BALCOL_FILIAL = loja.loj_codigo || loja.loj_digito\n" +
                    "    left join AA1DITEM det on\n" +
                    "        p.GIT_COD_ITEM = det.DET_COD_ITEM\n" +
                    "    join AA2CESTQ est on\n" +
                    "        est.GET_COD_PRODUTO = p.GIT_COD_ITEM || p.GIT_DIGITO and\n" +
                    "        est.GET_COD_LOCAL = loja.LOJ_CODIGO || loja.LOJ_DIGITO\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            PDV_FILIAL filial,\n" +
                    "            PDV_ITEM id,\n" +
                    "            PDV_ITEM_DIGITO digito,\n" +
                    "            max(PDV_CUSTO) custo,\n" +
                    "            max(cast((PDV_PRECO_NORMAL / PDV_EMB_VENDA_UN) as numeric(10,2))) preco,\n" +
                    "            max(case when PDV_EXCLUIR = 'S' then 0 else 1 end) id_situacaocadastral\n" +
                    "        from\n" +
                    "            AG1PDVPD\n" +
                    "        group by\n" +
                    "            PDV_FILIAL,\n" +
                    "            PDV_ITEM,\n" +
                    "            PDV_ITEM_DIGITO\n" +
                    "        ) preco on\n" +
                    "            preco.filial = loja.LOJ_CODIGO and\n" +
                    "            preco.id = p.GIT_COD_ITEM and\n" +
                    "            preco.digito = p.GIT_DIGITO\n" +
                    "    left join (\n" +
                    "        select distinct\n" +
                    "            pdv.PDV_FILIAL filial,\n" +
                    "            pdv.PDV_ITEM id,\n" +
                    "            pdv.PDV_ITEM_DIGITO digito,\n" +
                    "            pdv.PDV_SIT_TRIBUT icms_cst,\n" +
                    "            pdv.PDV_TRIBUT icms_aliq,\n" +
                    "            pdv.PDV_REDUCAO icms_red,\n" +
                    "            pdv.PDV_CST_PIS piscofins_debito\n" +
                    "        from\n" +
                    "            AG1PDVPD pdv\n" +
                    "        where pdv.PDV_EXCLUIR != 'S'\n" +
                    "        ) trib on\n" +
                    "        trib.filial = loja.LOJ_CODIGO and\n" +
                    "        trib.id = p.GIT_COD_ITEM and\n" +
                    "        trib.digito = p.GIT_DIGITO\n" +
                    "    left join (\n" +
                    "        select distinct\n" +
                    "            pdv.PDV_FILIAL filial,\n" +
                    "            pdv.PDV_CODIGO_EAN13 ean,\n" +
                    "            pdv.PDV_PRECO_NORMAL / pdv.PDV_EMB_VENDA_UN precoatac,\n" +
                    "            pdv.PDV_TPO_EMB_VENDA tipoembalagem,\n" +
                    "            pdv.PDV_EMB_VENDA qtd\n" +
                    "        from\n" +
                    "            AG1PDVPD pdv\n" +
                    "        ) atac on\n" +
                    "        atac.filial = loja.LOJ_CODIGO and\n" +
                    "        atac.ean = ean.EAN_COD_EAN\n" +
                    "order by p.git_cod_item"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setCodigoSped(rst.getString("codigosped"));
                    imp.setDataCadastro(format.parse(rst.getString("datacadastro")));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1("0".equals(rst.getString("merc1")) ? "" : rst.getString("merc1"));
                    imp.setCodMercadologico2("0".equals(rst.getString("merc2")) ? "" : rst.getString("merc2"));
                    imp.setCodMercadologico3("0".equals(rst.getString("merc3")) ? "" : rst.getString("merc3"));
                    imp.setCodMercadologico4("0".equals(rst.getString("merc4")) ? "" : rst.getString("merc4"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(Utils.stringToInt(rst.getString("id_situacaocadastral"))));
                    imp.setPesoBruto(rst.getDouble("pesoliquido"));
                    imp.setPesoLiquido(rst.getDouble("pesobruto"));
                    
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_debito"));
                    imp.setPiscofinsCstCredito(0);
                    imp.setPiscofinsNaturezaReceita(rst.getInt("nat_rec"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducao(rst.getDouble("icms_red"));
                    imp.setAtacadoPreco(rst.getDouble("precoatac"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	F.TIP_CODIGO||F.TIP_DIGITO id,\n" +
                    "	F.TIP_RAZAO_SOCIAL razao,\n" +
                    "	F.TIP_NOME_FANTASIA fantasia,\n" +
                    "	F.TIP_CGC_CPF cnpj,\n" +
                    "	F.TIP_INSC_EST_IDENT inscricaoestadual,\n" +
                    "	F.TIP_INSC_MUN insc_municipal,\n" +
                    "	F.TIP_ENDERECO endereco,\n" +
                    "	F.TIP_BAIRRO bairro,\n" +
                    "	F.TIP_CIDADE cidade,\n" +
                    "	F.TIP_ESTADO uf,\n" +
                    "	F.TIP_CEP cep,\n" +
                    "	F.TIP_DATA_CAD datacadastro,\n" +
                    "	coalesce(cast(F.TIP_FONE_DDD as varchar(20)), '') ||	cast(F.TIP_FONE_NUM as varchar(20)) fone1,\n" +
                    "	case when not F.TIP_TELEX_NUM is null then coalesce(cast(F.TIP_TELEX_DDD as varchar(20)), '') || cast(F.TIP_TELEX_NUM as varchar(20)) else null end fone2,\n" +
                    "	case when not F.TIP_FAX_NUM is null then coalesce(cast(F.TIP_FAX_DDD as varchar(20)), '') || cast(F.TIP_FAX_NUM as varchar(20)) else null end fax,\n" +
                    "    F2.FOR_COND_1 condicaopag,\n" +
                    "    f2.FOR_PRZ_ENTREGA entrega,\n" +
                    "    f2.FOR_FREQ_VISITA visita,\n" +
                    "    f3.FOR_PED_MIN_EMB qtd_pedido_minimo,\n" +
                    "    f3.FOR_PED_MIN_VLR valor_pedido_minimo\n" +
                    "FROM\n" +
                    "	AA2CTIPO F\n" +
                    "    left join AA2CFORN f2 on f.TIP_CODIGO = f2.FOR_CODIGO and F.TIP_DIGITO = f2.FOR_DIG_FOR \n" +
                    "    left join AA1FORDT f3 on f.TIP_CODIGO = f3.FOR_CODIGO\n" +
                    "WHERE\n" +
                    "	F.TIP_LOJ_CLI = 'F'\n" +
                    "order by\n" +
                    "    id"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rst.getString("insc_municipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(format.parse(rst.getString("datacadastro")));
                    imp.setTel_principal(rst.getString("fone1"));                    
                    if (Utils.stringToLong(rst.getString("fone2")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("FONE 2");
                        cont.setTelefone(Utils.stringLong(rst.getString("fone2")));
                        imp.getContatos().put(cont, "1");
                    }
                    if (Utils.stringToLong(rst.getString("fax")) > 0) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTelefone(Utils.stringLong(rst.getString("fax")));
                    }
                    imp.setQtd_minima_pedido(rst.getInt("qtd_pedido_minimo"));
                    imp.setValor_minimo_pedido(rst.getDouble("valor_pedido_minimo"));
                    imp.setPrazoEntrega(rst.getInt("entrega"));
                    imp.setPrazoVisita(rst.getInt("visita"));
                    imp.setPrazoSeguranca(0);
                    imp.setCondicaoPagamento(rst.getInt("condicaopag"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();   
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "	FORITE_COD_FORN||FORITE_DIG_FORN AS FORNECEDOR,\n" +
                    "	GIT_COD_ITEM PRODUTO,\n" +
                    //"	GIT_COD_ITEM||GIT_DIGITO PRODUTO,\n" +
                    "	FORITE_REFERENCIA AS REFERENCIA,\n" +
                    "	PROD.GIT_EMB_FOR AS EMBALAGEM  \n" +
                    "FROM \n" +
                    "	AA1FORIT FORN\n" +
                    "	join AA3CITEM PROD on\n" +
                    "		PROD.GIT_COD_ITEM = FORN.FORITE_COD_ITEM"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setQtdEmbalagem(rst.getInt("embalagem"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    result.add(imp);
                }
            }
        }        
        
        return result;
    }    

    @Override
    public List<ClienteIMP> getClientesEventuais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(                        
                    "SELECT\n" +
                    "	F.TIP_CODIGO||F.TIP_DIGITO id,\n" +
                    "	F.TIP_RAZAO_SOCIAL razao,\n" +
                    "	F.TIP_NOME_FANTASIA fantasia,\n" +
                    "	F.TIP_CGC_CPF cnpj,\n" +
                    "	F.TIP_INSC_EST_IDENT inscricaoestadual,\n" +
                    "	F.TIP_INSC_MUN insc_municipal,\n" +
                    "	F.TIP_ENDERECO endereco,\n" +
                    "	F.TIP_BAIRRO bairro,\n" +
                    "	F.TIP_CIDADE cidade,\n" +
                    "	F.TIP_ESTADO uf,\n" +
                    "	F.TIP_CEP cep,\n" +
                    "	F.TIP_DATA_CAD datacadastro,\n" +
                    "	coalesce(cast(F.TIP_FONE_DDD as varchar(20)), '') ||	cast(F.TIP_FONE_NUM as varchar(20)) fone1,\n" +
                    "	case when not F.TIP_TELEX_NUM is null then coalesce(cast(F.TIP_TELEX_DDD as varchar(20)), '') || cast(F.TIP_TELEX_NUM as varchar(20)) else null end fone2,\n" +
                    "	case when not F.TIP_FAX_NUM is null then coalesce(cast(F.TIP_FAX_DDD as varchar(20)), '') || cast(F.TIP_FAX_NUM as varchar(20)) else null end fax,\n" +
                    "    F2.FOR_COND_1 prazoPagamento,\n" +
                    "    f2.FOR_PRZ_ENTREGA entrega,\n" +
                    "    f2.FOR_FREQ_VISITA visita,\n" +
                    "    f3.FOR_PED_MIN_EMB qtd_pedido_minimo,\n" +
                    "    f3.FOR_PED_MIN_VLR valor_pedido_minimo\n" +
                    "FROM\n" +
                    "	AA2CTIPO F\n" +
                    "    left join AA2CFORN f2 on f.TIP_CODIGO = f2.FOR_CODIGO and F.TIP_DIGITO = f2.FOR_DIG_FOR \n" +
                    "    left join AA1FORDT f3 on f.TIP_CODIGO = f3.FOR_CODIGO\n" +
                    "WHERE\n" +
                    "	F.TIP_LOJ_CLI = 'C'\n" +
                    "order by\n" +
                    "    id"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rst.getString("insc_municipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(format.parse(rst.getString("datacadastro")));
                    imp.setTelefone(rst.getString("fone1"));                    
                    if (Utils.stringToLong(rst.getString("fone2")) > 0) {
                        imp.addContato("1", "FONE 2", Utils.stringLong(rst.getString("fone2")), "", "");
                    }
                    if (Utils.stringToLong(rst.getString("fax")) > 0) {
                        imp.addContato("2", "FAX", Utils.stringLong(rst.getString("fax")), "", "");
                    }
                    imp.setPrazoPagamento(rst.getInt("prazoPagamento"));                         

                    result.add(imp);
                }                
            }
        }
        
        return result;                        
    }

    
    
    @Override
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (Statement stm2 = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(                        
                        "select \n" +
                        "    cli.cli_codigo||cli.cli_digito id,\n" +
                        "    cli.cli_codigo idSemDigito,\n" +
                        "    cli.CLI_CPF_CNPJ cnpj,\n" +
                        "    cli.CLI_RG_INSC_EST inscricaoestadual,\n" +
                        "    cli.CLI_ORG_EMIS orgaoemissor,\n" +
                        "    cli.CLI_NOME razao,\n" +
                        "    cli.CLI_NOME fantasia,\n" +
                        "    1 ativo,\n" +
                        "    case when cli.CLI_STATUS = 0 then 0 else 1 end bloqueado,\n" +
                        "    ender.END_RUA endereco,\n" +
                        "    ender.END_NRO numero,\n" +
                        "    ender.END_COMPL complemento,\n" +
                        "    ender.END_BAIRRO bairro,\n" +
                        "    ender.END_CID cidade,\n" +
                        "    ender.END_UF uf,\n" +
                        "    ender.END_CEP cep,\n" +
                        "    cli.CLI_ESTADO_CIVIL estadoCivil,\n" +
                        "    cli.CLI_DTA_NASC dataNascimento,\n" +
                        "    cli.CLI_DTA_CAD dataCadastro,\n" +
                        "    coalesce(cli.CLI_SEXO, 'M') sexo,\n" +
                        "    cli.CLI_NOME_EMPRESA empresa,\n" +
                        "    cli.CLI_DTA_ADMIS dataAdmissao,\n" +
                        "    cli.CLI_CARGO cargo,\n" +
                        "    cli.CLI_SALARIO salario,\n" +
                        "    coalesce(lim_ch.LIM_LIMITE,0) limite_cheque,\n" +
                        "    coalesce(lim_rt.LIM_LIMITE,0) limite_rotativo,\n" +
                        "    coalesce(lim_cv.LIM_LIMITE,0) limite_convenio,\n" +
                        "    cli.CLI_NOME_PAI nomepai,\n" +
                        "    cli.CLI_NOME_MAE nomemae\n" +
                        "from \n" +
                        "    CAD_CLIENTE cli\n" +
                        "    left join END_CLIENTE ender on\n" +
                        "        cli.cli_codigo = ender.cli_codigo\n" +
                        "        and ender.end_tpo_end = 1\n" +
                        "    left join AC1QLIMI lim_ch on\n" +
                        "        cli.cli_codigo = lim_ch.lim_codigo\n" +
                        "        and cli.cli_digito = lim_ch.LIM_DIGITO\n" +
                        "        and lim_ch.LIM_MODALIDADE = 1\n" +
                        "    left join AC1QLIMI lim_rt on\n" +
                        "        cli.cli_codigo = lim_rt.lim_codigo\n" +
                        "        and cli.cli_digito = lim_rt.LIM_DIGITO\n" +
                        "        and lim_rt.LIM_MODALIDADE = 2\n" +
                        "    left join AC1QLIMI lim_cv on\n" +
                        "        cli.cli_codigo = lim_cv.lim_codigo\n" +
                        "        and cli.cli_digito = lim_cv.LIM_DIGITO\n" +
                        "        and lim_cv.LIM_MODALIDADE = 3\n" +
                        "order by \n" +
                        "    cli.cli_codigo"
                )) {
                    SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                    while (rst.next()) {
                        ClienteIMP imp = new ClienteIMP();
                        imp.setId(rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj"));
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                        imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setAtivo(true);
                        imp.setBloqueado(rst.getBoolean("bloqueado"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setUf(rst.getString("uf"));
                        imp.setCep(rst.getString("cep"));
                        switch(rst.getInt("estadocivil")) {
                            case 1: imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO); break;
                            case 2: imp.setEstadoCivil(TipoEstadoCivil.CASADO); break;
                            case 3: imp.setEstadoCivil(TipoEstadoCivil.VIUVO); break;
                            case 4: imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO); break;
                            default: imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO); break;
                        }
                        imp.setDataNascimento(rst.getDate("datanascimento"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        switch (rst.getString("sexo")) {
                            case "F": imp.setSexo(TipoSexo.FEMININO); break;
                            default: imp.setSexo(TipoSexo.MASCULINO); break;
                        }
                        imp.setEmpresa(rst.getString("empresa"));
                        imp.setDataAdmissao(rst.getDate("dataadmissao"));
                        imp.setCargo(rst.getString("cargo"));
                        imp.setSalario(rst.getDouble("salario"));
                        imp.setValorLimite(rst.getDouble("limite_rotativo"));
                        imp.setNomePai(rst.getString("nomepai"));
                        imp.setNomeMae(rst.getString("nomemae"));

                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n" +
                                "    CLI_CODIGO idCliente,\n" +
                                "    CONT_COD_SEQ seq,\n" +
                                "    CONT_TIPO tipo,\n" +
                                "    cast(CONT_DDD||CONT_NUMERO as numeric) telefone,\n" +
                                "    CONT_RAMAL ramal,\n" +
                                "    CONT_OBS obs\n" +
                                "from \n" +
                                "    CONTATO_CLIENTE\n" +
                                "where \n" +
                                "    CLI_CODIGO = " + rst.getString("idSemDigito") + "\n" +
                                "order by\n" +
                                "    CLI_CODIGO, CONT_COD_SEQ"
                        )) {
                            while (rst2.next()) {
                                if (imp.getTelefone() == null) {
                                    imp.setTelefone(rst2.getString("telefone"));
                                } else {
                                    ClienteContatoIMP cont = new ClienteContatoIMP();
                                    if (rst2.getInt("tipo") == 1) {
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("RESIDENCIA");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    } else if (rst2.getInt("tipo") == 2) {                                        
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("COMERCIAL");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    } else if (rst2.getInt("tipo") == 3) {                                        
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("CELULAR");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    } else if (rst2.getInt("tipo") == 4) {                                        
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("FAX");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    } else if (rst2.getInt("tipo") == 5) {                                        
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("RECADOS");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    }
                                    imp.getContatos().add(cont);
                                }
                            }
                        }                    

                        result.add(imp);
                    }
                }
            }
        }
        
        return result;
    }

    public void importarPagamentoRotativo() throws Exception {        
        Conexao.begin();
        try {
            Map<String, Double> pagamentos = new HashMap<>();

            ProgressBar.setStatus("Importando pagamentos...");
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    lan.lanc_codigo,\n" +
                        "    sum(lan.lanc_valor) valor\n" +
                        "from\n" +
                        "    ac1clanc lan\n" +
                        "    join CAD_CLIENTE c on\n" +
                        "        lan.lanc_codigo = c.cli_codigo||c.cli_digito\n" +
                        "where\n" +
                        "    lan.lanc_tipo = 2\n" +
                        "    and lan.lanc_modalidade in (2, 3)\n" +
                        "    and c.cli_convenio = 0\n" +
                        "group by\n" +
                        "    lan.lanc_codigo\n" +
                        "order by\n" +
                        "    lanc_codigo"
                )) {
                    while (rst.next()) {
                        double valor = rst.getDouble("valor");
                        if (valor < 0) {
                            valor *= -1;
                        }
                        pagamentos.put(rst.getString("lanc_codigo"), MathUtils.trunc(valor, 2));
                    }
                }
            }

            System.out.println("Pagamentos: " + pagamentos.size() + " (209015) = " + pagamentos.get("209015"));
            
            CreditoRotativoDAO rotDao = new CreditoRotativoDAO();
            CreditoRotativoItemDAO dao = new CreditoRotativoItemDAO();
            CreditoRotativoItemAnteriorDAO antDao = new CreditoRotativoItemAnteriorDAO();
            MultiMap<String, CreditoRotativoItemAnteriorVO> baixasAnteriores = antDao.getBaixasAnteriores(null, null);

            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	 ant.sistema,\n" +
                        "    ant.loja,\n" +
                        "    ant.id_cliente,\n" +
                        "    ant.id,\n" +
                        "    ant.codigoatual,\n" +
                        "    r.id_loja,\n" +
                        "    r.valor,\n" +
                        "    r.datavencimento\n" +
                        "from \n" +
                        "	implantacao.codant_recebercreditorotativo ant\n" +
                        "    join recebercreditorotativo r on\n" +
                        "    	ant.codigoatual = r.id\n" +
                        "order by\n" +
                        "	ant.id_cliente, r.datavencimento"
                )) {
                    int cont1 = 0, cont2 = 0;
                    while (rst.next()) {
                        
                        String sistema = rst.getString("sistema");
                        String loja = rst.getString("loja");
                        String idCliente = rst.getString("id_cliente");
                        String idRotativo = rst.getString("id");
                        int codigoAtual = rst.getInt("codigoatual");
                        int id_loja = rst.getInt("id_loja");
                        double valor = rst.getDouble("valor");
                        Date vencimento = rst.getDate("datavencimento");
                        
                        if ( !baixasAnteriores.containsKey(sistema, loja, idRotativo, idRotativo) ) {
                            if ( pagamentos.containsKey(idCliente) ) {                                
                                double valorPagoTotal = pagamentos.get(idCliente);
                                if (valorPagoTotal > 0) {
                                    double valorParc;
                                    if (valorPagoTotal >= valor) {
                                        valorPagoTotal -= valor;
                                        valorParc = valor;
                                    } else {
                                        valorParc = valorPagoTotal;
                                        valorPagoTotal = 0;
                                    }

                                    CreditoRotativoItemVO pag = new CreditoRotativoItemVO();
                                    pag.setId_receberCreditoRotativo(codigoAtual);
                                    pag.setValor(valorParc);
                                    pag.setValorTotal(valorParc);
                                    pag.setDatabaixa(vencimento);
                                    pag.setDataPagamento(vencimento);
                                    pag.setObservacao("IMPORTADO VR");
                                    pag.setId_loja(id_loja);

                                    dao.gravarRotativoItem(pag);

                                    CreditoRotativoItemAnteriorVO ant = new CreditoRotativoItemAnteriorVO();
                                    ant.setSistema(sistema);
                                    ant.setLoja(loja);
                                    ant.setIdCreditoRotativo(idRotativo);
                                    ant.setId(idRotativo);
                                    ant.setCodigoAtual(pag.getId());
                                    ant.setDataPagamento(vencimento);
                                    ant.setValor(pag.getValor());

                                    antDao.gravarRotativoItemAnterior(ant);
                                    
                                    rotDao.verificarBaixado(codigoAtual);

                                    pagamentos.put(idCliente, valorPagoTotal);
                                    baixasAnteriores.put(ant, 
                                            ant.getSistema(),
                                            ant.getLoja(),
                                            ant.getIdCreditoRotativo(),
                                            ant.getId()
                                    );
                                }
                            }
                        } 
                        cont1++;
                        cont2++;
                        
                        if (cont1 == 1000) {
                            cont1 = 0;
                            ProgressBar.setStatus("Importando pagamentos..." + cont2);
                        }
                    }
                }
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    lan.lanc_codigo||'-'||lan.lanc_data||'-'||lan.lanc_seq id,\n" +
                    "    lan.lanc_codigo,\n" +
                    "    lan.lanc_data,\n" +
                    "    lan.lanc_seq,\n" +
                    "    lan.lanc_loja id_loja,\n" +
                    "    lan.lanc_data emissao,\n" +
                    "    case lan.lanc_cupom when 0 then lan.lanc_documento else lan.lanc_cupom end numerocupom,\n" +
                    "    lan.lanc_caixa ecf,\n" +
                    "    lan.lanc_valor valor,\n" +
                    "    lan.lanc_historico historico,\n" +
                    "    lan.lanc_usuario usuario,\n" +
                    "    lan.lanc_codigo idcliente,\n" +
                    "    lan.lanc_vencimento vencimento,\n" +
                    "    decode(lan.lanc_parcela, 0, 1, lan.lanc_parcela) parcela,\n" +
                    "    lan.lanc_vlr_juros juros,\n" +
                    "    lan.lanc_vlr_multa multa,\n" +
                    "    c.cli_convenio\n" +
                    "from\n" +
                    "    ac1clanc lan\n" +
                    "    join CAD_CLIENTE c on\n" +
                    "        lan.lanc_codigo = c.cli_codigo||c.cli_digito\n" +
                    "where\n" +
                    "    lan.lanc_tipo = 1\n" +
                    "    and lan.lanc_modalidade in (2, 3)\n" +
                    "    and c.cli_convenio = 0\n" +
                    "    and lan.lanc_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n" +
                    "order by\n" +
                    "    lanc_codigo,\n" +
                    "    lanc_data,\n" +
                    "    lanc_seq"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(format.parse(rst.getString("emissao")));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(
                            (rst.getString("usuario") != null ? " USUARIO: " + rst.getString("usuario") : "") +
                            (rst.getString("historico") != null ? " HISTORICO: " + rst.getString("historico") : "")
                    );
                    imp.setIdCliente(rst.getString("idcliente"));
                    try {
                        imp.setDataVencimento(format.parse(rst.getString("vencimento")));
                    } catch (ParseException e) {
                        imp.setDataVencimento(imp.getDataEmissao());
                        System.out.println("**ERRO DE PARSING - vencimento: " + rst.getString("id") + " valor: " + rst.getString("valor"));                        
                    }
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));
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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "    ch.CHE_CODIGO||'-'||ch.CHE_BANCO||'-'||ch.CHE_AGENCIA||'-'||ch.CHE_CONTA_CORRENTE||'-'||ch.CHE_CHEQUE id,\n" +
                    "    ch.CHE_LOJA id_loja,\n" +
                    "    cli.CLI_CPF_CNPJ cpf,\n" +
                    "    ch.CHE_CHEQUE numeroCheque,\n" +
                    "    ch.CHE_BANCO banco,\n" +
                    "    ch.CHE_AGENCIA agencia,\n" +
                    "    ch.CHE_CONTA_CORRENTE conta,\n" +
                    "    ch.CHE_EMISSAO data,\n" +
                    "    ch.CHE_VALOR valor,\n" +
                    "    cli.CLI_RG_INSC_EST rg,\n" +
                    "    (select cast(CONT_DDD||CONT_NUMERO as numeric) from CONTATO_CLIENTE where CLI_CODIGO = cli.CLI_CODIGO and ROWNUM = 1) telefone,\n" +
                    "    cli.CLI_NOME nome,\n" +
                    "    ch.CHE_STATUS status,\n" +
                    "    ch.CHE_CMC7 cmc7,\n" +
                    "    ch.CHE_ALINEA alinea,\n" +
                    "    ch.CHE_VLR_JUROS juros,\n" +
                    "    ch.CHE_DTA_STATUS dataHoraAlteracao\n" +
                    "FROM \n" +
                    "    AC1CCHEQ ch\n" +
                    "    left join CAD_CLIENTE cli on\n" +
                    "        ch.che_codigo = cli.CLI_CODIGO||cli.CLI_DIGITO\n" +
                    "where \n" +
                    "    ch.CHE_STATUS = 1\n" +
                    "    and ch.CHE_LOJA = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1)
            )) {
                System.out.println(getLojaOrigem().substring(0, getLojaOrigem().length() - 1));
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroCheque(rst.getString("numeroCheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(format.parse(rst.getString("data")));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setCmc7(rst.getString("cmc7"));
                    imp.setAlinea(rst.getInt("alinea"));
                    imp.setValorJuros(rst.getDouble("juros"));
                    imp.setDataHoraAlteracao(new Timestamp(format.parse(rst.getString("dataHoraAlteracao")).getTime()));
                    result.add(imp);
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
                    "SELECT\n" +
                    "    emp.CONV_CODIGO id,\n" +
                    "    emp.CONV_DESCRICAO razao,\n" +
                    "    cli.TIP_CGC_CPF cnpj,\n" +
                    "    cli.TIP_INSC_EST_IDENT inscricaoestadual,\n" +
                    "    cli.TIP_ENDERECO endereco,\n" +
                    "    cli.TIP_BAIRRO bairro,\n" +
                    "    cli.TIP_CIDADE cidade,\n" +
                    "    cli.TIP_ESTADO uf,\n" +
                    "    cli.TIP_CEP cep,\n" +
                    "    cast(cli.TIP_FONE_DDD||cli.TIP_FONE_NUM as numeric) fone1,\n" +
                    "    cicl.cicl_dta_inicio dataInicio,\n" +
                    "    cicl.cicl_dta_fim dataTermino,\n" +
                    "    emp.CONV_DESCONTO desconto,\n" +
                    "    emp.CONV_DIA_COBRANCA diapagamento,\n" +
                    "    emp.CONV_DIA_CORTE diainiciorenovacao,\n" +
                    "    emp.CONV_BLOQUEAR bloquear\n" +
                    "FROM \n" +
                    "    AC1CCONV emp\n" +
                    "    left join AA2CTIPO cli on\n" +
                    "        emp.conv_emp_codigo = cli.TIP_CODIGO and\n" +
                    "        emp.conv_emp_digito = cli.TIP_DIGITO\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            *\n" +
                    "        from\n" +
                    "            AC2CVCIC c\n" +
                    "        where\n" +
                    "            c.cicl_codigo = \n" +
                    "            (select max(cicl_codigo) from AC2CVCIC where conv_codigo = c.CONV_CODIGO)\n" +
                    "    ) cicl on\n" +
                    "        emp.conv_codigo = cicl.conv_codigo\n" +
                    "order by \n" +
                    "    emp.CONV_CODIGO"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero("0");
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTelefone(rst.getString("fone1"));
                    imp.setDataInicio(format.parse(rst.getString("datainicio")));
                    imp.setDataTermino(format.parse(rst.getString("datatermino")));
                    imp.setDesconto(rst.getDouble("desconto"));
                    imp.setDiaPagamento(rst.getInt("diapagamento"));
                    imp.setDiaInicioRenovacao(rst.getInt("diainiciorenovacao"));
                    imp.setBloqueado(rst.getBoolean("bloquear"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "    cli.cli_codigo||cli.cli_digito id,\n" +
                    "    cli.CLI_CPF_CNPJ cnpj,\n" +
                    "    cli.CLI_NOME razao,\n" +
                    "    cli.CLI_FIL_CAD loja,\n" +
                    "    cli.cli_convenio idEmpresa,\n" +
                    "    case when cli.CLI_STATUS = 0 then 0 else 1 end bloqueado,\n" +
                    "    coalesce(lim_cv.LIM_LIMITE,0) limite_convenio,\n" +
                    "    coalesce(conv.CONV_DESCONTO,0) desconto\n" +
                    "from \n" +
                    "    CAD_CLIENTE cli\n" +
                    "    left join END_CLIENTE ender on\n" +
                    "        cli.cli_codigo = ender.cli_codigo\n" +
                    "        and ender.end_tpo_end = 1\n" +
                    "    left join AC1QLIMI lim_cv on\n" +
                    "        cli.cli_codigo = lim_cv.lim_codigo\n" +
                    "        and cli.cli_digito = lim_cv.LIM_DIGITO\n" +
                    "        and lim_cv.LIM_MODALIDADE = 3\n" +
                    "    left join AC1CCONV conv on\n" +
                    "        conv.CONV_CODIGO = cli.CLI_convenio\n" +
                    "where\n" +
                    "    cli.cli_convenio > 0\n" +
                    "order by \n" +
                    "    cli.cli_codigo"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setNome(rst.getString("razao"));
                    imp.setIdEmpresa(rst.getString("idEmpresa"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setConvenioLimite(rst.getDouble("limite_convenio"));
                    imp.setConvenioDesconto(rst.getDouble("desconto"));    
                    imp.setLojaCadastro(rst.getString("loja"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    lan.lanc_codigo||'-'||lan.lanc_data||'-'||lan.lanc_seq id,\n" +
                    "    lan.lanc_loja id_loja,\n" +
                    "    lan.lanc_codigo idcliente,\n" +
                    "    lan.lanc_caixa ecf,\n" +
                    "    case lan.lanc_cupom when 0 then lan.lanc_documento else lan.lanc_cupom end numerocupom,\n" +
                    "    lan.lanc_data dataHora,\n" +
                    "    lan.lanc_valor valor,\n" +
                    "    lan.lanc_historico historico,\n" +
                    "    emp.CONV_DIA_CORTE,\n" +
                    "    cicl.cicl_dta_inicio dataInicio,\n" +
                    "    cicl.cicl_dta_fim dataTermino\n" +
                    "from\n" +
                    "    ac1clanc lan\n" +
                    "    join CAD_CLIENTE c on\n" +
                    "        lan.lanc_codigo = c.cli_codigo||c.cli_digito\n" +
                    "    join AC1CCONV emp on\n" +
                    "        c.cli_convenio = emp.conv_codigo\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            *\n" +
                    "        from\n" +
                    "            AC2CVCIC c\n" +
                    "        where\n" +
                    "            c.cicl_codigo = \n" +
                    "            (select max(cicl_codigo) from AC2CVCIC where conv_codigo = c.CONV_CODIGO)\n" +
                    "    ) cicl on\n" +
                    "        emp.conv_codigo = cicl.conv_codigo\n" +
                    "where\n" +
                    "    lan.lanc_tipo = 1\n" +
                    "    and lan.lanc_modalidade = 3\n" +
                    "    and c.cli_convenio > 0\n" +
                    "    and lan.lanc_data >= cicl.cicl_dta_inicio\n" +
                    "    and cicl_dta_fim >= cast('1' || (extract(year from current_date) - 2000) || \n" +
                    "    (lpad(extract(month from current_date), 2, '0')) ||\n" +
                    "    (lpad(extract(day from current_date), 2, '0')) as numeric)\n" +
                    "    and lan.lanc_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n" +
                    "order by\n" +
                    "    lan.lanc_codigo,\n" +
                    "    lan.lanc_data,\n" +
                    "    lan.lanc_seq"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("idCliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataHora(new Timestamp(format.parse(rst.getString("datahora")).getTime()));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("historico"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    public void importarOfertas(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertas(idLojaVR, idLojaCliente);
        
        new OfertaDAO().salvar(ofertas, idLojaVR);
    }
    
    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    i.pof_loja id_loja,\n" +
                    "    i.pof_cod_item id_produto,\n" +
                    "    o.ofta_ini_vig datainicio,\n" +
                    "    o.ofta_fim_vig datafim,\n" +
                    "    i.POF_PRECO_OFTA precooferta\n" +
                    "from \n" +
                    "    AA1PROFT i\n" +
                    "    join AA1COFTA o on\n" +
                    "        i.POF_COD_OFERTA = o.OFTA_COD_OFERTA\n" +
                    "where\n" +
                    "    o.OFTA_FIM_VIG >= (1||substr(extract(year from current_date),3,4) || \n" +
                    "        lpad(extract(month from current_date), 2, '0') ||\n" +
                    "        lpad(extract(day from current_date), 2, '0'))\n" +
                    "    and i.POF_LOJA = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1)
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setDatainicio(new Date(format.parse(rst.getString("datainicio")).getTime()));
                    vo.setDatatermino(new Date(format.parse(rst.getString("datafim")).getTime()));
                    vo.setPrecooferta(rst.getDouble("precooferta"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }

    public void importarContasAPagar(int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando as Contas a Pagar...");
        List<PagarOutrasDespesasVO> vPagarOutrasDespesas = getContasAPagar(idLojaVR);
        PagarOutrasDespesasDAO pagarOutrasDespesasDAO = new PagarOutrasDespesasDAO();
        pagarOutrasDespesasDAO.salvar(vPagarOutrasDespesas);
    }

    private List<PagarOutrasDespesasVO> getContasAPagar(int idLojaVR) throws Exception {
        List<PagarOutrasDespesasVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cp.cpd_cgc_cpf cnpj,\n" +
                    "    cp.cpd_forpri id_fornecedor,\n" +
                    "    cp.cpd_forne,\n" +
                    "    cp.cpd_ntfis numerodocumento,\n" +
                    "    210 tipoentrada,\n" +
                    "    cp.cpd_emissao dataemissao,\n" +
                    "    cp.cpd_dt_emi,\n" +
                    "    cp.cpd_recep dataentrada,\n" +
                    "    cp.cpd_dt_inclusao,\n" +
                    "    cp.cpd_vrnota valor,\n" +
                    "    cp.cpd_loja id_loja,\n" +
                    "    cp.cpd_dta_baixa,\n" +
                    "    cp.cpd_ven_org,\n" +
                    "    cp.cpd_serie,\n" +
                    "    cp.cpd_ntfis,\n" +
                    "    cp.cpd_vlr_pago_cheque,\n" +
                    "    cp.cpd_vlr_pago_dinhei,\n" +
                    "    cp.cpm_banco,\n" +
                    "    cp.cpd_agencia_emp,\n" +
                    "    cp.cpd_conta_emp,\n" +
                    "    cp.cpm_ncheq,\n" +
                    "    cp.cpm_venc datavencimento\n" +
                    "from\n" +
                    "    ag1pagcp cp\n" +
                    "where cp.cpd_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n" +
                    "   and cp.cpd_emissao >= 170601\n" +
                    "order by\n" +
                    "    cp.cpd_emissao"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                while (rst.next()) {
                    PagarOutrasDespesasVO vo = new PagarOutrasDespesasVO();
                    
                    vo.setId_loja(idLojaVR);
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setNumerodocumento(rst.getInt("numerodocumento"));
                    vo.setId_tipoentrada(rst.getInt("tipoentrada"));
                    vo.setDataemissao(new Date(format.parse(rst.getString("dataemissao")).getTime()));
                    vo.setDataentrada(new Date(format.parse(rst.getString("dataentrada")).getTime()));
                    vo.setValor(rst.getDouble("valor"));
                    PagarOutrasDespesasVencimentoVO dup = new PagarOutrasDespesasVencimentoVO();
                    dup.setDatavencimento(new Date(format.parse(rst.getString("datavencimento")).getTime()));
                    dup.setValor(vo.getValor());
                    vo.getvPagarOutrasDespesasVencimento().add(dup);
                    vo.setObservacao("IMPORTADO VR");
                    
                    result.add(vo);
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
                    "select\n" +
                    "    cp.cpd_cgc_cpf cnpj,\n" +
                    "    F.TIP_CODIGO||F.TIP_DIGITO id_fornecedor,\n" +
                    "    cp.cpd_forne,\n" +
                    "    cp.cpd_ntfis numerodocumento,\n" +
                    "    210 tipoentrada,\n" +
                    "    cp.cpd_emissao dataemissao,\n" +
                    "    cp.cpd_dt_emi,\n" +
                    "    cp.cpd_recep dataentrada,\n" +
                    "    cp.cpd_dt_inclusao,\n" +
                    "    cp.cpd_vrnota valor,\n" +
                    "    cp.cpd_loja id_loja,\n" +
                    "    cp.cpd_dta_baixa,\n" +
                    "    cp.cpd_ven_org,\n" +
                    "    cp.cpd_serie,\n" +
                    "    cp.cpd_ntfis,\n" +
                    "    cp.cpd_vlr_pago_cheque,\n" +
                    "    cp.cpd_vlr_pago_dinhei,\n" +
                    "    cp.cpm_banco,\n" +
                    "    cp.cpd_agencia_emp,\n" +
                    "    cp.cpd_conta_emp,\n" +
                    "    cp.cpm_ncheq,\n" +
                    "    cp.cpm_venc datavencimento\n" +
                    "from\n" +
                    "    ag1pagcp cp\n" +
                    "    join AA2CTIPO F on\n" +
                    "         cp.cpd_forne = f.TIP_CODIGO\n" +
                    "where cp.cpd_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n" +
                    "   and cp.cpd_emissao >= 170601\n" +
                    "order by\n" +
                    "    cp.cpd_emissao"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                while (rst.next()) {
                    ContaPagarIMP vo = new ContaPagarIMP();
                    
                    vo.setId(rst.getString("id_fornecedor") );
                    vo.setIdFornecedor(rst.getString("id_fornecedor"));
                    vo.setDataHoraAlteracao(new Timestamp(format.parse(rst.getString("dataemissao")).getTime()));
                    vo.setDataEmissao(new Date(format.parse(rst.getString("dataemissao")).getTime()));
                    vo.setDataEntrada(new Date(format.parse(rst.getString("dataentrada")).getTime()));
                    vo.setNumeroDocumento(rst.getString("numerodocumento"));
                    vo.setFinalizada(false);
                    vo.setValor(rst.getDouble("valor"));
                    vo.addVencimento(new Date(format.parse(rst.getString("datavencimento")).getTime()), vo.getValor());
                    
                    result.add(vo);
                }
            }
        }
        
        return result;
    }
    
}
