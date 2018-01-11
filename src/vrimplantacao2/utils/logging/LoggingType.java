package vrimplantacao2.utils.logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Leandro
 */
public enum LoggingType {
    
    CONSOLE {
        @Override
        public Handler getHandler() {
            Handler handler = new Handler() {
                @Override
                public void publish(LogRecord record) {
                    System.out.println(getFormatter().format(record));
                    flush();
                }

                @Override
                public void flush() {
                    
                }

                @Override
                public void close() throws SecurityException {
                    
                }
            };
            handler.setFormatter(new SimpleFormatter());
            return handler;
        }
    },
    FILE {
        private Handler handler;
        @Override
        public Handler getHandler() {
            try {
                if (handler != null) {
                    handler.flush();  
                } else {
                    File f = new File("c:/vr/implantacao/log");
                    if (!f.exists()) {
                        f.mkdir();
                    }
                    handler = new FileHandler("c:/vr/implantacao/log/LOG " + DATE_FORMAT.format(new Date()) + ".log", true);                  
                    handler.setFormatter(new SimpleFormatter() {

                        @Override
                        public String getHead(Handler h) {
                            return "------> ";
                        }
                        
                    });
                }
                return handler;
            } catch (IOException | SecurityException ex) {
                LOG.log(Level.SEVERE, "Erro ao gerar o arquivo de log", ex);
                return new ConsoleHandler();
            }
        }
    };
    
    private static final Logger LOG = Logger.getLogger(LoggingType.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    private Map<String, String> params = new LinkedHashMap<>();

    public Map<String, String> getParams() {
        return params;
    }
    
    public abstract Handler getHandler();
    
}
