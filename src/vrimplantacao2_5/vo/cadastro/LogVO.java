package vrimplantacao2_5.vo.cadastro;

import java.util.Date;

/**
 *
 * @author guilhermegomes
 */
public class LogVO {
    
    private int id;
    private int idUsuario;
    private String dataHoraTime;
    private int idTipoOperacao;
    private int idLoja;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDataHoraTime() {
        return dataHoraTime;
    }

    public void setDataHoraTime(String dataHoraTime) {
        this.dataHoraTime = dataHoraTime;
    }

    public int getIdTipoOperacao() {
        return idTipoOperacao;
    }

    public void setIdTipoOperacao(int idTipoOperacao) {
        this.idTipoOperacao = idTipoOperacao;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }
}
