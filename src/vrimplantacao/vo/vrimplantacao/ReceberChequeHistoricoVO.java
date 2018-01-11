package vrimplantacao.vo.vrimplantacao;

import java.util.Date;

/**
 * Classe que representa o histórico de mudança de alinea do cheque.
 * @author Leandro
 */
public class ReceberChequeHistoricoVO {
    private int id = -1;
    private long id_recebercheque = -1;
    private Date datahora;
    private int id_tipoalinea = 0;
    private int id_usuario = 0;
    //Assistentes da integração
    public String impSistemaId;
    public String impLojaId;
    public String impId;
    
    public String getChave() {
        return impSistemaId + "-" + impId + "-" + impId;
    }

    public ReceberChequeHistoricoVO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getId_recebercheque() {
        return id_recebercheque;
    }

    public void setId_recebercheque(long id_recebercheque) {
        this.id_recebercheque = id_recebercheque;
    }

    public Date getDatahora() {
        return datahora;
    }

    public void setDatahora(Date datahora) {
        this.datahora = datahora;
    }

    public int getId_tipoalinea() {
        return id_tipoalinea;
    }

    public void setId_tipoalinea(int id_tipoalinea) {
        this.id_tipoalinea = id_tipoalinea;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
}
