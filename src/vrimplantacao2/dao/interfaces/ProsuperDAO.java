package vrimplantacao2.dao.interfaces;

/**
 *
 * @author guilhermegomes
 * Banco de dados em arquivo DBF
 * Aqui está armazenado os scripts para criar a estrutura das planilhas para importação
 */
public class ProsuperDAO {
    
    //<editor-fold defaultstate="collapsed" desc="Mercadológico">
    /*create table implantacao.impmercadologico
    (
            ELCODIGO varchar,
            ELDESCRI varchar
    );

    copy 
            implantacao.impmercadologico 
    from 
            '/home/guilhermegomes/Documents/Cliente/SP/Popular - SP/planilha/mercadologico.csv'
    with 
            encoding 'win1252' 
            delimiter '^' 
            csv header;

    with 
            merc as 
                    (select
                            elcodigo,
                            substring(elcodigo, 0, 3) merc1,
                            case when (trim(substring(elcodigo, 4, 3))) = '' then '000' else (trim(substring(elcodigo, 4, 3))) end merc2,
                            case when (trim(substring(elcodigo, 8, 3))) = '' then '000' else (trim(substring(elcodigo, 8, 3))) end merc3,
                            eldescri descr
                    from 
                            implantacao.impmercadologico)
    select 
            a.merc1 cod_mercadologico1, 
            (select descr from merc where merc1 = a.merc1 and merc2 = '000' and merc3 = '000') mercadologico1,
            a.merc2 cod_mercadologico2,
            coalesce((select descr from merc where merc1 = a.merc1 and merc2 = a.merc2 and merc3 = '000'),
                            (select descr from merc where merc1 = a.merc1 and merc2 = '000' and merc3 = '000')) mercadologico2,
            a.merc3 cod_mercadologico3,
            (select descr from merc where merc1 = a.merc1 and merc2 = a.merc2 and merc3 = a.merc3) mercadologico3
    from 
            merc a
    where 
            a.merc2 != '000' and 
            a.merc3 != '000' and 
            (select descr from merc where merc1 = a.merc1 and merc2 = '000' and merc3 = '000') is not null;*/
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Mapa de Tributação após gerar a tabela de produto">
    /*
        select 
                distinct
                (tqtribut || '-' || tqaliicm || '-' || tqsubsti) codtrib,
                ('ALI: ' || tqaliicm || ' CST: ' || tqtribut || ' RED: ' || tqsubsti) descricao, 
                tqtribut cst, 
                tqaliicm aliquota, 
                tqsubsti reduzido
        from 
                implantacao.impproduto;
    */
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Produto">
    /*
            create table implantacao.impproduto
            (
                    TQCLASSI varchar, 
                    TQCODIGO varchar,
                    TQGRADE varchar,
                    TQDESCRI varchar, 
                    TQCODBAR varchar,
                    TQEMBENT varchar,
                    TQENTQTD varchar,	
                    TQEMBSAI varchar,	
                    TQSAIQTD varchar,	
                    TQQTDMAX varchar,
                    TQQTDMIN varchar,	
                    TQSALDO varchar,	
                    TQULTENT varchar,
                    TQULTSAI varchar,
                    TQPREVIS varchar,
                    TQPRECUS varchar,
                    TQPREVEN varchar,
                    TQSUBSTI varchar,
                    TQTRIBUT varchar,
                    TQALIQUO varchar,
                    TQALIICM varchar,
                    TQIPV varchar,
                    TQOBSERV varchar,
                    TQOBSERV1 varchar,
                    TQALTERA varchar,
                    TQLUCRO varchar,
                    TQPREOFE varchar,
                    TQPREBAS varchar,
                    TQATIVO varchar,
                    TQETIQUE varchar,
                    TQPREALT varchar,
                    TQDATCAD varchar,
                    TQDATALT varchar,
                    TQCOMISS varchar,
                    TQVASILH varchar,
                    TQVASEAN varchar,
                    TQCODANT varchar,
                    TQCODPEN varchar,
                    TQCOMBUS varchar,
                    TQSETOR varchar,
                    TQIPI varchar,
                    TQTRANSF varchar,
                    TQVALIDA varchar,
                    TQPREGAS varchar,
                    TQVALOFE varchar,
                    TQDESCRO varchar,
                    TQFATOR varchar,
                    TQPIS varchar,
                    TQCOFINS varchar,
                    TQIVA varchar,
                    TQCFOP varchar,
                    TQNCM varchar,
                    TQCSTALT varchar,
                    TQREDUST varchar,
                    TQPREREV varchar,	
                    TQLUCROA varchar,
                    TQCSTPIS varchar,
                    TQCSTCOF varchar,
                    TQCODANP varchar,
                    TQALIIPI varchar,
                    TQINVEN varchar,
                    TQCSOSN varchar,
                    TQFOTOS varchar,
                    TQCFOPE varchar,
                    TQCEST varchar,
                    TQCSTPIE varchar,	
                    TQCSTCOE varchar,
                    TQNATISE varchar,	
                    TQNOMANT varchar,
                    TQOUTRAS varchar,
                    TQHORALT varchar,
                    TQSTATUS varchar,
                    TQID varchar,
                    TQDESANP varchar,
                    TQGRUPO varchar,
                    TQSGRUPO varchar,
                    TQPRINT varchar,
                    TQLOCAL varchar,
                    TQREDPIS varchar,
                    TQCOMBO varchar,
                    TQCOMPDV varchar,	
                    TQIDTB varchar,
                    TQCARGA varchar,	
                    TQREGESP varchar,
                    TQMODBC varchar,
                    TQMODBCST varchar,
                    TQDUPLIC varchar,
                    TQFCP varchar,
                    TQCOMATU varchar,	
                    TQPROSER varchar,
                    TQALIPDV varchar,
                    TQCUSMED varchar,
                    TQIDPROM varchar,
                    TQQTDPRO varchar,
                    TQVALPRO varchar
            );

            copy 
                    implantacao.impproduto 
            from 
                    '/home/guilhermegomes/Documents/Cliente/SP/Popular - SP/planilha/produto.csv' 
            with
                    encoding 'win1252'
                    delimiter '^' 
                    csv header;

            select 
                    p.tqid id,
                    p.tqgrade,
                    case when 
                            p.tqstatus = '1' then true
                    else false end ativo,
                    p.tqdatcad datacadastro,
                    (p.tqdescri || ' ' || p.tqgrade) descricaocompleta,
                    (p.tqdescri || ' ' || p.tqgrade) descricaoreduzida,
                    (p.tqdescri || ' ' || p.tqgrade) descricaogondola,
                    trim(pe.cbcodbar) codigobarras,
                    p.tqembent embcotacao,
                    p.tqentqtd qtdembalagemcotacao,
                    p.tqembsai unidade,
                    p.tqsaiqtd qtdembalagem,
                    case when 
                            length(replace(p.tqclassi, '.', '')) = 8 then 
                                    substring(replace(p.tqclassi, '.', ''), 0, 3) 
                    else substring(replace(p.tqclassi, '.', ''), 0, 2) end as cod_mercadologico1,
                    case when 
                            length(replace(p.tqclassi, '.', '')) = 8 then 
                                    substring(replace(p.tqclassi, '.', ''), 3, 3) 
                    else substring(replace(p.tqclassi, '.', ''), 2, 3) end as cod_mercadologico2,
                    case when 
                            length(replace(p.tqclassi, '.', '')) = 8 then 
                                    substring(replace(p.tqclassi, '.', ''), 6, 3) 
                    else substring(replace(p.tqclassi, '.', ''), 5, 3) end as cod_mercadologico3,
                    p.tqqtdmax estoquemaximo,
                    p.tqqtdmin estoqueminimo,
                    p.tqsaldo estoque,
                    p.tqprecus custocomimposto,
                    p.tqprecus custosemimposto,
                    p.tqcusmed customediocomimposto,
                    p.tqcusmed customediosemimposto,
                    p.tqlucro margem,
                    p.tqpreven precovenda,
                    (p.tqtribut || '-' || p.tqaliicm || '-' || p.tqsubsti) icms_consumidor_id,
                    (p.tqtribut || '-' || p.tqaliicm || '-' || p.tqsubsti) icms_debito_id,
                    (p.tqtribut || '-' || p.tqaliicm || '-' || p.tqsubsti) icms_debito_foraestado_id,
                    (p.tqtribut || '-' || p.tqaliicm || '-' || p.tqsubsti) icms_debito_foraestadonf_id,
                    (p.tqtribut || '-' || p.tqaliicm || '-' || p.tqsubsti) icms_credito_id,
                    (p.tqtribut || '-' || p.tqaliicm || '-' || p.tqsubsti) icms_credito_foraestado_id,
                    p.tqncm ncm,
                    p.tqcest cest,
                    p.tqcstcof piscofins_cst_debito,
                    p.tqcstcoe piscofins_cst_credito,
                    p.tqnatise piscofins_natureza_receita
            from 
                    implantacao.impproduto p
            left join 
                    implantacao.impean pe on trim(p.tqcodigo) = trim(pe.cbcodigo) and 
                                                                     trim(p.tqgrade) = trim(pe.cbgrade)
    */
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Fornecedor">
    /*
    create table implantacao.impfornecedor
    (
            FOCODIGO varchar,
            FOCGC varchar,
            FOFANTASIA varchar,
            FORAZAO varchar,
            FOINSEST varchar,
            FOENDERECO varchar,
            FOBAIRRO varchar,
            FOCIDADE varchar,
            FOESTADO varchar,
            FOCEP varchar,
            FOCONTATO1 varchar,
            FOFONE1 varchar,
            FOCONTATO2 varchar,
            FOFONE2 varchar,
            FOOBSERVAC varchar,
            FODATCAD varchar,
            FOATIVO varchar,
            FOSIMPLE varchar,
            FONUMERO varchar,
            FOCODIBGE varchar,
            FOEMAIL varchar,
            FOCODPAR varchar,
            FOIEDEST varchar,
            FOID varchar
    );

    copy 
            implantacao.impfornecedor 
    from 
            '/home/guilhermegomes/Documents/Cliente/SP/Popular - SP/planilha/fornecedor.csv' 
    with 
            encoding 'win1252'
            delimiter '^' 
            csv header;

    select 
            focodigo id,
            focgc cnpj_cpf,
            forazao razao,
            fofantasia fantasia,
            foinsest ie_rg,
            foendereco endereco,
            fobairro bairro,
            focidade municipio,
            fonumero numero,
            foestado uf,
            focep cep,
            focontato1 cont1_nome,
            foemail cont1_email,
            fofone1 cont1_telefone,
            focontato2 cont2_nome,
            fofone2 cont2_telefone,
            fodatcad datacadastro,
            coalesce(foativo, 'S') ativo
    from 
            implantacao.impfornecedor;*/
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Produto Fornecedor">
    /*
        create table implantacao.impprodutofornecedor 
        (
                SPCODBAR varchar,
                SPCODIGO varchar,
                SPGRADE varchar,
                SPCODXML varchar,
                SPCODFOR varchar
        );

        copy 
                implantacao.impprodutofornecedor 
        from 
                '/home/guilhermegomes/Documents/Cliente/SP/Popular - SP/planilha/produtofornecedor.csv' 
        with
                encoding 'win1252'
                delimiter '^' 
                csv header;

        select
                distinct
                pf.spcodfor id_fornecedor,
                pr.tqid id_produto,
                pf.spcodxml codigoexterno
        from 
                implantacao.impprodutofornecedor pf 
        join 
                implantacao.impproduto pr on 
                                pf.spcodigo = pr.tqcodigo and 
                                pf.spgrade = pr.tqgrade;

        select count(*) from produtofornecedor;

        select * from implantacao.codant_fornecedor;
        select * from implantacao.codant_produto;
    */
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Cliente">
    /*
        create table implantacao.impcliente
        (
                CLCODIGO varchar,
                CLNOME varchar,
                CLRAZAO varchar,
                CLCGC varchar,
                CLINSEST varchar,
                CLCPF varchar,
                CLRG varchar,
                CLENDERECO varchar,
                CLBAIRRO varchar,
                CLCIDADE varchar,
                CLESTADO varchar,
                CLCEP varchar,
                CLTEL1 varchar,
                CLCONTAT1 varchar,
                CLCONTAT2 varchar,
                CLENDEREC varchar,
                CLENDERC varchar,
                CLBAIRROC varchar,
                CLCIDADEC varchar,
                CLESTADOC varchar,
                CLCEPC varchar,
                CLTELC varchar,
                CLDATA varchar,
                CLAREATU varchar,
                CLRAMO varchar,
                CLREPRES varchar,
                CLSALDO varchar,
                CLDATULT varchar,
                CLVALULT varchar,
                CLCREDITO varchar,
                CLSTATUS varchar,
                CLDATNAS varchar,
                CLNOVO varchar,
                CLOBS1 varchar,
                CLOBS2 varchar,
                CLOBS3 varchar,
                CLSIMPLE varchar,
                CLNUMERC varchar,
                CLNUMERO varchar,
                CLVALACU varchar,
                CLCHEDEV varchar,
                CLNUMCUP varchar,
                CLTITPAG varchar,
                CLTITCAR varchar,
                CLDTNEGA varchar,
                CLATACAD varchar,
                CLFISICA varchar,
                CLCODPAR varchar,
                CLFOTOS varchar,
                CLXML varchar,
                CLIEDEST varchar,
                CLDATUP varchar,
                CLDATHOR varchar,
                CLID varchar,
                CLNIVEL varchar,
                CLAPURA varchar,
                CLATIVO varchar,
                CLCATEGO varchar,
                CLNUMINF varchar,
                CLCLIAVA varchar,
                CLEMAILAV varchar,
                CLNOMAVA varchar,
                CLESTCIV varchar
        );

        copy 
                implantacao.impcliente 
        from 
                '/home/guilhermegomes/Documents/Cliente/SP/Popular - SP/planilha/cliente.csv'
        with 
                encoding 'win1252' 
                delimiter '^' 
                csv header;

        select 
                trim(clcodigo) id,
                clid,
                clnome razao,
                clcgc cnpj,
                clcpf cpf,
                clrg rg,
                clinsest inscricaoestadual,
                clendereco endereco,
                clnumero numero,
                clbairro bairro,
                clcidade municipio,
                clestado uf,
                clcep cep,
                cldatup datacadastro,
                cltel1 telefone,
                clcontat1 cont1_nome,
                clcontat2 cont2_nome,
                clsaldo,
                clstatus,
                cldatnas datanascimento,
                clativo,
                clobs1,
                clobs2,
                clobs3
        from 
                implantacao.impcliente;
    */
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Rotativo">
    /*
        create table implantacao.improtativo 
        (
                CONUMDOC varchar,
                CODTVCTO varchar,
                COSTATUS varchar,
                CODTDCTO varchar,
                COCLIENT varchar,
                CONOME varchar,
                COVALOR varchar,
                COCODHIS varchar,
                COCOMPLE varchar,
                COFORPAG varchar,
                COCONDIC varchar,
                CONORESP varchar,
                COVALPAG varchar,
                CODTPGTO varchar,
                CONOSSON varchar,
                COEMPRES varchar,
                COEMPBAI varchar,
                COOFLINE varchar,
                CONEGATI varchar,
                CODATBAI varchar,
                COHORBAI varchar,
                COOPEBAI varchar,
                COFPPAGO varchar,
                CODESCON varchar,
                COENTRAD varchar,
                COXML varchar,
                COSTAREN varchar,
                COCONTRA varchar,
                COBOLEMI varchar,
                CODATBOL varchar,
                COREMESS varchar,
                COBANCO varchar,
                COCREDIT varchar,
                CORETORN varchar,
                COCREDID varchar,
                CODTNEGA varchar
        );

        copy 
                implantacao.improtativo 
        from 
                '/home/guilhermegomes/Documents/Cliente/SP/Popular - SP/planilha/rotativo.csv'
        with 
                encoding 'win1252' 
                delimiter '^' 
                csv header;

        select 
                conumdoc id,
                codtvcto vencimento,
                codtdcto emissao,
                coclient idcliente,
                covalor valor,
                cocomple observacao
        from 
                implantacao.improtativo 
        where 
                costatus = 'ABE';
    */
    //</editor-fold>
}
