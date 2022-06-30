/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.nutricional;

import java.util.List;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.vo.importacao.NutricionalToledoIMP;

/**
 *
 * @author Michael
 */
public enum CondicaoToledo {

    INFNUTRI {
        @Override
        public void importarNutricionalToledo(String arquivo) throws Exception {
            long antes = System.currentTimeMillis();
            List<NutricionalToledoVO> nutri = carregaDadosInfnutri.getNutricionalToledoINFNUTRI(arquivo);
            long durante = System.currentTimeMillis();
            gravar.salvarNutricionalToledoINFNUTRI(nutri);
            long depois = System.currentTimeMillis();
            System.out.println("Tempo de carregar: " + (durante - antes));
            System.out.println("Tempo de salvar: " + (depois - durante));
            System.out.println("Tempo de carregar e salvar: " + (depois - antes));
        }
    }, INTENSMGV {
        @Override
        public void importarNutricionalToledo(String arquivo) throws Exception {
            List<NutricionalToledoIMP> nutricionalToledo = operacoes.getNutricionalToledoITENSMGV(arquivo);
            gravar.salvarNutricionalProdutoITENSMGV(nutricionalToledo);
        }
    };

    OperacoesArquivoToledoItensMGV operacoes = new OperacoesArquivoToledoItensMGV();
    OperacoesArquivoToledoInfnutri carregaDadosInfnutri = new OperacoesArquivoToledoInfnutri();
    ArquivoToledoRepository gravar = new ArquivoToledoRepository();

    public abstract void importarNutricionalToledo(String arquivo) throws Exception;
}
