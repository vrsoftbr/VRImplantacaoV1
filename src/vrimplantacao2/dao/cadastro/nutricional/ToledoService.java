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

    public static String sistema;
    public static String loja = "1";
    public static boolean ignorarUltimoDigito = false;
    public static int opcaoCodigo = 1;
    OperacoesArquivoToledoInfnutri importaInfnutri;
    OperacoesArquivoToledoItensMGV importaMgv;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        if (loja.equals("")){
            loja = "1";
        }
        this.loja = loja;
    }

    public boolean isIgnorarUltimoDigito() {
        return ignorarUltimoDigito;
    }

    public void setIgnorarUltimoDigito(boolean ignorarUltimoDigito) {
        this.ignorarUltimoDigito = ignorarUltimoDigito;
    }

    public int getOpcaoCodigo() {
        return opcaoCodigo;
    }

    public void setOpcaoCodigo(int opcaoCodigo) {
        this.opcaoCodigo = opcaoCodigo;
    }

    public void direcionaImportacao(String arquivo, CondicaoToledo condicao) throws Exception {
        condicao.importarNutricionalToledo(arquivo);
    }

}
