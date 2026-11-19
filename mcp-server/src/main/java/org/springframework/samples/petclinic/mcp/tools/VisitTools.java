package org.springframework.samples.petclinic.mcp.tools;

import java.time.LocalDate;
import java.util.List;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.samples.petclinic.mcp.client.PetclinicApiClient;
import org.springframework.samples.petclinic.mcp.client.dto.Visit;
import org.springframework.samples.petclinic.mcp.client.dto.VisitFields;
import org.springframework.stereotype.Component;

@Component
public class VisitTools {

    private final PetclinicApiClient apiClient;

    public VisitTools(PetclinicApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @McpTool(
        name = "list_visits",
        description = "来院記録一覧を取得します。petId を指定すると、そのペットの記録だけに絞り込みます。",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false)
    )
    public List<Visit> listVisits(
        @McpToolParam(description = "ペット ID（省略可）", required = false) Integer petId
    ) {
        List<Visit> visits = apiClient.listVisits();
        if (petId == null) {
            return visits;
        }
        return visits.stream().filter(visit -> petId.equals(visit.petId())).toList();
    }

    @McpTool(
        name = "create_visit",
        description = "飼い主のペットに来院記録を登録します。date を省略すると当日になります。description は 1〜255 文字です。",
        annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = false)
    )
    public Visit createVisit(
        @McpToolParam(description = "飼い主 ID", required = true) int ownerId,
        @McpToolParam(description = "ペット ID", required = true) int petId,
        @McpToolParam(description = "来院内容", required = true) String description,
        @McpToolParam(description = "来院日（yyyy-MM-dd、省略可）", required = false) String date
    ) {
        apiClient.getOwnersPet(ownerId, petId);
        String visitDate = (date == null || date.isBlank()) ? LocalDate.now().toString() : date;
        return apiClient.createVisit(ownerId, petId, new VisitFields(visitDate, description));
    }
}