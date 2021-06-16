package vr.implantacao.service.cadastro.panelobserver;

/**
 *
 * @author guilhermegomes
 */
public interface PanelObservable {
    void registrarObservador(PanelObserver observer);
    void removerObservador(PanelObserver observer);
    void notificarObservador();  
}
