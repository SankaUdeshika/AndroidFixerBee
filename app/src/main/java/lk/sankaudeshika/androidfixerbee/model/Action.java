package lk.sankaudeshika.androidfixerbee.model;

public class Action {
    String ActionName ;
    String ActionDate;
    String ActinTime;

    public Action(String actionName, String actionDate, String actinTime) {
        ActionName = actionName;
        ActionDate = actionDate;
        ActinTime = actinTime;
    }

    public String getActionName() {
        return ActionName;
    }

    public void setActionName(String actionName) {
        ActionName = actionName;
    }

    public String getActionDate() {
        return ActionDate;
    }

    public void setActionDate(String actionDate) {
        ActionDate = actionDate;
    }

    public String getActinTime() {
        return ActinTime;
    }

    public void setActinTime(String actinTime) {
        ActinTime = actinTime;
    }
}
