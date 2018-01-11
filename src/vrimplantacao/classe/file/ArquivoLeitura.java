package vrimplantacao.classe.file;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Leandro
 */
public class ArquivoLeitura implements Closeable, Iterable<String> {
    
    private final List<String> linhas;
    
    public ArquivoLeitura(String fileName) throws FileNotFoundException, IOException {                
        File file = new File(fileName);
        linhas = new ArrayList<>();
        try (FileReader fr = new FileReader(file)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                for(String linha = br.readLine(); linha != null; linha = br.readLine()) {
                    linhas.add(linha);
                }
            }
        }
    }
    
    public String read(int index) {
        if (index < 0 || index >= linhas.size()) {
            return null;
        }
        return linhas.get(index);
    }

    public String remove(int index) {
        return linhas.remove(index);
    }
    
    @Override
    public void close() throws IOException {
        linhas.clear();
    }

    @Override
    public Iterator<String> iterator() {
        return linhas.iterator();
    }


}
