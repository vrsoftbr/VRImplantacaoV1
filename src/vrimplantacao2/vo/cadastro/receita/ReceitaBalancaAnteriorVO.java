package vrimplantacao2.vo.cadastro.receita;

/**
 *
 * @author Leandro
 */
public class ReceitaBalancaAnteriorVO {
    
    private String sistema;
    private String loja;
    private String id;
    private int codigoAtualFilizola;
    private int codigoAtualToledo;
    private String descricao;

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

    public int getCodigoAtualFilizola() {
        return codigoAtualFilizola;
    }

    public void setCodigoAtualFilizola(int codigoAtualFilizola) {
        this.codigoAtualFilizola = codigoAtualFilizola;
    }

    public int getCodigoAtualToledo() {
        return codigoAtualToledo;
    }

    public void setCodigoAtualToledo(int codigoAtualToledo) {
        this.codigoAtualToledo = codigoAtualToledo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
