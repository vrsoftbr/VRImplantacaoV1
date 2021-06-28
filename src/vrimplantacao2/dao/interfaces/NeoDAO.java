package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class NeoDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "Neo" + (!"".equals(complemento) ? " - " + complemento : "");
    }

    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select \n" +
                        "	e.EMPCODIGO id,\n" +
                        "	p.PESCNPJCPF,\n" +
                        "	p.PESNOMEUSUAL\n" +
                        "from\n" +
                        "	EMPRESAPESSOA e\n" +
                        "	join PESSOA p on\n" +
                        "		e.EMPCODIGO = p.EMPCODIGO and\n" +
                        "		e.PESCODIGO = p.PESCODIGO\n" +
                        "order by\n" +
                        "	e.EMPCODIGO "
                )
        ) {
            while (rs.next()) {
                result.add(new Estabelecimento(
                        rs.getString("id"),
                        String.format(
                                "%s - %s",
                                rs.getString("PESCNPJCPF"),
                                rs.getString("PESNOMEUSUAL")
                        )
                ));
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select * from (\n" +
                        "	select\n" +
                        "		distinct \n" +
                        "		cst.CSTCODIGO cst,\n" +
                        "		a.ALIQVALOR aliquota,\n" +
                        "		coalesce(p.PRODREDUCAOBCICMS, 0) reducao\n" +
                        "	from\n" +
                        "		ALIQUOTAS a \n" +
                        "		join produto p on\n" +
                        "			a.EMPCODIGO = p.EMPCODIGO and\n" +
                        "			a.ALIQCODIGO = p.ALIQICMS\n" +
                        "		join CST on\n" +
                        "			p.CSTCONTADOR = cst.CSTCONTADOR \n" +
                        "	where\n" +
                        "		a.EMPCODIGO = " + getLojaOrigem() + "\n" +
                        "	union\n" +
                        "	select\n" +
                        "		distinct \n" +
                        "		cst.CSTCODIGO cst,\n" +
                        "		a.ALIQVALOR aliquota,\n" +
                        "		coalesce(p.PRODREDUCAOBCICMS, 0) reducao\n" +
                        "	from\n" +
                        "		ALIQUOTAS a \n" +
                        "		join produto p on\n" +
                        "			a.EMPCODIGO = p.EMPCODIGO and\n" +
                        "			a.ALIQCODIGO = p.ALIQECF\n" +
                        "		join CST on\n" +
                        "			p.CSTCONTADOR = cst.CSTCONTADOR \n" +
                        "	where\n" +
                        "		a.EMPCODIGO = " + getLojaOrigem() + "\n" +
                        ") a\n" +
                        "order by\n" +
                        "	cst, aliquota, reducao"
                )
        ) {
            while (rs.next()) {
                final String formatTributacaoId = formatTributacaoId(rs.getInt("cst"), rs.getDouble("aliquota"), rs.getDouble("reducao"));
                result.add(new MapaTributoIMP(
                        formatTributacaoId,
                        formatTributacaoId,
                        rs.getInt("cst"),
                        rs.getDouble("aliquota"),
                        rs.getDouble("reducao")
                ));
            }
        }
        
        return result;
    }
    
    private String formatTributacaoId(int cst, double aliquota, double reducao) {
        return String.format("%s-%.2f-%.2f", cst, aliquota, reducao);
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	distinct\n" +
                        "	g.GRUPOCONTADOR merc1,\n" +
                        "	g.GRUPODESCRICAO merc1_desc,\n" +
                        "	s.SUBGRUPOCONTADOR merc2,\n" +
                        "	s.SUBGRUPODESCRICAO merc2_desc\n" +
                        "from\n" +
                        "	PRODUTO p\n" +
                        "	join GRUPO g on\n" +
                        "		p.EMPCODIGO = g.EMPCODIGO and\n" +
                        "		p.GRUPOCONTADOR = g.GRUPOCONTADOR \n" +
                        "	join SUBGRUPO s on\n" +
                        "		p.EMPCODIGO = s.EMPCODIGO and\n" +
                        "		p.SUBGRUPOCONTADOR = s.SUBGRUPOCONTADOR\n" +
                        "where\n" +
                        "	p.EMPCODIGO = " + getLojaOrigem() + "\n" +
                        "order by\n" +
                        "	p.GRUPOCONTADOR,\n" +
                        "	p.SUBGRUPOCONTADOR"
                )
        ) {
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
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	f.PRODGRUPCONTADOR id,\n" +
                        "	p.PRODDESCRICAO descricao\n" +
                        "from\n" +
                        "	PRODUTOGRUPO f\n" +
                        "	join PRODUTO p on\n" +
                        "		f.EMPCODIGO = p.EMPCODIGO and\n" +
                        "		f.PRODCONTADOR = p.PRODCONTADOR \n" +
                        "where\n" +
                        "	f.EMPCODIGO = " + getLojaOrigem() + "\n" +
                        "order by\n" +
                        "	1, 2"
                )
        ) {
            ProdutoParaFamiliaHelper helper = new ProdutoParaFamiliaHelper(result);
            
            while (rs.next()) {
                helper.gerarFamilia(rs.getString("id"), rs.getString("descricao"));
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	p.EMPCODIGO,\n" +
                        "	p.PRODCONTADOR id,\n" +
                        "	p.PRODDATACADASTRO datacadastro,\n" +
                        "	p.PRODBARRAS ean,\n" +
                        "	1 qtdembalagem,\n" +
                        "	1 qtdembalagemcotacao,\n" +
                        "	un.UNIDSIMBOLO tipoembalagem,	\n" +
                        "	un.UNIDSIMBOLO tipoembalagemcotacao,\n" +
                        "	coalesce(p.PRODENVIARBALANCA, 0) pesavel,\n" +
                        "	p.PRODDIASVALIDADE validade,\n" +
                        "	p.PRODDESCRICAO descricaocompleta,\n" +
                        "	p.GRUPOCONTADOR cod_mercadologico1,\n" +
                        "	p.SUBGRUPOCONTADOR cod_mercadologico2,\n" +
                        "	fam.PRODGRUPCONTADOR id_familiaproduto,\n" +
                        "	p.PRODPESOBRUTO pesobruto,\n" +
                        "	p.PRODPESOLIQUIDO pesoliquido,\n" +
                        "	p.PRODESTOQUEMAXIMO estoquemaximo,\n" +
                        "	p.PRODESTOQUESEGURANCA estoqueminimo,\n" +
                        "	p.PRODESTOQUE estoque,\n" +
                        "	p.PRODMARGEMVAREJO margem,\n" +
                        "	p.PRODMARGEMATACADO,\n" +
                        "	p.PRODCUSTOFISCAL custocomimpostoanterior,\n" +
                        "	p.PRODCUSTOFISCAL custosemimpostoanterior,\n" +
                        "	p.PRODCUSTOFISCALMEDIA customediocomimposto,\n" +
                        "	p.PRODCUSTOFISCALMEDIA customediosemimposto,\n" +
                        "	p.PRODPRECOCUSTO custocomimposto,\n" +
                        "	p.PRODPRECOCUSTO custosemimposto,\n" +
                        "	p.PRODPRECOVAREJO precovenda,\n" +
                        "	p.PRODATIVO ativo,\n" +
                        "	ncm.NCMDESCRICAO ncm,\n" +
                        "	cest.CESTCODIGO cest,\n" +
                        "	p.CSTPISCODIGO piscofinssaida,\n" +
                        "	p.CSTPISENTRADA piscofinsentrada,\n" +
                        "	nat.NATRECCODRECEITA piscofinsnaturezareceita,\n" +
                        "	p.ALIQECF icmsconsumidor,\n" +
                        "	case aliqc.ALIQTIPOTRIBUTACAO\n" +
                        "		when 0 then 0\n" +
                        "		when 1 then 40\n" +
                        "		when 2 then 40\n" +
                        "		when 3 then 60\n" +
                        "	end icms_cst_consumidor,\n" +
                        "	aliqc.ALIQVALOR icms_aliq_consumidor,\n" +
                        "	0 as icms_reducao_consumidor,\n" +
                        "	cst.CSTCODIGO icms_cst,\n" +
                        "	aliq.ALIQVALOR icms_aliq,\n" +
                        "	p.PRODREDUCAOBCICMS icms_reducao\n" +
                        "from\n" +
                        "	PRODUTO p\n" +
                        "	join UNIDADEMEDIDA un on\n" +
                        "		un.EMPCODIGO = p.EMPCODIGO and\n" +
                        "		un.UNIDCONTADOR = p.UNIDCONTADOR\n" +
                        "	left join PRODUTOGRUPO fam on\n" +
                        "		fam.EMPCODIGO = p.EMPCODIGO and\n" +
                        "		fam.PRODCONTADOR = p.PRODCONTADOR \n" +
                        "	left join NCM on\n" +
                        "		ncm.NCMCODIGO = p.NCMCODIGO\n" +
                        "	left join CEST on\n" +
                        "		cest.CESTCONTADOR = p.CESTCONTADOR \n" +
                        "	left join NATUREZARECEITA nat on\n" +
                        "		nat.NATRECCONTADOR = p.NATRECCONTADOR\n" +
                        "	left join cst on\n" +
                        "		p.CSTCONTADOR = cst.CSTCONTADOR\n" +
                        "	left join ALIQUOTAS aliq on\n" +
                        "		aliq.EMPCODIGO = p.EMPCODIGO and\n" +
                        "		aliq.ALIQCODIGO = p.ALIQICMS \n" +
                        "	left join ALIQUOTAS aliqc on\n" +
                        "		aliqc.EMPCODIGO = p.EMPCODIGO and\n" +
                        "		aliqc.ALIQCODIGO = p.ALIQECF\n" +
                        "where\n" +
                        "	p.EMPCODIGO = " + getLojaOrigem() + "\n" +
                        "order by id"
                )
        ) {            
            while (rs.next()) {
                ProdutoIMP imp = new ProdutoIMP();
                
                /*imp.setSituacaoCadastro(rs.getInt("ativo"));
                if (SituacaoCadastro.EXCLUIDO.equals(imp.getSituacaoCadastro())) {
                    continue;
                }*/
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setDataCadastro(rs.getDate("datacadastro"));
                imp.setEan(rs.getString("ean"));
                imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                imp.setTipoEmbalagemCotacao(rs.getString("tipoembalagemcotacao"));
                imp.seteBalanca(rs.getBoolean("pesavel"));
                imp.setValidade(rs.getInt("validade"));
                imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                imp.setCodMercadologico1(rs.getString("cod_mercadologico1"));
                imp.setCodMercadologico2(rs.getString("cod_mercadologico2"));
                imp.setIdFamiliaProduto(rs.getString("id_familiaproduto"));
                imp.setPesoBruto(rs.getDouble("pesobruto"));
                imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                imp.setEstoque(rs.getDouble("estoque"));
                imp.setMargem(rs.getDouble("margem"));
                imp.setCustoAnteriorComImposto(rs.getDouble("custocomimpostoanterior"));
                imp.setCustoAnteriorSemImposto(rs.getDouble("custosemimpostoanterior"));
                imp.setCustoMedioComImposto(rs.getDouble("customediocomimposto"));
                imp.setCustoMedioSemImposto(rs.getDouble("customediosemimposto"));
                imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                imp.setPrecovenda(rs.getDouble("precovenda"));                
                imp.setSituacaoCadastro(rs.getInt("ativo"));
                imp.setNcm(rs.getString("ncm"));
                imp.setCest(rs.getString("cest"));
                imp.setPiscofinsCstDebito(rs.getString("piscofinssaida"));
                imp.setPiscofinsCstCredito(rs.getString("piscofinsentrada"));
                imp.setPiscofinsNaturezaReceita(rs.getString("piscofinsnaturezareceita"));
                final String icmsKey = formatTributacaoId(rs.getInt("icms_cst"), rs.getDouble("icms_aliq"), rs.getDouble("icms_reducao"));
                imp.setIcmsConsumidorId(icmsKey);
                imp.setIcmsDebitoId(icmsKey);
                imp.setIcmsDebitoForaEstadoId(icmsKey);
                imp.setIcmsDebitoForaEstadoNfId(icmsKey);
                imp.setIcmsCreditoId(icmsKey);
                imp.setIcmsCreditoForaEstadoId(icmsKey);
                
                result.add(imp);
            }
        }
        
        return result;
    }    

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.CUSTO_ANTERIOR,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.BLOQUEADO,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.NUMERO,
                OpcaoFornecedor.COMPLEMENTO,
                OpcaoFornecedor.BAIRRO,
                OpcaoFornecedor.MUNICIPIO,
                OpcaoFornecedor.UF,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.CELULAR,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.OBSERVACAO,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.CONTATO_NOME,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.ENDERECO_COMPLETO_COBRANCA,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        Map<String, List<FornecedorContatoIMP>> contatos = getContatoFornecedor();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	f.FORCODIGO id,\n" +
                        "	f.FORCRT tipoempresa,\n" +
                        "	p.PESNOME razao,\n" +
                        "	coalesce(p.PESNOMEUSUAL, p.PESNOME) fantasia,\n" +
                        "	p.PESCNPJCPF cnpjcpf,\n" +
                        "	p.PESie rg,\n" +
                        "	p.PESINSCSUFRAMA suframa,\n" +
                        "	f.FORATIVO ativo,\n" +
                        "	p.PESATIVO bloqueado,\n" +
                        "	ender.TIPOLOGRLOGRADOURO tipologradouro,\n" +
                        "	ender.logrlogradouro logradouro,\n" +
                        "	ender.numero,\n" +
                        "	ender.complemento,\n" +
                        "	ender.bairro,\n" +
                        "	ender.municipio_ibge,\n" +
                        "	ender.cep,\n" +
                        "	p.PESTELEFONE telefone,\n" +
                        "	p.PESCELULAR celular,\n" +
                        "	p.PESCADASTRO datacadastro,\n" +
                        "	p.PESOBS observacao,\n" +
                        "	f.FORCRT,\n" +
                        "	f.FORSALDO \n" +
                        "from\n" +
                        "	FORNECEDOR f \n" +
                        "	join PESSOA p on\n" +
                        "		f.EMPCODIGO = p.EMPCODIGO and\n" +
                        "		f.PESCODIGO = p.PESCODIGO\n" +
                        "	left join (\n" +
                        "		select\n" +
                        "			distinct\n" +
                        "			ender.EMPCODIGO,\n" +
                        "			ender.PESCODIGO,\n" +
                        "			tp.TIPOLOGRLOGRADOURO,\n" +
                        "			lg.LOGRLOGRADOURO,\n" +
                        "			ender.PESENDNUMERO numero,\n" +
                        "			nullif(trim(ender.PESENDCOMPLEMENTO),'') complemento,\n" +
                        "			bai.BAIBAIRRO bairro,\n" +
                        "			ender.MUNCODIGO municipio_ibge,\n" +
                        "			ender.PESENDCEP cep\n" +
                        "		from\n" +
                        "			PESSOAENDERECO ender\n" +
                        "			join LOGRADOURO lg on\n" +
                        "				lg.EMPCODIGO = ender.EMPCODIGO and\n" +
                        "				lg.LOGRCODIGO = ender.LOGRCODIGO\n" +
                        "			join TIPOLOGRADOURO tp on\n" +
                        "				lg.TIPOLOGRCODIGO = tp.TIPOLOGRCODIGO\n" +
                        "			join BAIRRO bai on\n" +
                        "				bai.EMPCODIGO = ender.EMPCODIGO and\n" +
                        "				bai.BAICODIGO = ender.BAICODIGO\n" +
                        "		where\n" +
                        "			ender.EMPCODIGO = " + getLojaOrigem() + " and\n" +
                        "			ender.PESTIPOENDERECO in (0,1)\n" +
                        "	) ender on\n" +
                        "		ender.EMPCODIGO = p.EMPCODIGO and\n" +
                        "		ender.PESCODIGO = p.PESCODIGO\n" +
                        "where f.empcodigo = " + getLojaOrigem()
                );
        ) {
            while (rs.next()) {
                FornecedorIMP imp = new FornecedorIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setCnpj_cpf(rs.getString("cnpjcpf"));
                imp.setIe_rg(rs.getString("rg"));
                imp.setSuframa(rs.getString("suframa"));
                imp.setAtivo(rs.getBoolean("ativo"));
                imp.setBloqueado(rs.getBoolean("bloqueado"));
                
                imp.setEndereco(rs.getString("tipologradouro") + " " + rs.getString("logradouro"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setIbge_municipio(rs.getInt("municipio_ibge"));
                imp.setCep(rs.getString("cep"));
                
                imp.setCob_endereco(rs.getString("tipologradouro") + " " + rs.getString("logradouro"));
                imp.setCob_numero(rs.getString("numero"));
                imp.setCob_complemento(rs.getString("complemento"));
                imp.setCob_bairro(rs.getString("bairro"));
                imp.setCob_ibge_municipio(rs.getInt("municipio_ibge"));
                imp.setCob_cep(rs.getString("cep"));
                
                imp.setTel_principal(rs.getString("telefone"));
                imp.addCelular("CELULAR", rs.getString("celular"));
                imp.setDatacadastro(rs.getDate("datacadastro"));
                imp.setObservacao(rs.getString("observacao"));
                switch (rs.getInt("tipoempresa")) {
                    case 1:
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                        break;
                    default:
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                        break;
                }
                
                List<FornecedorContatoIMP> cont = contatos.get(imp.getImportId());
                if (cont != null) {
                    for (FornecedorContatoIMP ct: cont) {
                        imp.getContatos().put(ct, imp.getImportId(), ct.getImportId());
                    }
                }
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
    private Map<String, List<FornecedorContatoIMP>> getContatoFornecedor() throws Exception {
        Map<String, List<FornecedorContatoIMP>> result = new HashMap<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	p.PESCODIGO || '-' || p.PESCONTCODIGO id,\n" +
                        "	p.PESCODIGO id_fornecedor,\n" +
                        "	coalesce(p.PESCONTNOMECONTATO, t.TIPOCONTCONTATO) nome,\n" +
                        "	null as telefone, \n" +
                        "	p.PESCONTCONTATO as email\n" +
                        "from\n" +
                        "	PESSOACONTATO p\n" +
                        "	left join TIPOCONTATO t on\n" +
                        "		p.TIPOCONTCODIGO = t.TIPOCONTCODIGO\n" +
                        "where \n" +
                        "	p.EMPCODIGO = " + getLojaOrigem() + " and\n" +
                        "	p.TIPOCONTCODIGO in (6)\n" +
                        "union\n" +
                        "select\n" +
                        "	p.PESCODIGO || '-' || p.PESCONTCODIGO id,\n" +
                        "	p.PESCODIGO id_fornecedor,\n" +
                        "	coalesce(p.PESCONTNOMECONTATO, t.TIPOCONTCONTATO) nome,\n" +
                        "	p.PESCONTCONTATO telefone, \n" +
                        "	null as email\n" +
                        "from\n" +
                        "	PESSOACONTATO p\n" +
                        "	left join TIPOCONTATO t on\n" +
                        "		p.TIPOCONTCODIGO = t.TIPOCONTCODIGO\n" +
                        "where \n" +
                        "	p.EMPCODIGO = " + getLojaOrigem() + " and\n" +
                        "	p.TIPOCONTCODIGO in (1,3,4,7)"
                )
        ) {
            while (rs.next()) {
                List<FornecedorContatoIMP> get = result.get(rs.getString("id_fornecedor"));
                if (get == null) {
                    get = new ArrayList<>();
                    result.put(rs.getString("id_fornecedor"), get);
                }
                
                FornecedorContatoIMP imp = new FornecedorContatoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setImportFornecedorId(rs.getString("id_fornecedor"));
                imp.setNome(rs.getString("nome"));
                imp.setTelefone(rs.getString("telefone"));
                imp.setEmail(rs.getString("email"));
                
                get.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (
            Statement st = ConexaoFirebird.getConexao().createStatement();
            ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	f.FORCODIGO id_fornecedor,\n" +
                    "	f.PRODCONTADOR id_produto,\n" +
                    "	f.FORPRODCODPRODUTO codigoexterno,\n" +
                    "	case \n" +
                    "		when f.FORPRODQTDE = 0 then 1\n" +
                    "		else f.FORPRODQTDE \n" +
                    "	end qtdembalagem\n" +
                    "from\n" +
                    "	FORNECEDORPRODUTO f\n" +
                    "where\n" +
                    "	EMPCODIGO = " + getLojaOrigem()
            );
        ) {
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
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        Map<String, List<ClienteContatoIMP>> contatosCliente = getContatoCliente();
        
        try (
            Statement st = ConexaoFirebird.getConexao().createStatement();
            ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	c.CLICODIGO id,\n" +
                    "	p.PESCNPJCPF cnpjcpf,\n" +
                    "	p.PESie rg,\n" +
                    "	p.PESNOME razao,\n" +
                    "	coalesce(p.PESNOMEUSUAL, p.PESNOME) fantasia,\n" +
                    "	p.PESATIVO ativo,\n" +
                    "	p.PESINSCSUFRAMA suframa,\n" +
                    "	p.PESATIVO bloqueado,\n" +
                    "	ender.TIPOLOGRLOGRADOURO tipologradouro,\n" +
                    "	ender.logrlogradouro logradouro,\n" +
                    "	ender.numero,\n" +
                    "	ender.complemento,\n" +
                    "	ender.bairro,\n" +
                    "	ender.municipio_ibge,\n" +
                    "	ender.cep,\n" +
                    "   case coalesce(pf.PESFESTADOCIVIL, 0)\n" +
                    "		when 0 then 'NAO INFORMADO'\n" +
                    "		when 3 then 'SOLTEIRO'\n" +
                    "		when 4 then 'CASADO'\n" +
                    "		when 5 then 'VIUVO'\n" +
                    "		when 6 then 'DIVORCIADO'\n" +
                    "		when 8 then 'OUTROS'\n" +
                    "		when 9 then 'UNIAO ESTAVEL'\n" +
                    "		when 10 then 'SEPARADO'\n" +
                    "	end estadocivil,\n" +
                    "	pf.PESFNASCIMENTO datanascimento,\n" +
                    "	p.PESCADASTRO datacadastro,\n" +
                    "	case pf.PESFSEXO\n" +
                    "		when 0 then 'M'\n" +
                    "		else 'F'\n" +
                    "	end sexo,\n" +
                    "	clif.CLIPFTRABALHO empresa,\n" +
                    "	clif.CLIPFADMISSAO dataadmissao,\n" +
                    "	clif.CLIPFCARGO cargo,\n" +
                    "	clif.CLIPFRENDA salario,	\n" +
                    "	c.CLILIMITECREDITO valorlimite,\n" +
                    "	pf.PESFCONJUGE conjuge,\n" +
                    "	pf.PESFCPFCONJUGE cpfconjuge,\n" +
                    "	pf.PESFPAI pai,\n" +
                    "	pf.PESFMAE mae,\n" +
                    "	p.PESOBS observacao,\n" +
                    "	c.CLIDIAVENCIMENTO diavencimento,\n" +
                    "	c.CLISITUACAO situacao,\n" +
                    "	c.CLISENHA senha,\n" +
                    "	p.PESTELEFONE telefone,\n" +
                    "	p.PESCELULAR celular,\n" +
                    "	p.PESEMAIL email\n" +
                    "from\n" +
                    "	CLIENTE c \n" +
                    "	join PESSOA p on\n" +
                    "		c.EMPCODIGO = p.EMPCODIGO and\n" +
                    "		c.PESCODIGO = p.PESCODIGO\n" +
                    "	left join (\n" +
                    "		select\n" +
                    "			distinct\n" +
                    "			ender.EMPCODIGO,\n" +
                    "			ender.PESCODIGO,\n" +
                    "			tp.TIPOLOGRLOGRADOURO,\n" +
                    "			lg.LOGRLOGRADOURO,\n" +
                    "			ender.PESENDNUMERO numero,\n" +
                    "			nullif(trim(ender.PESENDCOMPLEMENTO),'') complemento,\n" +
                    "			bai.BAIBAIRRO bairro,\n" +
                    "			ender.MUNCODIGO municipio_ibge,\n" +
                    "			ender.PESENDCEP cep\n" +
                    "		from\n" +
                    "			PESSOAENDERECO ender\n" +
                    "			join LOGRADOURO lg on\n" +
                    "				lg.EMPCODIGO = ender.EMPCODIGO and\n" +
                    "				lg.LOGRCODIGO = ender.LOGRCODIGO\n" +
                    "			join TIPOLOGRADOURO tp on\n" +
                    "				lg.TIPOLOGRCODIGO = tp.TIPOLOGRCODIGO\n" +
                    "			join BAIRRO bai on\n" +
                    "				bai.EMPCODIGO = ender.EMPCODIGO and\n" +
                    "				bai.BAICODIGO = ender.BAICODIGO\n" +
                    "		where\n" +
                    "			ender.EMPCODIGO = " + getLojaOrigem() + " and\n" +
                    "			ender.PESTIPOENDERECO in (0,1)\n" +
                    "	) ender on\n" +
                    "		ender.EMPCODIGO = p.EMPCODIGO and\n" +
                    "		ender.PESCODIGO = p.PESCODIGO\n" +
                    "	left join CLIENTEPESSOAFISICA clif on\n" +
                    "		clif.EMPCODIGO = c.EMPCODIGO and\n" +
                    "		clif.CLICODIGO = c.CLICODIGO\n" +
                    "	left join PESSOAFISICA pf on\n" +
                    "		p.EMPCODIGO = pf.EMPCODIGO and\n" +
                    "		p.PESCODIGO = pf.PESCODIGO\n" +
                    "where \n" +
                    "	c.empcodigo = " + getLojaOrigem()
            );
        ) {
            while (rs.next()) {
                ClienteIMP imp = new ClienteIMP();
                
                imp.setId(rs.getString("id"));
                imp.setCnpj(rs.getString("cnpjcpf"));
                imp.setInscricaoestadual(rs.getString("rg"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setAtivo(rs.getBoolean("ativo"));                
                imp.setEndereco(rs.getString("tipologradouro") + " " + rs.getString("logradouro"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setMunicipioIBGE(rs.getString("municipio_ibge"));
                imp.setCep(rs.getString("cep"));
                imp.setEstadoCivil(rs.getString("estadocivil"));
                imp.setDataNascimento(rs.getDate("datanascimento"));
                imp.setDataCadastro(rs.getDate("datacadastro"));
                imp.setSexo("M".equals(rs.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                imp.setEmpresa(rs.getString("empresa"));
                imp.setDataAdmissao(rs.getDate("dataadmissao"));
                imp.setCargo(rs.getString("cargo"));
                imp.setSalario(rs.getDouble("salario"));
                imp.setValorLimite(rs.getDouble("valorlimite"));
                imp.setNomeConjuge(rs.getString("conjuge"));
                imp.setCpfConjuge(rs.getString("cpfconjuge"));
                imp.setNomePai(rs.getString("pai"));
                imp.setNomeMae(rs.getString("mae"));
                imp.setObservacao(rs.getString("observacao"));
                imp.setDiaVencimento(rs.getInt("diavencimento"));
                imp.setTelefone(rs.getString("telefone"));
                imp.setCelular(rs.getString("celular"));
                imp.setEmail(rs.getString("email"));
                
                List<ClienteContatoIMP> cont = contatosCliente.get(imp.getId());
                if (cont != null) {
                    for (ClienteContatoIMP ct: cont) {
                        ct.setCliente(imp);
                        imp.getContatos().add(ct);
                    }
                }
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
    private Map<String, List<ClienteContatoIMP>> getContatoCliente() throws Exception {
        Map<String, List<ClienteContatoIMP>> result = new HashMap<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	p.PESCODIGO || '-' || p.PESCONTCODIGO id,\n" +
                        "	p.PESCODIGO id_cliente,\n" +
                        "	coalesce(p.PESCONTNOMECONTATO, t.TIPOCONTCONTATO) nome,\n" +
                        "	null as telefone, \n" +
                        "	p.PESCONTCONTATO as email\n" +
                        "from\n" +
                        "	PESSOACONTATO p\n" +
                        "	left join TIPOCONTATO t on\n" +
                        "		p.TIPOCONTCODIGO = t.TIPOCONTCODIGO\n" +
                        "where \n" +
                        "	p.EMPCODIGO = " + getLojaOrigem() + " and\n" +
                        "	p.TIPOCONTCODIGO in (6)\n" +
                        "union\n" +
                        "select\n" +
                        "	p.PESCODIGO || '-' || p.PESCONTCODIGO id,\n" +
                        "	p.PESCODIGO id_cliente,\n" +
                        "	coalesce(p.PESCONTNOMECONTATO, t.TIPOCONTCONTATO) nome,\n" +
                        "	p.PESCONTCONTATO telefone, \n" +
                        "	null as email\n" +
                        "from\n" +
                        "	PESSOACONTATO p\n" +
                        "	left join TIPOCONTATO t on\n" +
                        "		p.TIPOCONTCODIGO = t.TIPOCONTCODIGO\n" +
                        "where \n" +
                        "	p.EMPCODIGO = " + getLojaOrigem() + " and\n" +
                        "	p.TIPOCONTCODIGO in (1,3,4,7)"
                )
        ) {
            while (rs.next()) {
                List<ClienteContatoIMP> get = result.get(rs.getString("id_cliente"));
                if (get == null) {
                    get = new ArrayList<>();
                    result.put(rs.getString("id_cliente"), get);
                }
                
                ClienteContatoIMP imp = new ClienteContatoIMP();
                
                imp.setId(rs.getString("id"));
                imp.setNome(rs.getString("nome"));
                imp.setTelefone(rs.getString("telefone"));
                imp.setEmail(rs.getString("email"));
                
                get.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	cr.EMPCODIGO || '-' || cr.CRNUMERO id,\n" +
                        "	pc.PCCODCADASTRO idCliente,\n" +
                        "	cr.CRDTEMISSAO dataemissao,\n" +
                        "	cr.CRDOCUMENTO numerocupom,\n" +
                        "	cr.CRVALOR valor,\n" +
                        "	cr.CROBSERVACAO observacao,\n" +
                        "	cr.CRDTVENCTO vencimento,\n" +
                        "	cr.CRVALORRECEBIDO valorpago\n" +
                        "from\n" +
                        "	CONTASRECEBER cr\n" +
                        "	join PLANOCONTAS pc on\n" +
                        "		cr.EMPCODIGO = pc.EMPCODIGO and\n" +
                        "		cr.PCCODIGO = pc.PCCODIGO\n" +
                        "where\n" +
                        "	cr.EMPCODIGO = " + getLojaOrigem() + " and\n" +
                        "	pc.PCCODIGOPAI = 11 and\n" +
                        "	cr.CRDTRECEBIMENTO is null and\n" +
                        "	cr.CRDATACANCELAMENTO is null"
                );
        ) {
            while (rs.next()) {
                CreditoRotativoIMP imp = new CreditoRotativoIMP();
                
                imp.setId(rs.getString("id"));
                imp.setIdCliente(rs.getString("idCliente"));
                imp.setDataEmissao(rs.getDate("dataemissao"));
                imp.setNumeroCupom(rs.getString("numerocupom"));
                imp.setValor(rs.getDouble("valor"));
                imp.setObservacao(rs.getString("observacao"));
                imp.setDataVencimento(rs.getDate("vencimento"));
                double pag = rs.getDouble("valorpago");
                if (pag > 0) {
                    imp.addPagamento(rs.getString("id"), pag, 0, 0, rs.getDate("vencimento"), "");
                }
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.RAZAO,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.CEP,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.SEXO,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.DATA_ADMISSAO,
                OpcaoCliente.CARGO,
                OpcaoCliente.SALARIO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.NOME_CONJUGE,
                OpcaoCliente.NOME_PAI,
                OpcaoCliente.NOME_MAE,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.DADOS,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }
    
}
