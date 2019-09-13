package vrimplantacao.dao.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.dao.ParametroDAO;
import vrimplantacao.dao.cadastro.AliquotaDAO;
import vrimplantacao.dao.cadastro.ContratoDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.ModeloDAO;
import vrimplantacao.dao.cadastro.TipoPisCofinsDAO;
import vrimplantacao.dao.estoque.EstoqueDAO;
import vrimplantacao.dao.financeiro.ContratoLancamentoDAO;
import vrimplantacao.dao.financeiro.ReceberDevolucaoDAO;
import vrimplantacao.dao.financeiro.ReceberVendaPrazoDAO;
import vrimplantacao.dao.fiscal.CfopDAO;
import vrimplantacao.dao.fiscal.EscritaDAO;
import vrimplantacao.dao.fiscal.EscritaFechamentoDAO;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.administrativo.AcertoEstoqueVO;
import vrimplantacao.vo.administrativo.TipoEntradaSaida;
import vrimplantacao.vo.administrativo.TipoMovimentacao;
import vrimplantacao.vo.cadastro.ContratoAcordoExcecaoLojaVO;
import vrimplantacao.vo.cadastro.ContratoAcordoVO;
import vrimplantacao.vo.cadastro.ContratoFornecedorExcecaoAcordoVO;
import vrimplantacao.vo.cadastro.ContratoFornecedorExcecaoVO;
import vrimplantacao.vo.cadastro.ContratoFornecedorVO;
import vrimplantacao.vo.cadastro.ContratoVO;
import vrimplantacao.vo.cadastro.SituacaoCadastro;
import vrimplantacao.vo.cadastro.TipoOrgaoPublico;
import vrimplantacao.vo.estoque.AcertoCestaBasicaVO;
import vrimplantacao.vo.estoque.AcertoTrocaVO;
import vrimplantacao.vo.estoque.TipoBaixaPerda;
import vrimplantacao.vo.notafiscal.CfopVO;
import vrimplantacao.vo.notafiscal.ContratoLancamentoVO;
import vrimplantacao.vo.notafiscal.EscritaItemVO;
import vrimplantacao.vo.notafiscal.EscritaVO;
import vrimplantacao.vo.notafiscal.NotaEntradaItemDesmembramentoVO;
import vrimplantacao.vo.notafiscal.NotaEntradaItemVO;
import vrimplantacao.vo.notafiscal.NotaEntradaVO;
import vrimplantacao.vo.notafiscal.NotaFiscalAliquotaVO;
import vrimplantacao.vo.notafiscal.NotaFiscalFornecedorVO;
import vrimplantacao.vo.notafiscal.NotaSaidaCupomVO;
import vrimplantacao.vo.notafiscal.NotaSaidaDevolucaoCupomVO;
import vrimplantacao.vo.notafiscal.NotaSaidaItemDesmembramentoVO;
import vrimplantacao.vo.notafiscal.NotaSaidaItemVO;
import vrimplantacao.vo.notafiscal.NotaSaidaNotaEntradaVO;
import vrimplantacao.vo.notafiscal.NotaSaidaReposicaoVO;
import vrimplantacao.vo.notafiscal.NotaSaidaTrocaCupomVO;
import vrimplantacao.vo.notafiscal.NotaSaidaVO;
import vrimplantacao.vo.notafiscal.NotaSaidaVencimentoVO;
import vrimplantacao.vo.notafiscal.ReceberDevolucaoVO;
import vrimplantacao.vo.notafiscal.ReceberVendaPrazoVO;
import vrimplantacao.vo.notafiscal.SituacaoNotaEntrada;
import vrimplantacao.vo.notafiscal.SituacaoNotaSaida;
import vrimplantacao.vo.notafiscal.SituacaoReceberDevolucao;
import vrimplantacao.vo.notafiscal.SituacaoReceberVendaPrazo;
import vrimplantacao.vo.notafiscal.SituacaoReposicao;
import vrimplantacao.vo.notafiscal.TipoContrato;
import vrimplantacao.vo.notafiscal.TipoCrt;
import vrimplantacao.vo.notafiscal.TipoEmpresaVO;
import vrimplantacao.vo.notafiscal.TipoEntradaVO;
import vrimplantacao.vo.notafiscal.TipoLocalBaixaNotaSaida;
import vrimplantacao.vo.notafiscal.TipoSaidaVO;
import vrframework.classe.Conexao;
import vrframework.classe.Database;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2.parametro.Parametros;

public class NotaSaidaDAO {

    public NotaSaidaVO carregar(long i_id) throws Exception {
        ResultSet rst = null;
        Statement stm = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT notasaida.*, COALESCE(fornecedor.id_estado, cliente.id_estado) AS id_estadodestinatario, COALESCE(nsc.numeronota, 0) AS numeronotacomplemento,");
        sql.append(" COALESCE(fornecedor.razaosocial, cliente.nome)AS destinatario, COALESCE(tipodevolucao.descricao, '') AS tipodevolucao");
        sql.append(" FROM notasaida");
        sql.append(" LEFT JOIN fornecedor ON fornecedor.id = notasaida.id_fornecedordestinatario");
        sql.append(" LEFT JOIN clienteeventual AS cliente ON cliente.id = notasaida.id_clienteeventualdestinatario");
        sql.append(" LEFT JOIN notasaida nsc ON nsc.id = notasaida.id_notasaidacomplemento");
        sql.append(" LEFT JOIN tipodevolucao ON tipodevolucao.id = notasaida.id_tipodevolucao");
        sql.append(" WHERE notasaida.id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Nota saída não encontrada!");
        }

        NotaSaidaVO oNotaSaida = new NotaSaidaVO();
        oNotaSaida.id = rst.getLong("id");
        oNotaSaida.idLoja = rst.getInt("id_loja");
        oNotaSaida.idTipoNota = rst.getInt("id_tiponota");
        oNotaSaida.numeroNota = rst.getInt("numeronota");
        oNotaSaida.idTipoSaida = rst.getInt("id_tiposaida");
        oNotaSaida.dataHoraEmissao = Util.formatDataHoraGUI(rst.getTimestamp("datahoraemissao"));
        oNotaSaida.dataSaida = Util.formatDataGUI(rst.getDate("datasaida"));
        oNotaSaida.idClienteEventualDestinatario = rst.getObject("id_clienteeventualdestinatario") == null ? -1 : rst.getInt("id_clienteeventualdestinatario");
        oNotaSaida.idFornecedorDestinatario = rst.getObject("id_fornecedordestinatario") == null ? -1 : rst.getInt("id_fornecedordestinatario");
        oNotaSaida.destinatario = rst.getString("destinatario");
        oNotaSaida.idEstadoDestinatario = rst.getInt("id_estadodestinatario");
        oNotaSaida.tipoLocalBaixa = rst.getInt("tipolocalbaixa");
        oNotaSaida.idSituacaoNotaSaida = rst.getInt("id_situacaonotasaida");
        oNotaSaida.idFornecedorTransportador = rst.getObject("id_fornecedortransportador") == null ? -1 : rst.getInt("id_fornecedortransportador");
        oNotaSaida.idClienteEventualTransportador = rst.getObject("id_clienteeventualtransportador") == null ? -1 : rst.getInt("id_clienteeventualtransportador");
        oNotaSaida.idMotoristaTransportador = rst.getObject("id_motoristatransportador") == null ? -1 : rst.getInt("id_motoristatransportador");
        oNotaSaida.placa = rst.getString("placa");
        oNotaSaida.idTipoFreteNotaFiscal = rst.getInt("id_tipofretenotafiscal");
        oNotaSaida.informacaoComplementar = rst.getString("informacaocomplementar");
        oNotaSaida.valorBaseCalculo = rst.getDouble("valorbasecalculo");
        oNotaSaida.valorBaseSubstituicao = rst.getDouble("valorbasesubstituicao");
        oNotaSaida.valorIcms = rst.getDouble("valoricms");
        oNotaSaida.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
        oNotaSaida.valorProduto = rst.getDouble("valorproduto");
        oNotaSaida.valorFrete = rst.getDouble("valorfrete");
        oNotaSaida.valorSeguro = rst.getDouble("valorseguro");
        oNotaSaida.valorDesconto = rst.getDouble("valordesconto");
        oNotaSaida.valorOutrasDespesas = rst.getDouble("valoroutrasdespesas");
        oNotaSaida.valorIpi = rst.getDouble("valoripi");
        oNotaSaida.valorTotal = rst.getDouble("valortotal");
        oNotaSaida.idTipoDevolucao = rst.getObject("id_tipodevolucao") == null ? -1 : rst.getInt("id_tipodevolucao");
        oNotaSaida.tipoDevolucao = rst.getString("tipodevolucao");
        oNotaSaida.idSituacaoNfe = rst.getInt("id_situacaonfe");
        oNotaSaida.chaveNfe = rst.getString("chavenfe");
        oNotaSaida.reciboNfe = rst.getString("recibonfe");
        oNotaSaida.impressao = rst.getBoolean("impressao");
        oNotaSaida.protocoloRecebimentoNfe = rst.getString("protocolorecebimentonfe");
        oNotaSaida.dataHoraRecebimentoNfe = rst.getTimestamp("datahorarecebimentonfe") == null ? "" : Util.formatDataHoraGUI(rst.getTimestamp("datahorarecebimentonfe"));
        oNotaSaida.numeroNotaComplemento = rst.getInt("numeronotacomplemento");
        oNotaSaida.idNotaSaidaComplemento = rst.getObject("id_notasaidacomplemento") == null ? -1 : rst.getLong("id_notasaidacomplemento");
        oNotaSaida.emailNfe = rst.getBoolean("emailnfe");
        oNotaSaida.contingenciaNfe = rst.getBoolean("contingencianfe");
        oNotaSaida.aplicaIcmsDesconto = rst.getBoolean("aplicaicmsdesconto");
        oNotaSaida.aplicaIcmsEncargo = rst.getBoolean("aplicaicmsencargo");
        oNotaSaida.idNotaEntrada = rst.getObject("id_notaentrada") == null ? -1 : rst.getInt("id_notaentrada");
        oNotaSaida.senha = rst.getString("senha");
        oNotaSaida.volume = rst.getLong("volume");
        oNotaSaida.pesoLiquido = rst.getDouble("pesoliquido");
        //oNotaSaida.pesoBruto = rst.getDouble("pesobruto");

        //carrega vencimentos
        sql = new StringBuilder();
        sql.append("SELECT *");
        sql.append(" FROM notasaidavencimento AS nsv");
        sql.append(" WHERE nsv.id_notasaida = " + i_id);
        sql.append(" ORDER BY datavencimento");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaSaidaVencimentoVO oVencimento = new NotaSaidaVencimentoVO();
            oVencimento.dataVencimento = rst.getDate("datavencimento") == null ? "" : Util.formatDataGUI(rst.getDate("datavencimento"));
            oVencimento.valor = rst.getDouble("valor");

