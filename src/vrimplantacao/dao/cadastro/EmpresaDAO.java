/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.EmpresaVO;

public class EmpresaDAO {

    public void salvar(List<EmpresaVO> v_empresa) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        java.sql.Date dataInicio = null, dataTermino = null;
        java.util.Date dataTerminoAux = new java.util.Date();
        GregorianCalendar gc = null;

        dataInicio = new java.sql.Date(new java.util.Date().getTime());

        gc = new GregorianCalendar();
        gc.setTime(dataInicio);
        gc.add(Calendar.DAY_OF_MONTH, 30);
        dataTerminoAux = gc.getTime();
        dataTermino = new java.sql.Date(dataTerminoAux.getTime());
        
        try {
            
            Conexao.begin();
            stm = Conexao.createStatement();
            
            ProgressBar.setStatus("Importando dados...Empresa...");
            ProgressBar.setMaximum(v_empresa.size());
            
            for (EmpresaVO i_empresa : v_empresa) {
                sql = new StringBuilder();
                sql.append("INSERT INTO empresa( ");
                sql.append("id, razaosocial, endereco, bairro, id_municipio, telefone, cep, ");
                sql.append("inscricaoestadual, cnpj, datainicio, datatermino, id_situacaocadastro, ");
                sql.append("id_tipoinscricao, renovacaoautomatica, percentualdesconto, diapagamento, ");
                sql.append("bloqueado, datadesbloqueio, id_estado, diainiciorenovacao, diaterminorenovacao, ");
                sql.append("tipoterminorenovacao, databloqueio, observacao, numero, complemento, ");
                sql.append("id_contacontabilfiscalpassivo, id_contacontabilfiscalativo) ");
                sql.append("VALUES ( ");
                sql.append(i_empresa.getId() + ", ");
                sql.append("'" + i_empresa.getRazaosocial() + "', ");
                sql.append("'" + i_empresa.getEndereco() + "', ");
                sql.append("'" + i_empresa.getBairro() + "', ");
                sql.append(i_empresa.getId_municipio() + ", ");
                sql.append("'" + i_empresa.getTelefone() + "',");
                sql.append(i_empresa.getCep() + ", ");
                sql.append(i_empresa.getInscricaoestadual() + ", ");
                sql.append((i_empresa.getCnpj() == -1 ? i_empresa.getId() : i_empresa.getCnpj()) + ", ");
                sql.append((i_empresa.getDatainicio() == "" ? "'" + dataInicio + "'" : "'" + i_empresa.getDatainicio() + "'") + ", ");
                sql.append((i_empresa.getDatatermino()== "" ? "'" + dataTermino + "'" : "'" + i_empresa.getDatatermino() + "'") + ", ");
                sql.append(i_empresa.getId_situacaocadastro() + ", ");
                sql.append(i_empresa.getId_tipoinscricao() + ", ");
                sql.append(i_empresa.isRenovacaoautomatica() + ", ");
                sql.append(i_empresa.getPercentualdesconto() + ", ");
                sql.append(i_empresa.getDiapagamento() + ", ");
                sql.append(i_empresa.isBloqueado() + ", ");
                sql.append((i_empresa.getDatadesbloqueio() == "" ? null : "'" + i_empresa.getDatadesbloqueio() + "'")+", ");
                sql.append(i_empresa.getId_estado() + ", ");
                sql.append(i_empresa.getDiainiciorenovacao() + ", ");
                sql.append(i_empresa.getDiaterminorenovacao() + ", ");
                sql.append(i_empresa.getTipoterminorenovacao() + ", ");
                sql.append((i_empresa.getDatabloqueio() == "" ? null : "'" + i_empresa.getDatabloqueio() + "'") + ", ");
                sql.append("'" + i_empresa.getObservacao() + "', ");
                sql.append("'" + i_empresa.getNumero() + "', ");
                sql.append("'" + i_empresa.getComplemento() + "', ");
                sql.append(i_empresa.getId_contacontabilfiscalpassivo() + ", ");
                sql.append(i_empresa.getId_contacontabilfiscalativo());
                sql.append(");");

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
