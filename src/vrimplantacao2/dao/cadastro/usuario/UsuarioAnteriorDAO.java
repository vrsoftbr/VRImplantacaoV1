package vrimplantacao2.dao.cadastro.usuario;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.usuario.UsuarioVO;
import vrimplantacao2.vo.cadastro.usuario.UsuarioAnteriorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class UsuarioAnteriorDAO {

    private MultiMap<String, UsuarioAnteriorVO> anteriores;

    public MultiMap<String, UsuarioAnteriorVO> getAnteriores() throws Exception {
        if (anteriores == null) {
            atualizarAnteriores();
        }
        return anteriores;
    }

    public void atualizarAnteriores() throws Exception {
        anteriores = new MultiMap<>(3);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ca.importsistema,\n"
                    + "	ca.importloja,\n"
                    + "	ca.importid,\n"
                    + "	ca.codigoatual,\n"
                    + "	u.login,\n"
                    + "	u.nome,\n"
                    + "	u.id_tiposetor,\n"
                    + "	u.id_situacaocadastro, \n"
                    + "	ca.observacaoimportacao \n"
                    + "from \n"
                    + "	implantacao.codant_usuario ca\n"
                    + "	left join usuario u on ca.codigoatual = u.id\n"
                    + "order by\n"
                    + "	ca.importsistema,\n"
                    + "	ca.importloja,\n"
                    + "	ca.importid"
            )) {
                while (rst.next()) {
                    UsuarioAnteriorVO vo = new UsuarioAnteriorVO();
                    vo.setImportSistema(rst.getString("importsistema"));
                    vo.setImportLoja(rst.getString("importloja"));
                    vo.setImportId(rst.getString("importid"));
                    vo.setLogin(rst.getString("login"));
                    vo.setNome(rst.getString("nome"));
                    vo.setIdTipoSetor(rst.getInt("id_tiposetor"));
                    vo.setSituacaoCadastro(
                            rst.getInt("id_situacaocadastro") == 1
                            ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    vo.setObservacaoImportacao(rst.getString("observacaoimportacao"));
                    int codigoAtual = rst.getInt("codigoatual");
                    if (codigoAtual > 0) {
                        UsuarioVO u = new UsuarioVO();
                        u.setId(rst.getInt("codigoatual"));
                        u.setLogin(rst.getString("login"));
                        u.setNome(rst.getString("nome"));
                        vo.setCodigoAtual(u);
                    }

                    anteriores.put(
                            vo,
                            vo.getImportSistema(),
                            vo.getImportLoja(),
                            vo.getImportId()
                    );
                }
            }
        }
    }
    
    public void gravarUsuarioAnterior(UsuarioAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_usuario");
            sql.put("importsistema", vo.getImportSistema());
            sql.put("importloja", vo.getImportLoja());
            sql.put("importid", vo.getImportId());
            if (vo.getCodigoAtual() != null) {
                sql.put("codigoatual", vo.getCodigoAtual().getId());
            } else {
                sql.putNull("codigoatual");
            }
            sql.put("login", vo.getLogin());
            sql.put("nome", vo.getNome());
            sql.put("tiposetor", vo.getIdTipoSetor());
            sql.put("situacaoCadastro", vo.getSituacaoCadastro() == SituacaoCadastro.ATIVO ? 1 : 0);
            sql.put("observacaoimportacao", vo.getObservacaoImportacao());
            stm.execute(sql.getInsert());
        }
    }

    public int getConexaoMigrada(int idConexao, String sistema) throws Exception {
        int conexao = 0;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id_conexao \n"
                    + "from \n"
                    + "	implantacao.codant_usuario\n"
                    + "where \n"
                    + "	importsistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = rs.getInt("id_conexao");
                }
            }
        }

        return conexao;
    }

    public int verificaRegistro() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	count(*) qtd \n"
                    + "from \n"
                    + "	implantacao.codant_usuario \n"
                    + "where \n"
                    + "	codigoatual in (select codigoatual from implantacao.codant_usuario limit 100)")) {
                if (rs.next()) {
                    return rs.getInt("qtd");
                }
            }
        }

        return 0;
    }

    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean conexao = false;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id_conexao \n"
                    + "from \n"
                    + "	implantacao.codant_usuario\n"
                    + "where \n"
                    + "	importsistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = true;
                }
            }
        }
        return conexao;
    }

    public void copiarCodantUsuario(String sistema, String lojaModelo, String lojaNova) throws Exception {

        String sql
                = "create temp table implantacao_lojas (sistema varchar, loja_modelo varchar, loja_nova varchar) on commit drop;\n"
                + "insert into implantacao_lojas values ('" + sistema + "', '" + lojaModelo + "', '" + lojaNova + "');\n"
                + "\n"
                + "do $$\n"
                + "declare\n"
                + "	r record;\n"
                + "begin\n"
                + "	for r in select * from implantacao_lojas\n"
                + "	loop\n"
                + "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_usuario' and t.table_schema = 'implantacao')) then\n"
                + "			insert into implantacao.codant_usuario\n"
                + "			select\n"
                + "				importsistema,\n"
                + "				r.loja_nova,\n"
                + "				importid,\n"
                + "				codigoatual,\n"
                + "				cnpj,\n"
                + "				razao,\n"
                + "				fantasia,\n"
                + "                             id_conexao\n"
                + "			from \n"
                + "				implantacao.codant_usuario\n"
                + "			where\n"
                + "				importsistema = r.sistema and \n"
                + "				importloja = r.loja_modelo and\n"
                + "				importid in (\n"
                + "					select\n"
                + "						importid\n"
                + "					from\n"
                + "						implantacao.codant_usuario\n"
                + "					where\n"
                + "						importsistema = r.sistema and \n"
                + "						importloja = r.loja_modelo\n"
                + "					except\n"
                + "					select\n"
                + "						importid\n"
                + "					from\n"
                + "						implantacao.codant_usuario\n"
                + "					where\n"
                + "						importsistema = r.sistema and \n"
                + "						importloja = r.loja_nova\n"
                + "				);\n"
                + "		end if;\n"
                + "	end loop;\n"
                + "end;\n"
                + "$$;";

        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql);
        }
    }

    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        String loja = "";

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	importloja \n"
                    + "from \n"
                    + "	implantacao.codant_usuario a\n"
                    + "where \n"
                    + "a.importsistema = '" + sistema + "' and \n"
                    + "	a.importloja =  (select id_lojaorigem from implantacao2_5.conexaoloja where lojamatriz = true \n"
                    + "and id_conexao = a.id_conexao)\n"
                    + "and a.id_conexao = " + idConexao + "\n"
                    + "limit 1")) {
                if (rs.next()) {
                    loja = rs.getString("importloja");
                }
            }
        }

        return loja;
    }

    public String getImpSistema() throws Exception {
        String loja = "";

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	importsistema\n"
                    + "from 	\n"
                    + "	implantacao.codant_usuario\n"
                    + "where \n"
                    + "	id_conexao = (select min(id_conexao) from implantacao.codant_usuario)\n"
                    + "limit 1")) {
                if (rs.next()) {
                    loja = rs.getString("importsistema");
                }
            }
        }

        return loja;
    }
}
