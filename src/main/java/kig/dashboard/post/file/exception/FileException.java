package kig.dashboard.post.file.exception;

import kig.dashboard.global.exception.BaseException;
import kig.dashboard.global.exception.BaseExceptionType;

public class FileException extends BaseException {

    private BaseExceptionType exceptionType;

    public FileException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return null;
    }
}
