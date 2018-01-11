package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Leandro
 */
public class ContechDAO extends InterfaceDAO {
    
    private String listGeral;
    private String listDepart;
    private String listPisCofins;

    public void setListGeral(String listGeral) {
        this.listGeral = listGeral;
    }

    public void setListDepart(String listDepart) {
        this.listDepart = listDepart;
    }

    public void setListPisCofins(String listPisCofins) {
        this.listPisCofins = listPisCofins;
    }

    @Override
    public String getSistema() {
        return "Contech";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        
        Arquivo arq = ArquivoFactory.getArquivo(this.listDepart, null);
        
        Map<String, String> departamentos = new LinkedHashMap<>();
        
        for (LinhaArquivo ln: arq) {
            String value = ln.getString("VALOR");
            
            String codigo = value.substring(0, 6);
            //Verifica se a linha é um registro válido.
            if (codigo != null && codigo.matches(" [0-9]{5}")) {
                String id = value.substring(62, 64);
                String descricao = value.substring(65, 77);
                
                departamentos.put(id, descricao);
            }
            //Se for joga no map para organizar o mercadológico.
        }
        
        List<MercadologicoIMP> result = new ArrayList<>();
        
        for (String key: departamentos.keySet()) {
            MercadologicoIMP imp = new MercadologicoIMP();
            imp.setImportSistema(getSistema());
            imp.setImportLoja(getLojaOrigem());
            imp.setMerc1ID(key);
            imp.setMerc1Descricao(departamentos.get(key));
            result.add(imp);
        }
        
        return result;
    }
    
    
    
}
