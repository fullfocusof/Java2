package org.example.OS;

public class OperationStatus
{
    private Status _status;
    private String _message;

    public OperationStatus()
    {

    }

    public OperationStatus(Status status, String message)
    {
        _status = status;
        _message = message;
    }

    public void setStatus(Status status)
    {
        _status = status;
    }

    public void setMessage(String message)
    {
        _message = message;
    }

    public Status getStatus()
    {
        return _status;
    }

    public String getMessage()
    {
        return _message;
    }

    @Override
    public String toString()
    {
        return _status + "\n" + _message + "\n";
    }
}
