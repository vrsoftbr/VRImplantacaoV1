package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.vo.vrimplantacao.ConveniadoVO;
import vrimplantacao.vo.vrimplantacao.ConveniadoServicoVO;

public class ConveniadoDAO {

    public void salvar(List<ConveniadoVO> v_conveniado, int idLoja, int idEmpresa) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        java.sql.Date dataValidadeCartao;
        dataValidadeCartao = new java.sql.Date(new java.util.Date().getTime() + 30);
        
        try {
            
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(v_conveniado.size());
            ProgressBar.setStatus("Importando Conveniados...");                        
            
            for (ConveniadoVO i_conveniado : v_conveniado) {
                
                if (i_conveniado.id == 0) {
                    i_conveniado.id = new CodigoInternoDAO().get("conveniado");
                }
                
                sql = new StringBuilder();
                sql.append("INSERT INTO conveniado( ");
                sql.append("id, nome, id_empresa, bloqueado, id_situacaocadastro, senha, ");
                sql.append("id_loja, cnpj, observacao, id_tipoinscricao, matricula, datavalidadecartao, ");
                sql.append("datadesbloqueio, visualizasaldo, databloqueio) ");
                sql.append("VALUES (");
                sql.append(i_conveniado.id+",");
                sql.append("'"+i_conveniado.nome+"',");
                sql.append(idEmpresa+",");
                sql.append(i_conveniado.bloqueado+",");
                sql.append(i_conveniado.id_situacaocadastro+",");
                sql.append(i_conveniado.senha+",");
                sql.append(idLoja+", ");
                sql.append(i_conveniado.cnpj+",");
                sql.append("'"+i_conveniado.observacao+"',");
                sql.append(i_conveniado.id_tipoinscricao+",");
                sql.append(i_conveniado.matricula+",");
                sql.append("'"+dataValidadeCartao+"',");
                sql.append(i_conveniado.datadesbloqueio+",");
                sql.append(i_conveniado.visualizasaldo+",");
                sql.append(i_conveniado.databloqueio);
                sql.append(");");
                
                stm.execute(sql.toString());
                
                for (ConveniadoServicoVO i_convServico : i_conveniado.vConveniadoServico) {
                    
                    sql = new StringBuilder();
                    sql.append("INSERT INTO conveniadoservico( ");
                    sql.append("id_conveniado, id_tiposervicoconvenio, valor, valordesconto) ");
                    sql.append("VALUES ( ");
                    sql.append(i_conveniado.id+", ");
                    sql.append(i_convServico.id_tiposervicoconvenio+", ");
                    sql.append(i_convServico.valor+", ");
                    sql.append(i_convServico.valordesconto+" ");
                    sql.append("); ");
                    
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
