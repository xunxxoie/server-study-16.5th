package org.todo.todo.domain.todo.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "투두 삭제 req dto")
@Getter
@NoArgsConstructor
public class TodoDeleteRequestDto {
    @Schema(description = "투두 아이디", example = "1")
    @NotBlank(message = "투두 ID가 포함되지 않았습니다.")
    private Long todoId;
}
