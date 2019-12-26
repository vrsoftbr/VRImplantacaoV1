package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.gui.interfaces.GCFGUI;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.repositories.Recorder;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.local.EstadoVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class GCFDAO extends InterfaceDAO {
       
    public GCFGUI gui;
    private int nivel = 1;
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("d/MM/yyyy");

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    @Override
    public String getSistema() {
        return "GCF";
    }

    @Override
    public void setLojaOrigem(String LojaOrigem) {
        super.setLojaOrigem(LojaOrigem);
        if (gui != null) {
            gui.setLojaBalanca(LojaOrigem);
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        Map<String, Estabelecimento> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    tp.DBA_TIP_CODIGO_1 id,\n" +
                    "    tp.DBA_TIP_RAZAO_SOCIAL descricao\n" +
                    "from \n" +
                    "    A_CADCTIPO tp\n" +
                    "    join A_CADCLOJA lj on tp.DBA_TIP_CODIGO_1 = lj.DBA_LOJ_CODIGO_1\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        
        return new ArrayList<>(result.values());
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.NCM,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS
        ));
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            MultiMap<String, MercadologicoNivelIMP> niveis = new MultiMap<>();
            //Copia o nível 1
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    m.dba_ncc_secao merc1,\n" +
                    "    m.dba_ncc_grupo merc2,\n" +
                    "    m.dba_ncc_subgrupo merc3,\n" +
                    "    m.dba_ncc_descricao descricao\n" +
                    "from \n" +
                    "    A_CADCMERC m\n" +
                    "where\n" +
                    "    m.dba_ncc_grupo = 0 and\n" +
                    "    m.dba_ncc_subgrupo = 0\n" +
                    "order by\n" +
                    "    1,2,3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("descricao"));
                    niveis.put(imp, rst.getString("merc1"));
                    result.add(imp);                    
                }
            }
            //Copia o nível 2
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    m.dba_ncc_secao merc1,\n" +
                    "    m.dba_ncc_grupo merc2,\n" +
                    "    m.dba_ncc_subgrupo merc3,\n" +
                    "    m.dba_ncc_descricao descricao\n" +
                    "from \n" +
                    "    A_CADCMERC m\n" +
                    "where\n" +
                    "    m.dba_ncc_grupo != 0 and\n" +
                    "    m.dba_ncc_subgrupo = 0\n" +
                    "order by\n" +
                    "    1,2,3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = niveis.get(
                            rst.getString("merc1")
                    ).addFilho(
                            rst.getString("merc2"),
                            rst.getString("descricao")
                    );
                    niveis.put(imp, rst.getString("merc1"), rst.getString("merc2"));
                }
            }
            //Copia o nível 3
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    m.dba_ncc_secao merc1,\n" +
                    "    m.dba_ncc_grupo merc2,\n" +
                    "    m.dba_ncc_subgrupo merc3,\n" +
                    "    m.dba_ncc_descricao descricao\n" +
                    "from \n" +
                    "    A_CADCMERC m\n" +
                    "where\n" +
                    "    m.dba_ncc_grupo != 0 and\n" +
                    "    m.dba_ncc_subgrupo != 0\n" +
                    "order by\n" +
                    "    1,2,3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = niveis.get(
                            rst.getString("merc1"), 
                            rst.getString("merc2")
                    ).addFilho(
                            rst.getString("merc3"),
                            rst.getString("descricao")
                    );
                    niveis.put(imp, rst.getString("merc1"), rst.getString("merc2"), rst.getString("merc3"));
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
                    "select\n" +
                    "    item.DBA_GIT_PRODUTO id,\n" +
                    "    item.DBA_GIT_DESC_COML descricao\n" +
                    "from\n" +
                    "    A_CADCITEM item\n" +
                    "    join (select distinct \n" +
                    "            DBA_GIT_CODIGO_PAI \n" +
                    "        from \n" +
                    "            A_CADCITEM a \n" +
                    "        where \n" +
                    "            DBA_GIT_CODIGO_PAI != 0) fam on\n" +
                    "            item.DBA_GIT_PRODUTO = fam.DBA_GIT_CODIGO_PAI"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            Map<String, int[]> pisCofinsList = new HashMap<>();
            
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "       cast(substr(dba_atributos_acesso,8,8) as numeric(14,0)) as produto,\n" +
                    "       cast(substr(dba_atributos_acesso,24,4) as numeric(10,0)) as ano,       \n" +
                    "       cast(substr(dba_atributos_acesso,28,2) as numeric(10,0)) as mes,\n" +
                    "       cast(substr(dba_atributos_acesso,30,2) as numeric(10,0)) as dia,\n" +
                    "       substr(dba_atributos_conteudo,0,2) as piscofinscredito, \n" +
                    "       substr(dba_atributos_conteudo,length(dba_atributos_conteudo)-3,2) as piscofinsdebito\n" +
                    "from A_ATRIBUTOS  a \n" +
                    "where length(dba_atributos_acesso) = 31  \n" +
                    "and dba_atributos_acesso like 'PRD%'\n" +
                    "order by produto, ano, mes, dia"
            )) {
                while (rst.next()) {
                    pisCofinsList.put(rst.getString("produto"), new int[] { 
                        Utils.stringToInt(rst.getString("piscofinscredito")),
                        Utils.stringToInt(rst.getString("piscofinsdebito"))
                    });
                }
            }
            
            
            EstadoVO uf = Parametros.get().getUfPadraoV2();
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.DBA_GIT_PRODUTO id,\n" +
                    "    P.DBA_GIT_DAT_ENT_LIN DATACADASTRO,\n" +
                    "    EAN.BARRA EAN,\n" +
                    "    p.DBA_GIT_EMB_VENDA QTD,\n" +
                    "    EAN.QTD QTD_COMPRA,\n" +
                    "    EAN.UN,\n" +
                    "    P.DBA_GIT_TPO_EMB_VENDA TIPOEMBALAGEM,\n" +
                    "    P.DBA_GIT_PRZ_VALIDADE VALIDADE,\n" +
                    "    P.DBA_GIT_DESCRICAO_COMPL DESCRICAOCOMPLETA,\n" +
                    "    P.DBA_GIT_DESC_REDUZ_COMPL DESCRICAOREDUZIDA,\n" +
                    "    P.DBA_GIT_SECAO COD_MERC1,\n" +
                    "    P.DBA_GIT_GRUPO COD_MERC2,\n" +
                    "    P.DBA_GIT_SUBGRUPO COD_MERC3,\n" +
                    "    NULLIF(P.DBA_GIT_CODIGO_PAI,0) ID_FAMILIA,\n" +
                    "    P.DBA_GIT_PESO_VND PESO,\n" +
                    "    EST.DBA_CADCESTQ_ESTOQUE ESTOQUE,\n" +
                    "    coalesce(merc.dba_ncc_margem1, merc.dba_ncc_margem2, merc.dba_ncc_margem3, MRG.DBA_CAD_MRG_LUCRO) MARGEM,\n" +
                    "    P.DBA_GIT_CUS_REP CUSTOMSEMIMPOSTO,\n" +
                    "    P.DBA_GIT_CUS_ULT_ENT_BRU CUSTOCOMIMPOSTO,\n" +
                    "    p.DBA_GIT_PRC_VEN" + getNivel() + " PRECO,\n" +
                    "    case WHEN P.DBA_GIT_DAT_SAI_LIN = 0 THEN 0 ELSE 1 END descontinuado,\n" +
                    "    P.DBA_GIT_CLASS_FIS NCM,\n" +
                    "    PIS.piscofinscredito,\n" +
                    "    PIS.piscofinsDEBITO,\n" +
                    "	ICMS.TRIBUTACAO,\n" +
                    "	ICMS.ALIQUOTA,\n" +
                    "	ICMS.REDUCAO,\n" +
                    "   nr.dba_pcnatrec_codigo as piscofinsNaturezaReceita,\n" +
                    "	CEST.dba_itematrib_cestq_fiscal cest,\n" +
                    "	TO_NUMBER(SUBSTR(P.DBA_GIT_FILLER, 97, 5))/1000 AS MARGEM_NIVEL_1,\n" +
                    "   TO_NUMBER(SUBSTR(P.DBA_GIT_FILLER, 102, 5))/1000 AS MARGEM_NIVEL_2,\n" +
                    "   TO_NUMBER(SUBSTR(P.DBA_GIT_FILLER, 107, 5))/1000 AS MARGEM_NIVEL_3,\n" +
                    "   TO_NUMBER(SUBSTR(P.DBA_GIT_FILLER, 112, 5))/1000 AS MARGEM_NIVEL_4,\n" +
                    "   TO_NUMBER(SUBSTR(P.DBA_GIT_FILLER, 117, 5))/1000 AS MARGEM_NIVEL_5\n" +
                    "from\n" +
                    "    A_CADCITEM p\n" +
                    "    LEFT JOIN A_CADCLOJA EMP ON EMP.DBA_LOJ_CODIGO_1 = " + getLojaOrigem() + "\n" +
                    "    LEFT JOIN A_CADCESTQ EST ON P.DBA_GIT_PRODUTO = EST.DBA_CADCESTQ_COD_PRODUTO AND EMP.DBA_LOJ_CODIGO_1 = EST.DBA_CADCESTQ_COD_LOCAL\n" +
                    "    LEFT JOIN A_AG1PDVST MRG ON MRG.DBA_CAD_PROD_1 = P.DBA_GIT_PRODUTO AND MRG.DBA_CAD_FILIAL = EMP.DBA_LOJ_CODIGO_1\n" +
                    "    LEFT JOIN A_CADCITEMATRIB CEST ON dba_itematrib_git_produto = P.DBA_GIT_PRODUTO\n" +
                    "    left join A_CADCMERC merc on p.dba_git_secao = merc.dba_ncc_secao and p.dba_git_grupo = merc.dba_ncc_grupo and p.dba_git_subgrupo = merc.dba_ncc_subgrupo\n" +
                    "    LEFT join A_PCNATREC NR on nr.dba_pcnatrec_prod_ncm = P.DBA_GIT_PRODUTO\n" +
                    "    left JOIN (\n" +
                    "         SELECT\n" +
                    "              DBA_GIT_PRODUTO AS ID, \n" +
                    "          	DBA_GIT_EAN_EMB_VENDA AS BARRA,\n" +
                    "            DBA_GIT_EMB_VENDA    QTD,\n" +
                    "            DBA_GIT_TPO_EMB_VENDA UN\n" +
                    "          FROM\n" +
                    "              A_CADCITEM\n" +
                    "          WHERE\n" +
                    "              DBA_GIT_EAN_EMB_VENDA != 0 AND\n" +
                    "              NOT DBA_GIT_EAN_EMB_VENDA IN (SELECT DISTINCT DBA_EAN_COD_EAN FROM A_CADCMEAN)\n" +
                    "          UNION\n" +
                    "          select \n" +
                    "          	DBA_EAN_COD_PRO_ALT AS ID, \n" +
                    "          	DBA_EAN_COD_EAN AS BARRA,\n" +
                    "            DBA_EAN_EMB_VENDA    QTD,\n" +
                    "            DBA_EAN_TPO_EMB_VENDA UN\n" +
                    "          from\n" +
                    "          	A_CADCMEAN\n" +
                    "          WHERE \n" +
                    "          	NOT (DBA_EAN_COD_EAN LIKE '3000%' AND LENGTH(DBA_EAN_COD_EAN) = 13)\n" +
                    "    ) EAN ON EAN.ID = P.DBA_GIT_PRODUTO\n" +
                    "    LEFT JOIN (\n" +
                    "         select distinct \n" +
                    "               cast(substr(dba_atributos_acesso,8,8) as numeric(14,0)) as produto,  \n" +
                    "               substr(dba_atributos_conteudo,0,2) as piscofinscredito, \n" +
                    "               substr(dba_atributos_conteudo,length(dba_atributos_conteudo)-3,2) as piscofinsdebito \n" +
                    "        from A_ATRIBUTOS  a \n" +
                    "        where length(dba_atributos_acesso) = 31  \n" +
                    "        and dba_atributos_acesso like 'PRD%' \n" +
                    "        order by 1\n" +
                    "    ) PIS ON PIS.PRODUTO = P.DBA_GIT_PRODUTO\n" +
                    "    LEFT JOIN (\n" +
                    "		select DISTINCT \n" +
                    "			a.DBA_GIT_PRODUTO PRODUTO, \n" +
                    "			B.DBA_TFIS_CODIGO_FIS_DEST AS TRIBUTACAO, \n" +
                    "			NVL(b.dba_tfis_aliq_icm,0) AS ALIQUOTA, \n" +
                    "			b.DBA_TFIS_BASE_REDUZ as REDUCAO \n" +
                    "		FROM \n" +
                    "			A_CADCITEM A, \n" +
                    "			A_CADCTFIS B \n" +
                    "		WHERE \n" +
                    "			A.DBA_GIT_NAT_FISCAL = B.DBA_TFIS_FIGURA \n" +
                    "			AND B.DBA_TFIS_ORIGEM = " + SQLUtils.stringSQL(uf.getSigla()) + " \n" +
                    "			AND B.DBA_TFIS_DESTINO = " + SQLUtils.stringSQL(uf.getSigla()) + " \n" +
                    "			AND B.DBA_TFIS_AUTOMACAO = 'S' \n" +
                    "			AND B.dba_tfis_cfop_fiscal IN (5102, 5405) \n" +
                    "			and b.dba_tfis_codigo = 512 \n" +
                    "    ) ICMS ON ICMS.PRODUTO = P.DBA_GIT_PRODUTO"
            )) {
                
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    String data = rst.getString("DATACADASTRO");
                    imp.setDataCadastro(getData(data));
                    imp.setEan(rst.getString("EAN"));
                    ProdutoBalancaVO bal = balanca.get(Utils.stringToInt(rst.getString("EAN")));
                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(bal.getValidade());
                        if ("U".equals(bal.getPesavel())) {
                            imp.setTipoEmbalagem("UN");
                        } else {
                            imp.setTipoEmbalagem("KG");
                        }
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setQtdEmbalagem(rst.getInt("QTD"));
                        imp.setTipoEmbalagem(rst.getString("TIPOEMBALAGEM"));
                        imp.setValidade(rst.getInt("VALIDADE"));
                    }
                    imp.setQtdEmbalagemCotacao(rst.getInt("QTD_COMPRA"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAOCOMPLETA"));
                    imp.setDescricaoGondola(rst.getString("DESCRICAOCOMPLETA"));
                    imp.setDescricaoReduzida(rst.getString("DESCRICAOREDUZIDA"));
                    imp.setCodMercadologico1(rst.getString("COD_MERC1"));
                    imp.setCodMercadologico2(rst.getString("COD_MERC2"));
                    imp.setCodMercadologico3(rst.getString("COD_MERC3"));
                    imp.setIdFamiliaProduto(rst.getString("ID_FAMILIA"));
                    if (imp.getIdFamiliaProduto() == null) {
                        imp.setIdFamiliaProduto(imp.getImportId());
                    }
                    imp.setPesoBruto(rst.getDouble("PESO"));
                    imp.setPesoLiquido(rst.getDouble("PESO"));
                    imp.setEstoque(rst.getDouble("ESTOQUE"));
                    imp.setMargem(rst.getDouble("MARGEM_NIVEL_1"));
                    imp.setCustoComImposto(rst.getDouble("CUSTOCOMIMPOSTO"));
                    imp.setCustoSemImposto(rst.getDouble("CUSTOMSEMIMPOSTO"));
                    imp.setPrecovenda(rst.getDouble("PRECO"));
                    imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
                    
                    int[] pisCofins = pisCofinsList.get(imp.getImportId());
                    if (pisCofins == null) {                    
                        imp.setPiscofinsCstCredito(rst.getInt("piscofinscredito"));
                        imp.setPiscofinsCstDebito(rst.getInt("piscofinsDEBITO"));
                    } else {
                        imp.setPiscofinsCstCredito(pisCofins[0]);
                        imp.setPiscofinsCstDebito(pisCofins[1]);
                    }
                    imp.setIcmsCst(rst.getInt("TRIBUTACAO"));
                    imp.setIcmsAliq(rst.getDouble("ALIQUOTA"));
                    imp.setIcmsReducao(rst.getDouble("REDUCAO"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofinsNaturezaReceita"));
                    
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
                    "select\n" +
                    "      t.dba_tip_codigo_1 id,\n" +
                    "      t.dba_tip_razao_social razao,\n" +
                    "      t.dba_tip_nome_fantasia fantasia,\n" +
                    "      t.dba_tip_cgc_cpf cnpj,\n" +
                    "      t.dba_tip_insc_est_ident ie,\n" +
                    "      t.dba_tip_insc_mun insc_mun,\n" +
                    "      t.dba_tip_endereco endereco,\n" +
                    "      t.dba_tip_numero_end numero,\n" +
                    "      t.dba_tip_bairro bairro,\n" +
                    "      t.dba_tip_cidade municipio,\n" +
                    "      t.dba_tip_estado uf,\n" +
                    "      t.dba_tip_cep cep,\n" +
                    "      t.dba_tip_fone_ddd || t.dba_tip_fone_num telefone,\n" +
                    "      t.dba_tip_data_cad datacadastro,\n" +
                    "      f.dba_for_prz_entrega prazo_entrega,\n" +
                    "      f.dba_for_freq_visita visita,\n" +
                    "      f.dba_for_cond1 condicao_pag,\n" +
                    "      f.dba_for_dia_visita dia_visita\n" +
                    "from \n" +
                    "     A_CADCTIPO t\n" +
                    "     join A_CADCFORN f on f.dba_for_codigo || f.dba_for_dig_for = t.dba_tip_codigo_1\n" +
                    "order by t.dba_tip_codigo_1"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("d/MM/yyyy");
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("insc_mun"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    String data = rst.getString("datacadastro");
                    if (data.length() == 8) {
                        String dia = data.substring(0, 2);
                        String mes = data.substring(2, 4);
                        String ano = data.substring(4, 8);
                        imp.setDatacadastro(format.parse(dia + "/" + mes + "/" + ano));
                    } else if (data.length() == 7) {
                        String dia = data.substring(0, 1);
                        String mes = data.substring(1, 3);
                        String ano = data.substring(3, 7);
                        imp.setDatacadastro(format.parse(dia + "/" + mes + "/" + ano));
                    } else {
                        imp.setDatacadastro(new Date());
                    }
                    imp.setPrazoEntrega(rst.getInt("prazo_entrega"));
                    imp.setPrazoVisita(rst.getInt("visita"));
                    imp.setCondicaoPagamento(rst.getInt("condicao_pag"));
                    
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
                    "select\n" +
                    "	CAST(pf.dba_assoc_cod_forn AS VARCHAR(50)) ID_FORNECEDOR,\n" +
                    "	pf.dba_assoc_cod_prod_gcf ID_PRODUTO,\n" +
                    "	p.DBA_GIT_CUS_REP CUSTOTABELA,\n" +
                    "	pf.dba_assoc_cod_prod_forn CODIGOEXTERNO,\n" +
                    "  pf.dba_assoc_base_emb qtdembalagem,\n" +
                    "  pf.dba_assoc_dt dataalteracao\n" +
                    "from\n" +
                    "	a_recnfeassocembforn pf\n" +
                    "	join a_cadcitem p on pf.dba_assoc_cod_prod_gcf = p.dba_git_produto\n" +
                    "order by\n" +
                    "      1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("ID_FORNECEDOR"));
                    imp.setIdProduto(rst.getString("ID_PRODUTO"));
                    imp.setCustoTabela(rst.getDouble("CUSTOTABELA"));
                    imp.setCodigoExterno(rst.getString("CODIGOEXTERNO"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setDataAlteracao(getData(rst.getString("dataalteracao")));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    t.dba_tip_codigo_1 id,\n" +
                    "    t.DBA_TIP_CGC_CPF cnpj,\n" +
                    "    t.DBA_TIP_INSC_EST_IDENT inscricaoestadual,\n" +
                    "    t.DBA_TIP_RAZAO_SOCIAL razao,\n" +
                    "    t.DBA_TIP_NOME_FANTASIA fantasia,\n" +
                    "    case when c.DBA_CLI_SITUACAO != 'A' then 0 else 1 end as ativo,\n" +
                    "    t.DBA_TIP_ENDERECO endereco,\n" +
                    "    t.DBA_TIP_NUMERO_END numero,\n" +
                    "    t.DBA_TIP_BAIRRO bairro,\n" +
                    "    t.DBA_TIP_CIDADE municipio,\n" +
                    "    t.DBA_TIP_ESTADO estado,\n" +
                    "    t.DBA_TIP_CEP cep,\n" +
                    "    t.DBA_TIP_DATA_CAD datacadastro,\n" +
                    "    c.DBA_CLI_FAT_DIA diavencimento,\n" +
                    "    nullif(coalesce(t.DBA_TIP_FONE_DDD,0) || coalesce(t.DBA_TIP_FONE_NUM,0),'00') fone,\n" +
                    "    nullif(coalesce(t.DBA_TIP_FAX_DDD,0) || coalesce(t.DBA_TIP_FAX_NUM,0),'00') fax,\n" +
                    "    t.DBA_TIP_INSC_MUN inscricaomunicipal\n" +
                    "from\n" +
                    "    A_CADCTIPO t\n" +
                    "    join A_CADCCLIR c on t.DBA_TIP_CODIGO_1 = c.DBA_CLI_COD_DIG\n" +
                    "order by\n" +
                    "    t.dba_tip_codigo_1"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("dMMyyyy");
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(format.parse(Utils.formataNumero(rst.getString("datacadastro"), "1012000")));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setFax(rst.getString("fax"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    rec.DBA_KEYDUPALT_SQ id,   \n" +
                    "    rec.DBA_DUP_DT_INCLUSAO dataemissao,\n" +
                    //"    rec.DBA_DUP_TITULO || rec.DBA_DUP_DESD numerocupom,\n" +
                    "    rec.DBA_DUP_DOCUMENTO_FIS numerocupom,\n" +
                    "    rec.DBA_DUP_VALOR valor,\n" +
                    "    t.DBA_TIP_CODIGO_1 id_cliente,\n" +
                    "    rec.DBA_DUP_VENC vencimento,\n" +
                    "    rec.DBA_DUP_PARCELA parcela,\n" +
                    "    rec.DBA_DUP_CGC_CPF cnpj\n" +
                    "from\n" +
                    "    A_RECCTITU rec\n" +
                    "    join A_CADCTIPO t on rec.dba_dup_cod_cli || rec.dba_dup_dig_cli = t.DBA_TIP_CODIGO_1\n" +
                    "where\n" +
                    "    rec.dba_dup_dt_pag = 0 and\n" +
                    "    t.dba_tip_codigo_1 != 200000 and\n" +
                    "    rec.dba_dup_filial || rec.dba_dup_dig_filial = '" + getLojaOrigem() + "'\n" +
                    "order by\n" +
                    "    rec.DBA_DUP_DT_INCLUSAO"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("d/MM/yyyy");
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(format.parse(rst.getString("dataemissao")));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(format.parse(rst.getString("vencimento")));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setCnpjCliente(rst.getString("cnpj"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private Date getData(String data) throws ParseException {
        if (data.length() == 8) {
            String dia = data.substring(0, 2);
            String mes = data.substring(2, 4);
            String ano = data.substring(4, 8);
            return (FORMAT.parse(dia + "/" + mes + "/" + ano));
        } else if (data.length() == 7) {
            String dia = data.substring(0, 1);
            String mes = data.substring(1, 3);
            String ano = data.substring(3, 7);
            return (FORMAT.parse(dia + "/" + mes + "/" + ano));
        } else {
            return new Date();
        }
    }
    
    
    
}
