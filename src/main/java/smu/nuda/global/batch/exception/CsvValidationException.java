package smu.nuda.global.batch.exception;

import smu.nuda.global.error.DomainException;
import smu.nuda.global.error.ErrorCode;

public class CsvValidationException extends DomainException {

    private final int rowNumber;

    public CsvValidationException(ErrorCode errorCode, int rowNumber, String detailMessage) {
        super(errorCode, buildMessage(rowNumber, detailMessage != null ? detailMessage : errorCode.getMessage()));
        this.rowNumber = rowNumber;
    }

    public CsvValidationException(ErrorCode errorCode, int rowNumber) {
        this(errorCode, rowNumber, errorCode.getMessage());
    }

    public int getRowNumber() {
        return rowNumber;
    }

    private static String buildMessage(int rowNumber, String message) {
        return "CSV " + rowNumber + "번째 줄: " + message;
    }
}
