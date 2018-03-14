package vrimplantacao2.vo.cadastro.nutricional;

/**
 *
 * @author Leandro
 */
public class NutricionalAnteriorVO {
    
    private String sistema;
    private String loja;
    private String id;
    private Integer codigoAtualFilizola;
    private Integer codigoAtualToledo;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCodigoAtualFilizola() {
        return codigoAtualFilizola;
    }

    public void setCodigoAtualFilizola(Integer codigoAtualFilizola) {
        this.codigoAtualFilizola = codigoAtualFilizola;
    }

    public Integer getCodigoAtualToledo() {
        return codigoAtualToledo;
    }

    public void setCodigoAtualToledo(Integer codigoAtualToledo) {
        this.codigoAtualToledo = codigoAtualToledo;
    }
    
}
