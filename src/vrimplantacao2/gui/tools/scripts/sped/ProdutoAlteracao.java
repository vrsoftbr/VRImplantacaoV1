package vrimplantacao2.gui.tools.scripts.sped;

import vrimplantacao2.gui.tools.scripts.ScriptNode;

/**
 * Script para preencher a tabela sped.produtoalteracao.
 * @author Leandro
 */
public class ProdutoAlteracao extends ScriptNode {

    public ProdutoAlteracao() {
    }

    @Override
    public String getNome() {
        return "Gerar SPED.ProdutoAlteração";
    }

    @Override
    public String getInstrucoes() {
        return "Este Script preenche a tabela sped.produtoalteracao\n"
                + "Este procedimento é obrigatório para clientes que geram SPED.";
    }
    
}
