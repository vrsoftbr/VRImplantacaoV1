package receita;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 * Utilize para importar a receita de produção.
 * @author leandro
 */
public class ReceitaIMP {
    
    private String id;
    private String descricao;
    private SituacaoCadastro situacaoCadastro;
    private String fichaTecnica;
    
    private final Set<ReceitaItemIMP> itens = new LinkedHashSet<>();
    private final Set<ReceitaProdutoIMP> rendimento = new LinkedHashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro == null ? SituacaoCadastro.ATIVO : situacaoCadastro;
    }

    public String getFichaTecnica() {
        return fichaTecnica;
    }

    public void setFichaTecnica(String fichaTecnica) {
        this.fichaTecnica = fichaTecnica;
    }

    public Set<ReceitaItemIMP> getItens() {
        return itens;
    }

    public Set<ReceitaProdutoIMP> getRendimento() {
        return rendimento;
    }
    
    public ReceitaItemIMP addItem() {
        ReceitaItemIMP item = new ReceitaItemIMP(this);
        this.itens.add(item);
        return item;
    }
    
    /**
     * Inclui um rendimento da receita.
     * @param idProduto Item resultante da receita.
     * @param rendimento Quantidade produzida pela receita.
     * @return
     */
    public ReceitaProdutoIMP addRendimento(String idProduto, double rendimento) {
        ReceitaProdutoIMP produto = new ReceitaProdutoIMP(this, idProduto, rendimento);
        this.rendimento.add(produto);
        return produto;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReceitaIMP other = (ReceitaIMP) obj;
        return Objects.equals(this.id, other.id);
    }
}
