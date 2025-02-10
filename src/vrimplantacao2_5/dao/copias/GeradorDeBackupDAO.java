package vrimplantacao2_5.dao.copias;

import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

/**
 *
 * @author Wesley
 */
public class GeradorDeBackupDAO {

    public void gerarBackup(List<String> listaDeOpcoes) throws Exception {

        for (int i = 0; i < listaDeOpcoes.size(); i++) {

            try (Statement stm = Conexao.createStatement()) {

                stm.execute(this.dropTableScript(listaDeOpcoes.get(i)));
                stm.execute(this.backupTabelaScript(listaDeOpcoes.get(i)));

            } catch (Exception e) {
                throw new RuntimeException("Erro ao tentar fazer o backup da tabela " + listaDeOpcoes.get(i) + ".", e);
            }
            Util.exibirMensagem("Feito backup da tabelas " + listaDeOpcoes.get(i) + " com sucesso.\n\n"
                    + "O backup se encontra em implantacao." + listaDeOpcoes.get(i) + "_bkp", "Confirmação de backup");
        }
    }

    public void dropBackup(List<String> listaDeOpcoes) throws Exception {

        for (int i = 0; i < listaDeOpcoes.size(); i++) {

            try (Statement stm = Conexao.createStatement()) {

                stm.execute(this.dropTableScript(listaDeOpcoes.get(i)));

            } catch (Exception e) {
                throw new RuntimeException("Erro ao tentar deletar o backup da tabela " + listaDeOpcoes.get(i) + ".", e);
            }
        }
        Util.exibirMensagem("Backup das tabelas selecionadas deletado com sucesso.", "Confirmação de exclusão de backup");

    }

    private String backupTabelaScript(String tabela) {

        String sql
                = "SELECT * INTO implantacao." + tabela + "_bkp FROM " + tabela + ";";

        return sql;
    }

    private String dropTableScript(String tabela) {

        String sql
                = "DROP TABLE IF EXISTS implantacao." + tabela + "_bkp;";

        return sql;
    }
}
