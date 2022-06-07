/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.nutricional;

/**
 *
 * @author Michael
 */
public class ToledoService {

    OperacoesArquivoToledoInfnutri importaInfnutri;
    OperacoesArquivoToledoItensMGV importaMgv;

    public void direcionaImportacao(String arquivo, CondicaoToledo condicao) throws Exception {
        condicao.importarNutricionalToledo(arquivo);
    }

}
