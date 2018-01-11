package vrimplantacao.dao.cadastro;

import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.AtacadoVendedorVO;

public class AtacadoVendedorDAO {
    public void salvar (List<AtacadoVendedorVO> v_vendedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_vendedor.size());
            ProgressBar.setStatus("Importando dados...atacado.vendedor..");
            
            for (AtacadoVendedorVO i_vendedorAtacado : v_vendedor) {
                sql = new StringBuilder();
                sql.append("INSERT INTO atacado.vendedor(\n" +
                            " id, nome, id_situacaocadastro, percentual) "
                        + "VALUES ("
                        + i_vendedorAtacado.getId() + ","
                        + "'" + i_vendedorAtacado.getNome() + "', "
                        + i_vendedorAtacado.getId_situacaocadastro() + ", "
                        + i_vendedorAtacado.getPercentual() + ");");
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