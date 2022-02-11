package vrimplantacao2.dao.interfaces.linear;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
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

/**
 *
 * @author Importacao
 */
public class LinearDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    private Date vendaDataIni;
    private Date vendaDataFim;
    private boolean multiplicarQtdEmbalagemPeloVolume = false;
    private boolean filtrarProdutos = false;

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    public void setFiltrarProdutos(boolean filtrarProdutos) {
        this.filtrarProdutos = filtrarProdutos;
    }   

    public void setMultiplicarQtdEmbalagemPeloVolume(boolean multiplicarQtdEmbalagemPeloVolume) {
        this.multiplicarQtdEmbalagemPeloVolume = multiplicarQtdEmbalagemPeloVolume;
    }

    public void setVendaDataIni(Date vendaDataIni) {
        this.vendaDataIni = vendaDataIni;
    }

    public void setVendaDataFim(Date vendaDataFim) {
        this.vendaDataFim = vendaDataFim;
    }
    
    /*@Override
    public String getSistema() {
        if (!"".equals(this.complemento)) {
            return "Linear - " + this.complemento;
        }
        return "Linear";
    }*/
    
     @Override
    public String getSistema() {
        return "Linear";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
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
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO
                }
        ));
    }
    
    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	emp_codigo AS id,\n" +
                    "	emp_razao AS razao,\n" +
                    "	emp_fantasia AS fantasia,\n" +
                    "	emp_cgc AS cnpj\n" +
                    "FROM empresa\n" +
                    "ORDER BY 1")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	codigo,\n" +
                    "	codpdv,\n" +
                    "	descricao,\n" +
                    "	valor icms,\n" +
                    "	cst,\n" +
                    "	reducao\n" +
                    "FROM \n" +
                    "	icms\n" +
                    "WHERE \n" +
                    "	codigo IN (SELECT e.ES1_TRIBUTACAO FROM es1 e WHERE e.es1_empresa = " + getLojaOrigem() + ")\n" +
                    "ORDER BY\n" +
                    "	codigo")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"), 
                            rs.getString("descricao"),
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
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	DISTINCT \n" +
                    "	f.TAB_COD merc1,\n" +
                    "	f.TAB_DESC descmerc1,\n" +
                    "	d.TAB_COD merc2,\n" +
                    "	d.TAB_DESC descmerc2,\n" +
                    "	s.tab_cod merc3,\n" +
                    "	s.tab_desc descmerc3\n" +
                    "FROM\n" +
                    "	es1p pr\n" +
                    "JOIN st_familia f ON pr.es1_familia = f.TAB_COD\n" +
                    "JOIN st_departamento d ON pr.es1_departamento = d.TAB_COD\n" +
                    "JOIN st_secao s ON pr.es1_secao = s.tab_cod\n" +
                    "order by f.TAB_DESC, d.TAB_DESC, s.tab_desc"
            )) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	tab_cod id,\n" +
                    "	tab_desc descricao\n" +
                    "FROM\n" +
                    "	st_semelhante")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	pr.es1_cod id,\n" +
                    "	ean.es1_codbarra ean,\n" +
                    "	pr.es1_desc descricaocompleta,\n" +
                    "	pr.es1_compl descricaoreduzida,\n" +
                    "	pr.es1_descetiqueta descricaogondola,\n" +
                    "	pc.Es1_UM unidade,\n" +
                    "	pc.ES1_QEMBV qtdembalagem,\n" +
                    "	pc.Es1_UM2 unidadecompra,\n" +
                    "	pc.es1_qembc qtdembalagemcompra,	\n" +
                    "	pr.es1_embalagem unidade_p,\n" +
                    "	pr.es1_convun qtdembalagem_p,	\n" +
                    "   if (pr.es1_familia = 0, null, pr.es1_familia) merc1,\n" +
                    "	if (pr.es1_departamento = 0, null, pr.es1_departamento) merc2,\n" +
                    "	if (pr.es1_secao = 0, null, pr.es1_secao) merc3,\n" +
                    "	pr.es1_semelhante idfamilia,\n" +
                    "	pc.es1_ncm ncm,\n" +
                    "	pc.es1_cest cest,\n" +
                    "	pc.Es1_Ativo situacao,\n" +
                    "	case \n" +
                    "		when pc.ES1_DTCAD < '1999-01-01' then '1995-01-01'\n" +
                    "		else pc.es1_dtcad \n" +
                    "	end cadastro,\n" +
                    "	pc.ES1_TRIBUTACAO idicms,\n" +
                    "	pc.es1_icmsent idicmsentrada,\n" +
                    "	pc.es1_margemcom margempadrao,\n" +
                    "	pc.es1_ultmargem margemvarejo,\n" +
                    "	pc.es1_prvarejo preco,\n" +
                    "	pc.es1_prcusto custosemimposto,\n" +
                    "	pc.es1_prcompra custocomimposto,\n" +
                    "	pc.es1_prcustomedio customedio,\n" +
                    "	pc.es1_classfiscal,\n" +
                    "	pc.es2_qatu estoque,\n" +
                    "	pc.Es1_ESTMINIMO estoqueminimo,\n" +
                    "	pc.Es1_ESTMAXIMO estoquemaximo,\n" +
                    "	pc.ES1_PESAVEL pesavel,\n" +
                    "	pc.ES1_BALANCA balanca,\n" +
                    "	pc.es1_vlbalanca validade,\n" +
                    "	pc.es1_pesol pesoliquido,\n" +
                    "	pc.es1_pesob pesobruto,\n" +
                    "	pc.es1_cstpis cstpis,\n" +
                    "	pc.es1_cstpisent cstpisent,\n" +
                    "	pc.pis_natreceita naturezareceita,\n" +
                    "	st.tab_valor iva,\n" +
                    "	pc.es1_icmsent idicmspauta,\n" +
                    "	icmsst.cst cstst\n" +
                    "FROM\n" +
                    "	es1p pr\n" +
                    "	JOIN es1 pc ON\n" +
                    "		pr.es1_cod = pc.ES1_COD\n" +
                    "	LEFT JOIN es1a ean ON\n" +
                    "		pr.es1_cod = ean.ES1_COD\n" +
                    "   left join st_margemst st on\n" +
                    "		pc.es1_margemst = st.tab_cod\n" +
                    "   left join (select * from icms where cst = '060') icmsst on\n" +
                    "		icmsst.codigo = pc.ES1_TRIBUTACAO\n"+
                    "WHERE \n" +
                    (this.filtrarProdutos ? "   not pr.id_central is null and\n": "") +
                    "	pc.es1_empresa = " + getLojaOrigem())) {
                //"   length(cast(convert(ean.es1_codbarra, UNSIGNED INTEGER) AS char)) > 6"
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getInt("balanca") == 1);
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    if (this.multiplicarQtdEmbalagemPeloVolume) {                        
                        imp.setTipoEmbalagemCotacao(rs.getString("unidade_p"));
                        imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagem_p"));
                    } else {
                        imp.setTipoEmbalagemCotacao(rs.getString("unidadecompra"));
                        imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcompra"));
                    }
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));
                    if (!"0000-00-00".equals(rs.getString("cadastro"))) {
                        imp.setDataCadastro(rs.getDate("cadastro"));
                    } else {
                        imp.setDataCadastro(format.parse("2000-01-01"));
                    }
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(rs.getString("idicmsentrada"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());
                    imp.setMargem(rs.getDouble("margemvarejo"));
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setCustoMedioComImposto(rs.getDouble("customedio"));
                    imp.setCustoMedioSemImposto(rs.getDouble("customedio"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpisent"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    if (rs.getString("cstst") != null) {
                        imp.setPautaFiscalId(buildPautaKey(rs.getString("ncm"), rs.getDouble("iva"), rs.getString("idicmspauta")));
                    }
                    
                    long ean = Utils.stringToLong(imp.getEan());
                    
                    if(imp.getEan() != null && !imp.getEan().equals("") && imp.isBalanca() == false) {
                        if(String.valueOf(ean).length() < 7) {
                            imp.setManterEAN(true);
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoMySQL.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select distinct\n" +
                        "	pc.es1_ncm ncm,\n" +
                        "	st.tab_valor iva,\n" +
                        "	pc.es1_icmsent idicmspauta\n" +
                        "FROM\n" +
                        "	es1p pr\n" +
                        "	JOIN es1 pc ON\n" +
                        "		pr.es1_cod = pc.ES1_COD\n" +
                        "	join st_margemst st on\n" +
                        "		pc.es1_margemst = st.tab_cod\n" +
                        "	join (select * from icms where cst = '060') icms on\n" +
                        "		icms.codigo = pc.ES1_TRIBUTACAO"
                )                
        ) {
            while (rs.next()) {
                PautaFiscalIMP imp = new PautaFiscalIMP();
                
                imp.setId(buildPautaKey(rs.getString("ncm"), rs.getDouble("iva"), rs.getString("idicmspauta")));
                imp.setNcm(rs.getString("ncm"));
                imp.setIva(rs.getDouble("iva"));
                imp.setIvaAjustado(rs.getDouble("iva"));
                imp.setAliquotaDebitoId(rs.getString("idicmspauta"));
                imp.setAliquotaDebitoForaEstadoId(rs.getString("idicmspauta"));
                imp.setAliquotaCreditoId(rs.getString("idicmspauta"));
                imp.setAliquotaCreditoForaEstadoId(rs.getString("idicmspauta"));
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
    private String buildPautaKey(String ncm, double iva, String idIcmsPauta) {
        return String.format("%s-%.2f-%s", ncm, iva, idIcmsPauta);
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	a.es1_cod idproduto,\n" +
                    "	a.cg2_cod idfornecedor,\n" +
                    "	a.es1_codforn codigoexterno,\n" +
                    "	b.cg2_data dataalteracao,\n" +
                    "	(case when p.es1_convun = 0 then 1 else p.es1_convun end) volume,\n" +
                    "	(case when b.es1_qemb = 0 then 1 else b.es1_qemb end) qtdembalagem,\n" +
                    "	b.cg2_valor custotabela\n" +
                    "FROM \n" +
                    "	es1i a\n" +
                    "	left join es1h b on\n" +
                    "		a.es1_cod = b.es1_cod and\n" +
                    "		a.cg2_cod = b.cg2_cod \n" +
                    "	join es1p p on\n" +
                    "		a.es1_cod = p.es1_cod\n" +
                    "where b.es1_empresa = " + getLojaOrigem()
            )) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setCustoTabela(rs.getDouble("custotabela"));
                    double volume = rs.getDouble("volume");
                    double qtdEmbalagem = rs.getDouble("qtdembalagem");
                    if (multiplicarQtdEmbalagemPeloVolume) {
                        imp.setQtdEmbalagem(qtdEmbalagem * volume);
                    } else {
                        imp.setQtdEmbalagem(qtdEmbalagem);                        
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	f.CG2_Cod id,\n" +
                    "	f.CG2_Nome razao,\n" +
                    "	f.cg2_fantasia fantasia,\n" +
                    "	f.CG2_CPF cpf,\n" +
                    "	f.cg2_cgc cnpj,\n" +
                    "	f.CG2_InscEstad ie,\n" +
                    "	f.cg2_rg rg,\n" +
                    "	f.cg2_inscmunicipal im,\n" +
                    "	f.cg2_inscprodutor ip,\n" +
                    "	f.CG2_End endereco,\n" +
                    "	f.cg2_numero numero,\n" +
                    "	f.cg2_compl complemento,\n" +
                    "	f.CG2_Bairro bairro,\n" +
                    "	f.CG2_CEP cep,\n" +
                    "	f.cg2_ibge municipioibge,\n" +
                    "	f.CG2_Cidade cidade,\n" +
                    "	f.CG2_UF uf,\n" +
                    "	f.CG2_Fone fone,\n" +
                    "	f.CG2_Fone1 fone1,\n" +
                    "	f.CG2_Fone2 fone2,\n" +
                    "	f.CG2_FAX fax,\n" +
                    "	f.CG2_Telex telefone,\n" +
                    "	f.CG2_Contato contato,\n" +
                    "	f.CG2_EMail email,\n" +
                    "	f.CG2_Data datacadastro,\n" +
                    "	f.cg2_observacao obs,\n" +
                    "	f.cg2_diavisita prazovisita,\n" +
                    "	f.cg2_frequencia frequencia,\n" +
                    "	f.cg2_prazoentrega prazoentrega,\n" +
                    "	f.cg2_ativo situacao,\n" +
                    "	f.cg2_tipofornecedor tipo\n" +
                    "FROM \n" +
                    "	cg2 f")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    String cpf, cnpj, rg, ie;
                    
                    cpf = rs.getString("cpf");
                    cnpj = rs.getString("cnpj");
                    rg = rs.getString("rg");
                    ie = rs.getString("ie");
                    
                    if(cpf != null && !"".equals(cpf)) {
                        imp.setCnpj_cpf(cpf);
                    } else {
                        imp.setCnpj_cpf(cnpj);
                    }
                    
                    if(ie != null && !"".equals(ie)) {
                        imp.setIe_rg(ie);
                    } else {
                        imp.setIe_rg(rg);
                    }
                    
                    imp.setInsc_municipal(rs.getString("im"));
                    imp.setEndereco(Utils.acertarTexto(rs.getString("endereco")));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(Utils.acertarTexto(rs.getString("complemento")));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setIbge_municipio(rs.getInt("municipioibge"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("fone"));
                    
                    String fax = rs.getString("fax"), email;
                    
                    if(fax != null && !"".equals(fax)) {
                        imp.addContato("FAX", Utils.acertarTexto(rs.getString("contato")), fax, null, TipoContato.NFE, null);
                    }
                    
                    email = rs.getString("email");
                    if(email != null && !"".equals(email)) {
                        imp.addContato("EMAIL", "EMAIL", null, null, TipoContato.NFE, email);
                    }
                    
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setObservacao(Utils.acertarTexto(rs.getString("obs")));
                    imp.setPrazoVisita(rs.getInt("prazovisita"));
                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    imp.setAtivo(rs.getInt("situacao") == 0);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	c.cg1_cod id,\n" +
                    "	c.cg1_dtcad cadastro,\n" +
                    "	c.cg1_cgc cnpj,\n" +
                    "	c.cg1_cpf cpf,\n" +
                    "	c.cg1_rg rg,\n" +
                    "	c.cg1_inscestadual ie,\n" +
                    "	c.cg1_inscmunicipal im,\n" +
                    "	c.cg1_nome razao,\n" +
                    "	c.cg1_fantasia fantasia,\n" +
                    "	c.cg1_end endereco,\n" +
                    "	c.cg1_compl complemento,\n" +
                    "	c.cg1_referencia1 referencia,\n" +
                    "	c.cg1_bairro bairro,\n" +
                    "	c.cg1_numero numero,\n" +
                    "	c.cg1_cep cep,\n" +
                    "	c.cg1_ibge ibgecidade,\n" +
                    "	c.cg1_cidade cidade,\n" +
                    "	c.cg1_uf uf,\n" +
                    "	c.cg1_celular celular,\n" +
                    "	c.cg1_fone telefone,\n" +
                    "	c.cg1_fax fax,\n" +
                    "	c.cg1_contato contato,\n" +
                    "	c.cg1_sexo sexo,\n" +
                    "	c.cg1_ativo ativo,\n" +
                    "	c.cg1_ativo2 ativo2,\n" +
                    "	c.cg1_email email,\n" +
                    "	c.cg1_EMailXmlNfe emailnfe,\n" +
                    "	c.cg1_email_boleto emailboleto,\n" +
                    "	c.cg1_pai pai,\n" +
                    "	c.cg1_mae mae,\n" +
                    "	case \n" +
                    "		when c.cg1_data < '1999-01-01' then '1995-01-01'\n" +
                    "		else c.cg1_data \n" +
                    "	end nascimento,\n" +
                    "	c.cg1_profissao profissao,\n" +
                    "	c.CG1_EstCivil estadocivil,\n" +
                    "	case\n" +
                    "	    when cg1_limite > 999999999.99 then 0 else\n" +
                    "	cg1_limite end as limite,\n" +
                    "	c.cg1_status STATUS,\n" +
                    "	c.cg1_localentrega enderecoentrega,\n" +
                    "	c.cg1_bairroent bairroentrega,\n" +
                    "	c.cg1_cidadeent cidadeentrega,\n" +
                    "	c.cg1_cepent cepentrega,\n" +
                    "	c.cg1_ufent ufentrega,\n" +
                    "	c.cg1_foneent foneentrega,\n" +
                    "	c.cg1_localcobra enderecocobranca,\n" +
                    "	c.cg1_complcobre complementocobranca,\n" +
                    "	c.cg1_bairrocobre bairrocobranca,\n" +
                    "	c.cg1_cidadecobre cidadecobranca,\n" +
                    "	c.cg1_cepcobre cepcobranca,\n" +
                    "	c.cg1_numerocobre numerocobranca,\n" +
                    "	c.cg1_ufcobre ufcobranca,\n" +
                    "	c.cg1_fonecobre fonecobranca,\n" +
                    "	c.cg1_observacao obs,\n" +
                    "	c.cg1_empresacliente empresa,\n" +
                    "	c.cg1_nomeconjuge conjuge\n" +
                    "FROM\n" +
                    "	cg1 c")) {                
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    String cpf, cnpj, rg, ie;
                    
                    cpf = rs.getString("cpf");
                    cnpj = rs.getString("cnpj");
                    rg = rs.getString("rg");
                    ie = rs.getString("ie");
                    
                    if(cpf != null && !"".equals(cpf)) {
                        imp.setCnpj(cpf);
                    } else {
                        imp.setCnpj(cnpj);
                    }
                    
                    if(ie != null && !"".equals(ie)) {
                        imp.setInscricaoestadual(ie);
                    } else {
                        imp.setInscricaoestadual(rg);
                    }
                    
                    imp.setInscricaoMunicipal(rs.getString("im"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipioIBGE(rs.getString("ibgecidade"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setFax(rs.getString("fax"));
                    
                    String contato = rs.getString("contato");
                    if(contato != null && !"".equals(contato)) {
                        imp.addContato("1", contato, null, null, null);
                    }
                    
                    imp.setSexo(rs.getInt("sexo") == 2 ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setAtivo(rs.getInt("ativo") == 0);
                    imp.setEmail(rs.getString("email"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setNomeMae(rs.getString("mae"));
                    if (!"0000-00-00".equals(rs.getString("nascimento"))) {
                        imp.setDataNascimento(rs.getDate("nascimento"));
                    } else {
                        imp.setDataNascimento(format.parse("2000-01-01"));
                    }
                    imp.setEstadoCivil(rs.getString("estadocivil"));
                    
                    String limite = rs.getString("limite");
                    imp.setValorLimite(rs.getDouble("limite"));
                    if(limite.length() > 17) {
                        imp.setValorLimite(0);
                    }
                    
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setObservacao(rs.getString("obs"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	f.FN1_NUM id,\n" +
                    "	f.FN1_PARC parcela,\n" +
                    "	f.CG1_COD idcliente,\n" +
                    "	f.fn1_doc documento,\n" +
                    "	f.caixa,\n" +
                    "	f.cupom,\n" +
                    "	f.FN1_EMISSAO emissao,\n" +
                    "	f.FN1_VENC vencimento,\n" +
                    "	f.fn1_hist observacao,\n" +
                    "	f.FN1_JUROS juros,\n" +
                    "	f.FN1_MULTA multa,\n" +
                    "	f.FN1_VALOR valor\n" +
                    "FROM \n" +
                    "	fn1 f\n" +
                    "WHERE \n" +
                    "	f.fn1_dtbaixa IS null AND\n" +
                    "	f.fn1_empresa = " + getLojaOrigem() + " AND\n" +
                    "	f.fn1_tipo NOT IN (37, 62, 64)")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setValor(rs.getDouble("valor"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	f.FN1_NUM id,\n" +
                    "	f.FN1_PARC parcela,\n" +
                    "	f.CG1_COD idcliente,\n" +
                    "	c.cg1_nome razao,\n" +
                    "	c.cg1_fone telefone,\n" +
                    "	c.cg1_cpf cpf,\n" +
                    "	c.cg1_cgc cnpj,\n" +
                    "	c.cg1_rg rg,\n" +
                    "	c.cg1_inscestadual ie,\n" +
                    "	f.fn1_doc documento,\n" +
                    "	f.fn1_cmc7 cmc7,\n" +
                    "	f.fn1_cheque cheque,\n" +
                    "	f.FN1_DTCHEQUE datacheque,\n" +
                    "	bc.cg1_banco banco,\n" +
                    "	bc.cg1_agencia agencia,\n" +
                    "	bc.cg1_conta conta,\n" +
                    "	f.caixa,\n" +
                    "	f.cupom,\n" +
                    "	f.FN1_EMISSAO emissao,\n" +
                    "	f.FN1_VENC vencimento,\n" +
                    "	f.fn1_hist observacao,\n" +
                    "	f.FN1_JUROS juros,\n" +
                    "	f.FN1_MULTA multa,\n" +
                    "	f.FN1_VALOR valor\n" +
                    "FROM \n" +
                    "	fn1 f\n" +
                    "JOIN cg1 c ON f.CG1_COD = c.cg1_cod\n" +
                    "LEFT JOIN cg1_banco bc ON f.cg1_banco_num = bc.cg1_banco_num\n" +
                    "WHERE \n" +
                    "	f.fn1_dtbaixa IS null AND\n" +
                    "	f.fn1_empresa = " + getLojaOrigem() + " AND \n" +
                    "	fn1_tipo IN (37, 62, 64)\n" +
                    "ORDER BY \n" +
                    "	f.FN1_VENC")) {
                while(rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setDataDeposito(rs.getDate("vencimento"));
                    imp.setNumeroCheque(rs.getString("documento"));
                    imp.setDate(rs.getDate("emissao"));
                    imp.setCmc7(rs.getString("cmc7"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setNome(rs.getString("razao"));
                    imp.setTelefone(rs.getString("telefone"));
                    
                    String cpf = rs.getString("cpf"), cnpj = rs.getString("cnpj"),
                            ie = rs.getString("ie"), rg = rs.getString("rg");
                    if(cpf != null && !"".equals(cpf)) {
                        imp.setCpf(cpf);
                    } else {
                        imp.setCpf(cnpj);
                    }
                    
                    if(rg != null && !"".equals(rg)) {
                        imp.setRg(rg);
                    } else {
                        imp.setRg(ie);
                    }
                    
                    imp.setValor(rs.getDouble("valor"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setEcf(rs.getString("caixa"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	o.es1_cod idproduto,\n" +
                    "	es1_dtini datainicio,\n" +
                    "	es1_dtfim datatermino,\n" +
                    "	es1_valor precooferta,\n" +
                    "	p.es1_prvarejo preconormal\n" +
                    "FROM \n" +
                    "	es1b o\n" +
                    "JOIN es1 p ON o.ES1_COD = p.ES1_COD AND \n" +
                    "	o.es1_empresa = p.es1_empresa\n" +
                    "WHERE \n" +
                    "	es1_dtfim > CURRENT_DATE AND \n" +
                    "	o.es1_empresa = " +  getLojaOrigem() + "\n" +
                    "ORDER BY 	\n" +
                    "	3")) {
                while(rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datatermino"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new LinearVendaIterator(getLojaOrigem(), this.vendaDataIni, this.vendaDataFim);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new LinearVendaItemIterator(getLojaOrigem(), this.vendaDataIni, this.vendaDataFim);
    }
    
}
