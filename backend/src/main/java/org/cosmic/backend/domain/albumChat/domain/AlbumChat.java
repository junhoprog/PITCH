package org.cosmic.backend.domain.albumChat.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cosmic.backend.domain.playList.domain.Album;

import java.time.Instant;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "albumChat") // 테이블 이름 수정
public class AlbumChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "albumChat_id") // 컬럼 이름 명시
    private Long albumChatId;

    private String cover; // 앨범커버
    private String title; // 앨범제목
    private String genre;
    private String artistName;
    private Instant CreateTime;

    //앨범이랑 연관져야할듯
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="albumId")
    private Album album;

    @OneToMany(mappedBy = "albumChat")
    private List<AlbumChatComment> albumChatComments;

    @OneToMany(mappedBy = "albumChat")
    private List<AlbumChatAlbumLike> albumChatAlbumLikes;
}