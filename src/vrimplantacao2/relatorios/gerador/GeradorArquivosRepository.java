/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.relatorios.gerador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrframework.remote.Arquivo;
import vrimplantacao2.relatorios.relatoriosDAO.ExecutaSpedDAO;

/**
 *
 * @author Michael
 */
public class GeradorArquivosRepository {

    GeradorCest cest = new GeradorCest();
    GeradorNcm ncm = new GeradorNcm();
    GeradorBalanca bal = new GeradorBalanca();

    public void geraPlanilha() throws Exception {
        Arquivo.mkdir(Util.getRoot() + "vr/implantacao/planilhas");

        HSSFWorkbook workbook = new HSSFWorkbook();

        cest.geraPlanilhaCest(workbook);
        ncm.geraPlanilhaNcm(workbook);
        bal.geraPlanilhaBalanca(workbook);

        try {
            FileOutputStream out = new FileOutputStream(new File("/vr/implantacao/planilhas/relatorios_fiscais.xls"));
            workbook.write(out);
            out.close();
            workbook.close();
            new ExecutaSpedDAO().executaSped();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Object[] options = {"Tentar Novamente", "Cancelar"};
            int decisao = JOptionPane.showOptionDialog(null, "Provavelmente o arquivo está aberto, feche-o e tente novamente.\n"
                    + "Se o erro persistir, procure o setor de migração.\n\n",
                    "Gerar Relatórios", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            if (decisao == 0) {
                try {
                    new GeradorArquivosRepository().geraPlanilha();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Provavel erro na geração de SPED");
            e.printStackTrace();
        }
    }

    public void geraRelaotirosTexto() throws Exception {
        Arquivo.mkdir(Util.getRoot() + "vr/implantacao/planilhas");

        Object[] options = {"Cest", "Códigos de balança alterados", "Ncm", "Cancelar"};
        int menu = 0;

        do {

            int decisao = JOptionPane.showOptionDialog(null, "Selecione um relatório:\n"
                    + "Para sair da tela, clique em cancelar.\n\n",
                    "Gerar Relatórios", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (decisao == 3 || decisao == -1) {
                menu = 3;
            }

            if (decisao == 0) {
                new GeradorCest().gerarCestTxt();
                JOptionPane.showMessageDialog(null, "Relatório Cest gerado", "Relatórios", JOptionPane.INFORMATION_MESSAGE);
            }

            if (decisao == 1) {
                new GeradorBalanca().gerarBalancaTxt();
                JOptionPane.showMessageDialog(null, "Relatório Cod. Balança gerado", "Relatórios", JOptionPane.INFORMATION_MESSAGE);
            }

            if (decisao == 2) {
                new GeradorNcm().gerarNcmTxt();
                JOptionPane.showMessageDialog(null, "Relatório Ncm gerado", "Relatórios", JOptionPane.INFORMATION_MESSAGE);
            }
        } while (menu == 0);
    }
}