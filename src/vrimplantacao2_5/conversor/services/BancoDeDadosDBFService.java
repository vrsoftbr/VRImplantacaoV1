/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.conversor.services;

import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import vrframework.classe.ProgressBar;
import vrimplantacao2_5.conversor.dao.ConversorDbfDAO;

/**
 *
 * @author Michael-Oliveira
 */
public class BancoDeDadosDBFService {

    private static String regexp = "([\\W])";
    private static String banco = "postgres";
    private static ConversorDbfDAO dao = null;

    public static void criarBanco() throws Exception {
        dao.criarBanco();
        dao.criarControleDeDadosConvertidos();
    }

    public void criarControleDeDadosConvertidos() throws Exception {
        dao = new ConversorDbfDAO();
        dao.criarControleDeDadosConvertidos();
    }

    public void setNomeBanco(String banco) {
        this.banco = banco;
        dao.setNomeBanco(banco);
    }

    public static void criarBanco(ConversorDbfDAO dao) throws Exception {
        dao.criarBanco();
        dao.criarControleDeDadosConvertidos();
    }

    public static void salvar(File[] filesDBF, File[] filesMemo, boolean haMemoFiles) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(false);
                    for (File file : filesDBF) {
                        String tabela = file.getName().substring(0, file.getName().lastIndexOf("."));
                        dao.setNomeDaTabela(tabela);
                        ProgressBar.setStatus("Criando tabela: " + tabela + ", por favor aguarde.");
                        DBFReader reader = new DBFReader(new FileInputStream(file));
                        if (haMemoFiles) {
                            File memoFile = inserirMemoFileEquivaentNoReader(filesMemo, tabela);
                            reader.setMemoFile(memoFile);
                        }
                        List<DBFDataType> dadosCabecalho = new ArrayList<>();
                        for (int i = 0; i < reader.getFieldCount(); i++) {
                            dadosCabecalho.add(reader.getField(i).getType());
                        }
                        criarTabela(reader, tabela, dadosCabecalho);
                        insereDadosConvertidos(tabela);

                        List<String> inserts = new ArrayList<>();

                        ProgressBar.setStatus("Preparando dados da tabela: " + tabela + ", por favor aguarde.");
                        ProgressBar.setMaximum(reader.getRecordCount());

                        if (reader.getRecordCount() >= 1000000) {
                            ProgressBar.setStatus("Preparando dados de: " + tabela + " em lotes, total restante:" + reader.getRecordCount());
                            ProgressBar.setMaximum(10000);
                            preparaESalvaArquivosEmLote(reader, tabela, dadosCabecalho, inserts);
                        } else {
                            DBFRow linha;
                            StringBuilder insertBuilder = new StringBuilder();
                            while ((linha = reader.nextRow()) != null) {
                                insertBuilder.setLength(0);
                                insertBuilder.append("insert into ").append(tabela).append(" values (");
                                inserts.add(DBFService.prepararArquivosEmLote(reader, tabela, dadosCabecalho, inserts, linha, insertBuilder));
                                ProgressBar.next();
                            }
                            System.gc();

                            ProgressBar.setStatus("Salvando dados da tabela: " + tabela + ", por favor aguarde.");
                            ProgressBar.setMaximum(reader.getRecordCount());

                            finaliza(inserts, tabela);
                            inserts.clear();
                            System.gc();
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Base " + banco + " criada com sucesso! \n");
                    ProgressBar.dispose();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ProgressBar.dispose();
                    JOptionPane.showMessageDialog(null, "Erro ao criar ou popular a tabela: " + dao.getNomeDaTabela() + "\n\nErro: " + ex);
                    Exceptions.printStackTrace(ex);
                    ProgressBar.dispose();
                }
            }
        };
        thread.start();
    }

    //Filtra memo file de acordo com a tabela
    private static File inserirMemoFileEquivaentNoReader(File[] filesMemo, String tabela) {
        for (File memo : filesMemo) {
            String tabelaMemo = memo.getName().substring(0, memo.getName().lastIndexOf("."));
            if (tabelaMemo.toUpperCase().trim().equals(tabela.toUpperCase().trim())) {
                return memo;
            }
        }
        return null;
    }

    private static void preparaESalvaArquivosEmLote(DBFReader reader, String tabela, List<DBFDataType> dadosCabecalho, List<String> inserts) {
        try {
            DBFRow linha;
            int batchSize = 10000;
            int rodada = 0;
            StringBuilder insertBuilder = new StringBuilder();
            while ((linha = reader.nextRow()) != null) {
                insertBuilder.setLength(0);
                insertBuilder.append("insert into ").append(tabela).append(" values (");
                inserts.add(DBFService.prepararArquivosEmLote(reader, tabela, dadosCabecalho, inserts, linha, insertBuilder));
                ProgressBar.next();
                if (inserts.size() >= batchSize) {
                    rodada++;
                    ProgressBar.setStatus("Salvando " + inserts.size() + " dados, por favor aguarde.");
                    gravaInsertsEmLote(inserts, tabela, rodada);
                    inserts.clear();
                    int restante = reader.getRecordCount() - (batchSize * rodada);
                    ProgressBar.setStatus("Preparando dados de: " + tabela + ", total restante: " + restante);
                    ProgressBar.setMaximum(restante > batchSize ? batchSize : restante);
                }
            }

            if (!inserts.isEmpty()) {
                gravaInsertsEmLote(inserts, tabela, rodada);
                inserts.clear();
            }
            System.gc();
        } catch (Exception e) {
            System.out.println("Erro ao preparar listagem em lote");
            e.printStackTrace();
        }
    }
    
    private static void criarTabela(DBFReader reader, String tabela, List<DBFDataType> dadosCabecalho) throws Exception {
        String sql = scriptTabela(reader, tabela, dadosCabecalho);
        System.out.println(sql);
        dao.criarTabelas(sql);
    }

    private static String scriptTabela(DBFReader reader, String tabela, List<DBFDataType> dadosCabecalho) {
        int contador = 0;
        String campos = "(\n";
        String sql = "CREATE TABLE IF NOT EXISTS " + tabela + "\n";
        for (int i = 0; i < reader.getFieldCount(); i++) {
            if (contador < dadosCabecalho.size()) {
                String dado = reader.getField(i).getName();
                String tipo = TiposDadosDBFService.retornaTipo(reader.getField(i).getType());
                campos += "\"" + dado.replaceAll(regexp, "").trim().replace(",", "").toLowerCase().toString()
                        + "\"" + tipo + ",\n";
                //campos += dado.replaceAll(regexp, "").trim().replace(",", "_") + " text,\n";//.replace("-", "").replace(" ", "").replace("\\", "").replace("/", "").replace(".", "").replace(",", "_") + " text,\n";
                contador++;
            }
        }
        sql = sql + campos.substring(0, campos.length() - 2) + "\n);";
        return sql;
    }

    private static void insereDadosConvertidos(String tabela) throws Exception {
        dao.insereDeDadosConvertidos(banco, tabela);
    }

    private static void finaliza(List<String> inserts, String tabela) throws Exception {
        dao.conferePopularTabelas();
        dao.abrirConexao();
        dao.popularTabelasDbf(inserts);
        dao.fecharConexao();
        dao.atualizaDeDadosConvertidos(banco, tabela);
    }

    private static void gravaInsertsEmLote(List<String> inserts, String tabela, int rodada) throws Exception {
        if (rodada == 1) {
            dao.conferePopularTabelas();
        }
        dao.abrirConexao();
        dao.popularTabelasDbfEmLote(inserts);
        dao.fecharConexao();
        dao.atualizaDeDadosConvertidos(banco, tabela);
    }
}
