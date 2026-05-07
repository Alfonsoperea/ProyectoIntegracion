package com.aiss.DailyMotionMiner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta anotación le dice a Spring que si se lanza esta excepción, devuelva un 404 (NOT_FOUND)
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Video no encontrada")
public class VideoNotFoundException extends Exception {
}
