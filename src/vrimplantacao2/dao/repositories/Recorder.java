package vrimplantacao2.dao.repositories;

import org.openide.util.Exceptions;
import vrframework.classe.Conexao;

/**
 *
 * @author leandro
 */
public abstract class Recorder<T> implements AutoCloseable {
    
    private boolean done = false;

    public Recorder() {

    }

    public abstract void add(T imp);

    public void done() {
        done = true;
    }

    @Override
    public void close() throws Exception {

    }
            
}
