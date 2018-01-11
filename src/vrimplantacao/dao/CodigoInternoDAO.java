package vrimplantacao.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;
import vrframework.classe.Conexao;
import vrimplantacao.vo.Formulario;
import vrimplantacao.classe.Global;

public class CodigoInternoDAO {

    public int get(Formulario i_formulario) throws Exception {
        return get(i_formulario, 1);
    }

    public int get(Formulario i_formulario, int i_tipo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //verifica codigo vago
            if (i_formulario.getId() == Formulario.NOTAFISCAL_SAIDA.getId()) {
                sql = new StringBuilder();
                sql.append("SELECT numerocontrole FROM notasaidasequencia WHERE id_loja = " + Global.idLoja + " FOR UPDATE");

                rst = stm.executeQuery(sql.toString());

                if (!rst.next()) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO notasaidasequencia (id_loja, numerocontrole) VALUES (");
                    sql.append(Global.idLoja + ", ");
                    sql.append("0)");

                    stm.execute(sql.toString());
                }

                sql = new StringBuilder();
                sql.append("UPDATE notasaidasequencia SET numerocontrole = numerocontrole + 1 WHERE id_loja = " + Global.idLoja);

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("SELECT numerocontrole AS id FROM notasaidasequencia WHERE id_loja = " + Global.idLoja);

                rst = stm.executeQuery(sql.toString());
                rst.next();

                int id = rst.getInt("id");

                stm.close();
                Conexao.commit();

                return id;

            } else {
                String tabela = "";

                if (i_formulario.getId() == Formulario.CADASTRO_USUARIO.getId()) {
                    tabela = "usuario";

                } else {
                    throw new Exception("Não foi possível gerar o código interno!");
                }

                sql = new StringBuilder();
                sql.append("SELECT id from (SELECT id FROM generate_series(1, (SELECT COALESCE(MAX(id), 0) + 1 FROM " + tabela + ")) AS s(id) EXCEPT SELECT id FROM " + tabela + ") AS codigointerno ORDER BY id LIMIT 1");
            }

            rst = stm.executeQuery(sql.toString());
            rst.next();

            int id = rst.getInt("id");

            stm.close();
            Conexao.commit();

            return id;

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public int get(String i_tabela) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id from (SELECT id FROM generate_series(1, (SELECT COALESCE(MAX(id), 0) + 1 FROM " + i_tabela + ")) AS s(id) EXCEPT SELECT id FROM " + i_tabela + ") AS codigointerno ORDER BY id LIMIT 1");

        rst = stm.executeQuery(sql.toString());
        rst.next();

        return rst.getInt("id");
    }

    public int get(String i_tabela, int QtdeMaxProduto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id from (SELECT id FROM generate_series(1, (SELECT COALESCE(MAX(id), 0) + 1 FROM " + i_tabela + " where id <= "+QtdeMaxProduto+")) AS s(id) EXCEPT SELECT id FROM " + i_tabela + ") AS codigointerno ORDER BY id LIMIT 1");

        rst = stm.executeQuery(sql.toString());
        rst.next();

        return rst.getInt("id");
    }
    
    public int getIdProduto(int QtdeMaxProduto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        int id = 10000;
        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id from ");
        sql.append("(SELECT id FROM generate_series(10000, (SELECT COALESCE(MAX(id), 0) + 1 FROM produto where id < "+QtdeMaxProduto+"))");
        sql.append("AS s(id) EXCEPT SELECT id FROM produto WHERE id > 9999) AS codigointerno ORDER BY id LIMIT 1");        

        rst = stm.executeQuery(sql.toString());
        
        if (rst.next()) {
            id = rst.getInt("id");
        }
        
        return id;
        
    }
    
    public int getIdProdutoBalanca() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        int id = 1;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("select coalesce(min(id + 1), 1) as id ");
        sql.append("from produto ");
        sql.append("where id < 10000 ");
        sql.append("and id + 1 not in (select id from produto where id < 10000) limit 1 ");

        rst = stm.executeQuery(sql.toString());
        
