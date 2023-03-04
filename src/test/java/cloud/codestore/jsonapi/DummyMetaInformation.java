package cloud.codestore.jsonapi;

import cloud.codestore.jsonapi.meta.MetaInformation;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DummyMetaInformation implements MetaInformation
{
    @JsonProperty("info")
    public String info = "dummy meta info";
}
