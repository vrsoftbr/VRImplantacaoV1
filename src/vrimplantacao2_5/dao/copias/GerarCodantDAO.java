package vrimplantacao2_5.dao.copias;

import java.sql.PreparedStatement;
import vrframework.classe.Conexao;

/**
 *
 * @author Wesley
 */
public class GerarCodantDAO {

    public void gerarCodant() throws Exception {

//        Util.exibirMensagemConfirmar("Deseja criar todas as Codant faltantes?", "Confirmação de criação de Codant");
        createCodant(sqlCodantProduto(), "PRODUTO");
        createCodant(sqlCodantFornecedor(), "FORNECEDOR");
        createCodant(sqlCodantClientePreferencial(), "CLIENTE_PREFERENCIAL");
        createCodant(sqlCodantClienteEventual(), "CLIENTE_EVENTUAL");
        createCodant(sqlCodantEan(), "EAN");
        createCodant(sqlMapaTributacao(), "MAPA_TRIBUTAÇÃO");
        createCodant(sqlCodantUsuario(), "USUARIO");

//        Util.exibirMensagem("Codant criadas com sucesso!", "Informativo");
    }

    public void updateCodantTable() {

        updateCodant(sqlUpdateCodantEan(), "codant_ean");
    }

    private void createCodant(String sql, String tabela) {

        try (PreparedStatement pst = Conexao.prepareStatement(sql)) {

            pst.execute();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar criar Codant referente a " + tabela + ".", e);
        }
    }

    private void updateCodant(String sql, String tabela) {

        try (PreparedStatement pst = Conexao.prepareStatement(sql)) {
            pst.execute();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar atualizar a tabela referente a " + tabela + ".", e);
        }
    }

    private String sqlCodantProduto() {

        String sql = "CREATE TABLE IF NOT EXISTS implantacao.codant_produto (\n"
                + "	impsistema varchar NOT NULL,\n"
                + "	imploja varchar NOT NULL,\n"
                + "	impid varchar NOT NULL,\n"
                + "	descricao varchar NULL,\n"
                + "	codigoatual int4 NULL,\n"
                + "	piscofinscredito int4 NULL,\n"
                + "	piscofinsdebito int4 NULL,\n"
                + "	piscofinsnaturezareceita int4 NULL,\n"
                + "	icmscst int4 NULL,\n"
                + "	icmsaliq numeric(14, 4) NULL,\n"
                + "	icmsreducao numeric(14, 4) NULL,\n"
                + "	estoque numeric(14, 4) NULL,\n"
                + "	e_balanca bool NULL,\n"
                + "	custosemimposto numeric(13, 4) NULL,\n"
                + "	custocomimposto numeric(13, 4) NULL,\n"
                + "	margem numeric(11, 2) NULL,\n"
                + "	precovenda numeric(11, 2) NULL,\n"
                + "	ncm varchar(15) NULL,\n"
                + "	cest varchar(15) NULL,\n"
                + "	contadorimportacao int4 DEFAULT 0 NOT NULL,\n"
                + "	novo bool DEFAULT false NOT NULL,\n"
                + "	codigosped varchar NULL,\n"
                + "	situacaocadastro int4 NULL,\n"
                + "	dataimportacao timestamp NULL,\n"
                + "	obsimportacao varchar NULL,\n"
                + "	icmscstsaida int4 NULL,\n"
                + "	icmsaliqsaida numeric(14, 4) NULL,\n"
                + "	icmsreducaosaida numeric(14, 4) NULL,\n"
                + "	icmscstsaidaforaestado int4 NULL,\n"
                + "	icmsaliqsaidaforaestado numeric(14, 4) NULL,\n"
                + "	icmsreducaosaidaforaestado numeric(14, 4) NULL,\n"
                + "	icmscstsaidaforaestadonf int4 NULL,\n"
                + "	icmsaliqsaidaforaestadonf numeric(14, 4) NULL,\n"
                + "	icmsreducaosaidaforaestadonf numeric(14, 4) NULL,\n"
                + "	icmscstentrada int4 NULL,\n"
                + "	icmsaliqentrada numeric(14, 4) NULL,\n"
                + "	icmsreducaoentrada numeric(14, 4) NULL,\n"
                + "	icmscstentradaforaestado int4 NULL,\n"
                + "	icmsaliqentradaforaestado numeric(14, 4) NULL,\n"
                + "	icmsreducaoentradaforaestado numeric(14, 4) NULL,\n"
                + "	icmscstconsumidor int4 NULL,\n"
                + "	icmsaliqconsumidor numeric(14, 4) NULL,\n"
                + "	icmsreducaoconsumidor numeric(14, 4) NULL,\n"
                + "	icmsdebitoid varchar NULL,\n"
                + "	icmsdebitoforaestadoid varchar NULL,\n"
                + "	icmsdebitoforaestadonfid varchar NULL,\n"
                + "	icmscreditoid varchar NULL,\n"
                + "	icmscreditoforaestadoid varchar NULL,\n"
                + "	icmsconsumidorid varchar NULL,\n"
                + "	datacadastro timestamp NULL,\n"
                + "	id_conexao int4 NULL,\n"
                + "	forcarnovo bool DEFAULT false NULL,\n"
                + "	dataalteracao timestamp NULL,\n"
                + "	CONSTRAINT codant_produto_impsistema_imploja_codigosped_key UNIQUE (impsistema, imploja, codigosped),\n"
                + "	CONSTRAINT codant_produto_pkey PRIMARY KEY (impsistema, imploja, impid)\n"
                + ");";

        return sql;
    }

