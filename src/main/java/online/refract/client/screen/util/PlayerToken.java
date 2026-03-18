package online.refract.client.gui;


public class PlayerToken {

    public String name;
    public String username;

    public int renderX;
    public int renderY;

    public PlayerToken(int id, String name, String username) {
        this.name = name; 
        this.username = username;
    }
    
}
