package io.annot8.integration.tests.pipelines;

import io.annot8.common.implementations.pipelines.PipelineBuilder;
import io.annot8.common.implementations.registries.ContentBuilderFactoryRegistry;
import io.annot8.components.cyber.processors.Email;
import io.annot8.components.files.processors.TxtFileExtractor;
import io.annot8.components.files.sources.FileSystemSource;
import io.annot8.components.files.sources.FileSystemSourceSettings;

public class EmailPipeline extends AbstractResourceDataPipeline {

  protected void configurePipeline(PipelineBuilder builder) {
    FileSystemSourceSettings fileSystemSourceSettings = new FileSystemSourceSettings(
        getResourceUri());
    fileSystemSourceSettings.setWatching(false);
    builder.addSource(new FileSystemSource(), fileSystemSourceSettings);
    builder.addProcessor(new TxtFileExtractor());
    builder.addProcessor(new Email());
  }

  @Override
  protected void configureContentBuilders(ContentBuilderFactoryRegistry registry) {
  }

}
