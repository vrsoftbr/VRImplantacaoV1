package vrimplantacao2.gui.tools.scripts.sped;

import vrimplantacao2.gui.tools.scripts.ScriptNode;

/**
 * Script para preencher a tabela sped.fornecedoralteracao.
 * @author Leandro
 */
public class FornecedorAlteracao extends ScriptNode {

    @Override
    public String getNome() {
        return "Gerar SPED.FornecedorAlteração";
    }
    
    @Override
    public String getInstrucoes() {
        return "Este Script preenche a tabela sped.fornecedoralteracao\n"
                + "Este procedimento é obrigatório para clientes que geram SPED.";
    }
    
}
