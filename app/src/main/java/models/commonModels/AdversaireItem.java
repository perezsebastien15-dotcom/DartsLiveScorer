package models.commonModels;

public class AdversaireItem {

    private String name;
    private Integer score;

    public AdversaireItem(String name,int score)
    {
        this.name = name;
        this.score = score;
    }

    public String getInfos(){return name + " : "+ String.valueOf(this.score);}

}
