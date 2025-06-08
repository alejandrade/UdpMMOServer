package io.shrouded.config;

import io.shrouded.data.player.PlayerId;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class IdentifierMongoConverters {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new ObjectIdToPlayerIdConverter(),
                new PlayerIdToObjectIdConverter()
        ));
    }


    static class ObjectIdToPlayerIdConverter implements Converter<ObjectId, PlayerId> {
        @Override
        public PlayerId convert(ObjectId source) {
            return PlayerId.of(source.toHexString());
        }
    }

    static class PlayerIdToObjectIdConverter implements Converter<PlayerId, ObjectId> {
        @Override
        public ObjectId convert(PlayerId source) {
            return new ObjectId(source.value());
        }
    }

}
