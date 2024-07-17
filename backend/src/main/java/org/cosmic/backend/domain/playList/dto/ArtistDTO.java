package org.cosmic.backend.domain.playList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistDTO {
    //artistId도 필요해보임
    private String artistName;
}
