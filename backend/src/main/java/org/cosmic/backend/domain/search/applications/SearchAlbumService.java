package org.cosmic.backend.domain.search.applications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cosmic.backend.domain.playList.domains.Album;
import org.cosmic.backend.domain.playList.domains.Artist;
import org.cosmic.backend.domain.playList.repositorys.AlbumRepository;
import org.cosmic.backend.domain.playList.repositorys.ArtistRepository;
import org.cosmic.backend.domain.search.dtos.SpotifySearchAlbumResponse;
import org.cosmic.backend.domain.search.dtos.SpotifySearchArtistResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchAlbumService extends SearchService {
    List<SpotifySearchAlbumResponse> spotifySearchAlbumResponses = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = null;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private ArtistRepository artistRepository;

    public List<SpotifySearchAlbumResponse> searchAlbum(String accessToken, String q) throws JsonProcessingException {
        rootNode = mapper.readTree(search(accessToken,q));
        try {
            JsonNode albumitemsNode = rootNode.path("albums").path("items");

            for (int i = 0; i < albumitemsNode.size(); i++) {
                SpotifySearchAlbumResponse spotifySearchAlbumResponse = new SpotifySearchAlbumResponse();
                JsonNode item = albumitemsNode.get(i);
                spotifySearchAlbumResponse.setRelease_date(item.path("release_date").asText());
                spotifySearchAlbumResponse.setAlbumId(item.path("id").asText());
                spotifySearchAlbumResponse.setName(item.path("name").asText());
                spotifySearchAlbumResponse.setTotal_tracks(item.path("total_tracks").asInt());

                spotifySearchAlbumResponse.setImageUrl(albumitemsNode.get(i).path("images").get(0).path("url").asText());
                item = albumitemsNode.get(i).path("artists");

                for (int j = 0; j < item.size(); j++) {
                    SpotifySearchArtistResponse spotifySearchArtistResponse = new SpotifySearchArtistResponse();
                    JsonNode artistsNode = item.get(j);
                    spotifySearchArtistResponse.setArtistId(artistsNode.path("id").asText());
                    spotifySearchArtistResponse.setName(artistsNode.path("name").asText());
                    spotifySearchArtistResponse.setImageUrl(null);
                    spotifySearchAlbumResponse.setAlbumArtist(spotifySearchArtistResponse);
                }
                spotifySearchAlbumResponses.add(spotifySearchAlbumResponse);

                String datas = searchSpotifyArtist(accessToken, spotifySearchAlbumResponses.get(i).getAlbumArtist().getArtistId());

                rootNode = mapper.readTree(datas);
                JsonNode artistNode = rootNode.path("images");
                String imgUrl = artistNode.get(0).path("url").asText();
                spotifySearchAlbumResponses.get(i).getAlbumArtist().setImageUrl(imgUrl);
            }
        }
        catch (Exception e) {

        }
        return spotifySearchAlbumResponses; // 예외 발생 시 null 반환
    }


    public SpotifySearchAlbumResponse searchAlbumId(String accessToken, String albumId) throws JsonProcessingException {
        JsonNode albumItem = mapper.readTree(searchSpotifyAlbum(accessToken, albumId));
        SpotifySearchAlbumResponse spotifySearchAlbumResponse = new SpotifySearchAlbumResponse();
        spotifySearchAlbumResponse.setAlbumId(albumItem.path("id").asText());
        spotifySearchAlbumResponse.setName(albumItem.path("name").asText());
        spotifySearchAlbumResponse.setTotal_tracks(albumItem.path("total_tracks").asInt());
        spotifySearchAlbumResponse.setRelease_date(albumItem.path("release_date").asText());

        JsonNode imageNode=albumItem.path("images").get(0);
        spotifySearchAlbumResponse.setImageUrl(imageNode.path("url").asText());

        JsonNode artistNode=albumItem.path("artists").get(0);
        SpotifySearchArtistResponse spotifySearchArtistResponse = new SpotifySearchArtistResponse();
        spotifySearchArtistResponse.setName(artistNode.path("name").asText());
        spotifySearchArtistResponse.setArtistId(artistNode.path("id").asText());
        //artistId로 img가져오기
        JsonNode rootNode2=mapper.readTree(searchSpotifyArtist(accessToken, artistNode.path("id").asText()));
        JsonNode artistImgNode = rootNode2.path("images");
        String imgUrl = artistImgNode.get(0).path("url").asText();
        spotifySearchArtistResponse.setImageUrl(imgUrl);
        spotifySearchAlbumResponse.setAlbumArtist(spotifySearchArtistResponse);

        LocalDate localDate = LocalDate.parse(spotifySearchAlbumResponse.getRelease_date());
        Instant instant = localDate.atStartOfDay(ZoneId.of("UTC")).toInstant();

        albumRepository.save(Album.builder()
                .spotifyAlbumId(spotifySearchAlbumResponse.getAlbumId())
                .title(spotifySearchAlbumResponse.getName())
                .albumCover(spotifySearchAlbumResponse.getImageUrl())
                .createdDate(instant)
                .build());

        if(artistRepository.findBySpotifyArtistId(spotifySearchArtistResponse.getArtistId()).isEmpty()){
            artistRepository.save(Artist.builder()
                .artistCover(spotifySearchArtistResponse.getImageUrl())
                .spotifyArtistId(spotifySearchArtistResponse.getArtistId())
                .artistName(spotifySearchArtistResponse.getName())
                .build());
        }

        return spotifySearchAlbumResponse; // 예외 발생 시 null 반환
    }

}
