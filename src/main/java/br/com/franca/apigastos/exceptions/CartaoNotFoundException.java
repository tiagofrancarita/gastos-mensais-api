package br.com.franca.apigastos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CartaoNotFoundException extends RuntimeException {

    public CartaoNotFoundException(String mensagem) {
        super(mensagem);
    }
}