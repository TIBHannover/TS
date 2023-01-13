package uk.ac.ebi.spot.ols.model.chart;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
@Builder
public class ChartData {
    byte[] data;
    MediaType contentType;

    public long getLength() {
        return data.length;
    }
}

