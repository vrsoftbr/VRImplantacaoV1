package vrimplantacao.gui.assistente.mapamercadologico;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.MercadologicoMapDAO;
import vrimplantacao.vo.vrimplantacao.MercadologicoMapaVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;

class RowData {

    private static List<CustomComboBoxItem> itens;
    private static Map<Integer, CustomComboBoxItem> index;
    
    private final List<RowData> tableData;
    private final MercadologicoMapaVO mapa;
    private final MercadologicoMapDAO dao = new MercadologicoMapDAO();
    
    public static void carregarComboBox() throws Exception {
        index = new LinkedHashMap<>();
        for (MercadologicoVO vo: new MercadologicoDAO().carregarMercadologicoParaMapeamento()) {
            index.put((int) vo.getId(), new CustomComboBoxItem(vo));
        }
        itens = new ArrayList<>(index.values());
    }
    
    public static void make(List<RowData> tableData, MercadologicoMapaVO mapa) throws Exception {
        RowData data = new RowData(tableData, mapa);
        tableData.add(data);
    }
    
    public RowData() throws Exception {
        if (itens == null) {
            carregarComboBox();
        }
        this.tableData = null;
        this.mapa = null;
    }

    private RowData(List<RowData> tableData, MercadologicoMapaVO mapa) throws Exception {
        if (itens == null) {
            carregarComboBox();
        }
        this.tableData = tableData;
        this.mapa = mapa;
    }

    public String getDescricao() {
        return mapa.getDescricao();
    }

    public Integer getMercadologicoVR() {
        return mapa.getMercadologicoVR();
    }

    public String getKey() {
        return mapa.getKey();
    }

    public void setMercadologicoVR(Integer object) {
        mapa.setMercadologicoVR(object);
    }

    private JComboBox<CustomComboBoxItem> comboBox;
    
    public Component getComponent() {
        if (comboBox == null) {
            CustomComboBoxItem item = index.get(this.mapa.getMercadologicoVR());
            comboBox = new JComboBox<>(new CustomComboBoxModel());
            comboBox.setSelectedItem(item);
            comboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        MercadologicoVO item = ((CustomComboBoxItem) e.getItem()).getMerc();                    
                        mapa.setMercadologicoVR(item != null ? (int) item.getId() : null);
                        try {
                            salvar();
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                            Util.exibirMensagemErro(ex, "Erro");
                        }
                    }
                }
            });
        }
        return comboBox;
    }
    
    public void salvar() throws Exception {
        dao.salvar(this.mapa);
        comboBox.setSelectedItem(index.get(this.mapa.getMercadologicoVR()));
    }

    public MercadologicoVO getMercadologico() {
        CustomComboBoxItem item = index.get(this.mapa.getMercadologicoVR());
        return item != null ? item.getMerc() : null;
    }    
    
    private static class CustomComboBoxModel extends DefaultComboBoxModel<CustomComboBoxItem> {

        @Override
        public CustomComboBoxItem getElementAt(int index) {
            return itens.get(index);
        }

        @Override
        public int getSize() {
            return itens.size();
        }
        
    }
    
    private static class CustomComboBoxItem {
        private final MercadologicoVO merc;

        public CustomComboBoxItem(MercadologicoVO merc) {
            this.merc = merc;
        }

        public MercadologicoVO getMerc() {
            return merc;
        }

        @Override
        public String toString() {
            return merc.getDescricao();
        }
    }
}
