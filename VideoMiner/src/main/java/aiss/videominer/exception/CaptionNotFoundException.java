package aiss.videominer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta anotación le dice a Spring que si se lanza esta excepción, devuelva un 404 (NOT_FOUND)
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Caption no encontrada")
public class CaptionNotFoundException extends Exception {
}
