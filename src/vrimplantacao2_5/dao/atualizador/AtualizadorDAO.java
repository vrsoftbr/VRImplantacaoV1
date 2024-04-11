package vrimplantacao2_5.dao.atualizador;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;
import vrframework.classe.Conexao;
import vrimplantacao2_5.dao.cadastro.sistemabancodados.SistemaBancoDadosDAO;
import vrimplantacao2_5.dao.cadastro.usuario.UsuarioDAO;
import vrimplantacao2_5.vo.enums.EMetodo;
import vrimplantacao2_5.vo.enums.EScriptLojaOrigemSistema;
import vrimplantacao2_5.vo.enums.ESistemaBancoDados;
import vrimplantacao2_5.vo.enums.ETipoOperacao;
import vrimplantacao2_5.vo.enums.EUnidade;

/**
 *
 * @author Desenvolvimento
 */
public class AtualizadorDAO {

    private SistemaBancoDadosDAO dao = new SistemaBancoDadosDAO();
    private String validaDados = null;

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

    public int verificarLogUsuario() throws Exception {

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct id_usuario from implantacao2_5.operacao"
            )) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        }

        return 0;
    }

    public void criarSchema() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("CREATE SCHEMA IF NOT EXISTS implantacao2_5");
        }
    }

    public void criarCampoDataImportacao() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "alter table implantacao.codant_clientepreferencial \n"
                    + "add column if not exists dataimportacao timestamp default now();\n"
                    + "alter table implantacao.codant_clienteeventual \n"
                    + "add column if not exists dataimportacao timestamp default now();\n"
                    + "alter table implantacao.codant_ean \n"
                    + "add column if not exists dataimportacao timestamp default now();\n"
                    + "alter table implantacao.codant_fornecedor \n"
                    + "add column if not exists dataimportacao timestamp default now();"
            );
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
                    + "    senha character varying(255) NOT NULL,\n"
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
                    + "     id VARCHAR(20) NOT NULL,\n"
                    + "     descricao VARCHAR(100) NOT NULL,\n"
                    + "     mixprincipal boolean default false,\n"
                    + "     id_conexaoloja INTEGER,\n"
                    + "     CONSTRAINT fk_id_conexaoloja FOREIGN KEY (id_conexaoloja)\n"
                    + "        REFERENCES implantacao2_5.conexaoloja (id)\n"
                    + ");\n"
                    + "ALTER TABLE implantacao.codant_produto ADD COLUMN IF NOT EXISTS id_conexao INTEGER;\n"
                    + "ALTER TABLE implantacao.codant_fornecedor ADD COLUMN IF NOT EXISTS id_conexao INTEGER;\n"
                    + "ALTER TABLE implantacao.codant_clientepreferencial ADD COLUMN IF NOT EXISTS id_conexao INTEGER;\n"
                    + "ALTER TABLE implantacao.codant_clienteeventual ADD COLUMN IF NOT EXISTS id_conexao INTEGER;\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.metodo \n"
                    + "(\n"
                    + "	id INTEGER NOT NULL PRIMARY KEY,\n"
                    + "	descricao CHARACTER VARYING (60) NOT NULL \n"
                    + ");\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.tipooperacao \n"
                    + "(\n"
                    + "     id INTEGER NOT NULL PRIMARY KEY,\n"
                    + "     descricao CHARACTER VARYING(60) NULL NULL,\n"
                    + "     id_metodo INTEGER NOT NULL,\n"
                    + "     CONSTRAINT fk_metodo FOREIGN KEY (id_metodo) REFERENCES implantacao2_5.metodo(id)\n"
                    + ");\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.operacao \n"
                    + "(\n"
                    + "     id serial NOT NULL PRIMARY KEY, \n"
                    + "     id_usuario INTEGER NOT NULL,\n"
                    + "     datahora TIMESTAMP NOT NULL,\n"
                    + "     id_tipooperacao INTEGER NOT NULL,\n"
                    + "     id_loja INTEGER NOT NULL,\n"
                    + "     CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES implantacao2_5.usuario(id),\n"
                    + "     CONSTRAINT fk_tipooperacao FOREIGN KEY (id_tipooperacao) REFERENCES implantacao2_5.tipooperacao(id),\n"
                    + "     CONSTRAINT fk_loja FOREIGN KEY (id_loja) REFERENCES public.loja(id)\n"
                    + ");\n"
                    + "CREATE TABLE IF NOT EXISTS implantacao2_5.dadossistemagenerico (\n"
                    + "    id_banco text,\n"
                    + "    banco text,\n"
                    + "    sistema text,\n"
                    + "    id_sistema int4,\n"
                    + "    script_getlojas text\n"
                    + ");"
                    + "ALTER TABLE implantacao2_5.usuario ALTER COLUMN senha TYPE varchar(255);"
            );
        }
    }

    public void alterarTabelas() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "ALTER TABLE implantacao2_5.sistemabancodados ADD COLUMN IF NOT EXISTS observacao text;");
        }
    }

    public void salvarBancoDados(EBancoDados eBancoDados) throws Exception {
        if (validarGenerico()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(
                        "INSERT INTO implantacao2_5.bancodados (id, nome) \n"
                        + "VALUES (\n"
                        + eBancoDados.getId() + ", \n"
                        + "'" + eBancoDados.getNome() + "'\n"
                        + ");");
            }
        }
    }

    public void salvarSistema(ESistema eSistema) throws Exception {
        if (validarGenerico()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute("INSERT INTO implantacao2_5.sistema (id, nome) \n"
                        + "VALUES (\n"
                        + eSistema.getId() + ", \n"
                        + "'" + eSistema.getNome() + "'\n"
                        + ")");
            }
        }
    }

    public void deletarSistemaBancoDados() throws Exception {
        if (validarGenerico()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute("DELETE FROM implantacao2_5.sistemabancodados;");
            }
        }
    }

    public void salvarSistemaBancoDados(ESistemaBancoDados eSistemaBancoDados) throws Exception {
        if (validarGenerico()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(
                        "INSERT INTO implantacao2_5.sistemabancodados ("
                        + "id_sistema, "
                        + "id_bancodados, "
                        + "usuario, "
                        + "senha, "
                        + "nomeschema, "
                        + "porta,"
                        + "observacao"
                        + ")\n"
                        + "VALUES ("
                        + "(SELECT id FROM implantacao2_5.sistema\n"
                        + "  WHERE nome = '" + eSistemaBancoDados.getNomeSistema() + "'),\n"
                        + "(SELECT id FROM implantacao2_5.bancodados\n"
                        + "  WHERE nome = '" + eSistemaBancoDados.getNomeBancoDados() + "'), "
                        + "'" + eSistemaBancoDados.getUsuario() + "', "
                        + "'" + eSistemaBancoDados.getSenha() + "', "
                        + "'" + eSistemaBancoDados.getNomeSchema() + "', "
                        + eSistemaBancoDados.getPorta() + ", "
                        + "'" + eSistemaBancoDados.getObservacao() + "');"
                );
            }
        }
    }

    public void deletarScriptGetLojaOrigemSistemas() throws Exception {
        if (validarGenerico()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute("DELETE FROM implantacao2_5.sistemabancodadosscripts");
            }
        }
    }

    public void salvarScriptGetLojaOrigemSistemas(EScriptLojaOrigemSistema eScriptLojaOrigemSistema) throws Exception {
        if (validarGenerico()) {
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
    }

    public void inserirUnidade() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (1, 'VR MATRIZ', 3526902, 35) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (2, 'VR BAURU', 3506003, 35) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (3, 'VR BELEM', 1501402, 15) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (4, 'VR FLORIANOPOLIS', 4205407, 42) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (5, 'VR FORTALEZA', 2304400, 23) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (6, 'VR GOIANIA', 5208707, 52) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (7, 'VR RECIFE', 2611606, 26) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (8, 'VR RJ', 3304557, 33) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (9, 'VR SALVADOR', 2927408, 29) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (10, 'VR SP', 3550308, 35) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (11, 'VR SP ZL', 3550308, 35) on conflict (id) do nothing;"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (12, 'VR MG', 3170206, 31) on conflict (id) do nothing;\n"
                    + "INSERT INTO implantacao2_5.unidade(id, nome, id_municipio, id_estado) VALUES (13, 'VR SP RP', 3543402, 35) on conflict (id) do nothing;");
        }
    }

    public void inserirUsuario() throws Exception {
        /*
        * Ao excluir um usuario, favor excluir ou alterar o mesmo no método atualizarSenhas();
        * O método está nessa mesma classe.
         */
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (1, 'BRUNO', 'BRUNO', '$2a$12$JWSRLo/HDhp2CrdL02xH8uZifJ7xZ.ZO/gRATwrukosu0Y58.OhVa', " + EUnidade.VR_MATRIZ.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (2, 'GUSTAVO', 'GUSTAVO', '$2a$12$LzL/NWCEtWg85ntxeGFiLOHqci/Lge2w/nihRV1WaGt5YxrXNwRcm', " + EUnidade.VR_MATRIZ.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (3, 'WAGNER', 'WAGNER', '$2a$12$.8DmNiwGEEXV/M1zxYP43uBOi2rQs3Q/7wxPe5Lbm7poAWyjnoozm', " + EUnidade.VR_MATRIZ.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (4, 'MICHAEL', 'MICHAEL', '$2a$12$NKdzIm0k/.QbltTcFDiyLOTxBCe9Mi01xHU/v93MdZIcvwESY404.', " + EUnidade.VR_MATRIZ.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (5, 'RODRIGO', 'RODRIGO', '$2a$12$JWSRLo/HDhp2CrdL02xH8uZifJ7xZ.ZO/gRATwrukosu0Y58.OhVa', " + EUnidade.VR_UBERLANDIA.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (6, 'IMP-BAURU', 'VRSP-BAU', '$2a$12$kKTEKlxo9Np5H1P2Vucaoe3EV6VMH8rzLmqt.XVMvnwrUL6Kl.vQm', " + EUnidade.VR_BAURU.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (7, 'IMP-BELEM', 'VRPA', '$2a$12$Et.db7kSw45KOPbZPqNhQuMFh53DRf84MJCVsvrBqIxby72EFzySy', " + EUnidade.VR_BELEM.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (8, 'IMP-FLORIANOPOLIS', 'VRSC', '$2a$12$qbyO3.wSN9b880HN21yvUelZemyWgOMhgq47rayRohuNOI82TNUqa', " + EUnidade.VR_FLORIANOPOLIS.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (9, 'IMP-FORTALEZA', 'VRCE', '$2a$12$AcCAvOts7q.tZpiKKHj/A.8Wcm3j8nVDb.SXgGWtHCOHYaSHUcnj.', " + EUnidade.VR_FORTALEZA.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (10, 'IMP-GOIANIA', 'VRGO', '$2a$12$5dMabsRMCMqpusFoLlF04utvRztG68gXzME0oywJry.tW2kviKn8O', " + EUnidade.VR_GOIANIA.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (11, 'IMP-RECIFE', 'VRPE', '$2a$12$u9zeWz77tQf6SQv0KhZi9u6zgtb7P5Gf3URPjtrejtWhfVB2bWKFi', " + EUnidade.VR_RECIFE.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (12, 'IMP-RJ', 'VRRJ', '$2a$12$92Puj08mF7a.lgArkJjc/.WtLLjAy9csQSLexIpqckXzT5L51CPl6', " + EUnidade.VR_RIO.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (13, 'IMP-SALVADOR', 'VRBA', '$2a$12$F1V/qUbIj4AyT4q5kastUur.Fv.avpXRGK7ZxEUYJzljVGYtT9wV2', " + EUnidade.VR_SALVADOR.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (14, 'IMP-SP', 'VRSP', '$2a$12$0jkS4F7B8fORQN1Z6fZuduReaqrI3Z5dIy7hf8.17ow5Eq3nPbZw.', " + EUnidade.VR_SAO_PAULO.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (15, 'IMP-SP_ZL', 'VRSP-ZL', '$2a$12$mbGK2/TbDKgEU7ZRHuNzwufR7A3aPd29EPVBRrswpVLAJhfpGGQjS', " + EUnidade.VR_SAO_PAULO_ZL.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (16, 'IMP-MG', 'VRMG', '$2a$12$foWrXPAxW88wBR5zWZxMrOFph6mYDpPFWKd8bYQN1Lt48o4zNkVye', " + EUnidade.VR_UBERLANDIA.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (17, 'JOSE', 'JOSE LAMONTANHA', '$2a$12$9him78m3Re0fM2uoj8DGmeu8ebdFHc/OSQgS1ZHyb98V.73vhYqO.', " + EUnidade.VR_GOIANIA.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (18, 'MARCIO JORDAO', 'MARCIO', '$2a$12$Tc2AnzRpK30ahUI290AuHu3bhmQLuzR9Yby3jV5.89rBD3sYLwBFm', " + EUnidade.VR_RIBEIRAPRETO.getId() + ") ON CONFLICT (id) DO NOTHING;\n"
                    + "INSERT INTO implantacao2_5.usuario(id, nome, login, senha, id_unidade) VALUES (19, 'SUPORTE', 'SUPORTE', '$2a$12$Y7xCtYzd82dRkDzVl72MS.wH5.KqyBDkjJc57wUTFcyKGdEgVHjGS', " + EUnidade.VR_MATRIZ.getId() + ") ON CONFLICT (id) DO NOTHING;");
        }
    }

    public void salvarMetodo(EMetodo eMetodo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id FROM implantacao2_5.metodo where descricao = '" + eMetodo.getDescricao() + "'"
            )) {
                if (!rst.next()) {
                    stm.execute("INSERT INTO implantacao2_5.metodo (id, descricao) "
                            + "VALUES "
                            + "(" + eMetodo.getId() + ", '" + eMetodo.getDescricao() + "');");
                }
            }
        }
    }

    public void salvarTipoOperacao(ETipoOperacao eTipoOperacao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from implantacao2_5.tipooperacao \n"
                    + "where descricao = '" + eTipoOperacao.getDescricao() + "' \n"
                    + "and id_metodo = " + eTipoOperacao.getIdMetodo()
            )) {
                if (!rst.next()) {
                    stm.execute("INSERT INTO implantacao2_5.tipooperacao( \n"
                            + "id, descricao, id_metodo) \n"
                            + "VALUES "
                            + "("
                            + eTipoOperacao.getId() + ", "
                            + "'" + eTipoOperacao.getDescricao() + "', "
                            + eTipoOperacao.getIdMetodo() + ");");
                }
            }
        }
    }

    public boolean validarGenerico() throws Exception {
        validaDados = dao.verificaDadosSistemaGenerico();
        if (validaDados == null || validaDados.toLowerCase().contains("vazio")) {
            return true;
        } else {
            return false;
        }
    }

    public void atualizarSenhas() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("do $$\n"
                    + "begin\n"
                    + "   update implantacao2_5.usuario set login = 'BRUNO', senha = '$2a$12$JWSRLo/HDhp2CrdL02xH8uZifJ7xZ.ZO/gRATwrukosu0Y58.OhVa' where id = 1;\n"
                    + "   update implantacao2_5.usuario set login = 'GUSTAVO', senha = '$2a$12$LzL/NWCEtWg85ntxeGFiLOHqci/Lge2w/nihRV1WaGt5YxrXNwRcm' where id = 2;\n"
                    + "   update implantacao2_5.usuario set login = 'WAGNER', senha = '$2a$12$.8DmNiwGEEXV/M1zxYP43uBOi2rQs3Q/7wxPe5Lbm7poAWyjnoozm' where id = 3;\n"
                    + "   update implantacao2_5.usuario set login = 'MICHAEL', senha = '$2a$12$NKdzIm0k/.QbltTcFDiyLOTxBCe9Mi01xHU/v93MdZIcvwESY404.' where id = 4;\n"
                    + "   update implantacao2_5.usuario set login = 'RODRIGO', senha = '$2a$12$JWSRLo/HDhp2CrdL02xH8uZifJ7xZ.ZO/gRATwrukosu0Y58.OhVa' where id = 5;\n"
                    + "   update implantacao2_5.usuario set login = 'VRSP-BAU', senha = '$2a$12$kKTEKlxo9Np5H1P2Vucaoe3EV6VMH8rzLmqt.XVMvnwrUL6Kl.vQm' where id = 6;\n"
                    + "   update implantacao2_5.usuario set login = 'VRPA', senha = '$2a$12$Et.db7kSw45KOPbZPqNhQuMFh53DRf84MJCVsvrBqIxby72EFzySy' where id = 7;\n"
                    + "   update implantacao2_5.usuario set login = 'VRSC', senha = '$2a$12$qbyO3.wSN9b880HN21yvUelZemyWgOMhgq47rayRohuNOI82TNUqa' where id = 8;\n"
                    + "   update implantacao2_5.usuario set login = 'VRCE', senha = '$2a$12$AcCAvOts7q.tZpiKKHj/A.8Wcm3j8nVDb.SXgGWtHCOHYaSHUcnj.' where id = 9;\n"
                    + "   update implantacao2_5.usuario set login = 'VRGO', senha = '$2a$12$5dMabsRMCMqpusFoLlF04utvRztG68gXzME0oywJry.tW2kviKn8O' where id = 10;\n"
                    + "   update implantacao2_5.usuario set login = 'VRPE', senha = '$2a$12$u9zeWz77tQf6SQv0KhZi9u6zgtb7P5Gf3URPjtrejtWhfVB2bWKFi' where id = 11;\n"
                    + "   update implantacao2_5.usuario set login = 'VRRJ', senha = '$2a$12$92Puj08mF7a.lgArkJjc/.WtLLjAy9csQSLexIpqckXzT5L51CPl6' where id = 12;\n"
                    + "   update implantacao2_5.usuario set login = 'VRBA', senha = '$2a$12$F1V/qUbIj4AyT4q5kastUur.Fv.avpXRGK7ZxEUYJzljVGYtT9wV2' where id = 13;\n"
                    + "   update implantacao2_5.usuario set login = 'VRSP', senha = '$2a$12$0jkS4F7B8fORQN1Z6fZuduReaqrI3Z5dIy7hf8.17ow5Eq3nPbZw.' where id = 14;\n"
                    + "   update implantacao2_5.usuario set login = 'VRSP-ZL', senha = '$2a$12$mbGK2/TbDKgEU7ZRHuNzwufR7A3aPd29EPVBRrswpVLAJhfpGGQjS' where id = 15;\n"
                    + "   update implantacao2_5.usuario set login = 'VRMG', senha = '$2a$12$foWrXPAxW88wBR5zWZxMrOFph6mYDpPFWKd8bYQN1Lt48o4zNkVye' where id = 16;\n"
                    + "   update implantacao2_5.usuario set login = 'JOSE LAMONTANHA', senha = '$2a$12$9him78m3Re0fM2uoj8DGmeu8ebdFHc/OSQgS1ZHyb98V.73vhYqO.' where id = 17;\n"
                    + "   update implantacao2_5.usuario set login = 'MARCIO', senha = '$2a$12$Tc2AnzRpK30ahUI290AuHu3bhmQLuzR9Yby3jV5.89rBD3sYLwBFm' where id = 18;\n"
                    + "   update implantacao2_5.usuario set login = 'SUPORTE', senha = '$2a$12$Y7xCtYzd82dRkDzVl72MS.wH5.KqyBDkjJc57wUTFcyKGdEgVHjGS' where id = 19;\n"
                    + "end;\n"
                    + "$$ language plpgsql");
        }
    }
}
