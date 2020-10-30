package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class SysmoPostgresDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;
    public String lojaMesmoID;

    @Override
    public String getSistema() {
        if(lojaMesmoID == null){ 
            lojaMesmoID = "";
        }
        return "Sysmo" + lojaMesmoID;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[] {
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
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
                    OpcaoProduto.MARGEM
                }
        ));
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
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
                    lojas.add(new Estabelecimento(
                            rs.getString("id"), 
                            rs.getString("cnpj") + " " + rs.getString("nome")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            String sql = "select\n"
                    + "      distinct\n"
                    + "      fiscal.cod,\n"
                    + "      fiscal.dsc,\n"
                    + "      fiscal.ctr cst,\n"
                    + "      fiscal.ica icms,\n"
                    + "      fiscal.icr reducao,\n"
                    + "      fiscal.icf\n"
                    + "from\n"
                    + "    gceffs01 fiscal\n"
                    + "where\n"
                    + "     fiscal.ufo = (select tx_crcuf from spsemp00 where cod = " + getLojaOrigem() + ") and\n"
                    + "     fiscal.ufd = (select tx_crcuf from spsemp00 where cod = " + getLojaOrigem() + ") and\n"
                    + "     fiscal.rgf = 0\n"
                    + "order by\n"
                    + "     fiscal.cod";
            System.out.println(sql);
            try (ResultSet rs = stm.executeQuery(sql)) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("cod"), 
                                rs.getString("dsc"),
                                rs.getInt("cst"),
                                rs.getDouble("icms"),
                                rs.getDouble("reducao")));
                }
            }
        }
        return result;
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
                    + "	pro idproduto,\n"
                    + "	ccf idfornecedor,\n"
                    + "	qnt qtdembalagem,\n"
                    + "	uni,\n"
                    + "	(select \n"
                    + "		ref \n"
                    + "	from \n"
                    + "		gceref01 ref \n"
                    + "	where \n"
                    + "		ref.cfr = forn.ccf and \n"
                    + "		ref.pro = forn.pro\n"
                    + "		limit 1) as referencia,\n"
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      distinct\n"
                    + "      prod.cod::integer as id,\n"
                    + "      prod.dsc as descricaocompleta,\n"
                    + "      prod.dsr as descricaoresumida,\n"
                    + "      prod.dsr as descricaogondola,\n"
                    + "      coalesce(prod.dep, 1) as mercadologico1,\n"
                    + "      coalesce(prod.sec, 1) as mercadologico2,\n"
                    + "      coalesce(prod.grp, 1) as mercadologico3,\n"
                    + "      coalesce(prod.seg, 1) as mercadologico4,\n"
                    + "      coalesce(prod.ssg, 1) as mercadologico5,\n"
                    + "      prod.dtc as datacadastro,\n"
                    + "      ean.bar::bigint as ean,\n"
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
                    + "      pis.ctp as piscofins,\n"
                    + "      pis.nrp as naturezareceita,\n"        
                    + "      prod.ffs idaliquota\n"        
                    + "from\n"
                    + "      gcepro02 as prod\n"
                    + "left join gcebar01 as ean on ean.pro = prod.cod\n"
                    + "left join gcepro05 as custo on custo.cod = prod.cod\n"
                    + "left join gcepro04 as preco on preco.cod = prod.cod\n"
                    + "join spsemp00 as emp on emp.cod = custo.emp\n"
                    + "left join gcepro06 val on emp.cod = val.emp and\n"
                    + "     val.cod = prod.cod\n"
                    + "join tb_produtoestoque est on prod.cod = est.cd_produto\n"
                    + "left join gceffs02 pis on prod.fpc = pis.cod\n"        
                    + "join gcepro03 as proest on prod.cod = proest.cod and\n"
                    + "     proest.emp = emp.cod and\n"
                    + "     est.cd_empresa = emp.cod and\n"
                    + "     emp.cod = preco.emp\n"
                    + "where pis.nop = 5102.00 and emp.cod = " + getLojaOrigem() + "\n"        
                    + "order by\n"
                    + "      prod.cod::integer")) {
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
                    imp.setCodMercadologico5("0".equals(rs.getString("mercadologico5")) ? "1" : rs.getString("mercadologico5"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem") == 0 ? 1 : rs.getInt("qtdembalagem"));
                    imp.setEan(rs.getString("ean"));
                    imp.setValidade(rs.getInt("validade"));
                   
                        if(("B".equals(rs.getString("balanca").trim()))) {
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
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        }
                    } else {
                        imp.seteBalanca(rs.getString("tipoembalagem").contains("KG") ? true : false);
                        imp.setValidade(rs.getInt("validade"));
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
                    
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());

                    imp.setPiscofinsCstDebito(rs.getString("piscofins"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + " t.cod as id,\n"
                    + " t.nom as nome,\n"
                    + " t.cgc::bigint as cpfcnpj,\n"
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
                    imp.setCnpj_cpf(rs.getString("cpfcnpj"));
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      t.cod as id,\n"
                    + "      t.nom as nome,\n"
                    + "      t.cgc::bigint as cpfcnpj,\n"
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
                    + "      (select vlc from trsccv01 tr where tr.cod = t.cod) as limitecredito,\n"
                    + "      case when conv.dbl is null\n"
                    + "      then 0 else 1 end bloqueado \n"
                    + "from\n"
                    + "    trstra01 t\n"
                    + "left join trstra01 conv on t.cod = conv.cod\n"
                    + "where\n"
                    + "     t.tip in ('D', 'C')\n"
                    + "order by\n"
                    + "     t.cod")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
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
}
