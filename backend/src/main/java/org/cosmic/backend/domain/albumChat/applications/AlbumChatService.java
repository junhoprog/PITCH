package org.cosmic.backend.domain.albumChat.applications;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cosmic.backend.domain.albumChat.dtos.albumChat.AlbumChatDetail;
import org.cosmic.backend.domain.albumChat.exceptions.NotFoundAlbumChatException;
import org.cosmic.backend.domain.playList.domains.Album;
import org.cosmic.backend.domain.playList.repositorys.AlbumRepository;
import org.cosmic.backend.domain.post.exceptions.NotFoundAlbumException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * <p>AlbumChat 페이지와 관련된 기능들을 제공하는 서비스 클래스입니다.</p>
 *
 * <p>이 서비스는 특정 앨범의 앨범챗 데이터를 가져오고, 최근 댓글 목록을 조회하는 기능을 제공합니다.</p>
 */
@Service
@RequiredArgsConstructor
public class AlbumChatService {

  @Autowired
  private AlbumRepository albumRepository;

  /**
   * <p>특정 앨범의 앨범챗 데이터를 조회합니다.</p>
   *
   * @param spotifyAlbumId 앨범 Id
   * @return 앨범챗 세부 정보를 포함한 {@link AlbumChatDetail}
   * @throws NotFoundAlbumChatException 앨범챗을 찾을 수 없을 때 발생합니다.
   */
  public AlbumChatDetail getAlbumChatById(String spotifyAlbumId) {
    Album album = albumRepository.findBySpotifyAlbumId(spotifyAlbumId)
        .orElseThrow(NotFoundAlbumException::new);
    return AlbumChatDetail.from(album);
  }

  private List<Album> getAlbumsSortedByCommentCount(Integer limit) {
    Pageable pageable = PageRequest.of(0, limit);
    return albumRepository.findAlbumsOrderByCommentCount(pageable).getContent();
  }

  public List<AlbumChatDetail> albumChatHomeChat() {
    return AlbumChatDetail.from(getAlbumsSortedByCommentCount(5));
  }

  private List<Album> getAlbumsSortedByLikeCount(Integer page, Integer limit) {
    Pageable pageable = PageRequest.of(page, limit);
    return albumRepository.findAlbumsOrderByAlbumLikeCount(pageable).getContent();
  }

  public List<AlbumChatDetail> albumChatHomeLike(Integer page, Integer limit) {
    return AlbumChatDetail.from(getAlbumsSortedByLikeCount(page, limit));
  }
}
