/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.nutricional.utils;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.nutricional.vo.ItensMgvVO;

/**
 *
 * @author Desenvolvimento
 */
public class OperacoesArquivoMgvToledo { 

    Utils util = new Utils();
    private int mgvColuna1Inicio = 0;
    private int mgvColuna1Fim = 2;
    private int mgvColuna2Inicio = 2;
    private int mgvColuna2Fim = 3;
    private int mgvColuna3Inicio = 3;
    private int mgvColuna3Fim = 9;
    private int mgvColuna4Inicio = 9;
    private int mgvColuna4Fim = 15;
    private int mgvColuna5Inicio = 15;
    private int mgvColuna5Fim = 18;
    private int mgvColuna6Inicio = 18;
    private int mgvColuna6Fim = 67;
    private int mgvColuna7Inicio = 78;
    private int mgvColuna7Fim = 84;
    private int mgvColuna8Inicio = 84;
    private int mgvColuna8Fim;

    public OperacoesArquivoMgvToledo() {
    }    

    public OperacoesArquivoMgvToledo(int mgvColuna1Inicio, int mgvColuna1Fim, int mgvColuna2Inicio, int mgvColuna2Fim, 
            int mgvColuna3Inicio, int mgvColuna3Fim, int mgvColuna4Inicio, int mgvColuna4Fim, int mgvColuna5Inicio, 
            int mgvColuna5Fim, int mgvColuna6Inicio, int mgvColuna6Fim, int mgvColuna7Inicio, int mgvColuna7Fim, 
            int mgvColuna8Inicio, int mgvColuna8Fim){
        this.mgvColuna1Inicio = mgvColuna1Inicio;
        this.mgvColuna1Fim = mgvColuna1Fim;
        this.mgvColuna2Inicio = mgvColuna2Inicio;
        this.mgvColuna2Fim = mgvColuna2Fim;
        this.mgvColuna3Inicio = mgvColuna3Inicio;
        this.mgvColuna3Fim = mgvColuna3Fim;
        this.mgvColuna4Inicio = mgvColuna4Inicio;
        this.mgvColuna4Fim = mgvColuna4Fim;
        this.mgvColuna5Inicio = mgvColuna5Inicio;
        this.mgvColuna5Fim = mgvColuna5Fim;
        this.mgvColuna6Inicio = mgvColuna6Inicio;
        this.mgvColuna6Fim = mgvColuna6Fim;
        this.mgvColuna7Inicio = mgvColuna7Inicio;
        this.mgvColuna7Fim = mgvColuna7Fim;
        this.mgvColuna8Inicio = mgvColuna8Inicio;
        this.mgvColuna8Fim = mgvColuna8Fim;
    }
    
    public List<ItensMgvVO> getNutricionalToledoItensMgv(String arquivo) throws Exception {
        List<ItensMgvVO> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);

        for (int i = 0; i < vToledo.size(); i++) {
            ItensMgvVO toledo = new ItensMgvVO();
            mgvColuna8Fim = vToledo.get(i).length();
            if (!vToledo.get(i).trim().isEmpty()) {
                if ("0".equals(vToledo.get(i).substring(2, 3))) {
                    toledo.setDepartamento(vToledo.get(i).substring(mgvColuna1Inicio, mgvColuna1Fim));
                    toledo.setTipo(vToledo.get(i).substring(mgvColuna2Inicio, mgvColuna2Fim));
                    toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(mgvColuna3Inicio, mgvColuna3Fim).equals("") ? "0" : vToledo.get(i).substring(mgvColuna3Inicio, mgvColuna3Fim)));
                    toledo.setPreco(vToledo.get(i).substring(mgvColuna4Inicio, mgvColuna4Fim));
                    toledo.setValidade(Integer.parseInt(vToledo.get(i).substring(mgvColuna5Inicio, mgvColuna5Fim).equals("") ? "0" : vToledo.get(i).substring(mgvColuna5Inicio, mgvColuna5Fim)));
                    toledo.setDescricao(util.acertarTexto(vToledo.get(i).substring(mgvColuna6Inicio, mgvColuna6Fim).replace("'", "").trim()));
                    toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(mgvColuna7Inicio, mgvColuna7Fim).equals("") ? "0" : vToledo.get(i).substring(mgvColuna7Inicio, mgvColuna7Fim)));
                    toledo.setDemaisDados(vToledo.get(i).substring(mgvColuna8Inicio, mgvColuna8Fim));
                    toledo.setPesavel("P");
                } else {
                    toledo.setDepartamento(vToledo.get(i).substring(mgvColuna1Inicio, mgvColuna1Fim));
                    toledo.setTipo(vToledo.get(i).substring(mgvColuna2Inicio, mgvColuna2Fim));
                    toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(mgvColuna3Inicio, mgvColuna3Fim).equals("") ? "0" : vToledo.get(i).substring(mgvColuna3Inicio, mgvColuna3Fim)));
                    toledo.setPreco(vToledo.get(i).substring(mgvColuna4Inicio, mgvColuna4Fim));
                    toledo.setValidade(Integer.parseInt(vToledo.get(i).substring(mgvColuna5Inicio, mgvColuna5Fim).equals("") ? "0" : vToledo.get(i).substring(mgvColuna5Inicio, mgvColuna5Fim)));
                    toledo.setDescricao(util.acertarTexto(vToledo.get(i).substring(mgvColuna6Inicio, mgvColuna6Fim).replace("'", "").trim()));
                    toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(mgvColuna7Inicio, mgvColuna7Fim).equals("") ? "0" : vToledo.get(i).substring(mgvColuna7Inicio, mgvColuna7Fim)));
                    toledo.setDemaisDados(vToledo.get(i).substring(mgvColuna8Inicio, mgvColuna8Fim));
                    toledo.setPesavel("U");
                }
            }
            result.add(toledo);
        }
        return result;
    }
    
    public int getLenghtFimLinhaMgv(String arquivo) throws Exception {
        List<String> vToledo = util.lerArquivoBalanca(arquivo);
        for (int i = 0; i <= 1; i++) {
            mgvColuna8Fim = vToledo.get(i).trim().length();            
            }
        return mgvColuna8Fim;
    }
}
