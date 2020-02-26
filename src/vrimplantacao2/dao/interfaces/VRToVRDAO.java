package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class VRToVRDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "VR";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
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
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA
                }
        ));
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	l.id,\n" +
                    "	l.descricao,\n" +
                    "	f.nomefantasia,\n" +
                    "	f.razaosocial \n" +
                    "from \n" +
                    "	loja l \n" +
                    "inner join fornecedor f on l.id_fornecedor = f.id\n" +
                    "order by 	\n" +
                    "	l.id")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id,\n" +
                    "	descricao,\n" +
                    "	situacaotributaria,\n" +
                    "	porcentagem,\n" +
                    "	reduzido\n" +
                    "from 	\n" +
                    "	aliquota\n" +
                    "where \n" +
                    "	id_situacaocadastro = 1\n" +
                    "order by\n" +
                    "	descricao")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), 
                            rs.getString("descricao"), 
                            rs.getInt("situacaotributaria"), 
                            rs.getDouble("porcentagem"), 
                            rs.getDouble("reduzido")));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	id,\n" +
                    "	descricao,\n" +
                    "	id_situacaocadastro\n" +
                    "from\n" +
                    "	familiaproduto\n" +
                    "order by\n" +
                    "	1")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));
                    imp.setSituacaoCadastro(rs.getInt("id_situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	m.mercadologico1 cod_mercadologico1,\n" +
                    "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and nivel = 1) mercadologico1,\n" +
                    "	m.mercadologico2 cod_mercadologico2,\n" +
                    "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and nivel = 2) mercadologico2,\n" +
                    "	m.mercadologico3 cod_mercadologico3,\n" +
                    "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and nivel = 3) mercadologico3,\n" +
                    "	m.mercadologico4 cod_mercadologico4,\n" +
                    "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and mercadologico4 = m.mercadologico4 and nivel = 4) mercadologico4,\n" +
                    "	m.mercadologico5 cod_mercadologico5,\n" +
                    "	(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and mercadologico4 = m.mercadologico4 and mercadologico5 = m.mercadologico5 and nivel = 5) mercadologico5\n" +
                    "from\n" +
                    "	mercadologico m\n" +
                    "where \n" +
                    "	nivel = (select valor::integer from public.parametrovalor where id_loja = " + getLojaOrigem() + " and id_parametro = 1)\n" +
                    "order by\n" +
                    "	1,3,5,7,9")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("cod_mercadologico1"));
                    imp.setMerc1Descricao(rs.getString("mercadologico1"));
                    imp.setMerc2ID(rs.getString("cod_mercadologico2"));
                    imp.setMerc2Descricao(rs.getString("mercadologico2"));
                    imp.setMerc3ID(rs.getString("cod_mercadologico3"));
                    imp.setMerc3Descricao(rs.getString("mercadologico3"));
                    imp.setMerc4ID(rs.getString("cod_mercadologico4"));
                    imp.setMerc4Descricao(rs.getString("mercadologico4"));
                    imp.setMerc5ID(rs.getString("cod_mercadologico5"));
                    imp.setMerc5Descricao(rs.getString("mercadologico5"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "with \n" +
                    "	lj as (select loja.id, f.id_estado from loja join fornecedor f on loja.id_fornecedor = f.id where loja.id = " + getLojaOrigem() + "),\n" +
                    "	merc as (\n" +
                    "	select\n" +
                    "		m.mercadologico1 cod_mercadologico1,\n" +
                    "		(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and nivel = 1) mercadologico1,\n" +
                    "		m.mercadologico2 cod_mercadologico2,\n" +
                    "		(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and nivel = 2) mercadologico2,\n" +
                    "		m.mercadologico3 cod_mercadologico3,\n" +
                    "		(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and nivel = 3) mercadologico3,\n" +
                    "		m.mercadologico4 cod_mercadologico4,\n" +
                    "		(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and mercadologico4 = m.mercadologico4 and nivel = 4) mercadologico4,\n" +
                    "		m.mercadologico5 cod_mercadologico5,\n" +
                    "		(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and mercadologico4 = m.mercadologico4 and mercadologico5 = m.mercadologico5 and nivel = 5) mercadologico5\n" +
                    "	from\n" +
                    "		mercadologico m\n" +
                    "	where \n" +
                    "		nivel = (select valor::integer from public.parametrovalor where id_loja = " + getLojaOrigem() + " and id_parametro = 1)\n" +
                    "	)\n" +
                    "select\n" +
                    "	p.id,\n" +
                    "	to_char(p.datacadastro, 'DD/MM/YYYY') datacadastro,\n" +
                    "	ean.codigobarras,\n" +
                    "	p.qtdembalagem qtdembalagemcotacao,\n" +
                    "	ean.qtdembalagem,\n" +
                    "	ean_un.descricao unidade,\n" +
                    "	case when p.id_tipoembalagem = 4 or p.pesavel then 'S' else 'N' end balanca,\n" +
                    "	p.validade,\n" +
                    "	p.descricaocompleta,\n" +
                    "	p.descricaoreduzida,\n" +
                    "	p.descricaogondola,\n" +
                    "	merc.*,\n" +
                    "	p.id_familiaproduto,\n" +
                    "	fam.descricao familiaproduto,\n" +
                    "	to_char(p.pesobruto, '999999990D00') pesobruto,\n" +
                    "	to_char(p.pesoliquido, '999999990D00') pesoliquido,\n" +
                    "	to_char(vend.estoquemaximo, '999999990D00') estoquemaximo,\n" +
                    "	to_char(vend.estoqueminimo, '999999990D00') estoqueminimo,\n" +
                    "	to_char(vend.estoque, '999999990D00') estoque,\n" +
                    "	to_char(vend.custosemimposto, '999999990D00') custosemimposto,\n" +
                    "	to_char(vend.custocomimposto, '999999990D00') custocomimposto,\n" +
                    "	to_char(vend.precovenda, '999999990D00') precovenda,\n" +
                    "	vend.id_situacaocadastro,\n" +
                    "	case when vend.descontinuado then 'S' else 'N' end as descontinuado,\n" +
                    "	lpad(p.ncm1::varchar,4,'0') || lpad(p.ncm2::varchar,2,'0') || lpad(p.ncm3::varchar,2,'0') ncm,\n" +
                    "	lpad(cest.cest1::varchar,2,'0') || lpad(cest.cest2::varchar,3,'0') || lpad(cest.cest3::varchar,2,'0') cest,\n" +
                    "	piscofdeb.cst piscofins_cst_debito,\n" +
                    "	piscofcred.cst piscofins_cst_credito,\n" +
                    "	p.tiponaturezareceita piscofins_natureza_receita,\n" +
                    "	icms.situacaotributaria icms_cst,\n" +
                    "	to_char(icms.porcentagem, '999999990D00') icms_aliquota,\n" +
                    "	to_char(icms.reduzido, '999999990D00') icms_reduzido,\n" +
                    "	case when p.sugestaocotacao then 'S' else 'N' end as sugestaocotacao,\n" +
                    "	case when p.sugestaopedido then 'S' else 'N' end as sugestaopedido,\n" +
                    "	pad.desconto atacadodesconto,\n" +
                    "	pf.id id_pautafiscal\n" +
                    "from\n" +
                    "	produto p\n" +
                    "	join lj on true\n" +
                    "	left join produtoautomacao ean on ean.id_produto = p.id\n" +
                    "	left join tipoembalagem ean_un on ean_un.id = ean.id_tipoembalagem\n" +
                    "	left join merc on\n" +
                    "		merc.cod_mercadologico1 = p.mercadologico1 and\n" +
                    "		merc.cod_mercadologico2 = p.mercadologico2 and\n" +
                    "		merc.cod_mercadologico3 = p.mercadologico3 and\n" +
                    "		merc.cod_mercadologico4 = p.mercadologico4 and\n" +
                    "		merc.cod_mercadologico5 = p.mercadologico5\n" +
                    "	left join familiaproduto fam on p.id_familiaproduto = fam.id\n" +
                    "	join produtocomplemento vend on p.id = vend.id_produto and vend.id_loja = lj.id\n" +
                    "	left join cest on cest.id = p.id_cest\n" +
                    "	left join tipopiscofins piscofcred on \n" +
                    "		p.id_tipopiscofinscredito = piscofcred.id\n" +
                    "	left join tipopiscofins piscofdeb on \n" +
                    "		p.id_tipopiscofins = piscofdeb.id\n" +
                    "	join produtoaliquota aliq on p.id = aliq.id_produto and aliq.id_estado = lj.id_estado\n" +
                    "	join aliquota icms on icms.id = aliq.id_aliquotadebito\n" +
                    "	left join produtoautomacaodesconto pad on pad.codigobarras = ean.codigobarras and pad.id_loja = lj.id\n" +
                    "	left join pautafiscal pf on\n" +
                    "		p.ncm1 = pf.ncm1 and\n" +
                    "		p.ncm2 = pf.ncm2 and\n" +
                    "		p.ncm3 = pf.ncm3 and\n" +
                    "		p.excecao = pf.excecao\n" +
                    "order by\n" +
                    "	p.id")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.seteBalanca("S".equals(rs.getString("balanca")));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setCodMercadologico1(rs.getString("cod_mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("cod_mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("cod_mercadologico3"));
                    imp.setCodMercadologico4(rs.getString("cod_mercadologico4"));
                    imp.setCodMercadologico5(rs.getString("cod_mercadologico5"));
                    imp.setIdFamiliaProduto(rs.getString("id_familiaproduto"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rs.getInt("id_situacaocadastro"));
                    imp.setDescontinuado("S".equals(rs.getString("descontinuado")));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("piscofins_cst_credito"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofins_cst_debito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("piscofins_natureza_receita"));
                    imp.setSugestaoCotacao("S".equals(rs.getString("sugestaocotacao")));
                    imp.setSugestaoPedido("S".equals(rs.getString("sugestaopedido")));
                    imp.setPautaFiscalId(rs.getString("id_pautafiscal"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	f.id,\n" +
                    "	f.razaosocial razao,\n" +
                    "	f.nomefantasia fantasia,\n" +
                    "	f.cnpj cnpj_cpf,\n" +
                    "	f.inscricaoestadual ie_rg,\n" +
                    "	f.inscricaomunicipal insc_municipal,\n" +
                    "	f.inscricaosuframa suframa,\n" +
                    "	case f.bloqueado when true then 0 else 1 end ativo,\n" +
                    "	f.endereco,\n" +
                    "	f.numero,\n" +
                    "	f.complemento,\n" +
                    "	f.bairro,\n" +
                    "	f.id_municipio ibge_municipio,\n" +
                    "	m.descricao municipio,\n" +
                    "	f.id_estado ibge_uf,\n" +
                    "	e.sigla uf,\n" +
                    "	f.cep,\n" +
                    "	f.enderecocobranca cob_endereco,\n" +
                    "	f.numerocobranca cob_numero,\n" +
                    "	f.complementocobranca cob_complemento,\n" +
                    "	f.bairrocobranca cob_bairro,\n" +
                    "	f.id_municipiocobranca cob_ibge_municipio,\n" +
                    "	cm.descricao cob_municipio,\n" +
                    "	f.id_estadocobranca cob_ibge_uf,\n" +
                    "	ce.sigla cob_uf,\n" +
                    "	f.cepcobranca cob_cep,		\n" +
                    "	f.telefone tel_principal,\n" +
                    "	to_char(f.pedidominimoqtd, '999999990D00') qtd_minima_pedido,\n" +
                    "	to_char(f.pedidominimovalor, '999999990D00') valor_minimo_pedido,\n" +
                    "	to_char(f.datacadastro, 'DD/MM/YYYY') datacadastro,\n" +
                    "	f.observacao\n" +
                    "from \n" +
                    "	fornecedor f\n" +
                    "	left join municipio m on f.id_municipio = m.id\n" +
                    "	left join estado e on f.id_estado = e.id\n" +
                    "	left join municipio cm on f.id_municipiocobranca = cm.id\n" +
                    "	left join estado ce on f.id_estadocobranca = ce.id\n" +
                    "order by \n" +
                    "	id")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setInsc_municipal(rs.getString("insc_municipal"));
                    imp.setSuframa(rs.getString("suframa"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_uf(rs.getInt("ibge_uf"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCob_endereco(rs.getString("cob_endereco"));
                    imp.setCob_numero(rs.getString("cob_numero"));
                    imp.setCob_complemento(rs.getString("cob_complemento"));
                    imp.setCob_bairro(rs.getString("cob_bairro"));
                    imp.setCob_ibge_municipio(rs.getInt("cob_ibge_municipio"));
                    imp.setCob_municipio(rs.getString("cob_municipio"));
                    imp.setCob_uf(rs.getString("cob_uf"));
                    imp.setCob_cep(rs.getString("cob_cep"));
                    imp.setTel_principal(rs.getString("tel_principal"));
                    imp.setQtd_minima_pedido(rs.getInt("qtd_minima_pedido"));
                    imp.setValor_minimo_pedido(rs.getDouble("valor_minimo_pedido"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    
                    getContatoFornecedor(imp);
                    getDivisaoFornecedor(imp);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	pf.id,\n" +
                    "	lpad(pf.ncm1::varchar, 4, '0') || lpad(pf.ncm2::varchar, 2, '0') || lpad(pf.ncm3::varchar, 2, '0') ncm,\n" +
                    "	pf.excecao,\n" +
                    "	uf.sigla uf,\n" +
                    "	pf.iva,\n" +
                    "	pf.tipoiva,\n" +
                    "	pf.ivaajustado,\n" +
                    "	pf.icmsrecolhidoantecipadamente,\n" +
                    "	ac.situacaotributaria credito_cst,\n" +
                    "	ac.porcentagem credito_aliquota,\n" +
                    "	ac.reduzido credito_reduzido,\n" +
                    "	ad.situacaotributaria debito_cst,\n" +
                    "	ad.porcentagem debito_aliquota,\n" +
                    "	ad.reduzido debito_reduzido,\n" +
                    "	adfe.situacaotributaria debito_foraest_cst,\n" +
                    "	adfe.porcentagem debito_foraest_aliquota,\n" +
                    "	adfe.reduzido debito_foraest_reduzido,\n" +
                    "	acfe.situacaotributaria credito_foraest_cst,\n" +
                    "	acfe.porcentagem credito_foraest_aliquota,\n" +
                    "	acfe.reduzido credito_foraest_reduzido\n" +
                    "from\n" +
                    "	pautafiscal pf\n" +
                    "	join estado uf on\n" +
                    "		pf.id_estado = uf.id\n" +
                    "	left join aliquota ac on\n" +
                    "		pf.id_aliquotacredito = ac.id\n" +
                    "	left join aliquota ad on\n" +
                    "		pf.id_aliquotadebito = ad.id\n" +
                    "	left join aliquota adfe on\n" +
                    "		pf.id_aliquotadebitoforaestado = adfe.id\n" +
                    "	left join aliquota acfe on\n" +
                    "		pf.id_aliquotacreditoforaestado = acfe.id\n" +
                    "order by\n" +
                    "	pf.id")) {
                while(rs.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setIva(rs.getDouble("iva"));
                    imp.setTipoIva(rs.getInt("tipoiva") == 0 ? TipoIva.PERCENTUAL : TipoIva.VALOR);
                    imp.setIvaAjustado(rs.getDouble("ivaajustado"));
                    imp.setIcmsRecolhidoAntecipadamente(rs.getBoolean("icmsrecolhidoantecipadamente"));
                    imp.setAliquotaCredito(rs.getInt("credito_cst"), rs.getDouble("credito_aliquota"), rs.getDouble("credito_reduzido"));
                    imp.setAliquotaDebito(rs.getInt("debito_cst"), rs.getDouble("debito_aliquota"), rs.getDouble("debito_reduzido"));
                    imp.setAliquotaCreditoForaEstado(rs.getInt("credito_foraest_cst"), rs.getDouble("credito_foraest_aliquota"), rs.getDouble("debito_foraest_reduzido"));
                    imp.setAliquotaDebitoForaEstado(rs.getInt("debito_foraest_cst"), rs.getDouble("debito_foraest_aliquota"), rs.getDouble("credito_foraest_reduzido"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	pf.id_fornecedor,\n" +
                    "	pf.id_produto,\n" +
                    "	pf.codigoexterno cod_produto_fornecedor,\n" +
                    "	pf.qtdembalagem,\n" +
                    "	to_char(pf.dataalteracao, 'DD/MM/YYYY') dataalteracao,\n" +
                    "	to_char(pf.pesoembalagem, '999999990D0000') pesoembalagem\n" +
                    "from\n" +
                    "	produtofornecedor pf\n" +
                    "order by\n" +
                    "	id_fornecedor,\n" +
                    "	id_produto,\n" +
                    "	codigoexterno")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("cod_produto_fornecedor"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setPesoEmbalagem(rs.getDouble("pesoembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    private void getContatoFornecedor(FornecedorIMP imp) throws SQLException {
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	telefone,\n" +
                    "	celular,\n" +
                    "	email,\n" +
                    "	tp.descricao contato\n" +
                    "from \n" +
                    "	fornecedorcontato fc\n" +
                    "join tipocontato tp on fc.id_fornecedor = tp.id \n" +
                    "where 	\n" +
                    "	id_fornecedor = " + imp.getImportId())) {
                while(rs.next()) {
                    imp.addContato(imp.getImportId(), 
                                rs.getString("contato"),
                                rs.getString("telefone"), 
                                rs.getString("celular"), 
                                TipoContato.COMERCIAL,
                                rs.getString("email"));
                }
            }
        }
    }
    
    private void getDivisaoFornecedor(FornecedorIMP imp) throws SQLException {
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id,\n" +
                    "	id_divisaofornecedor,\n" +
                    "	prazoentrega,\n" +
                    "	prazovisita,\n" +
                    "	prazoseguranca\n" +
                    "from \n" +
                    "	fornecedorprazo\n" +
                    "where 	\n" +
                    "	id_fornecedor = " + imp.getImportId() + "\n" +
                    "	id_loja = " + getLojaOrigem())) {
                while(rs.next()) {
                    imp.addDivisao(rs.getString("id"), rs.getInt("prazovisita"), rs.getInt("prazoentrega"), rs.getInt("prazoseguranca"));
                }
            }
        }
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	c.id id,\n" +
                    "	c.cnpj,\n" +
                    "	c.inscricaoestadual,\n" +
                    "	c.orgaoemissor,\n" +
                    "	c.nome razao,\n" +
                    "	c.nome fantasia,\n" +
                    "	c.id_situacaocadastro,\n" +
                    "	case when c.bloqueado then 'S' else 'N' end bloqueado,\n" +
                    "	to_char(c.datarestricao, 'DD/MM/YYYY') databloqueio,\n" +
                    "	c.endereco,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.id_municipio municipioIBGE,\n" +
                    "	mun.descricao municipio,\n" +
                    "	c.id_estado ufIBGE,\n" +
                    "	est.sigla uf,\n" +
                    "	c.cep,\n" +
                    "   civil.id id_estadocivil,\n" +        
                    "	coalesce(substring(civil.descricao,1,3), 'NAO') estadocivil,\n" +
                    "	to_char(c.datanascimento, 'DD/MM/YYYY') datanascimento,\n" +
                    "	to_char(c.datacadastro, 'DD/MM/YYYY') datacadastro,\n" +
                    "	case c.sexo when 0 then 'F' else 'M' end sexo,\n" +
                    "	c.empresa,\n" +
                    "	c.enderecoempresa empresaendereco,\n" +
                    "	c.numeroempresa empresanumero,\n" +
                    "	c.complementoempresa empresacomplemento,\n" +
                    "	c.bairroempresa empresabairro,\n" +
                    "	c.id_municipioempresa empresamunicipioIBGE,\n" +
                    "	mun_emp.descricao empresamunicipio,\n" +
                    "	c.id_estadoempresa empresaufIBGE,\n" +
                    "	est_emp.sigla empresauf,\n" +
                    "	c.cepempresa empresacep,\n" +
                    "	c.telefoneempresa empresatelefone,\n" +
                    "	to_char(c.dataadmissao, 'DD/MM/YYYY') dataadmissao,\n" +
                    "	c.cargo,\n" +
                    "	to_char(c.salario, '999999990D00') salario,\n" +
                    "	to_char(c.valorlimite, '999999990D00') valorlimite,\n" +
                    "	c.nomeconjuge,\n" +
                    "	c.nomepai,\n" +
                    "	c.nomemae,\n" +
                    "	regexp_replace(c.observacao2,'[\\\\n\\\\r]+',' ','g') observacao,\n" +
                    "	c.vencimentocreditorotativo diavencimento,\n" +
                    "	case when c.permitecreditorotativo then 'S' else 'N' end permitecreditorotativo,\n" +
                    "	case when c.permitecheque then 'S' else 'N' end permitecheque,\n" +
                    "	c.telefone,\n" +
                    "	c.celular,\n" +
                    "	c.email,\n" +
                    "	c.telefone cobrancaTelefone,\n" +
                    "	0 prazopagamento,\n" +
                    "	c.endereco cobrancaendereco,\n" +
                    "	c.numero cobrancanumero,\n" +
                    "	c.complemento cobrancacomplemento,\n" +
                    "	c.bairro cobrancabairro,\n" +
                    "	c.id_municipio cobrancamunicipioibge,\n" +
                    "	mun.descricao cobrancamunicipio,\n" +
                    "	c.id_estado cobrancaufibge,\n" +
                    "	est.sigla cobrancauf,\n" +
                    "	c.cep cobrancacep,\n" +
                    "	'NENHUM'::varchar tipoorgaopublico,\n" +
                    "	0 limitecompra,\n" +
                    "	''::varchar inscricaomunicipal,\n" +
                    "	'NAO CONTRIBUINTE'::varchar tipoindicadorie\n" +
                    "from \n" +
                    "	clientepreferencial c\n" +
                    "	left join municipio mun on\n" +
                    "		c.id_municipio = mun.id\n" +
                    "	left join estado est on\n" +
                    "		c.id_estado = est.id\n" +
                    "	left join municipio mun_emp on\n" +
                    "		c.id_municipioempresa = mun_emp.id\n" +
                    "	left join estado est_emp on\n" +
                    "		c.id_estadoempresa = est_emp.id\n" +
                    "	left join tipoestadocivil civil on\n" +
                    "		c.id_tipoestadocivil = civil.id\n" +
                    "order by\n" +
                    "	c.id")) {
                ClienteIMP imp = new ClienteIMP();
                
                imp.setId(rs.getString("id"));
                imp.setCnpj(rs.getString("cnpj"));
                imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                imp.setOrgaoemissor(rs.getString("orgaoemissor"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setAtivo(rs.getInt("id_situacaocadastro") == 1);
                imp.setBloqueado("S".equals(rs.getString("bloqueado")));
                imp.setDataBloqueio(rs.getDate("databloqueio"));
                imp.setEndereco(rs.getString("endereco"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setMunicipioIBGE(rs.getString("municipioibge"));
                imp.setMunicipio(rs.getString("municipio"));
                imp.setUfIBGE(rs.getInt("ufibge"));
                imp.setUf(rs.getString("uf"));
                imp.setCep(rs.getString("cep"));
                imp.setEstadoCivil(rs.getInt("id_estadocivil"));
                imp.setDataNascimento(rs.getDate("datanascimento"));
                imp.setDataCadastro(rs.getDate("datacadastro"));
                imp.setSexo("F".equals(rs.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                imp.setEmpresa(rs.getString("empresa"));
                imp.setEmpresaEndereco(rs.getString("empresaendereco"));
                imp.setEmpresaNumero(rs.getString("empresanumero"));
                imp.setEmpresaComplemento(rs.getString("empresacomplemento"));
                imp.setEmpresaBairro(rs.getString("empresabairro"));
                imp.setEmpresaMunicipioIBGE(rs.getInt("empresamunicipioibge"));
                imp.setEmpresaMunicipio(rs.getString("empresamunicipio"));
                imp.setEmpresaUfIBGE(rs.getInt("empresaufibge"));
                imp.setEmpresaUf(rs.getString("empresauf"));
                imp.setEmpresaCep(rs.getString("empresacep"));
                imp.setEmpresaTelefone(rs.getString("empresatelefone"));
                imp.setDataAdmissao(rs.getDate("dataadmissao"));
                imp.setCargo(rs.getString("cargo"));
                imp.setSalario(rs.getDouble("salario"));
                imp.setValorLimite(rs.getDouble("valorlimite"));
                imp.setNomeConjuge(rs.getString("nomeconjuge"));
                imp.setNomePai(rs.getString("nomepai"));
                imp.setNomeMae(rs.getString("nomemae"));
                imp.setEmail(rs.getString("email"));
                imp.setCobrancaTelefone(rs.getString("cobrancatelefone"));
                imp.setPrazoPagamento(rs.getInt("prazopagamento"));
                imp.setCobrancaEndereco(rs.getString("cobrancaendereco"));
                imp.setCobrancaNumero(rs.getString("cobrancanumero"));
                imp.setCobrancaComplemento(rs.getString("cobrancacomplemento"));
                imp.setCobrancaBairro(rs.getString("cobrancaobairro"));
                imp.setCobrancaMunicipioIBGE(rs.getInt("cobrancamunicipioibge"));
                imp.setCobrancaMunicipio(rs.getString("cobrancamunicipio"));
                imp.setCobrancaUfIBGE(rs.getInt("cobrancaufibge"));
                imp.setCobrancaUf(rs.getString("cobrancauf"));
                imp.setCobrancaCep(rs.getString("cobrancacep"));
                imp.setInscricaoMunicipal(rs.getString("inscricaomunicipal"));
                imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);
                
                getContatoCliente(imp);
                
                result.add(imp);
            }
        }
        return result;
    }
    
    private void getContatoCliente(ClienteIMP imp) throws SQLException {
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id,\n" +
                    "	nome,\n" +
                    "	telefone,\n" +
                    "	celular,\n" +
                    "	tc.descricao contato\n" +
                    "from \n" +
                    "	clientepreferencialcontato cp\n" +
                    "join tipocontato tc on cp.id_tipocontato = tc.id \n" +
                    "where 	\n" +
                    "	id_clientepreferencial = " + imp.getId())) {
                while(rs.next()) {
                    imp.addContato(rs.getString("id"), rs.getString("contato"), rs.getString("telefone"), rs.getString("celular"), null);
                }
            }
        }
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	r.id,\n" +
                    "	c.cnpj,\n" +
                    "	to_char(r.dataemissao, 'DD/MM/YYYY') emissao,\n" +
                    "	to_char(r.datavencimento, 'DD/MM/YYYY') vencimento,\n" +
                    "	r.ecf,\n" +
                    "	'P' || r.id_clientepreferencial idcliente,\n" +
                    "	r.valorjuros juros,\n" +
                    "	r.valormulta multa,\n" +
                    "	r.numerocupom cupom,\n" +
                    "	r.observacao,\n" +
                    "	r.parcela,\n" +
                    "	r.valor\n" +
                    "from\n" +
                    "	recebercreditorotativo r\n" +
                    "	join clientepreferencial c on\n" +
                    "		r.id_clientepreferencial = c.id\n" +
                    "where\n" +
                    "	id_situacaocadastro = 0 and\n" +
                    "	id_loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	r.id")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setMulta(rs.getDouble("multa"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valor"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
