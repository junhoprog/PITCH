package org.cosmic.backend.domainsTest.bestAlbum;
import lombok.extern.log4j.Log4j2;
import org.cosmic.backend.domain.auth.dtos.UserLogin;
import org.cosmic.backend.domain.bestAlbum.dtos.BestAlbumDto;
import org.cosmic.backend.domain.playList.domains.Album;
import org.cosmic.backend.domain.playList.domains.Artist;
import org.cosmic.backend.domain.playList.repositorys.AlbumRepository;
import org.cosmic.backend.domain.playList.repositorys.ArtistRepository;
import org.cosmic.backend.domain.playList.repositorys.TrackRepository;
import org.cosmic.backend.domain.user.domains.Email;
import org.cosmic.backend.domain.user.domains.User;
import org.cosmic.backend.domain.user.repositorys.EmailRepository;
import org.cosmic.backend.domain.user.repositorys.UsersRepository;
import org.cosmic.backend.domainsTest.BaseSetting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
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
public class AddBestAlbumTest extends BaseSetting {

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
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    @Transactional
    @Sql("/data/albumPost.sql")
    public void albumAddTest() throws Exception {
        User user=userRepository.findByEmail_Email("test1@example.com").get();
        user.setPassword(encoder.encode(user.getPassword()));
        UserLogin userLogin = loginUser("test1@example.com");
        Album album=albumRepository.findByTitleAndArtist_ArtistName("bam","bibi").get();

        BestAlbumDto bestAlbumDto=BestAlbumDto.createBestAlbumDto(user.getUserId(),album.getAlbumId());
        mockMvcHelper("/api/bestAlbum/add/{albumId}",album.getAlbumId(),bestAlbumDto,userLogin.getToken())
            .andExpect(status().isOk());
    }
    @Test
    @Transactional
    @Sql("/data/albumPost.sql")
    public void notMatchAlbumAddTest() throws Exception {
        User user=userRepository.findByEmail_Email("test1@example.com").get();
        user.setPassword(encoder.encode(user.getPassword()));
        UserLogin userLogin = loginUser("test1@example.com");
        Album album=albumRepository.findByTitleAndArtist_ArtistName("bam","bibi").get();

        BestAlbumDto bestAlbumDto=BestAlbumDto.createBestAlbumDto(100L,album.getAlbumId());
        mockMvcHelper("/api/bestAlbum/add/{albumId}",album.getAlbumId(),bestAlbumDto,userLogin.getToken())
                .andExpect(status().isNotFound());
        bestAlbumDto=BestAlbumDto.createBestAlbumDto(user.getUserId(),100L);
        mockMvcHelper("/api/bestAlbum/add/{albumId}",100L,bestAlbumDto,userLogin.getToken())
                .andExpect(status().isNotFound());
    }
}
