package entity;

public interface Explodable {
    public static enum State { idle, exploding, finishedExploding };
    public State getState();
    public void setState(State state);
    public void explode();    
}
