package vrimplantacao2_5.dao.cadastro.usuario;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2_5.vo.cadastro.UsuarioVO;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Desenvolvimento
 */
public class UsuarioDAO {

    private String filtro = "\n";

    public String getFiltro() {
        return this.filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<UsuarioVO> consultar(UsuarioVO vo) throws Exception {
        List<UsuarioVO> result = new ArrayList<>();

        if (vo != null) {
            if (vo.getNome() != null && !vo.getNome().trim().isEmpty() && vo.getIdUnidade() > 0) {
                setFiltro("where us.nome like '%" + vo.getNome() + "%' and us.id_unidade = " + vo.getIdUnidade() + "\n");
            } else if (vo.getNome() != null && !vo.getNome().trim().isEmpty() && vo.getIdUnidade() == 0) {
                setFiltro("where us.nome like '%" + vo.getNome() + "%' \n");
            } else if ((vo.getNome() == null || vo.getNome().trim().isEmpty()) && vo.getIdUnidade() > 0) {
                setFiltro("and us.id_unidade = " + vo.getIdUnidade() + "\n");
            } else {
                setFiltro("\n");
            }
        }

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	us.id,\n"
                    + "	us.nome,\n"
                    + "	us.login,\n"
                    + " us.senha,\n"
                    + " us.id_unidade, \n"
                    + "	un.nome as unidade\n"
                    + "from implantacao2_5.usuario us\n"
                    + "join implantacao2_5.unidade un on un.id = us.id_unidade\n"
                    + getFiltro()
                    + "order by 2"
            )) {
                while (rst.next()) {
                    UsuarioVO usuarioVO = new UsuarioVO();
                    usuarioVO.setId(rst.getInt("id"));
                    usuarioVO.setNome(rst.getString("nome"));
                    usuarioVO.setLogin(rst.getString("login"));
                    usuarioVO.setSenha(rst.getString("senha"));
                    usuarioVO.setIdUnidade(rst.getInt("id_unidade"));
                    usuarioVO.setDescricaoUnidade(rst.getString("unidade"));
                    result.add(usuarioVO);
                }
            }
        }
        return result;
    }

    public List<UsuarioVO> getUsuario() throws Exception {
        List<UsuarioVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	us.id,\n"
                    + "	us.nome,\n"
                    + "	us.login,\n"
                    + " us.id_unidade, \n"
                    + "	un.nome as unidade\n"
                    + "from implantacao2_5.usuario us\n"
                    + "join implantacao2_5.unidade un on un.id = us.id_unidade\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    UsuarioVO usuarioVO = new UsuarioVO();
                    usuarioVO.setId(rst.getInt("id"));
                    usuarioVO.setNome(rst.getString("nome"));
                    usuarioVO.setLogin(rst.getString("login"));
                    usuarioVO.setSenha(rst.getString("senha"));
                    usuarioVO.setIdUnidade(rst.getInt("id_unidade"));
                    usuarioVO.setDescricaoUnidade(rst.getString("unidade"));
                    result.add(usuarioVO);
                }
            }
        }
        return result;
    }

    public void inserir(UsuarioVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setSchema("implantacao2_5");
        sql.setTableName("usuario");

        sql.put("nome", vo.getNome());
        sql.put("login", vo.getLogin());
        sql.put("senha", vo.getSenha());
        sql.put("id_unidade", vo.getIdUnidade());

        sql.getReturning().add("id");

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                    if (rst.next()) {
                        vo.setId(rst.getInt("id"));
                    }
                }
            }
        }
    }

    public void alterar(UsuarioVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setSchema("implantacao2_5");
        sql.setTableName("usuario");

        sql.put("nome", vo.getNome());
        sql.put("login", vo.getLogin());
        sql.put("senha", vo.getSenha());
        sql.put("id_unidade", vo.getIdUnidade());

        sql.setWhere("id = " + vo.getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public boolean existeUsuario(UsuarioVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id \n"
                    + "from implantacao2_5.usuario \n"
                    + "where login = '" + vo.getLogin() + "' \n"
                    + "and id_unidade = " + vo.getIdUnidade()
            )) {
                return rst.next();
            }
        }
    }

    public List<UsuarioVO> autenticar(UsuarioVO vo) throws Exception {
        List<UsuarioVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	us.id,\n"
                    + "	us.nome,\n"
                    + "	us.login,\n"
                    + " us.id_unidade, \n"
                    + "	un.nome as unidade\n"
                    + "from implantacao2_5.usuario us\n"
                    + "join implantacao2_5.unidade un on un.id = us.id_unidade\n"
                    + "where us.login = '" + vo.getLogin() + "' \n"
                    + "and us.senha = '" + vo.getSenha() + "' \n"
                    + "and us.id_unidade = " + vo.getIdUnidade()
            )) {
                if (rst.next()) {
                    UsuarioVO usuarioVO = new UsuarioVO();
                    usuarioVO.setId(rst.getInt("id"));
                    usuarioVO.setNome(rst.getString("nome"));
                    usuarioVO.setIdUnidade(rst.getInt("id_unidade"));
                    usuarioVO.setDescricaoUnidade(rst.getString("unidade"));
                    result.add(usuarioVO);
                }
            }
        }
        return result;
    }
}
