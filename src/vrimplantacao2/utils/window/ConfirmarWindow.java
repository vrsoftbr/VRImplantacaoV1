package vrimplantacao2.utils.window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Janela de confirmação.
 * @author Leandro
 */
class ConfirmarWindow {
    
    private boolean confirmado = false;
    
    private final JButton btnSim;
    private final JButton btnNao;
    private final JOptionPane option;
    private JDialog dialog;

    public ConfirmarWindow(String titulo, String mensagem) {
        this.btnSim = new JButton("Sim");
        this.btnNao = new JButton("Não");
        this.option = new JOptionPane(mensagem, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, new Object[] { btnSim, btnNao });
        
        btnSim.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    btnSim.transferFocus();
                }
            }
        });
        
        btnNao.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    btnNao.transferFocus();
                }
            }
        });
        
        btnSim.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmado = true;
                dialog.dispose();
            }
        });

        btnNao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmado = false;
                dialog.dispose();
            }
        });

        
        show(titulo);
    }

    private void show(String titulo) {
        dialog = option.createDialog(null, titulo);
        option.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }    
    
}
