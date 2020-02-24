package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Importacao
 */
public class VRToVRDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "VR";
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
}
