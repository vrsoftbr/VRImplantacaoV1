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
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descricao")));
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
            try (ResultSet rst = stm.executeQuery(
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
                    "	p.PROCCODCES cest\n" +
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
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    
                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));
                    if (bal != null) {
                        imp.setQtdEmbalagem(1);
                        imp.seteBalanca(true);
                        imp.setEan(rst.getString("ean"));
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(bal.getValidade());
                    } else {                    
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.seteBalanca("S".equals(rst.getString("pesado")));
                        imp.setEan(rst.getString("ean"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(rst.getInt("validade"));
                    }
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));
                    imp.setCodMercadologico5(rst.getString("merc5"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoMedio(rst.getDouble("customedio"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("ativo")) ? 1 : 0);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    
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
                    + "	forc10apel fantasia,\n"
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

}
