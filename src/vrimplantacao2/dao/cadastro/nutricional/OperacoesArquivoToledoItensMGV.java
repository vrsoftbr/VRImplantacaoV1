/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.nutricional;

import java.util.ArrayList;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.importacao.NutricionalToledoIMP;

/**
 *
 * @author Michael
 */
public class OperacoesArquivoToledoItensMGV{

    Utils util = new Utils();

    public List<NutricionalToledoIMP> getNutricionalToledoITENSMGV(String arquivo) throws Exception {
        ProgressBar.setStatus("Carregando dados Toledo...");
        List<NutricionalToledoIMP> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);

        for (int i = 0; i < vToledo.size(); i++) {
            NutricionalToledoIMP toledo = new NutricionalToledoIMP();
            StringLine ln = new StringLine(vToledo.get(i));
            if (!vToledo.get(i).trim().isEmpty()) {
                if ("0".equals(vToledo.get(i).substring(2, 3))) {
                    toledo.setPesavel("P");
                    toledo.setDescricao(util.acertarTexto(vToledo.get(i).substring(18, 67).replace("'", "").trim()));
                    toledo.setValidade(Integer.parseInt(vToledo.get(i).substring(15, 18)));
                    toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(78, 84)));
                    toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(3, 9)));
                } else {
                    toledo.setPesavel("U");
                    toledo.setDescricao(util.acertarTexto(vToledo.get(i).substring(18, 67).replace("'", "").trim()));
                    toledo.setValidade(Integer.parseInt(vToledo.get(i).substring(15, 18)));
                    toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(78, 84)));
                    toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(3, 9)));
                }
            }
            result.add(toledo);
        }
        return result;
    }
}
