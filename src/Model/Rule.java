package Model;

public class Rule {
    private String name;
    private String value;

    public Rule(String name, String rule) {
        this.name = name;
        this.value = rule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String rule) {
        this.value = rule;
    }
    

    

}
