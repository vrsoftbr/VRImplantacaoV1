package vrimplantacao2.vo.cadastro.tributacao;

import vr.core.utils.StringUtils;

public class AliquotaPdvVO {
    
    private int id;
    private int idAliquota;
    private String descricao;
    private double porcentagem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAliquota() {
        return idAliquota;
    }

    public void setIdAliquota(int idAliquota) {
        this.idAliquota = idAliquota;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = StringUtils.acertarTexto(descricao, 20);
    }

    public double getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(double porcentagem) {
        this.porcentagem = porcentagem;
    }
    
    public static AliquotaPdvVO of(AliquotaVO aliquota) {
        AliquotaPdvVO vo = new AliquotaPdvVO();
        
        vo.setIdAliquota(aliquota.getId());
        vo.setDescricao(aliquota.getDescricao());
        vo.setPorcentagem(aliquota.getAliquotaFinal());
        
        return vo;
    }
    
}
