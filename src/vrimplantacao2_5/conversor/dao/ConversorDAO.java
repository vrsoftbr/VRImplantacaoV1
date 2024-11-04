/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.conversor.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.postgresql.util.PSQLException;
import vr.core.collection.Properties;
import vr.implantacao.main.App;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao2_5.conversor.vo.ControleDadosConvertidosVO;

/**
 *
 * @author Michael
 */
public class ConversorDAO {

    private String nomeBanco = null;
    private String nomeDaTabela = null;
    private String arquivo;
    private int totalCabecalho = 0;
    private Map<String, String> opcoes = new LinkedHashMap<>();
    ConexaoPostgres con = new ConexaoPostgres();
    Properties prop = App.properties();
    private String ip = prop.get("database.ip");
    private String bancoProperties = prop.get("database.nome");
    private int porta = Integer.parseInt(prop.get("database.porta"));
    private String password = prop.get("database.senha");
    private String regexp = "([\\W])";

    public ConversorDAO() {

    }

    public int getTotalCabecalho() {
        return totalCabecalho;
    }

    public void setTotalCabecalho(int totalCabecalho) {
        this.totalCabecalho = totalCabecalho;
    }

    public String getNomeDaTabela() {
        return nomeDaTabela;
    }

    public void setNomeDaTabela(String nomeDaTabela) {
        this.nomeDaTabela = nomeDaTabela;
    }

    public void setArquivo(String nomeDaTabela) {
        this.arquivo = nomeDaTabela;
    }

    public void setNomeBanco(String nomeBanco) {
        this.nomeBanco = nomeBanco;
    }

    public String getNomeBanco() {
        return nomeBanco;
    }

    public Map<String, String> getOpcoes() {
        return opcoes;
    }

