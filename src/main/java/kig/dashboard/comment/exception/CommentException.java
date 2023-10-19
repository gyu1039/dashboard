package kig.dashboard.comment.exception;

import kig.dashboard.global.exception.BaseException;
import kig.dashboard.global.exception.BaseExceptionType;

public class CommentException extends BaseException {

    private BaseExceptionType baseExceptionType;

    public CommentException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.baseExceptionType;
    }
}
