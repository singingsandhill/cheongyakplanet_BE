package org.cheonyakplanet.be.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentDTO {

    @Schema(example = "댓글 내용")
    private String content;
}
