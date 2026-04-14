package com.project.MplTournament.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.MplTournament.config.MatchResultSyncProperties;
import com.project.MplTournament.dto.MatchResultSyncResponse;
import com.project.MplTournament.entity.MatchDetails;
import com.project.MplTournament.repository.MatchRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RapidApiMatchSyncService {

    private static final Logger log = LoggerFactory.getLogger(RapidApiMatchSyncService.class);
    private static final String NO_RESULT = "No result";
    private static final String COMPLETE = "Complete";
    private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");

    private final MatchRepo matchRepo;
    private final UserService userService;
    private final MatchResultSyncProperties syncProperties;
    private final RestClient.Builder restClientBuilder;

    public String syncPendingMatchResults() {
        validateSyncConfiguration();

        LocalDate today = LocalDate.now(INDIA_ZONE);
        LocalDate startDate = today.minusDays(syncProperties.getLookbackDays());
        List<MatchDetails> pendingMatches =
                matchRepo.findAllByMatchStatusAndMatchDateBetween(NO_RESULT, startDate, today);

        if (pendingMatches.isEmpty()) {
            return "No pending matches found for sync";
        }

        List<MatchResultSyncResponse> apiMatches = fetchRecentIplMatches();
        int updatedCount = 0;

        for (MatchDetails matchDetails : pendingMatches) {
            MatchResultSyncResponse apiMatch = findMatchingApiMatch(matchDetails, apiMatches);
            if (apiMatch == null) {
                log.info("No API match found for local match {} between {} and {}", matchDetails.getId(), matchDetails.getTeam1(), matchDetails.getTeam2());
                continue;
            }

            if (!apiMatch.isCompleted()) {
                log.info("Match {} is still not complete in API feed", matchDetails.getId());
                continue;
            }

            String resolvedStatus = resolveMatchStatus(matchDetails, apiMatch);
            matchDetails.setMatchStatus(resolvedStatus);
            MatchDetails savedMatch = matchRepo.save(matchDetails);

            if (isWinnerStatus(matchDetails, resolvedStatus)) {
                userService.updateUserPoints(savedMatch, syncProperties.getDefaultBetValue());
            }

            updatedCount++;
            log.info("Updated local match {} with status {}", matchDetails.getId(), resolvedStatus);
        }

        return "Recent match sync completed. Updated " + updatedCount + " match(es)";
    }

    private List<MatchResultSyncResponse> fetchRecentIplMatches() {
        RestClient restClient = restClientBuilder.baseUrl(syncProperties.getBaseUrl()).build();
        JsonNode response = restClient.get()
                .uri(syncProperties.getRecentMatchesPath())
                .header("x-rapidapi-key", syncProperties.getKey())
                .header("x-rapidapi-host", syncProperties.getHost())
                .header("Content-Type", "application/json")
                .retrieve()
                .body(JsonNode.class);

        List<MatchResultSyncResponse> matches = new ArrayList<>();
        JsonNode typeMatches = response.path("typeMatches");
        if (!typeMatches.isArray()) {
            return matches;
        }

        for (JsonNode typeMatch : typeMatches) {
            JsonNode seriesMatches = typeMatch.path("seriesMatches");
            if (!seriesMatches.isArray()) {
                continue;
            }

            for (JsonNode seriesMatch : seriesMatches) {
                JsonNode seriesWrapper = seriesMatch.path("seriesAdWrapper");
                if (seriesWrapper.isMissingNode()) {
                    continue;
                }

                String seriesName = readText(seriesWrapper.path("seriesName"));
                if (!syncProperties.getSeriesName().equalsIgnoreCase(seriesName)) {
                    continue;
                }

                JsonNode matchesNode = seriesWrapper.path("matches");
                if (!matchesNode.isArray()) {
                    continue;
                }

                for (JsonNode matchNode : matchesNode) {
                    JsonNode matchInfo = matchNode.path("matchInfo");
                    String stateTitle = readText(matchInfo.path("stateTitle"));
                    String status = readText(matchInfo.path("status"));
                    matches.add(MatchResultSyncResponse.builder()
                            .completed(COMPLETE.equalsIgnoreCase(readText(matchInfo.path("state"))))
                            .statusText(firstNonBlank(
                                    stateTitle,
                                    status,
                                    COMPLETE))
                            .winningTeam(extractWinningTeam(stateTitle, status))
                            .team1Name(readText(matchInfo.path("team1").path("teamName")))
                            .team1ShortName(readText(matchInfo.path("team1").path("teamSName")))
                            .team2Name(readText(matchInfo.path("team2").path("teamName")))
                            .team2ShortName(readText(matchInfo.path("team2").path("teamSName")))
                            .build());
                }
            }
        }

        return matches;
    }

    private MatchResultSyncResponse findMatchingApiMatch(MatchDetails matchDetails, List<MatchResultSyncResponse> apiMatches) {
        for (MatchResultSyncResponse apiMatch : apiMatches) {
            boolean sameOrder =
                    matchesTeam(matchDetails.getTeam1(), apiMatch.getTeam1Name(), apiMatch.getTeam1ShortName()) &&
                    matchesTeam(matchDetails.getTeam2(), apiMatch.getTeam2Name(), apiMatch.getTeam2ShortName());

            boolean reverseOrder =
                    matchesTeam(matchDetails.getTeam1(), apiMatch.getTeam2Name(), apiMatch.getTeam2ShortName()) &&
                    matchesTeam(matchDetails.getTeam2(), apiMatch.getTeam1Name(), apiMatch.getTeam1ShortName());

            if (sameOrder || reverseOrder) {
                return apiMatch;
            }
        }
        return null;
    }

    private String resolveMatchStatus(MatchDetails matchDetails, MatchResultSyncResponse apiMatch) {
        String winningTeam = apiMatch.getWinningTeam();
        String mappedWinner = mapWinningTeamToLocalTeam(matchDetails, apiMatch, winningTeam);
        if (StringUtils.hasText(mappedWinner)) {
            return mappedWinner;
        }

        String statusText = apiMatch.getStatusText();
        if (!StringUtils.hasText(statusText) || COMPLETE.equalsIgnoreCase(statusText)) {
            return "Match completed";
        }
        return statusText;
    }

    private String extractWinningTeam(String stateTitle, String status) {
        String winnerFromStateTitle = extractWinnerFromText(stateTitle);
        if (StringUtils.hasText(winnerFromStateTitle)) {
            return winnerFromStateTitle;
        }
        return extractWinnerFromStatus(status);
    }

    private String extractWinnerFromStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }

        String normalized = status.trim();
        if (normalized.equalsIgnoreCase(NO_RESULT)
                || normalized.toLowerCase(Locale.ENGLISH).contains("no result")
                || normalized.toLowerCase(Locale.ENGLISH).contains("abandon")
                || normalized.toLowerCase(Locale.ENGLISH).contains("draw")) {
            return null;
        }

        int wonIndex = normalized.toLowerCase(Locale.ENGLISH).indexOf(" won ");
        if (wonIndex > 0) {
            return normalized.substring(0, wonIndex).trim();
        }

        return null;
    }

    private String extractWinnerFromText(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }

        String normalized = text.trim();
        if (normalized.equalsIgnoreCase(COMPLETE)) {
            return null;
        }
        return normalized.replaceAll("(?i)\\s+won$", "").trim();
    }

    private boolean isWinnerStatus(MatchDetails matchDetails, String resolvedStatus) {
        return matchesTeam(matchDetails.getTeam1(), resolvedStatus, null)
                || matchesTeam(matchDetails.getTeam2(), resolvedStatus, null);
    }

    private String mapWinningTeamToLocalTeam(MatchDetails matchDetails, MatchResultSyncResponse apiMatch, String winningTeam) {
        if (!StringUtils.hasText(winningTeam)) {
            return null;
        }

        if (matchesTeam(winningTeam, apiMatch.getTeam1Name(), apiMatch.getTeam1ShortName())) {
            if (matchesTeam(matchDetails.getTeam1(), apiMatch.getTeam1Name(), apiMatch.getTeam1ShortName())) {
                return matchDetails.getTeam1();
            }
            if (matchesTeam(matchDetails.getTeam2(), apiMatch.getTeam1Name(), apiMatch.getTeam1ShortName())) {
                return matchDetails.getTeam2();
            }
        }

        if (matchesTeam(winningTeam, apiMatch.getTeam2Name(), apiMatch.getTeam2ShortName())) {
            if (matchesTeam(matchDetails.getTeam1(), apiMatch.getTeam2Name(), apiMatch.getTeam2ShortName())) {
                return matchDetails.getTeam1();
            }
            if (matchesTeam(matchDetails.getTeam2(), apiMatch.getTeam2Name(), apiMatch.getTeam2ShortName())) {
                return matchDetails.getTeam2();
            }
        }

        if (matchesTeam(matchDetails.getTeam1(), winningTeam, null)) {
            return matchDetails.getTeam1();
        }
        if (matchesTeam(matchDetails.getTeam2(), winningTeam, null)) {
            return matchDetails.getTeam2();
        }

        return null;
    }

    private boolean matchesTeam(String localTeam, String apiTeamName, String apiShortName) {
        String normalizedLocal = normalize(localTeam);
        String normalizedApiName = normalize(apiTeamName);
        String normalizedApiShortName = normalize(apiShortName);

        return sameOrContains(normalizedLocal, normalizedApiName)
                || sameOrContains(normalizedLocal, normalizedApiShortName)
                || sameOrContains(normalizedApiName, normalizedLocal)
                || sameOrContains(normalizedApiShortName, normalizedLocal);
    }

    private boolean sameOrContains(String left, String right) {
        return StringUtils.hasText(left)
                && StringUtils.hasText(right)
                && (left.equals(right) || left.contains(right) || right.contains(left));
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ENGLISH);
    }

    private String readText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return StringUtils.hasText(value) ? value : null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private void validateSyncConfiguration() {
        if (!syncProperties.isEnabled()) {
            throw new IllegalStateException("Rapid API sync is disabled. Enable rapid.api.enabled to use this endpoint.");
        }
        if (!StringUtils.hasText(syncProperties.getBaseUrl())
                || !StringUtils.hasText(syncProperties.getHost())
                || !StringUtils.hasText(syncProperties.getKey())) {
            throw new IllegalStateException("Rapid API configuration is incomplete. Set baseUrl, host and key in application properties.");
        }
    }
}
