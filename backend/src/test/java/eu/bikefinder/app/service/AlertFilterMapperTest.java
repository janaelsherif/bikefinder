package eu.bikefinder.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bikefinder.app.web.dto.OfferSearchParams;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AlertFilterMapperTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void mapsStringFieldsAndBooleansFromJson() throws Exception {
        var json =
                mapper.readTree(
                        """
                        {
                          "brand": "Trek",
                          "bikeCategory": "trekking",
                          "maxLandedPriceChf": "3000",
                          "warrantyPresent": "true",
                          "bargainOnly": "false"
                        }
                        """);
        OfferSearchParams p = AlertFilterMapper.toParams(json);
        assertThat(p.getBrand()).isEqualTo("Trek");
        assertThat(p.getBikeCategory()).isEqualTo("trekking");
        assertThat(p.getMaxLandedPriceChf()).isEqualByComparingTo(new BigDecimal("3000"));
        assertThat(p.getWarrantyPresent()).isTrue();
        assertThat(p.getBargainOnly()).isFalse();
    }

    @Test
    void emptyObjectYieldsEmptyParams() {
        OfferSearchParams p = AlertFilterMapper.toParams(mapper.createObjectNode());
        assertThat(p.getBrand()).isNull();
    }
}
