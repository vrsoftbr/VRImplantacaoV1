package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class GondolaDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Gondola";
    }
    
    public List<Estabelecimento> getLoja() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select fi_cod id, fi_razsoc razao from gondola.filial")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "    ai_cod id,\n" +
                    "    ai_nom descricao,\n" +
                    "    ai_per_icms icms,\n" +
                    "    ai_per_reduc_icms reducao,\n" +
                    "    ai_flg_tip_sitrib cst\n" +
                    "from \n" +
                    " gondola.aliq_icms")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), 
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  p.pr_cod id,\n" +
                    "  p.pr_cod_altiv idalter,\n" +
                    "  ean.bapr_cod_barra ean,\n" +
                    "  p.um_sig_compra unidadecompra,\n" +
                    "  decode(p.pr_flg_balanca, 'S', 1, 0) balanca,\n" +
                    "  p.um_sig_venda unidadevenda,\n" +
                    "  p.pr_nom descricao,\n" +
                    "  p.pr_nom_reduz descricaoreduzida,\n" +
                    "  p.pr_pesobr pesobruto,\n" +
                    "  p.pr_pesolq pesoliquido,\n" +
                    "  p.pr_flg_peso_variav variavel,\n" +
                    "  decode(p.pr_flg_ativo, 'S', 1, 0) ativo,\n" +
                    "  p.pr_val_preco_custo custosemimposto,\n" +
                    "  decode(p.pr_val_compra_final, 0, \n" +
                    "        p.pr_val_preco_custo, \n" +
                    "        p.pr_val_compra_final) custocomimposto,\n" +
                    "  p.pr_margem_venda margem,\n" +
                    "  prv.pvpr_val_prvda precovenda,\n" +
                    "  p.pr_dia_valid validade,\n" +
                    "  p.pr_dat_cadstr cadastro,\n" +
                    "  p.depr_cod departamento,\n" +
                    "  p.grpr_cod grupo,\n" +
                    "  p.capr_cod categoria,\n" +
                    "  p.clfi_cod ncm,\n" +
                    "  p.cest_cod cest,\n" +
                    "  p.cpc_cod_ent piscredito,\n" +
                    "  p.cpc_cod_sai pisdebito,\n" +
                    "  p.cpcn_cod_sai naturezareceita,\n" +
                    "  picm.ai_cod idicms,\n" +
                    "  picm.PUAI_PER_MVA_ST percmva,\n" +
                    "  picm.PUAI_PER_REDUC_MVA_ST redmva,\n" +
                    "  picm.PUAI_VAL_PAUTA_ST mva\n" +
                    "from \n" +
                    "  gondola.prduto p\n" +
                    "left join gondola.barra_prduto ean on p.pr_cod = ean.pr_cod\n" +
                    "left join gondola.prvda_prduto prv on p.pr_cod = prv.pr_cod\n" +
                    "left join gondola.prduto_uf_aliq_icms picm on p.pr_cod = picm.pr_cod\n" +
                    "where \n" +
                    "     prv.fi_cod = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("unidadevenda"));
                    imp.setTipoEmbalagemCotacao(rs.getString("unidadecompra"));
                    imp.seteBalanca(rs.getInt("balanca") == 1);
                    
                    if(imp.isBalanca()) {
                        imp.setEan(rs.getString("idalter"));
                    }
                    
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("pisdebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   pr_cod id,\n" +
                    "   bapr_cod_barra ean,\n" +
                    "   bapr_qtd_unid qtd,\n" +
                    "   um_sig embalagem\n" +
                    "from\n" +
                    "   gondola.barra_prduto")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setQtdEmbalagem(rs.getInt("qtd"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
}
