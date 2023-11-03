/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.conversor.services;

import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFRow;
import java.text.SimpleDateFormat;
import vrimplantacao.utils.Utils;

/**
 *
 * @author Michael-Oliveira
 */
public class TiposDadosService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void retornDadoTipado(StringBuilder insertBuilder, DBFRow linha, int i, DBFDataType tipo) {
        if (tipo.equals(DBFDataType.CHARACTER)) {
            insertBuilder.append("\'" + Utils.acertarTexto(linha.getString(i)) + "\'");
            return;
        }
        if (tipo.equals(DBFDataType.NUMERIC)) {
            insertBuilder.append(linha.getDouble(i));
            return;
        }
        if (tipo.equals(DBFDataType.FLOATING_POINT)) {
            insertBuilder.append(linha.getDouble(i));
            return;
        }
        if (tipo.equals(DBFDataType.LOGICAL)) {
            insertBuilder.append(linha.getBoolean(i));
            return;
        }
        if (tipo.equals(DBFDataType.CURRENCY)) {
            insertBuilder.append(linha.getBigDecimal(i));
            return;
        }
        if (tipo.equals(DBFDataType.LONG)) {
            insertBuilder.append(linha.getLong(i));
            return;
        }
        if (tipo.equals(DBFDataType.AUTOINCREMENT)) {
            insertBuilder.append(linha.getInt(i));
            return;
        }
        if (tipo.equals(DBFDataType.VARCHAR)) {
            insertBuilder.append("\'" + Utils.acertarTexto(linha.getString(i)) + "\'");
            return;
        }
        if (tipo.equals(DBFDataType.DOUBLE)) {
            insertBuilder.append(linha.getDouble(i));
            return;
        }
        if (tipo.equals(DBFDataType.DATE)) {
            insertBuilder.append(linha.getString(i) == null ? null : "\'" + DATE_FORMAT.format(linha.getDate(i)) + "\'");
            return;
        } else {
            insertBuilder.append("\'" + linha.getObject(i).toString() + "\'");
            return;
        }
    }

    public static String retornaTipo(DBFDataType tipo) {
        if (tipo.equals(DBFDataType.NUMERIC)) {
            return " numeric";
        }
        if (tipo.equals(DBFDataType.CHARACTER)) {
            return " varchar(255)";
        }
        if (tipo.equals(DBFDataType.FLOATING_POINT)) {
            return " numeric";
        }
        if (tipo.equals(DBFDataType.LOGICAL)) {
            return " bool";
        }
        if (tipo.equals(DBFDataType.CURRENCY)) {
            return " numeric(11,2)";
        }
        if (tipo.equals(DBFDataType.LONG)) {
            return " int4";
        }
        if (tipo.equals(DBFDataType.AUTOINCREMENT)) {
            return " int4";
        }
        if (tipo.equals(DBFDataType.VARCHAR)) {
            return " text";
        }
        if (tipo.equals(DBFDataType.DOUBLE)) {
            return " numeric";
        }
        if (tipo.equals(DBFDataType.DATE)) {
            return " date";
        }
        return "text";
    }
}
