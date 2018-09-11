package io.annot8.integration.tests.pipelines;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.annot8.common.data.bounds.SpanBounds;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.bounds.Bounds;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

public class MongoPipelineTest {

    private static final String DOCUMENT = "document";
    private static final String TEXT = "text";
    private static final String EXPECTED_EMAIL = "test@testing.com";
    private GenericContainer MONGO;

    @BeforeEach
    public void beforeEach(){
        Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(27017), new ExposedPort(27017)));
        MONGO = new GenericContainer("mongo:latest");
        MONGO.withExposedPorts(27017).withCreateContainerCmdModifier(cmd).start();
        MongoClient client = MongoClients.create("mongodb://" + MONGO.getContainerIpAddress() + ":27017");
        MongoDatabase db = client.getDatabase("annot8");
        MongoCollection<Document> test = db.getCollection("test");

        Document parse = Document.parse("{'text':'" + EXPECTED_EMAIL + "'}");
        test.insertOne(parse);
    }

    @AfterEach
    public void afterEach(){
        MONGO.stop();
    }

    @Test
    public void testMongoPipeline(){
        MongoPipeline pipeline = new MongoPipeline();
        List<Item> items = pipeline.run();

        assertEquals(1, items.size());

        Item item = items.get(0);

        assertThat(item.getContentByName(DOCUMENT)).isNotEmpty();
        assertThat(item.getContentByName(TEXT)).isNotEmpty();
        assertThat(item.getContentByName(TEXT).count()).isEqualTo(1);

        Content<?> content = item.getContentByName(TEXT).findFirst().get();

        assertThat(content.getData()).isNotNull();
        assertThat(content.getData()).isEqualTo(EXPECTED_EMAIL);

        List<Annotation> annotations = content.getAnnotations().getAll().collect(
            Collectors.toList());
        assertThat(annotations).isNotEmpty();
        assertThat(annotations.size()).isEqualTo(1);

        Annotation annotation = annotations.get(0);
        Bounds bounds = annotation.getBounds();

        assertThat(bounds).isNotNull();
        assertThat(bounds).isInstanceOf(SpanBounds.class);
        assertThat(bounds.getData(content).get()).isEqualTo(EXPECTED_EMAIL);
        
        SpanBounds spanBounds = (SpanBounds) bounds;
        assertThat(spanBounds.getBegin()).isEqualTo(0);
        assertThat(spanBounds.getEnd()).isEqualTo(16);
    }


}
