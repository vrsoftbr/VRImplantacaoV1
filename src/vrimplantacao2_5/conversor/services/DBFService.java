/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.conversor.services;

import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import java.util.List;

/**
 *
 * @author Michael-Oliveira
 */
public class DBFService {

    public static String prepararArquivosEmLote(DBFReader reader, String tabela, List<DBFDataType> dadosCabecalho, List<String> inserts, DBFRow linha, StringBuilder insertBuilder) {
        for (int i = 0; i < dadosCabecalho.size(); i++) {
            //insertBuilder.append("'");
            TiposDadosDBFService.retornDadoTipado(insertBuilder, linha, i, dadosCabecalho.get(i));
            insertBuilder.append(", ");
        }
        insertBuilder.setLength(insertBuilder.length() - 2); // Remove a última vírgula
        insertBuilder.append(");");
        return insertBuilder.toString();
    }

}