    public void criarBanco() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("CREATE DATABASE " + getNomeBanco());
        } catch (PSQLException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void criarTabelas(List<String> dados) throws Exception {
        List<ControleDadosConvertidosVO> dadosCOnvertidos = captaDadosConvertidos(getNomeBanco(), getNomeDaTabela());
        for (ControleDadosConvertidosVO dado : dadosCOnvertidos) {
            if (dado.getNomeTabela().trim().equals(getNomeDaTabela().trim())) {
                throw new Exception("A tabela já foi criada");
            }
        };
        String campos = "(\n";
        abrirConexao();
        try (Statement stm = con.getConexao().createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + getNomeDaTabela() + "\n";
            int contador = 0;
            for (String dado : dados) {
                if (contador < dados.size()) {
                    dado = dados.get(contador++);
                    campos += dado.replaceAll(regexp, "").trim().replace(",", "_") + " text,\n";//.replace("-", "").replace(" ", "").replace("\\", "").replace("/", "").replace(".", "").replace(",", "_") + " text,\n";
                }
            }
            System.out.println(sql + campos.substring(0, campos.length() - 2) + "\n);");
            stm.execute(
                    sql + campos.substring(0, campos.length() - 2) + "\n);"
            );
        } catch (PSQLException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        fecharConexao();
    }

    public ArrayList<String> captaCabecalhoTabela() throws Exception {
        ArrayList<String> result = new ArrayList<>();
        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        setTotalCabecalho(arq.getCabecalho().size());
        for (String linha : arq.getCabecalho()) {
            result.add(linha);
        }
        return result;
    }

    public void popularTabelas() throws Exception {
        List<ControleDadosConvertidosVO> dadosCOnvertidos = captaDadosConvertidos(getNomeBanco(), getNomeDaTabela());
        for (ControleDadosConvertidosVO dado : dadosCOnvertidos) {
            if ("SIM".equals(dado.getPopulada().trim().toUpperCase()) && dado.getNomeTabela().trim().equals(getNomeDaTabela().trim())) {
                throw new Exception("A tabela já foi populada");
            }
        };
        SQLBuilder sql = new SQLBuilder();
        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        abrirConexao();
        Statement stm = con.getConexao().createStatement();
        ArrayList<String> cabecalho = new ArrayList<>();
        sql.setTableName(getNomeDaTabela());
        for (String cabeca : arq.getCabecalho()) {
            cabecalho.add(cabeca);
        }

        for (LinhaArquivo linha : arq) {
            for (int i = 0; i < cabecalho.size(); i++) {
                String cabecalhoBase = cabecalho.get(i).replaceAll(regexp, "").trim().replace(",", "_");//.replace("-", "").replace(" ", "").replace("\\", "").replace("/", "").replace(".", "").replace(",", "_");
                sql.put(cabecalhoBase, linha.getString(cabecalho.get(i)));
            }
            try {
                stm.execute(sql.getInsert());

            } catch (PSQLException e) {
                System.out.println(sql.getInsert());
                System.out.println("PLSQLexception = " + e.getMessage());
                e.printStackTrace();
                throw e;
            } catch (Exception e) {
                System.out.println(sql.getInsert());
                System.out.println("exception = " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }

        fecharConexao();
    }

    public void criarControleDeDadosConvertidos() throws Exception {
        abrirConexao();
        try (Statement stm = con.getConexao().createStatement();) {
            stm.execute("CREATE SCHEMA IF NOT EXISTS conversao");
            String sql = "CREATE TABLE IF NOT EXISTS conversao.dados(\n"
                    + "    banco varchar,\n"
                    + "    nome_tabela varchar, \n"
                    + "    populada varchar\n"
                    + ");";
            stm.execute(sql);
            fecharConexao();
        }
    }

    public void insereDeDadosConvertidos(String banco, String tabela) throws Exception {
        abrirConexao();
        try (Statement stm = con.getConexao().createStatement();) {
            String sql = "insert into	conversao.dados\n"
                    + "(banco,	nome_tabela, populada)\n"
                    + "values ('" + banco + "', '" + tabela + "', 'não')";
            stm.execute(sql);
            fecharConexao();
        } catch (PSQLException e) {
            System.out.println("erro em insereDeDadosConvertidos = " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizaDeDadosConvertidos(String banco, String tabela) throws Exception {
        abrirConexao();
        try (Statement stm = con.getConexao().createStatement();) {
            String sql = "UPDATE conversao.dados SET \n"
                    + " populada = 'sim' \n"
                    + "WHERE \n"
                    + "banco = '" + banco + "'\n"
                    + "AND nome_tabela = '" + tabela + "'";
            stm.execute(sql);
            fecharConexao();
        } catch (PSQLException e) {
            System.out.println("erro em insereDeDadosConvertidos = " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<ControleDadosConvertidosVO> captaDadosConvertidos(String banco, String tabela) throws Exception {
        abrirConexao();
        List<ControleDadosConvertidosVO> result = new ArrayList<>();

        try (Statement stm = con.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	distinct \n"
                    + "	banco,\n"
                    + "	nome_tabela tabela,\n"
                    + "	populada\n"
                    + "from\n"
                    + "	conversao.dados"
            )) {
                while (rst.next()) {
                    ControleDadosConvertidosVO dados = new ControleDadosConvertidosVO();
                    dados.setBanco(rst.getString("banco"));
                    dados.setNomeTabela(rst.getString("tabela"));
                    dados.setPopulada(rst.getString("populada"));
                    result.add(dados);
                }
            }
        }
        return result;
    }

    public void abrirConexao() throws Exception {
        con.abrirConexao(ip, porta, getNomeBanco(), "postgres", password);
    }

    public void fecharConexao() throws Exception {
        con.close();
    }

    public void criarControleDeDadosConvertidosProperties() throws Exception {
        con.abrirConexao(ip, porta, bancoProperties, "postgres", password);
        try (Statement stm = con.getConexao().createStatement();) {
            stm.execute("CREATE SCHEMA IF NOT EXISTS conversao");
            String sql = "CREATE TABLE IF NOT EXISTS conversao.dados(\n"
                    + "    banco varchar,\n"
                    + "    nome_tabela varchar, \n"
                    + "    populada varchar\n"
                    + ");";
            stm.execute(sql);
            con.close();
        }
    }
}
