package com.talabaty.swever.admin.Managment.Employees.AddEmployee.SpinnerModels;

public class State {
    int Id ;
    String Name;

    public State(int id, String name) {
        Id = id;
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }
}
