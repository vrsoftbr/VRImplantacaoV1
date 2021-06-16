package vr.implantacao.dao.atualizador;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vr.implantacao.vo.enums.EBancoDados;
import vr.implantacao.vo.enums.ESistema;
import vrframework.classe.Conexao;

/**
 *
 * @author Desenvolvimento
 */

public class AtualizadorDAO {

    private List<String> verificarBancoDados() throws Exception {
        List<String> result = null;
        for (EBancoDados bancoDados : EBancoDados.values()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select nome \n"
                        + "from implantacao2_5.bancodados \n"
                        + "where nome = '" + bancoDados + "'"
                )) {
                    if (!rs.next()) {
                        result = new ArrayList<>();
                        result.add(rs.getString("nome"));
                    }
                }
            }
        }
        return result;
    }
    
    private List<String> verificarSistema() throws Exception {
        List<String> result = null;
        for (ESistema sistema : ESistema.values()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select nome \n"
                        + "from implantacao2_5.sistema \n"
                        + "where nome = '" + sistema + "'"
                )) {
                    if (!rs.next()) {
                        result = new ArrayList<>();
                        result.add(rs.getString("nome"));
                    }
                }
            }
        }
        return result;
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
                    + "	porta INTEGER NOT NULL,\n"
                    + "	usuario VARCHAR(30) NOT NULL,\n"
                    + "	senha VARCHAR(30) NOT NULL,\n"
                    + "	descricao VARCHAR (60),\n"
                    + "	id_sistemabancodados INTEGER NOT NULL,\n"
                    + "	CONSTRAINT fk_id_sistemabancodados FOREIGN KEY (id_sistemabancodados)\n"
                    + "		REFERENCES implantacao2_5.sistemabancodados (id)\n"
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

    public void inserirTabelaBancoDados() throws Exception {
        List<String> bancoDados = new ArrayList<>();
        bancoDados = verificarBancoDados();

        for (String bancoDado : bancoDados) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute("INSERT INTO implantacao2_5.bancodados (nome) VALUES ('" + bancoDado + "');");
            }
        }
    }

    public void inserirTabelaSistema() throws Exception {
        List<String> sistemas = new ArrayList<>();
        sistemas = verificarSistema();

        for (String sistema : sistemas) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute("INSERT INTO implantacao2_5.sistema (nome) VALUES ('" + sistema + "')");
            }
        }
    }

    public void inserirTabelaSistemaBancoDados() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%SYSPDV%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'masterkey', null);\n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%RKSOFTWARE%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'Office25', null);\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%LINEAR%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%MYSQL%'), 'adminlinear', '@2013linear', 'sglinx');\n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%ISERVER%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%MYSQL%'), 'root', '750051', 'db_iserver');\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%TSTI%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%MYSQL%'), 'tsti', '1234', 'tsl');\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%SIRCOM%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'masterkey', null);\n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%INTELICASH%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'masterkey', null);\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%FUTURA%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'sbofutura', 'Dados.fdb');\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%LOGUS%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%INFORMIX%'), 'informix', 'loooge', null);\n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%SIIT%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%MYSQL%'), 'root', 'JesusCristo', null);\n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%AVANCE%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%MYSQL%'), 'root', 'infor', null);\n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%GZSISTEMAS%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%MYSQL%'), 'root', 'mestre', null);\n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%SIACRIARE%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%MYSQL%'), 'root', 'Hs8Tw13kPx7uDPs', null);\n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%HIPCOM%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%MYSQL%'), 'root', 'hpc00', null);\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%ARIUS%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%ORACLE%'), 'PROREG', 'automa', null);\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%SUPERUS'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%ORACLE%'), 'xe', 'smart', null);\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%DEVMASTER'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%POSTGRESQL%'), 'devmaster', 'devmaster', null); \n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%BRAJAN%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%POSTGRESQL%'), 'postgres', 'orple', null); \n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%NCA%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%POSTGRESQL%'), 'postgres', 'post', null);  \n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%UNIPLUS%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%POSTGRESQL%'), 'postgres', 'postgres', null);   \n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%ATHOS%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%POSTGRESQL%'), 'athos', 'j2mhw82dyu1kn5g4', null);   	 \n"
                    + "\n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%RESULTMAIS%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%POSTGRESQL%'), 'postgres', 'rmpostgres', 'rmbancodados');   	  \n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%DIRECTOR%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%SQL SERVER%'), 'sa', '#1qwer0987', null);   	   \n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%ATENAS%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%SQL SERVER%'), 'sa', 'personal', 'nca_adm'); \n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%TELECON%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%SQL SERVER%'), 'sa', 'a2m8x7h5', 'GESTAO');   	     \n"
                    + "\n"
                    + "INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)\n"
                    + "VALUES ((SELECT id FROM implantacao2_5.sistema\n"
                    + "	 WHERE nome LIKE '%POLIGON%'),\n"
                    + "	 (SELECT id FROM implantacao2_5.bancodados\n"
                    + "	 WHERE nome LIKE '%SQL SERVER%'), 'sa', 'Pol!gon5oft', 'PADARIA');   	     "
            );
        }
    }

    public void criarEstrutura() throws Exception {
        criarSchema();
        criarTabelas();
        inserirTabelaBancoDados();
        inserirTabelaSistema();
        //inserirTabelaSistemaBancoDados();
    }
}
