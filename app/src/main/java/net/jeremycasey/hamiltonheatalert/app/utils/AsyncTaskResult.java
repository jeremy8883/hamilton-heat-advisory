package net.jeremycasey.hamiltonheatalert.app.utils;

public class AsyncTaskResult<T> {
    private T mResult;
    private Exception mError;

    public T getResult() {
        return mResult;
    }

    public Exception getError() {
        return mError;
    }

    public AsyncTaskResult(T result) {
        this.mResult = result;
    }

    public AsyncTaskResult(Exception error) {
        this.mError = error;
    }
}
