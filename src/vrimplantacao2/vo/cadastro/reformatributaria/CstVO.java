/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.reformatributaria;

import java.util.Date;

/**
 *
 * @author wesley
 */
public class CstVO {
    
    private int id = -1;
    private String cst;
    private String descricao;
    private final Date datainicio = new Date();
    private boolean grupoibscbs;
    private boolean gruporeducao;
    private boolean grupodiferimento;
    private boolean grupotribregular;
    private boolean grupoibscbsmono;

    public CstVO(String cst, String descricao, boolean grupoibscbs, boolean gruporeducao, boolean grupodiferimento, boolean grupotribregular, boolean grupoibscbsmono) {
        this.cst = cst;
        this.descricao = descricao;
        this.grupoibscbs = grupoibscbs;
        this.gruporeducao = gruporeducao;
        this.grupodiferimento = grupodiferimento;
        this.grupotribregular = grupotribregular;
        this.grupoibscbsmono = grupoibscbsmono;
    }

    public String getCst() {
        return cst;
    }

    public String getDescricao() {
        return descricao;
    }

    public Date getDatainicio() {
        return datainicio;
    }

    public boolean isGrupoibscbs() {
        return grupoibscbs;
    }

    public boolean isGruporeducao() {
        return gruporeducao;
    }

    public boolean isGrupodiferimento() {
        return grupodiferimento;
    }

    public boolean isGrupotribregular() {
        return grupotribregular;
    }

    public boolean isGrupoibscbsmono() {
        return grupoibscbsmono;
    }
    
    
}
