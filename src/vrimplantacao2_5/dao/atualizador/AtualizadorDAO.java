package vrimplantacao2_5.dao.atualizador;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;
import vrframework.classe.Conexao;

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
                    + "where nome = '" + eBancoDados + "'"
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
                    + "where nome = '" + eSistema + "'"
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

    public void criarTabelas() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "CREATE TABLE IF NOT EXISTS implantacao2_5.bancodados(\n"
                    + "	id serial PRIMARY KEY NOT NULL,\n"
                    + "	nome VARCHAR(60) NOT NULL\n"
                    + ");"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.sistema(\n"
                    + "	id serial PRIMARY KEY NOT NULL,\n"
                    + "	nome VARCHAR(60) NOT NULL\n"
                    + ");\n"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.sistemabancodados(\n"
                    + "	id serial PRIMARY KEY NOT NULL,\n"
                    + "	id_sistema INTEGER NOT NULL,\n"
                    + "	id_bancodados INTEGER NOT NULL,\n"
                    + "	nomeschema VARCHAR(60),\n"
                    + "	usuario VARCHAR(30),\n"
                    + "	senha VARCHAR(30),\n"
                    + "	CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)\n"
                    + "		REFERENCES implantacao2_5.sistema (id),\n"
                    + "	CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)\n"
                    + "		REFERENCES implantacao2_5.bancodados (id),\n"
                    + "	CONSTRAINT un_sistema_bancodados \n"
                    + "		UNIQUE (id_sistema, id_bancodados)\n"
                    + ");"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.conexao(\n"
                    + "	id serial PRIMARY KEY NOT NULL,\n"
                    + "	host VARCHAR(20) NOT NULL,\n"
                    + " nomeschema VARCHAR(255) NOT NULL,"
                    + "	porta INTEGER NOT NULL,\n"
                    + "	usuario VARCHAR(30) NOT NULL,\n"
                    + "	senha VARCHAR(30) NOT NULL,\n"
                    + "	descricao VARCHAR(100),\n"
                    + " id_sistema integer NOT NULL,\n"
                    + " id_bancodados integer NOT NULL,"
                    + " CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)\n"
                    + "         REFERENCES implantacao2_5.bancodados (id) MATCH SIMPLE\n"
                    + "      ON UPDATE NO ACTION ON DELETE NO ACTION,\n"
                    + " CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)\n"
                    + "         REFERENCES implantacao2_5.sistema (id) MATCH SIMPLE\n"
                    + "      ON UPDATE NO ACTION ON DELETE NO ACTION,\n"
                    + " CONSTRAINT un_sistema_bancodados_conexao UNIQUE (id_sistema, id_bancodados)"
                    + ");"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.conexaoloja(\n"
                    + "	id serial PRIMARY KEY NOT NULL,\n"
                    + "	id_conexao INTEGER NOT NULL,\n"
                    + "	id_lojaorigem VARCHAR NOT NULL,\n"
                    + "	id_lojadestino INTEGER NOT NULL,\n"
                    + "	datacadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),\n"
                    + "	id_situacaomigracao INTEGER NOT NULL,\n"
                    + "	lojamatriz BOOLEAN,\n"
                    + "	CONSTRAINT fk_if_conexao FOREIGN KEY (id_conexao)\n"
                    + "		REFERENCES implantacao2_5.conexao(id)\n"
                    + ");"
            );
        }
    }

    public void inserirTabelaBancoDados(EBancoDados eBancoDados) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("INSERT INTO implantacao2_5.bancodados (nome) VALUES ('" + eBancoDados + "');");
        }
    }

    public void inserirTabelaSistema(ESistema eSistema) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("INSERT INTO implantacao2_5.sistema (nome) VALUES ('" + eSistema + "')");
        }
    }

    public void inserirTabelaSistemaBancoDados() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "DELETE FROM implantacao2_5.sistemabancodados; \n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ACCESYS'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'SQLSERVER'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ACOM'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'SQLSERVER'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ALPHASYS'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'FIREBIRD'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'APOLLO'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'ORACLE'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ARIUS'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'ORACLE'), 'PROREG', 'automa', null);\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ARTSYSTEM'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'SQLSERVER'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ASEFE'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'SQLSERVER'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ASOFT'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'FIREBIRD'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ATENAS'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'FIREBIRD'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ATHOS'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'POSTGRESQL'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'ATMA'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'SQLSERVER'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'AUTOADM'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'FIREBIRD'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'AUTOCOM'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'FIREBIRD'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'AUTOSYSTEM'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'POSTGRESQL'));\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'AVANCE'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'MYSQL'), 'root', 'infor', null);\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'AVISTARE'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'SQLSERVER')); \n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'BASE'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'ACCESS'));	 	\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome = 'BRAJANGESTORES'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome = 'POSTGRESQL'));"
            );
        }
    }
}
