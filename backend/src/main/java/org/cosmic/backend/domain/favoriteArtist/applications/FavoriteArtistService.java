package org.cosmic.backend.domain.favoriteArtist.applications;

import org.cosmic.backend.domain.favoriteArtist.domains.FavoriteArtist;
import org.cosmic.backend.domain.favoriteArtist.dtos.*;
import org.cosmic.backend.domain.favoriteArtist.repositorys.FavoriteArtistRepository;
import org.cosmic.backend.domain.playList.domains.Album;
import org.cosmic.backend.domain.playList.domains.Artist;
import org.cosmic.backend.domain.playList.domains.Track;
import org.cosmic.backend.domain.playList.exceptions.NotFoundArtistException;
import org.cosmic.backend.domain.playList.exceptions.NotFoundTrackException;
import org.cosmic.backend.domain.playList.exceptions.NotFoundUserException;
import org.cosmic.backend.domain.playList.repositorys.AlbumRepository;
import org.cosmic.backend.domain.playList.repositorys.ArtistRepository;
import org.cosmic.backend.domain.playList.repositorys.TrackRepository;
import org.cosmic.backend.domain.post.exceptions.NotFoundAlbumException;
import org.cosmic.backend.domain.user.domains.User;
import org.cosmic.backend.domain.user.repositorys.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class FavoriteArtistService {
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UsersRepository usersRepository;
    private final FavoriteArtistRepository favoriteArtistRepository;

    public FavoriteArtistService(ArtistRepository artistRepository, AlbumRepository albumRepository, TrackRepository trackRepository, UsersRepository usersRepository, FavoriteArtistRepository favoriteArtistRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
        this.usersRepository = usersRepository;
        this.favoriteArtistRepository = favoriteArtistRepository;
    }


    public FavoriteArtistDto favoriteArtistGiveData(Long userId) {
        if(usersRepository.findById(userId).isEmpty()||favoriteArtistRepository.findByUser_UserId(userId).isEmpty()) {
            throw new NotFoundUserException();
        }
        FavoriteArtist favoriteArtist1=favoriteArtistRepository.findByUser_UserId(userId).get();
        return new FavoriteArtistDto(favoriteArtist1.getAlbumName(),
            favoriteArtist1.getArtistName(),favoriteArtist1.getTrackName(),favoriteArtist1.getCover());
    }

    public List<ArtistData> artistSearchData(String artistName) {//artist이름 주면
        List<ArtistData> artistDataList = new ArrayList<>();
        if(artistRepository.findByArtistName(artistName).isEmpty())
        {
            throw new NotFoundArtistException();
        }
        List<Artist> artists=artistRepository.findAllByArtistName(artistName).orElseThrow();
        for(Artist artist:artists) {
            List<Album> album=albumRepository.findAllByArtist_ArtistId(artist.getArtistId());
            for(Album album1:album) {
                ArtistData artistData=new ArtistData(artist.getArtistId(),
                        album1.getTitle(),album1.getCover(),album1.getCreatedDate(),artistName);
                artistDataList.add(artistData);
            }
        }
        return artistDataList;
    }

    public List<AlbumData> albumSearchData(Long artistId,String albumName) {
        List<AlbumData> albumDataList = new ArrayList<>();

        Album albums;
        if(albumRepository.findByTitleAndArtist_ArtistId(albumName,artistId).isEmpty())
        {
            throw new NotFoundAlbumException();
        }
        albums= albumRepository.findByTitleAndArtist_ArtistId(albumName,artistId).get();
        List<Track> track=trackRepository.findByAlbum_AlbumIdAndArtist_ArtistId
            (albums.getAlbumId(),albums.getArtist().getArtistId()).orElseThrow();
        for(Track track1:track) {
            AlbumData albumData=new AlbumData(track1.getAlbum().getAlbumId(),track1.getTitle());
            albumDataList.add(albumData);
        }
        return albumDataList;
    }

    public TrackData trackSearchData(Long albumId,String trackName) {
        if(trackRepository.findByTitleAndAlbum_AlbumId(trackName,albumId).isEmpty())
        {
            throw new NotFoundTrackException();
        }
        Track track=trackRepository.findByTitleAndAlbum_AlbumId(trackName,albumId).get();
        return new TrackData(track.getTrackId(),track.getTitle());
    }

    public void favoriteArtistSaveData(FavoriteReq favoriteArtist) {
        if(usersRepository.findById(favoriteArtist.getUserId()).isEmpty()) {
            throw new NotFoundUserException();
        }
        if(trackRepository.findByTrackIdAndArtist_ArtistId
                (favoriteArtist.getTrackId(),favoriteArtist.getArtistId()).isEmpty())
        {
            throw new NotFoundTrackException();
        }
        if(albumRepository.findByAlbumIdAndArtist_ArtistId
            (favoriteArtist.getAlbumId(),favoriteArtist.getArtistId()).isEmpty())
        {
            throw new NotFoundAlbumException();
        }
        User user=usersRepository.findByUserId(favoriteArtist.getUserId()).orElseThrow();
        favoriteArtistRepository.deleteByUser_UserId(user.getUserId());
        System.out.println(favoriteArtistRepository.findByUser_UserId(user.getUserId()));
        FavoriteArtist favoriteArtist1=new FavoriteArtist(
            artistRepository.findById(favoriteArtist.getArtistId()).orElseThrow().getArtistName(),
            albumRepository.findById(favoriteArtist.getAlbumId()).orElseThrow().getTitle(),
            trackRepository.findById(favoriteArtist.getTrackId()).orElseThrow().getTitle(),
            favoriteArtist.getCover(),user);
        favoriteArtistRepository.save(favoriteArtist1);
    }
}
