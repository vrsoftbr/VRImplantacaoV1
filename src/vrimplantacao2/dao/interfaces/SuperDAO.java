package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

public class SuperDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Super";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cd_loja id,\n" +
                    "    cd_loja || ' - ' || nm_fantazia descricao\n" +
                    "from\n" +
                    "    loja\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    seg.cd_segmt,\n" +
                    "    seg.dsc_segmt,\n" +
                    "    d.cd_depart,\n" +
                    "    d.dsc_depart,\n" +
                    "    g.cd_grupo,\n" +
                    "    g.dsc_grupo,\n" +
                    "    sb.cd_subgrupo,\n" +
                    "    sb.dsc_subgrupo\n" +
                    "from\n" +
                    "    subgrupo sb\n" +
                    "    join grupo g on\n" +
                    "        sb.cd_grupo = g.cd_grupo\n" +
                    "    join secao s on\n" +
                    "        g.cd_secao = s.cd_secao\n" +
                    "    join departamento d on\n" +
                    "        s.cd_depart = d.cd_depart\n" +
                    "    join segmento seg on\n" +
                    "        d.cd_segmt = seg.cd_segmt\n" +
                    "order by\n" +
                    "    1,3,5,7"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("cd_segmt"));
                    imp.setMerc1Descricao(rst.getString("dsc_segmt"));
                    imp.setMerc2ID(rst.getString("cd_depart"));
                    imp.setMerc2Descricao(rst.getString("dsc_depart"));
                    imp.setMerc3ID(rst.getString("cd_grupo"));
                    imp.setMerc3Descricao(rst.getString("dsc_grupo"));
                    imp.setMerc4ID(rst.getString("cd_subgrupo"));
                    imp.setMerc4Descricao(rst.getString("dsc_subgrupo"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
