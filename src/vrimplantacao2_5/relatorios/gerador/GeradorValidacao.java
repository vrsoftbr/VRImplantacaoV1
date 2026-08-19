package vrimplantacao2_5.relatorios.gerador;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2_5.relatorios.relatoriosDAO.RelatorioValidacaoDAO;

/**
 *
 * @author Wesley
 */
public class GeradorValidacao {

    private final InterfaceDAO dao;

    public GeradorValidacao(InterfaceDAO dao) {
        this.dao = dao;
    }

    public Map<String, Long> getValidacaoProduto() {
        Map<String, Long> validacao = new LinkedHashMap<>();

        try {
            validacao = new RelatorioValidacaoDAO().getValidacaoProduto(dao.getSistema(), dao.getLojaOrigem());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        return validacao;
    }

    public boolean gerarValidacaoProdutoTxt() {
        try {
            File f = new File("/vr/implantacao/planilhas/Produtos-Validacao.txt");

            Map<String, Long> antigo = dao.getValidacaoProduto();
            Map<String, Long> novo = this.getValidacaoProduto();

            try (PrintWriter printWriter = new PrintWriter(f)) {

                printWriter.println("| CAMPO                              | ANTIGO     | NOVO       | RESULTADO  |");
                printWriter.println("|------------------------------------|------------|------------|------------|");

                for (String campo : antigo.keySet()) {

                    Long valorAntigo = antigo.get(campo);
                    Long valorNovo = novo.get(campo);

                    boolean correto = Objects.equals(valorAntigo, valorNovo);

                    printWriter.printf(
                            "| %-34s | %10d | %10d | %-9s |%n",
                            campo,
                            valorAntigo,
                            valorNovo,
                            correto ? "✓ CORRETO" : "✗ ERRADO"
                    );
                }
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Erro em Validação de Produtos\n"
                    + "Entre em contato com o setor de migração e reporte esse erro\n\n"
                    + e,
                    "Relatórios",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return false;

        }
    }

    public Map<String, Long> getValidacaoFornecedor() {
        Map<String, Long> validacao = new LinkedHashMap<>();

        try {
            validacao = new RelatorioValidacaoDAO().getValidacaoFornecedor(dao.getSistema(), dao.getLojaOrigem());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        return validacao;
    }

    public boolean gerarValidacaoFornecedorTxt() {
        try {
            File f = new File("/vr/implantacao/planilhas/Fornecedor-Validacao.txt");

            Map<String, Long> antigo = dao.getValidacaoFornecedor();
            Map<String, Long> novo = this.getValidacaoFornecedor();

            try (PrintWriter printWriter = new PrintWriter(f)) {

                printWriter.println("| CAMPO                              | ANTIGO     | NOVO       | RESULTADO  |");
                printWriter.println("|------------------------------------|------------|------------|------------|");

                for (String campo : antigo.keySet()) {

                    Long valorAntigo = antigo.get(campo);
                    Long valorNovo = novo.get(campo);

                    boolean correto = Objects.equals(valorAntigo, valorNovo);

                    printWriter.printf(
                            "| %-34s | %10d | %10d | %-9s |%n",
                            campo,
                            valorAntigo,
                            valorNovo,
                            correto ? "✓ CORRETO" : "✗ ERRADO"
                    );
                }
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Erro em Validação de Produtos\n"
                    + "Entre em contato com o setor de migração e reporte esse erro\n\n"
                    + e,
                    "Relatórios",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return false;

        }
    }
}
