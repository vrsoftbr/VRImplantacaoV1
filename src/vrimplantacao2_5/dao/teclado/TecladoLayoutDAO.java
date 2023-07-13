/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.teclado;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.TecladoLayoutFuncaoVO;
import vrimplantacao2_5.vo.cadastro.TecladoLayoutVO;

/**
 *
 * @author Michael
 */
public class TecladoLayoutDAO {
           
    public List<Integer> proximoIdTecladoLayoutVO = new ArrayList<>();

    public void copiarPdvTecladoLayout(LojaVO i_loja) throws Exception {

        List<TecladoLayoutVO> tecladoLayoutVO = getTecladoLayout(i_loja);

        try (Statement stm = Conexao.createStatement()) {
            if (tecladoLayoutVO.isEmpty()){
                throw new NullPointerException("Não há dados da loja em tecladolayout");
            }
            for (TecladoLayoutVO vo : tecladoLayoutVO) {

                int i_idTecladoLayoutVO = 0;

                int proximoIdTecladoLayout = new CodigoInternoDAO().get("pdv.tecladolayout");

                SQLBuilder sqlTecladoLayout = new SQLBuilder();
                sqlTecladoLayout.setSchema("pdv");
                sqlTecladoLayout.setTableName("tecladolayout");

                sqlTecladoLayout.put("id", proximoIdTecladoLayout);
                sqlTecladoLayout.put("id_loja", i_loja.getId());
                sqlTecladoLayout.put("descricao", vo.getDescricao());

                i_idTecladoLayoutVO = proximoIdTecladoLayout;
                proximoIdTecladoLayoutVO.add(i_idTecladoLayoutVO);

                stm.execute(sqlTecladoLayout.getInsert());
            }
        }
    }

    public List<TecladoLayoutFuncaoVO> getPdvTecladoLayoutFuncao(LojaVO i_loja) throws Exception {
        List<TecladoLayoutFuncaoVO> result = new ArrayList<>();

        List<TecladoLayoutVO> tecladoLayout = getTecladoLayout(i_loja);

        try (Statement stm = Conexao.createStatement()) {

            for (int i = 0; i < tecladoLayout.size(); i++) {
                TecladoLayoutVO tecladoLayoutVO = tecladoLayout.get(i);
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "     tl.id as idtecladolayout, \n"
                        + "     tlf.codigoretorno, \n"
                        + "     tlf.id_funcao \n"
                        + "FROM pdv.tecladolayoutfuncao AS tlf \n"
                        + "INNER JOIN pdv.tecladolayout AS tl ON tl.id = tlf.id_tecladolayout \n"
                        + "WHERE tl.id_loja = " + i_loja.getIdCopiarLoja() + "\n"
                        + "AND tl.id = " + tecladoLayoutVO.getIdTecladoLayoutCopiado()
                )) {
                    int i_proximoIdTecladoLayoutVO = proximoIdTecladoLayoutVO.get(i);
                    while (rst.next()) {

                        TecladoLayoutFuncaoVO vo = new TecladoLayoutFuncaoVO();
                        vo.setIdTecladoLayout(i_proximoIdTecladoLayoutVO);
                        vo.setCodigoRetorno(rst.getInt("codigoretorno"));
                        vo.setIdFuncao(rst.getInt("id_funcao"));

                        result.add(vo);

                    }
                }
            }
        }
        return result;
    }

    public void copiarPdvTecladoLayoutFuncao(LojaVO i_loja) throws Exception {

        List<TecladoLayoutFuncaoVO> tecladoLayoutFuncao = getPdvTecladoLayoutFuncao(i_loja);

        try (Statement stm = Conexao.createStatement()) {
            for (TecladoLayoutFuncaoVO vo : tecladoLayoutFuncao) {
                int proximoIdLayoutFuncao = new CodigoInternoDAO().get("pdv.tecladolayoutfuncao");

                SQLBuilder sqlTecladoLayoutFuncao = new SQLBuilder();
                sqlTecladoLayoutFuncao.setSchema("pdv");
                sqlTecladoLayoutFuncao.setTableName("tecladolayoutfuncao");

                sqlTecladoLayoutFuncao.put("id", proximoIdLayoutFuncao);
                sqlTecladoLayoutFuncao.put("id_tecladolayout", vo.getIdTecladoLayout());
                sqlTecladoLayoutFuncao.put("codigoretorno", vo.getCodigoRetorno());
                sqlTecladoLayoutFuncao.put("id_funcao", vo.getIdFuncao());

                stm.execute(sqlTecladoLayoutFuncao.getInsert());
            }
        }
    }

    public List<TecladoLayoutVO> getTecladoLayout(LojaVO i_loja) throws Exception {
        List<TecladoLayoutVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "     id, \n"
                    + "     id_loja, \n"
                    + "     descricao \n"
                    + "FROM pdv.tecladolayout \n"
                    + "WHERE id_loja = " + i_loja.getIdCopiarLoja()
            )) {
                while (rst.next()) {
                    TecladoLayoutVO vo = new TecladoLayoutVO();
                    vo.setIdTecladoLayoutCopiado(rst.getInt("id"));
                    vo.setDescricao(rst.getString("descricao"));
                    result.add(vo);
                }
            }
            return result;
        }
    }
}
