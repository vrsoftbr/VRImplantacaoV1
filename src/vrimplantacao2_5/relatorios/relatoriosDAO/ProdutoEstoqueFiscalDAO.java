/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.relatorios.relatoriosDAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import vrframework.classe.Conexao;
import vrimplantacao2_5.relatorios.vo.ProdutoEstoqueFiscalVO;

/**
 *
 * @author Michael
 */
public class ProdutoEstoqueFiscalDAO {

    public List<ProdutoEstoqueFiscalVO> getProdutoEstoqueFiscal() throws Exception {
        List<ProdutoEstoqueFiscalVO> result = new ArrayList<>();
        int loja = escolherLoja();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	row_number() over() id,\n"
                    + "	id_produto,\n"
                    + "	replace(round((estoque), 3)::varchar,\n"
                    + "	'.',\n"
                    + "	',') estoque\n"
                    + "from\n"
                    + "	produtocomplemento\n"
                    + "where\n"
                    + "	id_situacaocadastro = 1\n"
                    + "	and id_loja = " + loja + "\n"
                    + "	and estoque > 0")) {
                while (rs.next()) {
                    ProdutoEstoqueFiscalVO imp = new ProdutoEstoqueFiscalVO();

                    imp.setId(rs.getInt("id"));
                    imp.setIdProduto(rs.getInt("id_produto"));
                    imp.setEstoque(rs.getString("estoque"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    private int escolherLoja() throws Exception {
        Map<Integer, String> lista = getTipoCarteiraContaPagar();
        JComboBox jcb = new JComboBox();
        jcb.removeAll();
        for (int i = 1; i < lista.size(); i++) {
            jcb.addItem(lista.get(i));
        }
        int resposta = JOptionPane.showConfirmDialog(null, jcb, "Selecione a loja de onde quer gerar a lista com produtos para gerar a nota.", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);
        String[] selecao = (String.valueOf(jcb.getSelectedItem()).split(" "));
        return resposta != 0 ? -1 : Integer.parseInt(selecao[0]);
    }

    public Map<Integer, String> getTipoCarteiraContaPagar() throws Exception {
        Map<Integer, String> result = new HashMap<Integer, String>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	descricao\n"
                    + "from\n"
                    + "	loja l"
            )) {
                while (rst.next()) {
                    result.put(rst.getInt("id"), rst.getString("descricao"));
                }
            }
        }
        return result;
    }
}
