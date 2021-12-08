package vrimplantacao2_5.vo.cadastro;

import java.sql.Date;
import org.joda.time.LocalDateTime;

/**
 *
 * @author guilhermegomes
 */
public class LogVO {
    
    private int id;
    private int idUsuario;
    private Date dataHora;
    private LocalDateTime dataHoraTime;
    private int idTipoOperacao;

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

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public int getIdTipoOperacao() {
        return idTipoOperacao;
    }

    public void setIdTipoOperacao(int idTipoOperacao) {
        this.idTipoOperacao = idTipoOperacao;
    }

    public LocalDateTime getDataHoraTime() {
        return dataHoraTime;
    }

    public void setDataHoraTime(LocalDateTime dataHoraTime) {
        this.dataHoraTime = dataHoraTime;
    }
    
}
