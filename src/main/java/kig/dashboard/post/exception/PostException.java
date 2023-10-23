package kig.dashboard.post.exception;

import kig.dashboard.global.exception.BaseException;
import kig.dashboard.global.exception.BaseExceptionType;
import lombok.Setter;

@Setter
public class PostException extends BaseException {

    private BaseExceptionType baseExceptionType;

    public PostException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.baseExceptionType;
    }
}
