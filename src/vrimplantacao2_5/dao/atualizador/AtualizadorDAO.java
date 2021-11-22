package vrimplantacao2_5.dao.atualizador;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;
import vrframework.classe.Conexao;
import vrimplantacao2_5.vo.enums.EScriptLojaOrigemSistema;
import vrimplantacao2_5.vo.enums.ESistemaBancoDados;

/**
 *
 * @author Desenvolvimento
 */
public class AtualizadorDAO {

    public boolean verificarBancoDados(EBancoDados eBancoDados) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select nome \n"
                    + "from implantacao2_5.bancodados \n"
                    + "where nome = '" + eBancoDados.getNome() + "'"
            )) {
                return rs.next();
            }
        }
    }

    public boolean verificarSistema(ESistema eSistema) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select nome \n"
                    + "from implantacao2_5.sistema \n"
                    + "where nome = '" + eSistema.getNome() + "'"
            )) {
                return rs.next();
            }
        }
    }

    public void criarSchema() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("CREATE SCHEMA IF NOT EXISTS implantacao2_5");
        }
    }

    public void criarConstraint() throws Exception {
        try (Statement stm = Conexao.createStatement()) { 
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct conname "
                    + "from pg_catalog.pg_constraint "
                    + "WHERE conname = 'fk_id_lojadestino'"
            )) {
                if (!rst.next()) {
                    stm.execute(
                            "ALTER TABLE implantacao2_5.conexaoloja \n"
                            + "ADD CONSTRAINT fk_id_lojadestino \n"
                            + "FOREIGN KEY (id_lojadestino) REFERENCES loja(id);"
                    );
                }
            }
        }
    }

    public void criarTabelas() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "CREATE TABLE IF NOT EXISTS implantacao2_5.unidade \n"
                    + "(\n"
                    + "    id integer PRIMARY KEY NOT NULL,\n"
                    + "    nome character varying(60) NOT NULL, \n"
                    + "    id_municipio integer NOT NULL,\n"
                    + "    id_estado integer NOT NULL,\n"
                    + "    CONSTRAINT un_unidade UNIQUE (nome, id_municipio, id_estado),\n"
                    + "    CONSTRAINT fk_id_estado FOREIGN KEY (id_estado)\n"
                    + "        REFERENCES public.estado (id) MATCH SIMPLE\n"
                    + "        ON UPDATE NO ACTION\n"
                    + "        ON DELETE NO ACTION,\n"
                    + "    CONSTRAINT fk_id_municipio FOREIGN KEY (id_municipio)\n"
                    + "        REFERENCES public.municipio (id) MATCH SIMPLE\n"
                    + "        ON UPDATE NO ACTION\n"
                    + "        ON DELETE NO ACTION\n"
                    + ");"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.usuario\n" 
                    + "(\n"
                    + "    id integer PRIMARY KEY NOT NULL,\n"
                    + "    nome character varying(30) NOT NULL,\n"
                    + "    login character varying(30) NOT NULL,\n"
                    + "    senha character varying(30) NOT NULL,\n"
                    + "    id_unidade integer NOT NULL,\n"
                    + "    CONSTRAINT fk_id_unidade FOREIGN KEY (id_unidade)\n"
                    + "        REFERENCES implantacao2_5.unidade (id) MATCH SIMPLE\n"
                    + "        ON UPDATE NO ACTION\n"
                    + "        ON DELETE NO ACTION\n"
                    + ");"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.bancodados(\n"
                    + "     id integer PRIMARY KEY NOT NULL,\n"
                    + "     nome VARCHAR(60) NOT NULL\n"
                    + ");"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.sistema(\n"
                    + "     id integer PRIMARY KEY NOT NULL,\n"
                    + "     nome VARCHAR(60) NOT NULL\n"
                    + ");\n"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.sistemabancodados(\n"
                    + "     id serial PRIMARY KEY NOT NULL,\n"
                    + "     id_sistema INTEGER NOT NULL,\n"
                    + "     id_bancodados INTEGER NOT NULL,\n"
                    + "     nomeschema VARCHAR(200),\n"
                    + "     usuario VARCHAR(30),\n"
                    + "     senha VARCHAR(30),\n"
                    + "     porta INTEGER, \n"
                    + "     CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)\n"
                    + "		REFERENCES implantacao2_5.sistema (id),\n"
                    + "     CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)\n"
                    + "		REFERENCES implantacao2_5.bancodados (id),\n"
                    + "     CONSTRAINT un_sistema_bancodados \n"
                    + "		UNIQUE (id_sistema, id_bancodados)\n"
                    + ");"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.conexao(\n"
                    + "     id serial PRIMARY KEY NOT NULL,\n"
                    + "     host VARCHAR(20) NOT NULL,\n"
                    + "     nomeschema VARCHAR(255) NOT NULL,"
                    + "     porta INTEGER NOT NULL,\n"
                    + "     usuario VARCHAR(30) NOT NULL,\n"
                    + "     senha VARCHAR(30) NOT NULL,\n"
                    + "     descricao VARCHAR(100),\n"
                    + "     id_sistema integer NOT NULL,\n"
                    + "     id_bancodados integer NOT NULL,\n"
                    + "     complemento varchar(10) NULL,\n"
                    + "     CONSTRAINT un_conexao_sistema UNIQUE (id_sistema, complemento),\n"
                    + "     CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)\n"
                    + "         REFERENCES implantacao2_5.bancodados (id) MATCH SIMPLE\n"
                    + "      ON UPDATE NO ACTION ON DELETE NO ACTION,\n"
                    + "     CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)\n"
                    + "         REFERENCES implantacao2_5.sistema (id) MATCH SIMPLE\n"
                    + "      ON UPDATE NO ACTION ON DELETE NO ACTION,\n"
                    + "     CONSTRAINT un_sistema_bancodados_host_nomeschema_conexao UNIQUE (id_sistema, id_bancodados, host, nomeschema)\n"
                    + ");"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.conexaoloja(\n"
                    + "     id serial PRIMARY KEY NOT NULL,\n"
                    + "     id_conexao INTEGER NOT NULL,\n"
                    + "     id_lojaorigem VARCHAR NOT NULL,\n"
                    + "     id_lojadestino INTEGER NOT NULL,\n"
                    + "     datacadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),\n"
                    + "     id_situacaomigracao INTEGER NOT NULL,\n"
                    + "     lojamatriz BOOLEAN,\n"
                    + "     CONSTRAINT fk_if_conexao FOREIGN KEY (id_conexao)\n"
                    + "		REFERENCES implantacao2_5.conexao(id)\n"
                    + ");\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.sistemabancodadosscripts (\n"
                    + "    id serial PRIMARY KEY NOT NULL,\n"
                    + "    id_sistema integer NOT NULL,\n"
                    + "    id_bancodados integer NOT NULL,\n"
                    + "    script_getlojas text COLLATE pg_catalog.\"default\",\n"
                    + "    CONSTRAINT un_sistema_bancodados_scripts UNIQUE (id_sistema, id_bancodados),\n"
                    + "    CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)\n"
                    + "        REFERENCES implantacao2_5.bancodados (id) MATCH SIMPLE\n"
                    + "        ON UPDATE NO ACTION\n"
                    + "        ON DELETE NO ACTION,\n"
                    + "    CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)\n"
                    + "        REFERENCES implantacao2_5.sistema (id) MATCH SIMPLE\n"
                    + "        ON UPDATE NO ACTION\n"
                    + "        ON DELETE NO ACTION\n"
                    + ");\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.lojaorigem (\n"
                    + "     id VARCHAR(10) NOT NULL,\n"
                    + "     descricao VARCHAR(100) NOT NULL,\n"
                    + "     mixprincipal boolean default false,\n"
                    + "     id_conexaoloja INTEGER,\n"
                    + "     CONSTRAINT fk_id_conexaoloja FOREIGN KEY (id_conexaoloja)\n"
                    + "        REFERENCES implantacao2_5.conexaoloja (id)\n"
                    + ");\n"
                    + "ALTER TABLE implantacao.codant_produto ADD COLUMN IF NOT EXISTS id_conexao INTEGER;"
            );
        }
    }

    public void salvarBancoDados(EBancoDados eBancoDados) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "INSERT INTO implantacao2_5.bancodados (id, nome) \n"
                    + "VALUES (\n"
                    + eBancoDados.getId() + ", \n"
                    + "'" + eBancoDados.getNome() + "'\n"
                    + ");");
        }
    }

    public void salvarSistema(ESistema eSistema) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("INSERT INTO implantacao2_5.sistema (id, nome) \n"
                    + "VALUES (\n"
                    + eSistema.getId() + ", \n"
                    + "'" + eSistema.getNome() + "'\n"
                    + ")");
        }
    }

    public void deletarSistemaBancoDados() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("DELETE FROM implantacao2_5.sistemabancodados;");
        }
    }

    public void salvarSistemaBancoDados(ESistemaBancoDados eSistemaBancoDados) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "INSERT INTO implantacao2_5.sistemabancodados ("
                    + "id_sistema, "
                    + "id_bancodados, "
                    + "usuario, "
                    + "senha, "
                    + "nomeschema, "
                    + "porta"
                    + ")\n"
                    + "VALUES ("
                    + "(SELECT id FROM implantacao2_5.sistema\n"
                    + "  WHERE nome = '" + eSistemaBancoDados.getNomeSistema() + "'),\n"
                    + "(SELECT id FROM implantacao2_5.bancodados\n"
                    + "  WHERE nome = '" + eSistemaBancoDados.getNomeBancoDados() + "'), "
                    + "'" + eSistemaBancoDados.getUsuario() + "', "
                    + "'" + eSistemaBancoDados.getSenha() + "', "
                    + "'" + eSistemaBancoDados.getNomeSchema() + "', "
                    + eSistemaBancoDados.getPorta() + ");"
            );
        }
    }

    public void deletarScriptGetLojaOrigemSistemas() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("DELETE FROM implantacao2_5.sistemabancodadosscripts");
        }
    }
    
    public void salvarScriptGetLojaOrigemSistemas(EScriptLojaOrigemSistema eScriptLojaOrigemSistema) throws Exception {
        
        String sql = "INSERT INTO implantacao2_5.sistemabancodadosscripts("
                    + "id_sistema, "
                    + "id_bancodados, "
                    + "script_getlojas"
                    + ")\n"
                    + "VALUES ("
                    + eScriptLojaOrigemSistema.getIdSistema() + ", "
                    + eScriptLojaOrigemSistema.getIdBancoDados() + ", "
                    + "'" + eScriptLojaOrigemSistema.getScriptGetLojaOrigem() + "');";
        
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql);
        }
    }
    
    public void inserirUnidade() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("DELETE FROM implantacao2_5.usuario; \n"
                    + "DELETE FROM implantacao2_5.unidade \n;"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) "
                    + "VALUES (1, 'VR MATRIZ', 3526902, 35);");
        }
    }

    public void inserirUsuario() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("DELETE FROM implantacao2_5.usuario; \n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (1, 'GUILHERME', 'GUILHERME', 'ZIRDA123', 1); \n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (2, 'LUCAS', 'LUCAS', 'ZIRDA123', 1); \n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (3, 'ALAN', 'ALAN', 'ZIRDA123', 1); \n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (4, 'WAGNER', 'WAGER', 'ZIRDA123', 1);");
        }
    }
}
