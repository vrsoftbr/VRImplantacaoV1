package vrimplantacao2.vo.cadastro.comprador;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 * Classe que representa o comprador do VR.
 * @author Leandro
 */
public class CompradorVO {
    
    private int id;
    private String nome;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 40);
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }
        
}
