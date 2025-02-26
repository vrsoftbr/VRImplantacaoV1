package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vr.core.utils.StringUtils;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class AlterData_WShopDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(AlterData_WShopDAO.class.getName());
    
    private String complemento = "";
    public boolean somenteBalanca = false;

    public void setComplemento(String complemento) {
        if (complemento == null) {
            complemento = "";
        }
        this.complemento = complemento.trim();
    }

    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "WShop";
        } else {
            return "WShop - " + complemento;
        }
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
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
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA
                }
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	idgrupo merc1,\n"
                    + "	nmgrupo merc1_desc\n"
                    + "from\n"
                    + "	wshop.grupo\n"
                    + "order by \n"
                    + "	nmgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cdempresa, nrcgc || '-' || nmempresa razao from wshop.empshop order by cdempresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cdempresa"), rst.getString("razao")));
                }
            }
        }

        return result;
    }

    private Map<String, String> mapaFamilia;

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        Set<String> fam = mapearFamilia();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            for (String key : fam) {
                try (ResultSet rst = stm.executeQuery(
                        "select iddetalhe, dsdetalhe from wshop.detalhe where iddetalhe = '" + key + "'"
                )) {
                    while (rst.next()) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("iddetalhe"));
                        imp.setDescricao(rst.getString("dsdetalhe"));
                        result.add(imp);
                    }
                }
            }
        }

        return result;
    }

    private Set<String> mapearFamilia() throws SQLException {
        Set<List<String>> itens = new LinkedHashSet<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	iddetalhe,\n"
                    + "	iddetalhe iddetalheequivalente\n"
                    + "from\n"
                    + "	wshop.prodequivalente\n"
                    + "union\n"
                    + "select \n"
                    + "	iddetalhe, \n"
                    + "	iddetalheequivalente \n"
                    + "from \n"
                    + "	wshop.ProdEquivalente \n"
                    + "order by \n"
                    + "	1, 2"
            )) {
                String lastKey = null;

                List<String> listaAtual = null;
                while (rst.next()) {
                    String key = rst.getString("iddetalhe");
                    String value = rst.getString("iddetalheequivalente");
                    if (!key.equals(lastKey)) {
                        if (listaAtual != null && !itens.add(listaAtual)) {
                            LOG.finer("lista " + listaAtual.get(0) + " já existe!");
                        }
                        listaAtual = new ArrayList<>();
                        lastKey = key;
                    }
                    listaAtual.add(value);
                }
                System.out.println("Lista: " + itens.size());
            }
        }
        mapaFamilia = new HashMap<>();
        Set<String> fam = new HashSet<>();
        for (List<String> lista : itens) {
            fam.add(lista.get(0));
            for (String item : lista) {
                mapaFamilia.put(item, lista.get(0));
            }
        }
        return fam;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with icms_usados as (\n" +
                    "	select distinct p.idcalculoicms from wshop.produto p where not nullif(trim(idcalculoicms), '') is null\n" +
                    ")\n" +
                    "select distinct\n" +
                    "	icms.idcalculoicms||'-'||icms.stexp id,\n" +
                    "	i.dscalculoicms,\n" +
                    "	icms.idcfop,\n" +
                    "	icms.cdsituacaotributaria cst,\n" +
                    "	icms.alicms aliq,\n" +
                    "	icms.alreducaobaseicms reducao\n" +
                    "from\n" +
                    "	wshop.icms_uf icms\n" +
                    "	join wshop.empshop emp on \n" +
                    "		emp.cdempresa = '" + getLojaOrigem() + "' and\n" +
                    "		iduf = emp.cduf and\n" +
                    "		idufdestino = emp.cduf\n" +
                    "	join wshop.icms i on\n" +
                    "		icms.idcalculoicms = i.idcalculoicms\n" +
                    "where\n" +
                    "	icms.idcalculoicms in (select idcalculoicms from icms_usados) and\n" +
                    "	icms.idcfop in (\n" +
                    "		'5101',\n" +
                    "		'5102',\n" +
                    "		'5401',\n" +
                    "		'5403',\n" +
                    "		'5405',\n" +
                    "		'1253'\n" +
                    "	)\n" +
                    "order by\n" +
                    "	cst,\n" +
                    "	aliq,\n" +
                    "	reducao"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                        rs.getString("id"),
                        String.format(
                                "%s - cst: %s - al: %.2f - rd: %.2f",
                                rs.getString("dscalculoicms"),
                                rs.getString("cst"),
                                rs.getDouble("aliq"),
                                rs.getDouble("reducao")
                        ),
                        rs.getInt("cst"),
                        rs.getDouble("aliq"),
                        rs.getDouble("reducao")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ids.dscodigo id,\n"
                    + "	dt.dtcadastro datacadastro,\n"
                    + "	dt.dtaltvlprecovenda dataalteracao,\n"
                    + "	coalesce(ean.dscodigo, ids.dscodigo) ean,\n"
                    + "	coalesce(nullif(dt.qtembalagem, 0), 1) qtdembalagemcotacao,\n"
                    + "	substring(un.dssigla, 1,2) unidade,\n"
                    + "	dt.stbalanca ebalanca,\n"
                    + "	coalesce(bal.nrdiasvalidade, 0) validade,\n"
                    + "	p.nmproduto descricaocompleta,\n"
                    + "	dt.dsdetalhe descricaoreduzida,\n"
                    + "	p.idgrupo merc1,\n"
                    + "	dt.idproduto,\n"
                    + "	dt.iddetalhe,\n"
                    + "	dt.pesoliquido,\n"
                    + "	dt.pesobruto,\n"
                    //+ "	est.qtestoque estoque,\n"
                    + "	dt.allucro margem,\n"
                    + "	dt.vlprecocusto custocomimposto,\n"
                    + "	dt.vlprecovenda precovenda,\n"
                    + "	dt.stdetalheativo ativo,\n"
                    + "	p.cdipi ncm,\n"
                    + "	p.cest,\n"
                    + "	dt.cdsittribpisentrada piscofins_entrada,\n"
                    + "	dt.cdsittribpis piscofins_saida,\n"
                    + "	dt.cdnaturezareceita piscofins_naturezareceita,	\n"
                    + "	p.idcalculoicms||'-'||p.stexp idcalculoicms\n"
                    + "from\n"
                    + "	wshop.produto p\n"
                    + "	join wshop.empshop emp on emp.cdempresa = '" + getLojaOrigem() + "'\n"
                    + "	join wshop.detalhe dt on p.idproduto = dt.idproduto\n"
                    + "	join wshop.codigos ids on dt.iddetalhe = ids.iddetalhe and dt.idproduto = ids.idproduto and ids.tpcodigo = 'Chamada'\n"
                    + "	left join wshop.codigos ean on dt.idproduto = ean.idproduto"
                    + (somenteBalanca ? " and ean.tpcodigo = 'Personalizado' and length(coalesce(ean.dscodigo, ids.dscodigo)) = 6\n" : "\n")
                    /*+ "	left join (\n"
                    + "		select \n"
                    + "			ean.*\n"
                    + "		from\n"
                    + "			wshop.codigos ean\n"
                    + "			join wshop.detalhe dt on ean.iddetalhe = dt.iddetalhe and ean.idproduto = dt.idproduto\n"
                    + "		where\n"
                    + "			(dt.stbalanca and ean.tpcodigo = 'Chamada') or\n"
                    + "			(not dt.stbalanca and ean.tpcodigo != 'Chamada')\n"
                    + "	) ean on dt.iddetalhe = ean.iddetalhe and dt.idproduto = ean.idproduto	\n"*/
                    + "	left join wshop.unidade un on un.idunidade = p.idunidade\n"
                    + "	left join wshop.produto_balanca bal on bal.iddetalhe = dt.iddetalhe\n"
                    /*
                    + "	left join (\n"
                    + "		select\n"
                    + "			est.iddetalhe,\n"
                    + "			est.cdempresa,\n"
                    + "			est.qtestoque\n"
                    + "		from\n"
                    + "			wshop.estoque est\n"
                    + "			join (\n"
                    + "				select \n"
                    + "					iddetalhe, \n"
                    + "					cdempresa,\n"
                    + "					max(dtreferencia) dtreferencia \n"
                    + "				from\n"
                    + "					wshop.estoque\n"
                    + "				group by \n"
                    + "					iddetalhe, cdempresa\n"
                    + "			) a on est.iddetalhe = a.iddetalhe and est.dtreferencia = a.dtreferencia\n"
                    + "	) est on est.iddetalhe = dt.iddetalhe and est.cdempresa = emp.cdempresa\n"
                    */      
                    + "order by\n"
                    + "	coalesce(ean.dscodigo, ids.dscodigo)"
            )) {
                //mapearFamilia();
                System.out.println("Mapeamento concluído");
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                int count = 0, count2 = 0;
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    
                    int ean = StringUtils.toInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO bal = balanca.get(ean);
                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(bal.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        if (somenteBalanca) continue;
                        imp.seteBalanca(rst.getBoolean("ebalanca"));
                        imp.setEan(rst.getString("ean"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setQtdEmbalagem(1);
                    }
                    
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    /*String familia = mapaFamilia.get(rst.getString("iddetalhe"));
                    if (familia != null) {
                        LOG.finer("Familia " + familia + " encontrada para " + imp.getImportId() + " - " + imp.getDescricaoCompleta());
                    }
                    imp.setIdFamiliaProduto(familia);*/
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    //imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_naturezareceita"));
                    
                    imp.setIcmsDebitoId(rst.getString("idcalculoicms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idcalculoicms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idcalculoicms"));
                    imp.setIcmsConsumidorId(rst.getString("idcalculoicms"));
                    imp.setIcmsCreditoId(rst.getString("idcalculoicms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idcalculoicms"));

                    result.add(imp);
                    
                    count++;
                    count2++;
                    
                    if (count == 500) {
                        count = 0;
                        System.out.println(count2 + " produtos prontos para importação!");
                    }
                }
                System.out.println(count2 + " produtos prontos para importação - Fim!");
            }
        }

        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.idpessoa id,\n"
                    + "	p.cdchamada chamada,\n"
                    + "	p.nmpessoa razao,\n"
                    + "	coalesce(p.nmfantasia, p.nmpessoa) fantasia,\n"
                    + "	p.nrinscrmun inscricaomunicipal,\n"
                    + "	p.nrcgc_cic cnpj,\n"
                    + "	p.nrincrest_rg ierj,\n"
                    + "	(p.stativo = 'S') ativo,\n"
                    + "	p.nmendereco endereco,\n"
                    + "	p.nrlogradouro numero,\n"
                    + "	p.dscomplemento complemento,\n"
                    + "	p.nmbairro bairro,\n"
                    + "	p.nmcidade municipio,\n"
                    + "	p.iduf uf,\n"
                    + "	p.nmcep cep,\n"
                    + "	p.email,\n"
                    + "	p.nrtelefone,\n"
                    + "	p.nrtelcomercial,\n"
                    + "	p.nrtelfax,\n"
                    + "	p.diavencimento,\n"
                    + "	p.diafaturamento,\n"
                    + "	p.idprazo,\n"
                    + "	p.dtcadastro datacadastro,\n"
                    + "	p.nmobservacao,\n"
                    + "	p.sticmssimples simplesnacional\n"
                    + "from\n"
                    + "	wshop.pessoas p\n"
                    + "where\n"
                    + "	sttipopessoa = 'F'\n"
                    + " and cdchamada not like 'N5%'\n"
                    + "order by\n"
                    + "	p.cdchamada::integer;"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("chamada"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ierj"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.addEmail("EMAIL", rst.getString("email"), TipoContato.COMERCIAL);
                    imp.setTel_principal(rst.getString("nrtelefone"));
                    imp.addTelefone("COMERCIAL", rst.getString("nrtelcomercial"));
                    imp.addTelefone("FAX", rst.getString("nrtelfax"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("nmobservacao"));
                    imp.setTipoEmpresa(rst.getBoolean("simplesnacional") ? TipoEmpresa.ME_SIMPLES : TipoEmpresa.LUCRO_REAL);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.cdchamada idfornecedor,\n"
                    + "	ids.dscodigo idproduto,\n"
                    + "	pf.cdprodutofornecedor codigoexterno,\n"
                    + "	pf.dtultimacompra dataalteracao\n"
                    + "from\n"
                    + "	wshop.prodfor pf\n"
                    + "	join wshop.produto p on pf.idproduto = p.idproduto\n"
                    + "	join wshop.detalhe dt on p.idproduto = dt.idproduto\n"
                    + "	join wshop.codigos ids on dt.iddetalhe = ids.iddetalhe and dt.idproduto = ids.idproduto and ids.tpcodigo = 'Chamada'\n"
                    + "	join wshop.pessoas f on f.idpessoa = pf.idpessoa\n"
                    + "order by\n"
                    + "	1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cdchamada id,\n"
                    + "	nrcgc_cic cnpj,\n"
                    + " nrincrest_rg as ie_rg,\n"
                    + "	nmpessoa razao,\n"
                    + "	nmfantasia fantasia,\n"
                    + "	case when stativo = 'S' then 1 else 0 end ativo,\n"
                    + "	nmendereco endereco,\n"
                    + "	nrlogradouro numero,\n"
                    + "	dscomplemento complemento,\n"
                    + "	nmbairro bairro,\n"
                    + "	nmcidade municipio,\n"
                    + "	iduf uf,\n"
                    + "	nmcep cep,\n"
                    + "	dtcadastro dataCadastro,\n"
                    + "	vlsalario salario,\n"
                    + "	vllimitecompra valorLimite,\n"
                    + "	nmobservacao observacao,\n"
                    + "	referenciaendereco observacao2, \n"
                    + "	diavencimento diaVencimento,\n"
                    + "	nrtelefone telefone,\n"
                    + "	nrpager celular,\n"
                    + "	email email,\n"
                    + "	nrtelfax fax\n"
                    + "from\n"
                    + "	wshop.pessoas cli\n"
                    + "where\n"
                    + "	sttipopessoa = 'C'\n"
                    + "order by \n"
                    + "	cdchamada"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("valorLimite"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setObservacao2(rst.getString("observacao2"));
                    imp.setDiaVencimento(rst.getInt("diaVencimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	rec.nrtitulo id,\n"
                    + "	rec.dtemissao dataEmissao,\n"
                    + "	rec.nrtitulo numeroCupom,\n"
                    + "	rec.nrtitulo ecf,\n"
                    + "	rec.vltitulo valor,\n"
                    + "	rec.nmobservacao observacao,\n"
                    + "	cli.cdchamada idCliente,\n"
                    + "	rec.dtvencimento dataVencimento,\n"
                    + "	cli.nrcgc_cic cnpjCliente\n"
                    + "from\n"
                    + "	wshop.fluxo rec\n"
                    + "join \n"
                    + "	wshop.pessoas cli on rec.idpessoa = cli.idpessoa\n"
                    + "where \n"
                    + "	rec.dtbaixa is null\n"
                    + "and rec.tppessoa = 'C'\n"        
                    + "order by\n"
                    + "	cdchamada::integer"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataEmissao"));
                    imp.setDataVencimento(rst.getDate("dataVencimento"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setCnpjCliente(rst.getString("cnpjCliente"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("numeroCupom")));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
