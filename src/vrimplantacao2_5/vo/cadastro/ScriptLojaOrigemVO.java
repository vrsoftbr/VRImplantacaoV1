/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.cadastro;

/**
 *
 * @author Michael
 */
public class ScriptLojaOrigemVO {
    
    private int idBanco = 11;
    private String banco = "";
    private String Sistema = "GENERICO";
    private String script = "vazio";

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getSistema() {
        return Sistema;
    }

    public void setSistema(String Sistema) {
        this.Sistema = Sistema;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
    
}
