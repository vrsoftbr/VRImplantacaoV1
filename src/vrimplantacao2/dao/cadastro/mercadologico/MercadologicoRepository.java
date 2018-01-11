package vrimplantacao2.dao.cadastro.mercadologico;

import java.util.Collection;
import java.util.List;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoAnteriorVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;

/**
 *
 * @author Leandro
 */
public class MercadologicoRepository {
    
    private final MercadologicoRepositoryProvider provider;
    private boolean gerarNiveisComoSubniveis = false;

    public MercadologicoRepository(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this(new MercadologicoRepositoryProvider(
                sistema,
                lojaOrigem,
                lojaVR
        ));
    }

    public void setGerarNiveisComoSubniveis(boolean gerarNiveisComoSubniveis) {
        this.gerarNiveisComoSubniveis = gerarNiveisComoSubniveis;
    }
    
    public MercadologicoRepository(MercadologicoRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }

    public void salvar(List<MercadologicoNivelIMP> mercadologicos) throws Exception {
        provider.setStatus("Gravando mercadológicos...");
        try {
            provider.begin();
            provider.excluir();
            int nivelMaximo = getNivelMaximo(mercadologicos);
            if (nivelMaximo < 3) {
                nivelMaximo = 3;
            }
            for (MercadologicoNivelIMP merc: mercadologicos) {
                salvar(null, merc, 1, nivelMaximo);
            }
            provider.gerarAAcertar(nivelMaximo);
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }
    
    public void salvar(MercadologicoVO pai, MercadologicoNivelIMP merc, int nivel, int nivelMaximo) throws Exception {
        MercadologicoVO vo = converterMercadologico(pai, merc, nivel);
        gravarMercadologico(vo);
        
        MercadologicoAnteriorVO ant = converterMercadologicoAnterior(merc, vo);
        gravarMercadologico(ant);
        
        if (!merc.getNiveis().isEmpty()) {
            if (this.gerarNiveisComoSubniveis) {
                completarSubNiveis(vo, nivel, nivelMaximo);
            }
            for (MercadologicoNivelIMP imp: merc.getNiveis().values()) {
                salvar(vo, imp, nivel + 1, nivelMaximo);
            }
        } else {
            if (nivelMaximo > nivel) {
                completarSubNiveis(vo, nivel, nivelMaximo);
            }
        }
    }

    public MercadologicoAnteriorVO converterMercadologicoAnterior(MercadologicoNivelIMP merc, MercadologicoVO vo) {
        MercadologicoAnteriorVO ant = new MercadologicoAnteriorVO();
        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLojaOrigem());      
        
        if (vo.getNivel() == 1) {
            ant.setAntMerc1(merc.getId());
        } else if (vo.getNivel() == 2) {
            ant.setAntMerc1(merc.getMercadologicoPai().getId());
            ant.setAntMerc2(merc.getId());
        } else if (vo.getNivel() == 3) {
            ant.setAntMerc1(merc.getMercadologicoPai().getMercadologicoPai().getId());            
            ant.setAntMerc2(merc.getMercadologicoPai().getId());
            ant.setAntMerc3(merc.getId());
        } else if (vo.getNivel() == 4) {
            ant.setAntMerc1(merc.getMercadologicoPai().getMercadologicoPai().getMercadologicoPai().getId());
            ant.setAntMerc2(merc.getMercadologicoPai().getMercadologicoPai().getId());
            ant.setAntMerc3(merc.getMercadologicoPai().getId());
            ant.setAntMerc4(merc.getId());
        } else if (vo.getNivel() == 5) {
            ant.setAntMerc1(merc.getMercadologicoPai().getMercadologicoPai().getMercadologicoPai().getMercadologicoPai().getId());
            ant.setAntMerc2(merc.getMercadologicoPai().getMercadologicoPai().getMercadologicoPai().getId());
            ant.setAntMerc3(merc.getMercadologicoPai().getMercadologicoPai().getId());
            ant.setAntMerc4(merc.getMercadologicoPai().getId());
            ant.setAntMerc5(merc.getId());
        }
        ant.setMerc1(vo.getMercadologico1());
        ant.setMerc2(vo.getMercadologico2());
        ant.setMerc3(vo.getMercadologico3());
        ant.setMerc4(vo.getMercadologico4());
        ant.setMerc5(vo.getMercadologico5());
        ant.setDescricao(merc.getDescricao());
        ant.setNivel(vo.getNivel());
        return ant;
    }
    
    
    public void gravarMercadologico(MercadologicoVO vo) throws Exception {
        provider.gravarMercadologico(vo);
    }
    
