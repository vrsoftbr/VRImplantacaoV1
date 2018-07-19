package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class WShopDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(WShopDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "WShop";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>(OpcaoProduto.getMercadologico());
        opt.addAll(OpcaoProduto.getPadrao());
        opt.addAll(OpcaoProduto.getFamilia());
        opt.addAll(OpcaoProduto.getComplementos());
        opt.addAll(OpcaoProduto.getTributos());
        return opt;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	idgrupo merc1,\n" +
                    "	nmgrupo merc1_desc\n" +
                    "from\n" +
                    "	wshop.grupo\n" +
                    "order by \n" +
                    "	nmgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cdempresa, nrcgc || '-' || nmempresa razao from wshop.empshop order by cdempresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cdempresa"), rst.getString("razao")));
                }
            }
        }
        
        return result;
    }
    
    private Map<String, String> mapaFamilia;

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        Set<String> fam = mapearFamilia();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            for (String key: fam) {
                try (ResultSet rst = stm.executeQuery(
                        "select iddetalhe, dsdetalhe from wshop.detalhe where iddetalhe = '" + key + "'"
                )) {
                    while (rst.next()) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("iddetalhe"));
                        imp.setDescricao(rst.getString("dsdetalhe"));
                        result.add(imp);
                    }
                }
            }
        }   
        
        
        return result;
    }

    private Set<String> mapearFamilia() throws SQLException {
        Map<String, Set<String>> idsFamilias = new HashMap<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select iddetalhe, iddetalheequivalente from wshop.ProdEquivalente order by 1, 2"
            )) {
                while (rst.next()) {
                    Set<String> fam = idsFamilias.get(rst.getString("iddetalhe"));
                    if (fam == null) {
                        fam = new HashSet<>();
                        idsFamilias.put(rst.getString("iddetalhe"), fam);
                    }
                    fam.add(rst.getString("iddetalheequivalente"));
                    LOG.finest("ITEM: " + rst.getString("iddetalhe") + ":" + rst.getString("iddetalheequivalente"));
                }
            }
        }
        
        LOG.fine("Nº de famílias: " + idsFamilias.size());
        Set<String> fam = new HashSet<>();
        mapaFamilia = new HashMap<>();
        
        for (String key: idsFamilias.keySet()) {
            boolean encontrou = false;
            
            for (Set<String> f: idsFamilias.values()) {
                if (f.contains(key)) {
                    encontrou = true;
                }
            }
            
            if (!encontrou) {
                fam.add(key);
                StringBuilder b = new StringBuilder(key).append("; {");
                for (String a: idsFamilias.get(key)) {
                    mapaFamilia.put(a, key);
                    b.append(a).append(",");
                }
                b.append("}");
                LOG.finer("Familia adicionada ao mapa: " + b.toString());
            }
        }
        
        return fam;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                
                }
            }
        }
        
        return result;
    }
    
    
    
}
