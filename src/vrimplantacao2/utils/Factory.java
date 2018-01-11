package vrimplantacao2.utils;

/**
 * Definine uma Factory para fabricar objetos.
 * @param <T> Tipo do objeto a ser fabricado.
 */
public interface Factory<T>{
    public T make();
}
