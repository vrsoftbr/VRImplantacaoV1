package vrimplantacao2.dao.interfaces;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class GTechDAO extends InterfaceDAO implements MapaTributoProvider {

    /*
     Para localizar a senha do banco de dados do GTech ou G3 Informática,
     localizar a classe GTechEncriptDAO, passar o texto encriptografado no método Main 
     para retornar a senha do atual banco de dados.
     */
    @Override
    public String getSistema() {
        return "GTech";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	 aliquota id,\n"
                    + "    descricao\n"
                    + "FROM \n"
                    + "	aliquotas_icms\n"
                    + "order by\n"
                    + "	2")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select 1 id, 'Mercado e Sacolao da Economia' razao")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 p.id,\n" +
                    "    p.codigo_interno,\n" +
                    "    p.ean,\n" +
                    "    p.descricao_pdv descricaocompleta,\n" +
                    "    p.id_grupo,\n" +
                    "    p.valor_custo,\n" +
                    "    p.valor_venda,\n" +
                    "    p.data_cadastro,\n" +
                    "    p.estoque_max,\n" +
                    "    p.estoque_min,\n" +
                    "    p.qtd_estoque estoque,\n" +
                    "    un.nome unidade,\n" +
                    "    p.ncm,\n" +
                    "    p.aliquota_icms_dentro icmsdebito,\n" +
                    "    p.cod_cst_dentro cstdebito,\n" +
                    "    p.reducao_bc_dentro icmsreducaodebito,\n" +
                    "    coalesce(p.balanca_integrada, 0) isBalanca,\n" +
                    "    p.excluido,\n" +
                    "    p.desativado,\n" +
                    "    p.cod_nat_rec naturezareceita,\n" +
                    "    p.cest,\n" +
                    "    ce.cst cofinsdebito,\n" +
                    "    cs.cst cofinscredito,\n" +
                    "    p.ECF_ICMS_ST idaliquota\n" +
                    "from\n" +
                    "	produto p\n" +
                    "left join unidade_produto un on (p.id_unidade_produto = un.id)\n" +
                    "left join grupocofins ce on (p.id_grupo_pis_saida = ce.id)\n" +
                    "left join grupocofins cs on (p.id_grupo_pis_entrada = cs.id)\n" +
                    "order by\n" +
                    "	p.id")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCustoComImposto(rs.getDouble("valor_custo"));
                    imp.setCustoSemImposto(rs.getDouble("valor_custo"));
                    imp.setPrecovenda(rs.getDouble("valor_venda"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_max"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_min"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setNcm(rs.getString("ncm"));
                    if(rs.getInt("isBalanca") == 1) {
                        imp.seteBalanca(true);
                        imp.setEan(imp.getImportId());
                    }
                    imp.setSituacaoCadastro(rs.getInt("desativado") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("cofinscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("cofinsdebito"));
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsCreditoId(rs.getString("idaliquota"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
