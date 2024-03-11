/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.pdv.ecf.EcfPdvVO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.AcumuladorIMP;
import vrimplantacao2.vo.importacao.AcumuladorLayoutIMP;
import vrimplantacao2.vo.importacao.AcumuladorLayoutRetornoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OperadorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.PromocaoIMP;

/**
 *
 * @author lucasrafael
 */
public class PdvVrDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "PdvVr";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id_loja, "
                    + "razaosocial "
                    + "from informacao"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id_loja"), rst.getString("razaosocial")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id,\n"
                    + "descricao,\n"
                    + "situacaotributaria cst,\n"
                    + "porcentagem aliq,\n"
                    + "0 reducao\n"
                    + "from aliquota\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.id,\n"
                    + "    pa.codigobarras,\n"
                    + "    pa.qtdembalagem,\n"
                    + "    p.precovenda,\n"
                    + "    p.descricaocompleta,\n"
                    + "    p.descricaoreduzida,\n"
                    + "    p.precovenda,\n"
                    + "    p.id_aliquota,\n"
                    + "    p.id_situacaocadastro,\n"
                    + "    p.aceitamultiplicacaopdv,\n"
                    + "    p.tipoembalagem,\n"
                    + "    p.ncm,\n"
                    + "    p.id_tipopiscofins,\n"
                    + "    p.cest,\n"
                    + "    a.situacaotributaria cstIcms,\n"
                    + "    a.porcentagem,\n"
                    + "    pis.cst cstPis,\n"
                    + "    p.id_aliquota\n"
                    + "from produto p\n"
                    + "    left join produtoautomacao pa on pa.id_produto = p.id\n"
                    + "    left join aliquota a on a.id_aliquota = p.id_aliquota\n"
                    + "    left join tipopiscofins pis on pis.id = p.id_tipopiscofins\n"
                    + "order by p.id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstPis"));
                    imp.setPiscofinsCstCredito(rst.getString("cstPis"));
                    imp.setIcmsDebitoId(rst.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_aliquota"));
                    imp.setIcmsCreditoId(rst.getString("id_aliquota"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_aliquota"));
                    imp.setIcmsConsumidorId(rst.getString("id_aliquota"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<EcfPdvVO> getECF() throws SQLException {
        List<EcfPdvVO> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	e.id,\n"
                    + "	e.ecf ecf,\n"
                    + "	e.ID_SITUACAOCADASTRO ativo\n"
                    + "FROM\n"
                    + "	ECF e "
            )) {
                while (rst.next()) {
                    EcfPdvVO imp = new EcfPdvVO();
                    imp.setId(rst.getInt("id"));
                    imp.setId_loja(Integer.parseInt(getLojaOrigem()));
                    imp.setEcf(rst.getInt("ecf"));
                    imp.setId_situacaocadastro(rst.getInt("ativo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OperadorIMP> getOperadores() throws Exception {
        List<OperadorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "matricula,\n"
                    + "nome,\n"
                    + "senha,\n"
                    + "id_tiponiveloperador,\n"
                    + "id_situacaocadastro\n"
                    + "from operador\n"
                    + "order by matricula"
            )) {
                while (rst.next()) {
                    OperadorIMP imp = new OperadorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportarMatricula(rst.getString("matricula"));
                    imp.setNome(rst.getString("nome"));
                    imp.setSenha(rst.getString("senha"));
                    imp.setId_tiponiveloperador(rst.getString("id_tiponiveloperador"));
                    imp.setId_situacadastro(rst.getString("id_situacaocadastro"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PromocaoIMP> getPromocoes() throws Exception {
        List<PromocaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id,\n"
                    + "descricao,\n"
                    + "datainicio,\n"
                    + "datatermino,\n"
                    + "pontuacao,\n"
                    + "quantidade,\n"
                    + "qtdcupom,\n"
                    + "id_situacaocadastro,\n"
                    + "verificaprodutosauditados,\n"
                    + "id_tipopromocao,\n"
                    + "valor,\n"
                    + "controle,\n"
                    + "id_tipopercentualvalor,\n"
                    + "id_tipoquantidade,\n"
                    + "aplicatodos,\n"
                    + "valorreferenteitenslista,\n"
                    + "valordesconto,\n"
                    + "codigoscanntech,\n"
                    + "valorpaga,\n"
                    + "id_tipopercentualvalordesconto,\n"
                    + "desconsideraritem,\n"
                    + "qtdlimite,\n"
                    + "somenteclubevantagens,\n"
                    + "diasexpiracao\n"
                    + "from promocao\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    PromocaoIMP imp = new PromocaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataTermino(rst.getDate("datatermino"));
                    imp.setPontuacao(rst.getInt("pontuacao"));
                    imp.setQuantidade(rst.getInt("quantidade"));
                    imp.setQtdcupom(rst.getInt("qtdcupom"));
                    imp.setIdSituacaocadastro(rst.getInt("id_situacaocadastro"));
                    imp.setVerificaProdutosAuditados(rst.getBoolean("verificaprodutosauditados"));
                    imp.setIdTipopromocao(rst.getInt("id_tipopromocao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setControle(rst.getInt("controle"));
                    imp.setIdTipopercentualvalor(rst.getInt("id_tipopercentualvalor"));
                    imp.setIdTipoquantidade(rst.getInt("id_tipoquantidade"));
                    imp.setAplicatodos(rst.getBoolean("aplicatodos"));
                    imp.setValorReferenteItensLista(rst.getBoolean("valorreferenteitenslista"));
                    imp.setValordesconto(rst.getDouble("valordesconto"));
                    imp.setValorPaga(rst.getDouble("valorpaga"));
                    imp.setIdTipoPercentualValorDesconto(rst.getInt("id_tipopercentualvalordesconto"));
                    imp.setDesconsiderarItem(rst.getBoolean("desconsideraritem"));
                    imp.setQtdLimite(rst.getInt("qtdlimite"));
                    imp.setSomenteClubeVantagens(rst.getBoolean("somenteclubevantagens"));
                    imp.setDiasExpiracao(rst.getInt("diasexpiracao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + " CASE WHEN ID_CLIENTEPREFERENCIAL IS NULL THEN CPFCRM\n"
                    + "   ELSE ID_CLIENTEPREFERENCIAL END id,\n"
                    + " CASE WHEN NOMECLIENTE = '' THEN 'CADASTRO INCOMPLETO'\n"
                    + "   ELSE NOMECLIENTE END nome,\n"
                    + " CPFCRM cpf\n"
                    + "FROM VENDA\n"
                    + "WHERE ID_CLIENTEPREFERENCIAL IS NOT NULL\n"
                    + "OR NOMECLIENTE <> ''\n"
                    + "OR CPFCRM <> 0"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cpf"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
