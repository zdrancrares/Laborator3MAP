package ro.ubbcluj.map.exceptions;

public class ServiceExceptions extends Exception{
    private String message;
    public ServiceExceptions(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
