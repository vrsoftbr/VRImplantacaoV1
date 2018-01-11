/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.parametro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Leandro Caires
 */
public class Parametro {
    private List<String> keys;
    private String value;

    public Parametro(List<String> keys, String value) {
        this.keys = keys;
        this.value = value;
    }
    
    public Parametro(String[] keys, String value) {
        this(new ArrayList<>(Arrays.asList(keys)), value);
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.keys);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Parametro other = (Parametro) obj;
        if (!Objects.equals(this.keys, other.keys)) {
            return false;
        }
        return true;
    }
    
}
