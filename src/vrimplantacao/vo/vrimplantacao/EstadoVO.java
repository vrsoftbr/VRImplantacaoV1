package vrimplantacao.vo.vrimplantacao;

import java.util.ArrayList;
import java.util.List;

public class EstadoVO {
    private int id = -1;
    private String sigla = "";
    private String descricao = "";
    private List<MunicipioVO> municipios = new ArrayList<>();

    public EstadoVO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<MunicipioVO> getMunicipios() {
        return municipios;
    }

    public void setMunicipios(List<MunicipioVO> municipios) {
        this.municipios = municipios;
    }
    
    
    
}
