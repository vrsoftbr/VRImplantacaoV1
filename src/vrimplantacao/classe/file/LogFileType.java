package vrimplantacao.classe.file;

/**
 *
 * @author Leandro.Caires
 */
public enum LogFileType {
    XML {
        @Override
        public LogFormat getFormat(String arquivo, String titulo) {
            return new XmlLogFormat();
        }
    }, HTML {
        @Override
        public LogFormat getFormat(String arquivo, String titulo) {
            return new HtmlLogFormat(titulo);
        }
    };

    public abstract LogFormat getFormat(String arquivo, String titulo);
    
}
