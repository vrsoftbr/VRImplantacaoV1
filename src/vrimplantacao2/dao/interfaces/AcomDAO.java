package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

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
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
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
                    "	1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString(rst.getString("Grp_id")));
                    imp.setDescricao(rst.getString("Grp_nome"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
