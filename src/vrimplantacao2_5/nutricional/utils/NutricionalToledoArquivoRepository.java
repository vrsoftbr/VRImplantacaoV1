/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.nutricional.utils;

import java.util.List;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.nutricional.NutricionalRepositoryProvider;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2_5.nutricional.DAO.NutricionalToledoArquivoDAO;
import vrimplantacao2_5.nutricional.vo.InfnutriVO;
import vrimplantacao2_5.nutricional.vo.ItensMgvVO;
import vrimplantacao2_5.nutricional.vo.TxtInfoVO;

/**
 *
 * @author Desenvolvimento
 */
public class NutricionalToledoArquivoRepository {

    NutricionalToledoArquivoDAO dao = new NutricionalToledoArquivoDAO();
    private int tipoScript = 0;

    public int getTipoScript() {
        return tipoScript;
    }

    public void setTipoScript(int tipoScript) {
        this.tipoScript = tipoScript;
    }

    public void criaTabelas() throws Exception {
        try {
            dao.iniciarConexao();
            dao.criarTabelaMgv();
            dao.criarTabelaInfnutri();
            dao.criarTabelaTxtinfo();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Exceptions.printStackTrace(ex);
        }
        dao.finalizarConexao();
    }

    public void popularItensMgv(List<ItensMgvVO> itensMgv, String sistema, String lojaOrigem) throws Exception {
        dao.iniciarConexao();
        if (dao.confereItensMgvPorLoja(lojaOrigem)) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        dao.iniciarConexao();
                        ProgressBar.show();
                        ProgressBar.setCancel(true);
                        ProgressBar.setStatus("Gravando ItensMgv.");
                        ProgressBar.setMaximum(itensMgv.size());
                        for (ItensMgvVO vo : itensMgv) {
                            dao.popularItensMgv6(vo, lojaOrigem, sistema);
                            ProgressBar.next();
                        }
                        ProgressBar.dispose();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        ProgressBar.dispose();
                        Exceptions.printStackTrace(ex);
                    }
                    ProgressBar.dispose();
                }
            };
            thread.start();
            dao.finalizarConexao();
        } else {
            JOptionPane.showMessageDialog(null, "Dados do ItensMgv ja cadastrados para a Loja de origem: " + lojaOrigem);
        }
    }

    public void popularInfnutri(List<InfnutriVO> infNutri, String sistema, String lojaOrigem) throws Exception {
        dao.iniciarConexao();
        if (dao.confereInfnutriPorLoja(lojaOrigem)) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        dao.iniciarConexao();
                        ProgressBar.show();
                        ProgressBar.setCancel(true);
                        ProgressBar.setStatus("Gravando infnutri.");
                        ProgressBar.setMaximum(infNutri.size());
                        for (InfnutriVO vo : infNutri) {
                            dao.popularInfnutri(vo, lojaOrigem, sistema);
                            ProgressBar.next();
                        }
                        ProgressBar.dispose();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        ProgressBar.dispose();
                        Exceptions.printStackTrace(ex);
                    }
                    ProgressBar.dispose();
                }
            };
            thread.start();
            dao.finalizarConexao();
        } else {
            JOptionPane.showMessageDialog(null, "Dados do Infnutri ja cadastrados para a Loja de origem: " + lojaOrigem);
        }
    }

    public void deletarTabelas(String sistema, String lojaOrigem) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    dao.iniciarConexao();
                    ProgressBar.show();
                    ProgressBar.setCancel(false);
                    ProgressBar.setStatus("Deletando ItensMgv e Infnutri.");
                    dao.deletarTabelaMgv(lojaOrigem, sistema);
                    dao.deletarTabelaInfnutri(lojaOrigem, sistema);
                    dao.deletarTabelaTxtinfo(lojaOrigem, sistema);
                    ProgressBar.dispose();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ProgressBar.dispose();
                    Exceptions.printStackTrace(ex);
                }
                ProgressBar.dispose();
            }
        };
        thread.start();
        dao.finalizarConexao();
    }

    public void atulizaMgv(List<ItensMgvVO> itensMgv, String sistema, String lojaOrigem) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    dao.iniciarConexao();
                    dao.deletarTabelaMgv(lojaOrigem, sistema);
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    ProgressBar.setStatus("Atualizando ItensMgv.");
                    ProgressBar.setMaximum(itensMgv.size());
                    for (ItensMgvVO vo : itensMgv) {
                        dao.atualizaItensMgv6(vo, lojaOrigem, sistema);
                        ProgressBar.next();
                    }
                    ProgressBar.dispose();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ProgressBar.dispose();
                    Exceptions.printStackTrace(ex);
                }
                ProgressBar.dispose();
            }
        };
        thread.start();
        dao.finalizarConexao();
        Thread.sleep(1);
    }

    public void atulizaInfnutri(List<InfnutriVO> infNutri, String sistema, String lojaOrigem) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    dao.iniciarConexao();
                    dao.deletarTabelaInfnutri(lojaOrigem, sistema);
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    ProgressBar.setStatus("Gravando infnutri.");
                    ProgressBar.setMaximum(infNutri.size());
                    for (InfnutriVO vo : infNutri) {
                        dao.popularInfnutri(vo, lojaOrigem, sistema);
                        ProgressBar.next();
                    }
                    ProgressBar.dispose();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ProgressBar.dispose();
                    Exceptions.printStackTrace(ex);
                }
                ProgressBar.dispose();
            }
        };
        thread.start();
        dao.finalizarConexao();
        Thread.sleep(1);
    }

    public void importarNutricional(String script, String sistema, String lojaOrigem, int lojaVr) throws Exception {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Importador importar = new Importador(dao);
                    new NutricionalRepositoryProvider(sistema, lojaOrigem, lojaVr);
                    dao.iniciarConexao();
                    dao.setTipoScript(getTipoScript());
                    dao.setSql(script);
                    dao.setSistemaOrigem(sistema);
                    dao.setLojaOrigem(lojaOrigem);
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    ProgressBar.setStatus("Migrando Nutricional.");
                    importar.importarNutricional(OpcaoNutricional.TOLEDO);
                    ProgressBar.dispose();
                    JOptionPane.showMessageDialog(null, "Nutricional Toledo migrado com Sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Houve algum erro durante a importação:\n\n" + ex.getMessage());
                    System.out.println(ex.getMessage());
                    ProgressBar.dispose();
                    Exceptions.printStackTrace(ex);
                }
                ProgressBar.dispose();
            }
        };
        thread.start();
        dao.finalizarConexao();
    }

    public List<String> carregarSistemasComboBox() throws Exception {
        List<String> sistemas;
        dao.iniciarConexao();
        sistemas = dao.getSistemaOrigemCombo();
        dao.finalizarConexao();
        return sistemas;
    }

    public List<String> carregarLojasComboBox() throws Exception {
        List<String> lojas;
        dao.iniciarConexao();
        lojas = dao.getLojaOrigemCombo();
        dao.finalizarConexao();
        return lojas;
    }

    public List<String> carregarLojasVrCombo() throws Exception {
        List<String> lojasVr;
        dao.iniciarConexao();
        lojasVr = dao.getLojaVrCombo();
        dao.finalizarConexao();
        return lojasVr;
    }

    public void setContador(String contador) {
        if ("SIM".equals(contador)) {
            dao.setUsarContador(true);
        }
    }

    public void deletarNutricionais(String sistema, String lojaOrigem) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    dao.iniciarConexao();
                    ProgressBar.show();
                    ProgressBar.setCancel(false);
                    ProgressBar.setStatus("Deletando dados de Nutricionais Toledo");
                    dao.deletarNutricionalToledo(sistema, lojaOrigem);
                    ProgressBar.dispose();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ProgressBar.dispose();
                    Exceptions.printStackTrace(ex);
                }
                ProgressBar.dispose();
            }
        };
        thread.start();
        dao.finalizarConexao();
    }

    public void popularTxtInfo(List<TxtInfoVO> listaTxtInfo, String sistema, String lojaOrigem) throws Exception {
        dao.iniciarConexao();
        if (dao.confereTxtinfoPorLoja(lojaOrigem)) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        dao.iniciarConexao();
                        ProgressBar.show();
                        ProgressBar.setCancel(true);
                        ProgressBar.setStatus("Gravando Txtinfo (alergênicos).");
                        ProgressBar.setMaximum(listaTxtInfo.size());
                        for (TxtInfoVO vo : listaTxtInfo) {
                            dao.popularTxtinfo(vo, lojaOrigem, sistema);
                            ProgressBar.next();
                        }
                        ProgressBar.dispose();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        ProgressBar.dispose();
                        Exceptions.printStackTrace(ex);
                    }
                    ProgressBar.dispose();
                }
            };
            thread.start();
            dao.finalizarConexao();
        } else {
            JOptionPane.showMessageDialog(null, "Dados do Txtinfo ja cadastrados para a Loja de origem: " + lojaOrigem);
        }
    }

    public void atulizaTxtinfo(List<TxtInfoVO> listaTxtInfo, String sistema, String lojaOrigem) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    dao.iniciarConexao();
                    dao.deletarTabelaTxtinfo(lojaOrigem, sistema);
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    ProgressBar.setStatus("Gravando infnutri.");
                    ProgressBar.setMaximum(listaTxtInfo.size());
                    for (TxtInfoVO vo : listaTxtInfo) {
                        dao.popularTxtinfo(vo, lojaOrigem, sistema);
                        ProgressBar.next();
                    }
                    ProgressBar.dispose();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ProgressBar.dispose();
                    Exceptions.printStackTrace(ex);
                }
                ProgressBar.dispose();
            }
        };
        thread.start();
        dao.finalizarConexao();
        Thread.sleep(1);
    }
}
