package org.cosmic.backend.domains.bestAlbum;

import lombok.extern.log4j.Log4j2;
import org.cosmic.backend.domain.auth.dtos.UserLogin;
import org.cosmic.backend.domain.bestAlbum.dtos.AlbumDto;
import org.cosmic.backend.domain.playList.domains.Album;
import org.cosmic.backend.domain.playList.domains.Artist;
import org.cosmic.backend.domain.playList.dtos.ArtistDto;
import org.cosmic.backend.domain.playList.repositorys.AlbumRepository;
import org.cosmic.backend.domain.playList.repositorys.ArtistRepository;
import org.cosmic.backend.domain.playList.repositorys.TrackRepository;
import org.cosmic.backend.domain.user.repositorys.EmailRepository;
import org.cosmic.backend.domain.user.repositorys.UsersRepository;
import org.cosmic.backend.domains.BaseSetting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Log4j2
public class SearchAlbumTest extends BaseSetting {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    EmailRepository emailRepository;
    @Autowired
    UsersRepository userRepository;
    @Autowired
    TrackRepository trackRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    ArtistRepository artistRepository;

    final ObjectMapper mapper = new ObjectMapper();
    @Test
    @Transactional
    public void albumNameSearchTest() throws Exception {
        UserLogin userLogin = loginUser("test@example.com");
        String validToken=userLogin.getToken();
        Instant now = Instant.now();

        Artist artist=saveArtist("비비");

        Album album=saveAlbum("밤양갱", artist, now);

        mockMvc.perform(post("/api/bestAlbum/Albumsearch")
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .content(mapper.writeValueAsString(AlbumDto.builder()
                    .albumName("밤양갱")
                    .build()
                )))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void artistNameSearchTest() throws Exception {
        UserLogin userLogin = loginUser("test@example.com");
        String validToken=userLogin.getToken();
        Instant now = Instant.now();

        Artist artist=saveArtist("비비");

        Album album=saveAlbum("밤양갱", artist, now);

        mockMvc.perform(post("/api/bestAlbum/Artistsearch")
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .content(mapper.writeValueAsString(ArtistDto.builder()
                    .artistName("비비")
                    .build()
                )))
            .andDo(print())
            .andExpect(status().isOk());
    }
}
