package vrimplantacao2.gui.component.mapatributacao;

import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrimplantacao2.gui.component.mapatributacao.incluiraliquota.IncluirAliquotaGUI;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author Leandro
 */
public class MapaTributacaoController {

    private final MapaTributacaoView view;
    private final MapaTributacaoDAO dao = new MapaTributacaoDAO();
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
        dao.gravarTributacaoOrigem(converterMapa(provider.getTributacao()));
        dao.vincularAliquotas(getSistema(), getAgrupador());
        mapa = dao.getMapa(getSistema(), getAgrupador());
        /*
        List<MapaTributoVO> convertido = converterMapa(provider.getTributacao());       
        dao.gravarTributacaoOrigem(convertido);
        //Retorna o mapa
        mapa = dao.getMapa(getSistema(), getAgrupador());
                */
        //Atualiza a view
        view.refresh();
    }

    void buscar(String texto) {
        try {
            aliquotas = dao.getTributacaoVR(texto);
            //Atualiza a view
            view.refreshBusca();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, "Erro ao gravar a aliquota");
        }
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
            vo.setOrigCst(imp.getCst());
            vo.setOrigAliquota(imp.getAliquota());
            vo.setOrigReduzido(imp.getReduzido());
            vo.setOrigFcp(imp.getFcp());
            vo.setOrigDesonerado(imp.isDesonerado());
            vo.setOrigPorcentagemDesonerado(imp.getPorcentagemDesonerado());
            
            int cst;
            if (imp.getCst() == 10 || imp.getCst() == 30 || imp.getCst() == 70) {
                cst = 60;
            } else {
                cst = imp.getCst();
            }
            double aliquota = 
                    imp.getCst() == 10 || imp.getCst() == 30 || imp.getCst() == 40 || imp.getCst() == 60 || imp.getCst() == 70 ?
                    0 :
                    imp.getAliquota();
            double reduzido = imp.getCst() != 20 ? 0 : imp.getReduzido();
            
            //vo.setAliquota(tributacaoVR.get(cst, aliquota, reduzido));
            
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

    public void incluirTributo() {        
        int mapaIndex = view.tblMapa.getLinhaSelecionada();
        MapaTributoVO map = this.mapa.get(mapaIndex);
        
        int id = IncluirAliquotaGUI.exibir(map.converterEmVo());
        if (id > 0) {
            buscar("@" + String.valueOf(id));
        }
    }

}
