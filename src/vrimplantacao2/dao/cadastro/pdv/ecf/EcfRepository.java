/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.ecf;

import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.EcfDAO;

/**
 *
 * @author Michael
 */
public class EcfRepository {

    EcfDAO dao = new EcfDAO();

    public void salvarECFPdv(List<EcfPdvVO> ecfs) throws Exception {
        ProgressBar.setStatus("Salvando ECF's...");
        ProgressBar.setMaximum(ecfs.size());
        try {
            for (EcfPdvVO ecf : ecfs) {
                dao.salvarECFPdv(ecf);
                ProgressBar.next();
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
}
