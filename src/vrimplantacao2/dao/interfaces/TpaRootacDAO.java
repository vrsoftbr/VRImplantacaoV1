package vrimplantacao2.dao.interfaces;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * 
 * @author leandro
 */
public class TpaRootacDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        if ("".equals(this.complemento)) {
            return "TPA/ROOTAC";
        } else {
            return "TPA/ROOTAC - " + this.complemento;
        }
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with loja as (\n" +
                    "	select\n" +
                    "		lojc05codi id,\n" +
                    "		lojc02esta uf\n" +
                    "	from\n" +
                    "		rc002loj lj\n" +
                    "	where\n" +
                    "		lj.lojc05codi = '" + getLojaOrigem() + "'\n" +
                    "),\n" +
                    "trib as(\n" +
                    "	select\n" +
                    "		rf.FIGNID_FIG id,\n" +
                    "		max(rf.FIGDDATVIG) datavigencia\n" +
                    "	from\n" +
                    "		RC104FIG rf\n" +
                    "		join loja on\n" +
                    "			rf.FIGC02ORIG = loja.uf and\n" +
                    "			rf.FIGC02DEST = loja.uf\n" +
                    "		join RCEstPre pre on\n" +
                    "			pre.PRECCODLOJ = loja.id\n" +
                    "	group by\n" +
                    "		FIGNID_FIG\n" +
                    ")\n" +
                    "select distinct\n" +
                    "	case substring(cast(rf.FIGC03CFOP as varchar(5)),1,1)\n" +
                    "		when '5' then 'SAIDA'\n" +
                    "		when '6' then 'SAIDA'\n" +
                    "		when '1' then 'ENTRADA'\n" +
                    "		when '2' then 'ENTRADA'\n" +
                    "	end operacao,\n" +
                    "	replace(rf.FIGC02FIGU, '\\', '\\\\') id,\n" +
                    "	rf.FIGC003CST cst,\n" +
                    "	rf.FIGN02ALIQ aliquota,\n" +
                    "	rf.FIGN05REDU reducao\n" +
                    "from\n" +
                    "	RC104FIG rf\n" +
                    "	join trib on\n" +
                    "		rf.FIGNID_FIG = trib.id and \n" +
                    "		rf.FIGDDATVIG = trib.datavigencia\n" +
                    "where\n" +
                    "	rf.FIGC03CFOP in (5929, 1102, 1403)"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            String.format(
                                    "%s-%s",
                                    rs.getString("operacao"),
                                    rs.getString("id")
                            ),
                            String.format(
                                    "%03d-%.2f-%.2f",
                                    rs.getInt("cst"),
                                    rs.getDouble("aliquota"),
                                    rs.getDouble("reducao")
                            ),                            
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")
                    ));
                }
            }
            return result;
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	lojc05codi id,\n" +
                    "	lojc35raza razao\n" +
                    "from\n" +
                    "	rc002loj\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("id"),
                                    rst.getString("razao")
                            )
                    );
                }
            }
        }
        return result;
    }
    
    private void preencherNivel5(MercadologicoNivelIMP n4) throws Exception {
        try (Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	GRUC03SETO merc1,\n" +
                        "	GRUC03GRUP merc2,\n" +
                        "	GRUC03SUBG merc3,\n" +
                        "	GRUC03FAMI merc4,\n" +
                        "	GRUC03SUBF merc5,\n" +
                        "	GRUC35DESC descricao\n" +
                        "from\n" +
                        "	RC001GRU rg\n" +
                        "where\n" +
                        "	GRUC03SETO = '" + n4.getMercadologicoPai().getMercadologicoPai().getMercadologicoPai().getId() + "'\n" +
                        "	and GRUC03GRUP = '" + n4.getMercadologicoPai().getMercadologicoPai().getId() + "'\n" +
                        "	and GRUC03SUBG = '" + n4.getMercadologicoPai().getId() + "'\n" +
                        "	and GRUC03FAMI = '" + n4.getId() + "'\n" +
                        "	and GRUC03SUBF != ''"
                )
        ) {
            while (rs.next()) {
                n4.addFilho(rs.getString("merc5"), rs.getString("descricao"));
            }
        }
    }
    
    private void preencherNivel4(MercadologicoNivelIMP n3) throws Exception {
        try (Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	GRUC03SETO merc1,\n" +
                        "	GRUC03GRUP merc2,\n" +
                        "	GRUC03SUBG merc3,\n" +
                        "	GRUC03FAMI merc4,\n" +
                        "	GRUC35DESC descricao\n" +
                        "from\n" +
                        "	RC001GRU rg\n" +
                        "where\n" +
                        "	GRUC03SETO = '" + n3.getMercadologicoPai().getMercadologicoPai().getId() + "'\n" +
                        "	and GRUC03GRUP = '" + n3.getMercadologicoPai().getId() + "'\n" +
                        "	and GRUC03SUBG = '" + n3.getId() + "'\n" +
                        "	and GRUC03FAMI != ''\n" +
                        "	and GRUC03SUBF = ''"
                )
        ) {
            while (rs.next()) {
                preencherNivel5(n3.addFilho(rs.getString("merc4"), rs.getString("descricao")));
            }
        }
    }
    
    private void preencherNivel3(MercadologicoNivelIMP n2) throws Exception {
        try (Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	GRUC03SETO merc1,\n" +
                        "	GRUC03GRUP merc2,\n" +
                        "	GRUC03SUBG merc3,\n" +
                        "	GRUC35DESC descricao\n" +
                        "from\n" +
                        "	RC001GRU rg\n" +
                        "where\n" +
                        "	GRUC03SETO = '" + n2.getMercadologicoPai().getId() + "'\n" +
                        "	and GRUC03GRUP = '" + n2.getId() + "'\n" +
                        "	and GRUC03SUBG != ''\n" +
                        "	and GRUC03FAMI = ''"
                )
        ) {
            while (rs.next()) {
                preencherNivel4(n2.addFilho(rs.getString("merc3"), rs.getString("descricao")));
            }
        }
    }
    
    private void preencherNivel2(MercadologicoNivelIMP n1) throws Exception {
        try (Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	GRUC03SETO merc1,\n" +
                        "	GRUC03GRUP merc2,\n" +
                        "	GRUC35DESC descricao\n" +
                        "from\n" +
                        "	RC001GRU rg\n" +
                        "where\n" +
                        "	GRUC03SETO = '" + n1.getId() + "'\n" +
                        "	and GRUC03GRUP != ''\n" +
                        "	and GRUC03SUBG = ''"
                )
        ) {
            while (rs.next()) {
                preencherNivel3(n1.addFilho(rs.getString("merc2"), rs.getString("descricao")));
            }
        }
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select GRUC03SETO merc1, GRUC35DESC descricao from RC001GRU rg where GRUC03GRUP = ''"
            )) {
                int count = 1;
                while (rs.next()) {
                    ProgressBar.setStatus("Carregando o mercadológico " + count);
                    count++;
                    MercadologicoNivelIMP n1 = new MercadologicoNivelIMP(
                        rs.getString("merc1"),
                        rs.getString("descricao")
                    );
                    preencherNivel2(n1);
                    result.add(n1);
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.NCM,
                OpcaoProduto.PRECO,
                OpcaoProduto.CEST,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS
        ));
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	p.CODIGOPLU id,\n" +
                        "	p.PROCDESCRI descricao\n" +
                        "from\n" +
                        "	RC003EST p\n" +
                        "where\n" +
                        "	not nullif(PROCCODPAI, '') is null\n" +
                        "	and CODIGOPLU = PROCCODPAI"
                )
        ) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with ean as (\n" +
                    "	select CODIGOPLU, EANCCODBAR, EANNQTDEMB from RC003EST where ltrim(rtrim(EANCCODBAR)) != ''\n" +
                    "	union\n" +
                    "	select CODIGOPLU, EANCCODBAR, EANNQTDEMB from RCEstEAN	\n" +
                    ")\n" +
                    "select\n" +
                    "	p.CODIGOPLU id,\n" +
                    "	coalesce(ean.EANCCODBAR, p.CODIGOPLU) ean,\n" +
                    "	coalesce(ean.EANNQTDEMB, p.EANNQTDEMB) qtdembalagem,\n" +
                    "	p.REFNQTDEMB qtdembalagemcotacao,\n" +
                    "	p.PROCEMBVDA unidade,\n" +
                    "	p.PROCPESADO pesado,\n" +
                    "	0 validade,\n" +
                    "	p.PROCDESCRI descricaocompleta,\n" +
                    "	p.PROCDESRES descricaoreduzida,\n" +
                    "	p.PROCCODSET merc1,	\n" +
                    "	p.PROCCODGRU merc2,\n" +
                    "	p.PROCCODSGR merc3,\n" +
                    "	p.PROCCODFAM merc4,\n" +
                    "	p.PROCCODSFA merc5,\n" +
                    "	p.PROCCODPAI id_familia,\n" +
                    "	p.PRONPESBRU pesobruto,\n" +
                    "	p.PRONPESLIQ pesoliquido,\n" +
                    "	est.ESTNESTMIN estoqueminimo,\n" +
                    "	est.ESTNESTMAX estoquemaximo,\n" +
                    "	est.ESTNESTATU estoque,\n" +
                    "	pre.PRENVDAMRE margem,\n" +
                    "	pre.PRENVDAATU preco,\n" +
                    "	pre.PRENCUSREP custocomimposto,\n" +
                    "	pre.PRENCUSCSA custosemimposto,\n" +
                    "	pre.PRENCUSMED customedio,\n" +
                    "	pre.PRECPROLIN ativo,\n" +
                    "	p.PROCCODNCM ncm,\n" +
                    "	p.PROCCODCES cest,\n" +
                    "	replace(pre.PRECFIGURA, '\\', '\\\\') id_icms\n" +
                    "from\n" +
                    "	RC003EST p\n" +
                    "	join rc002loj lj on\n" +
                    "		lj.lojc05codi = '" + getLojaOrigem() + "'\n" +
                    "	left join ean on\n" +
                    "		p.CODIGOPLU = ean.CODIGOPLU\n" +
                    "	left join RCEstEst est on\n" +
                    "		p.CODIGOPLU = est.CODIGOPLU AND \n" +
                    "		est.ESTCCODLOJ = lj.lojc05codi\n" +
                    "	left join RCEstPre pre on\n" +
                    "		p.CODIGOPLU = pre.CODIGOPLU and\n" +
                    "		pre.PRECCODLOJ = lj.lojc05codi\n" +
                    "order by\n" +
                    "	p.CODIGOPLU"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    
                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rs.getString("ean"), -2));
                    if (bal != null) {
                        imp.setQtdEmbalagem(1);
                        imp.seteBalanca(true);
                        imp.setEan(rs.getString("ean"));
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(bal.getValidade());
                    } else {                    
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                        imp.seteBalanca("S".equals(rs.getString("pesado")));
                        imp.setEan(rs.getString("ean"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setValidade(rs.getInt("validade"));
                    }
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    imp.setCodMercadologico5(rs.getString("merc5"));
                    imp.setIdFamiliaProduto(rs.getString("id_familia"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoMedio(rs.getDouble("customedio"));
                    imp.setSituacaoCadastro("S".equals(rs.getString("ativo")) ? 1 : 0);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    
                    imp.setIcmsDebitoId("SAIDA-" + rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId("SAIDA-" + rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId("SAIDA-" + rs.getString("id_icms"));
                    imp.setIcmsConsumidorId("SAIDA-" + rs.getString("id_icms"));
                    if (rs.getString("id_icms") != null) {
                        imp.setIcmsCreditoId("ENTRADA-" + rs.getString("id_icms"));
                        imp.setIcmsCreditoForaEstadoId("ENTRADA-" + rs.getString("id_icms"));
                    } else {
                        imp.setIcmsCreditoId("SAIDA-" + rs.getString("id_icms"));
                        imp.setIcmsCreditoForaEstadoId("SAIDA-" + rs.getString("id_icms"));
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
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                      "select \n"
                    + "	codifabric id,\n"
                    + "	forc35raza razao,\n"
                    + "	forc35raza fantasia,\n"
                    //+ "	forc10apel fantasia,\n"
                    + "	forc15cgc cnpj,\n"
                    + "	forc19insc ie_rg,\n"
                    + "	forc35ende endereco,\n"
                    + "	forc10comp complemento,\n"
                    + "	forc20bair bairro,\n"
                    + "	forccdibge ibge_municipio,\n"
                    + "	forc20cida municipio,\n"
                    + "	forc02esta uf,\n"
                    + "	forc08cep cep,\n"
                    + "	forc25fone telefone,\n"
                    + "	forddtinic datacadastro,\n"
                    + "	observacoe observacao,"
                    + " forc20cont contato\n"
                    + "from rc008for"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(Utils.stringToInt(rst.getString("ibge_municipio")));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("contato"));
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
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	rr.REFCCADCOD id_fornecedor,\n" +
                        "	rr.CODIGOPLU id_produto,\n" +
                        "	rr.REFCREFERE codigoexterno,\n" +
                        "	rr.REFNQTDEMB qtdembalagem\n" +
                        "from\n" +
                        "	RCEstREF rr\n" +
                        "where\n" +
                        "	not nullif(rr.REFCCADCOD,'') is null and\n" +
                        "	not nullif(rr.CODIGOPLU,'') is null and\n" +
                        "	not nullif(rr.REFCREFERE,'') is null\n" +
                        "union\n" +
                        "select\n" +
                        "	re.REFCCODFOR id_fornecedor,\n" +
                        "	re.CODIGOPLU id_produto,\n" +
                        "	re.REFCREFERE codigoexterno,\n" +
                        "	re.REFNQTDEMB qtdembalagem\n" +
                        "from\n" +
                        "	RC003EST re\n" +
                        "where\n" +
                        "	not nullif(re.REFCCODFOR,'') is null and\n" +
                        "	not nullif(re.CODIGOPLU,'') is null and\n" +
                        "	not nullif(re.REFCREFERE,'') is null\n" +
                        "order by\n" +
                        "	1, 2"
                )
        ) {
            while (rs.next()) {
                ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setIdFornecedor(rs.getString("id_fornecedor"));
                imp.setIdProduto(rs.getString("id_produto"));
                imp.setCodigoExterno(rs.getString("codigoexterno"));
                imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	c.clic05clie id,\n" +
                        "	c.clic15cgc cnpj,\n" +
                        "	c.clic19rg ie,\n" +
                        "	c.clic35nome razaosocial,\n" +
                        "	c.clic10apel nomefantasia,\n" +
                        "	c.clic01stat ativo,\n" +
                        "	c.clic35ende endereco,\n" +
                        "	c.clic20bair bairro,\n" +
                        "	c.clic20cida municipio,\n" +
                        "	c.clic02esta uf,\n" +
                        "	c.clic08cep cep,\n" +
                        "	c.clic18fone as telefone,\n" +
                        "	c.clic11fax as fax,\n" +
                        "	c.clic40obs1 as observacao,\n" +
                        "	c.estadocivi as estadocivil,\n" +
                        "	c.datanascim datanascimento,\n" +
                        "	c.clid08cada datacadastro,\n" +
                        "	c.clic01sexo sexo,\n" +
                        "	c.empresnome as empresa,\n" +
                        "	c.empresende as empresaendereco,\n" +
                        "	c.empresesta as empresauf,\n" +
                        "	c.emprescida as empresamunicipio,\n" +
                        "	c.empresbair as empresabairro,\n" +
                        "	c.empresacep as empresacep,\n" +
                        "	c.empresfone as empresatelefone,\n" +
                        "	c.empresafax as empresafax,\n" +
                        "	c.clin14sala salario,\n" +
                        "	c.clinlimcon limite,\n" +
                        "	c.clic40obs1,\n" +
                        "	c.clic40obs2,\n" +
                        "	c.clic40obs3,\n" +
                        "	c.clindiavct diavencimento,\n" +
                        "	c.clic18fone fone,\n" +
                        "	c.CLIC11FAX fax,\n" +
                        "	c.clicemail email\n" +
                        "from\n" +
                        "	rc042cli c\n" +
                        "order by\n" +
                        "	1"
                )
        ) {
            while (rs.next()) {
                ClienteIMP imp = new ClienteIMP();
                
                imp.setId(rs.getString("id"));
                imp.setCnpj(rs.getString("cnpj"));
                imp.setInscricaoestadual(rs.getString("ie"));
                imp.setRazao(rs.getString("razaosocial"));
                imp.setFantasia(rs.getString("nomefantasia"));
                imp.setAtivo("0".equals(rs.getString("ativo")));
                imp.setEndereco(rs.getString("endereco"));
                imp.setBairro(rs.getString("bairro"));
                imp.setMunicipio(rs.getString("municipio"));
                imp.setUf(rs.getString("uf"));
                imp.setCep(rs.getString("cep"));
                imp.setTelefone(rs.getString("telefone"));
                imp.setFax(rs.getString("fax"));
                imp.setObservacao2(rs.getString("observacao"));
                imp.setEstadoCivil(rs.getString("estadocivil"));
                imp.setDataNascimento(rs.getDate("datanascimento"));
                imp.setDataCadastro(rs.getDate("datacadastro"));
                imp.setSexo(rs.getString("sexo"));
                imp.setEmpresa(rs.getString("empresa"));
                imp.setEmpresaEndereco(rs.getString("empresaendereco"));
                imp.setEmpresaUf(rs.getString("empresauf"));
                imp.setEmpresaMunicipio(rs.getString("empresamunicipio"));
                imp.setEmpresaBairro(rs.getString("empresabairro"));
                imp.setEmpresaCep(rs.getString("empresacep"));
                imp.setEmpresaTelefone(rs.getString("empresatelefone"));                
                imp.setSalario(rs.getDouble("salario"));
                imp.setValorLimite(rs.getDouble("limite"));
                imp.setDiaVencimento(rs.getInt("diavencimento"));
                imp.setTelefone(rs.getString("fone"));
                imp.setFax(rs.getString("fax"));
                imp.setEmail(rs.getString("email"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

}

/*
CONVENIO

select
    l.lojc05codi id,
    l.lojc35raza razao,
    l.lojc15cgc cnpj,
    l.lojc19insc inscricaoestadual,
    l.lojc35ende endereco,
    l.lojc10comp complemento,
    l.lojcbairro bairro,
    l.lojc20cida municipio,
    l.lojc02esta uf,
    l.lojc08cep cep,
    l.lojc18fone telefone,
    getDate() datainicio,
    getDate() + 12000 datatermino,
    'S' ativo,
    0 desconto,
    10 diapagamento,
    'N' bloqueado
from
    rc002loj l
*/