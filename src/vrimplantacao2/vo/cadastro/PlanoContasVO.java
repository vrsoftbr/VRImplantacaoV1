package vrimplantacao2.vo.cadastro;

/**
 *
 * @author Importacao
 */
public class PlanoContasVO {
    public int id;
    public String vid;
    public String descricao;
    
    public PlanoContasVO() {}
    
    public PlanoContasVO(int i_id, String descricao) {
        this.id = i_id;
        this.descricao = descricao;
    }
    
    public PlanoContasVO(String i_id, String descricao) {
        this.vid = i_id;
        this.descricao = descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}
