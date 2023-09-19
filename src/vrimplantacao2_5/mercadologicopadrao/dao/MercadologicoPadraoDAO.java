package vrimplantacao2_5.mercadologicopadrao.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2_5.mercadologicopadrao.service.Utils;

/**
 *
 * @author Michael
 */
public class MercadologicoPadraoDAO {

    private static Utils utils = new Utils();

//    public void mercadologicoPadraoPasso1() throws Exception {
//        try {
//            insereMercadologicoTemporario();
//            atualizaMercadologicoAtualParaTEMP();
//        } catch (SQLException ex) {
//            System.out.println("Erro em MercadologicoPadraoDAO: mercadologicoPadraoPasso1()\n\n");
//            ex.printStackTrace();
//            throw ex;
//        }
//    }

    public void insereMercadologicoTemporario() throws Exception {
        try {
            Statement stm = Conexao.createStatement();
            stm.execute("INSERT INTO mercadologico VALUES (-1,0,0,0,0,0,1,'TEMP');");
        } catch (Exception ex) {
            System.out.println("Erro em MercadologicoPadraoDAO: mercadologicoPadraoPasso1()\n\n");
            ex.printStackTrace();
            throw ex;
        }
    }

    public void atualizaMercadologicoAtualParaTEMP() throws Exception {
        try {
            Statement stm = Conexao.createStatement();
            stm.execute("UPDATE produto SET \n"
                    + "mercadologico1 = 0,\n"
                    + "mercadologico2 = 0,\n"
                    + "mercadologico3 = 0,\n"
                    + "mercadologico4 = 0,\n"
                    + "mercadologico5 = 0;");
        } catch (Exception ex) {
            System.out.println("Erro em MercadologicoPadraoDAO: mercadologicoPadraoPasso1()\n\n");
            ex.printStackTrace();
            throw ex;
        }
    }

    public void mercadologicoPadraoPasso2() throws Exception {
        try {
            Statement stm = Conexao.createStatement();
            stm.execute("DELETE FROM mercadologico WHERE id > 0;");
        } catch (SQLException ex) {
            System.out.println("Erro em MercadologicoPadraoDAO: mercadologicoPadraoPasso2 - Deletar mercadologico\n\n");
            ex.printStackTrace();
            throw ex;
        }
    }

    public void mercadologicoPadraoPasso3() throws Exception {
        try {
            atualizaSequencia(1);
            Statement stm = Conexao.createStatement();
            List<String> inserts = utils.listaInsertMercadologicoPadrao();
            for (String insert : inserts) {
                stm.execute(insert);
            }
        } catch (SQLException ex) {
            System.out.println("Erro em MercadologicoPadraoDAO: mercadologicoPadraoPasso3 - Inserir mercadologico\n\n");
            ex.printStackTrace();
            throw ex;
        }
    }

    public void mercadologicoPadraoPasso4() throws Exception {
        try {
            atualizaMercadologico();
            deleteMercadologico();
            atualizaSequencia(1117);
        } catch (SQLException ex) {
            System.out.println("Erro em MercadologicoPadraoDAO: mercadologicoPadraoPasso3 - Inserir mercadologico\n\n");
            ex.printStackTrace();
            throw ex;
        }
    }

    private void atualizaMercadologico() throws Exception {
        Statement stm = Conexao.createStatement();
        stm.execute("UPDATE produto SET \n"
                + "mercadologico1 = 14,\n"
                + "mercadologico2 = 1,\n"
                + "mercadologico3 = 1,\n"
                + "mercadologico4 = 0,\n"
                + "mercadologico5 = 0;");
    }

    private void deleteMercadologico() throws Exception {
        Statement stm = Conexao.createStatement();
        stm.execute("DELETE FROM mercadologico WHERE id < 0;");
    }

    private void atualizaSequencia(int indice) throws Exception {
        Statement stm = Conexao.createStatement();
        stm.execute("SELECT pg_catalog.setval('mercadologico_id_seq', " + indice + ", TRUE);");
    }

    public void abrirConexao() throws Exception {
        Conexao.begin();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public void fecharConexao() throws Exception {
        Conexao.close();
    }
}
