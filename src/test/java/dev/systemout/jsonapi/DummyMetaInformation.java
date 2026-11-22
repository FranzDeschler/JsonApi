package dev.systemout.jsonapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.systemout.jsonapi.meta.MetaInformation;

public class DummyMetaInformation implements MetaInformation
{
    @JsonProperty("info")
    public String info = "dummy meta info";
}
