package vrimplantacao2.gui.component.mapareformatributaria.classificacaotributaria;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import vrframework.classe.Util;
import org.openide.util.Exceptions;
import vrimplantacao2.vo.enums.ClassificacaoTributaria;
import vrimplantacao.dao.cadastro.ClassificacaoTributariaDAO;
import vrimplantacao2.vo.importacao.MapaReformaTributariaClassificacaoIMP;
import vrimplantacao2.gui.component.mapareformatributaria.MapaReformaTributariaDAO;
import vrimplantacao2.gui.component.mapareformatributaria.MapaReformaTributariaProvider;
import vrimplantacao2.gui.component.mapareformatributaria.cst.MapaReformaTributariaCstVO;

/**
 *
 * @author Wesley
 */
public class MapaReformaTributariaClassificacaoController {

    private String loja;
    private String sistema;
    private MapaReformaTributariaProvider provider;
    private List<MapaReformaTributariaCstVO> mapaCst;
    private List<ClassificacaoTributaria> classificacoesVR;
    private List<MapaReformaTributariaClassificacaoVO> mapa;
    private final MapaReformaTributariaClassificacaoView view;
    private final MapaReformaTributariaDAO dao = new MapaReformaTributariaDAO();
    private final ClassificacaoTributariaDAO classificacaoDao = new ClassificacaoTributariaDAO();
        
    public MapaReformaTributariaClassificacaoController(MapaReformaTributariaClassificacaoView view) {
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

    public List<ClassificacaoTributaria> getClassificacaoVR() {
        return classificacoesVR;
    }

    public List<MapaReformaTributariaClassificacaoVO> getMapa() {
        return mapa;
    }

    public MapaReformaTributariaProvider getProvider() {
        return provider;
    }

    public void setProvider(MapaReformaTributariaProvider provider) {
        this.provider = provider;
    }
    
    public void setMapaCst() throws Exception {
        mapaCst = dao.getMapaCst(getSistema(), getLoja());
    }
    
    void atualizarMapa() throws Exception {
        //Cria a tabela do mapeamento cst se ainda não existir.

        dao.createTableReformaTributariaClassificacao();
        
        dao.gravarReformaTributariaClassificacaoOrigem(converterMapa(provider.getMapaReformaTributariaClassificacao()));

        try {
            dao.vincularClassificacoes(getSistema(), getLoja());
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        
        mapa = dao.getMapaClassificacao(getSistema(), getLoja());

        //Atualiza a view
        view.refresh();
    }
    
    private List<MapaReformaTributariaClassificacaoVO> converterMapa(List<MapaReformaTributariaClassificacaoIMP> reformaTributariaClassificacao) throws Exception {        
        
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
        
        List<MapaReformaTributariaClassificacaoVO> result = new ArrayList<>();
        for (MapaReformaTributariaClassificacaoIMP imp: reformaTributariaClassificacao) {
            MapaReformaTributariaClassificacaoVO vo = new MapaReformaTributariaClassificacaoVO(
                    getSistema(),
                    getLoja(),
                    imp.getOrigId(),
                    imp.getId_cstIbsCbs(),
                    imp.getOrigReducao(),
                    imp.getOrigDiferimento(),
                    imp.getCclasstrib(),
                    imp.getDescricao(),
                    imp.getFundamentacaoLegal(),
                    imp.isAliquotaZero()
            );         
            
            result.add(vo);
        }
        return result;
    }
    
    public void incluirClassificacao() throws Exception {        
        int mapaIndex = view.tblMapa.getLinhaSelecionada();
        MapaReformaTributariaClassificacaoVO map = this.mapa.get(mapaIndex);
        
        classificacaoDao.insert(map.converterEmVo(mapaCst));
    }
    
    public void incluirTodasClassificacoes() {

        for (Iterator<MapaReformaTributariaClassificacaoVO> it = mapa.iterator(); it.hasNext();) {
            MapaReformaTributariaClassificacaoVO map = it.next();
            if (map.getClassificacao()== null) {
                try {
                    classificacaoDao.insert(map.converterEmVo(mapaCst));
                } catch (Exception e) {
                    System.out.println("Erro ao incluir CST: " + map.getOrigDescricao());
                }
            }
        }
    }
    
    void buscar(String classificacao, String reducao, String diferimento) {
        try {
            classificacoesVR = dao.getClassificacoesVR(classificacao, reducao, diferimento);
            
            //Atualiza a view
            view.refreshBusca();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, "Erro ao gravar a CST");
        }
    }
    
    void gravarClassificacao() throws Exception {
        if (view.tblVR.getRowCount() > 0 && view.tblMapa.getRowCount() > 0) {
            
            ClassificacaoTributaria classificacaoTributaria = this.classificacoesVR.get(view.tblVR.getLinhaSelecionada());
            int mapaIndex = view.tblMapa.getLinhaSelecionada();
            MapaReformaTributariaClassificacaoVO map = this.mapa.get(mapaIndex);
            
            map.setClassificacao(classificacaoTributaria);
            
            dao.gravarMapaClassificacao(map);            
            
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
