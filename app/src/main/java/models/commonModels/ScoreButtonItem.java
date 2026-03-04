package models.commonModels;

import com.example.dartslivescorer.enums.eButtons;

public class ScoreButtonItem {
    private String label;
    private Integer point;

    private Integer id;
    private eButtons type;

    public ScoreButtonItem(Integer id, String label, Integer points, eButtons type) {
        this.id = id;
        this.label = label;
        this.point = points;
        this.type = type;
    }

    public Integer getId(){return id;}

    public String getLabel() {
        return label;
    }

    public Integer getPoint(){
        return point;
    }

    public eButtons getType(){ return type; }

}