    public void gravarMercadologico(MercadologicoAnteriorVO vo) throws Exception {
        provider.gravarMercadologico(vo);
    }

    public int getNivelMaximo(Collection<MercadologicoNivelIMP> mercadologicos) {
        if (!mercadologicos.isEmpty()) {
            int nv = -1;
            for (MercadologicoNivelIMP m: mercadologicos) {
                int val = getNivelMaximo(m.getNiveis().values());
                if (val > nv) {
                    nv = val;
                }
            }
            return nv + 1;
        } else {
            return 0;
        }
    }


    public MercadologicoVO converterMercadologico(MercadologicoVO pai, MercadologicoNivelIMP merc, int nivel) throws Exception {
        MercadologicoVO vo = new MercadologicoVO();
        vo.setDescricao(merc.getDescricao());
        vo.setNivel(nivel);
        if (nivel == 1) {
            vo.setMercadologico1(provider.getNextMercadologico1());
        } else if (nivel == 2) {
            vo.setMercadologico1(pai.getMercadologico1());
            vo.setMercadologico2(provider.getNextMercadologico2(
                    pai.getMercadologico1()
            ));
        } else if (nivel == 3) {
            vo.setMercadologico1(pai.getMercadologico1());
            vo.setMercadologico2(pai.getMercadologico2());
            vo.setMercadologico3(provider.getNextMercadologico3(
                    pai.getMercadologico1(),
                    pai.getMercadologico2()
            ));
        } else if (nivel == 4) {
            vo.setMercadologico1(pai.getMercadologico1());
            vo.setMercadologico2(pai.getMercadologico2());
            vo.setMercadologico3(pai.getMercadologico3());
            vo.setMercadologico4(provider.getNextMercadologico4(
                    pai.getMercadologico1(),
                    pai.getMercadologico2(),
                    pai.getMercadologico3()
            ));
        } else if (nivel == 5) {
            vo.setMercadologico1(pai.getMercadologico1());
            vo.setMercadologico2(pai.getMercadologico2());
            vo.setMercadologico3(pai.getMercadologico3());
            vo.setMercadologico4(pai.getMercadologico4());
            vo.setMercadologico5(provider.getNextMercadologico5(
                    pai.getMercadologico1(),
                    pai.getMercadologico2(),
                    pai.getMercadologico3(),
                    pai.getMercadologico4()
            ));
        }
        return vo;
    }    

    public void completarSubNiveis(MercadologicoVO vo, int nivel, int nivelMaximo) throws Exception {
        int i = nivel + 1; 
        if (i <= nivelMaximo) {
            MercadologicoVO merc = new MercadologicoVO();

            if (i == 2) {
                merc.setMercadologico1(vo.getMercadologico1());
                merc.setMercadologico2(1);                
            } else if (i == 3) {
                merc.setMercadologico1(vo.getMercadologico1());
                merc.setMercadologico2(vo.getMercadologico2());
                merc.setMercadologico3(1);
            } else if (i == 4) {
                merc.setMercadologico1(vo.getMercadologico1());
                merc.setMercadologico2(vo.getMercadologico2());
                merc.setMercadologico3(vo.getMercadologico3());
                merc.setMercadologico4(1);
            } else if (i == 5) {
                merc.setMercadologico1(vo.getMercadologico1());
                merc.setMercadologico2(vo.getMercadologico2());
                merc.setMercadologico3(vo.getMercadologico3());
                merc.setMercadologico4(vo.getMercadologico4());
                merc.setMercadologico5(1);
            }

            merc.setDescricao(vo.getDescricao());
            merc.setNivel(i);

            gravarMercadologico(merc);
            
            completarSubNiveis(merc, i, nivelMaximo);
        }        
    }
    
}
