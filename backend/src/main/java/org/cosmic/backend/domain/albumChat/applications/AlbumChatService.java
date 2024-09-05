package org.cosmic.backend.domain.albumChat.applications;

import org.cosmic.backend.domain.albumChat.dtos.albumChat.AlbumChatResponse;
import org.cosmic.backend.domain.albumChat.dtos.comment.AlbumChatCommentResponse;
import org.cosmic.backend.domain.albumChat.exceptions.NotFoundAlbumChatException;
import org.cosmic.backend.domain.albumChat.repositorys.AlbumChatCommentRepository;
import org.cosmic.backend.domain.playList.repositorys.AlbumRepository;
import org.cosmic.backend.domain.post.dtos.Post.AlbumDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AlbumChat페이지와 관련된 기능들을 제공하는 서비스 클래스.
 */
@Service
public class AlbumChatService {
    private final AlbumRepository albumRepository;
    private final AlbumChatCommentRepository albumChatCommentRepository;
    /**
     * AlbumChatService 생성자.
     *
     * @param albumChatCommentRepository AlbumChatCommentRepository 주입
     */
    public AlbumChatService(AlbumRepository albumRepository, AlbumChatCommentRepository albumChatCommentRepository) {
        this.albumRepository = albumRepository;
        this.albumChatCommentRepository = albumChatCommentRepository;
    }

    /**
     * 주어진 앨범 ID에 해당하는 AlbumChat을 반환합니다.
     *
     * @param album AlbumDto 객체로, 조회하려는 앨범id를 포함합니다.
     * @return 앨범 및 아티스트 정보를 포함한 AlbumChatResponse 객체
     * @throws NotFoundAlbumChatException 앨범 채팅을 찾을 수 없는 경우 발생
     */
    @Transactional
    public AlbumChatResponse getAlbumChatById(AlbumDto album) {
        if(albumRepository.findById(album.getAlbumId()).isEmpty()) {
            throw new NotFoundAlbumChatException();
        }
        return new AlbumChatResponse(albumRepository.findById(album.getAlbumId()).get());
    }
    /**
     * 주어진 앨범 채팅 ID에 해당하는 댓글을 좋아요 순으로 정렬하여 반환합니다.
     *
     * @param album AlbumChatDto객체로, 조회하려는 앨범 채팅Id를 포함합니다.
     * @return 좋아요 순으로 정렬된 AlbumChatCommentResponse 객체의 리스트
     * @throws NotFoundAlbumChatException 앨범 채팅을 찾을 수 없는 경우 발생
     */
    @Transactional
    public List<AlbumChatCommentResponse> getAlbumChatCommentByManyLikeId(AlbumDto album) {
        if(albumRepository.findById(album.getAlbumId()).isEmpty()) {
            throw new NotFoundAlbumChatException();
        }

        return albumChatCommentRepository.findByAlbumIdOrderByCountAlbumChatCommentLikes(album.getAlbumId())
                .orElse(Collections.emptyList())
                .stream()
                .map(AlbumChatCommentResponse::new)
                .collect(Collectors.toList());
    }
}
