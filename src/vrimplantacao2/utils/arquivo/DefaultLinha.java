package vrimplantacao2.utils.arquivo;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultLinha implements LinhaArquivo {
    
    private final Map<String, String> linha = new LinkedHashMap<>();
    
    private final static SimpleDateFormat DEFAULT_DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("hh:mm:ss");
    
    private SimpleDateFormat dataFormat;
    private SimpleDateFormat timeFormat;
    
    public DefaultLinha() {
        this(DEFAULT_DATA_FORMAT, DEFAULT_TIME_FORMAT);
    }
    
    public DefaultLinha(SimpleDateFormat dataFormat, SimpleDateFormat timeFormat) {        
        this.dataFormat = dataFormat;
        this.timeFormat = timeFormat;    
    }

    @Override
    public String getString(String campo) {
        return linha.get(trataCampo(campo));
    }

    @Override
    public void putString(String campo, String contents) {
        linha.put(trataCampo(campo), contents);
    }

    private String trataCampo(String campo) {
        if (campo == null) {
            campo = "";
        }
        return campo.toUpperCase().trim();
    }

    @Override
    public int getInt(String campo) {
        String valor = getString(campo);
        //Coloca um valor padrão se for nulo ou vazio.
        if (valor == null || "".equals(valor.trim())) {
            valor = "0";
        }
        valor = valor.replace(",", ".");
        
        if (valor != null && !"".equals(valor.trim())) {
            String[] val = valor.split("\\.");
            if (val.length == 2) {
                return Integer.parseInt(val[0]);
            } else if (val.length > 2) {
                String inteiro = "";
                for (int i = 0; i < val.length - 1; i++) {
                    inteiro += val[i];
                }
                return Integer.parseInt(inteiro);
            } else {
                return Integer.parseInt(valor.trim());
            }
        } else {
            return 0;
        }
    }

    @Override
    public void putInt(String campo, int contents) {
        linha.put(trataCampo(campo), String.valueOf(contents));
    }

    @Override
    public double getDouble(String campo) {
        String valor = getString(campo);
        if (valor != null && !"".equals(valor.trim())) {
            valor = valor.replace(",", ".");
            valor = valor.replace("%", "");
            String[] val = valor.split("\\.");
            if (val.length == 2) {
                return Double.parseDouble(val[0] + "." + val[1]);
            } else if (val.length > 2) {
                String inteiro = "";
                for (int i = 0; i < val.length - 1; i++) {
                    inteiro += val[i];
                }
                return Double.parseDouble(inteiro + "." + val[val.length - 1]);
            } else {
                return Double.parseDouble(valor);
            }
        } else {
            return 0;
        }
    }

    @Override
    public void putDouble(String campo, double contents) {
        linha.put(trataCampo(campo), String.valueOf(contents));
    }

    //TODO: Melhorar o tratamento dos dados.
    
    @Override
    public Date getData(String campo) {
        String string = getString(campo);
        try {
            if (string != null && !"".equals(string.trim())) {
                return this.dataFormat.parse(string);
            } else {
                return null;
            }
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void putData(String campo, Date contents) {        
        putString(trataCampo(campo), this.dataFormat.format(contents));
    }

    @Override
    public boolean existsColumn(String campo) {
        return linha.containsKey(trataCampo(campo));
    }

    @Override
    public Time getTime(String campo) {
        String string = getString(campo);
        try {
            if (string != null && !"".equals(string.trim())) {
                return new Time(this.timeFormat.parse(string).getTime());
            } else {
                return null;
            }
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void putTime(String campo, Time contents) {
        putString(campo, this.timeFormat.format(contents));
    }

    @Override
    public void putBoolean(String campo, boolean contents) {
        linha.put(campo, (contents ? "S" : "N"));
    }

    @Override
    public boolean getBoolean(String campo) {
        String string = getString(campo);
        
        if (string != null && !"".equals(string.trim())) {
            String val = string.trim().toUpperCase();                

            return val.matches("(S.*|T.*|Y.*|OK|V.*)");
        }

        return false; 
    }

    @Override
    public Object get(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
