package com.teamtacles.teamtacles_api.dto.request;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestPatchDTO {
    private Optional<String> title = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<List<Long>> team = Optional.empty();
}
