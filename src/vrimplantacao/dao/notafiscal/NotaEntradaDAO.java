package vrimplantacao.dao.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.dao.ParametroDAO;
import vrimplantacao.dao.cadastro.AliquotaDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.TipoPisCofinsDAO;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.cadastro.SituacaoCadastro;
import vrimplantacao.vo.notafiscal.NotaEntradaBonificacaoVO;
import vrimplantacao.vo.notafiscal.NotaEntradaItemDesmembramentoVO;
import vrimplantacao.vo.notafiscal.NotaEntradaItemVO;
import vrimplantacao.vo.notafiscal.NotaEntradaPedidoVO;
import vrimplantacao.vo.notafiscal.NotaEntradaVO;
import vrimplantacao.vo.notafiscal.NotaEntradaVencimentoVO;
import vrimplantacao.vo.notafiscal.NotaFiscalAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.notafiscal.SituacaoNotaEntrada;
import vrimplantacao.vo.notafiscal.TipoCrt;
import vrimplantacao.vo.notafiscal.TipoEmpresaVO;
import vrimplantacao.vo.notafiscal.TipoEntradaVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class NotaEntradaDAO {

    public void excluir(long i_id) throws Exception {
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            NotaEntradaVO oNotaEntrada = carregar(i_id);

            stm.execute("UPDATE notasaida SET id_notaentrada = NULL WHERE id_notaentrada = " + i_id);
            stm.execute("UPDATE reposicao SET id_notaentrada = NULL WHERE id_notaentrada = " + i_id);

            stm.execute("DELETE FROM notasaidanotaentrada WHERE id_notaentrada = " + i_id);
            stm.execute("DELETE FROM notaentradadivergencia WHERE id_notaentrada = " + i_id);
            stm.execute("DELETE FROM notaentradavencimento WHERE id_notaentrada = " + i_id);
            stm.execute("DELETE FROM notaentradaitemdesmembramento WHERE id_notaentrada = " + i_id);
            stm.execute("DELETE FROM notaentradabonificacao WHERE id_notaentrada = " + i_id);
            stm.execute("DELETE FROM notaentradapedido WHERE id_notaentrada = " + i_id);
            stm.execute("DELETE FROM notaentradaitem WHERE id_notaentrada = " + i_id);
            stm.execute("DELETE FROM notaentrada WHERE id = " + i_id);

            new LogTransacaoDAO().gerar(Formulario.NOTAFISCAL_ENTRADA, TipoTransacao.EXCLUSAO, oNotaEntrada.numeroNota, "", oNotaEntrada.id);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public NotaEntradaVO carregar(long i_id) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT ne.*, fornecedor.id_estado, fornecedor.id_tipopagamento");
        sql.append(" FROM notaentrada ne");
        sql.append(" INNER JOIN fornecedor ON fornecedor.id = ne.id_fornecedor");
        sql.append(" WHERE ne.id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Nota entrada não encontrada!");
        }

        NotaEntradaVO oNotaEntrada = new NotaEntradaVO();
        oNotaEntrada.id = rst.getLong("id");
        oNotaEntrada.idLoja = rst.getInt("id_loja");
        oNotaEntrada.numeroNota = rst.getInt("numeronota");
        oNotaEntrada.idFornecedor = rst.getInt("id_fornecedor");
        oNotaEntrada.dataEntrada = Util.formatDataGUI(rst.getDate("dataentrada"));
        oNotaEntrada.idTipoEntrada = rst.getInt("id_tipoentrada");
        oNotaEntrada.dataEmissao = Util.formatDataGUI(rst.getDate("dataemissao"));
        oNotaEntrada.dataHoraLancamento = Util.formatDataHoraGUI(rst.getTimestamp("datahoralancamento"));
        oNotaEntrada.valorIpi = rst.getDouble("valoripi");
        oNotaEntrada.valorFrete = rst.getDouble("valorfrete");
        oNotaEntrada.valorDesconto = rst.getDouble("valordesconto");
        oNotaEntrada.valorOutrasDespesas = rst.getDouble("valoroutradespesa");
        oNotaEntrada.valorDespesaAdicional = rst.getDouble("valordespesaadicional");
        oNotaEntrada.valorMercadoria = rst.getDouble("valormercadoria");
        oNotaEntrada.valorTotal = rst.getDouble("valortotal");
        oNotaEntrada.valorIcms = rst.getDouble("valoricms");
        oNotaEntrada.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
        oNotaEntrada.observacao = rst.getString("observacao");
        oNotaEntrada.idUsuario = rst.getInt("id_usuario");
        oNotaEntrada.impressao = rst.getBoolean("impressao");
        oNotaEntrada.produtorRural = rst.getBoolean("produtorrural");
        oNotaEntrada.aplicaAliquota = rst.getInt("aplicaaliquota");
        oNotaEntrada.aplicaCustoDesconto = rst.getBoolean("aplicacustodesconto");
        oNotaEntrada.aplicaIcmsDesconto = rst.getBoolean("aplicaicmsdesconto");
        oNotaEntrada.aplicaCustoEncargo = rst.getBoolean("aplicacustoencargo");
        oNotaEntrada.aplicaIcmsEncargo = rst.getBoolean("aplicaicmsencargo");
        oNotaEntrada.aplicaDespesaAdicional = rst.getBoolean("aplicadespesaadicional");
        oNotaEntrada.idSituacaoNotaEntrada = rst.getInt("id_situacaonotaentrada");
        oNotaEntrada.serie = rst.getString("serie");
        oNotaEntrada.modelo = rst.getString("modelo");
        oNotaEntrada.valorGuiaSubstituicao = rst.getDouble("valorguiasubstituicao");
        oNotaEntrada.valorBaseCalculo = rst.getDouble("valorbasecalculo");
        oNotaEntrada.idEstado = rst.getInt("id_estado");
        oNotaEntrada.valorBaseSubstituicao = rst.getDouble("valorbasesubstituicao");
        oNotaEntrada.valorFunRural = rst.getDouble("valorfunrural");
        oNotaEntrada.idTipoPagamento = rst.getInt("id_tipopagamento");
        oNotaEntrada.valorDescontoBoleto = rst.getDouble("valordescontoboleto");
        oNotaEntrada.chaveNfe = rst.getString("chavenfe");
        oNotaEntrada.idTipoFreteNotaFiscal = rst.getInt("id_tipofretenotafiscal");
        oNotaEntrada.conferido = rst.getBoolean("conferido");
        oNotaEntrada.idNotaSaida = rst.getObject("id_notasaida") == null ? -1 : rst.getLong("id_notasaida");
        oNotaEntrada.idTipoNota = rst.getInt("id_tiponota");
        oNotaEntrada.liberadoPedido = rst.getBoolean("liberadopedido");
        oNotaEntrada.importadoXml = rst.getBoolean("importadoxml");
        oNotaEntrada.aplicaIcmsIpi = rst.getBoolean("aplicaicmsipi");

        sql = new StringBuilder();
        sql.append("SELECT nei.*, produto.descricaocompleta AS produto, produto.id_tipoembalagem, aliquota.descricao AS aliquota,");
        sql.append(" produto.ncm1, produto.ncm2, produto.ncm3, produto.excecao, COALESCE(pf.codigoexterno, '') AS codigoexterno, produto.verificacustotabela,");
        sql.append(" produto.perda, nei.id_tipopiscofins, produto.id_familiaproduto, te.descricao AS tipoembalagem, nei.cfop,");
        sql.append(" COALESCE((SELECT codigobarras FROM produtoautomacao WHERE id_produto = nei.id_produto AND LENGTH(codigobarras::varchar) = 14 LIMIT 1), 0) AS codigocaixa,");
        sql.append(" COALESCE((SELECT codigobarras FROM produtoautomacao WHERE id_produto = nei.id_produto AND LENGTH(codigobarras::varchar) <= 13 LIMIT 1), 0) AS codigobarras");
        sql.append(" FROM notaentradaitem nei");
        sql.append(" INNER JOIN produto ON nei.id_produto = produto.id");
        sql.append(" INNER JOIN aliquota ON aliquota.id = nei.id_aliquota");
        sql.append(" INNER JOIN tipoembalagem te ON te.id = produto.id_tipoembalagem");
        sql.append(" LEFT JOIN produtofornecedor pf ON pf.id_produto = nei.id_produto AND pf.id_fornecedor = " + oNotaEntrada.idFornecedor);
        sql.append(" AND pf.id_estado = " + Global.idEstado);
        sql.append(" WHERE nei.id_notaentrada = " + i_id);
        sql.append(" ORDER BY nei.id");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaEntradaItemVO oItem = new NotaEntradaItemVO();
            oItem.id = rst.getLong("id");
            oItem.idProduto = rst.getInt("id_produto");
            oItem.quantidade = rst.getDouble("quantidade");
            oItem.qtdEmbalagem = rst.getInt("qtdembalagem");
            oItem.valor = rst.getDouble("valor");
            oItem.valorEmbalagem = rst.getDouble("valorembalagem");
            oItem.valorTotal = rst.getDouble("valortotal");
            oItem.valorIpi = rst.getDouble("valoripi");
            oItem.idAliquota = rst.getInt("id_aliquota");
            oItem.valorTotalFinal = rst.getDouble("valortotalfinal");
            oItem.produto = rst.getString("produto");
            oItem.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
            oItem.tipoEmbalagem = rst.getString("tipoembalagem");
            oItem.aliquota = rst.getString("aliquota");
            oItem.ncm1 = rst.getObject("ncm1") == null ? -1 : rst.getInt("ncm1");
            oItem.ncm2 = rst.getObject("ncm2") == null ? -1 : rst.getInt("ncm2");
            oItem.ncm3 = rst.getObject("ncm3") == null ? -1 : rst.getInt("ncm3");
            oItem.excecao = rst.getObject("excecao") == null ? -1 : rst.getInt("excecao");
            oItem.codigoExterno = rst.getString("codigoexterno");
            oItem.verificaCustoTabela = rst.getBoolean("verificacustotabela");
            oItem.valorBaseCalculo = rst.getDouble("valorbasecalculo");
            oItem.valorIcms = rst.getDouble("valoricms");
            oItem.valorBaseSubstituicao = rst.getDouble("valorbasesubstituicao");
            oItem.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
            oItem.valorIcmsSubstituicaoXml = rst.getDouble("valoricmssubstituicaoxml");
            oItem.custoComImpostoAnterior = rst.getDouble("custocomimpostoanterior");
            oItem.valorBonificacao = rst.getDouble("valorbonificacao");
            oItem.valorVerba = rst.getDouble("valorverba");
            oItem.quantidadeDevolvida = rst.getDouble("quantidadedevolvida");
            oItem.percentualPerda = rst.getDouble("perda");
            oItem.idTipoPisCofins = rst.getInt("id_tipopiscofins");
            oItem.valorPisCofins = rst.getDouble("valorpiscofins");
            oItem.contabilizaValor = rst.getBoolean("contabilizavalor");
            oItem.idFamiliaProduto = rst.getInt("id_familiaproduto");
            oItem.codigoBarras = rst.getLong("codigobarras");
            oItem.codigoCaixa = rst.getLong("codigocaixa");
            oItem.cfop = rst.getString("cfop") == null ? "" : rst.getString("cfop");
            oItem.valorIsento = rst.getDouble("valorisento");
            oItem.valorOutras = rst.getDouble("valoroutras");
            oItem.situacaoTributaria = rst.getInt("situacaotributaria");
            oItem.valorFrete = rst.getDouble("valorfrete");
            oItem.valorOutrasDespesas = rst.getDouble("valoroutrasdespesas");
            oItem.valorDesconto = rst.getDouble("valordesconto");
            oItem.idAliquotaCreditoForaEstado = rst.getInt("id_aliquotacreditoforaestado");
            oItem.idTipoEntrada = rst.getInt("id_tipoentrada");

            oNotaEntrada.vItem.add(oItem);
        }

        for (NotaEntradaItemVO oItem : oNotaEntrada.vItem) {
            sql = new StringBuilder();
            sql.append("SELECT neid.*, produto.descricaocompleta AS produto, produto.id_tipoembalagem");
            sql.append(" FROM notaentradaitemdesmembramento neid");
            sql.append(" INNER JOIN produto ON produto.id = neid.id_produto");
            sql.append(" WHERE neid.id_notaentradaitem = " + oItem.id);

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                NotaEntradaItemDesmembramentoVO oDesmembramento = new NotaEntradaItemDesmembramentoVO();
                oDesmembramento.idProduto = rst.getInt("id_produto");
                oDesmembramento.produto = rst.getString("produto");
                oDesmembramento.percentualEstoque = rst.getDouble("percentualestoque");
                oDesmembramento.percentualPerda = rst.getDouble("percentualperda");
                oDesmembramento.percentualDesossa = rst.getDouble("percentualdesossa");
                oDesmembramento.percentualCusto = rst.getDouble("percentualcusto");
                oDesmembramento.quantidade = rst.getDouble("quantidade");
                oDesmembramento.qtdEmbalagem = rst.getInt("qtdembalagem");
                oDesmembramento.valor = rst.getDouble("valor");
                oDesmembramento.valorTotal = rst.getDouble("valortotal");
                oDesmembramento.valorIpi = rst.getDouble("valoripi");
                oDesmembramento.idAliquota = rst.getInt("id_aliquota");
                oDesmembramento.valorBaseCalculo = rst.getDouble("valorbasecalculo");
                oDesmembramento.valorIcms = rst.getDouble("valoricms");
                oDesmembramento.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
                oDesmembramento.valorPisCofins = rst.getDouble("valorpiscofins");
                oDesmembramento.contabilizaValor = true;
                oDesmembramento.valorBaseSubstituicao = rst.getDouble("valorbasesubstituicao");
                oDesmembramento.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                oDesmembramento.idTipoPisCofins = oItem.idTipoPisCofins;

                oItem.vDesmembramento.add(oDesmembramento);
            }
        }

        sql = new StringBuilder();
        sql.append("SELECT * FROM notaentradavencimento WHERE id_notaentrada = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaEntradaVencimentoVO oVencimento = new NotaEntradaVencimentoVO();
            oVencimento.dataVencimento = Util.formatDataGUI(rst.getDate("datavencimento"));
            oVencimento.valor = rst.getDouble("valor");

            oNotaEntrada.vVencimento.add(oVencimento);
        }

        sql = new StringBuilder();
        sql.append("SELECT * FROM notaentradapedido WHERE id_notaentrada = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaEntradaPedidoVO oPedido = new NotaEntradaPedidoVO();
            oPedido.idPedido = rst.getInt("id_pedido");

            oNotaEntrada.vPedido.add(oPedido);
        }

        sql = new StringBuilder();
        sql.append("SELECT neb.id_notaentradaitem, nei.id_produto, p.descricaocompleta AS produto, neb.valor AS valorutilizado,");
        sql.append(" (nei.valortotal - COALESCE((SELECT SUM(nebex.valor) FROM notaentradabonificacao nebex WHERE nebex.id_notaentradaitem = neb.id_notaentradaitem AND nebex.id <> neb.id), 0)) AS valortotal");
        sql.append(" FROM notaentradabonificacao neb");
        sql.append(" INNER JOIN notaentradaitem nei ON nei.id = neb.id_notaentradaitem");
        sql.append(" INNER JOIN produto p ON p.id = nei.id_produto");
        sql.append(" WHERE neb.id_notaentrada = " + i_id);
        sql.append(" ORDER BY neb.id");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaEntradaBonificacaoVO oBonificacao = new NotaEntradaBonificacaoVO();
            oBonificacao.idNotaEntrada = i_id;
            oBonificacao.idNotaEntradaItem = rst.getLong("id_notaentradaitem");
            oBonificacao.idProduto = rst.getInt("id_produto");
            oBonificacao.produto = rst.getString("produto");
            oBonificacao.valorTotal = rst.getDouble("valortotal");
            oBonificacao.valorUtilizado = rst.getDouble("valorutilizado");

            oNotaEntrada.vBonificacao.add(oBonificacao);
        }

        stm.close();

        return oNotaEntrada;
    }

    public NotaEntradaItemVO carregarProduto(int i_idProduto, int i_idFornecedor, int i_idEstado, int i_idLoja, int i_idTipoEntrada) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        boolean utilizaCustoMedio = new ParametroDAO().get(217).getBoolean();

        stm = Conexao.createStatement();

        //carregar produto
        sql = new StringBuilder();
        //sql.append("SELECT produto.id_tipoembalagem, produto.ncm1, produto.ncm2, produto.ncm3, produto.excecao, produto.qtdembalagem, COALESCE(pf.qtdembalagem, 0) AS qtdembalagemfornecedor,");
        sql.append("SELECT produto.id_tipoembalagem, produto.ncm1, produto.ncm2, produto.ncm3, produto.qtdembalagem, COALESCE(pf.qtdembalagem, 0) AS qtdembalagemfornecedor,");
        sql.append(" COALESCE(pf.codigoexterno, '') AS codigoexterno, produto.verificacustotabela, produto.perda,");

        if (utilizaCustoMedio) {
            sql.append(" complemento.customediocomimposto AS custocomimposto,");
        } else {
            sql.append(" complemento.custocomimposto,");
        }

        sql.append(" produto.id_tipopiscofinscredito, produto.id_familiaproduto, produto.id AS id_produto, produto.descricaocompleta AS produto,");
        sql.append(" COALESCE((SELECT codigobarras FROM produtoautomacao WHERE id_produto = produto.id AND LENGTH(codigobarras::varchar) = 14 LIMIT 1), 0) AS codigocaixa,");
        sql.append(" COALESCE((SELECT codigobarras FROM produtoautomacao WHERE id_produto = produto.id AND LENGTH(codigobarras::varchar) <= 13 LIMIT 1), 0) AS codigobarras,");
        sql.append(" te.descricao AS tipoembalagem");
        sql.append(" FROM produto");
        sql.append(" LEFT JOIN produtofornecedor pf ON pf.id_produto = produto.id AND pf.id_fornecedor = " + i_idFornecedor + " AND pf.id_estado = " + i_idEstado);
        sql.append(" INNER JOIN tipoembalagem te ON te.id = produto.id_tipoembalagem");
        sql.append(" INNER JOIN produtocomplemento AS complemento ON complemento.id_produto = produto.id AND complemento.id_loja = " + i_idLoja);
        sql.append(" WHERE produto.id = " + i_idProduto);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Produto " + i_idProduto + " não encontrado!");
        }

        NotaEntradaItemVO oItem = new NotaEntradaItemVO();
        oItem.idProduto = rst.getInt("id_produto");
        oItem.produto = rst.getString("produto");
        oItem.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
        oItem.tipoEmbalagem = rst.getString("tipoembalagem");
        oItem.ncm1 = rst.getObject("ncm1") == null ? -1 : rst.getInt("ncm1");
        oItem.ncm2 = rst.getObject("ncm2") == null ? -1 : rst.getInt("ncm2");
        oItem.ncm3 = rst.getObject("ncm3") == null ? -1 : rst.getInt("ncm3");
        //oItem.excecao = rst.getObject("excecao") == null ? -1 : rst.getInt("excecao");
        oItem.codigoExterno = rst.getString("codigoexterno");

        if (new ParametroDAO().get(24).getBoolean()) { //utiliza embalgem fornecedor
            if (rst.getInt("qtdembalagemfornecedor") == 0) {
                oItem.qtdEmbalagem = rst.getInt("qtdembalagem");
            } else {
                oItem.qtdEmbalagem = rst.getInt("qtdembalagemfornecedor");
            }
        } else {
            oItem.qtdEmbalagem = rst.getInt("qtdembalagem");
        }

        oItem.verificaCustoTabela = rst.getBoolean("verificacustotabela");
        oItem.custoComImpostoAnterior = rst.getDouble("custocomimposto");
        oItem.percentualPerda = rst.getDouble("perda");
        oItem.idFamiliaProduto = rst.getInt("id_familiaproduto");
        oItem.codigoBarras = rst.getLong("codigobarras");
        oItem.codigoCaixa = rst.getLong("codigocaixa");
        oItem.idTipoEntrada = i_idTipoEntrada;

        //verifica pis/cofins
        if (i_idTipoEntrada != -1) {
            TipoEntradaVO oTipoEntrada = new TipoEntradaDAO().carregar(i_idTipoEntrada);

            if (oTipoEntrada.bonificacao) {
                int idTipoPisCofinsBonificacao = new TipoPisCofinsDAO().getId(98);

                if (idTipoPisCofinsBonificacao == -1) {
                    oItem.idTipoPisCofins = rst.getInt("id_tipopiscofinscredito");
                } else {
                    oItem.idTipoPisCofins = idTipoPisCofinsBonificacao;
                }
            } else {
                oItem.idTipoPisCofins = rst.getInt("id_tipopiscofinscredito");
            }
        } else {
            oItem.idTipoPisCofins = rst.getInt("id_tipopiscofinscredito");
        }

        //verifica aliquota credito
        sql = new StringBuilder();
        sql.append("SELECT id_tipoempresa FROM fornecedor WHERE id = " + i_idFornecedor);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Fornecedor não encontrado!");
        }

        int idTipoEmpresa = rst.getInt("id_tipoempresa");

        rst.close();

        sql = new StringBuilder();
        sql.append("SELECT id_aliquotacredito, id_aliquotadebitoforaestado, id_aliquotacreditoforaestado");
        sql.append(" FROM produtoaliquota");
        sql.append(" WHERE id_produto = " + i_idProduto);
        sql.append(" AND id_estado = " + i_idEstado);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Alíquota não encontrada!");
        }

        if (i_idEstado != Global.idEstado) {
            oItem.idAliquota = rst.getInt("id_aliquotadebitoforaestado");
        } else {
            oItem.idAliquota = rst.getInt("id_aliquotacredito");
        }

        oItem.idAliquotaCreditoForaEstado = rst.getInt("id_aliquotacreditoforaestado");

        NotaFiscalAliquotaVO oAliquota = new NotaFiscalAliquotaDAO().carregar(oItem.idAliquota);

        if (!new AliquotaDAO().isSubstituido(oAliquota.situacaoTributaria)) {
            TipoEmpresaVO oTipoEmpresa = new TipoEmpresaDAO().carregar(idTipoEmpresa);

            if (oTipoEmpresa.idTipoCrt == TipoCrt.SIMPLES_NACIONAL.getId()) {
                oItem.idAliquota = new AliquotaDAO().getIdOutras();
                oItem.idAliquotaCreditoForaEstado = new AliquotaDAO().getIdOutras();
            }
        }

        //carregar desmembramento
        sql = new StringBuilder();
        sql.append("SELECT di.id_produto, produto.descricaocompleta AS produto, di.percentualdesossa, di.percentualcusto, di.percentualestoque,");
        sql.append(" di.percentualperda, produto.id_tipoembalagem");
        sql.append(" FROM desmembramento AS d");
        sql.append(" INNER JOIN desmembramentoitem AS di ON di.id_desmembramento = d.id");
        sql.append(" INNER JOIN produto ON di.id_produto = produto.id");
        sql.append(" INNER JOIN produtocomplemento AS pc ON di.id_produto = pc.id_produto AND pc.id_loja = " + i_idLoja);
        sql.append(" WHERE d.id_produto = " + i_idProduto);
        sql.append(" AND d.id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            NotaEntradaItemDesmembramentoVO oDesmembramento = new NotaEntradaItemDesmembramentoVO();
            oDesmembramento.idProduto = rst.getInt("id_produto");
            oDesmembramento.produto = rst.getString("produto");
            oDesmembramento.percentualCusto = rst.getDouble("percentualcusto");
            oDesmembramento.percentualDesossa = rst.getDouble("percentualdesossa");
            oDesmembramento.percentualEstoque = rst.getDouble("percentualestoque");
            oDesmembramento.percentualPerda = rst.getDouble("percentualperda");
            oDesmembramento.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
            oDesmembramento.idTipoPisCofins = oItem.idTipoPisCofins;

            oItem.vDesmembramento.add(oDesmembramento);
        }

        stm.close();

        return oItem;
    }

    public void salvar(NotaEntradaVO i_notaEntrada) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //verifica status
            rst = stm.executeQuery("SELECT id_situacaonotaentrada FROM notaentrada WHERE id = " + i_notaEntrada.id + " FOR UPDATE");

            if (rst.next() && rst.getInt("id_situacaonotaentrada") == SituacaoNotaEntrada.FINALIZADO.getId()) {
                throw new VRException("Esta nota já está finalizada!");
            }

            //salva nota
            sql = new StringBuilder();
            sql.append("SELECT id FROM notaentrada WHERE id = " + i_notaEntrada.id);

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                sql = new StringBuilder();
                sql.append("UPDATE notaentrada SET");
                sql.append(" numeronota = " + i_notaEntrada.numeroNota + ",");
                sql.append(" id_fornecedor = " + i_notaEntrada.idFornecedor + ",");
                sql.append(" dataentrada = '" + Util.formatDataBanco(i_notaEntrada.dataEntrada) + "',");
                sql.append(" id_tipoentrada = " + i_notaEntrada.idTipoEntrada + ",");
                sql.append(" dataemissao = '" + Util.formatDataBanco(i_notaEntrada.dataEmissao) + "',");
                sql.append(" datahoralancamento = '" + Util.formatDataHoraBanco(i_notaEntrada.dataHoraLancamento) + "',");
                sql.append(" valoripi = " + i_notaEntrada.valorIpi + ",");
                sql.append(" valorfrete = " + i_notaEntrada.valorFrete + ",");
                sql.append(" valordesconto = " + i_notaEntrada.valorDesconto + ",");
                sql.append(" valoroutradespesa = " + i_notaEntrada.valorOutrasDespesas + ",");
                sql.append(" valordespesaadicional = " + i_notaEntrada.valorDespesaAdicional + ",");
                sql.append(" valormercadoria = " + i_notaEntrada.valorMercadoria + ",");
                sql.append(" valortotal = " + i_notaEntrada.valorTotal + ",");
                sql.append(" valoricms = " + i_notaEntrada.valorIcms + ",");
                sql.append(" valoricmssubstituicao = " + i_notaEntrada.valorIcmsSubstituicao + ",");
                sql.append(" id_usuario = " + i_notaEntrada.idUsuario + ",");
                sql.append(" impressao = " + i_notaEntrada.impressao + ",");
                sql.append(" produtorrural = " + i_notaEntrada.produtorRural + ",");
                sql.append(" aplicaaliquota = " + i_notaEntrada.aplicaAliquota + ",");
                sql.append(" aplicacustodesconto = " + i_notaEntrada.aplicaCustoDesconto + ",");
                sql.append(" aplicaicmsdesconto = " + i_notaEntrada.aplicaIcmsDesconto + ",");
                sql.append(" aplicacustoencargo = " + i_notaEntrada.aplicaCustoEncargo + ",");
                sql.append(" aplicaicmsencargo = " + i_notaEntrada.aplicaIcmsEncargo + ",");
                sql.append(" aplicadespesaadicional = " + i_notaEntrada.aplicaDespesaAdicional + ",");
                sql.append(" id_situacaonotaentrada = " + i_notaEntrada.idSituacaoNotaEntrada + ",");
                sql.append(" chavenfe = '" + i_notaEntrada.chaveNfe + "',");
                sql.append(" id_tipofretenotafiscal = " + i_notaEntrada.idTipoFreteNotaFiscal + ",");
                sql.append(" serie = '" + i_notaEntrada.serie + "',");
                sql.append(" modelo = '" + i_notaEntrada.modelo + "',");
                sql.append(" valorguiasubstituicao = " + i_notaEntrada.valorGuiaSubstituicao + ",");
                sql.append(" valorbasecalculo = " + i_notaEntrada.valorBaseCalculo + ",");
                sql.append(" valorbasesubstituicao = " + i_notaEntrada.valorBaseSubstituicao + ",");
                sql.append(" valorfunrural = " + i_notaEntrada.valorFunRural + ",");
                sql.append(" valordescontoboleto = " + i_notaEntrada.valorDescontoBoleto + ",");
                sql.append(" conferido = " + i_notaEntrada.conferido + ",");
                sql.append(" observacao = '" + i_notaEntrada.observacao + "',");
                sql.append(" id_notasaida = " + (i_notaEntrada.idNotaSaida == -1 ? "NULL" : i_notaEntrada.idNotaSaida) + ",");
                sql.append(" id_tiponota = " + i_notaEntrada.idTipoNota + ",");
                sql.append(" liberadopedido = " + i_notaEntrada.liberadoPedido + ",");
                sql.append(" importadoxml = " + i_notaEntrada.importadoXml + ",");
                sql.append(" aplicaicmsipi = " + i_notaEntrada.aplicaIcmsIpi);
                sql.append(" WHERE id = " + i_notaEntrada.id);

                stm.execute(sql.toString());

                new LogTransacaoDAO().gerar(Formulario.NOTAFISCAL_ENTRADA, TipoTransacao.ALTERACAO, i_notaEntrada.numeroNota, "", i_notaEntrada.id);

            } else {
                i_notaEntrada.dataHoraLancamento = Util.getDataHoraAtual();

                sql = new StringBuilder();
                sql.append("INSERT INTO notaentrada (id_loja, numeronota, id_fornecedor, dataentrada, id_tipoentrada, dataemissao, datahoralancamento,");
                sql.append(" valoripi, valorfrete, valordesconto, valoroutradespesa, valordespesaadicional, valormercadoria, valortotal, valoricms,");
                sql.append(" valoricmssubstituicao, id_usuario, impressao, produtorrural, aplicaaliquota, aplicacustodesconto, aplicaicmsdesconto,");
                sql.append(" aplicacustoencargo, aplicaicmsencargo, aplicadespesaadicional, id_situacaonotaentrada, chavenfe, serie, modelo,");
                sql.append(" valorguiasubstituicao, valorbasecalculo, valorbasesubstituicao, valorfunrural, valordescontoboleto, conferido,");
                sql.append(" id_tipofretenotafiscal, observacao, id_notasaida, id_tiponota, liberadopedido, importadoxml, aplicaicmsipi) VALUES (");
                sql.append(i_notaEntrada.idLoja + ", ");
                sql.append(i_notaEntrada.numeroNota + ", ");
                sql.append(i_notaEntrada.idFornecedor + ", ");
                sql.append("'" + Util.formatDataBanco(i_notaEntrada.dataEntrada) + "', ");
                sql.append(i_notaEntrada.idTipoEntrada + ", ");
                sql.append("'" + Util.formatDataBanco(i_notaEntrada.dataEmissao) + "', ");
                sql.append("'" + Util.formatDataHoraBanco(i_notaEntrada.dataHoraLancamento) + "', ");
                sql.append(i_notaEntrada.valorIpi + ", ");
                sql.append(i_notaEntrada.valorFrete + ", ");
                sql.append(i_notaEntrada.valorDesconto + ", ");
                sql.append(i_notaEntrada.valorOutrasDespesas + ", ");
                sql.append(i_notaEntrada.valorDespesaAdicional + ", ");
                sql.append(i_notaEntrada.valorMercadoria + ", ");
                sql.append(i_notaEntrada.valorTotal + ", ");
                sql.append(i_notaEntrada.valorIcms + ", ");
                sql.append(i_notaEntrada.valorIcmsSubstituicao + ", ");
                sql.append(i_notaEntrada.idUsuario + ", ");
                sql.append(i_notaEntrada.impressao + ", ");
                sql.append(i_notaEntrada.produtorRural + ", ");
                sql.append(i_notaEntrada.aplicaAliquota + ", ");
                sql.append(i_notaEntrada.aplicaCustoDesconto + ", ");
                sql.append(i_notaEntrada.aplicaIcmsDesconto + ", ");
                sql.append(i_notaEntrada.aplicaCustoEncargo + ", ");
                sql.append(i_notaEntrada.aplicaIcmsEncargo + ", ");
                sql.append(i_notaEntrada.aplicaDespesaAdicional + ", ");
                sql.append(i_notaEntrada.idSituacaoNotaEntrada + ", ");
                sql.append("'" + i_notaEntrada.chaveNfe + "', ");
                sql.append("'" + i_notaEntrada.serie + "', ");
                sql.append("'" + i_notaEntrada.modelo + "', ");
                sql.append(i_notaEntrada.valorGuiaSubstituicao + ", ");
                sql.append(i_notaEntrada.valorBaseCalculo + ", ");
                sql.append(i_notaEntrada.valorBaseSubstituicao + ", ");
                sql.append(i_notaEntrada.valorFunRural + ", ");
                sql.append(i_notaEntrada.valorDescontoBoleto + ", ");
                sql.append(i_notaEntrada.conferido + ", ");
                sql.append(i_notaEntrada.idTipoFreteNotaFiscal + ", ");
                sql.append("'" + i_notaEntrada.observacao + "', ");
                sql.append((i_notaEntrada.idNotaSaida == -1 ? "NULL" : i_notaEntrada.idNotaSaida) + ", ");
                sql.append(i_notaEntrada.idTipoNota + ", ");
                sql.append(i_notaEntrada.liberadoPedido + ", ");
                sql.append(i_notaEntrada.importadoXml + ", ");
                sql.append(i_notaEntrada.aplicaIcmsIpi + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('notaentrada_id_seq') AS id");
                rst.next();

                i_notaEntrada.id = rst.getLong("id");

                new LogTransacaoDAO().gerar(Formulario.NOTAFISCAL_ENTRADA, TipoTransacao.INCLUSAO, i_notaEntrada.numeroNota, "", i_notaEntrada.id);
            }

            stm.execute("DELETE FROM notaentradabonificacao WHERE id_notaentrada = " + i_notaEntrada.id);
            stm.execute("DELETE FROM notaentradaitemdesmembramento WHERE id_notaentrada = " + i_notaEntrada.id);
            stm.execute("DELETE FROM notaentradaitem WHERE id_notaentrada = " + i_notaEntrada.id);

            //salva item
            for (NotaEntradaItemVO oItem : i_notaEntrada.vItem) {
                long idNotaEntradaItemAnterior = oItem.id;

                sql = new StringBuilder();
                sql.append("INSERT INTO notaentradaitem (id_notaentrada, id_produto, quantidade, qtdembalagem, valor, valorembalagem, valortotal, valoripi,");
                sql.append(" id_aliquota, custocomimposto, valortotalfinal, valorbasecalculo, valoricms, valoricmssubstituicao, custocomimpostoanterior, valorbonificacao,");
                sql.append(" valorverba, quantidadedevolvida, valorpiscofins, contabilizavalor, valorbasesubstituicao, cfop, valoricmssubstituicaoxml,");
                sql.append(" valorisento, valoroutras, situacaotributaria, valorfrete, valoroutrasdespesas, valordesconto, id_tipopiscofins, id_aliquotacreditoforaestado) VALUES (");
                sql.append(i_notaEntrada.id + ", ");
                sql.append(oItem.idProduto + ", ");
                sql.append(oItem.quantidade + ", ");
                sql.append(oItem.qtdEmbalagem + ", ");
                sql.append(oItem.valor + ", ");
                sql.append(oItem.valorEmbalagem + ", ");
                sql.append(oItem.valorTotal + ", ");
                sql.append(oItem.valorIpi + ", ");
                sql.append(oItem.idAliquota + ", ");
                sql.append(oItem.custoComImposto + ", ");
                sql.append(oItem.valorTotalFinal + ", ");
                sql.append(oItem.valorBaseCalculo + ", ");
                sql.append(oItem.valorIcms + ", ");
                sql.append(oItem.valorIcmsSubstituicao + ", ");
                sql.append(oItem.custoComImpostoAnterior + ", ");
                sql.append(oItem.valorBonificacao + ", ");
                sql.append(oItem.valorVerba + ", ");
                sql.append(oItem.quantidadeDevolvida + ", ");
                sql.append(oItem.valorPisCofins + ", ");
                sql.append(oItem.contabilizaValor + ", ");
                sql.append(oItem.valorBaseSubstituicao + ", ");
                sql.append((oItem.cfop.isEmpty() ? "NULL" : "'" + oItem.cfop + "'") + ", ");
                sql.append(oItem.valorIcmsSubstituicaoXml + ", ");
                sql.append(oItem.valorIsento + ", ");
                sql.append(oItem.valorOutras + ", ");
                sql.append(oItem.situacaoTributaria + ", ");
                sql.append(oItem.valorFrete + ", ");
                sql.append(oItem.valorOutrasDespesas + ", ");
                sql.append(oItem.valorDesconto + ", ");
                sql.append(oItem.idTipoPisCofins + ", ");
                sql.append(oItem.idAliquotaCreditoForaEstado + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('notaentradaitem_id_seq') AS id");
                rst.next();

                oItem.id = rst.getLong("id");

                //acerta bonificacao
                for (NotaEntradaBonificacaoVO oBonificacao : i_notaEntrada.vBonificacao) {
                    if (oBonificacao.idNotaEntrada == i_notaEntrada.id && oBonificacao.idNotaEntradaItem == idNotaEntradaItemAnterior) {
                        oBonificacao.idNotaEntradaItem = oItem.id;
                        break;
                    }
                }

                //altera codigo externo
                if (!oItem.codigoExterno.isEmpty()) {
                    long idProdutoFornecedor = new ProdutoFornecedorDAO().verificar(oItem.idProduto, i_notaEntrada.idFornecedor, i_notaEntrada.idEstado);

                    if (idProdutoFornecedor > 0) {
                        ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorDAO().carregar(idProdutoFornecedor, i_notaEntrada.idLoja);

                        if (!oItem.codigoExterno.equals(oProdutoFornecedor.codigoexterno)) {
                            oProdutoFornecedor.codigoexterno = oItem.codigoExterno;

                            new ProdutoFornecedorDAO().salvar(oProdutoFornecedor);
                        }
                    } else {
                        ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                        oProdutoFornecedor.id_produto = oItem.idProduto;
                        oProdutoFornecedor.id_fornecedor = i_notaEntrada.idFornecedor;
                        oProdutoFornecedor.id_estado = Global.idEstado;
                        oProdutoFornecedor.codigoexterno = oItem.codigoExterno;

                        new ProdutoFornecedorDAO().salvar(oProdutoFornecedor);
                    }
                }

                //salva desmembramento
                sql = new StringBuilder();

                for (NotaEntradaItemDesmembramentoVO oDesmembramento : oItem.vDesmembramento) {
                    sql.append("INSERT INTO notaentradaitemdesmembramento (id_notaentradaitem, id_notaentrada, id_produto, percentualestoque,");
                    sql.append(" percentualperda, percentualdesossa, percentualcusto, quantidade, qtdembalagem, valor, valortotal, valoripi, id_aliquota,");
                    sql.append(" valorbasecalculo, valoricms, valoricmssubstituicao, valorpiscofins, valorbasesubstituicao) VALUES (");
                    sql.append(oItem.id + ", ");
                    sql.append(i_notaEntrada.id + ", ");
                    sql.append(oDesmembramento.idProduto + ", ");
                    sql.append(oDesmembramento.percentualEstoque + ", ");
                    sql.append(oDesmembramento.percentualPerda + ", ");
                    sql.append(oDesmembramento.percentualDesossa + ", ");
                    sql.append(oDesmembramento.percentualCusto + ", ");
                    sql.append(oDesmembramento.quantidade + ", ");
                    sql.append(oDesmembramento.qtdEmbalagem + ", ");
                    sql.append(oDesmembramento.valor + ", ");
                    sql.append(oDesmembramento.valorTotal + ", ");
                    sql.append(oDesmembramento.valorIpi + ", ");
                    sql.append(oDesmembramento.idAliquota + ", ");
                    sql.append(oDesmembramento.valorBaseCalculo + ", ");
                    sql.append(oDesmembramento.valorIcms + ", ");
                    sql.append(oDesmembramento.valorIcmsSubstituicao + ", ");
                    sql.append(oDesmembramento.valorPisCofins + ", ");
                    sql.append(oDesmembramento.valorBaseSubstituicao + ");");
                }

                stm.execute(sql.toString());
            }

            //salva vencimento
            sql = new StringBuilder();
            sql.append("DELETE FROM notaentradavencimento WHERE id_notaentrada = " + i_notaEntrada.id + ";");

            for (NotaEntradaVencimentoVO oVencimento : i_notaEntrada.vVencimento) {
                sql.append("INSERT INTO notaentradavencimento (id_notaentrada, datavencimento, valor) VALUES (");
                sql.append(i_notaEntrada.id + ",");
                sql.append("'" + Util.formatDataBanco(oVencimento.dataVencimento) + "',");
                sql.append(oVencimento.valor + ");");
            }

            stm.execute(sql.toString());

            //salva pedido
            sql = new StringBuilder();
            sql.append("DELETE FROM notaentradapedido WHERE id_notaentrada = " + i_notaEntrada.id + ";");

            for (NotaEntradaPedidoVO oPedido : i_notaEntrada.vPedido) {
                sql.append("INSERT INTO notaentradapedido (id_notaentrada, id_pedido) VALUES (");
                sql.append(i_notaEntrada.id + ", ");
                sql.append(oPedido.idPedido + ");");
            }

            stm.execute(sql.toString());

            //salva bonificacao
            sql = new StringBuilder();
            sql.append("DELETE FROM notaentradabonificacao WHERE id_notaentrada = " + i_notaEntrada.id + ";");

            for (NotaEntradaBonificacaoVO oBonificacao : i_notaEntrada.vBonificacao) {
                sql.append("INSERT INTO notaentradabonificacao(id_notaentrada, id_notaentradaitem, valor) VALUES (");
                sql.append(i_notaEntrada.id + ", ");
                sql.append(oBonificacao.idNotaEntradaItem + ", ");
                sql.append(oBonificacao.valorUtilizado + ");");
            }

            stm.execute(sql.toString());

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
