package vrimplantacao.dao.implantacao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.AliquotaCgaVO;

/**
 *
 * @author lucasrafael
 */
public class AliquotaCgaDAO {

    public void salvar(List<AliquotaCgaVO> v_aliquotaCga) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_aliquotaCga.size());
            ProgressBar.setStatus("Importando Aliquotas Sistema CGA...");

            for (AliquotaCgaVO i_aliquotaCga : v_aliquotaCga) {
                sql = new StringBuilder();
                sql.append("insert into implantacao.aliquotaCGA (codigo, aliquotadescricao, aliquotaNFperc, "
                        + "aliquotaNFred, aliquotaperc, codigoaliquota) "
                        + "values ("
                        + i_aliquotaCga.getCodigo() + ", "
                        + "'" + i_aliquotaCga.getAliquotadescricao() + "', "
                        + i_aliquotaCga.getAliquotaNFperc() + ", "
                        + i_aliquotaCga.getAliquotaNFred() + ", "
                        + i_aliquotaCga.getAliquotaperc() + ", "
                        + "'" + i_aliquotaCga.getCodigoaliquota() + "' "
                        + ");");
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

    public String getCodigoAliquota(int i_codigo) throws Exception {
        String retorno = "";
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigoaliquota "
                    + "from implantacao.aliquotaCGA "
                    + "where codigo = " + i_codigo
            )) {
                if (rst.next()) {
                    retorno = rst.getString("codigoaliquota");
                }
            }
        }
        return retorno;
    }

    public AliquotaCgaVO validar(double aliquotaPerc, double aliquotaRed) throws Exception {
        AliquotaCgaVO oAliquotaCga = new AliquotaCgaVO();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select aliquotaNFperc, aliquotaNFred "
                    + "from implantacao.aliquotaCGA "
                    + "where aliquotaNFperc = " + aliquotaPerc + " "
                    + "and aliquotaNFred = " + aliquotaRed
            )) {
                if (rst.next()) {
                    oAliquotaCga.setAliquotaNFperc(rst.getDouble("aliquotaNFperc"));
                    oAliquotaCga.setAliquotaNFred(rst.getDouble("aliquotaNFred"));
                }
            }
        }
        return oAliquotaCga;
    }
}
