/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.nutricional.utils;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.nutricional.vo.InfnutriVO;

/**
 *
 * @author Desenvolvimento
 */
public class OperacoesArquivoInfnutriToledo {

    Utils util = new Utils();
    private int infColuna1Inicio = 0;
    private int infColuna1Fim = 1;
    private int infColuna2Inicio = 1;
    private int infColuna2Fim = 7;
    private int infColuna3Inicio = 7;
    private int infColuna3Fim = 8;
    private int infColuna4Inicio = 8;
    private int infColuna4Fim = 11;
    private int infColuna5Inicio = 11;
    private int infColuna5Fim = 12;
    private int infColuna6Inicio = 12;
    private int infColuna6Fim = 14;
    private int infColuna7Inicio = 14;
    private int infColuna7Fim = 15;
    private int infColuna8Inicio = 15;
    private int infColuna8Fim = 17;
    private int infColuna9Inicio = 17;
    private int infColuna9Fim = 21;
    private int infColuna10Inicio = 21;
    private int infColuna10Fim = 25;
    private int infColuna11Inicio = 25;
    private int infColuna11Fim = 28;
    private int infColuna12Inicio = 28;
    private int infColuna12Fim = 31;
    private int infColuna13Inicio = 31;
    private int infColuna13Fim = 34;
    private int infColuna14Inicio = 34;
    private int infColuna14Fim = 37;
    private int infColuna15Inicio = 37;
    private int infColuna15Fim = 40;
    private int infColuna16Inicio = 40;
    private int infColuna16Fim;

    public OperacoesArquivoInfnutriToledo() {
    }

    public OperacoesArquivoInfnutriToledo(int infColuna1Inicio, int infColuna1Fim, 
            int infColuna2Inicio, int infColuna2Fim, 
            int infColuna3Inicio, int infColuna3Fim, 
            int infColuna4Inicio, int infColuna4Fim,
            int infColuna5Inicio, int infColuna5Fim, 
            int infColuna6Inicio, int infColuna6Fim, 
            int infColuna7Inicio, int infColuna7Fim, 
            int infColuna8Inicio, int infColuna8Fim, 
            int infColuna9Inicio, int infColuna9Fim, 
            int infColuna10Inicio, int infColuna10Fim, 
            int infColuna11Inicio, int infColuna11Fim, 
            int infColuna12Inicio, int infColuna12Fim, 
            int infColuna13Inicio, int infColuna13Fim, 
            int infColuna14Inicio, int infColuna14Fim, 
            int infColuna15Inicio, int infColuna15Fim, 
            int infColuna16Inicio, int infColuna16Fim) {
        infColuna1Inicio = 0;
        this.infColuna1Inicio = infColuna1Inicio;
        this.infColuna1Fim = infColuna1Fim;
        this.infColuna2Inicio = infColuna2Inicio;
        this.infColuna2Fim = infColuna2Fim;
        this.infColuna3Inicio = infColuna3Inicio;
        this.infColuna3Fim = infColuna3Fim;
        this.infColuna4Inicio = infColuna4Inicio;
        this.infColuna4Fim = infColuna4Fim;
        this.infColuna5Inicio = infColuna5Inicio;
        this.infColuna5Fim = infColuna5Fim;
        this.infColuna6Inicio = infColuna6Inicio;
        this.infColuna6Fim = infColuna6Fim;
        this.infColuna7Inicio = infColuna7Inicio;
        this.infColuna7Fim = infColuna7Fim;
        this.infColuna8Inicio = infColuna8Inicio;
        this.infColuna8Fim = infColuna8Fim;
        this.infColuna9Inicio = infColuna9Inicio;
        this.infColuna9Fim = infColuna9Fim;
        this.infColuna10Inicio = infColuna10Inicio;
        this.infColuna10Fim = infColuna10Fim;
        this.infColuna11Inicio = infColuna11Inicio;
        this.infColuna11Fim = infColuna11Fim;
        this.infColuna12Inicio = infColuna12Inicio;
        this.infColuna12Fim = infColuna12Fim;
        this.infColuna13Inicio = infColuna13Inicio;
        this.infColuna13Fim = infColuna13Fim;
        this.infColuna14Inicio = infColuna14Inicio;
        this.infColuna14Fim = infColuna14Fim;
        this.infColuna15Inicio = infColuna15Inicio;
        this.infColuna15Fim = infColuna15Fim;
        this.infColuna16Inicio = infColuna16Inicio;
        this.infColuna16Fim = infColuna16Fim;
    }

