package vrimplantacao2.dao.cadastro.desmembramento;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoAnteriorVO;

public class DesmembramentoDAO {

    public void gravar(DesmembramentoVO desmem) throws Exception {

    }

    public void gravarItens(DesmembramentoAnteriorVO itens) throws Exception {
    }

    List<DesmembramentoIMP> getDesmembramentoItens() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
