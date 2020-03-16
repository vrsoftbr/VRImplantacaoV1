package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoProduto;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class SnSistemaDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {        
        return "SN Sistema" + (
                "".equals(complemento) ?
                "" :
                " - " + complemento
        );
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	fantasia\n" +
                    "from\n" +
                    "	empresa \n" +
                    "order by\n" +
                    "	codigo"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(
                            rs.getString("codigo"),
                            rs.getString("fantasia")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.TIPO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS,
                OpcaoProduto.OFERTA
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	dp.CODDEPARTAMENTO merc1,\n" +
                    "	dp.DESCRICAO merc1_desc,\n" +
                    "	sc.CODSECAO merc2,\n" +
                    "	sc.DESCRICAO merc2_desc\n" +
                    "from\n" +
                    "	departamento dp\n" +
                    "	join SECAO sc on\n" +
                    "		sc.CODDEPARTAMENTO = dp.CODDEPARTAMENTO\n" +
                    "order by\n" +
                    "	dp.CODDEPARTAMENTO,\n" +
                    "	sc.CODSECAO"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("merc1_desc"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("merc2_desc"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT\n" +
                    "	f.CODIGO id,\n" +
                    "	f.NOME descricao,\n" +
                    "	count(*)\n" +
                    "from\n" +
                    "	PRODUTO p\n" +
                    "	join produto f on\n" +
                    "		p.CODPRODPRINC = f.CODIGO\n" +
                    "WHERE \n" +
                    "	p.CODPRODPRINC != p.CODIGO\n" +
                    "group by\n" +
                    "	f.CODIGO, f.NOME\n" +
                    "having\n" +
                    "	count(*) > 1\n" +
                    "order BY\n" +
                    "	f.CODIGO "
            )) {
                while (rs.next()) {
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
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select distinct\n" +
                    "	t.GRUPOICMS,\n" +
                    "	case cfop.OPERACAO when 'S' then 1 else 0 end operacao,\n" +
                    "	case t.TIPOCALCULO when 2 then 1 else 0 end contribicms,\n" +
                    "	case when t.UF = cid.CODESTADO then 1 else 0 end dentroUf,\n" +
                    "	concat(\n" +
                    "		'cst: ', cst.codigo, ' ',\n" +
                    "		'aliq: ', t.PERCENTUALICMS, ' ',\n" +
                    "		'red: ', case t.PERCENTUALBASECALCULO when 100 then 0 else t.PERCENTUALBASECALCULO end, ' ',\n" +
                    "		g.DESCRICAO\n" +
                    "	) descricao,\n" +
                    "	cst.codigo cst,\n" +
                    "	t.PERCENTUALICMS aliquota,\n" +
                    "	case t.PERCENTUALBASECALCULO when 100 then 0 else t.PERCENTUALBASECALCULO end reduzido\n" +
                    "from\n" +
                    "	TRIBUTACAOICMS t\n" +
                    "	join EMPRESA e on\n" +
                    "		e.CODIGO = 1\n" +
                    "	join CIDADE cid on\n" +
                    "		e.CODCIDADE = cid.CODMUNICIPIO \n" +
                    "	join CST on\n" +
                    "		t.cst = cst.id\n" +
                    "	join cfop on\n" +
                    "		t.CFOP = cfop.CODOPERACAO\n" +
                    "	join GRUPOICMS g on\n" +
                    "		t.GRUPOICMS = g.ID \n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            getTributacaoKey(
                                    rs.getString("GRUPOICMS"),
                                    rs.getBoolean("operacao"),
                                    rs.getBoolean("contribicms"),
                                    rs.getBoolean("dentroUf")
                            ),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reduzido")
                    ));
                }
            }
        }
        
        return result;
    }

    private String getTributacaoKey(String grupoIcms, boolean saida, boolean contribIcms, boolean dentroDoEstado) {
        return String.format(
                "%s-%s-%s-%s",
                grupoIcms,
                (saida ? "S" : "E"),
                (contribIcms ? "CONTRIB" : "NAO CONTRIB"),
                (dentroDoEstado ? "DENT_UF" : "FORA_UF")
        );
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "with ean as (\n" +
                    "	select codigo, CODBARRA from PRODUTO p\n" +
                    "	union\n" +
                    "	select CODIGO, INFADICIONAIS codbarra from produto where ltrim(rtrim(coalesce(INFADICIONAIS , ''))) != '' \n" +
                    "),\n" +
                    "pis as (\n" +
                    "	select distinct\n" +
                    "		t.GRUPOPISCOFINS,\n" +
                    "		cst.CODIGO cst,\n" +
                    "		case op.FINALIDADE\n" +
                    "			when 1 then 'S'\n" +
                    "			when 4 then 'E'\n" +
                    "		end as entradasaida\n" +
                    "	from\n" +
                    "		TRIBUTACAOPISCOFINS t\n" +
                    "		join CSTPISCOFINS cst on\n" +
                    "			t.CSTPIS = cst.CODIGO\n" +
                    "		join NATOPERACAO op on\n" +
                    "			t.NATUREZAOPERACAO = op.CODIGO\n" +
                    "	WHERE\n" +
                    "		t.NATUREZAOPERACAO in (3, 5)\n" +
                    ")\n" +
                    "select\n" +
                    "	p.CODIGO id,\n" +
                    "	p.DTCADASTRO datacadastro,\n" +
                    "	p.DTULTALTERACAO dataultimaalteracao,\n" +
                    "	ean.codbarra ean,\n" +
                    "	p.UNIDADE,\n" +
                    "	p.FTEMBALAGEM qtdembalagemcotacao,\n" +
                    "	1 as qtdembalagem,\n" +
                    "	p.ISPRODUTOPESADO pesavel,\n" +
                    "	p.DIASVALIDADE validade,\n" +
                    "	p.ACEITAVENDAFRACIONADA,\n" +
                    "	p.NOME descricaocompleta,\n" +
                    "	p.TIPO,\n" +
                    "	p.DEPARTAMENTO merc1,\n" +
                    "	p.SECAO merc2,\n" +
                    "	p.CODPRODPRINC id_familia,\n" +
                    "	p.FORNECEDOR id_fornecedor,\n" +
                    "	p.PESOBRUTO ,\n" +
                    "	p.PESOLIQUIDO,\n" +
                    "	pe.QTESTOQUEMINIMO estoqueminimo,\n" +
                    "	pe.QTESTOQUE estoque,\n" +
                    "	pr.PERMARGEMLUCRO margem,\n" +
                    "	pe.CUSTOULTENTRADA custocomimposto,\n" +
                    "	pe.CUSTOMEDIO custocomimpostomedio,\n" +
                    "	pr.PRECOCONSUMIDOR precovenda,\n" +
                    "	p.ATIVO,\n" +
                    "	p.CODNCM ncm,\n" +
                    "	p.CODCEST cest,\n" +
                    "	pise.cst piscofins_e,\n" +
                    "	piss.cst piscofins_s,\n" +
                    "	'PADRAO VR' as natrec,\n" +
                    "	pe.CODGRUPOICMS\n" +
                    "from\n" +
                    "	PRODUTO p\n" +
                    "	join EMPRESA e on\n" +
                    "		e.CODIGO = " + getLojaOrigem() + "\n" +
                    "	join CIDADE cid on\n" +
                    "		e.CODCIDADE = cid.CODMUNICIPIO \n" +
                    "	join ean on\n" +
                    "		p.CODIGO = ean.codigo\n" +
                    "	left join PRECO_ESTOQUE pe on \n" +
                    "		pe.CODEMPRESA = e.CODIGO and\n" +
                    "		pe.CODPRODUTO = p.CODIGO\n" +
                    "	left join PRECOTABELA pr on\n" +
                    "		pr.CODEMPRESAPRECO = pe.CODEMPRESA and\n" +
                    "		pr.CODPRODUTOPRECO = pe.CODPRODUTO and\n" +
                    "		pr.CODTABELA = 1\n" +
                    "	left join pis pise on\n" +
                    "		pise.GRUPOPISCOFINS = pe.CODGRUPOPISCOFINS and\n" +
                    "		pise.entradasaida = 'E'\n" +
                    "	left join pis piss on\n" +
                    "		piss.GRUPOPISCOFINS = pe.CODGRUPOPISCOFINS and\n" +
                    "		piss.entradasaida = 'S'\n" +
                    "order by\n" +
                    "	p.CODIGO "
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataultimaalteracao"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("UNIDADE"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.seteBalanca(rs.getBoolean("pesavel"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setTipoProduto(
                            "S".equals(rs.getString("tipo"))
                                    ? TipoProduto.SERVICOS
                                    : TipoProduto.MERCADORIA_REVENDA
                    );
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setIdFamiliaProduto(rs.getString("id_familia"));
                    imp.setFornecedorFabricante(rs.getString("id_fornecedor"));
                    imp.setPesoBruto(rs.getDouble("PESOBRUTO"));
                    imp.setPesoLiquido(rs.getDouble("PESOLIQUIDO"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custocomimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rs.getBoolean("ATIVO") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("piscofins_e"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofins_s"));
                    imp.setIcmsDebitoId(getTributacaoKey(
                            rs.getString("CODGRUPOICMS"),
                            true,
                            true,
                            true
                    ));
                    imp.setIcmsDebitoForaEstadoId(getTributacaoKey(
                            rs.getString("CODGRUPOICMS"),
                            true,
                            true,
                            false
                    ));
                    imp.setIcmsDebitoForaEstadoId(getTributacaoKey(
                            rs.getString("CODGRUPOICMS"),
                            true,
                            true,
                            false
                    ));
                    imp.setIcmsConsumidorId(getTributacaoKey(
                            rs.getString("CODGRUPOICMS"),
                            true,
                            true,
                            true
                    ));
                    imp.setIcmsCreditoId(getTributacaoKey(
                            rs.getString("CODGRUPOICMS"),
                            false,
                            true,
                            true
                    ));
                    imp.setIcmsCreditoId(getTributacaoKey(
                            rs.getString("CODGRUPOICMS"),
                            false,
                            true,
                            false
                    ));                    
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	p.CODIGO id_produto,\n" +
                    "	pr.DTVALIDADEPRECOOFERTA datatermino,\n" +
                    "	pr.PRECOOFERTA,\n" +
                    "	pr.PRECOOFERTAATIVO\n" +
                    "from\n" +
                    "	PRODUTO p\n" +
                    "	join EMPRESA e on\n" +
                    "		e.CODIGO = " + getLojaOrigem() + "\n" +
                    "	left join PRECO_ESTOQUE pe on \n" +
                    "		pe.CODEMPRESA = e.CODIGO and\n" +
                    "		pe.CODPRODUTO = p.CODIGO\n" +
                    "	left join PRECOTABELA pr on\n" +
                    "		pr.CODEMPRESAPRECO = pe.CODEMPRESA and\n" +
                    "		pr.CODPRODUTOPRECO = pe.CODPRODUTO and\n" +
                    "		pr.CODTABELA = 1\n" +
                    "where \n" +
                    "	pr.PRECOOFERTAATIVO = 1 and\n" +
                    "	pr.DTVALIDADEPRECOOFERTA >= convert(date, getDate())\n" +
                    "order by\n" +
                    "	p.CODIGO  "
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(new Date());
                    imp.setDataFim(rs.getDate("datatermino"));
                    imp.setPrecoOferta(rs.getDouble("PRECOOFERTA"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	p.CODIGO id,\n" +
                    "	p.NOME razao,\n" +
                    "	p.FANTASIA,\n" +
                    "	p.CNPJ,\n" +
                    "	p.IE,\n" +
                    "	p.ATIVO,\n" +
                    "	p.BLOQUEADO,\n" +
                    "	p.DTBLOQUEIO,\n" +
                    "	p.ENDERECO,\n" +
                    "	p.NUMERO,\n" +
                    "	p.COMPLEMENTO,\n" +
                    "	p.BAIRRO,\n" +
                    "	p.CODCIDADE,\n" +
                    "	p.CEP,\n" +
                    "	p.ENDCOBRANCA,\n" +
                    "	p.NUMCOBRANCA,\n" +
                    "	p.COMPCOBRANCA,\n" +
                    "	p.BAIRROCOBRANCA,\n" +
                    "	p.CODCIDADECOBRANCA,\n" +
                    "	p.CEPCOBRANCA,\n" +
                    "	p.TELEFONE tel_principal,\n" +
                    "	p.DTCADASTRO datacadastro,\n" +
                    "	p.PRAZOENTREGA\n" +
                    "from\n" +
                    "	PESSOA p\n" +
                    "where\n" +
                    "	p.ISFORNECEDOR = 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("FANTASIA"));
                    imp.setCnpj_cpf(rs.getString("CNPJ"));
                    imp.setIe_rg(rs.getString("IE"));
                    imp.setAtivo(rs.getBoolean("ATIVO"));
                    imp.setBloqueado(rs.getBoolean("BLOQUEADO"));
                    imp.setEndereco(rs.getString("ENDERECO"));
                    imp.setNumero(rs.getString("NUMERO"));
                    imp.setComplemento(rs.getString("COMPLEMENTO"));
                    imp.setBairro(rs.getString("BAIRRO"));
                    imp.setIbge_municipio(rs.getInt("CODCIDADE"));
                    imp.setCep(rs.getString("CEP"));
                    imp.setCob_endereco(rs.getString("ENDCOBRANCA"));
                    imp.setCob_numero(rs.getString("NUMCOBRANCA"));
                    imp.setCob_complemento(rs.getString("COMPCOBRANCA"));
                    imp.setCob_bairro(rs.getString("BAIRROCOBRANCA"));
                    imp.setCob_ibge_municipio(rs.getInt("CODCIDADECOBRANCA"));
                    imp.setCob_cep(rs.getString("CEPCOBRANCA"));
                    imp.setTel_principal(rs.getString("tel_principal"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setPrazoEntrega(rs.getInt("PRAZOENTREGA"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select DISTINCT\n" +
                    "	pf.CODIGOFORNECEDOR id_fornecedor,\n" +
                    "	pf.CODIGOPRODUTO id_produto,\n" +
                    "	pf.CODIGOPRODFORNECEDOR codigoexterno,\n" +
                    "	pf.FATORCONVERSAO qtdembalagem\n" +
                    "from\n" +
                    "	PRODUTO_FORNECEDOR pf\n" +
                    "order by\n" +
                    "	pf.CODIGOFORNECEDOR,\n" +
                    "	pf.CODIGOPRODUTO"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
