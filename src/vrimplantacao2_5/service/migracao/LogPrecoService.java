/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.service.migracao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.LogPrecoDAO;
import vrimplantacao2_5.vo.cadastro.LogPrecoVO;

/**
 *
 * @author Michael
 */
public class LogPrecoService {

    private LogPrecoDAO logPrecoDAO;

    public LogPrecoService() {
        this.logPrecoDAO = new LogPrecoDAO();
    }

    public void converteLogPreco(List<ProdutoIMP> organizados, String sistema, String loja) throws Exception {
        List<LogPrecoVO> listaVo = new ArrayList<>();
        ProgressBar.setStatus("Convertendo id de log: " + organizados.size());
        ProgressBar.setMaximum(organizados.size());
        for (ProdutoIMP organizado : organizados) {
            LogPrecoVO vo = new LogPrecoVO();
            vo.setImpSistema(sistema);
            vo.setImpLoja(loja);
            vo.setImpId(organizado.getImportId());
            vo.setDescricao(organizado.getDescricaoCompleta());
            vo.setCoigoatual(new ProdutoAnteriorDAO().getCodigoAnterior2(sistema, loja, String.valueOf(organizado.getImportId())));
            vo.setPreco(organizado.getPrecovenda());
            vo.setDataAlteracao(new Date());
            listaVo.add(vo);
            ProgressBar.next();
        }
        logPrecoDAO.deletarLogAtualizaPreco(sistema, loja);
        logPrecoDAO.salvarLogPreco(listaVo);
    }

}
