package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class FMDAO extends InterfaceDAO {
    
    private static final Logger LOG = Logger.getLogger(FMDAO.class.getName());

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	codigo, \n" +
                    "	secao\n" +
                    "from \n" +
                    "	fm.secoes\n" +
                    "where \n" +
                    "	secao <> ''\n" +
                    "	and char_length(secao) > 1\n" +
                    "order by \n" +
                    "	codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("secao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	distinct(familia) familia\n" +
                    "from\n" +
                    "	fm.mercadorias\n" +
                    "where \n" +
                    "	familia <> '' and\n" +
                    "    char_length(secao) > 4\n" +
                    "order by\n" +
                    "	familia"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("familia"));
                    imp.setDescricao(rst.getString("familia"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            Map<Integer, Double> estoque = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	e.CodMercadoria id_produto,\n" +
                    "	coalesce(e.estoque, 0) estoque\n" +
                    "from\n" +
                    "	fm.estoque e    \n" +
                    "	join (select\n" +
                    "		max(es.Codigo) Codigo,\n" +
                    "		es.codMercadoria\n" +
                    "	from\n" +
                    "		fm.estoque es\n" +
                    "		join (select \n" +
                    "			CodMercadoria, \n" +
                    "			max(data) data\n" +
                    "		from \n" +
                    "			fm.estoque \n" +
                    "		group by \n" +
                    "			codMercadoria) ids on\n" +
                    "			es.codmercadoria = ids.codmercadoria and\n" +
                    "			es.data = ids.data\n" +
                    "	group by\n" +
                    "		es.codMercadoria) est on\n" +
                    "		e.Codigo = est.Codigo"
            )) {
                while (rst.next()) {
                    estoque.put(rst.getInt("id_produto"), rst.getDouble("estoque"));
                }
            }
            
            LOG.fine("Número de estoque encontrado: " + estoque.size());
            
            Map<String, String> mercadologicos = new HashMap<>();
            for (MercadologicoIMP mp: getMercadologicos()) {
                mercadologicos.put(mp.getMerc1Descricao(), mp.getMerc1ID());
            }
            
            LOG.fine("Número de mercadológicos mapeados: " + mercadologicos.size());
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	 p.Codigo id,\n" +
                    "    p.DataCadastro datacadastro,\n" +
                    "    p.CodBarras ean,\n" +
                    "    p.UM unidade,\n" +
                    "    p.Validade,\n" +
                    "    p.Nome descricaocompleta,\n" +
                    "    p.Secao merc1,\n" +
                    "    p.Familia id_familia,\n" +
                    "    p.Peso,\n" +
                    "    p.EstoqueMin,\n" +
                    "    p.Custo,\n" +
                    "    p.Venda preco,\n" +
                    "    p.NCM,\n" +
                    "    p.CEST,\n" +
                    "    substring(p.cstPIS, 1, 2) piscofins_saida,\n" +
                    "    substring(p.cstICMS, 1, 2) icms_cst,\n" +
                    "    p.AliICMS icms_aliquota,\n" +
                    "    p.AliRedBaseCalcICMS icms_reduzido\n" +
                    "from\n" +
                    "	fm.mercadorias p\n" +
                    "order by\n" +
                    "	p.Codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    Double est = estoque.get(rst.getInt("id"));
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("Validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(mercadologicos.get(rst.getString("merc1")));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("Peso"));
                    imp.setPesoLiquido(rst.getDouble("Peso"));
                    imp.setEstoqueMinimo(rst.getDouble("EstoqueMin"));
                    imp.setEstoque(est != null ? est : 0D);
                    imp.setCustoComImposto(rst.getDouble("Custo"));
                    imp.setCustoSemImposto(rst.getDouble("Custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	 f.Codigo id,\n" +
                    "    f.Nome razao,\n" +
                    "    f.CNPJ cnpj,\n" +
                    "    f.IE,\n" +
                    "    f.Endereco,\n" +
                    "    f.Numero,\n" +
                    "    f.Complemento,\n" +
                    "    f.Bairro,\n" +
                    "    f.CodMunicipio,\n" +
                    "    f.CEP,\n" +
                    "    f.Fone1,\n" +
                    "    f.Fone2,\n" +
                    "    f.DataCadastro,\n" +
                    "    f.Observacoes,\n" +
                    "    f.Contato\n" +
                    "from\n" +
                    "	fm.fornecedores f\n" +
                    "order by\n" +
                    "	f.Codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setIbge_municipio(rst.getInt("CodMunicipio"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTel_principal(rst.getString("Fone1"));
                    imp.setDatacadastro(rst.getDate("DataCadastro"));
                    imp.setObservacao(rst.getString("Observacoes"));
                    imp.addContato(rst.getString("Contato"), rst.getString("Fone2"), "", TipoContato.COMERCIAL, "");
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>();
        opt.addAll(OpcaoProduto.getMercadologico());
        opt.addAll(OpcaoProduto.getFamilia());
        opt.addAll(OpcaoProduto.getProduto());
        opt.addAll(OpcaoProduto.getComplementos());
        opt.addAll(Arrays.asList(
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.ICMS,
                OpcaoProduto.PIS_COFINS
        ));
        opt.addAll(Arrays.asList(
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE
        ));
        opt.remove(OpcaoProduto.MARGEM);
        opt.remove(OpcaoProduto.NATUREZA_RECEITA);
        return opt;
    }
    
    private List<ClientePreferencialVO> carregarCliente(int idLoja, int idLOjaCliente) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        String nome, endereco, bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge = null,
               dataResidencia, dataCadastro, numero, complemento, dataNascimento, nomePai, nomeMae,
               telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
               conjuge = "", orgaoExp = "", celular;
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha = 0,
                estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT codigo, datacadastro, nome, endereco, numero, "
                            + "complemento,  bairro, cidade, estado, cep, observacoes, "
                            + "fone1, fone2, celular, rg, cpf, DataNascimento, email, limitecredito, bloqueado "
                            + "FROM clientes"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    id = rst.getInt("codigo");
                    id_situacaocadastro = 1;
                    dataResidencia = "1990/01/01";
                    id_tipoinscricao = 1;
                    
                    if ((rst.getString("nome") != null)
                            && (!rst.getString("nome").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("nome");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nome = "SEM NOME VR " + id;
                    }
                    
                    if ((rst.getString("endereco") != null)
                            && (!rst.getString("endereco").trim().isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("endereco").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }
                    
                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cpf").trim()));
                    } else {
                        cnpj = -1;
                    }
                    
                    if ((rst.getString("bairro") != null)
                            && (!rst.getString("bairro").trim().isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }
                    
                    if ((rst.getString("fone1") != null)
                            && (!rst.getString("fone1").trim().isEmpty())) {
                        telefone1 = Utils.formataNumero(rst.getString("fone1").trim());
                    } else {
                        telefone1 = "0000000000";
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        celular = Utils.formataNumero(rst.getString("celular").trim());
                    } else {
                        celular = "0";
                    }
                    
                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").trim().isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("cep").trim()));
                    } else {
                        cep = Parametros.get().getCepPadrao();
                    }
                    
                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").trim().isEmpty())) {
                        if ((rst.getString("estado") != null)
                                && (!rst.getString("estado").trim().isEmpty())) {
                            id_municipio = Utils.retornarMunicipioIBGEDescricao(rst.getString("cidade").trim().replace("'", ""),
                                    rst.getString("estado").trim().replace("'", ""));
                            
                            if (id_municipio == 0) {
                                id_municipio = Parametros.get().getMunicipioPadrao2().getId();
                            }
                        } else {
                            id_municipio = Parametros.get().getMunicipioPadrao2().getId();
                        }
                    } else {
                        id_municipio = Parametros.get().getMunicipioPadrao2().getId();
                    }
                    
                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").trim().isEmpty())) {
                        id_estado = Utils.retornarEstadoDescricao(
                                rst.getString("estado").trim().replace("'", ""));
                        
                        if (id_estado == 0) {
                            id_estado = Parametros.get().getUfPadraoV2().getId();
                        } else {
                            id_estado = Parametros.get().getUfPadraoV2().getId();
                        }
                    } else {
                        id_estado = Parametros.get().getUfPadraoV2().getId();
                    }
                    
                    if ((rst.getString("numero") != null)
                            && (!rst.getString("numero").trim().isEmpty())) {
                        numero = Utils.acertarTexto(rst.getString("numero").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }
                    
                    if ((rst.getString("complemento") != null)
                            && (!rst.getString("complemento").trim().isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("complemento").trim().replace("'", ""));
                    } else {
                        complemento = "";
                    }
                    
                    if ((rst.getString("limitecredito") != null)
                            && (!rst.getString("limitecredito").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("limitecredito").replace(".", "").replace(",", "."));
                    } else {
                        limite = 0;
                    }
                    
                    if ((rst.getString("RG") != null)
                            && (!rst.getString("RG").trim().isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("RG").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "");
                        inscricaoestadual = inscricaoestadual.replace("-", "");
                        inscricaoestadual = inscricaoestadual.replace(".", "");
                    } else {
                        inscricaoestadual = "ISENTO";
                    }
                    
                    dataCadastro = rst.getString("datacadastro").substring(0, 10).trim().replace("-", "/");

                    if ((rst.getString("datanascimento") != null)
                            && (!rst.getString("datanascimento").trim().isEmpty())) {
                        dataNascimento = rst.getString("datanascimento").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataNascimento = null;
                    }
                    
                    if ((rst.getString("bloqueado") != null)
                            && (!rst.getString("bloqueado").trim().isEmpty())) {
                        if ("SIM".equals(rst.getString("bloqueado").trim())) {
                            bloqueado = true;
                        } else {
                            bloqueado = false;
                        }
                    } else {
                        bloqueado = false;
                    }

                    nomePai = "";
                    nomeMae = "";
                    telefone2 = "";
                    fax = "";
                    observacao = "";

                    if ((rst.getString("observacoes") != null)
                            && (!rst.getString("observacoes").trim().isEmpty())
                            && (rst.getString("observacoes").contains("@"))) {
                        email = Utils.acertarTexto(rst.getString("observacoes").trim().replace("'", ""));
                    } else {
                        email = "";
                    }

                    id_sexo = 1;
                    empresa = "";
                    telEmpresa = "";
                    cargo = "";
                    enderecoEmpresa = "";
                    salario = 0;
                    estadoCivil = 0;
                    conjuge = "";
                    orgaoExp = "";
                    
                    vo.setId(id) ;
                    vo.setNome(nome);
                    vo.setEndereco(endereco);
                    vo.setBairro(bairro);
                    vo.setId_estado(id_estado);
                    vo.setId_municipio(id_municipio);
                    vo.setCep(cep);
                    vo.setTelefone(telefone1);
                    vo.setInscricaoestadual(inscricaoestadual);
                    vo.setCnpj(cnpj);
                    vo.setSexo(id_sexo);
                    vo.setDataresidencia(dataResidencia);
                    vo.setDatacadastro(dataCadastro);
                    vo.setEmail(email);
                    vo.setValorlimite(limite);
                    vo.setCodigoanterior(id);
                    vo.setFax(fax);
                    vo.setBloqueado(bloqueado);
                    vo.setId_situacaocadastro(id_situacaocadastro);
                    vo.setCelular(celular);
                    vo.setObservacao(observacao);
                    vo.setDatanascimento(dataNascimento);
                    vo.setNomepai(nomePai);
                    vo.setNomemae(nomeMae);
                    vo.setEmpresa(empresa);
                    vo.setTelefoneempresa(telEmpresa);
                    vo.setNumero(numero);
                    vo.setCargo(cargo);
                    vo.setEnderecoempresa(enderecoEmpresa);
                    vo.setId_tipoinscricao(id_tipoinscricao);
                    vo.setSalario(salario);
                    vo.setId_tipoestadocivil(estadoCivil);
                    vo.setNomeconjuge(nomeConjuge);
                    vo.setOrgaoemissor(orgaoExp);
                    vResult.add(vo);                    
                }
            }
        }
        return vResult;
    }
    
    
    
    
    
    public List<FornecedorVO> carregarFornecedorFM() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        
        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                numero = "", complemento = "", telefone = "", email = "", fax = "";
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        Long cnpj, cep;
        double pedidoMin;
        boolean ativo = true;
        
        try {
            stm = ConexaoMySQL.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select forcod, fordes, forend, forbai, forcid, forest, ");
            sql.append("fortel, forfax, forcep, fornum, forcmp, forcon, forobs, ");
            sql.append("forfan, forcgc, forcgf, foremail, forpfpj, forpais, forcodibge ");
            sql.append("from fornecedor  ");
            sql.append("order by fordes ");
            
            rst = stm.executeQuery(sql.toString());
            
            Linha = 0;
            
            try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();
                    
                    id = rst.getInt("forcod");
                    
                    Linha++;
                    if (Linha == 3) {
                        Linha--;
                        Linha++;
                    }
                    if ((rst.getString("fordes") != null)
                            && (!rst.getString("fordes").isEmpty())) {
                        byte[] bytes = rst.getBytes("fordes");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }
                    
                    if ((rst.getString("forfan") != null)
                            && (!rst.getString("forfan").isEmpty())) {
                        byte[] bytes = rst.getBytes("forfan");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }
                    
                    if ((rst.getString("forcgc") != null)
                            && (!rst.getString("forcgc").isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("forcgc").trim()));
                    } else {
                        cnpj = Long.parseLong(rst.getString("forcod"));
                    }
                    
                    if ((rst.getString("forcgf") != null)
                            && (!rst.getString("forcgf").isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("forcgf").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }
                    
                    id_tipoinscricao = 0;
                    
                    if ((rst.getString("forend") != null)
                            && (!rst.getString("forend").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("forend").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }
                    
                    if ((rst.getString("forbai") != null)
                            && (!rst.getString("forbai").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("forbai").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }
                    
                    if ((rst.getString("forcep") != null)
                            && (!rst.getString("forcep").isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("forcep").trim()));
                    } else {
                        cep = Long.parseLong("0");
                    }
                    
                    if ((rst.getString("forcid") != null)
                            && (!rst.getString("forcid").isEmpty())) {
                        
                        if ((rst.getString("forest") != null)
                                && (!rst.getString("forest").isEmpty())) {
                            
                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("forcid").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("forest").replace("'", "").trim()));
                            
                            if (id_municipio == 0) {
                                id_municipio = 3525508;
                            }
                        }
                    } else {
                        id_municipio = 3525508;
                    }
                    
                    if ((rst.getString("forest") != null)
                            && (!rst.getString("forest").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("forest").replace("'", "").trim()));
                        
                        if (id_estado == 0) {
                            id_estado = 23;
                        }
                    } else {
                        id_estado = 23;
                    }
                    
                    if (rst.getString("forobs") != null) {
                        obs = rst.getString("forobs").trim();
                    } else {
                        obs = "";
                    }
                    
                    datacadastro = "";
                    
                    pedidoMin = 0;
                    
                    ativo = true;
                    
                    if ((rst.getString("forpfpj") != null)
                            && (!rst.getString("forpfpj").trim().isEmpty())) {
                        if ("J".equals(rst.getString("forpfpj").trim())) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }
                    } else {
                        id_tipoinscricao = 0;
                    }
                    
                    if ((rst.getString("fornum") != null)
                            && (!rst.getString("fornum").trim().isEmpty())) {
                        numero = util.acertarTexto(rst.getString("fornum").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }
                    
                    if ((rst.getString("forcmp") != null)
                            && (!rst.getString("forcmp").trim().isEmpty())) {
                        complemento = util.acertarTexto(rst.getString("forcmp").replace("'", "").trim());
                    } else {
                        complemento = "";
                    }
                    
                    if ((rst.getString("fortel") != null)
                            && (!rst.getString("fortel").trim().isEmpty())) {
                        telefone = util.formataNumero(rst.getString("fortel").trim());
                    } else {
                        telefone = "0";
                    }
                    
                    if (razaosocial.length() > 40) {
                        razaosocial = razaosocial.substring(0, 40);
                    }
                    
                    if (nomefantasia.length() > 30) {
                        nomefantasia = nomefantasia.substring(0, 30);
                    }
                    
                    if ((rst.getString("foremail") != null)
                            && (!rst.getString("foremail").trim().isEmpty())
                            && (rst.getString("foremail").contains("@"))) {
                        email = util.acertarTexto(rst.getString("foremail").replace("'", ""));
                    } else {
                        email = "";
                    }
                    
                    if ((rst.getString("forfax") != null)
                            && (!rst.getString("forfax").trim().isEmpty())) {
                        fax = util.formataNumero(rst.getString("forfax").trim());
                    } else {
                        fax = "";
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
                    
                    oFornecedor.codigoanterior = rst.getInt("forcod");
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
            } catch (Exception ex) {
                if (Linha > 0) {
                    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }
            
            return vFornecedor;
            
        } catch (SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    public List<ReceberChequeVO> carregarReceberCheque(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        
        int numerocupom, idBanco, cheque, idTipoInscricao, id_tipoalinea;
        double valor, juros;
        long cpfCnpj;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia, conta, nome, rg, telefone;
        
        try {
            
            stm = ConexaoMySQL.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT c.cheque, c.ciccgc, c.client, c.bancox, c.agenci, c.contax, ");
            sql.append("c.valorx, c.dataxx, c.vencim, c.status, c.devol1, c.motdv1, c.devol2, c.motdv2, ");
            sql.append("c.reapre, c.quitad, c.codfor, c.nomfor, c.datfor, c.caixax, c.observ, c.seqdev, ");
            sql.append("c.datcad, c.usucad, c.datalt, c.usualt, c.cobran, c.datcob, c.entrad ");
            sql.append("FROM CHEQUES c ");
            sql.append("WHERE c.FILIAL = " + String.valueOf(id_lojaCliente));
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberChequeVO oReceberCheque = new ReceberChequeVO();
                
                cpfCnpj = Long.parseLong(rst.getString("ciccgc").trim());
                
                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }
                
                idBanco = util.retornarBanco(Integer.parseInt(rst.getString("bancox").trim()));
                
                if ((rst.getString("agenci") != null)
                        && (!rst.getString("agenci").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("agenci").trim().replace("'", ""));
                } else {
                    agencia = "";
                }
                
                if ((rst.getString("contax") != null)
                        && (!rst.getString("contax").trim().isEmpty())) {
                    conta = util.acertarTexto(rst.getString("contax").trim().replace("'", ""));
                } else {
                    conta = "";
                }
                
                if ((rst.getString("cheque") != null)
                        && (!rst.getString("cheque").trim().isEmpty())) {
                    
                    cheque = Integer.parseInt(util.formataNumero(rst.getString("cheque")));
                    
                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }
                
                if ((rst.getString("dataxx") != null)
                        && (!rst.getString("dataxx").trim().isEmpty())) {
                    
                    dataemissao = rst.getString("dataxx").trim();
                } else {
                    dataemissao = "2016/02/01";
                }
                
                if ((rst.getString("vencim") != null)
                        && (!rst.getString("vencim").trim().isEmpty())) {
                    
                    datavencimento = rst.getString("vencim").trim();
                } else {
                    datavencimento = "2016/02/12";
                }
                
                if ((rst.getString("observ") != null)
                        && (!rst.getString("observ").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("observ").replace("'", "").trim());
                } else {
                    nome = "";
                }

                /*if ((rst.getString("chrinscrg") != null) &&
                 (!rst.getString("chrinscrg").isEmpty())) {
                 rg = util.acertarTexto(rst.getString("chrinscrg").trim().replace("'", ""));
                    
                 if (rg.length() > 20) {
                 rg = rg.substring(0, 20);
                 }
                 } else {*/
                rg = "";
                //}

                valor = Double.parseDouble(rst.getString("valorx"));
                numerocupom = 0;
                juros = 0;

                /*if ((rst.getString("chrobserv1") != null)
                 && (!rst.getString("chrobserv1").isEmpty())) {
                 observacao = util.acertarTexto(rst.getString("chrobserv1").replace("'", "").trim());
                 } else {*/
                observacao = "IMPORTADO VR";
                //}

                /*if ((rst.getString("chrtelefone") != null) &&
                 (!rst.getString("chrtelefone").isEmpty()) &&
                 (!"0".equals(rst.getString("chrtelefone").trim()))) {
                 telefone = util.formataNumero(rst.getString("chrtelefone"));
                 } else {*/
                telefone = "";
                //}

                if (rst.getInt("status") == 1) {
                    id_tipoalinea = 0;
                } else if (rst.getInt("status") == 2) {
                    id_tipoalinea = 15;
                } else {
                    id_tipoalinea = 0;
                }
                
                oReceberCheque.id_loja = id_loja;
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
    
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int id_loja, int id_lojaCliente) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        int id_cliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DATA, DESCRICAO, VALOR, VENCIMENTO, CODCLIENTE, CODCUPOM "
                    + "FROM fm.contasreceber "
                    + "WHERE PAGO = 'NÃO'"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                    
                    id_cliente = rst.getInt("CODCLIENTE");
                    dataemissao = rst.getString("DATA").substring(0, 10).trim();
                    datavencimento = rst.getString("VENCIMENTO").substring(0, 10).trim();
                    numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("CODCUPOM")));
                    valor = Double.parseDouble(rst.getString("VALOR"));
                    juros = 0;
                    ecf = 0;

                    if ((rst.getString("DESCRICAO") != null)
                            && (!rst.getString("DESCRICAO").isEmpty())) {
                        observacao = Utils.acertarTexto(rst.getString("DESCRICAO").replace("'", ""));
                    } else {
                        observacao = "IMPORTADO VR";
                    }
                    
                    vo.setId_loja(id_loja);
                    vo.setDataemissao(dataemissao);
                    vo.setNumerocupom(numerocupom);
                    vo.setValor(valor);
                    vo.setEcf(ecf);
                    vo.setObservacao(observacao);
                    vo.setId_clientepreferencial(id_cliente);
                    vo.setDatavencimento(datavencimento);
                    vo.setValorjuros(juros);;
                    vResult.add(vo);                    
                }
            }
        }
        return vResult;
    }
    
    
    //** **IMPORTAÇÕES ***/
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarReceberCreditoRotativo(int idLoja, int idLojaCliente) throws Exception {        
        try {            
            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);            
            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);            
        } catch (Exception ex) {            
            throw ex;
        }
    }
    
    
    /*
    *
    *
    *
    *
    *
    **/
    
    
    
    public void importarFornecedor() throws Exception {
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorFM();
            
            new FornecedorDAO().salvar(vFornecedor);
            
        } catch (Exception ex) {
            
            throw ex;
        }
    }
    
    public void importarChequeReceber(int id_loja, int id_lojaCliente) throws Exception {
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(id_loja, id_lojaCliente);
            
            new ReceberChequeDAO().salvar(vReceberCheque, id_loja);
            
        } catch (Exception ex) {
            
            throw ex;
        }
    }
    
    // FUNÇÕES
    private int retornarAliquotaICMSFM(String codTrib, String descTrib) {
        
        int retorno = 8;
        if (codTrib.trim() != "") {
            if ("7.0000".equals(codTrib.trim())) {
                retorno = 0;
            } else if ("12.0000".equals(codTrib.trim())) {
                retorno = 1;
            } else if ("18.0000".equals(codTrib.trim())) {
                retorno = 2;
            } else if ("25.0000".equals(codTrib.trim())) {
                retorno = 3;
            } else {
                retorno = 8;
            }
        } else if (descTrib.trim() != "") {
            if ("NN".equals(descTrib.trim())) {
                retorno = 6;
            } else if ("FF".equals(descTrib.trim())) {
                retorno = 7;
            } else if ("II".equals(descTrib.trim())) {
                retorno = 6;
            } else if ("IC".equals(descTrib.trim())) {
                retorno = 8;
            } else {
                retorno = 8;
            }
        }
        return retorno;
    }

    @Override
    public String getSistema() {
        return "FM";
    }

    public Iterable<Estabelecimento> getLojas() {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
}
