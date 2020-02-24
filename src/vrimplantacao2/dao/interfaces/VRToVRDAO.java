package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
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
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
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
                    "lj as (select loja.id, f.id_estado from loja join fornecedor f on loja.id_fornecedor = f.id where loja.id = " + getLojaOrigem() + "),\n" +
                    "merc as (\n" +
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
                    ")\n" +
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
                    "	pad.desconto atacadodesconto\n" +
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
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
