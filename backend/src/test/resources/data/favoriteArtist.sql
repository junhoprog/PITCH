-- Email 테이블에 데이터 삽입
INSERT INTO emails (email, verified, verification_code, create_time)
VALUES ('test1@example.com', true, '12345678', CURRENT_TIMESTAMP);

-- User 테이블에 데이터 삽입
INSERT INTO users (email, username, password, profile_picture, create_time)
VALUES (
           'test1@example.com',  -- 외래키로 참조되는 email 값
           'testboy',
           '12345678',
           'base',
           CURRENT_TIMESTAMP
       );

-- Artist 테이블에 데이터 삽입
INSERT INTO Artist(artist_name,artist_cover,spotify_artist_id) VALUES('bibi','base','base');

-- Album 테이블에 데이터 삽입
INSERT INTO Album(title,album_cover, artist_id, created_date,spotify_album_id)
VALUES (
           'bam',
           'base',
           (SELECT Artist.artist_id FROM Artist WHERE artist_name='bibi'),
           CURRENT_TIMESTAMP,
           'base'
       );

INSERT INTO Track(title,album_id,artist_id,spotify_track_id)
VALUES(
          'bam',
          (SELECT Album.album_id FROM Album WHERE title='bam'),
          (SELECT Artist.artist_id FROM Artist WHERE Artist.artist_name='bibi'),
          'base'
      );
