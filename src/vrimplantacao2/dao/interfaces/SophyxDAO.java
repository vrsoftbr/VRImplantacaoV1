package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class SophyxDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SOPHYX";
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
                    "    s_razao_social razaosocial\n" +
                    "from\n" +
                    "    lojas"
            )) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razaosocial")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    g.id codmercadologico1,\n" +
                    "    g.s_descricao descricao1,\n" +
                    "    d.id codmercadologico2,\n" +
                    "    d.s_descricao descricao2,\n" +
                    "    s.id codmercadolodico3,\n" +
                    "    s.s_descricao descricao3\n" +
                    "from\n" +
                    "    departamentos d\n" +
                    "join grupos g on d.id_grupo = g.id\n" +
                    "join sessoes s on d.id = s.id_departamento\n" +
                    "order by\n" +
                    "    g.id, d.id, s.id"
            )) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codmercadologico1"));
                    imp.setMerc1Descricao(rs.getString("descricao1"));
                    imp.setMerc2ID(rs.getString("codmercadologico2"));
                    imp.setMerc2Descricao(rs.getString("descricao2"));
                    imp.setMerc3ID(rs.getString("codmercadologico3"));
                    imp.setMerc3Descricao(rs.getString("descricao3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
                    "    s_descricao descricao\n" +
                    "from\n" +
                    "    familias\n" +
                    "order by    \n" +
                    "    s_descricao"
            )) {
                while(rs.next()) {
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "   p.id,\n" +
                    "   p.codigo_barras ean,\n" +
                    "   p.codigo_interno,\n" +
                    "   p.ativo,\n" +
                    "   p.descricao descricaocompleta,\n" +
                    "   p.descricao_resumida descricaoreduzida,\n" +
                    "   p.grupo merc1,\n" +
                    "   p.departamento merc2,\n" +
                    "   p.sessao merc3,\n" +
                    "   p.familia,\n" +
                    "   p.unidade,\n" +
                    "   p.margem,\n" +
                    "   p.f_ult_preco_compra custoanterior,\n" +
                    "   p.preco_compra custosemimposto,\n" +
                    "   p.preco_custo custocomimposto,\n" +
                    "   p.preco_venda1 precovenda,\n" +
                    "   p.pesado,\n" +
                    "   p.validade,\n" +
                    "   p.estoque_max,\n" +
                    "   p.estoque_min,\n" +
                    "   p.estoque_atual,\n" +
                    "   p.data_inclusao,\n" +
                    "   p.s_ncm ncm,\n" +
                    "   p.f_mva_st mva,\n" +
                    "   p.icms,\n" +
                    "   p.s_cod_cst_pis_entrada pis_entrada,\n" +
                    "   p.s_cod_cst_pis_saida pis_saida,\n" +
                    "   p.s_cod_cst_cofins_entrada cofins_entrada,\n" +
                    "   p.s_cod_cst_cofins_saida cofins_saida,\n" +
                    "   p.s_cest cest,\n" +
                    "   p.aliquota id_aliquotadebito\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "order by\n" +
                    "    p.id"
            )) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                }
            }
        }
        return result;
    }
    
}
