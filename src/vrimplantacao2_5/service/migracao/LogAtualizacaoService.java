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
import vrimplantacao2_5.dao.LogAtualizacaoDAO;
import vrimplantacao2_5.vo.cadastro.LogAtualizacaoVO;

/**
 *
 * @author Michael
 */
public class LogAtualizacaoService {

    private final LogAtualizacaoDAO logAtualizacaoDAO;

    public LogAtualizacaoService() {
        this.logAtualizacaoDAO = new LogAtualizacaoDAO();
    }

    public void converteLogAtualizacao(List<ProdutoIMP> organizados, String sistema, String impLoja, Integer lojaAtual) throws Exception {
        List<LogAtualizacaoVO> listaVo = new ArrayList<>();
        ProgressBar.setStatus("Convertendo id de log: " + organizados.size());
        ProgressBar.setMaximum(organizados.size());
        for (ProdutoIMP organizado : organizados) {
            LogAtualizacaoVO vo = new LogAtualizacaoVO();
            vo.setImpSistema(sistema);
            vo.setImpLoja(impLoja);
            vo.setImpId(organizado.getImportId());
            vo.setLojaatual(lojaAtual);
            vo.setDescricao(organizado.getDescricaoCompleta());
            vo.setCoigoatual(new ProdutoAnteriorDAO().getCodigoAnterior2(sistema, impLoja, String.valueOf(organizado.getImportId())));
            vo.setPreco(organizado.getPrecovenda());
            vo.setDataAlteracao(new Date());
            vo.setEstoque(organizado.getEstoque());
            vo.setCustoComImposto(organizado.getCustoComImposto());
            vo.setCustoSemImposto(organizado.getCustoComImposto());
            listaVo.add(vo);
            ProgressBar.next();
        }
        logAtualizacaoDAO.deletarLogAtualizacao(sistema, impLoja, lojaAtual);
        logAtualizacaoDAO.salvarLogAtualizacao(listaVo);
    }

}
