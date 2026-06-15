package com.example.demo.dto;


import lombok.Data;
import java.util.Map;

@Data
public class DashboardStatsDTO {
    private long totalLeads;
    private long activeLeads;
    private double conversionRate;
    private long followUpsDueToday;
    private Map<String, Long> leadsByStage;
    private Map<String, Object> teamPerformance;
}