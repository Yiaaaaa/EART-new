package org.example.erat.model;

import java.util.List;

public abstract class ReportComponent {
    protected String name;

    public ReportComponent(String name) {
        this.name = name;
    }

    public abstract List<String> getExperiments();
}