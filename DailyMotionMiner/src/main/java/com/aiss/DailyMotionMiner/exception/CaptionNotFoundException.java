package com.aiss.DailyMotionMiner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Caption no encontrada")
public class CaptionNotFoundException extends Exception {
}
