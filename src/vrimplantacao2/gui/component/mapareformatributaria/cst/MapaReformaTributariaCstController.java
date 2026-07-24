package vrimplantacao2.gui.component.mapareformatributaria.cst;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import vrframework.classe.Util;
import vrimplantacao2.vo.enums.Cst;
import org.openide.util.Exceptions;
import vrimplantacao.dao.cadastro.CstDAO;
import vrimplantacao2.vo.importacao.MapaReformaTributariaCstIMP;
import vrimplantacao2.gui.component.mapareformatributaria.MapaReformaTributariaDAO;
import vrimplantacao2.gui.component.mapareformatributaria.MapaReformaTributariaProvider;

/**
 *
 * @author Wesley
 */
public class MapaReformaTributariaCstController {

    private String loja;
    private String sistema;
    private List<Cst> cstsVR;
    private final CstDAO cstDao = new CstDAO();
    private List<MapaReformaTributariaCstVO> mapa;
    private MapaReformaTributariaProvider provider;
    private final MapaReformaTributariaCstView view;
    private final MapaReformaTributariaDAO dao = new MapaReformaTributariaDAO();
        
    public MapaReformaTributariaCstController(MapaReformaTributariaCstView view) {
        this.view = view;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public List<Cst> getCstsVR() {
        return cstsVR;
    }

    public List<MapaReformaTributariaCstVO> getMapa() {
        return mapa;
    }

    public MapaReformaTributariaProvider getProvider() {
        return provider;
    }

    public void setProvider(MapaReformaTributariaProvider provider) {
        this.provider = provider;
    }
    
    void atualizarMapa() throws Exception {
        //Cria a tabela do mapeamento cst se ainda não existir.
        dao.createTableReformaTributariaCst();
        
        dao.gravarReformaTributariaCstOrigem(converterMapa(provider.getMapaReformaTributariaCst()));

        dao.vincularCsts(getSistema(), getLoja());

        mapa = dao.getMapaCst(getSistema(), getLoja());

        //Atualiza a view
        view.refresh();
    }
    
    private List<MapaReformaTributariaCstVO> converterMapa(List<MapaReformaTributariaCstIMP> reformaTributariaCst) throws Exception {        
        
//        MultiMap<Comparable, Cst> cstVR = new MultiMap<>();        
//        for (Cst cst: dao.getReformaTributariaCstVR()) {
//            cstVR.put(
//                    cst, 
//                    cst.getCst(),
//                    cst.isGrupoibscbs(),
//                    cst.isGruporeducao(),
//                    cst.isGrupodiferimento(),
//                    cst.isGrupotribregular(),
//                    cst.isGrupoibscbsmono()
//            );
//        }        
        
        List<MapaReformaTributariaCstVO> result = new ArrayList<>();
        for (MapaReformaTributariaCstIMP imp: reformaTributariaCst) {
            MapaReformaTributariaCstVO vo = new MapaReformaTributariaCstVO(
                    getSistema(),
                    getLoja(),
                    imp.getOrigId(),
                    imp.getOrigCst(),
                    imp.getOrigDescricao(),
                    imp.isGrupoIbsCbs(),
                    imp.isGrupoReducao(),
                    imp.isGrupoDiferimento(),
                    imp.isGrupoTribRegular(),
                    imp.isGrupoIbsCbsMono()
            );            
            
            result.add(vo);
        }
        return result;
    }
    
    public void incluirCst() {        
        int mapaIndex = view.tblMapa.getLinhaSelecionada();
        MapaReformaTributariaCstVO map = this.mapa.get(mapaIndex);
        
        try {
            cstDao.insert(map.converterEmVo());
        } catch(Exception e) {
            System.out.println("Erro ao tentar incluir CST");
        }
    }
    
    public void incluirTodasCst() {

        for (Iterator<MapaReformaTributariaCstVO> it = mapa.iterator(); it.hasNext();) {
            MapaReformaTributariaCstVO map = it.next();
            if (map.getCst() == null) {
                try {
                    cstDao.insert(map.converterEmVo());
                } catch (Exception e) {
                    System.out.println("Erro ao incluir CST: " + map.getOrigDescricao());
                }
            }
        }
    }
    
    void buscar(String texto) {
        try {
            cstsVR = dao.getCstVR(texto);
            
            //Atualiza a view
            view.refreshBusca();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, "Erro ao gravar a CST");
        }
    }
    
    void gravarCst() throws Exception {
        if (view.tblVR.getRowCount() > 0 && view.tblMapa.getRowCount() > 0) {
            
            Cst cst = this.cstsVR.get(view.tblVR.getLinhaSelecionada());
            int mapaIndex = view.tblMapa.getLinhaSelecionada();
            MapaReformaTributariaCstVO map = this.mapa.get(mapaIndex);
            
            map.setCst(cst);
            
            dao.gravarMapaCst(map);            
            
            view.refresh();
            if (mapaIndex < view.tblMapa.getRowCount() - 1) {
                view.tblMapa.setLinhaSelecionada(mapaIndex + 1);
            } else {
                view.tblMapa.setLinhaSelecionada(view.tblMapa.getRowCount() - 1);
            }           
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
}
