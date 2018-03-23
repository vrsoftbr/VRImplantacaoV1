package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;

/**
 *
 * @author Leandro
 */
public class HipcomDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private static final Logger LOG = Logger.getLogger(HipcomDAO.class.getName());
    
    private Date rotativoDataInicial;
    private Date rotativoDataFinal;

    public void setRotativoDataInicial(Date rotativoDataInicial) {
        this.rotativoDataInicial = rotativoDataInicial;
    }

    public void setRotativoDataFinal(Date rotativoDataFinal) {
        this.rotativoDataFinal = rotativoDataFinal;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select lojcod, concat(lojcod,' - ', lojfantas) descricao from hiploj order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("lojcod"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public String getSistema() {
        return "Hipcom";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	t.trbcod,\n" +
                    "	t.trbdescr,\n" +
                    "	t.trbstrib cst,\n" +
                    "	t.trbaliq aliq,\n" +
                    "	t.trbreduc reducao\n" +
                    "from\n" +
                    "	hiptrb t\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("trbcod"),
                            rst.getString("trbdescr"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            MultiMap<String, MercadologicoNivelIMP> maps = new MultiMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m1.depdepto merc1,\n" +
                    "	m1.depdescr merc1desc\n" +
                    "from\n" +
                    "	hipdep m1	\n" +
                    "where\n" +
                    "	depsecao = 0\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL1: " + rst.getString("merc1") + " - " + rst.getString("merc1desc"));
                    MercadologicoNivelIMP merc = new MercadologicoNivelIMP(rst.getString("merc1"), rst.getString("merc1desc"));
                    maps.put(merc,
                            rst.getString("merc1")
                    );
                    result.add(merc);
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m.depdepto merc1,\n" +
                    "	m.depsecao merc2,\n" +
                    "	m.depdescr merc2desc\n" +
                    "from\n" +
                    "	hipdep m	\n" +
                    "where\n" +
                    "	m.depdepto != 0 and\n" +
                    "	m.depsecao != 0 and\n" +
                    "	m.depgrupo = 0\n" +
                    "order by 1,2"
            )) {
                while (rst.next()) {                    
                    LOG.fine("NIVEL2: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc2desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc2"), rst.getString("merc2desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2")
                        );
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m.depdepto merc1,\n" +
                    "	m.depsecao merc2,\n" +
                    "	m.depgrupo merc3,\n" +
                    "	m.depdescr merc3desc\n" +
                    "from\n" +
                    "	hipdep m	\n" +
                    "where\n" +
                    "	m.depdepto != 0 and\n" +
                    "	m.depsecao != 0 and\n" +
                    "	m.depgrupo != 0 and\n" +
                    "	m.depsubgr = 0\n" +
                    "order by 1,2, 3"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL3: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc3") + " - " + rst.getString("merc3desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"), rst.getString("merc2"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc3"), rst.getString("merc3desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2"),
                                rst.getString("merc3")
                        );
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	m.depdepto merc1,\n" +
                    "	m.depsecao merc2,\n" +
                    "	m.depgrupo merc3,\n" +
                    "	m.depsubgr merc4,\n" +
                    "	m.depdescr merc4desc\n" +
                    "from\n" +
                    "	hipdep m	\n" +
                    "where\n" +
                    "	m.depdepto != 0 and\n" +
                    "	m.depsecao != 0 and\n" +
                    "	m.depgrupo != 0 and\n" +
                    "	m.depsubgr != 0\n" +
                    "order by 1,2, 3, 4"
            )) {
                while (rst.next()) {
                    LOG.fine("NIVEL3: " + rst.getString("merc1") + " - " + rst.getString("merc2") + " - " + rst.getString("merc3") + " - " + rst.getString("merc4") + " - " + rst.getString("merc4desc"));
                    MercadologicoNivelIMP pai = maps.get(rst.getString("merc1"), rst.getString("merc2"), rst.getString("merc3"));
                    if (pai != null) {
                        MercadologicoNivelIMP merc = pai.addFilho(rst.getString("merc4"), rst.getString("merc4desc"));
                        maps.put(merc,
                                rst.getString("merc1"),
                                rst.getString("merc2"),
                                rst.getString("merc3"),
                                rst.getString("merc4")
                        );
                    }
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select famcod, famdescr from hipfam order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("famcod"));
                    imp.setDescricao(rst.getString("famdescr"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.procodplu id,\n" +
                    "	p.prodtcad datacadastro,\n" +
                    "	coalesce(ean.barcodbar, p.procodplu) ean,\n" +
                    "	coalesce(ean.barqtemb, 1) qtdembalagem,\n" +
                    "	coalesce(cot.embqtemb, 1) qtdcotacao,\n" +
                    "	substring(p.proembu, 1,2) tipoembalagem,\n" +
                    "	case p.propesado\n" +
                    "	when 'S' then 1\n" +
                    "	else 0 end as ebalanca,\n" +
                    "	p.provalid validade,\n" +
                    "	p.prodescr descricaocompleta,\n" +
                    "	coalesce(nullif(trim(p.prodescgon),''), p.prodescr) descricaogondola,\n" +
                    "	p.prodescres descricaoreduzida,\n" +
                    "	p.prodepto merc1,\n" +
                    "	p.prosecao merc2,\n" +
                    "	p.progrupo merc3,\n" +
                    "	p.prosubgr merc4,\n" +
                    "	p.procodfam id_familia,\n" +
                    "	p.propeso peso,\n" +
                    "	prc.prlestoq estoque,\n" +
                    "	prc.prlmargind margemunit,\n" +
                    "	prc.prlctentru custosemimposto,\n" +
                    "	prc.prlctnfu custocomimposto,\n" +
                    "	prc.prlprconc1 precovenda,\n" +
                    "	prc.prlforalin id_situacaocadastro,\n" +
                    "	case prc.prlcotacao when 'S' then 1 else 0 end cotacao,\n" +
                    "	p.proclasfisc ncm,\n" +
                    "	p.procest cest,\n" +
                    "	prc.prlcodpiscofe piscofinsentrada,\n" +
                    "	prc.prlcodpiscofs piscofinssaida,\n" +
                    "	prc.prltabpiscof piscofinsnatrec,\n" +
                    "	prc.prlcodtris icmssaidaid,\n" +
                    "	prc.prlcodtrie icmsentradaid,\n" +
                    "	prc.prlprvena precoatacado,\n" +
                    "	prc.prlmargata margematacado,\n" +
                    "	l.lojestado estado,\n" +
                    "	prc.prlpivast p_iva,\n" +
                    "	prc.prlvivast v_iva,\n" +
                    "	prc.prlcotacao sugestaocotacao,\n" +
                    "	prc.prlcodcmp id_comprador\n" +
                    "from\n" +
                    "	hippro p\n" +
                    "	left join hiploj l on\n" +
                    "		l.lojcod = " + getLojaOrigem() + "\n" +
                    "	left join hipbar ean on\n" +
                    "		ean.barcodplu = p.procodplu\n" +
                    "	left join hipprl prc on\n" +
                    "		prc.prlcodplu = p.procodplu and\n" +
                    "		prc.prlloja = l.lojcod\n" +
                    "	left join cotemb cot on\n" +
                    "		cot.embcodplu = p.procodplu\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdcotacao"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margemunit"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    
                    switch (Utils.acertarTexto(rst.getString("id_situacaocadastro"))) {
                        case "S":
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setDescontinuado(false);
                            imp.setVendaPdv(false);
                            break;
                        case "E":
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setDescontinuado(true);
                            imp.setVendaPdv(true);
                            break;
                        case "A": 
                            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                            imp.setDescontinuado(false);
                            imp.setVendaPdv(true);
                            break;
                        default:
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setDescontinuado(false);
                            imp.setVendaPdv(true);
                            break;
                    }
                    
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsentrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofinsnatrec"));
                    imp.setIcmsDebitoId(rst.getString("icmssaidaid"));
                    imp.setIcmsCreditoId(rst.getString("icmsentradaid"));
                    imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                    imp.setAtacadoPorcentagem(rst.getDouble("margematacado"));
                    imp.setPautaFiscalId(formatPautaFiscalId(
                            rst.getString("estado"),
                            rst.getString("ncm"),
                            rst.getDouble("p_iva"),
                            rst.getDouble("v_iva")
                    ));
                    imp.setSugestaoCotacao("S".equals(rst.getString("sugestaocotacao")));
                    imp.setIdComprador(rst.getString("id_comprador"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	f.forcod id,\n" +
                    "	f.forrazao razao,\n" +
                    "	coalesce(nullif(trim(f.forfantas),''), f.forrazao) fantasia,\n" +
                    "	f.forcnpj cnpj,\n" +
                    "	f.forinsest inscricaoestadual,\n" +
                    "	f.forinsmunic inscricaomunicipal,\n" +
                    "	case f.forforalin when 'S' then 0 else 1 end ativo,\n" +
                    "	f.forendere endereco,\n" +
                    "	f.forbairro bairro,\n" +
                    "	f.formunicip municipio,\n" +
                    "	f.forestado uf,\n" +
                    "	f.forcodmunic ibge_munic,\n" +
                    "	f.forcep cep,\n" +
                    "	f.forfone telefone,\n" +
                    "	f.forqtmincxa qtdminimapedido,\n" +
                    "	f.forfatmin valorminimopedido,\n" +
                    "	f.forobserv observacao,\n" +
                    "	f.forentrega prazoentrega,\n" +
                    "	f.forvisita prazovisita,\n" +
                    "	f.forcondpag condicaopagamento,\n" +
                    "	f.forcontato contato,\n" +
                    "	f.forfonecont fonecontato,\n" +
                    "	f.forsite site,\n" +
                    "	f.foremail email,\n" +
                    "	f.foremailxml emailnfe,\n" +
                    "	f.fortipforn tipofornecedor,\n" +
                    "	case f.forprodutor when 'S' then 1 else 0 end produtorural\n" +
                    "from\n" +
                    "	hipfor f\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    
                    String endereco = rst.getString("endereco");
                    if (endereco == null) {
                        endereco = "";
                    }
                    int index = endereco.indexOf(",");
                    String numero = "SN";
                    if (index >= 0) {
                        numero = endereco.substring(index + 1, endereco.length());
                        endereco = endereco.substring(0, index);
                    }
                    
                    imp.setEndereco(endereco);
                    imp.setNumero(numero);
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_municipio(rst.getInt("ibge_munic"));
                    imp.setCep(rst.getString("cep"));
                    
                    imp.copiarEnderecoParaCobranca();
                    
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setQtd_minima_pedido(rst.getInt("qtdminimapedido"));
                    imp.setValor_minimo_pedido(rst.getDouble("valorminimopedido"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setPrazoVisita(rst.getInt("prazovisita"));
                    
                    String cond = rst.getString("condicaopagamento");
                    if (cond == null) cond = "";
                    cond = cond.replaceAll("[^0-9 \\/-]", "");
                    
                    String[] parcs = cond.split("\\/| |\\-");
                    
                    for (String parc: parcs) {
                        if (!"".equals(parc.trim())) {
                            int dia = Utils.stringToInt(parc);
                            imp.addCondicaoPagamento(dia);
                        }
                    }
                    
                    imp.addContato(rst.getString("contato"), rst.getString("fonecontato"), "", TipoContato.COMERCIAL, "");
                    imp.addEmail("SITE", rst.getString("site"), TipoContato.COMERCIAL);
                    imp.addEmail("EMAIL", rst.getString("email"), TipoContato.COMERCIAL);
                    imp.addEmail("NFE", rst.getString("emailnfe"), TipoContato.NFE);
                    if (rst.getBoolean("produtorural")) {
                        imp.setProdutorRural();
                    }
                    switch (Utils.acertarTexto(rst.getString("tipofornecedor"))) {
                        case "I": imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA); break;
                        case "A": imp.setTipoFornecedor(TipoFornecedor.ATACADO); break;
                        case "P": imp.setTipoFornecedor(TipoFornecedor.PRESTADOR); break;
                    }
                    if ("S".equals(Utils.acertarTexto(rst.getString("tipofornecedor")))) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
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
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	pf.prfforn idfornecedor,\n" +
                    "	pf.prfcodplu idproduto,\n" +
                    "	pf.prfreffor codigoexterno,\n" +
                    "	case when pf.prfqtemb < 1 then 1 else pf.prfqtemb end qtdembalagem,\n" +
                    "	pf.prfprtab precopacote\n" +
                    "from\n" +
                    "	hipprf pf\n" +
                    "where\n" +
                    "	pf.prfloja = " + getLojaOrigem() + "\n" +
                    "order by 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    //imp.setCustoTabela(MathUtils.round(rst.getDouble("precopacote") / rst.getDouble("qtdembalagem"), 2));                                        
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	n.nutcodplu id,\n" +
                    "	p.prodescres descricao,\n" +
                    "	n.nutcaloria caloria,\n" +
                    "	n.nutcarboid carboidrato,\n" +
                    "	n.nutproteina proteina,\n" +
                    "	n.nutgordtot gordura,\n" +
                    "	n.nutgordsat gordurasaturada,\n" +
                    "	n.nutgordtrns gorduratrans,\n" +
                    "	n.nutfibra fibra,\n" +
                    "	n.nutsodio sodio,\n" +
                    "	concat(n.nutqtde, n.nutunidade) porcao\n" +
                    "from\n" +
                    "	hipnut n\n" +
                    "	join hippro p on n.nutcodplu = p.procodplu\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setCaloria(rst.getInt("caloria"));
                    imp.setCarboidrato(rst.getDouble("carboidrato"));
                    imp.setProteina(rst.getDouble("proteina"));
                    imp.setGordura(rst.getDouble("gordura"));
                    imp.setGorduraSaturada(rst.getDouble("gordurasaturada"));
                    imp.setGorduraTrans(rst.getDouble("gorduratrans"));
                    imp.setFibra(rst.getDouble("fibra"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPorcao(rst.getString("porcao"));
                    
                    imp.addProduto(rst.getString("id"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	l.lojestado uf,\n" +
                    "	p.proclasfisc ncm,\n" +
                    "	pl.prlpivast p_iva,\n" +
                    "	pl.prlvivast v_iva,\n" +
                    "	#pl.prlvpauta pauta,\n" +
                    "	pl.prlcodtrie icmside,\n" +
                    "	pl.prlcodtris icmsids\n" +
                    "from\n" +
                    "	hipprl pl\n" +
                    "	join hiploj l on\n" +
                    "		pl.prlloja = l.lojcod\n" +
                    "	join hippro p on\n" +
                    "		pl.prlcodplu = p.procodplu\n" +
                    "where\n" +
                    "	pl.prlloja = " + getLojaOrigem() + " and\n" +
                    "	(pl.prlpivast != 0 or\n" +
                    "	pl.prlvivast != 0)\n" +
                    "order by 1,2"
            )) {                
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    
                    imp.setId(formatPautaFiscalId(
                            rst.getString("uf"),
                            rst.getString("ncm"),
                            rst.getDouble("p_iva"),
                            rst.getDouble("v_iva")
                    ));
                    
                    imp.setNcm(rst.getString("ncm"));
                    imp.setUf(rst.getString("uf"));
                    
                    if (rst.getDouble("p_iva") != 0) {
                        imp.setTipoIva(TipoIva.PERCENTUAL);
                        imp.setIva(rst.getDouble("p_iva"));
                    } else {
                        imp.setTipoIva(TipoIva.VALOR);
                        imp.setIva(rst.getDouble("v_iva"));
                    }
                    
                    imp.setAliquotaDebitoId(rst.getString("icmsids"));
                    imp.setAliquotaCreditoId(rst.getString("icmside"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private String formatPautaFiscalId(String uf, String ncm, double p_iva, double v_iva) {
        return String.format("%s-%s-%.2f-%.2f", uf, ncm, p_iva, v_iva);
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	concat(c.cliloja,'-',c.clicod) id,\n" +
                    "	c.clicpfcnpj cnpj,\n" +
                    "	c.clirgie inscricaoestadual,\n" +
                    "	c.cliorgaopublic,\n" +
                    "	c.clinome razao,\n" +
                    "	coalesce(nullif(trim(c.clireduzid),''), c.clinome) fantasia,\n" +
                    "	1 ativo,\n" +
                    "	case sit.sitlicsn\n" +
                    "	when 'S' then 0\n" +
                    "	else 1 end as bloqueado,\n" +
                    "	c.cliender endereco,\n" +
                    "	c.clicompl complemento,\n" +
                    "	c.clibairro bairro,\n" +
                    "	c.clicidade cidade,\n" +
                    "	c.cliestado estado,\n" +
                    "	c.clicep cep,\n" +
                    "	c.cliestciv estadocivil,\n" +
                    "	c.clidtcadas datacadastro,\n" +
                    "	c.clidtnasc datanascimento,\n" +
                    "	c.clirazcom empresa,\n" +
                    "	c.cliendcom endereco_empresa,\n" +
                    "	c.clibaicom bairro_empresa,\n" +
                    "	c.clicidcom cidade_empresa,\n" +
                    "	c.cliestcom estado_empresa,\n" +
                    "	c.clicepcom cep_empresa,\n" +
                    "	c.clifonecom telefone_empresa,\n" +
                    "	c.clirenda salario,\n" +
                    "	c.clilimite valorlimite,\n" +
                    "	c.clicontconj conjuge,\n" +
                    "	c.clinomepai pai,\n" +
                    "	c.clinomemae mae,\n" +
                    "	c.clivdd,\n" +
                    "	c.cliprazopag prazopagamento,\n" +
                    "	c.clifoneres fone,\n" +
                    "	c.clifonepro,\n" +
                    "	c.cliemail email,\n" +
                    "	c.clicontato,\n" +
                    "	c.clifax\n" +
                    "from\n" +
                    "	clicli c\n" +
                    "	left join clisit sit on\n" +
                    "		c.clicodsitu = sit.sitcod\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    String estCiv = Utils.acertarTexto(rst.getString("estadocivil"));
                    if (estCiv.startsWith("CAS")) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else if (estCiv.startsWith("DIV")) {
                        imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                    } else if (estCiv.startsWith("SOL")) {
                        imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                    } else if (estCiv.startsWith("SEP")) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("endereco_empresa"));
                    imp.setEmpresaBairro(rst.getString("bairro_empresa"));
                    imp.setEmpresaMunicipio(rst.getString("cidade_empresa"));
                    imp.setEmpresaUf(rst.getString("estado_empresa"));
                    imp.setEmpresaCep(rst.getString("cep_empresa"));
                    imp.setEmpresaTelefone(rst.getString("telefone_empresa"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setPrazoPagamento(rst.getInt("prazopagamento"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setCelular(rst.getString("clifonepro"));
                    imp.setEmail(rst.getString("email"));
                    imp.addContato("A", rst.getString("clicontato"), "", "", "");
                    imp.setFax(rst.getString("clifax"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	concat(r.ctrtipo,'-',r.ctrcod,'-',r.ctrclilj,'-',r.ctrdoc,'-',r.ctrserie,'-',r.ctrparc,'-',r.ctrloja) id,\n" +
                    "	r.ctrdtemiss dataemissao,\n" +
                    "	r.ctrdoc numerocupom,\n" +
                    "	r.ctrcaixa ecf,\n" +
                    "	r.ctrvalor valor,\n" +
                    "	r.ctrjuros juros,\n" +
                    "	r.ctrdesc desconto,\n" +
                    "	r.ctrvalabt abatimento,\n" +
                    "	r.ctrsaldo valorfinal,\n" +
                    "	r.ctrobs observacao,\n" +
                    "	concat(r.ctrclilj,'-',r.ctrcod) idcliente,\n" +
                    "	r.ctrdtvenc vencimento,\n" +
                    "	r.ctrparc parcela\n" +
                    "from\n" +
                    "	finctr r\n" +
                    "where\n" +
                    "	r.ctrdtemiss >= '" + dateFormat.format(rotativoDataInicial) + "' and\n" +
                    "	r.ctrdtemiss <= '" + dateFormat.format(rotativoDataFinal) + "' and\n" +
                    "	r.ctrloja = " + getLojaOrigem() + " and\n" +
                    "	r.ctrvalor > 0 and r.ctrsaldo > 0 and\n" +
                    "	r.ctrtipo = 'C'\n" +
                    "order by\n" +
                    "	r.ctrdtemiss"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valorfinal"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CompradorIMP> getCompradores() throws Exception {
        List<CompradorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct cmpcod, cmpnome from hipcmp order by 1"
            )) {
                while (rst.next()) {
                    CompradorIMP imp = new CompradorIMP();
                    
                    imp.setId(rst.getString("cmpcod"));
                    imp.setDescricao(rst.getString("cmpnome"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rst.getString(""));
                    imp.setDataInicio(rst.getDate(""));
                    imp.setDataFim(rst.getDate(""));
                    imp.setPrecoOferta(rst.getDouble(""));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.procodplu,\n" +
                    "	p.prodescr,\n" +
                    "	a.reccod,\n" +
                    "	a.recdescr,\n" +
                    "	a.recmemo\n" +
                    "from\n" +
                    "	hiprec a\n" +
                    "	join hipprl pl on\n" +
                    "		a.reccod = pl.prlcodrec and\n" +
                    "		pl.prlloja = " + getLojaOrigem() + "\n" +
                    "	join hippro p on\n" +
                    "		p.procodplu = pl.prlcodplu\n" +
                    "order by\n" +
                    "	reccod, prlcodplu"
            )) {
                Map<String, ReceitaBalancaIMP> receitas = new HashMap<>();
                
                while (rst.next()) {
                    
                    ReceitaBalancaIMP imp = receitas.get(rst.getString("reccod"));
                    
                    if (imp == null) {
                       imp = new ReceitaBalancaIMP();
                       imp.setId(rst.getString("reccod"));
                       imp.setDescricao(rst.getString("recdescr"));
                       imp.setReceita(rst.getString("recmemo"));
                       receitas.put(imp.getId(), imp);
                    }
                    
                    imp.getProdutos().add(rst.getString("procodplu"));
                }
                
                return new ArrayList<>(receitas.values());
            }
        }
       
    }
    
    
    
}