    private String sqlCodantFornecedor() {

        String sql = "CREATE TABLE IF NOT EXISTS implantacao.codant_fornecedor (\n"
                + "	importsistema varchar NOT NULL,\n"
                + "	importloja varchar NOT NULL,\n"
                + "	importid varchar NOT NULL,\n"
                + "	codigoatual int4 NULL,\n"
                + "	cnpj varchar(30) NULL,\n"
                + "	razao varchar NULL,\n"
                + "	fantasia varchar NULL,\n"
                + "	id_conexao int4 NULL,\n"
                + "	dataimportacao timestamp DEFAULT now() NULL,\n"
                + "	CONSTRAINT codant_fornecedor_pkey PRIMARY KEY (importsistema, importloja, importid)\n"
                + ");";

        return sql;
    }

    private String sqlCodantClientePreferencial() {

        String sql = "CREATE TABLE IF NOT EXISTS implantacao.codant_clientepreferencial (\n"
                + "	sistema varchar NOT NULL,\n"
                + "	loja varchar NOT NULL,\n"
                + "	id varchar NOT NULL,\n"
                + "	codigoatual int4 NULL,\n"
                + "	cnpj varchar NULL,\n"
                + "	ie varchar NULL,\n"
                + "	nome varchar NULL,\n"
                + "	forcargravacao bool DEFAULT false NOT NULL,\n"
                + "	id_conexao int4 NULL,\n"
                + "	dataimportacao timestamp DEFAULT now() NULL,\n"
                + "	CONSTRAINT codant_clientepreferencial_pkey PRIMARY KEY (sistema, loja, id)\n"
                + ");";

        return sql;
    }

    private String sqlCodantClienteEventual() {

        String sql = "CREATE TABLE IF NOT EXISTS implantacao.codant_clienteeventual (\n"
                + "	sistema varchar NOT NULL,\n"
                + "	loja varchar NOT NULL,\n"
                + "	id varchar NOT NULL,\n"
                + "	codigoatual int4 NULL,\n"
                + "	cnpj varchar NULL,\n"
                + "	ie varchar NULL,\n"
                + "	nome varchar NULL,\n"
                + "	forcargravacao bool DEFAULT false NOT NULL,\n"
                + "	id_conexao int4 NULL,\n"
                + "	dataimportacao timestamp DEFAULT now() NULL,\n"
                + "	CONSTRAINT codant_clienteeventual_pkey PRIMARY KEY (sistema, loja, id)\n"
                + ");";

        return sql;
    }

    private String sqlCodantEan() {

        String sql = "CREATE TABLE IF NOT EXISTS implantacao.codant_ean (\n"
                + "	importsistema varchar NOT NULL,\n"
                + "	importloja varchar NOT NULL,\n"
                + "	importid varchar NOT NULL,\n"
                + "	ean varchar NOT NULL,\n"
                + "	qtdembalagem int4 DEFAULT 1 NOT NULL,\n"
                + "	valor numeric(10, 3) DEFAULT 0 NOT NULL,\n"
                + "	tipoembalagem varchar(20) DEFAULT 'UN'::character varying NOT NULL,\n"
                + "	dataimportacao timestamp DEFAULT now() NULL,\n"
                + "	obsimportacao varchar,"
                + "	CONSTRAINT codant_ean_pkey PRIMARY KEY (importsistema, importloja, importid, ean)\n"
                + ");";

        return sql;
    }

    private String sqlMapaTributacao() {

        String sql = "CREATE TABLE IF NOT EXISTS implantacao.mapatributacao (\n"
                + "	sistema varchar NOT NULL,\n"
                + "	agrupador varchar NOT NULL,\n"
                + "	orig_id varchar NOT NULL,\n"
                + "	orig_descricao varchar NOT NULL,\n"
                + "	id_aliquota int4 NULL,\n"
                + "	orig_cst int4 NULL,\n"
                + "	orig_aliquota numeric(11, 2) NULL,\n"
                + "	orig_reduzido numeric(13, 3) NULL,\n"
                + "	orig_fcp numeric(13, 3) NULL,\n"
                + "	orig_desonerado bool NULL,\n"
                + "	orig_porcentagemdesonerado numeric(13, 3) NULL,\n"
                + "	CONSTRAINT mapatributacao_pkey PRIMARY KEY (sistema, agrupador, orig_id)\n"
                + "	);";

        return sql;
    }

    private String sqlCodantUsuario() {

        String sql = "CREATE TABLE IF NOT EXISTS implantacao.codant_usuario (\n"
                + "    importsistema varchar NOT NULL,\n"
                + "    importloja varchar NOT NULL,\n"
                + "    importid varchar NOT NULL,\n"
                + "    codigoatual int4 NULL,\n"
                + "    login varchar NULL,\n"
                + "    nome varchar NULL,\n"
                + "    tiposetor int4 NULL,\n"
                + "    situacaocadastro int4 NULL,\n"
                + "    dataimportacao timestamp,\n"
                + "    id_conexao int4, \n"
                + "    observacaoimportacao varchar, \n"
                + "    CONSTRAINT codant_usuario_pkey PRIMARY KEY (importsistema, importloja, importid)\n"
                + ");";

        return sql;
    }

    private String sqlUpdateCodantEan() {

        String sql = "    DO $$\n"
                + "        BEGIN\n"
                + "            IF NOT EXISTS (\n"
                + "                SELECT 1\n"
                + "                FROM information_schema.columns\n"
                + "                WHERE table_schema = 'implantacao'\n"
                + "                  AND table_name = 'codant_ean'\n"
                + "                  AND column_name = 'obsimportacao'\n"
                + "            ) THEN\n"
                + "                ALTER TABLE implantacao.codant_ean ADD COLUMN obsimportacao varchar;\n"
                + "            END IF;\n"
                + "        END;\n"
                + "        $$;";

        return sql;
    }
}
