/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.nutricional.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import vrimplantacao2_5.nutricional.vo.InfnutriVO;
import vrimplantacao2_5.nutricional.vo.ItensMgvVO;
import vrimplantacao2_5.nutricional.vo.TxtInfoVO;

/**
 * Clase que implementa o padrão Factory. Gera tabelas de acordo com a o tipo do
 * arquivo.
 *
 * @author Michael
 */
public class GeradorTabelas {

    private JTable jTableLerArquivos = new JTable();

    public GeradorTabelas(JTable jTableLerArquivos) {
        this.jTableLerArquivos = jTableLerArquivos;
    }

    public void carregarTabelaMgv(int parametro, List<ItensMgvVO> listaItensMgv) {
        if (parametro == 1) {
            Object[] colunasIntensMgv = {"DEPARTAMENTO", "TIPO", "COD_ITEM", "PREÇO", "VALIDADE", "DESCRIÇÃO 1 E 2", "COD_NUTRICIONAL",
                "BALANÇA", "DEMAIS DADOS"};
            //INVERTIDA SEQUENCIA, EXTRA E IMAGEM VEM ANTES DO NUTRICIONAL.
            DefaultTableModel modelo = (DefaultTableModel) jTableLerArquivos.getModel();
            modelo.setColumnIdentifiers(colunasIntensMgv);
            modelo.setNumRows(0);
            jTableLerArquivos.getTableHeader().setOpaque(false);
            ((DefaultTableCellRenderer) jTableLerArquivos.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            jTableLerArquivos.getTableHeader().setBackground(Color.BLACK);
            jTableLerArquivos.getTableHeader().setForeground(Color.ORANGE);
            jTableLerArquivos.getTableHeader().setFont(new Font("Tahome", Font.BOLD, 8));
            jTableLerArquivos.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableLerArquivos.getColumnModel().getColumn(1).setPreferredWidth(30);
            jTableLerArquivos.getColumnModel().getColumn(2).setPreferredWidth(60);
            jTableLerArquivos.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTableLerArquivos.getColumnModel().getColumn(4).setPreferredWidth(60);
            jTableLerArquivos.getColumnModel().getColumn(5).setPreferredWidth(200);
            jTableLerArquivos.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableLerArquivos.getColumnModel().getColumn(8).setPreferredWidth(450);
            for (ItensMgvVO vo : listaItensMgv) {
                modelo.addRow(new Object[]{
                    vo.getDepartamento(), vo.getTipo(), vo.getCodigo(), vo.getPreco(), vo.getValidade(),
                    vo.getDescricao(), vo.getNutricional(), vo.getPesavel(), /*vo.getInfoExtra(), vo.getImg(),
                    vo.getDadosImpressao(), vo.getCodFornecedor(), vo.getLote(), vo.getEan(),*/ vo.getDemaisDados()
                });
            }
            jTableLerArquivos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected,
                            hasFocus, row, column);
                    setHorizontalAlignment(JLabel.CENTER);
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(220, 220, 220));
                    }
                    return this;
                }
            });
        } else {
            carregarTabelaInfo(-2);
        }
    }

    public void carregarTabelaInfinutri(int parametro, List<InfnutriVO> listaItensInfnutri) {
        if (parametro == 2) {
            Object[] colunasIntensMgv = {"INDICADOR", "C. NUTRICIONAL", "RESERVADO", "QTD",
                "PORÇAO G OU UN", "M. CAS. INTEIRA", "M. CAS. FRAC.", "M. CAS. XICARAS E FATIAS", "CALORIAS",
                "CARBOIDRATO", "PROTEINAS", "GORDURAS TOTAIS", "G. SATURADAS", "G. TRANS", "FIBRA", "SÓDIO"};
            //INVERTIDA SEQUENCIA, EXTRA E IMAGEM VEM ANTES DO NUTRICIONAL.
            DefaultTableModel modelo = (DefaultTableModel) jTableLerArquivos.getModel();
            modelo.setColumnIdentifiers(colunasIntensMgv);
            modelo.setNumRows(0);
            jTableLerArquivos.getTableHeader().setOpaque(false);
            ((DefaultTableCellRenderer) jTableLerArquivos.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            jTableLerArquivos.getTableHeader().setBackground(Color.BLACK);
            jTableLerArquivos.getTableHeader().setForeground(Color.ORANGE);
            jTableLerArquivos.getTableHeader().setFont(new Font("Tahome", Font.BOLD, 8));
            jTableLerArquivos.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTableLerArquivos.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTableLerArquivos.getColumnModel().getColumn(2).setPreferredWidth(40);
            jTableLerArquivos.getColumnModel().getColumn(3).setPreferredWidth(40);
            jTableLerArquivos.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableLerArquivos.getColumnModel().getColumn(11).setPreferredWidth(100);
            for (InfnutriVO vo : listaItensInfnutri) {
                modelo.addRow(new Object[]{
                    vo.getIndicador(), vo.getNutricional(), vo.getReservado(), vo.getQuantidade(), vo.getPorcaoUnGr(), vo.getMedidaCaseiraInteira(),
                    vo.getMedidaCaseiraDecimalFracionado(), vo.getMedidaCaseiraXicaraFatia(), vo.getCalorias(), vo.getCarboidratos(),
                    vo.getProteinas(), vo.getGordurasTotais(), vo.getGordurasSaturadas(), vo.getGordurasTrans(), vo.getFibra(), vo.getSodio()
                });
            }
            jTableLerArquivos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected,
                            hasFocus, row, column);
                    setHorizontalAlignment(JLabel.CENTER);
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(220, 220, 220));
                    }
                    return this;
                }
            });
        } else {
            carregarTabelaInfo(-2);
        }
    }

    public void carregarTabelaTxtInfo(int parametro, List<TxtInfoVO> listaTxtInfo) {
        if (parametro == 3) {
            Object[] colunasIntensMgv = {"CODIGO", "OBS", "LINHA 1", "LINHA 2",
                "LINHA 3", "LINHA 4", "LINHA 5", "LINHA 6", "LINHA 7",
                "LINHA 8", "LINHA 9", "LINHA 10", "LINHA 11", "LINHA 12", "LINHA 13", "LINHA 14 E 15"
            };
            //INVERTIDA SEQUENCIA, EXTRA E IMAGEM VEM ANTES DO NUTRICIONAL.
            DefaultTableModel modelo = (DefaultTableModel) jTableLerArquivos.getModel();
            modelo.setColumnIdentifiers(colunasIntensMgv);
            modelo.setNumRows(0);
            jTableLerArquivos.getTableHeader().setOpaque(false);
            ((DefaultTableCellRenderer) jTableLerArquivos.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            jTableLerArquivos.getTableHeader().setBackground(Color.BLACK);
            jTableLerArquivos.getTableHeader().setForeground(Color.ORANGE);
            jTableLerArquivos.getTableHeader().setFont(new Font("Tahome", Font.BOLD, 8));
            jTableLerArquivos.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableLerArquivos.getColumnModel().getColumn(1).setPreferredWidth(400);
            jTableLerArquivos.getColumnModel().getColumn(2).setPreferredWidth(300);
            jTableLerArquivos.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTableLerArquivos.getColumnModel().getColumn(4).setPreferredWidth(300);
            jTableLerArquivos.getColumnModel().getColumn(5).setPreferredWidth(300);
            for (TxtInfoVO vo : listaTxtInfo) {
                modelo.addRow(new Object[]{
                    vo.getCodigo(), vo.getObs(), vo.getLinha1(), vo.getLinha2(), vo.getLinha3(), vo.getLinha4(), vo.getLinha5(),
                    vo.getLinha6(), vo.getLinha7(), vo.getLinha8(), vo.getLinha9(), vo.getLinha10(), vo.getLinha11(), vo.getLinha12(),
                    vo.getLinha13(), vo.getLinha14E15()
                });
            }
            jTableLerArquivos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected,
                            hasFocus, row, column);
                    setHorizontalAlignment(JLabel.CENTER);
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(220, 220, 220));
                    }
                    return this;
                }
            });
        } else {
            carregarTabelaInfo(-2);
        }
    }

    public void carregarTabelaInfo(int parametro) {
        if (parametro == -1) {
            Object[] colunas = {"TABELA SEM DADOS"};
            Object[] linhas = {"  Selecione o tipo e o arquivo, depois clique em carregar"};
            DefaultTableModel modelo = (DefaultTableModel) jTableLerArquivos.getModel();
            modelo.setColumnIdentifiers(colunas);
            modelo.setNumRows(0);
            modelo.addRow(linhas);
            jTableLerArquivos.getTableHeader().setBackground(Color.BLACK);
            jTableLerArquivos.getTableHeader().setForeground(Color.ORANGE);
        } else if (parametro == -2) {
            Object[] colunas = {"Ops, Problema ao gerar a Tabela."};
            Object[] linhas = {"  Houve alguem erro, contate a Migração"};
            DefaultTableModel modelo = (DefaultTableModel) jTableLerArquivos.getModel();
            modelo.setColumnIdentifiers(colunas);
            modelo.setNumRows(0);
            modelo.addRow(linhas);
            jTableLerArquivos.getTableHeader().setBackground(new Color(160, 10, 0));
            jTableLerArquivos.getTableHeader().setForeground(new Color(255, 165, 0));
        }
        jTableLerArquivos.getTableHeader().setOpaque(false);
        ((DefaultTableCellRenderer) jTableLerArquivos.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        jTableLerArquivos.getTableHeader().setFont(new Font("Tahome", Font.BOLD, 12));
        jTableLerArquivos.setFont(new Font("Default", Font.CENTER_BASELINE, 18));
        jTableLerArquivos.setRowHeight(50);
        jTableLerArquivos.setAutoResizeMode(1);
    }

}
