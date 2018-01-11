package vrimplantacao2.gui.component.mapatiporecebiveis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao2.vo.cadastro.financeiro.TipoRecebivelVO;
import vrimplantacao2.vo.importacao.MapaTipoRecebivelIMP;

/**
 * Classe controller do componente
 * @author Leandro
 */
public class MapaTipoRecebiveisController {
    
    private final MapaTipoRecebiveisView view;
    private FinanceiroProvider provider;
    private MapaTipoRecebivelDAO dao = new MapaTipoRecebivelDAO();
    private List<MapaTipoRecebivelVO> mapa = new ArrayList<>();   
    private List<TipoRecebivelVO> recebiveis = new ArrayList<>();

    public void setProvider(FinanceiroProvider provider) {
        this.provider = provider;
    }

    public List<MapaTipoRecebivelVO> getMapa() {
        return mapa;
    }

    public MapaTipoRecebiveisController(MapaTipoRecebiveisView view) {
        this.view = view;        
    }

    public void gravar() throws Exception {
        if (view.tblVR.getRowCount() > 0 && view.tblMapa.getRowCount() > 0) {
            TipoRecebivelVO tipoRecebivel = this.recebiveis.get(view.tblVR.getLinhaSelecionada());
            int mapaIndex = view.tblMapa.getLinhaSelecionada();
            MapaTipoRecebivelVO map = this.mapa.get(mapaIndex);
            
            map.setCodigoatual(tipoRecebivel);
            
            dao.gravarMapa(map);            
            
            view.refresh();
            if (mapaIndex < view.tblMapa.getRowCount() - 1) {
                view.tblMapa.setLinhaSelecionada(mapaIndex + 1);
            } else {
                view.tblMapa.setLinhaSelecionada(view.tblMapa.getRowCount() - 1);
            }           
        }
    }

    public void nextVR() {
        int index = view.tblVR.getLinhaSelecionada() + 1;
        if (index < view.tblVR.getRowCount()) {
            view.tblVR.setLinhaSelecionada(index);
        }
    }

    public void previousVR() {
        int index = view.tblVR.getLinhaSelecionada() - 1;
        if (index >= 0) {
            view.tblVR.setLinhaSelecionada(index);
        }
    }

    public void buscar(String text) throws Exception {
        recebiveis = dao.getTipoRecebiveis(text);
        view.refreshBusca();
    }

    public void atualizarMapa() throws Exception {
        //Cria a tabela do mapeamento se ainda não existir.
        dao.createTable();
        List<MapaTipoRecebivelVO> convertido = converterMapa(provider.getTipoRecebiveis());
        dao.gravarTipoRecebivelOrigem(convertido);
        //Retorna o mapa
        mapa = dao.getMapa(provider.getSistema(), provider.getLojaOrigem());
        //Atualiza a view
        view.refresh();
    }

    private List<MapaTipoRecebivelVO> converterMapa(List<MapaTipoRecebivelIMP> tipoRecebiveis) {
        Map<String, MapaTipoRecebivelVO> result = new LinkedHashMap<>();
        for (MapaTipoRecebivelIMP imp: tipoRecebiveis) {
            MapaTipoRecebivelVO vo = new MapaTipoRecebivelVO();
            vo.setSistema(provider.getSistema());
            vo.setAgrupador(provider.getLojaOrigem());
            vo.setId(imp.getId());
            vo.setDescricao(imp.getDescricao());
            result.put(vo.getId(), vo);
        }
        return new ArrayList<>(result.values());
    }

    List<TipoRecebivelVO> getTiposRecebiveis() throws Exception {
        return recebiveis;
    }

    void incluir() throws Exception {
        int mapaIndex = view.tblMapa.getLinhaSelecionada();
        MapaTipoRecebivelVO map = this.mapa.get(mapaIndex);
   
        TipoRecebivelVO tipoRecebivel = new TipoRecebivelVO();
        tipoRecebivel.setDescricao(map.getDescricao());
        dao.gravarTipoRecebivel(tipoRecebivel);

        map.setCodigoatual(tipoRecebivel);

        dao.gravarMapa(map);            

        view.refresh();
        if (mapaIndex < view.tblMapa.getRowCount() - 1) {
            view.tblMapa.setLinhaSelecionada(mapaIndex + 1);
        } else {
            view.tblMapa.setLinhaSelecionada(view.tblMapa.getRowCount() - 1);
        }
    }
    
}
