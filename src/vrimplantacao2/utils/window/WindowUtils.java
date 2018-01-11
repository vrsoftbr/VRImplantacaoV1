package vrimplantacao2.utils.window;

/**
 * Classe proxy para facilitar o manuseamento de janelas no sistema.
 * @author Leandro
 */
public final class WindowUtils {
    
    private WindowUtils(){}
    
    public static boolean confirmar(String mensagem) {
        
        ConfirmarWindow confirmar = new ConfirmarWindow("Confirmar", mensagem);
        
        return confirmar.isConfirmado();

    }
    
}
