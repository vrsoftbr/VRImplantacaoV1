package vrimplantacao.dao.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.vo.notafiscal.TipoEmpresaVO;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class TipoEmpresaDAO {

    public TipoEmpresaVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT * FROM tipoempresa WHERE id = " + i_id);

        if (!rst.next()) {
            throw new VRException("Tipo empresa " + i_id + " não encontrada!");
        }

        TipoEmpresaVO oTipoEmpresa = new TipoEmpresaVO();
        oTipoEmpresa.id = rst.getInt("id");
        oTipoEmpresa.descricao = rst.getString("descricao");
        oTipoEmpresa.produtorRural = rst.getBoolean("produtorrural");
        oTipoEmpresa.idTipoCrt = rst.getInt("id_tipocrt");

        stm.close();

        return oTipoEmpresa;
    }
}
