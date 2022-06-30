package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.rmi.rmic.iiop.Constants;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.AtacadoVendedorClienteDAO;
import vrimplantacao.dao.cadastro.AtacadoVendedorDAO;
import vrimplantacao.dao.cadastro.ClienteEventualContatoDAO;
import vrimplantacao.dao.cadastro.ClienteEventuallDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialContatoDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FornecedorContatoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.NutricionalFilizolaDAO;
import vrimplantacao.dao.cadastro.NutricionalFilizolaRepository;
import vrimplantacao.dao.cadastro.NutricionalToledoRepository;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.FornecedorContatoVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.AtacadoVendedorClienteVO;
import vrimplantacao.vo.vrimplantacao.AtacadoVendedorVO;
import vrimplantacao.vo.vrimplantacao.ClienteEventualContatoVO;
import vrimplantacao.vo.vrimplantacao.ClienteEventualVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialContatoVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstoqueTerceiroVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class KairosDAO {

    //CARREGAMENTOS
    private List<NutricionalFilizolaVO> carregarNutricionalFilizola() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<NutricionalFilizolaVO> vNutricionalFilizola = new ArrayList<>();
        int caloria;
        double carboidratos, proteinas, gordurasTotais, gordurasSaturadas,
                gordurasTrans, fibra, sodio, idProduto;
        String descricao = "", porcao = "", porcaoQuantidade;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select ");
            sql.append("CodigoProduto, ");
            sql.append("Descricao, ");
            sql.append("InfoNutPorcaoQuantidade, ");
            sql.append("InfoNutPorcaoUnidade, ");
            sql.append("InfoNutParteIntMedCaseira, ");
            sql.append("InfoNutParteDecMedCaseira, ");
            sql.append("InfoNutMedCaseiraUtil, ");
            sql.append("InfoNutValorEnergetico, ");
            sql.append("InfoNutCarboidratos, ");
            sql.append("InfoNutProteinas, ");
            sql.append("InfoNutGordurasTotais, ");
            sql.append("InfoNutGordurasSaturadas, ");
            sql.append("InfoNutGordurasTrans, ");
            sql.append("InfoNutFibraAlimentar, ");
            sql.append("InfoNutSodio, ");
            sql.append("InfoNutObservacao ");
            sql.append("from Produto ");
            sql.append("where InfoNutPorcaoQuantidade > 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("CodigoProduto").trim());
                porcaoQuantidade = rst.getString("InfoNutPorcaoQuantidade");
                caloria = rst.getInt("InfoNutValorEnergetico");
                carboidratos = rst.getDouble("InfoNutCarboidratos");
                proteinas = rst.getDouble("InfoNutProteinas");
                gordurasTotais = rst.getDouble("InfoNutGordurasTotais");
                gordurasSaturadas = rst.getDouble("InfoNutGordurasSaturadas");
                gordurasTrans = rst.getDouble("InfoNutGordurasTrans");
                fibra = rst.getDouble("InfoNutFibraAlimentar");
                sodio = rst.getDouble("InfoNutSodio");

                porcao = "PORCAOQTD: " + porcaoQuantidade;
                porcao = porcao + " => " + Utils.acertarTexto(rst.getString("InfoNutObservacao").trim());
                descricao = Utils.acertarTexto(rst.getString("Descricao").trim().replace("'", ""));

                if (porcao.length() > 35) {
                    porcao = porcao.substring(0, 35);
                }

                if (descricao.length() > 20) {
                    descricao = descricao.substring(0, 20);
                }

                NutricionalFilizolaVO oNutricionalFilizola = new NutricionalFilizolaVO();
                oNutricionalFilizola.setCaloria(caloria);
                oNutricionalFilizola.setCarboidrato(carboidratos);
                oNutricionalFilizola.setProteina(proteinas);
                oNutricionalFilizola.setGordura(gordurasTotais);
                oNutricionalFilizola.setGordurasaturada(gordurasSaturadas);
                oNutricionalFilizola.setGorduratrans(gordurasTrans);
                oNutricionalFilizola.setFibra(fibra);
                oNutricionalFilizola.setSodio(sodio);
                oNutricionalFilizola.setDescricao(descricao);
                oNutricionalFilizola.setPorcao(porcao);

                NutricionalFilizolaItemVO oNutricionalFilizolaItem = new NutricionalFilizolaItemVO();
                oNutricionalFilizolaItem.setId_produtoDouble(idProduto);
                oNutricionalFilizola.vNutricionalFilizolaItem.add(oNutricionalFilizolaItem);

                vNutricionalFilizola.add(oNutricionalFilizola);
            }

            stm.close();
            return vNutricionalFilizola;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<NutricionalToledoVO> carregarNutricionalToledo() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<NutricionalToledoVO> vNutricionalToledo = new ArrayList<>();
        int caloria, quantidade, idTipoMedida;
        double carboidratos, proteinas, gordurasTotais, gordurasSaturadas,
                gordurasTrans, fibra, sodio, idProduto;
        String descricao = "", porcao = "";

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select ");
            sql.append("CodigoProduto, ");
            sql.append("Descricao, ");
            sql.append("InfoNutPorcaoQuantidade, ");
            sql.append("InfoNutPorcaoUnidade, ");
            sql.append("InfoNutParteIntMedCaseira, ");
            sql.append("InfoNutParteDecMedCaseira, ");
            sql.append("InfoNutMedCaseiraUtil, ");
            sql.append("InfoNutValorEnergetico, ");
            sql.append("InfoNutCarboidratos, ");
            sql.append("InfoNutProteinas, ");
            sql.append("InfoNutGordurasTotais, ");
            sql.append("InfoNutGordurasSaturadas, ");
            sql.append("InfoNutGordurasTrans, ");
            sql.append("InfoNutFibraAlimentar, ");
            sql.append("InfoNutSodio, ");
            sql.append("InfoNutObservacao ");
            sql.append("from Produto ");
            sql.append("where InfoNutPorcaoQuantidade > 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("CodigoProduto").trim());
                quantidade = rst.getInt("InfoNutPorcaoQuantidade");
                caloria = rst.getInt("InfoNutValorEnergetico");
                carboidratos = rst.getDouble("InfoNutCarboidratos");
                proteinas = rst.getDouble("InfoNutProteinas");
                gordurasTotais = rst.getDouble("InfoNutGordurasTotais");
                gordurasSaturadas = rst.getDouble("InfoNutGordurasSaturadas");
                gordurasTrans = rst.getDouble("InfoNutGordurasTrans");
                fibra = rst.getDouble("InfoNutFibraAlimentar");
                sodio = rst.getDouble("InfoNutSodio");

                porcao = Utils.acertarTexto(rst.getString("InfoNutObservacao").trim());
                descricao = Utils.acertarTexto(rst.getString("Descricao").trim().replace("'", ""));

                if (porcao.contains("GRAMAS")
                        || (porcao.contains("gramas"))
                        || (porcao.contains("g"))) {
                    idTipoMedida = 0;
                } else {
                    idTipoMedida = 2;
                }

                if (porcao.length() > 35) {
                    porcao = porcao.substring(0, 35);
                }

                if (descricao.length() > 20) {
                    descricao = descricao.substring(0, 20);
                }

                NutricionalToledoVO oNutricionalToledo = new NutricionalToledoVO();
                oNutricionalToledo.setCaloria(caloria);
                oNutricionalToledo.setCarboidrato(carboidratos);
                oNutricionalToledo.setProteina(proteinas);
                oNutricionalToledo.setGordura(gordurasTotais);
                oNutricionalToledo.setGordurasaturada(gordurasSaturadas);
                oNutricionalToledo.setGorduratrans(gordurasTrans);
                oNutricionalToledo.setFibra(fibra);
                oNutricionalToledo.setSodio(sodio);
                oNutricionalToledo.setDescricao(descricao);
                oNutricionalToledo.setId_tipomedida(idTipoMedida);

                NutricionalToledoItemVO oNutricionalToledoItem = new NutricionalToledoItemVO();
                oNutricionalToledoItem.setId_produtoDouble(idProduto);
                oNutricionalToledo.vNutricionalToledoItem.add(oNutricionalToledoItem);

                vNutricionalToledo.add(oNutricionalToledo);
            }

            stm.close();
            return vNutricionalToledo;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<MercadologicoVO> carregarMercadologicoKairos(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select m1.codigogrupoproduto codM1, m1.descricao descM1, ");
            sql.append("m2.codigosubgrupoproduto codM2, m2.descricao descM2, ");
            sql.append("1 codM3, m2.Descricao descM3 ");
            sql.append("from GrupoProduto m1 ");
            sql.append("left join SubGrupoProduto m2 ");
            sql.append("on m2.CodigoGrupoProduto = m1.CodigoGrupoProduto ");
            sql.append("order by codM1, codM2; ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("codM1"));

                    if ((rst.getString("descM1") != null)
                            && (!rst.getString("descM1").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("descM1").replace("'", "").trim());
                    } else {
                        descricao = "";
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if ((nivel == 2)) {

                    mercadologico1 = Integer.parseInt(rst.getString("codM1"));

                    if ((rst.getString("codM2") != null)
                            && (!rst.getString("codM2").trim().isEmpty())) {

                        mercadologico2 = Integer.parseInt(rst.getString("codM2"));
                    } else {
                        mercadologico2 = 1;
                    }

                    if ((rst.getString("descM2") != null)
                            && (!rst.getString("descM2").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("descM2").replace("'", "").trim());
                    } else {
                        descricao = Utils.acertarTexto(rst.getString("descM1").trim().replace("'", ""));
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if ((nivel == 3)) {
                    mercadologico1 = Integer.parseInt(rst.getString("codM1"));

                    if ((rst.getString("codM2") != null)
                            && (!rst.getString("codM2").trim().isEmpty())) {

                        mercadologico2 = Integer.parseInt(rst.getString("codM2"));
                    } else {
                        mercadologico2 = 1;
                    }

                    if ((rst.getString("codM3") != null)
                            && (!rst.getString("codM3").trim().isEmpty())) {
                        mercadologico3 = Integer.parseInt(rst.getString("codM3"));
                    } else {
                        mercadologico3 = 1;
                    }

                    if ((rst.getString("descM3") != null)
                            && (!rst.getString("descM3").trim().isEmpty())) {
                        descricao = Utils.acertarTexto(rst.getString("descM3").replace("'", "").trim());
                    } else {
                        if ((rst.getString("descM2") != null)
                                && (!rst.getString("descM2").trim().isEmpty())) {

                            descricao = Utils.acertarTexto(rst.getString("descM2").replace("'", "").trim());
                        } else {

                            descricao = Utils.acertarTexto(rst.getString("descM1").replace("'", "").trim());
                        }
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }

            return vMercadologico;

        } catch (Exception ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarProdutoKairos() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
                idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
                ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, pesoBruto, pesoLiquido;

        try {

            stmPostgres = Conexao.createStatement();

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select p.CodigoProduto, p.CodigoNCM, p.CodigoGrupoProduto, p.CodigoSubGrupoProduto, ");
            sql.append("p.Descricao, p.DescricaoTecnica, p.Observacoes, p.SiglaUnidade, p.PesoBruto, ");
            sql.append("p.PesoLiquido, p.PrazoValidade, p.DataCadastro, p.MargemLucroTeorica, ");
            sql.append("p.Situacao, p.ClassificacaoFiscal, p.SituacaoTributariaPISEnt, p.SituacaoTributariaPIS, ");
            sql.append("p.SituacaoTributariaCOFINSEnt, p.SituacaoTributariaCOFINS, p.NaturezaReceitaPisCofins, ");
            sql.append("codB.NumeroCodigoBarraProduto, codB.SiglaUnidade TipoEmbalagem, codB.QuantidadeProduto, ");
            sql.append("sitTrib.CodigoSituacaoTributariaB, sitTrib.Descricao, ");
            sql.append("imp.PercentualICMS, imp.PercentualICMSReduzido, imp.MargemLucro, ");
            sql.append("gfp.CodigoGrupoFiscal, gf.Descricao ");
            sql.append("from Produto p ");
            sql.append("left join SituacaoTributariaB sitTrib on sitTrib.CodigoSituacaoTributariaB = p.SituacaoTributariaB ");
            sql.append("left join CodigoBarraProduto codB on codB.CodigoProduto = p.CodigoProduto ");
            sql.append("left join ImpostoProduto imp on imp.CodigoProduto = p.CodigoProduto ");
            sql.append("left join GrupoFiscalProduto gfp on gfp.CodigoProduto = p.CodigoProduto ");
            sql.append("inner join GrupoFiscal gf on gf.CodigoGrupoFiscal = gfp.CodigoGrupoFiscal ");
            sql.append("where p.CodigoProduto <= 999999 ");
            sql.append(" and imp.OrigemSiglaPais = 'BRA' ");
            sql.append(" and imp.DestinoSiglaPais = 'BRA' ");
            sql.append(" and imp.OrigemSiglaUF = 'PE' ");
            sql.append(" and imp.DestinoSiglaUF = 'PE' ");
            sql.append("and gfp.SiglaPais = 'BRA' ");
            sql.append("and gfp.SiglaUF = 'PE' ");
            sql.append("and gf.SiglaUF =  'PE' ");
            sql.append(" order by p.CodigoProduto; ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                if ((rst.getString("DataCadastro") != null)
                        && (!rst.getString("DataCadastro").trim().isEmpty())) {
                    dataCadastro = rst.getString("DataCadastro").substring(0, 10).replace("-", "/");
                } else {
                    dataCadastro = "";
                }

                if ((rst.getString("Situacao") != null)
                        && (!rst.getString("Situacao").trim().isEmpty())) {

                    if ("A".equals(rst.getString("Situacao").trim())) {
                        idSituacaoCadastro = 1;
                    } else {
                        idSituacaoCadastro = 0;
                    }
                } else {
                    idSituacaoCadastro = 1;
                }

                codigoAnterior = Double.parseDouble(rst.getString("CodigoProduto").trim().replace(".", ""));
                idProduto = Integer.parseInt(rst.getString("CodigoProduto").trim().replace(".", ""));

                if ((rst.getString("NumeroCodigoBarraProduto") != null)
                        && (!rst.getString("NumeroCodigoBarraProduto").trim().isEmpty())) {

                    sql = new StringBuilder();
                    sql.append("select codigo, descricao, pesavel, validade ");
                    sql.append("from implantacao.produtobalanca ");
                    sql.append("where cast(codigo as numeric(14,0)) = " + Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").replace(".", "")));

                    rstPostgres = stmPostgres.executeQuery(sql.toString());

                    if (rstPostgres.next()) {

                        eBalanca = true;
                        codigoBalanca = rstPostgres.getInt("codigo");
                        validade = rstPostgres.getInt("validade");

                        if (null != rstPostgres.getString("pesavel").trim()) {
                            switch (rstPostgres.getString("pesavel").trim()) {
                                case "U":
                                    pesavel = true;
                                    idTipoEmbalagem = 0;
                                    break;
                                case "P":
                                    pesavel = false;
                                    idTipoEmbalagem = 4;
                                    break;
                            }
                        }

                    } else {
                        codigoBalanca = -1;
                        eBalanca = false;
                        pesavel = false;

                        if ((rst.getString("PrazoValidade") != null)
                                && (!rst.getString("PrazoValidade").trim().isEmpty())) {
                            validade = Integer.parseInt(rst.getString("PrazoValidade").trim());
                        } else {
                            validade = 0;
                        }

                        if (null != rst.getString("SiglaUnidade").trim()) {
                            switch (rst.getString("SiglaUnidade").trim()) {
                                case "KG":
                                    idTipoEmbalagem = 4;
                                    break;
                                case "UN":
                                    idTipoEmbalagem = 0;
                                    break;
                                default:
                                    idTipoEmbalagem = 0;
                                    break;
                            }
                        }
                    }
                } else {
                    codigoBalanca = -1;
                    eBalanca = false;
                    pesavel = false;

                    if ((rst.getString("PrazoValidade") != null)
                            && (!rst.getString("PrazoValidade").trim().isEmpty())) {
                        validade = Integer.parseInt(rst.getString("PrazoValidade").trim());
                    } else {
                        validade = 0;
                    }

                    if (null != rst.getString("SiglaUnidade").trim()) {
                        switch (rst.getString("SiglaUnidade").trim()) {
                            case "KG":
                                idTipoEmbalagem = 4;
                                break;
                            case "UN":
                                idTipoEmbalagem = 0;
                                break;
                            default:
                                idTipoEmbalagem = 0;
                                break;
                        }
                    }
                }

                if ((rst.getString("Descricao") != null)
                        && (!rst.getString("Descricao").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("Descricao");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descriaoCompleta = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descriaoCompleta = "";
                }

                if ((rst.getString("DescricaoTecnica") != null)
                        && (!rst.getString("DescricaoTecnica").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DescricaoTecnica");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = descriaoCompleta;
                }

                descricaoGondola = descricaoReduzida;

                if ((rst.getString("QuantidadeProduto") != null)
                        && (!rst.getString("QuantidadeProduto").trim().isEmpty())) {

                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("QuantidadeProduto").trim());
                } else {
                    qtdEmbalagem = 1;
                }

                idFamilia = -1;

                if ((rst.getString("CodigoGrupoProduto") != null)
                        && (!rst.getString("CodigoGrupoProduto").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("CodigoGrupoProduto"));
                } else {
                    mercadologico1 = -1;
                }

                if ((rst.getString("CodigoSubGrupoProduto") != null)
                        && (!rst.getString("CodigoSubGrupoProduto").trim().isEmpty())) {

                    if (rst.getInt("CodigoSubGrupoProduto") < 1) {
                        mercadologico2 = 1;
                    } else {
                        mercadologico2 = Integer.parseInt(rst.getString("CodigoSubGrupoProduto"));
                    }

                } else {
                    mercadologico2 = -1;
                }

                mercadologico3 = 1;

                if (!util.verificaExisteMercadologico(mercadologico1, mercadologico2, mercadologico3)) {

                    sql = new StringBuilder();
                    sql.append("select mercadologico1 as mercadologico1 ");
                    sql.append("from mercadologico ");
                    sql.append("where descricao like '%ACERTAR%' ");
                    rstPostgres = stmPostgres.executeQuery(sql.toString());

                    if (rstPostgres.next()) {
                        mercadologico1 = rstPostgres.getInt("mercadologico1");
                        mercadologico2 = 1;
                        mercadologico3 = 1;
                    }
                }

                if ((rst.getString("CodigoNCM") != null)
                        && (!rst.getString("CodigoNCM").trim().isEmpty())
                        && (rst.getString("CodigoNCM").trim().length() > 5)) {

                    ncmAtual = Utils.formataNumero(rst.getString("CodigoNCM").trim());
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try {
                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                        } catch (Exception ex) {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                } else if ((rst.getString("ClassificacaoFiscal") != null)
                        && (!rst.getString("ClassificacaoFiscal").trim().isEmpty())
                        && (rst.getString("ClassificacaoFiscal").trim().length() > 5)) {

                    ncmAtual = Utils.formataNumero(rst.getString("ClassificacaoFiscal").trim());
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try {
                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                        } catch (Exception ex) {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf(idProduto));
                } else {

                    if ((rst.getString("NumeroCodigoBarraProduto") != null)
                            && (!rst.getString("NumeroCodigoBarraProduto").trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").replace(".", "").trim());

                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {
                            codigoBarras = -1;
                        } else {
                            codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").trim()));
                        }
                    } else {
                        codigoBarras = -1;
                    }
                }

                if ((rst.getString("SituacaoTributariaPIS") != null)
                        && (!rst.getString("SituacaoTributariaPIS").trim().isEmpty())) {
                    idTipoPisCofins = Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("SituacaoTributariaPIS").trim()));
                } else {
                    idTipoPisCofins = 1;
                }

                if ((rst.getString("SituacaoTributariaCOFINSEnt") != null)
                        && (!rst.getString("SituacaoTributariaCOFINSEnt").trim().isEmpty())) {

                    idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("SituacaoTributariaCOFINSEnt").trim()));
                } else {
                    idTipoPisCofinsCredito = 13;
                }

                if ((rst.getString("NaturezaReceitaPisCofins") != null)
                        && (!rst.getString("NaturezaReceitaPisCofins").trim().isEmpty())) {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins,
                            rst.getString("NaturezaReceitaPisCofins").trim());
                } else {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                }

                if ((rst.getString("CodigoGrupoFiscal") != null)
                        && (!rst.getString("CodigoGrupoFiscal").trim().isEmpty())) {
                    idAliquota = retornarIcmsKairos(rst.getString("CodigoGrupoFiscal").trim());
                } else {
                    idAliquota = 8;
                }

                precoVenda = 0;

                custo = 0;

                if ((rst.getString("MargemLucroTeorica") != null)
                        && (!rst.getString("MargemLucroTeorica").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("MargemLucroTeorica"));
                } else {
                    margem = 0;
                }

                if ((rst.getString("PesoBruto") != null)
                        && (!rst.getString("PesoBruto").trim().isEmpty())) {
                    pesoBruto = Double.parseDouble(rst.getString("PesoBruto"));
                } else {
                    pesoBruto = 0;
                }

                if ((rst.getString("PesoLiquido") != null)
                        && (!rst.getString("PesoLiquido").trim().isEmpty())) {
                    pesoLiquido = Double.parseDouble(rst.getString("PesoLiquido"));
                } else {
                    pesoLiquido = 0;
                }

                if (descriaoCompleta.length() > 60) {

                    descriaoCompleta = descriaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {

                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {

                    descricaoGondola = descricaoGondola.substring(0, 60);
                }

                oProduto.id = idProduto;
                oProduto.descricaoCompleta = descriaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = idFamilia;
                oProduto.idFornecedorFabricante = 1;
                oProduto.sugestaoPedido = true;
                oProduto.aceitaMultiplicacaoPdv = true;
                oProduto.sazonal = false;
                oProduto.fabricacaoPropria = false;
                oProduto.consignado = false;
                oProduto.ddv = 0;
                oProduto.permiteTroca = true;
                oProduto.vendaControlada = false;
                oProduto.vendaPdv = true;
                oProduto.conferido = true;
                oProduto.permiteQuebra = true;
                oProduto.permitePerda = true;
                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                oProduto.utilizaValidadeEntrada = false;
                oProduto.validade = validade;
                oProduto.margem = margem;
                oProduto.pesoBruto = pesoBruto;
                oProduto.pesoLiquido = pesoLiquido;
                oProduto.dataCadastro = dataCadastro;
                oProduto.eBalanca = eBalanca;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;

                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                oAliquota.idEstado = 26;
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;

                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;

                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.codigoanterior = codigoAnterior;

                if ((rst.getString("NumeroCodigoBarraProduto") != null)
                        && (!rst.getString("NumeroCodigoBarraProduto").trim().isEmpty())) {
                    oCodigoAnterior.barras = Long.parseLong(
                            Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").trim()));
                } else {
                    oCodigoAnterior.barras = -1;
                }

                if ((rst.getString("SituacaoTributariaPIS") != null)
                        && (!rst.getString("SituacaoTributariaPIS").trim().isEmpty())) {
                    oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("SituacaoTributariaPIS").trim());
                } else {
                    oCodigoAnterior.piscofinsdebito = -1;
                }

                if ((rst.getString("SituacaoTributariaCOFINSEnt") != null)
                        && (!rst.getString("SituacaoTributariaCOFINSEnt").trim().isEmpty())) {
                    oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("SituacaoTributariaCOFINSEnt").trim());
                } else {
                    oCodigoAnterior.piscofinscredito = -1;
                }

                if ((rst.getString("NaturezaReceitaPisCofins") != null)
                        && (!rst.getString("NaturezaReceitaPisCofins").trim().isEmpty())) {
                    oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("NaturezaReceitaPisCofins").trim());
                } else {
                    oCodigoAnterior.naturezareceita = -1;
                }

                if ((rst.getString("CodigoGrupoFiscal") != null)
                        && (!rst.getString("CodigoGrupoFiscal").trim().isEmpty())) {
                    oCodigoAnterior.ref_icmsdebito = Utils.acertarTexto(rst.getString("CodigoGrupoFiscal").trim().replace("'", ""));
                } else {
                    oCodigoAnterior.ref_icmsdebito = "";
                }

                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;

                if ((rst.getString("CodigoNCM") != null)
                        && (!rst.getString("CodigoNCM").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("CodigoNCM").trim();
                } else if ((rst.getString("ClassificacaoFiscal") != null)
                        && (!rst.getString("ClassificacaoFiscal").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("ClassificacaoFiscal").trim();
                } else {
                    oCodigoAnterior.ncm = "";
                }

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            stm.close();
            stmPostgres.close();
            return vProduto;

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public Map<Double, ProdutoVO> carregarProdutoKairosMaior6Digitos() throws Exception {
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
                idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
                ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, pesoBruto, pesoLiquido, idProduto;

        try {

            stmPostgres = Conexao.createStatement();

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select p.CodigoProduto, p.CodigoNCM, p.CodigoGrupoProduto, p.CodigoSubGrupoProduto, ");
            sql.append("p.Descricao, p.DescricaoTecnica, p.Observacoes, p.SiglaUnidade, p.PesoBruto, ");
            sql.append("p.PesoLiquido, p.PrazoValidade, p.DataCadastro, p.MargemLucroTeorica, ");
            sql.append("p.Situacao, p.ClassificacaoFiscal, p.SituacaoTributariaPISEnt, p.SituacaoTributariaPIS, ");
            sql.append("p.SituacaoTributariaCOFINSEnt, p.SituacaoTributariaCOFINS, p.NaturezaReceitaPisCofins, ");
            sql.append("codB.NumeroCodigoBarraProduto, codB.SiglaUnidade TipoEmbalagem, codB.QuantidadeProduto, ");
            sql.append("sitTrib.CodigoSituacaoTributariaB, sitTrib.Descricao, ");
            sql.append("imp.PercentualICMS, imp.PercentualICMSReduzido, imp.MargemLucro, ");
            sql.append("gfp.CodigoGrupoFiscal, gf.Descricao ");
            sql.append("from Produto p ");
            sql.append("left join SituacaoTributariaB sitTrib on sitTrib.CodigoSituacaoTributariaB = p.SituacaoTributariaB ");
            sql.append("left join CodigoBarraProduto codB on codB.CodigoProduto = p.CodigoProduto ");
            sql.append("left join ImpostoProduto imp on imp.CodigoProduto = p.CodigoProduto ");
            sql.append("left join GrupoFiscalProduto gfp on gfp.CodigoProduto = p.CodigoProduto ");
            sql.append("inner join GrupoFiscal gf on gf.CodigoGrupoFiscal = gfp.CodigoGrupoFiscal ");
            sql.append("where p.CodigoProduto > 999999 ");
            sql.append(" and imp.OrigemSiglaPais = 'BRA' ");
            sql.append(" and imp.DestinoSiglaPais = 'BRA' ");
            sql.append(" and imp.OrigemSiglaUF = 'PE' ");
            sql.append(" and imp.DestinoSiglaUF = 'PE' ");
            sql.append("and gfp.SiglaPais = 'BRA' ");
            sql.append("and gfp.SiglaUF = 'PE' ");
            sql.append("and gf.SiglaUF =  'PE' ");
            sql.append(" order by p.CodigoProduto; ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                if ((rst.getString("DataCadastro") != null)
                        && (!rst.getString("DataCadastro").trim().isEmpty())) {
                    dataCadastro = rst.getString("DataCadastro").substring(0, 10).replace("-", "/");
                } else {
                    dataCadastro = "";
                }

                if ((rst.getString("Situacao") != null)
                        && (!rst.getString("Situacao").trim().isEmpty())) {

                    if ("A".equals(rst.getString("Situacao").trim())) {
                        idSituacaoCadastro = 1;
                    } else {
                        idSituacaoCadastro = 0;
                    }
                } else {
                    idSituacaoCadastro = 1;
                }

                codigoAnterior = Double.parseDouble(rst.getString("CodigoProduto").trim().replace(".", ""));
                idProduto = Integer.parseInt(rst.getString("CodigoProduto").trim().replace(".", ""));

                if ((rst.getString("NumeroCodigoBarraProduto") != null)
                        && (!rst.getString("NumeroCodigoBarraProduto").trim().isEmpty())) {

                    sql = new StringBuilder();
                    sql.append("select codigo, descricao, pesavel, validade ");
                    sql.append("from implantacao.produtobalanca ");
                    sql.append("where cast(codigo as numeric(14,0)) = " + Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").replace(".", "")));

                    rstPostgres = stmPostgres.executeQuery(sql.toString());

                    if (rstPostgres.next()) {

                        eBalanca = true;
                        codigoBalanca = rstPostgres.getInt("codigo");
                        validade = rstPostgres.getInt("validade");

                        if (null != rstPostgres.getString("pesavel").trim()) {
                            switch (rstPostgres.getString("pesavel").trim()) {
                                case "U":
                                    pesavel = true;
                                    idTipoEmbalagem = 0;
                                    break;
                                case "P":
                                    pesavel = false;
                                    idTipoEmbalagem = 4;
                                    break;
                            }
                        }

                    } else {
                        codigoBalanca = -1;
                        eBalanca = false;
                        pesavel = false;

                        if ((rst.getString("PrazoValidade") != null)
                                && (!rst.getString("PrazoValidade").trim().isEmpty())) {
                            validade = Integer.parseInt(rst.getString("PrazoValidade").trim());
                        } else {
                            validade = 0;
                        }

                        if (null != rst.getString("SiglaUnidade").trim()) {
                            switch (rst.getString("SiglaUnidade").trim()) {
                                case "KG":
                                    idTipoEmbalagem = 4;
                                    break;
                                case "UN":
                                    idTipoEmbalagem = 0;
                                    break;
                                default:
                                    idTipoEmbalagem = 0;
                                    break;
                            }
                        }
                    }
                } else {
                    codigoBalanca = -1;
                    eBalanca = false;
                    pesavel = false;

                    if ((rst.getString("PrazoValidade") != null)
                            && (!rst.getString("PrazoValidade").trim().isEmpty())) {
                        validade = Integer.parseInt(rst.getString("PrazoValidade").trim());
                    } else {
                        validade = 0;
                    }

                    if (null != rst.getString("SiglaUnidade").trim()) {
                        switch (rst.getString("SiglaUnidade").trim()) {
                            case "KG":
                                idTipoEmbalagem = 4;
                                break;
                            case "UN":
                                idTipoEmbalagem = 0;
                                break;
                            default:
                                idTipoEmbalagem = 0;
                                break;
                        }
                    }
                }

                if ((rst.getString("Descricao") != null)
                        && (!rst.getString("Descricao").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("Descricao");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descriaoCompleta = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descriaoCompleta = "";
                }

                if ((rst.getString("DescricaoTecnica") != null)
                        && (!rst.getString("DescricaoTecnica").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DescricaoTecnica");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = descriaoCompleta;
                }

                descricaoGondola = descricaoReduzida;

                if ((rst.getString("QuantidadeProduto") != null)
                        && (!rst.getString("QuantidadeProduto").trim().isEmpty())) {

                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("QuantidadeProduto").trim());
                } else {
                    qtdEmbalagem = 1;
                }

                idFamilia = -1;

                if ((rst.getString("CodigoGrupoProduto") != null)
                        && (!rst.getString("CodigoGrupoProduto").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("CodigoGrupoProduto"));
                } else {
                    mercadologico1 = -1;
                }

                if ((rst.getString("CodigoSubGrupoProduto") != null)
                        && (!rst.getString("CodigoSubGrupoProduto").trim().isEmpty())) {

                    if (rst.getInt("CodigoSubGrupoProduto") < 1) {
                        mercadologico2 = 1;
                    } else {
                        mercadologico2 = Integer.parseInt(rst.getString("CodigoSubGrupoProduto"));
                    }

                } else {
                    mercadologico2 = -1;
                }

                mercadologico3 = 1;

                if (!util.verificaExisteMercadologico(mercadologico1, mercadologico2, mercadologico3)) {

                    sql = new StringBuilder();
                    sql.append("select mercadologico1 as mercadologico1 ");
                    sql.append("from mercadologico ");
                    sql.append("where descricao like '%ACERTAR%' ");
                    rstPostgres = stmPostgres.executeQuery(sql.toString());

                    if (rstPostgres.next()) {
                        mercadologico1 = rstPostgres.getInt("mercadologico1");
                        mercadologico2 = 1;
                        mercadologico3 = 1;
                    }
                }

                if ((rst.getString("CodigoNCM") != null)
                        && (!rst.getString("CodigoNCM").trim().isEmpty())
                        && (rst.getString("CodigoNCM").trim().length() > 5)) {

                    ncmAtual = Utils.formataNumero(rst.getString("CodigoNCM").trim());
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try {
                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                        } catch (Exception ex) {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                } else if ((rst.getString("ClassificacaoFiscal") != null)
                        && (!rst.getString("ClassificacaoFiscal").trim().isEmpty())
                        && (rst.getString("ClassificacaoFiscal").trim().length() > 5)) {

                    ncmAtual = Utils.formataNumero(rst.getString("ClassificacaoFiscal").trim());
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try {
                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                        } catch (Exception ex) {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if (eBalanca == true) {
                    codigoBarras = -1;
                } else {

                    if ((rst.getString("NumeroCodigoBarraProduto") != null)
                            && (!rst.getString("NumeroCodigoBarraProduto").trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").replace(".", "").trim());

                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {
                            codigoBarras = -1;
                        } else {
                            codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").trim()));
                        }
                    } else {
                        codigoBarras = -1;
                    }
                }

                if ((rst.getString("SituacaoTributariaPIS") != null)
                        && (!rst.getString("SituacaoTributariaPIS").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("SituacaoTributariaPIS").trim()));
                } else {
                    idTipoPisCofins = 1;
                }

                if ((rst.getString("SituacaoTributariaCOFINSEnt") != null)
                        && (!rst.getString("SituacaoTributariaCOFINSEnt").trim().isEmpty())) {

                    idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("SituacaoTributariaCOFINSEnt").trim()));
                } else {
                    idTipoPisCofinsCredito = 13;
                }

                if ((rst.getString("NaturezaReceitaPisCofins") != null)
                        && (!rst.getString("NaturezaReceitaPisCofins").trim().isEmpty())) {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins,
                            rst.getString("NaturezaReceitaPisCofins").trim());
                } else {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                }

                if ((rst.getString("CodigoGrupoFiscal") != null)
                        && (!rst.getString("CodigoGrupoFiscal").trim().isEmpty())) {
                    idAliquota = retornarIcmsKairos(rst.getString("CodigoGrupoFiscal").trim());
                } else {
                    idAliquota = 8;
                }

                precoVenda = 0;

                custo = 0;

                if ((rst.getString("MargemLucroTeorica") != null)
                        && (!rst.getString("MargemLucroTeorica").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("MargemLucroTeorica"));
                } else {
                    margem = 0;
                }

                if ((rst.getString("PesoBruto") != null)
                        && (!rst.getString("PesoBruto").trim().isEmpty())) {
                    pesoBruto = Double.parseDouble(rst.getString("PesoBruto"));
                } else {
                    pesoBruto = 0;
                }

                if ((rst.getString("PesoLiquido") != null)
                        && (!rst.getString("PesoLiquido").trim().isEmpty())) {
                    pesoLiquido = Double.parseDouble(rst.getString("PesoLiquido"));
                } else {
                    pesoLiquido = 0;
                }

                if (descriaoCompleta.length() > 60) {

                    descriaoCompleta = descriaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {

                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {

                    descricaoGondola = descricaoGondola.substring(0, 60);
                }

                oProduto.idDouble = idProduto;
                oProduto.descricaoCompleta = descriaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = idFamilia;
                oProduto.idFornecedorFabricante = 1;
                oProduto.sugestaoPedido = true;
                oProduto.aceitaMultiplicacaoPdv = true;
                oProduto.sazonal = false;
                oProduto.fabricacaoPropria = false;
                oProduto.consignado = false;
                oProduto.ddv = 0;
                oProduto.permiteTroca = true;
                oProduto.vendaControlada = false;
                oProduto.vendaPdv = true;
                oProduto.conferido = true;
                oProduto.permiteQuebra = true;
                oProduto.permitePerda = true;
                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                oProduto.utilizaValidadeEntrada = false;
                oProduto.validade = validade;
                oProduto.margem = margem;
                oProduto.pesoBruto = pesoBruto;
                oProduto.pesoLiquido = pesoLiquido;
                oProduto.dataCadastro = dataCadastro;
                oProduto.eBalanca = eBalanca;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;

                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                oAliquota.idEstado = 26;
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;

                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;

                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.codigoanterior = codigoAnterior;

                if ((rst.getString("NumeroCodigoBarraProduto") != null)
                        && (!rst.getString("NumeroCodigoBarraProduto").trim().isEmpty())) {
                    oCodigoAnterior.barras = Long.parseLong(
                            Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").trim()));
                } else {
                    oCodigoAnterior.barras = -1;
                }

                if ((rst.getString("SituacaoTributariaPIS") != null)
                        && (!rst.getString("SituacaoTributariaPIS").trim().isEmpty())) {
                    oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("SituacaoTributariaPIS").trim());
                } else {
                    oCodigoAnterior.piscofinsdebito = -1;
                }

                if ((rst.getString("SituacaoTributariaCOFINSEnt") != null)
                        && (!rst.getString("SituacaoTributariaCOFINSEnt").trim().isEmpty())) {
                    oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("SituacaoTributariaCOFINSEnt").trim());
                } else {
                    oCodigoAnterior.piscofinscredito = -1;
                }

                if ((rst.getString("NaturezaReceitaPisCofins") != null)
                        && (!rst.getString("NaturezaReceitaPisCofins").trim().isEmpty())) {
                    oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("NaturezaReceitaPisCofins").trim());
                } else {
                    oCodigoAnterior.naturezareceita = -1;
                }

                if ((rst.getString("CodigoGrupoFiscal") != null)
                        && (!rst.getString("CodigoGrupoFiscal").trim().isEmpty())) {
                    oCodigoAnterior.ref_icmsdebito = Utils.acertarTexto(rst.getString("CodigoGrupoFiscal").trim().replace("'", ""));
                } else {
                    oCodigoAnterior.ref_icmsdebito = "";
                }

                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;

                if ((rst.getString("CodigoNCM") != null)
                        && (!rst.getString("CodigoNCM").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("CodigoNCM").trim();
                } else if ((rst.getString("ClassificacaoFiscal") != null)
                        && (!rst.getString("ClassificacaoFiscal").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("ClassificacaoFiscal").trim();
                } else {
                    oCodigoAnterior.ncm = "";
                }

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            stm.close();
            stmPostgres.close();
            return vProduto;

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public Map<Double, ProdutoVO> carregarPrecoProdutoKairos(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double preco = 0, idProduto;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();

            sql.append("select CodigoProduto, SiglaUnidade, PrecoNormal ");
            sql.append("from vwPrecoVendaProduto ");
            sql.append("where CodigoFilial = " + id_lojaCliente + " ");
            sql.append("and PrecoNormal <> 0 ");
            sql.append("and CodigoCondicaoPagamento = 1 ");
            sql.append("and CodigoPrazoPagamento = 30 ");
            /*sql.append("select CodigoProduto, PrecoNormal ");
             sql.append("from PrecoVendaProduto ");
             sql.append("where CodigoFilial = " + id_lojaCliente + " ");
             sql.append("and SiglaUnidade <> 'DP' ");
             sql.append("and PrecoNormal <> 0 ");*/

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("CodigoProduto"));

                if ((rst.getString("PrecoNormal") != null)
                        && (!rst.getString("PrecoNormal").trim().isEmpty())) {
                    preco = Double.parseDouble(rst.getString("PrecoNormal"));
                } else {
                    preco = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = preco;
                oComplemento.precoDiaSeguinte = preco;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.precovenda = preco;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Double, ProdutoVO> carregarCustoProdutoKairos(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("WITH data AS ( ");
            sql.append("select c.CodigoProduto, MAX(DataMovimento) as data ");
            sql.append("from CustoProduto as c where c.CodigoFilial in (" + idLojaCliente + ") group by c.CodigoProduto) ");
            sql.append("select c.CodigoProduto, c.ValorCustoReal, c.DataMovimento ");
            sql.append("from CustoProduto c ");
            sql.append("inner join data dt on dt.CodigoProduto = c.CodigoProduto and dt.data = c.DataMovimento ");
            sql.append("where c.CodigoFilial = " + idLojaCliente + " ");
            sql.append("order by c.CodigoProduto; ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("CodigoProduto"));

                if ((rst.getString("ValorCustoReal") != null)
                        && (!rst.getString("ValorCustoReal").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("ValorCustoReal"));
                } else {
                    custo = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.custocomimposto = custo;
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Double, ProdutoVO> carregarEstoqueProdutoKairos(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double saldo = 0, idProduto;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("WITH data AS ( ");
            sql.append("select e.CodigoProduto, MAX(AlteracaoDataHora) as data ");
            sql.append("from EstoqueProduto as e where e.CodigoFilial in (" + id_lojaCliente + ") group by e.CodigoProduto) ");
            sql.append("select e.CodigoProduto, e.QuantidadeSaldoEstoque, e.AlteracaoDataHora ");
            sql.append("from EstoqueProduto e ");
            sql.append("inner join data dt on dt.CodigoProduto = e.CodigoProduto and dt.data = e.AlteracaoDataHora ");
            sql.append("where e.CodigoFilial = " + id_lojaCliente + " ");
            sql.append("order by e.CodigoProduto; ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("CodigoProduto"));

                if ((rst.getString("QuantidadeSaldoEstoque") != null)
                        && (!rst.getString("QuantidadeSaldoEstoque").trim().isEmpty())
                        && (rst.getDouble("QuantidadeSaldoEstoque") < 100000000000.0)) {
                    saldo = Double.parseDouble(rst.getString("QuantidadeSaldoEstoque"));
                } else {
                    saldo = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.estoque = saldo;
                oProduto.vComplemento.add(oComplemento);
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.estoque = saldo;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<EstoqueTerceiroVO> carregarEstoqueTerceiroKairos(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<EstoqueTerceiroVO> vEstoque = new ArrayList<>();
        int codigoDeposito = 0;
        boolean isLoja;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select v.CodigoProduto, v.SaldoEstoque, v.Disponivel, v.CodigoDeposito, "
                    + "b.NumeroCodigoBarraProduto, p.Descricao "
                    + "from vwEstoqueDisponivelProduto v "
                    + "inner join CodigoBarraProduto b on b.CodigoProduto = v.CodigoProduto "
                    + "inner join Produto p on p.CodigoProduto = v.CodigoProduto "
                    + "where CodigoFilial = " + id_lojaCliente
                    + " and SaldoEstoque <> 0 ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                isLoja = true;
                codigoDeposito = rst.getInt("CodigoDeposito");

                if (codigoDeposito == 1000) {
                    codigoDeposito = 100;
                } else if (codigoDeposito == 3000) {
                    codigoDeposito = 300;
                } else if (codigoDeposito == 5000) {
                    codigoDeposito = 500;
                } else if (codigoDeposito == 4000) {
                    codigoDeposito = 400;
                }

                LojaVO oLoja = new LojaDAO().carregar2(codigoDeposito);
                isLoja = new LojaDAO().isLoja(oLoja.id);
                if (isLoja) {
                    EstoqueTerceiroVO oEstoque = new EstoqueTerceiroVO();
                    oEstoque.setId_produto(rst.getDouble("CodigoProduto"));
                    oEstoque.setQuantidade(rst.getDouble("SaldoEstoque"));
                    oEstoque.setId_loja(oLoja.id);
                    oEstoque.setId_lojaterceiro(idLoja);
                    oEstoque.setCodigoBarras(rst.getLong("NumeroCodigoBarraProduto"));
                    oEstoque.setDescProduto(Utils.acertarTexto(rst.getString("Descricao").trim().replace("'", "")));
                    vEstoque.add(oEstoque);
                }
            }
            return vEstoque;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasKairos() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        double idProduto;
        long codigobarras;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select NumeroCodigoBarraProduto, CodigoProduto, ");
            sql.append("SiglaUnidade, QuantidadeProduto ");
            sql.append("from CodigoBarraProduto ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("CodigoProduto"));

                if ((rst.getString("NumeroCodigoBarraProduto") != null)
                        && (!rst.getString("NumeroCodigoBarraProduto").trim().isEmpty())) {
                    codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto")));
                } else {
                    codigobarras = -1;
                }

                if (String.valueOf(codigobarras).length() >= 7) {

                    if (String.valueOf(codigobarras).length() > 14) {
                        codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                    }

                    ProdutoVO oProduto = new ProdutoVO();

                    oProduto.idDouble = idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigobarras;

                    oProduto.vAutomacao.add(oAutomacao);

                    vProduto.put(codigobarras, oProduto);
                }

            }

            return vProduto;

        } catch (Exception ex) {

            throw ex;
        }
    }

    private List<ProdutoVO> carregarCodigoBarrasAnteriorKairos() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;
        long codigoBarras = 0;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select NumeroCodigoBarraProduto, CodigoProduto, ");
            sql.append("       SiglaUnidade, QuantidadeProduto ");
            sql.append("  from CodigoBarraProduto");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                if ((rst.getString("NumeroCodigoBarraProduto") != null)
                        && (!rst.getString("NumeroCodigoBarraProduto").trim().isEmpty())) {

                    idProduto = Double.parseDouble(Utils.acertarTexto(rst.getString("CodigoProduto").trim()));
                    codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("NumeroCodigoBarraProduto").trim()));

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.idDouble = idProduto;
                    oProduto.codigoBarras = codigoBarras;
                    vProduto.add(oProduto);
                }
            }
            stm.close();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarDataCadastroProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;
        String dataCadastro = "";

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select CodigoProduto, DataCadastro ");
            sql.append("from Produto ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("CodigoProduto").trim()));

                if ((rst.getString("DataCadastro") != null)
                        && (!rst.getString("DataCadastro").trim().isEmpty())) {

                    dataCadastro = rst.getString("DataCadastro").trim().substring(0, 10).replace(".", "/").replace("-", "/");

                } else {
                    dataCadastro = "";
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.dataCadastro = dataCadastro;

                vProduto.add(oProduto);
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarIcmsProduto() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto;
        int idEstado, idAliquota;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();

            sql.append("select CodigoProduto, PercentualICMS, PercentualICMSReduzido, DestinoSiglaUF ");
            sql.append("from ImpostoProduto ");
            sql.append("where OrigemSiglaUF = 'PE' ");
            sql.append("and DestinoSiglaUF <> 'PE' ");
            sql.append("and OrigemSiglaPais = 'BRA' ");
            sql.append("and DestinoSiglaPais = 'BRA' ");
            sql.append("order by CodigoProduto ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("CodigoProduto"));

                if ((rst.getString("PercentualICMS") != null)
                        && (!rst.getString("PercentualICMS").trim().isEmpty())
                        && (rst.getString("PercentualICMSReduzido") != null)
                        && (!rst.getString("PercentualICMSReduzido").trim().isEmpty())) {

                    if ("7.000".equals(rst.getString("PercentualICMS"))
                            && ("0.000".equals(rst.getString("PercentualICMSReduzido")))) {
                        idAliquota = 0;
                    } else if ("12.000".equals(rst.getString("PercentualICMS"))
                            && ("0.000".equals(rst.getString("PercentualICMSReduzido")))) {
                        idAliquota = 1;
                    } else if ("18.000".equals(rst.getString("PercentualICMS"))
                            && ("0.000".equals(rst.getString("PercentualICMSReduzido")))) {
                        idAliquota = 2;
                    } else if ("25.000".equals(rst.getString("PercentualICMS"))
                            && ("0.000".equals(rst.getString("PercentualICMSReduzido")))) {
                        idAliquota = 3;
                    } else if ("17.000".equals(rst.getString("PercentualICMS"))
                            && ("0.000".equals(rst.getString("PercentualICMSReduzido")))) {
                        idAliquota = 18;
                    } else if ("27.000".equals(rst.getString("PercentualICMS"))
                            && ("0.000".equals(rst.getString("PercentualICMSReduzido")))) {
                        idAliquota = 19;
                    } else {
                        idAliquota = 8;
                    }

                    if ((rst.getString("DestinoSiglaUF") != null)
                            && (!rst.getString("DestinoSiglaUF").trim().isEmpty())) {
                        idEstado = Utils.retornarEstadoDescricao(rst.getString("DestinoSiglaUF").trim());

                        if (idEstado != 0) {

                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.idDouble = idProduto;

                            ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                            oAliquota.idEstado = idEstado;
                            oAliquota.idAliquotaDebito = idAliquota;
                            oAliquota.idAliquotaCredito = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                            oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                            oProduto.vAliquota.add(oAliquota);

                            vProduto.add(oProduto);
                        }
                    }
                }
            }
            stm.close();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<FornecedorVO> carregarFornecedorKairos() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                numero = "", complemento = "", telefone = "", email = "", fax = "", orgaoExp = "";
        int id, id_municipio = 0, id_estado, id_tipoinscricao = 0;
        long cnpj, cep;
        boolean ativo = true;

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select tp.CodigoTipoPessoa, p.CodigoPessoa, p.RazaoSocial, p.NomeFantasia, ");
            sql.append("       p.DataNascimento, p.Sexo, p.EstadoCivil, p.Contato, p.Observacoes, ");
            sql.append("       p.DataCadastro, endP.Endereco, endP.Numero, endP.Bairro, endP.Complemento, ");
            sql.append("       m.CodigoIBGEMunicipio, m.Nome, m.SiglaUF, endP.CEP, endP.PontoReferencia, ");
            sql.append("       endP.ContatoEndereco, ");
            sql.append("(select top(1) telP.NumeroTelefone ");
            sql.append("   from TelefonePessoa telP ");
            sql.append("  where telP.CodigoPessoa = p.CodigoPessoa) as Telefone, ");
            sql.append("(select docCnpj.NumeroDocumento ");
            sql.append("   from DocumentoPessoa docCnpj ");
            sql.append("   left join Documento doc on doc.CodigoDocumento = docCnpj.CodigoDocumento ");
            sql.append("  where docCnpj.CodigoPessoa = p.CodigoPessoa ");
            sql.append("    and doc.CodigoDocumento = 1) as Cnpj, ");
            sql.append("(select docInscEst.NumeroDocumento ");
            sql.append("   from DocumentoPessoa docInscEst ");
            sql.append("   left join Documento doc on doc.CodigoDocumento = docInscEst.CodigoDocumento ");
            sql.append("  where docInscEst.CodigoPessoa = p.CodigoPessoa ");
            sql.append("    and doc.CodigoDocumento = 3) as InscricaoEstadual, ");
            sql.append("(select docRG.NumeroDocumento ");
            sql.append("   from DocumentoPessoa docRG ");
            sql.append("   left join Documento doc on doc.CodigoDocumento = docRG.CodigoDocumento ");
            sql.append("  where docRG.CodigoPessoa = p.CodigoPessoa ");
            sql.append("    and doc.CodigoDocumento = 2) as RG, ");
            sql.append("(select docRG.OrgaoExpedidor ");
            sql.append("   from DocumentoPessoa docRG ");
            sql.append("   left join Documento doc on doc.CodigoDocumento = docRG.CodigoDocumento ");
            sql.append("  where docRG.CodigoPessoa = p.CodigoPessoa ");
            sql.append("    and doc.CodigoDocumento = 2) as OrgaoExp ");
            sql.append("from Pessoa p ");
            sql.append("left join EnderecoPessoa endP on endP.CodigoPessoa = p.CodigoPessoa ");
            sql.append("left join Municipio m on endP.CodigoMunicipio = m.CodigoMunicipio ");
            sql.append("inner join TipoPessoa tp on tp.CodigoPessoa = p.CodigoPessoa and tp.CodigoTipoPessoa = 'F' ");
            sql.append("order by p.RazaoSocial; ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                FornecedorVO oFornecedor = new FornecedorVO();

                if ((rst.getString("RazaoSocial") != null)
                        && (!rst.getString("RazaoSocial").isEmpty())) {
                    byte[] bytes = rst.getBytes("RazaoSocial");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    razaosocial = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    razaosocial = "";
                }

                if ((rst.getString("NomeFantasia") != null)
                        && (!rst.getString("NomeFantasia").isEmpty())) {
                    byte[] bytes = rst.getBytes("NomeFantasia");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    nomefantasia = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    nomefantasia = "";
                }

                if ((rst.getString("Cnpj") != null)
                        && (!rst.getString("Cnpj").isEmpty())) {

                    if (rst.getString("Cnpj").trim().length() < 14) {
                        id_tipoinscricao = 1;
                    } else {
                        id_tipoinscricao = 0;
                    }

                    cnpj = Long.parseLong(Utils.formataNumero(rst.getString("Cnpj").trim()));
                } else {
                    cnpj = -1;
                }

                if ((rst.getString("InscricaoEstadual") != null)
                        && (!rst.getString("InscricaoEstadual").isEmpty())) {
                    inscricaoestadual = Utils.acertarTexto(rst.getString("InscricaoEstadual").replace("'", "").trim());
                } else if ((rst.getString("RG") != null)
                        && (!rst.getString("RG").trim().isEmpty())) {
                    inscricaoestadual = Utils.acertarTexto(rst.getString("RG").replace("'", "").trim());

                    if ((rst.getString("OrgaoExp") != null)
                            && (!rst.getString("OrgaoExp").trim().isEmpty())) {
                        orgaoExp = Utils.acertarTexto(rst.getString("OrgaoExp").replace("'", "").trim());
                    } else {
                        orgaoExp = "";
                    }
                } else {
                    inscricaoestadual = "ISENTO";
                }

                if ((rst.getString("Endereco") != null)
                        && (!rst.getString("Endereco").isEmpty())) {
                    endereco = Utils.acertarTexto(rst.getString("Endereco").replace("'", "").trim());
                } else {
                    endereco = "";
                }

                if ((rst.getString("Bairro") != null)
                        && (!rst.getString("Bairro").isEmpty())) {
                    bairro = Utils.acertarTexto(rst.getString("Bairro").replace("'", "").trim());
                } else {
                    bairro = "";
                }

                if ((rst.getString("CEP") != null)
                        && (!rst.getString("CEP").isEmpty())) {
                    cep = Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim()));
                } else {
                    cep = Long.parseLong("0");
                }

                if ((rst.getString("Nome") != null)
                        && (!rst.getString("Nome").isEmpty())) {

                    if ((rst.getString("SiglaUF") != null)
                            && (!rst.getString("SiglaUF").isEmpty())) {

                        id_municipio = util.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("Nome").replace("'", "").trim()),
                                Utils.acertarTexto(rst.getString("SiglaUF").replace("'", "").trim()));

                        if (id_municipio == 0) {
                            id_municipio = 2611606;
                        }
                    }
                } else {
                    id_municipio = 2611606;
                }

                if ((rst.getString("SiglaUF") != null)
                        && (!rst.getString("SiglaUF").isEmpty())) {
                    id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("SiglaUF").replace("'", "").trim()));

                    if (id_estado == 0) {
                        id_estado = 26;
                    }
                } else {
                    id_estado = 26;
                }

                if (rst.getString("Observacoes") != null) {
                    obs = Utils.acertarTexto(rst.getString("Observacoes").trim().replace("'", ""));
                } else {
                    obs = "";
                }

                if ((rst.getString("DataCadastro") != null)
                        && (!rst.getString("DataCadastro").trim().isEmpty())) {
                    datacadastro = rst.getString("DataCadastro").trim().substring(0, 10).replace("-", "/");
                } else {
                    datacadastro = "";
                }

                ativo = true;

                if ((rst.getString("Numero") != null)
                        && (!rst.getString("Numero").trim().isEmpty())) {
                    numero = Utils.acertarTexto(rst.getString("Numero").trim().replace("'", ""));
                } else {
                    numero = "0";
                }

                if ((rst.getString("Complemento") != null)
                        && (!rst.getString("Complemento").trim().isEmpty())) {
                    complemento = Utils.acertarTexto(rst.getString("Complemento").replace("'", "").trim());
                } else {
                    complemento = "";
                }

                if ((rst.getString("Telefone") != null)
                        && (!rst.getString("Telefone").trim().isEmpty())) {
                    telefone = Utils.formataNumero(rst.getString("Telefone").trim());
                } else {
                    telefone = "0";
                }

                if (razaosocial.length() > 40) {
                    razaosocial = razaosocial.substring(0, 40);
                }

                if (nomefantasia.length() > 30) {
                    nomefantasia = nomefantasia.substring(0, 30);
                }

                if (endereco.length() > 40) {
                    endereco = endereco.substring(0, 40);
                }

                if (bairro.length() > 30) {
                    bairro = bairro.substring(0, 30);
                }

                if (String.valueOf(cep).length() > 8) {
                    cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                }

                if (String.valueOf(cnpj).length() > 14) {
                    cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                }

                if (inscricaoestadual.length() > 20) {
                    inscricaoestadual = inscricaoestadual.substring(0, 20);
                }

                if (telefone.length() > 14) {
                    telefone = telefone.substring(0, 14);
                }

                oFornecedor.codigoanterior = Long.parseLong(Utils.formataNumero(rst.getString("CodigoPessoa").trim()));
                oFornecedor.razaosocial = razaosocial;
                oFornecedor.nomefantasia = nomefantasia;
                oFornecedor.endereco = endereco;
                oFornecedor.bairro = bairro;
                oFornecedor.numero = numero;
                oFornecedor.id_municipio = id_municipio;
                oFornecedor.cep = cep;
                oFornecedor.id_estado = id_estado;
                oFornecedor.id_tipoinscricao = id_tipoinscricao;
                oFornecedor.inscricaoestadual = inscricaoestadual;
                oFornecedor.cnpj = cnpj;
                oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                oFornecedor.observacao = obs;
                oFornecedor.complemento = complemento;
                oFornecedor.telefone = telefone;
                oFornecedor.email = email;
                oFornecedor.fax = fax;

                vFornecedor.add(oFornecedor);
            }
            return vFornecedor;

        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<FornecedorContatoVO> carregarFornecedorContato() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<FornecedorContatoVO> vFornecedorContato = new ArrayList<>();
        long idFornecedor = 0;
        String telefone = "", email = "", numeroRamal = "", contato = "";

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select p.CodigoPessoa, telP.NumeroTelefone, telP.NumeroRamal, ");
            sql.append("telP.ContatoTelefone, em.EnderecoEletronicoPessoa ");
            sql.append("from Pessoa p ");
            sql.append("left join TelefonePessoa telP on telP.CodigoPessoa = p.CodigoPessoa ");
            sql.append("left join EnderecoEletronicoPessoa em on em.CodigoPessoa = p.CodigoPessoa ");
            sql.append("inner join TipoPessoa tp on tp.CodigoPessoa = p.CodigoPessoa and tp.CodigoTipoPessoa = 'F' ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = Long.parseLong(rst.getString("CodigoPessoa").trim());

                if ((rst.getString("NumeroTelefone") != null)
                        && (!rst.getString("NumeroTelefone").trim().isEmpty())) {
                    telefone = Utils.formataNumero(rst.getString("NumeroTelefone").trim());
                } else {
                    telefone = "";
                }

                if ((rst.getString("NumeroRamal") != null)
                        && (!rst.getString("NumeroRamal").trim().isEmpty())) {
                    numeroRamal = "RAMAL: " + Utils.formataNumero(rst.getString("NumeroRamal").trim());
                } else {
                    numeroRamal = "";
                }

                if ((rst.getString("ContatoTelefone") != null)
                        && (!rst.getString("ContatoTelefone").trim().isEmpty())) {
                    contato = "CONTATO: " + Utils.acertarTexto(rst.getString("ContatoTelefone").trim().replace("'", ""));
                } else {
                    contato = "CONTATO";
                }

                if ((rst.getString("EnderecoEletronicoPessoa") != null)
                        && (!rst.getString("EnderecoEletronicoPessoa").trim().isEmpty())
                        && (rst.getString("EnderecoEletronicoPessoa").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("EnderecoEletronicoPessoa").trim().replace("'", ""));
                    email = email.toLowerCase();
                } else {
                    email = "";
                }

                FornecedorContatoVO oFornecedorContato = new FornecedorContatoVO();
                oFornecedorContato.setNome(contato);
                oFornecedorContato.setIdFornecedorAnterior(idFornecedor);
                oFornecedorContato.setTelefone(telefone);
                oFornecedorContato.setEmail(email);

                vFornecedorContato.add(oFornecedorContato);
            }

            return vFornecedorContato;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoFornecedorVO> carregarProdutoFornecedorKairos() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        double idProduto, idFornecedor;
        String codigoExterno;
        java.sql.Date dataAlteracao = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select CodigoPessoa, CodigoProduto, ");
            sql.append("Referencia, AlteracaoDataHora ");
            sql.append("from ProdutoFornecedor ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = Double.parseDouble(rst.getString("CodigoPessoa").trim());
                idProduto = Double.parseDouble(rst.getString("CodigoProduto"));

                if ((rst.getString("Referencia") != null)
                        && (!rst.getString("Referencia").isEmpty())) {
                    codigoExterno = Utils.acertarTexto(rst.getString("Referencia").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }

                if ((rst.getString("AlteracaoDataHora") != null)
                        && (!rst.getString("AlteracaoDataHora").trim().isEmpty())) {
                    dataAlteracao = new java.sql.Date(
                            sdf.parse(rst.getString("AlteracaoDataHora").substring(0, 10).replace("-", "/")).getTime());
                } else {
                    dataAlteracao = new Date(new java.util.Date().getTime());
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedorDouble = idFornecedor;
                oProdutoFornecedor.id_produtoDouble = idProduto;
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ClienteEventualVO> carregarClienteEventualKairos(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClienteEventualVO> vClienteEventual = new ArrayList<>();

        String nome, endereco, bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,
                dataResidencia, dataCadastro, numero, complemento, dataNascimento, nomePai, nomeMae,
                telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                conjuge = "", orgaoExp = "", observacao2 = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha = 0,
                estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;
        //DecimalFormat df = new DecimalFormat("#.00");

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select tp.CodigoTipoPessoa, p.CodigoPessoa, p.RazaoSocial, p.NomeFantasia, ");
            sql.append("       p.DataNascimento, p.Sexo, p.EstadoCivil, p.Contato, p.Observacoes, ");
            sql.append("       p.DataCadastro, endP.Endereco, endP.Numero, endP.Bairro, endP.Complemento, ");
            sql.append("       m.CodigoIBGEMunicipio, m.Nome, m.SiglaUF, endP.CEP, endP.PontoReferencia, ");
            sql.append("       endP.ContatoEndereco, ");
            sql.append("(select top(1) telP.NumeroTelefone ");
            sql.append("   from TelefonePessoa telP ");
            sql.append("  where telP.CodigoPessoa = p.CodigoPessoa) as Telefone, ");
            sql.append("(select docCnpj.NumeroDocumento ");
            sql.append("   from DocumentoPessoa docCnpj ");
            sql.append("   left join Documento doc on doc.CodigoDocumento = docCnpj.CodigoDocumento ");
            sql.append("  where docCnpj.CodigoPessoa = p.CodigoPessoa ");
            sql.append("    and doc.CodigoDocumento = 1) as Cnpj, ");
            sql.append("(select docInscEst.NumeroDocumento ");
            sql.append("   from DocumentoPessoa docInscEst ");
            sql.append("   left join Documento doc on doc.CodigoDocumento = docInscEst.CodigoDocumento ");
            sql.append("  where docInscEst.CodigoPessoa = p.CodigoPessoa ");
            sql.append("    and doc.CodigoDocumento = 3) as InscricaoEstadual, ");
            sql.append("(select docRG.NumeroDocumento ");
            sql.append("   from DocumentoPessoa docRG ");
            sql.append("   left join Documento doc on doc.CodigoDocumento = docRG.CodigoDocumento ");
            sql.append("  where docRG.CodigoPessoa = p.CodigoPessoa ");
            sql.append("    and doc.CodigoDocumento = 2) as RG, ");
            sql.append("(select docRG.OrgaoExpedidor ");
            sql.append("   from DocumentoPessoa docRG ");
            sql.append("   left join Documento doc on doc.CodigoDocumento = docRG.CodigoDocumento ");
            sql.append("  where docRG.CodigoPessoa = p.CodigoPessoa ");
            sql.append("    and doc.CodigoDocumento = 2) as OrgaoExp, ");
            sql.append(" (select top(1) c.LimiteCredito ");
            sql.append("   from Cliente c ");
            sql.append("  inner join DocumentoPessoa dp on dp.NumeroDocumento = c.CnpjCpfCliente ");
            sql.append("    and dp.CodigoPessoa = p.CodigoPessoa) LimiteCredito ");
            sql.append("from Pessoa p ");
            sql.append("left join EnderecoPessoa endP on endP.CodigoPessoa = p.CodigoPessoa ");
            sql.append("left join Municipio m on endP.CodigoMunicipio = m.CodigoMunicipio ");
            sql.append("inner join TipoPessoa tp on tp.CodigoPessoa = p.CodigoPessoa and tp.CodigoTipoPessoa = 'C' ");
            sql.append("order by p.RazaoSocial; ");

            rst = stm.executeQuery(sql.toString());
            Linha = 1;
            try {
                while (rst.next()) {
                    ClienteEventualVO oClienteEventual = new ClienteEventualVO();

                    id = rst.getInt("CodigoPessoa");
                    id_situacaocadastro = 1;
                    dataResidencia = "1990/01/01";

                    if ((rst.getString("Cnpj") != null)
                            && (!rst.getString("Cnpj").trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("Cnpj").trim()));

                        if (rst.getString("Cnpj").trim().length() < 14) {
                            id_tipoinscricao = 1;
                        } else {
                            id_tipoinscricao = 0;
                        }
                    } else {
                        cnpj = -1;
                        id_tipoinscricao = 1;
                    }

                    if ((rst.getString("RazaoSocial") != null)
                            && (!rst.getString("RazaoSocial").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("RazaoSocial");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {

                        if ((rst.getString("NomeFantasia") != null)
                                && (!rst.getString("NomeFantasia").trim().isEmpty())) {
                            byte[] bytes = rst.getBytes("RazaoSocial");
                            String textoAcertado = new String(bytes, "ISO-8859-1");
                            nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                        } else {
                            nome = "SEM NOME VR " + id;
                        }
                    }

                    if ((rst.getString("Endereco") != null)
                            && (!rst.getString("Endereco").trim().isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("Endereco").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("Bairro") != null)
                            && (!rst.getString("Bairro").trim().isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("Bairro").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("Numero") != null)
                            && (!rst.getString("Numero").trim().isEmpty())) {
                        numero = Utils.acertarTexto(rst.getString("Numero").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }

                    if ((rst.getString("Complemento") != null)
                            && (!rst.getString("Complemento").trim().isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("Complemento").trim().replace("'", ""));
                    } else {
                        complemento = "";
                    }

                    if ((rst.getString("Telefone") != null)
                            && (!rst.getString("Telefone").trim().isEmpty())) {
                        telefone1 = Utils.formataNumero(rst.getString("Telefone").trim());
                    } else {
                        telefone1 = "0";
                    }

                    if ((rst.getString("CEP") != null)
                            && (!rst.getString("CEP").trim().isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim()));
                    } else {
                        cep = 0;
                    }

                    if ((rst.getString("Nome") != null)
                            && (!rst.getString("Nome").trim().isEmpty())) {
                        if ((rst.getString("SiglaUF") != null)
                                && (!rst.getString("SiglaUF").trim().isEmpty())) {
                            id_municipio = util.retornarMunicipioIBGEDescricao(rst.getString("Nome").trim().replace("'", ""),
                                    rst.getString("SiglaUF").trim().replace("'", ""));

                            if (id_municipio == 0) {
                                id_municipio = 2611606;
                            }
                        } else {
                            id_municipio = 2611606;
                        }
                    } else {
                        id_municipio = 2611606;
                    }

                    if ((rst.getString("Nome") != null)
                            && (!rst.getString("Nome").trim().isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(
                                rst.getString("Nome").trim().replace("'", ""));

                        if (id_estado == 0) {
                            id_estado = 26;
                        } else {
                            id_estado = 26;
                        }
                    } else {
                        id_estado = 26;
                    }

                    if ((rst.getString("LimiteCredito") != null)
                            && (!rst.getString("LimiteCredito").trim().isEmpty())) {

                        limite = rst.getDouble("LimiteCredito");

                        if (limite == 1000000000) {
                            limite = 100000000;
                            observacao2 = "VALOR LIMITE SISTEMA KAIROS: "
                                    + rst.getString("LimiteCredito") + " = VALOR LIMITE VR: " + limite + ", VALOR SISTEMA KAIROS NÃO SUPORTADO PELO VR.";

                        }

                    } else {
                        limite = 0;
                    }

                    if ((rst.getString("InscricaoEstadual") != null)
                            && (!rst.getString("InscricaoEstadual").trim().isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("InscricaoEstadual").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "");
                        inscricaoestadual = inscricaoestadual.replace("-", "");
                        inscricaoestadual = inscricaoestadual.replace(".", "");
                    } else {

                        if ((rst.getString("RG") != null)
                                && (!rst.getString("RG").trim().isEmpty())) {
                            inscricaoestadual = Utils.acertarTexto(rst.getString("RG").trim());
                            inscricaoestadual = inscricaoestadual.replace("'", "");
                            inscricaoestadual = inscricaoestadual.replace("-", "");
                            inscricaoestadual = inscricaoestadual.replace(".", "");

                            if ((rst.getString("OrgaoExp") != null)
                                    && (!rst.getString("OrgaoExp").trim().isEmpty())) {
                                orgaoExp = Utils.acertarTexto(rst.getString("OrgaoExp").trim().replace("'", ""));
                            } else {
                                orgaoExp = "";
                            }
                        } else {
                            inscricaoestadual = "ISENTO";
                        }
                    }

                    if ((rst.getString("DataCadastro") != null)
                            && (!rst.getString("DataCadastro").trim().isEmpty())) {
                        dataCadastro = rst.getString("DataCadastro").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataCadastro = "";
                    }

                    if ((rst.getString("DataNascimento") != null)
                            && (!rst.getString("DataNascimento").trim().isEmpty())) {
                        dataNascimento = rst.getString("DataNascimento").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataNascimento = null;
                    }

                    bloqueado = false;

                    nomePai = "";

                    nomeMae = "";

                    telefone2 = "";

                    fax = "";

                    if ((rst.getString("Observacoes") != null)
                            && (!rst.getString("Observacoes").trim().isEmpty())) {
                        observacao = Utils.acertarTexto(rst.getString("Observacoes").replace("'", "").trim());
                    } else {
                        observacao = "";
                    }

                    email = "";

                    if ((rst.getString("Sexo") != null)
                            && (!rst.getString("Sexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("Sexo").trim())) {
                            id_sexo = 0;
                        } else {
                            id_sexo = 1;
                        }
                    } else {
                        id_sexo = 1;
                    }

                    empresa = "";

                    telEmpresa = "";

                    cargo = "";

                    enderecoEmpresa = "";

                    salario = 0;

                    if ((rst.getString("EstadoCivil") != null)
                            && (!rst.getString("EstadoCivil").trim().isEmpty())) {
                        if (null != rst.getString("EstadoCivil").trim()) {
                            switch (rst.getString("EstadoCivil").trim()) {
                                case "C":
                                    estadoCivil = 2;
                                    break;
                                case "S":
                                    estadoCivil = 1;
                                    break;
                                case "O":
                                    estadoCivil = 5;
                                    break;
                            }
                        }
                    } else {
                        estadoCivil = 0;
                    }

                    conjuge = "";

                    if (nome.length() > 40) {
                        nome = nome.substring(0, 40);
                    }

                    if (conjuge.length() > 25) {
                        conjuge = conjuge.substring(0, 25);
                    }

                    if (endereco.length() > 40) {
                        endereco = endereco.substring(0, 40);
                    }

                    if (bairro.length() > 30) {
                        bairro = bairro.substring(0, 30);
                    }

                    if (String.valueOf(cep).length() > 8) {
                        cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                    }

                    if (telefone1.length() > 14) {
                        telefone1 = telefone1.substring(0, 14);
                    }

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 18) {
                        inscricaoestadual = inscricaoestadual.substring(0, 18);
                    }

                    if (complemento.length() > 30) {
                        complemento = complemento.substring(0, 30);
                    }

                    if (email.length() > 50) {
                        email = email.substring(0, 50);
                    }

                    if (observacao.length() > 80) {
                        observacao = observacao.substring(0, 80);
                    }

                    oClienteEventual.id = id;
                    oClienteEventual.nome = nome;
                    oClienteEventual.endereco = endereco;
                    oClienteEventual.bairro = bairro;
                    oClienteEventual.id_estado = id_estado;
                    oClienteEventual.id_municipio = id_municipio;
                    oClienteEventual.cep = cep;
                    oClienteEventual.telefone = telefone1;
                    oClienteEventual.inscricaoestadual = inscricaoestadual;
                    oClienteEventual.cnpj = cnpj;
                    //oClienteEventual.sexo = id_sexo;
                    //oClienteEventual.dataresidencia = dataResidencia;
                    oClienteEventual.datacadastro = dataCadastro;
                    oClienteEventual.email = email;
                    oClienteEventual.limitecompra = limite;
                    //oClienteEventual.codigoanterior = id;
                    oClienteEventual.fax = fax;
                    oClienteEventual.bloqueado = bloqueado;
                    oClienteEventual.id_situacaocadastro = id_situacaocadastro;
                    oClienteEventual.telefone2 = telefone2;
                    oClienteEventual.observacao = observacao;
                    //oClienteEventual.observacao2 = observacao2;
                    //oClienteEventual.datanascimento = dataNascimento;
                    //oClienteEventual.nomepai = nomePai;
                    //oClienteEventual.nomemae = nomeMae;
                    //oClienteEventual.empresa = empresa;
                    //oClienteEventual.telefoneempresa = telEmpresa;
                    oClienteEventual.numero = numero;
                    //oClienteEventual.cargo = cargo;
                    //oClienteEventual.enderecoempresa = enderecoEmpresa;
                    oClienteEventual.id_tipoinscricao = id_tipoinscricao;
                    //oClienteEventual.salario = salario;
                    //oClienteEventual.id_tipoestadocivil = estadoCivil;
                    //oClienteEventual.nomeconjuge = conjuge;
                    //oClienteEventual.orgaoemissor = orgaoExp;
                    vClienteEventual.add(oClienteEventual);
                }

                stm.close();
            } catch (Exception ex) {
                throw ex;
                //if (Linha > 0) {
                //    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                //} else {
                //    throw ex;
                //}
            }
            return vClienteEventual;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ClienteEventualContatoVO> carregarClienteEventualContato() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ClienteEventualContatoVO> vClienteEventualContato = new ArrayList<>();
        long idCliente = 0;
        String telefone = "", email = "", numeroRamal = "", contato = "";

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select p.CodigoPessoa, telP.NumeroTelefone, telP.NumeroRamal, ");
            sql.append("telP.ContatoTelefone, em.EnderecoEletronicoPessoa ");
            sql.append("from Pessoa p ");
            sql.append("left join TelefonePessoa telP on telP.CodigoPessoa = p.CodigoPessoa ");
            sql.append("left join EnderecoEletronicoPessoa em on em.CodigoPessoa = p.CodigoPessoa ");
            sql.append("inner join TipoPessoa tp on tp.CodigoPessoa = p.CodigoPessoa and tp.CodigoTipoPessoa = 'C' ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idCliente = Long.parseLong(rst.getString("CodigoPessoa").trim());

                if ((rst.getString("NumeroTelefone") != null)
                        && (!rst.getString("NumeroTelefone").trim().isEmpty())) {
                    telefone = Utils.formataNumero(rst.getString("NumeroTelefone").trim());
                } else {
                    telefone = "";
                }

                if ((rst.getString("NumeroRamal") != null)
                        && (!rst.getString("NumeroRamal").trim().isEmpty())) {
                    numeroRamal = "RAMAL: " + Utils.formataNumero(rst.getString("NumeroRamal").trim());
                } else {
                    numeroRamal = "";
                }

                if ((rst.getString("ContatoTelefone") != null)
                        && (!rst.getString("ContatoTelefone").trim().isEmpty())) {
                    contato = "CONTATO: " + Utils.acertarTexto(rst.getString("ContatoTelefone").trim().replace("'", ""));
                } else {
                    contato = "CONTATO";
                }

                if ((rst.getString("EnderecoEletronicoPessoa") != null)
                        && (!rst.getString("EnderecoEletronicoPessoa").trim().isEmpty())
                        && (rst.getString("EnderecoEletronicoPessoa").contains("@"))) {
                    email = Utils.acertarTexto(rst.getString("EnderecoEletronicoPessoa").trim().replace("'", ""));
                    email = email.toLowerCase();
                } else {
                    email = "";
                }

                ClienteEventualContatoVO oClienteEventualContato = new ClienteEventualContatoVO();
                oClienteEventualContato.setNome(contato);
                oClienteEventualContato.setIdClienteEventualAnterior(idCliente);
                oClienteEventualContato.setTelefone(telefone);
                oClienteEventualContato.setEmail(email);

                vClienteEventualContato.add(oClienteEventualContato);
            }

            return vClienteEventualContato;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberChequeVO> carregarReceberCheque(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco = 0, cheque, idTipoInscricao = 0, id_tipoalinea, ecf = 0;
        double valor, juros;
        long cpfCnpj = 0;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia = null, conta = null, nome, rg, telefone;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            /*sql.append("SELECT c.cheque, c.ciccgc, c.client, c.bancox, c.agenci, c.contax, ");
             sql.append("c.valorx, c.dataxx, c.vencim, c.status, c.devol1, c.motdv1, c.devol2, c.motdv2, ");
             sql.append("c.reapre, c.quitad, c.codfor, c.nomfor, c.datfor, c.caixax, c.observ, c.seqdev, ");
             sql.append("c.datcad, c.usucad, c.datalt, c.usualt, c.cobran, c.datcob, c.entrad ");
             sql.append("FROM CHEQUES c ");             
             sql.append("WHERE c.FILIAL = "+String.valueOf(id_lojaCliente));                         */

            sql.append("select R.CXANUM, R.CTRNUM, R.CTRDATEMI, R.CTRDATVNC, ");
            sql.append("R.CTROBS, R.CTRNUMBCO, C.CLIDES, C.CLICPFCGC, ");
            sql.append("R.CTRVLRDEV, C.CLIRGCGF, C.CLITEL, C.CLIPFPJ ");
            sql.append("from CONTARECEBER R ");
            sql.append("INNER JOIN CLIENTE C ON C.CLICOD = R.CLICOD ");
            sql.append("where FZDCOD in ('002', '003') ");
            sql.append("and CTRDATPGT is null ");
            sql.append("or CTRVLRPAG < CTRVLRDEV ");
            sql.append("and R.CTRVLRDEV > 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("CLICPFCGC") != null)
                        && (!rst.getString("CLICPFCGC").trim().isEmpty())) {
                    cpfCnpj = Long.parseLong(rst.getString("CLICPFCGC").trim());
                } else {
                    cpfCnpj = 0;
                }

                if ((rst.getString("CLIPFPJ") != null)
                        && (!rst.getString("CLIPFPJ").trim().isEmpty())) {

                    if ("J".equals(rst.getString("CLIPFPJ").trim())) {
                        idTipoInscricao = 0;
                    } else if ("F".equals(rst.getString("CLIPFPJ").trim())) {
                        idTipoInscricao = 1;
                    }
                } else {
                    idTipoInscricao = 1;
                }

                idBanco = 804;

                //if ((rst.getString("agenci") != null) &&
                //        (!rst.getString("agenci").trim().isEmpty())) {
                //    agencia = util.acertarTexto(rst.getString("agenci").trim().replace("'", ""));
                //} else {
                agencia = "";
                //}

                //if ((rst.getString("contax") != null) &&
                //        (!rst.getString("contax").trim().isEmpty()))  {
                //    conta = util.acertarTexto(rst.getString("contax").trim().replace("'", ""));
                //} else {
                conta = "";

                //if ((rst.getString("cheque") != null) &&
                //        (!rst.getString("cheque").trim().isEmpty())) {
                //    
                //    cheque = Integer.parseInt(util.formataNumero(rst.getString("cheque")));
                //    
                //    if (String.valueOf(cheque).length() > 10) {
                //        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                //    }
                //} else {
                cheque = 0;
                //}

                if ((rst.getString("CTRDATEMI") != null)
                        && (!rst.getString("CTRDATEMI").trim().isEmpty())) {

                    dataemissao = rst.getString("CTRDATEMI").trim().substring(0, 10)/*.replace("-", "/")*/;
                } else {
                    dataemissao = "2016/05/20";
                }

                if ((rst.getString("CTRDATVNC") != null)
                        && (!rst.getString("CTRDATVNC").trim().isEmpty())) {

                    datavencimento = rst.getString("CTRDATVNC").trim().substring(0, 10)/*.replace("-", "/")*/;
                } else {
                    datavencimento = "2016/06/20";
                }

                if ((rst.getString("CLIDES") != null)
                        && (!rst.getString("CLIDES").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("CLIDES").replace("'", "").trim());
                } else {
                    nome = "";
                }

                if ((rst.getString("CLIRGCGF") != null)
                        && (!rst.getString("CLIRGCGF").isEmpty())) {
                    rg = util.acertarTexto(rst.getString("CLIRGCGF").trim().replace("'", ""));

                    if (rg.length() > 20) {
                        rg = rg.substring(0, 20);
                    }
                } else {
                    rg = "";
                }

                valor = Double.parseDouble(rst.getString("CTRVLRDEV"));

                if ((rst.getString("CTRNUM") != null)
                        && (!rst.getString("CTRNUM").trim().isEmpty())) {

                    if (rst.getString("CTRNUM").trim().length() > 9) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("CTRNUM").substring(0, 9)));
                    } else {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("CTRNUM").trim()));
                    }
                } else {
                    numerocupom = 0;
                }

                juros = 0;

                if ((rst.getString("CTROBS") != null)
                        && (!rst.getString("CTROBS").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("CTROBS").replace("'", "").trim());
                } else {
                    observacao = "IMPORTADO VR";
                }

                if ((rst.getString("CLITEL") != null)
                        && (!rst.getString("CLITEL").isEmpty())
                        && (!"0".equals(rst.getString("CLITEL").trim()))) {
                    telefone = util.formataNumero(rst.getString("CLITEL"));
                } else {
                    telefone = "";
                }

                //if (rst.getInt("status")==1){
                id_tipoalinea = 0;
                //} else if (rst.getInt("status")==2){
                //    id_tipoalinea = 15;                    
                //} else {
                //    id_tipoalinea = 0;
                //}

                if ((rst.getString("CXANUM") != null)
                        && (!rst.getString("CXANUM").trim().isEmpty())) {
                    ecf = Integer.parseInt(Utils.formataNumero(rst.getString("CXANUM").trim()));
                } else {
                    ecf = 0;
                }

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();
                oReceberCheque.id_loja = id_loja;
                oReceberCheque.ecf = ecf;
                oReceberCheque.id_tipoalinea = id_tipoalinea;
                oReceberCheque.data = dataemissao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.cpf = cpfCnpj;
                oReceberCheque.numerocheque = cheque;
                oReceberCheque.id_banco = idBanco;
                oReceberCheque.agencia = agencia;
                oReceberCheque.conta = conta;
                oReceberCheque.numerocupom = numerocupom;
                oReceberCheque.valor = valor;
                oReceberCheque.observacao = observacao;
                oReceberCheque.rg = rg;
                oReceberCheque.telefone = telefone;
                oReceberCheque.nome = nome;
                oReceberCheque.id_tipoinscricao = idTipoInscricao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.valorjuros = juros;
                oReceberCheque.valorinicial = valor;

                vReceberCheque.add(oReceberCheque);
            }
            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativoKairos(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int id_cliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT CTRID, ctrnum, clicod, cxanum, ctrdatemi, ctrdatvnc, ctrvlrdev, ctrobs ");
            sql.append("FROM CONTARECEBER ");
            sql.append("WHERE CTRVLRPAG < CTRVLRNOM ");
            sql.append(" OR CTRVLRPAG IS NULL ");
            sql.append("and FZDCOD not in ('002', '003') ");
            sql.append("or FZDCOD is null ");
            sql.append("and ctrvlrdev > 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                id_cliente = rst.getInt("clicod");
                dataemissao = rst.getString("ctrdatemi").substring(0, 10).trim();
                datavencimento = rst.getString("ctrdatvnc").substring(0, 10).trim();

                if ((rst.getString("CTRID") != null)
                        && (!rst.getString("CTRID").trim().isEmpty())) {

                    if (rst.getString("CTRID").trim().length() > 9) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("CTRID").substring(0, 9)));
                    } else {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("CTRID").trim()));
                    }
                } else {
                    numerocupom = 0;
                }

                valor = Double.parseDouble(rst.getString("ctrvlrdev"));
                juros = 0;

                if ((rst.getString("cxanum") != null)
                        && (!rst.getString("cxanum").trim().isEmpty())) {
                    ecf = Integer.parseInt(rst.getString("cxanum").trim());
                } else {
                    ecf = 0;
                }

                if ((rst.getString("ctrobs") != null)
                        && (!rst.getString("ctrobs").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("ctrobs").replace("'", "").trim());
                } else {
                    observacao = "IMPORTADO VR";
                }

                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.ecf = ecf;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                oReceberCreditoRotativo.valorjuros = juros;

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);

            }

            return vReceberCreditoRotativo;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    private List<ProdutoVO> carregarMercadologicoProdutoKairos() throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto = 0;
        int mercad1, mercad2, mercad3;
        Utils util = new Utils();

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select p.CodigoProduto, p.CodigoGrupoProduto, p.CodigoSubGrupoProduto ");
            sql.append("from Produto p ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                idProduto = Double.parseDouble(rst.getString("CodigoProduto"));

                if ((rst.getString("CodigoGrupoProduto") != null)
                        && (!rst.getString("CodigoGrupoProduto").trim().isEmpty())) {
                    mercad1 = Integer.parseInt(rst.getString("CodigoGrupoProduto"));
                } else {
                    mercad1 = -1;
                }

                if ((rst.getString("CodigoSubGrupoProduto") != null)
                        && (!rst.getString("CodigoSubGrupoProduto").trim().isEmpty())) {

                    if (rst.getInt("CodigoSubGrupoProduto") < 1) {
                        mercad2 = 1;
                    } else {
                        mercad2 = Integer.parseInt(rst.getString("CodigoSubGrupoProduto"));
                    }

                } else {
                    mercad2 = -1;
                }

                mercad3 = 1;

                if (!util.verificaExisteMercadologico(mercad1, mercad2, mercad3)) {

                    sql = new StringBuilder();
                    sql.append("select mercadologico1 as mercadologico1 ");
                    sql.append("from mercadologico ");
                    sql.append("where descricao like '%ACERTAR%' ");
                    rstPostgres = stmPostgres.executeQuery(sql.toString());

                    if (rstPostgres.next()) {
                        mercad1 = rstPostgres.getInt("mercadologico1");
                        mercad2 = 1;
                        mercad3 = 1;
                    }
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.mercadologico1 = mercad1;
                oProduto.mercadologico2 = mercad2;
                oProduto.mercadologico3 = mercad3;
                vProduto.add(oProduto);
            }

            stm.close();
            stmPostgres.close();
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<AtacadoVendedorVO> carregarVendedor() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<AtacadoVendedorVO> v_vendedor = new ArrayList<>();

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select p.CodigoPessoa, p.RazaoSocial, p.NomeFantasia, tp.CodigoPessoa as codigo \n"
                    + "from Pessoa p\n"
                    + "inner join TipoPessoa tp on tp.CodigoPessoa = p.CodigoPessoa\n"
                    + "where tp.CodigoTipoPessoa = 'V'\n"
                    + "order by p.CodigoPessoa");
            rst = stm.executeQuery(sql.toString());
            while (rst.next()) {
                AtacadoVendedorVO oAtacadoVendedor = new AtacadoVendedorVO();
                oAtacadoVendedor.setId(rst.getInt("CodigoPessoa"));
                oAtacadoVendedor.setNome(Utils.acertarTexto(rst.getString("RazaoSocial").trim()));
                v_vendedor.add(oAtacadoVendedor);
            }
            stm.close();
            return v_vendedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarVendedor() throws Exception {
        List<AtacadoVendedorVO> v_vendedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...atacado.vendedor...");
            v_vendedor = carregarVendedor();
            if (!v_vendedor.isEmpty()) {
                new AtacadoVendedorDAO().salvar(v_vendedor);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<AtacadoVendedorClienteVO> carregarVendedorCliente() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<AtacadoVendedorClienteVO> v_vendedorCliente = new ArrayList<>();

        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select CodigoCliente, CodigoVendedor "
                    + "from ClienteVendedor");
            rst = stm.executeQuery(sql.toString());
            while (rst.next()) {
                AtacadoVendedorClienteVO oVendedorCliente = new AtacadoVendedorClienteVO();
                oVendedorCliente.setId_clienteeventual(rst.getInt("CodigoCliente"));
                oVendedorCliente.setId_vendedor(rst.getInt("CodigoVendedor"));
                v_vendedorCliente.add(oVendedorCliente);
            }
            stm.close();
            return v_vendedorCliente;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarVendedorCliente() throws Exception {
        List<AtacadoVendedorClienteVO> v_vendedorCliente = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...atacado.vendedorcliente...");
            v_vendedorCliente = carregarVendedorCliente();
            if (!v_vendedorCliente.isEmpty()) {
                new AtacadoVendedorClienteDAO().salvar(v_vendedorCliente);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    //IMPORTAÇÕES
    public void importarAcertarMercadologicoProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico Produto...");
            vProduto = carregarMercadologicoProdutoKairos();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().alterarMercadologicoProdutoRapido(vProduto);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoKairos(1);
            new MercadologicoDAO().salvar2(vMercadologico, false);

            vMercadologico = carregarMercadologicoKairos(2);
            new MercadologicoDAO().salvar2(vMercadologico, false);

            vMercadologico = carregarMercadologicoKairos(3);
            new MercadologicoDAO().salvar2(vMercadologico, false);

            //new MercadologicoDAO().salvarMax();
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProduto6(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos Menor 7 digitos...");
            Map<Integer, ProdutoVO> vProdutoSysPdv = carregarProdutoKairos();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoSysPdv.size());

            for (Integer keyId : vProdutoSysPdv.keySet()) {

                ProdutoVO oProduto = vProdutoSysPdv.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.usarMercadoligicoProduto = true;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoMaior6(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos Maior 6 digitos...");
            Map<Double, ProdutoVO> vProduto = carregarProdutoKairosMaior6Digitos();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProduto.size());

            for (Double keyId : vProduto.keySet()) {

                ProdutoVO oProduto = vProduto.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.usarMercadoligicoProduto = true;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPrecoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Double, ProdutoVO> vPrecoProduto = carregarPrecoProdutoKairos(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Double keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarPrecoProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCustoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProdutoKairos(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Double keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProdutoRapido(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEstoqueProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Double, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoKairos(id_loja, id_lojaCliente);

            ProgressBar.setMaximum(vEstoqueProduto.size());

            for (Double keyId : vEstoqueProduto.keySet()) {

                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasKairos();

            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {

                ProdutoVO oProduto = vCodigoBarra.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarDataCadastroProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {

            ProgressBar.setStatus("Carregando dados...Data Cadastro Produto...");
            vProduto = carregarDataCadastroProduto();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().altertarDataCadastroProdutoGdoor(vProduto);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarIcmsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Icms Produtos...");
            vProduto = carregarIcmsProduto();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().adicionarIcmsProduto(vProduto);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorKairos();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFornecedorCnpj() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorKairos();

            new FornecedorDAO().salvarCnpj(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFornecedorContato() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Contatos Fornecedores...");

            List<FornecedorContatoVO> v_fornecedorContato = carregarFornecedorContato();

            if (!v_fornecedorContato.isEmpty()) {
                new FornecedorContatoDAO().salvar(v_fornecedorContato);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoFornecedor() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorKairos();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClienteEventual(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClienteEventualVO> vClienteEventual = carregarClienteEventualKairos(idLoja, idLojaCliente);
            new ClienteEventuallDAO().salvar(vClienteEventual);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarClienteEventualContato() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Contatos Cliente Eventual...");

            List<ClienteEventualContatoVO> v_clienteEventualContato = carregarClienteEventualContato();

            if (!v_clienteEventualContato.isEmpty()) {
                new ClienteEventualContatoDAO().salvar(v_clienteEventualContato);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarNutricionalFilizola() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Nutricional Filizola...");
            List<NutricionalFilizolaVO> vNutricionalFilizola = carregarNutricionalFilizola();

            if (!vNutricionalFilizola.isEmpty()) {
                //desabilitar
                String sistema= "";
                String loja = "";
                new NutricionalFilizolaRepository().salvarClassesEspecificas(vNutricionalFilizola, sistema, loja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarNutricionalToledo() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Nutricional Toledo...");
            List<NutricionalToledoVO> vNutricionalToledo = carregarNutricionalToledo();

            if (!vNutricionalToledo.isEmpty()) {
                //desabilitar
                String sistema= "";
                String loja = "";
                new NutricionalToledoRepository().salvarClassesEspecificas(vNutricionalToledo, sistema, loja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCodigoBarrasAnterior() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Codigo Barras Anterior...");
            vProduto = carregarCodigoBarrasAnteriorKairos();

            if (!vProduto.isEmpty()) {
                new ProdutoDAO().salvarCodigoBarrasAnterior(vProduto);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarEstoqueTerceiro(int idLoja, int idLojaFilial) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Terceiro...");
            List<EstoqueTerceiroVO> v_estoqueTerceiro = carregarEstoqueTerceiroKairos(idLoja, idLojaFilial);
            if (!v_estoqueTerceiro.isEmpty()) {
                new ProdutoDAO().inserirEstoqueTerceiro(v_estoqueTerceiro);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    // métodos Kairos
    private int retornarIcmsKairos(String codigoIcms) {
        int retorno = 8;

        if (null != codigoIcms) {
            switch (codigoIcms) {
                case "07P":
                    retorno = 1;
                    break;
                case "17P":
                    retorno = 21;
                    break;
                case "25P":
                    retorno = 3;
                    break;
                case "00I":
                    retorno = 6;
                    break;
                case "00S":
                    retorno = 7;
                    break;
                case "27P":
                    retorno = 22;
                    break;
                case "12P":
                    retorno = 1;
                    break;
                case "18P":
                    retorno = 2;
                    break;
                case "18B":
                    retorno = 2;
                    break;
                case "18A":
                    retorno = 2;
                    break;
            }
        }

        return retorno;
    }
}
