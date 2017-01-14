package strategy.actions;
/**
 * Created by Simon Rovder
 */
public interface ActionInterface {

    void enterState(int newState);
    void tok() throws ActionException;
    void tik() throws ActionException;
    void delay(long millis);
    String description();
}
