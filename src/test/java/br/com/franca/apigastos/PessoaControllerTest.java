package br.com.franca.apigastos;


import br.com.franca.apigastos.controller.PessoaController;
import br.com.franca.apigastos.enums.StatusPessoaEnum;
import br.com.franca.apigastos.model.Pessoa;
import br.com.franca.apigastos.model.Usuario;
import br.com.franca.apigastos.service.PessoaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class PessoaControllerTest {

    @Mock
    private PessoaService pessoaService;

    @InjectMocks
    private PessoaController pessoaController;

    private Pessoa pessoa;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("John Doe");
        pessoa.setCpf("12345678900");
        pessoa.setEmail("tiagofranca.rita@gmail.com");
        pessoa.setStatus(StatusPessoaEnum.ATIVO);
        pessoa.setDataCadastro(LocalDate.from(LocalDateTime.of(2023, 12, 14, 0, 0, 0)));
        pessoa.setDataNascimento(LocalDate.from(LocalDateTime.of(1995, 12, 14, 0, 0, 0)));

        usuario = new Usuario();
        usuario.setPessoa(pessoa);
    }

    @Test
    void cadastrarPessoaDeveRetornarNovaPessoaQuandoCadastradaComSucesso() {
        // Arrange
        Mockito.when(pessoaService.salvarPessoa(ArgumentMatchers.any(Pessoa.class))).thenReturn(pessoa);


        // Act
        ResponseEntity<Pessoa> responseEntity = pessoaController.cadastrarPessoa(pessoa);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(pessoa, responseEntity.getBody());
        Assertions.assertEquals(true, pessoa.getId() > 0 );
    }

    @Test
    void listarPessoasDeveRetornarListaVaziaQuandoNaoHouverPessoas() {
        // Arrange
        Mockito.when(pessoaService.listarPessoas()).thenReturn(Collections.emptyList());

        // Act
        List<Pessoa> result = pessoaController.listarPessoasCadastradas();

        // Assert
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void listarPessoasDeveRetornarListaDePessoasQuandoExistirem() {
        // Arrange
        Mockito.when(pessoaService.listarPessoas()).thenReturn(Collections.singletonList(pessoa));

        // Act
        List<Pessoa> result = pessoaController.listarPessoasCadastradas();

        // Assert
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(pessoa, result.get(0));
    }

    @Test
    void buscarPessoaPorIdDeveRetornarPessoaQuandoExistir() {
        // Arrange
        Mockito.when(pessoaService.buscarPessoaPorId(1L)).thenReturn(pessoa);

        // Act
        Pessoa result = pessoaController.buscarPessoaPorId(1L).getBody();

        // Assert
        Assertions.assertEquals(pessoa, result);
    }
}
