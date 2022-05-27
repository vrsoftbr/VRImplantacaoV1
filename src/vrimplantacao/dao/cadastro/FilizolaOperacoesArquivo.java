/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.utils.arquivo.delimited.ArquivoTXT;

/**
 *
 * @author Michael
 */
public class FilizolaOperacoesArquivo {

    NutricionalFilizolaDAO daoNutFilizola = new NutricionalFilizolaDAO();
    FilizolaSalvarArquivos filizolaSalvarArquivos = new FilizolaSalvarArquivos();

    private static final Logger LOG = Logger.getLogger(NutricionalFilizolaDAO.class.getName());
    Utils util = new Utils();

    public List<NutricionalFilizolaVO> getNutricionalFilizola(String arquivo) throws Exception {
        List<NutricionalFilizolaVO> result = new ArrayList<>();
        List<String> vFilizola = util.lerArquivoBalanca(arquivo);

        for (int i = 0; i < vFilizola.size(); i++) {
            if (!vFilizola.get(i).trim().isEmpty()) {

                NutricionalFilizolaVO vo = new NutricionalFilizolaVO();
                vo.setId(Utils.stringToInt(vFilizola.get(i).substring(0, 6)));
                vo.setDescricao(vFilizola.get(i).substring(7, 29).trim());
                vo.setPorcao(vFilizola.get(i).substring(40, 75).trim());
                vo.setCaloria(Utils.stringToInt(vFilizola.get(i).substring(75, 80)));
                vo.setPercentualcaloria(Utils.stringToInt(vFilizola.get(i).substring(80, 84)));
                vo.setCarboidrato(Utils.stringToDouble(vFilizola.get(i).substring(84, 89)) / 10);
                vo.setPercentualcarboidrato(Utils.stringToInt(vFilizola.get(i).substring(89, 93)));
                vo.setProteina(Utils.stringToDouble(vFilizola.get(i).substring(93, 98)) / 10);
                vo.setPercentualproteina(Utils.stringToInt(vFilizola.get(i).substring(98, 102)));
                vo.setGordura(Utils.stringToDouble(vFilizola.get(i).substring(102, 107)) / 10);
                vo.setPercentualgordura(Utils.stringToInt(vFilizola.get(i).substring(107, 111)));
                vo.setGordurasaturada(Utils.stringToDouble(vFilizola.get(i).substring(111, 116)) / 10);
                vo.setPercentualgordurasaturada(Utils.stringToInt(vFilizola.get(i).substring(116, 120)));
                vo.setGorduratrans(Utils.stringToDouble(vFilizola.get(i).substring(120, 125)) / 10);
                // 125, 129
                vo.setFibra(Utils.stringToDouble(vFilizola.get(i).substring(129, 134)) / 10);
                vo.setPercentualfibra(Utils.stringToInt(vFilizola.get(i).substring(134, 138)));
                // 138, 143
                // 143, 147
                // 147, 152
                // 152, 156
                vo.setSodio(Utils.stringToDouble(vFilizola.get(i).substring(156, 161)) / 10);
                vo.setPercentualsodio(Utils.stringToInt(vFilizola.get(i).substring(161, 165)));
                result.add(vo);
            }
        }

        return result;
    }

    private static String trim(StringBuilder linha, int i) {
        String result = linha.toString().substring(0, i).trim();
        linha.delete(0, i);
        return result;
    }

    private static int trimInt(StringBuilder linha, int i) {
        return Utils.stringToInt(trim(linha, i));
    }  

    
    public List<NutricionalFilizolaVO> getArquivoRdc360(String arquivo) throws Exception {
        ArquivoTXT arq = new ArquivoTXT(arquivo);

        List<NutricionalFilizolaVO> list = new ArrayList<>();
        
        for (LinhaArquivo ln : arq) {
            StringBuilder linha = new StringBuilder(ln.getString(""));

            NutricionalFilizolaVO vo = new NutricionalFilizolaVO();

            vo.addProduto(Utils.stringLong(trim(linha, 6)));
            vo.setPorcao(trim(linha, 35));
            vo.setCaloria(trimInt(linha, 5));
            vo.setPercentualcaloria(trimInt(linha, 4));
            vo.setCarboidrato(trimInt(linha, 5) / 10);
            vo.setPercentualcarboidrato(trimInt(linha, 4));
            vo.setProteina(trimInt(linha, 5) / 10);
            vo.setPercentualproteina(trimInt(linha, 4));
            vo.setGordura(trimInt(linha, 5) / 10);
            vo.setPercentualgordura(trimInt(linha, 4));
            vo.setGordurasaturada(trimInt(linha, 5) / 10);
            vo.setPercentualgordurasaturada(trimInt(linha, 4));
            vo.setGorduratrans(trimInt(linha, 5) / 10);
            trim(linha, 4); //pula 4 digitos
            vo.setFibra(trimInt(linha, 5) / 10);
            vo.setPercentualfibra(trimInt(linha, 4));
            trim(linha, 5);//pula registros
            trim(linha, 4);//pula registros
            trim(linha, 5);//pula registros
            trim(linha, 4);//pula registros
            vo.setSodio(trimInt(linha, 5) / 10);
            vo.setPercentualsodio(trimInt(linha, 4));

            list.add(vo);
        }
        LOG.fine(list.size() + " nutricionais para serem importados");
        return list;
    }
}
