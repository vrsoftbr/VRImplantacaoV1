package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.FornecedorContatoVO;

public class FornecedorContatoDAO {

    public void salvar(List<FornecedorContatoVO> v_fornecedorContato) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            
            Conexao.begin();
            
            ProgressBar.setStatus("Importando Dados...Contato Fornecedor...");
            ProgressBar.setMaximum(v_fornecedorContato.size());

            stm = Conexao.createStatement();
            
            for (FornecedorContatoVO i_fornecedorContato : v_fornecedorContato) {
                
                sql = new StringBuilder();
                sql.append("select f.id from fornecedor f ");
                sql.append("inner join implantacao.codigoanteriorforn ant on ant.codigoatual = f.id ");
                sql.append("where ant.codigoanterior = " + i_fornecedorContato.getIdFornecedorAnterior()+"; ");
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {
                
                    i_fornecedorContato.setIdFornecedor(rst.getInt("id"));
                    
                    sql = new StringBuilder();
                    sql.append("INSERT INTO fornecedorcontato( ");
                    sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                    sql.append("VALUES (");
                    sql.append(i_fornecedorContato.getIdFornecedor() + ", ");
                    sql.append("'" + i_fornecedorContato.getNome() + "', ");
                    sql.append("'" + i_fornecedorContato.getTelefone() + "', ");
                    sql.append(i_fornecedorContato.getIdTipoContato() + ", ");
                    sql.append("'" + i_fornecedorContato.getEmail() + "', ");
                    sql.append("'" + i_fornecedorContato.getCelular() + "'");
                    sql.append(");");
                    stm.execute(sql.toString());                
                }                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}