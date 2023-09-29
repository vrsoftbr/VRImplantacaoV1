/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.mural.vo;

/**
 *
 * @author Michael
 */
public class MemorandoVO {
    
    private int id = 0;
    private String data = "Sem data no banco";
    private String lembrete = "Anote detalhes do projeto aqui, depois clique em Gravar.\n"
            + "Cuidado para não salvar dados sensíveis."; 
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLembrete() {
        return lembrete;
    }

    public void setLembrete(String lembrete) {
        this.lembrete = lembrete;
    }
       
}
