package br.com.franca.apigastos.exceptions;

public class BookNotFoundException extends Throwable {

    public BookNotFoundException(String message) {
        super(message);
    }
}
