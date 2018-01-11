package vrimplantacao2.vo.cadastro.local;

public class MunicipioVO {
    
    private int id;
    private String descricao;
    private EstadoVO estado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EstadoVO getEstado() {
        return estado;
    }

    public void setEstado(EstadoVO estado) {
        this.estado = estado;
    }

    public MunicipioVO() {
    }

    public MunicipioVO(int id, String descricao, EstadoVO estado) {
        this.id = id;
        this.descricao = descricao;
        this.estado = estado;
    }

}
