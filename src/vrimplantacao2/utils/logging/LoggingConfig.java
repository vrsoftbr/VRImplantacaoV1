package vrimplantacao2.utils.logging;

import java.util.logging.Level;

/**
 *
 * @author Leandro
 */
public class LoggingConfig {
    
    private String nome;
    private LoggingType type = LoggingType.CONSOLE;
    private Level level = Level.OFF;

    public LoggingConfig() {
        this.nome = "";
    }
    
    public LoggingConfig(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LoggingType getType() {
        return type;
    }

    public void setType(LoggingType type) {
        this.type = type;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
    
}
