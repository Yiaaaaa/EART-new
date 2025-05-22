package org.example.erat.model;

import java.util.List;

public abstract class ReportComponent {
    protected String reportName;

    public ReportComponent(String name) {
        this.reportName = reportName;
    }

    public String getreportName() {
        return reportName;
    }

    public abstract List<String> getExperiments();

    public String getDisplayInfo() {
        return reportName;
    }
}