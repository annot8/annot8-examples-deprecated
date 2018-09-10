package io.annot8.integration.tests.pipelines;

import io.annot8.common.implementations.registries.ContentBuilderFactoryRegistry;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.annot8.core.exceptions.BadConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.annot8.common.implementations.pipelines.PipelineBuilder;
import io.annot8.core.data.Item;
import io.annot8.defaultimpl.Annot8PipelineApplication;
import io.annot8.integration.tests.pipleines.processor.MonitoringProcessor;

public abstract class AbstractResourceDataPipeline {
  
  private static final Logger logger = LoggerFactory.getLogger(AbstractResourceDataPipeline.class);
  
  private List<Item> items;

  protected abstract void configurePipeline(PipelineBuilder builder) throws BadConfigurationException;

  protected abstract void configureContentBuilders(ContentBuilderFactoryRegistry registry);

  public List<Item> run(){
      items = new ArrayList<>();
      Annot8PipelineApplication application = new Annot8PipelineApplication(this::configure, this::configureContentBuilders);
      application.run();
      return items;
  }
  
  public List<Item> getItems(){
    return items;
  }
  
  protected Path getResourceUri() {
    URI uri = null;
    try {
        uri = EmailPipeline.class.getClassLoader().getResource("TestData").toURI();
    } catch (URISyntaxException e) {
        logger.error("Error finding provided path.", e);
    }

    return Paths.get(uri);
  }
  
  private void configure(PipelineBuilder builder) {
      try {
          configurePipeline(builder);
      } catch (BadConfigurationException e) {
          logger.error("Error configuring pipeline", e);
      }
      builder.addProcessor(new MonitoringProcessor(items::add));
  }

}
