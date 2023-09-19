/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.nutricional.utils;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.nutricional.vo.TxtInfoVO;

/**
 *
 * @author Desenvolvimento
 */
public class OperacoesArquivoTxtInfoToledo {
    Utils util = new Utils();
    private int txtInfoColuna1Inicio = 0;
    private int txtInfoColuna1Fim = 6;
    private int txtInfoColuna2Inicio = 6;
    private int txtInfoColuna2Fim = 106;
    private int txtInfoColuna3Inicio = 106;
    private int txtInfoColuna3Fim = 162;
    private int txtInfoColuna4Inicio = 162;
    private int txtInfoColuna4Fim = 218;
    private int txtInfoColuna5Inicio = 218;
    private int txtInfoColuna5Fim = 274;
    private int txtInfoColuna6Inicio = 274;
    private int txtInfoColuna6Fim = 330;
    private int txtInfoColuna7Inicio = 330;
    private int txtInfoColuna7Fim = 386;
    private int txtInfoColuna8Inicio = 386;
    private int txtInfoColuna8Fim = 442;
    private int txtInfoColuna9Inicio = 442;
    private int txtInfoColuna9Fim = 498;
    private int txtInfoColuna10Inicio = 498;
    private int txtInfoColuna10Fim = 554;
    private int txtInfoColuna11Inicio = 554;
    private int txtInfoColuna11Fim = 610;
    private int txtInfoColuna12Inicio = 610;
    private int txtInfoColuna12Fim = 666;
    private int txtInfoColuna13Inicio = 666;
    private int txtInfoColuna13Fim = 722;
    private int txtInfoColuna14Inicio = 722;
    private int txtInfoColuna14Fim = 778;
    private int txtInfoColuna15Inicio = 778;
    private int txtInfoColuna15Fim = 834;
    private int txtInfoColuna16Inicio = 834;
    private int txtInfoColuna16Fim;

    public OperacoesArquivoTxtInfoToledo() {
    }    

    public OperacoesArquivoTxtInfoToledo(int txtInfoColuna1Inicio, int txtInfoColuna1Fim, 
            int txtInfoColuna2Inicio, int txtInfoColuna2Fim, 
            int txtInfoColuna3Inicio, int txtInfoColuna3Fim, 
            int txtInfoColuna4Inicio, int txtInfoColuna4Fim,
            int txtInfoColuna5Inicio, int txtInfoColuna5Fim, 
            int txtInfoColuna6Inicio, int txtInfoColuna6Fim, 
            int txtInfoColuna7Inicio, int txtInfoColuna7Fim, 
            int txtInfoColuna8Inicio, int txtInfoColuna8Fim, 
            int txtInfoColuna9Inicio, int txtInfoColuna9Fim, 
            int txtInfoColuna10Inicio, int txtInfoColuna10Fim, 
            int txtInfoColuna11Inicio, int txtInfoColuna11Fim, 
            int txtInfoColuna12Inicio, int txtInfoColuna12Fim, 
            int txtInfoColuna13Inicio, int txtInfoColuna13Fim, 
            int txtInfoColuna14Inicio, int txtInfoColuna14Fim, 
            int txtInfoColuna15Inicio, int txtInfoColuna15Fim, 
            int txtInfoColuna16Inicio, int txtInfoColuna16Fim) {
        txtInfoColuna1Inicio = 0;
        this.txtInfoColuna1Inicio = txtInfoColuna1Inicio;
        this.txtInfoColuna1Fim = txtInfoColuna1Fim;
        this.txtInfoColuna2Inicio = txtInfoColuna2Inicio;
        this.txtInfoColuna2Fim = txtInfoColuna2Fim;
        this.txtInfoColuna3Inicio = txtInfoColuna3Inicio;
        this.txtInfoColuna3Fim = txtInfoColuna3Fim;
        this.txtInfoColuna4Inicio = txtInfoColuna4Inicio;
        this.txtInfoColuna4Fim = txtInfoColuna4Fim;
        this.txtInfoColuna5Inicio = txtInfoColuna5Inicio;
        this.txtInfoColuna5Fim = txtInfoColuna5Fim;
        this.txtInfoColuna6Inicio = txtInfoColuna6Inicio;
        this.txtInfoColuna6Fim = txtInfoColuna6Fim;
        this.txtInfoColuna7Inicio = txtInfoColuna7Inicio;
        this.txtInfoColuna7Fim = txtInfoColuna7Fim;
        this.txtInfoColuna8Inicio = txtInfoColuna8Inicio;
        this.txtInfoColuna8Fim = txtInfoColuna8Fim;
        this.txtInfoColuna9Inicio = txtInfoColuna9Inicio;
        this.txtInfoColuna9Fim = txtInfoColuna9Fim;
        this.txtInfoColuna10Inicio = txtInfoColuna10Inicio;
        this.txtInfoColuna10Fim = txtInfoColuna10Fim;
        this.txtInfoColuna11Inicio = txtInfoColuna11Inicio;
        this.txtInfoColuna11Fim = txtInfoColuna11Fim;
        this.txtInfoColuna12Inicio = txtInfoColuna12Inicio;
        this.txtInfoColuna12Fim = txtInfoColuna12Fim;
        this.txtInfoColuna13Inicio = txtInfoColuna13Inicio;
        this.txtInfoColuna13Fim = txtInfoColuna13Fim;
        this.txtInfoColuna14Inicio = txtInfoColuna14Inicio;
        this.txtInfoColuna14Fim = txtInfoColuna14Fim;
        this.txtInfoColuna15Inicio = txtInfoColuna15Inicio;
        this.txtInfoColuna15Fim = txtInfoColuna15Fim;
        this.txtInfoColuna16Inicio = txtInfoColuna16Inicio;
        this.txtInfoColuna16Fim = txtInfoColuna16Fim;
    }
    
