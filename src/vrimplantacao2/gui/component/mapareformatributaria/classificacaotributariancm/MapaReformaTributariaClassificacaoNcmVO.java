package vrimplantacao2.gui.component.mapareformatributaria.classificacaotributariancm;

import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import vrimplantacao2.gui.component.mapareformatributaria.classificacaotributaria.MapaReformaTributariaClassificacaoVO;
import vrimplantacao2.vo.enums.ClassificacaoTributariaNcm;
import vrimplantacao2.vo.cadastro.reformatributaria.ClassificacaoTributariaNcmVO;
import vrimplantacao2.vo.enums.NcmVO;

/**
 *
 * @author wesley
 */
public class MapaReformaTributariaClassificacaoNcmVO {

    private String sistema;
    private String loja;
    private String origId;
    private String origIdClassificacao;
    private int origNcm1;
    private int origNcm2;
    private int origNcm3;
    private ClassificacaoTributariaNcm classificacaoNcm;

    public MapaReformaTributariaClassificacaoNcmVO(String sistema, String loja, String origId, String origIdClassificacao, int origNcm1, int origNcm2, int origNcm3) {
        this.sistema = sistema;
        this.loja = loja;
        this.origId = origId;
        this.origIdClassificacao = origIdClassificacao;
        this.origNcm1 = origNcm1;
        this.origNcm2 = origNcm2;
        this.origNcm3 = origNcm3;
    }

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public String getOrigId() {
        return origId;
    }

    public String getOrigIdClassificacao() {
        return origIdClassificacao;
    }

    public int getOrigNcm1() {
        return origNcm1;
    }

    public int getOrigNcm2() {
        return origNcm2;
    }

    public int getOrigNcm3() {
        return origNcm3;
    }

    public ClassificacaoTributariaNcm getClassificacaoNcm() {
        return classificacaoNcm;
    }

    public void setClassificacaoNcm(ClassificacaoTributariaNcm classificacaoNcm) {
        this.classificacaoNcm = classificacaoNcm;
    }

    public ClassificacaoTributariaNcmVO converterEmVo(List<MapaReformaTributariaClassificacaoVO> listaClassificacao, int lojaVR, NcmVO ncm) throws Exception {

        Map<String, MapaReformaTributariaClassificacaoVO> mapaClassificacao = listaClassificacao.stream()
                .collect(Collectors.toMap(
                        MapaReformaTributariaClassificacaoVO::getOrigId,
                        Function.identity()
                ));

        MapaReformaTributariaClassificacaoVO mapa = mapaClassificacao.get(this.getOrigIdClassificacao());

        if (mapa == null || mapa.getClassificacao() == null) {
            throw new Exception(
                    "Erro ao tentar incluir a CLASSIFICAÇÃO TRIBUTARIA NCM de origem ID " + getOrigId() + "\n"
                    + "Classificação de ID Origem " + getOrigIdClassificacao() + "' não encontrada.\n"
                    + "Verifique o Mapa de Classificação Tributária."
            );
        }

        if (ncm == null) {
            throw new Exception(
                    "NCM não encontrado - " + origNcm1 + " - " + origNcm2 + " - " + origNcm3
            );
        }

        return new ClassificacaoTributariaNcmVO(
                mapa.getClassificacao().getId(),
                (int) ncm.getId(),
                ncm.getNcm1(),
                ncm.getNcm2(),
                ncm.getNcm3(),
                lojaVR
        );
    }
}
