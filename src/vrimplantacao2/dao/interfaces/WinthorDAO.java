package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class WinthorDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "WINTHOR";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT\n" +
                "    codigo,\n" +
                "    codigo || ' - ' || coalesce(fantasia, razaosocial) descricao\n" +
                "FROM \n" +
                "    pcfilial \n" +
                "ORDER BY \n" +
                "    codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
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
                    "SELECT \n" +
                    "    codprodprinc id,\n" +
                    "    descricao,\n" +
                    "    usados\n" +
                    "from\n" +
                    "    (SELECT\n" +
                    "        p.codprod,\n" +
                    "        p.descricao,\n" +
                    "        p.CODPRODPRINC,\n" +
                    "        (SELECT COUNT(*) FROM pcprodut WHERE codprodprinc = p.codprod) usados\n" +
                    "    FROM \n" +
                    "        pcprodut p) A\n" +
                    "WHERE\n" +
                    "    usados > 1\n" +
                    "ORDER by\n" +
                    "    id"
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
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "    dp.codepto merc1,\n" +
                    "    dp.descricao merc1_descricao,\n" +
                    "    sc.codsec merc2,\n" +
                    "    sc.descricao merc2_descricao,\n" +
                    "    ct.codcategoria merc3,\n" +
                    "    ct.categoria merc3_descricao,\n" +
                    "    sc.codsubcategoria merc4,\n" +
                    "    sc.subcategoria merc4_descricao\n" +
                    "FROM\n" +
                    "    pcdepto dp\n" +
                    "    LEFT JOIN pcsecao sc ON\n" +
                    "        dp.codepto = sc.codepto\n" +
                    "    LEFT JOIN pccategoria ct ON\n" +
                    "        ct.codsec = sc.codsec\n" +
                    "    LEFT JOIN pcsubcategoria sc ON\n" +
                    "        sc.codsec = ct.codsec AND\n" +
                    "        sc.codcategoria = ct.codcategoria\n" +
                    "order BY\n" +
                    "    dp.codepto,\n" +
                    "    sc.codsec,\n" +
                    "    ct.codcategoria,\n" +
                    "    sc.codsubcategoria"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_descricao"));
                    imp.setMerc4ID(rst.getString("merc4"));
                    imp.setMerc4Descricao(rst.getString("merc4_descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT  \n" +
                    "    codst id,\n" +
                    "    mensagem descricao,\n" +
                    "    codicm icms,\n" +
                    "    sittribut cst,\n" +
                    "    percbasered reducao\n" +
                    "from\n" +
                    "    PCTRIBUT\n" +
                    "order by 1")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"), 
                            rs.getString("descricao"), 
                            rs.getInt("cst"), 
                            rs.getDouble("icms"), 
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    private class Trib {
        public int icmsCst;
        public double icmsAliq;
        public double icmsRed;
        public int pisCofins;

        public Trib(int icmsCst, double icmsAliq, double icmsRed, int pisCofins) {
            this.icmsCst = icmsCst;
            this.icmsAliq = icmsAliq;
            this.icmsRed = icmsRed;
            this.pisCofins = pisCofins;
        }
        
        
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            Map<String, Trib> tribs = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "      ent.ncm,\n" +
                    "      case ent.tipofornec\n" +
                    "      when 'I' then 1\n" +
                    "      when 'D' then 2\n" +
                    "      when 'C' then 3\n" +
                    "      when 'V' then 4\n" +
                    "      else 10 end tipo,\n" +
                    "      trib.codsittribpiscofins piscofins,\n" +
                    "      trib.sittribut icms_cst,\n" +
                    "      trib.percicm icms_aliquota,\n" +
                    "      trib.percbaseredent icms_reducao\n" +
                    "from \n" +
                    "     PCTRIBENTRADA ent\n" +
                    "     join PCTRIBFIGURA trib on\n" +
                    "          ent.codfigura = trib.codfigura\n" +
                    "     join pcfilial e on\n" +
                    "          e.codigo = ent.codfilial\n" +
                    "where\n" +
                    "     ent.codfilial = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "      ncm, tipo"
            )) {
                while (rst.next()) {
                    if (!tribs.containsKey(rst.getString("ncm"))) {
                        tribs.put(rst.getString("ncm"), new Trib(
                                rst.getInt("icms_cst"),
                                rst.getDouble("icms_aliquota"),
                                rst.getDouble("icms_reducao"),
                                rst.getInt("piscofins")
                        ));
                    }
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "    p.codprod id,\n" +
                    "    p.dtcadastro datacadastro, \n" +
                    "    ean.ean,\n" +
                    "    ean.qtunit qtdembalagem,\n" +
                    "    ean.unidade tipoembalagem,\n" +
                    "    p.aceitavendafracao e_balanca,\n" +
                    "    ean.prazoval validade,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    p.codepto merc1,\n" +
                    "    p.codsec merc2,\n" +
                    "    p.codcategoria merc3,\n" +
                    "    p.codsubcategoria merc4,\n" +
                    "    CASE WHEN p.codprodprinc != p.codprod then p.codprodprinc else null END id_familiaproduto,\n" +
                    "    round(coalesce(p.pesobruto, 0),2) pesobruto,\n" +
                    "    round(coalesce(p.pesoliq, 0),2) pesoliquido,\n" +
                    "    coalesce(est.estmin, 0) estoqueminimo,\n" +
                    "    coalesce(est.estmax, 0) estoquemaximo,    \n" +
                    "    coalesce(est.qtest,0) estoque,\n" +
                    "    coalesce(ean.margem,0) margem,\n" +
                    "    coalesce(est.custoreal,0) custosemimposto,\n" +
                    "    coalesce(est.custorep,0) custocomimposto,\n" +
                    "    coalesce(ean.pvenda / (CASE WHEN coalesce(ean.qtunit,1) = 0 THEN 1 ELSE coalesce(ean.qtunit,1) end),0) precovenda,\n" +
                    "    CASE WHEN pf.ativo = 'N' THEN 0 ELSE 1 END situacaocadastro,\n" +
                    "    p.nbm ncm,\n" +
                    "    coalesce(est.codcest, p.codcest) cest,\n" +
                    "    piscofins.sittribut piscofins_debito,\n" +
                    "    piscofins.descricaotribpiscofins,\n" +
                    "    p.codsittribpiscofins piscofins,\n" +
                    "    t.codnatrec piscofins_natrec,\n" +
                    "    tabst.codst idtributacao,\n" +
                    "    icms.sittribut icmscst,\n" +
                    "    icms.codicm icmsaliq,\n" +
                    "    icms.codicmtab icmsred,\n" +
                    "    p.codncmex\n" +
                    "FROM\n" +
                    "    pcprodut p\n" +
                    "    JOIN pcfilial emp ON emp.codigo = " + getLojaOrigem() + "\n" +
                    "    JOIN pcfornec f ON emp.codfornec = f.codfornec\n" +
                    "    LEFT JOIN (\n" +
                    "        SELECT\n" +
                    "            ean.codprod,\n" +
                    "            ean.codfilial,\n" +
                    "            ean.codauxiliar ean,\n" +
                    "            ean.unidade,\n" +
                    "            ean.qtunit,\n" +
                    "            ean.prazoval,\n" +
                    "            ean.pvenda,\n" +
                    "            ean.margem\n" +
                    "        FROM\n" +
                    "            pcembalagem ean\n" +
                    "        union\n" +
                    "        SELECT\n" +
                    "            a.codprod,\n" +
                    "            '0' codfilial,\n" +
                    "            a.ean,\n" +
                    "            a.unidade,\n" +
                    "            a.qtunit,\n" +
                    "            a.prazoval,\n" +
                    "            0 precovenda,\n" +
                    "            0 margem\n" +
                    "        from\n" +
                    "            (SELECT codprod, codauxiliar ean, descricao, unidade, qtunit, prazoval FROM pcprodut\n" +
                    "            UNION\n" +
                    "            SELECT codprod, codauxiliar2 ean, descricao, unidade, qtunit, prazoval FROM pcprodut) a\n" +
                    "        WHERE \n" +
                    "            NOT ean IN (SELECT codauxiliar FROM pcembalagem)\n" +
                    "        ORDER BY \n" +
                    "            codprod    \n" +
                    "    ) ean ON\n" +
                    "        ean.codprod = p.codprod AND\n" +
                    "        ean.codfilial = emp.codigo\n" +
                    "    JOIN pcest est ON\n" +
                    "        est.codprod = p.codprod AND\n" +
                    "        est.codfilial = emp.codigo\n" +
                    "    LEFT JOIN pcprodfilial pf ON\n" +
                    "        pf.codprod = p.codprod AND\n" +
                    "        pf.codfilial = emp.codigo\n" +
                    "    LEFT JOIN PCTABESCRSPED t ON\n" +
                    "        (T.CODPROD = p.codprod OR p.codprod = 0)\n" +
                    "        AND T.TIPOREGISTRO IN ('M4310','C4311','B4311','A4311','P4312')\n" +
                    "        AND ((T.DATAINIESCR IS NULL AND T.DATAFINESCR IS NULL)\n" +
                    "        OR  (T.DATAINIESCR <= current_date AND T.DATAFINESCR IS NULL)\n" +
                    "        OR  (current_date BETWEEN T.DATAINIESCR AND T.DATAFINESCR AND T.DATAINIESCR IS NOT NULL AND T.DATAFINESCR IS NOT NULL))\n" +
                    "    LEFT JOIN PCTABTRIB ic ON\n" +
                    "        ic.codprod = p.codprod\n" +
                    "        AND ic.codfilialnf = emp.codigo\n" +
                    "        AND ic.ufdestino = emp.uf\n" +
                    "    LEFT JOIN PCTRIBUT icms ON\n" +
                    "        ic.codst = icms.codst\n" +
                    "    LEFT JOIN pctribpiscofins piscofins ON\n" +
                    "        piscofins.codtribpiscofins = ic.codtribpiscofins\n" +
                    "    LEFT JOIN (select \n" +
                    "                    icm.codprod,\n" +
                    "                    icm.codst,\n" +
                    "                    reg.codfilial\n" +
                    "                from \n" +
                    "                    pctabpr icm \n" +
                    "                left join pcregiao reg on icm.numregiao = reg.numregiao\n" +
                    "                left join PCTRIBUT trb on icm.codst = trb.codst) tabst on tabst.codprod = p.codprod and\n" +
                    "                tabst.codfilial = emp.codigo\n" +
                    "ORDER BY id, ean"
            )) {
                int cont = 0, cont2 = 0;
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    if(rst.getString("e_balanca") != null && !"".equals(rst.getString("e_balanca"))) {
                        imp.seteBalanca("S".equals(rst.getString("e_balanca").trim()) ? true : false);
                    } else {
                        imp.seteBalanca(false);
                    }
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1("0".equals(rst.getString("merc1")) ? "" : rst.getString("merc1"));
                    imp.setCodMercadologico2("0".equals(rst.getString("merc2")) ? "" : rst.getString("merc2"));
                    imp.setCodMercadologico3("0".equals(rst.getString("merc3")) ? "" : rst.getString("merc3"));
                    imp.setCodMercadologico4("0".equals(rst.getString("merc4")) ? "" : rst.getString("merc4"));
                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setPesoBruto(Utils.stringToDouble(rst.getString("pesoliquido")));
                    imp.setPesoLiquido(Utils.stringToDouble(rst.getString("pesobruto")));                    
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(Utils.stringToInt(rst.getString("situacaocadastro"))));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    //imp.setPiscofinsCstDebito(rst.getInt("piscofins_debito"));
                    //imp.setPiscofinsCstCredito(0);
                    imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natrec"));
                    //imp.setIcmsCst(rst.getInt("icmscst"));
                    //imp.setIcmsAliq(rst.getDouble("icmsaliq"));
                    //imp.setIcmsReducao(rst.getDouble("icmsred"));
                    imp.setIcmsDebitoId(rst.getString("idtributacao"));
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());

                    Trib trib = tribs.get(rst.getString("codncmex"));
                    if (trib != null) {
                        imp.setPiscofinsCstDebito(0);
                        imp.setPiscofinsCstCredito(trib.pisCofins);
                        imp.setIcmsCst(trib.icmsCst);
                        imp.setIcmsAliq(trib.icmsAliq);
                        imp.setIcmsReducao(trib.icmsRed);
                        if ("561421".equals(imp.getImportId())) {
                            System.out.println(String.format(
                                    "cst=%d aliq=%f red=%f piscofins=%d",
                                    trib.icmsCst,
                                    trib.icmsAliq,
                                    trib.icmsRed,
                                    trib.pisCofins                                    
                            ));
                        }
                    }

                    result.add(imp);

                    cont2++;
                    cont++;

                    if (cont == 1000) {
                        ProgressBar.setStatus("Carregando produtos...." + cont2);
                        cont = 0;
                    }
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
                    "    f.codfornec id,\n" +
                    "    f.fornecedor razao,\n" +
                    "    f.fantasia,\n" +
                    "    f.cgc cnpj,\n" +
                    "    f.ie ie,\n" +
                    "    f.inscmunicip,\n" +
                    "    f.suframa,\n" +
                    "    CASE f.excluido WHEN 'S' THEN 0 ELSE 1 END ativo,\n" +
                    "    f.ender endereco,\n" +
                    "    f.numeroend numero,\n" +
                    "    f.complementoend complemento,\n" +
                    "    f.bairro,\n" +
                    "    f.cidade,\n" +
                    "    f.estado,\n" +
                    "    f.cep,\n" +
                    "    f.endercob cob_endereco,\n" +
                    "    f.bairrocob cob_bairro,\n" +
                    "    f.municob cob_cidade,\n" +
                    "    f.estcob cob_estado,\n" +
                    "    f.cepcob cob_cep,\n" +
                    "    f.telefonecom,\n" +
                    "    f.telefoneadm,\n" +
                    "    f.telrep,\n" +
                    "    f.telfab,\n" +
                    "    f.telcob,\n" +
                    "    f.prazomin,\n" +
                    "    f.prazoentrega,\n" +
                    "    f.email,\n" +
                    "    f.emailnfe,\n" +
                    "    CASE f.contribuinteicms WHEN 'S' THEN 1 ELSE 3 END contribuinteicms,\n" +
                    "    f.vlminpedcompra,\n" +
                    "    f.vlminpedreposicao,\n" +
                    "    f.dtcadastro,\n" +
                    "    f.obs,\n" +
                    "    f.obs2,\n" +
                    "    f.observacao,\n" +
                    "    f.codparcela,\n" +
                    "    parc.descricao parcela,\n" +
                    "    f.tipofornec\n" +
                    "FROM\n" +
                    "    pcfornec f\n" +
                    "    LEFT JOIN PCPARCELASC parc ON\n" +
                    "        f.codparcela = parc.codparcela\n" +
                    "ORDER BY\n" +
                    "    f.codfornec"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("inscmunicip"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCob_endereco(rst.getString("cob_endereco"));
                    imp.setCob_bairro(rst.getString("cob_bairro"));
                    imp.setCob_municipio(rst.getString("cob_cidade"));
                    imp.setCob_uf(rst.getString("cob_estado"));
                    imp.setCob_cep(rst.getString("cob_cep"));
                    imp.setTel_principal(rst.getString("telrep"));     
                    
                    if (Utils.stringToLong(rst.getString("telrep")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("REPRESENTANTE");
                        cont.setTelefone(Utils.stringLong(rst.getString("telrep")));
                        imp.getContatos().put(cont, "1");
                    }                    
                    if (Utils.stringToLong(rst.getString("telfab")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("FABRICANTE");
                        cont.setTelefone(Utils.stringLong(rst.getString("telfab")));
                        imp.getContatos().put(cont, "2");
                    }
                    if (Utils.stringToLong(rst.getString("telcob")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("COBRANCA");
                        cont.setTelefone(Utils.stringLong(rst.getString("telcob")));
                        imp.getContatos().put(cont, "3");
                    }                    
                    if (Utils.stringToLong(rst.getString("email")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("4");
                        cont.setNome("EMAIL");
                        cont.setTelefone(Utils.stringLong(rst.getString("email")));
                        imp.getContatos().put(cont, "4");
                    }
                    if (Utils.stringToLong(rst.getString("emailnfe")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("5");
                        cont.setNome("EMAIL NFE");
                        cont.setTelefone(Utils.stringLong(rst.getString("emailnfe")));
                        cont.setTipoContato(TipoContato.NFE);
                        imp.getContatos().put(cont, "5");
                    }
                    
                    imp.setObservacao(
                            "PRAZO MINIMO: " + Utils.acertarTexto(rst.getString("prazomin")) + "\n" +
                            "VLR. MINIMO REPOSICAO: " + Utils.stringToDouble(rst.getString("vlminpedreposicao")) + "\n" +
                            "PARCELAMENTO: " + Utils.stringToDouble(rst.getString("parcela")) + "\n" +
                            Utils.acertarTexto(rst.getString("obs")) + "\n" +
                            Utils.acertarTexto(rst.getString("obs2")) + "\n" +
                            Utils.acertarTexto(rst.getString("observacao"))
                    );
                    imp.setValor_minimo_pedido(rst.getDouble("vlminpedcompra"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));
                    switch (Utils.acertarTexto(rst.getString("tipofornec"))) {
                        case "I": imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA); break;
                        case "D": imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR); break;
                        default: imp.setTipoFornecedor(TipoFornecedor.ATACADO); break;
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
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT m.* from\n" +
                    "(SELECT codfornec, codprod \n" +
                    "FROM pcmov GROUP BY codfornec, codprod) m\n" +
                    "JOIN pcprodut p ON m.codprod = p.codprod\n" +
                    "JOIN pcfornec f ON m.codfornec = f.codfornec"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("codfornec"));
                    imp.setIdProduto(rst.getString("codprod"));
                    imp.setCodigoExterno(rst.getString("codprod"));
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
            try (Statement stm2 = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(                        
                        "SELECT\n" +
                        "    c.codcli id,\n" +
                        "    c.cgcent cnpj,\n" +
                        "    coalesce(c.ieent, c.rg) inscricaoestadual,\n" +
                        "    c.orgaorg orgaoemissor,\n" +
                        "    c.cliente razao,\n" +
                        "    c.fantasia,    \n" +
                        "    CASE c.bloqueio WHEN 'S' THEN 1 ELSE 0 END bloqueado,\n" +
                        "    c.dtbloq databloqueio,\n" +
                        "    c.enderent endereco,\n" +
                        "    c.numeroent numero,\n" +
                        "    c.complementoent complemento,\n" +
                        "    c.bairroent bairro,\n" +
                        "    c.municent municipio,\n" +
                        "    c.estent estado,\n" +
                        "    c.cepent cep,\n" +
                        "    c.dtnasc datanascimento,\n" +
                        "    c.dtcadastro datacadastro,\n" +
                        "    CASE c.sexo WHEN 'F' THEN 0 ELSE 1 END sexo,\n" +
                        "    c.empresa,\n" +
                        "    c.enderempr empresaendereco,\n" +
                        "    c.municempr empresamunicipio,\n" +
                        "    c.estempr empresauf,\n" +
                        "    c.telempr empresatelefone,\n" +
                        "    c.dtadmissao dataadmissao,\n" +
                        "    c.cargo,\n" +
                        "    c.rendamensal salario,\n" +
                        "    c.limcred valorlimite,\n" +
                        "    c.nomeconjuge,\n" +
                        "    c.filiacaopai nomepai,\n" +
                        "    c.filiacaomae nomemae,\n" +
                        "    c.observacao,\n" +
                        "    c.obscredito,\n" +
                        "    c.obs,\n" +
                        "    c.obs2,\n" +
                        "    c.obs3,\n" +
                        "    c.obs4,\n" +
                        "    c.diafaturar diavencimento,\n" +
                        "    c.telent,\n" +
                        "    c.telent1,\n" +
                        "    c.telcob,\n" +
                        "    c.telcom,\n" +
                        "    c.telconjuge,\n" +
                        "    c.telcelent,\n" +
                        "    c.email,\n" +
                        "    c.emailnfe,\n" +
                        "    c.emailcob,\n" +
                        "    c.faxcli,\n" +
                        "    c.endercob,\n" +
                        "    c.numerocob,\n" +
                        "    c.complementocob,\n" +
                        "    c.bairrocob,\n" +
                        "    c.municcob,\n" +
                        "    c.estcob,\n" +
                        "    c.cepcob\n" +
                        "FROM \n" +
                        "    PCCLIENT c\n" +
                        "WHERE\n" +
                        "    CODCOB <> 'CONV'\n" +
                        "ORDER BY\n" +
                        "    c.codcli"
                )) {
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
                        imp.setDataBloqueio(rst.getDate("databloqueio"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("municipio"));
                        imp.setUf(rst.getString("estado"));
                        imp.setCep(rst.getString("cep"));
                        imp.setDataNascimento(rst.getDate("datanascimento"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        switch (rst.getString("sexo")) {
                            case "F": imp.setSexo(TipoSexo.FEMININO); break;
                            default: imp.setSexo(TipoSexo.MASCULINO); break;
                        }
                        imp.setEmpresa(rst.getString("empresa"));
                        imp.setEmpresaEndereco(rst.getString("empresaendereco"));
                        imp.setEmpresaMunicipio(rst.getString("empresamunicipio"));
                        imp.setEmpresaUf(rst.getString("empresauf"));
                        imp.setEmpresaTelefone(rst.getString("empresatelefone"));
                        imp.setDataAdmissao(rst.getDate("dataadmissao"));
                        imp.setCargo(rst.getString("cargo"));
                        imp.setSalario(rst.getDouble("salario"));
                        imp.setValorLimite(rst.getDouble("valorlimite"));
                        imp.setNomeConjuge(rst.getString("nomeconjuge"));
                        imp.setNomePai(rst.getString("nomepai"));
                        imp.setNomeMae(rst.getString("nomemae"));
                        imp.setObservacao(
                                Utils.acertarTexto(rst.getString("observacao")) + "\n" + 
                                Utils.acertarTexto(rst.getString("obs")) + "\n" +
                                (rst.getString("telent") != null ?  "\n" + "TEL. PRINC: " + rst.getString("telent") : "") +
                                (rst.getString("telent1") != null ? "\n" + "CELULAR: " + rst.getString("telent1") : "") +
                                (rst.getString("telcob") != null ? "\n" + "TEL. COB: " + rst.getString("telcob") : "") +
                                (rst.getString("telcom") != null ? "\n" + "TEL. COM: " + rst.getString("telcom") : "") +
                                (rst.getString("telconjuge") != null ? "\n" + "TEL. CONJUGE: " + rst.getString("telconjuge") : "") +
                                (rst.getString("telcelent") != null ? "\n" + "CELULAR2: " + rst.getString("telcelent") : "") +
                                (rst.getString("email") != null ? "\n" + "EMAIL: " + rst.getString("email") : "") +
                                (rst.getString("emailnfe") != null ? "\n" + "EMAIL NF-E: " + rst.getString("emailnfe") : "") +
                                (rst.getString("emailcob") != null ? "\n" + "EMAIL COB.: " + rst.getString("emailcob") : "") 
                        );
                        imp.setTelefone(rst.getString("telent"));
                        imp.setCelular(rst.getString("telent1"));
                        
                        if (rst.getString("emailnfe") != null) {
                            imp.addContato("1", "NFE", "", "", rst.getString("emailnfe"));
                        }
                        
                        imp.setCobrancaEndereco(rst.getString("endercob"));
                        imp.setCobrancaNumero(rst.getString("numerocob"));
                        imp.setCobrancaComplemento(rst.getString("complementocob"));
                        imp.setCobrancaBairro(rst.getString("bairrocob"));
                        imp.setCobrancaMunicipio(rst.getString("municcob"));
                        imp.setCobrancaUf(rst.getString("estcob"));
                        imp.setCobrancaCep(rst.getString("cepcob"));

                        result.add(imp);
                    }
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
                    "SELECT \n" +
                    "    r.numtransvenda || '-' || r.prest id,\n" +
                    "    r.dtemissao emissao,\n" +
                    "    r.duplic cupom,\n" +
                    "    r.valor,\n" +
                    "    r.obsfinanc obs,\n" +
                    "    r.codcli,\n" +
                    "    r.dtvenc vencimento\n" +
                    "FROM \n" +
                    "    PCPREST r\n" +
                    "WHERE \n" +
                    "    r.codfilialnf = " + getLojaOrigem() + " and\n" +
                    "    r.dtpag IS NULL AND \n" +
                    "    r.codcli != 1 AND \n" +
                    "    not r.codcob IN ('CANC', 'BK', 'CHDV', 'CHD1', 'CHP', 'CONV', 'JUR')"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setIdCliente(rst.getString("codcli"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
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
                    "    r.numtransvenda || '-' || r.prest id,\n" +
                    "    c.cgcent cnpj,\n" +
                    "    r.numcheque || CASE WHEN r.dvcheque IS NULL THEN '' ELSE '-' || r.dvcheque END numerocheque,\n" +
                    "    r.numbanco banco,\n" +
                    "    r.numagencia || CASE WHEN r.dvagencia IS NULL THEN '' ELSE '-' || r.dvagencia END agencia,\n" +
                    "    r.numcontacorrente || CASE WHEN r.dvconta IS NULL THEN '' ELSE '-' || r.dvconta END conta,\n" +
                    "    r.dtemissao data,\n" +
                    "    r.duplic numerocupom,\n" +
                    "    r.valor,\n" +
                    "    coalesce(c.ieent, c.rg) rg,\n" +
                    "    c.telent telefone,\n" +
                    "    c.cliente nome,\n" +
                    "    r.obs observacao,\n" +
                    "    r.alinea,\n" +
                    "    r.dtultalter dataHoraAlteracao\n" +
                    "FROM \n" +
                    "    PCPREST r\n" +
                    "    JOIN pcclient c ON\n" +
                    "        r.codcli = c.codcli\n" +
                    "WHERE \n" +
                    "    r.codfilialnf = " + getLojaOrigem() + " and\n" +
                    "    r.dtpag IS NULL AND \n" +
                    "    r.codcli != 1 AND \n" +
                    "    r.codcob IN ('CHP')"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cnpj"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(rst.getDate("data"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setAlinea(rst.getInt("alinea"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataHoraAlteracao"));
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
    
}
