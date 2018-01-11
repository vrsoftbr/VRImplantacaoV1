package vrimplantacao.vo;

public class ParametroVO {

    public int id = 0;
    public String descricao = "";
    public String valor = "";

    public ParametroVO() {
    }

    public ParametroVO(int i_id, String i_valor) {
        this.id = i_id;
        this.valor = i_valor;
    }

    public ParametroVO(int i_id, boolean i_valor) {
        this(i_id, String.valueOf(i_valor));
    }

    public ParametroVO(int i_id, int i_valor) {
        this(i_id, String.valueOf(i_valor));
    }

    public String getString() {
        return this.valor;
    }

    public boolean getBoolean() {
        if (valor.equals("")) {
            return false;
        } else {
            try {
                return Boolean.parseBoolean(this.valor);

            } catch (Exception ex) {
                return false;
            }
        }
    }

    public int getInt() {
        if (valor.equals("")) {
            return 0;
        } else {
            try {
                return Integer.parseInt(this.valor);

            } catch (Exception ex) {
                return 0;
            }
        }
    }

    public double getDouble() {
        if (valor.equals("")) {
            return 0;
        } else {
            try {
                return Double.parseDouble(this.valor.replace(".", "").replace(",", "."));

            } catch (Exception ex) {
                return 0;
            }
        }
    }
}
