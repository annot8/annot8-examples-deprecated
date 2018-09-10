package io.annot8.integration.tests.pipelines;

import io.annot8.common.data.bounds.SpanBounds;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.bounds.Bounds;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.stores.AnnotationStore;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailPipelineTest {

    @Test
    public void testEmailPipeline() {
        EmailPipeline pipeline = new EmailPipeline();
        List<Item> items = pipeline.run();

        assertThat(items.size()).isEqualTo(1);

        Item item = items.get(0);

        assertThat(item.getContents().count()).isEqualTo(2);
        assertThat(item.listNames()).containsExactlyInAnyOrder("file", "text");

        Content<?> fileContent = item.getContentByName("file").findFirst().get();
        Content<?> textContent = item.getContentByName("text").findFirst().get();

        assertThat(fileContent.getDataClass()).isEqualTo(File.class);
        File expectedFile = pipeline.getResourceUri().resolve("text.txt").toFile();
        assertThat(fileContent.getData()).isEqualTo(expectedFile);

        assertThat(textContent.getDataClass()).isEqualTo(String.class);
        assertThat(textContent.getData()).isNotNull();

        AnnotationStore textAnnotationStore = textContent.getAnnotations();
        List<Annotation> annotations = textAnnotationStore.getAll().collect(Collectors.toList());
        assertThat(annotations.size()).isEqualTo(4);

        List<Bounds> bounds = annotations.stream()
                .map(Annotation::getBounds).collect(Collectors.toList());
        assertThat(bounds).hasOnlyElementsOfType(SpanBounds.class);
        assertThat(toSpanBounds(bounds)
                .filter(b -> b.getBegin() == 0 && b.getEnd() == 24).count()).isEqualTo(1);
        assertThat(toSpanBounds(bounds)
                .filter(b -> b.getBegin() == 25 && b.getEnd() == 41).count()).isEqualTo(1);
        assertThat(toSpanBounds(bounds)
                .filter(b -> b.getBegin() == 42 && b.getEnd() == 60).count()).isEqualTo(1);
        assertThat(toSpanBounds(bounds)
                .filter(b -> b.getBegin() == 61 && b.getEnd() == 77).count()).isEqualTo(1);

        List<String> values = bounds.stream()
                                .map(b -> b.getData(textContent, String.class))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList());

        assertThat(values).containsExactlyInAnyOrder("test.account@testing.com",
                "email@account.io", "me@somewhere.co.uk", "someone@info.org");
    }

    private Stream<SpanBounds> toSpanBounds(List<Bounds> bounds){
        return bounds.stream().map(SpanBounds.class::cast);
    }

}
