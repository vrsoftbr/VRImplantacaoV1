package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author lucasrafael
 */
public class CodigoAnteriorFornecedorVO {
    
    private long codigoAnterior = 0;
    private int codigoAtual = 0;
    private int idLoja = 0;
    private int id_uf = 0;
  
    public void setCodigoAnterior(long codigoAnterior) {
        this.codigoAnterior = codigoAnterior;        
    }
    
    public void setCodigoAtual(int codigoAtual) {
        this.codigoAtual = codigoAtual;
    }
    
    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }
    
    public long getCodigoAnterior() {
        return codigoAnterior;
    }
    
    public int getCodigoAtual() {
        return codigoAtual;
    }
    
    public int getIdLoja() {
        return idLoja;
    }

    public int getId_uf() {
        return id_uf;
    }

    public void setId_uf(int id_uf) {
        this.id_uf = id_uf;
    }
    
    public String getChaveUnica() {
        return makeChaveUnica(getIdLoja(), getCodigoAnterior());
    }
    
    public static String makeChaveUnica(int idLojaCliente, long codigoAnterior) {
        return idLojaCliente + "-" + codigoAnterior;
    }
}
