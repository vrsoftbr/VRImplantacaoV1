package vrimplantacao2.dao.repositories;

/**
 *
 * @author leandro
 */
public interface MigracaoFuture<T> {
    
    public void call(Recorder<T> recorder) throws Exception;
    
}
