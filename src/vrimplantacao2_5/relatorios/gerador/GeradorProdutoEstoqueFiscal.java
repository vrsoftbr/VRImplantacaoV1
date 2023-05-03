/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.relatorios.gerador;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrframework.remote.Arquivo;
import vrimplantacao2_5.relatorios.relatoriosDAO.ProdutoEstoqueFiscalDAO;
import vrimplantacao2_5.relatorios.utils.ParticionadorDeLista;
import vrimplantacao2_5.relatorios.vo.ProdutoEstoqueFiscalVO;

/**
 *
 * @author Desenvolvimento
 */
public class GeradorProdutoEstoqueFiscal {

    public List<ProdutoEstoqueFiscalVO> carregarDadosProdutoEstoque() {
        List<ProdutoEstoqueFiscalVO> listProdutoEstoqueFiscal = null;
        try {
            listProdutoEstoqueFiscal = new ProdutoEstoqueFiscalDAO().getProdutoEstoqueFiscal();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return listProdutoEstoqueFiscal;
    }



    public void gerarProdutoEstoqueFiscalTxt() throws Exception {
        try {
            Arquivo.delete(Util.getRoot() + "vr/planilhas_fiscais");
            Arquivo.mkdir(Util.getRoot() + "vr/planilhas_fiscais");
            List<ProdutoEstoqueFiscalVO> listProdutoEstoqueFiscal = null;
            List[] listaPrcionada = null;
            try {
                listProdutoEstoqueFiscal = carregarDadosProdutoEstoque();
                listaPrcionada = new ParticionadorDeLista().particionar(listProdutoEstoqueFiscal, 400);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            if (listaPrcionada.length == 0) {
                JOptionPane.showMessageDialog(null, "Loja selecionada não tem estoque para gerar notas");
            } else {
                for (int i = 0; i < listaPrcionada.length; i++) {
                    listProdutoEstoqueFiscal = listaPrcionada[i];
                    String primeiro = String.valueOf(listProdutoEstoqueFiscal.get(0).getId());
                    String ultimo = String.valueOf(listProdutoEstoqueFiscal.get(listProdutoEstoqueFiscal.size() - 1).getId());
                    File f = new File("/vr/planilhas_fiscais/ProdutoEstoqueDe_" + primeiro + "_Ate_" + ultimo + ".txt");
                    PrintWriter printWriter = new PrintWriter(f);
                    for (ProdutoEstoqueFiscalVO produtoEstoque : listProdutoEstoqueFiscal) {

                        String linhaFormatada = String.valueOf(produtoEstoque.getIdProduto()) + ";" + String.valueOf(produtoEstoque.getEstoque()) + "\n";
                        printWriter.print(linhaFormatada);
                    }
                    printWriter.flush();
                    printWriter.close();
                }
                JOptionPane.showMessageDialog(null, "Arquivos gerados com sucesso!\n\nFavor conferir em: " + Util.getRoot() + "vr/planilhas_fiscais");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro em GeradorProdutoEstoqueFiscal\n"
                    + "Entre em contato com o setor de migração e reporte esse erro\n\n"
                    + e, "Relatórios", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
