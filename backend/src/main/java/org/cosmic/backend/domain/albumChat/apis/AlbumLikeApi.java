package org.cosmic.backend.domain.albumChat.apis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cosmic.backend.domain.albumChat.applications.AlbumLikeService;
import org.cosmic.backend.domain.albumChat.exceptions.ExistAlbumLikeException;
import org.cosmic.backend.domain.albumChat.exceptions.NotFoundAlbumChatException;
import org.cosmic.backend.domain.playList.exceptions.NotFoundUserException;
import org.cosmic.backend.domain.post.dtos.Post.AlbumDetail;
import org.cosmic.backend.globals.annotations.ApiCommonResponses;
import org.cosmic.backend.globals.exceptions.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>AlbumLikeApi 클래스는 앨범 챗에서 특정 앨범에 대한 좋아요 기능을 제공하는 API를 정의합니다.</p>
 *
 * <p>이 API는 특정 앨범에 대한 좋아요 조회, 생성, 삭제 기능을 제공합니다.</p>
 */
@RestController
@RequestMapping("/api")
@ApiCommonResponses
@Tag(name = "앨범 챗 관련 API", description = "앨범 챗 댓글/대댓글/좋아요 제공")
public class AlbumLikeApi {

  private final AlbumLikeService likeService;

  /**
   * <p>AlbumLikeApi 생성자입니다.</p>
   *
   * @param likeService 앨범 챗 좋아요 서비스 객체
   */
  public AlbumLikeApi(AlbumLikeService likeService) {
    this.likeService = likeService;
  }

  /**
   * <p>특정 앨범의 앨범챗 좋아요 목록을 조회합니다.</p>
   *
   * @param spotifyAlbumId 조회할 앨범의 ID
   * @return 좋아요 목록을 포함한 {@link ResponseEntity}
   * @throws NotFoundAlbumChatException 앨범챗을 찾을 수 없을 때 발생합니다.
   */
  @GetMapping("/album/{spotifyAlbumId}/albumLike")
  @ApiResponse(responseCode = "404", description = "Not Found AlbumChat")
  @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = AlbumDetail.class)))
  @Operation(summary = "앨범 좋아요 조회", description = "특정 앨범의 앨범챗의 좋아요 조회")
  public ResponseEntity<AlbumDetail> searchAlbumChatAlbumLikeByAlbumId(
      @Parameter(description = "유저 id")
      @PathVariable String spotifyAlbumId) {
    return ResponseEntity.ok(likeService.getAlbumChatAlbumLikeBySpotifyAlbumId(spotifyAlbumId));
  }

  /**
   * <p>특정 앨범에 대한 새로운 앨범챗 좋아요를 생성합니다.</p>
   *
   * @param spotifyAlbumId 좋아요를 추가할 앨범의 ID
   * @param userId         좋아요를 생성하는 사용자 ID (인증된 사용자)
   * @return 생성된 좋아요 목록을 포함한 {@link ResponseEntity}
   * @throws NotFoundUserException      사용자를 찾을 수 없을 때 발생합니다.
   * @throws NotFoundAlbumChatException 앨범챗을 찾을 수 없을 때 발생합니다.
   * @throws ExistAlbumLikeException    이미 존재하는 좋아요가 있을 때 발생합니다.
   */
  @PostMapping("/album/{spotifyAlbumId}/like")
  @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
      array = @ArraySchema(schema = @Schema(implementation = AlbumDetail.class))))
  @ApiResponse(responseCode = "404", description = "Not Found User or album")
  @Operation(summary = "앨범 좋아요 API", description = "앨범에 대해 좋아요 혹은 좋아요를 취소합니다.")
  public ResponseEntity<AlbumDetail> likeAlbum(@PathVariable String spotifyAlbumId,
      @AuthenticationPrincipal Long userId) {
    try {
      likeService.unlike(spotifyAlbumId, userId);
    } catch (NotFoundException e) {
      likeService.like(spotifyAlbumId, userId);
    }
    return ResponseEntity.ok(likeService.getAlbumChatAlbumLikeBySpotifyAlbumId(spotifyAlbumId));
  }


}
