package vrimplantacao.dao.estoque;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.administrativo.AdministracaoPrecoDAO;
import vrimplantacao.dao.notafiscal.CustoDAO;
import vrimplantacao.vo.administrativo.AcertoEstoqueVO;
import vrimplantacao.vo.administrativo.AdministracaoPrecoVO;
import vrimplantacao.vo.administrativo.CustoEntradaVO;
import vrimplantacao.vo.administrativo.PepsVO;
import vrimplantacao.vo.administrativo.TipoEntradaSaida;
import vrimplantacao.vo.cadastro.TipoAdministracaoPreco;
import vrframework.classe.Conexao;

public class PepsDAO {

    public void alterar(AcertoEstoqueVO i_acertoEstoque) throws Exception {
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ArrayList<PepsVO> vPeps = carregar(i_acertoEstoque.idProduto, i_acertoEstoque.idLoja);

            if (i_acertoEstoque.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
                //entra no ultimo
                PepsVO oPeps = vPeps.get(vPeps.size() - 1);
                oPeps.estoque += i_acertoEstoque.quantidade;

            } else if (i_acertoEstoque.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
                //sai do primeiro
                PepsVO oPeps = vPeps.get(0);
                oPeps.estoque -= i_acertoEstoque.quantidade;
            }

            //acerta lista
            if (vPeps.size() > 1) {
                boolean alteraCusto = false;

                for (int i = 0; i < vPeps.size(); i++) {
                    PepsVO oPeps = vPeps.get(i);

                    if (oPeps.estoque > 0) {
                        break;

                    } else if (oPeps.estoque == 0) {
                        if (i < vPeps.size() - 1) {
                            vPeps.remove(oPeps);
                            alteraCusto = true;
                        }

                        break;

                    } else {
                        if (i + 1 < vPeps.size()) {
                            vPeps.get(i + 1).estoque += oPeps.estoque;
                            vPeps.remove(oPeps);
                            i--;

                            alteraCusto = true;
                        }
                    }
                }

                if (alteraCusto) {
                    PepsVO oPeps = vPeps.get(0);

                    CustoEntradaVO oCusto = new CustoEntradaVO();
                    oCusto.idProduto = oPeps.idProduto;
                    oCusto.custoComImposto = oPeps.custoComImposto;
                    oCusto.custoSemImposto = oPeps.custoSemImposto;
                    oCusto.custoSemPerdaSemImposto = oPeps.custoSemPerdaSemImposto;
                    oCusto.idAliquotaCredito = oPeps.idAliquotaCredito;
                    oCusto.valorIcmsSubstituicao = oPeps.valorIcmsSubstituicao;
                    oCusto.valorIpi = oPeps.valorIpi;
                    oCusto.quantidade = oPeps.estoque;
                    oCusto.observacao = "PEPS";
                    oCusto.idLoja = i_acertoEstoque.idLoja;

                    new CustoDAO().alterarEntrada(oCusto);

                    AdministracaoPrecoVO oAdministracao = new AdministracaoPrecoVO();
                    oAdministracao.idLoja = i_acertoEstoque.idLoja;
                    oAdministracao.idNotaEntrada = -1;
                    oAdministracao.idPedido = -1;
                    oAdministracao.idTransferenciaEntrada = -1;
                    oAdministracao.idFornecedor = Global.idFornecedor;
                    oAdministracao.idTipoEntrada = -1;
                    oAdministracao.idProduto = oPeps.idProduto;
                    oAdministracao.qtdEmbalagem = 1;
                    oAdministracao.quantidade = oPeps.estoque;
                    oAdministracao.idAliquota = oPeps.idAliquotaCredito;
                    oAdministracao.custoComImposto = oPeps.custoComImposto;
                    oAdministracao.custoSemImposto = oPeps.custoSemImposto;
                    oAdministracao.idTipoAdministracaoPreco = TipoAdministracaoPreco.PEPS.getId();

                    new AdministracaoPrecoDAO().adicionar(oAdministracao);
                }
            }

            //reordena lista
            for (PepsVO oPeps : vPeps) {
                oPeps.sequencia = vPeps.indexOf(oPeps) + 1;
            }

            salvar(vPeps);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public ArrayList<PepsVO> carregar(int i_idProduto, int idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT peps.*, p.id_tipoembalagem, COALESCE(ac.descricao, '') AS aliquotacredito");
        sql.append(" FROM peps");
        sql.append(" INNER JOIN produto AS p ON p.id = peps.id_produto");
        sql.append(" INNER JOIN aliquota AS ac ON ac.id = peps.id_aliquotacredito");
        sql.append(" WHERE peps.id_produto = " + i_idProduto);
        sql.append(" AND peps.id_loja = " + idLoja);
        sql.append(" ORDER BY peps.sequencia");

        rst = stm.executeQuery(sql.toString());

        ArrayList<PepsVO> vPeps = new ArrayList();

        while (rst.next()) {
            PepsVO oPeps = new PepsVO();
            oPeps.idLoja = rst.getInt("id_loja");
            oPeps.idProduto = rst.getInt("id_produto");
            oPeps.sequencia = rst.getInt("sequencia");
            oPeps.estoque = rst.getDouble("estoque");
            oPeps.custoSemImposto = rst.getDouble("custosemimposto");
            oPeps.custoComImposto = rst.getDouble("custocomimposto");
            oPeps.custoSemPerdaSemImposto = rst.getDouble("custosemperdasemimposto");
            oPeps.valorIpi = rst.getDouble("valoripi");
            oPeps.idAliquotaCredito = rst.getObject("id_aliquotacredito") == null ? -1 : rst.getInt("id_aliquotacredito");
            oPeps.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
            oPeps.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
            oPeps.aliquotaCredito = rst.getString("aliquotacredito");

            vPeps.add(oPeps);
        }

        if (vPeps.isEmpty()) {
            sql = new StringBuilder();
            sql.append("SELECT id_loja, id_produto, estoque, custosemimposto, custocomimposto, custosemperdasemimposto, valoripi,");
            sql.append(" id_aliquotacredito, valoricmssubstituicao");
            sql.append(" FROM produtocomplemento");
            sql.append(" WHERE id_produto = " + i_idProduto);
            sql.append(" AND id_loja = " + idLoja);

            rst = stm.executeQuery(sql.toString());

            if (!rst.next()) {
                throw new Exception("Produto " + i_idProduto + " não encontrado!");
            }

            PepsVO oPeps = new PepsVO();
            oPeps.idLoja = rst.getInt("id_loja");
            oPeps.idProduto = rst.getInt("id_produto");
            oPeps.sequencia = 1;
            oPeps.estoque = rst.getDouble("estoque");
            oPeps.custoSemImposto = rst.getDouble("custosemimposto");
            oPeps.custoComImposto = rst.getDouble("custocomimposto");
            oPeps.custoSemPerdaSemImposto = rst.getDouble("custosemperdasemimposto");
            oPeps.valorIpi = rst.getDouble("valoripi");
            oPeps.idAliquotaCredito = rst.getObject("id_aliquotacredito") == null ? -1 : rst.getInt("id_aliquotacredito");
            oPeps.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");

            vPeps.add(oPeps);
        }

        stm.close();

        return vPeps;
    }

    public void salvar(ArrayList<PepsVO> i_peps) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            stm.execute("DELETE FROM peps WHERE id_produto = " + i_peps.get(0).idProduto + " AND id_loja = " + i_peps.get(0).idLoja);

            for (PepsVO oPeps : i_peps) {
                sql = new StringBuilder();
                sql.append("INSERT INTO peps (id_loja, id_produto, sequencia, estoque, custosemimposto, custocomimposto, custosemperdasemimposto,");
                sql.append(" valoripi, id_aliquotacredito, valoricmssubstituicao) VALUES (");
                sql.append(oPeps.idLoja + ",");
                sql.append(oPeps.idProduto + ",");
                sql.append(oPeps.sequencia + ",");
                sql.append(oPeps.estoque + ",");
                sql.append(oPeps.custoSemImposto + ",");
                sql.append(oPeps.custoComImposto + ",");
                sql.append(oPeps.custoSemPerdaSemImposto + ",");
                sql.append(oPeps.valorIpi + ",");
                sql.append((oPeps.idAliquotaCredito == -1 ? "NULL" : oPeps.idAliquotaCredito) + ",");
                sql.append(oPeps.valorIcmsSubstituicao + ")");

                stm.execute(sql.toString());
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
