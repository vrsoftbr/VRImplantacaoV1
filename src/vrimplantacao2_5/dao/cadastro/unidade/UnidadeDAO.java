package vrimplantacao2_5.dao.cadastro.unidade;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;

/**
 *
 * @author Desenvolvimento
 */
public class UnidadeDAO {

    private String filtro = "\n";
    
    public String getFiltro() {
        return this.filtro;                
    }
    
    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
    
    public List<UnidadeVO> consultar(UnidadeVO vo) throws Exception {
        List<UnidadeVO> result = new ArrayList<>();

        if (vo != null) {
            if (vo.getNome() != null && !vo.getNome().trim().isEmpty()) {
                setFiltro("where u.nome like '%" + vo.getNome() + "%'");
            } else {
                setFiltro("\n");
            }
        }
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	u.id, \n"
                    + "	u.nome,\n"
                    + "	u.id_municipio,\n"
                    + "	m.descricao as municicio,\n"
                    + "	u.id_estado,\n"
                    + "	e.sigla as estado\n"
                    + "from implantacao2_5.unidade u\n"
                    + "join municipio m on m.id = u.id_municipio\n"
                    + "join estado e on e.id = u.id_estado\n"
                    + getFiltro()
                    + "order by 2"
            )) {
                while (rst.next()) {
                    UnidadeVO unidadeVO = new UnidadeVO();
                    unidadeVO.setId(rst.getInt("id"));
                    unidadeVO.setNome(rst.getString("nome"));
                    unidadeVO.setIdMunicipio(rst.getInt("id_municipio"));
                    unidadeVO.setDescricaoMunicipio(rst.getString("municicio"));
                    unidadeVO.setIdEstado(rst.getInt("id_estado"));
                    unidadeVO.setDescricaoEstado(rst.getString("estado"));
                    result.add(unidadeVO);
                }
            }
        }
        return result;
    }

    public List<UnidadeVO> getUnidades() throws Exception {
        List<UnidadeVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	u.id, \n"
                    + "	u.nome,\n"
                    + "	u.id_municipio,\n"
                    + "	m.descricao as municicio,\n"
                    + "	u.id_estado,\n"
                    + "	e.sigla as estado\n"
                    + "from implantacao2_5.unidade u\n"
                    + "join municipio m on m.id = u.id_municipio\n"
                    + "join estado e on e.id = u.id_estado\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    UnidadeVO vo = new UnidadeVO();
                    vo.setId(rst.getInt("id"));
                    vo.setNome(rst.getString("nome"));
                    vo.setIdMunicipio(rst.getInt("id_municipio"));
                    vo.setDescricaoMunicipio(rst.getString("municicio"));
                    vo.setIdEstado(rst.getInt("id_estado"));
                    vo.setDescricaoEstado(rst.getString("estado"));
                    result.add(vo);
                }
            }
        }
        return result;
    }

    public void inserir(UnidadeVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setSchema("implantacao2_5");
        sql.setTableName("unidade");

        sql.put("id", vo.getId());
        sql.put("nome", vo.getNome());
        sql.put("id_municipio", vo.getIdMunicipio());
        sql.put("id_estado", vo.getIdEstado());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getInsert());
            }
        }
    }

    public void alterar(UnidadeVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setSchema("implantacao2_5");
        sql.setTableName("unidade");

        sql.put("nome", vo.getNome());
        sql.put("id_municipio", vo.getIdMunicipio());
        sql.put("id_estado", vo.getIdEstado());

        sql.setWhere("id = " + vo.getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public boolean existeUnidade(UnidadeVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id \n"
                    + "from implantacao2_5.unidade \n"
                    + "where nome = '" + vo.getNome() + "' \n"
                    + "and id_municipio = " + vo.getIdMunicipio()
                    + " and id_estado = " + vo.getIdEstado()
            )) {
                return rst.next();
            }
        }
    }
    
    public int getProximoId() throws Exception {
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT COALESCE(MAX(id), 0) + 1 id FROM implantacao2_5.unidade"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                }
            }
        }
        return 0;
    }
    
}
