/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;

/**
 *
 * @author lucasrafael
 */
public class PlanilhaClientesDAO {
    
    public void importarClientes(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ClientePreferencialVO> vClientePreferencial = carregarClientes(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vClientePreferencial.size());

            ClientePreferencialDAO clientePreferencialDAO =  new ClientePreferencialDAO();
            clientePreferencialDAO.salvar(vClientePreferencial, 1, 1);
            
        } catch (Exception e) {
            throw e;
        }
    }

    public void importarClientesSysPdv(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ClientePreferencialVO> vClientePreferencial = carregarClientesSysPdv(i_arquivo);

            ProgressBar.setMaximum(vClientePreferencial.size());

            new PlanoDAO().salvar(i_idLojaDestino);
            
            ClientePreferencialDAO clientePreferencialDAO =  new ClientePreferencialDAO();
            clientePreferencialDAO.salvar(vClientePreferencial, i_idLojaDestino, i_idLojaDestino);
            
        } catch (Exception e) {
            throw e;
        }
    }    
    private List<ClientePreferencialVO> carregarClientes(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        Utils util = new Utils();
        String nome, endereco, bairro, telefone, numero, complemento;
        int idTipoInscricao, idMunicipio = 0, idEstado;
        long cnpj, cep;
        
        try {
            int linha = 0;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            
            Sheet[] sheets = arquivo.getSheets();
            
            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        //} else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                        //    continue;
                        }
                        
                        Cell cellCodigo = sheet.getCell(0,i);
                        Cell cellNome = sheet.getCell(1,i);                        
                        Cell cellCnpj = sheet.getCell(3, i);
                        Cell cellEndereco = sheet.getCell(4, i);
                        Cell cellNumero = sheet.getCell(5, i);
                        Cell cellBairro = sheet.getCell(6, i);
                        Cell cellTelefone = sheet.getCell(11, i);
                        Cell cellCidade = sheet.getCell(7, i);
                        Cell cellUf = sheet.getCell(8, i);
                        Cell cellComplemento = sheet.getCell(9, i);
                        Cell cellCep = sheet.getCell(12, i);
                        
                        if ((cellCodigo.getContents() != null) &&
                                (!cellCodigo.getContents().trim().isEmpty())) {
                        
                            if ((cellNome.getContents() != null)
                                    && (!cellNome.getContents().trim().isEmpty())) {
                                nome = util.acertarTexto(cellNome.getContents().trim().replace("'", ""));
                            } else {
                                nome = "";
                            }

                            if ((cellCnpj.getContents() != null)
                                    && (!cellCnpj.getContents().trim().isEmpty())) {
                                cnpj = Long.parseLong(util.formataNumero(cellCnpj.getContents().trim()));
                                
                                if (String.valueOf(cnpj).length() > 11) {
                                    idTipoInscricao = 0;
                                } else {
                                    idTipoInscricao = 1;
                                }
                            } else {
                                cnpj = Long.parseLong(cellCodigo.getContents().trim());
                                idTipoInscricao = 1;
                            }

                            if ((cellEndereco.getContents() != null)
                                    && (!cellEndereco.getContents().trim().isEmpty())) {
                                endereco = util.acertarTexto(cellEndereco.getContents().trim().replace("'", ""));
                            } else {
                                endereco = "0";
                            }

                            if ((cellBairro.getContents() != null)
                                    && (!cellBairro.getContents().trim().isEmpty())) {
                                bairro = util.acertarTexto(cellBairro.getContents().trim().replace("'", ""));
                            } else {
                                bairro = "0";
                            }

                            if ((cellTelefone.getContents() != null)
                                    && (!cellTelefone.getContents().trim().isEmpty())) {
                                telefone = util.formataNumero(cellTelefone.getContents().trim().replace("'", ""));
                            } else {
                                telefone = "0";
                            }

                            if ((cellNumero.getContents() != null) &&
                                    (!cellNumero.getContents().trim().isEmpty())) {
                                numero = util.acertarTexto(cellNumero.getContents().trim().replace("'", ""));
                            } else {
                                numero = "0";
                            }
                            
                            if ((cellComplemento.getContents() != null) &&
                                    (!cellComplemento.getContents().trim().isEmpty())) {
                                complemento = util.acertarTexto(cellComplemento.getContents().trim().replace("'", ""));
                            } else {
                                complemento = "";
                            }
                            
                            if ((cellCep.getContents() != null) &&
                                    (cellCep.getContents().trim().isEmpty())) {
                                cep = Long.parseLong(util.formataNumero(cellCep.getContents().trim()));
                            } else {
                                cep = 60000000;
                            }
                        
                            if ((cellCidade.getContents() != null)
                                    && (!cellCidade.getContents().trim().isEmpty())) {

                                if ((cellUf.getContents() != null)
                                        && (!cellUf.getContents().trim().isEmpty())) {

                                    idMunicipio = util.retornarMunicipioIBGEDescricao(
                                            util.acertarTexto(cellCidade.getContents().trim().replace("'", "")),
                                            util.acertarTexto(cellUf.getContents().trim().replace("'", "")));

                                    if (idMunicipio == 0) {
                                        idMunicipio = 2304400;
                                    }
                                }
                            } else {
                                idMunicipio = 2304400;
                            }
                            
                            if ((cellUf.getContents() != null)
                                    && (!cellUf.getContents().trim().isEmpty())) {

                                idEstado = util.retornarEstadoDescricao(
                                        util.acertarTexto(cellUf.getContents().replace("'", "").trim()));

                                if (idEstado == 0) {
                                    idEstado = 23;
                                }
                            } else {
                                idEstado = 23;
                            }
                            
                            if (nome.length() > 40) {
                                nome = nome.substring(0, 40);
                            }

                            if (endereco.length() > 40) {
                                endereco = endereco.substring(0, 40);
                            }

                            if (bairro.length() > 30) {
                                bairro = bairro.substring(0, 30);
                            }

                            if (telefone.length() > 14) {
                                telefone = telefone.substring(0, 14);
                            }

                            if (String.valueOf(cnpj).length() > 14) {
                                cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                            }
                            
                            ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                            oClientePreferencial.id = Integer.parseInt(cellCodigo.getContents().trim());
                            oClientePreferencial.nome = nome;
                            oClientePreferencial.cnpj = cnpj;
                            oClientePreferencial.endereco = endereco;
                            oClientePreferencial.bairro = bairro;
                            oClientePreferencial.numero = numero;
                            oClientePreferencial.telefone = telefone;
                            oClientePreferencial.id_tipoinscricao = idTipoInscricao;
                            oClientePreferencial.id_situacaocadastro = 1;
                            oClientePreferencial.datacadastro = "2016/03/09";
                            oClientePreferencial.datanascimento = null;
                            oClientePreferencial.id_estado = idEstado;
                            oClientePreferencial.id_municipio = idMunicipio;
                            oClientePreferencial.complemento = complemento;
                            oClientePreferencial.bloqueado = true;
                            oClientePreferencial.permitecheque = false;
                            oClientePreferencial.permitecreditorotativo = false;
                            
                            vClientePreferencial.add(oClientePreferencial);
                            
                        }
                    }
                }

                return vClientePreferencial;

            } catch (Exception ex) {
                throw ex;
                /*if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }*/
            }

        } catch (Exception e) {
            throw e;
        }
    }
    
    private List<ClientePreferencialVO> carregarClientesSysPdv(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        Utils util = new Utils();
        String nome = null, endereco = null, bairro = null, telefone = null, numero = null, complemento = null,
               nomePai, nomeMae, conjuge, orgaoExp,
               dataNascimento, dataCadastro, observacao, telefone2, email, fax,
               inscricaoEstadual, dataResidencia;
        int idTipoInscricao, idMunicipio = 0, idEstado, idSexo, idEstadoCivil,
            idSituacaoCadastro;
        long cnpj = 0, cep = 0, idCliente = 0;
        double limite, salario;
        boolean bloqueado;
        
        try {
            int linha = 0;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            
            Sheet[] sheets = arquivo.getSheets();
            
            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        //} else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                        //    continue;
                        }
                        
                        Cell cellCodigo = sheet.getCell(0,i);
                        Cell cellNome = sheet.getCell(1,i);                        
                        Cell cellEndereco = sheet.getCell(2, i);
                        Cell cellCnpj = sheet.getCell(3, i);
                        Cell cellBairro = sheet.getCell(4, i);
                        Cell cellTelefone = sheet.getCell(5, i);
                        Cell cellCep = sheet.getCell(6, i);                        
                        Cell cellCidade = sheet.getCell(7, i);
                        Cell cellNumero = sheet.getCell(8, i);
                        Cell cellComplemento = sheet.getCell(9, i);
                        Cell cellUf = sheet.getCell(10, i);
                        Cell cellLimiteCredito = sheet.getCell(11, i);
                        Cell cellInscricaoEstadual = sheet.getCell(13, i);
                        Cell cellDataCadastro = sheet.getCell(14, i);
                        Cell cellDataNascimento = sheet.getCell(15, i);
                        Cell cellBloqueado = sheet.getCell(16, i);
                        Cell cellNomePai = sheet.getCell(17, i);
                        Cell cellNomeMae = sheet.getCell(18, i);
                        Cell cellIdTipoInscricao = sheet.getCell(19, i);
                        Cell cellTelefone2 = sheet.getCell(20, i);
                        Cell cellFax = sheet.getCell(21, i);
                        Cell cellObservacao = sheet.getCell(29, i);
                        Cell cellEmail = sheet.getCell(30, i);
                        Cell cellSexo = sheet.getCell(31, i);
                        Cell cellSalario = sheet.getCell(36, i);
                        Cell cellEstadoCivil = sheet.getCell(37, i);
                        Cell cellConjuge = sheet.getCell(38, i);
                        Cell cellOrgaoExp = sheet.getCell(39, i);
                        
                        if ((cellNome.getContents() != null) &&
                                (!cellNome.getContents().trim().isEmpty()) &&
                                (!"NULL".equals(cellNome.getContents().trim()))) {
                            
                            idSituacaoCadastro = 1;
                            dataResidencia = "1990/01/01";
                        
                            if ((cellCodigo.getContents() != null) &&
                                   (!cellCodigo.getContents().trim().isEmpty())) {
                                
                                idCliente = Long.parseLong(cellCodigo.getContents().trim().substring(0, 
                                        cellCodigo.getContents().trim().length() -3));
                                
                                if ((cellNome.getContents() != null)
                                        && (!cellNome.getContents().trim().isEmpty())) {
                                    nome = util.acertarTexto(cellNome.getContents().trim().replace("'", ""));
                                } else {
                                    nome = "";
                                }
                                
                                if ((cellEndereco.getContents() != null) &&
                                        (!cellEndereco.getContents().trim().isEmpty()) &&
                                        (!"NULL".equals(cellEndereco.getContents().trim()))) {
                                    endereco = util.acertarTexto(cellEndereco.getContents().trim().replace("'", ""));
                                } else {
                                    endereco = "NULL";
                                }
                                
                                if ((cellCnpj.getContents() != null) &&
                                        (!cellCnpj.getContents().trim().isEmpty()) &&
                                        (!"NULL".equals(cellCnpj.getContents().trim()))) {
                                    
                                    if (cellCnpj.getContents().contains(",")) {
                                        cnpj = Long.parseLong(util.formataNumero(cellCnpj.getContents().trim().substring(0, 
                                                cellCnpj.getContents().trim().length() -3)));
                                    } else {
                                        cnpj = -1;
                                    }
                                } else {
                                    cnpj = -1;
                                }
                                
                                if ((cellBairro.getContents() != null) &&
                                        (!cellBairro.getContents().trim().isEmpty()) &&
                                        (!"NULL".equals(cellBairro.getContents().trim()))) {
                                    bairro = util.acertarTexto(cellBairro.getContents().trim().replace("'", ""));
                                } else {
                                    bairro = "";
                                }
                                
                                if ((cellTelefone.getContents() != null) &&
                                        (!cellTelefone.getContents().trim().isEmpty()) &&
                                        (!"NULL".equals(cellTelefone.getContents().trim()))) {
                                    telefone = util.formataNumero(cellTelefone.getContents().trim());
                                } else {
                                    telefone = "0000000000";
                                }
                                
                                if ((cellCep.getContents() != null) &&
                                        (!cellCep.getContents().trim().isEmpty()) &&
                                        (!"NULL".equals(cellCep.getContents().trim()))) {
                                    cep = Long.parseLong(util.formataNumero(cellCep.getContents().trim()));
                                } else {
                                    cep = 61700000;
                                }
                                
                                if ((cellCidade.getContents() != null) &&
                                        (!cellCidade.getContents().trim().isEmpty()) &&
                                        (!"NULL".equals(cellCidade.getContents().trim()))) {
                                    
                                    if ((cellUf.getContents() != null) &&
                                            (!cellUf.getContents().trim().isEmpty()) &&
                                            (!"NULL".equals(cellUf.getContents().trim()))) {
                                        
                                        idMunicipio = util.retornarMunicipioIBGEDescricao(
                                                util.acertarTexto(cellCidade.getContents().trim().replace("'", "")),
                                                util.acertarTexto(cellUf.getContents().trim().replace("'", "")));

                                        if (idMunicipio == 0) {
                                            idMunicipio = 2301000;
                                        }
                                        
                                    }
                                } else {
                                    idMunicipio = 2301000;
                                }
                                
                                if ((cellNumero.getContents() != null) &&
                                        (!cellNumero.getContents().trim().isEmpty()) &&
                                        (!"NULL".equals(cellNumero.getContents().trim())) &&
                                        (!"S/N".equals(cellNumero.getContents().trim()))) {
                                    numero = util.acertarTexto(cellNumero.getContents().trim().replace("'", ""));
                                } else {
                                    numero = "0";
                                }
                                
                                if ((cellComplemento.getContents() != null) &&
                                        (!cellComplemento.getContents().trim().isEmpty()) &&
                                        (!"NULL".equals(cellComplemento.getContents().trim()))) {
                                    complemento = util.acertarTexto(cellComplemento.getContents().trim().replace("'", ""));
                                } else {
                                    complemento = "";
                                }
                            }
                            
                            if ((cellUf.getContents() != null)
                                    && (!cellUf.getContents().trim().isEmpty())) {

                                idEstado = util.retornarEstadoDescricao(
                                        util.acertarTexto(cellUf.getContents().replace("'", "").trim()));

                                if (idEstado == 0) {
                                    idEstado = 23;
                                }
                            } else {
                                idEstado = 23;
                            }
                            
                            if ((cellLimiteCredito.getContents() != null) &&
                                    (!cellLimiteCredito.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellLimiteCredito.getContents().trim()))) {
                                limite = Double.parseDouble(cellLimiteCredito.getContents().trim().replace(",", "."));
                            } else {
                                limite = 0;
                            }
                            
                            if ((cellInscricaoEstadual.getContents() != null) &&
                                    (!cellInscricaoEstadual.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellInscricaoEstadual.getContents().trim()))) {
                                
                                if (cellInscricaoEstadual.getContents().trim().length() > 3) {
                                    inscricaoEstadual = util.acertarTexto(cellInscricaoEstadual.getContents().trim().replace("'", "").substring(0,
                                            cellInscricaoEstadual.getContents().trim().length() - 3));
                                } else {
                                    inscricaoEstadual = "ISENTO";
                                }
                            } else {
                                inscricaoEstadual = "ISENTO";
                            }
                            
                            if ((cellDataCadastro.getContents() != null) &&
                                    (!cellDataCadastro.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellDataCadastro.getContents().trim())) &&
                                    (cellDataCadastro.getContents().trim().length() >= 10)) {
                                
                                dataCadastro = cellDataCadastro.getContents().trim().substring(0, 10);
                                dataCadastro = dataCadastro.replace("-", "/");
                            } else {
                                dataCadastro = "";
                            }
                            
                            if ((cellDataNascimento.getContents() != null) &&
                                    (!cellDataNascimento.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellDataNascimento.getContents().trim())) &&
                                    (cellDataNascimento.getContents().trim().length() >= 10)) {
                                
                                dataNascimento = cellDataNascimento.getContents().trim().substring(0, 10);
                                dataNascimento = dataNascimento.replace("-", "/");
                            } else {
                                dataNascimento = null;
                            }
                            
                            if (!"NULL".equals(cellBloqueado.getContents().trim())) {
                                bloqueado = true;
                            } else {
                                bloqueado = false;
                            }
                            
                            if ((cellNomePai.getContents() != null) &&
                                    (!cellNomePai.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellNomePai.getContents().trim()))) {
                                nomePai = util.acertarTexto(cellNome.getContents().trim().replace("'", ""));
                            } else {
                                nomePai = "";
                            }
                            
                            if ((cellNomeMae.getContents() != null) &&
                                    (!cellNomeMae.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellNomeMae.getContents().trim()))) {
                                nomeMae = util.acertarTexto(cellNomeMae.getContents().trim().replace("'", ""));
                            } else {
                                nomeMae = "";
                            }
                            
                            if ((cellIdTipoInscricao.getContents() != null) &&
                                    (!cellIdTipoInscricao.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellIdTipoInscricao.getContents().trim()))) {
                                if ("F".equals(cellIdTipoInscricao.getContents().trim())) {
                                    idTipoInscricao = 1;
                                } else {
                                    idTipoInscricao = 0;
                                }
                            } else {
                                idTipoInscricao = 1;
                            }
                            
                            if ((cellTelefone2.getContents() != null) &&
                                    (!cellTelefone2.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellTelefone2.getContents().trim()))) {
                                telefone2 = util.formataNumero(cellTelefone2.getContents().trim());
                            } else {
                                telefone2 = "";
                            }
                            
                            if ((cellFax.getContents() != null) &&
                                    (!cellFax.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellFax.getContents().trim()))) {
                                fax = util.formataNumero(cellFax.getContents().trim());
                            } else {
                                fax = "";
                            }
                            
                            if ((cellObservacao.getContents() != null) &&
                                    (!cellObservacao.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellObservacao.getContents().trim()))) {
                                observacao = util.acertarTexto(cellObservacao.getContents().replace("'", "").trim());
                            } else {
                                observacao = "";
                            }
                            
                            if ((cellEmail.getContents() != null) &&
                                    (!cellEmail.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellEmail.getContents().trim())) &&
                                    (cellEmail.getContents().contains("@"))) {
                                email = util.acertarTexto(cellEmail.getContents().trim().replace("'", ""));
                                email = email.toLowerCase();
                            } else {
                                email = "";
                            }
                        
                            if ((cellSexo.getContents() != null) &&
                                    (!cellSexo.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellSexo.getContents().trim()))) {
                                if ("F".equals(cellSexo.getContents().trim())) {
                                    idSexo = 0;
                                } else {
                                    idSexo = 1;
                                }
                            } else {
                                idSexo = 1;
                            }
                            
                            if ((cellSalario.getContents() != null) &&
                                    (!cellSalario.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellSalario.getContents().trim()))) {
                                salario = Double.parseDouble(cellSalario.getContents().trim());
                            } else {
                                salario = 0;
                            }
                            
                            if ((cellEstadoCivil.getContents() != null) &&
                                    (!cellEstadoCivil.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellEstadoCivil.getContents().trim()))) {
                                
                                if ("C".equals(cellEstadoCivil.getContents().trim())) {
                                    idEstadoCivil = 2;
                                } else if ("S".equals(cellEstadoCivil.getContents().trim())) {
                                    idEstadoCivil = 1;
                                } else {
                                    idEstadoCivil = 0;
                                }
                            } else {
                                idEstadoCivil = 0;
                            }
                        
                            if ((cellConjuge.getContents() != null) &&
                                    (!cellConjuge.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellConjuge.getContents().trim()))) {
                                conjuge = util.acertarTexto(cellConjuge.getContents().trim().replace("'", ""));
                            } else {
                                conjuge = "";
                            }
                            
                            if ((cellOrgaoExp.getContents() != null) &&
                                    (!cellOrgaoExp.getContents().trim().isEmpty()) &&
                                    (!"NULL".equals(cellOrgaoExp.getContents().trim()))) {
                                orgaoExp = util.acertarTexto(cellOrgaoExp.getContents().trim().replace("'", ""));
                            } else {
                                orgaoExp = "";
                            }
                            
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

                            if (telefone.length() > 14) {
                                telefone = telefone.substring(0, 14);
                            }

                            if (String.valueOf(cnpj).length() > 14) {
                                cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                            }

                            if (inscricaoEstadual.length() > 18) {
                                inscricaoEstadual = inscricaoEstadual.substring(0, 18);
                            }

                            if (complemento.length() > 30) {
                                complemento = complemento.substring(0, 30);
                            }

                            if (email.length() > 50) {
                                email = email.substring(0, 50);
                            }

                            if (orgaoExp.length() > 6) {
                                orgaoExp = orgaoExp.substring(0, 6);
                            }
                            
                            if (numero.length() > 6) {
                                numero = numero.substring(0, 6);
                            }
                            
                            if (observacao.length() > 80) {
                                observacao = observacao.substring(0, 80);
                            }
                            
                            ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                            
                            oClientePreferencial.codigoanterior = idCliente;
                            oClientePreferencial.idLong = idCliente;
                            oClientePreferencial.nome = nome;
                            oClientePreferencial.endereco = endereco;
                            oClientePreferencial.bairro = bairro;
                            oClientePreferencial.id_estado = idEstado;
                            oClientePreferencial.id_municipio = idMunicipio;
                            oClientePreferencial.cep = cep;
                            oClientePreferencial.telefone = telefone;
                            oClientePreferencial.inscricaoestadual = inscricaoEstadual;
                            oClientePreferencial.cnpj = cnpj;
                            oClientePreferencial.sexo = idSexo;
                            oClientePreferencial.dataresidencia = dataResidencia;
                            oClientePreferencial.datacadastro = dataCadastro;
                            oClientePreferencial.email = email;
                            oClientePreferencial.valorlimite = limite;
                            oClientePreferencial.fax = fax;
                            oClientePreferencial.bloqueado = bloqueado;
                            oClientePreferencial.id_situacaocadastro = idSituacaoCadastro;
                            oClientePreferencial.telefone2 = telefone2;
                            oClientePreferencial.observacao = observacao;
                            oClientePreferencial.datanascimento = dataNascimento;
                            oClientePreferencial.nomepai = nomePai;
                            oClientePreferencial.nomemae = nomeMae;
                            oClientePreferencial.empresa = "";
                            oClientePreferencial.telefoneempresa = "";
                            oClientePreferencial.numero = numero;
                            oClientePreferencial.cargo = "";
                            oClientePreferencial.enderecoempresa = "";
                            oClientePreferencial.id_tipoinscricao = idTipoInscricao;
                            oClientePreferencial.salario = salario;
                            oClientePreferencial.id_tipoestadocivil = idEstadoCivil;
                            oClientePreferencial.nomeconjuge = conjuge;
                            oClientePreferencial.orgaoemissor = orgaoExp;
                            vClientePreferencial.add(oClientePreferencial);
                            
                        }
                    }
                }

                return vClientePreferencial;

            } catch (Exception ex) {
                throw ex;
                /*if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }*/
            }

        } catch (Exception e) {
            throw e;
        }
    }

    public void importarClientesRabelo(String i_arquivo, int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados para comparação...");

        List<ClientePreferencialVO> vClientePreferencial = carregarClientesRabelo(i_arquivo);

        ProgressBar.setMaximum(vClientePreferencial.size());

        new PlanoDAO().salvar(idLojaVR);

        ClientePreferencialDAO clientePreferencialDAO =  new ClientePreferencialDAO();
        clientePreferencialDAO.salvar(vClientePreferencial, idLojaVR, idLojaVR);
    }
    
    private List<ClientePreferencialVO> carregarClientesRabelo(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        
        int linha = 0;

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();           

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;

                //ignora o cabeçalho
                if (linha == 1) {
                    continue;

                //} else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                //    continue;
                }

                Cell cellCodigo = sheet.getCell(0,i);
                Cell cellNome = sheet.getCell(3,i);                        
                Cell cellCnpj = sheet.getCell(2,i);
                Cell cellEndereco = sheet.getCell(7, i);
                Cell cellNumero = sheet.getCell(8, i);
                Cell cellComplemento = sheet.getCell(9, i);
                Cell cellBairro = sheet.getCell(10, i);
                Cell cellTelefone = sheet.getCell(14, i);
                Cell cellCep = sheet.getCell(11, i);                        
                Cell cellCidade = sheet.getCell(12, i);
                Cell cellUf = sheet.getCell(13, i);
                Cell cellInscricaoEstadual = sheet.getCell(5, i);
                Cell cellIdTipoInscricao = sheet.getCell(1, i);
                Cell cellEmail = sheet.getCell(15, i);
                Cell cellLimite = sheet.getCell(16, i);

                if (cellCodigo.getContents() != null && !"".equals(cellCodigo.getContents().trim())) {

                    ClientePreferencialVO cli = new ClientePreferencialVO();

                    cli.setId(Utils.stringToInt(cellCodigo.getContents()));
                    cli.setIdLong(cli.getId());
                    cli.setCodigoanterior(cli.getId());
                    cli.setCnpj(cellCnpj.getContents());
                    cli.setNome(cellNome.getContents());
                    cli.setEndereco(cellEndereco.getContents());
                    cli.setNumero(cellNumero.getContents());
                    cli.setComplemento(cellComplemento.getContents());
                    cli.setBairro(cellBairro.getContents());
                    cli.setId_estado(Utils.getEstadoPelaSigla(cellUf.getContents()));
                    cli.setId_municipio(Utils.retornarMunicipioIBGEDescricao(cellCidade.getContents(), cellUf.getContents()));
                    cli.setCep(cellCep.getContents());
                    cli.setTelefone(cellTelefone.getContents());
                    cli.setInscricaoestadual(cellInscricaoEstadual.getContents());
                    cli.setValorlimite(Utils.stringToDouble(cellLimite.getContents()));
                    cli.setDatacadastro(Utils.getDataAtual());
                    cli.setEmail(cellEmail.getContents());
                    cli.setId_situacaocadastro(1);
                    cli.setObservacao("IMPORTADO VR");
                    switch (Utils.acertarTexto(cellIdTipoInscricao.getContents())) {
                        case "FISICA": cli.setId_tipoinscricao(1);
                            break;
                        default: cli.setId_tipoinscricao(0);
                            break;
                    }
                    vClientePreferencial.add(cli);

                }
            }
        }

        return vClientePreferencial;
    }
    
}
