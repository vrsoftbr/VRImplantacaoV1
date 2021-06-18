package vrimplantacao2_5.vo.cadastro;

import java.util.Date;
import vrimplantacao2_5.vo.enums.ESituacaoMigracao;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBancoLojaVO {
    
    private int id;
    private String idLojaOrigem;
    private int idLojaVR;
    private String descricaoVR;
    private Date dataCadastro;
    private ESituacaoMigracao situacaoMigracao;
    private boolean lojaMatriz;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdLojaOrigem() {
        return idLojaOrigem;
    }

    public void setIdLojaOrigem(String idLojaOrigem) {
        this.idLojaOrigem = idLojaOrigem;
    }

    public int getIdLojaVR() {
        return idLojaVR;
    }

    public void setIdLojaVR(int idLojaVR) {
        this.idLojaVR = idLojaVR;
    }

    public String getDescricaoVR() {
        return descricaoVR;
    }

    public void setDescricaoVR(String descricaoVR) {
        this.descricaoVR = descricaoVR;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public ESituacaoMigracao getSituacaoMigracao() {
        return situacaoMigracao;
    }

    public void setSituacaoMigracao(ESituacaoMigracao situacaoMigracao) {
        this.situacaoMigracao = situacaoMigracao;
    }

    public boolean isLojaMatriz() {
        return lojaMatriz;
    }

    public void setLojaMatriz(boolean lojaMatriz) {
        this.lojaMatriz = lojaMatriz;
    }
}
