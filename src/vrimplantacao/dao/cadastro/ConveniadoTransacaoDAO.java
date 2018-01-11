package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.ConveniadoTransacaoVO;

public class ConveniadoTransacaoDAO {

    public void salvar(List<ConveniadoTransacaoVO> v_conveniadoTransacao, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        java.sql.Date dataMovimento;
        dataMovimento = new java.sql.Date(new java.util.Date().getTime());
        
        try {
            
            Conexao.begin();
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(v_conveniadoTransacao.size());
            ProgressBar.setStatus("Importando Contas a Receber Convenio...");
            
            for (ConveniadoTransacaoVO i_conveniadoTransacao : v_conveniadoTransacao) {
                sql = new StringBuilder();
                sql.append("select id from conveniado where id = "+i_conveniadoTransacao.id_conveniado+" ");
                sql.append("and id_loja = "+idLoja);
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {
                
                    sql = new StringBuilder();
                    sql.append("INSERT INTO conveniadotransacao( ");
                    sql.append("id_conveniado, ecf, numerocupom, datahora, id_loja, valor,  ");
                    sql.append("id_situacaotransacaoconveniado, lancamentomanual, matricula,  ");
                    sql.append("datamovimento, finalizado, id_tiposervicoconvenio, observacao) ");
                    sql.append("VALUES (");
                    sql.append(i_conveniadoTransacao.id_conveniado + ",");
                    sql.append(i_conveniadoTransacao.ecf + ",");
                    sql.append(i_conveniadoTransacao.numerocupom + ", ");
                    
                    if (i_conveniadoTransacao.datahora.trim().isEmpty()) {
                        sql.append("'" + dataMovimento + "',");
                    } else {
                        sql.append("'"+i_conveniadoTransacao.datahora+"', ");
                    }
                    
                    sql.append(idLoja + ",");
                    sql.append(i_conveniadoTransacao.valor + ",");
                    sql.append(i_conveniadoTransacao.id_situacaotransacaoconveniado + ",");
                    sql.append(i_conveniadoTransacao.lancamentomanual + ",");
                    sql.append(i_conveniadoTransacao.matricula + ",");
                    
                    if (i_conveniadoTransacao.datamovimento.trim().isEmpty()) {
                        sql.append("'" + dataMovimento + "',");
                    } else {
                        sql.append("'"+i_conveniadoTransacao.datamovimento.trim()+"', ");
                    }
                    
                    sql.append(i_conveniadoTransacao.finalizado + ",");
                    sql.append(i_conveniadoTransacao.id_tiposervicoconvenio + ",");
                    sql.append("'" + i_conveniadoTransacao.observacao + "'");
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