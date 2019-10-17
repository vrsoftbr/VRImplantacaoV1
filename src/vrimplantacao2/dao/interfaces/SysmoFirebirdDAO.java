package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class SysmoFirebirdDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;
    public String complemento;

    @Override
    public String getSistema() {
        if (complemento != null && !"".equals(complemento.trim())) {
            return "Sysmo - " + complemento;
        } else {
            return "Sysmo";
        }
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.CUSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ICMS,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM
        ));
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      emp.cod as id,\n"
                    + "      emp.ccg as cnpj,\n"
                    + "      emp.cnm as nome\n"
                    + "from\n"
                    + "    spsemp00 emp\n"
                    + "order by\n"
                    + "      emp.cod"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n" +
                    "	  fiscal.ctr,\n" +
                    "	  fiscal.ica,\n" +
                    "	  fiscal.icr,\n" +
                    "	  fiscal.icf\n" +
                    "from\n" +
                    "	gceffs01 fiscal\n" +
                    "where\n" +
                    "	 fiscal.ufo = 'SC' and\n" +
                    "	 fiscal.ufd = 'SC' and\n" +
                    "	 fiscal.rgf = 0\n" +
                    "order by\n" +
                    "	 fiscal.ctr")) {
                while (rs.next()) {
                    String id = String.format("%s-%s-%s-%s",
                            rs.getString("ctr"),
                            rs.getString("ica"),
                            rs.getString("icr"),
                            rs.getString("icf")
                    );
                    result.add(new MapaTributoIMP(
                            id, 
                            id,
                            rs.getInt("ctr"),
                            rs.getDouble("ica"),
                            rs.getDouble("icr")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	coalesce(dep.cod, 1) mercadologico1,\n" +
                    "	coalesce(dep.dsc, 'DIVERSOS') descmercadologico1,\n" +
                    "	coalesce(cat.cod, 1) mercadologico2,\n" +
                    "	coalesce(cat.dsc, dep.dsc) descmercadologico2,\n" +
                    "	coalesce(subcat.cod, 1) mercadologico3,\n" +
                    "	coalesce(subcat.dsc, cat.dsc) descmercadologico3\n" +
//                    "	coalesce(seg.cod, 1) mercadologico4,\n" +
//                    "	coalesce(seg.dsc, subcat.dsc) descmercadologico4,\n" +
//                    "	coalesce(subseg.cod, 1) mercadologico5,\n" +
//                    "	coalesce(subseg.dsc, seg.dsc) descmercadologico5\n" +
                    "from\n" +
                    "	gcesec01 cat\n" +
                    "	left join gcegrp01 subcat on\n" +
                    "		cat.cod = subcat.sec and\n" +
                    "		cat.dep = subcat.dep\n" +
                    "	left join gcedep01 dep on\n" +
                    "		cat.dep = dep.cod and\n" +
                    "		subcat.dep = dep.cod\n" +
//                    "	left join gceseg01 seg on\n" +
//                    "		dep.cod = seg.dep and\n" +
//                    "		cat.cod = seg.ctg and\n" +
//                    "		subcat.cod = seg.sct\n" +
//                    "	left join gcessg01 subseg on\n" +
//                    "		dep.cod = subseg.dep and\n" +
//                    "		cat.cod = subseg.ctg and\n" +
//                    "		subcat.cod = subseg.sct and\n" +
//                    "		seg.cod = subseg.seg\n" +
                    "order by\n" +
                    " 	dep.cod, cat.cod"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("mercadologico1"));
                    imp.setMerc1Descricao(rs.getString("descmercadologico1"));
                    imp.setMerc2ID(rs.getString("mercadologico2"));
                    imp.setMerc2Descricao(rs.getString("descmercadologico2"));
                    imp.setMerc3ID(rs.getString("mercadologico3"));
                    imp.setMerc3Descricao(rs.getString("descmercadologico3"));
                    /*imp.setMerc4ID(rs.getString("mercadologico4"));
                    imp.setMerc4Descricao(rs.getString("descmercadologico4"));
                    imp.setMerc5ID(rs.getString("mercadologico5"));
                    imp.setMerc5Descricao(rs.getString("descmercadologico5"));*/
                    
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
                    + "	pro idproduto,\n"
                    + "	ccf idfornecedor,\n"
                    + "	qnt qtdembalagem,\n"
                    + "	uni,\n"
                    + "	(select top 1\n"
                    + "		ref \n"
                    + "	from \n"
                    + "		gceref01 ref \n"
                    + "	where \n"
                    + "		ref.cfr = forn.ccf and \n"
                    + "		ref.pro = forn.pro\n"
                    + ") as referencia,\n"
                    + "	dtr dataalteracao\n"
                    + "from\n"
                    + "	gcefor01 forn \n"
                    + "where \n"
                    + "   emp = " + getLojaOrigem() + " \n"
                    + "order by \n"
                    + "	pro, \n"
                    + "	ccf")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setCodigoExterno(rs.getString("referencia"));

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
                    ""
            )) {
                while (rst.next()) {
                
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
                    "with icms as (\n" +
                    "    select distinct\n" +
                    "          fiscal.cod,\n" +
                    "          fiscal.dsc,\n" +
                    "          fiscal.ctr,\n" +
                    "          fiscal.ica,\n" +
                    "          fiscal.icr,\n" +
                    "          fiscal.icf\n" +
                    "    from\n" +
                    "        gceffs01 fiscal\n" +
                    "    where\n" +
                    "         fiscal.ufo = 'SC' and\n" +
                    "         fiscal.ufd = 'SC' and\n" +
                    "         fiscal.rgf = 0\n" +
                    "    order by\n" +
                    "         fiscal.cod\n" +
                    "),\n" +
                    "piscof as (\n" +
                    "    select\n" +
                    "        COD id,\n" +
                    "        NOP cfop,\n" +
                    "        DSF descricao,\n" +
                    "        CTP cst,\n" +
                    "        nrp naturezareceita\n" +
                    "    from\n" +
                    "        GCEFFS02\n" +
                    "    where\n" +
                    "        COD > 0\n" +
                    "        and RGF = 0\n" +
                    "        and DTX is null\n" +
                    "    order by\n" +
                    "        ctp\n" +
                    ")\n" +
                    "select distinct\n" +
                    "    prod.cod as id,\n" +
                    "    prod.dsc as descricaocompleta,\n" +
                    "    prod.dsr as descricaoresumida,\n" +
                    "    prod.dsr as descricaogondola,\n" +
                    "    coalesce(prod.dep, 1) as mercadologico1,\n" +
                    "    coalesce(prod.sec, 1) as mercadologico2,\n" +
                    "    coalesce(prod.grp, 1) as mercadologico3,\n" +
                    "    coalesce(prod.seg, 1) as mercadologico4,\n" +
                    "    coalesce(prod.ssg, 1) as mercadologico5,\n" +
                    "    prod.dtc as datacadastro,\n" +
                    "    cast(ean.bar as bigint) as ean,\n" +
                    "    prod.emb as qtdembalagem,\n" +
                    "    prod.uni as tipoembalagem,\n" +
                    "    prod.tip as balanca,\n" +
                    "    val.dvl as validade,\n" +
                    "    prod.fl_situacao as ativo,\n" +
                    "    prod.psb as pesobruto,\n" +
                    "    prod.psl as pesoliquido,\n" +
                    "    prod.clf as ncm,\n" +
                    "    custo.cci as custocomimposto,\n" +
                    "    custo.csi as custosemimposto,\n" +
                    "    preco.mrg as margem,\n" +
                    "    preco.pv1 as vlvenda,\n" +
                    "    est.nr_quantidade as estoque,\n" +
                    "    proest.emn as estoquemin,\n" +
                    "    proest.emx as estoquemax,\n" +
                    "    prod.cd_especificadorst as cest,\n" +
                    "    custo.icm as icmscredito,\n" +
                    "    fis.ctr as cstdebito,\n" +
                    "    fis.icf as icmsdebito,\n" +
                    "    fis.icr as icmsreducao,\n" +
                    "    pis_e.cst pis_e_cst,\n" +
                    "    pis_s.naturezareceita pis_s_natrec,\n" +
                    "    pis_s.cst pis_s_cst,\n" +
                    "    prod.fcv unidade_volume,\n" +
                    "    prod.qem qtd_volume\n" +
                    "from\n" +
                    "    gcepro02 as prod\n" +
                    "    left join gcebar01 as ean on\n" +
                    "        ean.pro = prod.cod\n" +
                    "    left join gcepro05 as custo on\n" +
                    "        custo.cod = prod.cod\n" +
                    "    left join gcepro04 as preco on\n" +
                    "        preco.cod = prod.cod\n" +
                    "    join spsemp00 as emp on\n" +
                    "        emp.cod = custo.emp\n" +
                    "    left join gcepro06 val on emp.cod = val.emp and\n" +
                    "         val.cod = prod.cod\n" +
                    "    join tb_produtoestoque est on\n" +
                    "        prod.cod = est.cd_produto\n" +
                    "    left join icms as fis on\n" +
                    "        prod.ffs = fis.cod\n" +
                    "    left join piscof pis_e on\n" +
                    "        prod.fpc = pis_e.id and\n" +
                    "        pis_e.cfop = 1102\n" +
                    "    left join piscof pis_s on\n" +
                    "        prod.fpc = pis_s.id and\n" +
                    "        pis_s.cfop = 5102\n" +
                    "    join gcepro03 as proest on \n" +
                    "        prod.cod = proest.cod and\n" +
                    "        proest.emp = emp.cod and\n" +
                    "        est.cd_empresa = emp.cod and\n" +
                    "        emp.cod = preco.emp and\n" +
                    "        emp.cod = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "      prod.cod"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoresumida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));
                    /*imp.setCodMercadologico4(rs.getString("mercadologico4"));
                    imp.setCodMercadologico5(rs.getString("mercadologico5"));*/
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValidade(rs.getInt("validade"));
                    
                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rs.getString("id"), -2));
                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(bal.getValidade());
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setEan(imp.getImportId());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.seteBalanca("B".equals(rs.getString("balanca")));
                        imp.setValidade(rs.getInt("validade"));
                        imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                        imp.setEan(rs.getString("ean"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem") == 0 ? 1 : rs.getInt("qtdembalagem"));
                    }
                    
                    imp.setSituacaoCadastro("A".equals(rs.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setPesoBruto(rs.getInt("pesobruto"));
                    imp.setPesoLiquido(rs.getInt("pesoliquido"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("vlvenda"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setCest(rs.getString("cest"));
                    //imp.setIcmsAliqEntrada(rs.getDouble("icmscredito"));
                    imp.setIcmsCstSaida(rs.getInt("cstdebito"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsdebito"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icmsreducao"));
                    
                    imp.setIcmsCstEntrada(rs.getInt("cstdebito"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmsdebito"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icmsreducao"));
                    
                    //imp.setPautaFiscalId(lojaMesmoID);

                    imp.setPiscofinsCstCredito(rs.getInt("pis_e_cst"));
                    imp.setPiscofinsCstDebito(rs.getInt("pis_s_cst"));
                    imp.setPiscofinsNaturezaReceita(rs.getInt("pis_s_natrec"));
                    
                    imp.setTipoEmbalagemVolume(rs.getString("unidade_volume"));
                    imp.setVolume(rs.getDouble("qtd_volume"));
                    
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
                    + " t.cod as id,\n"
                    + " t.nom as nome,\n"
                    + " t.cgc as cpfcnpj,\n"
                    + " t.pss as tipopessoa,\n"
                    + " t.fan as fantasia,\n"
                    + " t.log as endereco,\n"
                    + " t.num as numero,\n"
                    + " t.bai as bairro,\n"
                    + " t.cmp as complemento,\n"
                    + " t.mun as municipio,\n"
                    + " t.cep,\n"
                    + " t.cxp as caixapostal,\n"
                    + " t.tel,\n"
                    + " t.fax,\n"
                    + " t.cel,\n"
                    + " t.ins as ie,\n"
                    + " t.oex as orgaoexp,\n"
                    + " t.dtn as datanascimento,\n"
                    + " t.eml as email,\n"
                    + " t.emn as emailnf,\n"
                    + " t.obs,\n"
                    + " t.dtc as datacadastro,\n"
                    + " t.dtm as datamovimentacao,\n"
                    + " t.lcr as limitecredito\n"
                    + "from\n"
                    + "    trstra01 t\n"
                    + "where\n"
                    + "     t.tip not in ('D', 'C')")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj_cpf(Utils.stringLong(rs.getString("cpfcnpj")));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("tel"));
                    if (!"".equals(rs.getString("fax"))) {
                        imp.addContato("1", "Fax", rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    if (!"".equals(rs.getString("cel"))) {
                        imp.addContato("2", "Celular", rs.getString("cel"), null, TipoContato.COMERCIAL, null);
                    }
                    imp.setIe_rg(rs.getString("ie"));
                    if (!"".equals(rs.getString("email"))) {
                        imp.addContato("3", "Email", null, null, TipoContato.FISCAL, rs.getString("email").toLowerCase());
                    }
                    if (!"".equals(rs.getString("emailnf"))) {
                        imp.addContato("4", "Email NF", null, null, TipoContato.NFE, rs.getString("emailnf").toLowerCase());
                    }
                    imp.setObservacao(rs.getString("obs"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird
                .getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      t.cod as id,\n"
                    + "      t.nom as nome,\n"
                    + "      t.cgc as cpfcnpj,\n"
                    + "      t.pss as tipopessoa,\n"
                    + "      t.fan as fantasia,\n"
                    + "      t.log as endereco,\n"
                    + "      t.num as numero,\n"
                    + "      t.bai as bairro,\n"
                    + "      t.cmp as complemento,\n"
                    + "      t.mun as municipio,\n"
                    + "      t.cep,\n"
                    + "      t.cxp as caixapostal,\n"
                    + "      t.tel,\n"
                    + "      t.fax,\n"
                    + "      t.cel,\n"
                    + "      t.ins as ie,\n"
                    + "      t.oex as orgaoexp,\n"
                    + "      t.dtn as datanascimento,\n"
                    + "      t.eml as email,\n"
                    + "      t.emn as emailnf,\n"
                    + "      t.obs,\n"
                    + "      t.dtc as datacadastro,\n"
                    + "      t.dtm as datamovimentacao,\n"
                    + "      --(select vlc from trsccv01 tr where tr.cod = t.cod) as limitecredito,\n"
                    + "      conv.vlc as limitecredito, \n"        
                    + "      case when conv.dbl is null\n"
                    + "      then 0 else 1 end bloqueado \n"
                    + "from\n"
                    + "    trstra01 t\n"
                    + "left join trsccv01 conv on t.cod = conv.cod\n"
                    + "where\n"
                    + "     t.tip in ('D', 'C') and\n"
                    + "    emp = " + getLojaOrigem() + "\n"        
                    + "order by\n"
                    + "     t.cod")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj(Utils.stringLong(rs.getString("cpfcnpj")));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("tel"));
                    imp.setFax(rs.getString("fax"));
                    imp.setCelular(rs.getString("cel"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setEmail(rs.getString("email").toLowerCase());
                    imp.addEmail(rs.getString("emailnf").toLowerCase(), TipoContato.NFE);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setLimiteCompra(rs.getDouble("limitecredito"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setOrgaoemissor(rs.getString("orgaoexp"));

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
                    "SELECT \n" +
                    "	C.EMP id_empresa,\n" +
                    "	C.TDC tipo_doc, --CV = CONVENIO\n" +
                    "	C.NUM numerodoc,\n" +
                    "	C.SER seriedoc,\n" +
                    "	C.CCF id_cliente,\n" +
                    "	C.PST parcela,\n" +
                    "	C.VPT valorparcela,\n" +
                    "	C.VBX valorbaixado,\n" +
                    "	C.DPV dataprevisao,\n" +
                    "	C.DTV datavencimento,\n" +
                    "	C.DTE dataemissao,\n" +
                    "	RG.DSC AS DESCRICAOORIGEM,\n" +
                    "	C.VTD valortotal,\n" +
                    "	T.NOM AS NCF,\n" +
                    "	cast(C.NDB AS numeric(18, 1)) AS NDB,\n" +
                    "	C.NSQ seq,\n" +
                    "	cast((C.VPT+C.VAD) AS numeric (18, 2)) AS LIQ,\n" +
                    "	T.CGC cnpjcpf,\n" +
                    "	C.PC_MULTA multa,\n" +
                    "	T2.NOM AS NOME_BANCO,\n" +
                    "	C.CMP observacoes,\n" +
                    "	C.NR_SEQUENCIAL\n" +
                    "FROM \n" +
                    "	CRCDOC01 C\n" +
                    "	LEFT OUTER JOIN TRSTRA01 T ON\n" +
                    "		C.CCF = T.COD\n" +
                    "	LEFT OUTER JOIN TB_PEFINDOCUMENTO D ON\n" +
                    "		(D.CD_EMPRESA = C.EMP)\n" +
                    "		AND (D.CD_TIPODOCUMENTO = C.TDC)\n" +
                    "		AND (D.NR_DOCUMENTO = C.NUM)\n" +
                    "		AND (D.TX_SERIE = C.SER)\n" +
                    "		AND (D.CD_TRANSACIONADOR = C.CCF)\n" +
                    "		AND (D.NR_PRESTACAO = C.PST)\n" +
                    "	LEFT OUTER JOIN TB_PEFINSITUACAODOCUMENTO S ON\n" +
                    "		(S.CD_CODIGO = D.FL_SITUACAOATUAL)\n" +
                    "	LEFT OUTER JOIN TB_FINANCEIROEDILOG EDI ON (\n" +
                    "		EDI.CD_EMPRESA = C.EMP\n" +
                    "		AND EDI.CD_TIPODOCUMENTO = C.TDC\n" +
                    "		AND EDI.NR_DOCUMENTO = C.NUM\n" +
                    "		AND EDI.TX_SERIE = C.SER\n" +
                    "		AND EDI.CD_TRANSACIONADOR = C.CCF\n" +
                    "		AND EDI.NR_PRESTACAO = C.PST\n" +
                    "	)\n" +
                    "	LEFT OUTER JOIN TRSTRA01 T2 ON (\n" +
                    "		EDI.CD_BANCO = T2.COD\n" +
                    "		AND EDI.FL_ULTIMAOPERACAO = 'S'\n" +
                    "	)\n" +
                    "	LEFT OUTER JOIN GCEORG01 RG ON\n" +
                    "		RG.COD = C.ORG\n" +
                    "WHERE\n" +
                    "	C.EMP >= 1\n" +
                    "	AND C.TDC IS NOT NULL\n" +
                    "	AND C.NUM >= 1\n" +
                    "	AND C.SER IS NOT NULL\n" +
                    "	AND C.PST > -1\n" +
                    "	and c.emp = " + getLojaOrigem() + "\n" +
                    "	and c.vbx < c.vtd --Sem baixa\n" +
                    "ORDER BY \n" +
                    "	C.EMP,\n" +
                    "	C.TDC,\n" +
                    "	C.NUM,\n" +
                    "	C.SER,\n" +
                    "	T.NOM,\n" +
                    "	C.PST"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(String.format(
                            "%s-%s-%s-%s-%s-%s",
                            rst.getString("id_empresa"),
                            rst.getString("tipo_doc"),
                            rst.getString("numerodoc"),
                            rst.getString("seriedoc"),
                            rst.getString("id_cliente"),
                            rst.getString("parcela")
                    ));
                    imp.setNumeroCupom(rst.getString("numerodoc"));
                    imp.setEcf(rst.getString("seriedoc"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setParcela(rst.getShort("parcela"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setValor(rst.getDouble("valorparcela"));
                    imp.setMulta(rst.getDouble("multa"));
                    imp.setObservacao(String.format(
                            "%s CPL: %s",
                            rst.getString("observacoes"),
                            rst.getString("DESCRICAOORIGEM")
                    ));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
