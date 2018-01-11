package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemAnteriorDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemDAO;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SifatDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Sifat";
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  id_familia, descricao\n"
                    + "from\n"
                    + "  bdsifat.ce27"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id_familia"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  id_grupo, id_subgrupo,\n"
                    + "  nome_grupo, nome_subgrupo\n"
                    + "from\n"
                    + "  bdsifat.ce07\n"
                    + "order by\n"
                    + "  id_grupo, id_subgrupo;"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("id_grupo"));
                    imp.setMerc1Descricao("LANCHONETE - " + rst.getString("nome_grupo"));
                    imp.setMerc2ID(rst.getString("id_subgrupo"));
                    imp.setMerc2Descricao("LANCHONETE - " + rst.getString("nome_subgrupo"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        int cstIcms;
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  p.id_produto, p.codigo, p.descricao, p.abreviacao,\n"
                    + "  p.unidade, p.peso, p.grupo, p.subgrupo, p.familia,\n"
                    + "  p.estoque_min, p.estoque_max, p.ncm, p.cest,\n"
                    + "  p.dt_cadastro, p.ean_gtin, p.validade,\n"
                    + "  case p.ativo when 1 then 'S' else 'N' end ativo,\n"
                    + "  case p.pesado when 1 then 'S' else 'N' end pesado,\n"
                    + "  case p.fraciona when 1 then 'S' else 'N' end fraciona,\n"
                    + "  pr.preco_custo, pr.preco_venda, pr.estoque,\n"
                    + "  i.descricao desc_imposto, i.cst_icms, i.red_bc_icms,\n"
                    + "  i.al_icms_compra, i.totalizador_ecf, pr.depto_pis,\n"
                    + "  pr.depto_pis_entrada, ps.cst cst_pis_saida,\n"
                    + "  pe.cst cst_pis_entrada, ps.nat_operacao\n"
                    + "from\n"
                    + "  bdsifat.ce01 p\n"
                    + "left join\n"
                    + "  bdsifat.ce01e pr on pr.id_produto = p.id_produto\n"
                    + "left join\n"
                    + "  bdsifat.ce01t i on i.depto_icms = pr.depto_icms and i.operacao = 1\n"
                    + "left join\n"
                    + "  bdsifat.ce61 ps on ps.id = pr.depto_pis\n"
                    + "left join\n"
                    + "  bdsifat.ce61 pe on pe.id = pr.depto_pis_entrada\n"
                    + "where\n"
                    + "  pr.loja = 1\n"
                    + "order by\n"
                    + "  codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigo"));
                    imp.setDataCadastro(rst.getDate("dt_cadastro"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("abreviacao"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setCodMercadologico1(rst.getString("grupo"));
                    imp.setCodMercadologico2(rst.getString("subgrupo"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_min"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_max"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPiscofinsCstDebito(rst.getInt("cst_pis_saida"));
                    imp.setPiscofinsCstCredito(rst.getInt("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("nat_operacao"));
                    if (rst.getString("totalizador_ecf").contains("II")) {
                        cstIcms = 40;
                    } else if (rst.getString("totalizador_ecf").contains("FF")) {
                        cstIcms = 60;
                    } else if (rst.getString("totalizador_ecf").contains("NN")) {
                        cstIcms = 41;
                    } else {
                        cstIcms = 0;
                    }
                    imp.setIcmsCst(cstIcms);
                    imp.setIcmsAliq(rst.getDouble("al_icms_compra"));
                    imp.setIcmsReducao(rst.getDouble("red_bc_icms"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  codigo,\n"
                    + "  case ativo when 1 then 'S' else 'N' end ativo,\n"
                    + "  case e_cliente when 1 then 'S' else 'N' end e_cliente,\n"
                    + "  case e_fornecedor when 1 then 'S' else 'N' end e_fornecedor,\n"
                    + "  case e_transportador when 1 then 'S' else 'N' end e_transportador,\n"
                    + "  case e_cheque when 1 then 'S' else 'N' end e_cheque,\n"
                    + "  case e_representante when 1 then 'S' else 'N' end e_representante,\n"
                    + "  case e_funcionario when 1 then 'S' else 'N' end e_funcionario,\n"
                    + "  nome razao_social, nome_fantasia, endereco, end_complemento,\n"
                    + "  bairro, cidade, uf, pais, caixa_postal, telefone, fax, celular,\n"
                    + "  email, site, ponto_referencia, pessoa_contato, pessoa, insc_federal,\n"
                    + "  insc_estadual, dt_cadastro, limite_credito, limite_cheque, dt_inatividade,\n"
                    + "  dt_nascimento, estado_civil, nome_conjuge, trabalho, trabalho_conjuge,\n"
                    + "  trabalho_setor, dt_admissao, trabalho_endereco, trabalho_endereco_conjuge,\n"
                    + "  trabalho_telefone, trabalho_telefone_conjuge, renda_mensal, profissao,\n"
                    + "  profissao_conjuge, cpf_conjuge\n"
                    + "from\n"
                    + "  bdsifat.cd02\n"
                    + "where\n"
                    + "  e_fornecedor = 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setAtivo("S".equals(rst.getString("ativo")));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("nome_fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    //imp.setNumero(rst.getString("end_numero"));
                    imp.setComplemento(rst.getString("end_complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setCnpj_cpf(rst.getString("insc_federal"));
                    imp.setIe_rg(rst.getString("insc_estadual"));
                    imp.setDatacadastro(rst.getDate("dt_cadastro"));
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "CELULAR",
                                null,
                                Utils.formataNumero(rst.getString("celular")),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email")
                        );
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "SITE",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("site")
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  codigo,\n"
                    + "  case ativo when 1 then 'S' else 'N' end ativo,\n"
                    + "  case e_cliente when 1 then 'S' else 'N' end e_cliente,\n"
                    + "  case e_fornecedor when 1 then 'S' else 'N' end e_fornecedor,\n"
                    + "  case e_transportador when 1 then 'S' else 'N' end e_transportador,\n"
                    + "  case e_cheque when 1 then 'S' else 'N' end e_cheque,\n"
                    + "  case e_representante when 1 then 'S' else 'N' end e_representante,\n"
                    + "  case e_funcionario when 1 then 'S' else 'N' end e_funcionario,\n"
                    + "  nome razao_social, nome_fantasia, endereco, end_complemento,\n"
                    + "  bairro, cidade, uf, pais, caixa_postal, telefone, fax, celular,\n"
                    + "  email, site, ponto_referencia, pessoa_contato, pessoa, insc_federal,\n"
                    + "  insc_estadual, dt_cadastro, limite_credito, limite_cheque, dt_inatividade,\n"
                    + "  dt_nascimento, estado_civil, nome_conjuge, trabalho, trabalho_conjuge,\n"
                    + "  trabalho_setor, dt_admissao, trabalho_endereco, trabalho_endereco_conjuge,\n"
                    + "  trabalho_telefone, trabalho_telefone_conjuge, renda_mensal, profissao,\n"
                    + "  profissao_conjuge, cpf_conjuge\n"
                    + "from\n"
                    + "  bdsifat.cd02\n"
                    + "where\n"
                    + "  e_cliente = 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setAtivo(rst.getString("ativo").equals("S"));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("nome_fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    //imp.setNumero(rst.getString("end_numero"));
                    imp.setComplemento(rst.getString("end_complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCnpj(rst.getString("insc_federal"));
                    imp.setInscricaoestadual(rst.getString("insc_estadual"));
                    imp.setDataCadastro(rst.getDate("dt_cadastro"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
                    imp.setDataNascimento(rst.getDate("dt_nascimento"));
                    imp.setNomeConjuge(rst.getString("nome_conjuge"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setObservacao(null);
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "SITE",
                                null,
                                null,
                                Utils.formataNumero(rst.getString("site"))
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, cliente, loja, caixa,\n"
                    + "venda, data emissao, faturado, valor,\n"
                    + "ADDDATE(data, interval 30 day) vencimento\n"
                    + "from bdsifat.cf11\n"
                    + "where DC = 'D'\n"
                    + "and historico like '%VENDA%'\n"
                    + "and loja = 1 "
                    + "order by data"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("cliente"));
                    imp.setNumeroCupom(rst.getString("venda"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    public void importarPagamentoRotativo() throws Exception {        
        Conexao.begin();
        try {
            Map<String, Double> pagamentos = new HashMap<>();

            ProgressBar.setStatus("Importando pagamentos...");
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select distinct a.cliente,\n"
                        + "(select sum(coalesce(valor, 0)) from bdsifat.cf11 where dc = 'C' and cliente = a.cliente) - "
                        + "(select sum(coalesce(valor, 0)) from bdsifat.cf11 where historico like '%ESTORNO%' and cliente = a.cliente)"
                        + " valor\n"
                        + "from bdsifat.cf11 a\n"
                        + "where loja = 1" 
                )) {
                    while (rst.next()) {
                        double valor = rst.getDouble("valor");

                        if (valor < 0) {
                            valor *= -1;
                        }
                        
                        pagamentos.put(rst.getString("cliente"), MathUtils.trunc(valor, 2));
                    }
                }
            }
            
            for (String id: pagamentos.keySet()) {
                double valorPagoTotal = pagamentos.get(id);
                System.out.println("ID: " + id + "  VALOR: " + valorPagoTotal);
            }

            CreditoRotativoDAO rotDao = new CreditoRotativoDAO();
            CreditoRotativoItemDAO dao = new CreditoRotativoItemDAO();
            CreditoRotativoItemAnteriorDAO antDao = new CreditoRotativoItemAnteriorDAO();
            MultiMap<String, CreditoRotativoItemAnteriorVO> baixasAnteriores = antDao.getBaixasAnteriores(null, null);

            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	 ant.sistema,\n"
                        + "    ant.loja,\n"
                        + "    ant.id_cliente,\n"
                        + "    ant.id,\n"
                        + "    ant.codigoatual,\n"
                        + "    r.id_loja,\n"
                        + "    r.valor,\n"
                        + "    r.datavencimento\n"
                        + "from \n"
                        + "	implantacao.codant_recebercreditorotativo ant\n"
                        + "    join recebercreditorotativo r on\n"
                        + "    	ant.codigoatual = r.id\n"
                        + " where ant.loja = '" + getLojaOrigem() + "' "
                        + " and ant.sistema = '" + getSistema() + "'"
                        + " order by\n"
                        + "	ant.id_cliente, r.datavencimento"
                )) {
                    int cont1 = 0, cont2 = 0;
                    while (rst.next()) {
                        String sistema = rst.getString("sistema");
                        String loja = rst.getString("loja");
                        String idCliente = rst.getString("id_cliente");
                        String idRotativo = rst.getString("id");
                        int codigoAtual = rst.getInt("codigoatual");
                        int id_loja = rst.getInt("id_loja");
                        double valor = rst.getDouble("valor");
                        Date vencimento = rst.getDate("datavencimento");
                        
                        
                        
                        if (!baixasAnteriores.containsKey(sistema, loja, idRotativo, idRotativo)) {
                            if (pagamentos.containsKey(idCliente)) {
                                double valorPagoTotal = pagamentos.get(idCliente);
                                if (valorPagoTotal > 0) {
                                    System.out.println("CLIENTE: " + idCliente + " VAL_PAGO: " + valorPagoTotal);
                                    double valorParc;
                                    if (valorPagoTotal >= valor) {
                                        valorPagoTotal -= valor;
                                        valorParc = valor;
                                    } else {
                                        valorParc = valorPagoTotal;
                                        valorPagoTotal = 0;
                                    }

                                    CreditoRotativoItemVO pag = new CreditoRotativoItemVO();
                                    pag.setId_receberCreditoRotativo(codigoAtual);
                                    pag.setValor(valorParc);
                                    pag.setValorTotal(valorParc);
                                    pag.setDatabaixa(vencimento);
                                    pag.setDataPagamento(vencimento);
                                    pag.setObservacao("IMPORTADO VR");
                                    pag.setId_loja(id_loja);

                                    dao.gravarRotativoItem(pag);

                                    CreditoRotativoItemAnteriorVO ant = new CreditoRotativoItemAnteriorVO();
                                    ant.setSistema(sistema);
                                    ant.setLoja(loja);
                                    ant.setIdCreditoRotativo(idRotativo);
                                    ant.setId(idRotativo);
                                    ant.setCodigoAtual(pag.getId());
                                    ant.setDataPagamento(vencimento);
                                    ant.setValor(pag.getValor());

                                    antDao.gravarRotativoItemAnterior(ant);
                                    
                                    rotDao.verificarBaixado(codigoAtual);

                                    pagamentos.put(idCliente, valorPagoTotal);
                                    baixasAnteriores.put(ant, 
                                            ant.getSistema(),
                                            ant.getLoja(),
                                            ant.getIdCreditoRotativo(),
                                            ant.getId()
                                    );
                                }
                            }
                        } 
                        cont1++;
                        cont2++;
                        
                        if (cont1 == 1000) {
                            cont1 = 0;
                            ProgressBar.setStatus("Importando pagamentos..." + cont2);
                        }
                    }
                }
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
}
