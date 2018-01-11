package vrimplantacao.dao.estoque;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.dao.ParametroDAO;
import vrimplantacao.dao.cadastro.AssociadoDAO;
import vrimplantacao.dao.cadastro.ReceitaDAO;
import vrimplantacao.vo.administrativo.AcertoEstoqueVO;
import vrimplantacao.vo.administrativo.ReceitaItemVO;
import vrimplantacao.vo.administrativo.ReceitaProdutoVO;
import vrimplantacao.vo.administrativo.ReceitaVO;
import vrimplantacao.vo.administrativo.TipoEntradaSaida;
import vrimplantacao.vo.administrativo.TipoMovimentacao;
import vrimplantacao.vo.cadastro.AssociadoItemVO;
import vrimplantacao.vo.cadastro.AssociadoVO;
import vrimplantacao.vo.cadastro.SituacaoCadastro;
import vrimplantacao.vo.estoque.AcertoCestaBasicaVO;
import vrimplantacao.vo.estoque.AcertoTrocaVO;
import vrimplantacao.vo.estoque.EstoqueCongeladoVO;
import vrimplantacao.vo.estoque.TipoBaixaPerda;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class EstoqueDAO {

    public void alterar(AcertoEstoqueVO i_acertoEstoque) throws Exception {
        alterar(i_acertoEstoque, true);
    }

    public void alterar(AcertoEstoqueVO i_acertoEstoque, boolean i_congelamento) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            if (new ParametroDAO().get(16).getBoolean() && i_congelamento) {
                EstoqueCongeladoVO oCongelado = new EstoqueCongeladoVO();
                oCongelado.idProduto = i_acertoEstoque.idProduto;
                oCongelado.idLoja = i_acertoEstoque.idLoja;
                oCongelado.idTipoEntradaSaida = i_acertoEstoque.idTipoEntradaSaida;
                oCongelado.idTipoMovimentacao = i_acertoEstoque.idTipoMovimentacao;
                oCongelado.quantidade = i_acertoEstoque.quantidade;
                oCongelado.baixaReceita = i_acertoEstoque.baixaReceita;
                oCongelado.baixaAssociado = i_acertoEstoque.baixaAssociado;
                oCongelado.baixaPerda = i_acertoEstoque.baixaPerda;
                oCongelado.observacao = i_acertoEstoque.observacao;
                oCongelado.data = i_acertoEstoque.data;

                new EstoqueCongeladoDAO().salvar(oCongelado);

                Conexao.commit();
                return;
            }

            boolean isReceita = false;
            boolean isAssociado = false;

            //receita
            if (i_acertoEstoque.baixaReceita) {
                isReceita = alterarReceita(i_acertoEstoque);
            }

            //associado
            if (i_acertoEstoque.baixaAssociado && !isReceita) {
                isAssociado = alterarAssociado(i_acertoEstoque);
            }

            if (!isReceita && !isAssociado) {
                //peps
                if (new ParametroDAO().get(129).getBoolean()) {
                    new PepsDAO().alterar(i_acertoEstoque);
                }

                //altera estoque
                sql = new StringBuilder();
                sql.append("UPDATE produtocomplemento SET");

                if (i_acertoEstoque.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
                    sql.append(" estoque = estoque + " + i_acertoEstoque.quantidade);

                } else if (i_acertoEstoque.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
                    sql.append(" estoque = estoque - " + i_acertoEstoque.quantidade);
                }

                sql.append(" WHERE id_produto = " + i_acertoEstoque.idProduto);
                sql.append(" and id_loja = " + i_acertoEstoque.idLoja);

                stm.execute(sql.toString());

                //extrato movimentacao
                new LogEstoqueDAO().gravarMovimentacao(i_acertoEstoque);

                //baixa perda
                if (i_acertoEstoque.baixaPerda) {
                    rst = stm.executeQuery("SELECT id_tipoembalagem, perda FROM produto WHERE id = " + i_acertoEstoque.idProduto);

                    if (!rst.next()) {
                        throw new VRException("Produto " + i_acertoEstoque.idProduto + " não encontrado!");
                    }

                    double perda = rst.getDouble("perda");
                    int idTipoEmbalagem = rst.getInt("id_tipoembalagem");

                    if (i_acertoEstoque.idProdutoAssociado != -1) {
                        rst = stm.executeQuery("SELECT perda FROM produto WHERE id = " + i_acertoEstoque.idProdutoAssociado);

                        if (!rst.next()) {
                            throw new VRException("Produto " + i_acertoEstoque.idProdutoAssociado + " não encontrado!");
                        }

                        perda = rst.getDouble("perda");
                    }

                    if (perda > 0) {
                        double quantidadePerda = Util.round(i_acertoEstoque.quantidade * (perda / 100), 3);

                        if (!Util.isTipoEmbalagemFracionado(idTipoEmbalagem)) {
                            quantidadePerda = Util.round(quantidadePerda, 0);
                        }

                        if (quantidadePerda > 0) {
                            AcertoEstoqueVO oPerda = new AcertoEstoqueVO();
                            oPerda.idProduto = i_acertoEstoque.idProduto;
                            oPerda.idLoja = i_acertoEstoque.idLoja;
                            oPerda.quantidade = quantidadePerda;
                            oPerda.data = i_acertoEstoque.data;

                            if (i_acertoEstoque.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
                                if (new ParametroDAO().get(193).getInt() == TipoBaixaPerda.ENTRADA.getId()) {
                                    oPerda.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                                } else {
                                    oPerda.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                                }
                            } else {
                                if (new ParametroDAO().get(193).getInt() == TipoBaixaPerda.ENTRADA.getId()) {
                                    oPerda.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                                } else {
                                    oPerda.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                                }
                            }

                            oPerda.idTipoMovimentacao = TipoMovimentacao.PERDA_NATURAL.getId();
                            oPerda.observacao = i_acertoEstoque.observacao;

                            alterar(oPerda);
                        }
                    }
                }
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private boolean alterarReceita(AcertoEstoqueVO i_acertoEstoque) throws Exception {
        int idReceita = new ReceitaDAO().verificar(i_acertoEstoque.idProduto);

        if (idReceita == -1) {
            return false;
        }

        ReceitaVO oReceita = new ReceitaDAO().carregar(idReceita);

        if (oReceita.idSituacaoCadastro != SituacaoCadastro.ATIVO.getId()) {
            return false;
        }

        double rendimento = 1;

        for (ReceitaProdutoVO oProduto : oReceita.vProduto) {
            if (oProduto.idProduto == i_acertoEstoque.idProduto) {
                rendimento = oProduto.rendimento;
                break;
            }
        }

        for (ReceitaItemVO oItem : oReceita.vItem) {
            if (oItem.baixaEstoque) {
                double quantidade = ((i_acertoEstoque.quantidade * oItem.qtdEmbalagemReceita) / oItem.qtdEmbalagemProduto) / rendimento;

                AcertoEstoqueVO oEstoqueReceita = new AcertoEstoqueVO();
                oEstoqueReceita.idProduto = oItem.idProduto;
                oEstoqueReceita.idLoja = i_acertoEstoque.idLoja;
                oEstoqueReceita.quantidade = quantidade;
                oEstoqueReceita.idTipoEntradaSaida = i_acertoEstoque.idTipoEntradaSaida;
                oEstoqueReceita.idTipoMovimentacao = i_acertoEstoque.idTipoMovimentacao;
                oEstoqueReceita.observacao = "RECEITA " + idReceita + ", " + i_acertoEstoque.observacao;
                oEstoqueReceita.baixaReceita = i_acertoEstoque.baixaReceita;
                oEstoqueReceita.baixaAssociado = i_acertoEstoque.baixaAssociado;
                oEstoqueReceita.baixaPerda = i_acertoEstoque.baixaPerda;
                oEstoqueReceita.data = i_acertoEstoque.data;

                alterar(oEstoqueReceita);
            }
        }

        return true;
    }

    public boolean alterarAssociado(AcertoEstoqueVO i_acertoEstoque) throws Exception {
        long idAssociado = new AssociadoDAO().verificar(i_acertoEstoque.idProduto);

        if (idAssociado == 0) {
            return false;
        }

        AssociadoVO oAssociado = new AssociadoDAO().carregar(idAssociado);

        for (AssociadoItemVO oItem : oAssociado.vProduto) {
            if (oItem.aplicaEstoque) {
                double quantidade = i_acertoEstoque.quantidade + (i_acertoEstoque.quantidade * (oItem.percentualCustoEstoque / 100));

                AcertoEstoqueVO oEstoqueAssociado = new AcertoEstoqueVO();
                oEstoqueAssociado.idProduto = oItem.idProduto;
                oEstoqueAssociado.idLoja = i_acertoEstoque.idLoja;
                oEstoqueAssociado.quantidade = quantidade;
                oEstoqueAssociado.idTipoEntradaSaida = i_acertoEstoque.idTipoEntradaSaida;
                oEstoqueAssociado.idTipoMovimentacao = i_acertoEstoque.idTipoMovimentacao;
                oEstoqueAssociado.observacao = "ASSOCIADO " + i_acertoEstoque.idProduto + ", " + i_acertoEstoque.observacao;
                oEstoqueAssociado.baixaReceita = i_acertoEstoque.baixaReceita;
                oEstoqueAssociado.baixaAssociado = i_acertoEstoque.baixaAssociado;
                oEstoqueAssociado.baixaPerda = i_acertoEstoque.baixaPerda;
                oEstoqueAssociado.idProdutoAssociado = i_acertoEstoque.idProduto;
                oEstoqueAssociado.data = i_acertoEstoque.data;

                alterar(oEstoqueAssociado);

                return true;
            }
        }

        return false;
    }

    public void alterarTroca(AcertoTrocaVO i_acertoTroca) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //altera estoque
            if (i_acertoTroca.alteraEstoque) {
                boolean baixaPerda = (new ParametroDAO().get(193).getInt() == TipoBaixaPerda.SAIDA.getId());

                AcertoEstoqueVO oAcertoEstoque = new AcertoEstoqueVO();
                oAcertoEstoque.idProduto = i_acertoTroca.idProduto;
                oAcertoEstoque.idLoja = i_acertoTroca.idLoja;
                oAcertoEstoque.data = i_acertoTroca.data;

                if (i_acertoTroca.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
                    oAcertoEstoque.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                    oAcertoEstoque.idTipoMovimentacao = TipoMovimentacao.ENTRADA_TROCA.getId();

                } else if (i_acertoTroca.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
                    oAcertoEstoque.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                    oAcertoEstoque.idTipoMovimentacao = TipoMovimentacao.SAIDA_TROCA.getId();
                }

                oAcertoEstoque.quantidade = i_acertoTroca.quantidade;
                oAcertoEstoque.baixaReceita = new ParametroDAO().get(105).getBoolean();
                oAcertoEstoque.baixaAssociado = true;
                oAcertoEstoque.baixaPerda = baixaPerda;

                alterar(oAcertoEstoque);
            }

            //altera troca
            sql = new StringBuilder();
            sql.append("SELECT id FROM produtocomplemento");
            sql.append(" WHERE id_produto = " + i_acertoTroca.idProduto + " AND id_loja = " + i_acertoTroca.idLoja);

            rst = stm.executeQuery(sql.toString());

            if (!rst.next()) {
                throw new VRException(Util.MSG_REGISTRO_NAO_ENCONTRADO);
            }

            sql = new StringBuilder();
            sql.append("UPDATE produtocomplemento SET");

            if (i_acertoTroca.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
                sql.append(" troca = troca + " + i_acertoTroca.quantidade);

            } else if (i_acertoTroca.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
                sql.append(" troca = troca - " + i_acertoTroca.quantidade);
            }

            sql.append(" WHERE id_produto = " + i_acertoTroca.idProduto);
            sql.append(" and id_loja = " + i_acertoTroca.idLoja);

            stm.execute(sql.toString());

            //extrato movimentaco
            new LogTrocaDAO().gravarMovimentacao(i_acertoTroca);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCestaBasica(AcertoCestaBasicaVO i_acertoCestaBasica) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuffer sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //altera estoque
            if (i_acertoCestaBasica.alteraEstoque) {
                boolean baixaPerda = (new ParametroDAO().get(193).getInt() == TipoBaixaPerda.SAIDA.getId());

                AcertoEstoqueVO oAcertoEstoque = new AcertoEstoqueVO();
                oAcertoEstoque.idProduto = i_acertoCestaBasica.idProduto;
                oAcertoEstoque.idLoja = i_acertoCestaBasica.idLoja;
                oAcertoEstoque.data = i_acertoCestaBasica.data;

                if (i_acertoCestaBasica.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
                    oAcertoEstoque.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
                    oAcertoEstoque.idTipoMovimentacao = TipoMovimentacao.ENTRADA_CESTA_BASICA.getId();

                } else if (i_acertoCestaBasica.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
                    oAcertoEstoque.idTipoEntradaSaida = TipoEntradaSaida.ENTRADA.getId();
                    oAcertoEstoque.idTipoMovimentacao = TipoMovimentacao.SAIDA_CESTA_BASICA.getId();
                }

                oAcertoEstoque.quantidade = i_acertoCestaBasica.quantidade;
                oAcertoEstoque.baixaReceita = new ParametroDAO().get(105).getBoolean();
                oAcertoEstoque.baixaAssociado = true;
                oAcertoEstoque.baixaPerda = baixaPerda;

                alterar(oAcertoEstoque);
            }

            //altera cesta basica
            sql = new StringBuffer();
            sql.append("SELECT id FROM produtocomplemento");
            sql.append(" WHERE id_produto = " + i_acertoCestaBasica.idProduto + " AND id_loja = " + i_acertoCestaBasica.idLoja);

            rst = stm.executeQuery(sql.toString());

            if (!rst.next()) {
                throw new VRException(Util.MSG_REGISTRO_NAO_ENCONTRADO);
            }

            sql = new StringBuffer();
            sql.append("UPDATE produtocomplemento SET");

            if (i_acertoCestaBasica.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
                sql.append(" cestabasica = cestabasica + " + i_acertoCestaBasica.quantidade);

            } else if (i_acertoCestaBasica.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
                sql.append(" cestabasica = cestabasica - " + i_acertoCestaBasica.quantidade);
            }

            sql.append(" WHERE id_produto = " + i_acertoCestaBasica.idProduto);
            sql.append(" and id_loja = " + i_acertoCestaBasica.idLoja);

            stm.execute(sql.toString());

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
