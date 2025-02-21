package postservice.exception;

import postservice.exception.common.BusinessException;
import postservice.exception.common.ErrorCode;

public class NotMatchWriterException extends BusinessException {
    public NotMatchWriterException() {
        super(ErrorCode.MATCH_USER_EXCEPTION);
    }
}
