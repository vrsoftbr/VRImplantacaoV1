package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SacLumiDAO extends InterfaceDAO {

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	nv1.nivel0 merc1,\n" +
                    "	nv1.descricao merc1_desc,\n" +
                    "	nv2.nivel1 merc2,\n" +
                    "	nv2.descricao merc2_desc,\n" +
                    "	nv3.nivel2 merc3,\n" +
                    "	nv3.descricao merc3_desc,\n" +
                    "	nv4.nivel3 merc4,\n" +
                    "	nv4.descricao merc4_desc,\n" +
                    "	nv5.nivel4 merc5,\n" +
                    "	nv5.descricao merc5_desc\n" +
                    "from\n" +
                    "	(select distinct nivel0, descricao from plano where tipo = 'M' and nivel1 = '000') nv1\n" +
                    "	left join (select distinct nivel0, nivel1, descricao from plano where tipo = 'M' and nivel1 != '000' and nivel2 = '000') nv2\n" +
                    "	on nv1.nivel0 = nv2.nivel0\n" +
                    "	left join (select distinct nivel0, nivel1, nivel2, descricao from plano where tipo = 'M' and nivel2 != '000' and nivel3 = '000') nv3\n" +
                    "	on nv2.nivel0 = nv3.nivel0 and nv2.nivel1 = nv3.nivel1\n" +
                    "	left join (select distinct nivel0, nivel1, nivel2, nivel3, descricao from plano where tipo = 'M' and nivel3 != '000' and nivel4 = '000') nv4\n" +
                    "	on nv3.nivel0 = nv4.nivel0 and nv3.nivel1 = nv4.nivel1 and nv3.nivel2 = nv4.nivel2\n" +
                    "	left join (select distinct nivel0, nivel1, nivel2, nivel3, nivel4, descricao from plano where tipo = 'M' and nivel4 != '000' and nivel5 = '000') nv5\n" +
                    "	on nv4.nivel0 = nv5.nivel0 and nv4.nivel1 = nv5.nivel1 and nv4.nivel2 = nv5.nivel2 and nv4.nivel3 = nv5.nivel3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
                    imp.setMerc4ID(rst.getString("merc4"));
                    imp.setMerc4Descricao(rst.getString("merc4_desc"));
                    imp.setMerc5ID(rst.getString("merc5"));
                    imp.setMerc5Descricao(rst.getString("merc5_desc"));
                    
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
            Set<String> familias = new LinkedHashSet<>();
            try (ResultSet rst = stm.executeQuery(
                "select \n" +
                "	produto \n" +
                "from \n" +
                "	embalagem \n" +
                "group by \n" +
                "	produto \n" +
                "having \n" +
                "	count(*) > 1"
            )) {
                while (rst.next()) {
                    familias.add(rst.getString("produto"));
                }
            }
            
            if (!familias.isEmpty()) {
                String where = "";
                for (Iterator<String> iterator = familias.iterator(); iterator.hasNext(); ) {
                    where += iterator.next();
                    if (iterator.hasNext()) {
                        where += ",\n";
                    }
                }
                try (ResultSet rst = stm.executeQuery(
                        "select \n" +
                        "	codigo, \n" +
                        "	descricao \n" +
                        "from \n" +
                        "	produto \n" +
                        "where \n" +
                        "	codigo in\n" +
                        "	(" + where + ")"
                )) {
                    while (rst.next()) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("codigo"));
                        imp.setDescricao(rst.getString("descricao"));

                        result.add(imp);
                    }
                }
            }
        }
        
        return result;
    }
    
    

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            stm.setFetchSize(1000);
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	trim(ean.codproduto) id,\n" +
                    "	p.horainc datacadastro,\n" +
                    "	case when trim(coalesce(ean.BARRA1,'')) != '' then trim(coalesce(ean.BARRA1,'')) else ean.codproduto end ean,\n" +
                    "	trim(coalesce(ean.BARRA2,'')) barra2,\n" +
                    "	trim(coalesce(ean.BARRA3,'')) barra3,\n" +
                    "	trim(coalesce(ean.PLU,'')) plu,\n" +
                    "	1 qtdEmbalagem,\n" +
                    "	ean.unidade tipoEmbalagem,\n" +
                    "	case upper(p.BALANCA) when 'S' then 1 else 0 end eBalanca,\n" +
                    "	case when ean.dias > 0 then ean.dias else p.dias end validade,\n" +
                    "	case when trim(coalesce(ean.descricao,'')) != ''\n" +
                    "	then concat(trim(p.descricao), ' ', trim(coalesce(ean.descricao,''))) else trim(p.descricao) end descricaocompleta,\n" +
                    "	case when trim(coalesce(ean.descricao,'')) != ''\n" +
                    "	then concat(trim(p.reduzido), ' ', trim(coalesce(ean.descricao,''))) else trim(p.reduzido) end descricaoreduzida,\n" +
                    "	case when trim(coalesce(ean.descricao,'')) != ''\n" +
                    "	then concat(trim(p.descricao), ' ', trim(coalesce(ean.descricao,''))) else trim(p.descricao) end descricaogondola,\n" +
                    "	rpad(p.grupo, 17, '0') mercadologico,\n" +
                    "	case when ean.PRODUTO != ean.CODPRODUTO then ean.produto else null end idfamilia,\n" +
                    "	ean.PESO pesobruto,\n" +
                    "	ean.PESO pesoliquido,\n" +
                    "	pr.minimo estoqueminimo,\n" +
                    "	pr.maximo estoquemaximo,\n" +
                    "	pr.estoque,\n" +
                    "	case when pl.margem = 0 then p.margem else pl.margem end margem,\n" +
                    "	pr.custoped custocomimposto,\n" +
                    "	pr.custoped custosemimposto,\n" +
                    "	pr.VALOR valorvenda,\n" +
                    "	case when ean.inativo = 'S' then 0 else 1 end situacaoCadastro,\n" +
                    "	case p.classificacao when 0 then pl.classificacao else p.classificacao end ncm,\n" +
                    "	p.cest,\n" +
                    "	p.cod_pis piscofinsCstCredito,\n" +
                    "	p.cod_cofins piscofinsCstDebito,\n" +
                    "	p.nat_rec_saida piscofinsNaturezaReceita,\n" +
                    "	f.atrib icmsCstSaida,\n" +
                    "	f.atrib2 icmsCstEntrada,\n" +
                    "	f.imposto icmsAliqSaida,\n" +
                    "	f.impostos icmsAliqEntrada,\n" +
                    "	f.reducao icmsReducaoSaida,\n" +
                    "	f.reducaos icmsReducaoEntrada\n" +
                    "from\n" +
                    "   produto p\n" +
                    "	join embalagem ean on\n" +
                    "		ean.PRODUTO = p.CODIGO\n" +
                    "	left join estab est on\n" +
                    "		est.cnpj =  " + Utils.quoteSQL(getLojaOrigem()) + "\n" +
                    "	left join preco pr on\n" +
                    "		pr.nsu = ean.codproduto and\n" +
                    "		pr.cnpj = est.cnpj\n" +
                    "	left join plano pl on\n" +
                    "		pl.tipo = 'M' and\n" +
                    "		concat(pl.nivel0,pl.nivel1,pl.nivel2,pl.nivel3,pl.nivel4,pl.nivel5) = rpad(p.grupo, 17, '0')\n" +
                    "	left join fiscal f on\n" +
                    "		f.codigo = p.FISCAL and\n" +
                    "		f.uforigem = est.uf and\n" +
                    "		f.ufdestino = est.uf\n" +
                    "order by\n" +
                    "	ean.codproduto"
            )) {
                while (rst.next()) {
                    if (rst.getBoolean("eBalanca")) {
                        ProdutoIMP imp = gerarProdutoImp(rst, rst.getString("plu") + MathUtils.getDV(rst.getLong("plu")));
                        result.add(imp);
                    } else {
                        if (!"".equals(rst.getString("ean"))) {
                            ProdutoIMP imp = gerarProdutoImp(rst, rst.getString("ean"));
                            result.add(imp);
                        }
                        if (!"".equals(rst.getString("barra2"))) {
                            ProdutoIMP imp = gerarProdutoImp(rst, rst.getString("barra2"));
                            result.add(imp);
                        }
                        if (!"".equals(rst.getString("barra3"))) {
                            ProdutoIMP imp = gerarProdutoImp(rst, rst.getString("barra3"));
                            result.add(imp);
                        }
                    }
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
                    "	trim(f.cnpj) id,\n" +
                    "	f.razao,\n" +
                    "	f.fantasia,\n" +
                    "	trim(f.cnpj) cnpj,\n" +
                    "	f.ie,\n" +
                    "	0 insc_municipal,\n" +
                    "	f.suframa,\n" +
                    "	f.endereco,\n" +
                    "	0 numero,\n" +
                    "	f.compl,\n" +
                    "	f.bairro,\n" +
                    "	f.CODMUNICIPIO ibge_municipio,\n" +
                    "	f.cep,\n" +
                    "	f.TELEFONE tel_principal,\n" +
                    "	f.VALOR,\n" +
                    "	f.CADASTRO,\n" +
                    "	f.DIAS,\n" +
                    "	trim(coalesce(f.email,'')) email\n" +
                    "from\n" +
                    "	fornece f\n" +
                    "where\n" +
                    "	f.tipo = 'F'\n and char_length(cast(cast(f.cnpj as signed) as char)) > 11\n" +
                    "order by\n" +
                    "	f.cnpj;"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("compl"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(format.parse(rst.getString("cadastro")));
                    
                    //imp.setPrazoEntrega(rst.getInt("dias"));
                    //imp.setCondicaoPagamentoPadrao(rst.getInt("vencimento"));
                    
                    if ("".equals(rst.getString("email"))) {
                        FornecedorContatoIMP a = imp.getContatos().make(rst.getString("email"));
                        a.setImportId("1");
                        a.setNome("EMAIL");
                        a.setEmail(rst.getString("email"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	tf.FORNECE id_fornecedor,\n" +
                    "	tf.NSU id_produto,\n" +
                    "	tf.CODFOR codigoexterno,\n" +
                    "	case tf.FATORFOR when 0 then 1 else tf.fatorfor end qtdEmbalagem\n" +
                    "FROM \n" +
                    "	sac.tabfor tf\n" +
                    "order by\n" +
                    "	tf.FORNECE,\n" +
                    "	tf.NSU"
            )) {                
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();                    
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.id,\n" +
                    "	c.cnpj,\n" +
                    "	c.ie inscricaoestadual,\n" +
                    "	c.emissor orgaoemissor,\n" +
                    "	c.razao,\n" +
                    "	c.fantasia,\n" +
                    "	1 ativo,\n" +
                    "	case when status in ('0034', '0045') then 1 else 0 end bloqueado,\n" +
                    "	null dataBloqueio,\n" +
                    "	c.endereco,\n" +
                    "	0 numero,\n" +
                    "	c.compl complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.cidade,\n" +
                    "	c.uf,\n" +
                    "	c.cep,\n" +
                    "	c.civil,\n" +
                    "	trim(coalesce(c.nascimento,'')) nascimento,\n" +
                    "	trim(coalesce(c.cadastro,'')) cadastro,\n" +
                    "	c.sexo,\n" +
                    "	c.empresa,\n" +
                    "	trim(coalesce(c.admissao,'')) admissao,\n" +
                    "	c.profissao cargo,\n" +
                    "	c.renda,\n" +
                    "	c.limite,\n" +
                    "	c.conjuge,\n" +
                    "	c.pai,\n" +
                    "	c.mae,\n" +
                    "	c.msg observacao,\n" +
                    "	c.refbanco,\n" +
                    "	c.refcom,\n" +
                    "	c.refpessoal,\n" +
                    "	c.vencimento,\n" +
                    "	1 permiteCreditoRotativo,\n" +
                    "	1 permiteCheque,\n" +
                    "	c.telefone,\n" +
                    "	c.tel2,\n" +
                    "	c.email\n" +
                    "from \n" +
                    "	cliente c\n" +
                    "order by\n" +
                    "	c.id"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                int cont1 = 0, cont2 = 0;
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setDataBloqueio(rst.getDate("dataBloqueio"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    if (!"".equals(rst.getString("nascimento"))) {
                        imp.setDataNascimento(format.parse(rst.getString("nascimento")));
                    }
                    if (!"".equals(rst.getString("cadastro"))) {
                        imp.setDataCadastro(format.parse(rst.getString("cadastro")));
                    }
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("empresa"));
                    if (!"".equals(rst.getString("admissao"))) {
                        imp.setDataAdmissao(format.parse(rst.getString("admissao")));
                    }
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setObservacao(
                            "Observações: " + rst.getString("observacao") + "\n" +
                            "Ref. Banco: " + rst.getString("refbanco") + "\n" +
                            "Ref. Com.: " + rst.getString("refcom") + "\n" +
                            "Ref. Pessoal: " + rst.getString("refpessoal")
                    );
                    imp.setDiaVencimento(rst.getInt("vencimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("tel2"));
                    imp.setEmail(rst.getString("email"));
                    
                    result.add(imp);
                    
                    cont1++;
                    cont2++;
                    if (cont1 == 100) {
                        cont1 = 0;
                        ProgressBar.setStatus("Carregando os clientes..." + cont2);
                    }
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientesEventuais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	trim(f.cnpj) id,\n" +
                    "	trim(f.cnpj) cnpj,\n" +
                    "	f.ie inscricaoestadual,\n" +
                    "	f.razao,\n" +
                    "	f.fantasia,\n" +
                    "	1 ativo,\n" +
                    "	0 bloqueado,\n" +
                    "	f.endereco,\n" +
                    "	0 numero,\n" +
                    "	f.compl complemento,\n" +
                    "	f.bairro,\n" +
                    "	f.cidade,\n" +
                    "	f.uf,\n" +
                    "	f.cep,\n" +
                    "	f.CADASTRO datacadastro,\n" +
                    "	f.VALOR limite,\n" +
                    "	f.DIAS diavencimento,\n" +
                    "	f.TELEFONE telefone,\n" +
                    "	trim(coalesce(f.email,'')) email\n" +
                    "from\n" +
                    "	fornece f\n" +
                    "where\n" +
                    "	f.tipo = 'F' and char_length(cast(cast(f.cnpj as signed) as char)) <= 11\n" +
                    "order by\n" +
                    "	f.cnpj;"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                int cont1 = 0, cont2 = 0;
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    if (!"".equals(rst.getString("datacadastro"))) {
                        imp.setDataCadastro(format.parse(rst.getString("datacadastro")));
                    }
                    imp.setDiaVencimento(rst.getInt("diavencimento"));                    
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email"));
                    
                    result.add(imp);
                    
                    cont1++;
                    cont2++;
                    if (cont1 == 100) {
                        cont1 = 0;
                        ProgressBar.setStatus("Carregando os clientes..." + cont2);
                    }
                }
            }
        }
        
        return result;
    }

    private ProdutoIMP gerarProdutoImp(final ResultSet rst, String ean) throws SQLException {
        ProdutoIMP imp = new ProdutoIMP();
        imp.setImportSistema(getSistema());
        imp.setImportLoja(getLojaOrigem());
        imp.setImportId(rst.getString("id"));
        imp.setDataCadastro(rst.getDate("datacadastro"));
        imp.setEan(ean);
        imp.setQtdEmbalagem(1);
        imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
        imp.seteBalanca(rst.getBoolean("eBalanca"));
        imp.setValidade(rst.getInt("validade"));
        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
        imp.setDescricaoGondola(rst.getString("descricaogondola"));
        String merc = rst.getString("mercadologico");
        String merc1 = merc.substring(0, 2),
                merc2 = merc.substring(2, 5),
                merc3 = merc.substring(5, 8),
                merc4 = merc.substring(8, 11),
                merc5 = merc.substring(11, 14);
        if (!"00".equals(merc1)) {
            imp.setCodMercadologico1(merc1);
            if (!"000".equals(merc2)) {
                imp.setCodMercadologico2(merc2);
                if (!"000".equals(merc3)) {
                    imp.setCodMercadologico3(merc3);
                    if (!"000".equals(merc4)) {
                        imp.setCodMercadologico4(merc4);
                        if (!"000".equals(merc5)) {
                            imp.setCodMercadologico5(merc5);
                        }
                    }
                }
            }
        }
        imp.setIdFamiliaProduto(rst.getString("idfamilia"));
        imp.setPesoBruto(rst.getDouble("pesobruto"));
        imp.setPesoLiquido(rst.getDouble("pesobruto"));
        imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
        imp.setEstoqueMinimo(rst.getDouble("estoquemaximo"));
        imp.setEstoque(rst.getDouble("estoque"));
        imp.setMargem(rst.getDouble("margem"));
        imp.setCustoSemImposto(rst.getDouble("custocomimposto"));
        imp.setCustoComImposto(rst.getDouble("custosemimposto"));
        imp.setPrecovenda(rst.getDouble("valorvenda"));
        imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaoCadastro")));
        imp.setNcm(rst.getString("ncm"));
        imp.setCest(rst.getString("cest"));
        imp.setPiscofinsCstDebito(rst.getInt("piscofinsCstDebito"));
        imp.setPiscofinsCstCredito(rst.getInt("piscofinsCstCredito"));
        imp.setPiscofinsNaturezaReceita(rst.getInt("piscofinsNaturezaReceita"));
        imp.setIcmsCstEntrada(rst.getInt("icmsCstEntrada"));
        imp.setIcmsAliqEntrada(rst.getInt("icmsAliqEntrada"));
        imp.setIcmsReducaoEntrada(rst.getInt("icmsReducaoEntrada"));
        imp.setIcmsCstSaida(rst.getInt("icmsCstSaida"));
        imp.setIcmsAliqSaida(rst.getInt("icmsAliqSaida"));
        imp.setIcmsReducaoSaida(rst.getInt("icmsReducaoSaida"));
        return imp;
    }  
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	cnpj, \n" +
                    "	razao \n" +
                    "from \n" +
                    "	estab \n" +
                    "order by \n" +
                    "	razao;"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cnpj"), rst.getString("razao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public String getSistema() {
        return "SAC Lumi";
    }

    public void importarSeletiva() throws Exception {
        ProgressBar.setStatus("Seletiva...Obtendo produtos anteriores...");
        
        Map<String, Set<Integer>> seletivas = new LinkedHashMap<>();
        ProdutoAnteriorDAO antDAO = new ProdutoAnteriorDAO();
        antDAO.setImportLoja(getLojaOrigem());
        antDAO.setImportSistema(getSistema());
        MultiMap<String, ProdutoAnteriorVO> anteriores = antDAO.getCodigoAnterior();
        
        ProgressBar.setStatus("Seletiva...Obtendo itens da seletiva");
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  i.material seletiva,\n" +
                    "  i.produto\n" +
                    "FROM\n" +
                    "  item i\n" +
                    "  join produto p on\n" +
                    "    i.produto = p.codigo\n" +
                    "group by\n" +
                    "  i.material,\n" +
                    "  i.produto"
            )) {
                while (rst.next()) {
                    System.out.println("" + rst.getString("seletiva") + ": " + rst.getString("produto"));
                    ProdutoAnteriorVO vo = anteriores.get(
                            getSistema(),
                            getLojaOrigem(),
                            rst.getString("produto").trim()
                    );
                    if (vo != null && vo.getCodigoAtual() != null) {
                        String seletiva = rst.getString("seletiva").trim();
                        Set<Integer> prods = seletivas.get(seletiva);
                        
                        if (prods == null) {
                            prods = new HashSet<>();
                            seletivas.put(seletiva, prods);
                        }                        
                        prods.add(vo.getCodigoAtual().getId());
                    }                    
                }
            }
        }
        
        ProgressBar.setStatus("Seletiva...Obtendo produto fornecedores existentes");
        MultiMap<Integer, Void> prodForExistentes = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_fornecedor, id_produto from produtofornecedor"
            )) {
                while (rst.next()) {
                    prodForExistentes.put(
                            null, 
                            rst.getInt("id_fornecedor"),
                            rst.getInt("id_produto")
                    );
                }
            }
        }
        
        System.out.println(String.format("Anteriores: %d; Seletivas: %d; Produtos Fornecedores: %d", anteriores.size(), seletivas.size(), prodForExistentes.size()));
        
        try (Statement stmExec = Conexao.createStatement()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	selecao,\n" +
                        "	descricao,\n" +
                        "	id_fornecedor\n" +
                        "from \n" +
                        "	implantacao.tp"
                )) {
                    Conexao.begin();
                    try {
                        int cont = 0;
                        while (rst.next()) {
                            Set<Integer> seletiva = seletivas.get(rst.getString("selecao").trim());
                            if (seletiva != null) {
                                for (int id_produto: seletiva) {
                                    int id_fornecedor = rst.getInt("id_fornecedor");
                                    if (!prodForExistentes.containsKey(id_fornecedor, id_produto)) {
                                        SQLBuilder sql = new SQLBuilder();
                                        sql.setTableName("produtofornecedor");
                                        sql.put("id_produto", id_produto);
                                        sql.put("id_fornecedor", id_fornecedor);
                                        sql.put("id_estado", Parametros.get().getUfPadraoV2().getId());
                                        sql.put("custotabela", 0);
                                        sql.put("codigoexterno", String.valueOf(id_produto));
                                        sql.put("qtdembalagem", 1);
                                        sql.put("id_divisaofornecedor", 0);
                                        sql.put("dataalteracao", new Date());
                                        sql.put("desconto", 0);
                                        sql.put("tipoipi", 0);
                                        sql.put("ipi", 0);
                                        sql.put("tipobonificacao", 0);
                                        sql.put("bonificacao", 0);
                                        sql.put("tipoverba", 0);
                                        sql.put("verba", 0);
                                        sql.put("custoinicial", 0);
                                        sql.put("tipodesconto", 0);
                                        sql.put("pesoembalagem", 0);
                                        sql.put("id_tipopiscofins", 0);
                                        sql.putNull("csosn");
                                        sql.put("fatorembalagem", 0);
                                        stmExec.execute(sql.getInsert());
                                        prodForExistentes.put(null, id_fornecedor, id_produto);
                                    }
                                }
                            }
                            cont++;
                            ProgressBar.setStatus("Seletiva...Gravando..." + cont);
                        }       
                        Conexao.commit();
                    } catch (Exception e) {
                        Conexao.rollback();
                        throw e;
                    }
                }
            }
        }
        
    }    
    
    @Override
    public List<NutricionalFilizolaVO> getNutricionalFilizola() throws Exception {        
        List<NutricionalFilizolaVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()){            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	trim(ean.codproduto) id,\n" +
                    "	substring(p.REDUZIDO, 1, 20) descricao,\n" +
                    "	p.qtd_nut quantidade,\n" +
                    "	p.und_nut unidade,\n" +
                    "	p.qtd_med medida_caseira,\n" +
                    "	p.VALOR_NUT1 valorenergetico,\n" +
                    "	p.VALOR_NUT2 carboidratos,\n" +
                    "	p.VALOR_NUT3 proteinas,\n" +
                    "	p.VALOR_NUT4 gordurastotais,\n" +
                    "	p.VALOR_NUT5 gordurassaturadas,\n" +
                    "	p.VALOR_NUT6 gordurastrans,\n" +
                    "	p.VALOR_NUT7 fibraalimentar,\n" +
                    "	p.VALOR_NUT8 sodio,\n" +
                    "	p.VALOR_FERRO ferro\n" +
                    "from\n" +
                    "   produto p\n" +
                    "	join embalagem ean on\n" +
                    "		ean.PRODUTO = p.CODIGO\n" +
                    "where\n" +
                    "	p.nutricao = 'S'\n" +
                    "order by\n" +
                    "	ean.codproduto"
            )) {            
                while (rst.next()) {
                    NutricionalFilizolaVO oNutricionalFilizola = new NutricionalFilizolaVO();
                    oNutricionalFilizola.setCaloria(rst.getInt("valorenergetico"));
                    oNutricionalFilizola.setCarboidrato(rst.getInt("carboidratos"));
                    oNutricionalFilizola.setProteina(rst.getInt("proteinas"));
                    oNutricionalFilizola.setGordura(rst.getInt("gordurastotais"));
                    oNutricionalFilizola.setGordurasaturada(rst.getInt("gordurassaturadas"));
                    oNutricionalFilizola.setGorduratrans(rst.getInt("gordurastrans"));
                    oNutricionalFilizola.setFibra(rst.getInt("fibraalimentar"));
                    oNutricionalFilizola.setSodio(rst.getInt("sodio"));
                    oNutricionalFilizola.setFerro(rst.getInt("ferro"));
                    oNutricionalFilizola.setDescricao(rst.getString("descricao"));
                    oNutricionalFilizola.setPorcao(rst.getString("quantidade") + " " + rst.getString("unidade"));

                    NutricionalFilizolaItemVO oNutricionalFilizolaItem = new NutricionalFilizolaItemVO();
                    oNutricionalFilizolaItem.setStrID(rst.getString("id"));
                    oNutricionalFilizola.vNutricionalFilizolaItem.add(oNutricionalFilizolaItem);

                    result.add(oNutricionalFilizola);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<NutricionalToledoVO> getNutricionalToledo() throws Exception {        
        List<NutricionalToledoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()){            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	trim(ean.codproduto) id,\n" +
                    "	substring(p.REDUZIDO, 1, 20) descricao,\n" +
                    "	p.qtd_nut quantidade,\n" +
                    "	p.und_nut unidade,\n" +
                    "	p.qtd_med medida_caseira,\n" +
                    "	p.VALOR_NUT1 valorenergetico,\n" +
                    "	p.VALOR_NUT2 carboidratos,\n" +
                    "	p.VALOR_NUT3 proteinas,\n" +
                    "	p.VALOR_NUT4 gordurastotais,\n" +
                    "	p.VALOR_NUT5 gordurassaturadas,\n" +
                    "	p.VALOR_NUT6 gordurastrans,\n" +
                    "	p.VALOR_NUT7 fibraalimentar,\n" +
                    "	p.VALOR_NUT8 sodio,\n" +
                    "	p.VALOR_FERRO ferro\n" +
                    "from\n" +
                    "   produto p\n" +
                    "	join embalagem ean on\n" +
                    "		ean.PRODUTO = p.CODIGO\n" +
                    "where\n" +
                    "	p.nutricao = 'S'\n" +
                    "order by\n" +
                    "	ean.codproduto"
            )) {            
                while (rst.next()) {
                    NutricionalToledoVO nutri = new NutricionalToledoVO();
                    nutri.setCaloria(rst.getInt("valorenergetico"));
                    nutri.setCarboidrato(rst.getInt("carboidratos"));
                    nutri.setProteina(rst.getInt("proteinas"));
                    nutri.setGordura(rst.getInt("gordurastotais"));
                    nutri.setGordurasaturada(rst.getInt("gordurassaturadas"));
                    nutri.setGorduratrans(rst.getInt("gordurastrans"));
                    nutri.setFibra(rst.getInt("fibraalimentar"));
                    nutri.setSodio(rst.getInt("sodio"));
                    nutri.setFerro(rst.getInt("ferro"));
                    nutri.setDescricao(rst.getString("descricao"));
                    nutri.setQuantidade(rst.getInt("quantidade"));

                    NutricionalToledoItemVO item = new NutricionalToledoItemVO();
                    item.setStrID(rst.getString("id"));
                    nutri.vNutricionalToledoItem.add(item);

                    result.add(nutri);
                }
            }
        }
        
        return result;
    }

    /*private List<NutricionalToledoVO> carregarNutricionalToledo() throws Exception {
        List<NutricionalToledoVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()){
            
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {            
                while (rst.next()) {
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

                    result.add(oNutricionalToledo);
                }
            }
        }
        
        return result;
    }*/
}
