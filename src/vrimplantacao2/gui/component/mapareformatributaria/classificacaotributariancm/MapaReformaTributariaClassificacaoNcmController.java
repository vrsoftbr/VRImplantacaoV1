package vrimplantacao2.gui.component.mapareformatributaria.classificacaotributariancm;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import vrframework.classe.Util;
import org.openide.util.Exceptions;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.vo.enums.ClassificacaoTributariaNcm;
import vrimplantacao.dao.cadastro.ClassificacaoTributariaNcmDAO;
import vrimplantacao2.vo.importacao.MapaReformaTributariaClassificacaoNcmIMP;
import vrimplantacao2.gui.component.mapareformatributaria.MapaReformaTributariaDAO;
import vrimplantacao2.gui.component.mapareformatributaria.MapaReformaTributariaProvider;
import vrimplantacao2.gui.component.mapareformatributaria.classificacaotributaria.MapaReformaTributariaClassificacaoVO;

/**
 *
 * @author Wesley
 */
public class MapaReformaTributariaClassificacaoNcmController {

    private int lojaVR;
    private String loja;
    private String sistema;
    private boolean exibirTodos = false;
    private List<NcmVO> ncmsVR = new ArrayList<>();
    private MapaReformaTributariaProvider provider;
    private List<MapaReformaTributariaClassificacaoNcmVO> mapa;
    private List<ClassificacaoTributariaNcm> classificacaoNcmVR;
    private final MapaReformaTributariaClassificacaoNcmView view;
    private List<MapaReformaTributariaClassificacaoVO> mapaClassificacao;
    private final MapaReformaTributariaDAO dao = new MapaReformaTributariaDAO();
    private final ClassificacaoTributariaNcmDAO classificacaoNcmDao = new ClassificacaoTributariaNcmDAO();

    public MapaReformaTributariaClassificacaoNcmController(MapaReformaTributariaClassificacaoNcmView view) {
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

    public int getLojaVR() {
        return lojaVR;
    }

    public void setLojaVR(int LojaVR) {
        this.lojaVR = LojaVR;
    }

    public boolean isExibirTodos() {
        return exibirTodos;
    }

    public void setExibirTodos(boolean exibirTodos) {
        this.exibirTodos = exibirTodos;
    }

    public List<NcmVO> getNcmsVR() {
        return ncmsVR;
    }

    public List<ClassificacaoTributariaNcm> getClassificacaoNcmVR() {
        return classificacaoNcmVR;
    }

    public List<MapaReformaTributariaClassificacaoNcmVO> getMapa() {
        return mapa;
    }

    public MapaReformaTributariaProvider getProvider() {
        return provider;
    }

    public void setProvider(MapaReformaTributariaProvider provider) {
        this.provider = provider;
    }

    public void setMapaClassificacao() {
        try {
            this.mapaClassificacao = dao.getMapaClassificacao(getSistema(), getLoja());
        } catch (Exception e) {
            System.err.println("Erro ao carregar o mapa de CST");
        }
    }

    void atualizarMapa() throws Exception {
        //Cria a tabela do mapeamento cst se ainda não existir.

        dao.createTableReformaTributariaClassificacaoNcm();

        dao.gravarReformaTributariaClassificacaoNcmOrigem(converterMapa(provider.getMapaReformaTributariaClassificacaoNcm()));

        try {
            dao.vincularClassificacoesNcm(getSistema(), getLoja(), lojaVR);
        } catch (Exception e) {
            e.printStackTrace();
            Exceptions.printStackTrace(e);
        }

        mapa = dao.getMapaClassificacaoNcm(getSistema(), getLoja(), this.exibirTodos);

        //Atualiza a view
        view.refresh();
    }

    private List<MapaReformaTributariaClassificacaoNcmVO> converterMapa(List<MapaReformaTributariaClassificacaoNcmIMP> reformaTributariaClassificacaoNcm) throws Exception {

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
        List<MapaReformaTributariaClassificacaoNcmVO> result = new ArrayList<>();
        for (MapaReformaTributariaClassificacaoNcmIMP imp : reformaTributariaClassificacaoNcm) {
            MapaReformaTributariaClassificacaoNcmVO vo = new MapaReformaTributariaClassificacaoNcmVO(
                    getSistema(),
                    getLoja(),
                    imp.getOrigId(),
                    imp.getOrigClassificacao(),
                    imp.getOrigNcm1(),
                    imp.getOrigNcm2(),
                    imp.getOrigNcm3()
            );

            result.add(vo);
        }
        return result;
    }

//    public void incluirClassificacao() throws Exception {        
//        int mapaIndex = view.tblMapa.getLinhaSelecionada();
//        MapaReformaTributariaClassificacaoVO map = this.mapa.get(mapaIndex);
//        
//        classificacaoDao.insert(map.converterEmVo(mapaCst));
//    }
    public void incluirTodasClassificacoesNcm() {

        int count = 0;
        
        for (Iterator<MapaReformaTributariaClassificacaoNcmVO> it = mapa.iterator(); it.hasNext();) {

            MapaReformaTributariaClassificacaoNcmVO map = it.next();
            if (map.getClassificacaoNcm() == null) {
                try {

                    NcmVO ncm = dao.getNcm(map.getOrigNcm1(), map.getOrigNcm2(), map.getOrigNcm3());

                    classificacaoNcmDao.insert(map.converterEmVo(mapaClassificacao, this.lojaVR, ncm));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Erro ao incluir classificação tributação Ncm: " + map.getOrigId());
                }
            }
            
            count++;
            System.err.println(count);
        }
    }

    void buscar(String ncm1, String ncm2, String ncm3) {
        try {
            ncmsVR = dao.getNcms(
                    ncm1.isEmpty() ? null : Integer.parseInt(ncm1),
                    ncm2.isEmpty() ? null : Integer.parseInt(ncm2),
                    ncm3.isEmpty() ? null : Integer.parseInt(ncm3)
            );

            view.refreshBusca();

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, "Erro ao buscar NCM");
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
