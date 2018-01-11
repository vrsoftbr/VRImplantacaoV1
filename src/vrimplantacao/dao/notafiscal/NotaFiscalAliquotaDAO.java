package vrimplantacao.dao.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.vo.notafiscal.NotaFiscalAliquotaVO;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class NotaFiscalAliquotaDAO {

    public NotaFiscalAliquotaVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT * FROM aliquota WHERE id = " + i_id);

        if (!rst.next()) {
            throw new VRException("Alíquota " + i_id + " não encontrada!");
        }

        NotaFiscalAliquotaVO oNotaFiscalAliquota = new NotaFiscalAliquotaVO();
        oNotaFiscalAliquota.descricao = rst.getString("descricao");
        oNotaFiscalAliquota.id = rst.getInt("id");
        oNotaFiscalAliquota.porcentagem = rst.getDouble("porcentagem");
        oNotaFiscalAliquota.reduzido = rst.getDouble("reduzido");
        oNotaFiscalAliquota.situacaoTributaria = rst.getInt("situacaotributaria");
        oNotaFiscalAliquota.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
        oNotaFiscalAliquota.idAliquotaPdv = rst.getObject("id_aliquotapdv") == null ? -1 : rst.getInt("id_aliquotapdv");
        oNotaFiscalAliquota.mensagemNf = rst.getString("mensagemnf");
        oNotaFiscalAliquota.csosn = rst.getInt("csosn");
        oNotaFiscalAliquota.porcentagemFinal = rst.getDouble("porcentagemfinal");

        stm.close();

        return oNotaFiscalAliquota;
    }
}
