package vrimplantacao2_5.dao.migracao;

import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.LogVO;

/**
 *
 * @author guilhermegomes
 */
public class LogDAO {

    public void executar(LogVO logVO) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("implantacao2_5.operacao");

            sql.put("id_usuario", logVO.getIdUsuario());
            sql.put("datahora", "'" + logVO.getDataHoraTime() + "'");
            sql.put("id_tipooperacao", logVO.getIdTipoOperacao());
            sql.put("id_loja", logVO.getIdLoja());

            stm.execute(sql.getInsert());
        }
    }
}