        if (rst.next()) {
            id = rst.getInt("id");
        }

        
        return id;
    }
    
    /*** ID PARA TROCAR CODIGOS *************/    
    public int getIdProduto2() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        int id = 10000;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id_produto2 from ");
        sql.append("(SELECT id_produto2 FROM generate_series(10000, (SELECT COALESCE(MAX(id_produto2), 0) + 1 FROM produto where id_produto2 <= 100000)) ");
        sql.append("AS s(id_produto2) EXCEPT SELECT id_produto2 FROM produto WHERE id_produto2 > 9999) AS codigointerno ORDER BY id_produto2 LIMIT 1");        

        rst = stm.executeQuery(sql.toString());
        
        if (rst.next()) {
            id = rst.getInt("id_produto2");
        }
        
        return id;
        
    }
    
    public int getIdProdutoBalanca2() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        int id = 1;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("select coalesce(min(id_produto2 + 1), 1) as id_produto2 ");
        sql.append("from produto ");
        sql.append("where id_produto2 < 10000 ");
        sql.append("and id_produto2 + 1 not in (select id_produto2 from produto where id_produto2 < 10000) limit 1 ");

        rst = stm.executeQuery(sql.toString());
        
        if (rst.next()) {
            id = rst.getInt("id_produto2");
        }

        
        return id;
    }

    public int gerarCodigoMercadologico1() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int retorno = 0;
        
        stm = Conexao.createStatement();
        
        sql = new StringBuilder();
        sql.append("SELECT COALESCE(MIN(mercadologico1 + 1), 1) AS mercadologico1 ");
        sql.append("FROM mercadologico WHERE mercadologico1 + 1 NOT IN (SELECT mercadologico1 FROM mercadologico) ");
        
        rst = stm.executeQuery(sql.toString());
        
        if (rst.next()) {
            
            retorno = rst.getInt("mercadologico1");
        }
        
        return retorno;
    }

    public int gerarCodigoMercadologico2(int mercadologico1) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int retorno = 0;
        
        stm = Conexao.createStatement();
        
        sql = new StringBuilder();
        sql.append("SELECT COALESCE(MIN(mercadologico2 + 1), 1) AS mercadologico2 ");
        sql.append("FROM mercadologico WHERE mercadologico2 + 1 NOT IN (SELECT mercadologico2 FROM mercadologico) ");
        sql.append("AND mercadologico1 = "+mercadologico1);
        
        rst = stm.executeQuery(sql.toString());
        
        if (rst.next()) {
            retorno = rst.getInt("mercadologico2");
        }
        
        return retorno;
    }
    
    public int gerarCodigoMercadologico3(int mercadologico1, int mercadologico2) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int retorno = 0;
        
        stm = Conexao.createStatement();
        
        sql = new StringBuilder();
        sql.append("SELECT COALESCE(MIN(mercadologico3 + 1), 1) AS mercadologico3 ");
        sql.append("FROM mercadologico WHERE mercadologico3 + 1 NOT IN (SELECT mercadologico3 FROM mercadologico) ");

        rst = stm.executeQuery(sql.toString());
        
        if (rst.next()) {
            
            retorno = rst.getInt("mercadologico3");
        }
        
        return retorno;
    }    

    public Stack<Integer> getIdsVagosBalanca() throws Exception {
        Stack<Integer> result = new Stack<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from \n" +
                    "(SELECT id FROM generate_series(1, 10000)\n" +
                    "AS s(id) EXCEPT SELECT id FROM produto WHERE id <= 9999) AS codigointerno ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }

    public Stack<Integer> getIdsVagosNormais() throws Exception {
        return getIdsVagosNormais(999999);
    }
    
    public Stack<Integer> getIdsVagosNormais(int qtdeProdutoCodigoInterno) throws Exception {
        Stack<Integer> result = new Stack<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from \n" +
                    "(SELECT id FROM generate_series(10000, " + qtdeProdutoCodigoInterno + ")\n" +
                    "AS s(id) EXCEPT SELECT id FROM produto WHERE id > 9999) AS codigointerno ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }
    
    public Set<Integer> getIdsExistentes() throws Exception {
        Set<Integer> result = new LinkedHashSet<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from produto order by id"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }
    
    /**
     * Retorna uma lista com todos os ids que não estão sendo utilizados no VR.
     * @param qtdIdsVagos Quantidade máxima de ids vagos que serão retornados.
     * @return Pilha com todos os ids vagos sendo o menor id no topo da lista.
     * @throws Exception 
     */
    public Stack<Integer> getIdsVagosClienteEventual(int qtdIdsVagos) throws Exception {
        Stack<Integer> result = new Stack<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from \n" +
                    "(SELECT id FROM generate_series(1, " + qtdIdsVagos + ")\n" +
                    "AS s(id) EXCEPT SELECT id FROM clienteeventual) AS codigointerno ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }
}