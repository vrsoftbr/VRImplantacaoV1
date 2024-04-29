package vrimplantacao2.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.MunicipioVO;

/**
 *
 * @author Leandro Caires
 */
public class LocalDAO {

    public List<EstadoVO> getEstados() throws Exception {
        List<EstadoVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, sigla, descricao from estado order by sigla"
            )) {
                while (rst.next()) {
                    EstadoVO vo = new EstadoVO();
                    vo.setId(rst.getInt("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setSigla(rst.getString("sigla"));

                    try (Statement stm2 = Conexao.createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select id, descricao from municipio where id_estado = " + vo.getId() + " order by descricao"
                        )) {
                            while (rst2.next()) {
                                vo.getMunicipios().add(new MunicipioVO(rst2.getInt("id"), rst2.getString("descricao")));
                            }
                        }
                    }

                    result.add(vo);
                }
            }
        }

        return result;
    }

    public EstadoVO getEstado(int id, boolean comCidades) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, sigla, descricao from estado where id = " + id
            )) {
                while (rst.next()) {
                    EstadoVO vo = new EstadoVO();
                    vo.setId(rst.getInt("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setSigla(rst.getString("sigla"));

                    if (comCidades) {
                        try (Statement stm2 = Conexao.createStatement()) {
                            try (ResultSet rst2 = stm2.executeQuery(
                                    "select id, descricao from municipio where id_estado = " + vo.getId() + " order by descricao"
                            )) {
                                while (rst2.next()) {
                                    vo.getMunicipios().add(new MunicipioVO(rst2.getInt("id"), rst2.getString("descricao")));
                                }
                            }
                        }
                    }

                    return vo;
                }
            }
        }
        return null;
    }

    public MunicipioVO getMunicipio(int id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, descricao, id_estado from municipio where id = " + id
            )) {
                while (rst.next()) {
                    MunicipioVO vo = new MunicipioVO();
                    vo.setId(rst.getInt("id"));
                    vo.setIdEstado(rst.getInt("id_estado"));
                    vo.setDescricao(rst.getString("descricao"));
                    return vo;
                }
            }
        }
        return null;
    }

    public List<vrimplantacao2.vo.cadastro.local.MunicipioVO> getMunicipios() throws Exception {
        List<vrimplantacao2.vo.cadastro.local.MunicipioVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	mun.id_estado,\n"
                    + "	est.sigla,\n"
                    + "	est.descricao estado,\n"
                    + "	mun.id id_municipio,\n"
                    + "	mun.descricao municipio	\n"
                    + "from \n"
                    + "	municipio mun\n"
                    + "	join estado est on\n"
                    + "		mun.id_estado = est.id\n"
                    + "order by\n"
                    + "	mun.id_estado,\n"
                    + "	mun.id"
            )) {
                while (rst.next()) {
                    vrimplantacao2.vo.cadastro.local.MunicipioVO mun = new vrimplantacao2.vo.cadastro.local.MunicipioVO();
                    mun.setId(rst.getInt("id_municipio"));
                    mun.setDescricao(rst.getString("municipio"));
                    vrimplantacao2.vo.cadastro.local.EstadoVO uf = new vrimplantacao2.vo.cadastro.local.EstadoVO();
                    uf.setId(rst.getInt("id_estado"));
                    uf.setDescricao(rst.getString("estado"));
                    uf.setSigla(rst.getString("sigla"));
                    mun.setEstado(uf);

                    result.add(mun);
                }
            }
        }
        return result;
    }

    public List<String> getSiglas() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, sigla, descricao from estado order by sigla"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("sigla"));
                }
            }
        }
        return result;
    }
}
