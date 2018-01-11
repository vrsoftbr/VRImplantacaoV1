package vrimplantacao.vo.notafiscal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class NotaEntradaItemDesmembramentoVO extends NotaEntradaItemVO implements Serializable {

    public double percentualEstoque = 0;
    public double percentualDesossa = 0;
    public double percentualCusto = 0;

    @Override
    public NotaEntradaItemDesmembramentoVO getCopia() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(this);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));

        return (NotaEntradaItemDesmembramentoVO) in.readObject();
    }
}