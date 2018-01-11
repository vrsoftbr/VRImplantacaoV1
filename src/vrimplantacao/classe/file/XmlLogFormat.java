/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.classe.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author Leandro.Caires
 */
class XmlLogFormat implements LogFormat{

    public XmlLogFormat() {
    }

    @Override
    public void fileStart(BufferedWriter bw) throws IOException {
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();
        bw.write("<log>");
        bw.newLine();
    }

    @Override
    public void formatHeader(BufferedWriter bw, String header) throws IOException {
        bw.write("<header>" + header + "</header>");
        bw.newLine();
    }

    @Override
    public void formatLinhas(BufferedWriter bw, List<LogLine> linhas) throws IOException {
        if (linhas.isEmpty()) {
            bw.write("<dados>Não há registros</dados>");
            bw.newLine();
        } else {
            bw.write("<dados>");
            for (LogLine line: linhas) {
                String strLine = "<linha>" + "<data>" + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(line.data) + "</data>" + "<categoria>" + line.categoria + "</categoria>" + "<descricao>" + line.descricao + "</descricao>" + "<observacao>" + convertLogAdicional(line.observacao) + "</observacao>" + "</linha>";
                bw.write(strLine);
                bw.newLine();
            }                    
            bw.write("</dados>");
        }
    }

    @Override
    public void formatFooter(BufferedWriter bw, String footer) throws IOException {
        bw.newLine();
        bw.write("<footer>" + footer + "</footer>");
    }

    @Override
    public void fileEnd(BufferedWriter bw) throws IOException {
        bw.newLine();
        bw.write("</log>");
    }

    @Override
    public String convertLogAdicional(LogAdicional observacao) {
        StringBuilder builder = new StringBuilder();
        for (String key : observacao.keySet()) {
            Object object = observacao.get(key);
            key = key.replace(" ", "");
            String strObject;
            if (object instanceof LogAdicional) {
                strObject = convertLogAdicional((LogAdicional) object);
            } else {
                strObject = object != null ? object.toString() : "null";
            }
            builder.append("<" + key + ">" + (strObject) + "</" + key + ">");
        }
        return builder.toString();
    }
    
}
