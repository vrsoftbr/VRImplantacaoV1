package vrimplantacao2_5.relatorios.gerador;

import java.io.File;
import java.util.Set;
import java.util.List;
import java.util.Objects;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.validacao.ProdutoBalancaValidacaoVO;
import vrimplantacao2_5.relatorios.relatoriosDAO.RelatorioValidacaoDAO;

/**
 *
 * @author Wesley
 */
public class GeradorValidacaoProdutoBalanca {

    private final InterfaceDAO dao;

    public GeradorValidacaoProdutoBalanca(InterfaceDAO dao) {
        this.dao = dao;
    }
    
    public List<ProdutoBalancaValidacaoVO> getCodigosProdutos() {

        List<ProdutoBalancaValidacaoVO> produtos = new ArrayList<>();

        try {
            produtos = new RelatorioValidacaoDAO()
                    .getCodigosProdutos(
                            dao.getSistema(),
                            dao.getLojaOrigem()
                    );

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        return produtos;
    }

    public boolean gerarValidacaoCodigoProdutoBalancaTxt() {

        try {

            File f = new File(
                    "/vr/implantacao/planilhas/Produto-Balanca-Validacao.txt"
            );

            Set<Long> produtosBalancaAntigo = dao.getValidaProdutosBalanca();

            List<ProdutoBalancaValidacaoVO> produtosNovos = this.getCodigosProdutos();

            try (PrintWriter printWriter = new PrintWriter(f)) {

                imprimirCabecalho(printWriter);

                for (Long codigoAntigo : produtosBalancaAntigo) {

                    ProdutoBalancaValidacaoVO produto = encontrarProduto(
                            produtosNovos,
                            codigoAntigo
                    );

                    imprimirValidacao(
                            printWriter,
                            codigoAntigo,
                            produto
                    );
                }
            }

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Erro em Validação de Produtos de Balança\n"
                    + "Entre em contato com o setor de migração e reporte esse erro\n\n"
                    + e,
                    "Relatórios",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return false;
        }
    }

    private void imprimirCabecalho(PrintWriter printWriter) {

        printWriter.println(
                "| CODIGO ANTIGO | CODIGO NOVO  | PESÁVEL | TIPO EMB. | TIPO EMB. AUT. | CÓD. BARRAS   | MOTIVO                 |"
        );

        printWriter.println(
                "|---------------|--------------|---------|-----------|----------------|---------------|------------------------|"
        );
    }

    private ProdutoBalancaValidacaoVO encontrarProduto(
            List<ProdutoBalancaValidacaoVO> produtos,
            Long codigoAntigo) {

        return produtos.stream()
                .filter(p -> Objects.equals(
                p.getCodigoAntigo(),
                codigoAntigo
        ))
                .findFirst()
                .orElse(null);
    }

    private void imprimirValidacao(
            PrintWriter printWriter,
            Long codigoAntigo,
            ProdutoBalancaValidacaoVO produto) {

        if (produto == null) {

            printWriter.printf(
                    "| %-13s | %-12s | %-7s | %-9s | %-14s | %-13s | %-22s |%n",
                    codigoAntigo,
                    "NÃO ENCON...",
                    "-",
                    "-",
                    "-",
                    "-",
                    "PRODUTO NÃO ENCONTRADO"
            );

            return;
        }

        /*
     * Era produto de balança na base antiga,
     * mas deixou de ser pesável na base nova.
         */
        if (!Boolean.TRUE.equals(produto.getPesavel())) {

            String motivo = identificarMotivo(produto);

            printWriter.printf(
                    "| %-13s | %-12s | %-7s | %-9s | %-14s | %-13s | %-22s |%n",
                    codigoAntigo,
                    produto.getCodigoAtual(),
                    produto.getPesavel(),
                    produto.getTipoEmbalagem(),
                    produto.getTipoEmbalagemAutomacao(),
                    produto.getCodigoBarras(),
                    motivo
            );
        }

        Long codigoNovo = produto.getCodigoAtual();

        // Produto não sofreu alteração
        if (Objects.equals(codigoAntigo, codigoNovo)) {
            return;
        }

        String motivo = identificarMotivo(produto);

        printWriter.printf(
                "| %-13s | %-12s | %-7s | %-9s | %-14s | %-13s | %-22s |%n",
                codigoAntigo,
                codigoNovo,
                produto.getPesavel(),
                produto.getTipoEmbalagem(),
                produto.getTipoEmbalagemAutomacao(),
                produto.getCodigoBarras(),
                motivo
        );
    }

    private String identificarMotivo(
            ProdutoBalancaValidacaoVO produto) {

        if (produto.getCodigoBarras() != null
                && produto.getCodigoBarras() > 999999) {

            return "CÓD. BARRAS > 999999";
        }

        if (Objects.equals(
                produto.getTipoEmbalagem(),
                14)) {

            return "TIPO EMBALAGEM 14";
        }

        return "CÓDIGO ALTERADO";
    }
}
