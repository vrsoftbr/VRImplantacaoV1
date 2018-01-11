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
class HtmlLogFormat implements LogFormat{
    private final String titulo;

    public HtmlLogFormat(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public void fileStart(BufferedWriter bw) throws IOException {
        bw.write("<!DOCTYPE html>");
        bw.newLine();
        bw.write("<html>");
        bw.newLine();
        bw.write("<header><meta charset=\"UTF-8\">");
        bw.newLine();   
        bw.write("<title>" + titulo + "</title>");
        bw.newLine(); 
        bw.write("<style>");
        bw.newLine(); 
        bw.write("body { font-family: sans-serif;}");
        bw.newLine(); 
        bw.write(".grid {border-style: solid;border-width: 5px; border-color: white; }");
        bw.newLine(); 
        bw.write(".column { background-color: lightblue; padding-top: 10px;");
        bw.newLine(); 
        bw.write("padding-right: 10px; padding-bottom: 10px; padding-left: 10px;}");
        bw.newLine(); 
        bw.write(".logadic { }");
        bw.newLine(); 
        bw.write("</style>");
        bw.newLine();   
        bw.write("</header>");
        bw.newLine();
        bw.write("<body>");
        bw.newLine();
        bw.write("<center>");
        bw.newLine();
    }

    @Override
    public void formatHeader(BufferedWriter bw, String header) throws IOException {
        bw.write("<h1>" + titulo + "</h1>");
        bw.newLine();
        bw.write("<p>" + replaceReturn(header) + "</p><br><br>");
        bw.newLine();
    }

    @Override
    public void formatLinhas(BufferedWriter bw, List<LogLine> linhas) throws IOException {
        if (linhas.isEmpty()) {
            bw.write("<table class=\"grid\"><tr><td>Não há registros</td></tr></table>");
            bw.newLine();
        } else {
            bw.write("<table class=\"grid\">");
            bw.write("<tr class=\"line\"><th class=\"column\">Ocorrido em</th><th class=\"column\">Categoria</th><th class=\"column\">Descrição</th><th class=\"column\">Informações<br>Adicionais</th></tr>");
            for (LogLine line: linhas) {
                String strLine = "<tr class=\"line\">" + 
                        "<td class=\"column\">" + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(line.data) + "</td>" + "<td class=\"column\">" + line.categoria + "</td>" + "<td class=\"column\">" + line.descricao + "</td>" + "<td class=\"column\">" + convertLogAdicional(line.observacao) + "</td>" + 
                "</tr>";
                bw.write(strLine);
                bw.newLine();
            }                    
            bw.write("</table>");
        }
    }

    @Override
    public void formatFooter(BufferedWriter bw, String footer) throws IOException {
        bw.newLine();
        bw.write("<h2>Encerramento</h2>");
        bw.newLine();
        bw.write("<p>" + replaceReturn(footer) + "</p>");
    }

    @Override
    public void fileEnd(BufferedWriter bw) throws IOException {
        bw.newLine();
        bw.write("</center>");
        bw.newLine();
        bw.write("</body>");
        bw.newLine();
        bw.write("</html>");
    }

    @Override
    public String convertLogAdicional(LogAdicional observacao) {
        StringBuilder builder = new StringBuilder();
        builder.append("<table class=\"logadic\">");
        for (String key : observacao.keySet()) {
            Object object = observacao.get(key);
            key = key.replace(" ", "");
            String strObject;
            if (object instanceof LogAdicional) {
                strObject = convertLogAdicional((LogAdicional) object);
            } else {
                strObject = object != null ? object.toString() : "null";
            }
            builder.append("<tr><td>").append(key).append("</td><td>").append(strObject).append("</td></tr>");
        }
        builder.append("</table>");
        return builder.toString();
    }

    private String replaceReturn(String footer) {
        return footer.replace("\\n", "<br>");
    }
    
}