    public List<InfnutriVO> getNutricionalToledoInfnutri(String arquivo) throws Exception {
        List<InfnutriVO> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);

        boolean isLayoutMgv6 = !vToledo.isEmpty() && vToledo.get(0).startsWith("N");

        if (isLayoutMgv6) {
            for (int i = 0; i < vToledo.size(); i++) {
                InfnutriVO vo = new InfnutriVO();
                infColuna16Fim = vToledo.get(i).trim().length();
                if (!vToledo.get(i).trim().isEmpty()) {

                    vo.setIndicador(vToledo.get(i).substring(infColuna1Inicio, infColuna1Fim));
                    vo.setNutricional(Integer.parseInt(vToledo.get(i).substring(infColuna2Inicio, infColuna2Fim)));
                    vo.setReservado(vToledo.get(i).substring(infColuna3Inicio, infColuna3Fim));
                    vo.setQuantidade(vToledo.get(i).substring(infColuna4Inicio, infColuna4Fim));
                    vo.setPorcaoUnGr(vToledo.get(i).substring(infColuna5Inicio, infColuna5Fim));
                    vo.setMedidaCaseiraInteira(vToledo.get(i).substring(infColuna6Inicio, infColuna6Fim));
                    vo.setMedidaCaseiraDecimalFracionado(vToledo.get(i).substring(infColuna7Inicio, infColuna7Fim));
                    vo.setMedidaCaseiraXicaraFatia(vToledo.get(i).substring(infColuna8Inicio, infColuna8Fim));
                    vo.setCalorias(vToledo.get(i).substring(infColuna9Inicio, infColuna9Fim));
                    vo.setCarboidratos(String.valueOf(Utils.stringToLong(vToledo.get(i).substring(infColuna10Inicio, infColuna10Fim)) / 10));
                    vo.setProteinas(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(infColuna11Inicio, infColuna11Fim)) / 10));
                    vo.setGordurasTotais(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(infColuna12Inicio, infColuna12Fim)) / 10));
                    vo.setGordurasSaturadas(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(infColuna13Inicio, infColuna13Fim)) / 10));
                    vo.setGordurasTrans(String.valueOf((Utils.stringToDouble(vToledo.get(i).substring(infColuna14Inicio, infColuna14Fim)) / 10)));
                    vo.setFibra(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(infColuna15Inicio, infColuna15Fim)) / 10));
                    vo.setSodio(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(infColuna16Inicio, infColuna16Fim)) / 10));

                    result.add(vo);
                }
            }
        } else {
            for (int i = 0; i < vToledo.size(); i++) {
                InfnutriVO vo = new InfnutriVO();
                if (!vToledo.get(i).trim().isEmpty()) {

                    vo.setIndicador("MGV5");
                    vo.setNutricional(Integer.parseInt(vToledo.get(i).substring(0, 3)));
                    vo.setReservado(vToledo.get(i).substring(3, 4));
                    vo.setQuantidade(vToledo.get(i).substring(4, 7));
                    vo.setPorcaoUnGr(vToledo.get(i).substring(7, 8));
                    vo.setMedidaCaseiraInteira(vToledo.get(i).substring(8, 10));
                    vo.setMedidaCaseiraDecimalFracionado(vToledo.get(i).substring(10, 12));
                    vo.setMedidaCaseiraXicaraFatia(vToledo.get(i).substring(12, 14));
                    vo.setCalorias(vToledo.get(i).substring(14, 18));
                    vo.setCarboidratos(String.valueOf(Utils.stringToLong(vToledo.get(i).substring(14, 17)) / 10));
                    vo.setProteinas(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(18, 20)) / 10));
                    vo.setGordurasTotais(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(21, 24)) / 10));
                    vo.setGordurasSaturadas(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(50, 54)) / 10));
                    vo.setGordurasTrans(String.valueOf((Utils.stringToDouble(vToledo.get(i).substring(34, 37)) / 10)));
                    vo.setFibra(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(30, 32)) / 10));
                    vo.setSodio(String.valueOf(Utils.stringToDouble(vToledo.get(i).substring(40, vToledo.get(i).trim().length())) / 10));

                    result.add(vo);
                }
            }
        }
        return result;
    }
    public int getLenghtFimLinhaInf(String arquivo) throws Exception {
        List<String> vToledo = util.lerArquivoBalanca(arquivo);
        for (int i = 0; i <= 1; i++) {
            infColuna16Fim = vToledo.get(i).trim().length();            
            }
        return infColuna16Fim;
    }
}
