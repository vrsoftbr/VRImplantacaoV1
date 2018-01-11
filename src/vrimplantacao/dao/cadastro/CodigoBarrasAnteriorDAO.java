package vrimplantacao.dao.cadastro;

import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.CodigoBarrasAnteriorVO;

public class CodigoBarrasAnteriorDAO {

    public void salvar(List<CodigoBarrasAnteriorVO> vo, int idLojaVR) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setStatus("Importando Código de Barras...Loja " + idLojaVR);
            ProgressBar.setMaximum(vo.size());
            
            sql = new StringBuilder();
            sql.append("create table IF NOT EXISTS implantacao.codigobarrasanterior ( "
                    + "codigoanterior numeric(14,0), "
                    + "codigoatual integer, "
                    + "barras numeric(14,0), "
                    + "id_loja integer); ");
            stm.execute(sql.toString());
            
            for (CodigoBarrasAnteriorVO i_codigoBarrasAnterior : vo) {
                sql = new StringBuilder();
                sql.append("INSERT INTO implantacao.codigobarrasanterior ("
                        + "codigoanterior, codigoatual, barras, id_loja) "
                        + "VALUES ("
                        + i_codigoBarrasAnterior.getCodigoAnterior() + ", "
                        + i_codigoBarrasAnterior.getCodigoAtual() + ", "
                        + i_codigoBarrasAnterior.getCodigoBarras() + ", "
                        + i_codigoBarrasAnterior.getIdLoja() + ");");
                stm.execute(sql.toString());
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
