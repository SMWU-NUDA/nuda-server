package smu.nuda.global.batch.exception;

public class CsvValidationException extends RuntimeException {

    private final int rowNumber;

    public CsvValidationException(int rowNumber, String message) {
        super("CSV " + rowNumber + "번째 줄: " + message);
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }
}
