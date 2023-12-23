package br.com.franca.apigastos;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API Controle Contas a Pagar", version = "1.0", description = "API para gerenciamento de contas a pagar", contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Tiago França", email = "tiagofranca.rita@gmail.com")))
public class ApiGastosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGastosApplication.class, args);
    }

}
