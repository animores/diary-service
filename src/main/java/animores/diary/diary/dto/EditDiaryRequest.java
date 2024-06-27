package animores.diary.diary.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EditDiaryRequest(
    @NotNull
    @NotEmpty
    String content
) {

}
