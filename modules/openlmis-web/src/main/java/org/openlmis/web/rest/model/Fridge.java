package org.openlmis.web.rest.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fridge {

    @JsonProperty("FacilityID")
    String facilityID;

    @JsonProperty("FridgeID")
    String fridgeID;

    @JsonProperty("HighAlarmCount")
    Long highAlarmCount;

    @JsonProperty("LowAlarmCount")
    Long lowAlarmCount;

    @JsonProperty("MinutesHigh")
    Long minutesHigh;

    @JsonProperty("MinutesInRange")
    Long minutesInRange;

    @JsonProperty("MinutesLow")
    Long minutesLow;

    @JsonProperty("MinutesNoData")
    Long minutesNoData;

    @JsonProperty("Status")
    Long status;

    @JsonProperty("URL")
    String url;

    public void updateURL(String user, String pwd) {
        this.url = url.replace("://", "://" + user + ":" + pwd + "@");
    }
}
