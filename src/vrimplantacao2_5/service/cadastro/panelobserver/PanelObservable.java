package vrimplantacao2_5.service.cadastro.panelobserver;

/**
 *
 * @author guilhermegomes
 */
public interface PanelObservable {
    void registrarObservador(PanelObserver observer);
    void removerObservador(PanelObserver observer);
    void notificarObservador();  
}
