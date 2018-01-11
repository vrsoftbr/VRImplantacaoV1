package vrimplantacao.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.MunicipioVO;

public class MunicipioDAO {

    /**
     * ****** acertar cidade e estado dos cliente e fornecedores
     * @param v_municipio
     * @throws java.lang.Exception
     */
    public void updateCidadeEstadoPessoas(List<MunicipioVO> v_municipio) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_municipio.size());
            ProgressBar.setStatus("Acertando dados...Cidade/Estado...");

            for (MunicipioVO i_municipio : v_municipio) {
                sql = new StringBuilder();
                sql.append("update fornecedor set "
                        + "id_estado = " + i_municipio.getIdEstado() + ", "
                        + "id_municipio = " + i_municipio.getCodigoIbge() + " "
                        + "from implantacao.codigoanteriorforn ant "
                        + "where ant.codigoatual = fornecedor.id "
                        + "and ant.codigocidade_sistemaanterior = " + i_municipio.getId() + ";"
                        + "update clientepreferencial set "
                        + "id_estado = " + i_municipio.getIdEstado() + ", "
                        + "id_municipio = " + i_municipio.getCodigoIbge() + " "
                        + "from implantacao.codigoanteriorcli ant "
                        + "where ant.codigoatual = clientepreferencial.id "
                        + "and ant.codigocidade_sistemaanterior = " + i_municipio.getId() + ";");
                stm.execute(sql.toString());
                ProgressBar.next();
            }

            Conexao.commit();
            stm.close();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
