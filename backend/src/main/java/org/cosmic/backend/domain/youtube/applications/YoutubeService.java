package org.cosmic.backend.domain.youtube.applications;

import org.cosmic.backend.domain.playList.domains.Playlist_Track;
import org.cosmic.backend.domain.playList.repositorys.PlaylistRepository;
import org.cosmic.backend.domain.playList.repositorys.PlaylistTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class YoutubeService {
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String clientId = System.getenv("YOUTUBE_CLIENT_ID");
    private final String clientSecret = System.getenv("YOUTUBE_CLIENT_SECRET");
    private final String redirectUri = "http://localhost:8080/oauth2/callback/google";
    private static final String YOUTUBE_PLAYLIST_URL = "https://www.googleapis.com/youtube/v3/playlists";
    private static final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
    private static final String YOUTUBE_PLAYLIST_ITEMS_URL = "https://www.googleapis.com/youtube/v3/playlistItems";
    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;
    @Autowired
    private PlaylistRepository playlistRepository;

    // 액세스 토큰을 요청하는 메서드
    public String getAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 본문 데이터 준비 (MultiValueMap 사용)
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", authorizationCode);
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            // Google 서버로 POST 요청 전송
            ResponseEntity<String> response = restTemplate.exchange(
                    TOKEN_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // 응답 본문에서 액세스 토큰 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get access token";
        }
    }

    public String createPlaylist(String title, String description,String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문 설정
        String requestBody = """
            {
              "snippet": {
                "title": "%s",
                "description": "%s",
                "privacyStatus": "public"
              }
            }
            """.formatted(title, description);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    YOUTUBE_PLAYLIST_URL + "?part=snippet",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            // 생성된 플레이리스트 ID 반환
            return jsonNode.get("id").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create playlist.";
        }
    }

    public String searchVideo(String query,String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    YOUTUBE_SEARCH_URL + "?part=snippet&q=" + query + "&maxResults=1",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            return jsonNode.get("items").get(0).get("id").get("videoId").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 플레이리스트에 비디오 추가
    public boolean addVideoToPlaylist(String playlistId, String videoId,String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
            {
              "snippet": {
                "playlistId": "%s",
                "resourceId": {
                  "kind": "youtube#video",
                  "videoId": "%s"
                }
              }
            }
            """.formatted(playlistId, videoId);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.exchange(
                    YOUTUBE_PLAYLIST_ITEMS_URL + "?part=snippet",
                    HttpMethod.POST,
                    request,
                    String.class
            );
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String createPlaylists(Long userId,String title,String description,String accessToken){
        // 1. 플레이리스트 생성
        String playlistId = createPlaylist(title,description,accessToken);
        List<Playlist_Track> playlistTracks= playlistTrackRepository.findByPlaylist_PlaylistId(
            playlistRepository.findByUser_UserId(userId).get().getPlaylistId()).get();
        for(int i=0; i<playlistTracks.size(); i++) {
            String query=playlistTracks.get(i).getTrack().getArtist().getArtistName()+" "+playlistTracks.get(i).getTrack().getTitle()+" official";
            String videoId = searchVideo(query,accessToken);
            if (videoId != null) {
                addVideoToPlaylist(playlistId, videoId,accessToken);
            }
        }
        return playlistId;
    }




}
