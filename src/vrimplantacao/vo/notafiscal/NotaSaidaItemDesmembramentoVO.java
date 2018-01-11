package vrimplantacao.vo.notafiscal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class NotaSaidaItemDesmembramentoVO extends NotaSaidaItemVO implements Serializable {

    public double percentualEstoque = 0;
    public double percentualDesossa = 0;
    public double percentualCusto = 0;
    public double percentualPerda = 0;

    @Override
    public NotaSaidaItemDesmembramentoVO getCopia() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(this);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));

        return (NotaSaidaItemDesmembramentoVO) in.readObject();
    }
}