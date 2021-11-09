package vrimplantacao2_5.dao.atualizador;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;
import vrframework.classe.Conexao;
import vrimplantacao2_5.dao.cadastro.bancodados.BancoDadosDAO;
import vrimplantacao2_5.dao.cadastro.sistema.SistemaDAO;
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

    public void criarTabelas() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "CREATE TABLE IF NOT EXISTS implantacao2_5.bancodados(\n"
                    + "	id integer PRIMARY KEY NOT NULL,\n"
                    + "	nome VARCHAR(60) NOT NULL\n"
                    + ");"
                    + "\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.sistema(\n"
                    + "	id integer PRIMARY KEY NOT NULL,\n"
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
                    + " porta INTEGER, \n"
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
                    + " id_bancodados integer NOT NULL,\n"
                    + " complemento varchar(10) NULL,\n"
                    + " CONSTRAINT un_conexao_sistema UNIQUE (id_sistema, complemento),\n"
                    + " CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)\n"
                    + "         REFERENCES implantacao2_5.bancodados (id) MATCH SIMPLE\n"
                    + "      ON UPDATE NO ACTION ON DELETE NO ACTION,\n"
                    + " CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)\n"
                    + "         REFERENCES implantacao2_5.sistema (id) MATCH SIMPLE\n"
                    + "      ON UPDATE NO ACTION ON DELETE NO ACTION,\n"
                    + " CONSTRAINT un_sistema_bancodados_host_nomeschema_conexao UNIQUE (id_sistema, id_bancodados, host, nomeschema)\n"
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
                    + "\n"
                    + "ALTER TABLE implantacao2_5.conexaoloja \n"
                    + "ADD CONSTRAINT fk_id_lojadestino \n"
                    + "FOREIGN KEY (id_lojadestino) REFERENCES loja(id);"
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

    public void salvarSistemaBancoDados(ESistemaBancoDados eSistemaBancoDados) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "DELETE FROM implantacao2_5.sistemabancodados;"
                    + "INSERT INTO implantacao2_5.sistemabancodados ("
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
}
