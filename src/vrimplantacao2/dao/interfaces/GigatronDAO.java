package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vr.core.utils.StringUtils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class GigatronDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "Gigatron" + ("".equals(complemento) ? "" : " - " + complemento);
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	COD_EMPRESA id,\n" +
                        "	NOME_FANTASIA descricao\n" +
                        "from\n" +
                        "	EMPRESAS e\n" +
                        "order by\n" +
                        "	id"
                )
        ) {
            while (rs.next()) {
                result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.PRODUTOS_BALANCA,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.CUSTO_ANTERIOR,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_CONSUMIDOR
        ));
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	IPV.COD_IMPOSTO as id,\n" +
                        "	imp.desc_imposto as decricao,\n" +
                        "	IPV.ICMS_CST as icms_cst,\n" +
                        "	IIF(\n" +
                        "		IMP.FLAG_TRIBUTACAO = 0,\n" +
                        "		IIF(IPV.ECF_CST = 'T', IPU.ICMS_ALIQ_INTERNA, 0),\n" +
                        "		IIF(IPV.ECF_CST = 'T', IPV.ISSQN_ALIQ, 0)\n" +
                        "	) as icms_aliquota\n" +
                        "from\n" +
                        "	IMPOSTOS_VARIACOES IPV\n" +
                        "	join IMPOSTOS IMP on\n" +
                        "		IMP.COD_IMPOSTO = IPV.COD_IMPOSTO\n" +
                        "		and IMP.FLAG_MODELO_IMPOSTO = 1\n" +
                        "	join EMPRESAS EMP on\n" +
                        "		EMP.COD_EMPRESA = IPV.COD_EMPRESA\n" +
                        "	join MUNICIPIOS MUN on\n" +
                        "		MUN.COD_MUNIC = EMP.COD_MUNIC\n" +
                        "	left join IMPOSTOS_VARIACOES_UF IPU on\n" +
                        "		IPU.COD_IMPOSTO = IPV.COD_IMPOSTO\n" +
                        "		and IPU.SEQ_VARIACAO = IPV.SEQ_VARIACAO\n" +
                        "		and IPU.UF = MUN.UF\n" +
                        "where\n" +
                        "	ipv.cod_empresa = " + getLojaOrigem()
                )
        ) {
            while (rs.next()) {
                result.add(new MapaTributoIMP(
                        rs.getString("id"),
                        rs.getString("decricao"),
                        rs.getInt("icms_cst"),
                        rs.getDouble("icms_aliquota"),
                        0
                ));
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
                        "	p.COD_PRODUTO as id,\n" +
                        "	p.DATA_CADASTRO as datacadastro,\n" +
                        "	p.DATA_ALTERACAO as dataalteracao,\n" +
                        "	ean.APELIDO as ean,\n" +
                        "	1 as qtdembalagem,\n" +
                        "	p.QTD_VOLUME as qtdvolume,\n" +
                        "	un.UNIDADE,\n" +
                        "	grade.COD_BALANCA,\n" +
                        "	grade.DIAS_VALIDADE as validade,\n" +
                        "	p.DESC_PRODUTO as descricao,\n" +
                        "	case when grade.COD_GRADE = '*' then null else grade.COD_GRADE end as complementodescricao,\n" +
                        "	p.DESC_PRODUTO_FISCAL as descricaoreduzida,\n" +
                        "	p.COD_GRUPO as mercadologico1,\n" +
                        "	p.COD_PRODUTO as id_familia,\n" +
                        "	p.PESO_BRUTO,\n" +
                        "	p.PESO_LIQUIDO,\n" +
                        "	grade.ESTOQUE_MIN,\n" +
                        "	grade.ESTOQUE_MAX,\n" +
                        "	est.ESTOQUE,\n" +
                        "	p.PERC_MARGEM_LUCRO margem,\n" +
                        "	custo.PRECO_CUSTO custocomimposto,\n" +
                        "	custo.PRECO_CUSTO_REAL custosemimposto,\n" +
                        "	preco.PRECO as precovenda,\n" +
                        "	p.FLAG_ATIVO as ativo,\n" +
                        "	p.COD_CLASS as ncm,\n" +
                        "	p.COD_CEST as cest,\n" +
                        "	pis.PIS_CST as piscofins_cst,\n" +
                        "	p.COD_NAT_REC as piscofins_natrec,\n" +
                        "	o.COD_IMPOSTO,\n" +
                        "	o.DESC_IMPOSTO,\n" +
                        "	pis.ICMS_CST,\n" +
                        "	CUSTO.icms_aliq,\n" +
                        "	custo.ICMS_RED_BASE\n" +
                        "from\n" +
                        "	PRODUTOS p\n" +
                        "	join EMPRESAS e on\n" +
                        "		e.COD_EMPRESA = " + getLojaOrigem() + "\n" +
                        "    JOIN PRODUTOS_GRADE grade ON\n" +
                        "        grade.COD_PRODUTO = P.COD_PRODUTO\n" +
                        "	JOIN PRODUTOS_APELIDOS_PROD ean ON \n" +
                        "	    ean.COD_PRODUTO = grade.COD_PRODUTO AND\n" +
                        "	    ean.COD_GRADE = grade.COD_GRADE AND\n" +
                        "	    ean.COD_APELIDO = 2\n" +
                        "    LEFT JOIN PRODUTOS_TAB_PRECOS_PROD preco on\n" +
                        "    	preco.COD_EMPRESA = e.COD_EMPRESA and\n" +
                        "        preco.COD_PRODUTO = grade.COD_PRODUTO AND\n" +
                        "        preco.COD_GRADE = grade.COD_GRADE AND\n" +
                        "        preco.COD_TABELA = 2\n" +
                        "    left join PRODUTOS_GRADE_UND un on\n" +
                        "    	un.COD_PRODUTO = grade.COD_PRODUTO and\n" +
                        "    	un.COD_GRADE = grade.COD_GRADE and\n" +
                        "    	un.FLAG_PADRAO = 1\n" +
                        "    join PRODUTOS_SALDOS est on\n" +
                        "    	est.COD_EMPRESA = e.COD_EMPRESA and\n" +
                        "    	est.COD_PRODUTO = grade.COD_PRODUTO and\n" +
                        "    	est.COD_GRADE = grade.COD_GRADE\n" +
                        "    join PRODUTOS_CUSTO custo on\n" +
                        "    	custo.COD_EMPRESA = e.COD_EMPRESA and\n" +
                        "    	custo.COD_PRODUTO = grade.COD_PRODUTO and\n" +
                        "    	custo.COD_GRADE = grade.COD_GRADE\n" +
                        "    join IMPOSTOS_VARIACOES pis on\n" +
                        "    	pis.COD_EMPRESA = e.COD_EMPRESA and\n" +
                        "    	pis.COD_IMPOSTO = p.COD_IMPOSTO and\n" +
                        "    	pis.SEQ_VARIACAO = 1\n" +
                        "    join impostos o on\n" +
                        "    	p.COD_IMPOSTO = o.cod_imposto\n" +
                        "order by\n" +
                        "	p.COD_PRODUTO"
                )
        ) {
            Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
            while (rs.next()) {
                ProdutoIMP imp = new ProdutoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setDataCadastro(rs.getDate("datacadastro"));
                imp.setDataAlteracao(rs.getDate("dataalteracao"));
                
                preencherEanUnidadeQtdEmbalagem(rs, imp, produtosBalanca);
                
                StringBuilder descricao = new StringBuilder(rs.getString("descricao"));
                if (rs.getString("complementodescricao") != null) {
                    descricao.append(" ").append(rs.getString("complementodescricao"));
                }
                imp.setVolume(rs.getDouble("qtdvolume"));
                imp.setDescricaoCompleta(descricao.toString());
                imp.setDescricaoGondola(imp.getDescricaoCompleta());
                imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                imp.setCodMercadologico1(rs.getString("mercadologico1"));
                imp.setIdFamiliaProduto(rs.getString("id_familia"));
                imp.setPesoBruto(rs.getDouble("PESO_BRUTO"));
                imp.setPesoLiquido(rs.getDouble("PESO_LIQUIDO"));
                imp.setEstoqueMinimo(rs.getDouble("ESTOQUE_MIN"));
                imp.setEstoqueMaximo(rs.getDouble("ESTOQUE_MAX"));
                imp.setEstoque(rs.getDouble("ESTOQUE"));
                imp.setMargem(rs.getDouble("margem"));
                imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                imp.setCustoMedioComImposto(rs.getDouble("custocomimposto"));
                imp.setCustoAnteriorComImposto(rs.getDouble("custocomimposto"));                
                imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                imp.setCustoMedioSemImposto(rs.getDouble("custosemimposto"));
                imp.setCustoAnteriorSemImposto(rs.getDouble("custosemimposto"));
                imp.setPrecovenda(rs.getDouble("precovenda"));
                imp.setSituacaoCadastro(rs.getInt("ativo"));
                imp.setNcm(rs.getString("ncm"));
                imp.setCest(rs.getString("cest"));
                imp.setPiscofinsCstDebito(rs.getString("piscofins_cst"));
                imp.setPiscofinsNaturezaReceita(rs.getString("piscofins_natrec"));
                imp.setIcmsDebitoId(rs.getString("COD_IMPOSTO"));
                imp.setIcmsDebitoForaEstadoId(rs.getString("COD_IMPOSTO"));
                imp.setIcmsDebitoForaEstadoNfId(rs.getString("COD_IMPOSTO"));
                imp.setIcmsConsumidorId(rs.getString("COD_IMPOSTO"));
                imp.setIcmsCreditoId(rs.getString("COD_IMPOSTO"));
                imp.setIcmsCreditoForaEstadoId(rs.getString("COD_IMPOSTO"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    private void preencherEanUnidadeQtdEmbalagem(final ResultSet rs, ProdutoIMP imp, Map<Integer, ProdutoBalancaVO> produtosBalanca) throws SQLException {
        String unidade = StringUtils.acertarTexto(rs.getString("UNIDADE"),2);
        final boolean temPlu = rs.getString("COD_BALANCA") != null;
        final boolean isProdutoDeKilo = "KG".equals(unidade);
        int plu = rs.getInt("COD_BALANCA");
        String ean;
        if (temPlu && isProdutoDeKilo)
            ean = rs.getString("COD_BALANCA");
        else
            ean = rs.getString("ean");
        
        if (!imp.from(produtosBalanca.get(StringUtils.toInt(ean, -2)))) {
            imp.setEan(ean);
            imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
            imp.setTipoEmbalagem(unidade);
            imp.setValidade(rs.getInt("validade"));
        }
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	COD_GRUPO merc1,\n" +
                        "	DESC_GRUPO merc1desc\n" +
                        "from\n" +
                        "	GRUPOS_PRODUTO gp\n" +
                        "order by\n" +
                        "	cod_grupo"
                )
        ) {
            while (rs.next()) {
                MercadologicoIMP imp = new MercadologicoIMP();
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setMerc1ID(rs.getString("merc1"));
                imp.setMerc1Descricao(rs.getString("merc1desc"));
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
                        "with familias as (\n" +
                        "	select\n" +
                        "		distinct cod_produto\n" +
                        "	from\n" +
                        "		produtos_grade pg\n" +
                        "	where\n" +
                        "		cod_grade != '*'\n" +
                        ")\n" +
                        "select\n" +
                        "	cod_produto id,\n" +
                        "	desc_produto descricao\n" +
                        "from\n" +
                        "	produtos\n" +
                        "where\n" +
                        "	cod_produto in (\n" +
                        "		select\n" +
                        "			cod_produto\n" +
                        "		from\n" +
                        "			familias\n" +
                        ")"
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
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	f.cod_fornecedor id,\n" +
                        "	p.razao_social razao,\n" +
                        "	p.nome_fantasia fantasia,\n" +
                        "	p.cnpj_cpf cnpj,\n" +
                        "	coalesce(p.inscr_estadual, p.rg) ie,\n" +
                        "	p.inscr_municipal,\n" +
                        "	p.suframa,\n" +
                        "	f.flag_ativo ativo,\n" +
                        "	f.obs_inativo,\n" +
                        "	p.obs_pessoa,\n" +
                        "	p.data_cadastro,\n" +
                        "	p.data_alteracao,\n" +
                        "	pe.endereco,\n" +
                        "	pe.numero,\n" +
                        "	pe.complemento,\n" +
                        "	pe.bairro,\n" +
                        "	m.cod_munic id_cidade,\n" +
                        "	m.nome_munic cidade,\n" +
                        "	m.uf,\n" +
                        "	pe.cep,\n" +
                        "	pe.celular,\n" +
                        "	pe.telefone,\n" +
                        "	pe.fax \n" +
                        "from\n" +
                        "	fornecedores f\n" +
                        "	join pessoas p on\n" +
                        "		p.cod_pessoa = f.cod_fornecedor\n" +
                        "	left join pessoas_enderecos pe on\n" +
                        "		pe.cod_pessoa = p.cod_pessoa and\n" +
                        "		pe.seq_pes_end = 1\n" +
                        "	left join municipios m on\n" +
                        "		pe.cod_munic = m.cod_munic \n" +
                        "order by\n" +
                        "	f.cod_fornecedor"
                )
        ) {
            while (rs.next()) {
                FornecedorIMP imp = new FornecedorIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setCnpj_cpf(rs.getString("cnpj"));
                imp.setIe_rg(rs.getString("ie"));
                imp.setInsc_municipal(rs.getString("inscr_municipal"));
                imp.setSuframa(rs.getString("suframa"));
                imp.setAtivo(rs.getBoolean("ativo"));
                imp.setObservacao(rs.getString("obs_pessoa"));
                imp.setDatacadastro(rs.getDate("data_cadastro"));
                imp.setEndereco(rs.getString("endereco"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setIbge_municipio(rs.getInt("id_cidade"));
                imp.setCep(rs.getString("cep"));
                imp.setTel_principal(rs.getString("telefone"));
                imp.addCelular("CELULAR", rs.getString("celular"));
                imp.addTelefone("FAX", rs.getString("fax"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	c.cod_cliente id,\n" +
                        "	p.cnpj_cpf cnpj,\n" +
                        "	coalesce(p.inscr_estadual, p.rg) ie,\n" +
                        "	p.razao_social razao,\n" +
                        "	p.nome_fantasia fantasia,\n" +
                        "	c.flag_ativo ativo,\n" +
                        "	c.bloqueado,\n" +
                        "	pe.endereco,\n" +
                        "	pe.numero,\n" +
                        "	pe.complemento,\n" +
                        "	pe.bairro,\n" +
                        "	m.cod_munic id_cidade,\n" +
                        "	m.nome_munic cidade,\n" +
                        "	m.uf,\n" +
                        "	pe.cep,\n" +
                        "	c.conj_estado_civil estadocivil,\n" +
                        "	c.data_nascimento,\n" +
                        "	p.data_cadastro,\n" +
                        "	case c.sexo when 1 then 'F' else 'M' end sexo,\n" +
                        "	c.trab_renda salario,\n" +
                        "	c.trab_cargo cargo,\n" +
                        "	c.trab_local empresa,\n" +
                        "	c.limite_credito limite,\n" +
                        "	c.conj_nome,\n" +
                        "	c.conj_cpf,\n" +
                        "	c.obs_inativo,\n" +
                        "	p.obs_pessoa,\n" +
                        "	c.dia_vencimento,\n" +
                        "	pe.telefone,\n" +
                        "	pe.celular,\n" +
                        "	p.email\n" +
                        "from\n" +
                        "	clientes c\n" +
                        "	join pessoas p on\n" +
                        "		p.cod_pessoa = c.cod_cliente \n" +
                        "	left join pessoas_enderecos pe on\n" +
                        "		pe.cod_pessoa = p.cod_pessoa and\n" +
                        "		pe.seq_pes_end = 1\n" +
                        "	left join municipios m on\n" +
                        "		pe.cod_munic = m.cod_munic \n" +
                        "order by\n" +
                        "	c.cod_cliente"
                );
        ) {
            while (rs.next()) {
                ClienteIMP imp = new ClienteIMP();
                
                imp.setId(rs.getString("id"));
                imp.setCnpj(rs.getString("cnpj"));
                imp.setInscricaoestadual(rs.getString("ie"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setAtivo(rs.getBoolean("ativo"));
                imp.setBloqueado(rs.getBoolean("bloqueado"));
                imp.setEndereco(rs.getString("endereco"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setMunicipioIBGE(rs.getString("id_cidade"));
                imp.setMunicipio(rs.getString("cidade"));
                imp.setUf(rs.getString("uf"));
                imp.setCep(rs.getString("cep"));
                imp.setEstadoCivil(rs.getString("estadocivil"));
                imp.setDataNascimento(rs.getDate("data_nascimento"));
                imp.setDataCadastro(rs.getDate("data_cadastro"));
                imp.setSexo(rs.getString("sexo"));
                imp.setSalario(rs.getDouble("salario"));
                imp.setCargo(rs.getString("cargo"));
                imp.setEmpresa(rs.getString("empresa"));
                imp.setLimiteCompra(rs.getDouble("limite"));
                imp.setNomeConjuge(rs.getString("conj_nome"));
                imp.setCpfConjuge(rs.getString("conj_cpf"));
                imp.setObservacao(rs.getString("obs_inativo"));
                imp.setObservacao2(rs.getString("obs_pessoa"));
                imp.setDiaVencimento(rs.getInt("dia_vencimento"));
                imp.setTelefone(rs.getString("telefone"));
                imp.setCelular(rs.getString("celular"));
                imp.setEmail(rs.getString("email"));
                
                result.add(imp);
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
                        "	p.COD_FATURA id,\n" +
                        "	p.DATA_EMISSAO dataemissao,\n" +
                        "	rp.VR_PARCELA valor,\n" +
                        "	rp.OBS observacoes,\n" +
                        "	p.COD_CLIENTE id_cliente,\n" +
                        "	rp.DATA_VENCIMENTO datavencimento,\n" +
                        "	rp.VR_BAIXA,\n" +
                        "	1 parcela\n" +
                        "from\n" +
                        "	FATURAS_RECEBER_PARCELAS rp\n" +
                        "	join FATURAS_RECEBER p on\n" +
                        "		rp.COD_EMPRESA = p.COD_EMPRESA and\n" +
                        "		rp.TIPO_FATURA = p.TIPO_FATURA and\n" +
                        "		rp.COD_FATURA = p.COD_FATURA\n" +
                        "where\n" +
                        "	rp.COD_EMPRESA = " + getLojaOrigem() + " and\n" +
                        "	rp.VR_PARCELA > rp.VR_BAIXA "
            )
        ) {
            while (rs.next()) {
                CreditoRotativoIMP imp = new CreditoRotativoIMP();
                
                imp.setId(rs.getString("id"));
                imp.setDataEmissao(rs.getDate("dataemissao"));
                imp.setValor(rs.getDouble("valor"));
                imp.setObservacao(rs.getString("observacoes"));
                imp.setIdCliente(rs.getString("id_cliente"));
                imp.setParcela(rs.getInt("parcela"));
                double baixa = rs.getDouble("vr_baixa");
                if (baixa > 0) {
                    imp.addPagamento(imp.getId(), baixa, 0, 0, imp.getDataVencimento(), "");
                }
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
}
