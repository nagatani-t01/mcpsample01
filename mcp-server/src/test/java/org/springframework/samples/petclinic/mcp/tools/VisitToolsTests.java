package org.springframework.samples.petclinic.mcp.tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.mcp.client.PetclinicApiClient;
import org.springframework.samples.petclinic.mcp.client.dto.Pet;
import org.springframework.samples.petclinic.mcp.client.dto.PetType;
import org.springframework.samples.petclinic.mcp.client.dto.Visit;

@ExtendWith(MockitoExtension.class)
class VisitToolsTests {

    @Mock
    private PetclinicApiClient apiClient;

    @InjectMocks
    private VisitTools visitTools;

    @Test
    void createVisitChecksPetExists() {
        when(apiClient.getOwnersPet(1, 2)).thenReturn(new Pet(2, 1, "Leo", "2010-01-01", new PetType(1, "cat"), null));
        when(apiClient.createVisit(anyInt(), anyInt(), any())).thenReturn(new Visit(9, 2, "2026-11-19", "checkup"));

        visitTools.createVisit(1, 2, "checkup", "2026-11-19");

        verify(apiClient).getOwnersPet(1, 2);
    }

    @Test
    void listVisitsFiltersByPetId() {
        when(apiClient.listVisits()).thenReturn(
            List.of(new Visit(1, 8, "2020-01-01", "a"), new Visit(2, 7, "2020-01-02", "b")));
        assertThat(visitTools.listVisits(8)).hasSize(1);
    }
}