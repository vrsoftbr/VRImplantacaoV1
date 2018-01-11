package vrimplantacao2.dao.cadastro.local;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.local.EstadoVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;

public class MunicipioDAO {
    
    private static MultiMap<String, MunicipioVO> municipios;
    private static MultiMap<Integer, MunicipioVO> municipiosId;

    public void atualizarMunicipios() throws Exception {
        municipios = new MultiMap<>(2);
        municipiosId = new MultiMap<>(1);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                            "	m.id_estado uf_id,\n" +
                            "	e.sigla uf_sigla,\n" +
                            "	e.descricao uf_descricao,\n" +
                            "	m.id mun_id,\n" +
                            "	m.descricao mun_descricao\n" +
                            "from\n" +
                            "	municipio m\n" +
                            "	join estado e on m.id_estado = e.id\n" +
                            "order by\n" +
                            "	m.id_estado,\n" +
                            "	m.id"
            )) {
                while (rst.next()) {
                    EstadoVO uf = new EstadoVO(
                            rst.getInt("uf_id"),
                            rst.getString("uf_sigla"),
                            rst.getString("uf_descricao")
                    );
                    MunicipioVO vo = new MunicipioVO(
                            rst.getInt("mun_id"),
                            rst.getString("mun_descricao"),
                            uf
                    );
                    municipios.put(vo, uf.getSigla(), vo.getDescricao());
                    municipiosId.put(vo, vo.getId());
                }
            }
        }
    }

    public MunicipioVO getMunicipio(int ibge_municipio) throws Exception {
        if (municipiosId == null) {
            atualizarMunicipios();
        }
        
        return municipiosId.get(ibge_municipio);
    }

    public MunicipioVO getMunicipio(String municipio, String uf) throws Exception {
        if (municipios == null || municipiosId == null) {
            atualizarMunicipios();
        }
        municipio = Utils.acertarTexto(municipio);
        uf = Utils.acertarTexto(uf, 2);
        
        return municipios.get(uf, municipio);
    }
    
}
