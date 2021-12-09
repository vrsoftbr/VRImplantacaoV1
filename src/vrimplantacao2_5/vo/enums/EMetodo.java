/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.enums;

/**
 *
 * @author Desenvolvimento
 */
public enum EMetodo {
    
    SALVAR(1, "SALVAR"),
    UNIFICAR(2, "UNIFICAR"),
    ATUALIZAR(3, "ATUALIZAR");
    
    EMetodo(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
    
    private int id;
    private String  descricao;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getDescricao() {
        return this.descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public static EMetodo getById(int id) {
        for (EMetodo metodo: values()) {
            if (metodo.getId() == id) {
                return metodo;
            }
        }
        return null;
    }
    
}
