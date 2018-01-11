package vrimplantacao.dao.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.gui.cadastro.FornecedorPagamentoVO;
import vrimplantacao.vo.cadastro.FornecedorContatoVO;
import vrimplantacao.vo.cadastro.FornecedorPrazoVO;
import vrimplantacao.vo.notafiscal.NotaFiscalFornecedorVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class NotaFiscalFornecedorDAO {

    public NotaFiscalFornecedorVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT f.*, municipio.descricao AS municipio, estado.sigla AS estado, mcobranca.descricao AS municipiocobranca, ecobranca.sigla AS estadocobranca");
        sql.append(" FROM fornecedor AS f");
        sql.append(" INNER JOIN municipio ON municipio.id = f.id_municipio");
        sql.append(" INNER JOIN estado ON estado.id = municipio.id_estado");
        sql.append(" LEFT JOIN municipio AS mcobranca ON mcobranca.id = f.id_municipiocobranca");
        sql.append(" LEFT JOIN estado AS ecobranca ON ecobranca.id = mcobranca.id_estado");
        sql.append(" WHERE f.id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Fornecedor não encontrado!");
        }

        NotaFiscalFornecedorVO oFornecedor = new NotaFiscalFornecedorVO();
        oFornecedor.agencia = rst.getString("agencia");
        oFornecedor.bairro = rst.getString("bairro");
        oFornecedor.bairroCobranca = rst.getString("bairrocobranca");
        oFornecedor.idBanco = rst.getInt("id_banco");
        oFornecedor.bloqueado = rst.getBoolean("bloqueado");
        oFornecedor.cep = rst.getInt("cep");
        oFornecedor.cepCobranca = rst.getInt("cepcobranca");
        oFornecedor.cnpj = rst.getLong("cnpj");
        oFornecedor.conta = rst.getString("conta");
        oFornecedor.dataSintegra = rst.getString("datasintegra") == null ? "" : Util.formatDataGUI(rst.getDate("datasintegra"));
        oFornecedor.descontoFunRural = rst.getBoolean("descontofunrural");
        oFornecedor.digitoAgencia = rst.getString("digitoagencia");
        oFornecedor.digitoConta = rst.getString("digitoconta");
        oFornecedor.endereco = rst.getString("endereco");
        oFornecedor.numero = rst.getString("numero");
        oFornecedor.enderecoCobranca = rst.getString("enderecocobranca");
        oFornecedor.id = rst.getInt("id");
        oFornecedor.idBanco = rst.getString("id_banco") == null ? -1 : rst.getInt("id_banco");
        oFornecedor.idEstado = rst.getInt("id_estado");
        oFornecedor.estado = rst.getString("estado");
        oFornecedor.idEstadoCobranca = rst.getString("id_estadocobranca") == null ? -1 : rst.getInt("id_estadocobranca");
        oFornecedor.estadoCobranca = rst.getString("estadocobranca") == null ? "" : rst.getString("estadocobranca");
        oFornecedor.idFamiliaFornecedor = rst.getString("id_familiafornecedor") == null ? -1 : rst.getInt("id_familiafornecedor");
        oFornecedor.idFornecedorFavorecido = rst.getString("id_fornecedorfavorecido") == null ? -1 : rst.getInt("id_fornecedorfavorecido");
        oFornecedor.idMunicipio = rst.getInt("id_municipio");
        oFornecedor.municipio = rst.getString("municipio");
        oFornecedor.idMunicipioCobranca = rst.getString("id_municipiocobranca") == null ? -1 : rst.getInt("id_municipiocobranca");
        oFornecedor.municipioCobranca = rst.getString("municipiocobranca") == null ? "" : rst.getString("municipiocobranca");
        oFornecedor.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
        oFornecedor.idTipoEmpresa = rst.getInt("id_tipoempresa");
        oFornecedor.idTipoFornecedor = rst.getInt("id_tipofornecedor");
        oFornecedor.idTipoInscricao = rst.getInt("id_tipoinscricao");
        oFornecedor.idTipoMotivoFornecedor = rst.getString("id_tipomotivofornecedor") == null ? -1 : rst.getInt("id_tipomotivofornecedor");
        oFornecedor.idTipoPagamento = rst.getInt("id_tipopagamento");
        oFornecedor.idTipoRecebimento = rst.getString("id_tiporecebimento") == null ? -1 : rst.getInt("id_tiporecebimento");
        oFornecedor.inscricaoEstadual = rst.getString("inscricaoestadual");
        oFornecedor.inscricaoSuframa = rst.getString("inscricaosuframa");
        oFornecedor.nomeFantasia = rst.getString("nomefantasia");
        oFornecedor.numeroDoc = rst.getInt("numerodoc");
        oFornecedor.pedidoMinimoQtd = rst.getInt("pedidominimoqtd");
        oFornecedor.pedidoMinimoValor = rst.getDouble("pedidominimovalor");
        oFornecedor.razaoSocial = rst.getString("razaosocial");
        oFornecedor.revenda = rst.getBoolean("revenda");
        oFornecedor.serieNf = rst.getString("serienf");
        oFornecedor.telefone = rst.getString("telefone");
        oFornecedor.modeloNf = rst.getString("modelonf");
        oFornecedor.utilizaIva = rst.getBoolean("utilizaiva");
        oFornecedor.senha = rst.getInt("senha");
        oFornecedor.idTipoInspecao = rst.getObject("id_tipoinspecao") == null ? -1 : rst.getInt("id_tipoinspecao");
        oFornecedor.numeroInspecao = rst.getInt("numeroinspecao");
        oFornecedor.idTipoTroca = rst.getObject("id_tipotroca") == null ? -1 : rst.getInt("id_tipotroca");
        oFornecedor.idContaContabilFinanceiro = rst.getObject("id_contacontabilfinanceiro") == null ? -1 : rst.getInt("id_contacontabilfinanceiro");
        oFornecedor.utilizaNfe = rst.getBoolean("utilizanfe");
        oFornecedor.utilizaConferencia = rst.getBoolean("utilizaconferencia");
        oFornecedor.permiteNfSemPedido = rst.getBoolean("permitenfsempedido");
        oFornecedor.emiteNf = rst.getBoolean("emitenf");
        oFornecedor.tipoNegociacao = rst.getInt("tiponegociacao");
        oFornecedor.utilizaCrossDocking = rst.getBoolean("utilizacrossdocking");
        oFornecedor.idLojaCrossDocking = rst.getObject("id_lojacrossdocking") == null ? -1 : rst.getInt("id_lojacrossdocking");

        sql = new StringBuilder();
        sql.append("SELECT contato.id_tipocontato, contato.nome, contato.telefone, contato.celular, tipocontato.descricao AS tipocontato,");
        sql.append(" contato.email");
        sql.append(" FROM fornecedorcontato AS contato");
        sql.append(" INNER JOIN tipocontato ON tipocontato.id = contato.id_tipocontato");
        sql.append(" WHERE contato.id_fornecedor = " + oFornecedor.id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            FornecedorContatoVO oContato = new FornecedorContatoVO();
            oContato.idTipoContato = rst.getInt("id_tipocontato");
            oContato.tipoContato = rst.getString("tipocontato");
            oContato.nome = rst.getString("nome");
            oContato.telefone = rst.getString("telefone");
            oContato.celular = rst.getString("celular");
            oContato.email = rst.getString("email");

            oFornecedor.vContato.add(oContato);
        }

        sql = new StringBuilder();
        sql.append("SELECT fp.id_loja, lj.descricao AS loja, fp.id_divisaofornecedor, df.descricao AS divisaofornecedor,");
        sql.append(" fp.prazoentrega, fp.prazovisita, fp.prazoseguranca");
        sql.append(" FROM fornecedorprazo AS fp");
        sql.append(" INNER JOIN loja AS lj ON lj.id = fp.id_loja");
        sql.append(" INNER JOIN divisaofornecedor AS df ON df.id = fp.id_divisaofornecedor");
        sql.append(" WHERE fp.id_fornecedor = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            FornecedorPrazoVO oPrazo = new FornecedorPrazoVO();
            oPrazo.idLoja = rst.getInt("id_loja");
            oPrazo.idDivisaoFornecedor = rst.getInt("id_divisaofornecedor");
            oPrazo.loja = rst.getString("loja");
            oPrazo.divisaoFornecedor = rst.getString("divisaofornecedor");
            oPrazo.prazoEntrega = rst.getInt("prazoentrega");
            oPrazo.prazoVisita = rst.getInt("prazovisita");
            oPrazo.prazoSeguranca = rst.getInt("prazoseguranca");

            oFornecedor.vPrazo.add(oPrazo);
        }

        sql = new StringBuilder();
        sql.append("SELECT fp.vencimento");
        sql.append(" FROM fornecedorpagamento AS fp");
        sql.append(" WHERE fp.id_fornecedor = " + oFornecedor.id);
        sql.append(" ORDER BY fp.vencimento");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            FornecedorPagamentoVO oPagamento = new FornecedorPagamentoVO();
            oPagamento.vencimento = rst.getInt("vencimento");

            oFornecedor.vPagamento.add(oPagamento);
        }

        stm.close();

        return oFornecedor;
    }
}
