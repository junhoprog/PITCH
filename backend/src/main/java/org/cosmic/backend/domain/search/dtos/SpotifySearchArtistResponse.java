package org.cosmic.backend.domain.search.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SpotifySearchArtistResponse {
    private String artistId;
    private String imageUrl;
    private String name;
}
