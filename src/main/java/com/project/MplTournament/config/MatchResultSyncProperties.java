package com.project.MplTournament.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rapid.api")
public class MatchResultSyncProperties {

    private boolean enabled;

    private String key;

    private String host;

    private String baseUrl;

    private String recentMatchesPath = "/matches/v1/recent";

    private String seriesName = "Indian Premier League 2026";

    private Integer defaultBetValue = 25;

    private Integer lookbackDays = 2;
}
