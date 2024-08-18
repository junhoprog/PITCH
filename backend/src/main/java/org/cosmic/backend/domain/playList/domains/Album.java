package org.cosmic.backend.domain.playList.domains;

import jakarta.persistence.*;
import lombok.*;
import org.cosmic.backend.domain.albumChat.domains.AlbumChat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="`Album`")
@EqualsAndHashCode(exclude = {"albumchat"})
public class Album {//앨범과 트랙은 1:N관계이며 앨범과 아티스트는 더 생각 필요

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="albumId")
    private Long albumId;

    @Column(nullable=false)
    private String title;//앨범 제목

    @Column(nullable=false)
    private String cover;

    @Column(nullable=false)
    private String genre;

    @Builder.Default
    @Column(nullable=false)
    private Instant createdDate=Instant.now();//발매 일

    //아티스트와 1:N 관계
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="artistId")
    private Artist artist;

    @OneToMany(mappedBy = "album")
    @Builder.Default
    private List<Track> tracks=new ArrayList<>();

    @OneToOne(mappedBy = "album")
    private AlbumChat albumchat;

    public Album(String genre, String title,String cover, Artist artist, Instant createdDate){
        this.genre = genre;
        this.title = title;
        this.artist = artist;
        this.createdDate = createdDate;
        this.cover = cover;
    }
    @Override
    public String toString() {
        return "Album{" +
                "albumId=" + albumId +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", genre='" + genre + '\'' +
                ", createdDate=" + createdDate +
                ", artist=" + (artist != null ? artist.getArtistId() : "null") +
                '}';
    }
}