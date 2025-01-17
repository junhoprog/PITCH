package org.cosmic.backend.domain.playList.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Playlist_Track")
public class Playlist_Track {//N:M을 이어줄 연결다리

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "playlistId")
  private Playlist playlist;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trackId")
  private Track track;

  //TODO: order 관련 서비스를 다시 만들어야 함
  @Builder.Default
  @Column(name = "track_order")
  private Integer trackOrder = 0;

  public static Playlist_Track from(Track track, Playlist playlist) {
    return Playlist_Track.builder()
        .track(track)
        .playlist(playlist)
        .build();
  }

  public static Playlist_Track from(Track track) {
    return Playlist_Track.from(track, null);
  }

  public String getTrackId() {
    return getTrack().getSpotifyTrackId();
  }
}