            oNotaSaida.vVencimento.add(oVencimento);
        }

        //carrega itens
        sql = new StringBuilder();
        sql.append("SELECT nsi.*, produto.descricaocompleta AS produto, produto.id_tipoembalagem, aliquota.descricao AS aliquota, produto.id_tipopiscofins, produto.id_tipopiscofinscredito,");
        sql.append(" tipoembalagem.descricao AS tipoembalagem, produto.ncm1, produto.ncm2, produto.ncm3, nsi.cfop, produto.pesoliquido,");
        sql.append(" COALESCE((SELECT codigobarras FROM produtoautomacao WHERE id_produto = produto.id AND LENGTH(codigobarras::varchar) <= 13 LIMIT 1), 0) AS codigobarras,");
        sql.append(" produto.id_tipoorigemmercadoria, produto.impostomedionacional, produto.impostomedioimportado");
        sql.append(" FROM notasaidaitem AS nsi");
        sql.append(" INNER JOIN produto ON produto.id = nsi.id_produto");
        sql.append(" INNER JOIN aliquota ON aliquota.id = nsi.id_aliquota");
        sql.append(" INNER JOIN tipoembalagem ON tipoembalagem.id = produto.id_tipoembalagem");
        sql.append(" WHERE nsi.id_notasaida = " + i_id);
        sql.append(" ORDER BY nsi.id");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaSaidaItemVO oItem = new NotaSaidaItemVO();
            oItem.id = rst.getLong("id");
            oItem.idProduto = rst.getInt("id_produto");
            oItem.produto = rst.getString("produto");
            oItem.quantidade = rst.getDouble("quantidade");
            oItem.qtdEmbalagem = rst.getInt("qtdembalagem");
            oItem.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
            oItem.tipoEmbalagem = rst.getString("tipoembalagem");
            oItem.valor = rst.getDouble("valor");
            oItem.valorTotal = rst.getDouble("valortotal");
            oItem.valorTotalIpi = rst.getDouble("valoripi");
            oItem.valorBaseIpi = rst.getDouble("valorbaseipi");
            oItem.valorIpi = (oItem.quantidade * oItem.qtdEmbalagem) == 0 ? 0 : Util.round(oItem.valorTotalIpi / (oItem.quantidade * oItem.qtdEmbalagem), 4);
            oItem.idAliquota = rst.getInt("id_aliquota");
            oItem.aliquota = rst.getString("aliquota");
            oItem.valorBaseCalculo = rst.getDouble("valorbasecalculo");
            oItem.valorIcms = rst.getDouble("valoricms");
            oItem.valorBaseSubstituicao = rst.getDouble("valorbasesubstituicao");
            oItem.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
            oItem.valorPisCofins = rst.getDouble("valorpiscofins");
            oItem.codigoBarras = rst.getLong("codigobarras");
            oItem.ncm1 = rst.getObject("ncm1") == null ? -1 : rst.getInt("ncm1");
            oItem.ncm2 = rst.getObject("ncm2") == null ? -1 : rst.getInt("ncm2");
            oItem.ncm3 = rst.getObject("ncm3") == null ? -1 : rst.getInt("ncm3");
            oItem.cfop = rst.getString("cfop") == null ? "" : rst.getString("cfop");
            oItem.tipoIva = rst.getInt("tipoiva");
            oItem.idAliquotaPautaFiscal = rst.getObject("id_aliquotapautafiscal") == null ? -1 : rst.getInt("id_aliquotapautafiscal");
            oItem.pesoLiquido = rst.getDouble("pesoliquido");
            oItem.valorDesconto = rst.getDouble("valordesconto");
            oItem.idTipoPisCofinsDebito = rst.getInt("id_tipopiscofins");
            oItem.idTipoPisCofinsCredito = rst.getInt("id_tipopiscofinscredito");
            oItem.valorIsento = rst.getDouble("valorisento");
            oItem.valorOutras = rst.getDouble("valoroutras");
            oItem.situacaoTributaria = rst.getInt("situacaotributaria");
            oItem.idAliquotaDispensado = rst.getObject("id_aliquotadispensado") == null ? -1 : rst.getInt("id_aliquotadispensado");
            oItem.valorIcmsDispensado = rst.getDouble("valoricmsdispensado");
            oItem.tipoNaturezaReceita = rst.getObject("tiponaturezareceita") == null ? -1 : rst.getInt("tiponaturezareceita");
            oItem.idTipoOrigemMercadoria = rst.getInt("id_tipoorigemmercadoria");
            oItem.localDesembaraco = rst.getString("localdesembaraco");
            oItem.dataDesembaraco = rst.getDate("datadesembaraco") == null ? "" : Util.formatDataGUI(rst.getDate("datadesembaraco"));
            oItem.idEstadoDesembaraco = rst.getObject("id_estadodesembaraco") == null ? -1 : rst.getInt("id_estadodesembaraco");
            oItem.numeroAdicao = rst.getInt("numeroadicao");
            oItem.idTipoSaida = rst.getInt("id_tiposaida");

            switch (rst.getInt("id_tipoorigemmercadoria")) {
                case 0:
                case 3:
                case 4:
                case 5:
                    oItem.impostoMedio = rst.getDouble("impostomedionacional");
                    break;
                case 1:
                case 2:
                case 6:
                case 7:
                    oItem.impostoMedio = rst.getDouble("impostomedioimportado");
                    break;
            }

            oNotaSaida.vItem.add(oItem);
        }

        //carrega reposicao
        sql = new StringBuilder();
        sql.append("SELECT * FROM notasaidareposicao WHERE id_notasaida = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaSaidaReposicaoVO oReposicao = new NotaSaidaReposicaoVO();
            oReposicao.idReposicao = rst.getInt("id_reposicao");

            oNotaSaida.vReposicao.add(oReposicao);
        }

        //carrega cupom
        sql = new StringBuilder();
        sql.append("SELECT * FROM notasaidacupom WHERE id_notasaida = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaSaidaCupomVO oCupom = new NotaSaidaCupomVO();
            oCupom.numeroCupom = rst.getInt("numerocupom");
            oCupom.data = Util.formatDataGUI(rst.getDate("data"));
            oCupom.ecf = rst.getInt("ecf");

            oNotaSaida.vCupom.add(oCupom);
        }

        //carrega nota entrada
        sql = new StringBuilder();
        sql.append("SELECT nsne.*, ne.numeronota, ne.dataentrada");
        sql.append(" FROM notasaidanotaentrada AS nsne");
        sql.append(" INNER JOIN notaentrada AS ne ON ne.id = nsne.id_notaentrada");
        sql.append(" WHERE nsne.id_notasaida = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaSaidaNotaEntradaVO oNotaEntrada = new NotaSaidaNotaEntradaVO();
            oNotaEntrada.idNotaEntrada = rst.getLong("id_notaentrada");
            oNotaEntrada.numeroNota = rst.getInt("numeronota");
            oNotaEntrada.dataEntrada = Util.formatDataGUI(rst.getDate("dataentrada"));

            oNotaSaida.vNotaEntrada.add(oNotaEntrada);
        }

        //carrega troca cupom
        sql = new StringBuilder();
        sql.append("SELECT * FROM notasaidatrocacupom WHERE id_notasaida = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaSaidaTrocaCupomVO oTrocaCupom = new NotaSaidaTrocaCupomVO();
            oTrocaCupom.idTrocaCupom = rst.getInt("id_trocacupom");

            oNotaSaida.vTrocaCupom.add(oTrocaCupom);
        }

        //carrega devolucao cupom
        sql = new StringBuilder();
        sql.append("SELECT * FROM notasaidadevolucaocupom WHERE id_notasaida = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaSaidaDevolucaoCupomVO oDevolucaoCupom = new NotaSaidaDevolucaoCupomVO();
            oDevolucaoCupom.idDevolucaoCupom = rst.getInt("id_devolucaocupom");

            oNotaSaida.vDevolucaoCupom.add(oDevolucaoCupom);
        }

        stm.close();

        return oNotaSaida;
    }

    public void excluir(NotaSaidaVO i_notaSaida) throws Exception {
        estornar(i_notaSaida, true, true);

        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            if (Database.tabelaExiste("atacado.venda")) {
                stm.execute("UPDATE atacado.venda SET id_notasaida = NULL WHERE id_notasaida = " + i_notaSaida.id);
            }

            if (Database.tabelaExiste("limerrede.venda")) {
                stm.execute("UPDATE limerrede.venda SET id_notasaida = NULL WHERE id_notasaida = " + i_notaSaida.id);
            }

            if (Database.tabelaExiste("centralrede.venda")) {
                stm.execute("UPDATE centralrede.venda SET id_notasaida = NULL WHERE id_notasaida = " + i_notaSaida.id);
            }

            if (Database.tabelaExiste("centralcompra.notasaida")) {
                stm.execute("UPDATE centralcompra.notasaida SET id_notasaida = NULL WHERE id_notasaida = " + i_notaSaida.id);
            }

            stm.execute("DELETE FROM notasaidadevolucaocupom WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidatrocacupom WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidanotaentrada WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidacupom WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidareposicao WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidaitemdesmembramento WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidaitem WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidanfe WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidavencimento WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaida WHERE id = " + i_notaSaida.id);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void estornar(NotaSaidaVO i_notaSaida, boolean i_situacao, boolean i_escrita) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //verifica status
            rst = stm.executeQuery("SELECT id_situacaonotasaida FROM notasaida WHERE id = " + i_notaSaida.id + " FOR UPDATE");

            if (rst.next() && rst.getInt("id_situacaonotasaida") == SituacaoNotaSaida.NAO_FINALIZADO.getId()) {
                throw new VRException("Esta nota já foi estornada!");
            }

            //executa processos
            TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_notaSaida.idTipoSaida);

            if (oTipoSaida.baixaEstoque || oTipoSaida.entraEstoque) {
                estornarEstoque(i_notaSaida);
            }

            estornarContrato(i_notaSaida);

            estornarDevolucao(i_notaSaida);

            estornarVendaPrazo(i_notaSaida);

            estornarPedido(i_notaSaida);

            estornarTrocaCupom(i_notaSaida);

            estornarDevolucaoCupom(i_notaSaida);

            if (i_escrita) {
                estornarEscrita(i_notaSaida);
            }

            estornarTransferencia(i_notaSaida);

            //atualiza status
            if (i_situacao) {
                sql = new StringBuilder();
                sql.append("UPDATE notasaida SET");
                sql.append(" id_situacaonotasaida = " + SituacaoNotaSaida.NAO_FINALIZADO.getId());
                sql.append(" WHERE id = " + i_notaSaida.id);

                stm.execute(sql.toString());

                new LogTransacaoDAO().gerar(Formulario.NOTAFISCAL_SAIDA, TipoTransacao.ESTORNO, i_notaSaida.numeroNota, "", i_notaSaida.id);

                i_notaSaida.idSituacaoNotaSaida = SituacaoNotaSaida.NAO_FINALIZADO.getId();
            }

            //commit
            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private void estornarEstoque(NotaSaidaVO i_notaSaida) throws Exception {
        TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_notaSaida.idTipoSaida);

        boolean baixaReceita = new ParametroDAO().get(105).getBoolean();

        boolean baixaPerda = (new ParametroDAO().get(193).getInt() == TipoBaixaPerda.SAIDA.getId());

        for (NotaSaidaItemVO oItem : i_notaSaida.vItem) {
            //atualiza estoque
            if (oItem.vDesmembramento.isEmpty()) {
                double quantidade = oItem.quantidade * oItem.qtdEmbalagem;

                if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.LOJA.getId()) {
                    AcertoEstoqueVO oEstoque = new AcertoEstoqueVO();
                    oEstoque.idProduto = oItem.idProduto;
                    oEstoque.idLoja = i_notaSaida.idLoja;
                    oEstoque.data = i_notaSaida.dataSaida;

                    if (oTipoSaida.entraEstoque) {
                        oEstoque.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();

                    } else if (oTipoSaida.baixaEstoque) {
                        oEstoque.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                    }

                    if (oTipoSaida.geraDevolucao) {
                        oEstoque.idTipoMovimentacao = TipoMovimentacao.DEVOLUCAO.getId();

                    } else {
                        oEstoque.idTipoMovimentacao = TipoMovimentacao.SAIDA.getId();
                    }

                    oEstoque.quantidade = quantidade;
                    oEstoque.baixaReceita = baixaReceita;
                    oEstoque.baixaAssociado = true;
                    oEstoque.baixaPerda = baixaPerda;
                    oEstoque.observacao = "NF " + i_notaSaida.numeroNota + ", TIPO: " + oTipoSaida.descricao;

                    new EstoqueDAO().alterar(oEstoque);

                } else if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.TROCA.getId()) {
                    AcertoTrocaVO oTroca = new AcertoTrocaVO();
                    oTroca.idProduto = oItem.idProduto;
                    oTroca.idLoja = i_notaSaida.idLoja;
                    oTroca.data = i_notaSaida.dataSaida;

                    if (oTipoSaida.entraEstoque) {
                        oTroca.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();

                    } else if (oTipoSaida.baixaEstoque) {
                        oTroca.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                    }

                    oTroca.quantidade = quantidade;

                    new EstoqueDAO().alterarTroca(oTroca);

                } else if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.CESTA_BASICA.getId()) {
                    AcertoCestaBasicaVO oCestaBasica = new AcertoCestaBasicaVO();
                    oCestaBasica.idProduto = oItem.idProduto;
                    oCestaBasica.idLoja = i_notaSaida.idLoja;
                    oCestaBasica.data = i_notaSaida.dataSaida;

                    if (oTipoSaida.entraEstoque) {
                        oCestaBasica.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();

                    } else if (oTipoSaida.baixaEstoque) {
                        oCestaBasica.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                    }

                    oCestaBasica.quantidade = quantidade;

                    new EstoqueDAO().alterarCestaBasica(oCestaBasica);
                }
            }
        }

        for (NotaSaidaItemVO oItem : i_notaSaida.vDesmembramento) {
            //atualiza estoque
            double quantidade = oItem.quantidade * oItem.qtdEmbalagem;

            if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.LOJA.getId()) {
                AcertoEstoqueVO oEstoque = new AcertoEstoqueVO();
                oEstoque.idProduto = oItem.idProduto;
                oEstoque.idLoja = i_notaSaida.idLoja;
                oEstoque.data = i_notaSaida.dataSaida;

                if (oTipoSaida.entraEstoque) {
                    oEstoque.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();

                } else if (oTipoSaida.baixaEstoque) {
                    oEstoque.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                }

                if (oTipoSaida.geraDevolucao) {
                    oEstoque.idTipoMovimentacao = TipoMovimentacao.DEVOLUCAO.getId();

                } else {
                    oEstoque.idTipoMovimentacao = TipoMovimentacao.SAIDA.getId();
                }

                oEstoque.quantidade = quantidade;
                oEstoque.baixaReceita = baixaReceita;
                oEstoque.baixaAssociado = true;
                oEstoque.baixaPerda = baixaPerda;
                oEstoque.observacao = "NF " + i_notaSaida.numeroNota + ", TIPO: " + oTipoSaida.descricao;

                new EstoqueDAO().alterar(oEstoque);

            } else if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.TROCA.getId()) {
                AcertoTrocaVO oTroca = new AcertoTrocaVO();
                oTroca.idProduto = oItem.idProduto;
                oTroca.idLoja = i_notaSaida.idLoja;

                if (oTipoSaida.entraEstoque) {
                    oTroca.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();

                } else if (oTipoSaida.baixaEstoque) {
                    oTroca.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                }

                oTroca.quantidade = quantidade;

                new EstoqueDAO().alterarTroca(oTroca);

            } else if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.CESTA_BASICA.getId()) {
                AcertoCestaBasicaVO oCestaBasica = new AcertoCestaBasicaVO();
                oCestaBasica.idProduto = oItem.idProduto;
                oCestaBasica.idLoja = i_notaSaida.idLoja;

                if (oTipoSaida.entraEstoque) {
                    oCestaBasica.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();

                } else if (oTipoSaida.baixaEstoque) {
                    oCestaBasica.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                }

                oCestaBasica.quantidade = quantidade;

                new EstoqueDAO().alterarCestaBasica(oCestaBasica);
            }
        }
    }

    private void estornarContrato(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id, id_loja");
        sql.append(" FROM contratolancamento");
        sql.append(" WHERE id_notasaida = " + i_notaSaida.id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            new ContratoLancamentoDAO().excluir(rst.getLong("id"));
        }

        stm.close();
    }

    private void estornarDevolucao(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id, id_loja, id_situacaoreceberdevolucao");
        sql.append(" FROM receberdevolucao");
        sql.append(" WHERE id_notasaida = " + i_notaSaida.id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            if (rst.getInt("id_situacaoreceberdevolucao") == SituacaoReceberDevolucao.BAIXADO.getId()) {
                throw new VRException("Esta nota possui uma devolução baixada e não pode ser estornada!");
            }

            new ReceberDevolucaoDAO().excluir(rst.getLong("id"));
        }
    }

    private void estornarVendaPrazo(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id, id_loja");
        sql.append(" FROM recebervendaprazo");
        sql.append(" WHERE id_notasaida = " + i_notaSaida.id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            new ReceberVendaPrazoDAO().excluir(rst.getLong("id"));
        }
    }

    private void estornarPedido(NotaSaidaVO i_notaSaida) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        for (NotaSaidaReposicaoVO oPedido : i_notaSaida.vReposicao) {
            sql = new StringBuilder();
            sql.append("UPDATE reposicao SET");
            sql.append(" id_situacaoreposicao = " + SituacaoReposicao.PREPARANDO.getId() + ",");
            sql.append(" id_notasaida = NULL");
            sql.append(" WHERE id = " + oPedido.idReposicao);

            stm.execute(sql.toString());
        }

        stm.close();
    }

    private void estornarTrocaCupom(NotaSaidaVO i_notaSaida) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        for (NotaSaidaTrocaCupomVO oTrocaCupom : i_notaSaida.vTrocaCupom) {
            sql = new StringBuilder();
            sql.append("UPDATE pdv.trocacupom SET emitido = FALSE WHERE id = " + oTrocaCupom.idTrocaCupom);

            stm.execute(sql.toString());
        }

        stm.close();
    }

    private void estornarDevolucaoCupom(NotaSaidaVO i_notaSaida) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        for (NotaSaidaDevolucaoCupomVO oDevolucaoCupom : i_notaSaida.vDevolucaoCupom) {
            sql = new StringBuilder();
            sql.append("UPDATE pdv.devolucaocupom SET emitido = FALSE WHERE id = " + oDevolucaoCupom.idDevolucaoCupom);

            stm.execute(sql.toString());
        }

        stm.close();
    }

    private void estornarEscrita(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM escrita WHERE id_notasaida = " + i_notaSaida.id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            return;
        }

        new EscritaDAO().excluir(rst.getLong("id"));

        stm.close();
    }

    private void estornarTransferencia(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id, id_situacaonotaentrada FROM notaentrada WHERE id_notasaida = " + i_notaSaida.id);

        if (rst.next()) {
            if (rst.getInt("id_situacaonotaentrada") == SituacaoNotaEntrada.FINALIZADO.getId()) {
                throw new VRException("Esta nota possui uma entrada finalizada e não pode ser estornada!");
            }

            long idNotaEntrada = rst.getLong("id");

            stm.execute("UPDATE notaentrada SET id_notasaida = NULL WHERE id_notasaida = " + i_notaSaida.id);

            new NotaEntradaDAO().excluir(idNotaEntrada);
        }

        stm.close();
    }

    public void finalizar(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //verifica status
            rst = stm.executeQuery("SELECT id_situacaonotasaida FROM notasaida WHERE id = " + i_notaSaida.id + " FOR UPDATE");

            if (rst.next() && rst.getInt("id_situacaonotasaida") == SituacaoNotaSaida.FINALIZADO.getId()) {
                throw new VRException("Esta nota já está finalizada!");
            }

            //salva nota
            if (i_notaSaida.numeroNota == 0) {
                i_notaSaida.numeroNota = new CodigoInternoDAO().get(Formulario.NOTAFISCAL_SAIDA);
            }

            //executa processos
            TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_notaSaida.idTipoSaida);

            finalizarCfop(i_notaSaida);

            if (oTipoSaida.atualizaEscrita) {
                if (Parametros.get().getBool(true, "IMPORT_NFE", "VERIFICAR_FECHAMENTO_ESCRITA")) {
                    new EscritaFechamentoDAO().verificar(i_notaSaida.dataSaida);
                }

                finalizarEscrita(i_notaSaida);
            }

            if (Parametros.get().getBool(true, "IMPORT_NFE", "PROCESSAR_FINALIZACOES")) {
                if (oTipoSaida.baixaEstoque || oTipoSaida.entraEstoque) {
                    finalizarEstoque(i_notaSaida);
                }

                if (i_notaSaida.valorTotal > 0) {
                    if (oTipoSaida.geraDevolucao) {
                        finalizarDevolucao(i_notaSaida);
                    }

                    if (oTipoSaida.geraReceber) {
                        finalizarVendaPrazo(i_notaSaida);
                    }

                    if (oTipoSaida.geraContrato) {
                        finalizarContrato(i_notaSaida);
                    }
                }

                if (oTipoSaida.consultaPedido) {
                    finalizarPedido(i_notaSaida);
                }

                finalizarTrocaCupom(i_notaSaida);

                finalizarDevolucaoCupom(i_notaSaida);

                if (oTipoSaida.transferencia && new LojaDAO().isFornecedor(i_notaSaida.idFornecedorDestinatario)) {
                    finalizarSenha(i_notaSaida);
                    finalizarTransferencia(i_notaSaida);
                }
            }

            //atualiza status
            sql = new StringBuilder();
            sql.append("UPDATE notasaida SET");
            sql.append(" id_situacaonotasaida = " + SituacaoNotaSaida.FINALIZADO.getId());
            sql.append(" WHERE id = " + i_notaSaida.id);

            stm.execute(sql.toString());

            new LogTransacaoDAO().gerar(Formulario.NOTAFISCAL_SAIDA, TipoTransacao.FINALIZACAO, i_notaSaida.numeroNota, "", i_notaSaida.id);

            i_notaSaida.idSituacaoNotaSaida = SituacaoNotaSaida.FINALIZADO.getId();

            //commit
            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private void finalizarTrocaCupom(NotaSaidaVO i_notaSaida) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        for (NotaSaidaTrocaCupomVO oTrocaCupom : i_notaSaida.vTrocaCupom) {
            sql = new StringBuilder();
            sql.append("UPDATE pdv.trocacupom SET emitido = TRUE WHERE id = " + oTrocaCupom.idTrocaCupom);

            stm.execute(sql.toString());
        }

        stm.close();
    }

    public void finalizarTransferencia(NotaSaidaVO i_notaSaida) throws Exception {
        TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_notaSaida.idTipoSaida);

        if (oTipoSaida.idTipoEntrada == -1) {
            throw new VRException("Tipo entrada não configurado para este tipo de nota!");
        }

        NotaFiscalFornecedorVO oFornecedor = new NotaFiscalFornecedorDAO().carregar(i_notaSaida.idFornecedorDestinatario);

        NotaEntradaVO oNotaEntrada = new NotaEntradaVO();
        oNotaEntrada.idLoja = new LojaDAO().getId(i_notaSaida.idFornecedorDestinatario);
        oNotaEntrada.idNotaSaida = i_notaSaida.id;
        oNotaEntrada.idFornecedor = Global.idFornecedor;
        oNotaEntrada.numeroNota = i_notaSaida.numeroNota;
        oNotaEntrada.idTipoEntrada = oTipoSaida.idTipoEntrada;
        oNotaEntrada.dataEmissao = Util.formatData(i_notaSaida.dataHoraEmissao, "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy");
        oNotaEntrada.serie = oFornecedor.serieNf;
        oNotaEntrada.modelo = oFornecedor.modeloNf;
        oNotaEntrada.dataEntrada = i_notaSaida.dataSaida;
        oNotaEntrada.idSituacaoNotaEntrada = SituacaoNotaEntrada.NAO_FINALIZADO.getId();
        oNotaEntrada.chaveNfe = i_notaSaida.chaveNfe;
        oNotaEntrada.idTipoFreteNotaFiscal = i_notaSaida.idTipoFreteNotaFiscal;
        oNotaEntrada.valorDesconto = i_notaSaida.valorDesconto;
        oNotaEntrada.valorMercadoria = i_notaSaida.valorProduto;
        oNotaEntrada.valorIpi = i_notaSaida.valorIpi;
        oNotaEntrada.valorTotal = i_notaSaida.valorTotal;
        oNotaEntrada.valorBaseCalculo = i_notaSaida.valorBaseCalculo;
        oNotaEntrada.valorIcms = i_notaSaida.valorIcms;
        oNotaEntrada.valorIcmsSubstituicao = i_notaSaida.valorIcmsSubstituicao;
        oNotaEntrada.valorFrete = i_notaSaida.valorFrete;
        oNotaEntrada.valorOutrasDespesas = i_notaSaida.valorOutrasDespesas;
        oNotaEntrada.valorBaseSubstituicao = i_notaSaida.valorBaseSubstituicao;
        oNotaEntrada.observacao = i_notaSaida.informacaoComplementar;
        oNotaEntrada.idUsuario = Global.idUsuario;
        oNotaEntrada.idTipoPagamento = oFornecedor.idTipoPagamento;
        oNotaEntrada.idEstado = i_notaSaida.idEstadoDestinatario;

        boolean foraEstado = false;

        TipoEntradaVO oTipoEntrada = new TipoEntradaDAO().carregar(oTipoSaida.idTipoEntrada);

        if (oTipoEntrada.foraEstado) {
            if (Global.idEstado != oFornecedor.idEstado) {
                foraEstado = true;
            }
        }

        for (NotaSaidaItemVO oItemSaida : i_notaSaida.vItem) {
            NotaEntradaItemVO oItemEntrada = new NotaEntradaDAO().carregarProduto(oItemSaida.idProduto, oNotaEntrada.idFornecedor, oNotaEntrada.idEstado, oNotaEntrada.idLoja, oNotaEntrada.idTipoEntrada);

            oItemEntrada.quantidade = oItemSaida.quantidade;
            oItemEntrada.qtdEmbalagem = oItemSaida.qtdEmbalagem;
            oItemEntrada.valor = oItemSaida.valor;
            oItemEntrada.valorEmbalagem = (oItemSaida.valorTotal / oItemSaida.quantidade);
            oItemEntrada.valorTotal = oItemSaida.valorTotal;
            oItemEntrada.valorIpi = oItemSaida.valorTotalIpi;
            oItemEntrada.idAliquota = oItemSaida.idAliquota;
            oItemEntrada.valorBaseCalculo = oItemSaida.valorBaseCalculo;
            oItemEntrada.valorIcms = oItemSaida.valorIcms;
            oItemEntrada.valorBaseSubstituicao = oItemSaida.valorBaseSubstituicao;
            oItemEntrada.valorIcmsSubstituicao = oItemSaida.valorIcmsSubstituicao;
            oItemEntrada.valorPisCofins = Util.round((oItemSaida.valorBaseCalculo + oItemSaida.valorIsento + oItemSaida.valorOutras) * (new TipoPisCofinsDAO().getPisCofins(oItemEntrada.idTipoPisCofins)) / 100, 2);
            oItemEntrada.valorIsento = oItemSaida.valorIsento;
            oItemEntrada.valorOutras = oItemSaida.valorOutras;
            oItemEntrada.contabilizaValor = true;

            NotaFiscalAliquotaVO oAliquota = new NotaFiscalAliquotaDAO().carregar(oItemEntrada.idAliquota);

            oItemEntrada.situacaoTributaria = oAliquota.situacaoTributaria;

            boolean substituido = false;

            if (oTipoEntrada.substituicao && new AliquotaDAO().isSubstituido(oAliquota.situacaoTributaria)) {
                substituido = true;
            }

            try {
                oItemEntrada.cfop = new CfopDAO().gerarEntrada(oTipoEntrada.id, foraEstado, substituido);

            } catch (Exception ex) {
            }

            oNotaEntrada.vItem.add(oItemEntrada);

            for (NotaEntradaItemDesmembramentoVO oDesmembramento : oItemEntrada.vDesmembramento) {
                oDesmembramento.quantidade = Util.round(oItemEntrada.quantidade * oItemEntrada.qtdEmbalagem * (oDesmembramento.percentualEstoque / 100), 3);
                oDesmembramento.qtdEmbalagem = 1;
                oDesmembramento.valor = Util.round((oItemEntrada.valor * (1 + (oDesmembramento.percentualDesossa / 100))) * (oDesmembramento.percentualCusto / 100), 3);
                oDesmembramento.valorTotal = Util.round(oDesmembramento.quantidade * oDesmembramento.qtdEmbalagem * oDesmembramento.valor, 2);
                oDesmembramento.valorIpi = Util.round((oItemEntrada.valorIpi * (1 + (oDesmembramento.percentualDesossa / 100))) * (oDesmembramento.percentualCusto / 100), 2);
                oDesmembramento.idAliquota = oItemEntrada.idAliquota;
                oDesmembramento.contabilizaValor = true;
            }
        }

        new NotaEntradaDAO().salvar(oNotaEntrada);
    }

    public void finalizarContrato(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        //verifica contrato
        sql = new StringBuilder();
        sql.append("SELECT ct.id");
        sql.append(" FROM contrato AS ct");
        sql.append(" INNER JOIN contratofornecedor AS ctf ON ctf.id_contrato = ct.id");
        sql.append(" WHERE ctf.id_fornecedor = " + i_notaSaida.idFornecedorDestinatario);
        sql.append(" AND ct.id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            return;
        }

        ContratoVO oContrato = new ContratoDAO().carregar(rst.getInt("id"));

        List<ContratoFornecedorExcecaoVO> vExcessao = new ArrayList();

        for (ContratoFornecedorVO oForcedor : oContrato.vFornecedor) {
            if (oForcedor.idFornecedor == i_notaSaida.idFornecedorDestinatario) {
                vExcessao = oForcedor.vExcecao;
            }
        }

        //calcula valor total
        List<ContratoLancamentoVO> vContratoLancamento = new ArrayList();

        for (NotaSaidaItemVO oItem : i_notaSaida.vItem) {
            double valorIcms = 0;
            double valorPisCofins = 0;
            double valorIpi = 0;

            if (oContrato.abatimentoIcms) {
                valorIcms = oItem.valorIcms;
            }

            if (oContrato.abatimentoPisCofins) {
                valorPisCofins = oItem.valorPisCofins;
            }

            if (oContrato.abatimentoIpi) {
                valorIpi = oItem.valorIpi;
            }

            double valorTotal = oItem.valorTotal - valorPisCofins - valorIcms - valorIpi;

            if (valorTotal > 0) {
                boolean achou = false;

                for (ContratoFornecedorExcecaoVO oExcessao : vExcessao) {
                    if (oExcessao.idProduto == oItem.idProduto) {
                        for (ContratoFornecedorExcecaoAcordoVO oAcordo : oExcessao.vAcordo) {
                            ContratoLancamentoVO oLancamento = new ContratoLancamentoVO();
                            oLancamento.idTipoAcordo = oAcordo.idTipoAcordo;
                            oLancamento.valorAcordo = Util.round(valorTotal * (oAcordo.percentual / 100), 2);
                            oLancamento.percentual = oAcordo.percentual;
                            oLancamento.valorBaseCalculo = valorTotal;

                            new ContratoLancamentoDAO().adicionar(oLancamento, vContratoLancamento);
                        }

                        achou = true;
                        break;
                    }
                }

                if (!achou) {
                    for (ContratoAcordoVO oAcordo : oContrato.vAcordo) {
                        boolean excecao = false;

                        for (ContratoAcordoExcecaoLojaVO oExcecao : oAcordo.vExcecao) {
                            if (oExcecao.idLoja == i_notaSaida.idLoja) {
                                excecao = oExcecao.selecionado;
                                break;
                            }
                        }

                        if (!excecao) {
                            ContratoLancamentoVO oLancamento = new ContratoLancamentoVO();
                            oLancamento.idTipoAcordo = oAcordo.idTipoAcordo;
                            oLancamento.valorAcordo = Util.round(valorTotal * (oAcordo.percentual / 100), 2);
                            oLancamento.percentual = oAcordo.percentual;
                            oLancamento.valorBaseCalculo = valorTotal;

                            new ContratoLancamentoDAO().adicionar(oLancamento, vContratoLancamento);
                        }
                    }
                }
            }
        }

        //salva contrato
        for (ContratoLancamentoVO oLancamento : vContratoLancamento) {
            oLancamento.idContrato = oContrato.id;
            oLancamento.dataLancamento = i_notaSaida.dataSaida;
            oLancamento.idNotaDespesa = -1;
            oLancamento.idNotaEntrada = -1;
            oLancamento.idNotaSaida = i_notaSaida.id;
            oLancamento.idTipoContrato = TipoContrato.DEVOLUCAO_FORNECEDOR.getId();
            oLancamento.idTipoRecebimento = oContrato.idTipoRecebimento;

            new ContratoLancamentoDAO().salvar(oLancamento);
        }

        stm.close();
    }

    private void finalizarDevolucao(NotaSaidaVO i_notaSaida) throws Exception {
        int numeroParcela = 0;

        for (NotaSaidaVencimentoVO oVencimento : i_notaSaida.vVencimento) {
            numeroParcela++;

            ReceberDevolucaoVO oDevolucao = new ReceberDevolucaoVO();
            oDevolucao.idLoja = i_notaSaida.idLoja;
            oDevolucao.idNotaSaida = i_notaSaida.id;
            oDevolucao.numeroNota = i_notaSaida.numeroNota;
            oDevolucao.idFornecedor = i_notaSaida.idFornecedorDestinatario;
            oDevolucao.dataEmissao = i_notaSaida.dataHoraEmissao.substring(0, 10);
            oDevolucao.dataVencimento = oVencimento.dataVencimento;
            oDevolucao.valor = oVencimento.valor;
            oDevolucao.idTipoDevolucao = i_notaSaida.idTipoDevolucao;
            oDevolucao.idSituacaoReceberDevolucao = SituacaoReceberDevolucao.ABERTO.getId();
            oDevolucao.numeroParcela = numeroParcela;

            new ReceberDevolucaoDAO().salvar(oDevolucao);
        }
    }

    private void finalizarPedido(NotaSaidaVO i_notaSaida) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        for (NotaSaidaReposicaoVO oPedido : i_notaSaida.vReposicao) {
            sql = new StringBuilder();
            sql.append("UPDATE reposicao SET");
            sql.append(" id_situacaoreposicao = " + SituacaoReposicao.FINALIZADO.getId() + ",");
            sql.append(" id_notasaida = " + i_notaSaida.id);
            sql.append(" WHERE id = " + oPedido.idReposicao);

            stm.execute(sql.toString());
        }

        stm.close();
    }

    private void finalizarSenha(NotaSaidaVO i_notaSaida) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        i_notaSaida.senha = calcularSenhaTransferencia(i_notaSaida.numeroNota, i_notaSaida.dataHoraEmissao, i_notaSaida.idLoja);

        sql = new StringBuilder();
        sql.append("UPDATE notasaida SET");
        sql.append(" senha = '" + i_notaSaida.senha + "'");
        sql.append(" WHERE id = " + i_notaSaida.id);

        stm.execute(sql.toString());

        stm.close();
    }

    private void finalizarVendaPrazo(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id_tipoorgaopublico FROM clienteeventual WHERE id = " + i_notaSaida.idClienteEventualDestinatario);

        if (!rst.next()) {
            throw new VRException("Cliente eventual não encontrado!");
        }

        double valorPis = 0;
        double valorCofins = 0;
        double valorCsll = 0;
        double valorImpostoRenda = 0;

        int idTipoOrgaoPublico = rst.getInt("id_tipoorgaopublico");

        if (idTipoOrgaoPublico == TipoOrgaoPublico.FEDERAL.getId()) {
            rst = stm.executeQuery("SELECT id, percentual FROM tipoimpostodespesa");

            while (rst.next()) {
                switch (rst.getInt("id")) {
                    case 1:
                        valorPis = rst.getDouble("percentual");
                        break;
                    case 2:
                        valorCofins = rst.getDouble("percentual");
                        break;
                    case 3:
                        valorCsll = rst.getDouble("percentual");
                        break;
                    case 4:
                        valorImpostoRenda = rst.getDouble("percentual");
                        break;
                }
            }
        }

        int numeroParcela = 0;

        for (NotaSaidaVencimentoVO oVencimento : i_notaSaida.vVencimento) {
            numeroParcela++;

            ReceberVendaPrazoVO oVenda = new ReceberVendaPrazoVO();
            oVenda.idLoja = i_notaSaida.idLoja;
            oVenda.idClienteEventual = i_notaSaida.idClienteEventualDestinatario;
            oVenda.idSituacaoReceberVendaPrazo = SituacaoReceberVendaPrazo.ABERTO.getId();
            oVenda.idTipoSaida = i_notaSaida.idTipoSaida;
            oVenda.numeroNota = i_notaSaida.numeroNota;
            oVenda.dataEmissao = i_notaSaida.dataHoraEmissao.substring(0, 10);
            oVenda.dataVencimento = oVencimento.dataVencimento;
            oVenda.pis = Util.round(oVencimento.valor * (valorPis / 100), 2);
            oVenda.cofins = Util.round(oVencimento.valor * (valorCofins / 100), 2);
            oVenda.csll = Util.round(oVencimento.valor * (valorCsll / 100), 2);
            oVenda.impostoRenda = Util.round(oVencimento.valor * (valorImpostoRenda / 100), 2);
            oVenda.valor = oVencimento.valor;
            oVenda.valorLiquido = oVencimento.valor - oVenda.pis - oVenda.cofins - oVenda.csll - oVenda.impostoRenda;
            oVenda.idNotaSaida = i_notaSaida.id;
            oVenda.numeroParcela = numeroParcela;

            new ReceberVendaPrazoDAO().salvar(oVenda);
        }
    }

    private void finalizarDevolucaoCupom(NotaSaidaVO i_notaSaida) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        for (NotaSaidaDevolucaoCupomVO oDevolucaoCupom : i_notaSaida.vDevolucaoCupom) {
            sql = new StringBuilder();
            sql.append("UPDATE pdv.devolucaocupom SET emitido = TRUE WHERE id = " + oDevolucaoCupom.idDevolucaoCupom);

            stm.execute(sql.toString());
        }

        stm.close();
    }

    private void finalizarCfop(NotaSaidaVO i_notaSaida) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_notaSaida.idTipoSaida);

        for (NotaSaidaItemVO oItem : i_notaSaida.vItem) {
            if (oItem.cfop.isEmpty()) {
                boolean foraEstado = false;
                boolean substituido = false;

                if (oTipoSaida.foraEstado) {
                    if (Global.idEstado != i_notaSaida.idEstadoDestinatario) {
                        foraEstado = true;
                    }
                }

                if (oTipoSaida.substituicao) {
                    NotaFiscalAliquotaVO oAliquota = new NotaFiscalAliquotaDAO().carregar(oItem.idAliquota);

                    if (new AliquotaDAO().isSubstituido(oAliquota.situacaoTributaria)) {
                        substituido = true;
                    }
                }

                oItem.cfop = new CfopDAO().gerarSaida(oTipoSaida.id, foraEstado, substituido);

                sql = new StringBuilder();
                sql.append("UPDATE notasaidaitem SET");
                sql.append(" cfop = '" + oItem.cfop + "'");
                sql.append(" WHERE id_notasaida = " + i_notaSaida.id);
                sql.append(" AND id_produto = " + oItem.idProduto);

                stm.execute(sql.toString());
            }
        }

        stm.close();
    }

    private void finalizarEscrita(NotaSaidaVO i_notaSaida) throws Exception {
        TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_notaSaida.idTipoSaida);

        NotaFiscalFornecedorVO oFornecedor = new NotaFiscalFornecedorDAO().carregar(Global.idFornecedor);

        EscritaVO oEscrita = new EscritaVO();
        oEscrita.data = i_notaSaida.dataSaida;
        oEscrita.dataEmissao = i_notaSaida.dataHoraEmissao.substring(0, 10);
        oEscrita.numeroNota = i_notaSaida.numeroNota;

        if (oTipoSaida.notaProdutor) {
            oEscrita.idFornecedor = new LojaDAO().getIdFornecedor(i_notaSaida.idLoja);
            oEscrita.idEstado = Global.idEstado;
            oEscrita.idFornecedorProdutorRural = i_notaSaida.idFornecedorDestinatario;
        } else {
            oEscrita.idFornecedor = i_notaSaida.idFornecedorDestinatario;
            oEscrita.idEstado = i_notaSaida.idEstadoDestinatario;
            oEscrita.idFornecedorProdutorRural = -1;
        }

        if (oTipoSaida.idTipoEntrada != -1 && !oTipoSaida.transferencia) {
            oEscrita.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
            oEscrita.idTipoEntrada = oTipoSaida.idTipoEntrada;
            oEscrita.idTipoSaida = -1;
        } else {
            oEscrita.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
            oEscrita.idTipoEntrada = -1;
            oEscrita.idTipoSaida = i_notaSaida.idTipoSaida;
        }

        oEscrita.idClienteEventual = i_notaSaida.idClienteEventualDestinatario;
        oEscrita.valorIpi = i_notaSaida.valorIpi;
        oEscrita.valorIcms = i_notaSaida.valorIcms;
        oEscrita.valorIcmsSubstituicao = i_notaSaida.valorIcmsSubstituicao;
        oEscrita.valorBaseCalculo = i_notaSaida.valorBaseCalculo;
        oEscrita.valorBaseSubstituicao = i_notaSaida.valorBaseSubstituicao;
        oEscrita.valorFrete = i_notaSaida.valorFrete;
        oEscrita.valorDesconto = i_notaSaida.valorDesconto;
        oEscrita.valorOutrasDespesas = i_notaSaida.valorOutrasDespesas;
        oEscrita.valorContabil = i_notaSaida.valorTotal;
        oEscrita.serie = oFornecedor.serieNf;
        oEscrita.especie = new ModeloDAO().getEspecie(oFornecedor.modeloNf);
        oEscrita.modelo = oFornecedor.modeloNf;
        oEscrita.idContaContabilFiscalCredito = oTipoSaida.idContaContabilFiscalCredito;
        oEscrita.idContaContabilFiscalDebito = oTipoSaida.idContaContabilFiscalDebito;
        oEscrita.idHistoricoPadrao = oTipoSaida.idHistoricoPadrao;
        oEscrita.idLoja = i_notaSaida.idLoja;
        oEscrita.ecf = -1;
        oEscrita.chaveNfe = i_notaSaida.chaveNfe;
        oEscrita.idNotaEntrada = -1;
        oEscrita.idNotaDespesa = -1;
        oEscrita.idNotaSaida = i_notaSaida.id;
        oEscrita.idTipoFreteNotaFiscal = i_notaSaida.idTipoFreteNotaFiscal;
        oEscrita.conferido = false;
        oEscrita.idTipoNota = i_notaSaida.idTipoNota;
        oEscrita.cupomFiscal = false;
        oEscrita.idSituacaoNfe = i_notaSaida.idSituacaoNfe;
        oEscrita.informacaoComplementar = i_notaSaida.informacaoComplementarNfe;

        if (i_notaSaida.idNotaEntrada != -1) {
            //NotaEntradaVO oNotaEntrada = carregarNotaEntrada(i_notaSaida.idNotaEntrada);

            if (!oEscrita.observacao.isEmpty()) {
                oEscrita.observacao += ", ";
            }

            //oEscrita.observacao += "REF NF: " + oNotaEntrada.numeroNota + " FORN: " + oNotaEntrada.idFornecedor + " EMISSAO: " + oNotaEntrada.dataEmissao;
        }

        double valorDesconto = 0;

        for (NotaSaidaItemVO oItemSaida : i_notaSaida.vItem) {
            EscritaItemVO oItemEscrita = new EscritaItemVO();
            oItemEscrita.idProduto = oItemSaida.idProduto;
            oItemEscrita.idAliquota = oItemSaida.idAliquota;
            oItemEscrita.quantidade = oItemSaida.quantidade * oItemSaida.qtdEmbalagem;
            oItemEscrita.valorTotal = oItemSaida.valorTotal;
            oItemEscrita.valorIpi = oItemSaida.valorTotalIpi;
            oItemEscrita.valorIsento = oItemSaida.valorIsento;
            oItemEscrita.valorBaseCalculo = oItemSaida.valorBaseCalculo;
            oItemEscrita.valorIcms = oItemSaida.valorIcms;
            oItemEscrita.valorOutras = oItemSaida.valorOutras;
            oItemEscrita.valorBaseSubstituicao = oItemSaida.valorBaseSubstituicao;
            oItemEscrita.valorIcmsSubstituicao = oItemSaida.valorIcmsSubstituicao;
            oItemEscrita.cfop = oItemSaida.cfop;
            oItemEscrita.situacaoTributaria = oItemSaida.situacaoTributaria;

            CfopVO oCfop = new CfopDAO().carregar(oItemSaida.cfop);

            if (oCfop.devolucaoCliente) {
                //oItemEscrita.idTipoPisCofins = Global.getIdTipoPisCofinsCredito(oItemSaida.idTipoPisCofinsDebito);

                oItemEscrita.idTipoPisCofins = new TipoPisCofinsDAO().getId(oItemSaida.cstPisCofins);

            } else if (oEscrita.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId() || oCfop.devolucaoFornecedor) {
                oItemEscrita.idTipoPisCofins = oItemSaida.idTipoPisCofinsCredito;

            } else if (oEscrita.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
                oItemEscrita.idTipoPisCofins = oItemSaida.idTipoPisCofinsDebito;
                oItemEscrita.tipoNaturezaReceita = oItemSaida.tipoNaturezaReceita;
            }

            //calcula desconto
            if (i_notaSaida.aplicaIcmsDesconto) {
                oItemEscrita.valorDesconto = 0;
            } else {
                if (i_notaSaida.valorProduto > 0) {
                    oItemEscrita.valorDesconto = Util.round((oItemSaida.valorTotal / i_notaSaida.valorProduto) * i_notaSaida.valorDesconto, 2);
                }
            }

            valorDesconto += oItemEscrita.valorDesconto;

            //adiciona item
            oEscrita.vItem.add(oItemEscrita);
        }

        //acerta centavos
        if (oEscrita.vItem.size() > 0) {
            EscritaItemVO oItemEscrita = oEscrita.vItem.get(oEscrita.vItem.size() - 1);

            if (!i_notaSaida.aplicaIcmsDesconto && Util.round(valorDesconto, 2) != i_notaSaida.valorDesconto) {
                oItemEscrita.valorDesconto += (i_notaSaida.valorDesconto - valorDesconto);
            }
        }

        //salva escrita
        new EscritaDAO().salvar(oEscrita);
    }

    private void finalizarEstoque(NotaSaidaVO i_notaSaida) throws Exception {
        TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_notaSaida.idTipoSaida);

        boolean baixaReceita = new ParametroDAO().get(105, i_notaSaida.idLoja).getBoolean();

        boolean baixaPerda = (new ParametroDAO().get(193, i_notaSaida.idLoja).getInt() == TipoBaixaPerda.SAIDA.getId());

        for (NotaSaidaItemVO oItem : i_notaSaida.vItem) {
            if (oItem.vDesmembramento.isEmpty()) {
                //atualiza estoque
                double quantidade = oItem.quantidade * oItem.qtdEmbalagem;

                if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.LOJA.getId()) {
                    AcertoEstoqueVO oEstoque = new AcertoEstoqueVO();
                    oEstoque.idLoja = i_notaSaida.idLoja;
                    oEstoque.idProduto = oItem.idProduto;
                    oEstoque.data = i_notaSaida.dataSaida;

                    if (oTipoSaida.entraEstoque) {
                        oEstoque.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();

                    } else if (oTipoSaida.baixaEstoque) {
                        oEstoque.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                    }

                    if (oTipoSaida.geraDevolucao) {
                        oEstoque.idTipoMovimentacao = TipoMovimentacao.DEVOLUCAO.getId();
                    } else {
                        oEstoque.idTipoMovimentacao = TipoMovimentacao.SAIDA.getId();
                    }

                    oEstoque.quantidade = quantidade;
                    oEstoque.baixaReceita = baixaReceita;
                    oEstoque.baixaAssociado = true;
                    oEstoque.baixaPerda = baixaPerda;
                    oEstoque.observacao = "NF " + i_notaSaida.numeroNota + ", TIPO: " + oTipoSaida.descricao;

                    new EstoqueDAO().alterar(oEstoque);

                } else if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.TROCA.getId()) {
                    AcertoTrocaVO oTroca = new AcertoTrocaVO();
                    oTroca.idProduto = oItem.idProduto;
                    oTroca.idLoja = i_notaSaida.idLoja;
                    oTroca.data = i_notaSaida.dataSaida;

                    if (oTipoSaida.entraEstoque) {
                        oTroca.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();

                    } else if (oTipoSaida.baixaEstoque) {
                        oTroca.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                    }

                    oTroca.quantidade = quantidade;

                    new EstoqueDAO().alterarTroca(oTroca);

                } else if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.CESTA_BASICA.getId()) {
                    AcertoCestaBasicaVO oCestaBasica = new AcertoCestaBasicaVO();
                    oCestaBasica.idProduto = oItem.idProduto;
                    oCestaBasica.idLoja = i_notaSaida.idLoja;
                    oCestaBasica.data = i_notaSaida.dataSaida;

                    if (oTipoSaida.entraEstoque) {
                        oCestaBasica.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();

                    } else if (oTipoSaida.baixaEstoque) {
                        oCestaBasica.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                    }

                    oCestaBasica.quantidade = quantidade;

                    new EstoqueDAO().alterarCestaBasica(oCestaBasica);
                }
            }
        }

        for (NotaSaidaItemVO oItem : i_notaSaida.vDesmembramento) {
            //atualiza estoque
            double quantidade = oItem.quantidade * oItem.qtdEmbalagem;

            if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.LOJA.getId()) {
                AcertoEstoqueVO oEstoque = new AcertoEstoqueVO();
                oEstoque.idProduto = oItem.idProduto;
                oEstoque.data = i_notaSaida.dataSaida;
                oEstoque.idLoja = i_notaSaida.idLoja;

                if (oTipoSaida.entraEstoque) {
                    oEstoque.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();

                } else if (oTipoSaida.baixaEstoque) {
                    oEstoque.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                }

                if (oTipoSaida.geraDevolucao) {
                    oEstoque.idTipoMovimentacao = TipoMovimentacao.DEVOLUCAO.getId();
                } else {
                    oEstoque.idTipoMovimentacao = TipoMovimentacao.SAIDA.getId();
                }

                oEstoque.quantidade = quantidade;
                oEstoque.baixaReceita = baixaReceita;
                oEstoque.baixaAssociado = true;
                oEstoque.baixaPerda = baixaPerda;
                oEstoque.observacao = "NF " + i_notaSaida.numeroNota + ", TIPO: " + oTipoSaida.descricao;

                new EstoqueDAO().alterar(oEstoque);

            } else if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.TROCA.getId()) {
                AcertoTrocaVO oTroca = new AcertoTrocaVO();
                oTroca.idProduto = oItem.idProduto;
                oTroca.data = i_notaSaida.dataSaida;
                oTroca.idLoja = i_notaSaida.idLoja;

                if (oTipoSaida.entraEstoque) {
                    oTroca.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();

                } else if (oTipoSaida.baixaEstoque) {
                    oTroca.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                }

                oTroca.quantidade = quantidade;

                new EstoqueDAO().alterarTroca(oTroca);

            } else if (i_notaSaida.tipoLocalBaixa == TipoLocalBaixaNotaSaida.CESTA_BASICA.getId()) {
                AcertoCestaBasicaVO oCestaBasica = new AcertoCestaBasicaVO();
                oCestaBasica.idProduto = oItem.idProduto;
                oCestaBasica.data = i_notaSaida.dataSaida;
                oCestaBasica.idLoja = i_notaSaida.idLoja;

                if (oTipoSaida.entraEstoque) {
                    oCestaBasica.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();

                } else if (oTipoSaida.baixaEstoque) {
                    oCestaBasica.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                }

                oCestaBasica.quantidade = quantidade;

                new EstoqueDAO().alterarCestaBasica(oCestaBasica);
            }
        }
    }

    public void salvar(NotaSaidaVO i_notaSaida) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id FROM notasaida WHERE chavenfe = '" + i_notaSaida.chaveNfe + "'");

            if (rst.next()) {
                Util.exibirMensagemConfirmar("Esta nota já existe, deseja exluir e importar novamente ?", "Atenção");

                NotaSaidaVO oNotaSaida = carregar(rst.getLong("id"));

                excluir(oNotaSaida);
            }

            //salva nota
            sql = new StringBuilder();
            sql.append("SELECT id, id_situacaonotasaida FROM notasaida WHERE id = " + i_notaSaida.id);

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                sql = new StringBuilder();
                sql.append("UPDATE notasaida SET");
                sql.append(" numeronota = " + i_notaSaida.numeroNota + ",");
                sql.append(" id_loja = " + i_notaSaida.idLoja + ",");
                sql.append(" id_tiponota = " + i_notaSaida.idTipoNota + ",");
                sql.append(" id_fornecedordestinatario = " + (i_notaSaida.idFornecedorDestinatario == -1 ? "NULL" : i_notaSaida.idFornecedorDestinatario) + ",");
                sql.append(" id_clienteeventualdestinatario = " + (i_notaSaida.idClienteEventualDestinatario == -1 ? "NULL" : i_notaSaida.idClienteEventualDestinatario) + ",");
                sql.append(" id_tiposaida = " + i_notaSaida.idTipoSaida + ",");
                sql.append(" datahoraemissao = '" + Util.formatDataHoraBanco(i_notaSaida.dataHoraEmissao) + "',");
                sql.append(" datasaida = '" + Util.formatDataBanco(i_notaSaida.dataSaida) + "',");
                sql.append(" valoripi = " + i_notaSaida.valorIpi + ",");
                sql.append(" valorbaseipi = " + i_notaSaida.valorBaseIpi + ",");
                sql.append(" valorfrete = " + i_notaSaida.valorFrete + ",");
                sql.append(" valoroutrasdespesas = " + i_notaSaida.valorOutrasDespesas + ",");
                sql.append(" valorproduto = " + i_notaSaida.valorProduto + ",");
                sql.append(" valortotal = " + i_notaSaida.valorTotal + ",");
                sql.append(" valorbasecalculo = " + i_notaSaida.valorBaseCalculo + ",");
                sql.append(" valoricms = " + i_notaSaida.valorIcms + ",");
                sql.append(" valorbasesubstituicao = " + i_notaSaida.valorBaseSubstituicao + ",");
                sql.append(" valoricmssubstituicao = " + i_notaSaida.valorIcmsSubstituicao + ",");
                sql.append(" valorseguro = " + i_notaSaida.valorSeguro + ",");
                sql.append(" valordesconto = " + i_notaSaida.valorDesconto + ",");
                sql.append(" impressao  = " + i_notaSaida.impressao + ",");
                sql.append(" id_situacaonotasaida = " + i_notaSaida.idSituacaoNotaSaida + ",");
                sql.append(" id_tipofretenotafiscal = " + i_notaSaida.idTipoFreteNotaFiscal + ",");
                sql.append(" id_motoristatransportador = " + (i_notaSaida.idMotoristaTransportador == -1 ? "NULL" : i_notaSaida.idMotoristaTransportador) + ",");
                sql.append(" id_fornecedortransportador = " + (i_notaSaida.idFornecedorTransportador == -1 ? "NULL" : i_notaSaida.idFornecedorTransportador) + ",");
                sql.append(" id_clienteeventualtransportador = " + (i_notaSaida.idClienteEventualTransportador == -1 ? "NULL" : i_notaSaida.idClienteEventualTransportador) + ",");
                sql.append(" placa = '" + i_notaSaida.placa + "',");
                sql.append(" id_tipodevolucao = " + (i_notaSaida.idTipoDevolucao == -1 ? "NULL" : i_notaSaida.idTipoDevolucao) + ",");
                sql.append(" informacaocomplementar = '" + i_notaSaida.informacaoComplementar + "',");
                sql.append(" senha = '" + i_notaSaida.senha + "',");
                sql.append(" tipolocalbaixa = " + i_notaSaida.tipoLocalBaixa + ",");
                sql.append(" id_situacaonfe = " + i_notaSaida.idSituacaoNfe + ",");
                sql.append(" chavenfe = '" + i_notaSaida.chaveNfe + "',");
                sql.append(" id_notasaidacomplemento = " + (i_notaSaida.idNotaSaidaComplemento == -1 ? "NULL" : i_notaSaida.idNotaSaidaComplemento) + ",");
                sql.append(" volume = " + i_notaSaida.volume + ",");
                sql.append(" pesoliquido = " + i_notaSaida.pesoLiquido + ",");
                //sql.append(" pesobruto = " + i_notaSaida.pesoBruto + ",");
                sql.append(" emailnfe = " + i_notaSaida.emailNfe + ",");
                sql.append(" aplicaicmsdesconto = " + i_notaSaida.aplicaIcmsDesconto + ",");
                sql.append(" aplicaicmsencargo = " + i_notaSaida.aplicaIcmsEncargo + ",");
                sql.append(" aplicapiscofinsdesconto = " + i_notaSaida.aplicaPisCofinsDesconto + ", ");
                sql.append(" aplicapiscofinsencargo = " + i_notaSaida.aplicaPisCofinsEncargo + ", ");
                sql.append(" id_notaentrada = " + (i_notaSaida.idNotaEntrada == -1 ? "NULL" : i_notaSaida.idNotaEntrada));
                sql.append(" WHERE id = " + i_notaSaida.id);

                stm.execute(sql.toString());

                new LogTransacaoDAO().gerar(Formulario.NOTAFISCAL_SAIDA, TipoTransacao.ALTERACAO, i_notaSaida.numeroNota, "", i_notaSaida.id);

            } else {
                sql = new StringBuilder();
                sql.append("INSERT INTO notasaida (id_loja, numeronota, id_tiponota, id_fornecedordestinatario, id_clienteeventualdestinatario,");
                sql.append(" id_tiposaida, datahoraemissao, datasaida, valoripi, valorbaseipi, valorfrete, valoroutrasdespesas, valorproduto, valortotal,");
                sql.append(" valorbasecalculo, valoricms, valorbasesubstituicao, valoricmssubstituicao, valorseguro, valordesconto, impressao,");
                sql.append(" id_situacaonotasaida, id_tipofretenotafiscal, id_motoristatransportador, id_fornecedortransportador, id_clienteeventualtransportador,");
                sql.append(" placa, id_tipodevolucao, informacaocomplementar, senha, tipolocalbaixa, volume, pesoliquido,");
                sql.append(" id_situacaonfe, chavenfe, id_notasaidacomplemento, emailnfe, aplicaicmsdesconto, aplicaicmsencargo, id_notaentrada) VALUES (");
                sql.append(i_notaSaida.idLoja + ", ");
                sql.append(i_notaSaida.numeroNota + ", ");
                sql.append(i_notaSaida.idTipoNota + ", ");
                sql.append((i_notaSaida.idFornecedorDestinatario == -1 ? "NULL" : i_notaSaida.idFornecedorDestinatario) + ", ");
                sql.append((i_notaSaida.idClienteEventualDestinatario == -1 ? "NULL" : i_notaSaida.idClienteEventualDestinatario) + ", ");
                sql.append(i_notaSaida.idTipoSaida + ", ");
                sql.append("'" + Util.formatDataHoraBanco(i_notaSaida.dataHoraEmissao) + "', ");
                sql.append("'" + Util.formatDataBanco(i_notaSaida.dataSaida) + "', ");
                sql.append(i_notaSaida.valorIpi + ", ");
                sql.append(i_notaSaida.valorBaseIpi + ", ");
                sql.append(i_notaSaida.valorFrete + ", ");
                sql.append(i_notaSaida.valorOutrasDespesas + ", ");
                sql.append(i_notaSaida.valorProduto + ", ");
                sql.append(i_notaSaida.valorTotal + ", ");
                sql.append(i_notaSaida.valorBaseCalculo + ", ");
                sql.append(i_notaSaida.valorIcms + ", ");
                sql.append(i_notaSaida.valorBaseSubstituicao + ", ");
                sql.append(i_notaSaida.valorIcmsSubstituicao + ", ");
                sql.append(i_notaSaida.valorSeguro + ", ");
                sql.append(i_notaSaida.valorDesconto + ", ");
                sql.append(i_notaSaida.impressao + ", ");
                sql.append(i_notaSaida.idSituacaoNotaSaida + ", ");
                sql.append(i_notaSaida.idTipoFreteNotaFiscal + ", ");
                sql.append((i_notaSaida.idMotoristaTransportador == -1 ? "NULL" : i_notaSaida.idMotoristaTransportador) + ", ");
                sql.append((i_notaSaida.idFornecedorTransportador == -1 ? "NULL" : i_notaSaida.idFornecedorTransportador) + ", ");
                sql.append((i_notaSaida.idClienteEventualTransportador == -1 ? "NULL" : i_notaSaida.idClienteEventualTransportador) + ", ");
                sql.append("'" + i_notaSaida.placa + "', ");
                sql.append((i_notaSaida.idTipoDevolucao == -1 ? "NULL" : i_notaSaida.idTipoDevolucao) + ", ");
                sql.append("'" + i_notaSaida.informacaoComplementar + "', ");
                sql.append("'" + i_notaSaida.senha + "', ");
                sql.append(i_notaSaida.tipoLocalBaixa + ", ");
                sql.append(i_notaSaida.volume + ", ");
                sql.append(i_notaSaida.pesoLiquido + ", ");
                //sql.append(i_notaSaida.pesoBruto + ", ");       //TODO
                sql.append(i_notaSaida.idSituacaoNfe + ", ");
                sql.append("'" + i_notaSaida.chaveNfe + "', ");
                sql.append((i_notaSaida.idNotaSaidaComplemento == -1 ? "NULL" : i_notaSaida.idNotaSaidaComplemento) + ",");
                sql.append("FALSE, ");
                sql.append(i_notaSaida.aplicaIcmsDesconto + ", ");
                sql.append(i_notaSaida.aplicaIcmsEncargo + ", ");
                sql.append((i_notaSaida.idNotaEntrada == -1 ? "NULL" : i_notaSaida.idNotaEntrada) + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('notasaida_id_seq') AS id");
                rst.next();

                i_notaSaida.id = rst.getLong("id");

                new LogTransacaoDAO().gerar(Formulario.NOTAFISCAL_SAIDA, TipoTransacao.INCLUSAO, i_notaSaida.numeroNota, "", i_notaSaida.id);
            }

            stm.execute("DELETE FROM notasaidaitemdesmembramento WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidaitem WHERE id_notasaida = " + i_notaSaida.id);
            stm.execute("DELETE FROM notasaidavencimento WHERE id_notasaida = " + i_notaSaida.id);

            for (NotaSaidaVencimentoVO oVencimento : i_notaSaida.vVencimento) {
                sql = new StringBuilder();

                sql.append("INSERT INTO notasaidavencimento (id_notasaida, datavencimento, valor) VALUES (");
                sql.append(i_notaSaida.id + ", ");
                sql.append("'" + Util.formatDataBanco(oVencimento.dataVencimento) + "', ");
                sql.append("'" + oVencimento.valor + "')");

                stm.execute(sql.toString());
            }

            for (NotaSaidaItemVO oItem : i_notaSaida.vItem) {
                sql = new StringBuilder();
                sql.append("INSERT INTO notasaidaitem (id_notasaida, id_produto, quantidade, qtdembalagem, valor, valortotal, valoripi, valorbaseipi,");
                sql.append(" id_aliquota, valorbasecalculo, valoricms, valorbasesubstituicao, valoricmssubstituicao, valorpiscofins, tipoiva,");
                sql.append(" id_aliquotapautafiscal, valordesconto, valorisento, valoroutras, situacaotributaria, id_aliquotadispensado, valoricmsdispensado,");
                sql.append(" tiponaturezareceita, cfop, localdesembaraco, id_estadodesembaraco, numeroadicao, datadesembaraco, id_tiposaida) VALUES (");
                sql.append(i_notaSaida.id + ", ");
                sql.append(oItem.idProduto + ", ");
                sql.append(oItem.quantidade + ", ");
                sql.append(oItem.qtdEmbalagem + ", ");
                sql.append(oItem.valor + ", ");
                sql.append(oItem.valorTotal + ", ");
                sql.append(oItem.valorTotalIpi + ", ");
                sql.append(oItem.valorBaseIpi + ", ");
                sql.append(oItem.idAliquota + ", ");
                sql.append(oItem.valorBaseCalculo + ", ");
                sql.append(oItem.valorIcms + ", ");
                sql.append(oItem.valorBaseSubstituicao + ", ");
                sql.append(oItem.valorIcmsSubstituicao + ", ");
                sql.append(oItem.valorPisCofins + ",");
                sql.append(oItem.tipoIva + ",");
                sql.append((oItem.idAliquotaPautaFiscal == -1 ? "NULL" : oItem.idAliquotaPautaFiscal) + ", ");
                sql.append(oItem.valorDesconto + ", ");
                sql.append(oItem.valorIsento + ", ");
                sql.append(oItem.valorOutras + ", ");
                sql.append(oItem.situacaoTributaria + ", ");
                sql.append((oItem.idAliquotaDispensado == -1 ? "NULL" : oItem.idAliquotaDispensado) + ", ");
                sql.append(oItem.valorIcmsDispensado + ", ");
                sql.append((oItem.tipoNaturezaReceita == -1 ? "NULL" : oItem.tipoNaturezaReceita) + ", ");
                sql.append((oItem.cfop.isEmpty() ? "NULL" : "'" + oItem.cfop + "', "));
                sql.append("'" + oItem.localDesembaraco + "', ");
                sql.append((oItem.idEstadoDesembaraco == -1 ? "NULL" : oItem.idEstadoDesembaraco) + ", ");
                sql.append(oItem.numeroAdicao + ", ");
                sql.append((oItem.dataDesembaraco.isEmpty() ? "NULL" : "'" + Util.formatDataBanco(oItem.dataDesembaraco) + "'") + ", ");
                sql.append(oItem.idTipoSaida + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('notasaidaitem_id_seq') AS id");
                rst.next();

                oItem.id = rst.getLong("id");
            }

            //salva reposicao
            stm.execute("DELETE FROM notasaidareposicao WHERE id_notasaida = " + i_notaSaida.id);

            sql = new StringBuilder();

            for (NotaSaidaReposicaoVO oReposicao : i_notaSaida.vReposicao) {  //TODO
                sql.append("INSERT INTO notasaidareposicao (id_notasaida, id_reposicao) VALUES (");
                sql.append(i_notaSaida.id + ", ");
                sql.append(oReposicao.idReposicao + ");");
            }

            stm.execute(sql.toString());

            //salva cupom
            stm.execute("DELETE FROM notasaidacupom WHERE id_notasaida = " + i_notaSaida.id);

            sql = new StringBuilder();

            for (NotaSaidaCupomVO oCupom : i_notaSaida.vCupom) {    //TODO
                sql.append("INSERT INTO notasaidacupom (id_notasaida, numerocupom, data, ecf) VALUES (");
                sql.append(i_notaSaida.id + ", ");
                sql.append(oCupom.numeroCupom + ",");
                sql.append("'" + Util.formatDataBanco(oCupom.data) + "',");
                sql.append(oCupom.ecf + ");");
            }

            stm.execute(sql.toString());

            //salva nota entrada
            stm.execute("DELETE FROM notasaidanotaentrada WHERE id_notasaida = " + i_notaSaida.id);

            sql = new StringBuilder();

            for (NotaSaidaNotaEntradaVO oNotaEntrada : i_notaSaida.vNotaEntrada) {   //TODO
                sql.append("INSERT INTO notasaidanotaentrada (id_notasaida, id_notaentrada) VALUES (");
                sql.append(i_notaSaida.id + ", ");
                sql.append(oNotaEntrada.idNotaEntrada + ");");
            }

            stm.execute(sql.toString());

            //salva troca cupom
            stm.execute("DELETE FROM notasaidatrocacupom WHERE id_notasaida = " + i_notaSaida.id);

            sql = new StringBuilder();

            for (NotaSaidaTrocaCupomVO oTrocaCupom : i_notaSaida.vTrocaCupom) {   //TODO
                sql.append("INSERT INTO notasaidatrocacupom (id_notasaida, id_trocacupom) VALUES (");
                sql.append(i_notaSaida.id + ", ");
                sql.append(oTrocaCupom.idTrocaCupom + ");");
            }

            stm.execute(sql.toString());

            //salva devolucao cupom
            stm.execute("DELETE FROM notasaidadevolucaocupom WHERE id_notasaida = " + i_notaSaida.id);

            sql = new StringBuilder();

            for (NotaSaidaDevolucaoCupomVO oDevolucaoCupom : i_notaSaida.vDevolucaoCupom) {   //TODO
                sql.append("INSERT INTO notasaidadevolucaocupom (id_notasaida, id_devolucaocupom) VALUES (");
                sql.append(i_notaSaida.id + ", ");
                sql.append(oDevolucaoCupom.idDevolucaoCupom + ");");
            }

            stm.execute(sql.toString());

            //salva xml
            stm.execute("DELETE FROM notasaidanfe WHERE id_notasaida = " + i_notaSaida.id);

            sql = new StringBuilder();
            sql.append("INSERT INTO notasaidanfe (id_notasaida, xml) VALUES (");
            sql.append(i_notaSaida.id + ",");
            sql.append("'" + i_notaSaida.xml.replace("'", "''").replace("\\", "\\\\") + "')");

            stm.execute(sql.toString());

            finalizar(i_notaSaida);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public NotaSaidaItemVO carregarProduto(int i_idProduto, String i_cfop, int i_idEstado, int i_idFornecedor) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        boolean utilizaCustoMedio = new ParametroDAO().get(217).getBoolean();

        stm = Conexao.createStatement();

        TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_cfop);

        sql = new StringBuilder();
        sql.append("SELECT p.id_tipoembalagem, p.descricaocompleta AS produto, p.qtdembalagem, pc.precovenda, p.id_tipopiscofins, p.id_tipopiscofinscredito, pc.valoricmssubstituicao,");
        sql.append(" pc.valoripi, pa.id_aliquotadebito, pa.id_aliquotacredito, pa.id_aliquotadebitoforaestado, pa.id_aliquotacreditoforaestado, pc.custosemperdasemimposto,");

        if (utilizaCustoMedio) {
            sql.append(" pc.customediosemimposto AS custosemimposto, pc.customediocomimposto AS custocomimposto,");
        } else {
            sql.append(" pc.custosemimposto, pc.custocomimposto,");
        }

        sql.append(" p.tiponaturezareceita, pc.id_aliquotacredito AS id_aliquotacreditoentrada, p.ncm1, p.ncm2, p.ncm3, tipoembalagem.descricao AS tipoembalagem, p.pesoliquido,");
        sql.append(" COALESCE((SELECT codigobarras FROM produtoautomacao WHERE id_produto = p.id AND LENGTH(codigobarras::varchar) <= 13 LIMIT 1), 0) AS codigobarras,");
        sql.append(" p.id_tipoorigemmercadoria, p.impostomedionacional, p.impostomedioimportado");
        sql.append(" FROM produto AS p");
        sql.append(" INNER JOIN produtocomplemento AS pc ON pc.id_produto = p.id AND pc.id_loja = " + Global.idLoja);
        sql.append(" INNER JOIN produtoaliquota AS pa ON pa.id_produto = p.id AND pa.id_estado = " + Global.idEstado);
        sql.append(" INNER JOIN tipoembalagem ON tipoembalagem.id = p.id_tipoembalagem");
        sql.append(" WHERE p.id = " + i_idProduto);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Produto " + i_idProduto + " não encontrado!");
        }

        NotaSaidaItemVO oItem = new NotaSaidaItemVO();
        oItem.idProduto = i_idProduto;
        oItem.produto = rst.getString("produto");
        oItem.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
        oItem.tipoEmbalagem = rst.getString("tipoembalagem");
        oItem.qtdEmbalagem = rst.getInt("qtdembalagem");
        oItem.idTipoPisCofinsDebito = rst.getInt("id_tipopiscofins");
        oItem.idTipoPisCofinsCredito = rst.getInt("id_tipopiscofinscredito");
        oItem.codigoBarras = rst.getLong("codigobarras");
        oItem.ncm1 = rst.getObject("ncm1") == null ? -1 : rst.getInt("ncm1");
        oItem.ncm2 = rst.getObject("ncm2") == null ? -1 : rst.getInt("ncm2");
        oItem.ncm3 = rst.getObject("ncm3") == null ? -1 : rst.getInt("ncm3");
        oItem.pesoLiquido = rst.getDouble("pesoliquido");
        oItem.tipoNaturezaReceita = rst.getObject("tiponaturezareceita") == null ? -1 : rst.getInt("tiponaturezareceita");
        oItem.idTipoOrigemMercadoria = rst.getInt("id_tipoorigemmercadoria");

        switch (rst.getInt("id_tipoorigemmercadoria")) {
            case 0:
            case 3:
            case 4:
            case 5:
                oItem.impostoMedio = rst.getDouble("impostomedionacional");
                break;
            case 1:
            case 2:
            case 6:
            case 7:
                oItem.impostoMedio = rst.getDouble("impostomedioimportado");
                break;
        }

        //verifica aliquota
        if (oTipoSaida.naoCreditaIcms) {
            oItem.idAliquota = new AliquotaDAO().getIdOutras();

        } else {
            if (oTipoSaida.utilizaIcmsEntrada) {
                oItem.idAliquota = rst.getInt("id_aliquotacreditoentrada");
            } else {
                if (oTipoSaida.utilizaIcmsCredito) {
                    if (i_idEstado != Global.idEstado) {
                        oItem.idAliquota = rst.getInt("id_aliquotacreditoforaestado");
                    } else {
                        oItem.idAliquota = rst.getInt("id_aliquotacredito");
                    }
                } else {
                    if (i_idEstado != Global.idEstado) {
                        oItem.idAliquota = rst.getInt("id_aliquotadebitoforaestado");
                    } else {
                        oItem.idAliquota = rst.getInt("id_aliquotadebito");
                    }
                }
            }
        }

        NotaFiscalAliquotaVO oNotaFiscalAliquota = new NotaFiscalAliquotaDAO().carregar(oItem.idAliquota);

        if (!new AliquotaDAO().isSubstituido(oNotaFiscalAliquota.situacaoTributaria) && i_idFornecedor != -1 && oTipoSaida.geraDevolucao) {
            NotaFiscalFornecedorVO oFornecedor = new NotaFiscalFornecedorDAO().carregar(i_idFornecedor);

            TipoEmpresaVO oTipoEmpresa = new TipoEmpresaDAO().carregar(oFornecedor.idTipoEmpresa);

            if (oTipoEmpresa.idTipoCrt == TipoCrt.SIMPLES_NACIONAL.getId()) {
                oItem.idAliquota = new AliquotaDAO().getIdOutras();

                oNotaFiscalAliquota = new NotaFiscalAliquotaDAO().carregar(oItem.idAliquota);
            }
        }

        oItem.aliquota = oNotaFiscalAliquota.descricao;

        //verifica custo
        if (oTipoSaida.utilizaPrecoVenda) {
            oItem.valor = rst.getDouble("precovenda");
        } else if (oTipoSaida.transferencia) {
            oItem.valor = rst.getDouble("custocomimposto");
        } else {
            double custoSemImposto = 0;

            if (rst.getDouble("custosemperdasemimposto") != 0) {
                custoSemImposto = rst.getDouble("custosemperdasemimposto");
            } else {
                custoSemImposto = rst.getDouble("custosemimposto");
            }

            double pisCofins = 0;

            if (oTipoSaida.utilizaIcmsCredito) {
                pisCofins = new TipoPisCofinsDAO().getPisCofins(oItem.idTipoPisCofinsCredito);
            } else {
                pisCofins = new TipoPisCofinsDAO().getPisCofins(oItem.idTipoPisCofinsDebito);
            }

            if (oTipoSaida.geraDevolucao) {
                oItem.valor = Util.round(((100 * (custoSemImposto - rst.getDouble("valoricmssubstituicao") - rst.getDouble("valoripi"))) / (100 - (pisCofins + oNotaFiscalAliquota.porcentagem - (oNotaFiscalAliquota.porcentagem * oNotaFiscalAliquota.reduzido / 100)))), 3);
            } else {
                oItem.valor = Util.round(((100 * (custoSemImposto - rst.getDouble("valoricmssubstituicao") - rst.getDouble("valoripi"))) / (100 - (pisCofins + oNotaFiscalAliquota.porcentagem - (oNotaFiscalAliquota.porcentagem * oNotaFiscalAliquota.reduzido / 100))) + rst.getDouble("valoripi") + rst.getDouble("valoricmssubstituicao")), 3);
            }
        }

        if (oTipoSaida.geraDevolucao) {
            oItem.valorIpi = rst.getDouble("valoripi");
        }

        //carregar desmembramento
        sql = new StringBuilder();
        sql.append("SELECT di.id_produto, produto.descricaocompleta AS produto, di.percentualdesossa, di.percentualcusto, di.percentualestoque,");
        sql.append(" di.percentualperda, produto.id_tipoembalagem");
        sql.append(" FROM desmembramento AS d");
        sql.append(" INNER JOIN desmembramentoitem AS di ON di.id_desmembramento = d.id");
        sql.append(" INNER JOIN produto ON di.id_produto = produto.id");
        sql.append(" INNER JOIN produtocomplemento AS pc ON di.id_produto = pc.id_produto AND pc.id_loja = " + Global.idLoja);
        sql.append(" WHERE d.id_produto = " + i_idProduto);
        sql.append(" AND d.id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaSaidaItemDesmembramentoVO oDesmembramento = new NotaSaidaItemDesmembramentoVO();
            oDesmembramento.idProduto = rst.getInt("id_produto");
            oDesmembramento.produto = rst.getString("produto");
            oDesmembramento.percentualCusto = rst.getDouble("percentualcusto");
            oDesmembramento.percentualDesossa = rst.getDouble("percentualdesossa");
            oDesmembramento.percentualEstoque = rst.getDouble("percentualestoque");
            oDesmembramento.percentualPerda = rst.getDouble("percentualperda");
            oDesmembramento.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
            oDesmembramento.idTipoPisCofinsDebito = oItem.idTipoPisCofinsCredito;

            oItem.vDesmembramento.add(oDesmembramento);
        }

        stm.close();

        return oItem;
    }

    private String calcularSenhaTransferencia(int i_numero, String i_data, int i_idLoja) throws Exception {
        int data = Integer.parseInt(new SimpleDateFormat("ddMMyyyy").format(new SimpleDateFormat("dd/MM/yyyy").parse(i_data)));

        String senha = Util.formatNumber(Long.toHexString((long) i_numero * data * i_idLoja), 8).substring(0, 8);

        return senha.toUpperCase();
    }
}
