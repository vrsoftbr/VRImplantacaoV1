package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class SysmoDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;

    @Override
    public String getSistema() {
        return "Sysmo";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      distinct\n"
                    + "      fiscal.cod::integer,\n"
                    + "      fiscal.dsc,\n"
                    + "      fiscal.ctr,\n"
                    + "      fiscal.ica,\n"
                    + "      fiscal.icr,\n"
                    + "      fiscal.icf\n"
                    + "from\n"
                    + "    gceffs01 fiscal\n"
                    + "where\n"
                    + "     fiscal.ufo = 'RJ' and\n"
                    + "     fiscal.ufd = 'RJ' and\n"
                    + "     fiscal.rgf = 0\n"
                    + "order by\n"
                    + "     fiscal.cod::integer")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("cod"), rs.getString("dsc")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      emp.cod::integer as id,\n"
                    + "      emp.ccg::bigint as cnpj,\n"
                    + "      emp.cnm as nome\n"
                    + "from\n"
                    + "    spsemp00 emp\n"
                    + "order by\n"
                    + "      emp.cod::integer"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + " coalesce(dep.cod, 1) mercadologico1,\n"
                    + " coalesce(dep.dsc, 'DIVERSOS') descmercadologico1,\n"
                    + " coalesce(cat.cod, 1) mercadologico2,\n"
                    + " coalesce(cat.dsc, dep.dsc) descmercadologico2,\n"
                    + " coalesce(subcat.cod, 1) mercadologico3,\n"
                    + " coalesce(subcat.dsc, cat.dsc) descmercadologico3,\n"
                    + " coalesce(seg.cod, 1) mercadologico4,\n"
                    + " coalesce(seg.dsc, subcat.dsc) descmercadologico4,\n"
                    + " coalesce(subseg.cod, 1) mercadologico5,\n"
                    + " coalesce(subseg.dsc, seg.dsc) descmercadologico5\n"
                    + " from\n"
                    + " gcesec01 cat\n"
                    + " left join\n"
                    + " gcegrp01 subcat on cat.cod = subcat.sec and\n"
                    + " cat.dep = subcat.dep\n"
                    + " join gcedep01 dep on cat.dep = dep.cod and\n"
                    + " subcat.dep = dep.cod\n"
                    + " join gceseg01 seg on dep.cod = seg.dep and\n"
                    + " cat.cod = seg.ctg and\n"
                    + " subcat.cod = seg.sct\n"
                    + " left join gcessg01 subseg on dep.cod = subseg.dep and\n"
                    + " cat.cod = subseg.ctg and\n"
                    + " subcat.cod = subseg.sct and\n"
                    + " seg.cod = subseg.seg\n"
                    + " order by\n"
                    + " dep.cod, cat.cod")) {
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
                    imp.setMerc4ID(rs.getString("mercadologico4"));
                    imp.setMerc4Descricao(rs.getString("descmercadologico4"));
                    imp.setMerc5ID(rs.getString("mercadologico5"));
                    imp.setMerc5Descricao(rs.getString("descmercadologico5"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	forn.pro as idproduto,\n"
                    + "	forn.ccf as idfornecedor,\n"
                    + " forn.qnt as qtdembalagem,\n"
                    + " forn.dtr as dataalteracao\n"    
                    + "from \n"
                    + "	gcefor01 forn \n"
                    + "where \n"
                    + "	emp = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "	forn.pro, forn.ccf")) {
                while (rs.next()) {
                       ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                       imp.setImportLoja(getLojaOrigem());
                       imp.setImportSistema(getSistema());
                       imp.setIdProduto(rs.getString("idproduto"));
                       imp.setIdFornecedor(rs.getString("idfornecedor"));
                       imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                       imp.setDataAlteracao(rs.getDate("dataalteracao"));
                       
                       result.add(imp);
                }

            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      distinct\n"
                    + "      prod.cod as id,\n"
                    + "      prod.dsc as descricaocompleta,\n"
                    + "      prod.dsr as descricaoresumida,\n"
                    + "      prod.dsr as descricaogondola,\n"
                    + "      coalesce(prod.dep, 1) as mercadologico1,\n"
                    + "      coalesce(prod.sec, 1) as mercadologico2,\n"
                    + "      coalesce(prod.grp, 1) as mercadologico3,\n"
                    + "      coalesce(prod.seg, 1) as mercadologico4,\n"
                    + "      coalesce(prod.ssg, 1) as mercadologico5,\n"
                    + "      prod.dtc as datacadastro,\n"
                    + "      ean.bar as ean,\n"
                    + "      prod.emb as qtdembalagem,\n"
                    + "      prod.uni as tipoembalagem,\n"
                    + "      prod.tip as balanca,\n"
                    + "      val.dvl as validade,\n"
                    + "      prod.fl_situacao as ativo,\n"
                    + "      prod.psb as pesobruto,\n"
                    + "      prod.psl as pesoliquido,\n"
                    + "      prod.clf as ncm,\n"
                    + "      custo.cci as custocomimposto,\n"
                    + "      custo.csi as custosemimposto,\n"
                    + "      preco.mrg as margem,\n"
                    + "      preco.pv1 as vlvenda,\n"
                    + "      est.nr_quantidade as estoque,\n"
                    + "      proest.emn as estoquemin,\n"
                    + "      proest.emx as estoquemax,\n"
                    + "      prod.cd_especificadorst as cest,\n"
                    + "      custo.icm as icmscredito,\n"
                    + "      fis.ctr as cstdebito,\n"
                    + "      fis.icf as icmsdebito,\n"
                    + "      fis.icr as icmsreducao\n"
                    + "from\n"
                    + "      gcepro02 as prod\n"
                    + "left join gcebar01 as ean on ean.pro = prod.cod\n"
                    + "left join gcepro05 as custo on custo.cod = prod.cod\n"
                    + "left join gcepro04 as preco on preco.cod = prod.cod\n"
                    + "join spsemp00 as emp on emp.cod = custo.emp\n"
                    + "left join gcepro06 val on emp.cod = val.emp and\n"
                    + "     val.cod = prod.cod\n"
                    + "join tb_produtoestoque est on prod.cod = est.cd_produto\n"
                    + "left join (select\n"
                    + "            fiscal.cod,\n"
                    + "            fiscal.ctr,\n"
                    + "            fiscal.ica,\n"
                    + "            fiscal.icr,\n"
                    + "            fiscal.icf\n"
                    + "      from\n"
                    + "          gceffs01 fiscal\n"
                    + "      where\n"
                    + "           fiscal.ufo = 'RJ' and\n"
                    + "           fiscal.ufd = 'RJ' and\n"
                    + "           fiscal.rgf = 0) as fis on prod.ffs = fis.cod\n"
                    + "join gcepro03 as proest on prod.cod = proest.cod and\n"
                    + "     proest.emp = emp.cod and\n"
                    + "     est.cd_empresa = emp.cod and\n"
                    + "     emp.cod = preco.emp and\n"
                    + "     emp.cod = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "      prod.cod")) {
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
                    imp.setCodMercadologico4(rs.getString("mercadologico4"));
                    imp.setCodMercadologico5(rs.getString("mercadologico5"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem") == 0 ? 1 : rs.getInt("qtdembalagem"));
                    imp.setEan(rs.getString("ean"));
                    imp.setValidade(rs.getInt("validade"));
                    if ((rs.getString("ean") == null) && ("B".equals(rs.getString("balanca").trim()))) {
                        imp.setEan(rs.getString("id").trim());
                        imp.seteBalanca(true);
                    }
                    if (v_usar_arquivoBalanca) {
                        ProdutoBalancaVO produtoBalanca;
                        long codigoProduto;
                        codigoProduto = Long.parseLong(imp.getImportId().trim());
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }
                        if (produtoBalanca != null) {
                            imp.setValidade(produtoBalanca.getValidade());
                        } else {
                            imp.setValidade(0);
                        }
                    }
                    imp.setSituacaoCadastro("A".equals(rs.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setPesoBruto(rs.getInt("pesobruto"));
                    imp.setPesoLiquido(rs.getInt("pesoliquido"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("vlvenda"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmscredito"));
                    imp.setIcmsCstSaida(rs.getInt("cstdebito"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsdebito"));
                    imp.setIcmsReducao(rs.getDouble("icmsreducao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        return result;
    }
}
