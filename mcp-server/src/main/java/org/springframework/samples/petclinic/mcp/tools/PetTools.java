package org.springframework.samples.petclinic.mcp.tools;

import java.util.List;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.samples.petclinic.mcp.client.PetclinicApiClient;
import org.springframework.samples.petclinic.mcp.client.dto.Pet;
import org.springframework.samples.petclinic.mcp.client.dto.PetFields;
import org.springframework.samples.petclinic.mcp.client.dto.PetType;
import org.springframework.stereotype.Component;

@Component
public class PetTools {

    private final PetclinicApiClient apiClient;

    public PetTools(PetclinicApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @McpTool(
        name = "list_pets",
        description = "ペット一覧を取得します。ownerId を指定すると、その飼い主のペットだけに絞り込みます。",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false)
    )
    public List<Pet> listPets(
        @McpToolParam(description = "飼い主 ID（省略可）", required = false) Integer ownerId
    ) {
        List<Pet> pets = apiClient.listPets();
        if (ownerId == null) {
            return pets;
        }
        return pets.stream().filter(pet -> ownerId.equals(pet.ownerId())).toList();
    }

    @McpTool(
        name = "create_pet",
        description = "飼い主にペットを登録します。birthDate は yyyy-MM-dd、petTypeId は list_pet_types で確認できます。",
        annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = false)
    )
    public Pet createPet(
        @McpToolParam(description = "飼い主 ID", required = true) int ownerId,
        @McpToolParam(description = "ペット名", required = true) String name,
        @McpToolParam(description = "生年月日（yyyy-MM-dd）", required = true) String birthDate,
        @McpToolParam(description = "ペット種別 ID", required = true) int petTypeId
    ) {
        PetType type = apiClient.requirePetType(petTypeId);
        return apiClient.createPet(ownerId, new PetFields(name, birthDate, new PetType(type.id(), type.name())));
    }
}