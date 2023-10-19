package kig.dashboard.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.ContinueResponseTiming;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.Repeatable;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity handleBaseException(BaseException baseException) {
        log.error("BaseException errorMessage(): {}", baseException.getExceptionType().getErrorMessage());
        log.error("BaseException errorCode(): {}", baseException.getExceptionType().getErrorCode());

        return new ResponseEntity(
                new ExceptionDto(baseException.getExceptionType().getErrorCode()),
                    baseException.getExceptionType().getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity handleValidationException(BindException exception) {

        log.error("@ValidException 발생! {}", exception.getMessage());
        return new ResponseEntity(new ExceptionDto(2000), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity HttpMessageNotReadableException(HttpMessageNotReadableException exception) {

        log.error("Json을 파싱하는 과정에서 예외 발생! {}", exception.getMessage());
        return new ResponseEntity(new ExceptionDto(3000), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberException(Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @Data
    @AllArgsConstructor
    static class ExceptionDto {
        private Integer errorCode;
    }
}
