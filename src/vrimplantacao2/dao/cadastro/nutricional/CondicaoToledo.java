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
            List<NutricionalToledoVO> nutri = carregaDadosInfnutri.getNutricionalToledoINFNUTRI(arquivo);
            gravar.salvarNutricionalToledoINFNUTRI(nutri);
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
