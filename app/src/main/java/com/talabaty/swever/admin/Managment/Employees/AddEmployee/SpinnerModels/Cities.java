package com.talabaty.swever.admin.Managment.Employees.AddEmployee.SpinnerModels;

public class Cities {
    int Id , StateId;
    String Name;

    public Cities(int id,int StateId, String name) {
        Id = id;
        Name = name;
        this.StateId = StateId;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public int getStateId() {
        return StateId;
    }
}
