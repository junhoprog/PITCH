package org.cosmic.backend.domain.post.applications;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cosmic.backend.domain.playList.exceptions.NotFoundUserException;
import org.cosmic.backend.domain.post.dtos.Comment.CommentDetail;
import org.cosmic.backend.domain.post.entities.Post;
import org.cosmic.backend.domain.post.entities.PostComment;
import org.cosmic.backend.domain.post.entities.PostCommentLike;
import org.cosmic.backend.domain.post.exceptions.NotFoundCommentException;
import org.cosmic.backend.domain.post.exceptions.NotFoundPostException;
import org.cosmic.backend.domain.post.exceptions.NotMatchPostException;
import org.cosmic.backend.domain.post.exceptions.NotMatchUserException;
import org.cosmic.backend.domain.post.repositories.PostCommentLikeRepository;
import org.cosmic.backend.domain.post.repositories.PostCommentRepository;
import org.cosmic.backend.domain.post.repositories.PostRepository;
import org.cosmic.backend.domain.user.domains.User;
import org.cosmic.backend.domain.user.repositorys.UsersRepository;
import org.springframework.stereotype.Service;

/**
 * 댓글과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다. 댓글 조회, 생성, 수정, 삭제 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

  private final PostCommentRepository postCommentRepository;
  private final UsersRepository userRepository;
  private final PostRepository postRepository;
  private final PostCommentLikeRepository postCommentLikeRepository;

  /**
   * 주어진 게시글 ID로 댓글 목록을 조회합니다.
   *
   * @param postId 조회할 게시글의 ID
   * @return 게시글에 해당하는 댓글 요청 객체 리스트
   * @throws NotFoundPostException 게시글이 존재하지 않을 경우 발생합니다.
   */
  public List<CommentDetail> getCommentsByPostId(Long postId) {
    return CommentDetail.from(
        postRepository.findById(postId).orElseThrow(NotFoundPostException::new).getPostComments());
  }

  /**
   * 새로운 댓글을 생성합니다.
   *
   * @param content 생성할 댓글 정보
   * @return 생성된 댓글의 DTO 객체 {@link CommentDetail}
   * @throws NotFoundPostException 게시글이 존재하지 않을 경우 발생합니다.
   * @throws NotFoundUserException 사용자가 존재하지 않을 경우 발생합니다.
   */
  @Transactional
  public List<CommentDetail> createComment(Long parent_id, String content, Long postId,
      Long userId) {
    Post post = postRepository.findById(postId).orElseThrow(NotFoundPostException::new);
    User user = userRepository.findById(userId).orElseThrow(NotFoundUserException::new);

    PostComment parent = null;
    if (parent_id != null) {
      parent = postCommentRepository.findById(parent_id).orElseThrow(NotFoundPostException::new);
    }
    postCommentRepository.save(PostComment.builder()
        .content(content)
        .parentComment(parent)
        .user(user)
        .post(post)
        .build());

    return CommentDetail.from(postCommentRepository.findByPost_PostId(postId));
  }

  /**
   * 기존 댓글을 수정합니다.
   *
   * @param content 수정할 댓글의 요청 정보를 포함한 객체
   * @throws NotFoundCommentException 댓글이 존재하지 않을 경우 발생합니다.
   * @throws NotMatchUserException    사용자가 댓글 작성자와 일치하지 않을 경우 발생합니다.
   * @throws NotMatchPostException    댓글이 게시글과 일치하지 않을 경우 발생합니다.
   */
  @Transactional
  public List<CommentDetail> updateComment(String content, Long commentId, Long userId) {
    PostComment postComment = postCommentRepository.findById(commentId)
        .orElseThrow(NotFoundCommentException::new);
    if (!postComment.getUser().getUserId().equals(userId)) {
      throw new NotMatchUserException();
    }
    postComment.setContent(content);
    postCommentRepository.saveAndFlush(postComment);
    return CommentDetail.from(postCommentRepository.findByPost_PostId(commentId));
  }

  /**
   * 댓글을 삭제합니다.
   *
   * @throws NotFoundCommentException 댓글이 존재하지 않을 경우 발생합니다.
   */
  @Transactional
  public List<CommentDetail> deleteComment(Long commentId, Long postId, Long userId) {
    PostComment postComment = postCommentRepository.findById(commentId)
        .orElseThrow(NotFoundCommentException::new);
    if (!postComment.getUser().getUserId().equals(userId)) {
      throw new NotMatchUserException();
    }
    postCommentRepository.deleteById(commentId);
    return CommentDetail.from(postCommentRepository.findByPost_PostId(postId));
  }

  @Transactional
  public List<CommentDetail> likeComment(Long commentId, Long postId, Long userId) {
    PostComment postComment = postCommentRepository.findById(commentId)
        .orElseThrow(NotFoundPostException::new);
    User user = userRepository.findById(userId).orElseThrow(NotFoundUserException::new);

    if (!postComment.getUser().getUserId().equals(userId)) {
      throw new NotMatchUserException();
    }

    postCommentLikeRepository.save(
        PostCommentLike.builder().postComment(postComment).user(user).build());
    return postCommentRepository.findByPost_PostId(postId).stream()
        .map(CommentDetail::from).toList();
  }
}