    public List<TxtInfoVO> getAlergenicosToledoTxtInfo(String arquivo) throws Exception {
        List<TxtInfoVO> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);

        for (int i = 0; i < vToledo.size(); i++) {
                TxtInfoVO vo = new TxtInfoVO();
                txtInfoColuna16Fim = vToledo.get(i).length();
                if (!vToledo.get(i).trim().isEmpty()) {

                    vo.setCodigo(Integer.parseInt(vToledo.get(i).substring(txtInfoColuna1Inicio, txtInfoColuna1Fim).equals("") ? "0" : vToledo.get(i).substring(txtInfoColuna1Inicio, txtInfoColuna1Fim)));
                    vo.setObs(txtInfoColuna2Fim > txtInfoColuna16Fim ? "" : Utils.removerAcentos(vToledo.get(i).substring(txtInfoColuna2Inicio, txtInfoColuna2Fim)));
                    vo.setLinha1(txtInfoColuna3Fim > txtInfoColuna16Fim ? "" : Utils.removerAcentos(vToledo.get(i).substring(txtInfoColuna3Inicio, txtInfoColuna3Fim)));
                    vo.setLinha2(txtInfoColuna4Fim > txtInfoColuna16Fim ? "" : Utils.removerAcentos(vToledo.get(i).substring(txtInfoColuna4Inicio, txtInfoColuna4Fim)));
                    vo.setLinha3(txtInfoColuna5Fim > txtInfoColuna16Fim ? "" : Utils.removerAcentos(vToledo.get(i).substring(txtInfoColuna5Inicio, txtInfoColuna5Fim)));
                    vo.setLinha4(txtInfoColuna6Fim > txtInfoColuna16Fim ? "" : Utils.removerAcentos(vToledo.get(i).substring(txtInfoColuna6Inicio, txtInfoColuna6Fim)));
                    vo.setLinha5(txtInfoColuna7Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna7Inicio, txtInfoColuna7Fim));
                    vo.setLinha6(txtInfoColuna8Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna8Inicio, txtInfoColuna8Fim));
                    vo.setLinha7(txtInfoColuna9Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna9Inicio, txtInfoColuna9Fim));
                    vo.setLinha8(txtInfoColuna10Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna10Inicio, txtInfoColuna10Fim));
                    vo.setLinha9(txtInfoColuna11Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna11Inicio, txtInfoColuna11Fim));
                    vo.setLinha10(txtInfoColuna12Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna12Inicio, txtInfoColuna12Fim));
                    vo.setLinha11(txtInfoColuna13Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna13Inicio, txtInfoColuna13Fim));
                    vo.setLinha12(txtInfoColuna14Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna14Inicio, txtInfoColuna14Fim));
                    vo.setLinha13(txtInfoColuna15Fim > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna15Inicio, txtInfoColuna15Fim));
                    vo.setLinha14E15(txtInfoColuna16Inicio > txtInfoColuna16Fim ? "" : vToledo.get(i).substring(txtInfoColuna16Inicio, txtInfoColuna16Fim));
                    result.add(vo);
                }
            }
        return result;
    }
    
    public int getLenghtFimLinhaTxtInfo(String arquivo) throws Exception {
        List<String> vToledo = util.lerArquivoBalanca(arquivo);
        for (int i = 0; i <= 0; i++) {
            txtInfoColuna16Fim = vToledo.get(i).trim().length();            
            }
        return txtInfoColuna16Fim;
    }
    
}
