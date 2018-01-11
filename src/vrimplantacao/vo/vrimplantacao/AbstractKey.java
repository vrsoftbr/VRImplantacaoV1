package vrimplantacao.vo.vrimplantacao;

import java.util.Objects;

/**
 * Classe desenvolvida para trabalhar com a formatação de chaves.
 * @author Leandro
 */
public abstract class AbstractKey {
    /**
     * Código que representa o sistema importado.
     */
    protected String sistemaId = "";
    /**
     * Código que representa a loja da onde foi importada a chave.
     */
    protected String lojaId = "";
    /**
     * Retorna a representação String item no sistema do cliente.
     * @return 
     */
    public abstract String getItemId();

    public void setLojaId(String lojaId) {
        if (lojaId == null) {
            lojaId = "";
        }
        this.lojaId = lojaId.trim();
    }

    public void setSistemaId(String sistemaId) {
        if (sistemaId == null) {
            sistemaId = "";
        }
        this.sistemaId = sistemaId.trim();
    }

    public String getLojaId() {
        return lojaId;
    }

    public String getSistemaId() {
        return sistemaId;
    }

    @Override
    public String toString() {
        return sistemaId + "-" + lojaId + "-" + getItemId();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.sistemaId);
        hash = 47 * hash + Objects.hashCode(this.lojaId);
        hash = 47 * hash + Objects.hashCode(this.getItemId());
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
        final AbstractKey other = (AbstractKey) obj;
        
        return 
                this.sistemaId.equals(other.sistemaId) &&
                this.lojaId.equals(other.lojaId) &&
                this.getItemId().equals(other.getItemId());
    }
 
}
