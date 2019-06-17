package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class ViaSoftDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "ViaSoft";
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  estab id,\n" +
                    "  reduzido || ' - CNPJ: ' || cnpj fantasia\n" +
                    "from\n" +
                    "  viasoftsys.filial\n" +
                    "where\n" +
                    "  inativa = 'N'")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  distinct\n" +
                    "  iddepto merc1,\n" +
                    "  dsdepto descmerc1,\n" +
                    "  idsetor merc2,\n" +
                    "  dssetor descmerc2,\n" +
                    "  idgrupoitem merc3,\n" +
                    "  dsgrupoitem descmerc3,\n" +
                    "  idfamilia merc4,\n" +
                    "  dsfamilia descmerc4,\n" +
                    "  idsubfamilia merc5,\n" +
                    "  dssubfamilia descmerc5\n" +
                    "from\n" +
                    "   VIASOFTMERC.V_ITEMCATEGORIA\n" +
                    "order by\n" +
                    "  iddepto, idsetor, idgrupoitem, idfamilia")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    imp.setMerc4ID(rs.getString("merc4"));
                    imp.setMerc4Descricao(rs.getString("descmerc4"));
                    imp.setMerc5ID(rs.getString("merc5"));
                    imp.setMerc5Descricao(rs.getString("descmerc5"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  idfamilia,\n" +
                    "  descricao\n" +
                    "from\n" +
                    "  viasoftmcp.familia\n" +
                    "order by\n" +
                    "  idfamilia")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("idfamilia"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
     
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "  IDITEM ID,\n" +
                    "  CODBARRAS,\n" +
                    "  QTDE\n" +
                    "FROM\n" +
                    "  VIASOFTBASE.ITEMCODBARRAS")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codbarras"));
                    imp.setQtdEmbalagem(rs.getInt("qtde"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "    IE.IDITEM id,\n" +
                    "    I.DESCRICAO descricaocompleta,\n" +
                    "    I.DESCRICAOALT descricaoreduzida,\n" +
                    "    I.NROCODBARR AS codigobarras,\n" +
                    "    I.UNIDADE,\n" +
                    "    I.NCM,\n" +
                    "    I.CODCEST,\n" +
                    "    I.PESOLIQUIDO,\n" +
                    "    I.PESOBRUTO,\n" +
                    "    P.IDFAMILIAPRECO,\n" +
                    "    CASE \n" +
                    "      WHEN I.EHKIT = 'S' THEN 0.01\n" +
                    "      ELSE COALESCE(FE.PRECO, F.PRECO, P.PRECO, 0) \n" +
                    "    END AS PRECO,\n" +
                    "    EST.SALDO ESTOQUE,\n" +
                    "    P.MARGEMVENDA AS MARGEMPRECO,\n" +
                    "    ARREDONDAR((DIVIDE(COALESCE(FE.PRECO, F.PRECO, P.PRECO, 0),IA.CUSTOAQUIS)-1)*100,2) AS MARKUP,\n" +
                    "    ARREDONDAR(100 - (DIVIDE(IA.CUSTOAQUIS,COALESCE(FE.PRECO, F.PRECO, P.PRECO, 0))*100),2) AS MARGEM,\n" +
                    "    IE.PMZ,\n" +
                    "    IM.DIASVALIDADE,\n" +
                    "    IM.VENDAPESO,\n" +
                    "    IM.ENVBALANCA PESAVEL,\n" +
                    "    CASE\n" +
                    "      WHEN FC.USACUSTO=1 THEN IA.CUSTOEST\n" +
                    "      ELSE IA.CUSTOAQUIS\n" +
                    "    END AS CUSTO,\n" +
                    "    V.ICMS_ALIQ ICMS,\n" +
                    "    V.ICMS_CST CST,\n" +
                    "    V.PIS_CST,\n" +
                    "    V.COFINS_CST,\n" +
                    "    NAT.COFINS_NATOPISEN NATUREZARECEITA,\n" +
                    "    V.ATIVO,\n" +
                    "    GP.IDDEPTO MERC1,\n" +
                    "    GP.IDSETOR MERC2,\n" +
                    "    GP.IDGRUPOITEM MERC3,\n" +
                    "    GP.IDFAMILIA MERC4,\n" +
                    "    GP.IDSUBFAMILIA MERC5\n" +
                    "  FROM VIASOFTMCP.ITEMESTAB IE\n" +
                    "  LEFT JOIN VIASOFTBASE.ITEM I\n" +
                    "    ON I.ESTAB  = IE.ESTABITEM\n" +
                    "   AND I.IDITEM = IE.IDITEM\n" +
                    "  LEFT JOIN VIASOFTMCP.FILIALCONFCAD FC\n" +
                    "    ON FC.ESTAB = IE.ESTAB\n" +
                    "  LEFT JOIN VIASOFTMCP.ITEMPRVDA P\n" +
                    "    ON P.IDBANDEIRA = FC.IDBANPRECO\n" +
                    "   AND P.ESTAB      = I.ESTAB\n" +
                    "   AND P.IDITEM     = I.IDITEM\n" +
                    "  LEFT JOIN VIASOFTBASE.ITEMAQUIS IA\n" +
                    "    ON IA.IDBANDEIRA = FC.IDBANCUSTO\n" +
                    "   AND IA.ESTAB = IE.ESTABITEM\n" +
                    "   AND IA.IDITEM = IE.IDITEM  \n" +
                    "  LEFT JOIN VIASOFTMCP.ITEMESTABPRVDA PE\n" +
                    "    ON PE.ESTAB     = IE.ESTAB\n" +
                    "   AND PE.ESTABITEM = IE.ESTABITEM\n" +
                    "   AND PE.IDITEM    = IE.IDITEM\n" +
                    "  LEFT JOIN VIASOFTMCP.FAMILIAPRECO F\n" +
                    "    ON F.IDBANDEIRA     = FC.IDBANPRECO\n" +
                    "   AND F.IDFAMILIAPRECO = P.IDFAMILIAPRECO\n" +
                    "  LEFT JOIN VIASOFTMCP.FAMILIAPRECOESTAB FE\n" +
                    "    ON FE.IDBANDEIRA     = FC.IDBANPRECO\n" +
                    "   AND FE.ESTAB          = IE.ESTAB\n" +
                    "   AND FE.IDFAMILIAPRECO = F.IDFAMILIAPRECO\n" +
                    "  LEFT JOIN VIASOFTMERC.V_PRODUTOS_COLETA V \n" +
                    "    ON I.NROCODBARR = V.CODBARRAS\n" +
                    "  LEFT JOIN VIASOFTMERC.V_ITEM_ALTPRECO GP \n" +
                    "    ON GP.IDITEM = I.IDITEM \n" +
                    "   AND GP.ESTAB = IE.ESTAB\n" +
                    "   LEFT JOIN VIASOFTMERC.ITEMMERC IM\n" +
                    "    ON IE.IDITEM = IM.IDITEM \n" +
                    "   LEFT JOIN VIASOFTMERC.V_ESTOQUEFISCAL EST\n" +
                    "    ON IE.IDITEM = EST.IDITEM\n" +
                    "   AND IE.ESTAB = EST.ESTAB\n" +
                    "   LEFT JOIN VIASOFTMERC.V_SYNCPDV_PRODUTOS NAT \n" +
                    "    ON IE.IDITEM = NAT.IDITEM \n" +
                    "   AND IE.ESTAB = NAT.ESTAB\n" +
                    "WHERE\n" +
                    "  IE.ESTAB = " + getLojaOrigem() + "\n" +
                    "ORDER BY\n" +
                    "  I.IDITEM")) {
                while(rs.next()) {
                   ProdutoIMP imp = new ProdutoIMP();
                   imp.setImportSistema(getSistema());
                   imp.setImportLoja(getLojaOrigem());
                   imp.setImportId(rs.getString("id"));
                   imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                   imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                   imp.setDescricaoGondola(rs.getString("descricaogondola"));
                   imp.setEan(rs.getString("codigobarras"));
                   imp.setTipoEmbalagem(rs.getString("unidade"));
                   imp.setNcm(rs.getString("ncm"));
                   imp.setCest(rs.getString("codcest"));
                   imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                   imp.setPesoBruto(rs.getDouble("pesobruto"));
                   imp.setIdFamiliaProduto(rs.getString("idfamiliapreco"));
                   imp.setPrecovenda(rs.getDouble("preco"));
                   imp.setEstoque(rs.getDouble("estoque"));
                   imp.setMargem(rs.getDouble("margempreco"));
                   imp.setValidade(rs.getInt("diasvalidade"));
                   if((rs.getString("pesavel") != null) && (!"".equals(rs.getString("pesavel")))) {
                       imp.seteBalanca("S".equals(rs.getString("pesavel").trim()));
                   }
                   imp.setCustoComImposto(rs.getDouble("custo"));
                   imp.setCustoSemImposto(rs.getDouble("custo"));
                   imp.setIcmsAliq(rs.getDouble("icns"));
                   imp.setIcmsCst(rs.getString("cst"));
                   imp.setIcmsReducao(0);
                   imp.setPiscofinsCstCredito(rs.getString("pis_cst"));
                   imp.setPiscofinsCstDebito(rs.getString("cofins_cst"));
                   imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                   if("S".equals(rs.getString("ativo"))) {
                       imp.setSituacaoCadastro(1);
                   } else {
                       imp.setSituacaoCadastro(0);
                   }
                   imp.setCodMercadologico1(rs.getString("merc1"));
                   imp.setCodMercadologico1(rs.getString("merc2"));
                   imp.setCodMercadologico1(rs.getString("merc3"));
                   imp.setCodMercadologico1(rs.getString("merc4"));
                   imp.setCodMercadologico1(rs.getString("merc5"));
                   
                   result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "  iditem idproduto,\n" +
                    "  idpess idfornecedor,\n" +
                    "  iditemforn codigoexterno,\n" +
                    "  e.unidade\n" +
                    "from \n" +
                    "  viasoftmcp.itempessban pf\n" +
                    "left join viasoftbase.embalagem e on (e.idembalagem = pf.idembalagem)\n" +
                    "order by\n" +
                    "  iditem, idpess")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  pd.IDPESS id,\n" +
                    "  p.NOME razao,\n" +
                    "  p.CNPJF cnpj,\n" +
                    "  pd.FANTASIA,\n" +
                    "  pd.rg,\n" +
                    "  pd.inscmun,\n" +
                    "  pd.email,\n" +
                    "  pc.ATIVO,\n" +
                    "  pd.DTINATIVO,\n" +
                    "  pc.LIMITE,\n" +
                    "  en.ENDERECO,\n" +
                    "  en.NUMEND,\n" +
                    "  en.BAIRRO,\n" +
                    "  ci.nome cidade,\n" +
                    "  ci.UF,\n" +
                    "  ci.ibge cidadeibge,\n" +
                    "  en.COMPLEMENTO,\n" +
                    "  en.CELULAR,\n" +
                    "  en.TELEFONE,\n" +
                    "  pm.DTCADASTRO,\n" +
                    "  pm.SEXO\n" +
                    "from \n" +
                    "  viasoftbase.pessoa p\n" +
                    "join viasoftbase.pessoadoc pd on (p.idpessoa = pd.idpessoa)\n" +
                    "left join viasoftbase.pessoadocconv pc on (pd.idpess = pc.idpess)\n" +
                    "left join viasoftbase.pessoadocend pe on (pe.idpess = pd.idpess)\n" +
                    "left join viasoftbase.endereco en on (pe.idend = en.idend)\n" +
                    "left join viasoftsys.cidade ci on (en.cidade = ci.cidade)\n" +
                    "left join viasoftmerc.pessoadocmerc pm on (pd.idpess = pm.idpess)\n" +
                    "where \n" +
                    "  pm.ehfornecedor = 'S' and\n" +
                    "  pe.tipoend = 'C'\n" +
                    "order by\n" +
                    "  pd.IDPESS")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setIe_rg(rs.getString("rg"));
                    imp.setInsc_municipal(rs.getString("inscmun"));
                    if(rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    imp.setAtivo(rs.getString("datainativo") != null ? false : true);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numend"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setComplemento(rs.getString("complemento"));
                    if(rs.getString("celular") != null && !"".equals(rs.getString("celular"))) {
                        imp.addContato("2", "CELULAR", null, rs.getString("celular"), TipoContato.COMERCIAL, null);
                    }
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setDatacadastro(rs.getDate("dtcadastro"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  pd.IDPESS id,\n" +
                    "  p.NOME razao,\n" +
                    "  p.CNPJF cnpj,\n" +
                    "  pd.FANTASIA,\n" +
                    "  pd.rg,\n" +
                    "  pd.inscmun,\n" +
                    "  pd.email,\n" +
                    "  pc.ATIVO,\n" +
                    "  pd.DTINATIVO,\n" +
                    "  pc.LIMITE,\n" +
                    "  en.ENDERECO,\n" +
                    "  en.NUMEND,\n" +
                    "  en.BAIRRO,\n" +
                    "  ci.nome cidade,\n" +
                    "  ci.UF,\n" +
                    "  ci.ibge cidadeibge,\n" +
                    "  en.COMPLEMENTO,\n" +
                    "  en.CELULAR,\n" +
                    "  en.TELEFONE,\n" +
                    "  pm.DTCADASTRO,\n" +
                    "  pm.SEXO\n" +
                    "from \n" +
                    "  viasoftbase.pessoa p\n" +
                    "join viasoftbase.pessoadoc pd on (p.idpessoa = pd.idpessoa)\n" +
                    "left join viasoftbase.pessoadocconv pc on (pd.idpess = pc.idpess)\n" +
                    "left join viasoftbase.pessoadocend pe on (pe.idpess = pd.idpess)\n" +
                    "left join viasoftbase.endereco en on (pe.idend = en.idend)\n" +
                    "left join viasoftsys.cidade ci on (en.cidade = ci.cidade)\n" +
                    "left join viasoftmerc.pessoadocmerc pm on (pd.idpess = pm.idpess)\n" +
                    "where \n" +
                    "  pd.ehcliente = 'S' and\n" +
                    "  pe.tipoend = 'C'\n" +
                    "order by\n" +
                    "  pd.IDPESS")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setInscricaoMunicipal(rs.getString("inscmun"));
                    imp.setEmail(rs.getString("email"));
                    imp.setAtivo(rs.getString("datainativo") != null ? false : true);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numend"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  idduprec id,\n" +
                    "  duprec documento,\n" +
                    "  fatura,\n" +
                    "  idpess idcliente,\n" +
                    "  dtemissao,\n" +
                    "  dtvencto,\n" +
                    "  valor,\n" +
                    "  valorfatura,\n" +
                    "  valororig,\n" +
                    "  historico,\n" +
                    "  idcaixa ecf\n" +
                    "from\n" +
                    "  viasoftfin.duprec f\n" +
                    "where\n" +
                    "  f.estab = " + getLojaOrigem() + " and\n" +
                    "  f.quitada = 'N'")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataVencimento(rs.getDate("dtvencto"));
                    imp.setValor(rs.getDouble("valorfatura"));
                    imp.setObservacao(rs.getString("historico"));
                    imp.setEcf(rs.getString("ecf"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
