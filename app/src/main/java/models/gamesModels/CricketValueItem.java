package models.gamesModels;

public class CricketValueItem {

    private Integer num_item;
    private String libelle;

    private boolean hidden;
    private boolean available;

    public CricketValueItem(Integer num, String lib, boolean hid,boolean available)
    {
        this.num_item = num;
        this.libelle = lib;
        this.hidden = hid;
        this.available = available;
    }

    public void setHidden (boolean hid)
    {this.hidden = hid;}

    public boolean getAvailableItem(){
        return this.available;
    }
    public Integer getNumItem()
    {return this.num_item;}

    public String getLibelleItem()
    {return this.libelle;}

    public Integer getIntLibelleItem()
    {
        if(!this.libelle.equals("B"))
            return Integer.parseInt(this.libelle);
        else
            return 21;
    }

    public boolean getHiddenItem()
    {return this.hidden;}

    public void setAvailable(boolean avail)
    {
        this.available = avail;
    }

    public boolean getAvailable()
    {
        return this.available;
    }

}
