package ua.profitsoft.jfd.lk9services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentUploadDto {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Surname")
    private String surname;

    @JsonProperty("Group")
    private Integer groupId;
}
