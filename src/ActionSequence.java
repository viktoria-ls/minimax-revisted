import java.util.ArrayList;

public class ActionSequence {
    private ArrayList<Action> sequence;
    private Integer orderingValue;

    public ActionSequence(ArrayList<Action> sequence) {
        this.sequence = sequence;
    }

    public ArrayList<Action> getActionSequence() {
        return sequence;
    }

    public void addToSequence(Action a) {
        sequence.add(a);
    }

    public Integer getOrderingValue() {
        return orderingValue;
    }

    public void setOrderingValue(Integer value) {
        orderingValue = value;
    }

    public boolean startsWith(ArrayList<Action> actions) {
        for(int i = 0; i < actions.size(); i++) {
            if(!actions.get(i).equals(sequence.get(i)))
                return false;
        }
        return true;
    }
}
