package vrimplantacao2.gui.component.mapatiporecebiveis;

import java.util.List;
import vrimplantacao2.vo.importacao.MapaTipoRecebivelIMP;

/**
 * Interface que define os campos de um Provider para importação do Financeiro.
 * 
 * @author Leandro
 */
public interface FinanceiroProvider {
    /**
     * Retorna uma listagem com os tipos de recebiveis para mapeamento.
     * @return Listagem de Tipo Recebíveis.
     * @throws Exception 
     */
    public List<MapaTipoRecebivelIMP> getTipoRecebiveis() throws Exception;
    
    /**
     * Sistema que esta sendo importado.
     * @return 
     */
    public String getSistema();
    
    /**
     * Retorna a loja de origem.
     * @return 
     */
    public String getLojaOrigem();
}
