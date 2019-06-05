package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class AcomDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    private String codigoMercadologico = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
            
    @Override
    public String getSistema() {
        if (!"".equals(complemento)) {
            return "ACOM - " + complemento;
        } else {
            return "ACOM";
        }
    }

    public void setCodigoMercadologico(String codigoMercadologico) {
        this.codigoMercadologico = codigoMercadologico;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	t.Tes_cod, \n" +
                    "	t.Tes_texto, \n" +
                    "	t.Tes_tipo,\n" +
                    "	t.Tes_sittrib,\n" +
                    "	t.Tes_aliquota_icms,\n" +
                    "	t.Tes_reducao_percentual\n" +
                    "from \n" +
                    "	Tipo_entrada_saida t\n" +
                    "	join Produto p on\n" +
                    "		t.Tes_cod = p.Pro_te or\n" +
                    "		t.Tes_cod = p.Pro_ts\n" +
                    "order by\n" +
                    "	t.Tes_cod"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("Tes_cod"), 
                            String.format(
                                    "Tipo: '%s' CST: '%s' Aliq: '%.2f' Red: '%.2f'",
                                    rst.getString("Tes_tipo"),
                                    rst.getString("Tes_sittrib"),
                                    rst.getDouble("Tes_aliquota_icms"),
                                    rst.getDouble("Tes_reducao_percentual")
                            ),
                            Utils.stringToInt(rst.getString("Tes_sittrib")),
                            rst.getDouble("Tes_aliquota_icms"), 
                            rst.getDouble("Tes_reducao_percentual")
                    ));
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	Fil_cod id,\n" +
                    "	Fil_nome nome\n" +
                    "from\n" +
                    "	filiais\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("nome")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	m1.Tgi_item,\n" +
                    "	m1.Tgi_nome,\n" +
                    "	m2.Tgs_sitem,\n" +
                    "	m2.Tgs_nome\n" +
                    "from \n" +
                    "	Tabela_generica_item m1\n" +
                    "	left join Tabela_generica_subitem m2 on\n" +
                    "		m1.tgi_cod = m2.Tgs_cod and\n" +
                    "		m1.Tgi_item = m2.Tgs_item\n" +
                    "where\n" +
                    "	m1.Tgi_cod = '" + this.codigoMercadologico +"' and\n" +
                    "	m1.Filial = '" + getLojaOrigem() + "'\n" +
                    "order by\n" +
                    "	1,3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("Tgi_item"));
                    imp.setMerc1Descricao(rst.getString("Tgi_nome"));
                    imp.setMerc2ID(rst.getString("Tgs_sitem"));
                    imp.setMerc2Descricao(rst.getString("Tgs_nome"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	Grp_id,\n" +
                    "	Grp_nome\n" +
                    "from\n" +
                    "	Grupo_preco\n" +
                    "order by\n" +
                    "	Grp_nome"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("Grp_id"));
                    imp.setDescricao(rst.getString("Grp_nome"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.Pro_cod id,\n" +
                    "	p.Dtinc datacadastro,\n" +
                    "	p.Dtalt dataalteracao,\n" +
                    "	p.Pro_codean ean,\n" +
                    "	p.Pro_qtdembcom qtdcotacao,\n" +
                    "	p.Pro_qtdemvem qtdembalagem,\n" +
                    "	p.Pro_um unidade,\n" +
                    "	case when p.Pro_um = 'KG' or p.Pro_fracionado = 'Sim' then 1 else 0 end e_balanca,\n" +
                    "	p.Pro_validade validade,\n" +
                    "	p.Pro_nome descricaocompleta,\n" +
                    "	p.Pro_descpdv descricaoreduzida,\n" +
                    "	p.Pro_grupo merc1,\n" +
                    "	p.Pro_subgrp merc2,\n" +
                    "	p.Pro_fornecedor,\n" +
                    "	(select top 1 grpi_id from Grupo_preco_item where Grpi_codint = p.Pro_cod) id_familia,\n" +
                    "	p.Pro_pbruto pesobruto,\n" +
                    "	p.Pro_pliquido pesoliquido,\n" +
                    "	p.Pro_estoqminimo estoqueminimo,\n" +
                    "	p.Pro_estoqmaximo estoquemaximo,\n" +
                    "	est.Alm_disponivel estoque,\n" +
                    "	p.Pro_p_margem margem,\n" +
                    "	p.Pro_custo_aquisicao custocomimposto,\n" +
                    "	p.Pro_preco_base,\n" +
                    "	p.Pro_preco_venda precovenda,\n" +
                    "	case when p.Pro_status = 1 then 0 else 1 end situacaocadastro,\n" +
                    "	p.Pro_ncm ncm,\n" +
                    "	p.Pro_cest cest,\n" +
                    "	p.Pro_pis_cofins_entrada piscofins_entrada,\n" +
                    "	p.Pro_pis_cofins_saida piscofins_saida,\n" +
                    "	p.Pro_natureza_receita piscofins_natureza_receita,\n" +
                    "	p.Pro_te,\n" +
                    "	p.Pro_ts\n" +
                    "from\n" +
                    "	Filiais f\n" +
                    "	join Produto p on	 \n" +
                    "		f.Fil_cod = p.Filial\n" +
                    "	join Almoxarifado est on \n" +
                    "		p.Pro_cod = est.Alm_cod13 and\n" +
                    "		est.Filial = f.Fil_cod\n" +
                    "where	\n" +
                    "	f.Fil_cod = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdcotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));
                    imp.setIcmsDebitoId(rst.getString("Pro_te"));
                    imp.setIcmsCreditoId(rst.getString("Pro_ts"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
