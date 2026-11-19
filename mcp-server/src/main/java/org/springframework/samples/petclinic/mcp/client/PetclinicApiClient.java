package org.springframework.samples.petclinic.mcp.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mcp.client.dto.Owner;
import org.springframework.samples.petclinic.mcp.client.dto.OwnerFields;
import org.springframework.samples.petclinic.mcp.client.dto.Pet;
import org.springframework.samples.petclinic.mcp.client.dto.PetFields;
import org.springframework.samples.petclinic.mcp.client.dto.PetType;
import org.springframework.samples.petclinic.mcp.client.dto.Vet;
import org.springframework.samples.petclinic.mcp.client.dto.Visit;
import org.springframework.samples.petclinic.mcp.client.dto.VisitFields;
import org.springframework.samples.petclinic.mcp.config.PetclinicApiProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 既存 Petclinic REST API を呼び出す薄いクライアント層。
 * 1 メソッドが 1 エンドポイントに対応し、HTTP エラーは {@link ApiErrorTranslator} 経由で {@link PetclinicApiException} に変換する。
 */
@Component
public class PetclinicApiClient {

    private static final ParameterizedTypeReference<List<Owner>> OWNER_LIST = new ParameterizedTypeReference<>() {};

    private static final ParameterizedTypeReference<List<Pet>> PET_LIST = new ParameterizedTypeReference<>() {};

    private static final ParameterizedTypeReference<List<Vet>> VET_LIST = new ParameterizedTypeReference<>() {};

    private static final ParameterizedTypeReference<List<PetType>> PET_TYPE_LIST = new ParameterizedTypeReference<>() {};

    private static final ParameterizedTypeReference<List<Visit>> VISIT_LIST = new ParameterizedTypeReference<>() {};

    private final RestClient apiClient;

    private final RestClient openApiClient;

    private final ApiErrorTranslator errorTranslator;

    private final PetclinicApiProperties properties;

    public PetclinicApiClient(
        RestClient petclinicRestClient,
        RestClient openApiRestClient,
        ApiErrorTranslator errorTranslator,
        PetclinicApiProperties properties
    ) {
        this.apiClient = petclinicRestClient;
        this.openApiClient = openApiRestClient;
        this.errorTranslator = errorTranslator;
        this.properties = properties;
    }

    public List<Owner> listOwners(String lastName) {
        if (lastName != null && !lastName.isBlank()) {
            String uri = UriComponentsBuilder.fromPath("/owners").queryParam("lastName", lastName).build().toUriString();
            return exchange(() -> apiClient.get().uri(uri).retrieve().body(OWNER_LIST), null);
        }
        return exchange(() -> apiClient.get().uri("/owners").retrieve().body(OWNER_LIST), null);
    }

    public Owner getOwner(int ownerId) {
        return exchange(
            () -> apiClient.get().uri("/owners/{id}", ownerId).retrieve().body(Owner.class),
            "飼い主 ID " + ownerId + " は見つかりません。list_owners で ID を確認してください。"
        );
    }

    public Owner createOwner(OwnerFields fields) {
        return exchange(() -> apiClient.post()
            .uri("/owners")
            .contentType(MediaType.APPLICATION_JSON)
            .body(fields)
            .retrieve()
            .body(Owner.class), null);
    }

    public List<Pet> listPets() {
        List<Pet> pets = exchange(() -> apiClient.get().uri("/pets").retrieve().body(PET_LIST), null);
        return pets != null ? pets : List.of();
    }

    public Pet getOwnersPet(int ownerId, int petId) {
        return exchange(
            () -> apiClient.get().uri("/owners/{ownerId}/pets/{petId}", ownerId, petId).retrieve().body(Pet.class),
            "飼い主 ID " + ownerId + " のペット ID " + petId + " は見つかりません。"
        );
    }

    public Pet createPet(int ownerId, PetFields fields) {
        return exchange(() -> apiClient.post()
            .uri("/owners/{ownerId}/pets", ownerId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(fields)
            .retrieve()
            .body(Pet.class), null);
    }

    public List<Vet> listVets() {
        List<Vet> vets = exchange(() -> apiClient.get().uri("/vets").retrieve().body(VET_LIST), null);
        return vets != null ? vets : List.of();
    }

    public List<PetType> listPetTypes() {
        List<PetType> types = exchange(() -> apiClient.get().uri("/pettypes").retrieve().body(PET_TYPE_LIST), null);
        return types != null ? types : List.of();
    }

    /** {@code create_pet} 用。存在しない ID のときは利用可能な種別一覧を例外メッセージに含める。 */
    public PetType requirePetType(int petTypeId) {
        return listPetTypes().stream()
            .filter(type -> type.id() != null && type.id() == petTypeId)
            .findFirst()
            .orElseThrow(() -> {
                String available = listPetTypes().stream()
                    .map(type -> type.id() + "=" + type.name())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("（取得できませんでした）");
                return new PetclinicApiException(
                    "ペット種別 ID " + petTypeId + " は存在しません。list_pet_types で確認してください。利用可能: " + available);
            });
    }

    public List<Visit> listVisits() {
        List<Visit> visits = exchange(() -> apiClient.get().uri("/visits").retrieve().body(VISIT_LIST), null);
        return visits != null ? visits : List.of();
    }

    public Visit createVisit(int ownerId, int petId, VisitFields fields) {
        return exchange(() -> apiClient.post()
            .uri("/owners/{ownerId}/pets/{petId}/visits", ownerId, petId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(fields)
            .retrieve()
            .body(Visit.class), null);
    }

    public String fetchOpenApiSpec() {
        String url = properties.openApiDocsUrl();
        try {
            return openApiClient.get().uri(url).accept(MediaType.valueOf("application/yaml")).retrieve().body(String.class);
        }
        catch (RestClientResponseException ex) {
            throw errorTranslator.translateResponse(ex, null);
        }
        catch (RuntimeException ex) {
            throw errorTranslator.translateConnection(ex);
        }
    }

    /** 共通の例外変換付き API 呼び出し。{@code notFoundHint} は 404 時に AI へ返す文言。 */
    private <T> T exchange(ApiCall<T> call, String notFoundHint) {
        try {
            return call.call();
        }
        catch (RestClientResponseException ex) {
            throw errorTranslator.translateResponse(ex, notFoundHint);
        }
        catch (RuntimeException ex) {
            if (ex instanceof PetclinicApiException) {
                throw ex;
            }
            throw errorTranslator.translateConnection(ex);
        }
    }

    @FunctionalInterface
    private interface ApiCall<T> {
        T call();
    }
}