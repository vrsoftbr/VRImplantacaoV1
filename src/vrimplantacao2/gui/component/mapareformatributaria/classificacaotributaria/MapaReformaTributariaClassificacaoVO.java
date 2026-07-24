package vrimplantacao2.gui.component.mapareformatributaria.classificacaotributaria;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import vrimplantacao2.vo.enums.ClassificacaoTributaria;
import vrimplantacao2.vo.cadastro.reformatributaria.ClassificacaoTributariaVO;
import vrimplantacao2.gui.component.mapareformatributaria.cst.MapaReformaTributariaCstVO;

/**
 *
 * @author wesley
 */
public class MapaReformaTributariaClassificacaoVO {
    
    private String sistema;
    private String loja;
    private String origId;
    private String origIdCstIbsCbs;
    private Double origReducao;
    private Double origDiferimento;
    private String origCclasstrib;
    private String origDescricao;
    private String origFundamentacaoLegal;
    private boolean origAliquotaZero;
    private ClassificacaoTributaria classificacao; 

    public MapaReformaTributariaClassificacaoVO(String sistema, String loja, String origId, String origIdCstIbsCbs, Double origReducao, Double origDiferimento, String origCclasstrib, String origDescricao, String origFundamentacaoLegal, boolean origAliquotaZero) {
        this.sistema = sistema;
        this.loja = loja;
        this.origId = origId;
        this.origIdCstIbsCbs = origIdCstIbsCbs;
        this.origReducao = origReducao;
        this.origDiferimento = origDiferimento;
        this.origCclasstrib = origCclasstrib;
        this.origDescricao = origDescricao;
        this.origFundamentacaoLegal = origFundamentacaoLegal;
        this.origAliquotaZero = origAliquotaZero;
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

    public String getOrigIdCstIbsCbs() {
        return origIdCstIbsCbs;
    }

    public Double getOrigReducao() {
        return origReducao;
    }

    public Double getOrigDiferimento() {
        return origDiferimento;
    }

    public String getOrigCclasstrib() {
        return origCclasstrib;
    }

    public String getOrigDescricao() {
        return origDescricao;
    }

    public String getOrigFundamentacaoLegal() {
        return origFundamentacaoLegal;
    }

    public boolean isOrigAliquotaZero() {
        return origAliquotaZero;
    }

    public ClassificacaoTributaria getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(ClassificacaoTributaria classificacao) {
        this.classificacao = classificacao;
    }
    
        
    public ClassificacaoTributariaVO converterEmVo(List<MapaReformaTributariaCstVO> listaCst) throws Exception {

        Map<String, MapaReformaTributariaCstVO> mapaCst = listaCst.stream()
                .collect(Collectors.toMap(
                        MapaReformaTributariaCstVO::getOrigId,
                        Function.identity()
                ));

        MapaReformaTributariaCstVO mapa = mapaCst.get(this.getOrigIdCstIbsCbs());

        if (mapa == null || mapa.getCst() == null) {
            throw new Exception(
                "Erro ao encontrar a CST de origem '" + getOrigIdCstIbsCbs()
                + "' para a classificação '" + getOrigCclasstrib() + "'.\n\n"
                + "Verifique o Mapa de CST."
            );
        }

        return new ClassificacaoTributariaVO(
                this.getOrigReducao(),
                this.getOrigDiferimento(),
                mapa.getCst().getId(),
                this.getOrigCclasstrib(),
                this.getOrigDescricao(),
                this.getOrigFundamentacaoLegal(),
                this.isOrigAliquotaZero()
        );
    }
}