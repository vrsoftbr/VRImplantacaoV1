package vrimplantacao2_5.vo.cadastro;

/**
 *
 * @author Desenvolvimento
 */
public class TecladoLayoutVO {

    private int id;
    private int idLoja;
    private String descricao;
    private int idTecladoLayoutCopiado;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }
    
    public int getIdTecladoLayoutCopiado() {
        return idTecladoLayoutCopiado;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public void setIdTecladoLayoutCopiado(int idTecladoLayoutCopiado) {
        this.idTecladoLayoutCopiado = idTecladoLayoutCopiado;
    }
}