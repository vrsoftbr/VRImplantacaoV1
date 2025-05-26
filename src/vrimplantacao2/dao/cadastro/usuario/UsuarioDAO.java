package vrimplantacao2.dao.cadastro.usuario;

import java.sql.ResultSet;
import java.sql.Statement;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.usuario.UsuarioVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class UsuarioDAO {

    private MultiMap<String, UsuarioVO> loginExistentes;

    public void gravarUsuario(UsuarioVO vo, Versao versao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("usuario");
            sql.put("id", vo.getId());
            sql.put("login", vo.getLogin());
            sql.put("nome", vo.getNome());
            sql.put("senha", vo.getSenha());
            sql.put("id_tiposetor", vo.getIdTipoSetor());
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro() == SituacaoCadastro.ATIVO ? 1 : 0);
            sql.put("id_loja", vo.getIdLoja());
            sql.put("datahoraultimoacesso", vo.getDataHoraUltimoAcesso());
            sql.put("verificaatualizacao", vo.isVerificaAtualizacao());
            sql.put("id_tema", vo.getIdTema());
            if (versao.igualOuMaiorQue(4, 1, 39)) {
                sql.put("exibepopupsnovidades", vo.getExibePopupsNovidades());
                sql.put("exibepopupestoquecongelado", vo.getExibepopupestoquecongelado());
                sql.put("tempoverificacaopopup", vo.getTempoVerificacaoPopup());
                sql.put("exibepopupofertacontingencia", vo.getExibePopupOfertaContingencia());
            }

            stm.execute(sql.getInsert());
        }
    }

    public MultiMap<String, UsuarioVO> getLoginExistentes() throws Exception {
        if (loginExistentes == null) {
            atualizarLoginExistentes();
        }
        return loginExistentes;
    }

    public void atualizarLoginExistentes() throws Exception {
        loginExistentes = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	id,\n"
                    + "	login,\n"
                    + "	nome\n"
                    + "FROM\n"
                    + "	usuario u\n"
                    + "ORDER BY\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    UsuarioVO vo = new UsuarioVO();
                    vo.setId(rst.getInt("id"));
                    vo.setLogin(rst.getString("login"));
                    vo.setNome(rst.getString("nome"));
                    loginExistentes.put(vo, vo.getLogin());
                }
            }
        }
    }
}
