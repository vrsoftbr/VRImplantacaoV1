package vrimplantacao2.gui.component.mapatributacao;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author Leandro
 */
public class MapaTributacaoController {

    private MapaTributacaoView view;
    private MapaTributacaoDAO dao = new MapaTributacaoDAO();
    private List<MapaTributoVO> mapa;
    private String sistema;
    private String agrupador;
    private MapaTributoProvider provider;
    private List<Icms> aliquotas;

    void setProvider(MapaTributoProvider provider) {
        this.provider = provider;
    }

    MapaTributoProvider getProvider() {
        return provider;
    }
    
    String getSistema() {
        return sistema;
    }

    void setSistema(String sistema) {
        this.sistema = sistema;
    }

    String getAgrupador() {
        return agrupador;
    }

    void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }

    MapaTributacaoController(MapaTributacaoView view) {
        this.view = view;
    }

    public MapaTributacaoView getView() {
        return view;
    }

    List<MapaTributoVO> getMapa() {
        return mapa;
    }

    List<Icms> getAliquotas() {
        return aliquotas;
    }

    void atualizarMapa() throws Exception {
        //Cria a tabela do mapeamento se ainda não existir.
        dao.createTable();
        List<MapaTributoVO> convertido = converterMapa(provider.getTributacao());       
        dao.gravarTributacaoOrigem(convertido);
        //Retorna o mapa
        mapa = dao.getMapa(getSistema(), getAgrupador());
        //Atualiza a view
        view.refresh();
    }

    void buscar(String texto) throws Exception {
        aliquotas = dao.getTributacaoVR(texto);
        //Atualiza a view
        view.refreshBusca();
    }

    void previousTributVR() throws Exception {
        int index = view.tblVR.getLinhaSelecionada() - 1;
        if (index >= 0) {
            view.tblVR.setLinhaSelecionada(index);
        }
    }

    void nextTributVR() {
        int index = view.tblVR.getLinhaSelecionada() + 1;
        if (index < view.tblVR.getRowCount()) {
            view.tblVR.setLinhaSelecionada(index);
        }
    }

    private List<MapaTributoVO> converterMapa(List<MapaTributoIMP> tributacao) throws Exception {        
        
        MultiMap<Comparable, Icms> tributacaoVR = new MultiMap<>();        
        for (Icms icms: dao.getTributacaoVR()) {
            tributacaoVR.put(icms, icms.getCst(), icms.getAliquota(), icms.getReduzido());
        }        
        
        List<MapaTributoVO> result = new ArrayList<>();
        for (MapaTributoIMP imp: tributacao) {
            MapaTributoVO vo = new MapaTributoVO();
            vo.setSistema(getSistema());
            vo.setAgrupador(getAgrupador());
            vo.setOrigId(imp.getId());
            vo.setOrigDescricao(imp.getDescricao());
                        
            vo.setAliquota(tributacaoVR.get(imp.getCst(), imp.getAliquota(), imp.getReduzido()));
            
            result.add(vo);
        }
        return result;
    }

    void gravarTributo() throws Exception {
        if (view.tblVR.getRowCount() > 0 && view.tblMapa.getRowCount() > 0) {
            Icms aliquota = this.aliquotas.get(view.tblVR.getLinhaSelecionada());
            int mapaIndex = view.tblMapa.getLinhaSelecionada();
            MapaTributoVO map = this.mapa.get(mapaIndex);
            
            map.setAliquota(aliquota);
            
            dao.gravarMapa(map);            
            
            view.refresh();
            if (mapaIndex < view.tblMapa.getRowCount() - 1) {
                view.tblMapa.setLinhaSelecionada(mapaIndex + 1);
            } else {
                view.tblMapa.setLinhaSelecionada(view.tblMapa.getRowCount() - 1);
            }           
        }
    }

}
