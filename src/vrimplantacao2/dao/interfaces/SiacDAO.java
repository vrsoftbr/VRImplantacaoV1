package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SiacDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Siac";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select empresa_id, fantasia from empresas order by empresa_id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("empresa_id"), rst.getString("fantasia")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select grupo_icms_id, descricao from grupo_icms order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("grupo_icms_id"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select grupo_id, descricao from grupos order by grupo_id"
            )) {
                while (rst.next()) {
                    String[] ids = rst.getString("grupo_id").split("\\.");
                    
                    if (ids.length == 1) {
                        if (!result.containsKey(ids[0])) {
                            MercadologicoNivelIMP imp = new MercadologicoNivelIMP(ids[0], rst.getString("descricao"));
                            result.put(imp.getId(), imp);
                        }
                    } else if (ids.length == 2) {
                        MercadologicoNivelIMP pai = result.get(ids[0]);
                        pai.addFilho(ids[1], rst.getString("descricao"));
                    }
                }
            }
        }
        
        return new ArrayList<>(result.values());
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select familia_id, descricao from produtos_familias order by familia_id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("familia_id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  p.produto_id id,\n" +
                    "  p.dt_cadastro datacadastro,\n" +
                    "  ean.ean,\n" +
                    "  p.emb_fracionada qtd_cotacao,\n" +
                    "  ean.qtd_embalagem,\n" +
                    "  p.unidade,\n" +
                    "  case when p.permite_venda_fracionada = 'S' then 1 else 0 end e_balanca,\n" +
                    "  case when p.pesavel = 'S' then 1 else 0 end pesavel,\n" +
                    "  p.nome_generico descricaocompleta,\n" +
                    "  p.nome_fracionado descricaoreduzida,\n" +
                    "  p.validade,\n" +
                    "  coalesce(p.grupo_id,'') grupo_id,\n" +
                    "  p.familia_id,\n" +
                    "  p.peso_unidade pesobruto,\n" +
                    "  p.peso_unidade_liquido pesoliquido,\n" +
                    "  est.estoque_atual,\n" +
                    "  est.estoque_minimo,\n" +
                    "  coalesce(p.perc_lucro, 0) margem,\n" +
                    "  p.custo_compra custosemimposto,\n" +
                    "  p.custo_venda preco,\n" +
                    "  case p.ativo when 'S' then 1 else 0 end situacaocadastro,\n" +
                    "  p.codigo_fiscal ncm,\n" +
                    "  pe.codigo_cest cest,\n" +
                    "  p.codigo_natureza_prod_pis pis_natureza_rec,\n" +
                    "  pe.grupo_icms_id id_icms,\n" +
                    "  p.codigo_fabrica id_fabricante\n" +
                    "from\n" +
                    "  produtos p\n" +
                    "  join empresas emp on emp.empresa_id = '" + getLojaOrigem() + "'\n" +
                    "  join produtos_empresas pe on\n" +
                    "       pe.produto_id = p.produto_id and\n" +
                    "       pe.empresa_id = emp.empresa_id\n" +
                    "  left join(\n" +
                    "          select\n" +
                    "            p.produto_id,\n" +
                    "            p.codigo_barra ean,\n" +
                    "            p.fator_mutiplicacao qtd_embalagem\n" +
                    "          from\n" +
                    "            produtos p\n" +
                    "          where\n" +
                    "            not nullif(trim(p.codigo_barra),'') is null\n" +
                    "          union  \n" +
                    "          select\n" +
                    "            ean.produto_id,\n" +
                    "            ean.codigo_barra,\n" +
                    "            1 qtd_embalagem\n" +
                    "          from\n" +
                    "            codigo_barras ean\n" +
                    "  ) ean on p.produto_id = ean.produto_id\n" +
                    "  join estoques est on\n" +
                    "       est.produto_id = p.produto_id and\n" +
                    "       est.empresa_id = emp.empresa_id\n" +
                    "order by\n" +
                    "      1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtd_cotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtd_embalagem"));
                    
                    if (rst.getBoolean("e_balanca")) {
                        imp.seteBalanca(true);
                        if (rst.getBoolean("pesavel")) {
                            imp.setTipoEmbalagem("KG");
                        } else {
                            imp.setTipoEmbalagem("UN");
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                    }
                    
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setValidade(rst.getInt("validade"));
                    
                    String[] ids = rst.getString("grupo_id").split("\\.");
                    if (ids.length > 0) {
                        imp.setCodMercadologico1(ids[0]);
                        if (ids.length > 1) {
                            imp.setCodMercadologico2(ids[1]);
                        }
                    }
                    
                    imp.setIdFamiliaProduto(rst.getString("familia_id"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoque(rst.getDouble("estoque_atual"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("pis_natureza_rec"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setFornecedorFabricante(rst.getString("id_fabricante"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
}
