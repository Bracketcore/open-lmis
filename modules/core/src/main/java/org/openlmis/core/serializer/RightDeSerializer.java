package org.openlmis.core.serializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.openlmis.core.domain.Right;

import java.io.IOException;

public class RightDeSerializer extends JsonDeserializer<Right> {
    @Override
    public Right deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        return Right.valueOf(node.get("right").getTextValue());
    }
}
