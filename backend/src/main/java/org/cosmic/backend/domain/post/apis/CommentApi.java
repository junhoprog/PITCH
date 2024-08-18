package org.cosmic.backend.domain.post.apis;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import org.cosmic.backend.domain.post.dtos.Comment.CommentDto;
import org.cosmic.backend.domain.post.dtos.Comment.CommentReq;
import org.cosmic.backend.domain.post.dtos.Comment.CreateCommentReq;
import org.cosmic.backend.domain.post.dtos.Comment.UpdateCommentReq;
import org.cosmic.backend.domain.post.dtos.Post.PostDto;
import org.cosmic.backend.domain.post.services.CommentService;
import org.cosmic.backend.globals.annotations.ApiCommonResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@ApiCommonResponses
public class CommentApi {
    private final CommentService commentService;

    public CommentApi(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/give")
    @Transactional
    @ApiResponse(responseCode = "404", description = "Not Found Post")
    public List<CommentReq> getCommentsByPostId(@RequestBody PostDto post) {//postid에 있는 comment들을
        return commentService.getCommentsByPostId(post.getPostId());
    }


    @PostMapping("/create")
    @Transactional
    @ApiResponse(responseCode = "404", description = "Not Found User or Post")
    public CommentDto createComment(@RequestBody CreateCommentReq comment) {
        return commentService.createComment(comment);
    }

    @PostMapping("/update")
    @Transactional
    @ApiResponse(responseCode = "404", description = "Not Found Post")
    public ResponseEntity<?> updateComment(@RequestBody UpdateCommentReq comment) {
        commentService.updateComment(comment);
        return ResponseEntity.ok("성공");
    }

    @PostMapping("/delete")
    @Transactional
    @ApiResponse(responseCode = "404", description = "Not Found Comment")
    public ResponseEntity<?> deleteComment(@RequestBody CommentDto commentdto) {
        commentService.deleteComment(commentdto);
        return ResponseEntity.ok("성공");
    }
}
