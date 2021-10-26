package vrimplantacao2.services;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.dao.interfaces.planilhas.ConversorPlanilhaDAO;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.utils.arquivo.csv.ArquivoCSV;

/**
 *
 * @author guilhermegomes
 */
public class ConversaoPlanilhaService extends ArquivoCSV {

    private String nameTable = "";
    private String arquivo;

    public ConversaoPlanilhaService(String arquivo, char delimiter, boolean quoteString, char stringQuote) throws Exception {
        super(arquivo, delimiter, quoteString, stringQuote);

        this.arquivo = arquivo;
    }

    public String getNameTable() {
        return nameTable;
    }

    public void setNameTable(String nameTable) {
        this.nameTable = nameTable;
    }

    public void converter() throws Exception {
        List<StringBuilder> inserts = new ArrayList<>();
        ConversorPlanilhaDAO dao = new ConversorPlanilhaDAO();
        StringBuilder createTable = new StringBuilder();

        dao.converter("drop table if exists implantacao." + getNameTable());

        createTable.append("create table implantacao." + getNameTable() + "(");

        String format = "";
        String campos = "";

        for (String column : cabecalho) {
            format = " varchar,\n";

            createTable.append(column + format);

            campos += column + ",";
        }

        dao.converter(createTable.toString().substring(0, createTable.length() - 2) + ");");

        campos = campos.substring(0, campos.length() - 1);
        
        StringBuilder insertTable = new StringBuilder();
        
        insertTable.append("insert into implantacao." + getNameTable() + " (");
        insertTable.append(campos + ") values (");        
        
        String cmp[] = campos.split(",");
        
        for (LinhaArquivo registro : dados) {
            
            for (int i = 0; i < cabecalho.size(); i++) {
                
                if(i == cabecalho.size() - 1) {
                    insertTable.append(registro.getString(cmp[i]) + ") ");
                } else {
                    insertTable.append(registro.getString(cmp[i]) + ",");
                }
                
            }

            //String formatted = insertTable.toString().substring(0, insertTable.toString().length() - 1) + "),";
            
            System.out.println(insertTable.toString());
        }
    }
}
